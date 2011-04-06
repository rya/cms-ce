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

  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:include href="common/displaypath.xsl"/>
  <xsl:include href="common/displaycontentpath.xsl"/>
  <xsl:include href="common/publishstatus.xsl"/>
  <xsl:include href="common/formatdate.xsl"/>
  <xsl:include href="common/newcontentoperations.xsl"/>
  <xsl:include href="common/sectionoperations.xsl"/>
  <xsl:include href="common/tablecolumnheader.xsl"/>
  <xsl:include href="common/tablerowpainter.xsl"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="common/paging.xsl"/>
  <xsl:include href="common/displaycontenticon.xsl"/>
  <xsl:include href="common/getpath.xsl"/>
  <xsl:include href="common/displayfeedback.xsl"/>
  <xsl:include href="column_templates/default.xsl"/>
  <xsl:include href="common/display-icon.xsl"/>
  <xsl:include href="common/display-content-icon.xsl"/>

  <xsl:param name="maximize"/>
  <xsl:param name="index"/>
  <xsl:param name="count"/>
  <xsl:param name="sortby"/>
  <xsl:param name="sortby-direction"/>

  <xsl:variable name="pageURL">
    <xsl:text>adminpage?page=</xsl:text>
    <xsl:value-of select="$page"/>
    <xsl:text>&amp;op=page</xsl:text>
  </xsl:variable>

  <xsl:variable name="default-content-count-pr-group" select="3"/>

  <xsl:variable name="icon-column-width" select="40"/>

  <xsl:variable name="user-photo-square-size" select="24"/>
  <xsl:variable name="user-image-filters" select="concat('scalesquare(',$user-photo-square-size, ');rounded(2)')"/>

  <xsl:template match="/">
    <html>
      <head>
        <link rel="stylesheet" type="text/css" href="css/admin.css"/>
        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" language="JavaScript">

          // ---------------------------------------------------------------------------------------------------

          /*
          Method: removeContent
          */

          function removeContent(contentKey, page, cat) {
            var toBeRemoved = [];
            toBeRemoved.push(contentKey);

            AjaxService.isContentInUse(toBeRemoved, {
              callback:function(bInUse) {
                doRemoveContent(bInUse, contentKey, page, cat);
              }
            });

            return false;
          }

          // ---------------------------------------------------------------------------------------------------

          /*
          Method: doRemoveContent
          */

          function doRemoveContent(bInUse, contentKey, page, cat) {
            var alertMsg;

            if(bInUse)
            {
              alertMsg = '%alertDeleteContentWithParents%';
            }
            else
            {
              alertMsg = '%msgConfirmRemoveSelected%';
            }

            if (confirm(alertMsg))
            {
              deleteOperationUrl = 'adminpage?page=' + page + '&amp;op=remove&amp;key=' + contentKey + '&amp;cat=' + cat + '&amp;selectedunitkey=1';
              document.location.href = deleteOperationUrl;
            }
          }
        </script>
      </head>
      <body>

        <h1>
          <a href="{$pageURL}">%headDashboard%</a>
        </h1>

        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <form id="formSearch" name="formSearch" method="get" action="adminpage" style="margin-bottom:0;">
            <tr>
              <td>
                <input type="hidden" name="op" value="browse"/>
                <input type="hidden" name="page" value="991"/>
                <input type="hidden" name="searchtype" value="simple"/>
                <input type="hidden" name="scope" value="title"/>
                <input type="hidden" name="waitscreen" value="true"/>

                <!-- Search field -->
                <input type="text" name="searchtext" size="12" style="height: 20px"/>

                <!-- Search button -->
                <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'submit'"/>
                  <xsl:with-param name="caption" select="'%cmdQuickSearch%'"/>
                  <xsl:with-param name="name" select="'search'"/>
                </xsl:call-template>

                <xsl:text>&nbsp;</xsl:text>

                <!-- Advanced search button -->
                <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'link'"/>
                  <xsl:with-param name="caption" select="'%cmdSearchDotDotDot%'"/>
                  <xsl:with-param name="href">
                    <xsl:text>adminpage?page=991&amp;op=searchform</xsl:text>
                  </xsl:with-param>
                </xsl:call-template>

                <xsl:text>&nbsp;</xsl:text>

                <!-- Create content wizard button -->
                <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'link'"/>
                  <xsl:with-param name="caption" select="'%createContentWizard%'"/>
                  <xsl:with-param name="href">
                    <xsl:text>adminpage?page=960&amp;op=createcontentwizard_step1</xsl:text>
                  </xsl:with-param>
                </xsl:call-template>
              </td>
            </tr>
          </form>
        </table>

        <xsl:if test="/node()/feedback/@code">
          <br/>
          <xsl:call-template name="displayfeedback"/>
        </xsl:if>

        <xsl:choose>
          <xsl:when test="not($maximize)">
            <table border="0" width="100%">
              <tr>
                <td width="65%" valign="top">

                  <!--xsl:if test="not($maximize) or $maximize = 'drafts'"-->
                  <!--
                  <xsl:call-template name="displaytable">
                    <xsl:with-param name="type" select="'drafts'"/>
                    <xsl:with-param name="contentxpath" select="/data/contents[@type = 'drafts']/content[@state = 0]"/>
                  </xsl:call-template>
                  -->
                  <!--/xsl:if-->

                  <!--xsl:if test="not($maximize) or $maximize = 'assignedto'"-->
                  <xsl:call-template name="displaytable">
                    <xsl:with-param name="type" select="'assignedto'"/>
                    <xsl:with-param name="contentxpath" select="/data/contents[@type = 'assignedto']/content"/>
                  </xsl:call-template>
                  <!--/xsl:if-->

                  <!--xsl:if test="not($maximize) or $maximize = 'activation'"-->
                  <xsl:call-template name="displaytableactivation">
                    <xsl:with-param name="type" select="'activation'"/>
                    <!--<xsl:with-param name="contentxpath" select="/data/contents[@type = 'activation']/content[@approved = 'false']"/>-->
                    <xsl:with-param name="contentxpath" select="/data/contents[@type = 'activation']/content"/>
                  </xsl:call-template>
                  <!--/xsl:if-->

                </td>
                <td>&nbsp;&nbsp;&nbsp;</td>
                <td width="35%" valign="top">

                  <!--xsl:if test="not($maximize) or $maximize = 'lastmodified'"-->
                  <xsl:call-template name="displaytable">
                    <xsl:with-param name="type" select="'lastmodified'"/>
                    <xsl:with-param name="contentxpath" select="/data/logs[@type = 'lastmodified']/log/content"/>
                  </xsl:call-template>
                  <!--/xsl:if-->

                </td>
              </tr>
            </table>
          </xsl:when>
          <xsl:otherwise>

            <xsl:if test="$maximize = 'lastmodified'">
              <xsl:call-template name="displaytable">
                <xsl:with-param name="type" select="'lastmodified'"/>
                <xsl:with-param name="contentxpath" select="/data/logs[@type = 'lastmodified']/log/content"/>
              </xsl:call-template>
            </xsl:if>

            <xsl:if test="$maximize = 'activation'">
              <xsl:call-template name="displaytableactivation">
                <xsl:with-param name="type" select="'activation'"/>
                <!--<xsl:with-param name="contentxpath" select="/data/contents[@type = 'activation']/content[@approved = 'false']"/>-->
                <xsl:with-param name="contentxpath" select="/data/contents[@type = 'activation']/content"/>
              </xsl:call-template>
            </xsl:if>

            <xsl:if test="$maximize = 'assignedto'">
              <xsl:call-template name="displaytable">
                <xsl:with-param name="type" select="'assignedto'"/>
                <xsl:with-param name="contentxpath" select="/data/contents[@type = 'assignedto']/content"/>
              </xsl:call-template>
            </xsl:if>

            <xsl:if test="$maximize = 'drafts'">
              <xsl:call-template name="displaytable">
                <xsl:with-param name="type" select="'drafts'"/>
                <xsl:with-param name="contentxpath" select="/data/contents[@type = 'drafts']/content[@state = 0]"/>
              </xsl:call-template>
            </xsl:if>

          </xsl:otherwise>
        </xsl:choose>

      </body>
    </html>
  </xsl:template>

  <xsl:template name="displaytable">
    <xsl:param name="type"/>
    <xsl:param name="contentxpath" select="."/>

    <xsl:variable name="columncount">
        <xsl:text>7</xsl:text>
    </xsl:variable>

    <xsl:variable name="totalcount">
      <xsl:choose>
        <xsl:when test="$type = 'lastmodified'">
          <xsl:choose>
            <xsl:when test="$contentxpath/../../@totalcount > 0">
              <xsl:value-of select="$contentxpath/../../@totalcount"/>
            </xsl:when>
            <!--xsl:when test="$contentxpath/parent::node()[@type = $type]/@totalcount > 0"><xsl:value-of select="$contentxpath/parent::node()[@type = $type]/@totalcount"/></xsl:when-->
            <xsl:otherwise>0</xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="$contentxpath/../@totalcount > 0">
              <xsl:value-of select="$contentxpath/../@totalcount"/>
            </xsl:when>
            <!--xsl:when test="$contentxpath/parent::node()[@type = $type]/@totalcount > 0"><xsl:value-of select="$contentxpath/parent::node()[@type = $type]/@totalcount"/></xsl:when-->
            <xsl:otherwise>0</xsl:otherwise>
          </xsl:choose>

        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <fieldset class="table-panel">

      <legend class="dashboard-window-header">
        <xsl:call-template name="display-type-header">
          <xsl:with-param name="type" select="$type"/>
          <xsl:with-param name="totalcontentcount" select="$totalcount"/>
        </xsl:call-template>
      </legend>

      <table class="browsetable" width="100%" border="0" cellspacing="0" cellpadding="0">
        <tbody>
          <tr>
            <xsl:call-template name="tablecolumnheader">
              <xsl:with-param name="caption" select="'%fldType%'"/>
              <xsl:with-param name="sortable" select="'false'"/>
              <xsl:with-param name="width" select="$icon-column-width"/>
              <xsl:with-param name="align" select="'center'"/>
            </xsl:call-template>

            <!-- AssignedTo table headers -->
            <xsl:if test="$type = 'assignedto'">
              <xsl:call-template name="tablecolumnheader">
                <xsl:with-param name="caption" select="'%fldTitle%'"/>
                <xsl:with-param name="sortable" select="'false'"/>
              </xsl:call-template>

              <xsl:call-template name="tablecolumnheader">
                <xsl:with-param name="caption" select="'%fldAssignmentDescr%'"/>
                <xsl:with-param name="width" select="'38%'"/>
                <xsl:with-param name="sortable" select="'false'"/>
              </xsl:call-template>

              <xsl:call-template name="tablecolumnheader">
                <xsl:with-param name="caption" select="'%fldStatus%'"/>
                <xsl:with-param name="width" select="'70'"/>
                <xsl:with-param name="align" select="'center'"/>
                <xsl:with-param name="sortable" select="'false'"/>
              </xsl:call-template>

              <xsl:call-template name="tablecolumnheader">
                <xsl:with-param name="caption" select="'%fldDueDate%'"/>
                <xsl:with-param name="width" select="'100'"/>
                <xsl:with-param name="sortable" select="'false'"/>
              </xsl:call-template>

            </xsl:if>
            <!-- End of assignedTo headers-->

            <xsl:if test="$type != 'assignedto'">
              <xsl:call-template name="tablecolumnheader">
                <xsl:with-param name="caption" select="'%fldTitle%'"/>
                <xsl:with-param name="sortable" select="'false'"/>
              </xsl:call-template>
            </xsl:if>

            <xsl:if test="$type != 'lastmodified' and $type != 'assignedto'">
              <xsl:call-template name="tablecolumnheader">
                <xsl:with-param name="caption" select="'%fldStatus%'"/>
                <xsl:with-param name="width" select="'70'"/>
                <xsl:with-param name="align" select="'center'"/>
                <xsl:with-param name="sortable" select="'false'"/>
              </xsl:call-template>

              <xsl:call-template name="tablecolumnheader">
                <xsl:with-param name="caption" select="'%fldLastModified%'"/>
                <xsl:with-param name="width" select="'100'"/>
                <xsl:with-param name="sortable" select="'false'"/>
              </xsl:call-template>
            </xsl:if>
          </tr>

          <xsl:for-each select="$contentxpath">
            <xsl:call-template name="displayRow">
              <xsl:with-param name="type" select="$type"/>
              <xsl:with-param name="content" select="."/>
            </xsl:call-template>
          </xsl:for-each>

          <xsl:if test="$totalcount = 0">
            <tr>
              <td colspan="10" class="browsetablecell row-last" style="cursor: text">
                <xsl:text>%msgNoContents%</xsl:text>
              </td>
            </tr>
          </xsl:if>

          <xsl:if test="not($maximize)">
            <xsl:if test="$totalcount > 0">
              <tr>
                <td colspan="10" class="browsetablecell row-last" style="cursor: text">
                  <xsl:if test="$totalcount > $default-content-count-pr-group">
                    <a href="{$pageURL}&amp;maximize={$type}">
                      <xsl:text>%cmdMore%...</xsl:text>
                    </a>
                  </xsl:if>
                  <xsl:text> (</xsl:text>
                  <xsl:value-of select="$totalcount"/>
                  <xsl:text> %txtTotal%)</xsl:text>
                </td>
              </tr>
            </xsl:if>
          </xsl:if>

          <xsl:choose>
            <xsl:when test="$maximize = 'lastmodified'">
              <xsl:if test="$totalcount &gt; $count">
                <tr>
                  <td colspan="10" class="browsetablecell row-last">
                    <xsl:call-template name="paging">
                      <xsl:with-param name="url">
                        <xsl:text>adminpage?page=</xsl:text>
                        <xsl:value-of select="$page"/>
                        <xsl:text>&amp;op=page</xsl:text>
                        <xsl:text>&amp;maximize=</xsl:text><xsl:value-of select="$type"/>
                      </xsl:with-param>
                      <xsl:with-param name="index" select="$index"/>
                      <xsl:with-param name="count" select="$count"/>
                      <xsl:with-param name="totalcount" select="/data/logs/@totalcount"/>
                    </xsl:call-template>
                  </td>
                </tr>
              </xsl:if>
            </xsl:when>
            <xsl:otherwise>
              <xsl:if test="$maximize = $type and $totalcount &gt; $count">
                <tr>
                  <td colspan="10" class="browsetablecell row-last">
                    <xsl:call-template name="paging">
                      <xsl:with-param name="url">
                        <xsl:text>adminpage?page=</xsl:text>
                        <xsl:value-of select="$page"/>
                        <xsl:text>&amp;op=page</xsl:text>
                        <xsl:text>&amp;maximize=</xsl:text><xsl:value-of select="$type"/>
                      </xsl:with-param>
                      <xsl:with-param name="index" select="$index"/>
                      <xsl:with-param name="count" select="$count"/>
                      <xsl:with-param name="totalcount" select="/data/contents/@totalcount"/>
                    </xsl:call-template>
                  </td>
                </tr>
              </xsl:if>
            </xsl:otherwise>
          </xsl:choose>

        </tbody>
      </table>
    </fieldset>

  </xsl:template>

  <xsl:template name="displayRow">

    <xsl:param name="type"/>
    <xsl:param name="content"/>

    <xsl:param name="is-last-section"/>

    <xsl:variable name="contentkey" select="$content/@key"/>
    <xsl:variable name="contenttypekey" select="$content/@contenttypekey"/>
    <xsl:variable name="contenttype" select="$content/@contenttype"/>
    <xsl:variable name="categorykey" select="$content/category/@key"/>
    <xsl:variable name="approveright" select="$content/userright/@update = 'true'"/>
    <xsl:variable name="publishright" select="$content/userright/@categorypublish = 'true'"/>
    <xsl:variable name="unitkey" select="$content/@unitkey"/>
    <xsl:variable name="versionkey" select="$content/@versionkey"/>
    <xsl:variable name="categorykey" select="$content/category/@key"/>
    <xsl:variable name="categoryxpath" select="$content/category"/>
    <xsl:variable name="tooltip">
      <xsl:choose>
        <xsl:when test="$content/@state = 0">
          <xsl:text>%msgClickToEdit%</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>%msgClickToOpen%</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="currentcontentxpath">
      <xsl:call-template name="getpath"/>
    </xsl:variable>

    <xsl:variable name="css-td-class">
      <xsl:text>browsetablecell</xsl:text>
      <!-- a bit ugly but it works -->

      <xsl:choose>
        <xsl:when test="position() = last() and $maximize = 'activation' and $is-last-section = 'true'">
          <xsl:text> row-last</xsl:text>
        </xsl:when>
        <xsl:when test="position() = last() and $maximize != 'activation' and $maximize = $type">
          <xsl:text> row-last</xsl:text>
        </xsl:when>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="css-tr-class">
      <xsl:choose>
        <xsl:when test="position() mod 2 = 1">
          <xsl:text>tablerowpainter_darkrow</xsl:text>
        </xsl:when>
        <xsl:otherwise>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$type = 'activation'">
        <tr>
          <xsl:call-template name="tablerowpainter">
            <xsl:with-param name="firstclass">
              <xsl:value-of select="$css-tr-class"/>
            </xsl:with-param>
            <xsl:with-param name="mouseoverclass">tablerowpainter_mouseoverrow</xsl:with-param>
          </xsl:call-template>

          <td title="{$tooltip}" class="{$css-td-class}" style="{concat('width:', $icon-column-width, 'px')}">
            <xsl:call-template name="addJSEvent">
              <xsl:with-param name="node" select="$content"/>
            </xsl:call-template>
            <xsl:call-template name="display-content-icon">
              <xsl:with-param name="content-node" select="$content"/>
              <xsl:with-param name="title" select="$content/title"/>
              <xsl:with-param name="contenthandler-class-name"
                              select="/data/contenthandlers/contenthandler[@contenttypekey = current()/@contenttypekey]"/>
              <xsl:with-param name="content-type-name" select="@contenttype"/>
            </xsl:call-template>

            <div style="display: none">
              <xsl:call-template name="sectionoperations">
                <xsl:with-param name="key" select="$contentkey"/>
                <xsl:with-param name="previewmenukey" select="../@sitekey"/>
                <xsl:with-param name="menukey" select="../@sitekey"/>
                <xsl:with-param name="contentpage" select="($contenttypekey + 999)"/>
                <xsl:with-param name="menuitemkey" select="../@menuitemkey"/>
                <xsl:with-param name="unitkey" select="$unitkey"/>
                <xsl:with-param name="cat" select="$categorykey"/>
                <xsl:with-param name="page" select="'950'"/>
                <xsl:with-param name="sec" select="../@menuitemkey"/>
                <xsl:with-param name="toplevel" select="'true'"/>
                <xsl:with-param name="usereferer" select="'true'"/>
                <xsl:with-param name="versionkey" select="$versionkey"/>
                <xsl:with-param name="approveright" select="$approveright"/>
                <xsl:with-param name="publishright" select="$publishright"/>
              </xsl:call-template>
            </div>

          </td>
          <td title="{$tooltip}" class="{$css-td-class}">
            <xsl:call-template name="addJSEvent">
              <xsl:with-param name="node" select="$content"/>
            </xsl:call-template>
            <xsl:choose>
              <xsl:when test="/data/contenttypes/contenttype[@key = $contenttypekey]/moduledata/browse/column[@maincolumn='true']/*">
                <xsl:apply-templates
                    select="/data/contenttypes/contenttype[@key = $contenttypekey]/moduledata/browse/column[@maincolumn='true']/*"
                    mode="display">
                  <xsl:with-param name="contentxpath" select="concat($currentcontentxpath,'/')"/>
                  <xsl:with-param name="contentelem" select="$content"/>
                  <xsl:with-param name="contenttypeelem" select="$contenttype"/>
                </xsl:apply-templates>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="/data/browse/column/title" mode="display">
                  <xsl:with-param name="contentxpath" select="$currentcontentxpath"/>
                  <xsl:with-param name="contentelem" select="$content"/>
                  <xsl:with-param name="contenttypeelem" select="$contenttype"/>
                </xsl:apply-templates>
              </xsl:otherwise>
            </xsl:choose>
          </td>

          <td align="center" title="{$tooltip}" class="{$css-td-class}">
            <xsl:call-template name="addJSEvent">
              <xsl:with-param name="node" select="$content"/>
            </xsl:call-template>
            <xsl:call-template name="publishstatus"/>
          </td>

          <td title="{$tooltip}" onclick="javascript:gotoLocation(document.getElementById('operation_{$type}_{position()}').href);"
              class="{$css-td-class}">
            <xsl:value-of select="$content/@timestamp"/>
          </td>

        </tr>
      </xsl:when>
      <xsl:otherwise>
        <tr>
          <xsl:call-template name="tablerowpainter">
            <xsl:with-param name="firstclass">
              <xsl:value-of select="$css-tr-class"/>
            </xsl:with-param>
            <xsl:with-param name="mouseoverclass">tablerowpainter_mouseoverrow</xsl:with-param>
          </xsl:call-template>

          <td title="{$tooltip}" class="{$css-td-class}" style="{concat('width:', $icon-column-width, 'px')}">
            <xsl:call-template name="addJSEvent">
              <xsl:with-param name="node" select="$content"/>
            </xsl:call-template>

            <xsl:call-template name="display-content-icon">
              <xsl:with-param name="content-node" select="$content"/>
              <xsl:with-param name="title" select="$content/title"/>
              <xsl:with-param name="contenthandler-class-name"
                              select="/data/contenthandlers/contenthandler[@contenttypekey = current()/@contenttypekey]"/>
              <xsl:with-param name="content-type-name" select="@contenttype"/>
            </xsl:call-template>
          </td>

          <td title="{$tooltip}" class="{$css-td-class}">
            <xsl:call-template name="addJSEvent">
              <xsl:with-param name="node" select="$content"/>
            </xsl:call-template>
            <xsl:choose>
              <xsl:when test="/data/contenttypes/contenttype[@key = $contenttypekey]/moduledata/browse/column[@maincolumn='true']/*">
                <xsl:apply-templates
                    select="/data/contenttypes/contenttype[@key = $contenttypekey]/moduledata/browse/column[@maincolumn='true']/*"
                    mode="display">
                  <xsl:with-param name="contentxpath" select="concat($currentcontentxpath,'/')"/>
                  <xsl:with-param name="contentelem" select="."/>
                  <xsl:with-param name="contenttypeelem" select="$contenttype"/>
                </xsl:apply-templates>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="/data/browse/column/title" mode="display">
                  <xsl:with-param name="contentxpath" select="$currentcontentxpath"/>
                  <xsl:with-param name="contentelem" select="."/>
                  <xsl:with-param name="contenttypeelem" select="$contenttype"/>
                </xsl:apply-templates>
              </xsl:otherwise>
            </xsl:choose>

            <!-- When clicking on the row we get the href from this a element and requsts it -->
            <xsl:variable name="ctykey" select="@contenttypekey"/>
            <xsl:variable name="include-remove-button" select="$type = 'lastmodified'"/>
            <xsl:variable name="include-remove-version-button" select="$type = 'drafts' or $type = 'assignedto'"/>

            <xsl:if test="$type = 'lastmodified' or $type = 'drafts' or $type = 'assignedto'">
              <div style="display: none">
                <xsl:call-template name="contentoperations">
                  <xsl:with-param name="contentelem" select="."/>
                  <xsl:with-param name="includecopy" select="false()"/>
                  <xsl:with-param name="includemove" select="false()"/>
                  <xsl:with-param name="includepublish" select="true()"/>
                  <xsl:with-param name="includepreview" select="true()"/>
                  <xsl:with-param name="includeremove" select="$include-remove-button"/>
                  <xsl:with-param name="includeremoveversion" select="$include-remove-version-button"/>
                  <xsl:with-param name="contenttypekey" select="@contenttypekey"/>
                  <xsl:with-param name="cat" select="category/@key"/>
                  <xsl:with-param name="page" select="(@contenttypekey + 999)"/>
                  <xsl:with-param name="key" select="@key"/>
                  <xsl:with-param name="usereferer" select="true()"/>
                  <xsl:with-param name="ischild" select="@child"/>
                  <xsl:with-param name="unitkey" select="@unitkey"/>
                  <xsl:with-param name="contenttypeelem" select="@contenttype"/>
                  <xsl:with-param name="versionkey">
                    <xsl:choose>
                      <xsl:when test="$content/@has-draft = 'true'">
                        <xsl:value-of select="$content/versions/version[@status = 0]/@key"/>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:value-of select="$content/@versionkey"/>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:with-param>
                </xsl:call-template>
              </div>
            </xsl:if>
          </td>

          <!-- here are the rows for assignedto-->
          <xsl:if test="$type = 'assignedto'">

            <td title="{$tooltip}" class="{$css-td-class}">
              <xsl:call-template name="addJSEvent">
                <xsl:with-param name="node" select="$content"/>
              </xsl:call-template>

              <xsl:variable name="user-photo-tooltip">
                <xsl:value-of select="assigner/display-name"/>
              </xsl:variable>

              <xsl:variable name="user-photo-src">
                <xsl:choose>
                  <xsl:when test="assigner/photo/@exists = 'true'">
                    <xsl:value-of select="concat('_image/user/', assigner/@key, '?_filter=',$user-image-filters)"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>images/dummy-user-small.png</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:variable>

              <xsl:if test="assignment-description != ''">
                <div style="margin-bottom: 4px">
                  <xsl:value-of select="assignment-description"/>
                </div>
              </xsl:if>
              
              <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td style="padding-right:8px">
                    <img src="{$user-photo-src}" alt="{$user-photo-tooltip}" title="{$user-photo-tooltip}"
                         width="{$user-photo-square-size}" height="{$user-photo-square-size}"/>
                  </td>
                  <td>
                    <div class="dashboard-user-display-name">
                      <xsl:value-of select="assigner/display-name"/>
                    </div>
                  </td>
                </tr>
              </table>
            </td>

            <td align="center" title="{$tooltip}" class="{$css-td-class}">
              <xsl:call-template name="addJSEvent">
                <xsl:with-param name="node" select="$content"/>
              </xsl:call-template>
              <xsl:call-template name="publishstatus"/>
            </td>

            <td title="{$tooltip}">
              <xsl:call-template name="addJSEvent">
                <xsl:with-param name="key" select="$content"/>
              </xsl:call-template>
              <xsl:attribute name="class">
                <xsl:value-of select="$css-td-class"/>
                <xsl:if test="assignment-due-date/@is-overdue = 'true'">
                  <xsl:text> dashboard-td-overdue</xsl:text>
                </xsl:if>
              </xsl:attribute>

              <xsl:choose>
                <xsl:when test="assignment-due-date = ''">
                  &nbsp;
                </xsl:when>
                <xsl:otherwise>

                  <xsl:call-template name="formatdatetime">
                    <xsl:with-param name="date" select="assignment-due-date"/>
                  </xsl:call-template>
                </xsl:otherwise>
              </xsl:choose>
            </td>


          </xsl:if>

          <!-- End of assigned-to rows -->

          <xsl:if test="$type != 'lastmodified' and $type != 'assignedto'">

            <td align="center" title="{$tooltip}" class="{$css-td-class}">
              <xsl:call-template name="addJSEvent">
                <xsl:with-param name="node" select="$content"/>
              </xsl:call-template>
              <xsl:call-template name="publishstatus"/>
            </td>

            <td title="{$tooltip}" onclick="javascript:gotoLocation(document.getElementById('operation_{$type}_{position()}').href);"
                class="{$css-td-class}">
              <xsl:value-of select="@timestamp"/>
            </td>
          </xsl:if>
        </tr>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <xsl:template name="displaytableactivation">
    <xsl:param name="type"/>
    <xsl:param name="sectionxpath" select="/data/sections/section"/>

    <xsl:variable name="columncount">
        <xsl:text>10</xsl:text>
    </xsl:variable>

    <fieldset class="table-panel">

      <legend class="dashboard-window-header">
        <xsl:call-template name="display-type-header">
          <xsl:with-param name="type" select="'activation'"/>
          <xsl:with-param name="totalcontentcount" select="/data/sections/@contenttotalcount"/>
        </xsl:call-template>
      </legend>

      <table class="browsetable" width="100%" border="0" cellspacing="0" cellpadding="0">
        <!-- pendingPublishing -->
        <tr>
          <xsl:call-template name="tablecolumnheader">
            <xsl:with-param name="caption" select="'%fldType%'"/>
            <xsl:with-param name="sortable" select="'false'"/>
            <xsl:with-param name="width" select="$icon-column-width"/>
            <xsl:with-param name="align" select="'center'"/>
          </xsl:call-template>

          <xsl:call-template name="tablecolumnheader">
            <xsl:with-param name="caption" select="'%fldTitle%'"/>
            <xsl:with-param name="sortable" select="'false'"/>
          </xsl:call-template>

          <xsl:call-template name="tablecolumnheader">
            <xsl:with-param name="caption" select="'%fldStatus%'"/>
            <xsl:with-param name="width" select="'70'"/>
            <xsl:with-param name="align" select="'center'"/>
            <xsl:with-param name="sortable" select="'false'"/>
          </xsl:call-template>

          <xsl:call-template name="tablecolumnheader">
            <xsl:with-param name="caption" select="'%fldLastModified%'"/>
            <xsl:with-param name="width" select="'100'"/>
            <xsl:with-param name="sortable" select="'false'"/>
          </xsl:call-template>
        </tr>

        <xsl:variable name="loopcount">
          <xsl:choose>
            <xsl:when test="$maximize">
              10000
            </xsl:when>
            <xsl:otherwise>
              3
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <xsl:variable name="section-total-count" select="count($sectionxpath)"/>

        <xsl:for-each
            select="/data/sections/section[count( descendant::contentlocation[@activated = 'false' and @menuitemkey = ../../../../@menuitemkey] ) &gt; 0][ position() &lt; ($loopcount + 1) ]">

          <xsl:variable name="sectionkey" select="@menuitemkey"/>
          <xsl:variable name="section-position" select="position()"/>

          <xsl:variable name="secURL">
            <xsl:text>adminpage?page=950</xsl:text>
            <xsl:text>&amp;op=browse</xsl:text>
            <xsl:text>&amp;menuitemkey=</xsl:text><xsl:value-of select="@menuitemkey"/>
            <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="@sitekey"/>
          </xsl:variable>

          <tr>
            <th colspan="{$columncount}" onclick="javascript:gotoLocation(document.getElementById('operation_{$type}_{position()}').href);"
                class="list-group-name">
              <xsl:attribute name="title">%msgOpenSection%</xsl:attribute>
              <h3 style="margin:0">
                <xsl:call-template name="displaycontenticon">
                  <xsl:with-param name="sectionkey" select="@menuitemkey"/>
                  <xsl:with-param name="contentelem" select="."/>
                  <xsl:with-param name="contextelem" select="node()"/>
                </xsl:call-template>
                <a href="{$secURL}" id="operation_{$type}_{position()}">
                  <xsl:value-of select="@sitename"/>
                  <xsl:value-of select="@path"/>
                </a>
              </h3>
            </th>
          </tr>

          <xsl:for-each
              select="content[descendant::contentlocation[@activated = 'false'] and descendant::contentlocation[@menuitemkey = $sectionkey] ]">
            <xsl:call-template name="displayRow">
              <xsl:with-param name="type" select="$type"/>
              <xsl:with-param name="content" select="."/>
              <xsl:with-param name="is-last-section" select="$section-position = $section-total-count"/>
            </xsl:call-template>
          </xsl:for-each>

        </xsl:for-each>

        <xsl:variable name="contentcount">
          <xsl:value-of select="/data/sections/@contentcount"/>
        </xsl:variable>

        <xsl:variable name="totalcount">
          <xsl:value-of select="/data/sections/@contentinsectioncount"/>
        </xsl:variable>

        <xsl:if test="$contentcount = 0">
          <tr>
            <td colspan="10" class="browsetablecell no-action">
              <xsl:text>%msgNoContents%</xsl:text>
            </td>
          </tr>
        </xsl:if>

        <xsl:if test="not($maximize)">
          <tr>
            <td colspan="10" class="browsetablecell row-last" style="cursor: text">
              <xsl:if test="/data/sections/@contenttotalcount > 5">
                <a href="{$pageURL}&amp;maximize={$type}">
                  <xsl:text>%cmdMore%...</xsl:text>
                </a>
              </xsl:if>
              <xsl:text> (</xsl:text>
              <xsl:value-of select="/data/sections/@contenttotalcount"/>
              <xsl:text> %txtUnique% </xsl:text>
              <xsl:text> %txtTotal%)</xsl:text>
            </td>
          </tr>
        </xsl:if>

        <xsl:if test="$maximize = $type and /data/sections/@contenttotalcount &gt; $count">
          <tr>
            <td colspan="10" class="browsetablecell row-last">
              <xsl:call-template name="paging">
                <xsl:with-param name="url">
                  <xsl:text>adminpage?page=</xsl:text>
                  <xsl:value-of select="$page"/>
                  <xsl:text>&amp;op=page</xsl:text>
                  <xsl:text>&amp;maximize=</xsl:text><xsl:value-of select="$type"/>
                </xsl:with-param>
                <xsl:with-param name="index" select="$index"/>
                <xsl:with-param name="count" select="$count"/>
                <xsl:with-param name="totalcount" select="/data/sections/@contenttotalcount"/>
              </xsl:call-template>
            </td>
          </tr>
        </xsl:if>
      </table>
    </fieldset>
  </xsl:template>

  <xsl:template name="display-type-header">
    <xsl:param name="type" select="''"/>
    <xsl:param name="totalcontentcount"/>

    <xsl:variable name="expand-contract-icon">
      <xsl:if test="$totalcontentcount &gt; $default-content-count-pr-group">
        <xsl:choose>
          <xsl:when test="$maximize = $type">
            <a href="{$pageURL}" title="%cmdMinimizeTable%">
              <img src="images/icon-minus-grey.png" style="vertical-align:middle" alt="%cmdMinimizeTable%" title="%cmdMinimizeTable%"/>
            </a>
          </xsl:when>
          <xsl:otherwise>
            <a href="{$pageURL}&amp;maximize={$type}" title="%cmdMaximizeTable%">
              <img src="images/icon-plus-grey.png" style="vertical-align:middle" alt="%cmdMaximizeTable%" title="%cmdMaximizeTable%"/>
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:if>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$type = 'lastmodified'">
        <img src="images/page_java.gif" width="16" height="16" border="0" style="vertical-align:bottom"/>
        <xsl:copy-of select="$expand-contract-icon"/>
        <xsl:text> %headMyLastVisited%</xsl:text>
      </xsl:when>
      <xsl:when test="$type = 'drafts'">
        <img src="images/icon_state_draft.gif" width="16" height="16" border="0" style="vertical-align:bottom"/>
        <xsl:copy-of select="$expand-contract-icon"/>
        <xsl:text> %headMyDrafts%</xsl:text>
      </xsl:when>
      <xsl:when test="$type = 'assignedto'">
        <img src="images/icon_state_approve.gif" width="16" height="16" border="0" style="vertical-align:bottom"/>
        <xsl:copy-of select="$expand-contract-icon"/>
        <xsl:text> %headAssignedTo%</xsl:text>
      </xsl:when>
      <xsl:when test="$type = 'activation'">
        <img src="images/icon_content_unapprove.gif" width="16" height="16" border="0" style="vertical-align:bottom"/>
        <xsl:copy-of select="$expand-contract-icon"/>
        <xsl:text> %headWaitingForActivation%</xsl:text>
      </xsl:when>
    </xsl:choose>

  </xsl:template>

  <xsl:template name="addJSEvent">
    <xsl:param name="node"/>
    <!--
      Hack!!!
      Firefox < 2.0 is missing the click() method.
      https://bugzilla.mozilla.org/show_bug.cgi?id=148585
    -->
    <xsl:attribute name="onclick">
      <xsl:variable name="editbutton-id">
        <xsl:text>operation_edit_</xsl:text>
        <xsl:value-of select="@key"/>
        <xsl:choose>
          <xsl:when test="@has-draft = 'true'">
            <xsl:value-of select="versions/version[@status = 0]/@key"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@versionkey"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:text>javascript:if( document.all) {</xsl:text>
      <xsl:text>document.getElementById('</xsl:text>
      <xsl:value-of select="$editbutton-id"/>
      <xsl:text>').click();</xsl:text>
      <xsl:text>} else { document.location.href = document.getElementById('</xsl:text>
      <xsl:value-of select="$editbutton-id"/>
      <xsl:text>').href; }</xsl:text>
    </xsl:attribute>
  </xsl:template>
</xsl:stylesheet>
