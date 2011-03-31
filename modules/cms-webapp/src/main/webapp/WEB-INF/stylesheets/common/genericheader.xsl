<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:template name="genericheader">
        <xsl:param name="sitelink" select="true()"/>
        <xsl:param name="endslash" select="true()"/>
        <xsl:param name="links" select="true()"/>

        <xsl:param name="subop"/>
        <xsl:param name="fieldrow"/>
        <xsl:param name="fieldname"/>
        <xsl:param name="minoccurrence"/>
        <xsl:param name="maxoccurrence"/>

        <xsl:if test="$unitname != ''">
            <!--xsl:if test="not($endslash)">
                <xsl:text> / </xsl:text>
            </xsl:if-->

            <xsl:choose>
                <xsl:when test="$links">
                    <a>
                      <xsl:attribute name="href">
                        <xsl:text>adminpage?page=600&amp;op=browse&amp;selecteddomainkey=</xsl:text>
                        <xsl:value-of select="$selecteddomainkey"/>
                        <xsl:if test="$subop">
                          <xsl:text>&amp;subop=</xsl:text>
                          <xsl:value-of select="$subop"/>
                        </xsl:if>
                        <xsl:if test="$fieldrow">
                          <xsl:text>&amp;fieldrow=</xsl:text>
                          <xsl:value-of select="$fieldrow"/>
                        </xsl:if>
                        <xsl:if test="$fieldname">
                          <xsl:text>&amp;fieldname=</xsl:text>
                          <xsl:value-of select="$fieldname"/>
                        </xsl:if>
                        <xsl:if test="$minoccurrence">
                          <xsl:text>&amp;minoccurrence=</xsl:text>
                          <xsl:value-of select="$minoccurrence"/>
                        </xsl:if>
                        <xsl:if test="$maxoccurrence">
                          <xsl:text>&amp;maxoccurrence=</xsl:text>
                          <xsl:value-of select="$maxoccurrence"/>
                        </xsl:if>
                      </xsl:attribute>
                      <xsl:text>%headContentRepositories%</xsl:text>
                    </a>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>%headContentRepositories%</xsl:text>
                </xsl:otherwise>
            </xsl:choose>

            <xsl:if test="$endslash">
                <xsl:text>&nbsp;/&nbsp;</xsl:text>
            </xsl:if>
        </xsl:if>

        <xsl:if test="$menuname">
            <xsl:if test="not($endslash)">
                <xsl:text>&nbsp;/&nbsp;</xsl:text>
            </xsl:if>

            <xsl:choose>
                <xsl:when test="$links">
                    <a>
                        <xsl:attribute name="href">
                            <xsl:text>adminpage?op=listmenus&amp;page=851</xsl:text>
                            <xsl:text>&amp;selectedunitkey=</xsl:text><xsl:value-of select="$selectedunitkey"/>
                            <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
                            <xsl:if test="$minoccurrence">
                              <xsl:text>&amp;minoccurrence=</xsl:text>
                              <xsl:value-of select="$minoccurrence"/>
                            </xsl:if>
                            <xsl:if test="$maxoccurrence">
                              <xsl:text>&amp;maxoccurrence=</xsl:text>
                              <xsl:value-of select="$maxoccurrence"/>
                            </xsl:if>
                        </xsl:attribute>
                        <xsl:text>%headMenus%</xsl:text>
                    </a>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>%headMenus%</xsl:text>
                </xsl:otherwise>
            </xsl:choose>

            <xsl:text>&nbsp;/&nbsp;</xsl:text>

            <xsl:choose>
                <xsl:when test="$links">
                    <xsl:variable name="href">
                      <xsl:text>adminpage?page=851&amp;op=browse&amp;selecteddomainkey=</xsl:text>
                      <xsl:value-of select="$selecteddomainkey"/>
                      <xsl:text>&amp;selectedunitkey=</xsl:text>
                      <xsl:value-of select="$selectedunitkey"/>
                      <xsl:text>&amp;menukey=</xsl:text>
                      <xsl:value-of select="$menukey"/>
                      <xsl:if test="$minoccurrence">
                        <xsl:text>&amp;minoccurrence=</xsl:text>
                        <xsl:value-of select="$minoccurrence"/>
                      </xsl:if>
                      <xsl:if test="$maxoccurrence">
                        <xsl:text>&amp;maxoccurrence=</xsl:text>
                        <xsl:value-of select="$maxoccurrence"/>
                      </xsl:if>
                    </xsl:variable>
                    <a href="{$href}">
                        <xsl:value-of select="$menuname"/>
                    </a>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$menuname"/>
                </xsl:otherwise>
            </xsl:choose>

            <xsl:if test="$endslash">
                <xsl:text>&nbsp;/&nbsp;</xsl:text>
            </xsl:if>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
