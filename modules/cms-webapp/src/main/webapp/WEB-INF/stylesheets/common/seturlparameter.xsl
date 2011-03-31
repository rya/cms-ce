<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:template name="seturlparameter">
    	<xsl:param name="url"/>
    	<xsl:param name="parameter"/>
    	<xsl:param name="value"/>
    	
    	<xsl:choose>
    		<!-- when there are no parameters in the url -->
    		<xsl:when test="not(contains($url, '?'))">
    			<xsl:value-of select="$url"/>
    			<xsl:text>?</xsl:text>
    			<xsl:value-of select="$parameter"/>
				<xsl:text>=</xsl:text>
				<xsl:value-of select="$value"/>
    		</xsl:when>
    		<xsl:otherwise>
    			<xsl:variable name="address">
    				<xsl:value-of select="substring-before($url, '?')"/>
    			</xsl:variable>
    			<xsl:variable name="paramstring">
    				<xsl:value-of select="substring-after($url, '?')"/>
    			</xsl:variable>
    			
    			<xsl:value-of select="$address"/>
    			<xsl:text>?</xsl:text>

				<xsl:choose>
					<xsl:when test="contains($paramstring, $parameter)">
						<xsl:variable name="beforeparam">
							<xsl:value-of select="substring-before($paramstring, $parameter)"/>
						</xsl:variable>
						<xsl:variable name="afterparam">
							<xsl:value-of select="substring-after($paramstring, $parameter)"/>
						</xsl:variable>
						<xsl:value-of select="$beforeparam"/>
						<xsl:value-of select="$parameter"/>
						<xsl:text>=</xsl:text>
						<xsl:value-of select="$value"/>
						<xsl:if test="contains($afterparam, '&amp;')">
							<xsl:text>&amp;</xsl:text>
							<xsl:value-of select="substring-after($afterparam, '&amp;')"/>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$paramstring"/>
						<xsl:text>&amp;</xsl:text>
						<xsl:value-of select="$parameter"/>
						<xsl:text>=</xsl:text>
						<xsl:value-of select="$value"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
    	
    </xsl:template>
    
</xsl:stylesheet>
