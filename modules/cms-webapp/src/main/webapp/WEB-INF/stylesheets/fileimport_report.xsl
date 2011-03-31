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

  <xsl:template match="/">
    <html>
      <head>
        <link rel="stylesheet" type="text/css" href="css/admin.css"/>
		<link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css" />
		<script type="text/javascript" language="JavaScript" src="javascript/admin.js"/>
		<script type="text/javascript" language="JavaScript" src="javascript/tabpane.js"/>
      </head>

      <body>
        <h1>
          <xsl:call-template name="genericheader">
              <xsl:with-param name="endslash" select="false()"/>
          </xsl:call-template>
          <xsl:call-template name="categoryheader"/>
        </h1>
		
		<table width="100%" border="0" cellspacing="0" cellpadding="2">
			<tr>
                              <td>
                                  <br/>
                              </td>
                          </tr>
                        <!-- form -->
                        <tr>
                            <td>
                                <div class="tab-pane" id="tab-pane-1">
                                    <script type="text/javascript" language="JavaScript">
                                        var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                                    </script>

                                    <div class="tab-page" id="tab-page-1">
                                        <span class="tab">%headFileImport%</span>

                                        <script type="text/javascript" language="JavaScript">
                                            tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                                        </script>

                                        <fieldset>
                                            <legend>&nbsp;%blockImportResults%&nbsp;</legend>
                                            <table border="0" cellspacing="2" cellpadding="0">
                                               <tr>
                  <td width="120">%msgContentInserts%:</td>
				  <td><xsl:value-of select="/data/importreport/inserted/@count"/></td>
                </tr>
                <tr>
                  <td>%msgContentUpdates%:</td>
				  <td><xsl:value-of select="/data/importreport/updated/@count"/></td>
                </tr>
                <tr>
                  <td>%msgContentSkips%:</td>
				  <td><xsl:value-of select="/data/importreport/skipped/@count"/></td>
                </tr>
                <tr>
                <td colspan="2">&nbsp;</td>
                </tr>
                <tr>
                  <td>%msgContentRemoved%:</td>
				  <td><xsl:value-of select="/data/importreport/deleted/@count"/></td>
                </tr>
                <tr>
                  <td>%msgContentArchived%:</td>
				  <td><xsl:value-of select="/data/importreport/archived/@count"/></td>
                </tr>
                <tr>
                  <td>%msgAlreadyArchived%:</td>
          <td><xsl:value-of select="/data/importreport/alreadyArchived/@count"/></td>
                </tr>
                <tr>
                  <td>%msgRemaining%:</td>
          <td><xsl:value-of select="/data/importreport/remaining/@count"/></td>
                </tr>
                <tr>
                <td colspan="2">&nbsp;</td>
                </tr>
                <tr>
                  <td>%msgElapsedTime%:</td>
          <td><xsl:value-of select="/data/importreport/@elapsedTimeInSeconds"/> %msgSeconds%</td>
                </tr>

											  </table>
											  </fieldset>
											 </div>
											 </div>
											 </td>
											 </tr>
											 <tr><td>
												<br/>
												<xsl:call-template name="button">
                  <xsl:with-param name="type" select="'link'"/>
                  <xsl:with-param name="caption" select="'%cmdBack%'"/>
				  <xsl:with-param name="href">
						<xsl:text>adminpage?op=browse</xsl:text>
						<xsl:text>&amp;page=</xsl:text><xsl:value-of select="$page"/>
						<xsl:text>&amp;selectedunitkey=</xsl:text><xsl:value-of select="$selectedunitkey"/>
						<xsl:text>&amp;cat=</xsl:text><xsl:value-of select="$cat"/>
					</xsl:with-param>
              </xsl:call-template></td></tr>
											 </table>

				
			
        
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>