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
package org.cruxframework.cruxsite.client.widget;

import org.cruxframework.crux.core.client.screen.DeviceAdaptive;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Template;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Templates;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@Templates({
	@Template(name="tutorialLarge", device=Device.all)
})
public interface Tutorial extends DeviceAdaptive
{
	String getLearnDescription();
	void setLearnDescription(String description);
	String getEstimatedTime();
	void setEstimatedTime(String time);
	void setLargeImage(String url);
	void setSmallImage(String url);
	void setTutorialTitle(String title);
	void setTutorialSubtitle(String subtitle);
	void setTutorialUrl(String url);
	String getTutorialUrl();
}
//<gwt:flexTable id="flexTlable" width="900px">
//<gwt:row>
//	<gwt:cell rowSpan="3" width="80%"  verticalAlignment="top">
//		<gwt:widget>
//			<gwt:simplePanel id="simplePanel" styleName="BgBlack MarginRight">
//			
//			
//				<gwt:flexTable id="Table">
//					<gwt:row>
//						<gwt:cell>
//							<gwt:html>
//								<img src="../img/img_example_learn.png"/>
//							</gwt:html>
//						</gwt:cell>
//						<gwt:cell verticalAlignment="top">
//							<gwt:html>
//								<h1>Crux in Few Steps</h1>
//								<p>Sed ante diam, consectetur id consectetur id, iaculis eget dolor. Morbi sem magna, iaculis non hendrerit nec, dictum eu arcu.</p>
//							</gwt:html>
//						</gwt:cell>
//					</gwt:row>
//					<gwt:row>
//						<gwt:cell colSpan="2">
//							<gwt:html>
//								<div class="BgBlack" style="margin-bottom:0;">
//									<table width="100%" cellpadding="4">
//										<tr>
//											<td width="33%">
//												<div class="SilverButton2">
//													Watch some tutorial
//												</div>
//											</td>
//											<td width="33%">
//												<div class="SilverButton2">
//													Read Crux Documentation
//												</div>
//											</td>
//											<td width="33%">
//												<div class="SilverButton2">
//													See Crux Features
//												</div>
//											</td>
//										</tr>
//									</table>
//								</div>
//							</gwt:html>
//						</gwt:cell>
//					</gwt:row>
//				</gwt:flexTable>
//				
//				
//			</gwt:simplePanel>
//		</gwt:widget>
//	</gwt:cell>
//	<gwt:cell>
//		<gwt:html>
//			<div class="BgBlack">
//				<span>Aproch. Time: 5min</span>
//			</div>
//		</gwt:html>
//	</gwt:cell>
//</gwt:row>
//<gwt:row>
//	<gwt:cell>
//		<gwt:html>
//			<div class="BgBlack">
//				<span><b>What will be learned:</b></span>
//				<br />
//				<span>How to start a project with Crux and Eclipse and how to make a simple Hello World.</span>
//			</div>
//		</gwt:html>
//	</gwt:cell>
//</gwt:row>
//<gwt:row>
//	<gwt:cell>
//		<gwt:html>
//			<div class="SilverButton">
//				Go to
//				private Image tutorialImage;
//				private Label title;
//				private Label subTitle; Tutorial
//			</div>
//		</gwt:html>
//	</gwt:cell>
//</gwt:row>
//</gwt:flexTable>