<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html"/>

    <xsl:include href="common/readonlyvalue.xsl"/>
    <xsl:include href="common/readonlydatetime.xsl"/>
    <xsl:include href="common/readonlymonth.xsl"/>
    <xsl:include href="common/readonlyurl.xsl"/>
    <xsl:include href="common/displaysystempath.xsl"/>
    <xsl:include href="common/displaypath.xsl"/>
    <xsl:include href="common/logentryutils.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/formatdate.xsl"/>
    <xsl:include href="common/categoryheader.xsl"/>

    <xsl:param name="tablekey"/>
    <xsl:param name="tablekeyvalue"/>
    <xsl:param name="parentkey"/>
    <xsl:param name="userkey"/>
    <xsl:param name="popup" select="'false'"/>

    <xsl:variable name="pageURL">
        <xsl:text>adminpage?page=</xsl:text>
        <xsl:value-of select="$page"/>
        <xsl:text>&amp;op=browse</xsl:text>
    </xsl:variable>

    <xsl:variable name="viewURL">
        <xsl:text>adminpage?page=</xsl:text>
        <xsl:value-of select="$page"/>
        <xsl:text>&amp;op=view</xsl:text>
        <xsl:text>&amp;key=</xsl:text><xsl:value-of select="/logentries/logentry/@key"/>
    </xsl:variable>

    <xsl:template match="/">
        <html>
            <head>
                <script type="text/javascript" src="javascript/admin.js"/>
                <script type="text/javascript" src="javascript/tabpane.js"/>
                <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css"/>
                <link type="text/css" rel="stylesheet" href="css/admin.css"/>
            </head>
            <body>
                <xsl:call-template name="header"/>

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
                                    <span class="tab">%headLogEntry%</span>

                                    <script type="text/javascript" language="JavaScript">
                                        tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                                    </script>

                                    <fieldset>
                                        <legend>&nbsp;%blockGeneral%&nbsp;</legend>

                                        <table class="formtable">
                                            <tr>
                                                <xsl:variable name="menuname">
                                                    <xsl:choose>
                                                        <xsl:when test="/logentries/logentry/@menukey">
                                                            <xsl:value-of select="/logentries/logentry/@menuname"/>
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            <xsl:text>Adminweb</xsl:text>
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </xsl:variable>
                                                <xsl:call-template name="readonlyvalue">
                                                    <xsl:with-param name="name" select="'__site'"/>
                                                    <xsl:with-param name="label" select="'%fldSite%:'"/>
                                                    <xsl:with-param name="selectnode" select="$menuname"/>
                                                    <xsl:with-param name="colspan" select="'1'"/>
                                                </xsl:call-template>
                                            </tr>
                                            <tr>
                                                <xsl:variable name="actionstring">
                                                    <xsl:call-template name="actionstring">
                                                        <xsl:with-param name="typekey" select="/logentries/logentry/@typekey"/>
                                                        <xsl:with-param name="tablekey" select="/logentries/logentry/@tablekey"/>
                                                        <xsl:with-param name="count" select="/logentries/logentry/@count"/>
                                                    </xsl:call-template>
                                                </xsl:variable>
                                                <xsl:call-template name="readonlyvalue">
                                                    <xsl:with-param name="name" select="'__action'"/>
                                                    <xsl:with-param name="label" select="'%fldEvent%:'"/>
                                                    <xsl:with-param name="selectnode" select="$actionstring"/>
                                                    <xsl:with-param name="colspan" select="'1'"/>
                                                </xsl:call-template>
                                            </tr>
                                            <tr>
                                                <xsl:choose>
                                                    <xsl:when test="$popup != 'true' and (/logentries/logentry/@typekey = 4 or /logentries/logentry/@typekey = 5 or /logentries/logentry/@typekey = 7) and (/logentries/content or /logentries/menuitem)">
                                                        <xsl:variable name="url">
                                                            <xsl:choose>
                                                                <xsl:when test="/logentries/logentry/@tablekey = 0">
                                                                    <xsl:text>adminpage?page=</xsl:text>
                                                                    <xsl:value-of select="/logentries/content/@contenttypekey + 999"/>
                                                                    <xsl:text>&amp;op=form&amp;key=</xsl:text>
                                                                    <xsl:value-of select="/logentries/content/@key"/>
                                                                    <xsl:text>&amp;cat=</xsl:text>
                                                                    <xsl:value-of select="/logentries/content/categoryname/@key"/>
                                                                    <xsl:text>&amp;selectedunitkey=</xsl:text>
                                                                    <xsl:value-of select="/logentries/content/@unitkey"/>
                                                                    <xsl:text>&amp;useredirect=referer</xsl:text>
                                                                </xsl:when>
                                                                <xsl:when test="/logentries/logentry/@tablekey = 1">
                                                                    <xsl:text>adminpage?page=850&amp;op=edit&amp;key=</xsl:text>
                                                                    <xsl:value-of select="/logentries/menuitem/@key"/>
                                                                    <xsl:text>&amp;type=</xsl:text>
                                                                    <xsl:value-of select="/logentries/menuitem/@type"/>
                                                                    <xsl:text>&amp;menukey=</xsl:text>
                                                                    <xsl:value-of select="/logentries/menuitem/@menukey"/>
                                                                    <xsl:text>&amp;inserbelow=-1&amp;useredirect=referer</xsl:text>
                                                                </xsl:when>
                                                            </xsl:choose>
                                                        </xsl:variable>

                                                        <xsl:call-template name="readonlyurl">
                                                            <xsl:with-param name="name" select="'__title'"/>
                                                            <xsl:with-param name="label" select="'%fldTitle%:'"/>
                                                            <xsl:with-param name="selectnode" select="/logentries/logentry/title"/>
                                                            <xsl:with-param name="colspan" select="'1'"/>
                                                            <xsl:with-param name="href" select="$url"/>
                                                        </xsl:call-template>
                                                        
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:call-template name="readonlyvalue">
                                                            <xsl:with-param name="name" select="'__title'"/>
                                                            <xsl:with-param name="label" select="'%fldTitle%:'"/>
                                                            <xsl:with-param name="selectnode" select="/logentries/logentry/title"/>
                                                            <xsl:with-param name="colspan" select="'1'"/>
                                                        </xsl:call-template>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </tr>
                                            <tr>
                                                <xsl:variable name="userstring">
                                                    <xsl:value-of select="/logentries/logentry/@username"/>
                                                    <xsl:text> (</xsl:text>
                                                    <xsl:value-of select="/logentries/logentry/@uid"/>
                                                    <xsl:text>)</xsl:text>
                                                </xsl:variable>
                                                <xsl:call-template name="readonlyvalue">
                                                    <xsl:with-param name="name" select="'__user'"/>
                                                    <xsl:with-param name="label" select="'%fldUser%:'"/>
                                                    <xsl:with-param name="selectnode" select="$userstring"/>
                                                    <xsl:with-param name="colspan" select="'1'"/>
                                                </xsl:call-template>
                                            </tr>
                                            <xsl:choose>
                                              <xsl:when test="/logentries/logentry/@count &gt; 1">
                                                <tr>
                                                  <xsl:call-template name="readonlydatetime">
                                                    <xsl:with-param name="name" select="'__timestamp'"/>
                                                    <xsl:with-param name="label" select="'%fldLastTimestamp%:'"/>
                                                    <xsl:with-param name="selectnode" select="/logentries/logentry/@timestamp"/>
                                                    <xsl:with-param name="colspan" select="'1'"/>
                                                  </xsl:call-template>
                                                </tr>
                                                <tr>
                                                  <xsl:call-template name="readonlymonth">
                                                    <xsl:with-param name="name" select="'__month'"/>
                                                    <xsl:with-param name="label" select="'%fldMonth%:'"/>
                                                    <xsl:with-param name="selectnode" select="/logentries/logentry/@timestamp"/>
                                                    <xsl:with-param name="colspan" select="'1'"/>
                                                  </xsl:call-template>
                                                </tr>
                                              </xsl:when>
                                              <xsl:otherwise>
                                                <tr>
                                                  <xsl:call-template name="readonlydatetime">
                                                    <xsl:with-param name="name" select="'__timestamp'"/>
                                                    <xsl:with-param name="label" select="'%fldTimestamp%:'"/>
                                                    <xsl:with-param name="selectnode" select="/logentries/logentry/@timestamp"/>
                                                    <xsl:with-param name="colspan" select="'1'"/>
                                                  </xsl:call-template>
                                                </tr>
                                              </xsl:otherwise>
                                            </xsl:choose>
                                        </table>
                                    </fieldset>

                                    <fieldset>
                                        <legend>&nbsp;%blockDetails%&nbsp;</legend>

                                        <table class="formtable">
                                            <xsl:if test="/logentries/logentry/data/requesturl">
                                                <tr>
                                                    <xsl:call-template name="readonlyurl">
                                                        <xsl:with-param name="name" select="'__requesturl'"/>
                                                        <xsl:with-param name="label" select="'%fldRequestURL%:'"/>
                                                        <xsl:with-param name="selectnode" select="/logentries/logentry/data/requesturl"/>
                                                        <xsl:with-param name="colspan" select="'1'"/>
                                                        <xsl:with-param name="href" select="/logentries/logentry/data/requesturl"/>
                                                        <xsl:with-param name="target" select="'_blank'"/>
                                                    </xsl:call-template>
                                                </tr>
                                            </xsl:if>

                                            <xsl:if test="/logentries/logentry/data/referrer">
                                                <tr>
                                                    <xsl:call-template name="readonlyurl">
                                                        <xsl:with-param name="name" select="'__referrer'"/>
                                                        <xsl:with-param name="label" select="'%fldReferrer%:'"/>
                                                        <xsl:with-param name="selectnode" select="/logentries/logentry/data/referrer"/>
                                                        <xsl:with-param name="colspan" select="'1'"/>
                                                        <xsl:with-param name="href" select="/logentries/logentry/data/referrer"/>
                                                        <xsl:with-param name="target" select="'_blank'"/>
                                                    </xsl:call-template>
                                                </tr>
                                            </xsl:if>

                                            <xsl:if test="/logentries/logentry/@path">
                                                <tr>
                                                    <xsl:call-template name="readonlyvalue">
                                                        <xsl:with-param name="name" select="'__path'"/>
                                                        <xsl:with-param name="label" select="'%fldPath%:'"/>
                                                        <xsl:with-param name="selectnode" select="/logentries/logentry/@path"/>
                                                        <xsl:with-param name="colspan" select="'1'"/>
                                                    </xsl:call-template>
                                                </tr>
                                            </xsl:if>

                                            <xsl:if test="/logentries/logentry/@sitename">
                                                <tr>
                                                    <xsl:call-template name="readonlyvalue">
                                                        <xsl:with-param name="name" select="'__sitename'"/>
                                                        <xsl:with-param name="label" select="'%fldDomain%:'"/>
                                                        <xsl:with-param name="selectnode" select="/logentries/logentry/@sitename"/>
                                                        <xsl:with-param name="colspan" select="'1'"/>
                                                    </xsl:call-template>
                                                </tr>
                                            </xsl:if>

                                            <xsl:if test="/logentries/logentry/@host">
                                              <tr>
                                                <xsl:variable name="host">
                                                  <xsl:choose>
                                                    <xsl:when test="/logentries/logentry/@host">
                                                      <xsl:value-of select="/logentries/logentry/@host"/>
                                                      <xsl:text> (</xsl:text>
                                                      <xsl:value-of select="/logentries/logentry/@inetaddress"/>
                                                      <xsl:text>)</xsl:text>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                      <xsl:value-of select="/logentries/logentry/@inetaddress"/>
                                                    </xsl:otherwise>
                                                  </xsl:choose>
                                                </xsl:variable>

                                                <xsl:choose>
                                                  <xsl:when test="/logentries/logentry/@host">
                                                    <xsl:call-template name="readonlyvalue">
                                                      <xsl:with-param name="name" select="'__host'"/>
                                                      <xsl:with-param name="label" select="'%fldClient%:'"/>
                                                      <xsl:with-param name="selectnode" select="$host"/>
                                                      <xsl:with-param name="colspan" select="'1'"/>
                                                    </xsl:call-template>
                                                  </xsl:when>
                                                  <xsl:otherwise>
                                                    <xsl:call-template name="readonlyvalue">
                                                      <xsl:with-param name="name" select="'__host'"/>
                                                      <xsl:with-param name="label" select="'%fldClient%:'"/>
                                                      <xsl:with-param name="selectnode" select="$host"/>
                                                      <xsl:with-param name="colspan" select="'1'"/>
                                                    </xsl:call-template>
                                                  </xsl:otherwise>
                                                </xsl:choose>
                                              </tr>
                                            </xsl:if>

                                            <xsl:if test="/logentries/logentry/data/@deflated = 'true'">
                                              <tr>
                                                <td>
                                                  <br/>
                                                  <xsl:call-template name="button">
                                                    <xsl:with-param name="type" select="'link'"/>
                                                    <xsl:with-param name="target" select="'_blank'"/>
                                                    <xsl:with-param name="caption" select="'%cmdShowDetails%'"/>
                                                    <xsl:with-param name="href">
                                                      <xsl:text>adminpage?page=350&amp;op=browse&amp;key=</xsl:text>
                                                      <xsl:value-of select="/logentries/logentry/@key"/>
                                                    </xsl:with-param>
                                                  </xsl:call-template>
                                                </td>
                                              </tr>
                                            </xsl:if>
                                        </table>
                                    </fieldset>

                                    <!--fieldset>
                                         <legend>&nbsp;%blockData%&nbsp;</legend>

                                         <xsl:call-template name="data"/>
                                     </fieldset-->
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
                                 <xsl:with-param name="caption" select="'%cmdOK%'"/>
                                 <xsl:with-param name="name" select="'okbtn'"/>
                                 <xsl:with-param name="onclick" select="'history.back()'"/>
                             </xsl:call-template>
                         </td>
                     </tr>
                 </table>
             </body>
         </html>
     </xsl:template>

     <xsl:template name="header">
         <h1>
             <xsl:choose>
                 <xsl:when test="$tablekey = 0">
                     <xsl:call-template name="displaypath">
                         <xsl:with-param name="domainkey" select="$selecteddomainkey"/>
                         <xsl:with-param name="nolinks" select="true()"/>
                     </xsl:call-template>
                     <xsl:if test="not(/logentries/categorynames/categoryname/@contenttypekey = 30 or /logentries/categorynames/categoryname/@contenttypekey = 46)">
                         <xsl:text> / %headContentRepositories%</xsl:text>
                     </xsl:if>
                     <xsl:call-template name="categoryheader">
                         <xsl:with-param name="rootelem" select="/logentries"/>
                         <xsl:with-param name="nolinks" select="true()"/>
                     </xsl:call-template>
                 </xsl:when>
                 <xsl:when test="$tablekey = 1">
                     <xsl:call-template name="displaypath">
                         <xsl:with-param name="domainkey" select="$selecteddomainkey"/>
                         <xsl:with-param name="presentationlayerkey" select="$menukey"/>
                         <xsl:with-param name="presentationlayername" select="$menuname"/>
                         <xsl:with-param name="nolinks" select="true()"/>
                     </xsl:call-template>
                     <xsl:text> / %headPageBuilder%</xsl:text>
                     <xsl:if test="number($parentkey) &gt;= 0">
                         <xsl:call-template name="generateheader">
                             <xsl:with-param name="menuitem" select="//menuitem[@key = $parentkey]"/>
                         </xsl:call-template>
                     </xsl:if>
                 </xsl:when>
                 <xsl:when test="$userkey &gt;= 0">
                     <xsl:call-template name="displaysystempath">
                         <xsl:with-param name="page" select="$page"/>
                         <xsl:with-param name="domainkey" select="$selecteddomainkey"/>
                         <xsl:with-param name="domainname" select="$domainname"/>
                         <xsl:with-param name="nolinks" select="true()"/>
                     </xsl:call-template>
                 </xsl:when>
                 <xsl:otherwise>
                     <xsl:call-template name="displaysystempath">
                         <xsl:with-param name="page" select="$page"/>
                     </xsl:call-template>
                 </xsl:otherwise>
             </xsl:choose>
         </h1>
         <h2>
             <xsl:text>%headTitle%: </xsl:text>
             <xsl:value-of select="/logentries/logentry/title"/>
         </h2>
     </xsl:template>

     <xsl:template name="data">
         <!--table class="formtable">
              <tr>
                  <td>foo</td>
              </tr>
         </table-->
    </xsl:template>

    <xsl:template name="generateheader">
        <xsl:param name="menuitem"/>

        <xsl:if test="boolean($menuitem/parent::node())">
            <xsl:call-template name="generateheader">
                <xsl:with-param name="menuitem" select="$menuitem/parent::node()"/>
            </xsl:call-template>
        </xsl:if>

        <xsl:if test="$menuitem/self::menuitem">
            <xsl:text> / </xsl:text>
            <xsl:value-of select="$menuitem/name"/>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
