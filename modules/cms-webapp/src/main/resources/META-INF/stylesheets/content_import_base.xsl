<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp   "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:x="mailto:vro@enonic.com?subject=foobar"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <!--xsl:include href="common/generic_parameters.xsl"/-->
    <xsl:include href="common/serialize.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/textfield.xsl"/>
    <xsl:include href="common/textarea.xsl"/>
    <xsl:include href="common/textfielddatetime.xsl"/>
    <xsl:include href="common/filefield.xsl"/>
    <xsl:include href="common/dropdown_boolean.xsl"/>
    <xsl:include href="common/tablecolumnheader.xsl"/>
    <xsl:include href="common/tablerowpainter.xsl"/>
    <xsl:include href="common/publishstatus.xsl"/>
    <xsl:include href="common/formatdate.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>
    <xsl:include href="common/displayerror.xsl"/>
    <xsl:include href="common/chooseicon.xsl"/>
    <xsl:include href="common/getsuffix.xsl"/>

    <xsl:include href="common/user-picker-with-autocomplete.xsl"/>

    <!-- parameter for all steps -->
    <!-- none -->

    <!-- parameter for all step1 and step2 -->
    <xsl:param name="categoryname"/>

    <xsl:template name="step0">

      <div class="tab-pane" id="tab-pane-1">
        <script type="text/javascript" language="JavaScript">
          var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
        </script>
        
        <div class="tab-page" id="tab-page-step">
          <span class="tab">%headStep% 1 %of% 2: %headChooseZipFile%</span>
          
          <script type="text/javascript" language="JavaScript">
            tabPane1.addTabPage( document.getElementById( "tab-page-step" ) );
          </script>
          
          <fieldset>
            <legend>&nbsp;%blockDescription%&nbsp;</legend>
            <img src="images/shim.gif" height="4" class="shim" border="0"/>
            <br/>
            <xsl:text>%txtDescChooseZipFile%</xsl:text>
          </fieldset>
          
          <fieldset>
            <legend>&nbsp;%blockZipFile%&nbsp;</legend>
            <table border="0" cellspacing="0" cellpadding="0" class="formtable">
              <tr>
                <xsl:call-template name="filefield">
                  <xsl:with-param name="label" select="'%fldChooseFile%:'"/>
                  <xsl:with-param name="name" select="'zipfile'"/>
                  <xsl:with-param name="size" select="'60'"/>
                  <xsl:with-param name="maxlength" select="'256'"/>
                  <xsl:with-param name="imagekey" select="'0'"/>
                  <xsl:with-param name="required" select="'true'"/>
                </xsl:call-template>
              </tr>
            </table>
          </fieldset>
        </div>
      </div>
        
      <script type="text/javascript" language="JavaScript">
        setupAllTabs();
      </script>
    </xsl:template>
    
    <xsl:template name="step1">

      <div class="tab-pane" id="tab-pane-1">
        <script type="text/javascript" language="JavaScript">
          var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
        </script>
        
        <div class="tab-page" id="tab-page-step">
          <span class="tab">%headStep% 2 %of% 2: %headSelectFoldersAndFiles%</span>
          
          <script type="text/javascript" language="JavaScript">
            tabPane1.addTabPage( document.getElementById( "tab-page-step" ) );
            
            var branchOpen = new Array;

            document.getElementsByTagName('body')[0].className = "jquery-ui";
            function isChoosen() {}
          </script>
          
          <fieldset>
            <legend>&nbsp;%blockDescription%&nbsp;</legend>
            <img src="images/shim.gif" height="4" class="shim" border="0"/>
            <br/>
            <xsl:call-template name="step1_description"/>
          </fieldset>
          <link rel="stylesheet" type="text/css" href="css/calendar_picker.css"/>
		      <script type="text/javascript" src="javascript/calendar_picker.js">//</script>
		  
          <fieldset>
            <legend>&nbsp;%blockPublishing%&nbsp;</legend>

            <script type="text/javascript">
              var dateTimeRangeValidator = new DateTimeRangeValidator('_publishfrom', '_publishto', false, {
                startDatetimeIsLaterThanEndDatetime: '%errOnlineFromIsLaterThanOnlineTo%',
                startLabel: '%fldOnlineFrom%',
                endLabel: '%fldOnlineTo%'
              });
            </script>

            <div id="textfielddatetime-error-message" style="display:none;"><xsl:comment>//</xsl:comment></div>

            <table border="0" cellspacing="0" cellpadding="2">
              <tr>
                <xsl:call-template name="labelcolumn">
                  <xsl:with-param name="label" select="'%fldStatus%: '"/>
                  <xsl:with-param name="required" select="false()"/>
                  <xsl:with-param name="fieldname" select="'stepstate_zip_'"/>
                </xsl:call-template>

                <td>
                  <select name="stepstate_zip_@publish" id="stepstate_zip_@publish" onchange="statusChanged(this)">
                    <option value="2">%optApproved%</option>
                    <option value="0">%optDraft%</option>
                  </select>
                </td>
              </tr>
              <tr id="_publishfrom-row" style="display: none">
                <xsl:call-template name="textfielddatetime">
                  <xsl:with-param name="name" select="'_publishfrom'"/>
                  <xsl:with-param name="id" select="'_publishfrom'"/>
                  <xsl:with-param name="label" select="'%fldOnlineFrom%:'"/>
                  <xsl:with-param name="selectnode" select="''"/>
                  <xsl:with-param name="colspan" select="'1'"/>
                  <xsl:with-param name="onbluroverridefunction">
                    <xsl:text>dateTimeRangeValidator.validate();</xsl:text>
                  </xsl:with-param>
                </xsl:call-template>
              </tr>
              <tr id="_publishto-row" style="display: none">
                <xsl:call-template name="textfielddatetime">
                  <xsl:with-param name="name" select="'_publishto'"/>
                  <xsl:with-param name="id" select="'_publishto'"/>
                  <xsl:with-param name="label" select="'%fldOnlineTo%:'"/>
                  <xsl:with-param name="selectnode" select="''"/>
                  <xsl:with-param name="colspan" select="'1'"/>
                  <xsl:with-param name="onbluroverridefunction">
                    <xsl:text>dateTimeRangeValidator.validate();</xsl:text>
                  </xsl:with-param>
                </xsl:call-template>
              </tr>
            </table>
          </fieldset>

          <input type="hidden" name="_assigner" value="{$currentuser_key}"/>

          <div id="assignment-fieldset" style="display:none">
            <xsl:call-template name="assignment-fieldset"/>
          </div>
          
          <fieldset>
            <legend>&nbsp;%blockFiles%&nbsp;</legend>
            <img src="images/shim.gif" height="4" class="shim" border="0"/>
            <br/>
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
            
            <xsl:if test="exslt-common:node-set($errors)/errors/error[@name = 'files']">
              <xsl:call-template name="displayerror">
                <xsl:with-param name="code" select="exslt-common:node-set($errors)/errors/error[@name = 'files']/@code"/>
              </xsl:call-template>
            </xsl:if>
            <table cellspacing="0" cellpadding="0" class="menuItem">
              <tr style="height: 16px;" valign="middle">
                <td width="16">
                  <input id="entry_0" name="stepstate_zip_@all" value="true" type="checkbox" onclick="checkBoxClicked(this)">
                    <xsl:if test="/wizarddata/wizardstate/stepstate/zip/@allchecked = 'true'">
                      <xsl:attribute name="checked">
                        <xsl:text>checked</xsl:text>
                      </xsl:attribute>
                    </xsl:if>
                    <xsl:if test="not(/wizarddata/wizardstate/stepstate/zip/@admin = 'true')">
                      <xsl:attribute name="disabled">
                        <xsl:text>disabled</xsl:text>
                      </xsl:attribute>
                    </xsl:if>
                  </input>
                </td>
                
                <td>
                  <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                  <img src="images/icon_imagefolder.gif" border="0"/>
                  <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                  <xsl:value-of select="$categoryname"/>
                </td>
              </tr>
              
              <tr>
                <td width="16">
                  <img border="0" src="images/shim.gif"/>
                </td>
                <td>
                  <table cellspacing="0" cellpadding="0" class="menuItem">
                    <xsl:call-template name="entrytree"/>
                  </table>
                </td>
              </tr>
            </table>
          </fieldset>
        </div>
      </div>
      
      <script type="text/javascript" language="JavaScript">
        setupAllTabs();
        var publishDropdown = document.getElementById("stepstate_zip_@publish");
        publishDropdown.selectedIndex = 0;
        statusChanged(publishDropdown);
      </script>
    </xsl:template>

  <xsl:template name="entrytree">
    <xsl:param name="root" select="/wizarddata/wizardstate/stepstate[@id = number(/wizarddata/wizardstate/@currentstepstateid) - 1]/zip"/>
    <xsl:param name="menuprefix" select="'_'"/>
    <xsl:param name="inputname" select="'stepstate_zip_entry'"/>
    <xsl:param name="checkbox" select="true()"/>
    <xsl:param name="positionprefix" select="'_0'"/>
    <xsl:param name="contenttype" select="$importtype"/>

    <xsl:for-each select="$root/entry">

      <xsl:variable name="entryname">
        <xsl:text>-entry</xsl:text>
        <xsl:value-of select="$menuprefix"/>
        <xsl:value-of select="position()"/>
      </xsl:variable>

      <tr style="height: 16px;" valign="middle">
        <td width="16">
          <a>
            <xsl:attribute name="href">
              <xsl:text>javaScript:openBranch('</xsl:text>
              <xsl:value-of select="$entryname"/>
              <xsl:text>');</xsl:text>
            </xsl:attribute>

            <img border="0">
              <xsl:attribute name="id">
                <xsl:text>img</xsl:text>
                <xsl:value-of select="$entryname"/>
              </xsl:attribute>

              <xsl:attribute name="src">
                <xsl:text>javascript/images/</xsl:text>
                <xsl:choose>
                  <xsl:when test="position() = last()">
                    <xsl:text>L</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>T</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="@type = 'dir'">
                  <xsl:text>minus</xsl:text>
                </xsl:if>
                <xsl:text>.png</xsl:text>
              </xsl:attribute>
            </img>
          </a>
        </td>

        <xsl:variable name="disabled_filtered" select="@filtered  = 'true'"/>
        <xsl:variable name="disabled_rights" select="not(@type = 'dir' and @admin = 'true') and not(@update = 'true')"/>
        <xsl:variable name="disabled_type" select="(@type = 'dir' and not(@contenttype = $importtype)) or (not($contenttype = $importtype) and @type = 'file')"/>
        <xsl:variable name="disabled" select="$disabled_rights or $disabled_type or $disabled_filtered"/>

        <xsl:if test="$checkbox">
          <td width="16">
            <xsl:variable name="currentstepstate">
              <xsl:copy-of select="/wizarddata/wizardstate/stepstate[@id = /wizarddata/wizardstate/@currentstepstateid]"/>
            </xsl:variable>

            <xsl:variable name="checkboxid">
              <xsl:text>entry</xsl:text>
              <xsl:value-of select="$positionprefix"/>
              <xsl:text>_</xsl:text>
              <xsl:value-of select="position()"/>
            </xsl:variable>
            
            <input type="checkbox" name="{$checkboxid}" id="{$checkboxid}" value="{@name}">
              <xsl:if test="( @type = 'file' and not( @exists = 'true' ) ) or @allchecked = 'true'">
                <xsl:attribute name="checked">
                  <xsl:text>checked</xsl:text>
                </xsl:attribute>
              </xsl:if>
              <xsl:if test="$disabled">
                <xsl:attribute name="disabled">
                  <xsl:text>disabled</xsl:text>
                </xsl:attribute>
              </xsl:if>
              <xsl:attribute name="onclick">
                <xsl:text>checkBoxClicked(this)</xsl:text>
              </xsl:attribute>
            </input>
          </td>
        </xsl:if>

        <td>
          <xsl:choose>
            <xsl:when test="@type = 'dir'">
              <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
              <img border="0" width="16" height="16" src="images/icon_{@contenttype}folder.gif">
                <xsl:if test="$disabled">
                  <xsl:attribute name="title">
                    <xsl:choose>
                      <xsl:when test="$disabled_rights and $disabled_type">
                        <xsl:text>%tooltipCategoryDisabled%</xsl:text>
                      </xsl:when>
                      <xsl:when test="$disabled_rights">
                        <xsl:text>%tooltipCategoryDisabledRights%</xsl:text>
                      </xsl:when>
                      <xsl:when test="$disabled_type">
                        <xsl:text>%tooltipCategoryDisabledType%</xsl:text>
                      </xsl:when>
                    </xsl:choose>
                  </xsl:attribute>
                </xsl:if>
              </img>
              <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name="chooseicon">
                <xsl:with-param name="filename" select="@name"/>
                <xsl:with-param name="shaded" select="$disabled"/>
                <xsl:with-param name="title">
                <xsl:choose>
                  <xsl:when test="$disabled_filtered">
                    <xsl:text>%tooltipFileDisabledFiltered%</xsl:text>
                  </xsl:when>
                  <xsl:when test="$disabled_rights and $disabled_type">
                    <xsl:text>%tooltipFileDisabled%</xsl:text>
                  </xsl:when>
                  <xsl:when test="$disabled_rights">
                    <xsl:text>%tooltipFileDisabledRights%</xsl:text>
                  </xsl:when>
                  <xsl:when test="$disabled_type">
                    <xsl:text>%tooltipFileDisabledType%</xsl:text>
                  </xsl:when>
                </xsl:choose>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:otherwise>
          </xsl:choose>

          <xsl:value-of select="@name"/>

          <xsl:if test="not(@exists = 'true')">
            <span class="requiredfield"> *</span>
          </xsl:if>
        </td>
      </tr>
      
      <xsl:if test="@type = 'dir'">
        <tr valign="top">
          <xsl:attribute name="id">
            <xsl:text>id</xsl:text>
            <xsl:value-of select="$entryname"/>
          </xsl:attribute>
          
          <td width="16">
            <xsl:if test="position() != last()">
              <xsl:attribute name="background">javascript/images/I.png</xsl:attribute>
            </xsl:if>
            <img border="0" src="images/shim.gif"/>
          </td>
          
          <td width="16">
            <img border="0" src="images/shim.gif"/>
          </td>
          
          <td colspan="2">
            <table cellspacing="0" cellpadding="0" class="menuItem">
              <xsl:call-template name="entrytree">
                <xsl:with-param name="root" select="."/>
                <xsl:with-param name="menuprefix">
                  <xsl:value-of select="$menuprefix"/>
                  <xsl:value-of select="position()"/>
                  <xsl:text>_</xsl:text>
                </xsl:with-param>
                <xsl:with-param name="inputname">
                  <xsl:value-of select="$inputname"/>
                  <xsl:text>_entry</xsl:text>
                </xsl:with-param>
                <xsl:with-param name="positionprefix">
                  <xsl:value-of select="$positionprefix"/>
                  <xsl:text>_</xsl:text>
                  <xsl:value-of select="position()"/>
                </xsl:with-param>
                <xsl:with-param name="contenttype" select="@contenttype"/>
              </xsl:call-template>
            </table>
          </td>
        </tr>
      </xsl:if>
    </xsl:for-each>
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
</xsl:stylesheet>
