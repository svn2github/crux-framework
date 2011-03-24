package org.cruxframework.crux.showcase.client.controller;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Create;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.controller.Parameter;
import org.cruxframework.crux.core.client.controller.ParameterObject;

import com.google.gwt.user.client.Window;

@Controller("parameterBindController")
public class ParameterBindController {
	
	@Create
	protected Parameters parameters;

	@Parameter(required=true)
	protected String parameter;
	
	@Expose
	public void onClick(){
		Window.alert("Parameter: "+parameter);
		Window.alert("Parameter (from parameter object): "+parameters.getParameter());
	}
	
	@ParameterObject
	public static class Parameters
	{
		@Parameter(required=true)
		private String parameter;
		
		private Integer intParamenter;

		public Integer getIntParamenter()
		{
			return intParamenter;
		}

		public void setIntParamenter(Integer intParamenter)
		{
			this.intParamenter = intParamenter;
		}

		public String getParameter()
		{
			return parameter;
		}

		public void setParameter(String parameter)
		{
			this.parameter = parameter;
		}
	}
}