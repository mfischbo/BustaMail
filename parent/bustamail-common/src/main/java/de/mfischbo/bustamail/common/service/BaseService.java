package de.mfischbo.bustamail.common.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.mfischbo.bustamail.common.domain.BaseDomain;
import de.mfischbo.bustamail.exception.EntityNotFoundException;

@Service
public class BaseService {

	@Autowired
	protected Mapper		mapper;
	
	protected Logger		log = LoggerFactory.getLogger(getClass());
	
	
	protected void checkOnNull(BaseDomain object) throws EntityNotFoundException {
		if (object == null)
			throw new EntityNotFoundException("Unable to find entity!");
	}

	protected <S, T> T asDTO(S object, Class<T> target) {
		return mapper.map(object, target);
	}
	
	protected <S, T> List<T> asDTO(List<S> source, Class<T> target) {
		List<T> retval = new LinkedList<T>();
		for (S src : source) {
			retval.add(mapper.map(src, target));
		}
		return retval;
	}

	protected <S, T> Set<T> asDTO(Set<S> source, Class<T> target) {
		Set<T> retval = new HashSet<>();
		for (S src : source) {
			retval.add(mapper.map(src, target));
		}
		return retval;
	}
	
	
	protected <S, T> Page<T> asDTO(Page<S> page, Class<T> target, Pageable pageable) {
		
		List<S> list = page.getContent();
		List<T> result = new ArrayList<T>(page.getContent().size());
		for (S source : list) {
			T tmp = mapper.map(source, target);
			result.add(tmp);
		}
		return new PageImpl<T>(result, pageable, page.getTotalElements());
	}
	
	protected <S, T> T fromDTO(S dto, Class<T> target) {
		return mapper.map(dto, target);
	}
	
	protected void fromDTO(Object dto, Object target) {
		mapper.map(dto, target);
	}
}
