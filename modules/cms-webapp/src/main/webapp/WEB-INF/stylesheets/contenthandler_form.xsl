<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:include href="common/readonlyvalue.xsl"/>
    <xsl:include href="common/textfield.xsl"/>
    <xsl:include href="common/textarea.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/displaysystempath.xsl"/>
    <xsl:include href="common/serialize.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>
    <xsl:include href="common/displayerror.xsl"/>

    <xsl:param name="create"/>
    <xsl:param name="page"/>

    <xsl:template match="/">

        <html>
            <head>
                <link rel="StyleSheet" type="text/css" href="javascript/tab.webfx.css" />
                <link rel="stylesheet" type="text/css" href="css/admin.css"/>

                <script type="text/javascript" language="JavaScript" src="javascript/tabpane.js"/>
                <script type="text/javascript" language="JavaScript" src="javascript/admin.js"/>
                <script type="text/javascript" language="JavaScript" src="javascript/validate.js"/>
                <script type="text/javascript" language="JavaScript">

                    var validateFields = new Array(3);

                    validateFields[0] = new Array("%fldName%", "name", validateRequired);
                    validateFields[1] = new Array("%fldClass%", "class", validateRequired);


                    function validateAll(formName) {

                        var f = document.forms[formName];

                        if ( !checkAll(formName, validateFields) )
                            return;

                        f.submit();
                    }

                </script>
            </head>

            <body>

              <form name="formAdmin" method="post">
                <xsl:attribute name="action">
                  <xsl:if test="$create=1">
                    <xsl:text>adminpage?page=</xsl:text>
                    <xsl:value-of select="$page"/>
                    <xsl:text>&amp;op=create</xsl:text>
                  </xsl:if>
                  <xsl:if test="$create=0">
                    <xsl:text>adminpage?page=</xsl:text>
                    <xsl:value-of select="$page"/>
                    <xsl:text>&amp;op=update</xsl:text>
                  </xsl:if>
                </xsl:attribute>

                <xsl:choose>
                  <xsl:when test="$create=1">
                    <h1>
                      <xsl:call-template name="displaysystempath">
                        <xsl:with-param name="page" select="$page"/>
                      </xsl:call-template>
                      <xsl:text>&nbsp;</xsl:text>
                      <span id='titlename'> </span>
                    </h1>
                  </xsl:when>
                  <xsl:otherwise>
                    <h1>
                      <xsl:call-template name="displaysystempath">
                        <xsl:with-param name="page" select="$page"/>
                      </xsl:call-template>
                      <xsl:text>&nbsp;</xsl:text>
                      <span id='titlename'><xsl:value-of select="concat('/ ', /contenthandlers/contenthandler/name)"/></span>
                    </h1>
                  </xsl:otherwise>
                </xsl:choose>

                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td class="form_title_form_seperator"><img src="images/1x1.gif"/></td>
                  </tr>
                  <tr>
                    <td>
                      <xsl:call-template name="contenthandlerform"/>
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

  <xsl:template name="contenthandlerform">
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
          <input type="hidden" name="key" value="{/contenthandlers/contenthandler/@key}"/>
          <table width="100%" cellspacing="0" cellpadding="2" border="0">
            <tr>
              <td class="form_labelcolumn"></td>
            </tr>
            <tr>
              <xsl:call-template name="textfield">
                <xsl:with-param name="name" select="'name'"/>
                <xsl:with-param name="label" select="'%fldName%:'"/>
                <xsl:with-param name="selectnode" select="/contenthandlers/contenthandler/name"/>
                <xsl:with-param name="size" select="'60'"/>
                <xsl:with-param name="maxlength" select="'100'"/>
                <xsl:with-param name="colspan" select="'1'"/>
                <xsl:with-param name="onkeyup">javascript: updateBreadCrumbHeader('titlename', this);</xsl:with-param>
                <xsl:with-param name="required" select="'true'"/>
              </xsl:call-template>
            </tr>
            <tr>
              <xsl:call-template name="textfield">
                <xsl:with-param name="name" select="'class'"/>
                <xsl:with-param name="label" select="'%fldClass%:'"/>
                <xsl:with-param name="selectnode" select="/contenthandlers/contenthandler/class"/>
                <xsl:with-param name="size" select="'60'"/>
                <xsl:with-param name="maxlength" select="'100'"/>
                <xsl:with-param name="colspan" select="'1'"/>
                <xsl:with-param name="required" select="'true'"/>
              </xsl:call-template>
            </tr>
            <tr>
              <xsl:call-template name="textarea">
                <xsl:with-param name="name" select="'description'"/>
                <xsl:with-param name="label" select="'%fldDescription%:'"/>
                <xsl:with-param name="selectnode" select="/contenthandlers/contenthandler/description"/>
                <xsl:with-param name="rows" select="'5'"/>
                <xsl:with-param name="cols" select="'60'"/>
                <xsl:with-param name="colspan" select="'1'"/>
              </xsl:call-template>
            </tr>
            <tr>
              <xsl:variable name="confignode">
                <xsl:choose>
                  <xsl:when test="$create = '1'">
                    <xsl:text>&lt;config/&gt;</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:call-template name="serialize">
                      <xsl:with-param name="xpath" select="/contenthandlers/contenthandler/xmlconfig/config"/>
                      <xsl:with-param name="include-self" select="true()"/>
                    </xsl:call-template>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:variable>
              <xsl:call-template name="textarea">
                <xsl:with-param name="name" select="'config'"/>
                <xsl:with-param name="label" select="'%fldConfig%:'"/>
                <xsl:with-param name="selectnode" select="$confignode"/>
                <xsl:with-param name="rows" select="'12'"/>
                <xsl:with-param name="cols" select="'80'"/>
                <xsl:with-param name="colspan" select="'1'"/>
                <xsl:with-param name="width" select="'100%'"/>
              </xsl:call-template>
            </tr>
            <tr>
              <xsl:variable name="indexparametersnode">
                <xsl:choose>
                  <xsl:when test="$create = '1'">
                    <xsl:text>&lt;indexparameters/&gt;</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:call-template name="serialize">
                      <xsl:with-param name="xpath" select="/contenthandlers/contenthandler/xmlconfig/indexparameters"/>
                      <xsl:with-param name="include-self" select="true()"/>
                    </xsl:call-template>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:variable>
              <xsl:call-template name="textarea">
                <xsl:with-param name="name" select="'indexparameters'"/>
                <xsl:with-param name="label" select="'%fldDefaultIndexing%:'"/>
                <xsl:with-param name="selectnode" select="$indexparametersnode"/>
                <xsl:with-param name="rows" select="'12'"/>
                <xsl:with-param name="cols" select="'80'"/>
                <xsl:with-param name="colspan" select="'1'"/>
                <xsl:with-param name="width" select="'100%'"/>
              </xsl:call-template>
            </tr>
                        
            <tr>
              <xsl:variable name="ctydefaultnode">
                <xsl:choose>
                  <xsl:when test="$create = '1'">
                    <xsl:text>&lt;ctydefault/&gt;</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:call-template name="serialize">
                      <xsl:with-param name="xpath" select="/contenthandlers/contenthandler/xmlconfig/ctydefault"/>
                      <xsl:with-param name="include-self" select="true()"/>
                    </xsl:call-template>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:variable>
              <xsl:call-template name="textarea">
                <xsl:with-param name="name" select="'ctydefault'"/>
                <xsl:with-param name="label" select="'%fldCtyDefault%:'"/>
                <xsl:with-param name="selectnode" select="$ctydefaultnode"/>
                <xsl:with-param name="rows" select="'12'"/>
                <xsl:with-param name="cols" select="'80'"/>
                <xsl:with-param name="colspan" select="'1'"/>
                <xsl:with-param name="width" select="'100%'"/>
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

</xsl:stylesheet>
