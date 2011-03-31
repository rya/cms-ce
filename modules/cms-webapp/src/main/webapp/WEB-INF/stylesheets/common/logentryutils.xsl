<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <xsl:template name="actionstring">
      <xsl:param name="typekey"/>
      <xsl:param name="tablekey"/>
      <xsl:param name="count"/>

      <xsl:choose>
          <!-- type: admin_login -->
          <xsl:when test="$typekey = 0">
              <xsl:text>%txtLogin%</xsl:text>
          </xsl:when>

          <!-- type: admin_login_site -->
          <xsl:when test="$typekey = 1">
              <xsl:text>%txtLoginSite%</xsl:text>
          </xsl:when>

          <!-- type: admin_login_failed -->
          <xsl:when test="$typekey = 2">
              <xsl:text>%txtLoginFailed%</xsl:text>
          </xsl:when>

          <!-- type: admin_logout -->
          <xsl:when test="$typekey = 3">
              <xsl:text>%txtLogout%</xsl:text>
          </xsl:when>

          <!-- type: entity_created -->
          <xsl:when test="$typekey &gt;= 4 and $typekey &lt;= 7">
              <xsl:choose>
                  <xsl:when test="$tablekey = 0">
                      <xsl:text>%txtContent%</xsl:text>
                  </xsl:when>
                  <xsl:when test="$tablekey = 1">
                      <xsl:text>%txtMenuItem%</xsl:text>
                  </xsl:when>
                  <xsl:when test="$tablekey = 2">
                      <xsl:text>%txtService%</xsl:text>
                  </xsl:when>
                  <xsl:when test="$tablekey = 3">
                      <xsl:text>%txtSection%</xsl:text>
                  </xsl:when>
                  <xsl:when test="$tablekey = 4">
                      <xsl:text>%txtResource%</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                      <xsl:text>%txtUnknown%</xsl:text>
                  </xsl:otherwise>
              </xsl:choose>
              <xsl:choose>
                  <xsl:when test="$typekey = 4">
                      <xsl:text> %txtCreated%</xsl:text>
                  </xsl:when>
                  <xsl:when test="$typekey = 5">
                      <xsl:text> %txtUpdated%</xsl:text>
                  </xsl:when>
                  <xsl:when test="$typekey = 6">
                      <xsl:text> %txtRemoved%</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                      <xsl:text> %txtOpened%</xsl:text>
                      <xsl:if test="$count &gt;= 0">
                          <xsl:text> (</xsl:text>
                          <xsl:value-of select="$count"/>
                          <xsl:text>)</xsl:text>
                      </xsl:if>
                  </xsl:otherwise>
              </xsl:choose>
          </xsl:when>

          <xsl:otherwise>
              <xsl:text>Unknown action (type:</xsl:text>
              <xsl:value-of select="$typekey"/>
              <xsl:text>)</xsl:text>
          </xsl:otherwise>
      </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
