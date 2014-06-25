package de.mfischbo.bustamail.subscriber.domain;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.mfischbo.bustamail.common.domain.BaseDomain;

@Entity
@Table(name = "Subscriber_Address")
public class Address extends BaseDomain {

	@Basic
	private String street;
	
	@Basic
	private String zipcode;
	
	@Basic
	private String city;
	
	@Basic
	@Column(length = 3)
	private String country;

	@Basic
	private Float	latitude;
	
	@Basic
	private Float	longitude;
	
	@ManyToOne
	@JoinColumn(name = "Contact_id", referencedColumnName = "id")
	private Contact	contact;
}
