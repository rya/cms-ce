<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:template name="resources">
        <xsl:param name="includeplus" select="true()"/>
        <xsl:param name="rootname" select="''"/>
        <xsl:param name="roothref" select="''"/>
        <xsl:param name="menukey" select="''"/>
        <xsl:param name="mode" select="''"/>
        <xsl:param name="inserttypekey" select="''"/>
        <xsl:param name="fieldname" select="''"/>
        <xsl:param name="last" select="false()"/>

        <tr>
            <xsl:if test="$includeplus">
                <td width="16">
                    <a>
                        <xsl:attribute name="href">
                            <xsl:text>javaScript:openBranch('-resources</xsl:text><xsl:value-of select="$menukey"/><xsl:text>');</xsl:text>
                        </xsl:attribute>
                        
                        <img border="0">
                            <xsl:attribute name="src">
                                <xsl:text>javascript/images/</xsl:text>
                                <xsl:choose>
                                    <xsl:when test="$menukey != '' or $last">
                                        <xsl:text>L</xsl:text>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:text>T</xsl:text>
                                    </xsl:otherwise>
                                </xsl:choose>
                                <xsl:choose>
                                    <xsl:when test="$mode = '' or ($menukey = '' and not($last))">
                                        <xsl:text>plus.png</xsl:text>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:text>minus.png</xsl:text>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>
                            
                            <xsl:attribute name="id">
                                <xsl:text>img-resources</xsl:text>
                                <xsl:value-of select="$menukey"/>
                            </xsl:attribute>
                        </img>
                    </a>
                </td>
            </xsl:if>

            <td>
                <a target="mainFrame">
                    <xsl:attribute name="href">
                        <xsl:choose>
                            <xsl:when test="$roothref = ''">
                                <xsl:text>adminpage?page=800&amp;op=browse</xsl:text>
                                <xsl:if test="$menukey != ''">
                                    <xsl:text>&amp;selectedmenukey=</xsl:text>
                                    <xsl:value-of select="$menukey"/>
                                </xsl:if>
                                <xsl:if test="$mode = 'popup'">
                                    <xsl:text>&amp;mode=popup&amp;inserttypekey=</xsl:text>
                                    <xsl:value-of select="$inserttypekey"/>
                                    <xsl:text>&amp;fieldname=</xsl:text>
                                    <xsl:value-of select="$fieldname"/>
                                </xsl:if>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$roothref"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    
                    <xsl:attribute name="style">
                        <xsl:text>cursor: hand;</xsl:text>
                    </xsl:attribute>
                    
                    <img border="0">
                        <xsl:attribute name="src">
                            <xsl:text>images/icon_</xsl:text>
                            <xsl:choose>
                                <xsl:when test="$menukey = '' or $mode = ''">
                                    <xsl:text>folder_resources</xsl:text>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:text>site</xsl:text>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:text>.gif</xsl:text>
                        </xsl:attribute>
                    </img>
                    <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                    
                    <xsl:choose>
                        <xsl:when test="$rootname != ''">
                            <xsl:value-of select="$rootname"/>
                        </xsl:when>
                        <xsl:when test="$menukey != ''">
                            <xsl:text>%mnuResources%</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>%mnuSharedResources%</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </a>
            </td>
        </tr>

        <tr valign="top">
            <xsl:if test="$includeplus and ( $mode = '' or ( $menukey = '' and not( $last ) ) )">
                <xsl:attribute name="style">
                    <xsl:text>display: none;</xsl:text>
                </xsl:attribute>
            </xsl:if>

            <xsl:attribute name="id">
                <xsl:text>id-resources</xsl:text>
                <xsl:value-of select="$menukey"/>
            </xsl:attribute>
            
            <xsl:if test="$includeplus">
                <td width="16">
                    <xsl:if test="$menukey = '' and not($last)">
                        <xsl:attribute name="background">
                            <xsl:text>javascript/images/I.png</xsl:text>
                        </xsl:attribute>
                    </xsl:if>
                    <img src="images/shim.gif" border="0"/>
                </td>
            </xsl:if>

            <td>
                <table cellpadding="0" cellspacing="0" class="menuItem">

                    <xsl:if test="$inserttypekey = '' or $inserttypekey = 4">
                        <xsl:call-template name="mi_resource">
                            <xsl:with-param name="key" select="4"/>
                            <xsl:with-param name="name" select="'%miSiteCSS%'"/>
                            <xsl:with-param name="menukey" select="$menukey"/>
                            <xsl:with-param name="last" select="$includeplus and $inserttypekey != ''"/>
                            <xsl:with-param name="mode" select="$mode"/>
                            <xsl:with-param name="inserttypekey" select="$inserttypekey"/>
                            <xsl:with-param name="fieldname" select="$fieldname"/>
                        </xsl:call-template>
                    </xsl:if>
                        
                    <xsl:if test="$menukey = '' and ($inserttypekey = '' or $inserttypekey = 7)">
                        <xsl:call-template name="mi_resource">
                            <xsl:with-param name="key" select="7"/>
                            <xsl:with-param name="name" select="'%miArchiveCSS%'"/>
                            <xsl:with-param name="menukey" select="$menukey"/>
                            <xsl:with-param name="last" select="$includeplus and $inserttypekey != ''"/>
                            <xsl:with-param name="mode" select="$mode"/>
                            <xsl:with-param name="inserttypekey" select="$inserttypekey"/>
                            <xsl:with-param name="fieldname" select="$fieldname"/>
                        </xsl:call-template>
                    </xsl:if>

                    <xsl:if test="$inserttypekey = '' or $inserttypekey = 8">
                        <xsl:call-template name="mi_resource">
                            <xsl:with-param name="key" select="8"/>
                            <xsl:with-param name="name" select="'%miScripts%'"/>
                            <xsl:with-param name="menukey" select="$menukey"/>
                            <xsl:with-param name="last" select="$includeplus and $inserttypekey != ''"/>
                            <xsl:with-param name="mode" select="$mode"/>
                            <xsl:with-param name="inserttypekey" select="$inserttypekey"/>
                            <xsl:with-param name="fieldname" select="$fieldname"/>
                        </xsl:call-template>
                    </xsl:if>

                    <xsl:if test="$inserttypekey = '' or $inserttypekey = 1 or $inserttypekey = 2 or $inserttypekey = 3 or $inserttypekey = 5">
                        <xsl:call-template name="mi_resource">
                            <xsl:with-param name="key" select="6"/>
                            <xsl:with-param name="name" select="'%miIncludeXSL%'"/>
                            <xsl:with-param name="menukey" select="$menukey"/>
                            <xsl:with-param name="mode" select="$mode"/>
                            <xsl:with-param name="inserttypekey" select="$inserttypekey"/>
                            <xsl:with-param name="fieldname" select="$fieldname"/>
                        </xsl:call-template>
                    </xsl:if>

                    <xsl:if test="$inserttypekey = '' or $inserttypekey = 2">
                        <xsl:call-template name="mi_resource">
                            <xsl:with-param name="key" select="2"/>
                            <xsl:with-param name="name" select="'%miPortletXSL%'"/>
                            <xsl:with-param name="menukey" select="$menukey"/>
                            <xsl:with-param name="last" select="$includeplus and $inserttypekey != ''"/>
                            <xsl:with-param name="mode" select="$mode"/>
                            <xsl:with-param name="inserttypekey" select="$inserttypekey"/>
                            <xsl:with-param name="fieldname" select="$fieldname"/>
                        </xsl:call-template>
                    </xsl:if>

                    <xsl:if test="$inserttypekey = '' or $inserttypekey = 1">
                        <xsl:call-template name="mi_resource">
                            <xsl:with-param name="key" select="1"/>
                            <xsl:with-param name="name" select="'%miFrameXSL%'"/>
                            <xsl:with-param name="menukey" select="$menukey"/>
                            <xsl:with-param name="last" select="$includeplus and $inserttypekey != ''"/>
                            <xsl:with-param name="mode" select="$mode"/>
                            <xsl:with-param name="inserttypekey" select="$inserttypekey"/>
                            <xsl:with-param name="fieldname" select="$fieldname"/>
                        </xsl:call-template>
                    </xsl:if>

                    <xsl:if test="$inserttypekey = '' or $inserttypekey = 3">
                        <xsl:call-template name="mi_resource">
                            <xsl:with-param name="key" select="3"/>
                            <xsl:with-param name="name" select="'%miFrameworkXSL%'"/>
                            <xsl:with-param name="menukey" select="$menukey"/>
                            <xsl:with-param name="last" select="$includeplus and ( ( $menukey != '' and $inserttypekey = '' ) or $inserttypekey != '' )"/>
                            <xsl:with-param name="mode" select="$mode"/>
                            <xsl:with-param name="inserttypekey" select="$inserttypekey"/>
                            <xsl:with-param name="fieldname" select="$fieldname"/>
                        </xsl:call-template>
                    </xsl:if>

                    <xsl:if test="$menukey = '' and ($inserttypekey = '' or $inserttypekey = 5)">
                        <xsl:call-template name="mi_resource">
                            <xsl:with-param name="key" select="5"/>
                            <xsl:with-param name="name" select="'%miReportXSL%'"/>
                            <xsl:with-param name="menukey" select="$menukey"/>
                            <xsl:with-param name="last" select="$includeplus"/>
                            <xsl:with-param name="mode" select="$mode"/>
                            <xsl:with-param name="inserttypekey" select="$inserttypekey"/>
                            <xsl:with-param name="fieldname" select="$fieldname"/>
                        </xsl:call-template>
                    </xsl:if>
					
                </table>
            </td>
        </tr>
    </xsl:template>

    <xsl:template name="mi_resource">
        <xsl:param name="key"/>
        <xsl:param name="name"/>
        <xsl:param name="last" select="false()"/>
        <xsl:param name="menukey"/>
        <xsl:param name="mode"/>
        <xsl:param name="inserttypekey"/>
        <xsl:param name="fieldname"/>

        <tr>
            <td width="16">
                <xsl:choose>
                    <xsl:when test="$last">
                        <img src="javascript/images/L.png" border="0"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <img src="javascript/images/T.png" border="0"/>
                    </xsl:otherwise>
                </xsl:choose>
            </td>
            <td>
                <a target="mainFrame">
                    <xsl:attribute name="href">
                        <xsl:text>adminpage?page=800&amp;op=browse</xsl:text>
                        <xsl:text>&amp;typekey=</xsl:text>
                        <xsl:value-of select="$key"/>
                        <xsl:if test="$menukey != ''">
                            <xsl:text>&amp;selectedmenukey=</xsl:text>
                            <xsl:value-of select="$menukey"/>
                        </xsl:if>
                        <xsl:if test="$mode = 'popup'">
                            <xsl:text>&amp;mode=popup&amp;inserttypekey=</xsl:text>
                            <xsl:value-of select="$inserttypekey"/>
                            <xsl:text>&amp;fieldname=</xsl:text>
                            <xsl:value-of select="$fieldname"/>
                        </xsl:if>
                    </xsl:attribute>
					
                    <img border="0">
                        <xsl:attribute name="src">
                            <xsl:text>images/icon_folder_</xsl:text>
                            <xsl:choose>
                                <xsl:when test="$key = 4 or $key = 7">
                                    <xsl:text>style</xsl:text>
                                </xsl:when>
                                <xsl:when test="$key = 8 or $key = 9">
                                    <xsl:text>script</xsl:text>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:text>xsl</xsl:text>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:text>.gif</xsl:text>
                        </xsl:attribute>
                        <xsl:if test="$inserttypekey != '' and $inserttypekey != $key">
                            <xsl:attribute name="style">
                                <xsl:text>filter: alpha(opacity=50)</xsl:text>
                            </xsl:attribute>
                        </xsl:if>
                    </img>
                    <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                    <xsl:value-of select="$name"/>
                </a>
            </td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
