<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html"/>

    <xsl:include href="common/genericheader.xsl"/>
    <xsl:include href="common/serialize.xsl"/>
    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/checkbox_boolean.xsl"/>
    <xsl:include href="common/textfield.xsl"/>
    <xsl:include href="common/textarea.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/searchfield.xsl"/>
    <xsl:include href="common/resourcefield.xsl"/>
    <xsl:include href="common/waitsplash.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>
    <xsl:include href="common/displayerror.xsl"/>
    <xsl:include href="common/dropdown_runas.xsl"/>
    <xsl:include href="common/codearea.xsl"/>
    <xsl:include href="editor/xhtmleditor.xsl"/>

    <xsl:param name="create"/>
    <xsl:param name="queryparam"/>
    <xsl:param name="defaultcsskey"/>
    <xsl:param name="rememberselectedtab"/>
    <xsl:param name="subop"/>
    <xsl:param name="fieldname"/>
    <xsl:param name="fieldrow"/>
    <xsl:param name="expertcontributor"/>
    <xsl:param name="developer"/>

    <xsl:param name="accessToHtmlSource">
        <xsl:choose>
            <xsl:when test="$expertcontributor = 'true' or $developer = 'true'">
                <xsl:value-of select="true()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="false()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:param>
    <xsl:param name="defaultRunAsUser"/>

    <xsl:variable name="styleSheetsExists" select="boolean(/contentobjects/contentobject/objectstylesheet/@exist = 'false' or /contentobjects/contentobject/borderstylesheet/@exist = 'false')"/>

    <xsl:template match="/">

        <html>
            <head>
                <link rel="stylesheet" type="text/css" href="css/admin.css"/>
                <link rel="stylesheet" type="text/css" href="css/codearea.css"/>

                <script type="text/javascript" language="JavaScript" src="javascript/admin.js">//</script>
                <script type="text/javascript" language="JavaScript" src="javascript/validate.js">//</script>
                <script type="text/javascript" src="javascript/tabpane.js">//</script>
                <script type="text/javascript" src="javascript/accessrights.js">//</script>
                <script type="text/javascript" src="tinymce/jscripts/cms/Util.js">//</script>
                <script type="text/javascript" src="tinymce/jscripts/cms/Editor.js">//</script>
                <script type="text/javascript" src="tinymce/jscripts/tiny_mce/tiny_mce.js">//</script>
                <script type="text/javascript" src="codemirror/js/codemirror.js">//</script>
                <script type="text/javascript" src="javascript/codearea.js">//</script>

                <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css"/>

                <xsl:call-template name="waitsplash"/>

                <script type="text/javascript" language="JavaScript">
                  var validatedFields = new Array(2);
                  validatedFields[0] = new Array("%fldName%", "name", validateRequired);
                  validatedFields[1] = new Array("%fldStylesheet%", "stylesheet", validateRequired);

                  function validateAll(formName)
                  {
                      var f = document.forms[formName];

                      f.datasources.value = codeArea_datasources.getCode();

                      if ( !checkAll(formName, validatedFields) )
                          return;

                      f.submit();
                  }

                  function GetCurrentContentObjectIndex(objThis,ObjName)
                  {
                      var lNumRows = itemcount(document.formAdmin[ObjName])

                      if( lNumRows > 1 )
                      {
                          for( var i=0; i &lt; lNumRows; i++ )
                          {
                              if( document.formAdmin[ObjName][i] == objThis )
                              {
                                  return i;
                              }
                          }
                      }
                      else
                      {
                          return 0;
                      }
                  }

                  var paramIndex = null;
                  var inputName = '';

                  function callback_newCategorySelector(categoryKey, categoryName) {
                    if (inputName == '') return;
                    document.getElementById(inputName + paramIndex).value = categoryKey;
                    document.getElementById('view' + inputName + paramIndex).value = categoryName;
                  }

                  function OpenSelectorWindowPage( objThis, page, keyname, viewname, width, height )
                  {
                      var currentRow = GetCurrentContentObjectIndex(objThis,objThis.name);
                      var pageURL = "adminpage?page=" + page;
                      pageURL = pageURL + "&amp;op=select&amp;returnrow=" + currentRow;
                      pageURL = pageURL + "&amp;returnkey=" + keyname;
                      pageURL = pageURL + "&amp;returnview=" + viewname;
                      pageURL = pageURL + "&amp;menukey=" + <xsl:value-of select="$menukey"/>;
                      var l = (screen.width - width) / 2;
                      var t = (screen.height - height) / 2;

                      var props = "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" + height + ",top=" + t + ",left=" + l;
                      newWindow = window.open(pageURL, "PageSelector", props);
                      newWindow.focus();
                  }

				  //DEPRECATED??

                  function addTableRow( table )
                  {
                      var destRow = document.all[table].insertRow();
                      var sourceRow = document.all[table].rows[0];

                      for( i=0; i&lt;sourceRow.cells.length;i++ )
                      {
                          var destCell = destRow.insertCell();
                          var sourceCell = sourceRow.cells[i];
                          destCell.insertAdjacentHTML( 'afterBegin', sourceCell.innerHTML );
                      }
                  }

                  function itemcount(elName)
                  {
                      var lItems;

                      if (elName.length!=null)
                      {
                          lItems = elName.length;
                      }
                      else
                      {
                          lItems = 1;
                      }

                      return lItems;
                  }

                  function GetCurrentObjectIndex(objThis)
                  {
                      var lNumRows = itemcount(document.formAdmin[objThis.name])

                      if( lNumRows > 1 )
                      {
                          for( var i=0; i &lt; lNumRows; i++ )
                          {
                              if ( document.formAdmin[objThis.name][i] == objThis )
                              {
                                  return i;
                              }
                          }
                      }
                      else
                      {
                          return 0;
                      }
                  }

                  function updateStyleSheet()
                  {
                      var form = document.forms["formAdmin"];
                      form.style.display = 'none';

                      waitsplash();

                      var selectedtab = tabPane1.getSelectedPage();
                      document.getElementById("rememberselectedtab").value = selectedtab;

                      form["op"].value = "updatestylesheet";

                      form.submit();
                  }

                  function removeStyleSheetParam(objThis)
                  {
                      var count = itemcount(document.formAdmin[objThis.name]);
                      var type = "xsl";
                      if (objThis.name == "removeborderparam_value")
                      	  type = "border";

                      if (count == 1)
                      {
                        document.formAdmin[type+"param_value"].value = '';
                        document.formAdmin["view"+type+"param_value"].value = '';
                      }
                      else
                      {
                        var index = GetCurrentObjectIndex(objThis);
                        document.formAdmin[type+"param_value"][index].value = '';
                        document.formAdmin["view"+type+"param_value"][index].value = '';

                      }
                  }
                </script>
            </head>

            <body onload="setFocus()">

            <script type="text/javascript">waitsplash();</script>

              <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr>
                  <td>
                    <xsl:choose>
                      <xsl:when test="$create = 1">
                        <h1>
                          <xsl:variable name="links" select="not($subop = 'popup') and not($subop = 'browsepopup')"/>
                          <xsl:call-template name="genericheader">
                            <xsl:with-param name="links" select="$links"/>
                          </xsl:call-template>
                          <a href="adminpage?page={$page}&amp;op=browse&amp;menukey={$menukey}">%headPortlets%</a>
                          <xsl:text>&nbsp;</xsl:text>
                          <span id="titlename"><!--xsl:value-of select="concat('/ ', contentobjects/contentobject/name)"/--></span>
                        </h1>
                      </xsl:when>
                      <xsl:otherwise>
                        <h1>
                          <xsl:variable name="links" select="not($subop = 'popup') and not($subop = 'browsepopup')"/>
                          <xsl:call-template name="genericheader">
                            <xsl:with-param name="links" select="$links"/>
                          </xsl:call-template>
                          <xsl:choose>
                            <xsl:when test="not($subop = 'popup')">
                              <a href="adminpage?page={$page}&amp;op=browse&amp;menukey={$menukey}&amp;subop={$subop}&amp;fieldname={$fieldname}&amp;fieldrow={$fieldrow}">
                                <xsl:text>%headPortlets%</xsl:text>
                              </a>
                            </xsl:when>
                            <xsl:otherwise>
                              <xsl:text>%headPortlets%</xsl:text>
                            </xsl:otherwise>
                          </xsl:choose>
                          <xsl:text>&nbsp;</xsl:text>
                          <span id="titlename"><xsl:value-of select="concat('/ ', contentobjects/contentobject/name)"/></span>
                        </h1>
                      </xsl:otherwise>
                    </xsl:choose>
                  </td>
                </tr>
                <tr>
                  <td>
                            <form name="formAdmin" id="formAdmin" method="post" action="adminpage">

                                <input type="hidden" name="page" value="{$page}"/>
                                <input type="hidden" name="op">
                                    <xsl:attribute name="value">
                                        <xsl:choose>
                                            <xsl:when test="$create = 1">
                                                <xsl:text>create</xsl:text>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:text>update</xsl:text>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:attribute>
                                </input>
                                <input type="hidden" name="object"/>
                                <input type="hidden" name="border"/>
                                <input type="hidden" name="menukey" value="{$menukey}"/>
                                <input type="hidden" id="rememberselectedtab" name="rememberselectedtab" value=""/>

                                <xsl:if test="$defaultcsskey != ''">
                                    <input type="hidden" name="css" value="{$defaultcsskey}"/>
                                </xsl:if>

                                <input type="hidden" name="docindex" value="2"/>

                                <input type="hidden" name="referer">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="$referer"/>
                                    </xsl:attribute>
                                </input>

                                <input type="hidden" name="subop">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="$subop"/>
                                    </xsl:attribute>
                                </input>

                                <input type="hidden" name="fieldname" value="{$fieldname}"/>
                                <input type="hidden" name="fieldrow" value="{$fieldrow}"/>

                                <xsl:if test="$create=0">
                                    <input type="hidden" name="key">
                                        <xsl:attribute name="value"><xsl:value-of select="contentobjects/contentobject/@key"/></xsl:attribute>
                                    </input>
                                </xsl:if>

                                <xsl:call-template name="contentobjectform"/>

                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                    <tr>
                                        <td class="form_form_buttonrow_seperator"><img src="images/1x1.gif"/></td>
                                    </tr>
                                    <tr>
                                        <td colspan="4">

                                            <xsl:call-template name="button">
                                                <xsl:with-param name="type" select="'button'"/>
                                                <xsl:with-param name="caption" select="'%cmdSave%'"/>
                                                <xsl:with-param name="name" select="'lagre'"/>
                                                <xsl:with-param name="disabled" select="$styleSheetsExists"/>
                                                <xsl:with-param name="onclick">
                                                    <xsl:text>javascript:validateAll('formAdmin');</xsl:text>
                                                </xsl:with-param>
                                            </xsl:call-template>

                                            <xsl:text>&nbsp;</xsl:text>

                                            <xsl:call-template name="button">
				                                <xsl:with-param name="type" select="'cancel'"/>
				                                <xsl:with-param name="referer" select="$referer"/>
                                            </xsl:call-template>
                                        </td>
                                    </tr>
                                </table>
                            </form>
                        </td>
                    </tr>
                </table>

                <form name="formAdminDataSource" target="_blank" id="formAdminDataSource" method="post">
                    <xsl:attribute name="action">
                        <xsl:text>adminpage?page=</xsl:text>
                        <xsl:value-of select="$page"/>
                        <xsl:text>&amp;op=datasourcepreview</xsl:text>
                    </xsl:attribute>
                    <input type="hidden" name="key">
                        <xsl:attribute name="value">
                            <xsl:value-of select="/contentobjects/contentobject/@key"/>
                        </xsl:attribute>
                    </input>
                    <input type="hidden" name="document" value=""/>
                    <input type="hidden" name="documenttype" value="co"/>
                    <input type="hidden" name="datasources" value=""/>
                    <input type="hidden" name="menukey" value="{$menukey}"/>
                </form>
            </body>

        </html>
    </xsl:template>

    <xsl:template name="contentobjectform">
        <div class="tab-pane" id="tab-pane-1">
            <script type="text/javascript" language="JavaScript">
                var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
            </script>

            <div class="tab-page" id="tab-page-1">
                <span class="tab">%blockGeneral%</span>

                <script type="text/javascript" language="JavaScript">
                    tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                </script>

                <fieldset>
                    <legend>&nbsp;%blockGeneral%&nbsp;</legend>

                    <table cellspacing="0" cellpadding="2" border="0">
                        <tr><td class="form_labelcolumn"> </td></tr>
                        <tr>
                            <xsl:call-template name="textfield">
                                <xsl:with-param name="name" select="'name'"/>
                                <xsl:with-param name="label" select="'%fldName%:'"/>
                                <xsl:with-param name="selectnode" select="/contentobjects/contentobject/name"/>
                                <xsl:with-param name="size" select="'40'"/>
                                <xsl:with-param name="maxlength" select="'255'"/>
                                <xsl:with-param name="colspan" select="'1'"/>
                                <xsl:with-param name="required" select="'true'"/>
                                <xsl:with-param name="onkeyup">javascript: updateBreadCrumbHeader('titlename', this);</xsl:with-param>
                            </xsl:call-template>
                        </tr>

                        <input type="hidden" name="contentkey">
                            <xsl:attribute name="value">
                                <xsl:value-of select="contentobjects/contentobject/content/@key"/>
                            </xsl:attribute>
                        </input>
                        <input type="hidden" name="publishdate">
                            <xsl:attribute name="value">
                                <xsl:value-of select="contentobjects/contentobject/content/publishdate"/>
                            </xsl:attribute>
                        </input>
                    </table>
                </fieldset>

                <fieldset>
                    <legend>&nbsp;%blockStylesheet%&nbsp;</legend>
                    <table border="0" cellspacing="0" cellpadding="2">
                        <tr><td class="form_labelcolumn"></td></tr>
                        <tr>
                            <xsl:call-template name="resourcefield">
                                <xsl:with-param name="name" select="'stylesheet'"/>
                                <xsl:with-param name="extension" select="'xsl'"/>
                                <xsl:with-param name="mimetype" select="'text/xml'"/>
                                <xsl:with-param name="label" select="'%fldStylesheet%:'"/>
                                <xsl:with-param name="value" select="/contentobjects/contentobject/objectstylesheet/@key"/>
                                <xsl:with-param name="required" select="'true'"/>
                                <xsl:with-param name="onchange" select="'javascript:updateStyleSheet();'"/>
                                <xsl:with-param name="exist" select="/contentobjects/contentobject/objectstylesheet/@exist"/>
                                <xsl:with-param name="valid" select="/contentobjects/contentobject/objectstylesheet/@valid"/>
                                <xsl:with-param name="removeButton" select="false()"/>
                                <xsl:with-param name="reloadButton" select="true()"/>
                                <xsl:with-param name="disableReloadButton" select="$styleSheetsExists"/>
                            </xsl:call-template>
                        </tr>
                    </table>
                    <xsl:call-template name="stylesheetparams"/>
                </fieldset>

                <fieldset>
                    <legend>&nbsp;%blockBorder%&nbsp;</legend>
                    <table cellspacing="0" cellpadding="2" border="0">
                        <tr id="frameRow">
                            <xsl:call-template name="resourcefield">
                                <xsl:with-param name="name" select="'borderstylesheet'"/>
                                <xsl:with-param name="extension" select="'xsl'"/>
                                <xsl:with-param name="mimetype" select="'text/xml'"/>
                                <xsl:with-param name="label" select="'%fldBorderStylesheet%:'"/>
                                <xsl:with-param name="value" select="/contentobjects/contentobject/borderstylesheet/@key"/>
                                <xsl:with-param name="onchange" select="'javascript:updateStyleSheet();'"/>
                                <xsl:with-param name="exist" select="/contentobjects/contentobject/borderstylesheet/@exist"/>
                                <xsl:with-param name="valid" select="/contentobjects/contentobject/borderstylesheet/@valid"/>
                                <xsl:with-param name="reloadButton" select="true()"/>
                                <xsl:with-param name="disableReloadButton" select="$styleSheetsExists"/>
                            </xsl:call-template>
                        </tr>
                    </table>
                    <xsl:call-template name="borderparams"/>
                </fieldset>
            </div>

            <div class="tab-page" id="tab-page-3">
                <span class="tab">%blockProperties%</span>

                <script type="text/javascript" language="JavaScript">
                    tabPane1.addTabPage( document.getElementById( "tab-page-3" ) );
                </script>

              <fieldset>
                <legend>&nbsp;%blockCaching%&nbsp;</legend>
                <table cellspacing="0" cellpadding="2" border="0">
                  <!--tr>
                    <td class="form_labelcolumn"></td>
                  </tr-->
                  <xsl:variable name="cachedisabled" select="/contentobjects/contentobject/contentobjectdata/@cachedisabled"/>
                  <xsl:variable name="cachetype" select="/contentobjects/contentobject/contentobjectdata/@cachetype"/>

                  <tr id="cachetime">
                    <td class="form_labelcolumn" nowrap="nowrap">%fldCacheSettings%:</td>
                    <td colspan="2">
                      <select name="cachetype">
                        <xsl:attribute name="onchange">
                          <xsl:text>javascript:if (this.value == 'specified'){document.getElementById('cachetimetable').style.display='inline';}
                            else{document.getElementById('cachetimetable').style.display='none';}
                          </xsl:text>
                        </xsl:attribute>

                        <option value="off">
                          <xsl:if test="$cachedisabled = 'true'">
                            <xsl:attribute name="selected">selected</xsl:attribute>
                          </xsl:if>
                          %optCachingOff%
                        </option>

                        <option value="default">
                          <xsl:if test="(not($cachedisabled = 'true') and not($cachetype)) or $cachetype = 'default'">
                            <xsl:attribute name="selected">selected</xsl:attribute>
                          </xsl:if>
                          %optDefaultCacheTime%
                        </option>
                        <option value="specified">
                          <xsl:if test="$cachetype = 'specified'">
                            <xsl:attribute name="selected">selected</xsl:attribute>
                          </xsl:if>
                          %optSpecifyCacheTime%
                        </option>
                        <option value="forever">
                          <xsl:if test="$cachetype = 'forever'">
                            <xsl:attribute name="selected">selected</xsl:attribute>
                          </xsl:if>
                          %optCacheForever%
                        </option>
                      </select>
                    </td>
                    <td style="padding-top:4px">

                      <table border="0" cellspacing="0" cellpadding="0" width="100%" id="cachetimetable">
                        <xsl:if test="not($cachetype) or $cachetype != 'specified'">
                          <xsl:attribute name="style">display: none</xsl:attribute>
                        </xsl:if>
                        <tr>
                          <xsl:call-template name="textfield">
                            <xsl:with-param name="name" select="'mincachetime'"/>
                            <xsl:with-param name="label" select="'%fldCacheTime%&nbsp;:'"/>
                            <xsl:with-param name="selectnode"
                                            select="/contentobjects/contentobject/contentobjectdata/@mincachetime"/>
                            <xsl:with-param name="size" select="5"/>
                            <xsl:with-param name="postfield" select="' (%seconds%)'"/>
                          </xsl:call-template>
                        </tr>
                      </table>
                    </td>
                  </tr>
                  <tr>
                    <td class="form_labelcolumn" nowrap="nowrap">
                      %fldRunAs%:
                    </td>
                    <td>
                      <xsl:call-template name="dropdown_runas">
                        <xsl:with-param name="name" select="'runAs'"/>
                        <xsl:with-param name="selectedKey" select="/contentobjects/contentobject/@runAs"/>
                        <xsl:with-param name="defaultRunAsUserName" select="$defaultRunAsUser"/>
                        <xsl:with-param name="inheritMessage" select="'%fldInheritFromPage%'"/>
                      </xsl:call-template>
                    </td>
                  </tr>
                </table>
              </fieldset>
            </div>

          <div class="tab-page" id="tab-page-4">
            <span class="tab">%blockDocument%</span>

            <script type="text/javascript" language="JavaScript">
              tabPane1.addTabPage( document.getElementById( "tab-page-4" ) );
            </script>

            <fieldset>
              <table cellspacing="2" border="0" cellpadding="2">
                <tr>
                  <td>
                    <xsl:call-template name="xhtmleditor">
                      <xsl:with-param name="id" select="'contentdata_body'"/>
                      <xsl:with-param name="name" select="'contentdata_body'"/>
                      <xsl:with-param name="content" select="/contentobjects/contentobject/contentobjectdata/document"/>
                      <xsl:with-param name="configxpath" select="/node()/htmleditorconfig"/>
                      <xsl:with-param name="config" select="'document'"/>
                      <xsl:with-param name="customcss" select="$defaultcsskey"/>
                      <xsl:with-param name="menukey" select="$menukey"/>
                      <xsl:with-param name="disabled" select="false()"/>
                      <xsl:with-param name="accessToHtmlSource" select="$accessToHtmlSource = 'true'"/>
                      <xsl:with-param name="classfilter" select="true()"/>
                    </xsl:call-template>
                  </td>
                </tr>
              </table>
            </fieldset>
          </div>

            <div class="tab-page" id="tab-page-5">
                <span class="tab">%blockDatasource%</span>

                <script type="text/javascript" language="JavaScript">
                    tabPane1.addTabPage( document.getElementById( "tab-page-5" ) );

                    function previewDataSource() {
                      tinyMCE.triggerSave();
                      document.formAdminDataSource.datasources.value = codeArea_datasources.getCode();
                      document.formAdminDataSource.document.value = document.formAdmin.contentdata_body.value;
                      document.formAdminDataSource.submit();
                    }

                </script>

                <fieldset>
                    <table border="0" cellspacing="2" cellpadding="2" width="100%">

                        <tr>
                          <xsl:call-template name="codearea">
                            <xsl:with-param name="name" select="'datasources'"/>
                            <xsl:with-param name="width" select="'100%'"/>
                            <xsl:with-param name="height" select="'380px'"/>
                            <xsl:with-param name="line-numbers" select="true()"/>
                            <xsl:with-param name="read-only" select="false()"/>
                            <xsl:with-param name="selectnode" select="$queryparam"/>
                            <xsl:with-param name="buttons" select="'find,replace,indentall,indentselection,gotoline'"/>
                            <xsl:with-param name="status-bar" select="true()"/>
                          </xsl:call-template>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <xsl:call-template name="button">
                                    <xsl:with-param name="type" select="'button'"/>
                                    <xsl:with-param name="caption" select="'%cmdPreviewDatasource%'"/>
                                    <xsl:with-param name="name" select="'datasourcepreview'"/>
                                    <xsl:with-param name="onclick" select="'javascript:previewDataSource();'"/>
                                </xsl:call-template>
                            </td>
                        </tr>
                    </table>
                </fieldset>
            </div>

            <div class="tab-page" id="tab-page-6">
                <span class="tab">%blockUsedBy%</span>

                <script type="text/javascript" language="JavaScript">
                    tabPane1.addTabPage( document.getElementById( "tab-page-6" ) );
                </script>

                <fieldset>
                    <legend>&nbsp;%blockPages%&nbsp;</legend>

                    <xsl:choose>
                      <xsl:when test="count(//menuitems/menuitem) != 0">
                        <table border="0" cellspacing="2" cellpadding="2">
                            <xsl:for-each select="//menuitems/menuitem">
                                <tr><td>&nbsp;&nbsp;<xsl:value-of select="name"/></td></tr>
                            </xsl:for-each>
                        </table>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:text>%msgNoMenuItemsIsUsingThisPortlet%</xsl:text>
                      </xsl:otherwise>
                    </xsl:choose>

                </fieldset>
                <fieldset>
                    <legend>&nbsp;%blockFrameworks%&nbsp;</legend>
                    <xsl:choose>
                      <xsl:when test="count(/contentobjects/contentobject/pagetemplates/pagetemplate) !=0">
                        <table border="0" cellspacing="2" cellpadding="2">
                            <xsl:for-each select="/contentobjects/contentobject/pagetemplates/pagetemplate">
                                <tr><td>&nbsp;&nbsp;<xsl:value-of select="name"/></td></tr>
                            </xsl:for-each>
                        </table>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:text>%msgNoPageTemplatesIsUsingThisPortlet%</xsl:text>
                      </xsl:otherwise>
                    </xsl:choose>
                </fieldset>
            </div>

            <script type="text/javascript" language="JavaScript">
                setupAllTabs();

                <xsl:if test="$rememberselectedtab != ''">

                    tabPane1.setSelectedPage("<xsl:value-of select="$rememberselectedtab"/>");

                </xsl:if>
            </script>
        </div>

    </xsl:template>

    <xsl:template name="stylesheetparams">
		<img height="6" width="1" src="images/shim.gif"/>
            <xsl:choose>
                <xsl:when test="count(/contentobjects/contentobject/objectstylesheet_xsl/xsl:stylesheet/xsl:param) = 0">
                    %msgNoParameters%
                </xsl:when>
                <xsl:otherwise>
                    <table border="0" cellspacing="0" cellpadding="2">

                        <xsl:for-each select="/contentobjects/contentobject/objectstylesheet_xsl/xsl:stylesheet/xsl:param">
                            <xsl:variable name="xslparam_name" select="@name"/>
                            <xsl:variable name="category-callback-js-fn-name" select="translate($xslparam_name, '-', '_')"/>

                            <xsl:variable name="type" select="node()[local-name() = 'type']"/>
                            <input type="hidden" name="xslparam_name">
                                <xsl:attribute name="value"><xsl:value-of select="$xslparam_name"/></xsl:attribute>
                            </input>

                            <input type="hidden" name="xslparam_type">
                                <xsl:attribute name="value"><xsl:value-of select="$type"/></xsl:attribute>
                            </input>
                            <tr>
                                <xsl:choose>
                                    <xsl:when test="$type = 'resource'">
                                      <xsl:call-template name="resourcefield">
                                        <xsl:with-param name="name" select="'xslparam_value'"/>
                                        <xsl:with-param name="id" select="concat('xslparam_value', position())"/>
                                        <xsl:with-param name="label" select="concat($xslparam_name, ':')"/>
                                        <xsl:with-param name="value" select="/contentobjects/contentobject/contentobjectdata/stylesheetparams/stylesheetparam[@name = $xslparam_name]"/>
                                        <xsl:with-param name="position" select="position()"/>
                                        <xsl:with-param name="size" select="40"/>
                                      </xsl:call-template>
                                      <input type="hidden" name="viewxslparam_value" value="dummy"/>
                                      <input type="hidden" name="btnxslparam_value" value="dummy"/>
                                      <input type="hidden" name="removexslparam_value" value="dummy"/>
                                    </xsl:when>
                                    <xsl:when test="$type = 'page'">
                                        <xsl:variable name="function">
                                          <xsl:text>javascript:OpenSelectorWindowPage( this, 850, &apos;xslparam_value</xsl:text>
                                          <xsl:value-of select="position()"/>
                                          <xsl:text>&apos;,&apos;viewxslparam_value</xsl:text>
                                          <xsl:value-of select="position()"/>
                                          <xsl:text>&apos;, 250, 300 )</xsl:text>
                                        </xsl:variable>
                                        <xsl:variable name="removefunction">
                                            <xsl:text>javascript:removeStyleSheetParam(this)</xsl:text>
                                        </xsl:variable>
                                        <xsl:call-template name="searchfield">
                                            <xsl:with-param name="name" select="'xslparam_value'"/>
                                            <xsl:with-param name="id" select="concat('xslparam_value', position())"/>
                                            <xsl:with-param name="label" select="concat($xslparam_name, ':')"/>
                                            <xsl:with-param name="selectedkey" select="/contentobjects/contentobject/contentobjectdata/stylesheetparams/stylesheetparam[@name = $xslparam_name]"/>
                                            <xsl:with-param name="selectnode" select="/contentobjects/contentobject/contentobjectdata/stylesheetparams/stylesheetparam[@name = $xslparam_name]/@valuename"/>
                                            <xsl:with-param name="size" select="'25'"/>
                                            <xsl:with-param name="maxlength" select="'25'"/>
                                            <xsl:with-param name="buttonfunction" select="$function"/>
                                            <xsl:with-param name="removefunction" select="$removefunction"/>
                                            <xsl:with-param name="colspan" select="'1'"/>
                                            <xsl:with-param name="lefttdwidth" select="'120'"/>
                                            <xsl:with-param name="helpelement">
                                            	<xsl:copy-of select="help"/>
                                            </xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:when>
                                    <xsl:when test="$type = 'category'">

                                        <script type="text/javascript" language="JavaScript">
                                            function callback_<xsl:value-of select="$category-callback-js-fn-name"/>(key, view, win) {
                                          <xsl:choose>
                                            <xsl:when test="last() = 1">
                                              document.getElementsByTagName("xslparam_value")[0].value = key;
                                              document.getElementsByTagName("viewxslparam_value")[0].value = view;
                                            </xsl:when>
                                            <xsl:otherwise>
                                              document.getElementsByTagName("xslparam_value")[<xsl:value-of
                                                    select="position() - 1"/>].value = key;
                                              document.getElementsByTagName("viewxslparam_value")[<xsl:value-of
                                                    select="position() - 1"/>].value = view;
                                            </xsl:otherwise>
                                          </xsl:choose>
                                        win.close();
                                        }
                                    </script>


                                      <xsl:variable name="function">
                                        <xsl:text>paramIndex =</xsl:text>
                                        <xsl:value-of select="position()"/>
                                        <xsl:text>; inputName = 'xslparam_value';</xsl:text>
                                        <xsl:text>OpenNewCategorySelector(</xsl:text>
                                        <xsl:text>-1, '', null, false, null, -1);</xsl:text>
                                      </xsl:variable>
                                      <xsl:variable name="removefunction">
                                        <xsl:text>javascript:removeStyleSheetParam(this)</xsl:text>
                                      </xsl:variable>

                                      <!--xsl:variable name="function">
                                        <xsl:text>paramIndex=getObjectIndex(this);</xsl:text>
                                        <xsl:text>OpenNewCategorySelector(</xsl:text>
                                        <xsl:text>-1, '', null, false, null, -1);</xsl:text>
                                      </xsl:variable>
                                      <xsl:variable name="removefunction">
                                        <xsl:text>javascript:removeStyleSheetParam(this)</xsl:text>
                                      </xsl:variable-->
                                      <xsl:call-template name="searchfield">
                                        <xsl:with-param name="name" select="'xslparam_value'"/>
                                        <xsl:with-param name="id" select="concat('xslparam_value', position())"/>
                                        <xsl:with-param name="label" select="concat($xslparam_name, ':')"/>
                                        <xsl:with-param name="selectedkey" select="/contentobjects/contentobject/contentobjectdata/stylesheetparams/stylesheetparam[@name = $xslparam_name]"/>
                                        <xsl:with-param name="selectnode" select="/contentobjects/contentobject/contentobjectdata/stylesheetparams/stylesheetparam[@name = $xslparam_name]/@valuename"/>
                                        <xsl:with-param name="size" select="'25'"/>
                                        <xsl:with-param name="maxlength" select="'25'"/>
                                        <xsl:with-param name="buttonfunction" select="$function"/>
                                        <xsl:with-param name="removefunction" select="$removefunction"/>
                                        <xsl:with-param name="colspan" select="'1'"/>
                                        <xsl:with-param name="lefttdwidth" select="'120'"/>
                                        <xsl:with-param name="helpelement">
	                                       	<xsl:copy-of select="help"/>
                                        </xsl:with-param>
                                    </xsl:call-template>
                                </xsl:when>
                                <xsl:otherwise>
                                	<xsl:variable name="postfield">
                                		<xsl:if test="@select">
                                			<xsl:text>&nbsp;&nbsp;%fldDefault%: </xsl:text><xsl:value-of select="@select"/>
                                		</xsl:if>
                                	</xsl:variable>
                                    <xsl:call-template name="textfield">
                                        <xsl:with-param name="name" select="'xslparam_value'"/>
                                        <xsl:with-param name="label" select="concat($xslparam_name, ':')"/>
                                        <xsl:with-param name="selectnode" select="/contentobjects/contentobject/contentobjectdata/stylesheetparams/stylesheetparam[@name = $xslparam_name]"/>
                                        <xsl:with-param name="size" select="'25'"/>
                                        <xsl:with-param name="maxlength" select="'255'"/>
                                        <xsl:with-param name="colspan" select="'1'"/>
                                        <xsl:with-param name="lefttdwidth" select="'120'"/>
                                        <xsl:with-param name="postfield" select="$postfield"/>
                                        <xsl:with-param name="helpelement">
                                           	<xsl:copy-of select="help"/>
                                        </xsl:with-param>
                                    </xsl:call-template>
                                    <input type="hidden" name="viewxslparam_value" value="dummy"/>
                                    <input type="hidden" name="btnxslparam_value" value="dummy"/>
                                    <input type="hidden" name="removexslparam_value" value="dummy"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </tr>
                    </xsl:for-each>
                </table>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="borderparams">
		<img height="6" width="1" src="images/shim.gif"/>
            <xsl:choose>
                <xsl:when test="count(/contentobjects/contentobject/borderstylesheet_xsl/xsl:stylesheet/xsl:param) = 0">
                    %msgNoParameters%
                </xsl:when>
                <xsl:otherwise>


                    <table border="0" cellspacing="0" cellpadding="2">
                        <xsl:for-each select="/contentobjects/contentobject/borderstylesheet_xsl/xsl:stylesheet/xsl:param">
                            <xsl:variable name="borderparam_name" select="@name"/>
                            <xsl:variable name="category-callback-js-fn-name" select="translate($borderparam_name, '-', '_')"/>

                            <xsl:variable name="type" select="node()[local-name() = 'type']"/>
                            <input type="hidden" name="borderparam_name">
                                <xsl:attribute name="value"><xsl:value-of select="$borderparam_name"/></xsl:attribute>
                            </input>
                            <input type="hidden" name="borderparam_type">
                                <xsl:attribute name="value"><xsl:value-of select="$type"/></xsl:attribute>
                            </input>
                            <tr>
                                <xsl:choose>
                                    <xsl:when test="$type = 'page'">
                                        <xsl:variable name="function">
                                          <xsl:text>javascript:OpenSelectorWindowPage( this, 850, &apos;borderparam_value</xsl:text>
                                          <xsl:value-of select="position()"/>
                                          <xsl:text>&apos;,&apos;viewborderparam_value</xsl:text>
                                          <xsl:value-of select="position()"/>
                                          <xsl:text>&apos;, 250, 300 )</xsl:text>
                                        </xsl:variable>
                                        <xsl:variable name="removefunction">
                                            <xsl:text>javascript:removeStyleSheetParam(this)</xsl:text>
                                        </xsl:variable>
                                        <xsl:call-template name="searchfield">
                                            <xsl:with-param name="name" select="'borderparam_value'"/>
                                            <xsl:with-param name="id" select="concat('borderparam_value', position())"/>
                                            <xsl:with-param name="label" select="concat($borderparam_name, ':')"/>
                                            <xsl:with-param name="selectedkey" select="/contentobjects/contentobject/contentobjectdata/borderparams/borderparam[@name = $borderparam_name]"/>
                                            <xsl:with-param name="selectnode" select="/contentobjects/contentobject/contentobjectdata/borderparams/borderparam[@name = $borderparam_name]/@valuename"/>
                                            <xsl:with-param name="size" select="'25'"/>
                                            <xsl:with-param name="maxlength" select="'25'"/>
                                            <xsl:with-param name="buttonfunction" select="$function"/>
                                            <xsl:with-param name="removefunction" select="$removefunction"/>
                                            <xsl:with-param name="colspan" select="'1'"/>
                                            <xsl:with-param name="lefttdwidth" select="'120'"/>
                                            <xsl:with-param name="helpelement">
                                            	<xsl:copy-of select="help"/>
                                            </xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:when>
                                    <xsl:when test="$type = 'category'">
                                        <script type="text/javascript" language="JavaScript">
                                            function callback_border_<xsl:value-of select="$category-callback-js-fn-name"/>(key, view, win) {
                                            <xsl:choose>
                                                <xsl:when test="last() = 1">
                                                    document.getElementsByName("borderparam_value")[0].value = key;
                                                    document.getElementsByName("viewborderparam_value")[0].value = view;
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    document.getElementsByName("borderparam_value")[<xsl:value-of select="position() - 1"/>].value = key;
                                                document.getElementsByName("viewborderparam_value")[<xsl:value-of select="position() - 1"/>].value = view;
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            win.close();
                                            }
                                        </script>
                                        <xsl:variable name="function">
                                          <xsl:text>paramIndex =</xsl:text>
                                          <xsl:value-of select="position()"/>
                                          <xsl:text>; inputName = 'borderparam_value' ;</xsl:text>
                                          <xsl:text>OpenNewCategorySelector(</xsl:text>
                                          <xsl:text>-1, '', null, false, null, -1);</xsl:text>
        				                        </xsl:variable>
                                        <xsl:variable name="removefunction">
                                            <xsl:text>javascript:removeStyleSheetParam(this)</xsl:text>
                                        </xsl:variable>
                                        <xsl:call-template name="searchfield">
                                            <xsl:with-param name="name" select="'borderparam_value'"/>
                                            <xsl:with-param name="id" select="concat('borderparam_value', position())"/>
                                            <xsl:with-param name="label" select="concat($borderparam_name, ':')"/>
                                            <xsl:with-param name="selectedkey" select="/contentobjects/contentobject/contentobjectdata/borderparams/borderparam[@name = $borderparam_name]"/>
                                            <xsl:with-param name="selectnode" select="/contentobjects/contentobject/contentobjectdata/borderparams/borderparam[@name = $borderparam_name]/@valuename"/>
                                            <xsl:with-param name="size" select="'25'"/>
                                            <xsl:with-param name="maxlength" select="'25'"/>
                                            <xsl:with-param name="buttonfunction" select="$function"/>
                                            <xsl:with-param name="removefunction" select="$removefunction"/>
                                            <xsl:with-param name="colspan" select="'1'"/>
                                            <xsl:with-param name="lefttdwidth" select="'120'"/>
                                            <xsl:with-param name="helpelement">
                                            	<xsl:copy-of select="help"/>
                                            </xsl:with-param>
                                        </xsl:call-template>
                                    </xsl:when>
                                    <xsl:when test="$type = 'content'">
                                        <td><xsl:value-of select="@name"/></td>
                                        <td>%msgContentParamType%</td>
                                        <input type="hidden" name="borderparam_value" value="dummy"/>
                                        <input type="hidden" name="viewborderparam_value" value="dummy"/>
                                        <input type="hidden" name="btnborderparam_value" value="dummy"/>
                                        <input type="hidden" name="removeborderparam_value" value="dummy"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                    	<xsl:variable name="postfield">
											<xsl:if test="@select">
												<xsl:text>&nbsp;&nbsp;%fldDefault%: </xsl:text><xsl:value-of select="@select"/>
											</xsl:if>
										</xsl:variable>
                                        <xsl:call-template name="textfield">
                                            <xsl:with-param name="name" select="'borderparam_value'"/>
                                            <xsl:with-param name="label" select="concat($borderparam_name, ':')"/>
                                            <xsl:with-param name="selectnode" select="/contentobjects/contentobject/contentobjectdata/borderparams/borderparam[@name = $borderparam_name]"/>
                                            <xsl:with-param name="size" select="'25'"/>
                                            <xsl:with-param name="maxlength" select="'255'"/>
                                            <xsl:with-param name="colspan" select="'1'"/>
                                            <xsl:with-param name="lefttdwidth" select="'120'"/>
                                            <xsl:with-param name="postfield" select="$postfield"/>
                                            <xsl:with-param name="helpelement">
                                            	<xsl:copy-of select="help"/>
                                            </xsl:with-param>
                                        </xsl:call-template>
                                        <input type="hidden" name="viewborderparam_value" value="dummy"/>
                                        <input type="hidden" name="btnborderparam_value" value="dummy"/>
                                        <input type="hidden" name="removeborderparam_value" value="dummy"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </tr>
                        </xsl:for-each>
                    </table>
                </xsl:otherwise>
            </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
