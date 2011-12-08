<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:include href="configs/default.xsl"/>
    <xsl:include href="../common/string.xsl"/>

    <xsl:template match="*" mode="displaytree">
        <xsl:param name="topnode" select="true()"/>
        <xsl:param name="url"/>
        <xsl:param name="callback"/>
        <xsl:param name="onclick"/>
        <xsl:param name="onclick_resources"/>
        <xsl:param name="linkshaded" select="true()"/>
        <xsl:param name="top-node-is-clickable" select="true()"/>
        <xsl:param name="hassiblingoverride" select="false()"/>
        <xsl:param name="sourceKey"/>
        <xsl:param name="target"/>
        <xsl:param name="selectedmenukey"/>

        <xsl:variable name="hide">
            <xsl:apply-templates select="." mode="hide"/>
        </xsl:variable>

        <xsl:if test="not($hide = 'true')">
            <xsl:variable name="loadurl">
                <xsl:apply-templates select="." mode="loadurl"/>
            </xsl:variable>

            <xsl:variable name="expanded">
                <xsl:choose>
                    <xsl:when test="$topnode">true</xsl:when>
                    <!-- hello -->
                    <xsl:when test="not($loadurl = '') and *">true</xsl:when>
                    <xsl:otherwise>false</xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:variable name="haschildren">
                <xsl:apply-templates select="." mode="haschildren"/>
            </xsl:variable>

            <!-- override is a parameter, elementoverride checks for overrides in the element templates -->
            <xsl:variable name="hassiblingelementoverride">
                <xsl:apply-templates select="." mode="hassibling"/>
            </xsl:variable>

            <xsl:variable name="hassibling">
                <xsl:choose>
                    <xsl:when test="$hassiblingoverride"><xsl:value-of select="$hassiblingoverride"/></xsl:when>
                    <xsl:when test="not($hassiblingelementoverride = '')"><xsl:value-of select="$hassiblingelementoverride"/></xsl:when>
                    <xsl:when test="position() != last()">true</xsl:when>
                    <xsl:otherwise>false</xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:variable name="key">
                <xsl:apply-templates select="." mode="key"/>
            </xsl:variable>

            <table class="menuItem" cellspacing="0" cellpadding="0">
                <tr>
                    <xsl:attribute name="id">
                        <xsl:text>tr-</xsl:text>
                        <xsl:value-of select="name()"/>
                        <xsl:value-of select="$key"/>
                    </xsl:attribute>
                    <xsl:if test="not($topnode)">
                        <td width="16">
                            <xsl:choose>
                                <xsl:when test="($haschildren = 'true' or @haschildren = 'true')">
                                    <a id="openBranch-{name()}-{$key}">
                                        <xsl:attribute name="href">
                                            <xsl:choose>
                                                <xsl:when test="*">
                                                    <xsl:text>javascript:openBranch('-</xsl:text>
                                                    <xsl:value-of select="name()"/>
                                                    <xsl:value-of select="$key"/>
                                                    <xsl:text>');</xsl:text>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:apply-templates select="." mode="loadurl"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:attribute>
                                        <xsl:variable name="imagestring">
                                            <xsl:choose>
                                                <xsl:when test="($expanded = 'true' and $hassibling = 'true') and ($selectedmenukey and $selectedmenukey = $key)">Tplus</xsl:when>
                                                <xsl:when test="($expanded = 'true' and $hassibling = 'true')">Tminus</xsl:when>
                                                <xsl:when test="($expanded = 'false' and $hassibling = 'true')">Tplus</xsl:when>
                                                <xsl:when test="($expanded = 'true' and $hassibling = 'false')">Lminus</xsl:when>
                                                <xsl:when test="($expanded = 'false' and $hassibling = 'false')">Lplus</xsl:when>
                                            </xsl:choose>
                                        </xsl:variable>

                                        <img border="0" src="javascript/images/{$imagestring}.png">
                                            <xsl:attribute name="id">
                                                <xsl:text>img-</xsl:text>
                                                <xsl:value-of select="name()"/>
                                                <xsl:value-of select="$key"/>
                                            </xsl:attribute>
                                        </img>
                                    </a>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:choose>
                                        <xsl:when test="$hassibling = 'true'">
                                            <img border="0" src="javascript/images/T.png"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <img border="0" src="javascript/images/L.png"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:otherwise>
                            </xsl:choose>
                        </td>
                    </xsl:if>
                    <td>
                        <xsl:variable name="shadeicon">
                            <xsl:apply-templates select="." mode="shadeicon"/>
                        </xsl:variable>

                        <xsl:variable name="imagestring">
                            <xsl:text>images/</xsl:text>
                            <xsl:apply-templates select="." mode="icon"/>
                            <xsl:text>.gif</xsl:text>
                        </xsl:variable>

                        <xsl:choose>
                            <xsl:when test="not($linkshaded) and $shadeicon = 'true'">
                                <img width="16" height="16" border="0" src="{$imagestring}"/>
                                <img border="0" class="shim" height="1" width="3" src="images/shim.gif"/>
                                <xsl:apply-templates select="." mode="text"/>
                            </xsl:when>

                            <!-- Resources -->
                            <xsl:when test="$subop = 'moveFile' and $path = @fullPath">
                                <img width="16" height="16" border="0" src="{$imagestring}"/>
                                <img border="0" class="shim" height="1" width="3" src="images/shim.gif"/>
                                <xsl:apply-templates select="." mode="text"/>
                            </xsl:when>
                            <xsl:when test="($subop = 'moveFolder' and ancestor-or-self::*/@fullPath = $path) or ($subop = 'moveFolder' and ./folder/@fullPath = $path)">
                                <img width="16" height="16" border="0" src="{$imagestring}"/>
                                <img border="0" class="shim" height="1" width="3" src="images/shim.gif"/>
                                <xsl:apply-templates select="." mode="text"/>
                                <xsl:text> </xsl:text>
                            </xsl:when>

                            <xsl:otherwise>
                                <xsl:variable name="fullurl">
                                    <xsl:choose>
                                        <xsl:when test="$url">
                                            <xsl:value-of select="$url"/>
                                            <xsl:text>&amp;op=</xsl:text>
                                            <xsl:apply-templates select="." mode="op"/>
                                            <xsl:text>&amp;page=</xsl:text>
                                            <xsl:apply-templates select="." mode="page"/>
                                            <xsl:apply-templates select="." mode="extraparams"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:text>#</xsl:text>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:variable>

                                <xsl:variable name="tooltip">
                                    <xsl:apply-templates select="." mode="tooltip"/>
                                </xsl:variable>

                                <xsl:variable name="text">
                                    <xsl:apply-templates select="." mode="text"/>
                                </xsl:variable>

                                <xsl:choose>
                                    <xsl:when test="not($top-node-is-clickable)">
                                        <img width="16" height="16" border="0" src="{$imagestring}" style="margin-right:3px;"/>
                                        <span id="menuitemText{$key}">
                                            <xsl:value-of select="$text"/>
                                        </span>
                                    </xsl:when>
                                    <xsl:when test="$shadeicon = 'true'">
                                        <img width="16" height="16" border="0" src="{$imagestring}" style="margin-right:3px;"/>
                                        <span id="menuitemText{$key}">
                                            <xsl:value-of select="$text"/>
                                        </span>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <a href="{$fullurl}" id="treeItem-{name()}-{$key}">
                                            <xsl:if test="$tooltip != ''">
                                                <xsl:attribute name="title">
                                                    <xsl:value-of select="$tooltip"/>
                                                </xsl:attribute>
                                            </xsl:if>
                                            <xsl:if test="$url">
                                                <xsl:attribute name="target">
                                                    <xsl:choose>
                                                        <xsl:when test="$target">
                                                            <xsl:value-of select="$target"/>
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            <xsl:text>mainFrame</xsl:text>
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </xsl:attribute>
                                            </xsl:if>
                                            <xsl:choose>
                                                <xsl:when test="$callback">
                                                    <xsl:attribute name="onclick">
                                                        <xsl:text>javascript:window.top.opener.</xsl:text><xsl:value-of select="$callback"/>
                                                        <xsl:text>(</xsl:text>
                                                        <xsl:value-of select="$key"/>
                                                        <xsl:text>, '</xsl:text>
                                                        <xsl:call-template name="string-replace-all">
                                                            <xsl:with-param name="text">
                                                                <xsl:value-of select="$text"/>
                                                            </xsl:with-param>
                                                            <xsl:with-param name="replace">'</xsl:with-param>
                                                            <xsl:with-param name="by">\'</xsl:with-param>
                                                        </xsl:call-template>
                                                        <xsl:text>'</xsl:text>
                                                        <xsl:text>);window.close();</xsl:text>
                                                    </xsl:attribute>
                                                </xsl:when>
                                                <xsl:when test="$onclick">
                                                    <xsl:attribute name="onclick">
                                                        <xsl:text>javascript:</xsl:text><xsl:value-of select="$onclick"/>
                                                        <xsl:text>(</xsl:text>
                                                        <xsl:value-of select="$key"/>
                                                        <xsl:text>);</xsl:text>
                                                    </xsl:attribute>
                                                </xsl:when>
                                                <xsl:when test="$onclick_resources">
                                                    <xsl:attribute name="onclick">
                                                        <xsl:text>javascript:</xsl:text><xsl:value-of select="$onclick_resources"/>
                                                        <xsl:text>('</xsl:text>
                                                        <xsl:value-of select="$sourceKey"/>
                                                        <xsl:text>', '</xsl:text>
                                                        <!-- Destination folder(do not use key, as key is hashed for resource key) -->
                                                        <xsl:choose>
                                                            <xsl:when test="@fullPath != ''">
                                                                <xsl:value-of select="@fullPath"/>
                                                            </xsl:when>
                                                            <xsl:otherwise>
                                                                <xsl:text>/</xsl:text>
                                                            </xsl:otherwise>
                                                        </xsl:choose>
                                                        <xsl:text>', '</xsl:text>
                                                        <xsl:value-of select="$subop"/>
                                                        <xsl:text>');window.close();</xsl:text>
                                                    </xsl:attribute>
                                                </xsl:when>
                                                <xsl:otherwise/>
                                            </xsl:choose>
                                            <img width="16" height="16" border="0" src="{$imagestring}" style="margin-right:3px;"/>
                                            <span id="menuitemText{$key}">
                                                <xsl:value-of select="$text"/>
                                            </span>
                                        </a>
                                    </xsl:otherwise>
                                </xsl:choose>

                            </xsl:otherwise>
                        </xsl:choose>
                    </td>
                </tr>
                <xsl:if test="*">
                    <tr>
                        <!-- Make sure that selected menu is collapsed. It will be opened by menu.js -->
                        <xsl:variable name="collapseSelectedMenu" select="($expanded = 'true' and ($selectedmenukey and $selectedmenukey = $key))"/>
                        <xsl:if test="$collapseSelectedMenu or not($expanded = 'true')">
                            <xsl:attribute name="style">display: none</xsl:attribute>
                        </xsl:if>
                        <xsl:attribute name="id">
                            <xsl:text>id-</xsl:text>
                            <xsl:value-of select="name()"/>
                            <xsl:value-of select="$key"/>
                        </xsl:attribute>
                        <xsl:if test="not($topnode)">
                            <td>
                                <xsl:if test="$hassibling = 'true'">
                                    <xsl:attribute name="background">
                                        <xsl:text>javascript/images/I.png</xsl:text>
                                    </xsl:attribute>
                                </xsl:if>
                            </td>
                        </xsl:if>
                        <td>
                            <xsl:variable name="sortchildren">
                                <xsl:apply-templates select="." mode="sortchildren"/>
                            </xsl:variable>

                            <xsl:variable name="sortchildrentype">
                                <xsl:apply-templates select="." mode="sortchildrentype"/>
                            </xsl:variable>

                            <xsl:choose>
                                <xsl:when test="$sortchildren != ''">
                                    <xsl:apply-templates select="*" mode="displaytree">
                                        <xsl:sort select="saxon:evaluate($sortchildren)" data-type="{$sortchildrentype}"/>
                                        <xsl:with-param name="url" select="$url"/>
                                        <xsl:with-param name="callback" select="$callback"/>
                                        <xsl:with-param name="onclick" select="$onclick"/>
                                        <xsl:with-param name="onclick_resources" select="$onclick_resources"/>
                                        <xsl:with-param name="topnode" select="false()"/>
                                        <xsl:with-param name="linkshaded" select="$linkshaded"/>
                                        <xsl:with-param name="target" select="$target"/>
                                        <xsl:with-param name="sourceKey" select="$sourceKey"/>
                                        <xsl:with-param name="selectedmenukey" select="$selectedmenukey"/>
                                    </xsl:apply-templates>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:apply-templates select="*" mode="displaytree">
                                        <xsl:with-param name="url" select="$url"/>
                                        <xsl:with-param name="callback" select="$callback"/>
                                        <xsl:with-param name="onclick" select="$onclick"/>
                                        <xsl:with-param name="onclick_resources" select="$onclick_resources"/>
                                        <xsl:with-param name="topnode" select="false()"/>
                                        <xsl:with-param name="linkshaded" select="$linkshaded"/>
                                        <xsl:with-param name="target" select="$target"/>
                                        <xsl:with-param name="sourceKey" select="$sourceKey"/>
                                        <xsl:with-param name="selectedmenukey" select="$selectedmenukey"/>
                                    </xsl:apply-templates>
                                </xsl:otherwise>
                            </xsl:choose>
                        </td>
                    </tr>
                </xsl:if>
            </table>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>