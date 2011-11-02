<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html"/>

  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:include href="common/genericheader.xsl"/>
  <xsl:include href="common/categoryheader.xsl"/>
  <xsl:include href="common/dropdown_boolean.xsl"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="common/filefield.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/readonlyvalue.xsl"/>
  <xsl:include href="common/textfielddatetime.xsl"/>
  <xsl:include href="common/formatdate.xsl"/>
  <xsl:include href="common/checkbox_boolean.xsl"/>
  <!--xsl:include href="common/waitsplash.xsl"/-->
  
  
  <xsl:variable name="category" select="/data/contentcategories/contentcategory"/>

  <xsl:template match="/">
      <html>
          <head>
              <link rel="stylesheet" type="text/css" href="css/admin.css"/>
              <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>
              <script type="text/javascript" language="JavaScript" src="javascript/tabpane.js"/>
          </head>
          
          <body>
              <div id="form">
                  <form name="formAdmin" method="post" enctype="multipart/form-data">
                      <xsl:attribute name="action">
                          <xsl:text>adminpage?page=</xsl:text>
                          <xsl:value-of select="$page"/>
                          <xsl:text>&amp;op=upload</xsl:text>
                          <xsl:text>&amp;referer=</xsl:text>
                      </xsl:attribute>
                      
                      <table width="100%" border="0" cellspacing="0" cellpadding="0">
                          <tr>
                              <td>
                                  <br/>
                              </td>
                          </tr>
                          <tr>
                              <td>
                                  <div class="tab-pane" id="tab-pane-1">
                                      <script type="text/javascript" language="JavaScript">
                                          var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                                      </script>
                                      <div class="tab-page" id="tab-page-1">
                                          <span class="tab">%blockFileImport%</span>
                                          
                                          <script type="text/javascript" language="JavaScript">
                                              tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                                          </script>

                                          <xsl:call-template name="resourceimportform"/>

                                      </div>
                                  </div>
                                  
                                  <script type="text/javascript" language="JavaScript">
                                      setupAllTabs();
                                  </script>
                              </td>
                          </tr>
                          <tr>
                              <td>
                                  <br/>
                              </td>
                          </tr>

                          <xsl:call-template name="resourceimportbuttons"/>

                      </table>
                  </form>
              </div>
          </body>
      </html>
  </xsl:template>

  <xsl:template name="resourceimportform">
      <fieldset>
          <legend>&nbsp;%blockResourceImport%&nbsp;</legend>
          <table border="0" cellspacing="0" cellpadding="2">
              <tr><td class="form_labelcolumn"></td></tr>
              <tr>
                  <xsl:call-template name="filefield">
                      <xsl:with-param name="name" select="'zipfile'"/>
                      <xsl:with-param name="label" select="'%fldFile%:'"/>
                      <xsl:with-param name="size" select="'60'"/>
                  </xsl:call-template>
              </tr>
          </table>
      </fieldset>
  </xsl:template>

  <xsl:template name="resourceimportbuttons">
      <xsl:param name="formname" select="'formAdmin'"/>

      <tr>
          <td>
              <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'submit'"/>
                  <xsl:with-param name="caption" select="'%cmdFileImport%'"/>
                  <xsl:with-param name="name" select="'importbtn'"/>
              </xsl:call-template>
              &nbsp;
              <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'button'"/>
                  <xsl:with-param name="caption" select="'%cmdCancel%'"/>
                  <xsl:with-param name="name" select="'cancelbtn'"/>
                  <xsl:with-param name="onclick">
                      <xsl:text>javascript: history.back();</xsl:text>
                  </xsl:with-param>
              </xsl:call-template>
          </td>
      </tr>
  </xsl:template>
 
</xsl:stylesheet>
