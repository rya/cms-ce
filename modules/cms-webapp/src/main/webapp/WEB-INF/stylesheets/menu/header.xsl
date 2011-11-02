<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:include href="getnodetext.xsl"/>
	<xsl:include href="getnodeurl.xsl"/>

	<xsl:template match="node()">
		<xsl:param name="url" select="'adminpage'"/>
		<xsl:param name="usedisable" select="true()"/>
		
		<xsl:variable name="fullurl">
			<xsl:call-template name="getnodeurl">
				<xsl:with-param name="node" select="."/>
				<xsl:with-param name="url" select="$url"/>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:if test="name() = 'site' or name() = 'unit' or name() = 'categories' or name() = 'category'">
			<xsl:variable name="nodetext">
				<xsl:call-template name="getnodetext"/>
			</xsl:variable>
			
			<xsl:choose>
				<xsl:when test="@disable = 'true' and $usedisable">
					<xsl:value-of select="$nodetext"/>
				</xsl:when>
				<xsl:otherwise>
					<a href="{$fullurl}">
						<xsl:value-of select="$nodetext"/>
					</a>
				</xsl:otherwise>
			</xsl:choose>
			
			<xsl:if test="*">
				<xsl:if test="not($nodetext = '')">
					<xsl:text> / </xsl:text>
				</xsl:if>
				<xsl:apply-templates>
					<xsl:with-param name="url" select="$fullurl"/>
					<xsl:with-param name="usedisable" select="$usedisable"/>
				</xsl:apply-templates>
			</xsl:if>
		</xsl:if>
		
	</xsl:template>

</xsl:stylesheet>