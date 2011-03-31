<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:x="mailto:vro@enonic.com?subject=foobar"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/operations_template.xsl"/>
    <xsl:include href="common/javascriptPreload.xsl"/>
    <xsl:include href="common/genericheader.xsl"/>
    <xsl:include href="common/formatdate.xsl"/>
    <xsl:include href="common/tablecolumnheader.xsl"/>
    <xsl:include href="common/tablerowpainter.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/browse_table_js.xsl"/>

    <xsl:param name="sortby" select="'name'"/>
    <xsl:param name="sortby-direction" select="'ascending'"/>

    <xsl:variable name="pageURL">
        <xsl:text>adminpage?page=</xsl:text>
        <xsl:value-of select="$page"/>
        <xsl:text>&amp;op=browse</xsl:text>
        <xsl:text>&amp;menukey=</xsl:text>
        <xsl:value-of select="$menukey"/>
    </xsl:variable>

    <xsl:template match="/">
        <xsl:call-template name="browse"/>
    </xsl:template>

    <xsl:template name="browse">

        <html>
            <head>
                <xsl:call-template name="javascriptPreload"/>
				<script type="text/javascript" src="javascript/admin.js">//</script>
                <link href="css/admin.css" rel="stylesheet" type="text/css"/>
            </head>
            <body onload="MM_preloadImages('images/icon_edit.gif','images/icon_delete.gif')">
                <h1>
                    <xsl:call-template name="genericheader"/>
                    <a>
                        <xsl:attribute name="href">
                            <xsl:text>adminpage?page=550&amp;op=browse</xsl:text>
                            <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
                        </xsl:attribute>
                        <xsl:text>%headFramework%</xsl:text>
                    </a>
                </h1>

                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td class="browse_title_buttonrow_seperator"><img src="images/1x1.gif"/></td>
                    </tr>
                    <tr>
                        <td>
                            <xsl:call-template name="button">
                                <xsl:with-param name="type" select="'link'" />
                                <xsl:with-param name="caption" select="'%cmdNew%'" />
                                <xsl:with-param name="href">
                                    <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page" />
                                    <xsl:text>&amp;op=form</xsl:text>
                                    <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey" />
                                </xsl:with-param>
                            </xsl:call-template>
                        </td>
                    </tr>
                    <tr>
                        <td class="browse_buttonrow_datarows_seperator"><img src="images/1x1.gif"/></td>
                    </tr>
                    <tr>
                        <td>
                            <table width="100%" cellspacing="0" cellpadding="0" class="browsetable">
                                <tr>

                                    <xsl:call-template name="tablecolumnheader">
                                        <xsl:with-param name="caption" select="'%fldName%'" />
                                        <xsl:with-param name="pageURL" select="$pageURL" />
                                        <xsl:with-param name="current-sortby" select="$sortby" />
                                        <xsl:with-param name="current-sortby-direction" select="$sortby-direction" />
                                        <xsl:with-param name="sortby" select="'name'" />
                                    </xsl:call-template>

                                    <xsl:call-template name="tablecolumnheader">
                                        <xsl:with-param name="caption" select="'%fldType%'" />
                                        <xsl:with-param name="pageURL" select="$pageURL" />
                                        <xsl:with-param name="current-sortby" select="$sortby" />
                                        <xsl:with-param name="current-sortby-direction" select="$sortby-direction" />
                                        <xsl:with-param name="sortby" select="'@type'" />
                                    </xsl:call-template>

                                    <xsl:call-template name="tablecolumnheader">
                                        <xsl:with-param name="width" select="'100'" />
                                        <xsl:with-param name="align" select="'center'" />
                                        <xsl:with-param name="caption" select="'%fldModified%'" />
                                        <xsl:with-param name="pageURL" select="$pageURL" />
                                        <xsl:with-param name="current-sortby" select="$sortby" />
                                        <xsl:with-param name="current-sortby-direction" select="$sortby-direction" />
                                        <xsl:with-param name="sortby" select="'timestamp'" />
                                    </xsl:call-template>

                                    <xsl:call-template name="tablecolumnheader">
                                        <xsl:with-param name="width" select="'70'" />
                                        <xsl:with-param name="caption" select="''" />
                                        <xsl:with-param name="sortable" select="'false'" />
                                    </xsl:call-template>

                                </tr>
								<xsl:variable name="sortby-data-type">text</xsl:variable>
								<xsl:variable name="temp">
									<types>
										<xsl:for-each select="/pagetemplates/pagetemplate">
											<xsl:sort data-type="{$sortby-data-type}" order="{$sortby-direction}" select="*[name() = $sortby] | @*[concat('@',name()) = $sortby]"/>
											<type>
												<xsl:value-of select="@type"/>
											</type>
										</xsl:for-each>
									</types>
								</xsl:variable>

                <xsl:for-each select="/pagetemplates/pagetemplate">
                  <xsl:sort data-type="{$sortby-data-type}" order="{$sortby-direction}"
                            select="*[name() = $sortby] | @*[concat('@',name()) = $sortby]"/>
                  <xsl:variable name="prevPosition" select="position()-1"/>

                  <xsl:variable name="className">
                    <xsl:choose>
                      <xsl:when test="stylesheet/@exists = 'false' or css/@exists = 'false'">
                        <xsl:text>browsetablecellred</xsl:text>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:text>browsetablecell</xsl:text>
                      </xsl:otherwise>
                    </xsl:choose>
                    <xsl:if test="position() = last()">
                      <xsl:text> row-last</xsl:text>
                    </xsl:if>
                  </xsl:variable>

                  <xsl:if test="$sortby = '@type'">
                    <xsl:if test="@type != exslt-common:node-set($temp)/types/type[$prevPosition]">
                      <tr>
                        <td colspan="4" style="font-weight:bold;padding:6px 4px">
                          <xsl:choose>
                            <xsl:when test="@type = 'page'">
                              <xsl:text>%optPageTemplateTypeDefault%</xsl:text>
                            </xsl:when>
                            <xsl:when test="@type = 'document'">
                              <xsl:text>%optPageTemplateTypeDocument%</xsl:text>
                            </xsl:when>
                            <xsl:when test="@type = 'form'">
                              <xsl:text>%optPageTemplateTypeForm%</xsl:text>
                            </xsl:when>
                            <xsl:when test="@type = 'newsletter'">
                              <xsl:text>%optPageTemplateTypeNewsletter%</xsl:text>
                            </xsl:when>
                            <xsl:when test="@type = 'content'">
                              <xsl:text>%optPageTemplateTypeContent%</xsl:text>
                            </xsl:when>
                            <xsl:when test="@type = 'sectionpage'">
                              <xsl:text>%optPageTemplateTypeSectionPage%</xsl:text>
                            </xsl:when>
                          </xsl:choose>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="4">
                          <div class="divider"></div>
                        </td>
                      </tr>
                    </xsl:if>
                  </xsl:if>

                  <tr>
                    <xsl:call-template name="tablerowpainter"/>
                    <td class="{$className}" title="%msgClickToEdit%">
                      <xsl:call-template name="addJSEvent">
                        <xsl:with-param name="key" select="@key"/>
                      </xsl:call-template>
                      <xsl:value-of select="name"/>
                    </td>
                    <td class="{$className}" title="%msgClickToEdit%">
                      <xsl:call-template name="addJSEvent">
                        <xsl:with-param name="key" select="@key"/>
                      </xsl:call-template>
                      <xsl:choose>
                        <xsl:when test="@type = 'page'">
                          <xsl:text>%optPageTemplateTypeDefault%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@type = 'document'">
                          <xsl:text>%optPageTemplateTypeDocument%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@type = 'form'">
                          <xsl:text>%optPageTemplateTypeForm%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@type = 'newsletter'">
                          <xsl:text>%optPageTemplateTypeNewsletter%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@type = 'content'">
                          <xsl:text>%optPageTemplateTypeContent%</xsl:text>
                        </xsl:when>
                        <xsl:when test="@type = 'sectionpage'">
                          <xsl:text>%optPageTemplateTypeSectionPage%</xsl:text>
                        </xsl:when>
                      </xsl:choose>
                    </td>
                    <td align="center" class="{$className}" title="%msgClickToEdit%">
                      <xsl:call-template name="addJSEvent">
                        <xsl:with-param name="key" select="@key"/>
                      </xsl:call-template>
                      <xsl:call-template name="formatdatetime">
                        <xsl:with-param name="date" select="timestamp"/>
                      </xsl:call-template>
                    </td>
                    <td align="center" class="{$className}">
                      <xsl:call-template name="operations">
                        <xsl:with-param name="page" select="$page"/>
                        <xsl:with-param name="key" select="@key"/>
                        <xsl:with-param name="includecopy" select="'true'"/>
                      </xsl:call-template>
                    </td>
                  </tr>
                </xsl:for-each>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>