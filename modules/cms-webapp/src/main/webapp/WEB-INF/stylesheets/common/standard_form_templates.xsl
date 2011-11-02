<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <xsl:param name="alwaysdisabled" select="false()"/>
  <xsl:param name="editlockedversionmode" select="false()"/>

  <xsl:include href="generic_parameters.xsl"/>
  <xsl:include href="dropdown.xsl"/>
  <xsl:include href="contentformbuttons.xsl"/>
  <xsl:include href="button.xsl"/>  
  <xsl:include href="searchfield.xsl"/>
  <xsl:include href="dropdown_language.xsl"/>
  <xsl:include href="readonlyvalue.xsl"/>
  <xsl:include href="readonlydatetime.xsl"/>
  <xsl:include href="checkbox_boolean.xsl"/>
  <xsl:include href="filefield.xsl"/>
  <xsl:include href="enhancedimage_selector.xsl"/>
  <xsl:include href="enhancedimage_multiple_selector.xsl"/>

  <xsl:include href="textfielddatetime.xsl"/>
  <xsl:include href="properties.xsl"/>
  <xsl:include href="publishing.xsl"/>

  <xsl:include href="displayfheader.xsl"/>
  <xsl:include href="generalhiddenfields.xsl"/>
  <xsl:include href="genericheader.xsl"/>
  <xsl:include href="categoryheader.xsl"/>
  <xsl:include href="contentheader.xsl"/>
  <xsl:include href="formatdate.xsl"/>
  <xsl:include href="status_and_versionstuff.xsl"/>
  <xsl:include href="publishstatus.xsl"/>
  <xsl:include href="content_accessrights.xsl"/>
  <xsl:include href="content_usedby.xsl"/>
  <xsl:include href="content_source.xsl"/>
  <xsl:include href="contentform.xsl"/>
  <xsl:include href="displayerror.xsl"/>
  <xsl:include href="displayfeedback.xsl"/>
  <xsl:include href="browse_table_js.xsl"/>
  <xsl:include href="waitsplash.xsl"/>
  <xsl:include href="assignee-status.xsl"/>
  <xsl:include href="serialize.xsl"/>

  <xsl:variable name="categorypublish" select="not(/contents/userright) or /contents/userright/@publish = 'true'"/>
  <xsl:variable name="contentupdate" select="/contents/content/userright/@update = 'true'"/>
  <xsl:variable name="categorycreate" select="not(/contents/userright) or /contents/userright/@create = 'true'"/>
  <xsl:variable name="categoryadmin" select="not(/contents/userright) or /contents/userright/@administrate = 'true'"/>

  <xsl:variable name="new" select="$create = 1"/>
  <xsl:variable name="draft" select="/contents/content/@status = 0 or $new"/>
  <xsl:variable name="waitingforapproval" select="/contents/content/@status = 1"/>
  <xsl:variable name="published" select="/contents/content/@state = 5"/>

  <xsl:variable name="approved" select="/contents/content/@status = 2"/>
  <xsl:variable name="archived" select="/contents/content/@status = 3"/>

  <xsl:variable name="current" select="/contents/content/@current = 'true'"/>
  <xsl:variable name="currentisdraft" select="$create = 1 or /contents/content/versions/version[@current = 'true']/@state = '0'"/>
  <xsl:variable name="hasversions" select="count(/contents/content/versions/version) > 1"/>

  <xsl:variable name="enableform" select="not($alwaysdisabled) and ( $draft and ($categorycreate or $contentupdate or $categorypublish)) or ( $editlockedversionmode and ($categorycreate or $contentupdate or $categorypublish))"/>

</xsl:stylesheet>
