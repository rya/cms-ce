<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html"/>

    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/accesslevel_parameters.xsl"/>
    <xsl:include href="common/accessrights.xsl"/>
    <xsl:include href="common/textfield.xsl"/>
    <xsl:include href="common/textfield2.xsl"/>
    <xsl:include href="common/displaypath.xsl"/>
    <xsl:include href="common/genericheader.xsl"/>
    <xsl:include href="common/virtualhosts.xsl"/>
    <xsl:include href="common/dropdown_language.xsl"/>
    <xsl:include href="common/checkbox_boolean.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/resourcefield.xsl"/>
    <xsl:include href="common/displayerror.xsl"/>
    <xsl:include href="common/searchfield.xsl"/>
    <xsl:include href="common/user-picker-with-autocomplete.xsl"/>

    <xsl:param name="returnop" select="''"/>
    <xsl:param name="defaultCssExist"/>
    <xsl:param name="deviceClassResolverExist"/>
    <xsl:param name="defaultLocalizationResourceExist"/>
    <xsl:param name="localeResolverExist"/>

    <xsl:variable name="isCssValid">
      <xsl:choose>
        <xsl:when test="boolean(/menus/menu/menudata/defaultcss/@key) and $defaultCssExist = 'false'">
          <xsl:text>false</xsl:text>
        </xsl:when>
        <xsl:when test="boolean(/menus/menu/menudata/defaultcss/@key) and $defaultCssExist = 'true'">
          <xsl:text>true</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>true</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="pageURL">
        <xsl:text>adminpage?page=851&amp;op=form</xsl:text>
        <xsl:text>&amp;key=</xsl:text><xsl:value-of select="$menukey"/>
    </xsl:variable>

    <xsl:variable name="create" select="'0'"/>

    <xsl:template match="/">
        <xsl:variable name="pageCacheEnabled">
            <xsl:choose>
                <xsl:when test="/menus/menu/menudata/caching/pagecache/@disabled = 'false'">
                    <xsl:text>true</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>false</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="objectCacheEnabled">
            <xsl:choose>
                <xsl:when test="/menus/menu/menudata/caching/objectcache/@disabled = 'false'">
                    <xsl:text>true</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>false</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>


        <html>
            <head>
                <link href="css/admin.css" rel="stylesheet" type="text/css"/>
                <link type="text/css" rel="stylesheet" href="javascript/lib/jquery/ui/autocomplete/css/cms/jquery-ui-1.8.1.custom.css"/>
                <link type="text/css" rel="stylesheet" href="javascript/lib/jquery/ui/autocomplete/css/cms/jquery-ui-1.8.1.overrides.css"/>
                <link type="text/css" rel="stylesheet" href="css/user-picker-with-autocomplete.css"/>
                <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>

                <script type="text/javascript" src="javascript/admin.js">//</script>
                <script type="text/javascript" src="javascript/accessrights.js">//</script>
                <script type="text/javascript" src="javascript/validate.js">//</script>
                <script type="text/javascript" src="javascript/tabpane.js">//</script>

                <script type="text/javascript" src="javascript/lib/jquery/jquery-1.4.2.min.js">//</script>
                <script type="text/javascript" src="javascript/lib/jquery/ui/autocomplete/js/jquery-ui-1.8.1.custom.min.js">//</script>
                <script type="text/javascript" src="javascript/user-picker-with-autocomplete.js">//</script>
                <script type="text/javascript" language="JavaScript">

                    var idx = 0;
                    var validatedFields = new Array(100);
                    validatedFields[idx] = new Array("%fldName%", "name", validateRequired);
                    idx++;


                    function validateAll(formName)
                    {

                        // Check if accessrights have changed,
                        // and ask user if he wants to propagate
                        if( isAccessRightsChanged() ) {

                            // Reset propagate flag
                            document.getElementById("propagate").value = "false";
                            document.getElementById("propagate").value = "true";

                        }

                        var f = document.forms[formName];

                        if ( !checkAll(formName, validatedFields) ) {
                            return;
                        }

                        f.submit();
                    }

                </script>
            </head>

          <body onload="setFocus()" class="jquery-ui">
            <h1>
              <a href="adminpage?page=851&amp;op=listmenus">%headMenus%</a>
              <xsl:text>&nbsp;</xsl:text>
              <span id="titlename">
                <xsl:if test="/menus/menu/name != ''">
                  <xsl:value-of select="concat('/ ',/menus/menu/name)"/>
                </xsl:if>
              </span>
            </h1>

                <!--
                boolean(/menus/menu/menudata/defaultcss/@key) = <xsl:value-of select="boolean(/menus/menu/menudata/defaultcss/@key)"/>
                <br/>
                defaultCssExist = <xsl:value-of select="$defaultCssExist"/>

                <p>
                  isCssValid: <xsl:value-of select="$isCssValid"/>
                </p>
                -->

                <form action="adminpage?page={$page}&amp;op=update&amp;reload=true" method="POST" name="formAdmin" id="formAdmin">

                    <input type="hidden" id="resetcache" name="resetcache" value="false"/>

                    <input type="hidden" id="propagate" name="propagate" value="false"/>
                    <input type="hidden" id="propagate" name="selecteddomainkey" value="{$selecteddomainkey}"/>
                    <input type="hidden" id="propagate" name="menukey" value="{$menukey}"/>
                    <input type="hidden" id="propagate" name="returnop" value="{$returnop}"/>

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
                                        <td class="form_labelcolumn">%fldId%</td>
                                        <td>
                                            <xsl:value-of select="/menus/menu/@key"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td valign="baseline" class="form_labelcolumn">%fldName%:<span class="requiredfield">*</span></td>
                                        <td valign="baseline">
                                            <input type="text" value="{/menus/menu/name}" name="name" size="25">
                                                <xsl:attribute name="onkeyup">
                                                    <xsl:text>javascript: updateBreadCrumbHeader('titlename', this);</xsl:text>
                                                </xsl:attribute>
                                            </input>
                                        </td>
                                    </tr>
                                    <tr>
                                        <xsl:call-template name="dropdown_language">
                                            <xsl:with-param name="name" select="'languagekey'"/>
                                            <xsl:with-param name="label" select="'%fldLanguage%:'"/>
                                            <xsl:with-param name="selectedkey" select="/menus/menu/@languagekey"/>
                                            <xsl:with-param name="selectnode" select="/menus/languages/language"/>
                                        </xsl:call-template>
                                    </tr>
                                  <tr>
                                    <xsl:call-template name="resourcefield">
                                      <xsl:with-param name="name" select="'csskey'"/>
                                      <xsl:with-param name="mimetype" select="'text/css'"/>
                                      <xsl:with-param name="label" select="'%fldDefaultCSS%:'"/>
                                      <xsl:with-param name="value" select="/menus/menu/menudata/defaultcss/@key"/>
                                      <xsl:with-param name="exist" select="$defaultCssExist"/>
                                    </xsl:call-template>
                                  </tr>
                                </table>
                            </fieldset>

                            <fieldset>
                                <legend>&nbsp;%blockDetails%&nbsp;</legend>

                                <table cellspacing="0" cellpadding="2" border="0">
                                    <tr>
                                        <xsl:call-template name="textfield">
                                            <xsl:with-param name="name" select="'statistics'"/>
                                            <xsl:with-param name="label" select="'%fldStatisticsURL%:'"/>
                                            <xsl:with-param name="selectnode" select="/menus/menu/statistics"/>
                                            <xsl:with-param name="size" select="'47'"/>
                                            <xsl:with-param name="maxlength" select="'255'"/>
                                            <xsl:with-param name="colspan" select="'1'"/>
                                        </xsl:call-template>
                                    </tr>
                                </table>
                            </fieldset>

                            <fieldset>
                                <legend>&nbsp;%blockAllowedMenuItemTypes%&nbsp;</legend>

                                <table cellspacing="0" cellpadding="2" border="0">
                                    <tr>
                                        <td class="form_labelcolumn">%fldAllowLabelPageType%:</td>
                                        <td>
                                            <input type="checkbox" name="allow_label">
                                                <xsl:if test="/menus/menu/menudata/pagetypes/allow[@type='label']">
                                                    <xsl:attribute name="checked">checked</xsl:attribute>
                                                </xsl:if>
                                            </input>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="form_labelcolumn">%fldAllowURLPageType%:</td>
                                        <td>
                                            <input type="checkbox" name="allow_url">
                                                <xsl:if test="/menus/menu/menudata/pagetypes/allow[@type='url']">
                                                    <xsl:attribute name="checked">checked</xsl:attribute>
                                                </xsl:if>
                                            </input>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="form_labelcolumn">%fldAllowSectionType%:</td>
                                        <td>
                                            <input type="checkbox" name="allow_section">
                                                <xsl:if test="/menus/menu/menudata/pagetypes/allow[@type='section']">
                                                    <xsl:attribute name="checked">checked</xsl:attribute>
                                                </xsl:if>
                                            </input>
                                        </td>
                                    </tr>
                                </table>
                            </fieldset>
                            <fieldset>
                              <legend>&nbsp;%blockResources%</legend>

                              <table cellspacing="0" cellpadding="2" border="0">
                                <tr>
                                    <xsl:call-template name="textfield">
                                        <xsl:with-param name="name" select="'pathtopublichome'"/>
                                        <xsl:with-param name="label" select="'%fldResourcePathToPublicHome%:'"/>
                                        <xsl:with-param name="selectnode" select="/menus/menu/path-to-public-home-resources/@key"/>
                                        <xsl:with-param name="size" select="'47'"/>
                                        <xsl:with-param name="maxlength" select="'255'"/>
                                        <xsl:with-param name="colspan" select="'1'"/>
                                    </xsl:call-template>
                                </tr>
                                <tr>
                                    <xsl:call-template name="textfield">
                                        <xsl:with-param name="name" select="'pathtohome'"/>
                                        <xsl:with-param name="label" select="'%fldResourcePathToHome%:'"/>
                                        <xsl:with-param name="selectnode" select="/menus/menu/path-to-home-resources/@key"/>
                                        <xsl:with-param name="size" select="'47'"/>
                                        <xsl:with-param name="maxlength" select="'255'"/>
                                        <xsl:with-param name="colspan" select="'1'"/>
                                    </xsl:call-template>
                                </tr>
                              </table>
                            </fieldset>
                            <fieldset>
                            <legend>&nbsp;%blockDeviceClassResolver%&nbsp;</legend>
                            <table cellspacing="0" cellpadding="2" border="0">
                              <tr>
                                <xsl:call-template name="resourcefield">
                                  <xsl:with-param name="name" select="'deviceclassresolver'"/>
                                  <xsl:with-param name="extension" select="'xsl'"/>
                                  <xsl:with-param name="mimetype" select="'text/xml'"/>
                                  <xsl:with-param name="label" select="'%fldDeviceClassResolver%:'"/>
                                  <xsl:with-param name="value" select="/menus/menu/deviceclassresolver/@key"/>
                                  <xsl:with-param name="exist" select="$deviceClassResolverExist"/>
                                </xsl:call-template>
                              </tr>
                            </table>
                          </fieldset>

                          <fieldset>
                            <legend>&nbsp;%blockLocalization%&nbsp;</legend>
                            <table cellspacing="0" cellpadding="2" border="0">
                              <tr>
                                <xsl:call-template name="resourcefield">
                                  <xsl:with-param name="name" select="'defaultlocalizationresource'"/>
                                  <xsl:with-param name="extension" select="'properties'"/>
                                  <xsl:with-param name="mimetype" select="'application/octet-stream'"/>
                                  <xsl:with-param name="label" select="'%fldDefaultLocalizationResource%:'"/>
                                  <xsl:with-param name="value" select="/menus/menu/defaultlocalizationresource/@key"/>
                                    <xsl:with-param name="exist" select="$defaultLocalizationResourceExist"/>
                                </xsl:call-template>
                              </tr>
                                <tr>
                                <xsl:call-template name="resourcefield">
                                  <xsl:with-param name="name" select="'localeresolver'"/>
                                  <xsl:with-param name="extension" select="'xsl'"/>
                                  <xsl:with-param name="mimetype" select="'text/xml'"/>
                                  <xsl:with-param name="label" select="'%fldLocaleResolver%:'"/>
                                  <xsl:with-param name="value" select="/menus/menu/localeresolver/@key"/>
                                    <xsl:with-param name="exist" select="$localeResolverExist"/>
                                </xsl:call-template>
                              </tr>
                            </table>
                          </fieldset>

                            <fieldset>
                              <legend>&nbsp;%blockCaching%&nbsp;</legend>
                              <table cellspacing="0" cellpadding="2" border="0">
                                <xsl:variable name="runas-user-key" select="/menus/menu/run-as/user/@groupKey"/>

                                <xsl:variable name="autocomplete-help-element">
                                  <help>%hlpAssignToAutocomplete%</help>
                                </xsl:variable>

                                <tr>
                                  <xsl:call-template name="user-picker-with-autocomplete">
                                    <xsl:with-param name="name" select="'runas'"/>
                                    <xsl:with-param name="label" select="'%fldRunAs%:'"/>
                                    <xsl:with-param name="selected-user-key" select="$runas-user-key"/>
                                    <xsl:with-param name="selected-user-display-name" select="/menus/menu/run-as/user/displayName"/>
                                    <xsl:with-param name="selected-user-qualified-name" select="/menus/menu/run-as/user/qualifiedName"/>
                                    <xsl:with-param name="required" select="false()"/>
                                    <xsl:with-param name="ajax-service-function-to-execute" select="'findUsers'"/>
                                    <xsl:with-param name="use-user-group-key" select="true()"/>
                                    <xsl:with-param name="help-element" select="$autocomplete-help-element"/>
                                  </xsl:call-template>
                                </tr>
                              </table>
                            </fieldset>
                        </div>

                        <div class="tab-page" id="tab-page-7">
                            <span class="tab">%blockPageSecurity%</span>

                            <script type="text/javascript" language="JavaScript">
                                tabPane1.addTabPage( document.getElementById( "tab-page-7" ) );
                            </script>

                            <fieldset>
                                <legend>&nbsp;%blockPageSecurity%&nbsp;</legend>
                                <br/>
                                <xsl:call-template name="accessrights">
                                    <xsl:with-param name="right_publish_available" select="true()"/>
                                    <xsl:with-param name="right_add_available" select="true()"/>
                                    <xsl:with-param name="right_adminread_available" select="false()"/>
                                    <xsl:with-param name="dataxpath" select="/menus/menu"/>
                                    <xsl:with-param name="allowauthenticated" select="true()"/>
                                </xsl:call-template>
                                <br/>
                            </fieldset>
                        </div>

                        <script type="text/javascript" language="JavaScript">
                            setupAllTabs();
                        </script>
                    </div>

                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td class="form_form_buttonrow_seperator"><img src="images/1x1.gif"/></td>
                        </tr>
                    </table>
                    <xsl:call-template name="button">
                        <xsl:with-param name="type" select="'button'"/>
                        <xsl:with-param name="caption" select="'%cmdSave%'"/>
                        <xsl:with-param name="name" select="'lagre'"/>
                        <xsl:with-param name="onclick">
                            <xsl:text>javascript:validateAll('formAdmin');</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="disabled" select="$isCssValid = 'false'"/>
                    </xsl:call-template>

                    <xsl:text>&nbsp;</xsl:text>

                    <xsl:call-template name="button">
                        <xsl:with-param name="type" select="'button'"/>
                        <xsl:with-param name="caption" select="'%cmdCancel%'"/>
                        <xsl:with-param name="name" select="'avbryt'"/>
                        <xsl:with-param name="onclick">
                            <xsl:text>javascript:history.back();</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>

                </form>
            </body>

        </html>
    </xsl:template>

</xsl:stylesheet>
