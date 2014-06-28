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

	
	public Address() {
		
	}
	
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}
}
