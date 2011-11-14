<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="html" />

  <xsl:include href="tree/displaytree.xsl"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="handlerconfigs/default.xsl"/>

  <xsl:param name="step"/>
  <xsl:param name="contenttypekey"/>
  <xsl:param name="source"/>

  <xsl:param name="fieldrow"/>
  <xsl:param name="fieldname"/>

  <xsl:param name="page"/>
  <xsl:param name="cat"/>

  <xsl:variable name="relatedContentParamsEncoded">
    <xsl:if test="$subop">
      <xsl:text>%26subop=</xsl:text>
      <xsl:value-of select="$subop"/>
    </xsl:if>
    <xsl:if test="$fieldrow">
      <xsl:text>%26fieldrow=</xsl:text>
      <xsl:value-of select="$fieldrow"/>
    </xsl:if>
    <xsl:if test="$fieldname">
      <xsl:text>%26fieldname=</xsl:text>
      <xsl:value-of select="$fieldname"/>
    </xsl:if>
  </xsl:variable>

  <xsl:variable name="relatedContentParams">
    <xsl:if test="$subop">
      <xsl:text>&amp;subop=</xsl:text>
      <xsl:value-of select="$subop"/>
    </xsl:if>
    <xsl:if test="$fieldrow">
      <xsl:text>&amp;fieldrow=</xsl:text>
      <xsl:value-of select="$fieldrow"/>
    </xsl:if>
    <xsl:if test="$fieldname">
      <xsl:text>&amp;fieldname=</xsl:text>
      <xsl:value-of select="$fieldname"/>
    </xsl:if>
  </xsl:variable>

  <xsl:variable name="referer">
    <xsl:choose>
      <xsl:when test="$source = 'mypage'">
        <xsl:text>adminpage?page=960&amp;op=page</xsl:text>
        <xsl:if test="$relatedContentParams">
          <xsl:value-of select="$relatedContentParams"/>
        </xsl:if>
      </xsl:when>
      <xsl:when test="$source = 'archives'">
        <xsl:text>adminpage?page=600&amp;op=browse</xsl:text>
        <xsl:if test="$relatedContentParams">
          <xsl:value-of select="$relatedContentParams"/>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise/>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="refererUrlEncoded">
    <xsl:choose>
      <xsl:when test="$source = 'mypage'">
        <xsl:text>adminpage?page=960%26op=page</xsl:text>
        <xsl:if test="$relatedContentParamsEncoded">
          <xsl:value-of select="$relatedContentParamsEncoded"/>
        </xsl:if>
      </xsl:when>
      <xsl:when test="$source = 'archives'">
        <xsl:text>adminpage?page=600%26op=browse</xsl:text>
        <xsl:if test="$relatedContentParamsEncoded">
          <xsl:value-of select="$relatedContentParamsEncoded"/>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise/>
    </xsl:choose>
  </xsl:variable>

  <xsl:template match="/">
    <html>
      <head>
        <script type="text/javascript" src="javascript/tabpane.js"/>
        <script type="text/javascript" src="javascript/admin.js"/>
        <script type="text/javascript" src="javascript/menu.js"/>
        <script type="text/javascript" language="JavaScript">
          var branchOpen = new Array();
					function loadTopCategory(topCategoryKey) {
					    var location = "adminpage?page=960&amp;op=createcontentwizard_step2&amp;contenttypekey=<xsl:value-of select="$contenttypekey"/>&amp;topcategorykey="+topCategoryKey+"&amp;source=<xsl:value-of select="$source"/>";
          <xsl:if test="$subop">
            location = location + "&amp;subop=<xsl:value-of select="$subop"/>";
          </xsl:if>
          <xsl:if test="$fieldname">
            location = location + "&amp;fieldname=<xsl:value-of select="$fieldname"/>";
          </xsl:if>
          <xsl:if test="$fieldrow">
            location = location + "&amp;fieldrow=<xsl:value-of select="$fieldrow"/>";
          </xsl:if>
						document.location = location;
					}
				</script>
        <link href="css/admin.css" rel="stylesheet" type="text/css" />
        <link href="css/menu.css" rel="stylesheet" type="text/css" />
        <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css" />
      </head>
      <body>
        <h1>
          <xsl:choose>
            <xsl:when test="$source = 'mypage'">
              <a href="{$referer}">%headDashboard%</a>
            </xsl:when>
            <xsl:when test="$source = 'archives'">
              <a href="{$referer}">%headContentRepositories%</a>
            </xsl:when>
            <xsl:when test="$source = 'ice'">
              ICE
            </xsl:when>
            <xsl:otherwise/>
          </xsl:choose>
          <xsl:text> / %headContentWizard%</xsl:text>
        </h1>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td>
              <div class="tab-pane" id="tab-pane-1">
                <script type="text/javascript" language="JavaScript">
                  var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                </script>

                <div class="tab-page" id="tab-page-1">
                  <span class="tab">
                    <xsl:choose>
                      <xsl:when test="$step = '1'">
                        <xsl:text> %selectContentType% </xsl:text>
                      </xsl:when>
                      <xsl:when test="$step = '2'">
                        <xsl:text> %altSelectCategory% </xsl:text>
                      </xsl:when>
                      <xsl:otherwise/>
                    </xsl:choose>
                  </span>

                  <script type="text/javascript" language="JavaScript">
                    tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                  </script>

                  <fieldset>
                    <table width="100%" cellspacing="0" cellpadding="2" border="0">
                      <tr>
                        <td class="form_labelcolumn"></td>
                      </tr>
                      <tr>
                        <td>
                          <xsl:choose>
                            <xsl:when test="$step = '1'">
                              <xsl:call-template name="step1"/>
                            </xsl:when>
                            <xsl:when test="$step = '2'">
                              <xsl:call-template name="step2"/>
                            </xsl:when>
                          </xsl:choose>
                        </td>
                      </tr>
                      <tr>
                        <td class="form_form_buttonrow_seperator"><img src="images/1x1.gif"/></td>
                      </tr>
                    </table>
                  </fieldset>
                </div>
              </div>
            </td>
          </tr>
          <tr>
            <td class="form_form_buttonrow_seperator"><img src="images/1x1.gif"/></td>
          </tr>
          <tr>
            <td>
              <xsl:if test="$step = '2'">
                <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'link'"/>
                  <xsl:with-param name="caption" select="'%cmdBack%'"/>
                  <xsl:with-param name="href">
                    <xsl:text>adminpage?page=960&amp;op=createcontentwizard_step1</xsl:text>
                    <xsl:if test="$relatedContentParams">
                      <xsl:value-of select="$relatedContentParams"/>
                    </xsl:if>
                    <xsl:if test="$source">
                      <xsl:text>&amp;source=</xsl:text>
                      <xsl:value-of select="$source"/>
                    </xsl:if>
                  </xsl:with-param>
                </xsl:call-template>

                <xsl:text> </xsl:text>
              </xsl:if>
              <xsl:choose>
                <xsl:when test="$source = 'ice'">
                  <xsl:call-template name="button">
                    <xsl:with-param name="type" select="'button'"/>
                    <xsl:with-param name="caption" select="'%cmdCancel%'"/>
                    <xsl:with-param name="onclick">
                      <xsl:text>window.close();</xsl:text>
                    </xsl:with-param>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:call-template name="button">
                    <xsl:with-param name="type" select="'cancel'"/>
                    <xsl:with-param name="referer">
                      <xsl:value-of disable-output-escaping="yes" select="$referer"/>
                    </xsl:with-param>
                  </xsl:call-template>
                </xsl:otherwise>
              </xsl:choose>

            </td>
          </tr>
        </table>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="step1">
    <xsl:for-each select="/contenttypes/contenttype">
      <xsl:sort select="name" case-order="upper-first"/>
      <a>
        <xsl:attribute name="href">
          <xsl:text>adminpage?page=960&amp;op=createcontentwizard_step2&amp;contenttypekey=</xsl:text>
          <xsl:value-of select="@key"/>
          <xsl:text>&amp;source=</xsl:text>
          <xsl:value-of select="$source"/>
          <xsl:if test="$subop">
            <xsl:text>&amp;subop=</xsl:text>
            <xsl:value-of select="$subop"/>
          </xsl:if>
          <xsl:if test="$fieldrow">
            <xsl:text>&amp;fieldrow=</xsl:text>
            <xsl:value-of select="$fieldrow"/>
          </xsl:if>
          <xsl:if test="$fieldname">
            <xsl:text>&amp;fieldname=</xsl:text>
            <xsl:value-of select="$fieldname"/>
          </xsl:if>
        </xsl:attribute>
        <xsl:value-of select="name"/>
      </a>
      <br/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="step2">
    <xsl:variable name="url">
      <xsl:choose>
        <xsl:when test="$source = 'ice'">
          <xsl:text>adminpage?callback=cms.ice.Utils.reloadPage&amp;fieldname=null&amp;fieldrow=-1&amp;subop=popup</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>adminpage?foobar&amp;referer=</xsl:text>
          <xsl:value-of select="$refererUrlEncoded"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:apply-templates select="/*" mode="displaytree">
      <xsl:with-param name="url" select="$url"/>
      <xsl:with-param name="linkshaded" select="false()"/>
      <xsl:with-param name="target" select="'_self'"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="category" mode="op" priority="1">
    <xsl:text>form</xsl:text>
  </xsl:template>

  <xsl:template match="category" mode="extraparams" priority="1">
		<xsl:text>&amp;cat=</xsl:text><xsl:value-of select="@key"/>
	</xsl:template>

</xsl:stylesheet>