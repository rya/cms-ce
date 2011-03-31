<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:include href="menu_menuitem.xsl"/>

    <xsl:template name="pagebuilder">

        <xsl:param name="xpathMenu"/>
        <xsl:param name="idname"/>
        <xsl:param name="root" select="false()"/>
        <xsl:param name="hasAfterComingSiblings" select="'false'"/>
        <!-- specifies target for links -->
        <xsl:param name="target" select="'mainFrame'"/>


        <xsl:variable name="href_pagebuilder">
            <xsl:call-template name="menu_href_pagebuilder">
                <xsl:with-param name="xpathMenu" select="$xpathMenu"/>
            </xsl:call-template>
        </xsl:variable>

		<xsl:if test="$xpathMenu/menuitems/menuitem/@key != '' or not($xpathMenu/accessrights/userright) or accessrights/userright/@create = 'true' or $xpathMenu/accessrights/userright/@administrate = 'true'">
        <table cellpadding="0" cellspacing="0" border="0" class="menuItem">
            <tr valign="middle" style="height: 17px;">
                <xsl:if test="not($root)">
                    <td width="16">
                        <xsl:choose>
                            <xsl:when test="$xpathMenu/menuitems/menuitem/@key != ''">
                                <!--xsl:attribute name="onclick">
                                    <xsl:text>openBranch('-pagebuilder</xsl:text><xsl:value-of select="$xpathMenu/@key"/><xsl:text>');</xsl:text>
                                </xsl:attribute>
                                <xsl:attribute name="style">
                                    <xsl:text>cursor: hand;</xsl:text>
                                </xsl:attribute-->
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:text>javascript:openBranch('-pagebuilder</xsl:text><xsl:value-of select="$xpathMenu/@key"/><xsl:text>');</xsl:text>
                                    </xsl:attribute>
                                    <xsl:choose>
                                        <xsl:when test="$hasAfterComingSiblings = 'true'">
                                            <img id="img-pagebuilder{$xpathMenu/@key}" src="javascript/images/Tplus.png" border="0"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <img id="img-pagebuilder{$xpathMenu/@key}" src="javascript/images/Lplus.png" border="0"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </a>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:choose>
                                    <xsl:when test="$hasAfterComingSiblings = 'true'">
                                        <img src="javascript/images/T.png" border="0"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <img src="javascript/images/L.png" border="0"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:otherwise>
                        </xsl:choose>
                    </td>
                </xsl:if>
                <td>
                    <a>
                        <xsl:if test="$target != 'none'">
                            <xsl:attribute name="target">
                                <xsl:value-of select="$target"/>
                            </xsl:attribute>
                        </xsl:if>
                        <xsl:attribute name="href">
                            <xsl:value-of select="$href_pagebuilder"/>
                        </xsl:attribute>
                        <xsl:if test="$xpathMenu/menuitems/menuitem/@key != ''">
                            <xsl:attribute name="onDblClick">
                                <xsl:text>openBranch('-pagebuilder</xsl:text><xsl:value-of select="$xpathMenu/@key"/><xsl:text>');</xsl:text>
                            </xsl:attribute>
                            <xsl:attribute name="style">
                                <xsl:text>cursor: hand;</xsl:text>
                            </xsl:attribute>
                        </xsl:if>

						<img src="images/icon_menuitems.gif" border="0">
							<xsl:if test="not(not($xpathMenu/accessrights/userright) or accessrights/userright/@create = 'true' or $xpathMenu/accessrights/userright/@administrate = 'true')">
								<xsl:attribute name="style">
									<xsl:text>filter: alpha(opacity=30)</xsl:text>
								</xsl:attribute>
							</xsl:if>
						</img>

                        <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                        <xsl:text>%mnuPageBuilder%</xsl:text>
                    </a>
                </td>
            </tr>
        </table>
        </xsl:if>
        <table cellpadding="0" cellspacing="0" border="0" class="menuItem">
            <xsl:if test="$xpathMenu/menuitems/menuitem/@key != ''">
                <tr valign="top" id="id-pagebuilder{$xpathMenu/@key}">
                    <xsl:if test="not($root)">
                        <xsl:attribute name="style">
                            <xsl:text>display: none;</xsl:text>
                        </xsl:attribute>
                        <td width="18">
                            <xsl:if test="$hasAfterComingSiblings = 'true'">
                                <xsl:attribute name="background">
                                    <xsl:text>javascript/images/I.png</xsl:text>
                                </xsl:attribute>
                            </xsl:if>
                            <img src="images/shim.gif" width="18" border="0"/>
                        </td>
                    </xsl:if>
                    <td>
                        <table cellpadding="0" cellspacing="0" border="0" class="menuItem">
                            <xsl:apply-templates select="$xpathMenu/menuitems/menuitem">
                                <xsl:with-param name="unitkey" select="$xpathMenu/@unitkey"/>
                                <xsl:with-param name="menukey" select="$xpathMenu/@key"/>
                                <xsl:with-param name="target" select="$target"/>
                            </xsl:apply-templates>
                        </table>
                    </td>
                </tr>
            </xsl:if>
        </table>
    </xsl:template>

</xsl:stylesheet>
