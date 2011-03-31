<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:include href="../handlerconfigs/default.xsl"/>

	<xsl:template name="getnodeicon">
		<xsl:choose>
			<xsl:when test="@handler">
				<xsl:apply-templates select="@handler" mode="icon"/>
             </xsl:when>
			<xsl:when test="(name() = 'categories' or name() = 'category')">folder</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>