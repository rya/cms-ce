<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp   "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:x="mailto:vro@enonic.com?subject=foobar"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html" indent="yes"/>
    
    <xsl:include href="content_import_base.xsl"/>

    <xsl:variable name="importtype" select="'image'"/>

    <xsl:template name="step1_description">
      <xsl:text>%txtDescSelectFoldersAndImageFiles%</xsl:text>
    </xsl:template>

</xsl:stylesheet>
