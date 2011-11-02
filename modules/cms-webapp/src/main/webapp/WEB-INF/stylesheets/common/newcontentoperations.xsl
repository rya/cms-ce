<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <xsl:include href="../handlerconfigs/default.xsl"/>

  <!-- map of allowed content-types in page-templates in sites by content-type-key -->
  <xsl:key name="key-page-template-content-type" match="/*/pagetemplates-in-sites/pagetemplates-in-site/pagetemplate/contenttypes/contenttype" use="@key"/>

  <xsl:template name="contentoperations">
    <xsl:param name="key"/>
    <xsl:param name="contentelem"/>
    <xsl:param name="contenttypeelem"/>
    <xsl:param name="ischild"/>
    <xsl:param name="includemove" select="true()"/>
    <xsl:param name="includepublish" select="true()"/>
    <xsl:param name="includecopy" select="false()"/>
    <xsl:param name="includepreview" select="true()"/>
    <xsl:param name="includeremove" select="true()"/>
    <xsl:param name="includeremoveversion" select="false()"/>
    <xsl:param name="includeparams" select="''"/>
    <xsl:param name="usereferer" select="false()"/>
    <xsl:param name="custom_operation" select="''"/>
    <xsl:param name="custom_tooltip" select="''"/>
    <xsl:param name="custom_image" select="''"/>
    <xsl:param name="custom_disabled" select="'false'"/>
    <xsl:param name="unitkey"/>
    <xsl:param name="versionkey"/>

    <xsl:variable name="cat" select="$contentelem/categoryname/@key"/>
    <xsl:variable name="contenttypekey" select="$contentelem/@contenttypekey"/>
    <xsl:variable name="page" select="number($contenttypekey) + 999"/>

    <xsl:variable name="categorycreate"
                  select="not($contentelem/accessrights/userright) or $contentelem/accessrights/userright/@categorycreate = 'true'"/>
    <xsl:variable name="categorypublish"
                  select="not($contentelem/accessrights/userright) or $contentelem/accessrights/userright/@categorypublish = 'true'"/>
    <xsl:variable name="contentupdate"
                  select="not($contentelem/accessrights/userright) or $contentelem/accessrights/userright/@update = 'true'"/>
    <xsl:variable name="contentdelete"
                  select="not($contentelem/accessrights/userright) or $contentelem/accessrights/userright/@delete = 'true'"/>

    <table border="0" cellspacing="0" cellpadding="0">

      <tr>
        <!-- handler specific operations -->
        <xsl:apply-templates select="$contenttypeelem/@handler" mode="operations">
          <xsl:with-param name="contentelem" select="$contentelem"/>
        </xsl:apply-templates>

        <!-- custom operation -->
        <xsl:if test="$custom_operation != '' and $custom_tooltip != '' and $custom_image != ''">
          <td align="center" class="operationscell">
            <xsl:call-template name="button">
              <xsl:with-param name="style" select="'flat'"/>
              <xsl:with-param name="type" select="'link'"/>
              <xsl:with-param name="name">
                <xsl:text>custom</xsl:text>
                <xsl:value-of select="$key"/>
              </xsl:with-param>
              <xsl:with-param name="image" select="$custom_image"/>
              <xsl:with-param name="disabled" select="$custom_disabled"/>
              <xsl:with-param name="tooltip" select="$custom_tooltip"/>
              <xsl:with-param name="href">
                <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
                <xsl:text>&amp;op=</xsl:text><xsl:value-of select="$custom_operation"/>
                <xsl:text>&amp;key=</xsl:text><xsl:value-of select="$key"/>
                <xsl:if test="$versionkey">
                  <xsl:text>&amp;versionkey=</xsl:text><xsl:value-of select="$versionkey"/>
                </xsl:if>
                <xsl:text>&amp;cat=</xsl:text><xsl:value-of select="$cat"/>
                <xsl:choose>
                  <xsl:when test="$selectedunitkey != ''">
                    <xsl:text>&amp;selectedunitkey=</xsl:text>
                    <xsl:value-of select="$selectedunitkey"/>
                  </xsl:when>
                  <xsl:when test="$unitkey != ''">
                    <xsl:text>&amp;selectedunitkey=</xsl:text>
                    <xsl:value-of select="$unitkey"/>
                  </xsl:when>
                </xsl:choose>
                <xsl:value-of select="$includeparams"/>
              </xsl:with-param>
            </xsl:call-template>
          </td>
        </xsl:if>

        <xsl:if test="$includepreview = 'true'">
          <xsl:variable name="has-page-template" select="key('key-page-template-content-type', $contenttypekey)"/>

          <xsl:variable name="tooltip">
            <xsl:choose>
              <xsl:when test="not($has-page-template)">
                <xsl:value-of select="'%altContentPreviewNotSupportedByAnySite%'"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="'%altContentPreview%'"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>

          <td align="center">
            <xsl:call-template name="button">
              <xsl:with-param name="style" select="'flat'"/>
              <xsl:with-param name="type" select="'link'"/>
              <xsl:with-param name="image" select="'images/icon_preview.gif'"/>
              <xsl:with-param name="disabled" select="not($has-page-template)"/>
              <xsl:with-param name="tooltip" select="$tooltip"/>
              <xsl:with-param name="href">
                <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
                <xsl:text>&amp;op=preview&amp;subop=frameset&amp;contentkey=</xsl:text>
                <xsl:value-of select="$key"/>
                <xsl:if test="$versionkey">
                  <xsl:text>&amp;versionkey=</xsl:text>
                  <xsl:value-of select="$versionkey"/>
                </xsl:if>
                <xsl:text>&amp;logread=true</xsl:text>
              </xsl:with-param>
              <xsl:with-param name="target" select="'_blank'"/>
            </xsl:call-template>
          </td>
        </xsl:if>

        <td align="center" class="operationscell">
          <xsl:call-template name="button">
            <xsl:with-param name="style" select="'flat'"/>
            <xsl:with-param name="type" select="'link'"/>
            <xsl:with-param name="id">
              <xsl:text>operation_edit_</xsl:text><xsl:value-of select="$key"/><xsl:value-of select="$versionkey"/>
            </xsl:with-param>
            <xsl:with-param name="name">
              <xsl:text>edit</xsl:text><xsl:value-of select="$key"/>
            </xsl:with-param>
            <xsl:with-param name="image" select="'images/icon_edit.gif'"/>
            <xsl:with-param name="disabled" select="'false'"/>
            <xsl:with-param name="tooltip">
              <xsl:choose>
                <xsl:when test="$contentelem/@state = 0">%altContentEdit%</xsl:when>
                <xsl:otherwise>%msgClickToOpen%</xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="href">
              <xsl:text>adminpage?page=</xsl:text>
              <xsl:value-of select="$page"/>
              <xsl:text>&amp;op=form</xsl:text>
              <xsl:text>&amp;key=</xsl:text><xsl:value-of select="$key"/>
              <xsl:if test="$versionkey">
                <xsl:text>&amp;versionkey=</xsl:text><xsl:value-of select="$versionkey"/>
              </xsl:if>
              <xsl:text>&amp;cat=</xsl:text>
              <xsl:value-of select="$cat"/>
              <xsl:choose>
                <xsl:when test="$selectedunitkey != ''">
                  <xsl:text>&amp;selectedunitkey=</xsl:text>
                  <xsl:value-of select="$selectedunitkey"/>
                </xsl:when>
                <xsl:when test="$unitkey != ''">
                  <xsl:text>&amp;selectedunitkey=</xsl:text>
                  <xsl:value-of select="$unitkey"/>
                </xsl:when>
              </xsl:choose>
              <xsl:if test="$usereferer">
                <xsl:text>&amp;useredirect=referer</xsl:text>
              </xsl:if>
              <xsl:value-of select="$includeparams"/>
              <xsl:text>&amp;logread=true</xsl:text>
            </xsl:with-param>
          </xsl:call-template>
        </td>

        <xsl:if test="$includepublish = 'true'">
          <xsl:variable name="sectionpublish_tooltip">
            <xsl:text>%txtApproveAndPublish%</xsl:text>
          </xsl:variable>

          <td align="center" class="operationscell">
            <xsl:call-template name="button">
              <xsl:with-param name="style" select="'flat'"/>
              <xsl:with-param name="type" select="'link'"/>
              <xsl:with-param name="name">
                <xsl:text>sectionpublish</xsl:text><xsl:value-of select="$key"/>
              </xsl:with-param>
              <xsl:with-param name="image" select="'images/icon_content_publish.gif'"/>
              <xsl:with-param name="disabled">
                <xsl:value-of
                    select="$contentelem/@status = 3 or not($contentupdate or $categorypublish) or (not($categorypublish) and $contentelem/@status = 2)"/>
              </xsl:with-param>
              <xsl:with-param name="tooltip" select="$sectionpublish_tooltip"/>
              <xsl:with-param name="href">
                <xsl:text>adminpage?page=950&amp;op=wizard&amp;name=publish&amp;</xsl:text>
                <xsl:choose>
                  <xsl:when test="$versionkey">
                    <xsl:text>versionkey=</xsl:text>
                    <xsl:value-of select="$versionkey"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>contentkey=</xsl:text>
                    <xsl:value-of select="$key"/>
                  </xsl:otherwise>
                </xsl:choose>
                <xsl:text>&amp;cat=</xsl:text>
                <xsl:value-of select="$cat"/>
                <xsl:choose>
                  <xsl:when test="$selectedunitkey != ''">
                    <xsl:text>&amp;selectedunitkey=</xsl:text>
                    <xsl:value-of select="$selectedunitkey"/>
                  </xsl:when>
                  <xsl:when test="$unitkey != ''">
                    <xsl:text>&amp;selectedunitkey=</xsl:text>
                    <xsl:value-of select="$unitkey"/>
                  </xsl:when>
                </xsl:choose>
                <xsl:if test="$includeparams != ''">
                  <xsl:value-of select="$includeparams"/>
                </xsl:if>
              </xsl:with-param>

            </xsl:call-template>
          </td>
        </xsl:if>

        <xsl:variable name="copy_tooltip">
          <xsl:choose>
            <xsl:when test="not($categorycreate)">
              <xsl:text>%msgNoRightsToDoThis%</xsl:text>
            </xsl:when>
            <xsl:otherwise>%altContentCopy%</xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <xsl:variable name="disabled">
          <xsl:choose>
            <xsl:when test="$contenttypeelem/@handler = 'com.enonic.vertical.adminweb.handlers.ContentPollHandlerServlet'">true</xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="not($categorycreate)"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <xsl:if test="$includecopy = 'true'">
          <td align="center" class="operationscell">
            <xsl:call-template name="button">
              <xsl:with-param name="style" select="'flat'"/>
              <xsl:with-param name="type" select="'link'"/>
              <xsl:with-param name="name">
                <xsl:text>copy</xsl:text><xsl:value-of select="$key"/>
              </xsl:with-param>
              <xsl:with-param name="image" select="'images/icon_copy.gif'"/>
              <xsl:with-param name="disabled" select="$disabled"/>
              <xsl:with-param name="tooltip" select="$copy_tooltip"/>
              <xsl:with-param name="href">
                <xsl:text>adminpage?page=</xsl:text>
                <xsl:value-of select="$page"/>
                <xsl:text>&amp;op=copy&amp;key=</xsl:text><xsl:value-of select="$key"/>
                <xsl:text>&amp;cat=</xsl:text>
                <xsl:value-of select="$cat"/>
                <xsl:choose>
                  <xsl:when test="$selectedunitkey != ''">
                    <xsl:text>&amp;selectedunitkey=</xsl:text>
                    <xsl:value-of select="$selectedunitkey"/>
                  </xsl:when>
                  <xsl:when test="$unitkey != ''">
                    <xsl:text>&amp;selectedunitkey=</xsl:text>
                    <xsl:value-of select="$unitkey"/>
                  </xsl:when>
                </xsl:choose>
                <xsl:if test="$includeparams != ''">
                  <xsl:value-of select="$includeparams"/>
                </xsl:if>
              </xsl:with-param>
            </xsl:call-template>
          </td>
        </xsl:if>

        <xsl:variable name="move_disabled">
          <xsl:choose>
            <xsl:when test="($contentdelete and $contentelem/@state &lt; 2) or $categorypublish">
              <xsl:text>false</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>true</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <xsl:if test="$includemove">
          <td align="center" class="operationscell">
            <xsl:call-template name="button">
              <xsl:with-param name="style" select="'flat'"/>
              <xsl:with-param name="type" select="'link'"/>
              <xsl:with-param name="name">
                <xsl:text>move</xsl:text><xsl:value-of select="$key"/>
              </xsl:with-param>
              <xsl:with-param name="image" select="'images/icon_content_move.gif'"/>
              <xsl:with-param name="disabled" select="$move_disabled"/>
              <xsl:with-param name="tooltip" select="'%altContentMove%'"/>
              <xsl:with-param name="href">
                <xsl:text>javascript: moveContent(</xsl:text>
                <xsl:value-of select="$key"/>
                <xsl:text>, </xsl:text>
                <xsl:value-of select="$contenttypekey"/>
                <xsl:text>);</xsl:text>
              </xsl:with-param>
            </xsl:call-template>
          </td>
        </xsl:if>

        <xsl:variable name="delete_disabled">
          <xsl:choose>
            <xsl:when test="($contentdelete and not($contentelem/@state = 4) and not($contentelem/@state = 5)) or $categorypublish">
              <xsl:text>false</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>true</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <xsl:if test="$includeremove">
          <td align="center" class="operationscell">
            <xsl:variable name="deletemsg">
              <xsl:choose>
                <xsl:when test="$ischild = 'true'">
                  <xsl:text>%alertDeleteContentWithParents%</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>%alertDeleteContent%</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <xsl:call-template name="button">
              <xsl:with-param name="style" select="'flat'"/>
              <xsl:with-param name="type" select="'link'"/>
              <xsl:with-param name="name">
                <xsl:text>del</xsl:text><xsl:value-of select="$key"/>
              </xsl:with-param>
              <xsl:with-param name="image" select="'images/icon_delete.gif'"/>
              <xsl:with-param name="disabled" select="$delete_disabled"/>
              <xsl:with-param name="tooltip" select="'%altContentDelete%'"/>
              <xsl:with-param name="condition">
                <xsl:text>removeContent(</xsl:text>
                <xsl:value-of select="$key"/>
                <xsl:text>,</xsl:text>
                <xsl:value-of select="$page"/>
                <xsl:text>,</xsl:text>
                <xsl:value-of select="$cat"/>
                <xsl:text>)</xsl:text>
              </xsl:with-param>
            </xsl:call-template>
          </td>
        </xsl:if>

        <xsl:if test="$includeremoveversion">
          <td align="center" class="operationscell">
            <xsl:choose>
              <xsl:when test="@current = 'true'">
                <xsl:call-template name="button">
                  <xsl:with-param name="style" select="'flat'"/>
                  <xsl:with-param name="type" select="'link'"/>
                  <xsl:with-param name="name">
                    <xsl:text>del</xsl:text><xsl:value-of select="$key"/>
                  </xsl:with-param>
                  <xsl:with-param name="image" select="'images/icon_delete.gif'"/>
                  <xsl:with-param name="disabled" select="$delete_disabled"/>
                  <xsl:with-param name="tooltip" select="'%altContentVersionDelete%'"/>
                  <xsl:with-param name="condition">
                    <xsl:text>confirm('%alertDeleteMainVersion%')</xsl:text>
                  </xsl:with-param>
                  <xsl:with-param name="href">
                    <xsl:text>adminpage?page=</xsl:text>
                    <xsl:value-of select="$page"/>
                    <xsl:text>&amp;op=remove&amp;key=</xsl:text><xsl:value-of select="$key"/>
                    <xsl:text>&amp;cat=</xsl:text>
                    <xsl:value-of select="$cat"/>
                    <xsl:choose>
                      <xsl:when test="$selectedunitkey != ''">
                        <xsl:text>&amp;selectedunitkey=</xsl:text>
                        <xsl:value-of select="$selectedunitkey"/>
                      </xsl:when>
                      <xsl:when test="$unitkey != ''">
                        <xsl:text>&amp;selectedunitkey=</xsl:text>
                        <xsl:value-of select="$unitkey"/>
                      </xsl:when>
                    </xsl:choose>
                    <xsl:if test="$includeparams != ''">
                      <xsl:value-of select="$includeparams"/>
                    </xsl:if>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:when>
              <xsl:otherwise>
                <xsl:call-template name="button">
                  <xsl:with-param name="style" select="'flat'"/>
                  <xsl:with-param name="type" select="'link'"/>
                  <xsl:with-param name="name">
                    <xsl:text>delversion</xsl:text><xsl:value-of select="$key"/>
                  </xsl:with-param>
                  <xsl:with-param name="image" select="'images/icon_delete.gif'"/>
                  <xsl:with-param name="tooltip" select="'%altContentVersionDelete%'"/>
                  <xsl:with-param name="condition">
                    <xsl:text>confirm('%alertDeleteVersion%')</xsl:text>
                  </xsl:with-param>
                  <xsl:with-param name="href">
                    <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
                    <xsl:text>&amp;op=delete_version</xsl:text>
                    <xsl:text>&amp;closeonsuccess=true</xsl:text>
                    <xsl:text>&amp;cat=</xsl:text><xsl:value-of select="$cat"/>
                    <xsl:text>&amp;versionkey=</xsl:text><xsl:value-of select="$versionkey"/>
                    <xsl:if test="$includeparams != ''">
                      <xsl:value-of select="$includeparams"/>
                    </xsl:if>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:otherwise>
            </xsl:choose>
          </td>
        </xsl:if>
      </tr>
    </table>
  </xsl:template>

</xsl:stylesheet>
