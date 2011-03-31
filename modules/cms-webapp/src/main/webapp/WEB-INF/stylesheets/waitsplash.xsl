<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
  <xsl:output method="html"/>

  <xsl:param name="redirect"/>

  <xsl:template match="/">
    <html>
      <head>
        <link rel="stylesheet" type="text/css" href="css/admin.css"/>
      </head>
      <body onload="javascript: location.replace('{$redirect}');">
        <table style="width: 100%; height: 100%;">
          <tr>
            <td align="center" valign="middle">
              <div style="width:64px;height:64px;background-image:url(images/waitsplash.gif);margin:5px 0">
              </div>
              <xsl:text>%headPleaseWait%</xsl:text>
            </td>
          </tr>
        </table>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
