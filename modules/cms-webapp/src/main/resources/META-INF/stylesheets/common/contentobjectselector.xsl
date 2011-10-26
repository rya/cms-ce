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

    <xsl:template name="contentobjectselector_multi">
        <xsl:param name="size"/>
        <xsl:param name="name"/>
        <xsl:param name="selectedkey"/>
        <xsl:param name="selectnode"/>
        <xsl:param name="colspan"/>
        <xsl:param name="maxlength"/>
        <xsl:param name="objdoc" select="'foo'"/>
        <xsl:param name="buttonfunction">
            <xsl:text>javascript: OpenSelectorWindowForObjects( this, &apos;</xsl:text>
            <xsl:value-of select="$name"/>
            <xsl:text>&apos;)</xsl:text>
        </xsl:param>
        <xsl:param name="disableup" select="false()"/>
        <xsl:param name="disabledown" select="false()"/>
        <xsl:param name="extraremovefunction" select="''"/>

        <td>
            <xsl:attribute name="colspan"><xsl:value-of select="$colspan"/></xsl:attribute>
            <input type="hidden" name="{$name}objdoc" value="{$objdoc}"/>
            <input type="text" readonly="readonly">
                <xsl:attribute name="name">view<xsl:value-of select="$name"/></xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="$selectnode"/></xsl:attribute>
                <xsl:attribute name="size"><xsl:value-of select="$size"/></xsl:attribute>
                <xsl:attribute name="maxlength"><xsl:value-of select="$maxlength"/></xsl:attribute>
            </input>

            <xsl:call-template name="button">
                <xsl:with-param name="type" select="'button'"/>
              <xsl:with-param name="image" select="'images/icon_browse.gif'"/>
              <xsl:with-param name="tooltip" select="'%cmdChoose%'"/>
                <xsl:with-param name="name">
                    <xsl:text>btn</xsl:text>
                    <xsl:value-of select="$name"/>
                </xsl:with-param>
                <xsl:with-param name="onclick"><xsl:value-of select="$buttonfunction"/></xsl:with-param>
            </xsl:call-template>

            <xsl:call-template name="button">
                <xsl:with-param name="type" select="'button'"/>
                <xsl:with-param name="image" select="'images/icon_edit_small.gif'"/>
                <xsl:with-param name="tooltip" select="'%cmdEdit%'"/>
                <xsl:with-param name="name">btn<xsl:value-of select="$name"/>edit</xsl:with-param>
                <xsl:with-param name="onclick">
                    <xsl:text>editObject(this, '</xsl:text>
                    <xsl:value-of select="$name"/>
                    <xsl:text>');</xsl:text>
                </xsl:with-param>
            </xsl:call-template>
            <input type="hidden">
                <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="$selectedkey"/></xsl:attribute>
            </input>
            <xsl:call-template name="button">
                <xsl:with-param name="type" select="'button'"/>
                <xsl:with-param name="image" select="'images/icon_move_up.gif'"/>
                <xsl:with-param name="tooltip" select="'%cmdMoveUp%'"/>
                <xsl:with-param name="name">btn<xsl:value-of select="$name"/>up</xsl:with-param>
                <xsl:with-param name="onclick">javascript: moveUp('<xsl:value-of select="$name"/>', this )</xsl:with-param>
                <xsl:with-param name="disabled" select="string($disableup)"/>
            </xsl:call-template>
            <xsl:call-template name="button">
                <xsl:with-param name="type" select="'button'"/>
                <xsl:with-param name="image" select="'images/icon_move_down.gif'"/>
                <xsl:with-param name="tooltip" select="'%cmdMoveDown%'"/>
                <xsl:with-param name="name">btn<xsl:value-of select="$name"/>down</xsl:with-param>
                <xsl:with-param name="onclick">javascript: moveDown('<xsl:value-of select="$name"/>', this )</xsl:with-param>
                <xsl:with-param name="disabled" select="string($disabledown)"/>
            </xsl:call-template>
            <xsl:call-template name="button">
                <xsl:with-param name="type" select="'button'"/>
                <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                <xsl:with-param name="tooltip" select="'%cmdRemove%'"/>
                <xsl:with-param name="name">btn<xsl:value-of select="$name"/>del</xsl:with-param>
                <xsl:with-param name="onclick">
                    <xsl:text>removeContentObject(this, '</xsl:text>
                    <xsl:value-of select="$name"/>
                    <xsl:text>');</xsl:text>
                    <xsl:if test="$extraremovefunction != ''">
                        <xsl:text> </xsl:text>
                        <xsl:value-of select="$extraremovefunction"/>
                    </xsl:if>
                </xsl:with-param>
            </xsl:call-template>

        </td>
    </xsl:template>

    <xsl:template name="contentobjectselector">
        <xsl:param name="size"/>
        <xsl:param name="name"/>
        <xsl:param name="selectedkey"/>
        <xsl:param name="selectnode"/>
        <xsl:param name="colspan"/>
        <xsl:param name="maxlength"/>
        <xsl:param name="objdoc" select="'foo'"/>

        <xsl:param name="buttonfunction">
            <xsl:text>javascript: OpenSelectorWindowForObjects( this, &apos;</xsl:text>
            <xsl:value-of select="$name"/>
            <xsl:text>&apos;)</xsl:text>
        </xsl:param>
        <xsl:attribute name="colspan"><xsl:value-of select="$colspan"/></xsl:attribute>
        <input type="hidden" name="{$name}objdoc" value="{$objdoc}"/>
        <input type="text" readonly="readonly">
            <xsl:attribute name="name">view<xsl:value-of select="$name"/></xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="$selectnode"/></xsl:attribute>
            <xsl:attribute name="size"><xsl:value-of select="$size"/></xsl:attribute>
            <xsl:attribute name="maxlength"><xsl:value-of select="$maxlength"/></xsl:attribute>
        </input>

        <xsl:call-template name="button">
            <xsl:with-param name="type" select="'button'"/>
            <xsl:with-param name="image" select="'images/icon_browse.gif'"/>
            <xsl:with-param name="tooltip" select="'%cmdChoose%'"/>
            <xsl:with-param name="name">btn<xsl:value-of select="$name"/></xsl:with-param>
            <xsl:with-param name="onclick"><xsl:value-of select="$buttonfunction"/></xsl:with-param>
        </xsl:call-template>
        <input type="hidden">
            <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
            <xsl:attribute name="value"><xsl:value-of select="$selectedkey"/></xsl:attribute>
        </input>

		    <xsl:call-template name="button">
            <xsl:with-param name="type" select="'button'"/>
            <xsl:with-param name="image" select="'images/icon_edit_small.gif'"/>
          <xsl:with-param name="tooltip" select="'%cmdEdit%'"/>
          <xsl:with-param name="name">btn<xsl:value-of select="$name"/>edit</xsl:with-param>
            <xsl:with-param name="onclick">
                <xsl:text>editObject(this, '</xsl:text>
                <xsl:value-of select="$name"/>
                <xsl:text>');</xsl:text>
            </xsl:with-param>
        </xsl:call-template>      

        <xsl:call-template name="button">
            <xsl:with-param name="type" select="'button'"/>
            <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
          <xsl:with-param name="tooltip" select="'%cmdRemove%'"/>
            <xsl:with-param name="name">btn<xsl:value-of select="$name"/>del</xsl:with-param>
            <xsl:with-param name="onclick">
                <xsl:text>removeContentObjectSingle('</xsl:text>
                <xsl:value-of select="$name"/>
                <xsl:text>')</xsl:text>
            </xsl:with-param>
        </xsl:call-template>

    </xsl:template>

</xsl:stylesheet>
