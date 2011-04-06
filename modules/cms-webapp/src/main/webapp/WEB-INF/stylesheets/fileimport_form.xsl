<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
    ]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
  <xsl:output method="html"/>

  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:include href="common/serialize.xsl"/>
  <xsl:include href="common/genericheader.xsl"/>
  <xsl:include href="common/categoryheader.xsl"/>
  <xsl:include href="common/dropdown_boolean.xsl"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="common/filefield.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/readonlyvalue.xsl"/>
  <xsl:include href="common/textfielddatetime.xsl"/>
  <xsl:include href="common/formatdate.xsl"/>
  <xsl:include href="common/checkbox_boolean.xsl"/>
  <xsl:include href="common/displayerror.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>
  <xsl:include href="common/textarea.xsl"/>
  <xsl:include href="common/user-picker-with-autocomplete.xsl"/>

  <xsl:param name="_current_user_key"/>

  <xsl:variable name="category" select="/data/contentcategories/contentcategory"/>

  <xsl:template match="/">
    <html>
      <head>
        <link rel="stylesheet" type="text/css" href="css/admin.css"/>
        <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>
        <link rel="stylesheet" type="text/css" href="css/calendar_picker.css"/>
        <link type="text/css" rel="stylesheet" href="javascript/lib/jquery/ui/autocomplete/css/cms/jquery-ui-1.8.1.custom.css"/>
        <link type="text/css" rel="stylesheet" href="javascript/lib/jquery/ui/autocomplete/css/cms/jquery-ui-1.8.1.overrides.css"/>
        <link type="text/css" rel="stylesheet" href="css/user-picker-with-autocomplete.css"/>

        <script type="text/javascript">
          var g_autoApprovedImports = new Array(<xsl:value-of select="count(/data/config/imports/import)"/>);

          <xsl:for-each select="/data/config/imports/import">
            g_autoApprovedImports[<xsl:value-of select="position() - 1"/>] = <xsl:value-of select="@status = 2"/>;
          </xsl:for-each>

          var g_assignedImports = new Array(<xsl:value-of select="count(/data/config/imports/import)"/>);

          <xsl:for-each select="/data/config/imports/import">
            g_assignedImports[<xsl:value-of select="position() - 1"/>] = <xsl:value-of select="@status = 0"/>;
          </xsl:for-each>

          function isChoosen() {}
        </script>
        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" src="javascript/calendar_picker.js">//</script>
        <script type="text/javascript" src="javascript/tabpane.js">//</script>
        <script type="text/javascript" src="javascript/validate.js">//</script>


        <script type="text/javascript" src="javascript/lib/jquery/jquery-1.4.2.min.js">//</script>
        <script type="text/javascript" src="javascript/lib/jquery/ui/autocomplete/js/jquery-ui-1.8.1.custom.min.js">//</script>
        <script type="text/javascript" src="javascript/user-picker-with-autocomplete.js">//</script>
        <script type="text/javascript" src="javascript/calendar_picker.js">//</script>
        <script type="text/javascript" src="javascript/fileimport_form.js">//</script>
        <script type="text/javascript" src="javascript/import-assignment.js">//</script>
      </head>

      <body onload="setFocus()" class="jquery-ui">
        <div id="wait" style="display: none; float: center; width: 100%; height: 100%; background: #ffffff; text-align: center;">
          <div style="position: relative; top: 40%">
            <img vspace="5" src="images/waitsplash.gif"/>
            <br/>
            <xsl:text>%headPleaseWait%</xsl:text>
          </div>
        </div>
        <div id="form">
          <form name="formAdmin" method="post" enctype="multipart/form-data" onsubmit="javascript: return validateAll();">
            <xsl:attribute name="action">
              <xsl:text>adminpage?page=</xsl:text>
              <xsl:value-of select="$page"/>
              <xsl:text>&amp;op=fileimport</xsl:text>
              <xsl:text>&amp;cat=</xsl:text>
              <xsl:value-of select="$cat"/>
              <xsl:text>&amp;selectedunitkey=</xsl:text>
              <xsl:value-of select="$selectedunitkey"/>
              <xsl:text>&amp;contenttypekey=</xsl:text>
              <xsl:value-of select="number($page) - 999"/>
            </xsl:attribute>
            <h1>
              <xsl:call-template name="genericheader">
                <xsl:with-param name="endslash" select="false()"/>
              </xsl:call-template>
              <xsl:call-template name="categoryheader"/>
            </h1>

            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td>
                  <br/>
                </td>
              </tr>
              <tr>
                <td>
                  <div class="tab-pane" id="tab-pane-1">
                    <script type="text/javascript" language="JavaScript">
                      var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                    </script>
                    <div class="tab-page" id="tab-page-1">
                      <span class="tab">%blockFileImport%</span>

                      <script type="text/javascript" language="JavaScript">
                        tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                      </script>

                      <xsl:call-template name="fileimportform"/>

                    </div>
                  </div>

                  <script type="text/javascript" language="JavaScript">
                    setupAllTabs();
                  </script>
                </td>
              </tr>
              <tr>
                <td>
                  <br/>
                </td>
              </tr>

              <xsl:call-template name="fileimportbuttons"/>

              <script type="text/javascript">
                statusChanged(document.getElementById("importname"));
              </script>

            </table>
          </form>
        </div>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="fileimportform">
    <script type="text/javascript">
      var dateTimeRangeValidator = new DateTimeRangeValidator('_pubdata_publishfrom', '_pubdata_publishto', false, {
          startDatetimeIsLaterThanEndDatetime: '%errOnlineFromIsLaterThanOnlineTo%',
          startLabel: '%fldOnlineFrom%',
          endLabel: '%fldOnlineTo%'
      });
    </script>

    <fieldset>
      <legend>&nbsp;%blockFileImport%&nbsp;</legend>
      <div id="textfielddatetime-error-message" style="display:none;"> </div>

      <table border="0" cellspacing="0" cellpadding="2">
        <tr><td class="form_labelcolumn"> </td></tr>
        <tr>
          <xsl:call-template name="filefield">
            <xsl:with-param name="name" select="'importfile'"/>
            <xsl:with-param name="label" select="'%fldFile%:'"/>
            <xsl:with-param name="required" select="'true'"/>
            <xsl:with-param name="size" select="'60'"/>
            <xsl:with-param name="onchange" select="'setButtonStatus()'"/>
          </xsl:call-template>
        </tr>
        <xsl:if test="not(/data/accessrights/userright) or /data/accessrights/userright/@publish = 'true'">
          <tr>
            <td>%fldImportName%:</td>
            <td>
              <select name="importname" id="importname"  style="width: 150" onchange="statusChanged(this)">
                <xsl:for-each select="data/config/imports/import">
                  <option value="{@name}">
                    <xsl:value-of select="@name"/>
                  </option>
                </xsl:for-each>
              </select>
            </td>
          </tr>
        </xsl:if>
        <xsl:if test="not(/data/accessrights/userright) or /data/accessrights/userright/@publish = 'true'">
          <tr id="publishfrom-row" style="display: none">
            <xsl:call-template name="textfielddatetime">
              <xsl:with-param name="name" select="'_pubdata_publishfrom'"/>
              <xsl:with-param name="label" select="'%fldOnlineFrom%:'"/>
              <xsl:with-param name="selectnode" select="''"/>
              <xsl:with-param name="colspan" select="'1'"/>
              <xsl:with-param name="onbluroverridefunction">
                <xsl:text>dateTimeRangeValidator.validate();</xsl:text>
              </xsl:with-param>
            </xsl:call-template>
          </tr>
          <tr id="publishto-row" style="display: none">
            <xsl:call-template name="textfielddatetime">
              <xsl:with-param name="name" select="'_pubdata_publishto'"/>
              <xsl:with-param name="label" select="'%fldOnlineTo%:'"/>
              <xsl:with-param name="selectnode" select="''"/>
              <xsl:with-param name="colspan" select="'1'"/>
              <xsl:with-param name="onbluroverridefunction">
                <xsl:text>dateTimeRangeValidator.validate();</xsl:text>
              </xsl:with-param>
            </xsl:call-template>
          </tr>
        </xsl:if>
      </table>
    </fieldset>

    <input type="hidden" name="_assigner" value="{$_current_user_key}"/>

    <div id="assignment-fieldset" style="display:none">
      <xsl:call-template name="assignment-fieldset"/>
    </div>

  </xsl:template>

  <xsl:template name="assignment-fieldset">

    <xsl:variable name="autocomplete-help-element">
      <help>%hlpAssignToAutocomplete%</help>
    </xsl:variable>

    <fieldset>
      <legend>%blockAssignment%</legend>
      <table width="100%" cellspacing="0" cellpadding="2" border="0">
        <tr>
          <xsl:call-template name="user-picker-with-autocomplete">
            <xsl:with-param name="name" select="'_assignee'"/>
            <xsl:with-param name="label" select="'%fldAssignee%:'"/>
            <xsl:with-param name="required" select="false()"/>
            <xsl:with-param name="size" select="53"/>
            <xsl:with-param name="help-element" select="$autocomplete-help-element"/>
            <xsl:with-param name="ajax-service-function-to-execute" select="'findUsers'"/>
          </xsl:call-template>
        </tr>
        <tr>
          <xsl:call-template name="textfielddatetime">
            <xsl:with-param name="name" select="'_assignment_duedate'"/>
            <xsl:with-param name="id" select="'_assignment_duedate'"/>
            <xsl:with-param name="label" select="'%fldDueDate%:'"/>
          </xsl:call-template>
        </tr>
        <tr>
          <xsl:call-template name="textarea">
            <xsl:with-param name="name" select="'_assignment_description'"/>
            <xsl:with-param name="id" select="'_assignment_description'"/>
            <xsl:with-param name="label" select="'%fldAssignmentDescr%:'"/>
            <xsl:with-param name="rows" select="'5'"/>
            <xsl:with-param name="cols" select="'50'"/>
            <xsl:with-param name="colspan" select="'1'"/>
          </xsl:call-template>
        </tr>
      </table>
    </fieldset>
  </xsl:template>


  <xsl:template name="fileimportbuttons">
    <tr>
      <td>
        <xsl:call-template name="button">
          <xsl:with-param name="type" select="'submit'"/>
          <xsl:with-param name="caption" select="'%cmdFileImport%'"/>
          <xsl:with-param name="name" select="'importbtn'"/>
          <xsl:with-param name="disabled" select="'true'"/>
        </xsl:call-template>
        <xsl:text>&nbsp;</xsl:text>
        <xsl:call-template name="button">
          <xsl:with-param name="type" select="'button'"/>
          <xsl:with-param name="caption" select="'%cmdCancel%'"/>
          <xsl:with-param name="name" select="'cancelbtn'"/>
          <xsl:with-param name="onclick">
            <xsl:text>javascript: history.back();</xsl:text>
          </xsl:with-param>
        </xsl:call-template>
      </td>
    </tr>
  </xsl:template>

  
</xsl:stylesheet>
