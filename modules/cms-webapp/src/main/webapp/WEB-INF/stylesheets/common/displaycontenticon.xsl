<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

	<xsl:template name="displaycontenticon">
		<xsl:param name="sectionkey"/>
		<xsl:param name="contentelem"/>
		<xsl:param name="contextelem"/>

		<xsl:variable name="filename">
			<xsl:text>icon</xsl:text>
			<xsl:choose>
				<xsl:when test="$sectionkey">
					<!-- section content -->
					<xsl:text>_menuitem_section</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<!-- category content -->
					<xsl:text>_folder</xsl:text>
					<xsl:if test="$contextelem/@anonaccess = 'false'"><xsl:text>_lock</xsl:text></xsl:if>
					<xsl:if test="$contextelem/@disable = 'true'"><xsl:text>_shaded</xsl:text></xsl:if>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>.gif</xsl:text>
		</xsl:variable>
		<img width="16" height="16" border="0" src="images/{$filename}" align="absmiddle"/>
	</xsl:template>

</xsl:stylesheet>