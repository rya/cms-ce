<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:template name="displaycontentpath">
		<xsl:param name="key"/>
		<xsl:param name="pathelem"/>
		<xsl:param name="path" select="''"/>
		<xsl:param name="levels" select="1"/>
		<xsl:param name="alwaysdisplaytop" select="true()"/>
		
		<xsl:variable name="parent" select="$pathelem/parent::node()"/>
		<xsl:variable name="grandparent" select="$pathelem/parent::node()/parent::node()"/>
		
		
		
		<xsl:variable name="newpath">
			
			<xsl:choose>
				<!-- When more pathnames should be written or we are at the top level -->
				<xsl:when test="($levels > 0) or ($alwaysdisplaytop and not(name($parent) = name($pathelem) or name($grandparent) = name($pathelem)))">
					<xsl:value-of select="$pathelem/@name"/>
					<xsl:if test="not($path = '')">
						<xsl:text> / </xsl:text><xsl:value-of select="$path"/>
					</xsl:if>
				</xsl:when>
				<!-- At level 0, draw ... -->
				<xsl:when test="$levels = 0">
					<xsl:text>...</xsl:text>
					<xsl:if test="not($path = '')">
						<xsl:text> / </xsl:text><xsl:value-of select="$path"/>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$path"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="not($pathelem)"/>
			<xsl:when test="name($parent) = name($pathelem)">
				<xsl:call-template name="displaycontentpath">
					<xsl:with-param name="path" select="$newpath"/>
					<xsl:with-param name="pathelem" select="$parent"/>
					<xsl:with-param name="levels" select="$levels - 1"/>
					<xsl:with-param name="alwaysdisplaytop" select="$alwaysdisplaytop"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="name($grandparent) = name($pathelem)">
				<xsl:call-template name="displaycontentpath">
					<xsl:with-param name="path" select="$newpath"/>
					<xsl:with-param name="pathelem" select="$grandparent"/>
					<xsl:with-param name="levels" select="$levels - 1"/>
					<xsl:with-param name="alwaysdisplaytop" select="$alwaysdisplaytop"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$newpath"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>

</xsl:stylesheet>