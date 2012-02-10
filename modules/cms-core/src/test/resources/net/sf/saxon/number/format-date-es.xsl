<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="text"/>
  <xsl:template match="/">
    <xsl:value-of select="format-date(current-date(), '[D1] [MNn] [Y0001]', 'es', (), ())"/>
  </xsl:template>
</xsl:stylesheet>
