<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
    ]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>

  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:include href="common/genericheader.xsl"/>
  <xsl:include href="common/categoryheader.xsl"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="common/searchfield.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>

  <xsl:param name="contenttypekey"/>
  <xsl:param name="searchtype"/>
  <xsl:param name="scope"/>
  <xsl:param name="searchtext"/>

  <xsl:param name="asearchtext"/>
  <xsl:param name="ascope"/>
  <xsl:param name="subcategories"/>
  <xsl:param name="contenttypestring" select="'-1'"/>
  <xsl:param name="state"/>
  <xsl:param name="owner"/>
  <xsl:param name="created.op"/>
  <xsl:param name="created"/>
  <xsl:param name="modifier"/>
  <xsl:param name="modified.op"/>
  <xsl:param name="modified"/>
  <xsl:param name="acontentkey"/>
  <xsl:param name="filter"/>
  <xsl:param name="assignment.assigneeUserKey"/>
  <xsl:param name="assignment.assignerUserKey"/>
  <xsl:param name="assignment.dueDate"/>
  <xsl:param name="assignment.dueDate.op"/>

  <xsl:variable name="browsepageURL">
    <xsl:text>adminpage?page=</xsl:text>
    <xsl:value-of select="$page"/>
    <xsl:text>&amp;op=browse</xsl:text>
    <xsl:text>&amp;selectedunitkey=</xsl:text>
    <xsl:value-of select="$selectedunitkey"/>
    <xsl:text>&amp;reportsonly=true</xsl:text>
  </xsl:variable>

    <xsl:template match="/">

        <html>

            <script type="text/javascript" language="JavaScript" src="javascript/admin.js"/>
            <script type="text/javascript" src="javascript/tabpane.js"/>
            <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css"/>

            <script type="text/javascript" language="JavaScript">
                function selectChanged(selectId) {
                    var select = document.getElementById(selectId);
                    var viewSelect = document.getElementById('view' + selectId);
                    var createButton = document.getElementById('createreport');

                    if (viewSelect)
                      viewSelect.value = select.value;

                    createButton.disabled = select.value == '' ? true : false;
                }


                function OpenSelectorWindowResource( page, inserttypekey, fieldname, width, height )
                {
                    var pageURL = "adminpage?page=" + page;
                    pageURL = pageURL + "&amp;op=popup";
                    <xsl:if test="$menukey != ''">
                        pageURL = pageURL + "&amp;selectedmenukey=" + <xsl:value-of select="$menukey"/>;
                    </xsl:if>
                    pageURL = pageURL + "&amp;inserttypekey=" + inserttypekey;
                    pageURL = pageURL + "&amp;fieldname=" + fieldname;

                    var leftPosition = (screen.width - width) / 2;
                    var topPosition = (screen.height - height) / 2;

                    var props = "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" + height + ",top=" + topPosition + ",left=" + leftPosition;
                    newWindow = window.open(pageURL, "ResourceSelector", props);
                    newWindow.focus();
                }
            </script>

            <link rel="stylesheet" type="text/css" href="css/admin.css"/>

            <body>

                <form name="formAdmin" method="post" action="adminpage" target="_blank">
                    <input type="hidden" name="page" value="{$page}"/>
                    <input type="hidden" name="op" value="report"/>
                    <input type="hidden" name="subop" value="create"/>

                    <h1>
                        <xsl:call-template name="genericheader">
                            <xsl:with-param name="endslash" select="false()"/>
                        </xsl:call-template>
                        <xsl:call-template name="categoryheader">
                            <xsl:with-param name="rootelem" select="/stylesheets"/>
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
                                    <xsl:with-param name="type" select="'submit'"/>
                                    <xsl:with-param name="caption" select="'%cmdCreateReport%'"/>
                                    <xsl:with-param name="name" select="'createreport'"/>
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
      <script type="text/javascript">
        var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
      </script>

      <div class="tab-page" id="tab-page-1">
        <span class="tab">%blockReport%</span>

        <script type="text/javascript">
          tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
        </script>

        <fieldset>
          <table border="0" cellspacing="0" cellpadding="2" width="100%">
            <tr>
              <input type="hidden" name="cat" value="{$cat}"/>
              <xsl:choose>
                <xsl:when test="$searchtype = 'simple'">
                  <input type="hidden" name="searchtype" value="simple"/>
                  <input type="hidden" name="scope" value="{$scope}"/>
                  <input type="hidden" name="searchtext" value="{$searchtext}"/>
                </xsl:when>
                <xsl:otherwise>
                  <input type="hidden" name="searchtype" value="advanced"/>
                  <input type="hidden" name="asearchtext" value="{$asearchtext}"/>
                  <input type="hidden" name="ascope" value="{$ascope}"/>
                  <input type="hidden" name="subcategories" value="{$subcategories}"/>
                  <input type="hidden" name="contenttypestring" value="{$contenttypestring}"/>
                  <input type="hidden" name="state" value="{$state}"/>
                  <input type="hidden" name="owner" value="{$owner}"/>
                  <input type="hidden" name="created.op" value="{$created.op}"/>
                  <input type="hidden" name="datecreated" value="{$created}"/>
                  <input type="hidden" name="modifier" value="{$modifier}"/>
                  <input type="hidden" name="modified.op" value="{$modified.op}"/>
                  <input type="hidden" name="datemodified" value="{$modified}"/>
                  <input type="hidden" name="acontentkey" value="{$acontentkey}"/>
                  <input type="hidden" name="filter" value="{$filter}"/>

                  <input type="hidden" name="_assignee" value="{$assignment.assigneeUserKey}"/>
                  <input type="hidden" name="_assigner" value="{$assignment.assignerUserKey}"/>
                  <input type="hidden" name="date_assignmentDueDate" value="{$assignment.dueDate}"/>
                  <input type="hidden" name="_assignmentDueDate.op" value="{$assignment.dueDate.op}"/>
                </xsl:otherwise>
              </xsl:choose>

              <xsl:variable name="function">
                <xsl:text>javascript:OpenSelectorWindowResource( 800, 5, &apos;stylesheetkey&apos;, 800, 600 )</xsl:text>
              </xsl:variable>

              <xsl:call-template name="searchfield">
                <xsl:with-param name="label" select="'%fldStylesheet%:'"/>
                <xsl:with-param name="id" select="'stylesheetkey'"/>
                <xsl:with-param name="name" select="'stylesheetkey'"/>
                <xsl:with-param name="size" select="'25'"/>
                <xsl:with-param name="maxlength" select="'25'"/>
                <xsl:with-param name="buttonfunction" select="$function"/>
                <xsl:with-param name="colspan" select="'1'"/>
                <xsl:with-param name="onchange">
                  <xsl:text>selectChanged('stylesheetkey')</xsl:text>
                </xsl:with-param>
              </xsl:call-template>
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
                <xsl:attribute name="name">
                    <xsl:value-of select="$name"/>
                </xsl:attribute>
                <xsl:attribute name="id">
                    <xsl:value-of select="$name"/>
                </xsl:attribute>
                <xsl:attribute name="onchange">
                    <xsl:text>selectChanged('</xsl:text>
                    <xsl:value-of select="$name"/>
                    <xsl:text>')</xsl:text>
                </xsl:attribute>

                <xsl:if test="$emptyrow!=''">
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

 </xsl:stylesheet>