<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:x="mailto:vro@enonic.com?subject=foobar"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:output method="html" />
	
	<xsl:include href="tree/displaytree.xsl"/>
	<xsl:include href="common/button.xsl"/>
	
	<xsl:param name="page"/>
  <xsl:param name="cat"/>

	<xsl:template match="/">
		<html>
			<head>
				<link rel="stylesheet" type="text/css" href="css/admin.css" />
				<link rel="stylesheet" type="text/css" href="css/menu.css" />
				<script type="text/javascript" src="javascript/menu.js">//</script>
				<script type="text/javascript" src="javascript/admin.js">//</script>
				
        <script type="text/javascript">
          var branchOpen = new Array;
        </script>
			</head>
      <body onload="javascript:openTree();" id="popup-body">
        <div class="info" style="white-space: nowrap;">
          "<xsl:value-of select="/resource/@fullPath"/>"<br/>
          %txtInUseBy%:
        </div>
        <p style="margin-top:0">
          <xsl:apply-templates select="/resource/usedBy/site" mode="displaytree">
          </xsl:apply-templates>
        </p>
      </body>
		</html>
	</xsl:template>
	
	<xsl:template match="contentObject | contentObjects" mode="icon">
		<xsl:text>icon_objects</xsl:text>
	</xsl:template>
	
	<xsl:template match="pageTemplate | pageTemplates" mode="icon">
		<xsl:text>icon_frameworks</xsl:text>
	</xsl:template>
	
	<xsl:template match="site" mode="icon">
		<xsl:text>icon_site</xsl:text>
	</xsl:template>
	
	<xsl:template match="contentObjects" mode="text">
		<xsl:text>%mnuPortlets%</xsl:text>
	</xsl:template>
	
	<xsl:template match="contentObjects | pageTemplates" mode="key">
		<xsl:value-of select="parent::node()/@key"/>
	</xsl:template>
	
	<xsl:template match="site" mode="text">
		<xsl:value-of select="@name"/>
		<xsl:if test="@defaultCss = 'true'">
			<xsl:text> (default css)</xsl:text>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="pageTemplates" mode="text">
		<xsl:text>%mnuFrameworks%</xsl:text>
	</xsl:template>
	
	<xsl:template match="contentTypes" mode="text">
		<xsl:text>%mnuContentTypes%</xsl:text>
	</xsl:template>
	
	<xsl:template match="contentTypes | contentType" mode="icon">
		<xsl:text>icon_contenttypes</xsl:text>
	</xsl:template>
	
	<xsl:template match="usedBy" mode="icon">
		<xsl:text>icon_folder_resources</xsl:text>
	</xsl:template>
	
	<xsl:template match="usedBy" mode="text">
		<xsl:value-of select="parent::node()/@fullPath"/>
	</xsl:template>

</xsl:stylesheet>
