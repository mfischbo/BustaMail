package de.mfischbo.bustamail.mailinglist.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dozer.CustomConverter;

import de.mfischbo.bustamail.subscriber.domain.EMailAddress;

public class EMailAddressConverter implements CustomConverter {

	@Override
	public Object convert(Object arg0, Object arg1, Class<?> arg2, Class<?> arg3) {

		if (arg2 == List.class || arg2 == Set.class) {
			if (arg0 == null) {
				List<EMailAddress> retval = new ArrayList<EMailAddress>();
				retval.add(new EMailAddress((String) arg1));
				return new EMailAddress((String) arg1);
			} else {
				//((List<EMailAddress>) arg0).add(new EMailAddress((String) arg1));
				//return arg0;
				return new EMailAddress((String) arg1);
			}
		}
		return null;
	}

}
