<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:template name="sectionoperations">
    	<xsl:param name="key"/>
    	<xsl:param name="versionkey"/>
    	<xsl:param name="menukey"/>
    	<xsl:param name="previewmenukey"/>
        <xsl:param name="previewnotavailable" select="false()"/>
    	<xsl:param name="menuitemkey"/>
    	<xsl:param name="sec"/>
    	<xsl:param name="cat"/>
    	<xsl:param name="page"/>
    	<xsl:param name="contentpage"/>
    	<xsl:param name="approved"/>
    	<xsl:param name="addright" select="false()"/>
    	<xsl:param name="approveright"/>
    	<xsl:param name="publishright"/>
    	<xsl:param name="ordered"/>
    	<xsl:param name="unitkey"/>
    	<xsl:param name="usereferer" select="'true'"/>
    	<xsl:param name="reordered" select="'false'"/>
    	<xsl:param name="toplevel" select="''"/>
    	<xsl:param name="state"/>

        <xsl:variable name="tooltip">
          <xsl:choose>
            <xsl:when test="not($menuitemkey) and $previewnotavailable">
              <xsl:value-of select="'%altContentPreviewNotAvailable%'"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="'%altContentPreview%'"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

      <table border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td>
            <!-- Preview -->
            <xsl:call-template name="button">
            <xsl:with-param name="style" select="'flat'"/>
            <xsl:with-param name="type" select="'link'"/>
            <xsl:with-param name="image" select="'images/icon_preview.gif'"/>
            <xsl:with-param name="tooltip" select="$tooltip"/>
            <xsl:with-param name="disabled" select="not($menuitemkey) or $previewnotavailable"/>
            <xsl:with-param name="href">
              <xsl:text>adminpage?page=950</xsl:text>
              <xsl:text>&amp;op=preview&amp;contentkey=</xsl:text>
              <xsl:value-of select="$key"/>
              <xsl:text>&amp;menukey=</xsl:text>
              <xsl:value-of select="$previewmenukey"/>
              <xsl:text>&amp;menuitemkey=</xsl:text>
              <xsl:value-of select="$menuitemkey"/>
              <xsl:text>&amp;logread=true</xsl:text>
            </xsl:with-param>
            <xsl:with-param name="target" select="'_blank'"/>
          </xsl:call-template>
          <xsl:text>&nbsp;</xsl:text>
        </td>
        <td>

          <!-- Approve / unapprove -->
          <xsl:choose>
            <xsl:when test="not($approved)">
              <!--xsl:variable name="condition">
                <xsl:if test="not($ordered)">
                  <xsl:text>confirm('%msgConfirmApproveSectionContent%')</xsl:text>
                </xsl:if>
              </xsl:variable-->

              <xsl:variable name="tooltip-for-approve-button">
                <xsl:choose>
                  <xsl:when test="$approveright">
                    <xsl:text>%altContentApprove%</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>%altContentNoPublishRight%</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:variable>

              <xsl:call-template name="button">
                <xsl:with-param name="style" select="'flat'"/>
                <xsl:with-param name="type" select="'link'"/>
                <xsl:with-param name="image" select="'images/icon_content_approve.gif'"/>
                <xsl:with-param name="disabled" select="not($approveright)"/>
                <xsl:with-param name="tooltip" select="$tooltip-for-approve-button"/>
                <xsl:with-param name="id">
                  <xsl:text>operation_approve_</xsl:text>
                  <xsl:value-of select="$key"/>
                </xsl:with-param>
                <!--xsl:with-param name="condition" select="$condition"/-->
                <xsl:with-param name="href">
                  <xsl:text>adminpage?page=</xsl:text>
                  <xsl:value-of select="$page"/>
                  <xsl:text>&amp;op=approve</xsl:text>
                  <xsl:text>&amp;sec=</xsl:text>
                  <xsl:value-of select="$sec"/>
                  <xsl:text>&amp;menuitemkey=</xsl:text>
                  <xsl:value-of select="$menuitemkey"/>
                  <xsl:text>&amp;key=</xsl:text>
                  <xsl:value-of select="$key"/>
                  <xsl:text>&amp;menukey=</xsl:text>
                  <xsl:value-of select="$menukey"/>
                  <xsl:if test="$toplevel = 'true'">
                    <xsl:text>&amp;toplevel=true</xsl:text>
                  </xsl:if>
                  <xsl:if test="$usereferer">
                    <xsl:text>&amp;useredirect=referer</xsl:text>
                  </xsl:if>
                </xsl:with-param>
              </xsl:call-template>
              <xsl:text>&nbsp;</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <!--xsl:variable name="condition">
                <xsl:if test="not($ordered)">
                  <xsl:text>confirm('%msgConfirmUnApproveSectionContent%')</xsl:text>
                </xsl:if>
              </xsl:variable-->

              <xsl:variable name="tooltip-for-un-publish-buttons">
                <xsl:choose>
                  <xsl:when test="$approveright">
                    <xsl:text>%altContentUnapprove%</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>%altContentNoPublishRight%</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:variable>

              <xsl:call-template name="button">
                <xsl:with-param name="style" select="'flat'"/>
                <xsl:with-param name="type" select="'link'"/>
                <xsl:with-param name="image" select="'images/icon_content_unapprove.gif'"/>
                <xsl:with-param name="disabled" select="not($approveright)"/>
                <xsl:with-param name="tooltip" select="$tooltip-for-un-publish-buttons"/>
                <!--xsl:with-param name="condition" select="$condition"/-->
                <xsl:with-param name="href">
                  <xsl:text>adminpage?page=</xsl:text>
                  <xsl:value-of select="$page"/>
                  <xsl:text>&amp;op=unapprove</xsl:text>
                  <xsl:text>&amp;sec=</xsl:text>
                  <xsl:value-of select="$sec"/>
                  <xsl:text>&amp;key=</xsl:text>
                  <xsl:value-of select="$key"/>
                  <xsl:text>&amp;menukey=</xsl:text>
                  <xsl:value-of select="$menukey"/>
                  <xsl:if test="$toplevel = 'true'">
                    <xsl:text>&amp;toplevel=true</xsl:text>
                  </xsl:if>
                  <xsl:if test="$usereferer">
                    <xsl:text>&amp;useredirect=referer</xsl:text>
                  </xsl:if>
                </xsl:with-param>
              </xsl:call-template>
              <xsl:text>&nbsp;</xsl:text>
            </xsl:otherwise>
          </xsl:choose>

        </td>
        <td>

          <!-- Edit -->
          <xsl:call-template name="button">
            <xsl:with-param name="style" select="'flat'"/>
            <xsl:with-param name="type" select="'link'"/>
            <xsl:with-param name="id">
              <xsl:text>operation_edit_</xsl:text>
              <xsl:value-of select="$key"/>
              <xsl:if test="$versionkey != ''">
                <xsl:value-of select="$versionkey"/>
              </xsl:if>
            </xsl:with-param>
            <xsl:with-param name="name">
              <xsl:text>edit</xsl:text><xsl:value-of select="$key"/>
            </xsl:with-param>
            <xsl:with-param name="image" select="'images/icon_edit.gif'"/>
            <xsl:with-param name="disabled" select="'false'"/>
            <xsl:with-param name="tooltip">
              <xsl:choose>
                <xsl:when test="$state = 0">%altContentEdit%</xsl:when>
                <xsl:otherwise>%msgClickToOpen%</xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="href">
              <xsl:text>adminpage?page=</xsl:text>
              <xsl:value-of select="$contentpage"/>
              <xsl:text>&amp;op=form&amp;key=</xsl:text><xsl:value-of select="$key"/>
              <xsl:text>&amp;cat=</xsl:text>
              <xsl:value-of select="$cat"/>
              <xsl:text>&amp;menukey=</xsl:text>
              <xsl:value-of select="$menukey"/>
              <xsl:text>&amp;selectedunitkey=</xsl:text>
              <xsl:value-of select="$unitkey"/>
              <xsl:text>&amp;logread=true</xsl:text>
              <xsl:if test="$usereferer">
                <xsl:text>&amp;useredirect=referer</xsl:text>
              </xsl:if>
            </xsl:with-param>
          </xsl:call-template>

        </td>
        <td style="padding: 0 0 2px 3px">

          <xsl:variable name="disable-remove-button" select="not($addright) or ($approved and not($approveright))"/>

          <xsl:variable name="tooltip-for-remove-button">
            <xsl:choose>
              <xsl:when test="$disable-remove-button">
                <xsl:text>%altContentCanNotRemoveFromSection%</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>%altContentRemoveFromSection%</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          
          <!-- Remove from section -->
          <xsl:call-template name="button">
            <xsl:with-param name="style" select="'flat'"/>
            <xsl:with-param name="type" select="'link'"/>
            <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
            <xsl:with-param name="disabled" select="$disable-remove-button"/>
            <xsl:with-param name="tooltip" select="$tooltip-for-remove-button"/>
            <xsl:with-param name="condition">
              <xsl:text>confirm('%msgConfirmRemoveFromSection%')</xsl:text>
            </xsl:with-param>
            <xsl:with-param name="href">
              <xsl:text>adminpage?page=</xsl:text>
              <xsl:value-of select="$page"/>
              <xsl:text>&amp;op=removecontent&amp;key=</xsl:text>
              <xsl:value-of select="$key"/>
              <xsl:text>&amp;sec=</xsl:text>
              <xsl:value-of select="$sec"/>
              <xsl:text>&amp;menukey=</xsl:text>
              <xsl:value-of select="$menukey"/>
              <xsl:text>&amp;menuitemkey=</xsl:text>
              <xsl:value-of select="$menuitemkey"/>
              <xsl:if test="$reordered = 'true'">
                <xsl:text>&amp;reordered=true</xsl:text>
              </xsl:if>
              <xsl:if test="$toplevel = 'true'">
                <xsl:text>&amp;toplevel=true</xsl:text>
              </xsl:if>
              <xsl:if test="$usereferer">
                <xsl:text>&amp;useredirect=referer</xsl:text>
              </xsl:if>
            </xsl:with-param>
          </xsl:call-template>

        </td>
      </tr>
    </table>
  </xsl:template>
</xsl:stylesheet>
