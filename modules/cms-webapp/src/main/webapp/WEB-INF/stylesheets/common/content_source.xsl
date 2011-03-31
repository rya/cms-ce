<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
    ]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:template name="content_source">
    <xsl:if test="/contents/source">

      <xsl:variable name="source" select="/contents/source"/>

      <div class="tab-page" id="tab-page-source">
        <span class="tab">%blockSource%</span>

        <script type="text/javascript" language="JavaScript">
          tabPane1.addTabPage( document.getElementById( "tab-page-source" ) );
        </script>

        <fieldset>
          <legend>&nbsp;%blockContentXml%&nbsp;</legend>
          <pre class="content-source">
            <xsl:value-of select="translate($source/data, '&#xA;','')"/>
          </pre>
        </fieldset>

        <xsl:if test="count($source/related-children/content) != 0">
          <fieldset>
            <legend>&nbsp;%blockRelatedChildren%&nbsp;</legend>

            <script type="text/javascript">
              function callback_content_source_related_children()
              {
                /* Needed for the content popup */
              
                return;
              }

            </script>

            <table class="full">
              <thead>
                <tr>
                  <th class="title left">%headTitle%</th>
                  <th class="left">%headPath%</th>
                  <th class="center">%headDeleted%</th>
                  <th class="left">&nbsp;</th>
                </tr>
              </thead>
              <tbody>
                <xsl:for-each select="$source/related-children/content">

                  <tr>
                    <td>
                      <xsl:value-of select="title"/>
                    </td>
                    <td style="padding-right: 8px">
                      <xsl:value-of select="repositorypath"/>
                    </td>
                    <td class="center">

                      <xsl:choose>
                        <xsl:when test="@deleted = 'true'">
                          <img src="images/icon_check.gif" alt=""/>
                        </xsl:when>
                        <xsl:otherwise>
                          <img src="images/icon_unchecked.gif" alt=""/>
                        </xsl:otherwise>
                      </xsl:choose>

                    </td>
                    <td class="right">

                      <xsl:variable name="disabled">
                        <xsl:choose>
                          <xsl:when test="/contents/relatedcontents/content[@key = current()/@key]/userright/@update = 'true' and /contents/relatedcontents/content[@key = current()/@key]/userright/@read = 'true'">false</xsl:when>
                          <xsl:otherwise>true</xsl:otherwise>
                        </xsl:choose>
                      </xsl:variable>
                      <xsl:variable name="tooltip">
                        <xsl:choose>
                          <xsl:when test="$disabled = 'true'">%cmdNoAccess%</xsl:when>
                          <xsl:otherwise>%cmdEdit%</xsl:otherwise>
                        </xsl:choose>
                      </xsl:variable>

                      <xsl:call-template name="button">
                        <xsl:with-param name="name">editcontent</xsl:with-param>
                        <xsl:with-param name="image" select="'images/icon_edit_small.gif'"/>
                        <xsl:with-param name="tooltip" select="$tooltip"/>
                        <xsl:with-param name="disabled" select="$disabled"/>
                        <xsl:with-param name="onclick">
                          <xsl:text>OpenEditContentPopup(</xsl:text>
                          <xsl:value-of select="@key"/>
                          <xsl:text>, -1, 'sourceRelatedChildren', getObjectIndex(this), 'callback_content_source_related_children');</xsl:text>
                        </xsl:with-param>
                      </xsl:call-template>
                    </td>
                  </tr>
                </xsl:for-each>
              </tbody>
            </table>
          </fieldset>
        </xsl:if>

        <xsl:if test="$source/indexes/index">
          <fieldset>
            <legend>&nbsp;%blockContentIndexedValues%&nbsp;</legend>
            <table class="full">
              <tr>
                <th class="left title">Name</th>
                <th class="left">Value</th>
              </tr>
              <tr>
                <th class="left" colspan="2">Standard</th>
              </tr>
              <xsl:for-each select="$source/indexes/index[@internal = 'true']">
                <xsl:sort select="@name"/>
                <tr>
                  <td valign="top">
                    <xsl:value-of select="@name"/>
                  </td>
                  <td valign="top">
                    <xsl:for-each select="value">
                      <xsl:value-of select="."/>
                      <br/>
                    </xsl:for-each>
                  </td>
                </tr>
              </xsl:for-each>
              <tr>
                <td class="left" colspan="2"><br/></td>
              </tr>
              <tr>
                <th class="left" colspan="2">Custom</th>
              </tr>
              <xsl:for-each select="$source/indexes/index[@internal = 'false']">
                <xsl:sort select="@name"/>
                <tr>
                  <td valign="top">
                    <xsl:value-of select="@name"/>
                  </td>
                  <td valign="top">
                    <xsl:for-each select="value">
                      <xsl:value-of select="."/>
                      <br/>
                    </xsl:for-each>
                  </td>
                </tr>
              </xsl:for-each>
            </table>
          </fieldset>
        </xsl:if>
      </div>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
