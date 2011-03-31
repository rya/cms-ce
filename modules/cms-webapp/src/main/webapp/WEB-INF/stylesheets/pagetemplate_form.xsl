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

    <xsl:output method="html"/>

    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/serialize.xsl"/>
    <xsl:include href="common/genericheader.xsl"/>
    <xsl:include href="common/waitsplash.xsl"/>

    <xsl:include href="common/textfield.xsl"/>
    <xsl:include href="common/textarea.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/searchfield.xsl"/>
    <xsl:include href="common/resourcefield.xsl"/>
    <xsl:include href="common/codearea.xsl"/>
    <xsl:include href="editor/xhtmleditor.xsl"/>
    <xsl:include href="common/contentobjectselector.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>
    <xsl:include href="common/displayerror.xsl"/>
    <xsl:include href="common/dropdown_runas.xsl"/>

    <xsl:param name="selstylesheetkey"/>
    <xsl:param name="selstylesheetExist"/>
    <xsl:param name="selstylesheetValid"/>
    <xsl:param name="datasources"/>
    <xsl:param name="stylesheetupdated"/>
    <xsl:param name="create"/>
    <xsl:param name="selectedtabpageid"/>
    <xsl:param name="defaultcsskey"/>
    <xsl:param name="defaultcssExist"/>
    <xsl:param name="cssStylesheetKey"/>
    <xsl:param name="cssStylesheetExist"/>
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

    <xsl:variable name="styleSheetsExists" select="boolean($selstylesheetExist = 'false' or $cssStylesheetExist = 'false')"/>

    <xsl:template match="/">

        <html>
            <script type="text/javascript" src="javascript/validate.js">//</script>
            <script type="text/javascript" src="javascript/tabpane.js">//</script>
            <script type="text/javascript" src="javascript/admin.js">//</script>
            <script type="text/javascript" src="tinymce/jscripts/cms/Util.js">//</script>
            <script type="text/javascript" src="tinymce/jscripts/cms/Editor.js">//</script>
            <script type="text/javascript" src="tinymce/jscripts/tiny_mce/tiny_mce.js">//</script>
            <script type="text/javascript" src="codemirror/js/codemirror.js">//</script>
            <script type="text/javascript" src="javascript/codearea.js">//</script>

            <xsl:call-template name="waitsplash"/>

            <script type="text/javascript" language="JavaScript">

                var contentObjectList = new Object();
                <xsl:for-each select="/pagetemplates/pagetemplate/contentobjects/contentobject[ @parameterkey = /pagetemplates/pagetemplate/pagetemplateparameters/pagetemplateparameter/@key ]">
                    <xsl:text>contentObjectList['</xsl:text><xsl:value-of select="@conobjkey"/><xsl:text>']</xsl:text> = 'added';
                </xsl:for-each>

                var validatedFields = new Array(1);
                var validatedFields = new Array(1);
                validatedFields[0] = new Array("%fldName%", "name", validateRequired);
                validatedFields[1] = new Array("%fldSelectedStylesheet%", "stylesheetkey", validateRequired);

				function validateAll(formName)
				{
					var f = document.forms[formName];

              f.datasources.value = codeArea_datasources.getCode();

           // copy editor body to hidden textbox
           if (document.getElementById('type').value == 'document')
					{
						document.getElementById('docRow').style.display = '';
              //document.forms['formAdminDataSource'].document.value = tinyMCE.get('contentdata_body').getContent();

              /*
            if (document.frames['editor'])
						{
							if (document.frames['editor'].ViewSource)
							{
								f.getElementsByName("contentdata_body")[0].value = document.frames['editor'].tbContentElement.DOM.body.innerHTML;
							}
							else
							{
								f.getElementsByName("contentdata_body")[0].value = document.frames['editor'].tbContentElement.DOM.body.innerText;
							}
						}
						*/
                    }

                    if ( !checkAll(formName, validatedFields) )
					{
                        return;
					}

					selectAllRowsInSelect('contenttypekey');



              f.submit();
                }

                function OpenSelectorWindowForObjects(objThis, fieldName) {
                	var menuKey = <xsl:value-of select="$menukey"/>;
                    var fieldRow = GetCurrentContentObjectIndex(objThis,objThis.name);

 			 		OpenObjectBrowsePopup(menuKey, fieldName, fieldRow);
                }

                  var paramIndex = null;
                  function callback_newCategorySelector(categoryKey, categoryName) {
                      document.getElementById("parameter_value" + paramIndex).value = categoryKey;
                  		document.getElementById("viewparameter_value" + paramIndex).value = categoryName;
                }

                function OpenSelectorWindowForPages( objThis, page, keyname, viewname, width, height)
                {
                  var currentRow = GetCurrentContentObjectIndex(objThis,objThis.name);
                  var l = (screen.width - width) / 2;
                  var t = (screen.height - height) / 2;

                  if (itemcount(document.getElementsByName(objThis.name)) &gt; 1)
                      newWindow = window.open("adminpage?returnrow=" + currentRow + "&amp;page=" + page + "&amp;op=select&amp;returnkey=" + keyname + "&amp;returnview=" + viewname +"&amp;menukey="+ <xsl:value-of select="$menukey"/>, "Preview", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" + height + ",left=" + l + ",top=" + t);
                  else
                      newWindow = window.open("adminpage?page=" + page + "&amp;op=select&amp;returnkey=" + keyname + "&amp;returnview=" + viewname +"&amp;menukey="+ <xsl:value-of select="$menukey"/>, "Preview", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" + height + ",left=" + l + ",top" + t);
                  newWindow.focus();
                }

                function itemcount(elName)
                {
                  var lItems;

                  if (elName.length!=null) {
                    lItems = elName.length;
                  }
                  else {
                    lItems = 1;
                  }

                  return lItems;
                }

                function GetCurrentContentObjectIndex(objThis,ObjName)
                {
                  var lNumRows = itemcount(document.getElementsByName(ObjName))

                  if( lNumRows > 1 ) {
                    for( var i=0; i &lt; lNumRows; i++ ) {
                      if( document.getElementsByName(ObjName)[i] == objThis ) {
                      return i;
                      }
                    }
                  }
                  else {
                    return 0;
                  }
                }

                function GetCurrentObjectIndex(objThis)
                {
                  var lNumRows = itemcount(document.forms['formAdmin'][objThis.name])

                  if( lNumRows > 1 ) {
                    for( var i=0; i &lt; lNumRows; i++ ) {
                      if( document.forms['formAdmin'][objThis.name][i] == objThis ) {
                        return i;
                      }
                    }
                  }
                  else {
                    return 0;
                  }
                }

				function toggleSeparator(object)
				{
					var idx = object.name.substr(9) - 1;
					if(object.checked)
					{
						//document.getElementsByName(object.name.substr(1)).value = '1';
						document.forms['formAdmin'][object.name.substr(1)].value = '1';
						if (document.getElementsByName('separatortr')[idx])
						{
							document.getElementsByName('separatortr')[idx].style.display = '';
							document.getElementsByName('newbtntd')[idx].style.display = '';
						}
						else
						{
							document.getElementsByName('separatortr').style.display = '';
							document.getElementsByName('newbtntd').style.display = '';
						}
					}
					else
					{
						document.forms['formAdmin'][object.name.substr(1)].value = '0';
						if (document.getElementsByName('separatortr')[idx])
						{
							document.getElementsByName('separatortr')[idx].style.display = 'none';
							document.getElementsByName('newbtntd')[idx].style.display = 'none';
						}
						else
						{
							document.getElementsByName('separatortr').style.display = 'none';
							document.getElementsByName('newbtntd').style.display = 'none';
						}
					}
				}

                function removeContentObjectSingle( name )
                {
                  contentObjectList[document.forms['formAdmin'][name].value] = 'removed';
                  document.forms['formAdmin'][name].value = "";
                  document.forms['formAdmin']['view' + name].value = "";
                }

                function removeParameter(objThis)
                {
                  var count = itemcount(document.forms['formAdmin'][objThis.name]);

                  if (count == 1) {
                    document.forms['formAdmin']['parameter_value'].value = '';
                    document.forms['formAdmin']['viewparameter_value'].value = '';
                  }
                  else {
                    var index = GetCurrentObjectIndex(objThis);
                    document.forms['formAdmin']['parameter_value'][index].value = '';
                    document.forms['formAdmin']['viewparameter_value'][index].value = '';
                  }
                }

                function addObjectSelector( name, idx )
                {
					var tbody = document.getElementsByName("tbl" + name)[0];
                    var sourceRow = tbody.rows[0];
					var destRow = sourceRow.cloneNode(true);
					tbody.appendChild(destRow);
					var lastIndex = tbody.rows.length;

					/**********************************************************
					var destCell = document.createElement('td');
                    var sourceCell = sourceRow.cells[0];
					var destRow = document.all["tbl" + name].insertRow();
                    var destCell = destRow.insertCell();
                    var sourceRow = document.all["tbl" + name].rows[0];
                    var sourceCell = sourceRow.cells[0];
                    destCell.insertAdjacentHTML( 'afterBegin', sourceCell.innerHTML );
                    var newIndex = itemcount( document.formAdmin[name] );
					**********************************************************/

                    document.forms['formAdmin'][name][lastIndex-1].value = '';
                    document.forms['formAdmin']['view' + name][lastIndex-1].value = '';

                    enableDisableButtons( name );
				}


				function enableDisableButtons( name )
				{
          if( document.forms['formAdmin'][name] == null )
					{
						return;
					}

					var count = itemcount(document.forms['formAdmin'][name]);

					if( count == 1 )
					{
						if( document.forms['formAdmin']['btn'+name+'up'] != null )
						{
							if( document.forms['formAdmin']['btn'+name+'up'].length == 1 )
							{
								setImageButtonEnabled( document.forms['formAdmin']['btn'+name+'up'][0], false );
							}
							else
							{
								setImageButtonEnabled( document.forms['formAdmin']['btn'+name+'up'], false );
							}

							if( document.forms['formAdmin']['btn'+name+'down'].length == 1 )
                            {
								setImageButtonEnabled( document.formAdmin['btn'+name+'down'][0], false );
							}
							else
							{
								setImageButtonEnabled( document.formAdmin['btn'+name+'down'], false );
							}
						}
					}
					else
					{
						for( var i=1; i &lt; count-1; i++ )
                        {
							setImageButtonEnabled( document.forms['formAdmin']['btn'+name+'down'][i], true );
							setImageButtonEnabled( document.forms['formAdmin']['btn'+name+'up'][i], true );
						}

						// Handle first

                        setImageButtonEnabled( document.forms['formAdmin']['btn'+name+'up'][0], false );
                        setImageButtonEnabled( document.forms['formAdmin']['btn'+name+'down'][0], true );

						// Handle last
						setImageButtonEnabled( document.forms['formAdmin']['btn'+name+'up'][count-1], true );
						setImageButtonEnabled( document.forms['formAdmin']['btn'+name+'down'][count-1], false );

                        }
                    }

                    function moveUp( col, objBtn )
                    {
						var index = GetCurrentContentObjectIndex(objBtn,objBtn.name);
						var tempKey = document.forms['formAdmin'][col][index-1].value;
						var tempView = document.forms['formAdmin']['view' + col][index-1].value;
						var tempObjdoc = document.forms['formAdmin'][col + 'objdoc'][index-1].value;

						document.forms['formAdmin'][col + 'objdoc'][index-1].value = document.forms['formAdmin'][col + 'objdoc'][index].value;
						document.forms['formAdmin'][col][index-1].value = document.forms['formAdmin'][col][index].value;
						document.forms['formAdmin']['view' + col][index-1].value = document.forms['formAdmin']['view' + col][index].value;
						document.forms['formAdmin'][col + 'objdoc'][index].value = tempObjdoc;
						document.forms['formAdmin'][col][index].value = tempKey;
						document.forms['formAdmin']['view' + col][index].value = tempView;
						enableDisableButtons( col );
                    }

                    function moveDown( col, objBtn )
                    {
                        var index = GetCurrentContentObjectIndex(objBtn,objBtn.name);

                        var tempKey = document.forms['formAdmin'][col][index].value;
                        var tempView = document.forms['formAdmin']['view' + col][index].value;
                        var tempObjdoc = document.forms['formAdmin'][col + 'objdoc'][index].value;

                		document.forms['formAdmin'][col + 'objdoc'][index].value = document.forms['formAdmin'][col + 'objdoc'][index+1].value;
                        document.forms['formAdmin'][col][index].value = document.forms['formAdmin'][col][index+1].value;
                        document.forms['formAdmin']['view' + col][index].value = document.forms['formAdmin']['view' + col][index+1].value;

                		document.forms['formAdmin'][col + 'objdoc'][index+1].value = tempObjdoc;
                        document.forms['formAdmin'][col][index+1].value = tempKey;
                        document.forms['formAdmin']['view' + col][index+1].value = tempView;

                        enableDisableButtons( col );
                    }

                    function removeContentObject( objThis, name )
                    {
						count = itemcount(document.forms['formAdmin'][objThis.name]);
						if( count == 1 )
						{
							if (document.forms['formAdmin'][name][0])
							{
								contentObjectList[document.forms['formAdmin'][name][0].value] = 'removed';
							}
							else
							{
								contentObjectList[document.forms['formAdmin'][name].value] = 'removed';
							}
							document.forms['formAdmin'][name].value = '';
	                    	document.forms['formAdmin']['view' + name].value = '';
							return;
						}
						var index = GetCurrentContentObjectIndex(objThis,objThis.name)
						contentObjectList[document.forms['formAdmin'][name][index].value] = 'removed';
						document.getElementsByName("tbl" + name)[0].deleteRow(index);
						enableDisableButtons( name );
                    }

					function activateDeactivateMultiple(multipleName, btnName)
					{
						if (itemcount(document.forms['formAdmin'][btnName]) &gt; 1)
						{
							document.forms['formAdmin'][multipleName].disabled = true;
						}
						else
						{
	                		document.forms['formAdmin'][multipleName].disabled = false;
						}
		            }

                    function editObject(objThis, objectName) {
                        var currentRow = getObjectIndex(objThis);
                        var objectKey = document.getElementsByName(objectName)[currentRow].value;
                        var menuKey = <xsl:value-of select="$menukey"/>;
                        if (objectKey != '') {
                            OpenObjectPopup(menuKey, objectKey, objectName, currentRow);
                        }
                    }

                    function callback_editObject(fieldName, fieldRow, key, title, current) {
                        if (!title == '') {
                            var view = document.getElementsByName("view"+fieldName)[fieldRow];
                            view.value = title;
                        }
                    }

                    function deletePortletFromObjectList( keyField )
                    {
                      var portletKey = keyField.value || null;

                      if ( portletKey != null )
                      {
                        delete contentObjectList[parseInt(keyField.value)];
                      }
                    }
              
                    function callback_selectObject(fieldName, fieldRow, key, title)
                    {
                      var keyField = document.getElementsByName(fieldName)[fieldRow];
                      var viewField = document.getElementsByName("view"+fieldName)[fieldRow];

                      deletePortletFromObjectList(keyField);

                      keyField.value = key;
                      viewField.value = title;
                    }

                    function updateCSS() {
	                       var selectedTabPage = tabPane1.getSelectedPage();
	                       cssKey = document.forms['formAdmin']['csskey'].value;
			               selectAllRowsInSelect('contenttypekey');
	                       document.forms['formAdmin'].action = 'adminpage?page=<xsl:value-of select="$page"/>&amp;op=form&amp;selectedcsskey=' + cssKey + "&amp;selectedtabpageid=" + selectedTabPage + "&amp;updatecss=true";
	                       document.forms['formAdmin'].submit();
	                   }
            </script>

            <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css"/>
            <link rel="stylesheet" type="text/css" href="css/codearea.css"/>
            <link rel="stylesheet" type="text/css" href="css/admin.css"/>

            <body onload="setFocus()">

                <script type="text/javascript">waitsplash();</script>

                <form id="formAdmin" name="formAdmin" method="post">
                    <xsl:attribute name="action">
                        <xsl:if test="$create=1">
                            <xsl:text>adminpage?page=</xsl:text>
                            <xsl:value-of select="$page"/>
                            <xsl:text>&amp;op=create</xsl:text>
                        </xsl:if>
                        <xsl:if test="$create=0">
                            <xsl:text>adminpage?page=</xsl:text>
                            <xsl:value-of select="$page"/>
                            <xsl:text>&amp;op=update</xsl:text>
                        </xsl:if>
                    </xsl:attribute>

                    <xsl:if test="$create=0">
                        <input type="hidden" name="key">
                            <xsl:attribute name="value"><xsl:value-of select="pagetemplates/pagetemplate/@key"/></xsl:attribute>
                        </input>
                    </xsl:if>

                    <input type="hidden" name="menukey" value="{$menukey}"/>


                  <h1>
                    <xsl:call-template name="genericheader"/>
                    <a href="adminpage?page={$page}&amp;op=browse&amp;menukey={$menukey}">%headFramework%</a>
                    <xsl:text>&nbsp;</xsl:text>
                    <span id="titlename">
                      <xsl:if test="$create != 1">
                        <xsl:value-of select="concat('/ ', /pagetemplates/pagetemplate/name)"/>
                      </xsl:if>
                    </span>
                  </h1>

                  <!--xsl:choose>
                    <xsl:when test="$create = 1">
                      <h1>
                        <xsl:call-template name="genericheader"/>
                        <a href="adminpage?page={$page}&amp;op=browse&amp;menukey={$menukey}">%headFramework%</a>
                      </h1>
                      <h2>
                        %headNew%: <span id="titlename"> </span>
                      </h2>
                    </xsl:when>
                    <xsl:otherwise>
                      <h1>
                        <xsl:call-template name="genericheader"/>
                        <a href="adminpage?page={$page}&amp;op=browse&amp;menukey={$menukey}">%headFramework%</a>
                      </h1>
                      <h2>
                        %headEdit%: <span id="titlename"><xsl:value-of select="/pagetemplates/pagetemplate/name"/></span>
                      </h2>
                    </xsl:otherwise>
                  </xsl:choose-->

                  <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td>
                                <xsl:call-template name="form"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="form_form_buttonrow_seperator"><img src="images/1x1.gif"/></td>
                        </tr>
                        <tr>
                            <td>
                                <xsl:call-template name="button">
                                    <xsl:with-param name="type" select="'button'"/>
                                    <xsl:with-param name="caption" select="'%cmdSave%'"/>
                                    <xsl:with-param name="name" select="'lagre'"/>
                                    <xsl:with-param name="onclick">
                                        <xsl:text>javascript:</xsl:text>
                                        <xsl:if test="/pagetemplates/pages/page and $stylesheetupdated">
                                            <xsl:text> if (confirm('%alertUpdatePageTemplate%'))</xsl:text>
                                        </xsl:if>
                                        <xsl:text> validateAll('formAdmin');</xsl:text>
                                    </xsl:with-param>
                                    <xsl:with-param name="disabled" select="$styleSheetsExists"/>
                                </xsl:call-template>

                                <xsl:text> </xsl:text>

                                <xsl:call-template name="button">
                                    <xsl:with-param name="type" select="'link'"/>
                                    <xsl:with-param name="caption" select="'%cmdCancel%'"/>
                                    <xsl:with-param name="href" select="'javascript:history.back();'"/>
                                </xsl:call-template>

                            </td>
                        </tr>

                    </table>
                </form>

                <form name="formAdminDataSource" target="_blank" id="formAdminDataSource" method="post">
                    <xsl:attribute name="action">
                        <xsl:text>adminpage?page=900&amp;op=datasourcepreview</xsl:text>
                    </xsl:attribute>
                    <input type="hidden" name="document" value=""/>
                    <input type="hidden" name="documenttype" value="pagetemplate"/>
                    <input type="hidden" name="datasources" value=""/>
                    <input type="hidden" name="menukey" value="{$menukey}"/>
                </form>

            </body>
        </html>

    </xsl:template>

    <xsl:template name="form">

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
                        <table border="0" cellspacing="2" cellpadding="2">
                            <tr>
                                <xsl:call-template name="textfield">
                                    <xsl:with-param name="name" select="'name'"/>
                                    <xsl:with-param name="label" select="'%fldName%:'"/>
                                    <xsl:with-param name="selectnode" select="/pagetemplates/pagetemplate/name"/>
                                    <xsl:with-param name="size" select="'40'"/>
                                    <xsl:with-param name="maxlength" select="'255'"/>
                                    <xsl:with-param name="colspan" select="'1'"/>
                                    <xsl:with-param name="lefttdwidth" select="'120'"/>
                                    <xsl:with-param name="required" select="'true'"/>
                                    <xsl:with-param name="onkeyup">javascript: updateBreadCrumbHeader('titlename', this);</xsl:with-param>
                                </xsl:call-template>
                            </tr>
                            <tr>
                                <td class="form_labelcolumn" nowrap="nowrap">
                                    %fldPageTemplateType%:
                                </td>
                                <td>
                                    <script language="JavaScript" type="text/javascript">
                                        function changeType(type) {
                                          if (type == 'document' || type == 'form' || type == 'newsletter')
                                            document.getElementById('docRow').style.display = '';
                                          else
                                            document.getElementById('docRow').style.display = 'none';

                                      if (type == 'content' || type == 'sectionpage'  || type == 'newsletter')
                                            document.getElementById('contentRow').style.display = '';
                                           else
                                            document.getElementById('contentRow').style.display = 'none';
                                      
                                        }
                                    </script>

                                    <select id="type" name="type" onChange="javascript: changeType(this.value);">
                                        <option value="page">
                                            <xsl:if test="/pagetemplates/pagetemplate/@type = 'page'">
                                                <xsl:attribute name="selected">selected</xsl:attribute>
                                            </xsl:if>
                                            %optPageTemplateTypeDefault%
                                        </option>
                                        <option value="document">
                                            <xsl:if test="/pagetemplates/pagetemplate/@type = 'document'">
                                                <xsl:attribute name="selected">selected</xsl:attribute>
                                            </xsl:if>
                                            %optPageTemplateTypeDocument%
                                        </option>
                                        <option value="form">
                                            <xsl:if test="/pagetemplates/pagetemplate/@type = 'form'">
                                                <xsl:attribute name="selected">selected</xsl:attribute>
                                            </xsl:if>
                                            %optPageTemplateTypeForm%
                                        </option>
                                        <option value="newsletter">
                                            <xsl:if test="/pagetemplates/pagetemplate/@type = 'newsletter'">
                                                <xsl:attribute name="selected">selected</xsl:attribute>
                                            </xsl:if>
                                            %optPageTemplateTypeNewsletter%
                                        </option>
										<option value="content">
                                            <xsl:if test="/pagetemplates/pagetemplate/@type = 'content'">
                                                <xsl:attribute name="selected">selected</xsl:attribute>
                                            </xsl:if>
                                            %optPageTemplateTypeContent%
                                        </option>
                                        <option value="sectionpage">
                                            <xsl:if test="/pagetemplates/pagetemplate/@type = 'sectionpage'">
                                                <xsl:attribute name="selected">selected</xsl:attribute>
                                            </xsl:if>
                                            %optPageTemplateTypeSectionPage%
                                        </option>
                                    </select>
                                    <script type="text/javascript">
                                      document.getElementById('type').onkeyup = function(e) {
                                        changeType(this.value);
                                      }
                                    </script>
                                </td>
                            </tr>
                            <tr>
                                <xsl:call-template name="textarea">
                                    <xsl:with-param name="name" select="'description'"/>
                                    <xsl:with-param name="label" select="'%fldDescription%:'"/>
                                    <xsl:with-param name="selectnode" select="/pagetemplates/pagetemplate/description"/>
                                    <xsl:with-param name="rows" select="'3'"/>
                                    <xsl:with-param name="cols" select="'60'"/>
                                    <xsl:with-param name="colspan" select="'1'"/>
                                </xsl:call-template>
                            </tr>
                            <xsl:call-template name="resourcefield">
                                <xsl:with-param name="name" select="'csskey'"/>
                                <xsl:with-param name="extension" select="'css'"/>
                                <xsl:with-param name="mimetype" select="'text/css'"/>
                                <xsl:with-param name="label" select="'%fldDefaultCSS%:'"/>
                                <xsl:with-param name="value" select="$cssStylesheetKey"/>
                                <xsl:with-param name="exist" select="$cssStylesheetExist"/>
                                <xsl:with-param name="onchange">
                                    <xsl:text>javascript: updateCSS();</xsl:text>
                                </xsl:with-param>
                            </xsl:call-template>

                               <tr id="docRow">

                                <td width="120" class="form_labelcolumn" valign="top" nowrap="nowrap" >%blockDocument%:</td>
                                <td colspan="2">
                                    <xsl:variable name="documentcontent">
                                        <xsl:choose>
                                            <xsl:when test="/pagetemplates/pagetemplate/pagetemplatedata/document/@mode = 'xhtml'">
                                                <xsl:copy-of select="/pagetemplates/pagetemplate/pagetemplatedata/document"/>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:value-of select="/pagetemplates/pagetemplate/pagetemplatedata/document"/>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:variable>

                                    <xsl:variable name="css">
                                      <xsl:choose>
                                        <xsl:when test="$cssStylesheetExist = 'true'">
                                          <xsl:value-of select="$cssStylesheetKey"/>
                                        </xsl:when>
                                        <xsl:when test="$defaultcssExist = 'true'">
                                          <xsl:value-of select="$defaultcsskey"/>
                                        </xsl:when>
                                        <xsl:otherwise/>
                                      </xsl:choose>
                                    </xsl:variable>

                                    <xsl:call-template name="xhtmleditor">
                                      <xsl:with-param name="id" select="'contentdata_body'"/>
                                      <xsl:with-param name="name" select="'contentdata_body'"/>
                                      <xsl:with-param name="content" select="$documentcontent"/>
                                      <xsl:with-param name="configxpath" select="/pagetemplates/htmleditorconfig"/>
                                      <xsl:with-param name="config" select="'document'"/>
                                      <xsl:with-param name="customcss" select="string($css)"/>
                                      <xsl:with-param name="helpelement"/>
                                      <xsl:with-param name="menukey" select="$menukey"/>
                                      <xsl:with-param name="disabled" select="false()"/>
                                      <xsl:with-param name="accessToHtmlSource" select="$accessToHtmlSource = 'true'"/>
                                      <xsl:with-param name="classfilter" select="true()"/>
                                    </xsl:call-template>

                                </td>
                            </tr>            

							<tr id="contentRow">
								<xsl:if test="not(/pagetemplates/pagetemplate/@type) or (/pagetemplates/pagetemplate/@type != 'content' and /pagetemplates/pagetemplate/@type != 'sectionpage')">
                                    <xsl:attribute name="style">display: none</xsl:attribute>
                                </xsl:if>
								<td valign="top">
									%fldAllowedContentTypes%:
								</td>
								<td>
									<table border="0" cellspacing="2" cellpadding="0">
										<tr>
											<td>
												<div style="padding-bottom: 1em;">
													%fldAvailableContentTypes%:
												</div>



												<select multiple="multiple" style="width: 13em; height: 10em;" name="availablect" id="availablect">
													<xsl:for-each select="/pagetemplates/contenttypes/contenttype">
														<xsl:sort select="name"/>

														<xsl:variable name="varkey">
															<xsl:value-of select="@key"/>
														</xsl:variable>

														<xsl:if test="not(/pagetemplates/pagetemplate/contenttypes/contenttype[@key=$varkey])">
															<option value="{@key}" ondblclick="moveOptions('availablect', 'contenttypekey');"><xsl:value-of select="name"/></option>
														</xsl:if>

													</xsl:for-each>
												</select>
											</td>

											<td style="padding: 0.5em;">
												<xsl:call-template name="button">
													<xsl:with-param name="type" select="'button'"/>
													<xsl:with-param name="image" select="'images/icon_move_right.gif'"/>
													<xsl:with-param name="onclick">
														<xsl:text>javascript:moveOptions('availablect', 'contenttypekey');</xsl:text>
													</xsl:with-param>
												</xsl:call-template>
												<br/>
												<xsl:call-template name="button">
													<xsl:with-param name="type" select="'button'"/>
													<xsl:with-param name="image" select="'images/icon_move_left.gif'"/>
													<xsl:with-param name="onclick">
														<xsl:text>javascript:moveOptions('contenttypekey', 'availablect');</xsl:text>
													</xsl:with-param>
												</xsl:call-template>
											</td>

											<td>
												<div style="padding-bottom: 1em;">
													%fldSelectedContentTypes%:
												</div>

												<select multiple="multiple" style="width: 13em; height: 10em;" name="contenttypekey" id="contenttypekey">
													<xsl:for-each select="/pagetemplates/contenttypes/contenttype">
														<xsl:sort select="name"/>

														<xsl:variable name="varkey">
															<xsl:value-of select="@key"/>
														</xsl:variable>

														<xsl:if test="/pagetemplates/pagetemplate/contenttypes/contenttype[@key=$varkey]">
															<option value="{@key}" ondblclick="moveOptions('contenttypekey', 'availablect');"><xsl:value-of select="name"/></option>
														</xsl:if>

													</xsl:for-each>
												</select>

											</td>
										</tr>
									</table>
								</td>
							</tr>

                        </table>
                        <script type="text/javascript">changeType(document.getElementById('type').value);</script>
                    </fieldset>

                  <fieldset>
                    <legend>&nbsp;%blockCaching%&nbsp;</legend>
                    <table cellspacing="2" cellpadding="2" border="0">
                      <tr>
                        <td class="form_labelcolumn" nowrap="nowrap">
                          %fldRunAs%:
                        </td>
                        <td>
                          <xsl:call-template name="dropdown_runas">
                            <xsl:with-param name="name" select="'runAs'"/>
                            <xsl:with-param name="selectedKey" select="/pagetemplates/pagetemplate/@runAs"/>
                            <xsl:with-param name="defaultRunAsUserName" select="$defaultRunAsUser"/>
                            <xsl:with-param name="inheritMessage" select="'%fldInherit%'"/>
                          </xsl:call-template>
                        </td>
                      </tr>
                    </table>
                  </fieldset>

                </div>

                <div class="tab-page" id="tab-page-2">
                    <span class="tab">%blockPageConfiguration%</span>

                    <script type="text/javascript" language="JavaScript">
                        tabPane1.addTabPage( document.getElementById( "tab-page-2" ) );

                        function updateStyleSheet() {
                            var selectedTabPage = tabPane1.getSelectedPage();
                            styleSheetKey = document.getElementById('stylesheetkey').value;
					        selectAllRowsInSelect('contenttypekey');
                            document.forms['formAdmin'].action = 'adminpage?page=' + <xsl:value-of select="$page"/> + '&amp;op=form&amp;selstylesheetkey=' + styleSheetKey + "&amp;selectedtabpageid=" + selectedTabPage + "&amp;updatestylesheet=true";
                            document.forms['formAdmin'].submit();
                        }

                    </script>

                    <fieldset>
                        <legend>&nbsp;%blockStylesheet%&nbsp;</legend>
                        <table border="0" cellspacing="0" cellpadding="2">
                        	<tr>

                            <xsl:call-template name="resourcefield">
	                                <xsl:with-param name="name" select="'stylesheetkey'"/>
	                                <xsl:with-param name="extension" select="'xsl'"/>
	                                <xsl:with-param name="mimetype" select="'text/xml'"/>
	                                <xsl:with-param name="label" select="'%fldSelectedStylesheet%:'"/>
	                                <xsl:with-param name="value" select="$selstylesheetkey"/>
	                                <xsl:with-param name="exist" select="$selstylesheetExist"/>
	                                <xsl:with-param name="valid" select="$selstylesheetValid"/>
	                                <xsl:with-param name="required" select="true()"/>
	                                <xsl:with-param name="removeButton" select="false()"/>
	                                <xsl:with-param name="reloadButton" select="true()"/>
	                                <xsl:with-param name="disableReloadButton" select="$styleSheetsExists"/>
                                  <xsl:with-param name="onchange">
	                                    <xsl:text>javascript: updateStyleSheet();</xsl:text>
	                                </xsl:with-param>
	                            </xsl:call-template>
                        	</tr>
                            <tr>
								<td>
									<img height="2" width="1" src="images/shim.gif"/>
								</td>
							</tr>
							<xsl:choose>
                                <xsl:when test="count(/pagetemplates/pagetemplate/pagetemplatedata/pagetemplateparameter) = 0">
                                    <tr><td>%msgNoParameters%</td></tr>
                                </xsl:when>
                                <xsl:otherwise>

                                    <xsl:choose>
                                        <xsl:when test="$create=1">
                                            <xsl:for-each select="/pagetemplates/pagetemplate/pagetemplatedata/pagetemplateparameter">
                                                <tr>
                                                    <input type="hidden">
                                                        <xsl:attribute name="name">
                                                            <xsl:text>parameter_name</xsl:text>
                                                        </xsl:attribute>
                                                        <xsl:attribute name="value">
                                                            <xsl:value-of select="@name"/>
                                                        </xsl:attribute>
                                                    </input>
                                                    <input type="hidden">
                                                        <xsl:attribute name="name">
                                                            <xsl:text>parameter_type</xsl:text>
                                                        </xsl:attribute>
                                                        <xsl:attribute name="value">
                                                            <xsl:value-of select="@type"/>
                                                        </xsl:attribute>
                                                    </input>
                                                    <xsl:choose>
                                                        <xsl:when test="@type = 'category'">
                                                            <xsl:variable name="function">
                                                              <xsl:text>paramIndex =</xsl:text>
                                                              <xsl:value-of select="position()"/>
                                                              <xsl:text>;</xsl:text>
                                                              <xsl:text>OpenNewCategorySelector(</xsl:text>
                                                              <xsl:text>-1, '', null, false, null, -1);</xsl:text>
                                                            </xsl:variable>
                                                            <xsl:variable name="removefunction">
                                                                <xsl:text>javascript: removeParameter(this)</xsl:text>
                                                            </xsl:variable>
                                                            <xsl:call-template name="searchfield">
                                                                <xsl:with-param name="name" select="'parameter_value'"/>
                                                                <xsl:with-param name="id" select="concat('parameter_value',position())"/>
                                                                <xsl:with-param name="label" select="concat(@name, ':')"/>
                                                                <xsl:with-param name="selectedkey" select="''"/>
                                                                <xsl:with-param name="selectnode" select="''"/>
                                                                <xsl:with-param name="size" select="'40'"/>
                                                                <xsl:with-param name="maxlength" select="'255'"/>
                                                                <xsl:with-param name="buttonfunction" select="$function"/>
                                                                <xsl:with-param name="removefunction" select="$removefunction"/>
                                                                <xsl:with-param name="colspan" select="'1'"/>
                                                            </xsl:call-template>
                                                        </xsl:when>
                                                        <xsl:when test="@type = 'page'">
                                                            <xsl:variable name="function">
                                                              <xsl:text>javascript: OpenSelectorWindowForPages( this, 850, 'parameter_value</xsl:text><xsl:value-of select="position()"/><xsl:text>', 'viewparameter_value</xsl:text><xsl:value-of select="position()"/><xsl:text>', 260, 360 )</xsl:text>
                                                            </xsl:variable>
                                                            <xsl:variable name="removefunction">
                                                                <xsl:text>javascript: removeParameter(this)</xsl:text>
                                                            </xsl:variable>
                                                            <xsl:call-template name="searchfield">
                                                                <xsl:with-param name="name" select="'parameter_value'"/>
                                                                <xsl:with-param name="id" select="concat('parameter_value',position())"/>
                                                                <xsl:with-param name="label" select="concat(@name, ':')"/>
                                                                <xsl:with-param name="selectedkey" select="''"/>
                                                                <xsl:with-param name="selectnode" select="''"/>
                                                                <xsl:with-param name="size" select="'40'"/>
                                                                <xsl:with-param name="maxlength" select="'255'"/>
                                                                <xsl:with-param name="buttonfunction" select="$function"/>
                                                                <xsl:with-param name="removefunction" select="$removefunction"/>
                                                                <xsl:with-param name="colspan" select="'1'"/>
                                                            </xsl:call-template>
                                                        </xsl:when>
                                                        <xsl:when test="@type = 'resource'">
                                                          <input type="hidden" name="viewparameter_value" value=""/>
                                                        	<xsl:call-template name="resourcefield">
                                                            <xsl:with-param name="name" select="'parameter_value'"/>
                                                            <xsl:with-param name="id" select="concat('parameter_value',position())"/>
                                                            <!--xsl:with-param name="extension" select="'xsl'"/>
                                                            <xsl:with-param name="mimetype" select="'text/xml'"/-->
                                                            <xsl:with-param name="label" select="concat(@name, ':')"/>
                                                            <xsl:with-param name="value" select="''"/>
                                                            <xsl:with-param name="position" select="position()"/>
                                                            <!--xsl:with-param name="exist" select="$selstylesheetExist"/>
                                                            <xsl:with-param name="valid" select="$selstylesheetValid"/>
                                                            <xsl:with-param name="required" select="true()"/>
                                                            <xsl:with-param name="onchange">
                                                                <xsl:text>javascript: updateStyleSheet();</xsl:text>
                                                            </xsl:with-param-->
                                                            </xsl:call-template>
                                                        </xsl:when>
                                                        <xsl:otherwise>
															                              <xsl:variable name="stylesheet" select="/pagetemplates/resource"/>
															                              <xsl:variable name="name" select="@name"/>
                                                            <xsl:variable name="postfield">
                                                              <xsl:if test="$stylesheet/xsl:stylesheet/xsl:param[@name = $name]/@as">
                                                                <xsl:text>&nbsp;&nbsp;Type: </xsl:text>
                                                                  <xsl:value-of select="substring-after($stylesheet/xsl:stylesheet/xsl:param[@name = $name]/@as,'xs:')"/>,
                                                              </xsl:if>
                                                              <xsl:if test="not($stylesheet/xsl:stylesheet/xsl:param[@name = $name]/@as)">
                                                                <xsl:text>&nbsp;&nbsp;</xsl:text>
                                                              </xsl:if>
                                                                <xsl:if test="$stylesheet/xsl:stylesheet/xsl:param[@name = $name]/@select">
                                                                    <xsl:text>%fldDefault%: </xsl:text>
                                                                    <xsl:value-of select="$stylesheet/xsl:stylesheet/xsl:param[@name = $name]/@select"/>
                                                                </xsl:if>
                                                            </xsl:variable>
                                                            <xsl:call-template name="textfield">
                                                                <xsl:with-param name="name" select="'parameter_value'"/>
                                                                <xsl:with-param name="label" select="concat(@name, ':')"/>
                                                                <xsl:with-param name="selectnode" select="''"/>
                                                                <xsl:with-param name="size" select="'40'"/>
                                                                <xsl:with-param name="maxlength" select="'255'"/>
                                                                <xsl:with-param name="colspan" select="'1'"/>
                                                                <xsl:with-param name="lefttdwidth" select="'120'"/>
                                                                <xsl:with-param name="postfield" select="$postfield"/>
                                                            </xsl:call-template>
                                                            <input type="hidden" name="viewparameter_value" value="dummy"/>
                                                            <input type="hidden" name="btnparameter_value" value="dummy"/>
                                                            <input type="hidden" name="removeparameter_value" value="dummy"/>
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </tr>
                                            </xsl:for-each>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:for-each select="/pagetemplates/pagetemplate/pagetemplatedata/pagetemplateparameter">
                                                <tr>
                                                    <input type="hidden">
                                                        <xsl:attribute name="name">
                                                            <xsl:text>parameter_name</xsl:text>
                                                        </xsl:attribute>
                                                        <xsl:attribute name="value">
                                                            <xsl:value-of select="@name"/>
                                                        </xsl:attribute>
                                                    </input>
                                                    <input type="hidden">
                                                        <xsl:attribute name="name">
                                                            <xsl:text>parameter_type</xsl:text>
                                                        </xsl:attribute>
                                                        <xsl:attribute name="value">
                                                            <xsl:value-of select="@type"/>
                                                        </xsl:attribute>
                                                    </input>
                                                    <xsl:choose>
                                                        <xsl:when test="@type = 'category'">
                                                            <xsl:variable name="function">
                                                              <xsl:text>paramIndex =</xsl:text>
                                                              <xsl:value-of select="position()"/>
                                                              <xsl:text>;</xsl:text>
                                                              <xsl:text>OpenNewCategorySelector(</xsl:text>
                                                              <xsl:text>-1, '', null, false, null, -1);</xsl:text>
                                                            </xsl:variable>
                                                            <xsl:variable name="removefunction">
                                                                <xsl:text>javascript: removeParameter(this)</xsl:text>
                                                            </xsl:variable>
                                                            <xsl:call-template name="searchfield">
                                                                <xsl:with-param name="name" select="'parameter_value'"/>
                                                                <xsl:with-param name="id" select="concat('parameter_value', position())"/>
                                                                <xsl:with-param name="label" select="concat(@name, ':')"/>
                                                                <xsl:with-param name="selectedkey" select="@value"/>
                                                                <xsl:with-param name="selectnode" select="@valuename"/>
                                                                <xsl:with-param name="size" select="'40'"/>
                                                                <xsl:with-param name="maxlength" select="'255'"/>
                                                                <xsl:with-param name="buttonfunction" select="$function"/>
                                                                <xsl:with-param name="removefunction" select="$removefunction"/>
                                                                <xsl:with-param name="colspan" select="'1'"/>
                                                            </xsl:call-template>
                                                        </xsl:when>
                                                        <xsl:when test="@type = 'page'">
                                                            <xsl:variable name="function">
                                                              <xsl:text>javascript: OpenSelectorWindowForPages( this, 850, 'parameter_value</xsl:text><xsl:value-of select="position()"/><xsl:text>', 'viewparameter_value</xsl:text><xsl:value-of select="position()"/><xsl:text>', 260, 360 )</xsl:text>
                                                            </xsl:variable>
                                                            <xsl:variable name="removefunction">
                                                                <xsl:text>javascript: removeParameter(this)</xsl:text>
                                                            </xsl:variable>
                                                            <xsl:call-template name="searchfield">
                                                                <xsl:with-param name="name" select="'parameter_value'"/>
                                                                <xsl:with-param name="id" select="concat('parameter_value', position())"/>
                                                                <xsl:with-param name="label" select="concat(@name, ':')"/>
                                                                <xsl:with-param name="selectedkey" select="@value"/>
                                                                <xsl:with-param name="selectnode" select="@valuename"/>
                                                                <xsl:with-param name="size" select="'40'"/>
                                                                <xsl:with-param name="maxlength" select="'255'"/>
                                                                <xsl:with-param name="buttonfunction" select="$function"/>
                                                                <xsl:with-param name="removefunction" select="$removefunction"/>
                                                                <xsl:with-param name="colspan" select="'1'"/>
                                                            </xsl:call-template>
                                                        </xsl:when>
                                                        <xsl:when test="@type = 'resource'">
                                                        	<input type="hidden" name="viewparameter_value" value=""/>
                                                        	<xsl:call-template name="resourcefield">
                                                              <xsl:with-param name="name" select="'parameter_value'"/>
                                                              <xsl:with-param name="id" select="concat('parameter_value',position())"/>
                                                              <xsl:with-param name="label" select="concat(@name, ':')"/>
                                                              <xsl:with-param name="value" select="@value"/>
                                                              <xsl:with-param name="position" select="position()"/>
                                                          </xsl:call-template>
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            <xsl:variable name="stylesheet" select="/pagetemplates/resource"/>
															                              <xsl:variable name="name" select="@name"/>
                                                            <xsl:variable name="postfield">
                                                                <xsl:if test="$stylesheet/xsl:stylesheet/xsl:param[@name = $name]/@as">
                                                                  <xsl:text>&nbsp;&nbsp;Type: </xsl:text>
                                                                    <xsl:value-of select="substring-after($stylesheet/xsl:stylesheet/xsl:param[@name = $name]/@as,'xs:')"/>,
                                                                </xsl:if>
                                                                <xsl:if test="not($stylesheet/xsl:stylesheet/xsl:param[@name = $name]/@as)">
                                                                  <xsl:text>&nbsp;&nbsp;</xsl:text>
                                                                </xsl:if>
                                                                <xsl:if test="$stylesheet/xsl:stylesheet/xsl:param[@name = $name]/@select">
                                                                    <xsl:text>%fldDefault%: </xsl:text>
                                                                  <xsl:value-of select="$stylesheet/xsl:stylesheet/xsl:param[@name = $name]/@select"/>
                                                                </xsl:if>
                                                            </xsl:variable>
                                                            <xsl:call-template name="textfield">
                                                                <xsl:with-param name="name" select="'parameter_value'"/>
                                                                <xsl:with-param name="label" select="concat(@name, ':')"/>
                                                                <xsl:with-param name="selectnode" select="@value"/>
                                                                <xsl:with-param name="size" select="'40'"/>
                                                                <xsl:with-param name="maxlength" select="'255'"/>
                                                                <xsl:with-param name="colspan" select="'1'"/>
                                                                <xsl:with-param name="lefttdwidth" select="'120'"/>
                                                                <xsl:with-param name="postfield" select="$postfield"/>
                                                            </xsl:call-template>
                                                            <input type="hidden" name="viewparameter_value" value="dummy"/>
                                                            <input type="hidden" name="btnparameter_value" value="dummy"/>
                                                            <input type="hidden" name="removeparameter_value" value="dummy"/>
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </tr>
                                            </xsl:for-each>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:otherwise>
                            </xsl:choose>
                        </table>
                    </fieldset>
                    <xsl:call-template name="objectplacement">
                        <xsl:with-param name="rootElem" select="/pagetemplates/pagetemplate"/>
                    </xsl:call-template>

                </div>

                <div class="tab-page" id="tab-page-4">
                    <span class="tab">%blockDatasource%</span>

                    <script type="text/javascript" language="JavaScript">
                        tabPane1.addTabPage( document.getElementById( "tab-page-4" ) );
                        function previewDataSource() {
                          tinyMCE.triggerSave();
                          document.forms['formAdminDataSource'].datasources.value = codeArea_datasources.getCode();
                          document.forms['formAdminDataSource'].submit();
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
                              <xsl:with-param name="selectnode" select="$datasources"/>
                              <xsl:with-param name="buttons" select="'find,replace,indentall,indentselection,gotoline'"/>
                              <xsl:with-param name="status-bar" select="true()"/>
                            </xsl:call-template>
                          </tr>
                          <tr>
                            <td>
                              <xsl:call-template name="button">
                                <xsl:with-param name="type" select="'button'"/>
                                <xsl:with-param name="caption" select="'%cmdViewDatasource%'"/>
                                <xsl:with-param name="name" select="'datasourcepreview'"/>
                                <xsl:with-param name="onclick" select="'javascript:previewDataSource();'"/>
                              </xsl:call-template>
                            </td>
                          </tr>
                        </table>
                    </fieldset>
                </div>

                <div class="tab-page" id="tab-page-5">
                    <span class="tab">%blockUsedBy%</span>

                    <script type="text/javascript" language="JavaScript">
                        tabPane1.addTabPage( document.getElementById( "tab-page-5" ) );
                    </script>

                    <fieldset>
                        <legend>&nbsp;%blockFrameworkUsedByPage%&nbsp;</legend>

                        <xsl:choose>
                          <xsl:when test="count(/pagetemplates/menuitems/menuitem) != 0">
                            <table border="0" cellspacing="2" cellpadding="2">
                                <xsl:for-each select="/pagetemplates/menuitems/menuitem">
                                    <tr><td>&nbsp;&nbsp;<xsl:value-of select="name"/></td></tr>
                                </xsl:for-each>
                            </table>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:text>%msgNoMenuItemsIsUsingThisPageTemplate%</xsl:text>
                          </xsl:otherwise>
                        </xsl:choose>
                    </fieldset>
                </div>

            </div>

            <script type="text/javascript" language="JavaScript">
                <xsl:choose>
                    <xsl:when test="$selectedtabpageid != 'none'">
                        setupAllTabs();
                        tabPane1.setSelectedPage("<xsl:value-of select="$selectedtabpageid"/>");
                    </xsl:when>
                    <xsl:otherwise>
                        setupAllTabs();
                    </xsl:otherwise>
                </xsl:choose>
            </script>

    </xsl:template>

    <xsl:template name="dropdown_refresh">
        <xsl:param name="label" select="''"/>
        <xsl:param name="name"/>
        <xsl:param name="selectedkey"/>
        <xsl:param name="selectnode"/>
        <xsl:param name="colspan"/>
        <xsl:param name="emptyrow"/>
        <xsl:param name="onchangefunction" select="''"/>

        <xsl:if test="$label != ''">
            <td><xsl:value-of select="$label"/></td>
        </xsl:if>
        <td>
            <select>
                <xsl:attribute name="onchange">
                    <xsl:choose>
                        <xsl:when test="$onchangefunction = ''">
                            <xsl:text>javascript: window.location.href = 'adminpage?page=</xsl:text>
                            <xsl:value-of select="$page"/>
                            <xsl:text>&amp;op=form&amp;selstylesheetkey=' + this.value + '</xsl:text>
                            <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
                            <xsl:text>';</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$onchangefunction"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>

                <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>

                <xsl:if test="$emptyrow!=''">
                    <option value=""><xsl:value-of select="$emptyrow"/></option>
                </xsl:if>

                <xsl:for-each select="$selectnode">
                    <option>
                        <xsl:if test="$selectedkey = @key">
                            <xsl:attribute name="selected">selected</xsl:attribute>
                        </xsl:if>
                        <xsl:attribute name="value"><xsl:value-of disable-output-escaping="yes" select="@key"/></xsl:attribute>
                        <xsl:value-of select="name"/>
                    </option>
                </xsl:for-each>
            </select>
        </td>
    </xsl:template>

    <xsl:template name="objectplacement">
        <xsl:param name="rootElem"/>

        <xsl:for-each select="$rootElem/pagetemplateparameters/pagetemplateparameter">
            <xsl:sort select="name" data-type="text" lang="en" order="ascending"/>
            <xsl:variable name="pagetemplateparameterkey" select="@key"/>
            <xsl:variable name="param_pos" select="position()"/>

            <fieldset>
                <legend>&nbsp;%blockPosition%: <xsl:value-of select="name"/></legend>
                <input type="hidden">
                    <xsl:attribute name="name">
                        <xsl:text>paramname</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="name"/>
                    </xsl:attribute>
                </input>
                <input type="hidden">
                    <xsl:attribute name="name">
                        <xsl:text>paramkey</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="@key"/>
                    </xsl:attribute>
                </input>
                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                    <tr>
                        <td class="form_labelcolumn">
                            <xsl:variable name="paramname" select="name"/>
                            <table width="100%" border="0">
                                <tr>
                                    <td>%fldDefaultPortlets%:</td>
                                    <td colspan="2">
                                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
											                    <tbody>
	                                            <xsl:attribute name="id">
	                                                <xsl:text>tbl</xsl:text>
	                                                <xsl:value-of select="concat($paramname, 'co')"/>
	                                            </xsl:attribute>
	                                            <xsl:attribute name="name">
	                                                <xsl:text>tbl</xsl:text>
	                                                <xsl:value-of select="concat($paramname, 'co')"/>
	                                            </xsl:attribute>
	                                            <xsl:choose>
	                                                <xsl:when test="not(/pagetemplates/pagetemplate/contentobjects/contentobject[@parameterkey = $pagetemplateparameterkey])">
	                                                    <tr>
	                                                        <xsl:call-template name="contentobjectselector_multi">
	                                                            <xsl:with-param name="name" select="concat($paramname, 'co')"/>
	                                                            <xsl:with-param name="selectedkey" select="''"/>
	                                                            <xsl:with-param name="selectnode" select="''"/>
	                                                            <xsl:with-param name="size" select="'25'"/>
	                                                            <xsl:with-param name="maxlength" select="'25'"/>
	                                                            <xsl:with-param name="colspan" select="'1'"/>
	                                                            <xsl:with-param name="disableup" select="true()"/>
	                                                            <xsl:with-param name="disabledown" select="true()"/>
	                                                            <xsl:with-param name="extraremovefunction">
	                                                                <xsl:text>activateDeactivateMultiple('_multiple</xsl:text>
	                                                                <xsl:value-of select="$param_pos"/>
	                                                                <xsl:text>', 'btn</xsl:text>
	                                                                <xsl:value-of select="concat($paramname, 'co')"/>
	                                                                <xsl:text>del');</xsl:text>
	                                                            </xsl:with-param>
	                                                        </xsl:call-template>
	                                                    </tr>
	                                                </xsl:when>
	                                                <xsl:otherwise>
	                                                    <xsl:for-each select="/pagetemplates/pagetemplate/contentobjects/contentobject[@parameterkey = $pagetemplateparameterkey]">
	                                                        <!--xsl:sort select="order"/-->
	                                                        <tr>
	                                                            <xsl:call-template name="contentobjectselector_multi">
	                                                                <xsl:with-param name="name" select="concat($paramname, 'co')"/>
	                                                                <xsl:with-param name="selectedkey" select="@conobjkey"/>
	                                                                <xsl:with-param name="selectnode" select="name"/>
	                                                                <xsl:with-param name="size" select="'25'"/>
	                                                                <xsl:with-param name="maxlength" select="'25'"/>
	                                                                <xsl:with-param name="colspan" select="'1'"/>
	                                                                <xsl:with-param name="objdoc" select="@objectdocument"/>
	                                                                <xsl:with-param name="disableup" select="position() = 1"/>
	                                                                <xsl:with-param name="disabledown" select="position() = last()"/>
	                                                                <xsl:with-param name="extraremovefunction">
	                                                                    <xsl:text>activateDeactivateMultiple('_multiple</xsl:text>
	                                                                    <xsl:value-of select="$param_pos"/>
	                                                                    <xsl:text>', 'btn</xsl:text>
	                                                                    <xsl:value-of select="concat($paramname, 'co')"/>
	                                                                    <xsl:text>del');</xsl:text>
	                                                                </xsl:with-param>
	                                                            </xsl:call-template>
	                                                        </tr>
	                                                    </xsl:for-each>
	                                                </xsl:otherwise>
	                                            </xsl:choose>
												                    </tbody>
	                                        </table>
	                                    </td>
	                                    <tr>
	                                        <td class="form_labelcolumn" nowrap="nowrap"/>
	                                        <td align="left" id="newbtntd" name="newbtntd">
	                                            <xsl:if test="not(@multiple) or @multiple != 1">
	                                                <xsl:attribute name="style">
	                                                    <xsl:text>display: none</xsl:text>
	                                                </xsl:attribute>
	                                            </xsl:if>
	                                            <xsl:call-template name="button">
	                                                <xsl:with-param name="name">add<xsl:value-of select="$paramname"/></xsl:with-param>
	                                                <xsl:with-param name="caption" select="'%cmdNewPortlet%'"/>
	                                                <xsl:with-param name="onclick">
	                                                    <xsl:text>addObjectSelector('</xsl:text>
	                                                    <xsl:value-of select="concat($paramname, 'co')"/>
	                                                    <xsl:text>',</xsl:text><xsl:value-of select="$param_pos -1"/><xsl:text>);</xsl:text>
	                                                    <xsl:text>activateDeactivateMultiple('_multiple</xsl:text>
	                                                    <xsl:value-of select="$param_pos"/>
	                                                    <xsl:text>', 'btn</xsl:text>
	                                                    <xsl:value-of select="concat($paramname, 'co')"/>
	                                                    <xsl:text>del');</xsl:text>
	                                                </xsl:with-param>
	                                            </xsl:call-template>
	                                        </td>
	                                    </tr>
	                                </tr>
	                                <tr>
	                                    <td class="form_labelcolumn" nowrap="nowrap">
	                                        %fldMultiplePortlets%:
	                                    </td>
	                                    <td align="left">
	                                        <input type="hidden">
	                                            <xsl:attribute name="name">
	                                                <xsl:text>multiple</xsl:text>
	                                                <xsl:value-of select="position()"/>
	                                            </xsl:attribute>
	                                            <xsl:choose>
	                                                <xsl:when test="@multiple = 1">
	                                                    <xsl:attribute name="value">
	                                                        <xsl:text>1</xsl:text>
	                                                    </xsl:attribute>
	                                                </xsl:when>
	                                                <xsl:otherwise>
	                                                    <xsl:attribute name="value">
	                                                        <xsl:text>0</xsl:text>
	                                                    </xsl:attribute>
	                                                </xsl:otherwise>
	                                            </xsl:choose>
	                                        </input>
	                                        <input type="checkbox" value="1" onclick="javascript:toggleSeparator(this);">
	                                            <xsl:attribute name="name">
	                                                <xsl:text>_multiple</xsl:text>
	                                                <xsl:value-of select="position()"/>
	                                            </xsl:attribute>
	                                            <xsl:if test="@multiple = 1">
	                                                <xsl:attribute name="checked">
	                                                    <xsl:text>checked</xsl:text>
	                                                </xsl:attribute>
	                                                <xsl:if test="count(/pagetemplates/pagetemplate/contentobjects/contentobject[@parameterkey = $pagetemplateparameterkey]) &gt; 1">
	                                                    <xsl:attribute name="disabled">
	                                                        <xsl:text>disabled</xsl:text>
	                                                    </xsl:attribute>
	                                                </xsl:if>
	                                            </xsl:if>
	                                        </input>
	                                    </td>
	                                </tr>
	                                <tr id="separatortr" name="separatortr">
	                                    <xsl:if test="not(@multiple) or @multiple != 1">
	                                        <xsl:attribute name="style">
	                                            <xsl:text>display: none</xsl:text>
	                                        </xsl:attribute>
	                                    </xsl:if>
	                                    <xsl:variable name="separatorinitial">
	                                    	<xsl:choose>
	                                    		<xsl:when test="separator">
	                                    			<xsl:value-of select="separator"/>
	                                    		</xsl:when>
	                                    		<xsl:otherwise>
	                                    			<xsl:text>&lt;br /&gt;</xsl:text>
	                                    		</xsl:otherwise>
	                                    	</xsl:choose>
	                                    </xsl:variable>
	                                    <xsl:call-template name="textarea">
	                                        <xsl:with-param name="name" select="'separator'"/>
	                                        <xsl:with-param name="label" select="'%fldPortletSeperator%:'"/>
	                                        <xsl:with-param name="selectnode" select="$separatorinitial"/>
	                                        <xsl:with-param name="rows" select="'2'"/>
	                                        <xsl:with-param name="cols" select="'25'"/>
	                                        <xsl:with-param name="colspan" select="'1'"/>
	                                    </xsl:call-template>
	                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </fieldset>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
