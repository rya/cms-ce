<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:template name="generic_formheader">
        <a href="adminpage?page=600&amp;op=browse&amp;selecteddomainkey={$selecteddomainkey}">
            <xsl:text>%headContentRepositories%</xsl:text>
        </a>
        <xsl:if test="$mediatypename">
            <xsl:text> / </xsl:text><xsl:value-of select="$mediatypename"/><xsl:text> / </xsl:text>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>