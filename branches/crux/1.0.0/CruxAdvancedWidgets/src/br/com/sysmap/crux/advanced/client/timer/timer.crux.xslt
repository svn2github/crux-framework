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
	xmlns:html="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="#all">

	<xsl:param name="xhtmlNS" select="'http://www.w3.org/1999/xhtml'"></xsl:param>

	<xsl:template name="timer" match="adv:timer">
		<xsl:element name="span" namespace="{$xhtmlNS}">
			<xsl:call-template name="widgetAttributes" />
			<xsl:if test="string-length(@initial) > 0">
				<xsl:attribute name="_initial" select="@initial" />
			</xsl:if>
			<xsl:if test="string-length(@regressive) > 0">
				<xsl:attribute name="_regressive" select="@regressive" />
			</xsl:if>
			<xsl:if test="string-length(@start) > 0">
				<xsl:attribute name="_start" select="@start" />
			</xsl:if>
			<xsl:for-each select="child::*">
				<xsl:element name="span" namespace="{$xhtmlNS}">
					<xsl:if test="string-length(@time) > 0">
						<xsl:attribute name="_time" select="@time" />
					</xsl:if>
					<xsl:if test="string-length(@execute) > 0">
						<xsl:attribute name="_execute" select="@execute" />
					</xsl:if>
					<xsl:value-of select="' '"/>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
		<xsl:value-of select="' '"/>
	</xsl:template>	
</xsl:stylesheet>