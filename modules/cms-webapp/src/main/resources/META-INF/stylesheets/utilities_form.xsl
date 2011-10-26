<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>

  <xsl:param name="operation"/>
  <xsl:param name="databaseversion"/>
  <xsl:param name="softwareversion"/>

  <xsl:variable name="title">
      <xsl:choose>
          <xsl:when test="$operation = 'init'">
              <xsl:text>Initialize</xsl:text>
          </xsl:when>
          <xsl:when test="$operation = 'upgrade'">
              <xsl:text>Upgrade</xsl:text>
          </xsl:when>
          <xsl:when test="$operation = 'import'">
              <xsl:text>Import into</xsl:text>
          </xsl:when>
      </xsl:choose>
      <xsl:text> Enonic CMS </xsl:text>
      <xsl:choose>
          <xsl:when test="$operation = 'import'">
              <xsl:value-of select="$databaseversion"/>
          </xsl:when>
          <xsl:when test="$operation = 'upgrade'">
              <xsl:text>from </xsl:text>
              <xsl:value-of select="$databaseversion"/>
              <xsl:text> to </xsl:text>
              <xsl:value-of select="$softwareversion"/>
          </xsl:when>
          <xsl:otherwise>
              <xsl:value-of select="$softwareversion"/>
          </xsl:otherwise>
      </xsl:choose>
  </xsl:variable>

  <xsl:template match="/">
      <html xmlns="http://www.w3.org/1999/xhtml">
          <head>
              <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
              <title><xsl:value-of select="$title"/></title>
              <link rel="stylesheet" type="text/css" href="css/admin.css"/> 
          </head>
          <body>
              <h1>
                  <xsl:value-of select="$title"/>
              </h1>

              <form method="post" enctype="multipart/form-data">
                  <xsl:attribute name="action">
                      <xsl:value-of select="$operation"/>
                  </xsl:attribute>

                  <table border="0" cellspacing="2" cellpadding="0" width="100%">
                      <tr>
                          <td>Administrator UID:</td>
                          <td><input type="text" name="uid"/></td>
                      </tr>
                      <tr>
                          <td>Administrator password:</td>
                          <td><input type="password" name="password"/></td>
                      </tr>
                      <tr>
                          <td>
                              <xsl:choose>
                                  <xsl:when test="$operation = 'upgrade'">ZIP</xsl:when>
                                  <xsl:otherwise>XML</xsl:otherwise>
                              </xsl:choose>
                              <xsl:text> file:</xsl:text>
                          </td>
                          <td>
                              <input type="file" name="uploadfile" accept="text/xml"/>
                          </td>
                      </tr>
                      <tr>
                          <td colspan="2"><br/></td>
                      </tr>
                      <tr>
                          <td colspan="2">
                              <input type="submit">
                                  <xsl:attribute name="value">
                                      <xsl:choose>
                                          <xsl:when test="$operation = 'init'">
                                              <xsl:text>Initialize</xsl:text>
                                          </xsl:when>
                                          <xsl:when test="$operation = 'upgrade'">
                                              <xsl:text>Upgrade</xsl:text>
                                          </xsl:when>
                                          <xsl:when test="$operation = 'import'">
                                              <xsl:text>Import</xsl:text>
                                          </xsl:when>
                                      </xsl:choose>
                                  </xsl:attribute>
                              </input>
                              <xsl:text>&nbsp;</xsl:text>
                              <input type="reset"/>
                          </td>
                      </tr>
                  </table>
              </form>
          </body>
      </html>
  </xsl:template>

</xsl:stylesheet>
