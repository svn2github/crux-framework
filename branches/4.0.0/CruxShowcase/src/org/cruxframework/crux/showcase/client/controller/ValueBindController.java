package org.cruxframework.crux.showcase.client.controller;

import java.util.Date;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.ValueObject;

import com.google.gwt.user.client.Window;

@Controller("valueBindController")
public class ValueBindController {
	
	@Create
	protected Person person;
	
	@Expose
	public void onClick(){
		Window.alert("Name: " + person.getName());
		Window.alert("Phone: " + person.getPhone());
		Window.alert("Date of Birth: " + person.getDateOfBirth());
	}
	
	@ValueObject
	public static class Person
	{
		private String name;
		private String phone;
		private Date dateOfBirth;
		
		public String getName(){
			return name;
		}

		public void setName(String name){
			this.name = name;
		}

		public String getPhone(){
			return phone;
		}

		public void setPhone(String phone){
			this.phone = phone;
		}

		public Date getDateOfBirth(){
			return dateOfBirth;
		}

		public void setDateOfBirth(Date dateOfBirth){
			this.dateOfBirth = dateOfBirth;
		}
	}
}