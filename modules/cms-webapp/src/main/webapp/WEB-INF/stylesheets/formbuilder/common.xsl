<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
	<!ENTITY Oslash "&#216;">
	<!ENTITY oslash "&#248;">
	<!ENTITY Aring  "&#197;">
	<!ENTITY aring  "&#229;">
	<!ENTITY AElig  "&#198;">
	<!ENTITY aelig  "&#230;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

    <xsl:output method="html"/>

    <xsl:param name="page"/>
    <xsl:param name="type" select="'none'"/>
    <xsl:param name="row" select="''"/>

    <xsl:include href="../common/textfield.xsl"/>
    <xsl:include href="../common/labelcolumn.xsl"/>
    <xsl:include href="../common/textarea.xsl"/>
    <xsl:include href="../common/button.xsl"/>
    <xsl:include href="../common/displayhelp.xsl"/>
    <xsl:include href="../common/displayerror.xsl"/>
    <xsl:include href="../common/serialize.xsl"/>

    <xsl:template match="/">
        <html>
            <head>
                <link href="css/admin.css" rel="stylesheet" type="text/css">
                </link>
                <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css">
                </link>
                <script type="text/javascript" language="JavaScript" src="javascript/admin.js">
                </script>
                <script type="text/javascript" language="JavaScript" src="javascript/accessrights.js">
                </script>
                <script type="text/javascript" language="JavaScript" src="javascript/validate.js">
                </script>
                <script type="text/javascript" language="JavaScript" src="javascript/tabpane.js">
                </script>

                <script language="JavaScript" type="text/javascript">
					// constructor for the field prototype class
					function FieldPrototype(type, label, helptext)
					{
						this.type = type;
						this.label = label;
						this.helptext = helptext;

						this.addButtons = FieldPrototype_addButtons;
						this.insertField = FieldPrototype_insertField;
						this.updateField = FieldPrototype_updateField;
						this.cssClassSeperator = 'formbuilder-seperator';
					}

                    // insert the separator into the field table in the parent window
                    function FieldPrototype_insertField()
					{
						// get table/tbody
						var ownerDoc = window.opener.document;
						var table = ownerDoc.getElementById('form_fieldtable');

						// create tr element
						var tr = ownerDoc.createElement('tr');

						//tr.style.display = 'none';
						tr.setAttribute('style','display:none');

						this.addFields(ownerDoc, table, tr);

						//tr.style.display = '';
						tr.setAttribute('style','display:');

						table.appendChild(tr);

          }

          function FieldPrototype_updateField()
					{
            <xsl:if test="$row != ''">
              var doc = window.opener.document;
              var table = doc.getElementById('form_fieldtable');

              var tr = table.rows[<xsl:value-of select="$row"/>];
              //tr.style.display = 'none';
              tr.setAttribute('style','display:none');

              while (tr.childNodes.length != 0) {
                tr.removeChild(tr.childNodes[0]);
              }

              this.addFields(doc, table, tr);
              tr.setAttribute('style','display:');
            </xsl:if>
          }

          function FieldPrototype_addButtons(doc, tr, type)
					{
						tr.id = 'foobar';
						// add td element with buttons
						//var button_td = tr.insertCell();
						var button_td = doc.createElement('td');

						/***********************************************************************************
						// create button for editing this field
						removeButton = doc.createElement('button');
						removeButton.setAttribute('type','button');
						removeButton.setAttribute('name','formbuilder_editbutton');
						removeButton.setAttribute('class','button_image_small');
						removeButton.setAttribute('className','button_image_small');
						removeButton.onclick = function()
						{
							formbuilder_editField(this);
						}
						var img = doc.createElement('img');
						img.setAttribute('alt','%cmdEdit%');
						img.setAttribute('src','images/icon_browse.gif');
						img.setAttribute('border',0);
						removeButton.appendChild(img);
						button_td.appendChild(removeButton);

						// create button for moving the field down
						removeButton = doc.createElement('button');
						removeButton.setAttribute('type','button');
						removeButton.setAttribute('name','field_moverdowner');
						removeButton.setAttribute('class','button_image_small');
						removeButton.setAttribute('className','button_image_small');
						removeButton.onclick = function()
						{
							moveTableRowDown('form_fieldtable', getObjectIndex(this) + 1);
							setDisabledEnabledButtons();
						}
						var img = doc.createElement('img');
						img.setAttribute('alt','%cmdMoveDown%');
						img.setAttribute('src','images/icon_move_down.gif');
						img.setAttribute('border',0);
						removeButton.appendChild(img);
						button_td.appendChild(removeButton);

						// create button for moving the field up
						removeButton = doc.createElement('button');
						removeButton.setAttribute('type','button');
						removeButton.setAttribute('name','field_moverupper');
						removeButton.setAttribute('class','button_image_small');
						removeButton.setAttribute('className','button_image_small');
						removeButton.onclick = function()
						{
							moveTableRowUp('form_fieldtable', getObjectIndex(this) + 1);
							setDisabledEnabledButtons();
						}
						var img = doc.createElement('img');
						img.setAttribute('alt','%cmdMoveUp%');
						img.setAttribute('src','images/icon_move_up.gif');
						img.setAttribute('border',0);
						removeButton.appendChild(img);
						button_td.appendChild(removeButton);

						// create button for removal
						removeButton = doc.createElement('button');
						removeButton.setAttribute('type','button');
						removeButton.setAttribute('name','formbuilder_removebutton');
						removeButton.setAttribute('class','button_image_small');
						removeButton.setAttribute('className','button_image_small');
						//removeButton.setAttribute('onclick', );

						removeButton.onclick = function()
						{
							removeTableRow(this, 'form_fieldtable', null, 1);
							setDisabledEnabledButtons();
						}

						var img = doc.createElement('img');
						img.setAttribute('alt','%cmdRemove%');
						img.setAttribute('src','images/icon_remove.gif');
						img.setAttribute('border',0);
						removeButton.appendChild(img);
						button_td.appendChild(removeButton);
						************************************************************************************/

						/**
						 * Can't find a DOM solution to attach events to parent element from a child window. Using innerHTML.
						 */

						button_td.innerHTML = '<button type="button" name="formbuilder_editbutton" class="button_image_small" onclick="formbuilder_editField(this)"><img alt="%cmdEdit%" src="images/icon_browse.gif" border="0"/></button>';
						button_td.innerHTML += '<button type="button" class="button_image_small" name="field_moverdowner" onclick="saveRadioState();moveTableRowDown(\'form_fieldtable\', getObjectIndex(this) + 1);setDisabledEnabledButtons();loadRadioState();"><img alt="%cmdMoveDown%" src="images/icon_move_down.gif" border="0"/></button>';
						button_td.innerHTML += '<button type="button" class="button_image_small" name="field_moverupper" onclick="saveRadioState();moveTableRowUp(\'form_fieldtable\', getObjectIndex(this) + 1);setDisabledEnabledButtons();loadRadioState();"><img alt="%cmdMoveUp%" src="images/icon_move_up.gif" border="0"/></button>';
						button_td.innerHTML += '<button type="button" class="button_image_small" name="formbuilder_removebutton" onclick="javascript:removeTableRow(this, \'form_fieldtable\', null, 1);setDisabledEnabledButtons();"><img alt="%cmdRemove%" src="images/icon_remove.gif" border="0"/></button>';

						/*********************************************************************************************
						// create button for editing this field
						var removeButton = doc.createElement('<button name="formbuilder_editbutton" class="button_image_small" onclick="formbuilder_editField(this)"></button>');
						removeButton.appendChild(doc.createElement('<img alt="%cmdEdit%" src="images/icon_browse.gif" border="0"/>'));
						button_td.appendChild(removeButton);

						// create button for moving the field down
						var removeButton = doc.createElement('<button class="button_image_small" name="field_moverdowner" onclick="moveTableRowDown(\'form_fieldtable\', getObjectIndex(this) + 1);setDisabledEnabledButtons();"></button>');
						removeButton.appendChild(doc.createElement('<img alt="%cmdMoveDown%" src="images/icon_move_down.gif" border="0"/>'));
						button_td.appendChild(removeButton);

						// create button for moving the field up
						var removeButton = doc.createElement('<button class="button_image_small" name="field_moverupper" onclick="moveTableRowUp(\'form_fieldtable\', getObjectIndex(this) + 1);setDisabledEnabledButtons();"></button>');
						removeButton.appendChild(doc.createElement('<img alt="%cmdMoveUp%" src="images/icon_move_up.gif" border="0"/>'));
						button_td.appendChild(removeButton);

						// create button for removal
						var removeButton = doc.createElement('<button class="button_image_small" name="formbuilder_removebutton" onclick="javascript:removeTableRow(this, \'form_fieldtable\', null, 1);setDisabledEnabledButtons();"></button>');
						removeButton.appendChild(doc.createElement('<img alt="%cmdRemove%" src="images/icon_remove.gif" border="0"/>'));
						button_td.appendChild(removeButton);
						*********************************************************************************************/

						tr.appendChild(button_td);
						if (type == 'seperator')
						{
							button_td.className = this.cssClassSeperator;
						}
          }

                    <xsl:call-template name="formbuilder_javascript"/>


                    // reload the form with the new type
                    function changeType(type) {
                      window.location = 'adminpage?page='+ <xsl:value-of select="$page"/>
                        +'&amp;op=formbuilder&amp;subop=typeselector&amp;type='+ type
                        <xsl:if test="$row != ''">+ '&amp;row=<xsl:value-of select="$row"/>'</xsl:if>;
                    }

                    function formbuilderAction() {
                      var field = getObject();

                    <xsl:if test="$type != 'none'">
                        if (!validateRequired(document.getElementById('field_label'), '%fldLabel%', 'tab-1'))
                        {
                          return;
                        }

                    </xsl:if>

                  
                    var currentFieldIndex;
                    <xsl:choose>
                      <xsl:when test="$row != ''">
                        currentFieldIndex = <xsl:value-of select="number($row) - 1"/>;
                      </xsl:when>
                      <xsl:otherwise>
                        currentFieldIndex = -1;
                      </xsl:otherwise>
                    </xsl:choose>

                    var g_fieldLabelValue = document.getElementById('field_label').value;

                    if ( window.opener.formbuilder_labelValueExist(g_fieldLabelValue, currentFieldIndex ) )
                    {
                      error(document.getElementById('field_label'), '%errFormBuilderLabelExists%: ' + g_fieldLabelValue, 'tab-1');
                      return;
                    }

                    <xsl:choose>
                      <xsl:when test="$row != ''">
                        field.updateField();
                      </xsl:when>
                      <xsl:otherwise>
                        field.insertField();
                      </xsl:otherwise>
                    </xsl:choose>

                      window.close();
					            window.top.opener.setDisabledEnabledButtons();

                    }

                    function fillFields() {
                    <xsl:if test="$row != ''">
                            <!-- Fill in data from table in parent window -->

                                var doc = window.opener.document;
                                var table = doc.getElementById('form_fieldtable');
                                var tr = table.rows[<xsl:value-of select="$row"/>];

                                var inputElements = tr.getElementsByTagName('input');
                                for (var i = 0; i &lt; inputElements.length; ++i) {
                                        var name = inputElements[i].name;

                                        var obj = document.getElementsByName(name);
                                        if (obj.length &gt;= 1) {
                                                if (obj[0].type == 'checkbox'
                                                    &amp;&amp; inputElements[i].value == 'true') {
                                                        obj[0].checked = true;
                                                }
                                                else if (obj[0].type == 'radio' || obj[0].type == 'dropdown') {
                                                        for (var j = 0; j &lt; obj.length; j++) {
                                                                if (obj[j].value == inputElements[i].value) {
                                                                        obj[j].checked = true;
                                                                        break;
                                                                }
                                                        }
                                                }
                                                else {
                                                        if (obj.length == 1)
                                                                obj[0].value = inputElements[i].value;

                                                        if (obj[0].tagName == 'SELECT') {
                                                                for (var j = 0; j &lt; obj[0].options.length; ++j) {
                                                                        if (obj[0].options[j].value == obj[0].value) {
                                                                                obj[0].options[j].selected = true;
                                                                                break;
                                                                        }
                                                                }
                                                        }
                                                }
                                        }
                                }
                    </xsl:if>
                   }

				   function formbuilder_setFocus()
				   {
						document.forms['fieldform']['field_label'].focus();
				   }
                </script>

            </head>
          <body id="popup">
            <form name="fieldform">
              <table border="0" cellspacing="2" cellpadding="0">
                <tr>
                  <td>%fldFormElementType%:</td>
                  <td>
                    <xsl:text>&nbsp;</xsl:text>
                    <select name="fieldtype" id="fieldtype" onchange="javascript:changeType(this.value);">
                      <option value="">%sysDropDownChoose%</option>

                      <option value="checkbox">
                        <xsl:if test="$type = 'checkbox'">
                          <xsl:attribute name="selected">selected</xsl:attribute>
                        </xsl:if>
                        %optFieldCheckbox%
                      </option>

                      <option value="checkboxes">
                        <xsl:if test="$type = 'checkboxes'">
                          <xsl:attribute name="selected">selected</xsl:attribute>
                        </xsl:if>
                        %optFieldCheckboxes%
                      </option>

                      <option value="dropdown">
                        <xsl:if test="$type = 'dropdown'">
                          <xsl:attribute name="selected">selected</xsl:attribute>
                        </xsl:if>
                        %optFieldDropdown%
                      </option>

                      <option value="fileattachment">
                        <xsl:if test="$type = 'fileattachment'">
                          <xsl:attribute name="selected">selected</xsl:attribute>
                        </xsl:if>
                        %optFieldFileAttachment%
                      </option>
                      
                      <option value="radiobuttons">
                        <xsl:if test="$type = 'radiobuttons'">
                          <xsl:attribute name="selected">selected</xsl:attribute>
                        </xsl:if>
                        %optFieldRadiobuttons%
                      </option>

                      <option value="separator">
                        <xsl:if test="$type = 'separator'">
                          <xsl:attribute name="selected">selected</xsl:attribute>
                        </xsl:if>
                        %optFieldSeparator%
                      </option>
                      
                      <option value="text">
                        <xsl:if test="$type = 'text'">
                          <xsl:attribute name="selected">selected</xsl:attribute>
                        </xsl:if>
                        %optFieldText%
                      </option>
                      
                      <option value="textarea">
                        <xsl:if test="$type = 'textarea'">
                          <xsl:attribute name="selected">selected</xsl:attribute>
                        </xsl:if>
                        %optFieldTextarea%
                      </option>
                      
                    </select>
                  </td>
                </tr>
                <tr>
                  <td>
                    <br/>
                  </td>
                </tr>
              </table>
              <!--xsl:if test="$type = 'none'">
                <script type="text/javascript">
        changeType('text');
      </script>
              </xsl:if-->
              <xsl:if test="$type != 'none'">
                <div class="tab-pane" id="tab-pane-1">
                  <script type="text/javascript" language="JavaScript">
                    tabPane = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                  </script>

                  <div class="tab-page" id="tab-1">
                    <span class="tab">%blockFormField%</span>

                    <script type="text/javascript" language="JavaScript">
                      tabPane.addTabPage( document.getElementById( "tab-1" ) );
                    </script>

                    <fieldset>
                      <legend>%blockConfigureFieldItem%</legend>

                      <table border="0" cellspacing="2" cellpadding="0">
                        <xsl:call-template name="formbuilder_body"/>

                        <script language="JavaScript" type="text/javascript">
                          fillFields();
                        </script>

                      </table>
                    </fieldset>

                  </div>
                </div>

                <table border="0" cellspacing="2" cellpadding="0">
                  <tr>
                    <td colspan="2">
                      <xsl:call-template name="button">
                        <xsl:with-param name="caption">%cmdDone%</xsl:with-param>
                        <xsl:with-param name="onclick">
                          <xsl:text>javascript:formbuilderAction();</xsl:text>
                        </xsl:with-param>
                      </xsl:call-template>

                      <xsl:text>&nbsp;</xsl:text>

                      <xsl:call-template name="button">
                        <xsl:with-param name="caption">%cmdCancel%</xsl:with-param>
                        <xsl:with-param name="onclick">
                          <xsl:text>javascript:window.close();</xsl:text>
                        </xsl:with-param>
                      </xsl:call-template>
                    </td>
                  </tr>
                </table>

              </xsl:if>

              <script type="text/javascript" language="JavaScript">
                setupAllTabs();
              </script>


            </form>


          </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
