package de.mfischbo.bustamail.stats.service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.mongodb.AggregationOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DBObject;

import de.mfischbo.bustamail.common.service.BaseService;
import de.mfischbo.bustamail.mailing.domain.Mailing;
import de.mfischbo.bustamail.stats.domain.StatsEntry;
import de.mfischbo.bustamail.stats.domain.StatsEntry.RecordType;
import de.mfischbo.bustamail.stats.dto.IntAggregationResult;
import de.mfischbo.bustamail.stats.dto.MailingStats;
import de.mfischbo.bustamail.stats.dto.MailingStats.ClickEntry;
import de.mfischbo.bustamail.stats.repository.StatsEntryRepo;

@Service
public class StatsServiceImpl extends BaseService implements StatsService {

	@Inject 
	private StatsEntryRepo		sRepo;

	@Inject
	private MongoTemplate		mTemplate;
	
	@Override
	public StatsEntry createStatsEntry(StatsEntry e) {
		return sRepo.save(e);
	}

	@Override
	public MailingStats getStatsByMailing(Mailing m) {
		MailingStats retval = new MailingStats();
		
		retval.setOpeningAmount(sRepo.countEntriesByMailingAndType(m.getId(), RecordType.OPEN));
		
		// start and end time of the mailing
		Pageable startP = new PageRequest(1, 1, Sort.Direction.ASC, "dateCreated");
		Pageable endP   = new PageRequest(1, 1, Sort.Direction.DESC, "dateCreated");
		
		List<StatsEntry> f = sRepo.getSendingStatusByMailingId(m.getId(), startP).getContent();
		List<StatsEntry> s = sRepo.getSendingStatusByMailingId(m.getId(), endP).getContent();
		if (f.size() > 0) retval.setStartedAt(f.get(0).getDateCreated());
		if (s.size() > 0) retval.setFinishedAt(s.get(0).getDateCreated());
		
		retval.setMailsSentSuccess(sRepo.countEntriesByMailingAndType(m.getId(), RecordType.SENT_SUCCESS));
		retval.setMailsSentFailure(sRepo.countEntriesByMailingAndType(m.getId(), RecordType.SENT_FAILURE));
		retval.setRecipientAmount(retval.getMailsSentSuccess() + retval.getMailsSentFailure());
		retval.setOpeningAmount(sRepo.countEntriesByMailingAndType(m.getId(), RecordType.OPEN));
		retval.setClickAmount(sRepo.countEntriesByMailingAndType(m.getId(), RecordType.CLICK));

		// aggregate unique openings
		Aggregation uOpen = newAggregation(
				match(Criteria.where("mailingId").is(m.getId()).and("type").is(RecordType.OPEN.toString())),
				group("subscriptionId"),
				group().count().as("count")
		);
		AggregationResults<IntAggregationResult> data = mTemplate.aggregate(uOpen, "Stats_StatsEntry", IntAggregationResult.class);
		List<IntAggregationResult> res = data.getMappedResults();
		if (res.size() > 0) {
			retval.setUniqueOpeningAmount(res.get(0).getCount());
		
			long quot = retval.getUniqueOpeningAmount() * 100;
			float div = retval.getRecipientAmount();
			retval.setOpeningRate(BigDecimal.valueOf(quot / div));
		}
		
		
		// This stuff sucks
		// Mongo Query was :
		/*
	    db.Stats_StatsEntry.aggregate([
	        { $match : {
	            "mailingId" : ObjectId("54d028a1e4b033d47d9609c9"),
	            "type"      : "CLICK"
	        }},
	        { $group : { 
	            _id : "$targetUrl", 
	            "distinctSubscribers" : {$addToSet : "$subscriptionId" },
	            "clickAmount"         : {$sum : 1}
	        }},  
	        { $project: { 
	            _id : 1, 
	            "targetUrl" : "$_id", 
	            "clickAmount"       : "$clickAmount",
	            "uniqueClickAmount" : { $size : "$distinctSubscribers" } 
	        }}
		]);
		*/
		DBObject mO    = new BasicDBObject("mailingId", m.getId());
		mO.put("type", RecordType.CLICK.toString());
		DBObject match = new BasicDBObject("$match", mO);
		
		DBObject addToSet = new BasicDBObject("$addToSet", "$subscriptionId");
		DBObject sum      = new BasicDBObject("$sum", 1);
		
		DBObject gr = new BasicDBObject("_id", "$targetUrl");
		gr.put("distinctSubscribers", addToSet);
		gr.put("clickAmount", sum);
		DBObject group = new BasicDBObject("$group", gr);
	
		DBObject size = new BasicDBObject("$size", "$distinctSubscribers");
		DBObject pCol = new BasicDBObject("targetUrl", "$_id");
		pCol.put("clickAmount", "$clickAmount");
		pCol.put("uniqueClickAmount", size);
		DBObject projection = new BasicDBObject("$project", pCol);
		
		List<DBObject> pipe = new ArrayList<>(3);
		pipe.add(match);
		pipe.add(group);
		pipe.add(projection);
		Cursor cursor = mTemplate.getCollection("Stats_StatsEntry").aggregate(pipe, AggregationOptions.builder().build());
		
		List<ClickEntry> entries = new LinkedList<ClickEntry>();
		long uniqueClickAmount = 0;
		while (cursor.hasNext()) {
			DBObject n = cursor.next();
			ClickEntry e = retval.new ClickEntry(
					(String) n.get("targetUrl"), 
					(Integer) n.get("clickAmount"), 
					(Integer) n.get("uniqueClickAmount"));
			entries.add(e);
			uniqueClickAmount += e.getUniqueClickAmount();
		}
		retval.setClickDetails(entries);
		retval.setUniqueClickAmount(uniqueClickAmount);
		
		// calculate sending rate
		long diff = retval.getFinishedAt().getMillis() - retval.getStartedAt().getMillis();
		float diffM = diff / (1000 * 60);
		retval.setMailsPerMinute(
				BigDecimal.valueOf(retval.getRecipientAmount() / diffM));
		return retval;
	}
}
