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
	xmlns:crux="http://www.sysmap.com.br/crux/basic-widgets/1.0"
	xmlns:html="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="#all">

	
	<xsl:param name="xhtmlNS" select="'http://www.w3.org/1999/xhtml'"></xsl:param>

	<xsl:template name="listBox" match="crux:listBox">
		
		<xsl:element name="span" namespace="{$xhtmlNS}">
			
			<xsl:call-template name="widgetAttributes"/>
			
			<xsl:if test="string-length(@multiple) > 0">
				<xsl:attribute name="_multiple" select="@multiple"/>
			</xsl:if>
			
			<xsl:if test="string-length(@visibleItemCount) > 0">
				<xsl:attribute name="_visibleItemCount" select="@visibleItemCount"/>
			</xsl:if>
			
			<xsl:for-each select="child::*">
				<xsl:element name="span" namespace="{$xhtmlNS}">
					<xsl:if test="string-length(@value) > 0">
						<xsl:attribute name="_value" select="@value"/>
					</xsl:if>					
					<xsl:attribute name="_item" select="@label"/>
					<xsl:if test="string-length(@selected) > 0">
						<xsl:attribute name="_selected" select="@selected"/>
					</xsl:if>
					<xsl:value-of select="' '"/>
				</xsl:element>
			</xsl:for-each>

			<xsl:value-of select="' '"/>

		</xsl:element> 

	</xsl:template>
	
</xsl:stylesheet>