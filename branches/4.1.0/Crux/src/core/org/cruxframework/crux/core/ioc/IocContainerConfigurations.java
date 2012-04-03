/*
 * Copyright 2011 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.core.ioc;

import java.util.HashMap;
import java.util.Map;

import org.cruxframework.crux.core.client.ioc.Inject;

/**
 * Base class for an IoC configuration class. Crux engine search for all subclasses of IocContainerConfig and 
 * invoke their configure method. 
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class IocContainerConfigurations implements IocConfiguration
{
	private static Map<String, IocConfig<?>> configurations = new HashMap<String, IocConfig<?>>();
	
	/**
	 * Call this method on your configure method to create a configuration for some Type. Eg: 
	 * <p>
	 * You can write something like:
	 * <p>
	 * <pre>
	 * bindType(List.class).toClass(ArrayList.class).inLocalScope();
	 * </pre>
	 * <p>
	 * That would cause Crux to inject a new instance of ArrayList whenever you declare 
	 * a field of type List annotated with {@link Inject}
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	protected <T> IocConfig<T> bindType(Class<T> clazz)
	{
		IocConfig<T> iocConfig = new IocConfig<T>(clazz);
		String className = clazz.getCanonicalName();
		if (configurations.containsKey(className))
		{
			throw new IoCException("Invalid Ioc configuration. Class "+className+" is already bound to the container.");//TODO message.
		}
		configurations.put(className, iocConfig);
		return iocConfig;
	}

	/**
	 * Override this method if you need to specify some conditions that enables your configuration.
	 * Eg: You can enable a configuration for all client classes only when running on 
	 * development environment.
	 * @return
	 */
	protected boolean isEnabled()
	{
		return true;
	}
	
	/**
	 * 
	 * @param className
	 * @return
	 */
	static IocConfig<?> getConfigurationForType(String className)
	{
		IocConfig<?> iocConfig = configurations.get(className);
		return iocConfig;
	}
}


/*
bindType(MyDTO.class).inLocalScope(); //Default is LocalSope
bindType(MyDTO.class).toClass(Class.class).inLocalScope(); //Default is LocalSope
bindType(MyDTO2.class).toProvider(MyProvider.class).inDocumentScope(); // inScreenScope()??? //userScope seria um dos tipos de scope "Shareable"
bindType(MyDTO3.class).inUserScope("myScope").managedByContainer();//autualizacoes feitas antes e depois de cada metodo
bindType(MyDTO4.class).inUserScope("myScope").managedByApplication();//DEFAULT. autualizacoes devem ser commitadas e lidas de forma expl√É¬≠cita, 
                                                                     //via os metodos CruxIoCContainer.[readScopeState() e writeScope()]
bindAnnotatedWith(Parameter.class).toProviderGenerator(ParameterProviderFactory.class).inLocalScope();
bindAnnotatedWith(ValueObject.class).toProviderGenerator(ValueObjectProviderFactory.class).inScreenScope(); // inViewScope()???
bindAnnotatedWith(Controller.class).toProviderGenerator(ControllerProviderFactory.class);
bindAssignableTo(Parameter.class).toProviderGenerator(TestProviderFactory.class).inUserScope("myScope");
*///bindIncludingName("**/*Async").toProviderGenerator(RpcProviderFactory.class).inLocalScope();
//bindExcludingName("**/*Controller").toProviderGenerator(StatisticsControllerProvider.class).inLocalScope();
/*
 * 1) Criar uma class IocConfig, que √É¬© retornada pelo metodo bindType.... outra IoCConfigList 
 * pelos metodos bindAnnotatedWith (e outros do genero)....criar tbm uma classe base pros 
 * 2 tipos citados. Usar mesma estrategia para os metodos in*Scope() retornando metodos de acordo com o 
 * tipo de escopo escolhido
 * 
 * 2) Fazer um scanner para registrar automaticamente todas as classes de configura√É¬ß√É¬£o do Ioc container
 * e executar o configure de todos eles..... essas classes ser√É¬£o executadas no rebind (e n√É¬£o no client)
 * 
 * 3) QQ tentativa de inje√É¬ß√É¬£o nao mapeada explicitamente aki √É¬© simplesmente tratada por um GWT.create()
 * apontando para o tipo do campo a ser injetado.
 */
