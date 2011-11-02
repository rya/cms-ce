<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>
    
    <xsl:template name="readonlyurl">
        <xsl:param name="label" select="''"/>
        <xsl:param name="name" select="''"/>
        <xsl:param name="selectnode"/>
        <xsl:param name="colspan" select="''"/>
        <xsl:param name="href" select="''"/>
        <xsl:param name="target" select="''"/>
        
        <xsl:if test="$label != ''">
            <td class="form_labelcolumn" valign="baseline">
                <xsl:value-of select="$label"/>
            </td>
        </xsl:if>
        
        <td>
            <xsl:if test="$colspan != ''">
                <xsl:attribute name="colspan">
                    <xsl:value-of select="$colspan"/>
                </xsl:attribute>
            </xsl:if>

            <xsl:choose>
                <xsl:when test="$href != ''">
                    <a>
                        <xsl:attribute name="href">
                            <xsl:value-of select="$href"/>
                        </xsl:attribute>
                        <xsl:if test="$target != ''">
                            <xsl:attribute name="target">
                                <xsl:value-of select="$target"/>
                            </xsl:attribute>
                        </xsl:if>
                        <xsl:value-of select="$selectnode"/>
                    </a>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$selectnode"/>
                </xsl:otherwise>
            </xsl:choose>

            <xsl:if test="$name != ''">
                <input type="hidden">
                    <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
                    <xsl:attribute name="value"><xsl:value-of select="$selectnode"/></xsl:attribute>
                </input>
            </xsl:if>
        </td>
    </xsl:template>

</xsl:stylesheet>