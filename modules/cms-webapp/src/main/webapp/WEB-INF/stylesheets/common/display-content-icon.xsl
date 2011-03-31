<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

  <xsl:template name="display-content-icon">
    <xsl:param name="content-node"/>
    <xsl:param name="title"/>
    <xsl:param name="content-type-name" select="''"/>
    <xsl:param name="contenthandler-class-name" select="''"/>
    <xsl:param name="is-home" select="''"/>
    <xsl:param name="is-link-to-menuitem" select="''"/>

    <xsl:variable name="file-suffix">
      <xsl:call-template name="getsuffix">
        <xsl:with-param name="fname" select="$title"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="is-image"
                  select="$file-suffix = 'jpg' or $file-suffix = 'jpeg' or $file-suffix = 'gif' or $file-suffix = 'png' or $file-suffix = 'tif' or $file-suffix = 'tiff' or $file-suffix = 'bmp'"/>

    <xsl:variable name="overlay-icon-path">
      <xsl:if test="$is-home != ''">
        <xsl:choose>
          <xsl:when test="$is-home = 'true'">
            <xsl:text>./images/fileicons/32/overlay_home.gif</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>./images/fileicons/32/overlay_arrow.gif</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:if>
    </xsl:variable>

    <xsl:variable name="section-overlay-icon-tooltip">
      <xsl:if test="$is-home != ''">
        <xsl:choose>
          <xsl:when test="$is-home = 'true'">
            <xsl:text>%tooltipSectionIsHomeforTheContent%</xsl:text>
          </xsl:when>
          <xsl:when test="$is-home = 'false' and $is-link-to-menuitem = 'true'">
            <xsl:text>%tooltipLinkToContentOnContentMenuItem%</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>%tooltipLinkToContentInAnotherSection%</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:if>
    </xsl:variable>

    <!-- Display icon -->
    <div class="content-icon">

      <table border="0" cellpadding="0" cellspacing="0" align="center">
        <tr>
          <td> 
            <div class="content-icon-container">

              <xsl:choose>
                <xsl:when test="contains($contenthandler-class-name, 'ContentFileHandlerServlet')">
                  <xsl:choose>
                    <xsl:when test="$is-image">
                      <img src="_image/{$content-node/@key}/binary/{$content-node/contentdata/binarydata/@key}?_filter=scalemax(64)" alt="%fldFile%" title="%fldFile%"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:call-template name="display-icon">
                        <xsl:with-param name="filename" select="$title"/>
                        <xsl:with-param name="format" select="32"/>
                        <xsl:with-param name="title" select="$content-type-name"/>
                      </xsl:call-template>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:when>
                <xsl:when test="contains($contenthandler-class-name, 'ContentEnhancedImageHandlerServlet')">
                  <xsl:variable name="label">
                    <xsl:choose>
                      <xsl:when test="$content-node/binaries/binary[@label = 'small']">small</xsl:when>
                      <xsl:when test="$content-node/binaries/binary[@label = 'medium']">medium</xsl:when>
                      <xsl:otherwise>source</xsl:otherwise>
                    </xsl:choose>
                  </xsl:variable>

                  <img src="_image/{$content-node/@key}/label/{$label}?_filter=scalemax(64)" alt="{$content-type-name}" title="{$content-type-name}"/>
                </xsl:when>

                <xsl:when test="contains($contenthandler-class-name, 'ContentNewsletterHandlerServlet')">
                  <img src="images/fileicons/32/icon_newsletter.gif" alt="{$content-type-name}" title="{$content-type-name}"/>
                </xsl:when>

                <xsl:when test="contains($contenthandler-class-name, 'ContentPollHandlerServlet')">
                  <img src="images/fileicons/32/icon_poll.gif" alt="{$content-type-name}" title="%iconPoll%"/>
                </xsl:when>

                <xsl:when test="contains($contenthandler-class-name, 'ContentOrderHandlerServlet')">
                  <img src="images/fileicons/32/icon_shoporder.gif" alt="{$content-type-name}" title="{$content-type-name}"/>
                </xsl:when>

                <xsl:when test="contains($contenthandler-class-name, 'ContentFormHandlerServlet')">
                  <img src="images/fileicons/32/icon_formbuilder.gif" alt="{$content-type-name}" title="{$content-type-name}"/>
                </xsl:when>

                <xsl:otherwise>
                  <img src="images/fileicons/32/icon_content.gif" alt="{$content-type-name}" title="{$content-type-name}"/>
                </xsl:otherwise>
              </xsl:choose>

              <xsl:if test="$overlay-icon-path !=''">
                <img src="{$overlay-icon-path}" alt="{$section-overlay-icon-tooltip}" title="{$section-overlay-icon-tooltip}" class="overlay-icon"/>
              </xsl:if>
            </div>

          </td>
        </tr>
      </table>
    </div>
  </xsl:template>
</xsl:stylesheet>
