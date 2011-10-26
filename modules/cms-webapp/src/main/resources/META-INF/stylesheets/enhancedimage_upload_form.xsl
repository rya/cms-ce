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
    <xsl:include href="common/waitsplash.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>
    <xsl:include href="common/filefield.xsl"/>

    <xsl:template match="/">
        <html>
            <head>
                <xsl:call-template name="waitsplash"/>
                
                <link type="text/css" rel="stylesheet" href="css/admin.css"/>
                <link type="text/css" rel="stylesheet" href="css/menu.css"/>
                <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>
                
                <script type="text/javascript" language="JavaScript" src="javascript/admin.js"/>
                <script type="text/javascript" language="JavaScript" src="javascript/validate.js"/>
                <script type="text/javascript" language="JavaScript" src="javascript/tabpane.js"/>

                <script type="text/javascript" language="JavaScript">
                    var validatedFields = new Array(2);
                    validatedFields[0] = new Array("%blockZipFile%", "zipfile", validateRequired);
                    
                    function validateAll(formName)
                    {
                        var f = document.forms[formName];
                    
                        if ( !checkAll(formName, validatedFields) )
                            return;
                    
                        waitsplash();
                        f.submit();
                    }

                    function cancel()
                    {
                        document.location = "<xsl:value-of select="$referer"/>";
                    }
                </script>
            </head>

            <body>
              <h1>
                <xsl:call-template name="genericheader">
                  <xsl:with-param name="endslash" select="false()"/>
                </xsl:call-template>
                
                <xsl:call-template name="categoryheader"/>
              </h1>

              <form name="formAdmin" method="post">
                  <xsl:attribute name="enctype">multipart/form-data</xsl:attribute>
                  <xsl:attribute name="action">
                      <xsl:text>adminpage?page=</xsl:text>
                      <xsl:value-of select="$page"/>
                      <xsl:text>&amp;op=zip&amp;subop=list</xsl:text>
                      <xsl:text>&amp;cat=</xsl:text>
                      <xsl:value-of select="$cat"/>
                  </xsl:attribute>

                  <input type="hidden" name="referer" value="{$referer}"/>

                  <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td class="form_title_form_seperator"><img src="images/1x1.gif"/></td>
                    </tr>
                    <tr>
                      <td>
                        <div class="tab-pane" id="tab-pane-1">
                          <script type="text/javascript" language="JavaScript">
                            var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                          </script>
                          <div class="tab-page" id="tab-page-1">
                            <span class="tab">%blockUploadZip%</span>
                            
                            <script type="text/javascript" language="JavaScript">
                              tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                            </script>
                            
                            <fieldset>
                              <legend>&nbsp;%blockZipFile%&nbsp;</legend>
                              <table border="0" cellspacing="0" cellpadding="0" class="formtable">
                                <tr>
                                  <xsl:call-template name="filefield">
                                    <xsl:with-param name="label" select="'%fldChooseFile%:'"/>
                                    <xsl:with-param name="name" select="'zipfile'"/>
                                    <xsl:with-param name="size" select="'60'"/>
                                    <xsl:with-param name="maxlength" select="'256'"/>
                                    <xsl:with-param name="imagekey" select="'0'"/>
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
                      <td>
                        <br/>
                      </td>
                    </tr>

                    <tr>
                      <td>
                        <xsl:call-template name="button">
                          <xsl:with-param name="type" select="'button'"/>
                          <xsl:with-param name="caption" select="'%cmdUpload%'"/>
                          <xsl:with-param name="name" select="'uploadbtn'"/>
                          <xsl:with-param name="onclick">
                            <xsl:text>javascript:validateAll('formAdmin');</xsl:text>
                          </xsl:with-param>
                        </xsl:call-template>
                        <xsl:text>&nbsp;</xsl:text>
                        <xsl:call-template name="button">
                          <xsl:with-param name="type" select="'button'"/>
                          <xsl:with-param name="caption" select="'%cmdCancel%'"/>
                          <xsl:with-param name="name" select="'cancelbtn'"/>
                          <xsl:with-param name="onclick">
                            <xsl:text>javascript:cancel('');</xsl:text>
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