<?xml version="1.0" encoding="utf-8"?>

<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
 ]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    <xsl:output method="html"/>

    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/genericheader.xsl"/>
    <xsl:include href="common/categoryheader.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/readonlyvalue.xsl"/>

    <xsl:param name="contenttypekey"/>
    <xsl:param name="modulename"/>

    <xsl:template match="/">
        <html>
            <head>
                <link href="css/admin.css" rel="stylesheet" type="text/css"/>
                <script type="text/javascript" language="JavaScript" src="javascript/admin.js"/>
                <script type="text/javascript" language="JavaScript" src="javascript/tabpane.js"></script>
                <script type="text/javascript" language="JavaScript" src="javascript/groups.js"></script>
                <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css" />

            </head>

            <body>

                <xsl:call-template name="cat_formheader"/>
                
                <table width="100%" border="0" cellspacing="0" cellpadding="2">
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
                                    <span class="tab">%blockSendReport%</span>
                                        
                                    <script type="text/javascript" language="JavaScript">
                                        tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                                    </script>

                                    <fieldset>
                                        <legend>&nbsp;%blockSender%&nbsp;</legend>
                                        <table>
                                            <tr>
                                                <td class="form_labelcolumn" valign="baseline" nowrap="nowrap">%fldName%</td>
                                                <td><xsl:value-of select="/contents/content/contentdata/sendhistory/sent[last()]/sender/@name"/></td>
                                            </tr>
                                            <tr>
                                                <td class="form_labelcolumn" valign="baseline" nowrap="nowrap">%fldEmail%</td>
                                                <td><xsl:value-of select="/contents/content/contentdata/sendhistory/sent[last()]/sender/@email"/></td>
                                            </tr>
                                        </table>
                                    </fieldset>

                                    <fieldset>
                                        <legend>&nbsp;%blockReport%&nbsp;</legend>
                                        <table border="0" cellspacing="2" cellpadding="0" width="100%">
                                            <!--tr>
                                                <td colspan="2">
                                                    <xsl:text>%txtNewsletterReportPre% </xsl:text><xsl:value-of select="count(/contents/content/contentdata/sendhistory/sent[last()]/recipients/recipient)"/><xsl:text> %txtNewsletterReportPost%</xsl:text>
                                                </td>
                                            </tr-->
                                            <tr>
                                                <td class="form_labelcolumn" valign="baseline" nowrap="nowrap">%fldEmailStatusOKCount%</td>
                                                <td><xsl:value-of select="count(/contents/content/contentdata/sendhistory/sent[not(@error = 'true')])"/></td>
                                            </tr>
                                            <tr>
                                                <td class="form_labelcolumn" valign="baseline" nowrap="nowrap">%fldEmailStatusERRORCount%</td>
                                                <td><xsl:value-of select="count(/contents/content/contentdata/sendhistory/sent[@error = 'true'])"/></td>
                                            </tr>
                                            <tr>
                                                <td class="form_labelcolumn" valign="baseline"/>
                                                <td>
                                                    <xsl:call-template name="button">
                                                        <xsl:with-param name="type" select="'link'"/>
                                                        <xsl:with-param name="name" select="'previewnewsletter'"/>
                                                        <xsl:with-param name="caption" select="'%cmdViewReport%'"/>
                                                        <xsl:with-param name="href">
                                                            <xsl:text>adminpage?page=</xsl:text>
                                                            <xsl:value-of select="$page"/>
                                                            <xsl:text>&amp;op=viewrecipients&amp;index=</xsl:text>
                                                            <xsl:value-of select="count(/contents/content/contentdata/sendhistory/sent)"/>
                                                        </xsl:with-param>
                                                        <xsl:with-param name="target" select="'_blank'"/>
                                                    </xsl:call-template>
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
                        <td>
                            <xsl:call-template name="button">
                                <xsl:with-param name="type" select="'link'"/>
                                <xsl:with-param name="name" select="'ok'"/>
                                <xsl:with-param name="caption" select="'%cmdOK%'"/>
                                <xsl:with-param name="href">
                                    <xsl:text>adminpage?page=</xsl:text>
                                    <xsl:value-of select="$page"/>
                                    <xsl:text>&amp;op=browse</xsl:text>
                                    <xsl:text>&amp;cat=</xsl:text>
                                    <xsl:value-of select="$cat"/>
                                    <xsl:text>&amp;selectedunitkey=</xsl:text>
                                    <xsl:value-of select="$selectedunitkey"/>
                                </xsl:with-param>
                            </xsl:call-template>
                        </td>
                    </tr>
                </table>
            </body>
        </html>
    </xsl:template>

    <xsl:template name="cat_formheader">
        <xsl:param name="name" select="$modulename"/>
        <xsl:param name="withsitename" select="false()"/>

        <h1>
            <xsl:call-template name="genericheader">
                <xsl:with-param name="endslash" select="false()"/>
            </xsl:call-template>
            <xsl:call-template name="categoryheader"/>
        </h1>
        <h2>%headSendReport%: <span id="titlename"><xsl:value-of select="/contents/content/title"/></span></h2>
    </xsl:template>

</xsl:stylesheet>