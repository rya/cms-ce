<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
	<!ENTITY Oslash "&#216;">
	<!ENTITY oslash "&#248;">
	<!ENTITY Aring  "&#197;">
	<!ENTITY aring  "&#229;">
	<!ENTITY AElig  "&#198;">
	<!ENTITY aelig  "&#230;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="html"/>

        <xsl:param name="unitkey" />
        <xsl:param name="contenttypekey" />
	<xsl:param name="returnkey"/>
        <xsl:param name="page" />
        <xsl:param name="to" />
        <xsl:param name="from" />
        <xsl:param name="type" />
        <xsl:param name="op" />
        <xsl:param name="search" />
        <xsl:param name="search_word" />

	<xsl:template match="/">
		<xsl:call-template name="imagelist"/>
	</xsl:template>

	<xsl:template name="imagelist">
        <xsl:variable name="imagetype" select="'thumbnail'"/>

	<html>
          <script type="text/javascript" language="JavaScript">
            function returnValue( bkey, width, height )
            {
              var f = document.formAdmin;

              f.op.value = "insert";
              f.width.value = width;
              f.height.value = height;
              f.binarykey.value = bkey;
              f.submit();
            }

            function OpenSelectorWindowSiteCty( page, width, height )
            {
              newWindow = window.open("adminpage?page=" + page + "&amp;op=select&amp;contenttypekey=" + document.formAdmin.contenttypekey.value, "Preview", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" + height);
              newWindow.focus();
            }

            function OpenSelectorWindowSiteUnit( page, keyname, viewname, width, height )
            {
              newWindow = window.open("adminpage?page=" + page + "&amp;returnkey=" + keyname + "&amp;returnview=" + viewname + "&amp;op=select&amp;unitkey=" + document.formAdmin.unitkey.value, "Preview", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" + height);
              newWindow.focus();
            }

          </script>

          <link rel="stylesheet" type="text/css" href="css/admin.css"/>
          <body>

            <table width="100%" border="0">
              <tr>
                <td>
                  <form name="formAdmin">
                    <xsl:attribute name="action">/servlet/com.enonic.vertical.adminweb.ContentDocumentHandlerServlet</xsl:attribute>
                    <input type="hidden" name="unitkey" value="{$unitkey}" />
                    <input type="hidden" name="contenttypekey" value="{$contenttypekey}"/>
                    <input type="hidden" name="op" value="{$op}"/>
                    <input type="hidden" name="returnkey" value="{$returnkey}"/>
                    <input type="hidden" name="page" value="{$page}"/>
                    <input type="hidden" name="type" value="{$type}"/>
                    <input type="hidden" name="imagetype" value="{$imagetype}"/>
                    <input type="hidden" name="from" value="0"/>
                    <input type="hidden" name="to" value="10"/>
                    <input type="hidden" name="search" value="search"/>
                    <input type="hidden" name="width"/>
                    <input type="hidden" name="height"/>
                    <input type="hidden" name="binarykey"/>

                    <table width="100%" border="0">
                      <tr bgcolor="#FOFOFO"><td colspan="4"><b>S&oslash;k:</b></td></tr>
                      <tr>
                        <td>S&oslash;k</td>
                        <td><input type="text" name="search_word" size="40"/></td>
                        <td><input type="submit" class="button" value="Søk" /></td>
                      </tr>
                    </table>
                  </form>
                </td>
              </tr>
              <tr>
                <td>
                  <xsl:choose>
                    <xsl:when test="count(/contents/content) &gt; 0">
                      <table width="100%" border="0" cellspacing="2" cellpadding="2">
                        <tr bgcolor="#FOFOFO"><td colspan="4"><b>Velg bilde:</b></td></tr>
                        <xsl:variable name="list" select="/contents/content"/>
                        <tr>
                        <xsl:for-each select="$list">
                          <xsl:sort select="contentdata/name"/>
                            <td valign="top" align="center">
                              <img border="0" hspace="4" vspace="4">
                                <xsl:attribute name="src">
                                  <xsl:text>adminbinary?id=</xsl:text>
                                  <xsl:value-of select="contentdata/images/image[@type = 'thumbnail']/binarydata/@key"/>
                                </xsl:attribute>
                              </img>
                              <br/>
                              <xsl:value-of select="contentdata/name"/>
                              <br/>
                              <center>
                                <xsl:for-each select="contentdata/images/image">
                                  <a>
                                    <xsl:attribute name="href">javascript:returnValue('<xsl:value-of select="binarydata/@key"/>','<xsl:value-of select="width"/>','<xsl:value-of select="height"/>')</xsl:attribute>
                                    (<xsl:value-of select="width"/> x <xsl:value-of select="height"/>)
                                  </a>
                                  <br/>
                                </xsl:for-each>
                              </center>
                            </td>
                            <xsl:if test="position() mod 2 != 1">
                              <xsl:text disable-output-escaping="yes">
                                &lt;/tr&gt;
                                &lt;tr&gt;
                              </xsl:text>
                            </xsl:if>
                          </xsl:for-each>
                          </tr>
                        <tr>
                          <td colspan="4">
                            <table width="100%">
                              <tr>
                                <td align="left">
                                  <xsl:if test="$from != '0'">
                                    <a href="adminpage?page={$page}&amp;op={$op}&amp;type={$type}&amp;returnkey={$returnkey}&amp;to={$to - 10}&amp;from={$from - 10}&amp;search={$search}&amp;search_word={$search_word}">
                                      &lt;&lt; Forrige
                                    </a>
                                  </xsl:if>
                                </td>
                                <td align="right">
                                  <xsl:if test="not(count(/contents/content) &lt; 10)">
                                    <a href="adminpage?page={$page}&amp;op={$op}&amp;type={$type}&amp;returnkey={$returnkey}&amp;to={$to + 10}&amp;from={$from + 10}&amp;search={$search}&amp;search_word={$search_word}">
                                      Neste &gt;&gt;
                                    </a>
                                  </xsl:if>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </xsl:when>
                    <xsl:otherwise>
                      <center>
                        <strong>Ingen bilder funnet.</strong>
                      </center>
                    </xsl:otherwise>
                  </xsl:choose>

                </td>
              </tr>
            </table>
          </body>
        </html>

      </xsl:template>

  </xsl:stylesheet>
