<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:output method="xml"/>

  <xsl:template match="/">
      <section name="{/wizardstate/stepstate[@id = '0']/section/name}">
          <xsl:attribute name="ordered">
              <xsl:choose>
                  <xsl:when test="/wizardstate/stepstate[@id = '0']/section/ordered = 'on'">true</xsl:when>
                  <xsl:otherwise>false</xsl:otherwise>
              </xsl:choose>
          </xsl:attribute>

          <description>
              <xsl:value-of select="/wizardstate/stepstate[@id = '0']/section/description"/>
          </description>
      </section>
  </xsl:template>

</xsl:stylesheet>
