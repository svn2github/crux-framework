/*
 * Copyright 2009 Sysmap Solutions Software e Consultoria Ltda.
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
package br.com.sysmap.crux.core.client.screen.factory.direction;

import br.com.sysmap.crux.core.client.screen.AttributeProcessor;
import br.com.sysmap.crux.core.rebind.widget.WidgetCreatorContext;

import com.google.gwt.i18n.shared.AnyRtlDirectionEstimator;
import com.google.gwt.i18n.shared.FirstStrongDirectionEstimator;
import com.google.gwt.i18n.shared.HasDirectionEstimator;
import com.google.gwt.i18n.shared.WordCountDirectionEstimator;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class DirectionEstimatorAttributeParser<C extends WidgetCreatorContext> implements AttributeProcessor<C>
{
	public void processAttribute(C context, String propertyValue) 
	{
		HasDirectionEstimator widget = (HasDirectionEstimator)context.getWidget();
		DirectionEstimator estimator = DirectionEstimator.valueOf(propertyValue);
		
		switch (estimator) 
		{
			case anyRtl:
				widget.setDirectionEstimator(AnyRtlDirectionEstimator.get());
				break;
			case firstStrong: 
				widget.setDirectionEstimator(FirstStrongDirectionEstimator.get());
				break;
			case wordCount: 
				widget.setDirectionEstimator(WordCountDirectionEstimator.get());
				break;
				
			default:
				widget.setDirectionEstimator(true);
		}
	}
}
