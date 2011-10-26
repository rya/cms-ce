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
        function TextArea() {
			this.addFields = TextArea_addFields;
        }

        function TextArea_addFields(ownerDoc, table, tr) {
			// empty td element
			//tr.insertCell();
			var title_td = ownerDoc.createElement('td');
			
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
		  
			// label
			//var label_td = tr.insertCell();
			//label_td.appendChild(ownerDoc.createTextNode(this.label));

			/*
			if (document.getElementsByName('field_required')[0].checked) {
			var req_elem = ownerDoc.createElement('span');
			req_elem.className = 'requiredfield';
			req_elem.appendChild(ownerDoc.createTextNode(' *'));
			label_td.appendChild(req_elem);
			}
			*/
		  
		  	if (document.getElementsByName('field_required')[0].checked) 
			{
				var req_elem = ownerDoc.createElement('span');
				req_elem.className = 'requiredfield';
				req_elem.appendChild(ownerDoc.createTextNode(' *'));
				label_td.appendChild(req_elem);
			}

			// type
			//var type_td = tr.insertCell();
			//type_td.appendChild(ownerDoc.createTextNode('%optFieldTextarea%'));

			var type_td = ownerDoc.createElement('td');
			var _type = ownerDoc.createTextNode('%optFieldTextarea%');
			type_td.appendChild(_type);

			// add hidden form elements for storing data ------------------------------------
			// type
			element = createNamedElement(ownerDoc, 'input', 'field_type');
			element.setAttribute('type','hidden');
			element.setAttribute('value',this.type);
			label_td.appendChild(element);

			// label
			element = createNamedElement(ownerDoc, 'input', 'field_label');
			element.setAttribute('type','hidden');
			element.setAttribute('value',this.label);
			label_td.appendChild(element);

			// required
			element = createNamedElement(ownerDoc, 'input', 'field_required');
			element.setAttribute('type','hidden');
			element.setAttribute('value',document.getElementsByName('field_required')[0].checked);
			label_td.appendChild(element);

			// width
			element = createNamedElement(ownerDoc, 'input', 'field_width');
			element.setAttribute('type','hidden');
			element.setAttribute('value',document.getElementsByName('field_width')[0].value);
			label_td.appendChild(element);

			// height
			element = createNamedElement(ownerDoc, 'input', 'field_height');
			element.setAttribute('type','hidden');
			element.setAttribute('value',document.getElementsByName('field_height')[0].value);
			label_td.appendChild(element);

			// help text
			element = createNamedElement(ownerDoc, 'input', 'field_helptext');
			element.setAttribute('type','hidden');
			element.setAttribute('value',this.helptext);
			label_td.appendChild(element);
			
			/*
          	// add hidden form elements for storing data ------------------------------------
          	// type
          	var element = ownerDoc.createElement('<input type="hidden" name="field_type" value="'+
                                           this.type +'" />');
          	label_td.appendChild(element);

          	// label
          	element = ownerDoc.createElement('<input type="hidden" name="field_label" value="'+
                                               this.label +'" />');
          	label_td.appendChild(element);

          	// required
          	element = ownerDoc.createElement('<input type="hidden" name="field_required" value="'+
                                            document.getElementsByName('field_required')[0].checked +'" />');
          	label_td.appendChild(element);        

          	// width
          	element = ownerDoc.createElement('<input type="hidden" name="field_width" value="'+
                                            document.getElementsByName('field_width')[0].value +'" />');
          	label_td.appendChild(element);

          	// height
          	element = ownerDoc.createElement('<input type="hidden" name="field_height" value="'+
                                            document.getElementsByName('field_height')[0].value +'" />');
          	label_td.appendChild(element);

          	// helptext
          	element = ownerDoc.createElement('<input type="hidden" name="field_helptext" value="'+
                                           this.helptext +'" />');
          	label_td.appendChild(element);
			*/
			
			tr.appendChild(title_td);
			tr.appendChild(fromname_td);
			tr.appendChild(fromemail_td);
			tr.appendChild(label_td);
			tr.appendChild(type_td);

			this.addButtons(ownerDoc, tr);
		}        
        

        // create a TextArea object
        function getObject() 
		{
			TextArea.prototype = new FieldPrototype(document.getElementById('fieldtype').value,
                                                  document.getElementsByName('field_label')[0].value,
                                                  document.getElementsByName('field_helptext')[0].value);
			var textArea = new TextArea();
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
            <xsl:call-template name="checkbox_boolean">
                <xsl:with-param name="label" select="'%fldRequired%:'"/>
                <xsl:with-param name="name" select="'field_required'"/>
                <xsl:with-param name="selectnode" select="''"/>
            </xsl:call-template>
        </tr>
        <tr>
            <xsl:call-template name="textfield">
                <xsl:with-param name="label" select="'%fldWidth%:'"/>
                <xsl:with-param name="name" select="'field_width'"/>
                <xsl:with-param name="selectnode" select="''"/>
                <xsl:with-param name="size" select="'5'"/>
            </xsl:call-template>
        </tr>

        <tr>
            <xsl:call-template name="textfield">
                <xsl:with-param name="label" select="'%fldHeight%:'"/>
                <xsl:with-param name="name" select="'field_height'"/>
                <xsl:with-param name="selectnode" select="''"/>
                <xsl:with-param name="size" select="'5'"/>
            </xsl:call-template>
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
