<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:param name="show_details"/>
  <xsl:param name="admin_email"/>

  <xsl:output method="html"/>
  
  <xsl:template match="/">
    <html>
      <link rel="stylesheet" type="text/css" href="css/admin.css"/>
      <body>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td>
              <table width="100%" border="0" cellspacing="4" cellpadding="0">
                <xsl:choose>
                    <xsl:when test="not(/error/throwables)">
                    <xsl:call-template name="error_display"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:call-template name="throwable_display"/>
                  </xsl:otherwise>
                </xsl:choose>
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
              <br/>
            </td>
          </tr>
          
        </table>
        
      </body>
    </html>  
  </xsl:template>

  <xsl:template name="error_display">
    <tr>
      <td bgcolor="#F7F7F7" colspan="2"><b>An error occurred while processing your request</b></td>
    </tr>
    <tr>
        <td><xsl:value-of select="/error/message"/></td>
    </tr>
    <tr bgcolor="#CCCCCC">
      <td colspan="2">
        <p><img src="images/1x1.gif" width="1" height="1"/></p>
      </td>
    </tr>
    <tr>
      <td colspan="2" align="left">
    	<xsl:choose>
      		<xsl:when test="$admin_email">
		      <a href="mailto:{$admin_email}">&lt; Contact administrator &gt;</a>
		    </xsl:when>
		    <xsl:otherwise>&nbsp;
		    </xsl:otherwise>
      	</xsl:choose>
      </td>
    </tr>
  </xsl:template>  

  <xsl:template name="throwable_display">
    <tr>
      <td bgcolor="#F7F7F7" colspan="2"><b>An error occurred while processing your request</b></td>
    </tr>
    <tr>
        <td><xsl:value-of select="/error/throwables/throwable/message"/></td>
    </tr>
    <tr bgcolor="#CCCCCC">
      <td colspan="2">
        <p><img src="images/1x1.gif" width="1" height="1"/></p>
      </td>
    </tr>
    <tr>
      <td align="left">
      	<xsl:choose>
      		<xsl:when test="$admin_email">
		      <a href="mailto:{$admin_email}">&lt; Contact administrator &gt;</a>
		    </xsl:when>
		    <xsl:otherwise>&nbsp;
		    </xsl:otherwise>
      	</xsl:choose>
      </td>
      <xsl:choose>
        <xsl:when test="$show_details = 'true'">
          <td align="right" nowrap="nowrap"><a href="errorpage?show_details=false">&lt; Hide details &gt;</a></td>
        </xsl:when>
        <xsl:otherwise>
          <td align="right" nowrap="nowrap"><a href="errorpage?show_details=true">&lt; Show details &gt;</a></td>
        </xsl:otherwise>
      </xsl:choose>
    </tr>
    <xsl:if test="$show_details = 'true'">
      <tr>
        <td colspan="2">
          <b></b>
        </td>
      </tr>
      <xsl:variable name="throwable_count">
          <xsl:value-of select="count(/error/throwables/throwable)"/>
      </xsl:variable>
      <xsl:for-each select="/error/throwables/throwable">
        <tr>
          <td colspan="2">
            <b></b>
          </td>
        </tr>
        <xsl:choose>
          <xsl:when test="number($throwable_count)-position() &lt; 1">
            <tr>
              <td bgcolor="#F7F7F7" colspan="2"><b>Root exception</b></td>
            </tr>
          </xsl:when>
          <xsl:otherwise>
            <tr>
              <td bgcolor="#F7F7F7" colspan="2">
                <b>Wrapped exception <xsl:value-of select="number($throwable_count)-position()"/></b>
              </td>
            </tr>
          </xsl:otherwise>
        </xsl:choose>
        <tr>
          <td colspan="2">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <xsl:for-each select="stacktrace/stacktraceitem">
                <xsl:choose>
                  <xsl:when test="position() = 1">
                    <tr>
                      <td colspan="2"><xsl:value-of select="."/></td>
                    </tr>
                  </xsl:when>
                  <xsl:otherwise>
                    <tr>
                      <td style="width: 20px;">&nbsp;</td>
                      <td><xsl:value-of select="."/></td>
                    </tr>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:for-each>
            </table>
          </td>
        </tr>
      </xsl:for-each>
    </xsl:if>
  </xsl:template>  
</xsl:stylesheet>