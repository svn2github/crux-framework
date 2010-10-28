<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:crux="http://www.sysmap.com.br/crux"
	xmlns:html="http://www.w3.org/1999/xhtml"
	xmlns:exsl="http://exslt.org/common"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:f="http://www.sysmap.com.br/functions"  
	exclude-result-prefixes="#all"
	extension-element-prefixes="html crux exsl xsl">

	<xsl:output method="xhtml" encoding="${charset}" indent="${indent}" exclude-result-prefixes="#all" omit-xml-declaration="yes" escape-uri-attributes="no" />

	<xsl:param name="xhtmlNS" select="'http://www.w3.org/1999/xhtml'"></xsl:param>
	<xsl:param name="allWidgets" select="'${allWidgets}'"></xsl:param>
	<xsl:param name="referencedWidgets" select="'${referencedWidgets}'"></xsl:param>

	<!-- 
	=====================================================================
	  Utility functions
	=====================================================================
	-->

	<!-- indexOf -->
	<xsl:function name="f:index-of-string" as="xs:integer*" >
	  <xsl:param name="arg" as="xs:string?"/> 
	  <xsl:param name="substring" as="xs:string"/> 
	 
	  <xsl:sequence select=" 
	  if (contains($arg, $substring))
	  then (string-length(substring-before($arg, $substring))+1,
	        for $other in
	           f:index-of-string(substring-after($arg, $substring),
	                               $substring)
	        return
	          $other +
	          string-length(substring-before($arg, $substring)) +
	          string-length($substring))
	  else ()
	 "/>	   
	</xsl:function>

	<!-- indexOfFirst -->
	<xsl:function name="f:index-of-string-first" as="xs:integer?" >
	  <xsl:param name="arg" as="xs:string?"/> 
	  <xsl:param name="substring" as="xs:string"/> 
	 
	  <xsl:sequence select=" 
	  if (contains($arg, $substring))
	  then string-length(substring-before($arg, $substring))+1
	  else ()
	 "/>
	</xsl:function>

	<!-- indexOfLast -->
	<xsl:function name="f:index-of-string-last" as="xs:integer?" >
	  <xsl:param name="arg" as="xs:string?"/> 
	  <xsl:param name="substring" as="xs:string"/> 
	 
	  <xsl:sequence select=" 
	  f:index-of-string($arg, $substring)[last()]
	 "/>
	</xsl:function>

	<!-- getTagType -->
	<xsl:function name="f:getTagType" as="xs:string?">
	  <xsl:param name="arg" as="xs:string?"/> 
      <xsl:variable name="internalIndex" select="(f:index-of-string-first($referencedWidgets, $arg) + string-length($arg) + 2)"></xsl:variable>
      <xsl:choose>
      	<xsl:when test="$internalIndex > 0">
      		<xsl:variable name="substringTag" select="substring($referencedWidgets, $internalIndex)"></xsl:variable>
      		<xsl:sequence select=" 
			  if (contains($referencedWidgets, $arg))
			  then substring($substringTag, 0, f:index-of-string-first($substringTag,'|'))
			  else ()
			 " />
      	</xsl:when>
      	<xsl:otherwise>
      		<xsl:sequence select="()"/>
      	</xsl:otherwise>
      </xsl:choose>
	</xsl:function>
	
	<!-- getTagName -->
	<xsl:function name="f:getTagName" as="xs:string?">
	  <xsl:param name="elem" as="node()*"/> 
	  <xsl:param name="elemName" as="xs:string?"/> 
	  
	  <xsl:variable name="libraryName" select="f:getLibraryName($elem/..)" />
	  
	  <xsl:choose>
	  	  <xsl:when test="string-length($libraryName) = 0 or f:isWidget($libraryName, $elem/../local-name())">
		  	  <xsl:sequence select="concat($libraryName, '_', $elem/../local-name(), '_', $elemName)"/>
	  	  </xsl:when>
	  	  <xsl:otherwise>
		  	  <xsl:sequence select="f:getTagName($elem/.., $elemName)"/>
	  	  </xsl:otherwise>
	  </xsl:choose>
	</xsl:function>

	<!-- escapeString -->
	<xsl:function name="f:escapeString" as="xs:string?">
	  <xsl:param name="aString" as="xs:string?"/> 
	  <xsl:variable name="backslash" select="replace($aString, '&#92;&#92;', '&#92;&#92;&#92;&#92;')" />
	  <xsl:variable name="quote" select="replace($backslash, '&quot;', '&#92;&#92;&quot;')" />
	  <xsl:variable name="lineFeed" select="replace($quote, '&#10;', '&#92;&#92;n')" />
	  <xsl:variable name="carriageReturn" select="replace($lineFeed, '&#13;', '&#92;&#92;r')" />
	  <xsl:variable name="tab" select="replace($carriageReturn, '&#09;', '&#92;&#92;t')" />
	  <xsl:sequence select="$tab"/>
	</xsl:function>


	<!-- isWidget -->
	<xsl:function name="f:isWidget" as="xs:boolean?">
	  <xsl:param name="libraryName" as="xs:string?"/> 
	  <xsl:param name="elemName" as="xs:string?"/> 
  	  <xsl:sequence select="contains($allWidgets, concat(',', $libraryName, '_', $elemName, ','))"/>
	</xsl:function>

	<!-- isReferencedWidget -->
	<xsl:function name="f:isReferencedWidget" as="xs:boolean?">
	  <xsl:param name="tagName" as="xs:string?"/> 
  	  <xsl:sequence select="contains($referencedWidgets, concat(',', $tagName, ','))"/>
	</xsl:function>

	<!-- isHTMLChild -->
	<xsl:function name="f:isHTMLChild" as="xs:boolean?">
	  <xsl:param name="elem" as="node()*"/> 
  	  <xsl:sequence select="namespace-uri($elem/..) = $xhtmlNS"/>
	</xsl:function>

	<!-- getLibraryName -->
	<xsl:function name="f:getLibraryName" as="xs:string?">
	  <xsl:param name="elem" as="node()*"/> 
	
	  <xsl:choose>
	      <xsl:when test="string-length(namespace-uri($elem)) > 30 and (contains(namespace-uri($elem), 'http://www.sysmap.com.br/crux/'))">
	      	 <xsl:sequence select="substring(namespace-uri($elem),31)" />
	      </xsl:when>
	  	  <xsl:otherwise>
	  	  	 <xsl:sequence select="()" />
	  	  </xsl:otherwise>
	  </xsl:choose>
	</xsl:function>
		
	<!-- 
	=====================================================================
	  Starts the transformation over the root element (HTML)
	=====================================================================
	-->
	<xsl:template match="/">
		<xsl:call-template name="handleInnerHtml" />
	</xsl:template>

	<!-- 
	=====================================================================
	  Process Crux widgets core tags
	=====================================================================
	-->
	<xsl:template name="cruxSplashScreen">
		<xsl:element name="div" namespace="{$xhtmlNS}">
			<xsl:attribute name="id" select="'cruxSplashScreen'"/>			
			<xsl:if test="string-length(@style) > 0">
				<xsl:attribute name="style" select="@style"/>			
			</xsl:if>
			<xsl:if test="string-length(@transactionDelay) > 0">
				<xsl:attribute name="_transactionDelay" select="@transactionDelay"/>			
			</xsl:if>
			<xsl:copy-of select="child::*" copy-namespaces="no"/>
		</xsl:element> 
	</xsl:template>

	<xsl:template name="cruxScreenMetaTag" >
		<xsl:value-of select="'{'"></xsl:value-of>
			<xsl:value-of select="'&quot;type&quot;:&quot;screen&quot;'"></xsl:value-of>
			<xsl:for-each select="current()/@*">
				<xsl:value-of select="','"></xsl:value-of>
				<xsl:call-template name="writeMetaAttribute"/>
			</xsl:for-each>
		<xsl:value-of select="'}'"></xsl:value-of>
	</xsl:template>

	<!-- 
	=====================================================================
	  Process Crux widgets libraries
	=====================================================================
	-->
	<xsl:template name="cruxInnerTags">
		<xsl:variable name="libraryName" select="f:getLibraryName(current())" />
		<xsl:variable name="tagName" select="f:getTagName(current(), local-name())" />
		<xsl:choose>
			<xsl:when test="(f:isReferencedWidget($tagName) or f:isWidget($libraryName, local-name())) and f:isHTMLChild(current())">
				<xsl:element name="span" namespace="{$xhtmlNS}">
					<xsl:if test="string-length(@id) > 0">
						<xsl:attribute name="id" select="concat('_crux_', @id)" />
					</xsl:if>
					<xsl:call-template name="handleInnerHtml" />
				</xsl:element> 
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="handleInnerHtml" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- 
	=====================================================================
	  Write tag attribute
	=====================================================================
	-->
	<xsl:template name="writeMetaAttribute">
		<xsl:value-of select="concat('&quot;', name(), '&quot;:&quot;', ., '&quot;')"></xsl:value-of>
	</xsl:template>	
	<!-- 
	=====================================================================
	  Process Crux widgets libraries
	=====================================================================
	-->
	
	<xsl:template name="cruxInnerMetaTags">
		<xsl:param name="libraryName" select="f:getLibraryName(current())"></xsl:param>
		<xsl:param name="innerText" select="string-join(text(), '')"></xsl:param>
		<xsl:value-of select="'{'"></xsl:value-of>
			<xsl:variable name="tagName" select="f:getTagName(current(), local-name())" />
			<xsl:choose>
				<xsl:when test="f:isReferencedWidget($tagName)">
					<xsl:value-of select="concat('&quot;type&quot;:&quot;', f:getTagType($tagName), '&quot;')"></xsl:value-of>
				</xsl:when>
				<xsl:when test="f:isWidget($libraryName, local-name())">
					<xsl:value-of select="concat('&quot;type&quot;:&quot;', $libraryName, '_', local-name(), '&quot;')"></xsl:value-of>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat('&quot;childTag&quot;:&quot;', local-name(), '&quot;')"></xsl:value-of>
				</xsl:otherwise>
			</xsl:choose>			
			
			<xsl:if test="string-length(normalize-space($innerText)) > 0">
				<xsl:value-of select="concat(',&quot;_text&quot;:&quot;', f:escapeString($innerText), '&quot;')"></xsl:value-of>
			</xsl:if>			
			<xsl:for-each select="current()/@*">
				<xsl:value-of select="','"></xsl:value-of>
				<xsl:call-template name="writeMetaAttribute"/>
			</xsl:for-each>
			
			<xsl:if test="count(child::*) > 0 ">
				<xsl:value-of select="',&quot;children&quot;:['"></xsl:value-of>
				<xsl:call-template name="createWidgetsMetaData" />
				<xsl:value-of select="']'"></xsl:value-of>
			</xsl:if>
			<xsl:value-of select="'}'"></xsl:value-of>
	</xsl:template>

	<!-- 
	=====================================================================
	  Recursively navigates through the document nodes, extracting crux
	  meta tags and building a json array to represent that meta data
	=====================================================================
	-->
	<xsl:template name="createWidgetsMetaData">
		<xsl:for-each select="child::*">
			<xsl:choose>
				<xsl:when test="namespace-uri() = 'http://www.sysmap.com.br/crux' and local-name() = 'screen'">
					<xsl:call-template name="cruxScreenMetaTag" />
					<xsl:if test="position() != last() or f:isHTMLChild(current())">
						<xsl:value-of select="','"></xsl:value-of>
					</xsl:if>
				</xsl:when>
				<xsl:when test="string-length(namespace-uri()) > 30 and (contains(namespace-uri(), 'http://www.sysmap.com.br/crux/'))">
					<xsl:call-template name="cruxInnerMetaTags" />
					<xsl:if test="position() != last() or f:isHTMLChild(current())">
						<xsl:value-of select="','"></xsl:value-of>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="createWidgetsMetaData" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	<!-- 
	=====================================================================
	  Recursively navigates through the document nodes, copying the 
	  HTML nodes and delegating crux nodes to its handler
	=====================================================================
	-->
	<xsl:template name="handleInnerHtml">
		<xsl:for-each select="child::*">
			<xsl:choose>
				<xsl:when test="namespace-uri() = 'http://www.sysmap.com.br/crux'">
					<xsl:choose>
						<xsl:when test="local-name() = 'splashScreen'">
							<xsl:call-template name="cruxSplashScreen" />
						</xsl:when>
						<xsl:when test="local-name() = 'screen'">
							<xsl:call-template name="handleInnerHtml" />
						</xsl:when>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="string-length(namespace-uri()) > 30 and (contains(namespace-uri(), 'http://www.sysmap.com.br/crux/'))">
					<xsl:call-template name="cruxInnerTags" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="count(child::*) = 0 and string-length(local-name()) > 0">
							<xsl:copy copy-namespaces="no">
								<xsl:copy-of select="@*"/>
								<xsl:if test="string-length(text()) > 0">
									<xsl:value-of select="text()"></xsl:value-of>
								</xsl:if>
							</xsl:copy>
						</xsl:when>
						<xsl:when test="count(child::*) = 0 and string-length(local-name()) = 0">
							<xsl:copy-of select="current()"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy copy-namespaces="no">
								<xsl:copy-of select="@*"/>
								<xsl:call-template name="handleInnerHtml" />
								<xsl:if test="namespace-uri() = $xhtmlNS and local-name() = 'body'">
									<xsl:element name="div" namespace="{$xhtmlNS}">
										<xsl:attribute name="id" select="'__CruxMetaData_'" />
										<xsl:attribute name="style" select="'display: none;'" />
										<!-- xsl:comment-->	
											<xsl:value-of select="'['"></xsl:value-of>
											<xsl:call-template name="createWidgetsMetaData" />
											<xsl:value-of select="']'"></xsl:value-of>
										<!-- /xsl:comment-->
									</xsl:element>
								</xsl:if>
							</xsl:copy>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>