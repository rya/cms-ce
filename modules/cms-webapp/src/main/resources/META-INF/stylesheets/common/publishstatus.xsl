<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <xsl:template name="publishstatus">
    <xsl:param name="state" select="@state"/>
    <xsl:param name="has-draft" select="@has-draft"/>
    <xsl:param name="publishfrom" select="@publishfrom"/>
    <xsl:param name="publishto" select="@publishto"/>
    <xsl:param name="isPlacedInMenuItemOrSection"/>
    <xsl:param name="isMasterVersion"/>
    <xsl:param name="icononly" select="'true'"/>
    <xsl:param name="id"/>
    <xsl:param name="unsaved-draft" select="false()"/>

    <xsl:choose>
      <xsl:when test="( ($state = 0 or not($state ) ) and $unsaved-draft = true() )">
        <img src="images/icon_state_draft_edit.gif" width="16" height="16" border="0" class="publish-status-icon">
          <xsl:attribute name="alt">
            <xsl:text>%txtContentState0NotSaved%</xsl:text>
          </xsl:attribute>
          <xsl:attribute name="title">
            <xsl:text>%txtContentState0NotSaved%</xsl:text>
          </xsl:attribute>
          <xsl:if test="$id">
            <xsl:attribute name="id">
              <xsl:value-of select="$id"/>
            </xsl:attribute>
          </xsl:if>
        </img>
        <xsl:text> </xsl:text>

        <xsl:if test="$icononly = 'false'">
          <xsl:text>%txtContentState0NotSaved%</xsl:text>

          <!--xsl:variable name="mainVersion" select="/contents/content/versions/version[@current = 'true']"/>
          <xsl:choose>
            <xsl:when test="$mainVersion/@state = 3">
              <xsl:text> (%txtAnotherVersionIsArchived%)</xsl:text>
            </xsl:when>
            <xsl:when test="$mainVersion/@state = 4">
              <xsl:text> (%txtAnotherVersionIsPending%)</xsl:text>
            </xsl:when>
            <xsl:when test="$mainVersion/@state = 5">
              <xsl:text> (%txtAnotherVersionIsOnline%)</xsl:text>
            </xsl:when>
            <xsl:when test="$mainVersion/@state = 6">
              <xsl:text> (%txtAnotherVersionHasExpired%)</xsl:text>
            </xsl:when>
          </xsl:choose-->

        </xsl:if>
      </xsl:when>
      <xsl:when test="$state = 0 or not($state)">
        <img src="images/icon_state_draft.gif" width="16" height="16" border="0" class="publish-status-icon">
          <xsl:attribute name="alt">
            <xsl:text>%txtContentState0%</xsl:text>
          </xsl:attribute>
          <xsl:attribute name="title">
            <xsl:text>%txtContentState0%</xsl:text>
          </xsl:attribute>
          <xsl:if test="$id">
            <xsl:attribute name="id">
              <xsl:value-of select="$id"/>
            </xsl:attribute>
          </xsl:if>
        </img>
        <xsl:text> </xsl:text>

        <xsl:if test="$icononly = 'false'">
          <xsl:text>%txtContentState0%</xsl:text>
          <xsl:choose>
            <xsl:when test="$isMasterVersion">
              <xsl:text> (%txtOffline%)</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:variable name="mainVersion" select="/contents/content/versions/version[@current = 'true']"/>
              <xsl:choose>
                <xsl:when test="$mainVersion/@state = 3">
                  <xsl:text> (%txtAnotherVersionIsArchived%)</xsl:text>
                </xsl:when>
                <xsl:when test="$mainVersion/@state = 4">
                  <xsl:text> (%txtAnotherVersionIsPending%)</xsl:text>
                </xsl:when>
                <xsl:when test="$mainVersion/@state = 5">
                  <xsl:text> (%txtAnotherVersionIsOnline%)</xsl:text>
                </xsl:when>
                <xsl:when test="$mainVersion/@state = 6">
                  <xsl:text> (%txtAnotherVersionHasExpired%)</xsl:text>
                </xsl:when>
              </xsl:choose>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:if>
      </xsl:when>

      <xsl:when test="$state = 1">
        <img src="images/icon_state_snapshot.gif" width="16" height="16" border="0" class="publish-status-icon">
          <xsl:attribute name="alt">%txtContentState1%</xsl:attribute>
          <xsl:attribute name="title">%txtContentState1%</xsl:attribute>
          <xsl:if test="$id">
            <xsl:attribute name="id">
              <xsl:value-of select="$id"/>
            </xsl:attribute>
          </xsl:if>
        </img>
        <xsl:text> </xsl:text>
        <xsl:if test="$icononly = 'false'">
          <xsl:text>%txtContentState1%</xsl:text>
        </xsl:if>
      </xsl:when>

      <xsl:when test="$state = 2">
        <img src="images/icon_state_approved.gif" width="16" height="16" border="0" class="publish-status-icon">
          <xsl:attribute name="alt">
            <xsl:text>%txtContentState2%</xsl:text>
          </xsl:attribute>
          <xsl:attribute name="title">
            <xsl:text>%txtContentState2%</xsl:text>
          </xsl:attribute>
          <xsl:if test="$id">
            <xsl:attribute name="id">
              <xsl:value-of select="$id"/>
            </xsl:attribute>
          </xsl:if>
        </img>
        <xsl:text> </xsl:text>

        <xsl:if test="$icononly = 'false'">
          <xsl:text>%txtContentState2%</xsl:text>
          <xsl:if test="$isPlacedInMenuItemOrSection">
            <xsl:text> %txtAndPublishedToSite%</xsl:text>
          </xsl:if>
          <xsl:text> (%offlineOnlineFromDateMissing%)</xsl:text>
        </xsl:if>
      </xsl:when>

      <xsl:when test="$state = 3">
        <img src="images/icon_state_archived.gif" width="16" height="16" border="0" class="publish-status-icon">
          <xsl:attribute name="alt">
            <xsl:text>%txtContentState3%</xsl:text>
          </xsl:attribute>
          <xsl:attribute name="title">
            <xsl:text>%txtContentState3%</xsl:text>
          </xsl:attribute>
          <xsl:if test="$id">
            <xsl:attribute name="id">
              <xsl:value-of select="$id"/>
            </xsl:attribute>
          </xsl:if>
        </img>
        <xsl:text> </xsl:text>
        <xsl:if test="$icononly = 'false'">
          <xsl:text>%txtContentState3%</xsl:text>
          <xsl:choose>
            <xsl:when test="$isMasterVersion">
              <xsl:text> (%txtOffline%)</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:variable name="mainVersion" select="/contents/content/versions/version[@current = 'true']"/>
              <xsl:choose>
                <xsl:when test="$mainVersion/@state = 4">
                  <xsl:text> (%txtAnotherVersionIsPending%)</xsl:text>
                </xsl:when>
                <xsl:when test="$mainVersion/@state = 5">
                  <xsl:text> (%txtAnotherVersionIsOnline%)</xsl:text>
                </xsl:when>
                <xsl:when test="$mainVersion/@state = 6">
                  <xsl:text> (%txtAnotherVersionHasExpired%)</xsl:text>
                </xsl:when>
              </xsl:choose>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:if>
      </xsl:when>

      <xsl:when test="$state = 4">

        <img src="images/icon_state_pending.gif" width="16" height="16" border="0" class="publish-status-icon">

          <xsl:attribute name="alt">
            <xsl:text>%txtContentState4% (%txtOnlineFrom% </xsl:text>
            <xsl:call-template name="formatdatetime">
              <xsl:with-param name="date" select="$publishfrom"/>
            </xsl:call-template>
            <xsl:text>)</xsl:text>
          </xsl:attribute>

          <xsl:attribute name="title">
            <xsl:text>%txtContentState4% (%txtOnlineFrom% </xsl:text>
            <xsl:call-template name="formatdatetime">
              <xsl:with-param name="date" select="$publishfrom"/>
            </xsl:call-template>
            <xsl:text>)</xsl:text>
          </xsl:attribute>

          <xsl:if test="$id">
            <xsl:attribute name="id">
              <xsl:value-of select="$id"/>
            </xsl:attribute>
          </xsl:if>
        </img>
        <xsl:text> </xsl:text>

        <xsl:if test="$icononly = 'false'">
          <xsl:text>%txtContentState4%</xsl:text>
          <xsl:if test="$isPlacedInMenuItemOrSection">
            <xsl:text> %txtAndPublishedToSite%</xsl:text>
          </xsl:if>
          <xsl:text> (%txtOnlineFrom% </xsl:text>
          <xsl:call-template name="formatdatetime">
            <xsl:with-param name="date" select="$publishfrom"/>
          </xsl:call-template>
          <xsl:text>)</xsl:text>
        </xsl:if>
      </xsl:when>

      <xsl:when test="$state = 5">
        <xsl:variable name="status-text">
          <xsl:choose>
            <xsl:when test="$has-draft = 'true'">
              <xsl:text>%txtContentState5withDraft%</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>%txtContentState5%</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <img width="16" height="16" border="0" class="publish-status-icon">
          <xsl:attribute name="src">
            <xsl:choose>
              <xsl:when test="$has-draft = 'true'">
                <xsl:text>images/icon_state_published_has_draft.gif</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>images/icon_state_published.gif</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>

          <xsl:attribute name="alt">
            <xsl:value-of select="$status-text"/>
          </xsl:attribute>

          <xsl:attribute name="title">
            <xsl:value-of select="$status-text"/>
          </xsl:attribute>

          <xsl:if test="$id">
            <xsl:attribute name="id">
              <xsl:value-of select="$id"/>
            </xsl:attribute>
          </xsl:if>
        </img>
        <xsl:text> </xsl:text>

        <xsl:if test="$icononly = 'false'">
          <xsl:value-of select="$status-text"/>

          <xsl:if test="$isPlacedInMenuItemOrSection">
            <xsl:text> %txtAndPublishedToSite%</xsl:text>
          </xsl:if>

          <xsl:text> (%txtOnlineSince% </xsl:text>
          <xsl:call-template name="formatdatetime">
            <xsl:with-param name="date" select="$publishfrom"/>
          </xsl:call-template>

          <xsl:if test="$publishto != ''">
            <xsl:text> - %txtExpiresOn% </xsl:text>
            <xsl:call-template name="formatdatetime">
              <xsl:with-param name="date" select="$publishto"/>
            </xsl:call-template>
          </xsl:if>

          <xsl:text>)</xsl:text>
        </xsl:if>
      </xsl:when>

      <xsl:when test="$state = 6">
        <img src="images/icon_state_expired.gif" width="16" height="16" border="0" class="publish-status-icon">
          <xsl:attribute name="alt">
            <xsl:text>%txtContentState6% (%txtOfflineSince% </xsl:text>
            <xsl:call-template name="formatdatetime">
              <xsl:with-param name="date" select="$publishto"/>
            </xsl:call-template>
            <xsl:text>)</xsl:text>
          </xsl:attribute>
          <xsl:attribute name="title">
            <xsl:text>%txtContentState6% (%txtOfflineSince% </xsl:text>
            <xsl:call-template name="formatdatetime">
              <xsl:with-param name="date" select="$publishto"/>
            </xsl:call-template>
            <xsl:text>)</xsl:text>
          </xsl:attribute>
          <xsl:if test="$id">
            <xsl:attribute name="id">
              <xsl:value-of select="$id"/>
            </xsl:attribute>
          </xsl:if>
        </img>
        <xsl:text> </xsl:text>

        <xsl:if test="$icononly = 'false'">
          <xsl:text>%txtContentState6%</xsl:text>
          <xsl:text> (%txtOfflineSince% </xsl:text>
          <xsl:call-template name="formatdatetime">
            <xsl:with-param name="date" select="$publishto"/>
          </xsl:call-template>
          <xsl:text>)</xsl:text>
        </xsl:if>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>