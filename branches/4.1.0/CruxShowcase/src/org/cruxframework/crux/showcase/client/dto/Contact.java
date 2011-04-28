package org.cruxframework.crux.showcase.client.dto;

import java.io.Serializable;
import java.util.Date;

import org.cruxframework.crux.core.client.dto.DataObject;
import org.cruxframework.crux.core.client.dto.DataObjectIdentifier;

@SuppressWarnings("serial")
@DataObject("contact")
public class Contact implements Serializable {
	
	public static enum Gender{
		FEMALE,
		MALE
	}

    @DataObjectIdentifier
    private int id;
    private String name;
	private String phone;
	private Date birthday;
	private Gender gender;
	private Address	address;
	
	
	public Contact(){
		
	}
	
	public Contact(int id, String name, String phone, Date birthday, Gender gender, Address address)	{
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.birthday = birthday;
		this.gender = gender;
		this.address = address;
	}
	
	/**
	 * @return the name
	 */
	public String getName()	{
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the birthday
	 */
	public Date getBirthday()
	{
		return birthday;
	}

	/**
	 * @param birthDay the birthday to set
	 */
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	/**
	 * @return the gender
	 */
	public Gender getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Address getAddress()
    {
    	return address;
    }

	public void setAddress(Address address)
    {
    	this.address = address;
    }

	public int getId()
    {
    	return id;
    }

	public void setId(int id)
    {
    	this.id = id;
    }
}