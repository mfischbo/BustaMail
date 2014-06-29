package de.mfischbo.bustamail.mailinglist.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import de.mfischbo.bustamail.mailinglist.dto.SubscriptionImportDTO;
import de.mfischbo.bustamail.mailinglist.service.DozerImportFactory;
import de.mfischbo.bustamail.reader.IndexedPropertyHolder;
import de.mfischbo.bustamail.subscriber.domain.Contact;
import de.mfischbo.bustamail.subscriber.domain.EMailAddress;

public class ImportMapperTest {

	public SubscriptionImportDTO settings;

	@Before
	public void setup() {
		settings = new SubscriptionImportDTO();
		String[] headerFields = new String[1];
		headerFields[0] = "emailAddresses[0]";
		settings.setFieldNames(headerFields);
	}
	
	@Test
	public void mapsEMailAddressCorrectly() throws Exception {
		
		List<String> props = new ArrayList<String>(1);
		props.add("john.doe@example.com");
		IndexedPropertyHolder holder = new IndexedPropertyHolder();
		holder.getColumns().addAll(props);
		
		DozerBeanMapper m = DozerImportFactory.createInstance(Contact.class, this.settings);
		Contact c = new Contact();
		m.map(holder, c);
		
		assertEquals(1, c.getEmailAddresses().size());
		EMailAddress t = c.getEmailAddresses().iterator().next();
		assertNotNull(t);
		assertEquals("john.doe", t.getLocalPart());
		assertEquals("example.com", t.getDomainPart());
	}
}
