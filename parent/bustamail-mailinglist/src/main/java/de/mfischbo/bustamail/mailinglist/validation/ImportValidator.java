package de.mfischbo.bustamail.mailinglist.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.routines.EmailValidator;

import de.mfischbo.bustamail.mailinglist.validation.Validator.ResultType;

public class ImportValidator {

	private Map<Integer, Validator> validators = new HashMap<>();
	
	public ImportValidator(String[] headerFields) {

		int i = 0;
		for (String f : headerFields) {
			if (f.equals("emailAddresses[0]")) {
				validators.put(i, new Validator() {
					@Override
					public ResultType validate(String value) {
						EmailValidator e = EmailValidator.getInstance(false);
						if (!e.isValid(value)) return ResultType.UNPARSEABLE_EMAIL_ADDRESS;
						return ResultType.SUCCESS;
					}
				});
			}
			
			if (f.equals("gender")) {
				validators.put(i, new Validator() {
					@Override
					public ResultType validate(String value) {
						if (value.toUpperCase().equals("M") || value.toUpperCase().equals("F"))
							return ResultType.SUCCESS;
						return ResultType.UNKNOWN_GENDER;
					}
				});
			}
			
			if (f.equals("formalSalutation")) {
				validators.put(i, new Validator() {

					@Override
					public ResultType validate(String value) {
						if (value.toLowerCase().equals("true") || value.equals("1"))
							return ResultType.SUCCESS;
						else if (value.toLowerCase().equals("false") || value.equals("0"))
							return ResultType.SUCCESS;
						return ResultType.INVALID_BOOLEAN_EXPRESSION;
					}
				});
			}
			
			if (f.equals("addresses[0].country")) {
				validators.put(i, new Validator() {

					@Override
					public ResultType validate(String value) {
						if (value.trim().length() > 3)
							return ResultType.INVALID_COUNTRY_CODE;
						return ResultType.SUCCESS;
					}
				});
			}
			
			i++;
		}
	}

	public List<ResultType> validate(List<String> row) {
		int i=0;
		List<ResultType> retval = new ArrayList<>(row.size());
		for (String s : row) {
			if (validators.get(i) != null)
				retval.add(validators.get(i).validate(s));
			else
				retval.add(ResultType.SUCCESS);
			i++;
		}
		return retval;
	}
}
