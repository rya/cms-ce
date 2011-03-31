<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:include href="ContentEnhancedImageHandlerServlet.xsl"/>
    <xsl:include href="ContentFileHandlerServlet.xsl"/>
    <xsl:include href="ContentNewsletterHandlerServlet.xsl"/>
    <xsl:include href="ContentTestResultHandlerServlet.xsl"/>
    <xsl:include href="ContentFormHandlerServlet.xsl"/>

    <xsl:template match="attribute::*" mode="icon">
      <xsl:text>folder</xsl:text>
    </xsl:template>
	
    <xsl:template match="attribute::*" mode="operations"/>
    
    <xsl:template match="attribute::*" mode="newbutton">
      <xsl:value-of select="true()"/>
    </xsl:template>
	
    <xsl:template match="attribute::*" mode="custombuttons_center"/>

</xsl:stylesheet>
