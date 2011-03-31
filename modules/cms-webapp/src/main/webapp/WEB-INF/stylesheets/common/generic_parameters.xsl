<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:param name="selecteddomainkey"/>
  <xsl:param name="selectedunitkey"/>
  <xsl:param name="menukey"/>
  <xsl:param name="cat"/>
  <xsl:param name="page"/>
  <xsl:param name="referer"/>

  <xsl:param name="current_uid"/>
  <xsl:param name="currentuser_key"/>
  <xsl:param name="currentuser_fullname"/>
  <xsl:param name="currentuser_qualifiedname"/>
  <xsl:param name="currentuser_email"/>
  <xsl:param name="currentuser_has_photo"/>

  <xsl:param name="domainname"/>
  <xsl:param name="unitname"/>
  <xsl:param name="mediatypename"/>
  <xsl:param name="menuname"/>

</xsl:stylesheet>
