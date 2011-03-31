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
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:param name="verticaloperationsunitkey"/>
  <xsl:param name="selectedcontentkey"/>
  
  <xsl:output method="html"/>
  
  <xsl:template match="/">
    
    <html>
      
      <link rel="stylesheet" type="text/css" href="css/admin.css"/>
      
      <body>
        
        <!-- table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td valign="top" width="100%">
              <xsl:call-template name="operations"/>
            </td>
            <td>
              <img src="images/1x1.gif" width="10" height="1"/>
            </td>
            <td valign="top">
              <xsl:call-template name="vertical_operations"/>
            </td>
          </tr>
        </table -->
        
      </body>
    </html>
    
  </xsl:template>

  <xsl:template name="operations">
    <table border="0" cellpadding="0" cellspacing="5" width="100%">
      <tr>
        <td>
          <xsl:if test="not(contents/content/ownerunit[@key!=$verticaloperationsunitkey])=false">
            <b><font size="+1">%headInternalMessagesFor% <xsl:value-of select="contents/content/ownerunit[@key!=$verticaloperationsunitkey]"/></font></b><br/><br/>
          </xsl:if>
        </td>
      </tr>
      
      <xsl:for-each select="contents/content[ownerunit/@key!=$verticaloperationsunitkey]">
        <xsl:sort select="publishdate" order="descending"/>
        <tr bgcolor="#CCCCCC">
          <td>
            <p><img src="images/1x1.gif" width="1" height="1"/></p>
          </td>
        </tr>
        <tr>
          <td>
            <br/>
          </td>
        </tr>
        <tr>
          <td>
            <xsl:call-template name="printdatetime">
              <xsl:with-param name="date"><xsl:value-of select="publishdate"/></xsl:with-param>
            </xsl:call-template><br/>
            <b><xsl:value-of select="contentdata/title"/></b><br/>
            <xsl:value-of disable-output-escaping="yes" select="contentdata/body"/>
            <br/>
          </td>
        </tr>
        <tr>
          <td>
            <br/>
          </td>
        </tr>
      </xsl:for-each>
    </table>
  </xsl:template>
  
  <xsl:template name="vertical_operations">
    <table border="0" cellpadding="0" cellspacing="0" width="280" bgcolor="#f7f7f7">
      <tr>
        <td width="8" height="8" valign="top"  nowrap="nowrap"><img src="images/corner_v_o.gif" width="8" height="8" align="TOP" border="0"/></td>
        <td height="8"><img src="images/fyll.gif" width="1" height="1" align="top" border="0"/></td>
        <td width="8" height="8" valign="top"  nowrap="nowrap"><img src="images/corner_h_o.gif" width="8" height="8" align="TOP" border="0"/></td>
      </tr>
      <tr>
        <td width="8">&nbsp;</td>
        <td>
          <p align="center"><b>%headOperationMessagesFrom% <xsl:value-of select="/contents/content/ownersite[../ownerunit/@key=$verticaloperationsunitkey]"/></b></p>
          
          <xsl:choose>
            <xsl:when test="$selectedcontentkey=''">
              <xsl:for-each select="contents/content[ownerunit/@key=$verticaloperationsunitkey]">
                <xsl:sort select="timestamp" order="descending"/>
                <p>
                  <xsl:call-template name="printdatetime">
                    <xsl:with-param name="date"><xsl:value-of select="publishdate"/></xsl:with-param>
                  </xsl:call-template><br/>
                  <a>
                    <xsl:attribute name="href">
                      <xsl:text>adminpage?page=50&amp;contentkey=</xsl:text>
                      <xsl:value-of select="@key"/>
                    </xsl:attribute>
                    <xsl:value-of select="contentdata/title"/>
                  </a>
                  <br/>
                </p>
              </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
              <p>
                <xsl:call-template name="printdatetime">
                  <xsl:with-param name="date"><xsl:value-of select="contents/content[@key=$selectedcontentkey]/publishdate"/></xsl:with-param>
                </xsl:call-template><br/>
                <b><xsl:value-of select="contents/content[@key=$selectedcontentkey]/contentdata/title"/></b><br/>
                <xsl:value-of disable-output-escaping="yes" select="contents/content[@key=$selectedcontentkey]/contentdata/body"/>
                <p>
                  <a>
                    <xsl:attribute name="href">
                      <xsl:text>adminpage?page=50</xsl:text>
                    </xsl:attribute>
                    &lt;&lt; %cmdBack%
                  </a>
                </p>
              </p>
            </xsl:otherwise>
          </xsl:choose>
        </td>
        <td width="8">&nbsp;</td>
      </tr>
      <tr>
        <td width="8" height="8" valign="bottom"  nowrap="nowrap"><img src="images/corner_v_n.gif" width="8" height="8" align="bottom" border="0"/></td>
        <td height="8"><img src="images/fyll.gif" width="1" height="1" align="bottom" border="0"/></td>
        <td width="8" height="8" valign="bottom"  nowrap="nowrap"><img src="images/corner_h_n.gif" width="8" height="8" align="bottom" border="0"/></td>
      </tr>
    </table>
  </xsl:template>
  
  <xsl:template name="printdatetime">
    <xsl:param name="date"/>
    <xsl:value-of select="substring($date, 9, 2)"/>.<xsl:value-of select="substring($date, 6, 2)"/>.<xsl:value-of select="substring($date, 1, 4)"/>&#160;<xsl:value-of select="substring($date, 12, 5)"/>
  </xsl:template>
  
</xsl:transform>