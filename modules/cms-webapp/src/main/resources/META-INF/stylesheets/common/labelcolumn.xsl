<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:template name="labelcolumn">
        <xsl:param name="width"/>
        <xsl:param name="label"/>
        <xsl:param name="required"/>
        <xsl:param name="hide-required" select="false()"/>
        <xsl:param name="fieldname"/>
        <xsl:param name="helpelement"/>
        <xsl:param name="nowrap" select="'true'"/>
        <xsl:param name="useIcon" select="false()"/>
        <xsl:param name="iconClass" select="''"/>
        <xsl:param name="iconText" select="''"/>
        <xsl:param name="valign" select="''"/>

        <td class="form_labelcolumn">
            <xsl:attribute name="valign">
                <xsl:choose>
                    <xsl:when test="$valign != ''">
                        <xsl:value-of select="$valign"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>top</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:if test="$nowrap = 'true'">
                <xsl:attribute name="nowrap">true</xsl:attribute>
            </xsl:if>
            <xsl:if test="$width != ''">
                <xsl:attribute name="style">
                    <xsl:value-of select="concat('width:', $width, 'px')"/>
                </xsl:attribute>
            </xsl:if>
            <div class="labelcolumn_inner"> <!-- Holly Hack for IE -->
                <div style="float:left">
                    <xsl:choose>
                        <xsl:when test="$helpelement and not($helpelement/@alwayson = 'true')">
                            <table width="100%" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td>
                                        <xsl:value-of select="$label" disable-output-escaping="yes"/>
                                        <xsl:if test="$required = 'true'">
                                            <span class="requiredfield">*</span>
                                        </xsl:if>
                                    </td>
                                    <td align="right">
                                        <a id="{$fieldname}_helplink" name="{$fieldname}_helplink" class="hand" onclick="showHideHelp(this, '{$fieldname}')" title="%msgClickToView%">
                                            <img id="{$fieldname}_helpicon" src="images/icon_help.gif" border="0"/>
                                        </a>
                                    </td>
                                </tr>
                            </table>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:if test="$helpelement and $helpelement/@alwayson = 'true'">
                                <a id="{$fieldname}_helplink"/>
                            </xsl:if>
                            <xsl:value-of select="$label" disable-output-escaping="yes"/>
                            <xsl:if test="$required = 'true'">
                                <span class="requiredfield">
                                    <xsl:if test="$hide-required">
                                        <xsl:attribute name="style">
                                            <xsl:text>display:none</xsl:text>
                                        </xsl:attribute>
                                    </xsl:if>
                                    <xsl:if test="$fieldname != ''">
                                        <xsl:attribute name="id">
                                            <xsl:value-of select="concat($fieldname, '_required_indicator')"/>
                                        </xsl:attribute>
                                        <xsl:text>*</xsl:text>
                                    </xsl:if>
                                </span>
                            </xsl:if>
                        </xsl:otherwise>
                    </xsl:choose>
                </div>
         
                <xsl:if test="$useIcon">
                    <div style="float:right">
                        <span class="{$iconClass}">
                            <xsl:if test="$iconText !=''">
                                <xsl:attribute name="title">
                                    <xsl:value-of select="$iconText"/>
                                </xsl:attribute>
                            </xsl:if>
                        </span>
                    </div>
                </xsl:if>
            </div>
        </td>
    </xsl:template>
</xsl:stylesheet>
