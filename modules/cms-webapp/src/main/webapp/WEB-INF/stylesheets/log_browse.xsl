<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html"/>

    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/accesslevel_parameters.xsl"/>
    <xsl:include href="common/formatdate.xsl"/>
    <xsl:include href="common/tablecolumnheader.xsl"/>
    <xsl:include href="common/tablerowpainter.xsl"/>
    <xsl:include href="common/displaysystempath.xsl"/>
    <xsl:include href="common/displaypath.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/logentryutils.xsl"/>
    <xsl:include href="common/categoryheader.xsl"/>
    <xsl:include href="common/replacesubstring.xsl"/>

    <xsl:param name="from"/>
    <xsl:param name="tablekey"/>
    <xsl:param name="tablekeyvalue"/>
    <xsl:param name="parentkey"/>
    <xsl:param name="userkey"/>
    <xsl:param name="popup" select="'false'"/>
    <xsl:param name="filter" select="''"/>
    <xsl:param name="key"/>

    <xsl:variable name="plusscount">20</xsl:variable>

    <xsl:param name="sortby"/>
    <xsl:param name="sortby-direction"/>

    <xsl:variable name="pageURL">
         <xsl:text>adminpage?page=</xsl:text>
         <xsl:value-of select="$page"/>
         <xsl:text>&amp;op=browse</xsl:text>
    </xsl:variable>

    <xsl:template match="/">
        <html>
            <head>
              <xsl:if test="/logentries/aggregation">
                <meta http-equiv="Refresh">
                  <xsl:attribute name="content">
                    <xsl:text>5;URL=</xsl:text>
                    <xsl:value-of select="$pageURL"/>
                    <xsl:if test="$from != ''">
                      <xsl:text>&amp;from=</xsl:text>
                      <xsl:value-of select="$from"/>
                    </xsl:if>
                    <xsl:if test="$filter != ''">
                      <xsl:text>&amp;filter=</xsl:text>
                      <xsl:call-template name="replacesubstring">
                        <xsl:with-param name="stringsource" select="$filter"/>
                        <xsl:with-param name="substringsource" select="';'"/>
                        <xsl:with-param name="substringdest" select="'%3B'"/>
                      </xsl:call-template>
                    </xsl:if>
                  </xsl:attribute>
                </meta>
              </xsl:if>
                <script type="text/javascript" src="javascript/admin.js">//</script>

                <script type="text/javascript" src="javascript/window.js"/>
                <script type="text/javascript">
                 cms.window.attatchKeyEvent('close');
                </script>


                <script type="text/javascript" language="JavaScript">
                    function OpenDetailsWindow( page, key, width, height )
                    {
                    newWindow = window.open("adminpage?page=" + page + "&amp;key=" + key + "&amp;op=show_details",
                    "Preview", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" + height);
                    newWindow.focus();
                    }
                    function MM_swapImgRestore() { //v3.0
                    var i,x,a=document.MM_sr; for(i=0;a&amp;&amp;i&lt;a.length&amp;&amp;(x=a[i])&amp;&amp;x.oSrc;i++) x.src=x.oSrc;
                    }
                </script>

                <link href="css/admin.css" rel="stylesheet" type="text/css"/>
            </head>

            <body><!--
              <strong>from: <xsl:value-of select="$from"/></strong><br/>
              <strong>tablekey: <xsl:value-of select="$tablekey"/></strong><br/>
              <strong>tablekeyvalue: <xsl:value-of select="$tablekeyvalue"/></b><br/>
              <strong>parentkey: <xsl:value-of select="$parentkey"/></strong><br/>
              <strong>userkey: <xsl:value-of select="$userkey"/></strong><br/>
              <strong>popup: <xsl:value-of select="$popup"/></strong><br/>
              <strong>filter: <xsl:value-of select="$filter"/></strong><br/>
              <strong>key: <xsl:value-of select="$key"/></strong><br/>
              <strong>Domain key: <xsl:value-of select="$selecteddomainkey"/></strong><br/-->

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
                        <xsl:when test="$userkey != ''">
                            <xsl:call-template name="displaysystempath">
                                <xsl:with-param name="page" select="$page"/>
                                <xsl:with-param name="domainkey" select="$selecteddomainkey"/>
                                <xsl:with-param name="domainname" select="$domainname"/>
                                <xsl:with-param name="nolinks" select="true()"/>
                            </xsl:call-template>
                        </xsl:when>
                        <xsl:when test="$key = ''">
                            <xsl:call-template name="displaysystempath">
                                <xsl:with-param name="page" select="$page"/>
                            </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:call-template name="displaysystempath">
                                <xsl:with-param name="page" select="$page"/>
                                <xsl:with-param name="nolinks" select="true()"/>
                            </xsl:call-template>
                        </xsl:otherwise>
                    </xsl:choose>
                </h1>

                <xsl:choose>
                    <xsl:when test="$tablekey != '' and $key = ''">
                        <h2>
                            <xsl:text>%headEventLog%: </xsl:text>
                            <xsl:value-of select="/logentries/@title"/>
                            <xsl:if test="$tablekey = 0 or $tablekey = 1">
                            	<xsl:text> (</xsl:text>
                            	<xsl:value-of select="/logentries/@totalread"/>
                            	<xsl:text> %txtOpened%)</xsl:text>
                            </xsl:if>
                        </h2>
                    </xsl:when>
                    <xsl:when test="$tablekey != ''">
                        <h2>
                            <xsl:text>%headEventLog%: </xsl:text>
                            <xsl:value-of select="/logentries/logentry/title"/>
                        </h2>
                    </xsl:when>
                    <xsl:when test="$userkey != ''">
                        <h2>
                            <xsl:text>%headEventLog%: </xsl:text>
                            <xsl:value-of select="/logentries/logentry/@username"/>
                        </h2>
                    </xsl:when>
                    <xsl:when test="$enterpriseadmin = 'true' and $key = ''">
                        <table border="0" cellspacing="0" cellpadding="0">
                          <tr>
                            <td>
                              <xsl:call-template name="button">
                                <xsl:with-param name="type" select="'link'"/>
                                <xsl:with-param name="caption" select="'%cmdFilter%'"/>
                                <xsl:with-param name="href">
                                  <xsl:text>adminpage?page=</xsl:text>
                                  <xsl:value-of select="$page"/>
                                  <xsl:text>&amp;op=filter</xsl:text>
                                  <xsl:if test="$from != ''">
                                    <xsl:text>&amp;from=</xsl:text>
                                    <xsl:value-of select="$from"/>
                                  </xsl:if>
                                  <xsl:if test="$filter != ''">
                                    <xsl:text>&amp;filter=</xsl:text>
                                    <xsl:value-of select="$filter"/>
                                  </xsl:if>
                                </xsl:with-param>
                              </xsl:call-template>
                            </td>
                          </tr>
                        </table>
                    </xsl:when>
                    <xsl:otherwise>
                        <table border="0" cellspacing="0" cellpadding="0">
                          <tr>
                            <td>
                              <xsl:call-template name="button">
                                <xsl:with-param name="type" select="'link'"/>
                                <xsl:with-param name="caption" select="'%cmdFilter%'"/>
                                <xsl:with-param name="href">
                                  <xsl:text>adminpage?page=</xsl:text>
                                  <xsl:value-of select="$page"/>
                                  <xsl:text>&amp;op=filter</xsl:text>
                                  <xsl:if test="$from != ''">
                                    <xsl:text>&amp;from=</xsl:text>
                                    <xsl:value-of select="$from"/>
                                  </xsl:if>
                                  <xsl:if test="$filter != ''">
                                    <xsl:text>&amp;filter=</xsl:text>
                                    <xsl:value-of select="$filter"/>
                                  </xsl:if>
                                </xsl:with-param>
                              </xsl:call-template>
                            </td>
                          </tr>
                        </table>
                    </xsl:otherwise>
                </xsl:choose>

                <xsl:if test="not(/logentries/aggregation)">
                  <xsl:call-template name="logentries"/>
                </xsl:if>

            </body>

        </html>

    </xsl:template>

    <xsl:template name="logentries">
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td class="browse_title_buttonrow_seperator"><img src="images/1x1.gif"/></td>
        </tr>
        <xsl:if test="$tablekey = ''">
          <tr>
            <td class="browse_buttonrow_datarows_seperator"><img src="images/1x1.gif"/></td>
          </tr>
        </xsl:if>
        <tr>
          <td>
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="browsetable">
              <tr>
                <xsl:call-template name="tablecolumnheader">
                  <xsl:with-param name="caption" select="'%fldSite%'"/>
                  <xsl:with-param name="pageURL" select="$pageURL"/>
                  <xsl:with-param name="sortable" select="'false'"/>
                  <xsl:with-param name="width" select="'150'"/>
                </xsl:call-template>

                <xsl:call-template name="tablecolumnheader">
                  <xsl:with-param name="caption" select="'%fldEvent%'"/>
                  <xsl:with-param name="pageURL" select="$pageURL"/>
                  <xsl:with-param name="sortable" select="'false'"/>
                  <xsl:with-param name="width" select="'100'"/>
                </xsl:call-template>

                <xsl:call-template name="tablecolumnheader">
                  <xsl:with-param name="caption" select="'%fldTitle%'"/>
                  <xsl:with-param name="pageURL" select="$pageURL"/>
                  <xsl:with-param name="sortable" select="'false'"/>
                </xsl:call-template>

                <xsl:call-template name="tablecolumnheader">
                  <xsl:with-param name="caption" select="'%fldUser%'"/>
                  <xsl:with-param name="pageURL" select="$pageURL"/>
                  <xsl:with-param name="sortable" select="'false'"/>
                  <xsl:with-param name="width" select="'140'"/>
                </xsl:call-template>

                <xsl:call-template name="tablecolumnheader">
                  <xsl:with-param name="caption" select="'%fldTimestamp%'"/>
                  <xsl:with-param name="pageURL" select="$pageURL"/>
                  <xsl:with-param name="sortable" select="'false'"/>
                  <xsl:with-param name="width" select="'100'"/>
                </xsl:call-template>

                <xsl:call-template name="tablecolumnheader">
                  <xsl:with-param name="width" select="'20'"/>
                  <xsl:with-param name="caption" select="''"/>
                  <xsl:with-param name="sortable" select="'false'"/>
                </xsl:call-template>
              </tr>

              <xsl:for-each select="/logentries/logentry">
                <xsl:sort select="@timestamp" order="descending"/>

                <xsl:variable name="css-class">
                  <xsl:text>browsetablecell</xsl:text>
                  <xsl:if test="position() = last()">
                    <xsl:text> row-last</xsl:text>
                  </xsl:if>
                </xsl:variable>


                <tr>
                  <xsl:call-template name="tablerowpainter"/>

                  <td class="{$css-class}" title="%msgClickToView%">
                    <xsl:if test="$key = ''">
						<xsl:attribute name="onclick">
	                        <xsl:text>javascript:gotoLocation(document.getElementById('view</xsl:text>
	                        <xsl:value-of select="@key"/>
	                        <xsl:text>').href);</xsl:text>
						</xsl:attribute>
                    </xsl:if>
                    <xsl:choose>
                      <xsl:when test="@menukey">
                        <xsl:value-of select="@menuname"/>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:text>Adminweb</xsl:text>
                      </xsl:otherwise>
                    </xsl:choose>
                  </td>

                  <td class="{$css-class}" title="%msgClickToView%">
                    <xsl:if test="$key = ''">
                      <xsl:attribute name="onclick">
                        <xsl:text>javascript:gotoLocation(document.getElementById('view</xsl:text>
                        <xsl:value-of select="@key"/>
                        <xsl:text>').href);</xsl:text>
                      </xsl:attribute>
                    </xsl:if>
                    <xsl:choose>
                      <xsl:when test="@typekey = 2">
                        <span class="warning-message">
                          <xsl:call-template name="actionstring">
                            <xsl:with-param name="typekey" select="@typekey"/>
                            <xsl:with-param name="tablekey" select="@tablekey"/>
                          </xsl:call-template>
                        </span>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:call-template name="actionstring">
                          <xsl:with-param name="typekey" select="@typekey"/>
                          <xsl:with-param name="tablekey" select="@tablekey"/>
                          <xsl:with-param name="count" select="@count"/>
                        </xsl:call-template>
                      </xsl:otherwise>
                    </xsl:choose>
                  </td>

                  <td class="{$css-class}" title="%msgClickToView%">
                    <xsl:if test="$key = ''">
                      <xsl:attribute name="onclick">
                        <xsl:text>javascript:gotoLocation(document.getElementById('view</xsl:text>
                        <xsl:value-of select="@key"/>
                        <xsl:text>').href);</xsl:text>
                      </xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="title"/>
                  </td>

                  <td class="{$css-class}" title="%msgClickToView%">
                    <xsl:if test="$key = ''">
                      <xsl:attribute name="onclick">
                        <xsl:text>javascript:gotoLocation(document.getElementById('view</xsl:text>
                        <xsl:value-of select="@key"/>
                        <xsl:text>').href);</xsl:text>
                      </xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="@username"/>
                  </td>

                  <td class="{$css-class}" title="%msgClickToView%">
                    <xsl:if test="$key = ''">
                      <xsl:attribute name="onclick">
                        <xsl:text>javascript:gotoLocation(document.getElementById('view</xsl:text>
                        <xsl:value-of select="@key"/>
                        <xsl:text>').href);</xsl:text>
                      </xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="formatdatetime">
                      <xsl:with-param name="date" select="@timestamp"/>
                    </xsl:call-template>
                    &nbsp;
                    <xsl:value-of select="substring(timestamp, 12, 8)"/>
                  </td>

                  <td class="{$css-class}" title="%msgClickToView%">
                    <xsl:if test="$key = ''">
                      <xsl:attribute name="onclick">
                        <xsl:text>javascript:gotoLocation(document.getElementById('view</xsl:text>
                        <xsl:value-of select="@key"/>
                        <xsl:text>').href);</xsl:text>
                      </xsl:attribute>
                      <xsl:call-template name="logoperations">
                        <xsl:with-param name="page" select="$page"/>
                        <xsl:with-param name="key" select="@key"/>
                      </xsl:call-template>
                    </xsl:if>
                  </td>
                </tr>
              </xsl:for-each>
            </table>
          </td>
        </tr>
        <tr>
          <td>
            <br/>
          </td>
        </tr>
        <tr>
          <td>
            <table cellspacing="0" cellpadding="2" border="0" width="100%">
              <tr>
                <td align="center">
                  <xsl:choose>
                    <xsl:when test="$from &gt; 0">
                      <a>
                        <xsl:attribute name="href">
                          <xsl:text>adminpage?page=</xsl:text>
                          <xsl:value-of select="$page"/>
                          <xsl:text>&amp;op=browse&amp;from=</xsl:text>
                          <xsl:value-of select="$from - $plusscount"/>
                          <xsl:if test="$selecteddomainkey &gt;= 0">
                            <xsl:text>&amp;selecteddomainkey=</xsl:text>
                            <xsl:value-of select="$selecteddomainkey"/>
                          </xsl:if>
                          <xsl:if test="$tablekeyvalue &gt;= 0">
                            <xsl:text>&amp;tablekey=</xsl:text>
                            <xsl:value-of select="$tablekey"/>
                            <xsl:text>&amp;tablekeyvalue=</xsl:text>
                            <xsl:value-of select="$tablekeyvalue"/>
                          </xsl:if>
                          <xsl:if test="$userkey != ''">
                            <xsl:text>&amp;userkey=</xsl:text>
                            <xsl:value-of select="$userkey"/>
                          </xsl:if>
                          <xsl:if test="$popup = 'true'">
                            <xsl:text>&amp;popup=true</xsl:text>
                          </xsl:if>
                          <xsl:if test="$filter != ''">
                            <xsl:text>&amp;filter=</xsl:text>
                            <xsl:value-of select="$filter"/>
                          </xsl:if>
                        </xsl:attribute>
                        <xsl:text>%cmdPrevious%</xsl:text>
                      </a>
                      <xsl:text>&nbsp;</xsl:text>
                      <xsl:text>&nbsp;</xsl:text>
                      <img src="images/icon_previous.gif"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <span style="color: #C0C0C0">%cmdPrevious%</span>
                      <xsl:text>&nbsp;</xsl:text>
                      <xsl:text>&nbsp;</xsl:text>
                      <img src="images/icon_previous.gif" style="filter: alpha(opacity=30);"/>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:choose>
                    <xsl:when test="count(/logentries/logentry) = 0">
                      <xsl:text>0 - 0</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:text></xsl:text><xsl:value-of select="$from + 1"/> - <xsl:value-of select="$from + count(/logentries/logentry)"/>
                      <xsl:text>&nbsp;&nbsp;%of%&nbsp;&nbsp;</xsl:text>
                      <xsl:value-of select="/logentries/@totalcount"/>
                      <xsl:text></xsl:text>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:choose>
                    <xsl:when test="/logentries/@totalcount &gt; $from + count(/logentries/logentry)">
                      <img src="images/icon_next.gif"/>
                      <xsl:text>&nbsp;</xsl:text>
                      <xsl:text>&nbsp;</xsl:text>
                      <a>
                        <xsl:attribute name="href">
                          <xsl:text>adminpage?page=</xsl:text>
                          <xsl:value-of select="$page"/>
                          <xsl:text>&amp;op=browse&amp;from=</xsl:text>
                          <xsl:value-of select="$from + $plusscount"/>
                          <xsl:if test="$selecteddomainkey &gt;= 0">
                            <xsl:text>&amp;selecteddomainkey=</xsl:text>
                            <xsl:value-of select="$selecteddomainkey"/>
                          </xsl:if>
                          <xsl:if test="$tablekeyvalue &gt;= 0">
                            <xsl:text>&amp;tablekey=</xsl:text>
                            <xsl:value-of select="$tablekey"/>
                            <xsl:text>&amp;tablekeyvalue=</xsl:text>
                            <xsl:value-of select="$tablekeyvalue"/>
                          </xsl:if>
                          <xsl:if test="$userkey != ''">
                            <xsl:text>&amp;userkey=</xsl:text>
                            <xsl:value-of select="$userkey"/>
                          </xsl:if>
                          <xsl:if test="$popup = 'true'">
                            <xsl:text>&amp;popup=true</xsl:text>
                          </xsl:if>
                          <xsl:if test="$filter != ''">
                            <xsl:text>&amp;filter=</xsl:text>
                            <xsl:value-of select="$filter"/>
                          </xsl:if>
                        </xsl:attribute>
                        <xsl:text>%cmdNext%</xsl:text>
                      </a>
                    </xsl:when>
                    <xsl:otherwise>
                      <img src="images/icon_next.gif" style="filter: alpha(opacity=30);"/>
                      <xsl:text>&nbsp;</xsl:text>
                      <xsl:text>&nbsp;</xsl:text>
                      <span style="color: #C0C0C0">%cmdNext%</span>
                    </xsl:otherwise>
                  </xsl:choose>
                </td>
              </tr>
            </table>
          </td>
        </tr>

      </table>
    </xsl:template>

    <xsl:template name="logoperations">
        <xsl:param name="page"/>
        <xsl:param name="key"/>

        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <!-- view operation -->
                <td align="center" class="operationscell">
                    <xsl:call-template name="button">
                        <xsl:with-param name="style" select="'flat'"/>
                        <xsl:with-param name="type" select="'link'"/>
                        <xsl:with-param name="name">
                            <xsl:text>view</xsl:text>
                            <xsl:value-of select="$key"/>
                        </xsl:with-param>
                        <xsl:with-param name="id">
                            <xsl:text>view</xsl:text>
                            <xsl:value-of select="$key"/>
                        </xsl:with-param>
                        <xsl:with-param name="image" select="'images/icon_preview.gif'"/>
                        <xsl:with-param name="tooltip" select="'%tooltipViewLogEntry%'"/>
                        <xsl:with-param name="href">
                            <xsl:text>adminpage?page=</xsl:text>
                            <xsl:value-of select="$page"/>
                            <xsl:text>&amp;op=view&amp;key=</xsl:text>
                            <xsl:value-of select="$key"/>
                            <xsl:if test="$selecteddomainkey &gt;= 0">
                                <xsl:text>&amp;selecteddomainkey=</xsl:text>
                                <xsl:value-of select="$selecteddomainkey"/>
                            </xsl:if>
                            <xsl:if test="$tablekeyvalue &gt;= 0">
                                <xsl:text>&amp;tablekey=</xsl:text>
                                <xsl:value-of select="$tablekey"/>
                                <xsl:text>&amp;tablekeyvalue=</xsl:text>
                                <xsl:value-of select="$tablekeyvalue"/>
                            </xsl:if>
                            <xsl:if test="$userkey != ''">
                                <xsl:text>&amp;userkey=</xsl:text>
                                <xsl:value-of select="$userkey"/>
                            </xsl:if>
                            <xsl:if test="$popup = 'true'">
                                <xsl:text>&amp;popup=true</xsl:text>
                            </xsl:if>
                        </xsl:with-param>
                    </xsl:call-template>
                </td>
            </tr>
        </table>
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