<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [<!ENTITY nbsp "&#160;">]>

<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

   <!-- map of allowed content-types in page-templates in sites by content-type-key -->
  <xsl:key name="key-page-template-content-type" match="/*/pagetemplates-in-sites/pagetemplates-in-site/pagetemplate/contenttypes/contenttype" use="@key"/>

  <xsl:template name="contentformbuttons">
    <xsl:param name="formname" select="'formAdmin'"/>
    <xsl:param name="subfunctions" select="''"/>
    <xsl:param name="enablepreview" select="false()"/>
    <xsl:param name="topbuttons" select="false()"/>
    <xsl:param name="show-edit-content-button" select="true()"/>

    <xsl:if test="$selectedtabpage and not($topbuttons)">
      <script language="javascript">
        tabPane1.setSelectedPage('<xsl:value-of select="$selectedtabpage"/>');
      </script>
    </xsl:if>

    <xsl:variable name="selected-version-is-draft" select="/contents/content/@state = 0"/>

    <xsl:variable name="selected-version-is-approved" select="/contents[1]/content[1]/@status = 2"/>

    <xsl:variable name="selected-version-is-source" select="$new or /contents[1]/content[1]/@status != 1"/>

    <xsl:variable name="selected-version-is-snapshot" select="/contents[1]/content[1]/@status = 1"/>

    <xsl:variable name="selectedVersionIsArchived" select="/contents/content/@status = 3"/>

    <xsl:variable name="includesave" select="($new and $categorycreate) or $contentupdate or $categorypublish"/>

    <xsl:variable name="includeapprove"
                  select="$categorypublish and (/contents/content/@state = 0 or $editlockedversionmode or $create = 1)"/>

    <xsl:variable name="includeassignto"
                  select="$categorycreate and (/contents/content/@state = 0 or $editlockedversionmode or $create = 1)"/>

    <xsl:variable name="includetakesnapshot"
                  select="$categorycreate and ($create = 1 or $editlockedversionmode or /contents/content/@state = 0)"/>

    <!--
      Include publish (publish wizard) button

      All users which has create access right in the category must see the publish button.
      This is because wether the create-only-user can approve or administrate the content is determined in the publishing wizard step 2.
    -->
    <xsl:variable name="includepublish"
                  select="$categorypublish and $selected-version-is-source and (not($selectedVersionIsArchived) or $editlockedversionmode)"/>

    <xsl:variable name="includearchive"
                  select="$categorypublish and not($includetakesnapshot) and $selected-version-is-source and not($selectedVersionIsArchived)"/>

    <xsl:variable name="show-save-button-dropdown"
                  select="$includeassignto or $includetakesnapshot or $includeapprove or $includepublish or $includearchive and not($selected-version-is-snapshot)"/>

    <xsl:variable name="has-draft" select="count(/contents[1]/content[1]/versions/version[@status = 0]) &gt; 0"/>

    <xsl:variable name="draft-version-key" select="/contents[1]/content[1]/versions/version[@status = 0]/@key"/>

    <xsl:variable name="reassign"
                  select="string-length(/contents/content/assignee/@key) != 0 and /contents/content/assignee/@key != $currentuser_key"/>

    <script type="text/javascript">
      // *********************************************************************************************************************************
      // *** Global variables
      // *********************************************************************************************************************************
      var g_formName = '<xsl:value-of select="$formname"/>';
      var g_form = document.forms[g_formName];
      var g_enableForm = <xsl:value-of select="$enableform"/>;
      var g_origFormAction = g_form.getAttribute("action");

      // ---------------------------------------------------------------------------------------------------------------------------------

      function previewContent( page, contentKey, versionKey )
      {
      var action = g_form.getAttribute("action");
      var newAction = "adminpage?op=preview&amp;subop=frameset&amp;page=" + page;
      if ( contentKey &gt;= 0 )
      newAction = newAction + "&amp;contentkey=" + contentKey;

      if ( versionKey &gt;= 0 )
      newAction = newAction + "&amp;versionkey=" + versionKey;

      if ( g_enableForm )
      {
      newAction = newAction + "&amp;sessiondata=true";
      g_form.setAttribute( "action", newAction );
      g_form.setAttribute( "target", "_blank" );
      validateAll( g_formName, true );
      }
      else
      {
      g_form.setAttribute("action", newAction);
      g_form.setAttribute("target", "_blank");
      g_form.submit();
      }

      // set old form attributes
      g_form.setAttribute("action", action);
      g_form.removeAttribute("target");
      }
      // ---------------------------------------------------------------------------------------------------------------------------------

      function disableFormButtons()
      {
      var action = g_form.action;
      if ( action.indexOf("preview") != -1 )
      return;

      var buttons = new Array("previewbtn", "closebtn", "cancelbtn", "editbtn", "savebtn");
      disableTextButtons(buttons);

      <xsl:if test="$show-save-button-dropdown">
        splitButton_saveSplitButtonTop.setDisabled( true );
        splitButton_saveSplitButtonBottom.setDisabled( true );
      </xsl:if>

      var buttons = document.getElementsByName("closebtn2");
      if ( buttons != null )
      {
      for ( var i=0; i&lt;buttons.length; i++ )
      {
      buttons[i].href = '#';
      }
      }
      }
      // ---------------------------------------------------------------------------------------------------------------------------------

      function submitForm(action)
      {

      <xsl:if test="$editlockedversionmode">
        if ( typeof handleBeforeUnLoad === 'function' )
        {
        removeEvent( window, 'beforeunload', handleBeforeUnLoad );
        }
      </xsl:if>

      // Load the content from htmlarea iframe documents to the hidden htmlarea textareas.
      if ( !window.tinyMCE === undefined )
      tinyMCE.triggerSave();

      g_form.selectedtabpage.value = tabPane1.getSelectedPage();
      g_form.target = "";

      <xsl:if test="$subfunctions != ''">
        <xsl:value-of select="$subfunctions"/>
      </xsl:if>

      var formAction = g_origFormAction;

      if ( action == "assignto" )
      {
      formAction = formAction + "&amp;assignto=true";
      }
      // TODO: Ikke i bruk. Sjekk med server før en tar bort.
      else if ( action == "createnewversion" )
      {
      <xsl:if test="$has-draft">
        alert('%msgPleaseUseExistingDraft%');
        if ( tabPane1 )
        {
        tabPane1.setSelectedPage('tab-page-1');
        }
        showVersionsTable( true );
        return;
      </xsl:if>
      formAction = formAction + "&amp;createnewversion=true";

      }
      else if ( action == "snapshot" )
      {
      formAction = formAction;
      <xsl:if test="not($new)">
        <xsl:text>formAction += "&amp;_create_snapshot=true"</xsl:text>
      </xsl:if>
      }
      else if ( action == "approve" )
      {
      formAction = formAction + "&amp;published=true";
      }
      else if ( action == "approveAndClose" )
      {
      formAction = formAction + "&amp;published=true&amp;closeaftersuccess=true";
      }
      else if ( action == "publish" )
      {
      formAction = formAction + "&amp;addtosection=true";
      }
      else if ( action == "archive" )
      {
      var pubStatusElement = document.getElementById('_pubdata_status');
      if ( pubStatusElement )
      {
      pubStatusElement.value = 3;
      }

      <xsl:if test="/contents/content/@approved = 'true' and number(/contents/content/@state) = 5">
        var archiveMainVersion = confirm('%msgConfirmArchiveMainVersion%');
        if ( !archiveMainVersion )
        {
        return;
        }
      </xsl:if>

      }

      g_form.setAttribute( "action", formAction );

      removePlaceholderTextFromVersionComment();

      if ( g_enableForm )
      {
      validateAll( g_formName );
      }
      else
      {
      disableFormButtons();
      g_form.submit();
      }
      }
      // ---------------------------------------------------------------------------------------------------------------------------------

      function removePlaceholderTextFromVersionComment()
      {
      var versionCommentField = document.getElementById('_comment');

      if ( versionCommentField !== null )
      {
      if ( versionCommentField.className.indexOf('placeholder-text') &gt; -1 )
      {
      versionCommentField.value = '';
      }
      }
      }

      function editContentVersion()
      {
      <xsl:if test="$has-draft">
        var confirmOverwriteDraft = confirm('%msgOverwriteDraftQuestion%');

        if ( !confirmOverwriteDraft )
        {
        return;
        }
      </xsl:if>

      var currentLocationHref = document.location.href;
      var modifiedUrl = currentLocationHref;

      if ( getUrlParameter(modifiedUrl, 'editlockedversionmode') === '' || getUrlParameter(modifiedUrl, 'editlockedversionmode') === 'false'
      )
      {
      if ( getUrlParameter(modifiedUrl, 'editlockedversionmode') === 'false' )
      {
      modifiedUrl = removeParamFromURL(modifiedUrl, 'editlockedversionmode');
      }
      modifiedUrl += '&amp;editlockedversionmode=true';
      }

      if ( getUrlParameter(modifiedUrl, 'referer') === '' )
      {
      modifiedUrl += '&amp;referer=<xsl:value-of select="admin:urlEncode(string($referer))"/>';
      }

      if ( getUrlParameter(modifiedUrl, 'feedback') !== '' )
      {
      modifiedUrl = removeParamFromURL(modifiedUrl, 'feedback');
      }

      <xsl:if test="$has-draft">
        modifiedUrl = removeParamFromURL(modifiedUrl, 'versionkey');
        modifiedUrl += '&amp;versionkey=<xsl:value-of select="$draft-version-key"/>';
      </xsl:if>

      modifiedUrl = removeParamFromURL(modifiedUrl, 'populateFromVersion');
      modifiedUrl += '&amp;populateFromVersion=<xsl:value-of select="/contents/content/@versionkey"/>';

      document.location.href = modifiedUrl;
      }
      // ---------------------------------------------------------------------------------------------------------------------------------

      function cancelEditContentVersion()
      {
      var modifiedUrl = document.location.href;
      modifiedUrl = removeParamFromURL(modifiedUrl, 'editlockedversionmode');
      modifiedUrl += '&amp;referer=<xsl:value-of select="admin:urlEncode(string($referer))"/>';
      document.location.href = modifiedUrl;
      }
      // ---------------------------------------------------------------------------------------------------------------------------------

      // url:String, param:String
      function removeParamFromURL(url, param)
      {
      var regex = new RegExp( "\\?" + param + "=[^&amp;]*&amp;?", "gi");

      url = url.replace(regex,'?');
      regex = new RegExp( "\\&amp;" + param + "=[^&amp;]*&amp;?", "gi");
      url = url.replace(regex,'&amp;');
      url = url.replace(/(\?|&amp;)$/,'');
      regex = null;

      return url;
      }
      // ---------------------------------------------------------------------------------------------------------------------------------
    </script>

    <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td>
          <xsl:variable name="splitButtonId">
            <xsl:choose>
              <xsl:when test="$topbuttons = true()">
                <xsl:text>saveSplitButtonTop</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>saveSplitButtonBottom</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>

          <!-- Edit-knappen skal kun komme hvis brukeren har create, approve eller administrate på kategorien eller update på selve innholdet.-->
          <xsl:variable name="user-has-accessrights-to-edit-button"
                        select="$show-edit-content-button and (/contents/content/userright/@categorycreate = 'true' or /contents/content/userright/@categorypublish = 'true' or /contents/content/userright/@update = 'true')"/>
          <xsl:variable name="show-edit-button"
                        select="$user-has-accessrights-to-edit-button and not($new) and /contents/content/@state != 0 and $editlockedversionmode = false()"/>

          <!--p id="-cms-debug">
            $new: <xsl:value-of select="$new"/><br/>
            $create: <xsl:value-of select="$create"/><br/>
            @state: <xsl:value-of select="/contents/content/@state"/><br/>
            $editlockedversionmode: <xsl:value-of select="$editlockedversionmode"/><br/>
            $editlockedversionmode = false(): <xsl:value-of select="$editlockedversionmode = false()"/><br/>
            $referer: <xsl:value-of select="$referer"/><br/>
            $show-edit-button: <xsl:value-of select="$show-edit-button"/><br/>
            user-has-accessrights-to-edit-button: <xsl:value-of select="$user-has-accessrights-to-edit-button"/>
          </p-->

          <xsl:choose>
            <xsl:when test="$show-save-button-dropdown">
              <ul id="{$splitButtonId}" title="%cmdSave%" class="cms-split-button">

                <!-- Default action -->
                <li>
                  <xsl:choose>
                    <xsl:when test="$includetakesnapshot">
                      <a href="javascript:submitForm('snapshot');">%txtSnapshot%</a>
                    </xsl:when>
                    <xsl:otherwise>
                      <a href="javascript:submitForm('save');">%txtCurrent%</a>
                    </xsl:otherwise>
                  </xsl:choose>
                </li>

                <xsl:if test="$includeapprove">
                  <li class="cms-menu-item-icon-state-published">
                    <a href="javascript:submitForm('approve');">%txtAndApprove%</a>
                  </li>
                </xsl:if>

                <xsl:if test="$includepublish">
                  <li class="cms-menu-item-icon-state-content-published">
                    <a href="javascript:submitForm('publish');">
                      <xsl:choose>
                        <xsl:when test="$selected-version-is-approved">%txtAndPublish%</xsl:when>
                        <xsl:otherwise>%txtApproveAndPublish%</xsl:otherwise>
                      </xsl:choose>
                    </a>
                  </li>
                </xsl:if>

                <xsl:if test="$includeassignto">
                  <li class="cms-menu-item-icon-assign-to">
                    <a href="javascript:submitForm('assignto');">
                      <xsl:choose>
                        <xsl:when test="$reassign">
                          <xsl:text>%txtAndReAssign%</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:text>%txtAndAssign%</xsl:text>
                        </xsl:otherwise>
                      </xsl:choose>
                    </a>
                  </li>
                </xsl:if>

                <xsl:if test="$includearchive">
                  <li class="cms-menu-item-icon-state-archived">
                    <a href="javascript:submitForm('archive');">%txtAndArchive%</a>
                  </li>
                </xsl:if>

              </ul>
            </xsl:when>
            <xsl:otherwise>
              <xsl:if test="$includesave">
                <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'button'"/>
                  <xsl:with-param name="caption" select="'%cmdSave%'"/>
                  <xsl:with-param name="name" select="'savebtn'"/>
                  <xsl:with-param name="onclick">
                    <xsl:text>javascript:submitForm('save');</xsl:text>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:if>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:text>&nbsp;</xsl:text>

          <xsl:if test="$show-edit-button">

            <xsl:variable name="edit-button-tooltip">
              <xsl:choose>
                <xsl:when test="/contents/content/@status = 1">%tipContentEditSnapshot%</xsl:when>
                <xsl:otherwise>%tipContentEdit%</xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <xsl:call-template name="button">
              <xsl:with-param name="type" select="'button'"/>
              <xsl:with-param name="caption" select="'%cmdEdit%'"/>
              <xsl:with-param name="name" select="'editbtn'"/>
              <xsl:with-param name="tooltip" select="$edit-button-tooltip"/>
              <xsl:with-param name="onclick">
                <xsl:text>javascript:editContentVersion();</xsl:text>
              </xsl:with-param>
            </xsl:call-template>
            <xsl:text>&nbsp;</xsl:text>
          </xsl:if>

          <xsl:if test="$editlockedversionmode">
            <xsl:call-template name="button">
              <xsl:with-param name="type" select="'button'"/>
              <xsl:with-param name="caption" select="'%cmdCancelEdit%'"/>
              <xsl:with-param name="name" select="'cancelbtn'"/>
              <xsl:with-param name="onclick">
                <xsl:text>javascript:cancelEditContentVersion();</xsl:text>
              </xsl:with-param>
            </xsl:call-template>
            <xsl:text>&nbsp;</xsl:text>
          </xsl:if>

          <xsl:if test="$enablepreview">
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

            <xsl:call-template name="button">
              <xsl:with-param name="type" select="'button'"/>
              <xsl:with-param name="caption" select="'%cmdPreview%'"/>
              <xsl:with-param name="id" select="'previewbtn'"/>
              <xsl:with-param name="name" select="'previewbtn'"/>
              <xsl:with-param name="disabled" select="$create = 1 or not($has-page-template)"/>
              <xsl:with-param name="tooltip" select="$tooltip"/>
              <xsl:with-param name="onclick">
                <xsl:if test="$subfunctions != ''">
                  <xsl:value-of select="$subfunctions"/>
                </xsl:if>
                <xsl:text>previewContent(</xsl:text>
                <xsl:value-of select="$page"/>
                <xsl:text>,</xsl:text>
                <xsl:choose>
                  <xsl:when test="/contents/content/@key">
                    <xsl:value-of select="/contents/content/@key"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>-1</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
                <xsl:text>, </xsl:text>
                <xsl:choose>
                  <xsl:when test="/contents/content/@versionkey">
                    <xsl:value-of select="/contents/content/@versionkey"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>-1</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
                <xsl:text>);</xsl:text>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:if>

          <xsl:text>&nbsp;</xsl:text>

          <xsl:call-template name="button">
            <xsl:with-param name="type" select="'cancel'"/>
            <xsl:with-param name="caption" select="'%cmdClose%'"/>
            <xsl:with-param name="name" select="'closebtn'"/>
            <xsl:with-param name="id" select="'closebtn2'"/>
            <xsl:with-param name="referer" select="$referer"/>
          </xsl:call-template>

          <xsl:if test="$show-save-button-dropdown">
            <script type="text/javascript" charset="utf-8">
              var splitButton_<xsl:value-of select="$splitButtonId"/> = new cms.ui.SplitButton('<xsl:value-of select="$splitButtonId"/>');
              splitButton_<xsl:value-of select="$splitButtonId"/>.insert();
            </script>
          </xsl:if>

        </td>
      </tr>
    </table>
  </xsl:template>

</xsl:stylesheet>
