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
  <xsl:include href="common/accesslevel_parameters.xsl"/>
  <xsl:include href="common/displaypath.xsl"/>
  <xsl:include href="common/button.xsl"/>

  <xsl:param name="reload"/>
  <xsl:param name="showstatistics" select="'false'"/>
  <xsl:param name="menuadministrate" select="'false'"/>
  <xsl:param name="pageCacheEnabled" select="''"/>
  <xsl:param name="selectedtabpage" select="''"/>
  <xsl:param name="defaultCssExist"/>
  <xsl:param name="deviceClassResolverExist"/>
  <xsl:param name="defaultLocalizationResourceExist"/>
  <xsl:param name="localeResolverExist"/>
  <xsl:param name="defaultRunAsUser"/>

  <xsl:param name="debugpath"/>

  <xsl:param name="feedback"/>

  <xsl:template match="/">
    <html>
      <head>
        <script type="text/javascript" src="javascript/admin.js"/>
        <script type="text/javascript" src="javascript/tabpane.js"/>

        <script type="text/javascript" src="javascript/cms/core.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Event.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Css.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Dimensions.js">//</script>
        <script type="text/javascript" src="javascript/cms/ui/SplitButton.js">//</script>

        <script type="text/javascript" language="JavaScript">
          <xsl:if test="$reload = 'true'">
            window.top.frames['leftFrame'].refreshMenu();
          </xsl:if>
        </script>

        <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css"/>
        <link href="css/admin.css" rel="stylesheet" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="javascript/cms/ui/style.css"/>

      </head>

      <body>

        <h1>
          <xsl:call-template name="displaypath">
            <xsl:with-param name="domainkey" select="$selecteddomainkey"/>
            <xsl:with-param name="unitkey" select="$selectedunitkey"/>
            <xsl:with-param name="presentationlayerkey" select="$menukey"/>
          </xsl:call-template>
        </h1>

        <xsl:if test="$siteadmin = 'true'">
          <xsl:if test="$feedback !=''">
            <p style="margin:2em 0">
              <span class="feedbackDiv">
                <xsl:choose>
                  <xsl:when test="$feedback = 'clearedcachedpages'">%msgPageCacheCleared%</xsl:when>
                  <xsl:when test="$feedback = 'clearedcachedobjects'">%msgPortletCacheCleared%</xsl:when>
                  <xsl:when test="$feedback = 'clearedcachedpagesandobjects'">%msgPageAndPortletCacheCleared%</xsl:when>
                  <xsl:otherwise> </xsl:otherwise>
                </xsl:choose>
              </span>
            </p>
          </xsl:if>
        </xsl:if>

        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td class="browse_title_buttonrow_seperator"><img src="images/1x1.gif"/></td>
          </tr>
          <tr>
            <td>
              <xsl:if test="$siteadmin = 'true'">

                <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'link'"/>
                  <xsl:with-param name="caption" select="'%cmdEdit%'"/>
                  <xsl:with-param name="href">
                    <xsl:text>adminpage?page=</xsl:text>
                    <xsl:value-of select="$page"/>
                    <xsl:text>&amp;op=form</xsl:text>
                    <xsl:text>&amp;key=</xsl:text>
                    <xsl:value-of select="$menukey"/>
                  </xsl:with-param>
                </xsl:call-template>

                <xsl:if test="$siteadmin = 'true' or $enterpriseadmin = 'true'">
                  <xsl:text>&nbsp;</xsl:text>

                  <xsl:call-template name="button">
                    <xsl:with-param name="type" select="'link'"/>
                    <xsl:with-param name="caption" select="'%cmdCopy%'"/>
                    <xsl:with-param name="href">
                      <xsl:text>adminpage?page=5&amp;redirect=adminpage%3Fpage=851%26op=copy</xsl:text>
                      <xsl:text>%26returnop=browse%26key=</xsl:text><xsl:value-of select="$menukey"/>
                    </xsl:with-param>
                    <xsl:with-param name="condition">
                      <xsl:text>confirm('%alertCopySiteCustom% </xsl:text>
                      <xsl:value-of select="/menus/menu/name"/>
                      <xsl:text>?')</xsl:text>
                    </xsl:with-param>
                  </xsl:call-template>
                </xsl:if>

                <xsl:text>&nbsp;</xsl:text>

                <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'link'"/>
                  <xsl:with-param name="caption" select="'%cmdDelete%'"/>
                  <xsl:with-param name="href">
                    <xsl:text>adminpage?page=</xsl:text>
                    <xsl:value-of select="$page"/>
                    <xsl:text>&amp;op=remove</xsl:text>
                    <xsl:text>&amp;key=</xsl:text><xsl:value-of select="$menukey"/>
                  </xsl:with-param>
                  <xsl:with-param name="condition">
                    <xsl:text>confirm('%alertDeleteSite%')</xsl:text>
                  </xsl:with-param>
                </xsl:call-template>

                <xsl:text>&nbsp;</xsl:text>
              </xsl:if>

              <xsl:call-template name="button">
                <xsl:with-param name="type" select="'link'"/>
                <xsl:with-param name="caption" select="'%btnOpenInIce%'"/>
                <xsl:with-param name="tooltip" select="'%btnOpenInIceTooltip%'"/>
                <xsl:with-param name="href" select="$debugpath"/>
                <xsl:with-param name="target" select="'_blank'"/>
              </xsl:call-template>

              <xsl:if test="$siteadmin = 'true'">

                <xsl:variable name="commandUrlPrefix">
                  <xsl:text>adminpage?page=</xsl:text>
                  <xsl:value-of select="$page"/>
                  <xsl:text>&amp;menukey=</xsl:text>
                  <xsl:value-of select="$menukey"/>
                </xsl:variable>

                <ul id="clearPageCacheEntriesBySite" title="%cmdClearPageCacheBySite%" class="cms-split-button no-default-action">
                  <li class="cms-menu-item-icon-clear-pages-only">
                    <a href="{concat($commandUrlPrefix, '&amp;op=clearcachedpages')}">%cmdClearPagesOnly%</a>
                  </li>
                  <li class="cms-menu-item-icon-clear-portlets-only">
                    <a href="{concat($commandUrlPrefix, '&amp;op=clearcachedobjects')}">%cmdClearPortletsOnly%</a>
                  </li>
                  <li>
                    <a href="{concat($commandUrlPrefix, '&amp;op=clearcachedpagesandobjects')}">%cmdClearPagesAndPortlets%</a>
                  </li>
                </ul>
                <script type="text/javascript" charset="utf-8">
                  var splitButton = new cms.ui.SplitButton('clearPageCacheEntriesBySite');
                  splitButton.insert();
                </script>

              </xsl:if>

            </td>
          </tr>
          <tr>
            <td class="browse_buttonrow_datarows_seperator"><img src="images/1x1.gif"/>
            </td>
          </tr>
        </table>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td>

              <div class="tab-pane" id="tab-pane-1">
                <script type="text/javascript" language="JavaScript">
                  var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                </script>

                <div class="tab-page" id="tab-page-1">
                  <span class="tab">%blockSite%</span>

                  <script type="text/javascript" language="JavaScript">
                    tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                  </script>

                  <fieldset>
                    <legend>&nbsp;%blockGeneral%&nbsp;</legend>
                    <table cellspacing="0" cellpadding="2" border="0">
                      <tr>
                        <td class="form_labelcolumn">%fldId%:</td>
                        <td><xsl:value-of select="/menus/menu/@key"/></td>
                      </tr>
                      <tr>
                        <td class="form_labelcolumn">%fldName%:</td>
                        <td><xsl:value-of select="/menus/menu/name"/></td>
                      </tr>
                      <tr>
                        <td class="form_labelcolumn">%fldLanguage%:</td>
                        <td><xsl:value-of select="/menus/menu/@language"/></td>
                      </tr>
                      <tr>
                        <xsl:variable name="csskey" select="/menus/menu/menudata/defaultcss/@key"/>
                        <td class="form_labelcolumn">%fldDefaultCSS%:</td>
                        <!--<td><xsl:value-of select="/menus/resources/resource[@key = $csskey]/name"/></td>-->
                        <td>
                          <xsl:choose>
                            <xsl:when test="not($csskey = '') and $defaultCssExist = 'false'">
                              <span class="warning-message" title="%msgResourceNotFound%">
                                <xsl:value-of select="$csskey"/>
                              </span>
                            </xsl:when>
                            <xsl:otherwise>
                              <xsl:value-of select="$csskey"/>
                            </xsl:otherwise>
                          </xsl:choose>
                        </td>
                      </tr>
                    </table>
                  </fieldset>

                  <fieldset>
                    <legend>&nbsp;%blockDetails%&nbsp;</legend>
                    <table cellspacing="0" cellpadding="2" border="0">
                      <tr>
                        <td class="form_labelcolumn">%fldStatisticsURL%:</td>
                        <td>
                          <xsl:if test="/menus/menu/statistics != ''">
                            <a href="{/menus/menu/statistics}" target="_blank">
                              <xsl:value-of select="/menus/menu/statistics"/>
                            </a>
                          </xsl:if>
                        </td>
                      </tr>
                    </table>
                  </fieldset>

                  <fieldset>
                    <legend>&nbsp;%blockSiteCaching%</legend>

                    <table cellspacing="0" cellpadding="2" border="0">
                      <tr>
                        <td colspan="4">
                          <table border="0" cellspacing="2" cellpadding="0">
                            <tr>
                              <td class="form_labelcolumn">%fldPageCache%:</td>
                              <td valign="top">
                                <xsl:choose>
                                  <xsl:when test="$pageCacheEnabled = 'true'">
                                    %enabled%
                                  </xsl:when>
                                  <xsl:otherwise>
                                    %disabled%
                                  </xsl:otherwise>
                                </xsl:choose>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </fieldset>

                  <fieldset>
                    <legend>&nbsp;%blockResources%</legend>
                    <table cellspacing="0" cellpadding="2" border="0">
                      <tr>
                        <td colspan="4">
                          <table border="0" cellspacing="2" cellpadding="0">
                            <tr>
                              <td class="form_labelcolumn">%fldResourcePathToPublicHome%:</td>
                              <td valign="top">
                                <xsl:value-of select="/menus/menu/path-to-public-home-resources/@key"/>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="4">
                          <table border="0" cellspacing="2" cellpadding="0">
                            <tr>
                              <td class="form_labelcolumn">%fldResourcePathToHome%:</td>
                              <td valign="top">
                                <xsl:value-of select="/menus/menu/path-to-home-resources/@key"/>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </fieldset>



                  <fieldset>
                    <legend>&nbsp;%blockDeviceClassResolver%</legend>
                    <table cellspacing="0" cellpadding="2" border="0">
                      <tr>
                        <xsl:variable name="deviceclassresolverkey" select="/menus/menu/deviceclassresolver/@key"/>
                        <td class="form_labelcolumn">%fldDeviceClassResolver%:</td>
                        <td>
                          <xsl:choose>
                            <xsl:when test="not($deviceclassresolverkey = '') and $deviceClassResolverExist = 'false'">
                              <span class="warning-message" title="%msgResourceNotFound%">
                                <xsl:value-of select="$deviceclassresolverkey"/>
                              </span>
                            </xsl:when>
                            <xsl:otherwise>
                              <xsl:value-of select="$deviceclassresolverkey"/>
                            </xsl:otherwise>
                          </xsl:choose>
                        </td>
                      </tr>
                    </table>
                  </fieldset>

                  <fieldset>
                    <legend>&nbsp;%blockLocalization%</legend>
                    <table cellspacing="0" cellpadding="2" border="0">
                      <tr>
                        <xsl:variable name="localizationResourceKey" select="/menus/menu/defaultlocalizationresource/@key"/>
                        <td class="form_labelcolumn">%fldDefaultLocalizationResource%:</td>
                        <td>
                          <xsl:choose>
                            <xsl:when test="not($localizationResourceKey = '') and $defaultLocalizationResourceExist = 'false'">
                              <span style="color: red" title="%msgResourceNotFound%">
                                <xsl:value-of select="$localizationResourceKey"/>
                              </span>
                            </xsl:when>

                            <xsl:otherwise>
                              <xsl:value-of select="$localizationResourceKey"/>
                            </xsl:otherwise>
                          </xsl:choose>
                        </td>
                      </tr>
                      <tr>
                        <xsl:variable name="localeResolverKey" select="/menus/menu/localeresolver/@key"/>
                        <td class="form_labelcolumn">%fldLocaleResolver%:</td>
                        <td>
                          <xsl:choose>
                            <xsl:when test="not($localeResolverKey = '') and $localeResolverExist = 'false'">
                              <span class="warning-message" title="%msgResourceNotFound%">
                                <xsl:value-of select="$localeResolverKey"/>
                              </span>
                            </xsl:when>

                            <xsl:otherwise>
                              <xsl:value-of select="$localeResolverKey"/>
                            </xsl:otherwise>
                          </xsl:choose>
                        </td>
                      </tr>


                    </table>
                  </fieldset>

                  <fieldset>
                    <legend>&nbsp;%blockCaching%&nbsp;</legend>
                    <table cellspacing="0" cellpadding="2" border="0">
                      <tr>
                        <td colspan="4">
                          <table border="0" cellspacing="2" cellpadding="0">
                            <tr>
                              <td class="form_labelcolumn">%fldRunAs%:</td>
                              <td valign="top">
                                <xsl:if test="string-length($defaultRunAsUser) &gt; 0">
                                  <xsl:value-of select="$defaultRunAsUser"/>
                                </xsl:if>
                                <xsl:if test="string-length($defaultRunAsUser) = 0">
                                  %txtNoDefaultUserSetOnSite%  
                                </xsl:if>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </fieldset>
                </div>

              </div>

              <script type="text/javascript" language="JavaScript">
                setupAllTabs();
              </script>

              <xsl:if test="$selectedtabpage !=''">
                <script type="text/javascript">
                  tabPane1.setSelectedPage('<xsl:value-of select="$selectedtabpage"/>');
                </script>
              </xsl:if>

            </td>
          </tr>
          <tr>
            <td class="form_form_buttonrow_seperator"><img src="images/1x1.gif"/></td>
          </tr>
        </table>

      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>