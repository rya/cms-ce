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

    <xsl:template name="formbuilder_javascript">

        // constructor for the Text class
		function Checkbox() 
		{
			this.addFields = Checkbox_addFields;
        }

		function Checkbox_addFields(ownerDoc, table, tr) 
		{
			// empty td element
			//tr.insertCell();
			var title_td = ownerDoc.createElement('td');
			title_td.setAttribute('align', 'center');
			
			// from name
			var fromname_td = ownerDoc.createElement('td');
			
			// from email
			var fromemail_td = ownerDoc.createElement('td');

			// label
			//var label_td = tr.insertCell();
			//label_td.appendChild(ownerDoc.createTextNode(this.label));
			var label_td = ownerDoc.createElement('td');
			var _label = ownerDoc.createTextNode(this.label);
			label_td.appendChild(_label);
			
			// type
			//var type_td = tr.insertCell();
			//type_td.appendChild(ownerDoc.createTextNode('%optFieldCheckbox%'));
			var type_td = ownerDoc.createElement('td');
			var _type = ownerDoc.createTextNode('%optFieldCheckbox%');
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

          /*******************************************************************************

			// type
			var type_td = tr.insertCell();
			type_td.appendChild(ownerDoc.createTextNode('%optFieldCheckbox%'));

		  // add hidden form elements for storing data ------------------------------------
          // type
          var element = ownerDoc.createElement('<input type="hidden" name="field_type" value="'+
                                           this.type +'" />');
          label_td.appendChild(element);

          // label
          element = ownerDoc.createElement('<input type="hidden" name="field_label" value="'+
                                               this.label +'" />');
          label_td.appendChild(element);
			************************************************************************************/
			
			// default value
			var radios = document.getElementsByName('field_defaultvalue');
			var value;
			for (var i = 0; i &lt; radios.length; ++i) 
			{
				if (radios[i].checked) 
				{
					value = radios[i].value;
					break;
                }
			}
		  
			// Checked index
			element = createNamedElement(ownerDoc, 'input','field_defaultvalue');
			element.setAttribute('type','hidden');
			element.setAttribute('value',value);
			label_td.appendChild(element);

			// helptext
			element = createNamedElement(ownerDoc, 'input', 'field_helptext');
			element.setAttribute('type','hidden');
			element.setAttribute('value',this.helptext);
			label_td.appendChild(element);

          /************************************************************************************
		  element = ownerDoc.createElement('<input type="hidden" name="field_defaultvalue" value="'+
                                            value +'" />');
          label_td.appendChild(element);        
          // helptext
          element = ownerDoc.createElement('<input type="hidden" name="field_helptext" value="'+
                                           this.helptext +'" />');
          label_td.appendChild(element);
		  **************************************************************************************/
			
			tr.appendChild(title_td);
			tr.appendChild(fromname_td);
			tr.appendChild(fromemail_td);
			tr.appendChild(label_td);
			tr.appendChild(type_td);
			this.addButtons(ownerDoc, tr);
		}        

        

        // create a Checkbox object
        function getObject() {
          Checkbox.prototype = new FieldPrototype(document.getElementById('fieldtype').value,
                                                  document.getElementsByName('field_label')[0].value,
                                                  document.getElementsByName('field_helptext')[0].value);
          var textArea = new Checkbox();
          return textArea;
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
        <tr>
            <td class="form_labelcolumn" valign="top">%fldDefaultValue%:</td>
            <td>
                <div>
                    <input type="radio" name="field_defaultvalue" value="checked"/> %optChecked%
                </div>
                <div>
                    <input type="radio" name="field_defaultvalue" value="notchecked" checked="checked"/> %optNotChecked%
                </div>
            </td>
        </tr>
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
			formbuilder_setFocus();
        </script>		
    </xsl:template>


</xsl:stylesheet>
