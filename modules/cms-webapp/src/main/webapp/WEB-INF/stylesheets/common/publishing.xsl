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

  <xsl:include href="../tree/configs/default.xsl"/>

  <xsl:template name="publishing">
    <xsl:param name="edit" select="true()"/>

    <div class="tab-page" id="tab-page-publishing">
      <span class="tab">%blockPublishing%</span>

      <script type="text/javascript" language="JavaScript">
        tabPane1.addTabPage( document.getElementById( "tab-page-publishing" ) );
      </script>

      <xsl:if test="$edit">
        <script type="text/javascript" language="JavaScript">
          var extraValidatedFields = new Array(4);
          extraValidatedFields[0] = new Array("%fldOnlineFrom%", "date_pubdata_publishfrom", validateDate);
          extraValidatedFields[1] = new Array("%fldOnlineFrom%", "time_pubdata_publishfrom", validateTime);
          extraValidatedFields[2] = new Array("%fldOnlineTo%", "date_pubdata_publishto", validateDate);
          extraValidatedFields[3] = new Array("%fldOnlineTo%", "time_pubdata_publishto", validateTime);
        </script>
      </xsl:if>

      <fieldset>
        <legend>&nbsp;%blockAvailability%&nbsp;</legend>

        <xsl:variable name="pubStatusValue">
          <xsl:choose>
            <xsl:when test="not($categorypublish) and $draft">
              <xsl:text>0</xsl:text>
            </xsl:when>
            <xsl:when test="not($categorypublish) and $waitingforapproval">
              <xsl:text>1</xsl:text>
            </xsl:when>
            <xsl:when test="not($categorypublish) and $approved">
              <xsl:text>2</xsl:text>
            </xsl:when>
            <xsl:when test="not($categorypublish) and $archived">
              <xsl:text>3</xsl:text>
            </xsl:when>
            <xsl:when test="$categorypublish and ($draft or $waitingforapproval)">
              <xsl:if test="$draft">
                <xsl:text>0</xsl:text>
              </xsl:if>
              <xsl:if test="$waitingforapproval">
                <xsl:text>1</xsl:text>
              </xsl:if>
            </xsl:when>
            <xsl:when test="$categorypublish and ($approved or $archived)">
              <xsl:if test="$approved">
                <xsl:text>2</xsl:text>
              </xsl:if>
              <xsl:if test="$archived">
                <xsl:text>3</xsl:text>
              </xsl:if>
            </xsl:when>
          </xsl:choose>
        </xsl:variable>

        <input type="hidden" name="_pubdata_status" id="_pubdata_status" value="{$pubStatusValue}"/>

        <script type="text/javascript">
          var dateTimeRangeValidator = new DateTimeRangeValidator('_pubdata_publishfrom', '_pubdata_publishto', false, {
            startDatetimeIsLaterThanEndDatetime: '%errOnlineFromIsLaterThanOnlineTo%',
            startLabel: '%fldOnlineFrom%',
            endLabel: '%fldOnlineTo%'
          });
        </script>

        <div id="textfielddatetime-error-message" style="display:none;"><xsl:comment>//</xsl:comment></div>

        <table width="100%" border="0" cellspacing="2" cellpadding="2">
          <tr id="publishfrom-row">
            <xsl:choose>
              <xsl:when test="$categorypublish or ( ($draft and $currentisdraft) and ($categorycreate or $contentupdate) )">
                <xsl:call-template name="textfielddatetime">
                  <xsl:with-param name="name" select="'_pubdata_publishfrom'"/>
                  <xsl:with-param name="label" select="'%fldOnlineFrom%:'"/>
                  <xsl:with-param name="selectnode" select="/contents/content/@publishfrom"/>
                  <xsl:with-param name="colspan" select="'1'"/>
                  <xsl:with-param name="onbluroverridefunction">
                    <xsl:text>dateTimeRangeValidator.validate();</xsl:text>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:when>
              <xsl:otherwise>
                <xsl:call-template name="readonlydatetime">
                  <xsl:with-param name="name" select="'_pubdata_publishfrom'"/>
                  <xsl:with-param name="label" select="'%fldOnlineFrom%:'"/>
                  <xsl:with-param name="selectnode" select="/contents/content/@publishfrom"/>
                  <xsl:with-param name="colspan" select="'1'"/>
                </xsl:call-template>
              </xsl:otherwise>
            </xsl:choose>
          </tr>
          <tr id="publishto-row">
            <xsl:choose>
              <xsl:when test="$categorypublish or ( ($draft and $currentisdraft) and ($categorycreate or $contentupdate) )">
                <xsl:call-template name="textfielddatetime">
                  <xsl:with-param name="name" select="'_pubdata_publishto'"/>
                  <xsl:with-param name="label" select="'%fldOnlineTo%:'"/>
                  <xsl:with-param name="selectnode" select="/contents/content/@publishto"/>
                  <xsl:with-param name="colspan" select="'1'"/>
                  <xsl:with-param name="onbluroverridefunction">
                    <xsl:text>dateTimeRangeValidator.validate();</xsl:text>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:when>
              <xsl:otherwise>
                <xsl:call-template name="readonlydatetime">
                  <xsl:with-param name="name" select="'_pubdata_publishto'"/>
                  <xsl:with-param name="label" select="'%fldOnlineTo%:'"/>
                  <xsl:with-param name="selectnode" select="/contents/content/@publishto"/>
                  <xsl:with-param name="colspan" select="'1'"/>
                </xsl:call-template>
              </xsl:otherwise>
            </xsl:choose>
          </tr>
          <xsl:variable name="isMainVersion" select="$create = 1 or ( not($categorypublish) and not($currentisdraft) )or $current"/>

          <xsl:if test="$hasversions">
            <!-- This row will always have style.display = 'none'. The input fields will still be available for the client and the server -->
            <tr style="display: none">
              <xsl:choose>
                <xsl:when test="$isMainVersion">
                  <td>
                    <xsl:text>%fldMainVersion%:</xsl:text>
                  </td>
                  <td>
                    <img src="images/icon_check.gif" alt=""/>
                  </td>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:call-template name="checkbox_boolean">
                    <xsl:with-param name="label" select="'%fldMainVersion%:'"/>
                    <xsl:with-param name="name" select="'_pubdata_activate'"/>
                    <xsl:with-param name="selectnode" select="$current or $create = 1"/>
                    <xsl:with-param name="disabled" select="$isMainVersion"/>
                  </xsl:call-template>
                </xsl:otherwise>
              </xsl:choose>
            </tr>
          </xsl:if>
        </table>
      </fieldset>

      <fieldset>
        <legend>%txtContentIsPlacedOnTheFollowingPages%</legend>

        <xsl:choose>
          <xsl:when test="count(/contents/location/site/contentlocation) != 0">

            <table style="width: 100%">
              
              <xsl:for-each select="/contents/location/site">
                <xsl:sort select="name" order="ascending"/>

                <xsl:variable name="location-site-key" select="@key"/>
                <xsl:variable name="location-site" select="/contents/location-sites/site[@key = $location-site-key]"/>

                <tr>
                  <td colspan="5" style="padding-bottom: 0px;">
                    <img src="images/icon_sites.gif" alt="{$location-site/name}" title="{$location-site/name}" style="vertical-align: middle;"/>
                    <xsl:text> </xsl:text>
                    <xsl:value-of select="$location-site/name"/>
                  </td>
                </tr>
                <tr>
                  <td colspan="5">
                    <xsl:call-template name="_publishing-display-row-pagetemplate"/>
                  </td>
                </tr>
                <tr>
                  <td align="center" style="width: 40px; padding-bottom: 0px; font-weight: bold; vertical-align: bottom">
                    %fldHome%
                  </td>
                  <td align="left" style="width: 290px; padding-bottom: 0px; font-weight: bold; vertical-align: bottom">
                    %fldPage%
                  </td>
                  <td align="center" style="width: 110px; padding-bottom: 0px; font-weight: bold; vertical-align: bottom">
                    %fldPublished%
                  </td>
                  <!--td align="left" style="padding-bottom: 0px; font-weight: bold; vertical-align: bottom">
                    type
                  </td-->
                </tr>
                <tr>
                  <td colspan="5" style="padding-top: 0px; padding-bottom: 0px; border-bottom: 1px solid #B8B8B8; font-size: 1px; height: 2px">
                    <br/>
                  </td>
                </tr>
                <xsl:for-each select="contentlocation">
                  <xsl:sort select="number(@level) * 1000 + number(@order)" order="ascending"/>
                  <tr>
                    <xsl:call-template name="_publishing-display-location-row">
                      <xsl:with-param name="contentlocation" select="."/>
                    </xsl:call-template>
                  </tr>
                </xsl:for-each>
                <tr>
                  <td colspan="5" style="height: 20px">
                    <br/>
                  </td>
                </tr>
              </xsl:for-each>

            </table>

          </xsl:when>
          <xsl:otherwise>
            <xsl:text>%txtContentIsNotPlacedOnAnyPages%</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </fieldset>

    </div>
  </xsl:template>

  <xsl:template name="_publishing-display-row-pagetemplate">

    <xsl:variable name="menukey" select="@key"/>
    <table border="0" cellspacing="2" cellpadding="4">
      <tr>
        <td>
          %fldContentFramework%:
        </td>
        <td>
          <xsl:choose>
            <xsl:when test="/contents/contenthomes/contenthome[@menukey = $menukey]/@pagetemplatekey">
              <xsl:value-of select="/contents/contenthomes/contenthome[@menukey = $menukey]/@pagetemplatename"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>%optNone%</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </td>
        <td>
          <xsl:call-template name="button">
            <xsl:with-param name="type" select="'link'"/>
            <xsl:with-param name="caption" select="'%cmdPreviewVersion%'"/>
            <xsl:with-param name="name" select="concat('button_', @key)"/>
            <xsl:with-param name="target" select="'_blank'"/>
            <xsl:with-param name="href">
              <xsl:text>adminpage?page=2002&amp;op=preview&amp;subop=frameset&amp;menukey=</xsl:text>
              <xsl:value-of select="@key"/>
              <xsl:text>&amp;contentkey=</xsl:text>
              <xsl:value-of select="/contents/content/@key"/>
              <xsl:if test="/contents/content/@versionkey">
                <xsl:text>&amp;versionkey=</xsl:text>
                <xsl:value-of select="/contents/content/@versionkey"/>
              </xsl:if>
            </xsl:with-param>
          </xsl:call-template>
        </td>
      </tr>
    </table>

  </xsl:template>

  <xsl:template name="_publishing-display-location-row">

    <xsl:param name="contentlocation"/>

    <xsl:variable name="menuItemKey" select="$contentlocation/@menuitemkey"/>
    <xsl:variable name="menuitem" select="/contents/location-menuitems/menuitem[ @key = $menuItemKey ]"/>

    <!-- Resolved Home -->
    <td align="center" style="padding-top: 4px">
      <xsl:choose>
        <xsl:when test="$contentlocation/@home = 'true'">
          <img src="./images/fileicons/32/overlay_home.gif"/>
        </xsl:when>
        <xsl:otherwise>
          <img src="./images/fileicons/32/overlay_arrow.gif"/>
        </xsl:otherwise>
      </xsl:choose>
    </td>

    <!-- Menuitem icon and path -->
    <td style="padding-top: 4px">
      <table border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td>
            <xsl:variable name="altText">
              <xsl:choose>
                <xsl:when test="$menuitem/@type = 'section'">
                  <xsl:text>%optSection%</xsl:text>
                </xsl:when>
                <!-- Page of type Content -->
                <xsl:when test="$menuitem/@type = 'content' and $menuitem/page/@pagetemplatetype = 5">
                  <xsl:text>%optPageTemplateTypeContent%</xsl:text>
                </xsl:when>
                <!-- Page of type Newsletter -->
                <xsl:when test="$menuitem/@type = 'content' and $menuitem/page/@pagetemplatetype = 4">
                  <xsl:text>%optPageTemplateTypeNewsletter%</xsl:text>
                </xsl:when>
                <!-- Page of type Section -->
                <xsl:when test="$menuitem/@type = 'content' and $menuitem/page/@pagetemplatetype = 6">
                  <xsl:text>%optPageTemplateTypeSectionPage%</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>%unknown%</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <img border="0" alt="{$altText}" title="{$altText}">
              <xsl:attribute name="src">
                <xsl:text>images/icon_menuitem</xsl:text>
                <xsl:choose>
                  <!--  -->
                  <xsl:when test="$menuitem/@type = 'section'">
                    <xsl:text>_section</xsl:text>
                  </xsl:when>
                  <!-- Page of type Content -->
                  <xsl:when test="$menuitem/@type = 'content' and $menuitem/page/@pagetemplatetype = 5">
                    <xsl:text>_content</xsl:text>
                  </xsl:when>
                  <!-- Page of type Newsletter -->
                  <xsl:when test="$menuitem/@type = 'content' and $menuitem/page/@pagetemplatetype = 4">
                    <xsl:text>_content</xsl:text>
                  </xsl:when>
                  <!-- Page of type Section -->
                  <xsl:when test="$menuitem/@type = 'content' and $menuitem/page/@pagetemplatetype = 6">
                    <xsl:text>_sectionpage</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>_unknown</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="$menuitem/@visible = 'yes'">_show</xsl:if>
                <xsl:if test="not($menuitem/@anonread = 'true')">_lock</xsl:if>
                <xsl:text>.gif</xsl:text>
              </xsl:attribute>
            </img>
          </td>
          <td>&nbsp;</td>
          <td>
            <xsl:value-of select="$contentlocation/@menuitempath"/>
          </td>
        </tr>
      </table>
    </td>


    <!-- Active in section -->
    <td align="center" style="padding-top: 4px">
      <xsl:choose>
        <xsl:when test="not(@type = 'section' or @type = 'section_and_sectionhome')">
          <!-- display nothing -->
          <br/>
        </xsl:when>
        <xsl:when test="@activated = 'true'">
          <img src="./images/icon_content_approve.gif"/>
        </xsl:when>
        <xsl:otherwise>
          <img src="./images/icon_content_unapprove.gif"/>
        </xsl:otherwise>
      </xsl:choose>
    </td>
    <!--td style="vertical-align: top">
      <xsl:value-of select="@type"/>
    </td-->

  </xsl:template>

  <xsl:template name="checkbox_nolabel">
    <xsl:param name="name"/>
    <xsl:param name="value"/>
    <xsl:param name="selectnode"/>
    <xsl:param name="disabled" select="false()"/>
    <xsl:param name="align" select="'center'"/>

    <td valign="baseline" nowrap="nowrap">
      <xsl:attribute name="align">
        <xsl:value-of select="$align"/>
      </xsl:attribute>
      <input type="checkbox">
				<xsl:if test="$selectnode = $value">
          <xsl:attribute name="checked">
            <xsl:value-of select="'checked'" />
          </xsl:attribute>
        </xsl:if>
        <xsl:attribute name="value">
          <xsl:value-of select="$value" />
        </xsl:attribute>
        <xsl:attribute name="name">
          <xsl:value-of select="$name" />
        </xsl:attribute>
        <xsl:attribute name="id">
          <xsl:value-of select="$name" />
        </xsl:attribute>
				<xsl:if test="$disabled">
					<xsl:attribute name="disabled">
						<xsl:text>disabled</xsl:text>
					</xsl:attribute>
				</xsl:if>
			</input>
		</td>
	</xsl:template>

  <xsl:template name="radiobutton_nolabel">
    <xsl:param name="name"/>
    <xsl:param name="value"/>
    <xsl:param name="selectnode"/>
    <xsl:param name="disabled" select="false()"/>
    <xsl:param name="align" select="'center'"/>

    <td valign="baseline" nowrap="nowrap">
      <xsl:attribute name="align">
        <xsl:value-of select="$align"/>
      </xsl:attribute>
      <input type="radio">
        <xsl:if test="$selectnode = $value">
          <xsl:attribute name="checked">
            <xsl:value-of select="'checked'"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:attribute name="value">
          <xsl:value-of select="$value"/>
        </xsl:attribute>
        <xsl:attribute name="name">
          <xsl:value-of select="$name"/>
        </xsl:attribute>
        <xsl:attribute name="id">
          <xsl:value-of select="$name"/>
        </xsl:attribute>
        <xsl:if test="$disabled">
          <xsl:attribute name="disabled">
            <xsl:text>disabled</xsl:text>
          </xsl:attribute>
        </xsl:if>
      </input>
    </td>
  </xsl:template>

</xsl:stylesheet>