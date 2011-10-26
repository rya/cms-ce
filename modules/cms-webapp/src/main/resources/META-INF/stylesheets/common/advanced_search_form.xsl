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

  <xsl:template name="advanced_search_form">
    <xsl:param name="subop"/>
    <xsl:param name="page"/>
    <xsl:param name="fieldname"/>
    <xsl:param name="fieldrow"/>
    <xsl:param name="cat"/>
    <xsl:param name="asearchtext" select="''"/>
    <xsl:param name="searchtype" select="'advanced'"/>
    <xsl:param name="ascope" select="'title'"/>
    <xsl:param name="includeSubcategories" select="'1'"/>
    <xsl:param name="contenttypestring" select="'-1'"/>
    <xsl:param name="cancelUrl" select="'#'"/>
    <xsl:param name="state"/>
    <xsl:param name="owner"/>
    <xsl:param name="owner.uid"/>
    <xsl:param name="owner.fullName"/>
    <xsl:param name="owner.qualifiedName"/>

    <xsl:param name="modifier"/>
    <xsl:param name="modifier.uid"/>
    <xsl:param name="modifier.fullName"/>
    <xsl:param name="modifier.qualifiedName"/>

    <xsl:param name="assignment.assigneeUserKey"/>
    <xsl:param name="assignment.assigneeDisplayName"/>
    <xsl:param name="assignment.assigneeQualifiedName"/>
    <xsl:param name="assignment.assignerUserKey"/>
    <xsl:param name="assignment.assignerDisplayName"/>
    <xsl:param name="assignment.assignerQualifiedName"/>
    <xsl:param name="duedate"/>
    <xsl:param name="assignment.dueDate"/>
    <xsl:param name="assignment.dueDate.op"/>

    <xsl:param name="created"/>
    <xsl:param name="created.op"/>
    <xsl:param name="modified"/>
    <xsl:param name="modified.op"/>
    <xsl:param name="acontentkey"/>
    <xsl:param name="filter"/>
    <xsl:param name="contenthandler"/>
    <xsl:param name="selectedtabpage"/>
    <xsl:param name="minoccurrence"/>
    <xsl:param name="maxoccurrence"/>

    <xsl:variable name="isPopup" select="$subop = 'relatedcontent' or $subop = 'relatedfiles' or $subop = 'relatedfile' or $subop = 'relatedimages' or $subop = 'relatedimage' or $subop = 'contentfield' or $subop = 'addcontenttosection'"/>

    <xsl:variable name="autocomplete-help-element">
      <help>%hlpAssignToAutocomplete%</help>
    </xsl:variable>

    <script type="text/javascript" language="JavaScript">
      var currview = "";

      function OpenUserSelect(name) {
        currview = name;
        showUserAndGroupsPopup(true, null, false, 'advsearchform');
      }

      function callback_selectednew(key, type, name, userstorename, qualifiedName, photoExists) {
        var n = document.createTextNode(name);
        var el = document.getElementById('view' + currview);

        if (el.firstChild != null) {
          el.removeChild(document.getElementById('view' + currview).firstChild);
        }
        el.appendChild(n);
        document.getElementsByName(currview)[0].value = key;
      }

      function validateQuery(form) {

        document.getElementById('selectedtabpage').value = tabPane.getSelectedPage();

        var fileAttachmentsRadioButton = document.getElementById("ascope_fileAttachments");
        var searchText = form["asearchtext"].value;
        var trimmedSearchText = searchText.replace(/^\s+|\s+$/g, '');

        if(fileAttachmentsRadioButton.checked &amp;&amp; trimmedSearchText.length &lt; 3) {
          alert("%alertSearchTextTooShort_Minium3%");
          return false;
        }

        return true;
      }

    </script>

    <form name="advSearchform" method="get" action="adminpage" onsubmit="return validateQuery(this);">
      <input type="hidden" name="page" value="{$page}"/>
      <input type="hidden" name="op" value="browse"/>
      <input type="hidden" name="subop" value="{$subop}"/>
      <input type="hidden" name="selectedunitkey" value="{$selectedunitkey}"/>
      <input type="hidden" name="cat" value="{$cat}"/>
      <input type="hidden" name="searchtype" value="advanced"/>
      <input type="hidden" name="waitscreen" value="true"/>
      <input type="hidden" name="fieldname" value="{$fieldname}"/>
      <input type="hidden" name="fieldrow" value="{$fieldrow}"/>
      <input type="hidden" name="contenthandler" value="{$contenthandler}"/>
      <input type="hidden" name="minoccurrence" value="{$minoccurrence}"/>
      <input type="hidden" name="maxoccurrence" value="{$maxoccurrence}"/>

      <!-- The value is set on submit -->
      <input type="hidden" name="selectedtabpage" id="selectedtabpage" value="{$selectedtabpage}"/>

      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td>

            <div class="tab-pane" id="tab-pane-1">
              <script type="text/javascript" language="JavaScript">
                var tabPane = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
              </script>

              <div class="tab-page" id="tab-page-general">
                <span class="tab">%blockSearch%</span>

                <script type="text/javascript" language="JavaScript">
                  tabPane.addTabPage( document.getElementById( "tab-page-general" ) );
                </script>

                <fieldset>
                  <legend>
                    <xsl:text>&nbsp;%blockGeneral%&nbsp;</xsl:text>
                  </legend>

                  <table border="0" cellspacing="2" cellpadding="2">
                    <!-- search text -->
                    <tr>
                      <td class="form_labelcolumn">%fldSearchText%:</td>
                      <td>
                        <input type="text" id="asearchtext" name="asearchtext" value="{$asearchtext}"/>
                      </td>
                    </tr>

                    <!-- scope -->
                    <tr>
                      <td class="form_labelcolumn"><xsl:text> </xsl:text> </td>
                      <td>
                        <input type="radio" name="ascope" value="title">
                          <xsl:if test="$ascope = 'title'">
                            <xsl:attribute name="checked">
                              true
                            </xsl:attribute>
                          </xsl:if>
                        </input>
                        <xsl:text>%radioSearchTitle%</xsl:text>

                        <input type="radio" name="ascope" value="all">
                          <xsl:if test="$ascope = 'all'">
                            <xsl:attribute name="checked">
                              true
                            </xsl:attribute>
                          </xsl:if>
                        </input>
                        <xsl:text>%radioSearchFields%&nbsp;</xsl:text>

                        <input type="radio" name="ascope" value="fileAttachments" id="ascope_fileAttachments">
                          <xsl:if test="$ascope = 'fileAttachments'">
                            <xsl:attribute name="checked">
                              true
                            </xsl:attribute>
                          </xsl:if>
                        </input>
                        <xsl:text>%radioSearchFileAttachments%</xsl:text>
                      </td>
                    </tr>

                    <!-- subcategories -->
                    <!-- We do not want to display this field when cat is not set (eg. content repositories) -->
                    <xsl:choose>
                      <xsl:when test="$cat and $cat != '' and $cat != -1">
                        <tr>
                          <td class="form_labelcolumn">%fldSearchSubcategories%:</td>
                          <td>
                            <select name="subcategories">
                              <option value="1">
                                <xsl:if test="$includeSubcategories = 1">
                                  <xsl:attribute name="selected">
                                    true
                                  </xsl:attribute>
                                </xsl:if>
                                %optYes%
                              </option>
                              <option value="0">
                                <xsl:if test="$includeSubcategories != 1">
                                  <xsl:attribute name="selected">
                                    true
                                  </xsl:attribute>
                                </xsl:if>
                                %optNo%
                              </option>
                            </select>
                          </td>
                        </tr>
                      </xsl:when>
                      <xsl:otherwise>
                        <!-- Display this field when cat is not set (eg. content repositories) See field decleared above. -->

                        <input type="hidden" name="subcategories" value="0"/>
                      </xsl:otherwise>
                    </xsl:choose>


                    <!-- content types -->
                    <tr>
                      <td valign="top" class="form_labelcolumn">%fldContentTypes%:</td>
                      <td>

                        <select name="contenttypestring" size="8" multiple="multiple">
                          <xsl:variable name="anyselected" select="contains($contenttypestring, '-1')"/>
                          <xsl:choose>
                            <xsl:when test="$isPopup">
                              <xsl:if test="$contenttypestring = ''">
                                <option value="-1">
                                  <xsl:if test="$anyselected">
                                    <xsl:attribute name="selected">
                                      true
                                    </xsl:attribute>
                                  </xsl:if>
                                  %lblAny%
                                </option>
                              </xsl:if>
                            </xsl:when>
                            <xsl:otherwise>
                              <option value="-1">
                                <xsl:if test="$anyselected">
                                  <xsl:attribute name="selected">
                                    true
                                  </xsl:attribute>
                                </xsl:if>
                                %lblAny%
                              </option>
                            </xsl:otherwise>
                          </xsl:choose>

                          <xsl:for-each select="/data/sitecontenttypes/contenttype">
                            <xsl:sort select="name" order="ascending"/>
                            <xsl:variable name="count-key-in-contenttype-string">
                              <xsl:call-template name="count-key-in-contenttype-string">
                                <xsl:with-param name="key" select="@key"/>
                                <xsl:with-param name="ctyStr" select="concat($contenttypestring, ',')"/>
                              </xsl:call-template>
                            </xsl:variable>

                            <xsl:choose>
                              <xsl:when test="$isPopup">
                                <xsl:if test="$count-key-in-contenttype-string &gt; -1 or $contenttypestring = ''">
                                  <option value="{@key}">
                                    <xsl:if test="$count-key-in-contenttype-string &gt; -1">
                                      <xsl:attribute name="selected">
                                        true
                                      </xsl:attribute>
                                    </xsl:if>
                                    <xsl:value-of select="name"/>
                                  </option>
                                </xsl:if>
                              </xsl:when>
                              <xsl:otherwise>
                                <option value="{@key}">
                                  <xsl:if test="$count-key-in-contenttype-string &gt; -1">
                                    <xsl:attribute name="selected">
                                      true
                                    </xsl:attribute>
                                  </xsl:if>
                                  <xsl:value-of select="name"/>
                                </option>
                              </xsl:otherwise>
                            </xsl:choose>

                          </xsl:for-each>
                        </select>
                      </td>
                    </tr>
                  </table>
                </fieldset>

                <fieldset>
                  <legend>
                    <xsl:text>&nbsp;%blockMetadata%&nbsp;</xsl:text>
                  </legend>

                  <table border="0" cellspacing="2" cellpadding="2">
                    <!-- status -->
                    <tr>
                      <td class="form_labelcolumn">%fldState%:</td>
                      <td colspan="2">
                        <select name="state" class="publish-status">
                          <option value="">
                            <xsl:if test="$state = ''">
                              <xsl:attribute name="selected">true</xsl:attribute>
                            </xsl:if>
                            %sysDropDownNone%
                          </option>
                          <option value="0">
                            <xsl:if test="$state = 0">
                              <xsl:attribute name="selected">true</xsl:attribute>
                            </xsl:if>
                            %optDraft%
                          </option>
                          <option value="2">
                            <xsl:if test="$state = 2">
                              <xsl:attribute name="selected">true</xsl:attribute>
                            </xsl:if>
                            %optApproved%
                          </option>
                          <option value="3">
                            <xsl:if test="$state = 3">
                              <xsl:attribute name="selected">true</xsl:attribute>
                            </xsl:if>
                            %optArchived%
                          </option>
                          <option value="4">
                            <xsl:if test="$state = 4">
                              <xsl:attribute name="selected">true</xsl:attribute>
                            </xsl:if>
                            %optAwaitingPublishing%
                          </option>
                          <option value="5">
                            <xsl:if test="$state = 5">
                              <xsl:attribute name="selected">true</xsl:attribute>
                            </xsl:if>
                            %optOnline%
                          </option>
                          <option value="6">
                            <xsl:if test="$state = 6">
                              <xsl:attribute name="selected">true</xsl:attribute>
                            </xsl:if>
                            %optExpired%
                          </option>
                        </select>
                      </td>
                    </tr>

                    <!-- owner -->
                    <tr>
                      <xsl:variable name="buttonfunction">
                        <xsl:text>javascript:OpenUserSelect('owner')</xsl:text>
                      </xsl:variable>

                      <xsl:call-template name="user-picker-with-autocomplete">
                        <xsl:with-param name="name" select="'owner'"/>
                        <xsl:with-param name="label" select="'%fldOwner%:'"/>
                        <xsl:with-param name="selected-user-key" select="$owner"/>
                        <xsl:with-param name="selected-user-display-name" select="$owner.fullName"/>
                        <xsl:with-param name="selected-user-qualified-name" select="$owner.qualifiedName"/>
                        <xsl:with-param name="use-user-group-key" select="true()"/>
                        <xsl:with-param name="required" select="false()"/>
                        <xsl:with-param name="help-element" select="$autocomplete-help-element"/>
                        <xsl:with-param name="size" select="'26'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                      </xsl:call-template>
                      
                    </tr>

                    <input name="owner.qualifiedName" type="hidden"  value="{$owner.qualifiedName}"/>

                    <!-- created time -->
                    <tr>
                      <td class="form_labelcolumn">
                        <xsl:text>%fldCreated%:</xsl:text>
                      </td>
                      <td style="width:1px">
                        <select name="created.op">
                          <option value="eq">
                            <xsl:if test="$created.op = 'eq'">
                              <xsl:attribute name="selected">true</xsl:attribute>
                            </xsl:if>
                            %txtEquals%
                          </option>
                          <option value="gte">
                            <xsl:if test="$created.op = 'gte'">
                              <xsl:attribute name="selected">true</xsl:attribute>
                            </xsl:if>
                            %txtGreaterThanOrEqualTo%
                          </option>
                          <option value="lte">
                            <xsl:if test="$created.op = 'lte'">
                              <xsl:attribute name="selected">true</xsl:attribute>
                            </xsl:if>
                            %txtLessThanOrEqualTo%
                          </option>
                        </select>
                      </td>
                      <xsl:call-template name="textfielddate">
                        <xsl:with-param name="name" select="'created'"/>
                        <xsl:with-param name="label" select="''"/>
                        <xsl:with-param name="selectnode" select="$created"/>
                        <xsl:with-param name="colspan" select="'1'"/>
                        <xsl:with-param name="formatdate" select="false()"/>
                      </xsl:call-template>
                    </tr>

                    <!-- modifier -->
                    <tr>
                      <xsl:variable name="modbuttonfunction">
                        <xsl:text>javascript:OpenUserSelect('modifier')</xsl:text>
                      </xsl:variable>

                      <xsl:call-template name="user-picker-with-autocomplete">
                        <xsl:with-param name="name" select="'modifier'"/>
                        <xsl:with-param name="label" select="'%fldModifier%:'"/>
                        <xsl:with-param name="selected-user-key" select="$modifier"/>
                        <xsl:with-param name="selected-user-display-name" select="$modifier.fullName"/>
                        <xsl:with-param name="selected-user-qualified-name" select="$modifier.qualifiedName"/>
                        <xsl:with-param name="use-user-group-key" select="true()"/>
                        <xsl:with-param name="required" select="false()"/>
                        <xsl:with-param name="help-element" select="$autocomplete-help-element"/>
                        <xsl:with-param name="size" select="'26'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                      </xsl:call-template>
                    </tr>

                    <input name="modifier.qualifiedName" type="hidden"  value="{$modifier.qualifiedName}"/>

                    <!-- modified time -->
                    <tr>
                      <td class="form_labelcolumn">
                        <xsl:text>%fldModified%:</xsl:text>
                      </td>
                      <td style="width: 1px">
                        <select name="modified.op">
                          <option value="eq">
                            <xsl:if test="$modified.op = 'eq'">
                              <xsl:attribute name="selected">true</xsl:attribute>
                            </xsl:if>
                            %txtEquals%
                          </option>
                          <option value="gte">
                            <xsl:if test="$modified.op = 'gte'">
                              <xsl:attribute name="selected">true</xsl:attribute>
                            </xsl:if>
                            %txtGreaterThanOrEqualTo%
                          </option>
                          <option value="lte">
                            <xsl:if test="$modified.op = 'lte'">
                              <xsl:attribute name="selected">true</xsl:attribute>
                            </xsl:if>
                            %txtLessThanOrEqualTo%
                          </option>
                        </select>
                      </td>
                      <xsl:call-template name="textfielddate">
                        <xsl:with-param name="name" select="'modified'"/>
                        <xsl:with-param name="label" select="''"/>
                        <xsl:with-param name="selectnode" select="$modified"/>
                        <xsl:with-param name="colspan" select="'1'"/>
                        <xsl:with-param name="formatdate" select="false()"/>
                      </xsl:call-template>
                    </tr>
                  </table>
                </fieldset>

                <fieldset>
                  <legend>&nbsp;%blockAssignment%&nbsp;</legend>
                  <table width="100%" border="0" cellspacing="2" cellpadding="2">
                    <tr>
                      <xsl:call-template name="user-picker-with-autocomplete">
                        <xsl:with-param name="name" select="'_assignee'"/>
                        <xsl:with-param name="label" select="'%fldAssignee%:'"/>
                        <xsl:with-param name="selected-user-key" select="$assignment.assigneeUserKey"/>
                        <xsl:with-param name="selected-user-display-name" select="$assignment.assigneeDisplayName"/>
                        <xsl:with-param name="selected-user-qualified-name" select="$assignment.assigneeQualifiedName"/>
                        <xsl:with-param name="use-user-group-key" select="false()"/>
                        <xsl:with-param name="help-element" select="$autocomplete-help-element"/>
                        <xsl:with-param name="size" select="'26'"/>
                        <xsl:with-param name="required" select="false()"/>
                      </xsl:call-template>
                    </tr>
                    <tr>
                      <xsl:call-template name="user-picker-with-autocomplete">
                        <xsl:with-param name="name" select="'_assigner'"/>
                        <xsl:with-param name="label" select="'%fldAssigner%:'"/>
                        <xsl:with-param name="selected-user-key" select="$assignment.assignerUserKey"/>
                        <xsl:with-param name="selected-user-display-name" select="$assignment.assignerDisplayName"/>
                        <xsl:with-param name="selected-user-qualified-name" select="$assignment.assignerQualifiedName"/>
                        <xsl:with-param name="use-user-group-key" select="false()"/>
                        <xsl:with-param name="help-element" select="$autocomplete-help-element"/>
                        <xsl:with-param name="size" select="'26'"/>
                        <xsl:with-param name="required" select="false()"/>
                      </xsl:call-template>
                    </tr>
                    <tr>
                      <td class="form_labelcolumn">
                        <xsl:text>%fldDue%:</xsl:text>
                      </td>
                      <td>
                        <table border="0" cellpadding="0" cellspacing="0">
                          <tr>
                            <td style="padding-right: 5px">
                              <select name="_assignmentDueDate.op">
                                <option value="eq">
                                  <xsl:if test="$assignment.dueDate.op = 'eq'">
                                    <xsl:attribute name="selected">true</xsl:attribute>
                                  </xsl:if>
                                  %txtEquals%
                                </option>
                                <option value="gte">
                                  <xsl:if test="$assignment.dueDate.op = 'gte'">
                                    <xsl:attribute name="selected">true</xsl:attribute>
                                  </xsl:if>
                                  %txtGreaterThanOrEqualTo%
                                </option>
                                <option value="lte">
                                  <xsl:if test="$assignment.dueDate.op = 'lte'">
                                    <xsl:attribute name="selected">true</xsl:attribute>
                                  </xsl:if>
                                  %txtLessThanOrEqualTo%
                                </option>
                              </select>
                            </td>
                            <xsl:call-template name="textfielddate">
                              <xsl:with-param name="name" select="'_assignmentDueDate'"/>
                              <xsl:with-param name="label" select="''"/>
                              <xsl:with-param name="selectnode" select="$assignment.dueDate"/>
                              <xsl:with-param name="colspan" select="'1'"/>
                              <xsl:with-param name="formatdate" select="true()"/>
                            </xsl:call-template>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </fieldset>

                <fieldset>
                  <legend>
                    <xsl:text>&nbsp;%blockByKey%&nbsp;</xsl:text>
                  </legend>

                  <table border="0" cellspacing="2" cellpadding="2">
                    <tr>
                      <td class="form_labelcolumn">%fldKey%:</td>
                      <td>
                        <input size="8" type="text" name="acontentkey" value="{$acontentkey}"/>
                      </td>
                    </tr>
                  </table>
                </fieldset>

                <fieldset>
                  <legend>
                    <xsl:text>&nbsp;%blockFilter%&nbsp;</xsl:text>
                  </legend>

                  <table border="0" cellspacing="2" cellpadding="2">
                    <tr>
                      <xsl:call-template name="textarea">
                        <xsl:with-param name="name" select="'filter'"/>
                        <xsl:with-param name="label" select="'%fldFilter%:'"/>
                        <xsl:with-param name="selectnode" select="$filter"/>
                        <xsl:with-param name="rows" select="'4'"/>
                        <xsl:with-param name="cols" select="'60'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
                      </xsl:call-template>
                    </tr>
                  </table>
                </fieldset>
              </div>
            </div>
          </td>
        </tr>
        <tr>
          <td class="form_form_buttonrow_seperator">
            <img src="images/1x1.gif"/>
          </td>
        </tr>
      </table>

      <script type="text/javascript" language="JavaScript">
        setupAllTabs();
      </script>

      <xsl:call-template name="button">
        <xsl:with-param name="type" select="'submit'"/>
        <xsl:with-param name="caption" select="'%cmdSearch%'"/>
      </xsl:call-template>
      <xsl:text>&nbsp;</xsl:text>
      <xsl:call-template name="button">
        <xsl:with-param name="type" select="'cancel'"/>
        <xsl:with-param name="caption" select="'%cmdCancel%'"/>
        <xsl:with-param name="referer" select="$cancelUrl"/>
      </xsl:call-template>
      <xsl:text>&nbsp;</xsl:text>

      <xsl:variable name="category">
        <xsl:choose>
          <xsl:when test="$cat != ''">
            <xsl:value-of select="$cat"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>-1</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <!-- Create report button -->
      <xsl:call-template name="button">
        <xsl:with-param name="type" select="'link'"/>
        <xsl:with-param name="caption" select="'%cmdCreateReport%'"/>
        <xsl:with-param name="name" select="'report'"/>
        <xsl:with-param name="href">
          <xsl:text>adminpage?page=</xsl:text>
          <xsl:value-of select="$page"/>
          <xsl:text>&amp;op=report&amp;subop=form</xsl:text>
          <xsl:text>&amp;selectedunitkey=</xsl:text>
          <xsl:value-of select="$selectedunitkey"/>
          <xsl:text>&amp;cat=</xsl:text>
          <xsl:value-of select="$category"/>
          <xsl:text>&amp;searchtype=</xsl:text>
          <xsl:value-of select="$searchtype"/>
          <xsl:text>&amp;asearchtext=</xsl:text>
          <xsl:value-of select="$asearchtext"/>
          <xsl:text>&amp;ascope=</xsl:text>
          <xsl:value-of select="$ascope"/>
          <xsl:text>&amp;subcategories=</xsl:text>
          <xsl:value-of select="$includeSubcategories"/>
          <xsl:text>&amp;state=</xsl:text>
          <xsl:value-of select="$state"/>
          <xsl:text>&amp;owner=</xsl:text>
          <xsl:value-of select="$owner"/>
          <xsl:text>&amp;created.op=</xsl:text>
          <xsl:value-of select="$created.op"/>
          <xsl:text>&amp;created=</xsl:text>
          <xsl:value-of select="$created"/>
          <xsl:text>&amp;modifier=</xsl:text>
          <xsl:value-of select="$modifier"/>
          <xsl:text>&amp;modified.op=</xsl:text>
          <xsl:value-of select="$modified.op"/>
          <xsl:text>&amp;modified=</xsl:text>
          <xsl:value-of select="$modified"/>
          <xsl:text>&amp;acontentkey=</xsl:text>
          <xsl:value-of select="$acontentkey"/>
          <xsl:text>&amp;filter=</xsl:text>
          <xsl:value-of select="$filter"/>
          <xsl:text>&amp;contenttypestring=</xsl:text>
          <xsl:value-of select="$contenttypestring"/>
          <xsl:text>&amp;_assignee=</xsl:text>
          <xsl:value-of select="$assignment.assigneeUserKey"/>
          <xsl:text>&amp;_assigner=</xsl:text>
          <xsl:value-of select="$assignment.assignerUserKey"/>
          <xsl:text>&amp;duedate=</xsl:text>
          <xsl:value-of select="$duedate"/>
          <xsl:text>&amp;date_assignmentDueDate=</xsl:text>
          <xsl:value-of select="$duedate"/>
          <xsl:text>&amp;_assignmentDueDate.op=</xsl:text>
          <xsl:value-of select="$assignment.dueDate.op"/>
        </xsl:with-param>
      </xsl:call-template>
    </form>

    <xsl:if test="$selectedtabpage != ''">
      <script type="text/javascript">
        <xsl:text>tabPane.setSelectedPage('</xsl:text>
        <xsl:value-of select="$selectedtabpage"/>
        <xsl:text>');</xsl:text>
      </script>
    </xsl:if>

  </xsl:template>

  <xsl:template name="count-key-in-contenttype-string">
    <xsl:param name="key"/>
    <xsl:param name="ctyStr"/>
    <xsl:param name="count" select="-1"/>
    <xsl:variable name="current" select="substring-before($ctyStr, ',')"/>
    <xsl:variable name="restStr" select="substring-after($ctyStr, ',')"/>
    <xsl:variable name="tempCount">
      <xsl:choose>
        <xsl:when test="$current = $key">
          <xsl:value-of select="$count + 1"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$count"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="contains($restStr, ',')">
        <xsl:call-template name="count-key-in-contenttype-string">
          <xsl:with-param name="key" select="$key"/>
          <xsl:with-param name="ctyStr" select="$restStr"/>
          <xsl:with-param name="count" select="$tempCount"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$tempCount"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
