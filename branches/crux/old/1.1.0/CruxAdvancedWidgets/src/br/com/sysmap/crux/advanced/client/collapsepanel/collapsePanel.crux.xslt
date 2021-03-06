<!--
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
 *-->
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:adv="http://www.sysmap.com.br/crux/advanced-widgets/1.0"
	xmlns:crux="http://www.sysmap.com.br/crux/basic-widgets/1.0"
	xmlns:html="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="#all">

	<xsl:param name="xhtmlNS" select="'http://www.w3.org/1999/xhtml'"></xsl:param>

	<xsl:template name="collapsePanel" match="adv:collapsePanel">
		<xsl:element name="span" namespace="{$xhtmlNS}">
			<xsl:call-template name="widgetAttributes"/>
			<xsl:if test="string-length(@collapsible) > 0">
				<xsl:attribute name="_collapsible" select="@collapsible"/>
			</xsl:if>
			<xsl:if test="string-length(@collapsed) > 0">
				<xsl:attribute name="_collapsed" select="@collapsed"/>
			</xsl:if>			
			<xsl:if test="string-length(@onBeforeExpand) > 0">
				<xsl:attribute name="_onbeforeexpand" select="@onBeforeExpand"/>
			</xsl:if>
			<xsl:if test="string-length(@onBeforeCollapse) > 0">
				<xsl:attribute name="_onbeforecollapse" select="@onBeforeCollapse"/>
			</xsl:if>
			<xsl:for-each select="child::*">
				<xsl:element name="span" namespace="{$xhtmlNS}">
					<xsl:attribute name="_part" select="local-name()"/>
					<xsl:for-each select="child::*">
						<xsl:attribute name="_contentType" select="local-name()"/>
						<xsl:choose>
							<xsl:when test="local-name() = 'widget'">
								<xsl:apply-templates select="child::*" />
							</xsl:when>
							<xsl:when test="local-name() = 'text'">
								<xsl:apply-templates select="text()" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:copy-of select="child::*" copy-namespaces="no"/>
							</xsl:otherwise>					
						</xsl:choose>
					</xsl:for-each>
				</xsl:element>
			</xsl:for-each>
		</xsl:element> 
	</xsl:template>
	
</xsl:stylesheet>