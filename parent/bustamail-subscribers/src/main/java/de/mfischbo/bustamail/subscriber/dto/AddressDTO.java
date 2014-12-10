package de.mfischbo.bustamail.subscriber.dto;

import de.mfischbo.bustamail.common.dto.BaseDTO;

public class AddressDTO extends BaseDTO {

	private static final long serialVersionUID = 250120218546348082L;
	
	private String		street;
	private String		zipcode;
	private String		city;
	private String		country;
	private Float		latitude;
	private Float		longitude;
	
	public AddressDTO() {
		
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
}
