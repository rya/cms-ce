<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html"/>

    <xsl:include href="common/textfield.xsl"/>
    <xsl:include href="common/passwordfield.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/displaysystempath.xsl"/>
    <xsl:include href="common/displayerror.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>

    <xsl:param name="page"/>
    <xsl:param name="create"/>
    <xsl:param name="key" select="''"/>

    <xsl:template match="/">
        <html>
            <head>
                <link href="css/admin.css" rel="stylesheet" type="text/css">
                </link>
                <script type="text/javascript" src="javascript/admin.js">
                </script>
                <script type="text/javascript" src="javascript/validate.js">
                </script>
                <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css">
                </link>
                <script type="text/javascript" language="JavaScript" src="javascript/tabpane.js">
                </script>

                <script type="text/javascript" language="JavaScript">

                    var validatedFields = new Array(5);
                    validatedFields[0] = new Array("%fldHostname%", "hostname", validateRequired);
                    validatedFields[1] = new Array("%fldPort%", "port", validateRequired);
                    validatedFields[2] = new Array("%fldPort%", "port", validateInt);
                    validatedFields[3] = new Array("%fldLoginDN%", "logindn", validateRequired);
                    validatedFields[4] = new Array("%fldLoginPassword%", "loginpassword", validateRequired);

                    function validateAll( formName )
                    {
                      var f = document.forms[formName];

                      if ( !checkAll(formName, validatedFields) )
                        return;

                      if ( !(document.getElementById('loginpassword').value == document.getElementById('loginpassword2').value) )
                      {
                        alert("%errPasswordsDontMatch%");
                        return;
                      }

                      selectAllRowsInSelect('selectedoc');
                      f.submit();
                    }
                </script>
            </head>

          <body>
            <h1>
              <xsl:call-template name="displaysystempath">
                <xsl:with-param name="page" select="$page"/>
              </xsl:call-template>
              <xsl:text>&nbsp;</xsl:text>
              <span id="titlename">
                <xsl:if test="$create != '1'">
                  <xsl:value-of select="concat('/ ', /ldapservers/ldapserver[@key = $key]/server/hostname)"/>
                </xsl:if>
              </span>
            </h1>


            <form name="formAdmin" method="post">
              <xsl:attribute name="action">
                <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
                        <xsl:choose>
                            <xsl:when test="$create = '1'">&amp;op=create</xsl:when>
                            <xsl:otherwise>&amp;op=update</xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>

                    <input type="hidden" name="key" value="{/ldapservers/ldapserver/@key}"/>

                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <!-- separator -->
                        <tr>
                            <td class="form_title_form_seperator"><img src="images/1x1.gif"/></td>
                        </tr>
                        <!-- form -->
                        <tr>
                            <td>
                                <div class="tab-pane" id="tab-pane-1">
                                    <script type="text/javascript" language="JavaScript">
                                        var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                                    </script>

                                    <div class="tab-page" id="tab-page-1">
                                        <span class="tab">%blockGeneral%</span>

                                        <script type="text/javascript" language="JavaScript">
                                            tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                                        </script>

                                        <fieldset>
                                            <legend>&nbsp;%blockGeneral%&nbsp;</legend>

                                            <table cellspacing="0" cellpadding="2" border="0">
                                                <tr>
                                                    <td class="form_labelcolumn"> </td>
                                                </tr>
                                                <tr>
                                                    <xsl:call-template name="textfield">
                                                        <xsl:with-param name="label" select="'%fldHostname%:'"/>
                                                        <xsl:with-param name="name" select="'hostname'"/>
                                                        <xsl:with-param name="selectnode" select="/ldapservers/ldapserver[@key = $key]/server/hostname"/>
                                                        <xsl:with-param name="required" select="'true'"/>
                                                        <xsl:with-param name="onkeyup">javascript: updateBreadCrumbHeader('titlename', this);</xsl:with-param>
                                                    </xsl:call-template>
                                                </tr>
                                                <tr>
                                                    <xsl:variable name="port">
                                                        <xsl:choose>
                                                            <xsl:when test="/ldapservers/ldapserver[@key = $key]">
                                                                <xsl:value-of select="/ldapservers/ldapserver[@key = $key]/server/port"/>
                                                            </xsl:when>
                                                            <xsl:otherwise>389</xsl:otherwise>
                                                        </xsl:choose>
                                                    </xsl:variable>
                                                    <xsl:call-template name="textfield">
                                                        <xsl:with-param name="label" select="'%fldPort%:'"/>
                                                        <xsl:with-param name="name" select="'port'"/>
                                                        <xsl:with-param name="selectnode" select="$port"/>
                                                        <xsl:with-param name="size" select="'5'"/>
                                                        <xsl:with-param name="required" select="'true'"/>
                                                    </xsl:call-template>
                                                </tr>
                                                <tr>
                                                    <xsl:call-template name="textfield">
                                                        <xsl:with-param name="label" select="'%fldLoginDN%:'"/>
                                                        <xsl:with-param name="name" select="'logindn'"/>
                                                        <xsl:with-param name="required" select="'true'"/>
                                                        <xsl:with-param name="selectnode" select="/ldapservers/ldapserver[@key = $key]/login/dn"/>
                                                    </xsl:call-template>
                                                </tr>
                                                <tr>
                                                    <xsl:call-template name="passwordfield">
                                                        <xsl:with-param name="label" select="'%fldLoginPassword%:'"/>
                                                        <xsl:with-param name="id" select="'loginpassword'"/>
                                                        <xsl:with-param name="name" select="'loginpassword'"/>
                                                        <xsl:with-param name="required" select="'true'"/>
                                                        <xsl:with-param name="selectnode" select="/ldapservers/ldapserver[@key = $key]/login/password"/>
                                                    </xsl:call-template>
                                                </tr>
                                                <tr>
                                                    <xsl:call-template name="passwordfield">
                                                        <xsl:with-param name="label" select="'%fldRepeatPassword%:'"/>
                                                        <xsl:with-param name="id" select="'loginpassword2'"/>
                                                        <xsl:with-param name="name" select="'loginpassword2'"/>
                                                        <xsl:with-param name="required" select="'true'"/>
                                                        <xsl:with-param name="selectnode" select="/ldapservers/ldapserver[@key = $key]/login/password"/>
                                                    </xsl:call-template>
                                                </tr>

                                            </table>
                                        </fieldset>
                                        <fieldset>
                                            <legend>&nbsp;%fldObjectClasses%&nbsp;</legend>
                                            <table>
                                                <tr>
                                                    <td valign="top"></td>
                                                    <td>
                                                        <table border="0" cellspacing="2" cellpadding="0" width="100%">
                                                            <tr>
                                                                <td>
                                                                    <div style="padding-bottom: 1em;">
                                                                        %msgAvailOC%
                                                                    </div>

                                                                    <select multiple="multiple" style="width: 13em; height: 10em;" name="availableoc" id="availableoc">
                                                                        <xsl:for-each select="/ldapservers/objectclasses/objectclass">
                                                                            <xsl:variable name="ockey" select="@key"/>
                                                                            <xsl:if test="not(/ldapservers/ldapserver[@key = $key]/objectclasses/objectclass[@key = $ockey])">
                                                                                <option value="{@key}" ondblclick="moveOptions('availableoc', 'selectedoc');">
                                                                                    <xsl:value-of select="name"/>
                                                                                    <xsl:if test="@auxilliary = 'true'">
                                                                                        <xsl:text> (%auxilliary%)</xsl:text>
                                                                                    </xsl:if>
                                                                                </option>
                                                                            </xsl:if>
                                                                        </xsl:for-each>
                                                                    </select>
                                                                </td>

                                                                <td style="padding: 0.5em;">
                                                                    <xsl:call-template name="button">
                                                                        <xsl:with-param name="type" select="'button'"/>
                                                                        <xsl:with-param name="image" select="'images/icon_move_right.gif'"/>
                                                                        <xsl:with-param name="onclick">
                                                                            <xsl:text>javascript:moveOptions('availableoc', 'selectedoc');</xsl:text>
                                                                        </xsl:with-param>
                                                                    </xsl:call-template>
                                                                    <br/>
                                                                    <xsl:call-template name="button">
                                                                        <xsl:with-param name="type" select="'button'"/>
                                                                        <xsl:with-param name="image" select="'images/icon_move_left.gif'"/>
                                                                        <xsl:with-param name="onclick">
                                                                            <xsl:text>javascript:moveOptions('selectedoc', 'availableoc');</xsl:text>
                                                                        </xsl:with-param>
                                                                    </xsl:call-template>
                                                                </td>

                                                                <td>
                                                                    <div style="padding-bottom: 1em;">
                                                                        %msgSelOC%
                                                                    </div>

                                                                    <select multiple="multiple" style="width: 13em; height: 10em;" name="selectedoc" id="selectedoc">
                                                                        <xsl:for-each select="/ldapservers/ldapserver[@key = $key]/objectclasses/objectclass">
                                                                            <xsl:variable name="key" select="@key"/>
                                                                            <option value="{$key}" ondblclick="moveOptions('selectedoc', 'availableoc');">
                                                                                <xsl:value-of select="/ldapservers/objectclasses/objectclass[@key = $key]/name"/>
                                                                                <xsl:if test="/ldapservers/objectclasses/objectclass[@key = $key]/@auxilliary = 'true'">
                                                                                    <xsl:text> (%auxilliary%)</xsl:text>
                                                                                </xsl:if>
                                                                            </option>
                                                                        </xsl:for-each>
                                                                    </select>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>

                                        </fieldset>
                                    </div>

                                    <div class="tab-page" id="tab-page-2">
                                        <span class="tab">%blockSecurity%</span>

                                        <script type="text/javascript" language="JavaScript">
                                            tabPane1.addTabPage( document.getElementById( "tab-page-2" ) );
                                        </script>

                                        <fieldset>
                                            <legend>&nbsp;%fldAllowedLDAPServerOperations%&nbsp;</legend>

                                            <table border="0" cellspacing="2" cellpadding="2">
                                                <tr>
                                                    <td>
                                                        <xsl:text>%fldRead%: </xsl:text>
                                                    </td>
                                                    <td>
                                                        <input type="checkbox" name="read">
                                                            <xsl:choose>
                                                                <xsl:when test="/ldapservers/ldapserver[@key = $key]">
                                                                    <xsl:if test="/ldapservers/ldapserver[@key = $key]/operations/operation[@type = 'read']/@allowed = 'true'">
                                                                        <xsl:attribute name="checked">checked</xsl:attribute>

                                                                    </xsl:if>
                                                                </xsl:when>
                                                                <xsl:otherwise><xsl:attribute name="checked">checked</xsl:attribute></xsl:otherwise>
                                                            </xsl:choose>
                                                        </input>
                                                    </td>

                                                </tr>

                                                <tr>
                                                    <td>
                                                        <xsl:text>%fldUpdate%: </xsl:text>
                                                    </td>
                                                    <td>
                                                        <input type="checkbox" name="update">
                                                            <xsl:choose>
                                                                <xsl:when test="/ldapservers/ldapserver[@key = $key]">
                                                                    <xsl:if test="/ldapservers/ldapserver[@key = $key]/operations/operation[@type = 'update']/@allowed = 'true'">
                                                                        <xsl:attribute name="checked">checked</xsl:attribute>
                                                                    </xsl:if>
                                                                </xsl:when>
                                                                <xsl:otherwise><xsl:attribute name="checked">checked</xsl:attribute></xsl:otherwise>
                                                            </xsl:choose>
                                                        </input>
                                                    </td>
                                                </tr>
                                                    
                                                <tr>
                                                    <td>
                                                        <xsl:text>%fldCreate%: </xsl:text>
                                                    </td>
                                                    <td>
                                                        <input type="checkbox" name="create">
                                                            <xsl:choose>
                                                                <xsl:when test="/ldapservers/ldapserver[@key = $key]">
                                                                    <xsl:if test="/ldapservers/ldapserver[@key = $key]/operations/operation[@type = 'create']/@allowed = 'true'">
                                                                        <xsl:attribute name="checked">checked</xsl:attribute>
                                                                    </xsl:if>
                                                                </xsl:when>
                                                                <xsl:otherwise><xsl:attribute name="checked">checked</xsl:attribute></xsl:otherwise>
                                                            </xsl:choose>
                                                        </input>
                                                    </td>
                                                </tr>

                                                <tr>
                                                    <td>
                                                        <xsl:text>%fldDelete%: </xsl:text>
                                                    </td>
                                                    <td>
                                                        <input type="checkbox" name="delete">
                                                            <xsl:choose>
                                                                <xsl:when test="/ldapservers/ldapserver[@key = $key]">
                                                                    <xsl:if test="/ldapservers/ldapserver[@key = $key]/operations/operation[@type = 'delete']/@allowed = 'true'">
                                                                        <xsl:attribute name="checked">checked</xsl:attribute>
                                                                    </xsl:if>
                                                                </xsl:when>
                                                                <xsl:otherwise><xsl:attribute name="checked">checked</xsl:attribute></xsl:otherwise>
                                                            </xsl:choose>
                                                        </input>
                                                    </td>
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
                            <td colspan="4">

                                <xsl:call-template name="button">
                                    <xsl:with-param name="type" select="'button'"/>
                                    <xsl:with-param name="caption" select="'%cmdSave%'"/>
                                    <xsl:with-param name="name" select="'lagre'"/>
                                    <xsl:with-param name="onclick">
                                        <xsl:text>javascript:validateAll('formAdmin');</xsl:text>
                                    </xsl:with-param>
                                </xsl:call-template>

                                <xsl:text>&nbsp;</xsl:text>

                                <xsl:call-template name="button">
                                    <xsl:with-param name="type" select="'button'"/>
                                    <xsl:with-param name="caption" select="'%cmdCancel%'"/>
                                    <xsl:with-param name="name" select="'avbryt'"/>
                                    <xsl:with-param name="onclick">
                                        <xsl:text>javascript:window.history.back();</xsl:text>
                                    </xsl:with-param>
                                </xsl:call-template>


                            </td>
                        </tr>
                    </table>
                </form>

            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
