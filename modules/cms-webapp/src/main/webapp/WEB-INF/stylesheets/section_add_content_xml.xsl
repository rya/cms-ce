<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:output method="xml"/>

  <xsl:template match="/">
      <sections>
          <xsl:attribute name="contentkey">
              <xsl:value-of select="/wizardstate/stepstate[1]/content/@key"/>
          </xsl:attribute>

          <xsl:for-each select="/wizardstate/stepstate[1]/section">
              <section>
                  <xsl:attribute name="key">
                      <xsl:value-of select="@key"/>
                  </xsl:attribute>
                  <xsl:variable name="key">
                      <xsl:value-of select="@key"/>
                  </xsl:variable>
                  
                  <xsl:if test="/wizardstate/stepstate[2]/section[@key = $key]">
                      <xsl:attribute name="approved">true</xsl:attribute>

                      <xsl:if test="@ordered = 'true'">
                          <contents>
                              <xsl:for-each select="/wizardstate/stepstate[@stepid = 2 and section/@key = $key]/content">
                                  <content>
                                      <xsl:attribute name="key">
                                          <xsl:value-of select="@key"/>
                                      </xsl:attribute>
                                  </content>
                              </xsl:for-each>
                          </contents>
                      </xsl:if>
                  </xsl:if>
              </section>
          </xsl:for-each>
      </sections>
  </xsl:template>

</xsl:stylesheet>