package de.mfischbo.bustamail.mailinglist.service;

import static org.dozer.loader.api.TypeMappingOptions.mapNull;
import static org.dozer.loader.api.TypeMappingOptions.oneWay;
import static org.dozer.loader.api.TypeMappingOptions.wildcard;

import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.FieldsMappingOptions;
import org.dozer.loader.api.TypeMappingBuilder;

import de.mfischbo.bustamail.mailinglist.dto.SubscriptionImportDTO;
import de.mfischbo.bustamail.mailinglist.util.EMailAddressConverter;
import de.mfischbo.bustamail.reader.IndexedPropertyHolder;

public class DozerImportFactory {

	public static <T> DozerBeanMapper createInstance(Class<T> bean, SubscriptionImportDTO dto) {
		
		BeanMappingBuilder b = new BeanMappingBuilder() {
		
			@Override
			protected void configure() {
				
				final TypeMappingBuilder tmb = mapping(IndexedPropertyHolder.class, bean, oneWay(), wildcard(false), mapNull(true));
				//tmb.fields("columns", "emailAddresses[0]", FieldsMappingOptions.customConverter(EMailAddressConverter.class));

				/*
				mapping(String.class, EMailAddress.class, TypeMappingOptions
						.oneWay(),
						TypeMappingOptions.beanFactory("de.mfischbo.bustamail.mailinglist.util.EMailAddressFactory"));
				*/
				for (int i=0; i < dto.getFieldNames().length; i++) {
					String field = dto.getFieldNames()[i];
					if (field.trim().length() == 0)
						continue;
					
					if (field.equals("emailAddresses[0]")) {
						tmb.fields("columns[" + i + "]", field, FieldsMappingOptions.customConverter(EMailAddressConverter.class));
					} else {
						tmb.fields("columns[" + i + "]", field);
					}
				}
			}
		};
		DozerBeanMapper retval = new DozerBeanMapper();
		retval.addMapping(b);
		return retval;
	}
}
