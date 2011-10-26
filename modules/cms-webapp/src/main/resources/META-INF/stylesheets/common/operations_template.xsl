<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:template name="operations">
        <xsl:param name="page"/>
        <xsl:param name="key"/>
        <xsl:param name="edithref" select="''"/>
        <xsl:param name="includecopy"/>
        <xsl:param name="copywarning" select="false()"/>
        <xsl:param name="copyhref" select="''"/>
        <xsl:param name="copycondition" select="''"/>
        <xsl:param name="enablecopy" select="'true'"/>
        <xsl:param name="includeparams" select="''"/>
        <xsl:param name="usereferer" select="false()"/>
        <xsl:param name="includeedit" select="'true'"/>
        <xsl:param name="enableedit" select="'true'"/>
        <xsl:param name="includedelete" select="'true'"/>
        <xsl:param name="JSdeleteCallback" select="''"/>
        <xsl:param name="enabledelete" select="'true'"/>
        <xsl:param name="domainkey" select="''"/>
        <xsl:param name="enablemove" select="'true'"/>

        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <xsl:if test="/contents/properties/property[@name='enableUserReview']/@value = 'true'">
                    <td align="center" class="operationscell">
                        <xsl:call-template name="button">
                            <xsl:with-param name="style" select="'flat'"/>
                            <xsl:with-param name="type" select="'link'"/>
                            <xsl:with-param name="name">
                                <xsl:text>userreviews</xsl:text><xsl:value-of select="$key"/>
                            </xsl:with-param>
                            <xsl:with-param name="image" select="'images/icon_userreview.gif'"/>
                            <xsl:with-param name="disabled" select="'false'"/>
                            <xsl:with-param name="tooltip" select="'%altContentUserReviews%'"/>
                            <xsl:with-param name="href">
                                <xsl:text>adminpage?page=1500</xsl:text>
                                <xsl:text>&amp;op=browse</xsl:text>
                                <xsl:text>&amp;contentkey=</xsl:text>
                                <xsl:value-of select="$key"/>
                                <xsl:text>&amp;cat=</xsl:text>
                                <xsl:value-of select="$cat"/>
                                <xsl:text>&amp;selectedunitkey=</xsl:text>
                                <xsl:value-of select="$selectedunitkey"/>
                                <xsl:text>&amp;store_redirect=true</xsl:text>
                            </xsl:with-param>
                        </xsl:call-template>
                    </td>
                </xsl:if>
                <xsl:if test="$includeedit = 'true'">
                    <xsl:variable name="disableedit">
                        <xsl:choose>
                            <xsl:when test="$enableedit = 'true'">
                                <xsl:text>false</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>true</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>

                    <xsl:variable name="edit_href">
                        <xsl:choose>
                            <xsl:when test="$edithref != ''">
                                <xsl:value-of select="$edithref"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>adminpage?page=</xsl:text>
                                <xsl:value-of select="$page"/>
                                <xsl:text>&amp;op=form&amp;key=</xsl:text><xsl:value-of select="$key"/>
                                <xsl:if test="$cat != ''">
                                    <xsl:text>&amp;cat=</xsl:text>
                                    <xsl:value-of select="$cat"/>
                                </xsl:if>
                                <xsl:if test="$selectedunitkey != ''">
                                    <xsl:text>&amp;selectedunitkey=</xsl:text>
                                    <xsl:value-of select="$selectedunitkey"/>
                                </xsl:if>
                                <xsl:if test="$menukey != ''">
                                    <xsl:text>&amp;menukey=</xsl:text>
                                    <xsl:value-of select="$menukey"/>
                                </xsl:if>
                                <xsl:if test="$usereferer">
                                    <xsl:text>&amp;useredirect=referer</xsl:text>
                                </xsl:if>
                                <xsl:if test="$domainkey != ''">
                                    <xsl:text>&amp;domainkey=</xsl:text><xsl:value-of select="$domainkey"/>
                                </xsl:if>
                                <xsl:value-of select="$includeparams"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>

                    <td align="center" class="operationscell">
                        <xsl:call-template name="button">
                            <xsl:with-param name="style" select="'flat'"/>
                            <xsl:with-param name="type" select="'link'"/>
                            <xsl:with-param name="id">
                                <xsl:text>operation_edit_</xsl:text><xsl:value-of select="$key"/>
                            </xsl:with-param>
                            <xsl:with-param name="image" select="'images/icon_edit.gif'"/>
                            <xsl:with-param name="disabled" select="$disableedit"/>
                            <xsl:with-param name="tooltip" select="'%altContentEdit%'"/>
                            <xsl:with-param name="href" select="$edit_href"/>
                        </xsl:call-template>
                    </td>

                </xsl:if>
                <xsl:if test="$includecopy='true' and not(//content)">
                    <td align="center" class="operationscell">

                        <xsl:variable name="copy_condition">
                            <xsl:choose>
                                <xsl:when test="$copywarning and $copycondition = ''">
                                    <xsl:text>confirm('%alertCopy%')</xsl:text>
                                </xsl:when>
                                <xsl:when test="$copywarning and $copycondition != ''">
                                    <xsl:value-of select="$copycondition"/>
                                </xsl:when>
                            </xsl:choose>
                        </xsl:variable>

                        <xsl:variable name="copy_href">
                            <xsl:choose>
                                <xsl:when test="$copyhref != ''">
                                    <xsl:value-of select="$copyhref"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:text>adminpage?page=</xsl:text>
                                    <xsl:value-of select="$page"/>
                                    <xsl:text>&amp;op=copy&amp;key=</xsl:text><xsl:value-of select="$key"/>
                                    <xsl:if test="$cat != ''">
                                        <xsl:text>&amp;cat=</xsl:text>
                                        <xsl:value-of select="$cat"/>
                                    </xsl:if>
                                    <xsl:if test="$selectedunitkey != ''">
                                        <xsl:text>&amp;selectedunitkey=</xsl:text>
                                        <xsl:value-of select="$selectedunitkey"/>
                                    </xsl:if>
                                    <xsl:if test="$menukey != ''">
                                        <xsl:text>&amp;menukey=</xsl:text>
                                        <xsl:value-of select="$menukey"/>
                                    </xsl:if>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>

                        <xsl:call-template name="button">
                            <xsl:with-param name="style" select="'flat'"/>
                            <xsl:with-param name="type" select="'link'"/>
                            <xsl:with-param name="id">
                                <xsl:text>copy</xsl:text><xsl:value-of select="$key"/>
                            </xsl:with-param>
                            <xsl:with-param name="image" select="'images/icon_copy.gif'"/>
                            <xsl:with-param name="disabled" select="string($enablecopy = 'false')"/>
                            <xsl:with-param name="tooltip" select="'%altContentCopy%'"/>
                            <xsl:with-param name="href" select="$copy_href"/>
                            <xsl:with-param name="condition" select="$copy_condition"/>
                        </xsl:call-template>

                    </td>
                </xsl:if>

                <xsl:if test="$includedelete = 'true'">
                    <td align="center" class="operationscell">
                        <xsl:variable name="deletedisabled">
                            <xsl:choose>
                                <xsl:when test="$enabledelete = 'true'">false</xsl:when>
                                <xsl:otherwise>true</xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="deletetooltip">
                            <xsl:choose>
                                <xsl:when test="$deletedisabled = 'true'">%altCannotDelete%</xsl:when>
                                <xsl:otherwise>%altContentDelete%</xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:call-template name="button">
                            <xsl:with-param name="style" select="'flat'"/>
                            <xsl:with-param name="type" select="'link'"/>
                            <xsl:with-param name="image" select="'images/icon_delete.gif'"/>
                            <xsl:with-param name="disabled" select="$deletedisabled"/>
                            <xsl:with-param name="tooltip" select="$deletetooltip"/>
                            <xsl:with-param name="href">
                                <xsl:text>adminpage?page=</xsl:text>
                                <xsl:value-of select="$page"/>
                                <xsl:text>&amp;op=remove&amp;key=</xsl:text><xsl:value-of select="$key"/>
                                <xsl:text>&amp;cat=</xsl:text>
                                <xsl:value-of select="$cat"/>
                                <xsl:text>&amp;selectedunitkey=</xsl:text>
                                <xsl:value-of select="$selectedunitkey"/>
                            </xsl:with-param>
                            <xsl:with-param name="condition">
                                <xsl:text>confirm('%alertDelete%')</xsl:text>
                            </xsl:with-param>
                            <xsl:with-param name="onclick">
                                <xsl:value-of select="$JSdeleteCallback"/>
                            </xsl:with-param>
                        </xsl:call-template>
                    </td>
                </xsl:if>

            </tr>
        </table>

    </xsl:template>

</xsl:stylesheet>
