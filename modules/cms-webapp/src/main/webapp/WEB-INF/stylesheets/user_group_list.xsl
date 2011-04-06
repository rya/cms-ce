<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:x="mailto:vro@enonic.com?subject=foobar"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html" />

  <xsl:include href="common/tablecolumnheader.xsl" />
  <xsl:include href="common/tablerowpainter.xsl" />
  <xsl:include href="common/button.xsl" />
  <xsl:include href="common/paging.xsl" />
  <xsl:include href="common/browse_table_js.xsl" />
  <xsl:include href="common/displayuserstorepath.xsl"/>
  <xsl:include href="common/formatdate.xsl"/>

  <xsl:param name="userstorekey" />
  <xsl:param name="userstorekeyincontext" select="-1"/>
  <xsl:param name="userstorename" />
  <xsl:param name="index" select="0" />
  <xsl:param name="count" select="20" />
  <xsl:param name="mode" select="'users'" />
  <xsl:param name="isGroups" select="$mode = 'groups' or $mode = 'globalgroups'" />
  <xsl:param name="query" select="''" />
  <xsl:param name="callback" />
  <xsl:param name="modeselector" select="'false'"/>
  <xsl:param name="userstoreselector" select="'false'"/>
  <xsl:param name="allowauthenticated" select="'false'"/>
  <xsl:param name="allow-all-to-be-added" select="'false'"/>
  <xsl:param name="excludekey" />
  <xsl:param name="userstoreadmin"/>
  <xsl:param name="admin" select="'false'"/>

  <xsl:param name="sortby" />
  <xsl:param name="sortby-direction" />

  <xsl:variable name="page" select="700" />
	<xsl:variable name="cat" select="''" />
  <xsl:variable name="selectedunitkey" select="''" />

  <xsl:param name="canCreateUser"/>
  <xsl:param name="canUpdateUser"/>
  <xsl:param name="canDeleteUser"/>
  <xsl:param name="canUpdatePassword"/>
  <xsl:param name="canCreateGroup"/>
  <xsl:param name="canUpdateGroup"/>
  <xsl:param name="canDeleteGroup"/>

  <xsl:param name="opener"/>
  <xsl:param name="use-user-group-key"/>
  <xsl:param name="user-picker-key-field" />

  <xsl:variable name="add-member-to-parent-form" select="$callback != ''"/>

  <xsl:variable name="pageURL">
    <xsl:text>adminpage?op=browse</xsl:text>
    <xsl:call-template name="createPageParams">
      <xsl:with-param name="userStoreKey" select="$userstorekey"/>
      <xsl:with-param name="userStoreKeyInContext" select="$userstorekeyincontext"/>
    </xsl:call-template>
    <xsl:text>&amp;page=</xsl:text>
    <xsl:value-of select="$page" />
    <xsl:text>&amp;allow-all-to-be-added=</xsl:text>
    <xsl:value-of select="$allow-all-to-be-added" />
    <xsl:text>&amp;opener=</xsl:text>
    <xsl:text>&amp;use-user-group-key=</xsl:text>
    <xsl:value-of select="$use-user-group-key" />
  </xsl:variable>

	<xsl:template match="/">
		<html>
			<head>
				<link type="text/css" href="css/admin.css" rel="stylesheet" />
				<script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" src="javascript/lib/jquery/jquery-1.3.2.min.js">//</script>

				<script type="text/javascript" src="javascript/batchAdd.js">//</script>
				<script type="text/javascript" src="javascript/accessrights.js">//</script>
        <script type="text/javascript" src="javascript/userstore.js">//</script>

        <xsl:if test="$callback">
          <script type="text/javascript" src="javascript/window.js"/>
          <script type="text/javascript">
            cms.window.attatchKeyEvent('close');
          </script>
        </xsl:if>

        <script type="text/javascript" lang="JavaScript">

          var formAdmin = document.forms['formAdmin'];

          /*
            Method: paintIsAdded
          */
          function paintIsAdded(key, selectedGroupIsSameAsGroupToAddTo ) {
            try {
              var trElements = document.getElementById('name_' + key).parentNode;
              var tdElements = trElements.getElementsByTagName('td');
              var tdLn = tdElements.length;
              var tdElement, tooltipText;
              for (var i = 0; i &lt; tdLn; i++) {
                tdElement = tdElements[i];
                tdElement.className += ' is-added';

                tooltipText = ( selectedGroupIsSameAsGroupToAddTo ) ? '%msgCanNotAddToSelf%' : '%msgMemberAlreadyAdded%';

                tdElement.title = tooltipText;
              }
            } catch (e) { /**/ }
          }

          // -------------------------------------------------------------------------------------------------------------------------------

          <xsl:variable name="append-qualified-name-to-display-name" select="$opener != 'user-picker' and $opener != 'assignee'"/>

          /**
           * addMember
           */
          function addMember( key )
          {
            var type = document.getElementById('type_'+key).innerHTML;

            // user has no type in the xml
            if ( type == '' )
              type = '6';

            var name = document.getElementById('name_placeholder_'+key).value;

            var qualifiedNameField = document.getElementById('qName_'+key);
            var qualifiedName = qualifiedNameField ? ' ('+qualifiedNameField.innerHTML+')' : '';
            var photoExistsSource = document.getElementById('photoExists_'+key);

            var photoExists = photoExistsSource ? photoExistsSource.innerHTML == 'true' : false;

            <xsl:if test="$append-qualified-name-to-display-name">
              if ( qualifiedNameField )
                name = name + qualifiedName;
            </xsl:if>

            var userstorename = document.getElementById('userstore_'+key).innerHTML;

            var excludeKey = '';

            <xsl:if test="$excludekey">
              excludeKey = '<xsl:value-of select="$excludekey"/>';
            </xsl:if>

            if ( key == excludeKey )
            {
              alert('%alertUserGroupCannotBeAdded%');
              return;
            }

            paintIsAdded(key);

            <xsl:value-of select="$callback"/>(key, type, name, userstorename, qualifiedName, photoExists);

          }

          // -------------------------------------------------------------------------------------------------------------------------------

          /**
           * rowClick
           */
          function rowClick( key )
          {
						<xsl:choose>
							<xsl:when test="$callback">
                cms.util.BatchAdd.toggleCheckBox(key);
              </xsl:when>
							<xsl:when test="$userstoreadmin = 'true' or $admin = 'true'">
                var editButton = document.getElementById('operation_edit_'+key);
                if ( !editButton) return;

                if( document.all )
                {
									editButton.click();
								}
								else
                {
										document.location.href = editButton.href;
								}
							</xsl:when>
							<xsl:otherwise>
							</xsl:otherwise>
						</xsl:choose>
					}


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
          <xsl:call-template name="displayuserstorepath">
            <xsl:with-param name="mode" select="$mode"/>
            <xsl:with-param name="popupMode" select="not($callback = '')"/>
            <xsl:with-param name="userstorekey" select="$userstorekey"/>
            <xsl:with-param name="userstorename" select="$userstorename"/>
            <xsl:with-param name="disabled" select="not($callback = '')"/>
            <xsl:with-param name="isGroups" select="$isGroups"/>
          </xsl:call-template>
        </h1>

        <form action="adminpage" name="formAdmin" method="get">
          <input type="hidden" name="op" value="browse"/>
          <input type="hidden" name="page" value="{$page}"/>
          <input type="hidden" name="callback" value="{$callback}"/>
          <input type="hidden" name="modeselector" value="{$modeselector}"/>
          <input type="hidden" name="userstoreselector" value="{$userstoreselector}"/>
          <input type="hidden" name="excludekey" value="{$excludekey}"/>
          <input type="hidden" name="allow-all-to-be-added" value="{$allow-all-to-be-added}"/>
          <input type="hidden" name="allowauthenticated" value="{$allowauthenticated}"/>
          <input type="hidden" name="opener" value="{$opener}"/>
          <input type="hidden" name="use-user-group-key" value="{$use-user-group-key}"/>

          <xsl:choose>
            <xsl:when test="$mode = 'users' and $canCreateUser != 'false' and not($userstorekey = '') and ($userstoreadmin = 'true')">
              <xsl:call-template name="button">
                <xsl:with-param name="type" select="'link'"/>
                <xsl:with-param name="caption" select="'%cmdNew%'"/>
                <xsl:with-param name="href">
                  <xsl:text>adminpage?op=form</xsl:text>
                  <xsl:text>&amp;page=700</xsl:text>
                  <xsl:call-template name="createPageParams">
                    <xsl:with-param name="userStoreKey" select="$userstorekey"/>
                  </xsl:call-template>
                </xsl:with-param>
              </xsl:call-template>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:call-template name="button">
                <xsl:with-param name="type" select="'link'"/>
                <xsl:with-param name="caption" select="'%cmdWizard%'"/>
                <xsl:with-param name="href">
                  <xsl:text>adminpage?op=form&amp;wizard=true</xsl:text>
                  <xsl:text>&amp;page=700</xsl:text>
                  <xsl:call-template name="createPageParams">
                    <xsl:with-param name="userStoreKey" select="$userstorekey"/>
                  </xsl:call-template>
                </xsl:with-param>
              </xsl:call-template>
              <xsl:text>&nbsp;</xsl:text>
            </xsl:when>
            <xsl:when test="$isGroups and $canCreateGroup != 'false' and (($userstoreadmin = 'true' and $userstorekey != '') or ($userstorekey = '' and $admin = 'true'))">
              <xsl:call-template name="button">
                <xsl:with-param name="type" select="'link'"/>
                <xsl:with-param name="caption" select="'%cmdNew%'"/>
                <xsl:with-param name="href">
                  <xsl:text>adminpage?op=form</xsl:text>
                  <xsl:text>&amp;page=701</xsl:text>
                  <xsl:call-template name="createPageParams">
                    <xsl:with-param name="userStoreKey" select="$userstorekey"/>
                  </xsl:call-template>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:when>
          </xsl:choose>

          <xsl:choose>
            <xsl:when test="$userstoreselector = 'false'">
              <input type="hidden" name="userstorekey" value="{$userstorekey}"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text> </xsl:text>
              <input type="hidden" name="userstorekeyincontext" value="{$userstorekeyincontext}"/>
              <select name="userstorekey" onchange="javascript:formAdmin.submit()">
                <option value="">%fldView%: %optAllGroups%</option>
                <xsl:for-each select="/node()/userstores/userstore">
                  <xsl:sort select="@name"/>
                  <option value="{@key}">
                    <xsl:if test="@key = $userstorekey">
                      <xsl:attribute name="selected">selected</xsl:attribute>
                    </xsl:if>
                    <xsl:text>%fldView%: </xsl:text>
                    <xsl:value-of select="@name" />
                  </option>
                </xsl:for-each>
              </select>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:text> </xsl:text>
          <xsl:choose>
            <xsl:when test="$modeselector = 'false'">
              <input type="hidden" name="mode" value="{$mode}"/>
            </xsl:when>
            <xsl:otherwise>
              <select name="mode" onchange="javascript:formAdmin.submit()">
                <option value="groups">
                  <xsl:if test="$isGroups">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:text>%fldView%: %optGroups%</xsl:text>
                </option>
                <option value="users">
                  <xsl:if test="$mode = 'users'">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:text>%fldView%: %optUsers%</xsl:text>
                </option>
              </select>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:text> </xsl:text>
          <input type="text" id="query" name="query" size="12" style="height: 20px" value="{$query}" />
          <xsl:call-template name="button">
            <xsl:with-param name="type" select="'submit'" />
            <xsl:with-param name="caption" select="'%cmdQuickSearch%'" />
          </xsl:call-template>
          <xsl:text>&nbsp;</xsl:text>

          <xsl:if test="$mode = 'users' and not($callback)">
            <xsl:call-template name="button">
              <xsl:with-param name="type" select="'link'"/>
              <xsl:with-param name="caption" select="'%cmdCreateReport%'"/>
              <xsl:with-param name="href">
                <xsl:text>adminpage?page=</xsl:text>
                <xsl:value-of select="$page"/>
                <xsl:text>&amp;op=report&amp;subop=reportform&amp;userstorekey=</xsl:text>
                <xsl:value-of select="$userstorekey"/>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:if>
        </form>

        <xsl:variable name="usersOnly" select="/node()/*[name() = 'user'] and not(/node()/*[name() = 'group'])" />
        <xsl:variable name="groupsOnly" select="/node()/*[name() = 'group'] and not(/node()/*[name() = 'user'])" />

        <fieldset class="table-panel">

          <xsl:call-template name="operationsBar">
            <xsl:with-param name="top" select="true()"/>
          </xsl:call-template>

          <table width="100%" cellpadding="2" cellspacing="0" class="browsetable">
            <xsl:if test="$callback != ''">
              <xsl:call-template name="tablecolumnheader">
                <xsl:with-param name="align" select="'center'"/>
                <xsl:with-param name="width" select="'18'" />
                <xsl:with-param name="caption" select="''" />
                <xsl:with-param name="sortable" select="'false'" />
                <xsl:with-param name="checkboxname" select="'batch_add_checkbox'"/>
                <xsl:with-param name="checkBoxOnClickFallback" select="'cms.util.BatchAdd.enableDisableAddButton()'"/>
              </xsl:call-template>
            </xsl:if>

            <xsl:call-template name="tablecolumnheader">
              <xsl:with-param name="caption" select="''" />
              <xsl:with-param name="width" select="'1'" />
              <xsl:with-param name="sortable" select="'false'" />
            </xsl:call-template>

            <xsl:call-template name="tablecolumnheader">
              <xsl:with-param name="caption" select="'%fldName%'" />
              <xsl:with-param name="width" select="'270'" />
              <xsl:with-param name="pageURL" select="$pageURL" />
              <xsl:with-param name="current-sortby" select="$sortby"/>
              <xsl:with-param name="current-sortby-direction" select="$sortby-direction"/>
              <xsl:with-param name="sortby" select="'name'"/>
            </xsl:call-template>

            <xsl:if test="$usersOnly">
              <xsl:call-template name="tablecolumnheader">
                <xsl:with-param name="caption" select="'%fldQualifiedName%'"/>
                <xsl:with-param name="width" select="'260'" />
                <xsl:with-param name="sortable" select="'false'" />
              </xsl:call-template>

              <xsl:call-template name="tablecolumnheader">
                <xsl:with-param name="caption" select="'%fldEmail%'"/>
                <xsl:with-param name="pageURL" select="$pageURL" />
                <xsl:with-param name="current-sortby" select="$sortby"/>
                <xsl:with-param name="current-sortby-direction" select="$sortby-direction"/>
                <xsl:with-param name="sortby" select="'email'"/>
              </xsl:call-template>

              <xsl:call-template name="tablecolumnheader">
                <xsl:with-param name="caption" select="'%fldLastModified%'" />
                <xsl:with-param name="width" select="'120'" />
                <xsl:with-param name="pageURL" select="$pageURL" />
                <xsl:with-param name="current-sortby" select="$sortby"/>
                <xsl:with-param name="current-sortby-direction" select="$sortby-direction"/>
                <xsl:with-param name="sortby" select="'timestamp'"/>
              </xsl:call-template>
            </xsl:if>

            <xsl:if test="$groupsOnly">
              <xsl:if test="$mode = 'groups'">
                <xsl:call-template name="tablecolumnheader">
                  <xsl:with-param name="caption" select="'%fldUserstore%'" />
                  <xsl:with-param name="width" select="'260'" />
                  <xsl:with-param name="sortable" select="'false'" />
                </xsl:call-template>
              </xsl:if>

              <xsl:call-template name="tablecolumnheader">
                <xsl:with-param name="caption" select="'%fldMemberCount%'"/>
                <xsl:with-param name="width" select="'100'" />
                <xsl:with-param name="align" select="'center'" />
                <xsl:with-param name="sortable" select="'false'" />
              </xsl:call-template>

              <xsl:call-template name="tablecolumnheader">
                <xsl:with-param name="caption" select="'%fldRestricted%'" />
                <xsl:with-param name="width" select="'140'" />
                <xsl:with-param name="align" select="'center'" />
                <xsl:with-param name="sortable" select="'false'" />
              </xsl:call-template>
            </xsl:if>

            <xsl:if test="not($userstoreadmin = 'false') or not($userstorekey = '' and $admin = 'false')">
              <xsl:call-template name="tablecolumnheader">
                <xsl:with-param name="caption" select="''" />
                <xsl:with-param name="width" select="'60'" />
                <xsl:with-param name="sortable" select="'false'" />
              </xsl:call-template>
            </xsl:if>

            <xsl:for-each select="/node()/*[name() = 'user' or name() = 'group']">
              <xsl:variable name="key">
                <xsl:choose>
                  <xsl:when test="name() = 'user' and $opener = 'assignee'"><xsl:value-of select="@key"/></xsl:when>
                  <xsl:when test="name() = 'user' and $opener = 'assigner'"><xsl:value-of select="@key"/></xsl:when>
                  <xsl:when test="name() = 'user' and $use-user-group-key = 'true'"><xsl:value-of select="@groupKey"/></xsl:when>
                  <xsl:when test="name() = 'user' and $use-user-group-key = 'false'"><xsl:value-of select="@key"/></xsl:when>
                  <xsl:when test="name() = 'user'"><xsl:value-of select="@groupKey"/></xsl:when>
                  <xsl:otherwise><xsl:value-of select="@key"/></xsl:otherwise>
                </xsl:choose>
              </xsl:variable>

              <xsl:variable name="is-local-group" select="(name() = 'group') and (current()/@builtIn = 'false')"/>
              <xsl:variable name="can-not-update-group-policy" select="/groups/userstores/userstore[@key = current()/@userStoreKey]/connector/config/group-policy/@can-update = 'false'"/>
              <xsl:variable name="is-authenticated-group" select="name() = 'group' and @type = 3" />

              <xsl:variable name="class">
                <xsl:choose>
                  <xsl:when test="$is-authenticated-group">
                    <xsl:text>browsetablecell</xsl:text>
                    <xsl:if test="$allowauthenticated = 'false'">
                      <xsl:text> is-added</xsl:text>
                    </xsl:if>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>browsetablecell</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="position() = last()">
                  <xsl:text> row-last</xsl:text>
                </xsl:if>
              </xsl:variable>

              <xsl:variable name="title">
                <xsl:choose>
                  <xsl:when test="$add-member-to-parent-form">
                    <xsl:choose>
                      <xsl:when test="@type = '3' and $allowauthenticated = 'false'">
                        <xsl:text>%msgAuthenticatedUsersCanNotBeAddedHere%</xsl:text>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:text>%msgClickToSelect%</xsl:text>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:choose>
                      <xsl:when test="$is-local-group and $can-not-update-group-policy">
                        <xsl:text>%msgNotUpdateableGroup%</xsl:text>
                      </xsl:when>
                      <xsl:when test="(name() = 'user') or (name() = 'group' and @type != 3) and $key != $excludekey">
                        <xsl:text>%msgClickToView%</xsl:text>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:text>%msgNotUpdateableGroup%</xsl:text>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:variable>

              <tr>
                <xsl:call-template name="tablerowpainter" />

                <xsl:if test="$callback != ''">
                  <td align="center" class="{$class}" title="{$title}">

                  <!--
                    Regler for visning av checkboks til å velge raden:
                    Forutsetninger:
                     - Brukergrupper blir ikke listet
                     - Ved visning av grupper, blir hverken globale grupper eller grupper fra andre brukerlager listet.
                     - Grupper som ikke kan endres blir heller ikke åpnet eller listet med denne koden.
                    1. Det skal ikke være mulig å legge en gruppe inn i seg selv.
                        - Det er ikke aktuelt for brukere, fordi brukergrupper ikke listes.
                    2. Det skal ikke være mulig å legge inn Authenticated Users( type 3) på en bruker.
                    2a. For rettigheter på innhold, menypunkter, arkiver og kategorier kan "Authenticated Users" velges.
                    2b. Hverken brukere eller grupper kan legges inn i "Authenticated Users".
                        - Dette skillet gjøres ved at "Authenticated users" gruppen ikke kan åpnes i det hele tatt fra adminkonsollet, og er
                          derfor ikke nødvendig å implementere.
                    4. I innhold, menypunkter, arkiver, kategorier og innbygde (lokale eller globale) grupper kan alle brukere og grupper legges inn.
                    5. Hvis et brukerlager har satt groupPolicy til en av all, update eller local, kan brukere / grupper i samme
                       brukerlager legges inn i gruppen.  Hvis ikke kan ikke gruppens medlemmer endres.  Unntaket er User Store Administratorgruppen
                       som alltid kan endres, fordi den ikke synkes opp mot fjernlageret.
                        - Dette løses ved at gruppene som ikke kan endres, ikke kan åpnes i adminkonsollet.

                  -->

                  <!--
                    Implementasjonskommetarer:  $allowauthenticated er "true", så lenge vi ikke jobber med brukerlageret (user / group).
                    Sånn sett kan den brukes til å gjenkjenne når det er snakk om rettigheter på innhold, menypunkter, arkiver og kategorier, men er det riktig?
                  -->

                    <xsl:choose>
                      <!--
                        Regel 1:
                      -->
                      <xsl:when test="name() = 'group' and $key = $excludekey">
                        <br/>
                      </xsl:when>
                      <!--
                        Regel 2:
                      -->
                      <xsl:when test="@type = '3' and $allowauthenticated = 'false'">
                        <br/>
                      </xsl:when>
                      <!--
                        Regel 3:
                      -->
                      <xsl:when test="$allow-all-to-be-added = 'true'">
                        <input type="checkbox" id="checkbox_{$key}" value="{$key}" name="batch_add_checkbox" onclick="cms.util.BatchAdd.enableDisableAddButton()"/>
                      </xsl:when>
                      <!--
                        Regel 4:
                      -->
                      <xsl:when test="$is-local-group and $can-not-update-group-policy">
                        <br/>
                      </xsl:when>
                      <xsl:otherwise>
                        <input type="checkbox" id="checkbox_{$key}" value="{$key}" name="batch_add_checkbox" onclick="cms.util.BatchAdd.enableDisableAddButton()"/>
                      </xsl:otherwise>
                    </xsl:choose>
                  </td>
                </xsl:if>

                <td nowrap="nowrap" class="{$class}" onmouseup="rowClick('{$key}');" align="center" title="{$title}">

                  <xsl:call-template name="display-user-icon">
                    <xsl:with-param name="user-key" select="@key"/>
                    <xsl:with-param name="photo-exists" select="photo"/>
                    <xsl:with-param name="icon-type" select="name()"/>
                  </xsl:call-template>

                  <span style="display: none;" id="type_{$key}"><xsl:value-of select="@type"/></span>
                </td>
                <td nowrap="nowrap" class="{$class}" id="name_{$key}" title="{$title}">
                  <xsl:call-template name="addJsClickEvent">
                    <xsl:with-param name="key" select="$key"/>
                  </xsl:call-template>
                  <xsl:variable name="name">
                    <xsl:apply-templates select="." mode="name" />
                  </xsl:variable>
                  <input type="hidden" id="name_placeholder_{$key}" name="name_placeholder_{$key}" value="{$name}"/>
                  <xsl:value-of select="$name"/>&nbsp;
                </td>

                <xsl:if test="$usersOnly">
                  <td nowrap="nowrap" class="{$class}" title="{$title}">
                    <xsl:call-template name="addJsClickEvent">
                      <xsl:with-param name="key" select="$key"/>
                    </xsl:call-template>

                    <xsl:value-of select="qualifiedName" />

                    <span style="display:none" id="userstore_{$key}">
                      <xsl:value-of select="/node()/userstores/userstore[@key = current()/@userStoreKey]/@name" />
                    </span>
                    <span style="display:none" id="qName_{$key}">
                      <xsl:value-of select="qualifiedName" />
                    </span>
                    <span style="display:none" id="photoExists_{$key}">
                      <xsl:value-of select="photo" />
                    </span>
                  </td>
                  <td nowrap="nowrap" class="{$class}" title="{$title}">
                    <xsl:call-template name="addJsClickEvent">
                      <xsl:with-param name="key" select="$key"/>
                    </xsl:call-template>
                    <xsl:choose>
                      <xsl:when test="email != ''">
                        <xsl:value-of select="email"/>
                      </xsl:when>
                      <xsl:otherwise>
                        &nbsp;
                      </xsl:otherwise>
                    </xsl:choose>
                  </td>
                  <td nowrap="nowrap" class="{$class}" title="{$title}">
                    <xsl:call-template name="addJsClickEvent">
                      <xsl:with-param name="key" select="$key"/>
                    </xsl:call-template>
                    <xsl:call-template name="formatdatetime">
                      <xsl:with-param name="date" select="lastModified"/>
                    </xsl:call-template>
                  </td>
                </xsl:if>

                <xsl:if test="$groupsOnly">
                  <xsl:if test="$mode = 'groups'">
                    <td nowrap="nowrap" class="{$class}" id="userstore_{$key}" title="{$title}">
                      <xsl:call-template name="addJsClickEvent">
                        <xsl:with-param name="key" select="$key"/>
                      </xsl:call-template>
                      <xsl:value-of select="/node()/userstores/userstore[@key = current()/@userStoreKey]/@name" />
                      &nbsp;

                      <xsl:variable name="is-local-group" select="@userStoreKey != ''"/>

                      <span style="display:none" id="qName_{$key}">
                        <xsl:choose>
                          <xsl:when test="$is-local-group">
                            <xsl:value-of select="qualifiedName" />
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:text>%txtGlobalGroup%</xsl:text>
                          </xsl:otherwise>
                        </xsl:choose>
                      </span>
                    </td>
                  </xsl:if>

                  <td align="center" nowrap="nowrap" class="{$class}" title="{$title}">
                    <xsl:call-template name="addJsClickEvent">
                      <xsl:with-param name="key" select="$key"/>
                    </xsl:call-template>
                    <xsl:value-of select="members/@count" />
                  </td>

                  <td nowrap="nowrap" class="{$class}" style="text-align:center" title="{$title}">
                    <xsl:call-template name="addJsClickEvent">
                      <xsl:with-param name="key" select="$key"/>
                    </xsl:call-template>
                    <xsl:choose>
                      <xsl:when test="@restricted = 'true'">
                        <img src="images/icon_check_noborder.gif" alt=""/>
                      </xsl:when>
                      <xsl:otherwise>
                        <br/>
                      </xsl:otherwise>
                    </xsl:choose>
                  </td>
                </xsl:if>

                <xsl:if test="not($userstoreadmin = 'false') or not($userstorekey = '' and $admin = 'false')">
                  <td nowrap="nowrap" class="{$class}" title="{$title}">

                    <xsl:variable name="currentpage">
                      <xsl:apply-templates select="." mode="page" />
                    </xsl:variable>

                    <xsl:if test="$canUpdatePassword != 'false' and $mode = 'users' and not($callback)">
                      <xsl:call-template name="button">
                        <xsl:with-param name="style" select="'flat'"/>
                        <xsl:with-param name="type" select="'link'"/>
                        <xsl:with-param name="id">
                          <xsl:text>operation_changepwd_</xsl:text><xsl:value-of select="block/uid"/>
                        </xsl:with-param>
                        <xsl:with-param name="name">
                          <xsl:text>changepwd</xsl:text><xsl:value-of select="block/uid"/>
                        </xsl:with-param>
                        <xsl:with-param name="image" select="'images/icon_password.gif'"/>
                        <xsl:with-param name="disabled" select="'false'"/>
                        <xsl:with-param name="tooltip" select="'%tipChangePassword%'"/>
                        <xsl:with-param name="href">
                          <xsl:text>adminpage?page=</xsl:text>
                          <xsl:value-of select="$currentpage" />
                          <xsl:text>&amp;key=</xsl:text>
                          <xsl:value-of select="$key" />
                          <xsl:text>&amp;op=changepassword</xsl:text>
                          <xsl:call-template name="createPageParams">
                            <xsl:with-param name="userStoreKey" select="@userStoreKey"/>
                          </xsl:call-template>

                        </xsl:with-param>
                      </xsl:call-template>
                    </xsl:if>

                    <xsl:variable name="includeEdit">
                      <xsl:choose>
                        <xsl:when test="@type = 3">false</xsl:when>
                        <xsl:when test="$is-local-group and $can-not-update-group-policy">false</xsl:when>
                        <xsl:otherwise>true</xsl:otherwise>
                      </xsl:choose>
                    </xsl:variable>

                    <xsl:if test="$includeEdit = 'true'">
                      <xsl:variable name="editViewIcon">
                        <xsl:choose>
                          <xsl:when test="($mode = 'users' and $canUpdateUser != 'false') or ($isGroups and $canUpdateGroup != 'false')">images/icon_edit.gif</xsl:when>
                          <xsl:otherwise>images/icon_view.gif</xsl:otherwise>
                        </xsl:choose>
                      </xsl:variable>
                      <xsl:variable name="editViewTooltip">
                        <xsl:choose>
                          <xsl:when test="($mode = 'users' and $canUpdateUser != 'false') or ($isGroups and $canUpdateGroup != 'false')">%tipModify%</xsl:when>
                          <xsl:otherwise>%tipView%</xsl:otherwise>
                        </xsl:choose>
                      </xsl:variable>

                      <xsl:variable name="edit-form-url">
                        <xsl:text>adminpage?page=</xsl:text>
                        <xsl:value-of select="$currentpage" />
                        <xsl:text>&amp;op=form</xsl:text>
                        <xsl:text>&amp;key=</xsl:text>
                        <xsl:value-of select="$key" />
                        <xsl:call-template name="createPageParams">
                          <xsl:with-param name="userStoreKey" select="@userStoreKey"/>
                        </xsl:call-template>
                      </xsl:variable>

                      <xsl:call-template name="button">
                        <xsl:with-param name="style" select="'flat'" />
                        <xsl:with-param name="type" select="'link'" />
                        <xsl:with-param name="id">
                          <xsl:text>operation_edit_</xsl:text>
                          <xsl:value-of select="$key" />
                        </xsl:with-param>
                        <xsl:with-param name="name">
                          <xsl:text>edit</xsl:text>
                          <xsl:value-of select="$key" />
                        </xsl:with-param>
                        <xsl:with-param name="image" select="$editViewIcon" />
                        <xsl:with-param name="tooltip" select="$editViewTooltip" />
                        <xsl:with-param name="href">
                          <xsl:value-of select="$edit-form-url"/>
                        </xsl:with-param>
                      </xsl:call-template>
                    </xsl:if>

                    <xsl:variable name="includeDelete">
                      <xsl:choose>
                        <xsl:when test="($isGroups and @builtIn = 'true') or ($isGroups and $canDeleteGroup = 'false') or ($mode = 'users' and $canDeleteUser = 'false')">false</xsl:when>
                        <xsl:when test="$is-local-group and /groups/userstores/userstore[@key = current()/@userStoreKey]/connector/config/group-policy/@can-delete = 'false'">
                          <br/>
                        </xsl:when>

                        <xsl:otherwise>true</xsl:otherwise>
                      </xsl:choose>
                    </xsl:variable>

                    <xsl:if test="$includeDelete = 'true'">
                      <xsl:call-template name="button">
                        <xsl:with-param name="style" select="'flat'" />
                        <xsl:with-param name="type" select="'link'" />
                        <xsl:with-param name="name">
                          <xsl:text>del</xsl:text>
                          <xsl:value-of select="name" />
                        </xsl:with-param>
                        <xsl:with-param name="image" select="'images/icon_delete.gif'" />
                        <xsl:with-param name="disabled" select="'false'" />
                        <xsl:with-param name="tooltip" select="'%tipDelete%'" />
                        <xsl:with-param name="condition">
                          <xsl:text>confirm('%alertDelete%')</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="href">
                          <xsl:text>adminpage?page=</xsl:text>
                          <xsl:value-of select="$currentpage" />
                          <xsl:text>&amp;op=remove</xsl:text>
                          <xsl:text>&amp;callback=</xsl:text>
                          <xsl:value-of select="$callback" />
                          <xsl:text>&amp;mode=</xsl:text>
                          <xsl:value-of select="$mode" />
                          <xsl:text>&amp;query=</xsl:text>
                          <xsl:value-of select="$query" />
                          <xsl:text>&amp;userstorekey=</xsl:text>
                          <xsl:value-of select="@userStoreKey" />
                          <xsl:text>&amp;key=</xsl:text>
                          <xsl:value-of select="$key" />
                        </xsl:with-param>
                      </xsl:call-template>
                    </xsl:if>
                    <!--/xsl:if-->
                    <script type="text/javascript">
                      <xsl:variable name="opener-is-cahce-form" select="$callback = 'opener.caching_callback_selectednew'"/>

                      if (window.opener) {
                        <xsl:choose>
                          <xsl:when test="$opener = 'user-picker'">
                            if ( window.opener.document.getElementById('<xsl:value-of select="$user-picker-key-field"/>').value === '<xsl:value-of select="$key"/>' )
                            {
                              paintIsAdded( '<xsl:value-of select="$key"/>', false );
                            }
                          </xsl:when>
                          <xsl:when test="$opener-is-cahce-form">
                            if (window.opener.document.getElementById('runas').value === '<xsl:value-of select="$key"/>')
                            {
                              paintIsAdded( '<xsl:value-of select="$key"/>', false );
                            }
                          </xsl:when>
                          <xsl:otherwise>
                            // Is allready added or current group is self.
                            if (window.opener.isChoosen('<xsl:value-of select="$key"/>') || '<xsl:value-of select="$key"/>' == '<xsl:value-of select="$excludekey"/>')
                            {
                              paintIsAdded( '<xsl:value-of select="$key"/>', <xsl:value-of select="$key = $excludekey"/> );
                            }
                          </xsl:otherwise>
                        </xsl:choose>
                      }
                      <!-- Authenticated users -->
                      <!--xsl:if test="name() = 'group' and @type = 3">
                      paintIsAdded('<xsl:value-of select="$key"/>');
                    </xsl:if-->
                    </script>
                    &nbsp;
                  </td>

                </xsl:if>
              </tr>
            </xsl:for-each>
          </table>

          <xsl:call-template name="operationsBar">
            <xsl:with-param name="top" select="false()"/>
          </xsl:call-template>

        </fieldset>

        <script type="text/javascript">
          setFormFieldFocus('query');
          cms.util.BatchAdd.addToolTip('%msgNoMembersSelected%');
        </script>

        <script type="text/javascript">
          $(document).ready( function()
          {
            checkIfUserstoreIsSynchronizing('<xsl:value-of select="$userstorekey"/>');
          });
        </script>

      </body>
    </html>
  </xsl:template>

  <xsl:template name="operationsBar">
    <xsl:param name="top"/>

    <xsl:variable name="css-margins">
      <xsl:choose>
        <xsl:when test="$top = true()">operation-top</xsl:when>
        <xsl:otherwise>operation-bottom</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:if test="$opener = 'advsearchform' or $opener = 'assignee' or $opener = 'assigner' or $opener = 'user-picker'">
      <script type="text/javascript">
        cms.util.BatchAdd.multipleCheckedCheckboxesIsAllowed = false;
      </script>
    </xsl:if>

    <table border="0" cellpadding="0" cellspacing="0" style="width:100%" class="{$css-margins}">
      <tr>
          <td style="width:33%">
            <xsl:if test="$callback != ''">
              <xsl:call-template name="button">
                <xsl:with-param name="type" select="'button'"/>
                <xsl:with-param name="name" select="'batch_add_button'"/>
                <xsl:with-param name="caption" select="'%cmdBatchAdd%'"/>
                <xsl:with-param name="onclick" select="'cms.util.BatchAdd.batchAdd(addMember);'"/>
                <xsl:with-param name="disabled" select="'true'"/>
              </xsl:call-template>
            </xsl:if>
            &nbsp;
          </td>
        <td width="33%" align="center">
          <xsl:call-template name="paging">
            <xsl:with-param name="url">
              <xsl:value-of select="$pageURL" />
              <xsl:text>&amp;sortby=</xsl:text>
              <xsl:value-of select="$sortby" />
              <xsl:text>&amp;sortby-direction=</xsl:text>
              <xsl:value-of select="$sortby-direction" />
            </xsl:with-param>
            <xsl:with-param name="index" select="/node()/@index" />
            <xsl:with-param name="count" select="number($count)" />
            <xsl:with-param name="totalcount" select="/node()/@totalCount" />
          </xsl:call-template>
        </td>
        <td align="right" style="width:33%">
          <xsl:call-template name="perPage">
            <xsl:with-param name="index" select="/node()/@index"/>
            <xsl:with-param name="count" select="number($count)"/>
            <xsl:with-param name="totalcount" select="/node()/@totalCount"/>
          </xsl:call-template>
          &nbsp;
          <xsl:call-template name="countSelect">
            <xsl:with-param name="count" select="$count"/>
          </xsl:call-template>
        </td>
      </tr>
    </table>
  </xsl:template>


  <xsl:template match="user" mode="name">
    <xsl:value-of select="displayName" />
  </xsl:template>

  <xsl:template match="user" mode="page">
    <xsl:text>700</xsl:text>
  </xsl:template>

  <xsl:template match="group" mode="name">
    <xsl:value-of select="displayName" />
  </xsl:template>

  <xsl:template match="group" mode="page">
    <xsl:text>701</xsl:text>
  </xsl:template>

  <xsl:template name="addJsClickEvent">
    <xsl:param name="key"/>
    <xsl:attribute name="onclick">
      <xsl:text>rowClick('</xsl:text>
      <xsl:value-of select="$key"/>
      <xsl:text>');</xsl:text>
    </xsl:attribute>
  </xsl:template>

  <xsl:template name="createPageParams">
    <xsl:param name="userStoreKey"/>
    <xsl:param name="userStoreKeyInContext"/>
    <xsl:text>&amp;callback=</xsl:text>
		<xsl:value-of select="$callback" />
		<xsl:text>&amp;mode=</xsl:text>
		<xsl:value-of select="$mode" />
		<xsl:text>&amp;query=</xsl:text>
		<xsl:value-of select="$query" />
    <xsl:if test="$userStoreKey">
      <xsl:text>&amp;userstorekey=</xsl:text>
      <xsl:value-of select="$userStoreKey" />
    </xsl:if>
    <xsl:if test="$userStoreKeyInContext">
      <xsl:text>&amp;userstorekeyincontext=</xsl:text>
      <xsl:value-of select="$userStoreKeyInContext" />
    </xsl:if>
    <xsl:if test="$modeselector">
			<xsl:text>&amp;modeselector=</xsl:text>
			<xsl:value-of select="$modeselector"/>
		</xsl:if>
		<xsl:if test="$userstoreselector">
			<xsl:text>&amp;userstoreselector=</xsl:text>
			<xsl:value-of select="$userstoreselector"/>
		</xsl:if>
		<xsl:if test="$excludekey">
			<xsl:text>&amp;excludekey=</xsl:text>
			<xsl:value-of select="$excludekey"/>
		</xsl:if>
  </xsl:template>

  <xsl:template name="display-user-icon">
    <xsl:param name="user-key"/>
    <xsl:param name="photo-exists"/>
    <xsl:param name="icon-type"/>
    <div class="list-icon-container">
      <xsl:choose>
        <xsl:when test="$photo-exists = 'true'">
          <img src="_image/user/{$user-key}?_filter=scalemax(64)" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="$icon-type = 'group'">
              <img src="images/icon_groups.gif" border="0" style="vertical-align: middle;" />
            </xsl:when>
            <xsl:otherwise>
              <img src="images/dummy-user-small.png" border="0" style="vertical-align: middle;" />
            </xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>

</xsl:stylesheet>
