<?xml version="1.0" encoding="utf-8"?>
<!--
- User: jvs
- Date: 28.mai.2003
- Time: 12:59:47
  -->

<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:include href="menu_pagebuilder.xsl"/>

    <!-- Presentation -->
    <xsl:template match="menu">

        <xsl:param name="hasAfterComingSiblings" select="'false'"/>

        <xsl:variable name="idname">
            <xsl:text>-rootmenu</xsl:text>
        </xsl:variable>

        <xsl:variable name="href">

            <xsl:text>adminpage?page=851&amp;op=browse&amp;menukey=</xsl:text><xsl:value-of select="@key"/>

        </xsl:variable>

        <tr valign="middle" style="height: 16px;">
            <td width="16">
                <xsl:choose>
                    <xsl:when test="@key = $selectedmenukey">
                        <a>
                            <xsl:attribute name="href">
                                <xsl:text>javaScript:openBranch('</xsl:text>
                                <xsl:value-of select="$idname"/>
                                <xsl:value-of select="@key"/>
                                <xsl:text>');</xsl:text>
                            </xsl:attribute>
                            <xsl:choose>
                                <xsl:when test="position() = last() and $hasAfterComingSiblings != 'true'">
                                    <img id="img{$idname}{@key}" src="javascript/images/Lplus.png" border="0"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <img id="img{$idname}{@key}" src="javascript/images/Tplus.png" border="0"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </a>
                    </xsl:when>
                    <xsl:otherwise>
                        <a>
                            <xsl:attribute name="href">
                                <xsl:text>javaScript:loadMenu(</xsl:text>
                                <xsl:value-of select="@key"/>, <xsl:value-of select="$selecteddomainkey"/>
                                <xsl:text>);</xsl:text>
                            </xsl:attribute>
                            <xsl:attribute name="style">
                                <xsl:text>cursor: hand;</xsl:text>
                            </xsl:attribute>
                            <xsl:choose>
                                <xsl:when test="position() = last()">
                                    <img id="img-unit{@key}" src="javascript/images/Lplus.png" border="0"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <img id="img-unit{@key}" src="javascript/images/Tplus.png" border="0"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </a>
                    </xsl:otherwise>
                </xsl:choose>
            </td>

            <td width="16">
                <a target="mainFrame" href="{$href}">
                    <xsl:attribute name="title">
                        <xsl:text>%fldKey%: </xsl:text>
                        <xsl:value-of select="@key"/>
                        <xsl:if test="description != ''">
                            <xsl:text>&#13;</xsl:text>
                            <xsl:value-of select="description"/>
                        </xsl:if>
                    </xsl:attribute>
                    <xsl:attribute name="onDblClick">
                        <xsl:text>openBranch('</xsl:text>
                        <xsl:value-of select="$idname"/>
                        <xsl:value-of select="@key"/>
                        <xsl:text>');</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="style">
                        <xsl:text>cursor: hand;</xsl:text>
                    </xsl:attribute>
                    
                    <xsl:variable name="lockstring">
                    	<xsl:if test="not(accessrights/accessright[@grouptype = 7])">_lock</xsl:if>
                   	</xsl:variable>

                    <img src="images/icon_site{$lockstring}.gif" border="0"/>
                    <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                    <xsl:value-of select="name"/> (<xsl:value-of select="@languagecode"/>)
                </a>
            </td>
        </tr>

        <tr valign="top" id="id{$idname}{@key}" >
            <xsl:attribute name="style">
                <xsl:text>display: none;</xsl:text>
            </xsl:attribute>
            <td width="16">
                <xsl:if test="position() != last() or $hasAfterComingSiblings = 'true'">
                    <xsl:attribute name="background">
                        <xsl:text>javascript/images/I.png</xsl:text>
                    </xsl:attribute>
                </xsl:if>
                <img src="images/shim.gif" border="0"/>
            </td>
            <td>
                <table cellpadding="0" cellspacing="0" border="0" class="menuItem">
                    <tr valign="top">
                        <td>

                            <xsl:variable name="showObjects">
                                <xsl:choose>
                                    <xsl:when test="accessrights/userright/@administrate = 'true' or not(accessrights/userright)">
                                        <xsl:text>true</xsl:text>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:text>false</xsl:text>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:variable>

                            <xsl:variable name="showFrameworks">
                                <xsl:choose>
                                    <xsl:when test="accessrights/userright/@administrate = 'true' or not(accessrights/userright)">
                                        <xsl:text>true</xsl:text>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:text>false</xsl:text>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:variable>
                            
                            <xsl:variable name="showServices" select="'false'">
                                <!--xsl:choose>
                                	<xsl:when test="accessrights/userright/@administrate = 'true' or not(accessrights/userright)">
                                        <xsl:text>true</xsl:text>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:text>false</xsl:text>
                                    </xsl:otherwise>
                                </xsl:choose-->
                            </xsl:variable>
							
                            <xsl:variable name="pagebuilder_hasAfterComingSiblings">
                                <xsl:choose>
                                    <xsl:when test="$showObjects = 'true' or $showFrameworks = 'true'"><xsl:text>true</xsl:text></xsl:when>
                                    <xsl:otherwise>
                                        <xsl:text>false</xsl:text>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:variable>
                            
                            <xsl:call-template name="pagebuilder">
                                <xsl:with-param name="idname" select="$idname"/>
                                <xsl:with-param name="xpathMenu" select="."/>
                                <xsl:with-param name="hasAfterComingSiblings" select="$pagebuilder_hasAfterComingSiblings"/>
                            </xsl:call-template>

                            <xsl:if test="$showFrameworks = 'true'">
                                <table cellpadding="0" cellspacing="0" border="0" class="menuItem">
                                    <tr valign="middle" style="height: 16px;">
                                        <td width="16"><img src="javascript/images/T.png" border="0"/></td>
                                        <td>
                                            <a target="mainFrame">
                                                <xsl:attribute name="href">
                                                    <xsl:text>adminpage?page=550&amp;op=browse</xsl:text>
                                                    <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="@key"/>
                                                </xsl:attribute>
                                                <img src="images/icon_frameworks.gif" border="0"/>
                                                <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                                                <xsl:text>%mnuFrameworks%</xsl:text>
                                            </a>
                                        </td>
                                    </tr>
                                </table>
                            </xsl:if>
                            
                            <xsl:if test="$showObjects = 'true'">
                                <table cellpadding="0" cellspacing="0" border="0" class="menuItem">
                                    <tr valign="middle" style="height: 16px;">
                                        <td width="16"><img src="javascript/images/T.png" border="0"/></td>
                                        <td>
                                            <a target="mainFrame">
                                                <xsl:attribute name="href">
                                                    <xsl:text>adminpage?page=900&amp;op=browse</xsl:text>
                                                    <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="@key"/>
                                                </xsl:attribute>
                                                <img src="images/icon_objects.gif" border="0"/>
                                                <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                                                <xsl:text>%mnuPortlets%</xsl:text>
                                            </a>
                                        </td>
                                    </tr>
                                </table>
                            </xsl:if>
                            
                            <xsl:if test="$showServices = 'true'">
                                <table cellpadding="0" cellspacing="0" border="0" class="menuItem">
                                    <tr valign="middle" style="height: 16px;">
                                        <td width="16"><img src="javascript/images/T.png" border="0"/></td>
                                        <td>
                                            <a href="adminpage?page=992&amp;op=browse&amp;menukey={@key}" target="mainFrame">
                                                <img src="images/icon_contenttypes.gif" border="0"/>
                                                <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                                                <xsl:text>%mnuServices%</xsl:text>
                                            </a>
                                        </td>
                                    </tr>
                                </table>
                            </xsl:if>

                        </td>
                    </tr>
                </table>
            </td>
        </tr>

    </xsl:template>


</xsl:stylesheet>
