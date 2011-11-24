<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
        <!ENTITY nbsp "&#160;">
        ]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:template match="image" mode="display">
        <xsl:param name="contentxpath"/>

        <xsl:variable name="width">
            <xsl:choose>
                <xsl:when test="@width and @width != ''">
                    <xsl:value-of select="@width"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>80</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <div class="content-list-image-container">
            <xsl:variable name="imageKey" select="saxon:evaluate(concat($contentxpath, @xpath))"/>
            <xsl:variable name="imageText" select="saxon:evaluate(concat($contentxpath, @textxpath))"/>
            <xsl:if test="$imageKey != ''">
                <img src="_image/{$imageKey}/label/source?_filter=scalemax({$width})">
                    <xsl:if test="$imageText != ''">
                        <xsl:attribute name="alt">
                            <xsl:value-of select="$imageText"/>
                        </xsl:attribute>
                        <xsl:attribute name="style">
                            <xsl:text>
                                float: left;
                                margin-right: 10px;
                            </xsl:text>
                        </xsl:attribute>
                    </xsl:if>
                </img>
            </xsl:if>

            <xsl:if test="$imageText != ''">
                <xsl:value-of select="$imageText"/>
            </xsl:if>
        </div>
    </xsl:template>
</xsl:stylesheet>
