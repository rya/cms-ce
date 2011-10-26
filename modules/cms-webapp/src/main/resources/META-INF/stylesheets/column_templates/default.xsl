<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:include href="title.xsl"/>
  <xsl:include href="owner.xsl"/>
  <xsl:include href="modifier.xsl"/>
  <xsl:include href="created.xsl"/>
  <xsl:include href="timestamp.xsl"/>
  <xsl:include href="image.xsl"/>
  <xsl:include href="xpath.xsl"/>
  <xsl:include href="text.xsl"/>
  <xsl:include href="filename.xsl"/>
  <xsl:include href="filesize.xsl"/>
  <xsl:include href="attachmentlink.xsl"/>
  <xsl:include href="key.xsl"/>
  <xsl:include href="date.xsl"/>
  <xsl:include href="mailto.xsl"/>
  <xsl:include href="contenttype.xsl"/>
  <xsl:include href="number.xsl"/>

  <xsl:template match="*" mode="display"/>
	
  <xsl:template match="*" mode="title"/>
	
  <xsl:template match="*" mode="orderby"/>
	
  <xsl:template match="*" mode="width"/>
	
  <xsl:template match="*" mode="titlealign"/>

  <xsl:template match="*" mode="columnalign"/>

  <xsl:template match="*" mode="clickable"/>

</xsl:stylesheet>
