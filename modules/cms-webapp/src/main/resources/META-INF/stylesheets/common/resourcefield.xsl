<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
    ]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:template name="resourcefield">
        <xsl:param name="label" select="''"/>
        <xsl:param name="name"/>
        <xsl:param name="id"/>
        <xsl:param name="mimetype" select="''"/>
        <xsl:param name="extension" select="''"/>
        <xsl:param name="value"/>
        <xsl:param name="required" select="'false'"/>
        <xsl:param name="helpelement"/>
        <xsl:param name="onchange" select="''"/>
        <xsl:param name="exist" select="'true'"/>
        <xsl:param name="valid" select="'true'"/>
        <xsl:param name="removeButton" select="true()"/>
        <xsl:param name="reloadButton" select="false()"/>
        <xsl:param name="disableReloadButton" select="false()"/>
        <xsl:param name="position"/>
        <xsl:param name="inputSize" select="40"/>

        <xsl:variable name="myId">
            <xsl:choose>
                <xsl:when test="$id != ''">
                    <xsl:value-of select="$id"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$name"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:if test="$label != ''">
            <xsl:call-template name="labelcolumn">
                <xsl:with-param name="label" select="$label"/>
                <xsl:with-param name="required" select="$required"/>
                <xsl:with-param name="fieldname" select="$name"/>
                <xsl:with-param name="helpelement" select="$helpelement"/>
                <xsl:with-param name="valign" select="'middle'"/>
            </xsl:call-template>
        </xsl:if>

        <td nowrap="nowrap">
            <xsl:if test="$helpelement">
                <xsl:call-template name="displayhelp">
                    <xsl:with-param name="fieldname" select="$name"/>
                    <xsl:with-param name="helpelement" select="$helpelement"/>
                </xsl:call-template>
            </xsl:if>
            
            <xsl:variable name="tooltip">
                <xsl:choose>
                    <xsl:when test="$exist = 'false'">
                        <xsl:text>%msgResourceNotFound%</xsl:text>
                    </xsl:when>
                    <xsl:when test="$valid = 'false'">
                        <xsl:text>%msgResourceNotValid%</xsl:text>
                    </xsl:when>
                    <xsl:otherwise/>
                </xsl:choose>
            </xsl:variable>
            
            <xsl:variable name="fieldclass">
                <xsl:choose>
                    <xsl:when test="$exist = 'false' or $valid = 'false'">
                        <xsl:text>textfieldred</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>textfield</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>


            <input type="text" readonly="readonly" class="{$fieldclass}" name="{$name}" id="{$myId}" size="{$inputSize}" value="{$value}">
                <xsl:if test="$onchange != ''">
                    <xsl:attribute name="onchange"><xsl:value-of select="$onchange"/></xsl:attribute>
                </xsl:if>
                <xsl:if test="$tooltip != ''">
                    <xsl:attribute name="title"><xsl:value-of select="$tooltip"/></xsl:attribute>
                </xsl:if>
            </input>

            <xsl:call-template name="button">
                <xsl:with-param name="name">btn<xsl:value-of select="$name"/></xsl:with-param>
                <xsl:with-param name="image" select="'images/icon_browse.gif'"/>
                <xsl:with-param name="tooltip" select="'%cmdChoose%'"/>
                <xsl:with-param name="onclick">
                    <xsl:if test="$position">
                        <xsl:text>javascript: paramIndex =</xsl:text>
                        <xsl:value-of select="$position"/>
                        <xsl:text>;</xsl:text>
                    </xsl:if>
                    <xsl:text>OpenResourcePopup('</xsl:text>
                    <xsl:value-of select="$name"/>
                    <xsl:text>', '</xsl:text>
                    <xsl:value-of select="$mimetype"/>
                    <xsl:text>', '</xsl:text>
                    <xsl:value-of select="$extension"/>
                    <xsl:text>');</xsl:text>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:if test="$removeButton">
                <xsl:call-template name="button">
                    <xsl:with-param name="name">remove
                        <xsl:value-of select="$name"/>
                    </xsl:with-param>
                    <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                    <xsl:with-param name="tooltip" select="'%cmdRemove%'"/>
                    <xsl:with-param name="onclick">
                        <xsl:choose>
                            <xsl:when test="$position">
                                <xsl:text>removeUserResourceParam('</xsl:text>
                                <xsl:value-of select="$name"/>
                                <xsl:text>',</xsl:text>
                                <xsl:value-of select="$position"/>
                                <xsl:text>)</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>removeResource('</xsl:text>
                                <xsl:value-of select="$name"/>
                                <xsl:text>');</xsl:text>
                                <xsl:if test="$onchange !=''">
                                    <xsl:text>document.forms['formAdmin']['</xsl:text>
                                    <xsl:value-of select="$name"/>
                                    <xsl:text>'].onchange();</xsl:text>
                                </xsl:if>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
            <xsl:if test="$reloadButton">
                <xsl:call-template name="button">
                    <xsl:with-param name="name">reload<xsl:value-of select="$name"/></xsl:with-param>
                    <xsl:with-param name="image" select="'images/icon_reload.gif'"/>
                    <xsl:with-param name="tooltip" select="'%cmdRefresh%'"/>
                    <xsl:with-param name="disabled" select="$disableReloadButton"/>
                    <xsl:with-param name="onclick">
                        <xsl:text>javascript:updateStyleSheet()</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </td>
    </xsl:template>
</xsl:stylesheet>
