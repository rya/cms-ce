<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html"/>

    <xsl:include href="common/textfield.xsl"/>
    <xsl:include href="common/textarea.xsl"/>
    <xsl:include href="common/dropdown_boolean.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/displaysystempath.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>
    <xsl:include href="common/displayerror.xsl"/>
    <xsl:include href="common/serialize.xsl"/>

    <xsl:param name="page"/>
    <xsl:param name="create"/>
    <xsl:param name="key" select="''"/>
    <xsl:param name="schema"/>

    <xsl:template match="/">
        <html>
            <head>
                <link href="css/admin.css" rel="stylesheet" type="text/css"/>

                <script type="text/javascript" src="javascript/admin.js">//</script>
                <script type="text/javascript" src="javascript/tabpane.js">//</script>
                <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css" />

                <script type="text/javascript" language="JavaScript" src="javascript/validate.js"/>
                <script type="text/javascript" language="JavaScript">
                    var validatedFields = new Array(3);
                    validatedFields[0] = new Array("%fldName%", "name", validateRequired);
                    validatedFields[1] = new Array("%fldOID%", "oid", validateRequired);
                    validatedFields[2] = new Array("%fldOCSchema%", "schema", validateRequired);

                    var editorClosed = 'true';

                    function validateAll(formName)
                    {
                      var f = document.forms[formName];

                      if ( !checkAll(formName, validatedFields) )
                        return;

                      f.submit();
                    }
                </script>
            </head>

            <body bgcolor="white" background="images/main_background.gif">
              <h1>
                <a>
                  <xsl:call-template name="displaysystempath">
                    <xsl:with-param name="page" select="$page"/>
                  </xsl:call-template>
                </a>
                <xsl:text>&nbsp;</xsl:text>
                <span id="titlename">
                  <xsl:if test="$create != '1'">
                    <xsl:value-of select="concat('/ ', /objectclasses/objectclass[@key = $key]/name)"/>
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

                <input type="hidden" name="key" value="{/objectclasses/objectclass/@key}"/>

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
                                <td class="form_labelcolumn"></td>
                              </tr>
                              <tr>
                                <xsl:call-template name="textfield">
                                  <xsl:with-param name="label" select="'%fldName%:'"/>
                                  <xsl:with-param name="name" select="'name'"/>
                                  <xsl:with-param name="selectnode" select="/objectclasses/objectclass[@key = $key]/name"/>
                                  <xsl:with-param name="required" select="'true'"/>
                                  <xsl:with-param name="onkeyup">javascript: updateBreadCrumbHeader('titlename', this);</xsl:with-param>
                                </xsl:call-template>
                              </tr>
                              <tr>
                                <xsl:call-template name="textfield">
                                  <xsl:with-param name="label" select="'%fldOID%:'"/>
                                  <xsl:with-param name="name" select="'oid'"/>
                                  <xsl:with-param name="selectnode" select="/objectclasses/objectclass[@key = $key]/oid"/>
                                  <xsl:with-param name="required" select="'true'"/>
                                </xsl:call-template>
                              </tr>
                              <tr>
                                <xsl:call-template name="dropdown_boolean">
                                  <xsl:with-param name="label" select="'%fldAuxilliary%:'"/>
                                  <xsl:with-param name="name" select="'auxilliary'"/>
                                  <xsl:with-param name="selectedkey" select="/objectclasses/objectclass[@key = $key]/@auxilliary"/>
                                  <xsl:with-param name="onchange">
                                    <xsl:text>javascript:if (this.value == 'false') {document.all.parent.removeAttribute('disabled');} else {document.all.parent.setAttribute('disabled', 'disabled');}</xsl:text>
                                  </xsl:with-param>
                                </xsl:call-template>
                              </tr>
                              <tr>
                                <td>%fldParent%:</td>
                                <td>
                                  <select name="parent">
                                    <option value="">%sysDropDownChoose%</option>
                                    <xsl:for-each select="/objectclasses/objectclasses/objectclass[@key != $key]">
                                      <option value="{@key}">
                                        <xsl:if test="@key = /objectclasses/objectclass[@key = $key]/@parent">
                                          <xsl:attribute name="selected">selected</xsl:attribute>
                                        </xsl:if>
                                        <xsl:value-of select="name"/>
                                      </option>
                                    </xsl:for-each>
                                  </select>
                                </td>
                              </tr>
                              <tr>
                                <xsl:call-template name="textarea">
                                  <xsl:with-param name="label" select="'%fldDescription%:'"/>
                                  <xsl:with-param name="name" select="'description'"/>
                                  <xsl:with-param name="selectnode" select="/objectclasses/objectclass[@key = $key]/description"/>
                                  <xsl:with-param name="cols" select="'60'"/>
                                  <xsl:with-param name="rows" select="'7'"/>
                                </xsl:call-template>
                              </tr>
                            </table>
                          </fieldset>
                        </div>

                        <div class="tab-page" id="tab-page-2">
                          <span class="tab">%blockOCSchema%</span>

                          <script type="text/javascript" language="JavaScript">
                            tabPane1.addTabPage( document.getElementById( "tab-page-2" ) );
                          </script>

                          <fieldset>
                            <legend>&nbsp;%blockOCSchema%&nbsp;</legend>

                            <table width="100%" cellspacing="0" cellpadding="2" border="0">
                              <tr>
                                <td class="form_labelcolumn"></td>
                              </tr>
                              <tr>
                                <xsl:call-template name="textarea">
                                  <xsl:with-param name="label" select="'%fldOCSchema%:'"/>
                                  <xsl:with-param name="name" select="'schema'"/>
                                  <xsl:with-param name="selectnode" select="$schema"/>
                                  <xsl:with-param name="cols" select="'60'"/>
                                  <xsl:with-param name="rows" select="'15'"/>
                                  <xsl:with-param name="width" select="'100%'"/>
                                  <xsl:with-param name="required" select="'true'"/>
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

</xsl:stylesheet>
