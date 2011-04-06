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

    <xsl:output method="html" doctype-public="-//W3C//DTD HTML 4.0 Transitional//EN"/>

    <xsl:include href="common/displayerror.xsl"/>
    <xsl:include href="common/serialize.xsl"/>
    <xsl:include href="common/checkbox_boolean.xsl"/>
    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/genericheader.xsl"/>
    <xsl:include href="common/textfield.xsl"/>
	  <xsl:include href="common/textarea.xsl"/>
    <xsl:include href="common/accessrights.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/searchfield.xsl"/>
    <xsl:include href="common/tablecolumnheader.xsl"/>
    <xsl:include href="editor/xhtmleditor.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>
    <xsl:include href="common/readonlydatetime.xsl"/>
    <xsl:include href="common/readonlyvalue.xsl"/>
    <xsl:include href="common/formatdate.xsl"/>
    <xsl:include href="common/dropdown_language.xsl"/>
    <xsl:include href="common/contentfield.xsl"/>
    <xsl:include href="common/contentobjectselector.xsl"/>
    <xsl:include href="common/waitsplash.xsl"/>
    <xsl:include href="common/dropdown_runas.xsl"/>

    <xsl:include href="formbuilder/formbuilder_js.xsl"/>

    <xsl:param name="key" select="'none'"/>
    <xsl:param name="type" select="'none'"/>
    <xsl:param name="insertbelow" select="'undefined'"/>
    <xsl:param name="contenttypekey" select="'none'"/>
    <xsl:param name="catselkey" select="'none'"/>
    <xsl:param name="selpagetemplatekey" select="''"/>
    <xsl:param name="forward_data" select="false()"/>
    <xsl:param name="name" select="''"/>
    <xsl:param name="closedunit" select="'false'"/>
    <xsl:param name="visibility" select="'off'"/>
    <xsl:param name="menu-name" select="''"/>
    <xsl:param name="displayname" select="''"/>
    <xsl:param name="description" select="''"/>
	  <xsl:param name="keywords" select="''"/>
    <xsl:param name="document" select="''"/>
    <xsl:param name="noauth" select="'notset'"/>
    <xsl:param name="catkey" select="'none'"/>
    <xsl:param name="catname" select="'none'"/>
    <xsl:param name="selectedtabpageid" select="'none'"/>
	  <xsl:param name="contenttitle"/>
    <xsl:param name="forward_shortcut" select="'false'"/>
    <xsl:param name="contenttypestring" select="''"/>
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

  <xsl:variable name="displayname-help-element">
    <help>%hlpMenuItemDisplayName%</help>
  </xsl:variable>
  <xsl:variable name="menuname-help-element">
    <help>%hlpMenuItemMenuName%</help>
  </xsl:variable>
  <xsl:variable name="name-help-element">
    <help>%hlpMenuItemName%</help>
  </xsl:variable>

  <xsl:variable name="key_quoted">
    <xsl:text>"</xsl:text><xsl:value-of select="$key"/><xsl:text>"</xsl:text>
  </xsl:variable>

  <xsl:variable name="text-auto-generated">
    <xsl:text>(Auto generated)</xsl:text>
  </xsl:variable>

  <xsl:variable name="menu" select="//menu[@key = $menukey]"/>
  <xsl:variable name="menuitem" select="//menuitem[@key = $key]"/>

  <xsl:variable name="parentmenuitem" select="//menuitem[@key = $insertbelow]"/>

  <xsl:variable name="contentobjectselector_multi_size" select="25"/>

  <xsl:variable name="parent-menu-item-key">
    <xsl:choose>
      <xsl:when test="$parentmenuitem/@key != ''">
        <xsl:value-of select="$parentmenuitem/@key"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>-1</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="menu-item-key">
    <xsl:choose>
      <xsl:when test="$key != 'none'">
        <xsl:value-of select="$key"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>-1</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <!-- Find out if the user have administrate rights -->
  <xsl:variable name="user_have_administrate_rights">
    <xsl:choose>
      <xsl:when test="$menuitem">
        <xsl:value-of select="not($menuitem/accessrights/userright) or $menuitem/accessrights/userright/@administrate = 'true'"/>
      </xsl:when>
      <!--xsl:when test="$parentmenuitem">
                <xsl:value-of select="not($parentmenuitem/accessrights/userright) or $parentmenuitem/accessrights/userright/@administrate = 'true'"/>
            </xsl:when-->
            <xsl:otherwise>
                <xsl:value-of select="not(/menus/accessrights/userright) or /menus/accessrights/userright/@administrate = 'true'"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <!-- Find out if we shall disable the save button -->
    <xsl:variable name="save_disabled">
        <xsl:choose>
            <xsl:when test="not($menuitem)">false</xsl:when>
            <xsl:when test="not($menuitem/accessrights/userright)">false</xsl:when>
            <xsl:when test="$menuitem/accessrights/userright/@update = 'true'">false</xsl:when>
            <xsl:when test="$menuitem/accessrights/userright/@administrate = 'true'">false</xsl:when>
            <xsl:otherwise>true</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="selected_type">
        <xsl:choose>
            <xsl:when test="($menuitem/@type = 'url' and boolean($menuitem/url[@local = 'yes']) and $type = 'none') or $type = 'localurl'">
                <xsl:text>localurl</xsl:text>
            </xsl:when>
            <xsl:when test="($menuitem/@type = 'url' and boolean($menuitem/url[@local = 'no']) and $type = 'none') or $type = 'url'">
                <xsl:text>externalurl</xsl:text>
            </xsl:when>
            <xsl:when test="($menuitem/@type = 'section' and $type = 'none') or $type = 'section'">
                <xsl:text>section</xsl:text>
            </xsl:when>
            <xsl:when test="($menuitem/@type = 'shortcut' and $type = 'none') or $type = 'shortcut'">
                <xsl:text>shortcut</xsl:text>
            </xsl:when>
            <xsl:when test="boolean($menuitem) and $type = 'none'">
                <xsl:value-of select="$menuitem/@type"/>
            </xsl:when>
            <xsl:when test="$type != 'none'">
                <xsl:value-of select="$type"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>none</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <!-- Find out if we shall disable the preview button -->
    <xsl:variable name="preview_disabled">
        <xsl:choose>
            <xsl:when test="$save_disabled = 'true'">true</xsl:when>
            <xsl:when test="$menuitem/@type = 'label' or $menuitem/@type = 'url' or $menuitem/@type = 'section'">true</xsl:when>
            <xsl:when test="$selected_type = 'externalurl' or $selected_type = 'label'">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="selpagetemplatekey2">
        <xsl:choose>
            <xsl:when test="$selpagetemplatekey!=''"><xsl:value-of select="$selpagetemplatekey"/></xsl:when>
            <xsl:when test="$menuitem[1]/page/@pagetemplatekey"><xsl:value-of select="$menuitem[1]/page/@pagetemplatekey"/></xsl:when>
            <xsl:when test="$insertbelow = 'undefined'">
                <xsl:value-of select="'-1'"/>
            </xsl:when>
            <xsl:otherwise>-2</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="pagetemplatetype" select="/menus/pagetemplates/pagetemplate[@key = $selpagetemplatekey2]/@type"/>

    <xsl:variable name="sectionmode" select="( ($pagetemplatetype = 'sectionpage' or $pagetemplatetype= 'newsletter') and $selected_type != 'externalurl' and $selected_type != 'shortcut' and $selected_type != 'label' and $selected_type != 'section') or $selected_type = 'section'"/>

    <xsl:variable name="create">
        <xsl:choose>
            <xsl:when test="boolean($menuitem[1]/page)">0</xsl:when>
            <xsl:otherwise>1</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="hideContentKey" select="$forward_data or (not($forward_data) and not($menuitem/@contentkey))"/>

    <xsl:template match="/">

    	<xsl:variable name="temp">
			<xsl:copy-of select="$menu"/>
			<xsl:copy-of select="$menuitem"/>
		</xsl:variable>

        <html>
            <head>
                <link href="css/admin.css" rel="stylesheet" type="text/css"/>
                <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>

                <script type="text/javascript" src="javascript/admin.js">//</script>
                <script type="text/javascript" src="javascript/accessrights.js">//</script>
                <script type="text/javascript" src="javascript/validate.js">//</script>
                <script type="text/javascript" src="javascript/tabpane.js">//</script>
                <script type="text/javascript" src="javascript/properties.js">//</script>
                <script type="text/javascript" src="javascript/menu_item_form.js">//</script>
                <script type="text/javascript" src="tinymce/jscripts/cms/Util.js">//</script>
                <script type="text/javascript" src="tinymce/jscripts/cms/Editor.js">//</script>
                <script type="text/javascript" src="tinymce/jscripts/tiny_mce/tiny_mce.js">//</script>

                <xsl:call-template name="waitsplash"/>

                <script type="text/javascript">
                    var tabPane = null;

                    var contentObjectList = new Object();
                    <xsl:for-each select="$menuitem/page/contentobjects/contentobject">
                        <xsl:text>contentObjectList['</xsl:text><xsl:value-of select="@conobjkey"/><xsl:text>']</xsl:text> = 'added';
                    </xsl:for-each>

                    function callback_newCategorySelector(categoryKey, categoryName)
					          {
                      document.getElementById("category_key").value = categoryKey;
                      document.getElementById("viewcategory_key").value = categoryName;
                    }

                    function removeContentObject( objThis, name )
                    {
          						count = itemcount(document.forms['formAdmin'][objThis.name]);
                        if( count == 1 )
                        {
                          var field;
                          if (document.forms['formAdmin'][name][0])
                          {
                            field = document.forms['formAdmin'][name][0];
                          }
                          else
                          {
                            field = document.forms['formAdmin'][name];
                          }

                          contentObjectList[field.value] = 'removed';
                          field.value = "";
                          document.forms['formAdmin']['view' + name].value = "";

                          return;
                        }

                        var index = GetCurrentContentObjectIndex(objThis,objThis.name);
                        contentObjectList[document.forms['formAdmin'][name][index].value] = 'removed';
                        document.getElementById("tbl" + name).deleteRow(index + 1);
						            enableDisableButtons( name );
                    }

                    function removeContentObjectSingle( name )
                    {
                      contentObjectList[document.formAdmin[name].value] = 'removed';
                      document.forms['formAdmin'][name].value = "";
                      document.forms['formAdmin']['view' + name].value = "";
                    }

                    function GetCurrentContentObjectIndex(objThis,ObjName)
                    {
                      var lNumRows = itemcount(document.getElementsByName(ObjName))

                      if( lNumRows > 1 )
                      {
                        for( var i=0; i &lt; lNumRows; i++ )
                        {
                          if( document.getElementsByName(ObjName)[i] == objThis )
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

                    function reloadPage()
                    {
                      var selectedPage = -1;
                      if (tabPane)
                      {
                        selectedpage = tabPane.getSelectedPage();
                      }
                      else
                      {
                        keepPane = false;
                      }

                      document.getElementById('formAdmin').setAttribute("action", <xsl:text>"adminpage?op=form&amp;forward_data=true&amp;selectedtabpageid=" + selectedPage + "&amp;page="+ "</xsl:text><xsl:value-of select="$page"/><xsl:text>&amp;selpagetemplatekey=</xsl:text><xsl:value-of select="$selpagetemplatekey2"/><xsl:text>");</xsl:text>
                      document.forms['formAdmin'].submit();
                    }

                    function reloadPageWithPageFramework( templateKey, keepPane )
                    {

                      var selectedPage = -1;
                      if (tabPane)
                      {
                        selectedpage = tabPane.getSelectedPage();
                      }
                      else
                      {
                        keepPane = false;
                      }

                      if (keepPane)
                      {
                        document.getElementById('formAdmin').setAttribute("action", <xsl:text>"adminpage?op=form&amp;forward_data=true&amp;selpagetemplatekey=" + templateKey +"&amp;selectedtabpageid="+ selectedPage</xsl:text>);
                      }
                      else
                      {
                        document.getElementById('formAdmin').setAttribute("action", <xsl:text>"adminpage?op=form&amp;forward_data=true&amp;selpagetemplatekey=" + templateKey</xsl:text>);
                      }

                      <xsl:if test="$type = 'content' and not($selpagetemplatekey2 = -2)">
                      var f = document.forms['formAdmin'];
                      </xsl:if>

                      document.forms['formAdmin'].submit();
                    }

                    function unitChanged()
          					{
                    	var newUnit = getSelected('unit').value;
					            var newType = getSelected('type').value;
						          document.forms['formAdmin'].setAttribute("action", <xsl:text>"adminpage?op=form&amp;page="+ "</xsl:text><xsl:value-of select="$page"/><xsl:text>&amp;unitkey=" + newUnit </xsl:text>);
						          document.forms['formAdmin'].submit();
					          }

                    function contentTypeChanged()
                    {
                      var newUnit = getSelected('unit').value;
                      var newType = getSelected('type').value;
                      var newCT = getSelected('contenttype').value;
                      document.forms['formAdmin'].setAttribute("action", <xsl:text>"adminpage?op=form&amp;page="+ "</xsl:text><xsl:value-of select="$page"/><xsl:text>&amp;unitkey=" + newUnit + "&amp;contenttype="+ newCT</xsl:text>);
                      document.forms['formAdmin'].submit();
                    }

                  function getSelected(name)
                  {
                    for (var i = 0; i &lt; document.getElementsByName[name].length; i++)
                    {
                      if (document.getElementsByName[name][i].selected)
                      {
                        return document.getElementsByName[name][i];
                        break;
                      }
                    }
                  }

                  function OpenSelectorWindow( page, width, height )
                  {
                    var l = (screen.width - width) / 2;
                    var t = (screen.height - height) / 2;

                    if (page == '1028')
                    {
                      newWindow = window.open("adminpage?page=" + page + "&amp;op=selectdoc&amp;returnkey=content_key&amp;returnview=content_name", "Preview", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" + height + ",top=" + t + ", left=" + l);
                    }
                    else
                    {
                      newWindow = window.open("adminpage?page=" + page + "&amp;op=select&amp;subop=top", "Preview", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" + height + ", top=" + t + ", left=" + l);
                    }
                    newWindow.focus();
                  }

                  function OpenSelectorWindowForObjects(objThis, fieldName)
                  {
                    var menuKey = <xsl:value-of select="$menukey"/>;
                    var fieldRow = GetCurrentContentObjectIndex(objThis,objThis.name);

                    OpenObjectBrowsePopup(menuKey, fieldName, fieldRow);
                }

                function OpenSelectorWindowForPages( objThis, page, keyname, viewname, width, height)
                {
                  var l = (screen.width - width) / 2;
                  var t = (screen.height - height) / 2;

                  newWindow = window.open("adminpage?page=" + page + "&amp;op=select&amp;returnkey=" + keyname + "&amp;returnview=" + viewname +"&amp;menukey="+ <xsl:value-of select="$menukey"/>, "Preview", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" + height + ",top=" + t + ",left=" + l);
                  newWindow.focus();
                }

                function removeShortcut()
                {
                  document.forms['formAdmin']['shortcut'].value = '';
                  document.forms['formAdmin']['viewshortcut'].value = '';
                }

				    function clearNewRow(tableName, fields)
					  {
						  var row = document.getElementsByName(fields[0]).length - 1;
              for (var i = 0; i &lt; fields.length; ++i)
						  {
							  document.getElementsByName(fields[i])[row].value = "";
              }
            }

            function removeParamObject( body, objThis, name )
            {
              var tbody = document.getElementById(body);
              var selectedTr = objThis.parentNode.parentNode;

              if (tbody.rows.length == 1)
              {
                document.getElementsByName('paramname')[1].value = '';
                document.getElementsByName('paramval')[1].value = '';
                document.getElementsByName('paramoverride')[1].selectedIndex = 0;
              }
              else
              {
                tbody.deleteRow(selectedTr.rowIndex);
              }
					  }

            function addObjectSelector( name )
            {
              var body = document.getElementById('tbl' + name);
              var sourceRow = body.rows[1];
              var destRow = sourceRow.cloneNode(true);
              body.appendChild(destRow);
              var lastIndex = body.rows.length;

						  // -2 because the first row is the position object separator.

              document.forms['formAdmin'][name][lastIndex-2].value = '';
              document.forms['formAdmin']['view' + name][lastIndex-2].value = '';
              enableDisableButtons( name );
            }

            function enableDisableButtons( name )
            {
              if( document.getElementsByName(name) == null )
              {
                return;
              }

              var count = itemcount(document.getElementsByName(name));
              if( count == 1 )
              {
                if( document.getElementsByName('btn'+name+'up') != null )
                {
                  if( document.getElementsByName('btn'+name+'up').length == 1 )
                  {
                      setImageButtonEnabled( document.getElementsByName('btn'+name+'up')[0], false );
                      setImageButtonEnabled( document.getElementsByName('btn'+name+'down')[0], false );

                  }
                  else
                  {
                      setImageButtonEnabled( document.getElementsByName('btn'+name+'down'), false );
                      setImageButtonEnabled( document.getElementsByName('btn'+name+'up'), false );
                  }
                }
              }
              else
              {
                for( var i=0; i &lt; count; i++ )
                {
                  setImageButtonEnabled( document.getElementsByName('btn'+name+'down')[i], true );
                  setImageButtonEnabled( document.getElementsByName('btn'+name+'up')[i], true );
                }

                // Handle first

                setImageButtonEnabled( document.getElementsByName('btn'+name+'up')[0], false );
                setImageButtonEnabled( document.getElementsByName('btn'+name+'down')[0], true );

                // Handle last

                setImageButtonEnabled( document.getElementsByName('btn'+name+'up')[count-1], true );
                setImageButtonEnabled( document.getElementsByName('btn'+name+'down')[count-1], false );

              }
            }

            function moveUp( col, objBtn )
            {
              var index = GetCurrentContentObjectIndex(objBtn,objBtn.name);

              var tempKey = document.formAdmin[col][index-1].value;
              var tempView = document.formAdmin['view' + col][index-1].value;
              var tempObjdoc = document.formAdmin[col + 'objdoc'][index-1].value;

              document.formAdmin[col + 'objdoc'][index-1].value = document.formAdmin[col + 'objdoc'][index].value;
              document.formAdmin[col][index-1].value = document.formAdmin[col][index].value;
              document.formAdmin['view' + col][index-1].value = document.formAdmin['view' + col][index].value;

              document.formAdmin[col + 'objdoc'][index].value = tempObjdoc;
              document.formAdmin[col][index].value = tempKey;
              document.formAdmin['view' + col][index].value = tempView;

              enableDisableButtons( col );
            }

            function moveDown( col, objBtn )
            {
              var index = GetCurrentContentObjectIndex(objBtn,objBtn.name);

              var tempKey = document.formAdmin[col][index].value;
              var tempView = document.formAdmin['view' + col][index].value;
              var tempObjdoc = document.formAdmin[col + 'objdoc'][index].value;

              document.formAdmin[col + 'objdoc'][index].value = document.formAdmin[col + 'objdoc'][index+1].value;
              document.formAdmin[col][index].value = document.formAdmin[col][index+1].value;
              document.formAdmin['view' + col][index].value = document.formAdmin['view' + col][index+1].value;

              document.formAdmin[col + 'objdoc'][index+1].value = tempObjdoc;
              document.formAdmin[col][index+1].value = tempKey;
              document.formAdmin['view' + col][index+1].value = tempView;

              enableDisableButtons( col );
            }

            var validatedFields = new Array();
            var valIdx = 0;

            // Add displayname to validation
            validatedFields[valIdx++] = new Array("%fldDisplayName%", "displayname", validateRequired);

            validatedFields[valIdx++] = new Array("%fldName%", "name", validateRequiredOpen);
            // Does not allow the following characters (from windows filename check): \ / : * ? " &lt; &gt; ; # |
            validatedFields[valIdx++] = new Array("%fldName%", "name", validateRegexp, "^[^\\\\/:*?\"&lt;&gt;|;#]+$", "%errNameIllegalChars% \\ / : * ? \" &lt; &gt; | ; #");

            <xsl:choose>
              <xsl:when test="$selected_type = 'externalurl'">
                  validatedFields[valIdx++] = new Array("%fldURL%", "url", validateRequired);
              </xsl:when>
              <xsl:when test="$selected_type = 'localurl'">
                  validatedFields[valIdx++] = new Array("%fldFile%", "url", validateRequired);
              </xsl:when>
               <xsl:when test="$selected_type = 'shortcut'">
                  validatedFields[valIdx++] = new Array("%fldShortcut%", "shortcut", validateRequired);
              </xsl:when>
            </xsl:choose>

            function validateAll(formName)
            {

               setType();

              var paramNameElements = document.getElementById('parambody').getElementsByTagName('input');
              var paramNameElementsLn = paramNameElements.length;
              var type = document.getElementById('type').value;

              for ( var i = 0; i &lt; paramNameElementsLn; i++ )
              {
                var paramNameElement = paramNameElements[i];
                var paramNameVal = paramNameElement.value;

                <xsl:if test="$type = 'content'">
                  if ( paramNameVal == 'key' &amp;&amp; type == 'content' )
                  {
                    error(paramNameElement, '%errKeyIsNotAllowed%', tabPane);
                    return;
                  }
                </xsl:if>
              }

              <xsl:if test="$key != 'none'">
                // Check if accessrights have changed,
                // and ask user if he wants to propagate
                if( isAccessRightsChanged() )
                {
                  document.getElementById("propagate").value = "true";
                }
              </xsl:if>

              var f = document.forms[formName];

              var valid = checkAll(formName, validatedFields, tabPane);

              if ( !valid )
              {
                  return;
              }

              if(!validateMenuItemName(document.getElementById('name')))
              {
                return;
              }

              if (type == 'form')
              {

                if ( !formbuilder_validateUniqueLabelValues() )
                {
                  error(null, '%errFormBuilderNotPossibleToHaveTwoLabelsWithSameName%', tabPane);
                  moveFocusTo(document.getElementById('form_fieldtable'));
                  return;
                }

                // Validate store responses in content repository.
                var cat = document.getElementById('viewcategory_key');

                if ( isEmpty(cat) )
                {
                  error(cat, errorRequired + "\"%fldStoreResponses%\"", tabPane);
                  return;
                }

                // Validate recipients.
                var formSendToElements = document.getElementsByName('form_sendto');
                var formSendToElement, formSendToElementValue;

                for ( var i = 0; i &lt; formSendToElements.length; i++ )
                {
                  formSendToElement = formSendToElements[i];
                  formSendToElementValue = formSendToElement.value;

                  if ( str_containsOnlyWhitespace(formSendToElementValue) )
                  {
                    error(formSendToElement, errorContainsWhitespaceOnly + ': ' + '%fldSendTo%', tabPane);
                    return;
                  }

                  if ( !isEmpty(formSendToElement) )
                  {
                    if ( !regExpEmail.test(formSendToElementValue) )
                    {
                      error(formSendToElement, errorEmail + ': ' + '%fldSendTo%', tabPane);
                      return;
                    }
                  }
                }

                var titleRadios = document.getElementsByName('field_form_title');

                if ( titleRadios &amp;&amp; titleRadios.length &gt; 0 )
                {
                  var checked = false;
                  for ( var i = 0; i &lt; titleRadios.length; i++ )
                  {
                    if ( titleRadios[i].checked )
                    {
                      checked = true;
                      break;
                    }
                  }

                  if ( !checked )
                  {
                    error(titleRadios[0], "%msgFormTitleMustBeSelected%", tabPane);
                    return;
                  }
                }

                if ( !formbuilder_isReceiptInformationValid() )
                {
                  return;
                }
              }

              <xsl:if test="$sectionmode">
                selectAllRowsInSelect('contenttypekey');
              </xsl:if>

              var cacheTypeDropdown = document.forms['formAdmin']['cachetype'];
              var cacheType = cacheTypeDropdown.options[cacheTypeDropdown.selectedIndex].value;

              if (cacheType == 'specified')
              {
                var minCacheTime = document.getElementById('mincachetime');

                if( minCacheTime.value == '' )
                {
                  error(minCacheTime, "%errorFieldRequired%: %fldCacheTime%", tabPane);
                  return;
                }
              }

              if ( type == 'content' )
              {
                var paramField = document.getElementById('content_key_paramval');

                if ( document.getElementById('contentkey') != null )
                {
                  document.getElementById('contentkey').value = paramField ? paramField.value : '';
                }
              }

              <xsl:text>var g_existingMenuItemKey = </xsl:text>
              <xsl:choose>
                <xsl:when test="$key != 'none'">
                  <xsl:value-of select="$key"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>-1</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
              <xsl:text>;</xsl:text>

              <xsl:text>var g_parentKey = </xsl:text>
              <xsl:choose>
                <xsl:when test="//menuitem[@key = $key]/@parent != ''">
                  <xsl:value-of select="//menuitem[@key = $key]/@parent"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>-1</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
              <xsl:text>;</xsl:text>

              menuitem_checkIfMenuItemNameExistsAndSubmit(document.getElementById('name'), <xsl:value-of select="$menu-item-key"/>, <xsl:value-of select="$parent-menu-item-key"/>, tabPane);

              /*
              var buttons = new Array("save", "cancel", "preview");
              disableTextButtons(buttons);

              // hack: the cancel button is actually a button inside a link
              var button = document.getElementById("cancel2");
              if (button != null)
              {
                button.href = '#';
              }

              f.submit();
              */
            }

            function validateMenuItemName( nameElement )
            {
              var name = nameElement.value;
              var retvalue = true;

              var nameStartsOrEndsWithSpaceOrContainsTwoSpaces = name.search( '^ | $| {2,}' ) >= 0;

              if( nameStartsOrEndsWithSpaceOrContainsTwoSpaces )
              {
                  error(nameElement, '%errInvalidMenuItemName%')
                  retvalue = false;
              }

              return retvalue;
            }

            function setType(){
            <xsl:choose>
                <xsl:when test="/menus/pagetemplates/pagetemplate[@key = $selpagetemplatekey2]/@type = 'document' and $selected_type != 'externalurl' and $selected_type != 'label' and $selected_type != 'section' and $selected_type != 'shortcut'">
                    document.getElementById('type').value = 'content';
                </xsl:when>
                <xsl:when test="/menus/pagetemplates/pagetemplate[@key = $selpagetemplatekey2]/@type = 'page' and $selected_type != 'externalurl' and $selected_type != 'label' and $selected_type != 'section' and $selected_type != 'shortcut'">
                    document.getElementById('type').value = 'page';
                </xsl:when>
                <xsl:when test="/menus/pagetemplates/pagetemplate[@key = $selpagetemplatekey2]/@type = 'content' and $selected_type != 'externalurl' and $selected_type != 'label' and $selected_type != 'section' and $selected_type != 'shortcut'">
                    document.getElementById('type').value = 'content';
                </xsl:when>
                <xsl:when test="/menus/pagetemplates/pagetemplate[@key = $selpagetemplatekey2]/@type = 'sectionpage' and $selected_type != 'externalurl' and $selected_type != 'label' and $selected_type != 'section' and $selected_type != 'shortcut'">
                    document.getElementById('type').value = 'sectionpage';
                </xsl:when>
                <xsl:when test="/menus/pagetemplates/pagetemplate[@key = $selpagetemplatekey2]/@type = 'form' and $selected_type != 'externalurl' and $selected_type != 'label' and $selected_type != 'section' and $selected_type != 'shortcut'">
                    document.getElementById('type').value = 'form';

                    <!--validatedFields[2] = new Array("%fldStoreResponses%", "viewcategory_key", validateRequired);-->
                    validatedFields[3] = new Array("%fldTitle%", "form_title", validateRequired);

                </xsl:when>
            </xsl:choose>
            }

            function previewPage(formName)
            {
                setType();
                var f = document.forms[formName];
                var valid = checkAll(formName, validatedFields, tabPane);
                if ( !valid )
                {
                    return;
                }
                var action = f.getAttribute("action");
                f.setAttribute("action","adminpage?op=preview");
                f.setAttribute("target","_blank");
                f.submit();

                // set old form attributes
                f.setAttribute("action", action);
                f.removeAttribute("target");
            }

            function editObject(objThis, objectName) {
                var currentRow = getObjectIndex(objThis);
                var button = document.getElementsByName("btn"+objectName+"edit")[currentRow];
                var objectKey = getNextSibling(button).value;
                var menuKey = <xsl:value-of select="$menukey"/>;
                if (objectKey != '') {
                    OpenObjectPopup(menuKey, objectKey, objectName, currentRow);
                }
                else {
                  var defaultObjectKeys = document.getElementsByName(objectName+"-defaultobject");
                  if (defaultObjectKeys != null &amp;&amp; defaultObjectKeys.length == 1) {
                    OpenObjectPopup(menuKey, defaultObjectKeys[0].value, objectName, currentRow);
                  }
                }
            }

            function callback_editObject(fieldName, fieldRow, key, title, current) {
              if (!title == '') {
                  var view = document.getElementsByName("view"+fieldName)[fieldRow];
                  if (!view.value == '') {
                    view.value = title;
                }
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
              var posibleKeyInputFields = document.getElementsByName(fieldName);
              var counter = 0;

              for(var i = 0; i &lt; posibleKeyInputFields.length; i++) {
                var currentField = posibleKeyInputFields[i];

                if(currentField.type == 'hidden')
                  break;
                else
                  counter++;
              }

              var keyInputField = posibleKeyInputFields[(fieldRow + counter)];

              if (!keyInputField)
                return;

              deletePortletFromObjectList(keyInputField);

              keyInputField.value = key;
              var viewField = document.getElementsByName("view"+fieldName)[fieldRow];
              viewField.value = title;

              contentObjectList[key] = 'added';
            }

            <xsl:call-template name="formbuilder_javascript"/>

            </script>
            </head>
            <body>
            	<xsl:attribute name="onload">
            		<xsl:choose>
            			<xsl:when test="$key = 'none'">setFocus()</xsl:when>
            			<xsl:otherwise>setFieldFocus('gui_type_combo')</xsl:otherwise>
            		</xsl:choose>
            	</xsl:attribute>

              <script type="text/javascript">waitsplash();</script>

                <form method="post" action="adminpage" name="formAdmin" id="formAdmin">
                    <input type="hidden" name="op" value="insert"/>

                    <input type="hidden" id="propagate" name="propagate" value="false"/>
                    <input type="hidden" name="referer" value="{$referer}"/>

                    <xsl:choose>
                        <xsl:when test="$key = 'none'">
                            <h1>
                                <xsl:call-template name="genericheader"/>
                                <a href="adminpage?op=browse&amp;page={$page}&amp;selectedunitkey={$selectedunitkey}&amp;menukey={$menukey}">
                                    <xsl:text>%headPageBuilder%</xsl:text>
                      </a>
                      <xsl:if test="number($insertbelow) &gt; -1">
                        <xsl:call-template name="generateheader">
                          <xsl:with-param name="menuitem" select="//menuitem[@key = $insertbelow]"/>
                        </xsl:call-template>
                      </xsl:if>
                      <xsl:text>&nbsp;</xsl:text>
                      <span id="titlename">
                        <xsl:if test="$forward_data">
                          <xsl:if test="$name != ''">
                            <xsl:text>/ </xsl:text>
                          </xsl:if>
                          <xsl:value-of select="$name"/>
                        </xsl:if>
                      </span>
                    </h1>
                  </xsl:when>
                  <xsl:otherwise>
                    <h1>
                      <xsl:call-template name="genericheader"/>
                      <a href="adminpage?op=browse&amp;page={$page}&amp;selectedunitkey={$selectedunitkey}&amp;menukey={$menukey}">
                        <xsl:text>%headPageBuilder%</xsl:text>
                      </a>

                      <xsl:call-template name="generateheader">
                        <xsl:with-param name="menuitem" select="//menuitem[@key = $insertbelow]"/>
                      </xsl:call-template>
                      <xsl:text>&nbsp;</xsl:text>
                      <span id="titlename">
                        <xsl:choose>
                          <xsl:when test="$forward_data">
                            <xsl:if test="$name != ''">
                              <xsl:text>/ </xsl:text>
                            </xsl:if>
                            <xsl:value-of select="$name"/>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:value-of select="concat('/ ', $menuitem[1]/name)"/>
                          </xsl:otherwise>
                        </xsl:choose>
                      </span>

                    </h1>
                  </xsl:otherwise>
                </xsl:choose>

                <input type="hidden" name="unitkey" value="{$selectedunitkey}"/>
                <input type="hidden" name="docindex" value="0"/>
                <input type="hidden" name="selecteddomainkey" value="{$selecteddomainkey}"/>
                <input type="hidden" name="selectedunitkey" value="{$selectedunitkey}"/>
                <input type="hidden" name="pagetemplatekey" value="{$selpagetemplatekey2}"/>
                <input type="hidden" name="pagetemplatetype"
                       value="{/menus/pagetemplates/pagetemplate[@key = $selpagetemplatekey2]/@type}"/>
                <input type="hidden" name="menukey" value="{$menukey}"/>
                <input type="hidden" name="page" value="{$page}"/>
                <input type="hidden" name="reload" value="true"/>
                <input type="hidden" name="key" value="{$key}"/>
                <xsl:variable name="parentkey">
                  <xsl:choose>
                    <xsl:when test="$menuitem/@parent">
                      <xsl:value-of select="$menuitem/@parent"/>
                    </xsl:when>
                    <xsl:when test="$insertbelow and $insertbelow != 'undefined'
                            and $insertbelow != 'none'">
                      <xsl:value-of select="$insertbelow"/>
                    </xsl:when>
                  </xsl:choose>
                </xsl:variable>
                <input type="hidden" name="parentkey" value="{$parentkey}"/>
                <input type="hidden" name="insertbelow">
                  <xsl:attribute name="value">
                    <xsl:choose>
                      <xsl:when test="$insertbelow and $insertbelow != 'undefined'
                                and $insertbelow != 'none'">
                        <xsl:value-of select="$insertbelow"/>
                      </xsl:when>
                      <xsl:when test="$menuitem/@parent">
                        <xsl:value-of select="$menuitem/@parent"/>
                      </xsl:when>
                    </xsl:choose>
                  </xsl:attribute>
                </input>

                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                  <tr>
                    <td>
                      <table cellpadding="2" cellspacing="0" border="0">
                        <tr>
                          <td>
                            %fldType%:
                          </td>
                          <td>
                            <xsl:call-template name="type_pulldown">
                              <xsl:with-param name="type" select="$selected_type"/>
                            </xsl:call-template>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                  <tr>
                    <td class="form_title_form_seperator">
                      <img src="images/1x1.gif"/>
                    </td>
                  </tr>
                </table>

                <xsl:if test="$type != 'none'">
                  <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td class="title_form_seperator">
                        <img src="images/1x1.gif"/>
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <div class="tab-pane" id="tab-pane-1">
                          <script type="text/javascript" language="JavaScript">
                            tabPane = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                          </script>

                          <div class="tab-page" id="tab-general">
                            <span class="tab">%blockGeneral%</span>

                            <script type="text/javascript" language="JavaScript">
                              tabPane.addTabPage( document.getElementById( "tab-general" ) );
                            </script>

                            <fieldset>
                              <legend>&nbsp;%blockGeneral%&nbsp;</legend>

                              <table cellpadding="2" cellspacing="0" border="0">
                                <tr>
                                  <td class="form_labelcolumn"> </td>
                                </tr>

                                <tr>
                                  <xsl:call-template name="textfield">
                                    <xsl:with-param name="name" select="'displayname'"/>
                                    <xsl:with-param name="label" select="'%fldDisplayName%:'"/>
                                    <xsl:with-param name="helpelement" select="$displayname-help-element"/>
                                    <xsl:with-param name="selectnode">
                                      <xsl:choose>
                                        <xsl:when test="$forward_data">
                                          <xsl:value-of select="$displayname"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                          <xsl:value-of select="$menuitem[1]/displayname"/>
                                        </xsl:otherwise>
                                      </xsl:choose>
                                    </xsl:with-param>
                                    <xsl:with-param name="size" select="'50'"/>
                                    <xsl:with-param name="maxlength" select="'256'"/>
                                    <xsl:with-param name="colspan" select="'1'"/>
                                    <xsl:with-param name="required" select="'true'"/>
                                  </xsl:call-template>
                                </tr>
                                 <tr>
                                  <xsl:call-template name="textfield">
                                    <xsl:with-param name="name" select="'menu-name'"/>
                                    <xsl:with-param name="label" select="'%fldMenuName%:'"/>
                                    <xsl:with-param name="helpelement" select="$menuname-help-element"/>
                                    <xsl:with-param name="selectnode">
                                      <xsl:choose>
                                        <xsl:when test="$forward_data">
                                          <xsl:value-of select="$menu-name"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                          <xsl:value-of select="$menuitem[1]/menu-name"/>
                                        </xsl:otherwise>
                                      </xsl:choose>
                                    </xsl:with-param>
                                    <xsl:with-param name="size" select="'50'"/>
                                    <xsl:with-param name="maxlength" select="'256'"/>
                                    <xsl:with-param name="colspan" select="'1'"/>
                                    <xsl:with-param name="required" select="'false'"/>
                                  </xsl:call-template>
                                </tr>
                                <tr>
                                  <td>%fldShowInMenu%:</td>
                                  <td>
                                    <input type="checkbox" name="visibility">
                                      <xsl:choose>
                                        <xsl:when test="$forward_data">
                                          <xsl:if test="$visibility = 'on'">
                                            <xsl:attribute name="checked">
                                              <xsl:text>checked</xsl:text>
                                            </xsl:attribute>
                                          </xsl:if>
                                        </xsl:when>
                                        <xsl:when test="$menuitem[1]/@visible = 'yes'">
                                          <xsl:attribute name="checked">
                                            <xsl:text>checked</xsl:text>
                                          </xsl:attribute>
                                        </xsl:when>
                                      </xsl:choose>
                                    </input>
                                  </td>
                                </tr>

                              </table>

                              <!-- why not merge these two tests?? -->
                              <xsl:variable name="thispagetemplatetype" select="/menus/pagetemplates/pagetemplate[@key = $selpagetemplatekey2]/@type"/>

                              <xsl:if test="( $thispagetemplatetype = 'document' or $thispagetemplatetype = 'newsletter' ) and $selected_type != 'externalurl' and $selected_type != 'label' and $selected_type != 'section' and $selected_type != 'shortcut'">
                                <xsl:call-template name="content_form">
                                  <xsl:with-param name="menuitem" select="$menuitem"/>
                                </xsl:call-template>
                              </xsl:if>

                              <xsl:if test="$thispagetemplatetype = 'form' and $selected_type != 'externalurl' and $selected_type != 'label' and $selected_type != 'section' and $selected_type != 'shortcut'">
                                <xsl:call-template name="content_form">
                                  <xsl:with-param name="menuitem" select="$menuitem"/>
                                </xsl:call-template>
                              </xsl:if>

                            </fieldset>

                            <!-- content selector -->
                            <xsl:if test="/menus/pagetemplates/pagetemplate[@key = $selpagetemplatekey2]/@type = 'content' and $selected_type != 'externalurl' and $selected_type != 'label' and $selected_type != 'section' and $selected_type != 'shortcut'">



                              <fieldset>
                                <legend>&nbsp;%blockParameters%&nbsp;</legend>
                                <table cellpadding="2" cellspacing="0" border="0">
                                  <tr>
                                    <td class="form_labelcolumn"><xsl:comment>empty</xsl:comment></td>
                                  </tr>
                                  <tr>
                                    <xsl:call-template name="contentfield">
                                      <!--xsl:with-param name="selectedkey" select="$menuitem/@contentkey"/>
                                      <xsl:with-param name="selectnode" select="$contenttitle"/-->

                                      <xsl:with-param name="selectedkey">
                                        <xsl:if test="not($hideContentKey)">
                                          <xsl:value-of select="$menuitem/@contentkey"/>
                                        </xsl:if>
                                      </xsl:with-param>

                                      <xsl:with-param name="selectnode">
                                        <xsl:if test="not($hideContentKey)">
                                          <xsl:value-of select="$contenttitle"/>
                                        </xsl:if>
                                      </xsl:with-param>

                                      <xsl:with-param name="name" select="'_selected_content'"/>
                                      <xsl:with-param name="index" select="'0'"/>
                                      <xsl:with-param name="contenttypekeys"
                                                      select="/menus/pagetemplates/pagetemplate[@key = $selpagetemplatekey2]/contenttypes/contenttype/@key"/>
                                    </xsl:call-template>

                                    <input type="hidden" name="contentkey" id="contentkey"/>

                                  </tr>
                                </table>
                                <script type="text/javascript">
                                  <xsl:text>var contentKeys = new Array();</xsl:text>
                                  <xsl:text>contentKeys[0] = </xsl:text>
                                  <xsl:choose>
                                    <xsl:when test="$menuitem/@contentkey">
                                      <xsl:value-of select="$menuitem/@contentkey"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <xsl:text>-1</xsl:text>
                                    </xsl:otherwise>
                                  </xsl:choose>
                                  <xsl:text>;</xsl:text>
                                </script>
                              </fieldset>
                            </xsl:if>

                            <xsl:if test="$selected_type = 'externalurl'">
                              <fieldset>
                                <legend>&nbsp;%blockURL%&nbsp;</legend>
                                <table cellpadding="2" cellspacing="2" border="0" width="100%">
                                  <xsl:call-template name="externalurl_form">
                                    <xsl:with-param name="menuitem" select="$menuitem"/>
                                  </xsl:call-template>
                                </table>
                              </fieldset>
                            </xsl:if>

                            <xsl:if test="$selected_type = 'shortcut'">
                              <fieldset>
                                <legend>&nbsp;%blockShortcut%&nbsp;</legend>
                                <br/>
                                <xsl:variable name="function">
                                  <xsl:text>javascript: OpenSelectorWindowForPages( this, 850, 'shortcut',
                                    'viewshortcut', 260, 360 )
                                  </xsl:text>
                                </xsl:variable>
                                <xsl:variable name="removefunction">
                                  <xsl:text>javascript: removeShortcut()</xsl:text>
                                </xsl:variable>
                                <table cellpadding="2" cellspacing="2" border="0" width="100%">
                                  <tr>
                                    <xsl:call-template name="searchfield">
                                      <xsl:with-param name="name" select="'shortcut'"/>
                                      <xsl:with-param name="label" select="'%fldShortcut%:'"/>
                                      <xsl:with-param name="selectedkey" select="$menuitem[1]/shortcut/@key"/>
                                      <xsl:with-param name="selectnode" select="$menuitem[1]/shortcut/@name"/>
                                      <xsl:with-param name="size" select="'40'"/>
                                      <xsl:with-param name="maxlength" select="'255'"/>
                                      <xsl:with-param name="buttonfunction" select="$function"/>
                                      <xsl:with-param name="removefunction" select="$removefunction"/>
                                      <xsl:with-param name="colspan" select="'1'"/>
                                      <xsl:with-param name="required" select="'true'"/>
                                    </xsl:call-template>
                                  </tr>
                                  <tr>
                                    <td class="form_labelcolumn">%fldForward%:</td>
                                    <td>
                                      <input type="checkbox" name="forward_shortcut" value="true">
                                        <xsl:choose>
                                          <xsl:when test="$forward_data">
                                            <xsl:if test="$forward_shortcut = 'false'">
                                              <xsl:attribute name="checked">
                                                <xsl:text>checked</xsl:text>
                                              </xsl:attribute>
                                            </xsl:if>
                                          </xsl:when>
                                          <xsl:when test="not($menuitem[1]/shortcut/@forward = 'true')">
                                            <xsl:attribute name="checked">
                                              <xsl:text>checked</xsl:text>
                                            </xsl:attribute>
                                          </xsl:when>
                                        </xsl:choose>
                                      </input>
                                    </td>
                                  </tr>
                                </table>
                              </fieldset>
                            </xsl:if>

                            <xsl:if test="$sectionmode">
                              <xsl:choose>
                                <xsl:when
                                        test="($pagetemplatetype = 'sectionpage' or $pagetemplatetype = 'newsletter') and $selected_type != 'externalurl' and $selected_type != 'label' and $selected_type != 'section'">
                                  <xsl:call-template name="sectionfield">
                                    <xsl:with-param name="sectionelem" select="$menuitem/section"/>
                                    <xsl:with-param name="selectedcontenttypes"
                                                    select="/menus/pagetemplates/pagetemplate[@key = $selpagetemplatekey2]/contenttypes/contenttype"/>
                                    <xsl:with-param name="disabled" select="true()"/>
                                  </xsl:call-template>
                                </xsl:when>
                                <xsl:otherwise>
                                  <xsl:call-template name="sectionfield">
                                    <xsl:with-param name="sectionelem" select="$menuitem/section"/>
                                  </xsl:call-template>
                                </xsl:otherwise>
                              </xsl:choose>
                            </xsl:if>

                            <xsl:if test="/menus/pagetemplates/pagetemplate[@key = $selpagetemplatekey2]/@type = 'form' and $selected_type != 'externalurl' and $selected_type != 'label' and $selected_type != 'section' and $selected_type != 'shortcut'">
                              <xsl:call-template name="formbuilder">
                                <xsl:with-param name="menuitem" select="$menuitem"/>
                              </xsl:call-template>
                            </xsl:if>

                          </div>

                          <div class="tab-page" id="tab-setting">
                            <span class="tab">%blockProperties%</span>

                            <script type="text/javascript" language="JavaScript">
                              tabPane.addTabPage( document.getElementById( "tab-setting" ) );
                            </script>

                            <xsl:call-template name="menuItemName"/>

                            <xsl:variable name="excludeparam">
                              <xsl:if test="$selected_type = 'content'">
                                <xsl:text>key</xsl:text>
                              </xsl:if>
                            </xsl:variable>

                            <xsl:call-template name="parameters">
                              <xsl:with-param name="menuitem" select="$menuitem"/>
                              <xsl:with-param name="includeoverride"
                                              select="$selected_type = 'page' or $selected_type = 'content' or $selected_type = 'section' or $selected_type = 'sectionpage'"/>
                              <xsl:with-param name="excludeparam" select="$excludeparam"/>
                            </xsl:call-template>

                            <xsl:if test="$selected_type != 'shortcut'">
                              <fieldset>
                                <legend>&nbsp;%blockMetadata%&nbsp;</legend>
                                <table cellpadding="2" cellspacing="0" border="0">
                                  <tr>
                                    <xsl:call-template name="dropdown_language">
                                      <xsl:with-param name="name" select="'languagekey'"/>
                                      <xsl:with-param name="label" select="'%fldLanguage%:'"/>
                                      <xsl:with-param name="selectedkey" select="$menuitem/@languagekey"/>
                                      <xsl:with-param name="selectnode" select="//languages/language"/>
                                      <xsl:with-param name="emptyrow" select="'%cmdSelect%'"/>
                                    </xsl:call-template>
                                  </tr>
                                  <tr>
                                    <xsl:variable name="description2">
                                      <xsl:choose>
                                        <xsl:when test="$forward_data">
                                          <xsl:value-of select="$description"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                          <xsl:value-of select="$menuitem[1]/description"/>
                                        </xsl:otherwise>
                                      </xsl:choose>
                                    </xsl:variable>
                                    <xsl:call-template name="textarea">
                                      <xsl:with-param name="label" select="'%fldDescription%:'"/>
                                      <xsl:with-param name="rows" select="'5'"/>
                                      <xsl:with-param name="cols" select="'50'"/>
                                      <xsl:with-param name="name" select="'description'"/>
                                      <xsl:with-param name="selectnode" select="$description2"/>
                                    </xsl:call-template>
                                  </tr>
                                  <tr>
                                    <xsl:variable name="keywords2">
                                      <xsl:choose>
                                        <xsl:when test="$forward_data">
                                          <xsl:value-of select="$keywords"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                          <xsl:value-of select="$menuitem[1]/keywords"/>
                                        </xsl:otherwise>
                                      </xsl:choose>
                                    </xsl:variable>
                                    <xsl:call-template name="textarea">
                                      <xsl:with-param name="label" select="'%fldKeywords%:'"/>
                                      <xsl:with-param name="rows" select="'5'"/>
                                      <xsl:with-param name="cols" select="'50'"/>
                                      <xsl:with-param name="name" select="'keywords'"/>
                                      <xsl:with-param name="selectnode" select="$keywords2"/>
                                    </xsl:call-template>
                                  </tr>
                                </table>
                              </fieldset>
                            </xsl:if>

                            <fieldset>
                              <legend>%blockPerformanceTuning%</legend>
                              <table border="0" cellspacing="0" cellpadding="2">
                                <tr>
                                  <td class="form_labelcolumn"></td>
                                </tr>

                                <xsl:variable name="cachedisabled" select="$menuitem/data/@cachedisabled"/>
                                <xsl:variable name="cachetype" select="$menuitem/data/@cachetype"/>

                                <tbody id="cachetime">
                                  <tr>

                                    <td valign="top">%fldCacheSettings%:</td>
                                    <td valign="top" width="160">

                                      <select name="cachetype">
                                        <xsl:attribute name="onchange">
                                          <xsl:text>javascript:if (this.value == 'specified'){ document.getElementById('cachetimetable').style.display='inline'; } else{ document.getElementById('cachetimetable').style.display='none'; }
                                          </xsl:text>
                                        </xsl:attribute>

                                        <option value="off">
                                          <xsl:if test="$cachedisabled = 'true'">
                                            <xsl:attribute name="selected">selected</xsl:attribute>
                                          </xsl:if>
                                          %optCachingOff%
                                        </option>

                                        <option value="default">
                                          <xsl:if test="(not($cachetype) and not($cachedisabled = 'true')) or $cachetype = 'default'">
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

                                    <td valign="top">

                                      <table border="0" cellspacing="2" cellpadding="0" width="100%"
                                             id="cachetimetable">
                                        <xsl:if test="not($cachetype) or $cachetype != 'specified'">
                                          <xsl:attribute name="style">display: none</xsl:attribute>
                                        </xsl:if>
                                        <tr>
                                          <xsl:call-template name="textfield">
                                            <xsl:with-param name="name" select="'mincachetime'"/>
                                            <xsl:with-param name="label" select="'%fldCacheTime%&nbsp;:'"/>
                                            <xsl:with-param name="selectnode"
                                                            select="$menuitem/data/@mincachetime"/>
                                            <xsl:with-param name="size" select="5"/>
                                            <xsl:with-param name="postfield" select="' (%seconds%)'"/>
                                          </xsl:call-template>
                                        </tr>
                                      </table>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td class="form_labelcolumn" valign="top" nowrap="nowrap">
                                      %fldRunAs%:
                                    </td>
                                    <td>
                                      <xsl:call-template name="dropdown_runas">
                                        <xsl:with-param name="name" select="'runAs'"/>
                                        <xsl:with-param name="selectedKey" select="$menuitem/@runAs"/>
                                        <xsl:with-param name="defaultRunAsUserName" select="$defaultRunAsUser"/>
                                        <xsl:with-param name="inheritMessage" select="'%fldInheritFromPageTemplateSite%'"/>
                                      </xsl:call-template>
                                    </td>
                                  </tr>

                                </tbody>

                              </table>
                            </fieldset>

                            <fieldset>
                              <legend>&nbsp;%blockEventLog%&nbsp;</legend>
                              <img src="images/shim.gif" height="4" class="shim" border="0"/>
                              <br/>

                              <table width="99%" cellspacing="0" cellpadding="2">
                                <tr>
                                  <td colspan="2">
                                    <xsl:if test="$menuitem/@key">
                                      <xsl:call-template name="button">
                                        <xsl:with-param name="name" select="'vieweventlog'"/>
                                        <xsl:with-param name="type" select="'button'"/>
                                        <xsl:with-param name="caption" select="'%cmdViewEventLog%'"/>
                                        <xsl:with-param name="onclick">
                                          <xsl:text>viewEventLog(1,</xsl:text>
                                          <xsl:text>-1,</xsl:text>
                                          <xsl:value-of select="$menuitem/@key"/>
                                          <xsl:text>, null)</xsl:text>
                                        </xsl:with-param>
                                      </xsl:call-template>
                                    </xsl:if>
                                  </td>
                                </tr>
                              </table>
                            </fieldset>
                          </div>

                          <xsl:if test="$selected_type = 'page' or $selected_type = 'content' or $selected_type = 'sectionpage' or $selected_type = 'newsletter'">
                            <div class="tab-page" id="tab-pageconfig">
                              <span class="tab">%blockPageConfiguration%</span>

                              <script type="text/javascript" language="JavaScript">
                                tabPane.addTabPage( document.getElementById( "tab-pageconfig" ) );
                              </script>

                              <xsl:call-template name="page_form">
                                <xsl:with-param name="menuitem" select="$menuitem"/>
                              </xsl:call-template>
                            </div>
                          </xsl:if>

                          <xsl:if test="$user_have_administrate_rights = 'true'">
                            <div class="tab-page" id="tab-page-security">
                              <span class="tab">%blockPageSecurity%</span>

                              <script type="text/javascript" language="JavaScript">
                                tabPane.addTabPage( document.getElementById( "tab-page-security" ) );
                              </script>

                              <fieldset>
                                <legend>&nbsp;%blockPageSecurity%&nbsp;</legend>
                                <br/>

                                <xsl:choose>
                                  <!-- edit existing menu item-->
                                  <xsl:when test="boolean($menuitem)">
                                    <xsl:variable name="keep_anonymous">
                                      <xsl:choose>
                                        <xsl:when
                                                test="$menu/@loginpage = $menuitem/@key or $menu/@errorpage = $menuitem/@key">
                                          <xsl:value-of select="'true'"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                          <xsl:value-of select="'false'"/>
                                        </xsl:otherwise>
                                      </xsl:choose>
                                    </xsl:variable>

                                    <xsl:call-template name="accessrights">
                                      <xsl:with-param name="right_publish_available" select="true()"/>
                                      <xsl:with-param name="right_add_available" select="true()"/>
                                      <xsl:with-param name="dataxpath" select="$menuitem"/>
                                      <xsl:with-param name="keep_anonymous" select="$keep_anonymous"/>
                                      <xsl:with-param name="allowauthenticated" select="true()"/>
                                    </xsl:call-template>
                                  </xsl:when>
                                  <!-- insert new menu item (default access rights)-->
                                  <xsl:when test="/menus/accessrights">
                                    <xsl:call-template name="accessrights">
                                      <xsl:with-param name="right_publish_available" select="true()"/>
                                      <xsl:with-param name="right_add_available" select="true()"/>
                                      <xsl:with-param name="dataxpath" select="/menus"/>
                                    	<xsl:with-param name="allowauthenticated" select="true()"/>
                                    </xsl:call-template>
                                  </xsl:when>
                                  <!-- insert new menu item (changed page type, possibly changed accessrights)-->
                                  <xsl:otherwise>
                                    <xsl:call-template name="accessrights">
                                      <xsl:with-param name="right_publish_available" select="true()"/>
                                      <xsl:with-param name="right_add_available" select="true()"/>
                                      <xsl:with-param name="dataxpath" select="/menus"/>
                                    	<xsl:with-param name="allowauthenticated" select="true()"/>
                                    </xsl:call-template>
                                  </xsl:otherwise>
                                </xsl:choose>

                                <br/>
                              </fieldset>
                            </div>
                          </xsl:if>

                        </div>

                        <script type="text/javascript" language="JavaScript">
                          <xsl:choose>
                            <xsl:when test="$selectedtabpageid != 'none'">
                              setupAllTabs();
                              tabPane.setSelectedPage("<xsl:value-of select="$selectedtabpageid"/>");
                            </xsl:when>
                            <xsl:otherwise>
                              setupAllTabs();
                            </xsl:otherwise>
                          </xsl:choose>
                        </script>

                      </td>
                    </tr>
                  </table>

                  <table border="0" cellspacing="0" cellpadding="0" width="100%">
                    <tr>
                      <td>
                      </td>
                    </tr>
                    <tr>
                      <td>
                        <br/>
                      </td>
                    </tr>

                    <tr>
                      <td>
                        <xsl:call-template name="button">
                          <xsl:with-param name="type" select="'button'"/>
                          <xsl:with-param name="caption" select="'%cmdSave%'"/>
                          <xsl:with-param name="name" select="'save'"/>
                          <xsl:with-param name="onclick">
                            <xsl:text>javascript:validateAll('formAdmin');</xsl:text>
                          </xsl:with-param>
                          <xsl:with-param name="disabled" select="$save_disabled"/>
                        </xsl:call-template>
                        <xsl:text>&nbsp;</xsl:text>
                        <xsl:call-template name="button">
                          <xsl:with-param name="type" select="'cancel'"/>
                          <xsl:with-param name="name" select="'cancel'"/>
                          <xsl:with-param name="id" select="'cancel2'"/>
                          <xsl:with-param name="referer" select="$referer"/>
                        </xsl:call-template>
                        <xsl:if test="not($preview_disabled = 'true')">
                          <xsl:text>&nbsp;</xsl:text>
                          <xsl:call-template name="button">
                            <xsl:with-param name="type" select="'button'"/>
                            <xsl:with-param name="caption" select="'%cmdPreview%'"/>
                            <xsl:with-param name="name" select="'preview'"/>
                            <xsl:with-param name="onclick">
                              <xsl:text>javascript:previewPage('formAdmin');</xsl:text>
                            </xsl:with-param>
                            <xsl:with-param name="disabled" select="$preview_disabled"/>
                          </xsl:call-template>
                        </xsl:if>
                      </td>
                    </tr>
                  </table>
                </xsl:if>
              </form>

              <script type="text/javascript" language="JavaScript">
                <xsl:if test="$selpagetemplatekey2 = -2 and ($type = 'content' or $type = 'page')">
                  <xsl:variable name="temp">
                    <xsl:call-template name="find_parent_framework">
                      <xsl:with-param name="menuitem" select="$menuitem"/>
                    </xsl:call-template>
                  </xsl:variable>
                  reloadPageWithPageFramework(<xsl:value-of select="$temp"/>, false);
                </xsl:if>
              </script>

              <script type="text/javascript">
                if ( typeof removeWaitsplash === 'function' )
                {
                  removeWaitsplash();
                }
              </script>

            </body>
        </html>
    </xsl:template>

  <xsl:template name="page_form">
    <xsl:param name="menuitem"/>

    <input type="hidden" name="pagekey">
      <xsl:attribute name="value">
        <xsl:value-of select="$menuitem[1]/page/@key"/>
      </xsl:attribute>
    </input>

    <xsl:choose>
      <xsl:when test="$menuitem[1]/page/@pagetemplatekey !='' and $selpagetemplatekey2 = ''">
        <xsl:call-template name="pageform">
          <xsl:with-param name="chosenpagetemplatekey" select="$menuitem[1]/page/@pagetemplatekey"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="pageform">
          <xsl:with-param name="chosenpagetemplatekey" select="$selpagetemplatekey2"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <xsl:template name="pageform">
    <xsl:param name="chosenpagetemplatekey"/>

    <input type="hidden" name="css">
      <xsl:attribute name="value">
        <xsl:value-of select="/menus/pagetemplates/pagetemplate[@key = $chosenpagetemplatekey]/css/@stylesheetkey"/>
      </xsl:attribute>
    </input>

    <xsl:call-template name="generictemplate"/>


  </xsl:template>

  <xsl:template name="sectionfield">
    <xsl:param name="sectionelem"/>
    <xsl:param name="selectedcontenttypes" select="$sectionelem/contenttypes/contenttype"/>
    <xsl:param name="disabled" select="false()"/>

    <xsl:variable name="contenttypes" select="/node()/contenttypes/contenttype"/>

    <fieldset>
      <legend>%fldSection%</legend>

      <table cellspacing="0" cellpadding="2" border="0">
        <tr>
          <td>
            <input type="radio" name="section_ordered" value="true">
              <xsl:if test="$sectionelem/@ordered = 'true'">
                <xsl:attribute name="checked">checked</xsl:attribute>
              </xsl:if>
            </input>
            %optOrdered%
          </td>
        </tr>
        <tr>
          <td>
            <input type="radio" name="section_ordered" value="false">
              <xsl:if test="not($sectionelem/@ordered = 'true')">
                <xsl:attribute name="checked">checked</xsl:attribute>
              </xsl:if>
            </input>
            %optUnordered%
          </td>
        </tr>
        <tr>
          <td colspan="2">

            <table border="0" cellspacing="2" cellpadding="0" width="100%">
              <tr>
                <td>
                  <!--
                    Hide this when the template type is section.
                  -->
                  <xsl:if test="$disabled">
                    <xsl:attribute name="style">
                      <xsl:text>display:none</xsl:text>
                    </xsl:attribute>
                  </xsl:if>

                  <div style="padding-bottom: 1em;">
                    <xsl:text>%fldAvailableContentTypes%:</xsl:text>
                  </div>

                  <select multiple="multiple" style="width: 13em; height: 10em;" name="availablect" id="availablect">
                    <xsl:if test="$disabled">
                      <xsl:attribute name="disabled">disabled</xsl:attribute>
                    </xsl:if>

                    <xsl:for-each select="$contenttypes">
                      <xsl:sort select="name"/>

                      <xsl:variable name="varkey">
                        <xsl:value-of select="@key"/>
                      </xsl:variable>

                      <xsl:if test="not($selectedcontenttypes[@key = $varkey])">
                        <option value="{@key}" ondblclick="moveOptions('availablect', 'contenttypekey');">
                          <xsl:value-of select="name"/>
                        </option>
                      </xsl:if>

                    </xsl:for-each>
                  </select>
                </td>

                <td>
                  <xsl:attribute name="style">
                    <xsl:text>padding: 0.5em;</xsl:text>
                    <!--
                      Hide this when the template type is section.
                    -->
                    <xsl:if test="$disabled">
                      <xsl:text>display:none</xsl:text>
                    </xsl:if>
                  </xsl:attribute>

                  <xsl:call-template name="button">
                    <xsl:with-param name="type" select="'button'"/>
                    <xsl:with-param name="image" select="'images/icon_move_right.gif'"/>
                    <xsl:with-param name="onclick">
                      <xsl:text>javascript:moveOptions('availablect', 'contenttypekey');</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="disabled" select="$disabled"/>
                  </xsl:call-template>
                  <br/>
                  <xsl:call-template name="button">
                    <xsl:with-param name="type" select="'button'"/>
                    <xsl:with-param name="image" select="'images/icon_move_left.gif'"/>
                    <xsl:with-param name="onclick">
                      <xsl:text>javascript:moveOptions('contenttypekey', 'availablect');</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="disabled" select="$disabled"/>
                  </xsl:call-template>
                </td>

                <td>
                  <div style="padding-bottom: 1em;">
                    <xsl:choose>
                      <xsl:when test="$disabled">
                        <xsl:text>%fldAvailableContentTypes%:</xsl:text>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:text>%fldSelectedContentTypes%:</xsl:text>
                      </xsl:otherwise>
                    </xsl:choose>
                  </div>

                  <select multiple="multiple" style="width: 13em; height: 10em;" name="contenttypekey"
                          id="contenttypekey">
                    <xsl:for-each select="$selectedcontenttypes">
                      <xsl:sort select="name"/>

                      <xsl:variable name="varkey">
                        <xsl:value-of select="@key"/>
                      </xsl:variable>

                      <option value="{@key}">
                        <xsl:if test="not($disabled)">
                          <xsl:attribute name="ondblclick">
                            <xsl:text>moveOptions('contenttypekey', 'availablect');</xsl:text>
                          </xsl:attribute>
                        </xsl:if>
                        <xsl:value-of select="name"/>
                      </option>

                    </xsl:for-each>
                  </select>

                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </fieldset>
  </xsl:template>

  <xsl:template name="generictemplate">
    <xsl:for-each select="/menus/pagetemplateparameters/pagetemplateparameter">
      <xsl:sort select="name" data-type="text" lang="en" order="ascending"/>
      <fieldset>
        <legend>&nbsp;%blockRegion%:
          <xsl:value-of select="name"/>
        </legend>
        <table border="0" cellspacing="0" cellpadding="0" class="mei-region-portlets">
          <tr>
            <td class="form_labelcolumn" style="width:300px">
              <xsl:variable name="currentparametername">
                <xsl:value-of select="name"/>
              </xsl:variable>

              <xsl:choose>
                <xsl:when test="@multiple=1">
                  <table border="0" cellspacing="0" cellpadding="1" class="mei-region-portlets">
                    <tbody>
                      <xsl:attribute name="id">
                        <xsl:text>tbl</xsl:text>
                        <xsl:value-of select="$currentparametername"/>
                      </xsl:attribute>
                      <xsl:attribute name="name">
                        <xsl:text>tbl</xsl:text>
                        <xsl:value-of select="$currentparametername"/>
                      </xsl:attribute>
                      <tr>
                        <td nowrap="nowrap" colspan="2">
                          <xsl:variable name="objectname" select="name"/>
                          <input type="hidden">
                            <xsl:attribute name="name">
                              <xsl:value-of select="name"/>
                              <xsl:text>separator</xsl:text>
                            </xsl:attribute>
                            <xsl:attribute name="value">
                              <xsl:value-of select="separator"/>
                            </xsl:attribute>
                          </input>
                          <xsl:for-each
                                  select="/menus/pagetemplates/pagetemplate[@key = $selpagetemplatekey2]/contentobjects/contentobject[parametername = $currentparametername]">
                            <input type="hidden">
                              <xsl:attribute name="name">
                                <xsl:value-of select="$objectname"/>
                                <xsl:text>-defaultobject</xsl:text>
                              </xsl:attribute>
                              <xsl:attribute name="value">
                                <xsl:value-of select="@conobjkey"/>
                              </xsl:attribute>
                            </input>
                            <span style="font-style: italic">
                              <xsl:value-of select="name"/>
                            </span>
                            (standard)
                            <xsl:if test="position() != last()">
                              <br/>
                            </xsl:if>
                          </xsl:for-each>
                        </td>
                      </tr>
                      <xsl:for-each
                              select="$menuitem[1]/page/contentobjects/contentobject[parametername=$currentparametername]">
                        <xsl:sort select="order"/>
                        <tr>
                          <xsl:call-template name="contentobjectselector_multi">
                            <xsl:with-param name="name" select="$currentparametername"/>
                            <xsl:with-param name="selectedkey" select="@conobjkey"/>
                            <xsl:with-param name="selectnode" select="name"/>
                            <xsl:with-param name="size" select="$contentobjectselector_multi_size"/>
                            <xsl:with-param name="maxlength" select="'50'"/>
                            <xsl:with-param name="colspan" select="'1'"/>
                            <xsl:with-param name="objdoc" select="contentobjectdata/datasources/@objectdocument"/>
                          </xsl:call-template>
                        </tr>
                      </xsl:for-each>

                      <xsl:if test="not($menuitem[1]/page/contentobjects/contentobject[parametername=$currentparametername])">
                        <tr>
                          <xsl:call-template name="contentobjectselector_multi">
                            <xsl:with-param name="name" select="$currentparametername"/>
                            <xsl:with-param name="selectedkey" select="''"/>
                            <xsl:with-param name="selectnode" select="''"/>
                            <xsl:with-param name="size" select="$contentobjectselector_multi_size"/>
                            <xsl:with-param name="maxlength" select="'50'"/>
                            <xsl:with-param name="colspan" select="'1'"/>
                          </xsl:call-template>
                        </tr>
                      </xsl:if>
                    </tbody>
                  </table>
                  <xsl:call-template name="button">
                    <xsl:with-param name="name">add
                      <xsl:value-of select="$currentparametername"/>
                    </xsl:with-param>
                    <xsl:with-param name="caption" select="'%cmdNewPortlet%'"/>
                    <xsl:with-param name="onclick">
                      <xsl:text>addObjectSelector('</xsl:text>
                      <xsl:value-of select="$currentparametername"/>
                      <xsl:text>')</xsl:text>
                    </xsl:with-param>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                  <table border="0" cellspacing="0" cellpadding="1" class="mei-region-portlets">
                    <tbody>
                      <tr>
                        <xsl:if test="/menus/pagetemplates/pagetemplate[@key = $selpagetemplatekey2]/contentobjects/contentobject[parametername = $currentparametername]">
                          <td nowrap="nowrap" colspan="2">
                            <span style="font-style: italic">
                              <xsl:value-of
                                      select="/menus/pagetemplates/pagetemplate[@key = $selpagetemplatekey2]/contentobjects/contentobject[parametername = $currentparametername]/name"/>
                            </span>
                            (standard)
                          </td>
                          <input type="hidden">
                            <xsl:attribute name="name">
                              <xsl:value-of select="$currentparametername"/>
                              <xsl:text>-defaultobject</xsl:text>
                            </xsl:attribute>
                            <xsl:attribute name="value">
                              <xsl:value-of
                                      select="/menus/pagetemplates/pagetemplate[@key = $selpagetemplatekey2]/contentobjects/contentobject[parametername = $currentparametername]/@conobjkey"/>
                            </xsl:attribute>
                          </input>
                        </xsl:if>
                      </tr>
                      <tr>
                        <td>
                          <xsl:call-template name="contentobjectselector">
                            <xsl:with-param name="name" select="$currentparametername"/>
                            <xsl:with-param name="selectedkey"
                                            select="$menuitem[1]/page/contentobjects/contentobject[parametername=$currentparametername]/@conobjkey"/>
                            <xsl:with-param name="selectnode"
                                            select="$menuitem[1]/page/contentobjects/contentobject[parametername=$currentparametername]/name"/>
                            <xsl:with-param name="size" select="$contentobjectselector_multi_size"/>
                            <xsl:with-param name="maxlength" select="'50'"/>
                            <xsl:with-param name="colspan" select="'1'"/>
                            <xsl:with-param name="objdoc"
                                            select="$menuitem[1]/page/contentobjects/contentobject[parametername=$currentparametername]/contentobjectdata/datasources/@objectdocument"/>
                          </xsl:call-template>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </xsl:otherwise>
              </xsl:choose>

              <!-- Initial enabling/disabling of move up/down buttons -->
              <script type="text/javascript" language="JavaScript">
                enableDisableButtons( '<xsl:value-of select="name"/>' );
              </script>

            </td>
          </tr>
        </table>
      </fieldset>
    </xsl:for-each>

  </xsl:template>

  <xsl:template name="content_form">
    <xsl:param name="menuitem"/>

      <xsl:variable name="document-as-elem">
          <xsl:element name="root">
              <xsl:value-of select="$document"/>
          </xsl:element>
      </xsl:variable>

    <table border="0" cellpadding="2" cellspacing="0">
      <tr>
        <xsl:variable name="documentcontent">
          <xsl:choose>
            <xsl:when test="$forward_data and $document != ''">
              <xsl:copy-of select="$document-as-elem"/>
            </xsl:when>
            <xsl:when test="$create = 1">
              <xsl:copy-of
                      select="/menus/pagetemplates/pagetemplate[@key = $selpagetemplatekey2]/pagetemplatedata/document"/>
            </xsl:when>
            <xsl:when test="$menuitem[1]/document/@mode = 'xhtml'">
              <xsl:copy-of select="$menuitem[1]/document"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$menuitem[1]/document"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <xsl:variable name="template">
          <xsl:choose>
            <xsl:when test="$menuitem[1]/page/@pagetemplatekey!='' and $selpagetemplatekey2 = ''">
              <xsl:value-of select="$menuitem[1]/page/@pagetemplatekey"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$selpagetemplatekey2"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:variable name="css">
          <xsl:choose>
            <xsl:when test="/menus/pagetemplates/pagetemplate[@key = $template]/css/@stylesheetkey">
              <xsl:value-of select="/menus/pagetemplates/pagetemplate[@key = $template]/css/@stylesheetkey"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$menu/@defaultcss"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <td width="120" class="form_labelcolumn" valign="top" nowrap="nowrap">%blockDocument%:</td>
        <td colspan="2">
          <xsl:variable name="editor_height">
            <xsl:choose>
              <xsl:when
                      test="/menus/pagetemplates/pagetemplate[@key = $selpagetemplatekey2]/@type = 'form' and $selected_type != 'externalurl' and $selected_type != 'label'">
                <xsl:text>300</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>500</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <xsl:call-template name="xhtmleditor">
            <xsl:with-param name="id" select="'contentdata_body'"/>
            <xsl:with-param name="name" select="'contentdata_body'"/>
            <xsl:with-param name="content" select="$documentcontent"/>
            <xsl:with-param name="configxpath" select="/menus/htmleditorconfig"/>
            <xsl:with-param name="config" select="'document'"/>
            <xsl:with-param name="customcss" select="$css"/>
            <xsl:with-param name="height" select="$editor_height"/>
            <xsl:with-param name="menukey" select="$menukey"/>
            <xsl:with-param name="disabled" select="false()"/>
            <xsl:with-param name="accessToHtmlSource" select="$accessToHtmlSource = 'true'"/>
            <xsl:with-param name="classfilter" select="true()"/>
          </xsl:call-template>
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template name="menuItemName">
    <fieldset>
      <legend>&nbsp;%blockName%&nbsp;</legend>

      <xsl:variable name="selectnode">
        <xsl:choose>
          <xsl:when test="$forward_data">
            <xsl:value-of select="$name"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$menuitem[1]/name"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:variable name="selectnode2">
        <xsl:choose>
          <xsl:when test="string-length($selectnode) &gt; 0">
            <xsl:value-of select="$selectnode"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$text-auto-generated"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <table width="100%" colspace="0" colpadding="2" border="0"
             name="menuitem_nametable" id="menuitem_nametable">
      <tbody id="content_key_parambody">
        <tr>
          <xsl:call-template name="textfield">
            <xsl:with-param name="name" select="'name'"/>
            <xsl:with-param name="label" select="'%fldName%:'"/>
            <xsl:with-param name="helpelement" select="$name-help-element"/>
            <xsl:with-param name="lock" select="true()"/>
            <xsl:with-param name="lock-tooltip" select="'%tooltipUnlockToEditManually%'"/>
            <xsl:with-param name="lock-click-callback">
              <xsl:text>menuitem_name_lockClickCallback('</xsl:text>
              <xsl:value-of select="$text-auto-generated"/>
              <xsl:text>')</xsl:text>
            </xsl:with-param>
            <xsl:with-param name="selectnode" select="$selectnode2"/>
            <xsl:with-param name="size" select="'51'"/>
            <xsl:with-param name="readonly" select="true()"/>
            <xsl:with-param name="disabled" select="$selectnode2 = $text-auto-generated"/>
            <xsl:with-param name="maxlength" select="'256'"/>
            <xsl:with-param name="colspan" select="'1'"/>
            <xsl:with-param name="onkeyup">
              <xsl:text>javascript: updateBreadCrumbHeader('titlename', this);</xsl:text>
            </xsl:with-param>
            <xsl:with-param name="extra-css-class">
              <xsl:if test="$selectnode2 = $text-auto-generated">
                <xsl:text>grey-text</xsl:text>
              </xsl:if>
            </xsl:with-param>
          </xsl:call-template>
        </tr>
      </tbody>
      </table>

    </fieldset>
  </xsl:template>

  <xsl:template name="parameters">
    <xsl:param name="menuitem"/>
    <xsl:param name="includeoverride" select="true()"/>
    <xsl:param name="excludeparam"/>

    <fieldset>
      <legend>&nbsp;%blockParameters%&nbsp;</legend>
      <!-- ***** content key ***** -->
      <table width="100%" colspace="0" colpadding="2" border="0"
             name="content_key_paramtable" id="content_key_paramtable">
        <!--xsl:if test="not($menuitem/@contentkey != '')"-->
        <xsl:if test="$hideContentKey">
          <xsl:attribute name="style">display: none</xsl:attribute>
        </xsl:if>

        <tbody id="content_key_parambody">
          <tr>
            <td>
              <xsl:text>%fldName%:&nbsp;</xsl:text>
              <input type="text" name="paramname" id="content_key_paramname" readonly="true" class="content-reference">
                <xsl:if test="not($hideContentKey)">
                  <xsl:attribute name="value">
                    <xsl:text>key</xsl:text>
                  </xsl:attribute>
                </xsl:if>
              </input>
              <xsl:text>&nbsp;&nbsp;%fldValue%:&nbsp;</xsl:text>
              <input type="text" name="paramval" id="content_key_paramval" readonly="true" class="content-reference">
                <xsl:if test="not($hideContentKey)">
                  <xsl:attribute name="value">
                    <xsl:value-of select="$menuitem/@contentkey"/>
                  </xsl:attribute>
                </xsl:if>
              </input>

              <!--xsl:choose>
                <xsl:when test="$includeoverride"-->
              <xsl:text>&nbsp;&nbsp;%fldOverride%:&nbsp;</xsl:text>
              <select name="_paramoverride" id="content_key_paramoverride" disabled="true">
                  <option value="false">
                    <xsl:text>%sysFalse%</xsl:text>
                  </option>
                <option value="true">
                  <xsl:text>%sysURL%</xsl:text>
                </option>
              </select>
              <input type="hidden" name="paramoverride" value="false"/>
              <xsl:text>&nbsp;&nbsp;</xsl:text>
              <xsl:call-template name="button">
                <xsl:with-param name="type" select="'button'"/>
                <xsl:with-param name="name">content_key_delbtn</xsl:with-param>
                <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                <xsl:with-param name="disabled" select="'true'"/>
                <xsl:with-param name="onclick">
                  <xsl:text>javascript:void(0);</xsl:text>
                </xsl:with-param>
              </xsl:call-template>
            </td>
          </tr>

        </tbody>
      </table>
      <!-- ***** // content key ***** -->

      <table width="100%" colspace="0" colpadding="2" border="0"
             name="paramtable" id="paramtable">
        <tbody id="parambody">

          <xsl:choose>
            <xsl:when test="boolean($menuitem[1]/parameters)
                    and count($menuitem[1]/parameters/parameter[@name != $excludeparam]) &gt; 0">
              <xsl:for-each select="$menuitem[1]/parameters/parameter[@name != $excludeparam]">
                <tr>
                  <td>
                    <xsl:text>%fldName%:&nbsp;</xsl:text>
                    <input type="text" name="paramname" value="{@name}"/>
                    <xsl:text>&nbsp;&nbsp;%fldValue%:&nbsp;</xsl:text>
                    <input type="text" name="paramval" value="{.}"/>
                    <xsl:choose>
                      <xsl:when test="$includeoverride">
                        <xsl:text>&nbsp;&nbsp;%fldOverride%:&nbsp;</xsl:text>
                        <select name="paramoverride">
                          <option value="false">
                            <xsl:if test="@override = 'false' or not(@override)">
                              <xsl:attribute name="selected">selected</xsl:attribute>
                            </xsl:if>
                            <xsl:text>%sysFalse%</xsl:text>
                          </option>
                          <option value="true">
                            <!-- support older override values that equals true -->
                            <xsl:if test="@override = 'true' or @override != 'false'">
                              <xsl:attribute name="selected">selected</xsl:attribute>
                            </xsl:if>
                            <xsl:text>%sysURL%</xsl:text>
                          </option>
                        </select>
                      </xsl:when>
                      <xsl:otherwise>
                        <input type="hidden" name="paramoverride" value="false"/>
                      </xsl:otherwise>
                    </xsl:choose>
                    <xsl:text>&nbsp;&nbsp;</xsl:text>
                    <xsl:call-template name="button">
                      <xsl:with-param name="type" select="'button'"/>
                      <xsl:with-param name="name">btndel</xsl:with-param>
                      <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                      <xsl:with-param name="onclick">
                        <xsl:text>javascript:removeParamObject('parambody', this, 'btndel');</xsl:text>
                      </xsl:with-param>
                    </xsl:call-template>
                  </td>
                </tr>
              </xsl:for-each>
            </xsl:when>
            <!-- When page is new -->
            <xsl:when test="boolean(/menus/parameters)
                    and count(/menus/parameters/parameter) &gt; 0">
              <xsl:for-each select="/menus/parameters/parameter">
                <tr>
                  <td>
                    <xsl:text>%fldName%:&nbsp;</xsl:text>
                    <input type="text" name="paramname" value="{@name}"/>
                    <xsl:text>&nbsp;&nbsp;%fldValue%:&nbsp;</xsl:text>
                    <input type="text" name="paramval" value="{.}"/>
                    <xsl:text>&nbsp;&nbsp;%fldOverride%:&nbsp;</xsl:text>
                    <select name="paramoverride">
                      <option value="false">
                        <xsl:if test="@override = 'false' or not(@override)">
                          <xsl:attribute name="selected">selected</xsl:attribute>
                        </xsl:if>
                        <xsl:text>%sysFalse%</xsl:text>
                      </option>
                      <option value="true">
                        <!-- support older override values that equals true -->
                        <xsl:if test="@override = 'true' or @override != 'false'">
                          <xsl:attribute name="selected">selected</xsl:attribute>
                        </xsl:if>
                        <xsl:text>%sysURL%</xsl:text>
                      </option>
                    </select>
                    <xsl:text>&nbsp;&nbsp;</xsl:text>
                    <xsl:call-template name="button">
                      <xsl:with-param name="type" select="'button'"/>
                      <xsl:with-param name="name">btndel</xsl:with-param>
                      <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                      <xsl:with-param name="onclick">
                        <xsl:text>javascript:removeParamObject('paramtable', this, 'btndel');</xsl:text>
                      </xsl:with-param>
                    </xsl:call-template>
                  </td>
                </tr>
              </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
              <tr>
                <td>
                  <xsl:text>%fldName%:&nbsp;</xsl:text>
                  <input type="text" name="paramname" value=""/>
                  <xsl:text>&nbsp;&nbsp;%fldValue%:&nbsp;</xsl:text>
                  <input type="text" name="paramval" value=""/>
                  <xsl:text>&nbsp;&nbsp;%fldOverride%:&nbsp;</xsl:text>
                  <select name="paramoverride">
                    <option value="false" selected="selected">%sysFalse%</option>
                    <option value="url">%sysURL%</option>
                  </select>
                  <xsl:text>&nbsp;&nbsp;</xsl:text>
                  <xsl:call-template name="button">
                    <xsl:with-param name="type" select="'button'"/>
                    <xsl:with-param name="name">btndel</xsl:with-param>
                    <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                    <xsl:with-param name="onclick">
                      <xsl:text>javascript:removeParamObject('paramtable', this, 'btndel');</xsl:text>
                    </xsl:with-param>
                  </xsl:call-template>
                </td>
              </tr>
            </xsl:otherwise>
          </xsl:choose>
        </tbody>
      </table>
      <xsl:call-template name="button">
        <xsl:with-param name="type" select="'button'"/>
        <xsl:with-param name="caption" select="'%cmdNewParameter%'"/>
        <xsl:with-param name="name" select="'addparameter'"/>
        <xsl:with-param name="onclick">
          <xsl:text>javascript:menuItem_addParamRow();</xsl:text>
          <!--xsl:text>clearNewRow('paramtable', ['paramname', 'paramval']);</xsl:text-->
        </xsl:with-param>
      </xsl:call-template>


    </fieldset>
  </xsl:template>

  <xsl:template name="externalurl_form">
    <!--
          this template displays the form for the URL type.
        -->
    <xsl:param name="menuitem"/>
    <input type="hidden" name="islocalurl" value="no"/>
    <tr>
      <td width="120" nowrap="nowrap">
        %fldURL%:<span class="requiredfield">*</span>
      </td>
      <td>
        <input type="text" name="url" value="{$menuitem[1]/url}" size="30"/>
        &nbsp;
        <select name="newwindow">
          <option value="no">
            <xsl:if test="boolean($menuitem[1]/url[@newwindow = 'no'])">
              <xsl:attribute name="selected">selected</xsl:attribute>
            </xsl:if>
            %optURLOpenSameWindow%
          </option>
          <option value="yes">
            <xsl:if test="boolean($menuitem[1]/url[@newwindow = 'yes'])">
              <xsl:attribute name="selected">selected</xsl:attribute>
            </xsl:if>
            %optURLOpenNewWindow%
          </option>
        </select>
      </td>
    </tr>

  </xsl:template>


  <xsl:template name="localurl_form">
    <!--
        this template displays the form for the URL type.
    -->
    <xsl:param name="menuitem"/>

    <input type="hidden" name="islocalurl" value="no"/>

    <tr bgcolor="#EEEEEE">
      <td colspan="2">
        %fldTarget%:
      </td>
    </tr>
    <tr>
      <td>
        %fldFile%:
      </td>
      <td>
        <input type="text" name="url" value="{$menuitem[1]/url}" size="30"/>
        <!--input type="button" class="button" value="..." onclick="javascript:OpenSelectorWindow(1029, 200, 300);"/-->
        <xsl:call-template name="button">
          <xsl:with-param name="type" select="'button'"/>
          <xsl:with-param name="image" select="'images/icon_browse.gif'"/>
          <xsl:with-param name="name" select="'noname'"/>
          <xsl:with-param name="onclick">
            <xsl:text>javascript:OpenSelectorWindow(1029, 200, 300);</xsl:text>
          </xsl:with-param>
        </xsl:call-template>
        &nbsp;
        <select name="newwindow">
          <option value="no">
            <xsl:if test="boolean($menuitem[1]/url[@newwindow = 'no'])">
              <xsl:attribute name="selected">selected</xsl:attribute>
            </xsl:if>
            %optURLOpenSameWindow%
          </option>
          <option value="yes">
            <xsl:if test="boolean($menuitem[1]/url[@newwindow = 'yes'])">
              <xsl:attribute name="selected">selected</xsl:attribute>
            </xsl:if>
            %optURLOpenNewWindow%
          </option>
        </select>
      </td>
    </tr>

  </xsl:template>

  <xsl:template name="type_pulldown">
    <xsl:param name="type"/>

    <input type="hidden" name="type" id="type" value="{$type}"/>

    <select name="gui_type_combo">
      <xsl:attribute name="onchange">
        <xsl:text>javascript: menuItem_removeContent(); if (this.value == 'url' || this.value == 'label' || this.value == 'section' || this.value == 'shortcut') { document.getElementById('type').value = this.value; reloadPage(); }</xsl:text>
        <xsl:text>else if (this.value != '') { document.getElementById('type').value = 'page'; reloadPageWithPageFramework(this.value, true); } </xsl:text>
      </xsl:attribute>

      <xsl:if test="$type = 'none'">
        <option value="" selected="selected">
          %sysDropDownChoose%
        </option>
      </xsl:if>

      <xsl:for-each select="/menus/pagetemplates/pagetemplate">
        <xsl:sort select="name"/>
        <option value="{@key}">
          <xsl:if test="$selpagetemplatekey2 = @key and $type != 'label' and $type != 'externalurl' and $type != 'none' and $type != 'section'">
            <xsl:attribute name="selected">selected</xsl:attribute>
          </xsl:if>
          %menuItemTypePage% (<xsl:value-of select="name"/>)
        </option>
      </xsl:for-each>

      <xsl:if test="string($menu/@allowsection) = 'true' or $menuitem/@type = 'section'">
        <option name="section" value="section">
          <xsl:if test="$type = 'section'">
            <xsl:attribute name="selected">
              <xsl:text>selected</xsl:text>
            </xsl:attribute>
          </xsl:if>
          <xsl:text>%optPageTypeSection%</xsl:text>
        </option>
      </xsl:if>

      <xsl:if test="string($menu/@allowlabel) = 'true'  or $menuitem/@type = 'label'">
        <option name="content" value="label">
          <xsl:if test="$type = 'label'">
            <xsl:attribute name="selected">
              <xsl:text>selected</xsl:text>
            </xsl:attribute>
          </xsl:if>
          <xsl:text>%optPageTypeLabel%</xsl:text>
        </option>
      </xsl:if>

      <xsl:if test="string($menu/@allowurl) = 'true'  or $menuitem/@type = 'url'">
        <option name="url" value="url">
          <xsl:if test="$type = 'externalurl'">
            <xsl:attribute name="selected">
              <xsl:text>selected</xsl:text>
            </xsl:attribute>
          </xsl:if>
          <xsl:text>%optPageTypeExtURL%</xsl:text>
        </option>
      </xsl:if>

      <xsl:if test="/menus/menu/menudata/pagetypes/allow[@type = 'shortcut'] or not(/menus/menu/menudata/pagetypes) or $menuitem/@type = 'shortcut'">
        <option name="shortcut" value="shortcut">
          <xsl:if test="$type = 'shortcut'">
            <xsl:attribute name="selected">
              <xsl:text>selected</xsl:text>
            </xsl:attribute>
          </xsl:if>
          <xsl:text>%optPageTypeShortcut%</xsl:text>
        </option>
      </xsl:if>

    </select>
  </xsl:template>

  <xsl:template name="formbuilder">
    <fieldset>
      <legend>&nbsp;%blockFormBuilder%&nbsp;</legend>
      <table width="100%" colspace="0" colpadding="2" border="0">
        <tr>
          <xsl:call-template name="textfield">
            <xsl:with-param name="label" select="'%fldTitle%:'"/>
            <xsl:with-param name="name" select="'form_title'"/>
            <xsl:with-param name="required" select="'true'"/>
            <xsl:with-param name="selectnode" select="$menuitem/data/form/title"/>
          </xsl:call-template>
        </tr>
        <tr>
          <script language="JavaScript" type="text/javascript">

                        // saveRadioState() and loadRadioState() is used because switching rows with multiple radio buttons doesn't work in IE (argh)
                        var radioButtons = new Array(3);
                        function saveRadioState() {
                        	radioButtons[0] = '';
                        	radioButtons[1] = '';
                        	radioButtons[2] = '';

                        	var radio1 = document.getElementsByName('field_form_title');
                        	if (radio1) {
                        		for (var i=0; i&lt;radio1.length; i++) {
                        			if (radio1[i].checked) {
	                        			radioButtons[0] = radio1[i].value;
	                        			break;
	                        		}
                        		}
                        	}

                        	var radio2 = document.getElementsByName('field_form_fromname');
                        	if (radio2) {
                        		for (var i=0; i&lt;radio2.length; i++) {
                        			if (radio2[i].checked) {
	                        			radioButtons[1] = radio2[i].value;
	                        			break;
	                        		}
                        		}
                        	}

                        	var radio3 = document.getElementsByName('field_form_fromemail');
                        	if (radio3) {
                        		for (var i=0; i&lt;radio3.length; i++) {
                        			if (radio3[i].checked) {
	                        			radioButtons[2] = radio3[i].value;
	                        			break;
	                        		}
                        		}
                        	}
                        }

                        function toggleReceipt() {

	                        var receiptSendEmail = document.getElementById('receiptSendEmail');
	                        var receiptTableToToggle = document.getElementById('receiptTableToToggle');

                        	if ( receiptSendEmail.checked  ) {
                        		receiptTableToToggle.style.display = '';
                        	} else {
                        		receiptTableToToggle.style.display = 'none';
                        	}
                        }

                        function loadRadioState() {
                        	var radio1 = document.getElementsByName('field_form_title');
                        	if (radio1) {
                        		for (var i=0; i&lt;radio1.length; i++) {
                        			radio1[i].checked = (radio1[i].value == radioButtons[0]);
                        		}
                        	}

                        	var radio2 = document.getElementsByName('field_form_fromname');
                        	if (radio2) {
                        		for (var i=0; i&lt;radio2.length; i++) {
                        			radio2[i].checked = (radio2[i].value == radioButtons[1]);
                        		}
                        	}

                        	var radio3 = document.getElementsByName('field_form_fromemail');
                        	if (radio3) {
                        		for (var i=0; i&lt;radio3.length; i++) {
                        			radio3[i].checked = (radio3[i].value == radioButtons[2]);
                        		}
                        	}
                        }

	                    // set correct disabled and disabled buttons
						          function setDisabledEnabledButtons()
						          {
                        var buttons = document.getElementsByName('field_moverupper');
                        if (buttons.length &gt; 0) {
                          buttons[0].setAttribute("disabled", 'disabled');
                          buttons[0].childNodes[0].src = 'images/icon_move_up-disabled.gif';
                          for (var i = 1; i &lt; buttons.length; ++i) {
                            if (buttons[i].disabled) {
                              buttons[i].removeAttribute("disabled");
                              buttons[i].childNodes[0].src = 'images/icon_move_up.gif';
                            }
                          }

                          buttons = document.getElementsByName('field_moverdowner');
                          buttons[buttons.length-1].setAttribute("disabled", "disabled");
                          buttons[buttons.length-1].childNodes[0].src = 'images/icon_move_down-disabled.gif';
                          for (var i = 0; i &lt; buttons.length - 1; ++i) {
                            if (buttons[i].disabled) {
                              buttons[i].removeAttribute("disabled");
                              buttons[i].childNodes[0].src = 'images/icon_move_down.gif';
                            }
                          }
                        }
                      }

                    </script>
                    <td valign="top" class="form_labelcolumn">&nbsp;</td>
          <td colspan="10">
            <table border="0" cellspacing="0" cellpadding="4">
              <tbody name="form_fieldtable" id="form_fieldtable">
                <tr>
                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="caption" select="'%fldTitle%'"/>
                    <xsl:with-param name="sortable" select="'false'"/>
                  </xsl:call-template>
                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="caption" select="'%fldFromName%'"/>
                    <xsl:with-param name="sortable" select="'false'"/>
                  </xsl:call-template>
                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="caption" select="'%fldFromMail%'"/>
                    <xsl:with-param name="sortable" select="'false'"/>
                  </xsl:call-template>
                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="caption" select="'%fldFormElementLabel%'"/>
                    <xsl:with-param name="sortable" select="'false'"/>
                    <xsl:with-param name="width" select="'200'"/>
                  </xsl:call-template>
                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="caption" select="'%fldFormElementType%'"/>
                    <xsl:with-param name="sortable" select="'false'"/>
                    <xsl:with-param name="width" select="'80'"/>
                  </xsl:call-template>
                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="sortable" select="'false'"/>
                    <xsl:with-param name="width" select="'90'"/>
                  </xsl:call-template>
                </tr>

                <xsl:for-each select="$menuitem/data/form/item">
                  <tr>
                    <td align="center">
                      <xsl:if test="@type = 'separator'">
                        <xsl:attribute name="class">formbuilder-seperator</xsl:attribute>
                      </xsl:if>
                      <xsl:choose>
                        <xsl:when test="@type = 'text' or @type = 'fileattachment'">
                          <input type="radio" name="field_form_title" id2="field_form_title" value="{@label}">
                            <xsl:if test="@title = 'true'">
                              <xsl:attribute name="checked">
                                <xsl:text>checked</xsl:text>
                              </xsl:attribute>
                            </xsl:if>
                          </input>
                        </xsl:when>
                        <xsl:otherwise>
                          <br/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </td>
                    <td align="center">
                      <xsl:if test="@type = 'separator'">
                        <xsl:attribute name="class">formbuilder-seperator</xsl:attribute>
                      </xsl:if>
                      <xsl:choose>
                        <xsl:when test="@type = 'text'">
                          <input type="radio" name="field_form_fromname" id2="field_form_fromname" value="{@label}">
                            <xsl:if test="@fromname = 'true'">
                              <xsl:attribute name="checked">
                                <xsl:text>checked</xsl:text>
                              </xsl:attribute>
                            </xsl:if>
                          </input>
                        </xsl:when>
                        <xsl:otherwise>
                          <br/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </td>
                    <td align="center">
                      <xsl:if test="@type = 'separator'">
                        <xsl:attribute name="class">formbuilder-seperator</xsl:attribute>
                      </xsl:if>
                      <xsl:choose>
                        <xsl:when test="@type = 'text' and @validation = '^.+@.+..+$'">
                          <input type="radio" name="field_form_fromemail" id2="field_form_fromemail" value="{@label}">
                            <xsl:if test="@fromemail = 'true'">
                              <xsl:attribute name="checked">
                                <xsl:text>checked</xsl:text>
                              </xsl:attribute>
                            </xsl:if>
                          </input>
                        </xsl:when>
                        <xsl:otherwise>
                          <br/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </td>
                    <td>
                      <xsl:if test="@type = 'separator'">
                        <xsl:attribute name="class">formbuilder-seperator</xsl:attribute>
                      </xsl:if>
                      <xsl:value-of select="@label"/>
                      <xsl:if test="@required = 'true'">
                        <span class="requiredfield"> *</span>
                      </xsl:if>
                    </td>
                    <td>
                      <xsl:if test="@type = 'separator'">
                        <xsl:attribute name="class">formbuilder-seperator</xsl:attribute>
                      </xsl:if>
                      <xsl:choose>
                        <xsl:when test="@type = 'separator'">
                          %optFieldSeparator%
                        </xsl:when>
                        <xsl:when test="@type = 'text'">
                          %optFieldText%
                        </xsl:when>
                        <xsl:when test="@type = 'textarea'">
                          %optFieldTextarea%
                        </xsl:when>
                        <xsl:when test="@type = 'checkbox'">
                          %optFieldCheckbox%
                        </xsl:when>
                        <xsl:when test="@type = 'checkboxes'">
                          %optFieldCheckboxes%
                        </xsl:when>
                        <xsl:when test="@type = 'radiobuttons'">
                          %optFieldRadiobuttons%
                        </xsl:when>
                        <xsl:when test="@type = 'dropdown'">
                          %optFieldDropdown%
                        </xsl:when>
                        <xsl:when test="@type = 'fileattachment'">
                          %optFieldFileAttachment%
                        </xsl:when>
                        <xsl:when test="@type = 'fromemail'">
                          %optFieldFromEmail%
                        </xsl:when>
                        <xsl:when test="@type = 'fromname'">
                          %optFieldFromName%
                        </xsl:when>
                      </xsl:choose>
                    </td>

                    <td>
                      <xsl:if test="@type = 'separator'">
                        <xsl:attribute name="class">formbuilder-seperator</xsl:attribute>
                      </xsl:if>
                      <input type="hidden" name="field_label" value="{@label}"/>
                      <input type="hidden" name="field_type" value="{@type}"/>
                      <input type="hidden" name="field_helptext" value="{help}"/>

                      <xsl:if test="@type = 'text' or @type = 'textarea' or @type = 'radiobuttons' or @type = 'fileattachment' or @type = 'dropdown' or @type = 'fromemail' or @type = 'fromname'">
                        <input type="hidden" name="field_required">
                          <xsl:attribute name="value">
                            <xsl:choose>
                              <xsl:when test="@required = 'true'">
                                <xsl:text>true</xsl:text>
                              </xsl:when>
                              <xsl:otherwise>
                                <xsl:text>false</xsl:text>
                              </xsl:otherwise>
                            </xsl:choose>
                          </xsl:attribute>
                        </input>

                        <xsl:if test="@type = 'text' or @type = 'textarea' or @type = 'fromemail' or @type = 'fromname'">
                          <input type="hidden" name="field_width" value="{@width}"/>
                        </xsl:if>
                      </xsl:if>

                      <xsl:choose>
                        <xsl:when test="@type = 'textarea'">
                          <input type="hidden" name="field_height" value="{@height}"/>
                        </xsl:when>

                        <xsl:when test="@type = 'checkbox'">
                          <input type="hidden" name="field_defaultvalue">
                            <xsl:attribute name="value">
                              <xsl:choose>
                                <xsl:when test="@default='checked'">
                                  <xsl:text>checked</xsl:text>
                                </xsl:when>
                                <xsl:otherwise>
                                  <xsl:text>notchecked</xsl:text>
                                </xsl:otherwise>
                              </xsl:choose>
                            </xsl:attribute>
                          </input>
                        </xsl:when>

                        <xsl:when test="@type = 'radiobuttons' or @type = 'dropdown'">
                          <xsl:for-each select="data/option">
                            <input type="hidden" name="field_value" value="{@value}"/>
                            <xsl:if test="@default = 'true'">
                              <input type="hidden" name="field_checkedindex" value="{position() - 1}"/>
                            </xsl:if>
                          </xsl:for-each>

                          <xsl:if test="not(data/option/@default)">
                            <input type="hidden" name="field_checkedindex" value="-1"/>
                          </xsl:if>

                          <input type="hidden" name="field_count" value="{count(data/option)}"/>
                        </xsl:when>

                        <xsl:when test="@type = 'checkboxes'">
                          <xsl:for-each select="data/option">
                            <input type="hidden" name="field_value" value="{@value}"/>
                            <xsl:choose>
                              <xsl:when test="@default = 'true'">
                                <input type="hidden" name="field_defaultchk" value="1"/>
                              </xsl:when>
                              <xsl:otherwise>
                                <input type="hidden" name="field_defaultchk" value="0"/>
                              </xsl:otherwise>
                            </xsl:choose>
                          </xsl:for-each>

                          <input type="hidden" name="field_count" value="{count(data/option)}"/>
                        </xsl:when>

                        <xsl:when test="@type = 'text' or @type = 'fromemail' or @type = 'fromname'">
                          <input type="hidden" name="field_defaultvalue" value="{data}"/>
                          <input type="hidden" name="field_regexp" value="{@validation}"/>
                          <input type="hidden" name="field_validation" value="{@validationtype}"/>
                        </xsl:when>
                      </xsl:choose>

                      <button type="button" name="formbuilder_editbutton" class="button_image_small" onclick="formbuilder_editField(this)">
                        <img alt="%cmdEdit%" src="images/icon_browse.gif" border="0"/>
                      </button>

                      <button type="button" class="button_image_small" name="field_moverdowner" onclick="saveRadioState();moveTableRowDown('form_fieldtable', getObjectIndex(this) + 1);loadRadioState();">
                        <img alt="%cmdMoveDown%" src="images/icon_move_down.gif" border="0"/>
                      </button>

                      <button type="button" class="button_image_small" name="field_moverupper" onclick="saveRadioState();moveTableRowUp('form_fieldtable', getObjectIndex(this) + 1);loadRadioState();">
                        <img alt="%cmdMoveUp%" src="images/icon_move_up.gif" border="0"/>
                      </button>

                      <button type="button" class="button_image_small" name="formbuilder_removebutton" onclick="javascript:removeTableRow(this, 'form_fieldtable', null, 1);">
                        <img alt="%cmdRemove%" src="images/icon_remove.gif" border="0"/>
                      </button>
                    </td>

                  </tr>
                </xsl:for-each>
              </tbody>
            </table>
          </td>
        </tr>
        <tr>
          <td><br/></td>
          <td>
            <input type="button" value="%cmdNewRow%" class="button_text">
              <xsl:attribute name="onclick">
                <xsl:text>javascript:showPopupWindow('adminpage?page=</xsl:text>
                <xsl:value-of select="$page"/>
                <xsl:text>&amp;op=formbuilder&amp;subop=typeselector', 'fieldselector', 500, 500);</xsl:text>
              </xsl:attribute>
            </input>
          </td>
        </tr>

      </table>
    </fieldset>

    <fieldset>

      <legend>%blockSendAndStore%</legend>

      <table width="100%" colspace="0" colpadding="2" border="0">

        <tr>
          <xsl:call-template name="searchfield">
            <xsl:with-param name="label" select="'%fldStoreResponses%:'"/>
            <xsl:with-param name="name" select="'category_key'"/>
            <xsl:with-param name="required" select="'true'"/>
            <xsl:with-param name="selectedkey" select="$menuitem/data/form/@categorykey"/>
            <xsl:with-param name="selectnode" select="/menus/categorynames/categoryname[@categorykey = $menuitem/data/form/@categorykey]"/>
            <xsl:with-param name="buttonfunction">
              <xsl:text>OpenNewCategorySelector(</xsl:text>
              <xsl:text>-1, '</xsl:text>
              <xsl:value-of select="$contenttypestring"/>
              <xsl:text>', null, false, null, -1);</xsl:text>
            </xsl:with-param>
            <xsl:with-param name="add-nowrap-on-label-column" select="'false'"/>
          </xsl:call-template>
        </tr>

        <tr>
          <td class="form_labelcolumn" valign="top">%fldSendTo%:</td>
          <td>

            <table border="0" cellspacing="0" cellpadding="0" class="form_sendtotable">
              <tbody id="form_sendtotable">
                <xsl:choose>
                  <xsl:when test="count($menuitem/data/form/recipients/e-mail) > 0">
                    <xsl:for-each select="$menuitem/data/form/recipients/e-mail">
                      <tr>
                        <td>
                          <input type="text" name="form_sendto" value="{.}"/>
                          <xsl:call-template name="button">
                            <xsl:with-param name="name">removeSendTo</xsl:with-param>
                            <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                            <xsl:with-param name="onclick">
                              <xsl:text>javascript:removeTableRow(this, 'form_sendtotable',</xsl:text>
                              <xsl:text>'clearNewRow(\'form_sendtotable\', [\'form_sendto\'])', 0);</xsl:text>
                            </xsl:with-param>
                          </xsl:call-template>
                        </td>
                      </tr>
                    </xsl:for-each>
                  </xsl:when>
                  <xsl:otherwise>
                    <tr>
                      <td>
                        <input type="text" name="form_sendto"/>
                        <xsl:call-template name="button">
                          <xsl:with-param name="name">removeSendTo</xsl:with-param>
                          <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                          <xsl:with-param name="onclick">
                            <xsl:text>javascript:removeTableRow(this, 'form_sendtotable',</xsl:text>
                            <xsl:text>'clearNewRow(\'form_sendtotable\', [\'form_sendto\'])', 0);</xsl:text>
                          </xsl:with-param>
                        </xsl:call-template>
                      </td>
                    </tr>
                  </xsl:otherwise>
                </xsl:choose>
              </tbody>
            </table>
          </td>
        </tr>

        <tr>
          <td class="form_labelcolumn">&nbsp;</td>
          <td>
            <script language="JavaScript" type="text/javascript">
              function form_addRecipient() {
              addTableRow('form_sendtotable', 0);
              clearNewRow('form_sendtotable', ['form_sendto']);
              }
            </script>

            <input type="button" value="%cmdNewRecipient%" class="button_text">
              <xsl:attribute name="onclick">
                <xsl:text>javascript:form_addRecipient();</xsl:text>
              </xsl:attribute>
            </input>
          </td>
        </tr>

      </table>
    </fieldset>

    <fieldset>
      <legend>%blockReceipt%</legend>
      <table width="100%" colspace="0" colpadding="2" border="0">
        <tr>
          <td class="form_labelcolumn"><xsl:text>%fldSendReceipt%:&nbsp;</xsl:text></td>
          <td>
            <input type="checkbox" name="receiptSendEmail" id="receiptSendEmail">
              <xsl:if test="$menuitem/data/form/receipt/sendreceipt = 'yes'">
                <xsl:attribute name="checked">true</xsl:attribute>
              </xsl:if>
              <xsl:attribute name="onclick">javascript:toggleReceipt()</xsl:attribute>
            </input>
          </td>
        </tr>
      </table>

      <xsl:variable name="style">
        <xsl:choose>
          <xsl:when test="$menuitem/data/form/receipt/sendreceipt = 'yes'">
            <xsl:text>display:''</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>display:none</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <table id="receiptTableToToggle" style="{$style}">
        <tr>
          <!--td class="form_labelcolumn"><xsl:text>%fldReceiptFromName%:</xsl:text><span class="requiredfield">*</span>&nbsp;</td>
              <td>
              	<input type="text" name="receiptFromName" id="receiptFromName" value="{$menuitem/data/form/receipt/name}"/>
              </td-->

          <xsl:call-template name="textfield">
            <xsl:with-param name="id" select="'receiptFromName'"/>
            <xsl:with-param name="name" select="'receiptFromName'"/>
            <xsl:with-param name="label" select="'%fldReceiptFromName%:'"/>
            <xsl:with-param name="selectnode" select="$menuitem/data/form/receipt/name"/>
            <xsl:with-param name="required" select="'true'"/>
          </xsl:call-template>
        </tr>
        <tr>
          <!--td class="form_labelcolumn"><xsl:text>%fldReceiptFromAddress%:</xsl:text><span class="requiredfield">*</span>&nbsp;</td>
              <td>
              	<input type="text" name="receiptFromAddress" id="receiptFromAddress" value="{$menuitem/data/form/receipt/email}"/>
              </td-->
          <xsl:call-template name="textfield">
            <xsl:with-param name="id" select="'receiptFromAddress'"/>
            <xsl:with-param name="name" select="'receiptFromAddress'"/>
            <xsl:with-param name="label" select="'%fldReceiptFromAddress%:'"/>
            <xsl:with-param name="selectnode" select="$menuitem/data/form/receipt/email"/>
            <xsl:with-param name="required" select="'true'"/>
          </xsl:call-template>
        </tr>
        <tr>
          <!--td class="form_labelcolumn"><xsl:text>%fldReceiptSubject%:</xsl:text><span class="requiredfield">*</span>&nbsp;</td>
            	<td><input type="text" name="receiptSubject" id="receiptSubject" value="{$menuitem/data/form/receipt/subject}"/></td-->
          <xsl:call-template name="textfield">
            <xsl:with-param name="id" select="'receiptSubject'"/>
            <xsl:with-param name="name" select="'receiptSubject'"/>
            <xsl:with-param name="label" select="'%fldReceiptSubject%:'"/>
            <xsl:with-param name="selectnode" select="$menuitem/data/form/receipt/subject"/>
            <xsl:with-param name="required" select="'true'"/>
          </xsl:call-template>
        </tr>
        <tr>
          <xsl:call-template name="textarea">
            <xsl:with-param name="label" select="'%fldReceiptMessage%:&nbsp;'"/>
            <xsl:with-param name="name" select="'receiptMessage'"/>
            <xsl:with-param name="id" select="'receiptMessage'"/>
            <xsl:with-param name="selectnode" select="$menuitem/data/form/receipt/message"/>
            <xsl:with-param name="rows" select="'4'"/>
            <xsl:with-param name="width" select="'600'"/>
            <xsl:with-param name="required" select="'true'"/>
          </xsl:call-template>
        </tr>
        <tr>
          <td class="form_labelcolumn"><xsl:text>%fldReceiptIncludeSubmittedFormData%:&nbsp;</xsl:text></td>
          <td>
            <input type="checkbox" name="receiptIncludeSubmittedFormData" id="receiptIncludeSubmittedFormData">
              <xsl:if test="$menuitem/data/form/receipt/includeform = 'yes'">
                <xsl:attribute name="checked">true</xsl:attribute>
              </xsl:if>
            </input>
          </td>
        </tr>
      </table>
    </fieldset>

    <fieldset>
      <legend>%blockConfirmationMessage%</legend>
      <table width="100%" colspace="0" colpadding="2" border="0">
        <tr>
          <td width="120" class="form_labelcolumn" valign="top" nowrap="nowrap" >%blockConfirmation%:</td>
          <td colspan="2">
            <xsl:variable name="template">
              <xsl:choose>
                <xsl:when test="$menuitem[1]/page/@pagetemplatekey!='' and $selpagetemplatekey2 = ''">
                  <xsl:value-of select="$menuitem[1]/page/@pagetemplatekey"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="$selpagetemplatekey2"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <xsl:variable name="css">
              <xsl:value-of select="/menus/pagetemplates/pagetemplate[@key = $template]/css/@stylesheetkey"/>
            </xsl:variable>

            <xsl:variable name="confirmation">
              <xsl:if test="$menuitem/data/form">
                <xsl:copy-of select="$menuitem/data/form/confirmation"/>
              </xsl:if>
            </xsl:variable>
            <xsl:call-template name="xhtmleditor">
              <xsl:with-param name="id" select="'form_confirmation'"/>
              <xsl:with-param name="name" select="'form_confirmation'"/>
              <xsl:with-param name="content" select="$confirmation"/>
              <xsl:with-param name="configxpath" select="/menus/htmleditorconfig"/>
              <xsl:with-param name="config" select="'document'"/>
              <xsl:with-param name="customcss" select="$css"/>
              <xsl:with-param name="height" select="300"/>
              <xsl:with-param name="menukey" select="$menukey"/>
              <xsl:with-param name="disabled" select="false()"/>
              <xsl:with-param name="accessToHtmlSource" select="$accessToHtmlSource = 'true'"/>
            </xsl:call-template>
          </td>
        </tr>
      </table>
    </fieldset>

  </xsl:template>

  <xsl:template name="find_parent_framework">
    <xsl:param name="menuitem"/>

    <xsl:choose>
      <xsl:when test="not(boolean($menuitem)) and boolean($insertbelow)">
        <xsl:call-template name="find_parent_framework">
          <xsl:with-param name="menuitem" select="//menuitem[@key = $insertbelow][1]"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="boolean($menuitem/page)">
        <xsl:value-of select="$menuitem/page/@pagetemplatekey"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="$menuitem/parent::node()">
            -1
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="find_parent_framework">
              <xsl:with-param name="menuitem" select="$menuitem/parent::node()"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="generateheader">
    <xsl:param name="menuitem"/>
    <xsl:if test="boolean($menuitem/parent::node())">
      <xsl:call-template name="generateheader">
        <xsl:with-param name="menuitem" select="$menuitem/parent::node()"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:if test="$menuitem/self::menuitem">
      <xsl:if test="not($menuitem/name)">
        <xsl:text> / </xsl:text>
        <a href="adminpage?op=browse&amp;page={$page}&amp;parentmi={$menuitem/@key}&amp;selectedunitkey={$selectedunitkey}&amp;menukey={$menukey}">
          <xsl:choose>
            <xsl:when test="$menuitem/name">
              <xsl:value-of select="$menuitem/name"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$menuitem/@name"/>
            </xsl:otherwise>
          </xsl:choose>
        </a>
      </xsl:if>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
