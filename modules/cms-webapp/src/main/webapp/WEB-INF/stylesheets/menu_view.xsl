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

  <xsl:include href="common/genericheader.xsl"/>
  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:include href="common/javascriptPreload.xsl"/>
  <xsl:include href="common/tablecolumnheader.xsl"/>
  <xsl:include href="common/tablerowpainter.xsl"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="common/displaycolumn_access.xsl"/>
  <xsl:include href="menu_view_buttons.xsl"/>
  <xsl:include href="tree/configs/default.xsl"/>
  <xsl:include href="common/browse_table_js.xsl"/>

  <xsl:param name="sortby" select="'name'"/>
  <xsl:param name="sortby-direction" select="'ascending'"/>
  <xsl:param name="sitepath"/>
  <xsl:param name="debugpath"/>

  <xsl:variable name="pageURL">
    <xsl:text>adminpage?page=</xsl:text>
    <xsl:value-of select="$page"/>
  </xsl:variable>

  <xsl:param name="changed" select="nope"/>
  <xsl:param name="highlight" select="-1"/>
  <xsl:param name="parentmi" select="-1"/>
  <xsl:param name="reload"/>

  <xsl:param name="move_menuitem"/>
  <xsl:param name="move_from_parent"/>
  <xsl:param name="move_to_parent"/>

  <xsl:variable name="selectedmenuitem" select="/model/selected-menuitem/menuitem"/>
  <xsl:variable name="parentmenuitem" select="/model/selected-menuitem/menuitem"/>
  <xsl:variable name="sectionmode" select="$selectedmenuitem/page/@pagetemplatetype = 6 or $selectedmenuitem/@type = 'section' or $selectedmenuitem/page/@pagetemplatetype = 4"/>

  <xsl:variable name="menuelem" select="/model/selected-menu/menu"/>

  <xsl:variable name="menucreate" select="$menuelem/@usercreate = 'true'"/>
  <xsl:variable name="menuadd" select="$menuelem/@useradd = 'true'"/>
  <xsl:variable name="menupublish" select="$menuelem/@userpublish = 'true'"/>
  <xsl:variable name="menuupdate" select="$menuelem/@userupdate = 'true'"/>
  <xsl:variable name="menuadministrate" select="$menuelem/@useradministrate = 'true'"/>

  <xsl:variable name="createright" select="($parentmi = -1 and $menucreate) or ($parentmi != -1 and $selectedmenuitem/@usercreate = 'true')"/>
  <xsl:variable name="addright" select="($parentmi = -1 and $menuadd) or ($parentmi != -1 and ( $selectedmenuitem/@useradd = 'true' or $selectedmenuitem/@userpublish = 'true' ))"/>
  <xsl:variable name="publishright" select="($parentmi = -1 and $menupublish) or ($parentmi != -1 and $selectedmenuitem/@userpublish = 'true')"/>
  <xsl:variable name="updateright" select="($parentmi = -1 and $menuupdate) or ($parentmi != -1 and $selectedmenuitem/@userupdate = 'true')"/>
  <xsl:variable name="deleteright" select="($parentmi = -1 and $menuupdate) or ($parentmi != -1 and $selectedmenuitem/@userdelete = 'true')"/>
  <xsl:variable name="administrateright" select="($parentmi = -1 and $menuadministrate) or ($parentmi != -1 and $selectedmenuitem/@useradministrate = 'true')"/>

  <xsl:variable name="parent-to-selected-menuitem" select="/model/parent-to-selected-menuitem/menuitem"/>
  <xsl:variable name="parentkey">
    <xsl:choose>
      <xsl:when test="count($parent-to-selected-menuitem) = 1"><xsl:value-of select="$parent-to-selected-menuitem/@key"/></xsl:when>
      <xsl:otherwise>-1</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="parentadministrateright" select="( $parentkey = -1 and $menuadministrate ) or ( $parentkey != -1 and $parent-to-selected-menuitem/@useradministrate = 'true') "/>

  <xsl:template match="/">
    <html>
      <head>
        <xsl:call-template name="javascriptPreload"/>

        <link href="css/admin.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" language="JavaScript">

          <xsl:if test="$reload = 'true'">
            window.top.frames['leftFrame'].refreshMenu();
          </xsl:if>

          function switchBrowseMode()
          {
            var value = document.getElementById("browsemode").value;
            if (value == "section")
            {
              document.location = "adminpage?page=950&amp;op=browse&amp;browsemode=section&amp;menuitemkey=<xsl:value-of select="$parentmi"/>&amp;menukey=<xsl:value-of select="$menukey"/>";
            }
          }
        </script>
      </head>

      <body>

        <!--
        count($parent-to-selected-menuitem) <xsl:value-of select="count($parent-to-selected-menuitem)"/><br/>
        sectionmode <xsl:value-of select="$sectionmode"/><br/>
			  menucreate <xsl:value-of select="$menucreate"/><br/>
			  menuadd <xsl:value-of select="$menuadd"/><br/>
			  menupublish <xsl:value-of select="$menupublish"/><br/>
			  menuupdate: <xsl:value-of select="$menuupdate"/><br/>
			  menuadministrate: <xsl:value-of select="$menuadministrate"/><br/>
				createright: <xsl:value-of select="$createright"/><br/>
				addright: <xsl:value-of select="$addright"/><br/>
				publishright: <xsl:value-of select="$publishright"/><br/>
				updateright: <xsl:value-of select="$updateright"/><br/>
				deleteright: <xsl:value-of select="$deleteright"/><br/>
				administrateright: <xsl:value-of select="$administrateright"/><br/>
				parentkey: <xsl:value-of select="$parentkey"/><br/>
				parentadministrateright: <xsl:value-of select="$parentadministrateright"/><br/>
        -->

        <h1>
          <xsl:call-template name="genericheader"/>
          <a href="adminpage?op=browse&amp;page={$page}&amp;parentmi=-1&amp;menukey={$menukey}">%headPageBuilder%</a>
          <xsl:call-template name="display-path-to-selected-menuitem"/>
        </h1>

        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td class="browse_title_buttonrow_seperator"><img src="images/1x1.gif"/></td>
          </tr>
          <tr>
            <td>
              <xsl:if test="$sectionmode">
                <select name="browsemode" id="browsemode" onchange="switchBrowseMode()">
                  <option value="menuitem" selected="selected">%fldView%: %optShowPages%</option>
                  <option value="section">%fldView%: %optShowSection%</option>
                </select>
                <xsl:text>&nbsp;</xsl:text>
              </xsl:if>

              <xsl:call-template name="menu_view_buttons">
                <xsl:with-param name="menuelem" select="$menuelem"/>
                <xsl:with-param name="menuitemelem" select="$selectedmenuitem"/>
                <xsl:with-param name="createright" select="$createright"/>
                <xsl:with-param name="addright" select="$addright"/>
                <xsl:with-param name="publishright" select="$publishright"/>
                <xsl:with-param name="updateright" select="$updateright"/>
                <xsl:with-param name="deleteright" select="$deleteright"/>
                <xsl:with-param name="administrateright" select="$administrateright"/>
                <xsl:with-param name="menuadministrateright" select="$menuadministrate"/>
                <xsl:with-param name="parentadministrateright" select="$parentadministrateright"/>
                <xsl:with-param name="highlight" select="$highlight"/>
                <xsl:with-param name="parentkey" select="$parentkey"/>
              </xsl:call-template>

            </td>
          </tr>
          <tr>
            <td>

              <form method="get" action="adminpage">
                <input type="hidden" name="page" value="{$page}"/>
                <input type="hidden" name="op" value="update"/>

                <fieldset>
                  <xsl:attribute name="class">
                    <xsl:text>table-panel</xsl:text>
                    <xsl:if test="$changed = 'yep'">
                      <xsl:text> table-panel-red</xsl:text>
                    </xsl:if>
                  </xsl:attribute>
                  <xsl:if test="$changed = 'yep'">
                    <span class="warning-message">
                      <xsl:call-template name="msgStructureChanged">
                        <xsl:with-param name="top" select="true()"/>
                      </xsl:call-template>
                    </span>
                  </xsl:if>
                                
                  <table width="100%" cellspacing="0" cellpadding="0" class="browsetable">
                    <xsl:if test="not($changed = 'yep')">
                      <xsl:attribute name="style">margin:0</xsl:attribute>
                    </xsl:if>
                    <tr>

                      <xsl:call-template name="tablecolumnheader">
                        <xsl:with-param name="width" select="'20'" />
                        <xsl:with-param name="caption" select="''" />
                        <xsl:with-param name="sortable" select="'false'" />
                      </xsl:call-template>

                      <xsl:call-template name="tablecolumnheader">
                        <xsl:with-param name="caption" select="'%headTitle%'" />
                        <xsl:with-param name="sortable" select="'false'" />
                      </xsl:call-template>

                      <xsl:call-template name="tablecolumnheader">
                        <xsl:with-param name="caption" select="'%headType%'"/>
                        <xsl:with-param name="sortable" select="'false'"/>
                      </xsl:call-template>

                      <xsl:call-template name="tablecolumnheader">
                        <xsl:with-param name="align" select="'center'" />
                        <xsl:with-param name="width" select="'120'" />
                        <xsl:with-param name="caption" select="'%headMove%'" />
                        <xsl:with-param name="sortable" select="'false'" />
                      </xsl:call-template>

                      <xsl:call-template name="tablecolumnheader">
                        <xsl:with-param name="width" select="'120'" />
                        <xsl:with-param name="caption" select="''" />
                        <xsl:with-param name="sortable" select="'false'" />
                      </xsl:call-template>

                    </tr>

                    <xsl:for-each select="/model/menuitems-to-list/menuitem">
                      <xsl:call-template name="showmenuitem">
                        <xsl:with-param name="parentmenuitem" select="$selectedmenuitem"/>
                        <xsl:with-param name="menuitem" select="."/>
                        <xsl:with-param name="indent" select="''"/>
                      </xsl:call-template>
                    </xsl:for-each>
                  </table>

                  <xsl:if test="$changed = 'yep'">
                    <input type="hidden" name="menukey" value="{$menukey}"/>
                    <input type="hidden" name="parentmi" value="{$parentmi}"/>
                    <input type="hidden" name="subop" value="{$subop}"/>
                    <input type="hidden" name="move_menuitem" value="{$move_menuitem}"/>
                    <input type="hidden" name="move_from_parent" value="{$move_from_parent}"/>
                    <input type="hidden" name="move_to_parent" value="{$move_to_parent}"/>
                    <input type="hidden" name="reload" value="true"/>
                    <xsl:call-template name="msgStructureChanged"/>
                  </xsl:if>
                </fieldset>
              </form>
            </td>
          </tr>
        </table>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="showmenuitem">
    <!--
        this template is used to display a single menuitem.

        it is called recursivly on all the menuitems that are
        children of the menuitem sent to this template.
    -->
    <xsl:param name="parentmenuitem"/>
    <xsl:param name="menuitem"/>
    <xsl:param name="indent"/>
    <xsl:param name="shade"/>

    <tr>
      <xsl:call-template name="tablerowpainter"/>

      <xsl:variable name="css-class">
        <xsl:text>browsetablecell</xsl:text>
        <xsl:if test="position() = last()">
          <xsl:text> row-last</xsl:text>
        </xsl:if>
      </xsl:variable>

      <td width="30" align="center" class="{$css-class}" title="%msgClickToEdit%">
        <xsl:call-template name="addJSEvent">
          <xsl:with-param name="key" select="@key"/>
        </xsl:call-template>
        <table cellpadding="0" cellspacing="0" border="0">
          <tr>
            <td align="center">
              <xsl:apply-templates select="$menuitem" mode="iconimage"/>
            </td>
          </tr>
        </table>
      </td>
      <td nowrap="nowrap" align="left" class="{$css-class}" title="%msgClickToEdit%">
        <xsl:call-template name="addJSEvent">
          <xsl:with-param name="key" select="@key"/>
        </xsl:call-template>
        <xsl:value-of select="$indent"/>
        <span>
          <xsl:value-of select="$menuitem/name"/>
        </span>
      </td>

      <td align="left" class="{$css-class}" title="%msgClickToEdit%">
        <xsl:call-template name="addJSEvent">
          <xsl:with-param name="key" select="@key"/>
        </xsl:call-template>
        <xsl:choose>
          <xsl:when test="$menuitem/@type = 'label'">
            %menuItemTypeLabel%
          </xsl:when>
          <xsl:when test="$menuitem/@type = 'url'">
            %menuItemTypeURL%
          </xsl:when>
          <xsl:when test="$menuitem/@type = 'section'">
            %menuItemTypeSection%
          </xsl:when>
          <xsl:when test="$menuitem/@type = 'shortcut'">
            %menuItemTypeShortcut%
          </xsl:when>
          <xsl:otherwise>
            %menuItemTypePage% (<xsl:value-of select="$menuitem/page/@pagetemplatename"/>)
          </xsl:otherwise>
        </xsl:choose>
      </td>

      <xsl:call-template name="movemenuitem">
        <xsl:with-param name="id" select="@key"/>
        <xsl:with-param name="css-class" select="$css-class"/>
      </xsl:call-template>

      <xsl:call-template name="menuitemoperations">
        <xsl:with-param name="menuitem" select="."/>
        <xsl:with-param name="id" select="@key"/>
        <xsl:with-param name="css-class" select="$css-class"/>
      </xsl:call-template>

    </tr>
  </xsl:template>

  <xsl:template name="movemenuitem">
    <xsl:param name="id"/>
    <xsl:param name="css-class"/>

    <td align="center" class="{$css-class}">

      <xsl:variable name="disabled_message">
        <xsl:if test="not($administrateright)">%msgNoRightsToDoThis%</xsl:if>
      </xsl:variable>

      <xsl:call-template name="button">
        <xsl:with-param name="style" select="'flat'"/>
        <xsl:with-param name="type" select="'link'"/>
        <xsl:with-param name="image" select="'images/icon_move_up.gif'"/>
        <xsl:with-param name="image-disabled" select="'images/icon_move_up-disabled.gif'"/>
        <xsl:with-param name="disabled" select="not($administrateright)"/>
        <xsl:with-param name="tooltip" select="$disabled_message"/>
        <xsl:with-param name="href">
          <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
          <xsl:text>&amp;op=moveup&amp;key=</xsl:text><xsl:value-of select="$id"/>
          <xsl:text>&amp;parentmi=</xsl:text><xsl:value-of select="$parentmi"/>
          <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
          <xsl:text>&amp;subop=</xsl:text><xsl:value-of select="$subop"/>
          <xsl:text>&amp;move_menuitem=</xsl:text><xsl:value-of select="$move_menuitem"/>
          <xsl:text>&amp;move_from_parent=</xsl:text><xsl:value-of select="$move_from_parent"/>
          <xsl:text>&amp;move_to_parent=</xsl:text><xsl:value-of select="$move_to_parent"/>
        </xsl:with-param>
      </xsl:call-template>

      <img border="0" src="images/1x1.gif" height="1" width="20"/>

      <xsl:call-template name="button">
        <xsl:with-param name="style" select="'flat'"/>
        <xsl:with-param name="type" select="'link'"/>
        <xsl:with-param name="image" select="'images/icon_move_down.gif'"/>
        <xsl:with-param name="image-disabled" select="'images/icon_move_down-disabled.gif'"/>
        <xsl:with-param name="disabled" select="not($administrateright)"/>
        <xsl:with-param name="tooltip" select="$disabled_message"/>
        <xsl:with-param name="href">
          <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
          <xsl:text>&amp;op=movedown&amp;key=</xsl:text><xsl:value-of select="$id"/>
          <xsl:text>&amp;parentmi=</xsl:text><xsl:value-of select="$parentmi"/>
          <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
          <xsl:text>&amp;subop=</xsl:text><xsl:value-of select="$subop"/>
          <xsl:text>&amp;move_menuitem=</xsl:text><xsl:value-of select="$move_menuitem"/>
          <xsl:text>&amp;move_from_parent=</xsl:text><xsl:value-of select="$move_from_parent"/>
          <xsl:text>&amp;move_to_parent=</xsl:text><xsl:value-of select="$move_to_parent"/>
        </xsl:with-param>
      </xsl:call-template>

    </td>
  </xsl:template>


  <xsl:template name="menuitemoperations">
    <!--
            this template is used to display the operations for
            each menuitem.
        -->
    <xsl:param name="menuitem"/>
    <xsl:param name="id"/>
    <xsl:param name="css-class"/>

    <td align="center" nowrap="nowrap" class="{$css-class}">

      <table border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td align="center" class="operationscell">
            <xsl:variable name="debug_href">
              <xsl:value-of select="$debugpath"/>
              <xsl:value-of select="$menuitem/path"/>
            </xsl:variable>

            <xsl:variable name="debug_disable" select="$menuitem/@type = 'label' or $menuitem/@type = 'section'"/>

            <xsl:variable name="debug_tooltip">
              <xsl:choose>
                <xsl:when test="$menuitem/@type = 'label'">
                  <xsl:text>%errMenuItemIsLabelDebug%</xsl:text>
                </xsl:when>
                <xsl:when test="$menuitem/@type = 'section'">
                  <xsl:text>%errMenuItemIsSectionDebug%</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>%altShowPage%</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <xsl:call-template name="button">
              <xsl:with-param name="style" select="'flat'"/>
              <xsl:with-param name="type" select="'link'"/>
              <xsl:with-param name="id">
                <xsl:text>operation_debug_</xsl:text><xsl:value-of select="$id"/>
              </xsl:with-param>
              <xsl:with-param name="name">
                <xsl:text>icon</xsl:text><xsl:value-of select="$menuitem/@key"/>
              </xsl:with-param>
              <xsl:with-param name="image" select="'images/icon_preview.gif'"/>
              <xsl:with-param name="disabled" select="string($debug_disable)"/>
              <xsl:with-param name="tooltip" select="$debug_tooltip"/>
              <xsl:with-param name="href" select="$debug_href"/>
              <xsl:with-param name="target" select="'_blank'"/>
            </xsl:call-template>
          </td>
          <td align="center" class="operationscell">

            <xsl:variable name="edit_href">
              <xsl:choose>
                <xsl:when test="$highlight = -1">
                  <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
                  <xsl:text>&amp;op=edit&amp;key=</xsl:text><xsl:value-of select="$menuitem[1]/@key"/>
                  <xsl:text>&amp;type=</xsl:text><xsl:value-of select="$menuitem[1]/@type"/>
                  <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
                  <xsl:text>&amp;insertbelow=</xsl:text><xsl:value-of select="$parentmi"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>alert('%errMenuItemMustSave%');</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <xsl:variable name="loggedInUserHasUpdateRightAndMenuItemsIsNotInMoveMode" select="$menuitem/@userupdate = 'true' and $highlight = -1"/>

            <xsl:variable name="edit_disable">
              <xsl:choose>
                <xsl:when test="$loggedInUserHasUpdateRightAndMenuItemsIsNotInMoveMode">
                  <xsl:text>false</xsl:text>
                </xsl:when>
                <!-- Faar ikke redigere naar rekkefoelgen endres -->
                <xsl:when test="$highlight != -1">
                  <xsl:text>true</xsl:text>
                </xsl:when>
                <!-- En bruker med administrate skal ha rettighet -->
                <xsl:when test="$administrateright">
                  <xsl:text>false</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>true</xsl:text>
                </xsl:otherwise>
              </xsl:choose>

            </xsl:variable>

            <xsl:variable name="edit_tooltip">
              <xsl:choose>
                <xsl:when test="not($loggedInUserHasUpdateRightAndMenuItemsIsNotInMoveMode)">
                  <xsl:text>%msgNoRightsToDoThis%</xsl:text>
                </xsl:when>
                <xsl:when test="$highlight = -1">
                  <xsl:text>%altEdit%</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>%errMenuItemMustSave%</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <xsl:call-template name="button">
              <xsl:with-param name="style" select="'flat'"/>
              <xsl:with-param name="type" select="'link'"/>
              <xsl:with-param name="id">
                <xsl:text>operation_edit_</xsl:text><xsl:value-of select="$id"/>
              </xsl:with-param>
              <xsl:with-param name="name">
                <xsl:text>icon</xsl:text><xsl:value-of select="@key"/>
              </xsl:with-param>
              <xsl:with-param name="image" select="'images/icon_edit.gif'"/>
              <xsl:with-param name="disabled" select="$edit_disable"/>
              <xsl:with-param name="tooltip" select="$edit_tooltip"/>
              <xsl:with-param name="href" select="$edit_href"/>
            </xsl:call-template>

          </td>
          <td align="center" class="operationscell">
            <xsl:variable name="parentKey">
              <xsl:choose>
                <xsl:when test="boolean($selectedmenuitem/@key)">
                  <xsl:value-of select="$selectedmenuitem/@key"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>-1</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <xsl:variable name="move_href">
              <xsl:choose>
                <xsl:when test="$highlight = -1">
                  <xsl:text>javascript:OpenMoveWindow( this, 850, </xsl:text>
                  <xsl:value-of select="$parentKey"/>
                  <xsl:text>,</xsl:text>
                  <xsl:value-of select="@key"/>
                  <xsl:text>,"</xsl:text>
                  <xsl:value-of select="name"/>
                  <xsl:text>", 250, 300);</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>alert('%errMenuItemMustSave%');</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <xsl:variable name="move_disable">
              <xsl:choose>
                <!-- Faar ikke flytte naar rekkefoelgen endres -->
                <xsl:when test="$highlight != -1">
                  <xsl:text>true</xsl:text>
                </xsl:when>
                <!-- En bruker med administrate skal ha rettighet -->
                <xsl:when test="$menuitem/@useradministrate = 'true'">
                  <xsl:text>false</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>true</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <xsl:variable name="move_tooltip">

              <xsl:choose>
                <!-- Faar ikke flytte naar rekkefoelgen endres -->
                <xsl:when test="$highlight != -1">
                  <xsl:text>%errMenuItemMustSave%</xsl:text>
                </xsl:when>
                <!-- En bruker med administrate skal ha rettighet -->
                <xsl:when test="$menuitem/@useradministrate = 'true'">
                  <xsl:text>%altMoveMenuItem%</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>%msgNoRightsToDoThis%</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <xsl:call-template name="button">
              <xsl:with-param name="style" select="'flat'"/>
              <xsl:with-param name="type" select="'link'"/>
              <xsl:with-param name="id">
                <xsl:text>operation_move_</xsl:text><xsl:value-of select="$id"/>
              </xsl:with-param>
              <xsl:with-param name="name">
                <xsl:text>move</xsl:text><xsl:value-of select="@key"/>
              </xsl:with-param>
              <xsl:with-param name="image" select="'images/icon_content_move.gif'"/>
              <xsl:with-param name="disabled" select="$move_disable"/>
              <xsl:with-param name="tooltip" select="$move_tooltip"/>
              <xsl:with-param name="href" select="$move_href"/>
            </xsl:call-template>

          </td>
          <td align="center" class="operationscell">

            <xsl:variable name="delete_href">
              <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
              <xsl:text>&amp;op=removeitem&amp;key=</xsl:text><xsl:value-of select="$id"/>
              <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
              <xsl:text>&amp;parentmi=</xsl:text><xsl:value-of select="$parentmi"/>
            </xsl:variable>

            <xsl:variable name="delete_condition">
              <xsl:if test="$highlight = -1">
                <xsl:text>confirm('%alertDeletePage%')</xsl:text>
              </xsl:if>
            </xsl:variable>

            <xsl:variable name="delete_disable">
              <xsl:choose>
                <xsl:when test="$menuelem/@firstpage = $menuitem/@key or $menuelem/@loginpage = $menuitem/@key or $menuelem/@errorpage = $menuitem/@key">
                  <xsl:text>true</xsl:text>
                </xsl:when>
                <!-- Faar ikke slette naar rekkefoelgen endres -->
                <xsl:when test="$highlight != -1">
                  <xsl:text>true</xsl:text>
                </xsl:when>
                <xsl:when test="$menuitem/@userdelete = 'true'">
                  <xsl:text>false</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>true</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <xsl:variable name="delete_tooltip">
              <xsl:choose>
                <!-- Faar ikke slette naar rekkefoelgen endres -->
                <xsl:when test="$highlight != -1">
                  <xsl:text>%errMenuItemMustSave%</xsl:text>
                </xsl:when>
                <!-- EA eller en bruker med delete eller administrate skal ha rettighet -->
                <xsl:when test="$menuitem/@userdelete = 'true'">
                  <xsl:choose>
                    <xsl:when test="$menuelem/@firstpage = $menuitem/@key">
                      <xsl:text>%altCannotDeleteFrontPage%</xsl:text>
                    </xsl:when>
                    <xsl:when test="$menuelem/@loginpage = $menuitem/@key">
                      <xsl:text>%altCannotDeleteLoginPage%</xsl:text>
                    </xsl:when>
                    <xsl:when test="$menuelem/@errorpage = $menuitem/@key">
                      <xsl:text>%altCannotDeleteErrorPage%</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:text>%altDeletePage%</xsl:text>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>%msgNoRightsToDoThis%</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <xsl:call-template name="button">
              <xsl:with-param name="style" select="'flat'"/>
              <xsl:with-param name="type" select="'link'"/>
              <xsl:with-param name="id">
                <xsl:text>operation_delete_</xsl:text><xsl:value-of select="$id"/>
              </xsl:with-param>
              <xsl:with-param name="name">
                <xsl:text>delete</xsl:text><xsl:value-of select="@key"/>
              </xsl:with-param>
              <xsl:with-param name="image" select="'images/icon_delete.gif'"/>
              <xsl:with-param name="disabled" select="$delete_disable"/>
              <xsl:with-param name="tooltip" select="$delete_tooltip"/>
              <xsl:with-param name="href" select="$delete_href"/>
              <xsl:with-param name="condition" select="$delete_condition"/>
            </xsl:call-template>

          </td>
        </tr>
      </table>
    </td>
  </xsl:template>

  <xsl:template name="display-path-to-selected-menuitem">

    <xsl:for-each select="/model/selected-menuitem-path/menuitem">
      / <a href="adminpage?op=browse&amp;page={$page}&amp;menukey={$menukey}&amp;parentmi={@key}"><xsl:value-of select="name"/></a>
    </xsl:for-each>

  </xsl:template>

  <xsl:template name="msgStructureChanged">
    <xsl:param name="top" select="false()"/>

    <xsl:choose>
      <xsl:when test="$top = true()">
        <p class="warning-message">
          %msgModifedPageStructure%
        </p>
        <p>
          <xsl:call-template name="button">
            <xsl:with-param name="type" select="'submit'"/>
            <xsl:with-param name="caption" select="'%cmdSave%'"/>
          </xsl:call-template>
          <xsl:text>&nbsp;</xsl:text>

          <xsl:call-template name="button">
            <xsl:with-param name="type" select="'button'"/>
            <xsl:with-param name="caption" select="'%cmdCancel%'"/>
            <xsl:with-param name="onclick">
              <xsl:text>javascript:window.location.href = 'adminpage?page=</xsl:text>
              <xsl:value-of select="$page"/>
              <xsl:text>&amp;op=browse</xsl:text>
              <xsl:text>&amp;selecteddomainkey=</xsl:text>
              <xsl:value-of select="$selecteddomainkey"/>
              <xsl:text>&amp;menukey=</xsl:text>
              <xsl:value-of select="$menukey"/>
              <xsl:text>&amp;parentmi=</xsl:text>
              <xsl:value-of select="$parentmi"/>
              <xsl:text>';</xsl:text>
            </xsl:with-param>
          </xsl:call-template>
        </p>

      </xsl:when>
      <xsl:otherwise>
        <p>
          <xsl:call-template name="button">
            <xsl:with-param name="type" select="'submit'"/>
            <xsl:with-param name="caption" select="'%cmdSave%'"/>
          </xsl:call-template>
          <xsl:text>&nbsp;</xsl:text>

          <xsl:call-template name="button">
            <xsl:with-param name="type" select="'button'"/>
            <xsl:with-param name="caption" select="'%cmdCancel%'"/>
            <xsl:with-param name="onclick">
              <xsl:text>javascript:window.location.href = 'adminpage?page=</xsl:text>
              <xsl:value-of select="$page"/>
              <xsl:text>&amp;op=browse</xsl:text>
              <xsl:text>&amp;selecteddomainkey=</xsl:text>
              <xsl:value-of select="$selecteddomainkey"/>
              <xsl:text>&amp;menukey=</xsl:text>
              <xsl:value-of select="$menukey"/>
              <xsl:text>&amp;parentmi=</xsl:text>
              <xsl:value-of select="$parentmi"/>
              <xsl:text>';</xsl:text>
            </xsl:with-param>
          </xsl:call-template>
        </p>

        <p class="warning-message">
          %msgModifedPageStructure%
        </p>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
