package de.mfischbo.bustamail.common.web;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.mfischbo.bustamail.common.domain.BaseDomain;
import de.mfischbo.bustamail.exception.DataIntegrityException;
import de.mfischbo.bustamail.exception.EntityNotFoundException;

@RestController
public class BaseApiController {

	@Autowired
	private Mapper mapper;
	
	protected Logger log = LoggerFactory.getLogger(getClass());

	@ExceptionHandler(value = EntityNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	protected void handleEntityNotFound(EntityNotFoundException e, HttpServletRequest req) {
		log.error("Caught EntityNotFoundException with message : " + e.getMessage());
		log.debug("Requested URL\t: " + req.getRequestURL().toString());
		log.debug("Method\t\t\t: " + req.getMethod());
	}
	
	@ExceptionHandler(value = ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	protected @ResponseBody Set<ConstraintViolation<?>> handleConstraintViolation(ConstraintViolationException e) {
		Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
		return violations;
	}
	
	@ExceptionHandler(value = DataIntegrityException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	protected void handleDataIntegrityException(DataIntegrityException e) {
		log.error("Processing request would end in conflict state.");
		log.debug("Error was : " + e.getMessage());
	}
	
		protected void checkOnNull(BaseDomain object) throws EntityNotFoundException {
		if (object == null)
			throw new EntityNotFoundException("Unable to find entity!");
	}
	

	protected <S, T> T asDTO(S object, Class<T> target) {
		if (object == null)
			return null;
	
		T retval = mapper.map(object, target);
		return retval;
	}
	
	protected <S, T> List<T> asDTO(List<S> source, Class<T> target) {
		if (source == null)
			return null;
		
		List<T> retval = new ArrayList<T>(source.size());
		for (S src : source) {
			retval.add(mapper.map(src, target));
		}
		return retval;
	}
	
	protected <S, T> Set<T> asDTO(Set<S> source, Class<T> target) {
		if (source == null)
			return null;
		
		Set<T> retval = new LinkedHashSet<T>();
		for (S src : source)
			retval.add(mapper.map(src, target));
		return retval;
	}
	
	protected <S, T> Page<T> asDTO(Page<S> page, Class<T> target, Pageable pageable) {
	
		if (page == null || page.getContent() == null)
			return null;
		
		List<S> list = page.getContent();
		List<T> result = new ArrayList<T>(page.getContent().size());
		for (S source : list) {
			T tmp = mapper.map(source, target);
			result.add(tmp);
		}
		return new PageImpl<T>(result, pageable, page.getTotalElements());
	}
}