<?xml version="1.0" encoding="utf-8"?>

<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
 ]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    <xsl:output method="html"/>
    <xsl:param name="index"/>
    <xsl:template match="/">
        <html>
            <head>
                <title>%headReport%</title>
                <link href="css/admin.css" rel="stylesheet" type="text/css"/>
            </head>
            <body>
                    <xsl:for-each select="/contents/content/contentdata/sendhistory/sent">
                            <div class="recipient">
                              <span class="name"><xsl:value-of select="recipients/recipient/@name"/></span>
                              <span class="email">
                              	<xsl:text> (</xsl:text>
                              	<xsl:value-of select="recipients/recipient/@email"/>
                              	<xsl:text>)</xsl:text>
                              </span>
                              <xsl:text> : </xsl:text>
                              <xsl:choose>
                                <xsl:when test="@error = 'true'">
                                  <span class="error">ERROR (<xsl:value-of select="error"/>)</span>
                                </xsl:when>
                                <xsl:otherwise>
                                  <span class="ok">OK</span>
                                </xsl:otherwise>
                              </xsl:choose>
                              <table class="parameters" cellpadding="0" cellspacing="0">
                              	<tr>
                              		<th>Parameter</th>
                              		<th>Value</th>
                              	</tr>
                                <xsl:for-each select="recipients/recipient/parameter">
                                  <tr>
                                    <xsl:choose>
	                                   	<xsl:when test="position() mod 2 = 1">
	                                    	<xsl:attribute name="class">odd</xsl:attribute>
	                                   	</xsl:when>
	                                   	<xsl:otherwise>
	                                    	<xsl:attribute name="class">even</xsl:attribute>
	                                   	</xsl:otherwise>
                                   	</xsl:choose>
                                    <td>
                                    <xsl:value-of select="@name"/></td>
                                    <td><xsl:value-of select="."/></td>
                                  </tr>
                                </xsl:for-each>
                              </table>
                              </div>
                    </xsl:for-each>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>