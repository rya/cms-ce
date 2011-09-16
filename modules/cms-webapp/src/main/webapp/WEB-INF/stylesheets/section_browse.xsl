<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html"/>

    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/genericheader.xsl"/>
    <xsl:include href="common/sectionheader.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/sectioncommands.xsl"/>
    <xsl:include href="common/tablecolumnheader.xsl"/>
    <xsl:include href="common/tablerowpainter.xsl"/>
    <xsl:include href="common/formatdate.xsl"/>
    <xsl:include href="common/publishstatus.xsl"/>
    <xsl:include href="common/sectionoperations.xsl"/>
    <xsl:include href="common/operations_template.xsl"/>
    <xsl:include href="common/paging.xsl"/>
	  <xsl:include href="common/displaycontentpath.xsl"/>
    <xsl:include href="common/getsuffix.xsl"/>
    <xsl:include href="common/display-icon.xsl"/>
    <xsl:include href="common/display-content-icon.xsl"/>
	  <xsl:include href="menu_view_buttons.xsl"/>

    <xsl:param name="index"/>
    <xsl:param name="count"/>
    <xsl:param name="sortby"/>
    <xsl:param name="sortby-direction"/>

    <xsl:param name="uid"/>
    <xsl:param name="reload"/>
    <xsl:param name="sec"/>
    <xsl:param name="reordered" select="'false'"/>
    <xsl:param name="menuitemkey"/>
    <xsl:param name="debugpath"/>

    <xsl:variable name="menuitemelem" select="/contenttitles/model/selected-menuitem/menuitem"/>
    <xsl:variable name="menuelem" select="/contenttitles/model/selected-menu/menu"/>

    <xsl:variable name="createright" select="string($menuitemelem/@usercreate) = 'true'"/>
    <xsl:variable name="addright" select="string($menuitemelem/@useradd) = 'true' or string($menuitemelem/@userpublish) = 'true'"/>
    <xsl:variable name="publishright" select="string($menuitemelem/@userpublish) = 'true'"/>
    <xsl:variable name="updateright" select="string($menuitemelem/@userupdate) = 'true'"/>
    <xsl:variable name="deleteright" select="string($menuitemelem/@userdelete) = 'true'"/>
    <xsl:variable name="readright" select="string($menuitemelem/@userread) = 'true'"/>
    <xsl:variable name="administrateright" select="string($menuitemelem/@useradministrate) = 'true'"/>
    <xsl:variable name="parentadministrateright" select="string(/contenttitles/model/parent-to-selected-menuitem/menuitem/@useradministrate) = 'true'"/>
    <xsl:variable name="menuadministrateright" select="string($menuelem/@useradministrate) = 'true'"/>

    <xsl:variable name="parentkey">
         <xsl:choose>
             <xsl:when test="$menuitemelem/@parent">
                 <xsl:value-of select="$menuitemelem/@parent"/>
             </xsl:when>
             <xsl:otherwise>
                 <xsl:text>-1</xsl:text>
             </xsl:otherwise>
         </xsl:choose>
     </xsl:variable>

	<xsl:variable name="pageURL">
		<xsl:text>adminpage?op=browse&amp;page=</xsl:text>
		<xsl:value-of select="$page"/>
		<xsl:text>&amp;sec=</xsl:text>
		<xsl:value-of select="$sec"/>
		<xsl:text>&amp;menukey=</xsl:text>
		<xsl:value-of select="$menukey"/>
		<xsl:text>&amp;menuitemkey=</xsl:text>
		<xsl:value-of select="$menuitemkey"/>
    </xsl:variable>

    <xsl:variable name="ordered" select="/contenttitles/section[@key = $sec]/@ordered = 'true'"/>

  <xsl:template match="/">

    <html>
      <head>
        <link rel="stylesheet" type="text/css" href="css/admin.css"/>
        <link rel="stylesheet" type="text/css" href="javascript/tab.webfx.css"/>
        <script type="text/javascript" language="JavaScript" src="javascript/admin.js">//</script>
        <script type="text/javascript" language="JavaScript" src="javascript/tabpane.js">//</script>
        <script type="text/javascript" language="JavaScript" src="javascript/tabpane.js">//</script>
        <script type="text/javascript" language="JavaScript">
          /*
            Method: switchBrowseMode
          */
				  function switchBrowseMode()
					{
            var value = document.getElementById("browsemode").value;
            if (value == "menuitem")
						{
	            <xsl:text>document.location = "adminpage?page=850&amp;op=browse&amp;browsemode=menuitem&amp;parentmi=</xsl:text>
              <xsl:value-of select="$menuitemkey"/>
              <xsl:text>&amp;menukey=</xsl:text>
              <xsl:value-of select="$menukey"/>";
            }
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Method: setBatchButtonsEnabled
          */
					function setBatchButtonsEnabled()
					{
            var batchSelectorElements = document.getElementsByName('batchSelector');
            var batchSelectorElementsLn = batchSelectorElements.length;
            for ( var i = 0; i &lt; batchSelectorElementsLn; i++ )
            {
              batchSelectorElements[i].disabled = anyChecked('batch_operation') ? false : true;
            }
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Method: batchRemove
          */
					function batchRemove()
					{
						document['formAdmin']['op'].value = 'batchremove';
						document['formAdmin'].submit();
					}
          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Method: batchActivate
          */
          function batchActivate()
					{
						document['formAdmin']['op'].value = 'batchactivate';
						document['formAdmin'].submit();
					}
          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Method: batchDeactivate
          */
					function batchDeactivate()
					{
						document['formAdmin']['op'].value = 'batchdeactivate';
						document['formAdmin'].submit();
					}
          // -------------------------------------------------------------------------------------------------------------------------------

          /**
           * Method: popup_callback
           */
          function popup_callback( win )
					{

            if (!<xsl:value-of select="$reordered"/>)
            {
              var url = "<xsl:text>adminpage?op=add&amp;page=</xsl:text>
              <xsl:value-of select="$page"/>
              <xsl:text>&amp;sec=</xsl:text>
              <xsl:value-of select="$sec"/>
              <xsl:text>&amp;menukey=</xsl:text>
              <xsl:value-of select="$menukey"/>";

              win.addContentToSectionPage( url );
            }
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          var contentTypes = new Array;
          <xsl:for-each select="/contenttitles/section/contenttypes/contenttype">
            <xsl:text>contentTypes[</xsl:text>
            <xsl:value-of select="(position()-1)"/>
            <xsl:text>] = </xsl:text>
            <xsl:value-of select="@key"/>
            <xsl:text>;</xsl:text>
          </xsl:for-each>

          var contentKeys = new Array;
          <xsl:for-each select="/contenttitles/contenttitle">
            <xsl:text>contentKeys[</xsl:text>
            <xsl:value-of select="(position()-1)"/>
            <xsl:text>] = </xsl:text>
            <xsl:value-of select="@key"/>
            <xsl:text>;</xsl:text>
          </xsl:for-each>
          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Method: batchSelectorHandler
          */
          function batchSelectorHandler( selectElement )
          {
            var op = selectElement.value;

            if ( op == 'publish' )
            {
              batchActivate();
            }
            else if ( op == 'stopPublish' )
            {
              batchDeactivate();
            }
            else if ( op == 'remove' )
            {
              batchRemove();
            }
            else
            {
              //
            }
            selectElement.selectedIndex = 0;
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Method: setCount
          */
          function setCount( selectElement )
          {
            var selectedValue = selectElement.value;
            var url =' <xsl:value-of select="$pageURL"/><xsl:text>&amp;index=</xsl:text><xsl:value-of select="$index"/><xsl:text>&amp;count=</xsl:text>' + selectedValue;
            document.location = url;
          }

        </script>

      </head>

      <body>
        <h1>
          <xsl:variable name="page" select="850"/>
          <xsl:call-template name="genericheader"/>
          <a href="adminpage?op=browse&amp;page={$page}&amp;menuitemkey=-1&amp;selectedunitkey={$selectedunitkey}&amp;menukey={$menukey}">%headPageBuilder%</a>
          <xsl:call-template name="display-path-to-selected-menuitem"/>
        </h1>

        <xsl:if test="$reload = 'true'">
          <script type="text/javascript" language="JavaScript">window.top.frames['leftFrame'].refreshMenu();</script>
        </xsl:if>

        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td class="browse_title_buttonrow_seperator">
              <img src="images/1x1.gif"/>
            </td>
          </tr>
          <xsl:call-template name="sectionlist"/>
        </table>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="sectionlist">
    <tr>
      <td>
        <select name="browsemode" id="browsemode" onchange="switchBrowseMode()">
          <option value="menuitem">%fldView%: %optShowPages%</option>
          <option value="section" selected="selected">%fldView%: %optShowSection%</option>
        </select>
        <xsl:text>&nbsp;</xsl:text>
        <xsl:call-template name="menu_view_buttons">
          <xsl:with-param name="menuelem" select="$menuelem"/>
          <xsl:with-param name="menuitemelem" select="$menuitemelem"/>
          <xsl:with-param name="createright" select="$createright"/>
          <xsl:with-param name="addright" select="$addright"/>
          <xsl:with-param name="updateright" select="$updateright"/>
          <xsl:with-param name="publishright" select="$publishright"/>
          <xsl:with-param name="deleteright" select="$deleteright"/>
          <xsl:with-param name="administrateright" select="$administrateright"/>
          <xsl:with-param name="menuadministrateright" select="$menuadministrateright"/>
          <xsl:with-param name="parentadministrateright" select="$parentadministrateright"/>
          <xsl:with-param name="highlight" select="-1"/>
          <xsl:with-param name="browsemode" select="'section'"/>
          <xsl:with-param name="parentkey" select="$parentkey"/>
        </xsl:call-template>
      </td>
    </tr>

    <xsl:if test="/contenttitles/section/contenttypes/contenttype or /contenttitles/contenttitle[not(@removed = 'true')]">
      <tr>
        <td>
          <xsl:variable name="page" select="950"/>
          <form name="formAdmin" method="post" action="adminpage?page={$page}&amp;sec={$sec}&amp;menukey={$menukey}">
            <xsl:if test="$ordered">
              <input type="hidden" name="timestamp" value="{/contenttitles/section/@timestamp}"/>
            </xsl:if>

            <input type="hidden" name="reordered" value="{$reordered}"/>
            <input type="hidden" name="op" value="save"/>
            <input type="hidden" name="menuitemkey" value="{$menuitemkey}"/>

            <fieldset>
              <xsl:attribute name="class">
                <xsl:text>table-panel</xsl:text>
                <xsl:if test="$reordered = 'true'">
                  <xsl:text> table-panel-red</xsl:text>
                </xsl:if>
              </xsl:attribute>

              <table border="0" cellpadding="0" cellspacing="0" style="width:100%">
                <tr>
                  <td>
                    <xsl:call-template name="batchControls"/>
                  </td>
                </tr>
              </table>

              <table width="100%" cellspacing="0" cellpadding="0" class="browsetable">
                <tr>
                  <!-- Display checkbox header -->
                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="align" select="'center'"/>
                    <xsl:with-param name="width" select="'20'"/>
                    <xsl:with-param name="sortable" select="'false'"/>
                    <xsl:with-param name="checkboxname" select="'batch_operation'"/>
                    <xsl:with-param name="checkBoxOnClickFallback" select="'setBatchButtonsEnabled'"/>
                  </xsl:call-template>

                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="align" select="'center'"/>
                    <xsl:with-param name="width" select="'70'"/>
                    <xsl:with-param name="caption" select="'%fldType%'"/>
                    <xsl:with-param name="sortable" select="'false'"/>
                  </xsl:call-template>

                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="caption" select="'%fldTitle%'"/>
                    <xsl:with-param name="sortable" select="'false'"/>
                  </xsl:call-template>

                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="width" select="'100'"/>
                    <xsl:with-param name="caption" select="'%fldContentType%'"/>
                    <xsl:with-param name="sortable" select="'false'"/>
                  </xsl:call-template>

                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="width" select="'100'"/>
                    <xsl:with-param name="align" select="'center'"/>
                    <xsl:with-param name="caption" select="'%fldModified%'"/>
                    <xsl:with-param name="sortable" select="'false'"/>
                  </xsl:call-template>

                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="width" select="'90'"/>
                    <xsl:with-param name="align" select="'center'"/>
                    <xsl:with-param name="caption" select="'%fldStatus%'"/>
                    <xsl:with-param name="sortable" select="'false'"/>
                  </xsl:call-template>

                  <xsl:if test="$ordered">
                    <xsl:call-template name="tablecolumnheader">
                      <xsl:with-param name="width" select="'60'"/>
                      <xsl:with-param name="caption" select="''"/>
                      <xsl:with-param name="sortable" select="'false'"/>
                    </xsl:call-template>
                  </xsl:if>

                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="width" select="'80'"/>
                    <xsl:with-param name="caption" select="''"/>
                    <xsl:with-param name="sortable" select="'false'"/>
                  </xsl:call-template>
                </tr>

                <xsl:variable name="unpublished_count" select="count(/contenttitles/contenttitle[@approved = 'false' and not(@removed = 'true')])"/>

                <xsl:for-each select="/contenttitles/contenttitle[not(@removed = 'true')]">
                  <xsl:sort select="@approved"/>

                  <xsl:variable name="cell-first-class">
                    <xsl:text>cell-first</xsl:text>
                    <xsl:if test="position() = last()">
                      <xsl:text> row-last</xsl:text>
                    </xsl:if>
                  </xsl:variable>

                  <xsl:variable name="tooltip-text">
                    <xsl:choose>
                      <xsl:when test="@state = 0">%msgClickToEdit%</xsl:when>
                      <xsl:otherwise>%msgClickToOpen%</xsl:otherwise>
                    </xsl:choose>
                  </xsl:variable>

                  <xsl:if test="position() = 1 and $unpublished_count > 0">
                    <tr>
                      <th colspan="10" class="list-group-name" style="padding-left: 8px">
                        <img src="images/icon_content_unapprove.gif" alt="%grpUnPublished%" title="%grpUnPublished%"/>
                        <xsl:text>%grpUnPublished%</xsl:text>
                      </th>
                    </tr>
                  </xsl:if>

                  <xsl:if test="position() = $unpublished_count + 1">
                    <tr>
                      <th colspan="10" class="list-group-name" style="padding-left: 8px">
                        <img src="images/icon_content_approve.gif" alt="%grpPublished%" title="%grpPublished%"/>
                        <xsl:text>%grpPublished%</xsl:text>
                      </th>
                    </tr>
                  </xsl:if>

                  <xsl:variable name="cell-class">
                    <xsl:choose>
                      <xsl:when test="@modified = 'true' and ($ordered)">browsetablecellbold</xsl:when>
                      <xsl:when test="@approved = 'true'">browsetablecell</xsl:when>
                      <xsl:otherwise>browsetablecelldisabled hand</xsl:otherwise>
                    </xsl:choose>
                    <xsl:if test="position() = last()">
                      <xsl:text> row-last</xsl:text>
                    </xsl:if>
                  </xsl:variable>

                  <xsl:variable name="key" select="@key"/>
                  <xsl:variable name="contenttypekey" select="@contenttypekey"/>
                  <xsl:variable name="categorykey" select="@categorykey"/>

                  <tr>
                    <xsl:call-template name="tablerowpainter"/>

                    <!-- Display checkbox column -->
                    <td class="{$cell-class}">
                      <input type="checkbox" style="margin:0;padding:0;border:none;" name="batch_operation" value="{@key}" onclick="javascript: setBatchButtonsEnabled();"/>
                      <input type="hidden" name="ischild_{@key}" value="{@child}"/>
                    </td>

                    <!-- Display icon column -->
                    <td class="{$cell-class}" style="text-align: left">
                      <xsl:call-template name="addJSEvent">
                        <xsl:with-param name="node" select="."/>
                      </xsl:call-template>

                      <xsl:call-template name="display-content-icon">
                        <xsl:with-param name="content-node" select="."/>
                        <xsl:with-param name="title" select="."/>
                        <xsl:with-param name="contenthandler-class-name" select="@contenthandler-class-name"/>
                        <xsl:with-param name="is-home" select="@is-home"/>
                        <xsl:with-param name="is-link-to-menuitem" select="@is-link-to-menuitem"/>
                        <xsl:with-param name="content-type-name" select="/contenttitles/contenttypes/contenttype[@key = current()/@contenttypekey]/name"/>
                      </xsl:call-template>
                    </td>

                    <!-- Display title column -->
                    <td class="{$cell-class}" title="{$tooltip-text}">
                      <xsl:call-template name="addJSEvent">
                        <xsl:with-param name="node" select="."/>
                      </xsl:call-template>
                        <div style="font-weight: bold"><xsl:value-of select="."/></div>
                        <span style="color: gray"><xsl:value-of select="@path-to-content"/></span>
                    </td>

                    <!-- Display contenttype column -->
                    <td class="{$cell-class}" title="{$tooltip-text}">
                      <xsl:call-template name="addJSEvent">
                        <xsl:with-param name="node" select="."/>
                      </xsl:call-template>
                      <xsl:value-of select="/node()/contenttypes/contenttype[@key = $contenttypekey]/name"/>
                    </td>

                    <!-- Display modified column -->
                    <td class="{$cell-class}" style="text-align: center" title="{$tooltip-text}">
                      <xsl:call-template name="formatdatetime">
                        <xsl:with-param name="date" select="@timestamp"/>
                      </xsl:call-template>
                    </td>

                    <!-- Display status column -->
                    <td class="{$cell-class}" style="text-align: center" title="{$tooltip-text}">
                      <xsl:call-template name="addJSEvent">
                        <xsl:with-param name="node" select="."/>
                      </xsl:call-template>
                      <xsl:call-template name="publishstatus">
                        <xsl:with-param name="key" select="@key"/>
                        <xsl:with-param name="state" select="@state"/>
                        <xsl:with-param name="has-draft" select="@has-draft"/>
                      </xsl:call-template>
                    </td>

                    <!-- Display move operation column -->
                    <xsl:if test="$ordered">
                      <td align="center" class="{$cell-class}">
                        <xsl:choose>
                          <xsl:when test="@approved = 'true'">
                            <xsl:call-template name="button">
                              <xsl:with-param name="style" select="'flat'"/>
                              <xsl:with-param name="type" select="'link'"/>
                              <xsl:with-param name="image" select="'images/icon_move_up.gif'"/>
                              <xsl:with-param name="image-disabled" select="'images/icon_move_up-disabled.gif'"/>
                              <xsl:with-param name="disabled" select="not($publishright)"/>
                              <xsl:with-param name="tooltip" select="'%altContentMoveUp%'"/>
                              <xsl:with-param name="href">
                                <xsl:text>adminpage?page=</xsl:text>
                                <xsl:value-of select="$page"/>
                                <xsl:text>&amp;op=moveup&amp;key=</xsl:text>
                                <xsl:value-of select="$key"/>
                                <xsl:text>&amp;sec=</xsl:text>
                                <xsl:value-of select="$sec"/>
                                <xsl:text>&amp;menukey=</xsl:text>
                                <xsl:value-of select="$menukey"/>
                                <xsl:text>&amp;menuitemkey=</xsl:text>
                                <xsl:value-of select="$menuitemkey"/>
                              </xsl:with-param>
                            </xsl:call-template>

                            <img border="0" src="images/1x1.gif" height="1" width="20"/>

                            <xsl:call-template name="button">
                              <xsl:with-param name="style" select="'flat'"/>
                              <xsl:with-param name="type" select="'link'"/>
                              <xsl:with-param name="image" select="'images/icon_move_down.gif'"/>
                              <xsl:with-param name="image-disabled" select="'images/icon_move_down-disabled.gif'"/>
                              <xsl:with-param name="disabled" select="not($publishright)"/>
                              <xsl:with-param name="tooltip" select="'%altContentMoveDown%'"/>
                              <xsl:with-param name="href">
                                <xsl:text>adminpage?page=</xsl:text>
                                <xsl:value-of select="$page"/>
                                <xsl:text>&amp;op=movedown&amp;key=</xsl:text>
                                <xsl:value-of select="$key"/>
                                <xsl:text>&amp;sec=</xsl:text>
                                <xsl:value-of select="$sec"/>
                                <xsl:text>&amp;menukey=</xsl:text>
                                <xsl:value-of select="$menukey"/>
                                <xsl:text>&amp;menuitemkey=</xsl:text>
                                <xsl:value-of select="$menuitemkey"/>
                              </xsl:with-param>
                            </xsl:call-template>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:text>&nbsp;</xsl:text>
                          </xsl:otherwise>
                        </xsl:choose>
                      </td>
                    </xsl:if>

                    <xsl:variable name="version-key">
                      <xsl:choose>
                        <xsl:when test="@has-draft = 'true'">
                          <xsl:value-of select="@draft-key"/>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:value-of select="@key"/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:variable>

                    <!-- Display general operation column -->
                    <td align="center" class="{$cell-class}">
                      <xsl:call-template name="sectionoperations">
                        <xsl:with-param name="key" select="$key"/>
                        <xsl:with-param name="versionkey" select="$version-key"/>
                        <xsl:with-param name="sec" select="$sec"/>
                        <xsl:with-param name="page" select="$page"/>
                        <xsl:with-param name="unitkey" select="@unitkey"/>
                        <xsl:with-param name="menukey" select="$menukey"/>
                        <xsl:with-param name="sitekey" select="$menukey"/>
                        <xsl:with-param name="previewmenukey" select="$menukey"/>
                        <xsl:with-param name="contentpage" select="(@contenttypekey + 999)"/>
                        <xsl:with-param name="cat" select="@categorykey"/>
                        <xsl:with-param name="previewmenuitemkey" select="@previewpage"/>
                        <xsl:with-param name="menuitemkey" select="$menuitemkey"/>
                        <xsl:with-param name="disablepreview" select="string(not($menuitemkey))"/>
                        <xsl:with-param name="approved" select="@approved = 'true'"/>
                        <xsl:with-param name="addright" select="$addright"/>
                        <xsl:with-param name="approveright" select="$publishright"/>
                        <xsl:with-param name="publishright" select="$publishright"/>
                        <xsl:with-param name="ordered" select="$ordered"/>
                        <xsl:with-param name="reordered" select="$reordered"/>
                        <xsl:with-param name="state" select="@state"/>
                        <xsl:with-param name="contenttypekey" select="@contenttypekey"/>
                      </xsl:call-template>
                    </td>
                  </tr>
                </xsl:for-each>
              </table>
              <table border="0" cellpadding="0" cellspacing="0" style="width:100%">
                <tr>
                  <td>
                    <xsl:call-template name="batchControls">
                      <xsl:with-param name="top" select="false()"/>
                    </xsl:call-template>
                  </td>
                </tr>
              </table>
            </fieldset>
          </form>
        </td>
      </tr>
    </xsl:if>
  </xsl:template>

  <xsl:template name="batchControls">
    <xsl:param name="top" select="true()"/>
    <xsl:if test="$reordered = 'true' and $top = true()">
      <p>
        <span class="warning-message">%msgModifedSectionStructure%</span>
      </p>
    </xsl:if>

    <xsl:variable name="css-margins">
      <xsl:choose>
        <xsl:when test="$top = true()">operation-top</xsl:when>
        <xsl:otherwise>operation-bottom</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <table border="0" cellpadding="0" cellspacing="0" style="width:100%" class="{$css-margins}">
      <tr>
        <td>
          <select disabled="true" name="batchSelector" onchange="batchSelectorHandler( this )" class="section-operation">
            <option value="0">%cmdChooseAction% ...</option>
            <option value="publish">%cmdBatchActivate%</option>
            <option value="stopPublish">%cmdBatchDeactivate%</option>
            <option value="remove">%cmdBatchRemove%</option>
          </select>
          <xsl:if test="$reordered = 'true'">
            <xsl:text> </xsl:text>
            <xsl:call-template name="button">
              <xsl:with-param name="type" select="'submit'"/>
              <xsl:with-param name="caption" select="'%cmdSave%'"/>
            </xsl:call-template>
            <xsl:text>&nbsp;</xsl:text>
            <xsl:call-template name="button">
              <xsl:with-param name="type" select="'cancel'"/>
              <xsl:with-param name="referer" select="$pageURL"/>
            </xsl:call-template>
          </xsl:if>
        </td>
        <td align="center">
          <xsl:if test="not($ordered)">
            <xsl:call-template name="paging">
              <xsl:with-param name="url">
                <xsl:text>adminpage?op=browse&amp;page=</xsl:text>
                <xsl:value-of select="$page"/>
                <xsl:text>&amp;sec=</xsl:text>
                <xsl:value-of select="$sec"/>
                <xsl:text>&amp;menukey=</xsl:text>
                <xsl:value-of select="$menukey"/>
                <xsl:text>&amp;menuitemkey=</xsl:text>
                <xsl:value-of select="$menuitemkey"/>
              </xsl:with-param>
              <xsl:with-param name="index" select="$index"/>
              <xsl:with-param name="count" select="$count"/>
              <xsl:with-param name="totalcount" select="/contenttitles/@totalcount"/>
            </xsl:call-template>
          </xsl:if>
        </td>
        <td align="right">
          <xsl:if test="not($ordered)">
            <xsl:call-template name="perPage">
              <xsl:with-param name="index" select="$index"/>
              <xsl:with-param name="count" select="number($count)"/>
              <xsl:with-param name="totalcount" select="/contenttitles/@totalcount"/>
            </xsl:call-template>
            &nbsp;
            <xsl:call-template name="countSelect">
              <xsl:with-param name="count" select="$count"/>
            </xsl:call-template>
          </xsl:if>
        </td>
      </tr>
    </table>

    <xsl:if test="$reordered = 'true' and $top = false()">
      <p>
        <span class="warning-message">%msgModifedSectionStructure%</span>
      </p>
    </xsl:if>
  </xsl:template>

  <xsl:template name="display-path-to-selected-menuitem">

    <xsl:for-each select="/contenttitles/model/selected-menuitem-path/menuitem">
      / <a href="adminpage?op=browse&amp;page={$page}&amp;menukey={$menukey}&amp;menuitemkey={@key}"><xsl:value-of select="name"/></a>
    </xsl:for-each>

  </xsl:template>

  <xsl:template name="addJSEvent">
    <xsl:param name="node"/>
    <xsl:variable name="editbutton-id">
      <xsl:text>operation_edit_</xsl:text>
      <xsl:value-of select="$node/@key"/>
      <xsl:choose>
        <xsl:when test="$node/@has-draft = 'true'">
          <xsl:value-of select="$node/@draft-key"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$node/@key"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:attribute name="onclick">
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

