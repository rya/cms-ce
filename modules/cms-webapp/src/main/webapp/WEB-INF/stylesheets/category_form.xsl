<?xml version="1.0" encoding="utf-8"?>

<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
    ]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>

  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:include href="common/genericheader.xsl"/>
  <xsl:include href="common/categoryheader.xsl"/>
  <xsl:include href="common/textarea.xsl"/>
  <xsl:include href="common/textfield.xsl"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="common/searchfield.xsl"/>
  <xsl:include href="common/dropdown.xsl"/>
  <xsl:include href="common/readonlyvalue.xsl"/>
  <xsl:include href="common/accessrights.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>
  <xsl:include href="common/displayerror.xsl"/>
  <xsl:include href="common/serialize.xsl"/>

  <xsl:param name="create"/>
  <xsl:param name="contenttypekey"/>
  <xsl:param name="modulepage"/>
  <xsl:param name="supercategoryname"/>
  <xsl:param name="nocontent" select="true()"/>
  <xsl:param name="contentarchive" select="''"/>
  <xsl:param name="enterpriseadmin"/>
  <xsl:param name="minoccurrence"/>
  <xsl:param name="maxoccurrence"/>

  <xsl:template match="/">
    <html>
      <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css"/>
      <link type="text/css" rel="stylesheet" href="css/admin.css"/>
      <script type="text/javascript" language="JavaScript" src="javascript/admin.js"/>
      <script type="text/javascript" language="JavaScript" src="javascript/validate.js"/>
      <script type="text/javascript" language="JavaScript" src="javascript/accessrights.js"/>
      <script type="text/javascript" language="JavaScript" src="javascript/tabpane.js"/>
      <script type="text/javascript" language="JavaScript">
        function calculateArchiveSizeByCategory(categoryKey) {
            document.getElementById("size").innerHTML = "%sysPleaseWait%";
            AjaxService.getArchiveSizeByCategory(categoryKey, {callback:handleResponse_getArchiveSizeByCategory});
        }

        function handleResponse_getArchiveSizeByCategory(size) {
            var niceSize = displayBytes(size);
            var code = "document.getElementById('size').innerHTML = '" + niceSize + "';";
            setTimeout(code, 500);
        }

        var validatedFields = new Array(1);
        validatedFields[0] = new Array("Navn", "name", validateRequired);

        function validateAll(formName) {

            <xsl:if test="not($create)">
            if( isAccessRightsChanged() ) {

                // Reset propagate flag
                document.getElementById("propagate").value = "false";

                //if( confirm("%askPropagateSecurity%") ) {
                    document.getElementById("propagate").value = "true";
                //}
            }
            </xsl:if>

            var f = document.forms[formName];

            if ( !checkAll(formName, validatedFields) )
                return;

            var saveButton = document.getElementById('lagre');
            saveButton.disabled = true;

            f.submit();
        }

        function OpenSelectorWindowSiteCty( page, width, height, categoryKey )
        {
            newWindow = window.open("adminpage?page=" + page + "&amp;op=select&amp;contenttypekey=" + document.formAdmin.contenttypekey.value + "&amp;excludecategorykey=" + categoryKey +"&amp;selectedunitkey="+ document.formAdmin.unitkey.value, "Preview", "toolbar=0,location=0,directories=0,status=1,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" + height);
            newWindow.focus();
        }
      </script>

      <body>
        <xsl:attribute name="onload">
          <xsl:choose>
            <xsl:when test="$create">setFocus()</xsl:when>
            <xsl:otherwise>setFieldFocus('name')</xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>

        <form name="formAdmin" method="post">
          <xsl:attribute name="action">
            <xsl:choose>
              <xsl:when test="$create">
                <xsl:text>adminpage?page=</xsl:text>
                <xsl:value-of select="$page"/>
                <xsl:text>&amp;op=create</xsl:text>
              </xsl:when>
              <xsl:when test="not($create)">
                <xsl:text>adminpage?page=</xsl:text>
                <xsl:value-of select="$page"/>
                <xsl:text>&amp;op=update</xsl:text>
              </xsl:when>
            </xsl:choose>
            <xsl:if test="$cat != ''">
              <xsl:text>&amp;cat=</xsl:text>
              <xsl:value-of select="$cat"/>
            </xsl:if>
          </xsl:attribute>

          <input type="hidden" name="minoccurrence" value="{$minoccurrence}"/>
          <input type="hidden" name="maxoccurrence" value="{$maxoccurrence}"/>

          <input type="hidden" id="propagate" name="propagate" value="false"/>

          <input type="hidden" name="modulepage">
            <xsl:attribute name="value"><xsl:value-of select="$modulepage"/></xsl:attribute>
          </input>
          <input type="hidden" name="contentarchive">
            <xsl:attribute name="value"><xsl:value-of select="$contentarchive"/></xsl:attribute>
          </input>
          <input type="hidden" name="selecteddomainkey" value="{$selecteddomainkey}"/>
          <xsl:if test="$selectedunitkey != ''">
            <input type="hidden" name="selectedunitkey" value="{$selectedunitkey}"/>
          </xsl:if>

          <xsl:if test="not($create)">
            <input type="hidden" name="key">
              <xsl:attribute name="value"><xsl:value-of select="/categories/category/@key"/></xsl:attribute>
            </input>
            <input type="hidden" name="supercategorykey">
              <xsl:attribute name="value"><xsl:value-of select="/categories/category/@supercategorykey"/></xsl:attribute>
            </input>
            <input type="hidden" name="ownerkey">
              <xsl:attribute name="value"><xsl:value-of select="/categories/category/owner/@key"/></xsl:attribute>
            </input>
            <input type="hidden" name="created">
              <xsl:attribute name="value"><xsl:value-of select="/categories/category/@created"/></xsl:attribute>
            </input>
          </xsl:if>

          <xsl:if test="$create">
            <input type="hidden" name="key" value="0"/>
            <input type="hidden" name="supercategorykey">
              <xsl:attribute name="value"><xsl:value-of select="$cat"/></xsl:attribute>
            </input>
            <xsl:if test="$selectedunitkey = ''">
              <input type="hidden" name="contenttypekey">
                <xsl:attribute name="value"><xsl:value-of select="$contenttypekey"/></xsl:attribute>
              </input>
            </xsl:if>
          </xsl:if>

          <h1>
            <xsl:call-template name="genericheader">
              <xsl:with-param name="sitelink" select="true()"/>
              <xsl:with-param name="endslash" select="false()"/>
              <xsl:with-param name="minoccurrence" select="$minoccurrence"/>
              <xsl:with-param name="maxoccurrence" select="$maxoccurrence"/>
            </xsl:call-template>
            <xsl:call-template name="categoryheader">
              <xsl:with-param name="create">
                <xsl:value-of select="$create"/>
              </xsl:with-param>
            </xsl:call-template>

            <xsl:text>&nbsp;</xsl:text>

          </h1>

          <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td>
                <xsl:call-template name="categoryform"/>
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

  <xsl:template name="categoryform">

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

          <table width="100%" border="0" cellspacing="2" cellpadding="2">
            <tr>
              <xsl:choose>
                <xsl:when test="$selectedunitkey = '' and /categories/category">
                  <input type="hidden" name="contenttypekey">
                    <xsl:attribute name="value">
                      <xsl:value-of select="/categories/category/@contenttypekey"/>
                    </xsl:attribute>
                  </input>
                </xsl:when>
                <xsl:when test="$selectedunitkey != '' and ($cat = '' or /categories/category and not(/categories/category/@supercategorykey))">
                  <input type="hidden" name="contenttypekey" value=""/>
                </xsl:when>
                <xsl:when test="$selectedunitkey != '' and $nocontent and ( not(/categories/category/accessrights/userright) or /categories/category/accessrights/userright/@administrate = 'true' )">
                  <xsl:choose>
                    <!-- When content types are specified in the unit -->
                    <xsl:when test="/categories/unit/contenttypes/contenttype/@key">
                      <td>%fldContentType%:</td>
                      <td>
                        <select name="contenttypekey">
                          <option value="">%sysDropDownNone%</option>
                          <xsl:for-each select="/categories/unit/contenttypes/contenttype">
                            <xsl:sort select="/categories/contenttypes/contenttype[@key = current()/@key]/name" order="ascending"/>
                            <xsl:variable name="ctykey"><xsl:value-of select="@key"/></xsl:variable>
                            <xsl:if test="/categories/contenttypes/contenttype[@key = $ctykey]">
                              <option value="{$ctykey}">
                                <xsl:if test="(/categories/category/@contenttypekey = $ctykey) or ($contenttypekey = $ctykey)">
                                  <xsl:attribute name="selected">selected</xsl:attribute>
                                </xsl:if>
                                <xsl:value-of select="/categories/contenttypes/contenttype[@key = $ctykey]/name"/></option>
                            </xsl:if>
                          </xsl:for-each>
                        </select>
                      </td>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:call-template name="dropdown">
                        <xsl:with-param name="name" select="'contenttypekey'"/>
                        <xsl:with-param name="label" select="'%fldContentType%:'"/>
                        <xsl:with-param name="selectnode" select="/categories/contenttypes/contenttype"/>
                        <xsl:with-param name="selectedkey" select="/categories/category/@contenttypekey"/>
                        <xsl:with-param name="defaultkey" select="$contenttypekey"/>
                        <xsl:with-param name="colspan" select="'1'"/>
                        <xsl:with-param name="emptyrow" select="'%sysDropDownNone%'"/>
                        <xsl:with-param name="sort-by-node-name" select="'name'"/>
                        <xsl:with-param name="sort-order" select="'ascending'"/>
                      </xsl:call-template>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:when>
                <xsl:when test="$selectedunitkey != ''">
                  <input type="hidden" name="contenttypekey">
                    <xsl:attribute name="value">
                      <xsl:value-of select="/categories/category/@contenttypekey"/>
                    </xsl:attribute>
                  </input>
                  <xsl:call-template name="readonlyvalue">
                    <xsl:with-param name="name" select="'contenttype'"/>
                    <xsl:with-param name="label" select="'%fldContentType%:'"/>
                    <xsl:with-param name="selectnode" select="/categories/contenttypes/contenttype[@key = /categories/category/@contenttypekey]/name"/>
                    <xsl:with-param name="colspan" select="'1'"/>
                  </xsl:call-template>
                </xsl:when>
              </xsl:choose>
            </tr>
            <xsl:choose>
              <xsl:when test="$create or $selectedunitkey != '' or ($selectedunitkey = '' and /categories/category/@supercategorykey)">
                <tr>
                  <xsl:call-template name="textfield">
                    <xsl:with-param name="name" select="'name'"/>
                    <xsl:with-param name="label" select="'%fldName%:'"/>
                    <xsl:with-param name="selectnode">
                      <xsl:choose>
                        <xsl:when test="/categories/category/@name">
                          <xsl:value-of select="/categories/category/@name"/>
                        </xsl:when>
                        <xsl:when test="$contentarchive = 'true'">
                          <xsl:text>%txtContentRepository%</xsl:text>
                        </xsl:when>
                      </xsl:choose>
                    </xsl:with-param>
                    <xsl:with-param name="size" select="'40'"/>
                    <xsl:with-param name="maxlength" select="'255'"/>
                    <xsl:with-param name="colspan" select="'1'"/>
                    <xsl:with-param name="required" select="'true'"/>
                    <xsl:with-param name="onkeyup">javascript: updateBreadCrumbHeader('titlename', this);</xsl:with-param>
                  </xsl:call-template>
                </tr>
              </xsl:when>

              <xsl:otherwise>
                <input type="hidden" name="name" value="{/categories/category/@name}"/>
              </xsl:otherwise>
            </xsl:choose>
            <tr>
              <xsl:call-template name="textarea">
                <xsl:with-param name="name" select="'description'"/>
                <xsl:with-param name="label" select="'%fldDescription%:'"/>
                <xsl:with-param name="selectnode" select="/categories/category/description"/>
                <xsl:with-param name="rows" select="'5'"/>
                <xsl:with-param name="cols" select="'60'"/>
                <xsl:with-param name="colspan" select="'1'"/>
              </xsl:call-template>
            </tr>
          </table>
        </fieldset>

        <xsl:if test="not($create)">
          <fieldset>
            <legend>&nbsp;%blockCategorySize%&nbsp;</legend>
            <table width="100%" border="0" cellspacing="2" cellpadding="2">
              <xsl:choose>
                <xsl:when test="$enterpriseadmin = 'true'">
                  <tr>
                    <xsl:call-template name="labelcolumn">
                      <xsl:with-param name="label" select="'%lblContentRepositorySize%:'"/>
                    </xsl:call-template>
                    <td>
                      <span id="size">?</span>
                      &nbsp;
                      &nbsp;
                      <a href="#">
                        <xsl:attribute name="onclick">
                          <xsl:text>javascript: calculateArchiveSizeByCategory(</xsl:text>
                          <xsl:value-of select="/categories/category/@key"/>
                          <xsl:text>);</xsl:text>
                        </xsl:attribute>
                        &lt;%msgClickHereToCalculate%&gt;
                      </a>
                    </td>
                  </tr>
                  <tr>
                    <td colspan="2">%msgContentRepositorySize%</td>
                  </tr>
                </xsl:when>
                <xsl:otherwise>
                  <tr><td>%msgOnlyAvailableForEnterpriseAdministrators%</td></tr>
                </xsl:otherwise>
              </xsl:choose>
            </table>
          </fieldset>
        </xsl:if>
      </div>

      <xsl:if test="not(/categories/category/accessrights/userright) or /categories/category/accessrights/userright/@administrate = 'true'">
        <div class="tab-page" id="tab-page-2">
          <span class="tab">%blockProperties%</span>

          <script type="text/javascript" language="JavaScript">
            tabPane1.addTabPage( document.getElementById( "tab-page-2" ) );
          </script>

          <fieldset>
            <legend>&nbsp;%blockProperties%&nbsp;</legend>

            <table width="100%" border="0" cellspacing="2" cellpadding="2">
              <tr>
                <xsl:call-template name="labelcolumn">
                  <xsl:with-param name="width" select="200"/>
                  <xsl:with-param name="label" select="'%lblAutoApprove%'"/>
                  <xsl:with-param name="fieldname" select="'autoApprove_help_text'"/>
                  <xsl:with-param name="helpelement">
                    <xsl:element name="help">
                      <xsl:text>%txtHelpAutoApprove%</xsl:text>
                    </xsl:element>
                  </xsl:with-param>
                </xsl:call-template>
                <td>
                  <xsl:call-template name="displayhelp">
                    <xsl:with-param name="fieldname" select="'autoApprove_help_text'"/>
                    <xsl:with-param name="helpelement">
                      <xsl:element name="help">
                        <xsl:text>%txtHelpAutoApprove%</xsl:text>
                      </xsl:element>
                    </xsl:with-param>
                  </xsl:call-template>
                  <select name="autoApprove">
                    <xsl:choose>
                      <xsl:when test="/categories/category/@autoApprove = 'true'">
                        <option value="true" selected="true">true</option>
                        <option value="false">false</option>
                      </xsl:when>
                      <xsl:otherwise>
                        <option value="true">true</option>
                        <option value="false" selected="true">false</option>
                      </xsl:otherwise>
                    </xsl:choose>
                  </select>
                </td>
              </tr>

            </table>
          </fieldset>
        </div>

        <div class="tab-page" id="tab-page-3">
          <span class="tab">%blockPageSecurity%</span>

          <script type="text/javascript" language="JavaScript">
            tabPane1.addTabPage( document.getElementById( "tab-page-3" ) );
          </script>

          <fieldset>
            <legend>&nbsp;%blockPageSecurity%&nbsp;</legend>
            <br/>
            <xsl:choose>
              <xsl:when test="$create = false()">
                <xsl:call-template name="accessrights">
                  <xsl:with-param name="right_adminread_available" select="true()"/>
                  <xsl:with-param name="right_update_available" select="false()"/>
                  <xsl:with-param name="right_delete_available" select="false()"/>
                  <xsl:with-param name="dataxpath" select="/categories/category"/>
                  <xsl:with-param name="archive_hack" select="true()"/>
                  <xsl:with-param name="read_disabled" select="false()"/>
                  <xsl:with-param name="allowauthenticated" select="true()"/>
                </xsl:call-template>
              </xsl:when>
              <xsl:otherwise>
                <xsl:call-template name="accessrights">
                  <xsl:with-param name="right_adminread_available" select="true()"/>
                  <xsl:with-param name="right_update_available" select="false()"/>
                  <xsl:with-param name="right_delete_available" select="false()"/>
                  <xsl:with-param name="dataxpath" select="/categories"/>
                  <xsl:with-param name="archive_hack" select="true()"/>
                  <xsl:with-param name="read_disabled" select="false()"/>
                  <xsl:with-param name="allowauthenticated" select="true()"/>
                </xsl:call-template>
              </xsl:otherwise>
            </xsl:choose>
            <br/>
          </fieldset>
        </div>
      </xsl:if>
    </div>
    <script type="text/javascript" language="JavaScript">
      setupAllTabs();
    </script>
  </xsl:template>
</xsl:stylesheet>