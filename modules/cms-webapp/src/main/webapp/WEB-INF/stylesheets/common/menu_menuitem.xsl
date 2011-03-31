<?xml version="1.0" encoding="utf-8"?>
<!--
- User: jvs
- Date: 30.mai.2003
- Time: 09:39:13
  -->

<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:template match="menuitem">
        <xsl:param name="unitkey"/>
        <xsl:param name="menukey"/>
        <xsl:param name="target" select="'mainFrame'"/>

        <xsl:variable name="href">
            <xsl:call-template name="menu_href_menuitem">
                <xsl:with-param name="xpathMenuItem" select="."/>
                <xsl:with-param name="unitkey" select="$unitkey"/>
                <xsl:with-param name="menukey" select="$menukey"/>
            </xsl:call-template>
        </xsl:variable>

        <tr valign="middle" align="left">
            <td width="16">
                <xsl:choose>
                    <xsl:when test="menuitems/menuitem != ''">
                        <!--xsl:attribute name="onclick">
                        <xsl:text>openBranch('-menu</xsl:text>
                        <xsl:value-of select="@key"/>
                        <xsl:text>');</xsl:text>
                        </xsl:attribute>
                        <xsl:attribute name="style">
                        <xsl:text>cursor: hand;</xsl:text>
                        </xsl:attribute-->
                        <a>
                            <xsl:attribute name="href">
                                <xsl:text>javaScript:openBranch('-menu</xsl:text>
                                <xsl:value-of select="@key"/>
                                <xsl:text>');</xsl:text>
                            </xsl:attribute>
                            <xsl:choose>
                                <xsl:when test="position() = last()">
                                    <img id="img-menu{@key}" src="javascript/images/Lplus.png" border="0"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <img id="img-menu{@key}" src="javascript/images/Tplus.png" border="0"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </a>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:choose>
                            <xsl:when test="position() = last()">
                                <img src="javascript/images/L.png" border="0"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <img src="javascript/images/T.png" border="0"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:otherwise>
                </xsl:choose>
            </td>
            <td>
                <a href="{$href}">
                    <xsl:if test="$target != 'none'">
                        <xsl:attribute name="target">
                            <xsl:value-of select="$target"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:attribute name="title">
                        <xsl:text>Key: </xsl:text>
                        <xsl:value-of select="@key"/>
                        <xsl:if test="subtitle != ''">
                            <xsl:text>&#13;</xsl:text>
                            <xsl:text>Subtitle: </xsl:text>
                            <xsl:value-of select="subtitle"/>
                        </xsl:if>
                    </xsl:attribute>
                    <xsl:if test="menuitems/menuitem != ''">
                        <xsl:attribute name="onDblClick">
                            <xsl:text>openBranch('-menu</xsl:text>
                            <xsl:value-of select="@key"/>
                            <xsl:text>');</xsl:text>
                        </xsl:attribute>
                        <xsl:attribute name="style">
                            <xsl:text>cursor: hand;</xsl:text>
                        </xsl:attribute>
                    </xsl:if>
                    
			        <xsl:apply-templates select="." mode="iconimage"/>
                    <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                    <xsl:value-of select="name"/>
                </a>
            </td>
        </tr>
        <xsl:if test="menuitems/menuitem != ''">
            <tr valign="top" id="id-menu{@key}" style="display: none;">
                <td width="16">
                    <xsl:if test="position() != last()">
                        <xsl:attribute name="background">
                            <xsl:text>javascript/images/I.png</xsl:text>
                        </xsl:attribute>
                    </xsl:if>
                </td>
                <td>
                    <table cellpadding="0" cellspacing="0" border="0" class="menuItem">
                        <xsl:apply-templates select="menuitems/menuitem">
                            <xsl:with-param name="unitkey" select="$unitkey"/>
                            <xsl:with-param name="menukey" select="$menukey"/>
                            <xsl:with-param name="target" select="$target"/>
                        </xsl:apply-templates>
                    </table>
                </td>
            </tr>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
