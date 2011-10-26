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

  <xsl:include href="common.xsl"/>
  <xsl:include href="../common/checkbox_boolean.xsl"/>
  <xsl:include href="../common/tablecolumnheader.xsl"/>

    <xsl:template name="formbuilder_javascript">

        // constructor for the Text class
		function Checkboxes()
		{
			this.addFields = Checkboxes_addFields;
        }

        function Checkboxes_addFields(ownerDoc, table, tr)
		{
			// empty td element
			//tr.insertCell();
			var title_td = ownerDoc.createElement('td');
			title_td.setAttribute('align', 'center');

			// from name
			var fromname_td = ownerDoc.createElement('td');

			// from email
			var fromemail_td = ownerDoc.createElement('td');

			// label td
			//var label_td = tr.insertCell();
			var label_td = ownerDoc.createElement('td');
			var _label = ownerDoc.createTextNode(this.label);
			label_td.appendChild(_label);

			// type td
			//var type_td = tr.insertCell();
			var type_td = ownerDoc.createElement('td');
			var _type = ownerDoc.createTextNode('%optFieldCheckboxes%');
			type_td.appendChild(_type);

			// add hidden form elements for storing data ------------------------------------

			// type
			element = createNamedElement(ownerDoc, 'input','field_type');
			element.setAttribute('type','hidden');
			element.setAttribute('value',this.type);
			label_td.appendChild(element);

			// label
			element = createNamedElement(ownerDoc, 'input','field_label');
			element.setAttribute('type','hidden');
			element.setAttribute('value',this.label);
			label_td.appendChild(element);

			/*
			// required
			element = ownerDoc.createElement('input');
			element.setAttribute('type','hidden');
			element.setAttribute('name','field_required');
			element.setAttribute('value',document.getElementsByName('field_required')[0].checked);
			label_td.appendChild(element);
			*/

			// values
			var values = document.getElementsByName('field_value');
			for (var i = 0; i &lt; values.length; ++i)
			{
				element = createNamedElement(ownerDoc, 'input','field_value');
				element.setAttribute('type','hidden');
				element.setAttribute('value',values[i].value);
				label_td.appendChild(element);
			}

			// value count
			element = createNamedElement(ownerDoc, 'input','field_count');
			element.setAttribute('type','hidden');
			element.setAttribute('value',document.getElementsByName('field_value').length);
			label_td.appendChild(element);

			// checked index
			var checkedIndicies = "";
			var checkBoxen = document.getElementsByName('default');

			for (var i = 0; i &lt; checkBoxen.length; ++i)
			{
				if (checkBoxen[i].value == 1)
				{
					element = createNamedElement(ownerDoc, 'input','field_defaultchk');
					element.setAttribute('type','hidden');
					element.setAttribute('value',1);
					label_td.appendChild(element);
                }
				else
				{
					element = createNamedElement(ownerDoc, 'input','field_defaultchk');
					element.setAttribute('type','hidden');
					element.setAttribute('value',0);
					label_td.appendChild(element);
                }
			}

			// helptext
			element = createNamedElement(ownerDoc, 'input','field_helptext');
			element.setAttribute('type','hidden');
			element.setAttribute('value',this.helptext);
			label_td.appendChild(element);

			tr.appendChild(title_td);
			tr.appendChild(fromname_td);
			tr.appendChild(fromemail_td);
			tr.appendChild(label_td);
			tr.appendChild(type_td);

			/**************************************************************************************************
			// type
			var element = ownerDoc.createElement('<input type="hidden" name="field_type" value="'+
                                           this.type +'" />');
			label_td.appendChild(element);

			// label
			element = ownerDoc.createElement('<input type="hidden" name="field_label" value="'+
                                               this.label +'" />');
			label_td.appendChild(element);

			// required
			//element = ownerDoc.createElement('<input type="hidden" name="field_required" value="'+
			//                                  document.getElementsByName('field_required')[0].checked +'" />');
			//label_td.appendChild(element);

			// values
			var values = document.getElementsByName('field_value');
			for (var i = 0; i &lt; values.length; ++i)
			{
				element = ownerDoc.createElement('<input type="hidden" name="field_value" value="'+
                                                 values[i].value +'" />');
				label_td.appendChild(element);
			}

			// value count
			element = ownerDoc.createElement('<input type="hidden" name="field_count" value="'+ document.getElementsByName('field_value').length +'" />');
			label_td.appendChild(element);

			// checked index
			var checkedIndicies = "";
			var checkBoxen = document.getElementsByName('default');
			for (var i = 0; i &lt; checkBoxen.length; ++i)
			{
			if (checkBoxen[i].value == 1)
			{
				element = ownerDoc.createElement('<input type="hidden" name="field_defaultchk" value="1" />');
				label_td.appendChild(element);
                }
				else
				{
					element = ownerDoc.createElement('<input type="hidden" name="field_defaultchk" value="0" />');
                    label_td.appendChild(element);
                }
			}

			// helptext
			element = ownerDoc.createElement('<input type="hidden" name="field_helptext" value="'+
                                           this.helptext +'" />');
			label_td.appendChild(element);
			*************************************************************************************************/

			this.addButtons(ownerDoc, tr);
        }



        // create a Checkboxes object
        function getObject() {
          Checkboxes.prototype = new FieldPrototype(document.getElementById('fieldtype').value,
                                                      document.getElementsByName('field_label')[0].value,
                                                      document.getElementsByName('field_helptext')[0].value);
          var textArea = new Checkboxes();
          return textArea;
        }


        // clears inputfields and checkboxes in the options table
        function clearOptionsTable() {
                var table = document.getElementById('optionstable');
                var inputElements = table.getElementsByTagName('input');

                for (var i = 0; i &lt; inputElements.length; ++i) {
                        if (inputElements[i].type == 'text')
                                inputElements[i].value = '';
                }
        }

        // clear or remove a row in the optionstable
        function clearOrRemove(obj)
		{
			if (itemcount(document.getElementsByName(obj.name)) == 1) {
	            var idx = getObjectIndex(obj);
	            document.getElementsByName('field_value')[idx].value = '';
	            document.getElementsByName('default')[idx].checked = false;
			}
			else
			{
				removeTableRow(obj, 'optionstable', null, 1);
			}
        }

        // read values from the parent document and create rows in the
        // options table
        function addValues() {
        <xsl:if test="$row != ''">
                var doc = window.opener.document;
                var table = doc.getElementById('form_fieldtable');
                var tr = table.rows[<xsl:value-of select="$row"/>];

                var defaultChkCounter = 0;

                var inputElements = tr.getElementsByTagName('input');
                var fieldCounter = 0;
                for(var i = 0; i &lt; inputElements.length; ++i) {
                        if (inputElements[i].name == 'field_value') {
                                if (fieldCounter == 0) {
                                        document.getElementsByName('field_value')[fieldCounter].value = inputElements[i].value;
                                        fieldCounter++;
                                }
                                else {
                                        addTableRow('optionstable', 1, true);
                                        document.getElementsByName('field_value')[fieldCounter].value = inputElements[i].value;
                                        fieldCounter++;
                                }
                        }
                        else if (inputElements[i].name == 'field_defaultchk') {
                                if (inputElements[i].value == '1') {
                                    document.getElementsByName('default')[defaultChkCounter].value = 1;
                                    document.getElementsByName('default_chkbox')[defaultChkCounter].checked = true;
                                }

                                defaultChkCounter++;
                        }
                }
        </xsl:if>
        }

        // set correct disabled and disabled buttons
        function setDisabledEnabledButtons() {
                var buttons = document.getElementsByName('moverupper');
                buttons[0].setAttribute("disabled", "disabled");
                buttons[0].childNodes[0].src = 'images/icon_move_up-disabled.gif';
                toggleDefault(0, true);

                for (var i = 1; i &lt; buttons.length; ++i) {
                        if (buttons[i].disabled) {
                                buttons[i].removeAttribute("disabled");
                                buttons[i].childNodes[0].src = 'images/icon_move_up.gif';
                        }
                        toggleDefault(i, true);
                }

                buttons = document.getElementsByName('moverdowner');
                buttons[buttons.length-1].setAttribute("disabled", "disabled");
                buttons[buttons.length-1].childNodes[0].src = 'images/icon_move_down-disabled.gif';
                for (var i = 0; i &lt; buttons.length - 1; ++i) {
                        if (buttons[i].disabled) {
                                buttons[i].removeAttribute("disabled");
                                buttons[i].childNodes[0].src = 'images/icon_move_down.gif';
                        }
                }
        }

        function toggleDefault(index, reverse) {
                var element = document.getElementsByName('default')[index];

                if (reverse) {
                        if (element.value == '1')
                                document.getElementsByName('default_chkbox')[index].checked = true;
                        else
                                document.getElementsByName('default_chkbox')[index].checked = false;
                } else {
                        if (document.getElementsByName('default_chkbox')[index].checked)
                                element.value = '1';
                        else
                                element.value = '0';
                }
        }
     </xsl:template>

    <xsl:template name="formbuilder_body">
        <tr>
            <xsl:call-template name="textfield">
                <xsl:with-param name="label" select="'%fldLabel%:'"/>
                <xsl:with-param name="name" select="'field_label'"/>
                <xsl:with-param name="selectnode" select="''"/>
                <xsl:with-param name="required" select="'true'"/>
            </xsl:call-template>
        </tr>
        <!-- tr>
            <xsl:call-template name="checkbox_boolean">
                <xsl:with-param name="label" select="'%fldRequired%:'"/>
                <xsl:with-param name="name" select="'field_required'"/>
                <xsl:with-param name="selectnode" select="''"/>
            </xsl:call-template>
        </tr -->

        <tr>
            <td valign="top" class="form_labelcolumn">%fldOptions%:</td>
            <td>
                <table border="0" cellspacing="2" cellpadding="0">
					<tbody name="optionstable" id="optionstable">
	                    <tr>
	                        <!-- td style="padding: 0.2em; background-color: #dddddd">%fldDefault%</td -->
	                        <!-- td style="padding: 0.2em; background-color: #dddddd">%fldValues%</td -->
	                        <xsl:call-template name="tablecolumnheader">
	                            <xsl:with-param name="caption" select="'%fldDefault%'"/>
	                            <xsl:with-param name="sortable" select="'false'"/>
	                        </xsl:call-template>
	                        <xsl:call-template name="tablecolumnheader">
	                            <xsl:with-param name="caption" select="'%fldValues%'"/>
	                            <xsl:with-param name="sortable" select="'false'"/>
	                        </xsl:call-template>
	                        <xsl:call-template name="tablecolumnheader">
	                            <xsl:with-param name="sortable" select="'false'"/>
	                            <xsl:with-param name="width" select="'50'"/>
	                        </xsl:call-template>
	                    </tr>
	                    <tr>
	                        <td align="center">
	                            <center><input type="checkbox" name="default_chkbox" onclick="javascript:toggleDefault(getObjectIndex(this));"/></center>
	                            <input type="hidden" value="0" name="default"/>
	                        </td>
	                        <td><input type="text" name="field_value"/></td>

	                        <td>
                            <!--button type="button" class="button_image_small" name="moverdowner" onclick="javascript:saveRadioState();moveTableRowDown('optionstable', getObjectIndex(this) + 1);setDisabledEnabledButtons();loadRadioState();"><img alt="%cmdMoveDown%" src="images/icon_move_down.gif" border="0"/></button>
                            <button type="button" class="button_image_small" name="moverupper" onclick="javascript:saveRadioState();moveTableRowUp('optionstable', getObjectIndex(this) + 1);setDisabledEnabledButtons();loadRadioState();"><img alt="%cmdMoveUp%" src="images/icon_move_up.gif" border="0"/></button-->
                              <button type="button" class="button_image_small" name="moverdowner" onclick="javascript:moveTableRowDown('optionstable', getObjectIndex(this) + 1);setDisabledEnabledButtons();"><img alt="%cmdMoveDown%" src="images/icon_move_down.gif" border="0"/></button>
                              <button type="button" class="button_image_small" name="moverupper" onclick="javascript:moveTableRowUp('optionstable', getObjectIndex(this) + 1);setDisabledEnabledButtons();"><img alt="%cmdMoveUp%" src="images/icon_move_up.gif" border="0"/></button>
	                            <button type="button" class="button_image_small" name="removeButton" onclick="javascript:clearOrRemove(this);setDisabledEnabledButtons();">
	                                <img alt="%cmdRemove%" src="images/icon_remove.gif" border="0"/>
	                            </button>
	                        </td>
	                    </tr>
					</tbody>
                </table>

                <xsl:call-template name="button">
                    <xsl:with-param name="caption">%cmdNewOption%</xsl:with-param>
                    <xsl:with-param name="onclick">
                        <xsl:text>javascript:addTableRow('optionstable', 1, true);setDisabledEnabledButtons();</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>
                <!-- xsl:text>&nbsp;</xsl:text>
                <xsl:call-template name="button">
                    <xsl:with-param name="caption">%cmdClear%</xsl:with-param>
                    <xsl:with-param name="onclick">
                        <xsl:text>javascript:clearOptionsTable();</xsl:text>
                    </xsl:with-param>
                </xsl:call-template -->
            </td>
        </tr>

        <tr><td><br/></td></tr>

        <tr>
            <xsl:call-template name="textarea">
                <xsl:with-param name="label" select="'%fldHelpText%:'"/>
                <xsl:with-param name="name" select="'field_helptext'"/>
                <xsl:with-param name="rows" select="5"/>
                <xsl:with-param name="cols" select="30"/>
                <xsl:with-param name="selectnode" select="''"/>
            </xsl:call-template>
        </tr>

        <script language="JavaScript" type="text/javascript">
            addValues();
            setDisabledEnabledButtons();
			formbuilder_setFocus();
        </script>
    </xsl:template>


</xsl:stylesheet>
