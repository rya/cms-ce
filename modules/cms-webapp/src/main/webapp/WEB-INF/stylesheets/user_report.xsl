<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html"/>

    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/genericheader.xsl"/>
    <xsl:include href="common/displayuserstorepath.xsl"/>
    <xsl:include href="common/checkbox_boolean.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/resourcefield.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>

    <xsl:param name="userstorekey" />
	<xsl:param name="userstorename" />

    <xsl:template match="/">

        <html>

            <script type="text/javascript" language="JavaScript" src="javascript/admin.js"/>
            <script type="text/javascript" language="JavaScript" src="javascript/groups.js"/>
            <script type="text/javascript" src="javascript/tabpane.js"/>
            <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css"/>

            <script type="text/javascript" language="JavaScript">
                function selectChanged(selectId) {
                    var select = document.getElementById(selectId);
                    var createButton = document.getElementById('createreport');
                    if (select.value == "")
                        createButton.disabled = true;
                    else
                        createButton.disabled = false;
                }

                function createReport() {

                  url = "adminpage?page=<xsl:value-of select="$page"/>&amp;op=report&amp;subop=create&amp;userstorekey=<xsl:value-of select="$userstorekey"/>&amp;stylesheetkey=";
                  url += document.forms["formAdmin"].stylesheetkey.value;

                  url += "&amp;selection="+ document.getElementById('selection').value;

                  if (document.getElementById('selection').value != 'all') {
                      var groups = document.getElementsByName('member');
                      for (var i = 0; i &lt; groups.length; ++i) {
                        url += "&amp;group="+ groups[i].value;
                      }

                      if (document.getElementById('recursivegroups').checked) {
                        url += "&amp;recursivegroups=true";
                      }
                  }

                  newWindow = window.open(url, "Report", "toolbar=0,location=0,directories=0,status=0,menubar=1,scrollbars=1,resizable=1,copyhistory=0,width=800,height=800");
                  newWindow.focus();
                }

            </script>

            <link rel="stylesheet" type="text/css" href="css/admin.css"/>

            <body>

                <form name="formAdmin" method="post">
                    <xsl:attribute name="action">
                        <xsl:text>adminpage?page=</xsl:text>
                        <xsl:value-of select="$page"/>
                        <xsl:text>&amp;op=report&amp;subop=create</xsl:text>
                    </xsl:attribute>

                    <h1>
                        <xsl:call-template name="displayuserstorepath">
							<xsl:with-param name="mode" select="'users'"/>
							<xsl:with-param name="userstorekey" select="$userstorekey"/>
							<xsl:with-param name="userstorename" select="$userstorename"/>
						</xsl:call-template>
                    </h1>

                    <h2>
                        <xsl:text>%headCreateReport%</xsl:text>
                    </h2>

                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td class="form_title_form_seperator"><img src="images/1x1.gif"/></td>
                        </tr>
                        <tr>
                            <td>
                                <xsl:call-template name="form"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="form_form_buttonrow_seperator"><img src="images/1x1.gif"/></td>
                        </tr>
                        <tr>
                            <td>
                                <xsl:call-template name="button">
                                    <xsl:with-param name="type" select="'button'"/>
                                    <xsl:with-param name="caption" select="'%cmdCreateReport%'"/>
                                    <xsl:with-param name="name" select="'createreport'"/>
                                    <xsl:with-param name="onclick" select="'javascript:createReport()'"/>
                                    <xsl:with-param name="disabled" select="'true'"/>
                                </xsl:call-template>
                                <xsl:text>&nbsp;</xsl:text>
                                <xsl:call-template name="button">
                                    <xsl:with-param name="type" select="'link'"/>
                                    <xsl:with-param name="caption" select="'%cmdBack%'"/>
                                    <xsl:with-param name="href" select="'javascript:history.back();'"/>
                                </xsl:call-template>
                            </td>
                        </tr>
                    </table>

                </form>
            </body>
        </html>

    </xsl:template>

    <xsl:template name="form">
        <div class="tab-pane" id="tab-pane-1">
            <script type="text/javascript" language="JavaScript">
                var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
            </script>

            <div class="tab-page" id="tab-page-1">
                <span class="tab">%blockReport%</span>

                <script type="text/javascript" language="JavaScript">
                    tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                </script>

                <fieldset>
                    <table border="0" cellspacing="0" cellpadding="2" width="100%">
                        <tr>
                            <xsl:variable name="function">
                                <xsl:text>javascript:OpenSelectorWindowResource( 800, 5, &apos;stylesheetkey&apos;, 800, 600 )</xsl:text>
                            </xsl:variable>

                            <!--xsl:call-template name="searchfield">
                                <xsl:with-param name="label" select="'%fldStylesheet%:'"/>
                                <xsl:with-param name="name" select="'stylesheetkey'"/>
                                <xsl:with-param name="size" select="'25'"/>
                                <xsl:with-param name="maxlength" select="'25'"/>
                                <xsl:with-param name="buttonfunction" select="$function"/>
                                <xsl:with-param name="colspan" select="'1'"/>
                                <xsl:with-param name="onchange">
                                    <xsl:text>selectChanged('stylesheetkey')</xsl:text>
                                </xsl:with-param>
                            </xsl:call-template-->

                            <xsl:call-template name="resourcefield">
                                <xsl:with-param name="name" select="'stylesheetkey'"/>
                                <xsl:with-param name="extension" select="'xsl'"/>
                                <xsl:with-param name="mimetype" select="'text/xml'"/>
                                <xsl:with-param name="label" select="'%fldStylesheet%:'"/>
                                <!--xsl:with-param name="value" select="/contentobjects/contentobject/objectstylesheet/@key"/-->
                                <xsl:with-param name="required" select="'true'"/>
                                <xsl:with-param name="onchange">
                                    <xsl:text>selectChanged('stylesheetkey')</xsl:text>
                                </xsl:with-param>
                            </xsl:call-template>
                        </tr>
                    </table>
                </fieldset>

                <fieldset>
                    <legend>%blockSelection%</legend>

                    <table border="0" cellspacing="0" cellpadding="2" width="100%">
                        <tr>
                            <td class="form_labelcolumn" valign="top">
                                %fldSelectUsers%:
                            </td>
                            <td>
                                <select name="selection" id="selection">
                                    <xsl:attribute name="onchange">
                                        <xsl:text>javascript:if (this.value == 'groups') { document.getElementById('selectgroups').style.display = 'inline'; document.getElementById('recursivegroupsSpan').style.display = 'inline'; }</xsl:text>
                                        <xsl:text> else { document.getElementById('selectgroups').style.display = 'none'; document.getElementById('recursivegroupsSpan').style.display = 'none'; }</xsl:text>
                                    </xsl:attribute>
                                    <option value="all">%optAllUsers%</option>
                                    <option value="groups">%optGroupMembers%</option>
                                </select>
                                <span id="recursivegroupsSpan" style="display: none">&nbsp;<input type="checkbox" name="recursivegroups" id="recursivegroups" value="true"/>&nbsp;%fldGetGroupMembersRecursivly%</span>

                                <br/>

                                <table id="selectgroups" style="display: none" border="0" cellspacing="0" cellpadding="2">
                                    <xsl:call-template name="groups"/>
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
     </xsl:template>

    <xsl:template name="dropdown_stylesheet">
        <xsl:param name="label"/>
        <xsl:param name="name"/>
        <xsl:param name="selectnode"/>
        <xsl:param name="colspan"/>
        <xsl:param name="emptyrow"/>

        <td class="form_labelcolumn" valign="baseline" nowrap="nowrap"><xsl:value-of select="$label"/></td>
        <td nowrap="nowrap">
            <select>
                <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>

                <xsl:if test="$emptyrow !=''">
                    <option value=""><xsl:value-of select="$emptyrow"/></option>
                </xsl:if>

                <xsl:for-each select="$selectnode">
                    <option>
                        <xsl:attribute name="value"><xsl:value-of disable-output-escaping="yes" select="@key"/></xsl:attribute>
                        <xsl:value-of select="name"/>
                    </option>
                </xsl:for-each>
            </select>
        </td>
    </xsl:template>

    <xsl:template name="groups">
        <tr>
            <td>
                <table border="0" cellspacing="2" cellpadding="0">
                  <tbody id="memberstable">

                  </tbody>
                </table>
            </td>
        </tr>

        <tr>
            <td>
                <xsl:call-template name="button">
                    <xsl:with-param name="name" select="'butAddAccesRightRow'"/>
                    <xsl:with-param name="type" select="'button'"/>
                    <xsl:with-param name="caption" select="'%cmdAddGroup%'"/>
                    <xsl:with-param name="onclick">
                        <xsl:text>javascript:showUserAndGroupsPopup(</xsl:text>
                        <xsl:value-of select="$userstorekey"/>
                        <xsl:text>, 'groups', false, false, true, true, true)</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>
            </td>
        </tr>
    </xsl:template>

</xsl:stylesheet>