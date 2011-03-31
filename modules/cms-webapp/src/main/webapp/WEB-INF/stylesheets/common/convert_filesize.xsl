<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

    <xsl:output method="html"/>

    <xsl:template name="convert_filesize">
        <xsl:param name="fsize"/>
        <xsl:choose>
            <xsl:when test="$fsize &gt;= 1073741824">
                <xsl:value-of select="format-number($fsize div 1073741824, '###0')"/> GB
            </xsl:when>
            <xsl:when test="$fsize &gt;= 1048576">
                <xsl:value-of select="format-number($fsize div 1048576, '###0')"/> MB
            </xsl:when>
            <xsl:when test="$fsize &gt;= 1024">
                <xsl:value-of select="format-number($fsize div 1024, '###0')"/> KB
            </xsl:when>
            <xsl:otherwise>
                1 KB
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>