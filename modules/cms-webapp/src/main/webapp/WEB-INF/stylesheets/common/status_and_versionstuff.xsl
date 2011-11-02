<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
    ]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:param name="creatednewversion" select="'false'"/>

  <xsl:variable name="isNewUnsavedContent" select="not(/contents/content/@state)"/>
  <xsl:variable name="content-has-draft" select="/contents/content/@has-draft = 'true'"/>
  <xsl:variable name="editlockedversionmode-and-content-has-not-draft" select="$editlockedversionmode and not($content-has-draft)"/>
  <xsl:variable name="editlockedversionmode-and-content-has-draft" select="$editlockedversionmode and $content-has-draft"/>
  <xsl:variable name="selected-version-is-unsaved-draft" select="$isNewUnsavedContent or $editlockedversionmode-and-content-has-not-draft"/>

  <xsl:template name="statusAndVersionstuff">
    <xsl:variable name="isMainVersion" select="boolean(/contents/content/@current = 'true')"/>
    <xsl:variable name="mainVersionIsNotApproved" select="boolean(/contents/content/versions/version[@current = 'true' and (@status = 0 or @status = 1 or @status = 3)])"/>
    <xsl:variable name="isPlacedInMenuItemOrSection" select="count(/contents/contentlocations/menu[menuitem/@type = 'section' or menuitem/@type = 'sectionpage']) != 0 or count(/contents/contentlocations/menu[menuitem/@type = 'content']) != 0"/>
    <xsl:variable name="isMasterVersion" select="boolean($isNewUnsavedContent or $isMainVersion or $mainVersionIsNotApproved )"/>
    <xsl:variable name="color-version-fieldset-white" select="not(/contents/content/@state) or /contents/content/@state = 0 or $editlockedversionmode"/>

    <!-- Debug -->

    <!--div style="clear:both;">
      /contents/content/@versionkey: <xsl:value-of select="/contents/content/@versionkey"/><br/>
      /contents/content/@state: <xsl:value-of select="/contents/content/@state"/><br/>

      $content-has-draft: <xsl:value-of select="$content-has-draft"/><br/>
      $unsaved-draft: <xsl:value-of select="$selected-version-is-unsaved-draft"/><br/>
      $isMasterVersion: <xsl:value-of select="$isMasterVersion"/><br/>
      $editlockedversionmode-and-content-has-draft: <xsl:value-of select="$editlockedversionmode-and-content-has-draft"/><br/>
      $editlockedversionmode-and-content-has-not-draft: <xsl:value-of select="$editlockedversionmode-and-content-has-not-draft"/><br/>
    </div-->

    <fieldset class="versions-container">
      <xsl:if test="$color-version-fieldset-white">
        <xsl:attribute name="style">
          <xsl:text>background-color:#fff;</xsl:text>
        </xsl:attribute>
      </xsl:if>
      <legend>%blockStatus%</legend>

      <div style="float: left; width:100%">

        <table border="0" cellpadding="0" cellspacing="0" style="width:100%">
          <tr>
            <td nowrap="true">
              <xsl:call-template name="publishstatus">
                <xsl:with-param name="state" select="/contents/content/@state"/>
                <xsl:with-param name="publishfrom" select="/contents/content/@publishfrom"/>
                <xsl:with-param name="publishto" select="/contents/content/@publishto"/>
                <xsl:with-param name="isPlacedInMenuItemOrSection" select="$isPlacedInMenuItemOrSection"/>
                <xsl:with-param name="isMasterVersion" select="$isMasterVersion"/>
                <xsl:with-param name="icononly" select="'false'"/>
                <xsl:with-param name="unsaved-draft" select="$selected-version-is-unsaved-draft"/>
              </xsl:call-template>
              <!--xsl:choose>
                <xsl:when test="$editlockedversionmode-and-content-has-not-draft or $editlockedversionmode-and-content-has-draft">
                  <- special case for ulagra versjon ->
                  <xsl:call-template name="publishstatus">
                    <xsl:with-param name="state" select="0"/>
                    <xsl:with-param name="unsaved-draft" select="true()"/>
                    <xsl:with-param name="icononly" select="'false'"/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:call-template name="publishstatus">
                    <xsl:with-param name="state" select="/contents/content/@state"/>
                    <xsl:with-param name="publishfrom" select="/contents/content/@publishfrom"/>
                    <xsl:with-param name="publishto" select="/contents/content/@publishto"/>
                    <xsl:with-param name="isPlacedInMenuItemOrSection" select="$isPlacedInMenuItemOrSection"/>
                    <xsl:with-param name="isMasterVersion" select="$isMasterVersion"/>
                    <xsl:with-param name="icononly" select="'false'"/>
                    <xsl:with-param name="unsaved-draft" select="$unsaved-draft"/>
                  </xsl:call-template>
                </xsl:otherwise>
              </xsl:choose-->
            </td>
            <td>
              <xsl:attribute name="style">
                <xsl:text>width:100%;padding-left:12px;</xsl:text>
                <xsl:if test="$isNewUnsavedContent or (count(/contents/content/versions/version) = 1 and /contents/content/@status = 0)">
                  <xsl:text>padding-top:2px</xsl:text>
                </xsl:if>
              </xsl:attribute>
              <!-- Show/hide versions button -->
              <xsl:if test="count(/contents/content/versions/version) &gt; 1 or $editlockedversionmode-and-content-has-not-draft">
                <a href="javascript:" onclick="javascript:showHideVersionsTable()" title="%showVersions%" id="showHideVersionsButton"
                   class="text-link-button">
                  %showVersions% (<xsl:value-of select="count(/contents/content/versions/version[@status != 1])"/>)
                </a>
              </xsl:if>


              <xsl:if test="$isNewUnsavedContent or (count(/contents/content/versions/version) = 1 and /contents/content/@status = 0)">
                <xsl:call-template name="version-comment-field">
                  <xsl:with-param name="comment" select="/contents/content/versions/version[@key = /contents/content/@versionkey]/@comment"/>
                </xsl:call-template>
              </xsl:if>

            </td>
            <td>
              <xsl:call-template name="thrash-can"/>
            </td>
          </tr>
        </table>

        <script type="text/javascript">
            function showVersionsTable( boolScrollToVersionTable )
            {
              var showHideVersionsButton = document.getElementById('showHideVersionsButton');
              var versionTableScrollableBodyElem = document.getElementById('version-list-table-scrollable-body');

              document.getElementById('version-list-wrapper').style.display = 'block';

              showHideVersionsButton.innerHTML = '%hideVersions% (<xsl:value-of select="count(/contents/content/versions/version[@status != 1])"/>)';
              showHideVersionsButton.title = '%hideVersions%';

              cms.utils.Cookie.create('versionsCssDisplayState', 'block', 100);

              if ( boolScrollToVersionTable )
              {
                document.location.hash = 'version-list-wrapper';
                versionTableScrollableBodyElem.scrollTop = 0;
              }
            }

            function hideVersionsTable()
            {
              document.getElementById('version-list-wrapper').style.display = 'none';

              var showHideVersionsButton = document.getElementById('showHideVersionsButton');
              showHideVersionsButton.innerHTML = '%showVersions% (<xsl:value-of select="count(/contents/content/versions/version[@status != 1])"/>)';
              showHideVersionsButton.title = '%showVersions%';

              cms.utils.Cookie.create('versionsCssDisplayState', 'none', 100);
            }

            function showHideVersionsTable()
            {
              var versionsTable = document.getElementById('version-list-wrapper');
              if (versionsTable.style.display == 'none')
              {
                showVersionsTable();
              }
              else
              {
                hideVersionsTable();
              }
            }

            function showHideSnapshots( sourceKey )
            {
              var plusMinusButton = document.getElementById('show-hide-snapshots-button-' + sourceKey );
              var show = plusMinusButton.className == 'expand-snapshots-button hand';

              if ( show )
              {
                plusMinusButton.src = 'images/icon-minus-grey.png';
                plusMinusButton.className = 'hide-snapshots-button hand';
                show = true;
              }
              else
              {
                plusMinusButton.src = 'images/icon-plus-grey.png';
                plusMinusButton.className = 'expand-snapshots-button hand';
                show = false;
              }

              var tableBody = document.getElementById('version-list-table-scrollable-body');
              var trElems = tableBody.getElementsByTagName('tr');
              var i, trElem;

              for ( i = 0; i &lt; trElems.length; i++ )
              {
                trElem = trElems[i];

                if ( trElem.className === 'snapshot-for-' + sourceKey )
                {
                  trElem.style.display = show ? '' : 'none';
                }
              }
            }

            function scrollToVersion( sourceVersionKey, snapshotVersionKey )
            {
              var versionTableScrollableBodyElem = document.getElementById('version-list-table-scrollable-body');
              var versionTableScrollableBodyYpos = /* admin.js */ findPosY(versionTableScrollableBodyElem);
              var versionRowForSelectedVersion;

              if ( snapshotVersionKey !== '' )
              {
                  versionRowForSelectedVersion = document.getElementById('row-' + sourceVersionKey + '-' + snapshotVersionKey);
              }
              else
              {
                  versionRowForSelectedVersion = document.getElementById('row-' + sourceVersionKey);
              }

              var versionRowForSelectedVersionYPos = /* admin.js */ findPosY(versionRowForSelectedVersion);

              versionTableScrollableBodyElem.scrollTop = versionRowForSelectedVersionYPos - versionTableScrollableBodyYpos;
            }
        </script>

      </div>

      <xsl:if test="count(/contents/content/versions/version) &gt; 1 or $editlockedversionmode-and-content-has-not-draft">
        <xsl:call-template name="version-list-table"/>
      </xsl:if>

    </fieldset>

  </xsl:template>

  <xsl:template name="version-form">

    <script type="text/javascript" language="JavaScript">
        function changeVersion( versionKey )
        {
            var form = document.forms['versionForm'];

            form.op.value = 'form';
            form.versionkey.value = versionKey;
            form.submit();
        }
        // -----------------------------------------------------------------------------------------------------------------------------------
      
        function deleteVersion( versionKey, hasAssignee, selectedVersionIsSnapshot )
        {
            var confirmMessageText = selectedVersionIsSnapshot ? '%alertDeleteSnapshotVersion%' : '%alertDeleteVersion%';

            if ( hasAssignee &amp;&amp; !selectedVersionIsSnapshot )
            {
                confirmMessageText += '\n%alertNoteAssignmentWillAlsoByRemoved%';
            }

            if ( confirm( confirmMessageText ) )
            {
                var form = document.forms['versionForm'];

                form.op.value = 'delete_version';
                form.versionkey.value = versionKey;
                form.submit();
            }
        }
        // -----------------------------------------------------------------------------------------------------------------------------------

        function editVersion( versionKey )
        {
            var form = document.forms['versionForm'];

            form.op.value = 'form';
            form.versionkey.value = versionKey;
            form.editlockedversionmode.value = 'true';
            form.submit();
        }
    </script>

    <form name="versionForm" method="get" action="adminpage">
      <input type="hidden" name="op"/>
      <input type="hidden" name="page" value="{$page}"/>
      <input type="hidden" name="cat" value="{/contents[1]/content[1]/categoryname/@key}"/>
      <input type="hidden" name="referer" value="{$referer}"/>
      <input type="hidden" name="key" value="{/contents[1]/content[1]/@key}"/>
      <input type="hidden" name="versionkey"/>
      <input type="hidden" name="selectedtabpage" value="tab-page-version"/>
      <input type="hidden" name="editlockedversionmode" value="false"/>
    </form>
  </xsl:template>

  <xsl:template name="version-list-table">

    <xsl:variable name="selected-version-key" select="/contents/content/@versionkey"/>
    <xsl:variable name="selected-version-is-snapshot" select="/contents/content/@status = 1"/>
    <xsl:variable name="source-version-key-to-selected-version" select="/contents/content/versions/version[ @key = $selected-version-key ]/@snapshotSource"/>
    <xsl:variable name="selected-version-is-snapshot-and-source-is-draft" select="$selected-version-is-snapshot and /contents/content/versions/version[ @key = $source-version-key-to-selected-version ]/@status = 0"/>

    <!-- Debug -->
    <!--
    <div style="clear:both;">
      $selected-version-key: <xsl:value-of select="$selected-version-key"/><br/>
      $selected-version-is-snapshot: <xsl:value-of select="$selected-version-is-snapshot"/><br/>
      $editlockedversionmode: <xsl:value-of select="$editlockedversionmode"/><br/>
      $selected-version-is-snapshot-and-source-is-draft: <xsl:value-of select="$selected-version-is-snapshot-and-source-is-draft"/><br/>
      $source-version-key-to-selected-version: <xsl:value-of select="$source-version-key-to-selected-version"/><br/>
      $editlockedversionmode-and-content-has-draft: <xsl:value-of select="$editlockedversionmode-and-content-has-draft"/><br/>
      $editlockedversionmode-and-content-has-not-draft: <xsl:value-of select="$editlockedversionmode-and-content-has-not-draft"/><br/>
    </div>
    -->

    <div id="version-list-wrapper">

      <!-- Table header -->
      <table cellpadding="3" cellspacing="0" border="0" width="100%" class="browsetable no-bottom-border" style="background-color:#fff">
        <tr>
          <td width="38" class="browsetablecolumnheader default-cursor">&nbsp;</td>
          <td width="42" class="browsetablecolumnheader default-cursor" style="text-align: center">
            <xsl:text>%fldStatus%</xsl:text>
          </td>
          <td width="100" class="browsetablecolumnheader default-cursor">
            <xsl:text>%fldModified%</xsl:text>
          </td>
          <td class="browsetablecolumnheader default-cursor">
            <xsl:text>%fldChangeComment%</xsl:text>
          </td>
          <td width="160" class="browsetablecolumnheader default-cursor">
            <xsl:text>%fldModifiedBy%</xsl:text>
          </td>
        </tr>
      </table>

      <div class="version-list-table-scrollable-body" id="version-list-table-scrollable-body">
        <table cellpadding="3" cellspacing="0" border="0" width="100%" class="browsetable no-top-border no-bottom-border" style="background-color:#fff">

          <!-- Draft -->
          <xsl:choose>
            <xsl:when test="$editlockedversionmode-and-content-has-not-draft">
              <!-- selected version is new and unsaaved -->
              <xsl:call-template name="source-version">
                <xsl:with-param name="name" select="'drafts'"/>
                <xsl:with-param name="versions" select="/do-not-exists"/>
                <xsl:with-param name="selected-version-is-unsaved" select="true()"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:when test="$selected-version-is-snapshot-and-source-is-draft">
              <!-- selected version is snapshot, source version is snapshotSource -->
              <xsl:call-template name="source-version">
                <xsl:with-param name="name" select="'drafts'"/>
                <xsl:with-param name="versions" select="/contents[1]/content[1]/versions/version[@status = 0 and @key = $source-version-key-to-selected-version]"/>
                <xsl:with-param name="selected-version-is-in-edit-mode" select="true()"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <!-- source version is one and only draft -->
              <xsl:call-template name="source-version">
                <xsl:with-param name="name" select="'drafts'"/>
                <xsl:with-param name="versions" select="/contents[1]/content[1]/versions/version[@status = 0]"/>
                <xsl:with-param name="selected-version-is-in-edit-mode" select="false()"/>
              </xsl:call-template>
            </xsl:otherwise>
          </xsl:choose>

          <!-- Approved -->
          <xsl:call-template name="source-version">
            <xsl:with-param name="name" select="'approved'"/>
            <xsl:with-param name="versions" select="/contents[1]/content[1]/versions/version[@current = 'true' and @status = 2]"/>
            <xsl:with-param name="selected-version-is-in-edit-mode" select="false()"/>
          </xsl:call-template>

          <!-- History / Archived -->
          <xsl:call-template name="source-version">
            <xsl:with-param name="name" select="'history'"/>
            <xsl:with-param name="versions"
                            select="/contents[1]/content[1]/versions/version[ ( @status = 2 and not(@current = 'true') ) or @status = 3 ]"/>
            <xsl:with-param name="selected-version-is-in-edit-mode" select="false()"/>
          </xsl:call-template>

        </table>
      </div>
    </div>

    <script type="text/javascript">
      var versionsCssDisplayState = cms.utils.Cookie.read('versionsCssDisplayState');

      if ( versionsCssDisplayState &amp;&amp; versionsCssDisplayState == 'block')
      {
        showVersionsTable();
      }
      else
      {
        hideVersionsTable();
      }

      <xsl:if test="not($editlockedversionmode)">
        <xsl:choose>
          <xsl:when test="$source-version-key-to-selected-version != ''">
            scrollToVersion('<xsl:value-of select="$source-version-key-to-selected-version"/>', '<xsl:value-of
              select="/contents/content/@versionkey"/>');
          </xsl:when>
          <xsl:otherwise>
            scrollToVersion('<xsl:value-of select="/contents/content/@versionkey"/>', '');
          </xsl:otherwise>
        </xsl:choose>
      </xsl:if>
    </script>

  </xsl:template>

  <xsl:template name="source-version">
    <xsl:param name="name"/>
    <xsl:param name="versions"/>
    <xsl:param name="selected-version-is-in-edit-mode" select="false()"/>
    <xsl:param name="selected-version-is-unsaved" select="false()"/>

    <xsl:variable name="status-name">
      <xsl:choose>
        <xsl:when test="$name = 'drafts'">
          <xsl:text>%blockDrafts%</xsl:text>
        </xsl:when>
        <xsl:when test="$name = 'approved'">
          <xsl:text>%blockApprovedVersion%</xsl:text>
        </xsl:when>
        <xsl:when test="$name = 'history'">
          <xsl:text>%blockHistory%</xsl:text>
        </xsl:when>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="remove-bottom-border-from-row" select="count($versions) = 0 and not($selected-version-is-unsaved)"/>

    <xsl:variable name="css-th-class">
      <xsl:text>list-group-name</xsl:text>
      <xsl:if test="$remove-bottom-border-from-row">
        <xsl:text> row-last</xsl:text>
      </xsl:if>
    </xsl:variable>

    <xsl:if test="count($versions) &gt; 0 or $selected-version-is-unsaved">
      <tr>
        <th colspan="10" class="{$css-th-class}">
          <xsl:value-of select="$status-name"/>
        </th>
      </tr>

      <xsl:choose>
        <xsl:when test="$selected-version-is-unsaved">
          <!-- special case for ulagra version -->
          <tr id="row-unsaved" class="tablerowpainter_darkrow">
            <xsl:attribute name="onmouseover">javascript:this.className='tablerowpainter_mouseoverrow';</xsl:attribute>
            <xsl:attribute name="onmouseout">javascript:this.className='tablerowpainter_darkrow';</xsl:attribute>

            <!-- Selected -->
            <td width="38">
              <xsl:call-template name="unsaved-version-cell-attributes"/>

              <span style="width:10px">
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              </span>

              &nbsp;<img alt="" src="images/icon_edit_small.gif" style="vertical-align: middle"/>
            </td>

            <!-- Status Icon -->
            <td style="text-align:center" width="42">
              <xsl:call-template name="unsaved-version-cell-attributes"/>

              <xsl:call-template name="publishstatus">
                <xsl:with-param name="state" select="0"/>
                <xsl:with-param name="unsaved-draft" select="true()"/>
              </xsl:call-template>
            </td>

            <!-- Modified -->
            <td style="white-space: nowrap" width="100">
              <xsl:call-template name="unsaved-version-cell-attributes"/>

              <br/>
            </td>

            <!-- Comment -->
            <td>
              <xsl:call-template name="unsaved-version-cell-attributes"/>

              <xsl:call-template name="version-comment-field">
                <xsl:with-param name="comment" select="@comment"/>
              </xsl:call-template>
            </td>

            <!-- Last Modified By -->
            <td style="white-space: nowrap" width="160">
              <xsl:call-template name="unsaved-version-cell-attributes"/>

              <br/>
            </td>

          </tr>

        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="$versions" mode="source-version-row">
            <xsl:with-param name="selected-version" select="/contents[1]/content[1]/@versionkey"/>
            <xsl:with-param name="selected-version-is-in-edit-mode" select="$selected-version-is-in-edit-mode"/>
          </xsl:apply-templates>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>

  <xsl:template match="version" mode="source-version-row">
    <xsl:param name="selected-version"/>
    <xsl:param name="selected-version-is-in-edit-mode" select="false()"/>

    <xsl:variable name="is-selected" select="$selected-version = @key and not($selected-version-is-unsaved-draft)"/>
    <xsl:variable name="has-snapshot" select="count(/contents/content/versions/version[@snapshotSource = current()/@key]) &gt; 0"/>
    <xsl:variable name="selected-is-draft" select="$is-selected and /contents[1]/content[1]/@status = 0"/>
    <xsl:variable name="snapshot-source-of-selected-version" select="/contents/content/versions/version[@key = $selected-version]/@snapshotSource"/>
    <xsl:variable name="is-snapshot-source-of-selected-version" select="@key = $snapshot-source-of-selected-version"/>
    <xsl:variable name="expand-snapshots" select="$has-snapshot"/>

    <tr id="row-{@key}">
      <xsl:choose>
        <xsl:when test="position() mod 2 = 1">
          <xsl:attribute name="class">tablerowpainter_darkrow</xsl:attribute>
          <xsl:attribute name="onmouseover">javascript:this.className='tablerowpainter_mouseoverrow';</xsl:attribute>
          <xsl:attribute name="onmouseout">javascript:this.className='tablerowpainter_darkrow';</xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="onmouseover">javascript:this.className='tablerowpainter_mouseoverrow'</xsl:attribute>
          <xsl:attribute name="onmouseout">javascript:this.className='';</xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>

      <!-- Selected -->
      <td width="38">
        <xsl:call-template name="source-version-cell-attributes">
          <xsl:with-param name="selected" select="$is-selected"/>
          <xsl:with-param name="is-clickable" select="false()"/>
        </xsl:call-template>

        <xsl:variable name="snapshot-source-to-expand">
          <xsl:choose>
            <xsl:when test="/contents/content/@state = 1 and $is-selected">
              <xsl:value-of select="$snapshot-source-of-selected-version"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="@key"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <span style="width:10px">
          <xsl:choose>
            <xsl:when test="$expand-snapshots">
              <img src="images/icon-minus-grey.png" id="show-hide-snapshots-button-{$snapshot-source-to-expand}" class="hide-snapshots-button hand" style="vertical-align:middle" onclick="javascript:showHideSnapshots({$snapshot-source-to-expand});"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:choose>
                <xsl:when test="not($selected-version-is-in-edit-mode) and $has-snapshot">
                  <img src="images/icon-plus-grey.png" id="show-hide-snapshots-button-{$snapshot-source-to-expand}" class="expand-snapshots-button hand" style="vertical-align:middle" onclick="javascript:showHideSnapshots({$snapshot-source-to-expand});"/>
                </xsl:when>
                <xsl:otherwise>
                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                </xsl:otherwise>
              </xsl:choose>
            </xsl:otherwise>
          </xsl:choose>
        </span>
        
        <xsl:if test="$is-selected">
          &nbsp;<img alt="" src="images/icon_edit_small.gif" style="vertical-align: middle"/>
        </xsl:if>

      </td>

      <!-- Status Icon -->
      <td style="text-align:center" width="42">
        <xsl:call-template name="source-version-cell-attributes">
          <xsl:with-param name="selected" select="$is-selected"/>
          <xsl:with-param name="is-clickable" select="not($is-selected)"/>
        </xsl:call-template>

        <xsl:choose>
          <xsl:when test="$selected-version-is-in-edit-mode">
            <xsl:call-template name="publishstatus">
              <xsl:with-param name="state" select="0"/>
              <xsl:with-param name="publishfrom" select="/contents/content/@publishfrom"/>
              <xsl:with-param name="publishto" select="/contents/content/@publishto"/>
              <xsl:with-param name="unsaved-draft" select="$selected-version-is-unsaved-draft"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="@current = 'true'">
                <xsl:call-template name="publishstatus">
                  <xsl:with-param name="state" select="@state"/>
                  <xsl:with-param name="publishfrom" select="/contents/content/@publishfrom"/>
                  <xsl:with-param name="publishto" select="/contents/content/@publishto"/>
                  <xsl:with-param name="unsaved-draft" select="$selected-version-is-unsaved-draft"/>
                </xsl:call-template>
              </xsl:when>
              <xsl:otherwise>
                <xsl:call-template name="publishstatus">
                  <xsl:with-param name="state" select="@state"/>
                  <xsl:with-param name="unsaved-draft" select="$selected-version-is-unsaved-draft"/>
                </xsl:call-template>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>
      </td>

      <!-- Modified -->
      <td style="white-space: nowrap" width="100">
        <xsl:call-template name="source-version-cell-attributes">
          <xsl:with-param name="selected" select="$is-selected"/>
          <xsl:with-param name="is-clickable" select="not($is-selected)"/>
        </xsl:call-template>

          <xsl:call-template name="formatdatetime">
            <xsl:with-param name="date" select="@timestamp"/>
          </xsl:call-template>

        <a style="display: none" href="javascript:changeVersion('{@key}')" id="operation_goto_{@key}">
          &nbsp;
        </a>
      </td>

      <!-- Comment -->
      <td>
        <xsl:call-template name="source-version-cell-attributes">
          <xsl:with-param name="selected" select="$is-selected"/>
          <xsl:with-param name="is-clickable" select="not($is-selected)"/>
        </xsl:call-template>

        <xsl:if test="$is-selected and not($selected-is-draft)">
          <input type="hidden" name="_comment" value="{@comment}" maxlength="140" id="_comment"/>
        </xsl:if>        

        <!-- Debug -->
        <!--
        selected-version-is-in-edit-mode: <xsl:value-of select="$selected-version-is-in-edit-mode"/><br/>
        selected-version: <xsl:value-of select="$selected-version"/><br/>
        @key: <xsl:value-of select="@key"/><br/>
        is-selected: <xsl:value-of select="$is-selected"/><br/>
        has-snapshot: <xsl:value-of select="$has-snapshot"/><br/>
        selected-is-draft: <xsl:value-of select="$selected-is-draft"/><br/>
        snapshot-source-of-selected-version: <xsl:value-of select="$snapshot-source-of-selected-version"/><br/>
        is-snapshot-source-of-selected-version: <xsl:value-of select="$is-snapshot-source-of-selected-version"/><br/>
        expand-snapshots: <xsl:value-of select="$expand-snapshots"/><br/>
        -->


        <xsl:choose>
          <xsl:when test="$selected-is-draft">
            <xsl:call-template name="version-comment-field">
              <xsl:with-param name="comment" select="@comment"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="@comment != ''">
                <xsl:value-of select="@comment"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>&nbsp;</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>

      </td>

      <!-- Last Modified By -->
      <td style="white-space: nowrap" width="160">
        <xsl:call-template name="source-version-cell-attributes">
          <xsl:with-param name="selected" select="$is-selected"/>
          <xsl:with-param name="is-clickable" select="not($is-selected)"/>
        </xsl:call-template>

          <xsl:value-of select="concat(@modifierFullName, ' (', @modifierQualifiedName, ')')"/>
      </td>
    </tr>

    <xsl:choose>
      <xsl:when test="/contents/content/@state = 1 and @key = /contents/content/@versionkey">
        <xsl:call-template name="snapshots">
          <xsl:with-param name="snapshots"
                          select="/contents/content/versions/version[@key != /contents/content/@versionkey and @snapshotSource = /contents/content/versions/version[@key = /contents/content/@versionkey]/@snapshotSource]"/>
          <xsl:with-param name="expand" select="$expand-snapshots"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="snapshots">
          <xsl:with-param name="snapshots"
                          select="/contents/content/versions/version[@snapshotSource = current()/@key]"/>
          <xsl:with-param name="expand" select="$expand-snapshots"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>


  </xsl:template>

  <xsl:template name="snapshots">
    <xsl:param name="snapshots"/>
    <xsl:param name="expand"/>

    <xsl:variable name="css-class-name" select="'browsetablecell'"/>

    <xsl:for-each select="$snapshots">

      <xsl:variable name="is-selected" select="/contents[1]/content[1]/@versionkey = @key"/>
      <xsl:variable name="source-key" select="@snapshotSource"/>

      <tr id="{ concat('row-',$source-key,'-',current()/@key) }" class="snapshot-for-{$source-key}">

        <xsl:if test="not($expand)">
          <xsl:attribute name="style">display: none</xsl:attribute>
        </xsl:if>

        <!-- Selected -->
        <td width="38">
          <xsl:call-template name="snapshot-version-cell-attributes">
            <xsl:with-param name="selected" select="$is-selected"/>
            <xsl:with-param name="is-clickable" select="not($is-selected)"/>
          </xsl:call-template>

          <xsl:if test="$is-selected and not($editlockedversionmode-and-content-has-draft)">
            <span style="padding-left:22px">
              <img alt="" src="images/icon_edit_small.gif" style="vertical-align: middle"/>
            </span>
          </xsl:if>
          <br/>
        </td>

        <!-- Status Icon -->
        <td style="text-align:center" width="42" class="{$css-class-name}">
          <xsl:call-template name="snapshot-version-cell-attributes">
            <xsl:with-param name="selected" select="$is-selected"/>
            <xsl:with-param name="is-clickable" select="not($is-selected)"/>
          </xsl:call-template>
          <span style="padding-left: 22px">
            <xsl:call-template name="publishstatus">
              <xsl:with-param name="state" select="1"/>
            </xsl:call-template>
          </span>
        </td>

        <!-- Modified -->
        <td style="white-space: nowrap" width="100">
          <xsl:call-template name="snapshot-version-cell-attributes">
            <xsl:with-param name="selected" select="$is-selected"/>
            <xsl:with-param name="is-clickable" select="not($is-selected)"/>
          </xsl:call-template>

          <xsl:call-template name="formatdatetime">
            <xsl:with-param name="date" select="@timestamp"/>
          </xsl:call-template>

          <a style="display: none" href="javascript:changeVersion('{@key}')" id="operation_goto_{@key}">
            &nbsp;
          </a>
        </td>
        
        <!-- Comment -->
        <td class="{$css-class-name}">
          <xsl:call-template name="snapshot-version-cell-attributes">
            <xsl:with-param name="selected" select="$is-selected"/>
            <xsl:with-param name="is-clickable" select="not($is-selected)"/>
          </xsl:call-template>

          <xsl:if test="$is-selected">
            <input type="hidden" name="_comment" value="{@comment}" maxlength="140" id="_comment"/>
          </xsl:if>

          <xsl:choose>
            <xsl:when test="@comment != ''">
              <xsl:value-of select="@comment"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>&nbsp;</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </td>

        <!-- Last Modified By -->
        <td style="white-space: nowrap" width="160">
          <xsl:call-template name="snapshot-version-cell-attributes">
            <xsl:with-param name="selected" select="$is-selected"/>
            <xsl:with-param name="is-clickable" select="not($is-selected)"/>
          </xsl:call-template>
          <xsl:value-of select="concat(@modifierFullName, ' (', @modifierQualifiedName, ')')"/>
        </td>

      </tr>

    </xsl:for-each>
  </xsl:template>

  <xsl:template name="thrash-can">
    <xsl:variable name="selectedIsnotMainVersionAndDraft" select="not(boolean(/contents/content/@current = 'true') and /contents/content/@status = 0)"/>
    <xsl:variable name="selectedVersionIsSnapshot" select="/contents/content/@status = 1"/>
    <xsl:if test="$selectedIsnotMainVersionAndDraft and count(/contents/content/versions/version) &gt; 1 and (/contents/content/@status = 0 or /contents/content/@status = 1)">
      <a title="%msgClickToDeleteVersion%" style="display: block; float: right;">
        <xsl:attribute name="href">
          <xsl:text>javascript:deleteVersion(</xsl:text>
          <xsl:value-of select="/contents/content/@versionkey"/>
          <xsl:text>,</xsl:text>
          <xsl:value-of select="/contents/content/@is-assigned = 'true'"/>,
          <xsl:value-of select="$selectedVersionIsSnapshot"/>
          <xsl:text>);</xsl:text>
        </xsl:attribute>
        <img src="images/icon_delete.gif" border="0" alt=""/>
      </a>
    </xsl:if>
  </xsl:template>

  <xsl:template name="source-version-cell-attributes">
    <xsl:param name="selected"/>
    <xsl:param name="is-clickable" select="true()"/>
    <xsl:variable name="toolTip">
      <xsl:choose>
        <xsl:when test="$selected">
          <xsl:value-of select="concat('%msgWorkingVersion% (', @key, ')')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="concat('%msgClickToSelectVersion% (', @key, ')')"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:attribute name="title">
      <xsl:value-of select="$toolTip"/>
    </xsl:attribute>
    <xsl:if test="$is-clickable">
      <xsl:attribute name="onclick">
        <xsl:text>javascript:if( document.all) {</xsl:text>
        <xsl:text>document.getElementById('operation_goto_</xsl:text>
        <xsl:value-of select="@key"/>
        <xsl:text>').click();</xsl:text>
        <xsl:text> } else { document.location.href = document.getElementById('operation_goto_</xsl:text>
        <xsl:value-of select="@key"/>
        <xsl:text>').href; }</xsl:text>
      </xsl:attribute>
    </xsl:if>
    <xsl:attribute name="class">
      <xsl:text>browsetablecell</xsl:text>
      <xsl:if test="not($is-clickable)">
        <xsl:text> no-action</xsl:text>
      </xsl:if>
    </xsl:attribute>
  </xsl:template>

  <xsl:template name="unsaved-version-cell-attributes">
    <xsl:attribute name="title">
      <xsl:text>%txtContentState0NotSaved%</xsl:text>
    </xsl:attribute>
    <xsl:attribute name="class">
      <xsl:text>browsetablecell no-action</xsl:text>
    </xsl:attribute>
  </xsl:template>

  <xsl:template name="snapshot-version-cell-attributes">
    <xsl:param name="selected"/>
    <xsl:param name="is-clickable" select="true()"/>
    <xsl:variable name="source-is-draft" select="/contents/content/versions/version[@key = current()/@snapshotSource]/@status = 0"/>
    <xsl:variable name="toolTip">
      <xsl:choose>
        <xsl:when test="$selected">
          <xsl:value-of select="concat('%msgWorkingSnapshot% (', @key, ')')"/>
        </xsl:when>
        <xsl:when test="$selected or not($source-is-draft)">
          <xsl:value-of select="concat('%msgVersionCanNotBeEdited% (', @key, ')')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="concat('%msgClickToSelectSnapshot% (', @key, ')')"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:attribute name="title">
      <xsl:value-of select="$toolTip"/>
    </xsl:attribute>

    <xsl:if test="$is-clickable">
      <xsl:attribute name="onclick">
        <xsl:text>javascript:if( document.all) {</xsl:text>
        <xsl:text>document.getElementById('operation_goto_</xsl:text>
        <xsl:value-of select="@key"/>
        <xsl:text>').click();</xsl:text>
        <xsl:text> } else { document.location.href = document.getElementById('operation_goto_</xsl:text>
        <xsl:value-of select="@key"/>
        <xsl:text>').href; }</xsl:text>
      </xsl:attribute>
    </xsl:if>

    <xsl:attribute name="class">
      <xsl:text>browsetablecell</xsl:text>
      <xsl:if test="not($is-clickable)">
        <xsl:text> no-action</xsl:text>
      </xsl:if>
    </xsl:attribute>
  </xsl:template>

  <xsl:template name="version-comment-field">
    <xsl:param name="comment"/>

    <xsl:variable name="_comment">
      <xsl:choose>
        <xsl:when test="string-length($comment) &gt; 0">
          <xsl:value-of select="$comment"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>%versionCommentPlaceholderText%</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <input type="text" name="_comment" value="{$_comment}" maxlength="140" id="_comment"
           onfocus="content_form_focusVersionCommentField(this)" onblur="content_form_blurVersionCommentField(this)" title="%fldChangeComment%">
      <xsl:attribute name="class">
        <xsl:text>textfield comment-input</xsl:text>
        <xsl:if test="string-length($comment) = 0">
          <xsl:text> placeholder-text</xsl:text>
        </xsl:if>
      </xsl:attribute>
    </input>
  </xsl:template>

</xsl:stylesheet>