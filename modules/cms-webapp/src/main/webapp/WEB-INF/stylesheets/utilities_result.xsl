<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:output method="html"/>

  <xsl:param name="operation"/>
  <xsl:param name="result"/>

  <xsl:variable name="title">
      <xsl:choose>
          <xsl:when test="$operation = 'init'">
              <xsl:text>Initialized</xsl:text>
          </xsl:when>
          <xsl:when test="$operation = 'upgrade'">
              <xsl:text>Upgraded</xsl:text>
          </xsl:when>
          <xsl:when test="$operation = 'import'">
              <xsl:text>Imported into</xsl:text>
          </xsl:when>
      </xsl:choose>
      <xsl:text> Enonic CMS</xsl:text>
  </xsl:variable>

  <xsl:template match="/">
      <html>
          <head>
              <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
              <title><xsl:value-of select="$title"/></title>
              <link rel="stylesheet" type="text/css" href="css/admin.css"/> 
          </head>
          <body>
              <h1>
                  <xsl:value-of select="$title"/>
              </h1>

              <pre>
                  <xsl:value-of select="$result"/>
              </pre>
          </body>
      </html>
  </xsl:template>

</xsl:stylesheet>
