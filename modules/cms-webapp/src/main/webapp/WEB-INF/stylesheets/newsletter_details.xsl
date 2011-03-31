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

	<xsl:param name="page"/>
	<xsl:param name="unitkey"/>
	<xsl:param name="key"/>

	<xsl:template match="/">

          <html>
            <script type="text/javascript" language="JavaScript" src="javascript/validate.js">
            </script>

            <head>
              <link rel="stylesheet" type="text/css" href="css/admin.css"/>
            </head>

            <body>

              <h1>%headTransmissionDetails%</h1>

              <table border="0" cellspacing="0" cellpadding="0" width="100%">
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
                    <table border="0" cellspacing="2" cellpadding="2">

                      <xsl:for-each select="/contents/content/contentdata/senddata">
                        <xsl:sort select="senddate" order="descending"/>
                        <tr>
                          <td>
                            <br/>
                          </td>
                        </tr>

                        <tr>
                          <td>Sent date:</td>
                          <td>
                            <xsl:call-template name="formatdate">
                              <xsl:with-param name="date" select="senddate"/>
                            </xsl:call-template>
                            &nbsp;
                            <xsl:call-template name="formattime">
                              <xsl:with-param name="date" select="senddate"/>
                            </xsl:call-template>
                          </td>
                        </tr>

                        <tr>
                          <td align="left">%fldSubject%:</td>
                          <td align="left"><xsl:value-of select="subject"/></td>
                        </tr>

                        <tr>
                          <td>%fldSentBy%:</td>
                          <td><xsl:value-of select="from/name"/>, <xsl:value-of select="from/mail"/></td>
                        </tr>

                        <tr>
                          <td align="left" valign="top">%fldRecipientsHTML%:</td>
                          <td align="left" valign="top">
                            <xsl:for-each select="recipients/recipient[@type = 'html']">
                              <xsl:sort select="."/>
                              <xsl:value-of select="."/>
                              <br/>
                            </xsl:for-each>
                          </td>
                        </tr>

                        <tr>
                          <td align="left" valign="top">%fldRecipientsText%:</td>
                          <td align="left" valign="top">
                            <xsl:for-each select="recipients/recipient[@type='text']">
                              <xsl:sort select="."/>
                              <xsl:value-of select="."/>
                              <br/>
                            </xsl:for-each>
                          </td>
                        </tr>

                      </xsl:for-each>

                      </table>
                    </td>
                </tr>

              </table>

              <form>
                <input type="button" class="button" value="%cmdBack%" onclick="javascript: window.history.back();"/>
              </form>

            </body>
          </html>


  </xsl:template>

</xsl:stylesheet>
