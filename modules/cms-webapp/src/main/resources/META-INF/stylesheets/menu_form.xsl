<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html"/>

    <xsl:variable name="create" select="'1'"/>

    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/genericheader.xsl"/>
    <xsl:include href="common/javascriptPreload.xsl"/>
    <xsl:include href="common/displaypath.xsl"/>
    <xsl:include href="common/virtualhosts.xsl"/>
    <xsl:include href="common/textfield.xsl"/>
    <xsl:include href="common/textfield2.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/resourcefield.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>
    <xsl:include href="common/displayerror.xsl"/>

    <xsl:include href="common/dropdown_language.xsl"/>

    <xsl:param name="reload"/>

    <xsl:template match="/">

        <html>
            <head>
                <link href="css/admin.css" rel="stylesheet" type="text/css"/>
                <script type="text/javascript" language="JavaScript" src="javascript/admin.js"/>
                <script type="text/javascript" language="JavaScript" src="javascript/validate.js"/>
                <script type="text/javascript" src="javascript/tabpane.js"/>
                <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css"/>

                <xsl:call-template name="javascriptPreload"/>

                <script type="text/javascript" language="JavaScript">

                    <xsl:if test="$reload = 'true'">
						window.top.frames['leftFrame'].refreshMenu();
                    </xsl:if>

                    var validateFields = new Array(1);

                    validateFields[1] = new Array("%fldName%", "name", validateRequired);

                    function validateAll(formName) {

                        var f = document.forms[formName];

                        if ( !checkAll(formName, validateFields) )
                            return;

                        f.submit();
                    }

                </script>

            </head>

          <body onload="setFocus()">
            <h1>
              <a href="adminpage?mainmenu=true&amp;op=listmenus&amp;page=851">
                <xsl:text>%headMenus%</xsl:text>
              </a>
              <xsl:text>&nbsp;</xsl:text><span id="titlename"> </span>
            </h1>



                <form name="formAdmin" method="post" action="adminpage?page=851&amp;op=createmenu&amp;selecteddomainkey={$selecteddomainkey}">

                    <input type="hidden" name="redirect" value="adminpage?page=851&amp;selecteddomainkey={$selecteddomainkey}&amp;op=listmenus"/>

                    <table border="0" cellspacing="0" cellpadding="0" width="100%">
                        <tr>
                            <td class="browse_title_buttonrow_seperator"><img src="images/1x1.gif"/></td>
                        </tr>
                        <tr>
                            <td>
                                <div class="tab-pane" id="tab-pane-1">
                                    <script type="text/javascript" language="JavaScript">
                                        var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                                    </script>

                                    <div class="tab-page" id="tab-page-1">
                                        <span class="tab">%cmdNewSite%</span>

                                        <script type="text/javascript" language="JavaScript">
                                            tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                                        </script>

                                        <fieldset>
                                            <legend>&nbsp;%cmdNewSite%&nbsp;</legend>

                                            <table border="0" cellspacing="0" cellpadding="2" width="100%">
                                                <tr>
                                                    <td class="form_labelcolumn">%fldName%:<span class="requiredfield">*</span></td>
                                                    <td>
                                                        <input name="name" type="text" size="40" maxlength="64">
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
                                                        <xsl:with-param name="selectedkey" select="''"/>
                                                        <xsl:with-param name="selectnode" select="/menulanguages/languages/language"/>
                                                    </xsl:call-template>
                                                </tr>
                                                <tr>
                                                    <xsl:call-template name="resourcefield">
						                                <xsl:with-param name="name" select="'csskey'"/>
						                                <xsl:with-param name="mimetype" select="'text/css'"/>
						                                <xsl:with-param name="label" select="'%fldDefaultCSS%:'"/>
						                                <xsl:with-param name="value" select="/menus/menu/menudata/defaultcss/@key"/>
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
                                                <xsl:with-param name="label" select="'%fldStatisticsURL%'"/>
                                                <xsl:with-param name="selectnode" select="/menus/menu/details/statistics"/>
                                                <xsl:with-param name="size" select="'53'"/>
                                                <xsl:with-param name="maxlength" select="'255'"/>
                                                <xsl:with-param name="colspan" select="'1'"/>
                                              </xsl:call-template>
                                            </tr>
                                          </table>
                                        </fieldset>
                                    </div>
                                </div>
                                <script type="text/javascript" language="JavaScript">
                                    setupAllTabs();
                                </script>
                            </td>
                        </tr>
                        <tr>
                            <td class="form_form_buttonrow_seperator"><img src="images/1x1.gif"/></td>
                        </tr>
                        <tr>
                            <td>
                                <xsl:call-template name="button">
                                    <xsl:with-param name="type" select="'button'"/>
                                    <xsl:with-param name="caption" select="'%cmdSave%'"/>
                                    <xsl:with-param name="onclick">
                                        <xsl:text>javascript:validateAll("formAdmin");</xsl:text>
                                    </xsl:with-param>
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
                             </td>
                        </tr>
                    </table>
                </form>
            </body>

        </html>
    </xsl:template>

    <xsl:template name="dropdown_onchange">
        <xsl:param name="name"/>
        <xsl:param name="selectedkey"/>
        <xsl:param name="selectnode"/>
        <xsl:param name="colspan"/>
        <xsl:param name="emptyrow"/>

        <td valign="baseline">
            <select>
                <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>

                <xsl:if test="$selectedkey = ''">
                    <option value=""><xsl:value-of select="$emptyrow"/></option>
                </xsl:if>

                <xsl:for-each select="$selectnode">
                    <option>
                        <xsl:if test="$selectedkey = @key">
                            <xsl:attribute name="selected">selected</xsl:attribute>
                        </xsl:if>
                        <xsl:attribute name="value"><xsl:value-of disable-output-escaping="yes" select="@key"/></xsl:attribute>
                        <xsl:value-of select="name"/>
                    </option>
                </xsl:for-each>
            </select>
        </td>
    </xsl:template>

</xsl:stylesheet>
