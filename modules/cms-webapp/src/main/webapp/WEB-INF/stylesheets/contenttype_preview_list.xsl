<?xml version="1.0" encoding="utf-8"?>

<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
 ]>

<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:output method="html" />

	<xsl:include href="common/generic_parameters.xsl" />
	<!-- xsl:include href="common/dropdown.xsl" /-->
	<xsl:include href="common/button.xsl" />
	<xsl:include href="common/displayerror.xsl" />

	<xsl:param name="pagetemplatekey"/>
	<xsl:param name="contentkey"/>
	<xsl:param name="versionkey"/>
	<xsl:param name="contenttypekey"/>
	<xsl:param name="menuitemkey"/>
	<xsl:param name="sessiondata"/>

	<!-- required by dropdown -->
	<xsl:variable name="create" select="'0'"/>

	<xsl:template match="/">

		<html>
			<head>
				<link rel="stylesheet" type="text/css" href="css/admin.css" />
				<script type="text/javascript" language="JavaScript" src="javascript/admin.js" />
				<script type="text/javascript" language="JavaScript">
          function dropdownSiteChanged()
          {
            var form = document.getElementById( "formAdmin" );
            form.submit();
          }

					function dropdownPageTemplateChanged( dropdown, page, unitKey, menuKey, menuItemKey, contentKey, sessionData )
					{

            if ( menuKey == -1 )
            {
              return;
            }
						var selectedIndex = dropdown.selectedIndex;
						if (dropdown.options[selectedIndex].value != '') {
							var href = "adminpage?page=" + page + "&amp;op=preview&amp;subop=pagetemplate";
							if (unitKey &gt;= 0)
								href = href + "&amp;selectedunitkey=" + unitKey;
							href = href + "&amp;menukey=" + menuKey;
							if (menuItemKey &gt;= 0)
								href = href + "&amp;menuitemkey=" + menuItemKey;
							href = href + "&amp;pagetemplatekey=" + dropdown.options[selectedIndex].value;
							if (sessionData)
								href = href + "&amp;sessiondata=" + true;
                            if (contentKey)
                              href += "&amp;contentkey=" + contentKey;
                            //if (versionkey)
                              //href += "&amp;versionkey=" + versionKey;

							var mainFrame = parent.frames["mainFrame"];
							mainFrame.location.href = href;
						}
					}
				</script>
        <style type="text/css">
          body
          {
            margin-top:7px;
            padding-top:7px;
          }
        </style>
			</head>

      <body>
        <xsl:attribute name="onload">
          <xsl:text>parent.frames['mainFrame'].location.href = </xsl:text>
          <xsl:choose>
            <xsl:when test="/menus/contenthomes/contenthome/@pagetemplatekey or /menus/pagetemplates/pagetemplate">
              <xsl:variable name="currentmenukey">
                <xsl:choose>
                  <xsl:when test="$menukey != ''">
                    <xsl:value-of select="$menukey"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="/menus/menu[1]/@key"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:variable>

              <xsl:text>'adminpage?page=</xsl:text>
              <xsl:value-of select="$page"/>
              <xsl:text>&amp;op=preview&amp;subop=pagetemplate</xsl:text>
              <xsl:if test="$selectedunitkey != ''">
                <xsl:text>&amp;selectedunitkey=</xsl:text>
                <xsl:value-of select="$selectedunitkey"/>
              </xsl:if>
              <xsl:text>&amp;menukey=</xsl:text>
              <xsl:value-of select="$currentmenukey"/>
              <xsl:if test="$menuitemkey != '' or /menus/contenthomes/contenthome[@menukey = $currentmenukey]/@menuitemkey">
                <xsl:text>&amp;menuitemkey=</xsl:text>
                <xsl:choose>
                  <xsl:when test="$menuitemkey != ''">
                    <xsl:value-of select="$menuitemkey"/>
                  </xsl:when>
                  <xsl:when test="/menus/contenthomes/contenthome[@menukey = $currentmenukey]/@menuitemkey">
                    <xsl:value-of select="/menus/contenthomes/contenthome[@menukey = $currentmenukey]/@menuitemkey"/>
                  </xsl:when>
                </xsl:choose>
              </xsl:if>
              <xsl:text>&amp;pagetemplatekey=</xsl:text>
              <xsl:choose>
                <xsl:when test="$pagetemplatekey != ''">
                  <xsl:value-of select="$pagetemplatekey"/>
                </xsl:when>
                <xsl:when test="/menus/contenthomes/contenthome[@menukey = $currentmenukey]/@pagetemplatekey">
                  <xsl:value-of select="/menus/contenthomes/contenthome[@menukey = $currentmenukey]/@pagetemplatekey"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="/menus/pagetemplates/pagetemplate[contenttypes/contenttype/@key = $contenttypekey]/@key"/>
                </xsl:otherwise>
              </xsl:choose>
              <xsl:if test="number($contentkey) &gt;= 0">
                <xsl:text>&amp;contentkey=</xsl:text>
                <xsl:value-of select="$contentkey"/>
              </xsl:if>
              <xsl:if test="number($versionkey) &gt;= 0">
                <xsl:text>&amp;versionkey=</xsl:text>
                <xsl:value-of select="$versionkey"/>
              </xsl:if>
              <xsl:if test="$sessiondata = 'true'">
                <xsl:text>&amp;sessiondata=true</xsl:text>
              </xsl:if>
              <xsl:text>';</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>'empty.html';</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>

        <form id="formAdmin" name="formAdmin" method="get" action="adminpage">
          <input type="hidden" name="page" value="{$page}"/>
          <input type="hidden" name="op" value="preview"/>
          <input type="hidden" name="subop" value="list"/>
          <input type="hidden" name="selectedunitkey" value="{$selectedunitkey}"/>
          <input type="hidden" name="contenttypekey" value="{$contenttypekey}"/>
          <xsl:if test="$menuitemkey != ''">
            <input type="hidden" name="menuitemkey" value="{$menuitemkey}"/>
          </xsl:if>
          <xsl:if test="$contentkey != ''">
            <input type="hidden" name="contentkey" value="{$contentkey}"/>
          </xsl:if>
          <xsl:if test="$versionkey != ''">
            <input type="hidden" name="versionkey" value="{$versionkey}"/>
          </xsl:if>
          <xsl:if test="$sessiondata = 'true'">
            <input type="hidden" name="sessiondata" value="{$sessiondata}"/>
          </xsl:if>

          <table>
            <tr>
              <!-- select menu -->
              <xsl:call-template name="dropdown">
                <xsl:with-param name="label" select="'%fldSite%: '" />
                <xsl:with-param name="name" select="'menukey'" />
                <xsl:with-param name="selectedkey">
                  <xsl:choose>
                    <xsl:when test="$menukey != ''">
                      <xsl:value-of select="$menukey"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="/menus/menu[1]/@key"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:with-param>
                <xsl:with-param name="selectnode" select="/menus/menu"/>
                <xsl:with-param name="colspan" select="'1'"/>
                <xsl:with-param name="onchangefunction" select="'dropdownSiteChanged()'"/>
              </xsl:call-template>

              <td width="10">&nbsp;</td>

              <!-- select page template -->
              <xsl:call-template name="dropdown">
                <xsl:with-param name="label" select="'%fldContentFramework%: '" />
                <xsl:with-param name="name" select="'ptkey'" />
                <xsl:with-param name="selectedkey">
                  <xsl:choose>
                    <xsl:when test="$pagetemplatekey != ''">
                      <xsl:value-of select="$pagetemplatekey"/>
                    </xsl:when>
                    <xsl:when test="/menus/contenthomes/contenthome/@pagetemplatekey">
                      <xsl:value-of select="/menus/contenthomes/contenthome/@pagetemplatekey"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="/menus/pagetemplates/pagetemplate[contenttypes/contenttype/@key = $contenttypekey]/@key"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:with-param>
                <xsl:with-param name="selectnode" select="/menus/pagetemplates/pagetemplate[contenttypes/contenttype/@key = $contenttypekey]" />
                <xsl:with-param name="colspan" select="'1'" />
                <xsl:with-param name="onchangefunction">
                  <xsl:text>dropdownPageTemplateChanged(</xsl:text>
                  <xsl:text>this,</xsl:text>
                  <xsl:value-of select="$page"/>
                  <xsl:text>,</xsl:text>
                  <xsl:choose>
                    <xsl:when test="$selectedunitkey != ''">
                      <xsl:value-of select="$selectedunitkey"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:text>-1</xsl:text>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:text>,</xsl:text>
                  <xsl:choose>
                    <xsl:when test="$menukey != ''">
                      <xsl:value-of select="$menukey"/>
                    </xsl:when>
                    <xsl:when test="/menus/menu[1]/@key != ''">
                      <xsl:value-of select="/menus/menu[1]/@key"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:text>-1</xsl:text>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:text>,</xsl:text>
                  <xsl:choose>
                    <xsl:when test="$menuitemkey != ''">
                      <xsl:value-of select="$menuitemkey"/>
                    </xsl:when>
                    <xsl:when test="/menus/contenthomes/contenthome/@menuitemkey">
                      <xsl:value-of select="/menus/contenthomes/contenthome/@menuitemkey"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:text>-1</xsl:text>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:text>,</xsl:text>
                  <xsl:choose>
                    <xsl:when test="number($contentkey) &gt;= 0">
                      <xsl:value-of select="$contentkey"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:text>-1</xsl:text>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:text>,</xsl:text>
                  <xsl:choose>
                    <xsl:when test="$sessiondata = 'true'">
                      <xsl:text>true</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:text>false</xsl:text>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:text>)</xsl:text>
                </xsl:with-param>
              </xsl:call-template>
            </tr>
          </table>
        </form>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="dropdown">
    <xsl:param name="label"/>
    <xsl:param name="name"/>
    <xsl:param name="selectedkey"/>
    <xsl:param name="defaultkey"/>
    <xsl:param name="selectnode"/>
    <xsl:param name="colspan"/>
    <xsl:param name="emptyrow"/>
    <xsl:param name="onchangefunction"/>
    <xsl:param name="required" select="'false'"/>
    <xsl:param name="buttoncaption" select="''"/>
    <xsl:param name="buttonfunction" select="''"/>
    <xsl:param name="buttonhref" select="''"/>
    <xsl:param name="buttondisabled" select="false()"/>
    <xsl:param name="disabled" select="false()"/>
    <xsl:param name="menuitemkey"/>

    <td nowrap="nowrap">
      <label for="{$name}">
        <xsl:value-of select="$label"/>
      </label>
      <xsl:if test="$required = 'true'">
        <span class="requiredfield">*</span>
      </xsl:if>
    </td>
    <td nowrap="nowrap">
      <xsl:variable name="errors">
        <xsl:choose>
          <xsl:when test="/*/errors">
            <xsl:copy-of select="/*/errors"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:copy-of select="/*/*/errors"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:if test="exslt-common:node-set($errors)/errors/error[@name=$name]">
        <xsl:call-template name="displayerror">
          <xsl:with-param name="code" select="exslt-common:node-set($errors)/errors/error[@name=$name]/@code"/>
        </xsl:call-template>
      </xsl:if>

      <select>
        <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
        <xsl:attribute name="id"><xsl:value-of select="$name"/></xsl:attribute>
        <xsl:if test="$onchangefunction != ''">
          <xsl:attribute name="onchange"><xsl:value-of select="$onchangefunction"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="$disabled">
          <xsl:attribute name="disabled">disabled</xsl:attribute>
        </xsl:if>

        <xsl:if test="$emptyrow!=''">
          <option value=""><xsl:value-of select="$emptyrow"/></option>
        </xsl:if>

        <xsl:choose>
          <xsl:when test="@name">
            <xsl:apply-templates select="$selectnode" mode="dropdown">
              <xsl:sort select="@name" order="ascending"/>
              <xsl:with-param name="selectedkey" select="$selectedkey"/>
              <xsl:with-param name="defaultkey" select="$defaultkey"/>
              <xsl:with-param name="create" select="$create"/>
            </xsl:apply-templates>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates select="$selectnode" mode="dropdown">
              <xsl:sort select="name" order="ascending"/>
              <xsl:with-param name="selectedkey" select="$selectedkey"/>
              <xsl:with-param name="defaultkey" select="$defaultkey"/>
              <xsl:with-param name="create" select="$create"/>
            </xsl:apply-templates>
          </xsl:otherwise>
        </xsl:choose>
      </select>
      <xsl:value-of select="$buttonhref"/>
      <xsl:choose>
        <xsl:when test="$buttoncaption != '' and $buttonhref != ''">
          <xsl:text>&nbsp;</xsl:text>
          <xsl:call-template name="button">
            <xsl:with-param name="type" select="'link'"/>
            <xsl:with-param name="caption" select="$buttoncaption"/>
            <xsl:with-param name="name" select="concat('dropdownbtn_', $name)"/>
            <xsl:with-param name="href" select="$buttonhref"/>
            <xsl:with-param name="target" select="'_top_'"/>
            <xsl:with-param name="disabled" select="$disabled or $buttondisabled"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="$buttoncaption != ''">
          <xsl:text>&nbsp;</xsl:text>
          <xsl:call-template name="button">
            <xsl:with-param name="type" select="'button'"/>
            <xsl:with-param name="caption" select="$buttoncaption"/>
            <xsl:with-param name="name" select="concat('dropdownbtn_', $name)"/>
            <xsl:with-param name="onclick" select="$buttonfunction"/>
            <xsl:with-param name="disabled" select="$disabled or $buttondisabled"/>
          </xsl:call-template>
        </xsl:when>
      </xsl:choose>
    </td>
  </xsl:template>

  <xsl:template match="menu | pagetemplate" mode="dropdown">
    <xsl:param name="selectedkey"/>
    <xsl:param name="defaultkey"/>
    <xsl:param name="create"/>

    <option>
      <xsl:choose>
        <xsl:when test="$selectedkey = @key">
          <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:when>
        <xsl:when test="$create = 1 and string($defaultkey) = string(@key)">
          <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:when>
      </xsl:choose>

      <xsl:attribute name="value">
        <xsl:value-of disable-output-escaping="yes" select="@key"/>
      </xsl:attribute>
      <xsl:choose>
        <xsl:when test="@name">
          <xsl:value-of select="@name"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="name"/>
        </xsl:otherwise>
      </xsl:choose>
    </option>
  </xsl:template>

</xsl:stylesheet>