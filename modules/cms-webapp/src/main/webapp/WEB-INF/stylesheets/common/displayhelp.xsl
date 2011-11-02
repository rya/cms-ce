<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:template name="displayhelp">
		<xsl:param name="fieldname"/>
        <xsl:param name="helpelement"/>

		<xsl:variable name="helpelem" select="exslt-common:node-set($helpelement)/help"/>
		
		<xsl:variable name="displaystring">
			<xsl:if test="not($helpelem/@default = 'on' or $helpelem/@alwayson = 'true')">
				<xsl:text>display: none;</xsl:text>
			</xsl:if>
		</xsl:variable>

		<xsl:if test="$helpelem">
			<div id="{$fieldname}_help" name="{$fieldname}_help" style="{$displaystring}" class="display-help">
				<xsl:copy-of select="$helpelem/node()"/>
			</div>
		</xsl:if>
			
	</xsl:template>

</xsl:stylesheet>
