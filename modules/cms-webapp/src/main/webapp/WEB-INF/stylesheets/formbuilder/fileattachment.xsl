<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    
    <xsl:output method="html"/>

    <xsl:include href="common.xsl"/>
    <xsl:include href="../common/checkbox_boolean.xsl"/>

    <xsl:template name="formbuilder_javascript">

		// constructor for the file attachment class
		function FileAttachment() 
		{
			this.addFields = FileAttachment_addFields;
		}

		function FileAttachment_addFields(ownerDoc, table, tr) 
		{
  			// empty td element
  			//var title_td = tr.insertCell();
  			var title_td = ownerDoc.createElement('td');
  			var element = ownerDoc.createElement('input');
  			title_td.setAttribute('align', 'center');
  			
  			// from name
			var fromname_td = ownerDoc.createElement('td');
			
			// from email
			var fromemail_td = ownerDoc.createElement('td');
  
			element = createNamedElement(ownerDoc, 'input','field_form_title');
			element.setAttribute('type','radio');
			element.setAttribute('value',this.label);
			if (document.getElementById('field_form_title').checked)
			{
				element.setAttribute('defaultChecked', 'checked'); // IE 
				element.checked = true; // others 
			}
			title_td.appendChild(element);

			/****************************************************************************
			if (document.getElementById('field_form_title').checked) {
				element = ownerDoc.createElement('<input type="radio" name="field_form_title" value="'+
																this.label +'" checked="checked"/>');
			} else {
				element = ownerDoc.createElement('<input type="radio" name="field_form_title" value="'+
                               this.label +'" />');
			}
			******************************************************************************/


			title_td.appendChild(element);

			// label
			//var label_td = tr.insertCell();
			var label_td = ownerDoc.createElement('td');
			var _label = ownerDoc.createTextNode(this.label);
			
			label_td.appendChild(_label);
			
			if (document.getElementsByName('field_required')[0].checked) 
			{

				var req_elem = ownerDoc.createElement('span');
				req_elem.className = 'requiredfield';
				req_elem.appendChild(ownerDoc.createTextNode(' *'));
				label_td.appendChild(req_elem);
  			}

			// type
			//var type_td = tr.insertCell();
			var type_td = ownerDoc.createElement('td');
  
			type_td.appendChild(ownerDoc.createTextNode('%optFieldFileAttachment%'));
			// required
			element = createNamedElement(ownerDoc, 'input', 'field_required');
			element.setAttribute('type','hidden');
			element.setAttribute('value',document.getElementsByName('field_required')[0].checked);
			label_td.appendChild(element);
  
			// add hidden form elements for storing data
			element = createNamedElement(ownerDoc, 'input', 'field_label');
			element.setAttribute('type','hidden');
			element.setAttribute('value',this.label);
			label_td.appendChild(element);

			element = createNamedElement(ownerDoc, 'input', 'field_type');
			element.setAttribute('type','hidden');
			element.setAttribute('value',this.type);
			label_td.appendChild(element);

			element = createNamedElement(ownerDoc, 'input', 'field_helptext');
			element.setAttribute('type','hidden');
			element.setAttribute('value',this.helptext);
			label_td.appendChild(element);

			
			/*****************************************************************************
			// required
			element = ownerDoc.createElement('<input type="hidden" name="field_required" value="'+
			                            document.getElementsByName('field_required')[0].checked +'" />');
			label_td.appendChild(element);
			
			// add hidden form elements for storing data
			var element = ownerDoc.createElement('<input type="hidden" name="field_label" value="'+
			                               this.label +'" />');
			label_td.appendChild(element);
			  
			element = ownerDoc.createElement('<input type="hidden" name="field_type" value="'+
			                           this.type +'" />');
			label_td.appendChild(element);
			element = ownerDoc.createElement('<input type="hidden" name="field_helptext" value="'+
			                           this.helptext +'" />');
			label_td.appendChild(element);
			******************************************************************************/      
			  

			tr.appendChild(title_td);
			tr.appendChild(fromname_td);
			tr.appendChild(fromemail_td);
			tr.appendChild(label_td);
			tr.appendChild(type_td);

			this.addButtons(ownerDoc, tr);
		}        

        // create a file attachment object
        function getObject() 
		{
			FileAttachment.prototype = new FieldPrototype(document.getElementsByName('fieldtype')[0].value, document.getElementsByName('field_label')[0].value, document.getElementsByName('field_helptext')[0].value);
			var fileAttachment = new FileAttachment();
			return fileAttachment;
        }
        
        function event_setTitleRadio() 
		{
            <xsl:if test="$row != ''">
				var doc = window.opener.document;
                var titles = doc.getElementsByName('field_form_title');
                for (var i = 0; i &lt; titles.length; ++i) 
				{
                    if (titles[i].checked &amp;&amp; titles[i].value == document.getElementsByName('field_label')[0].value) 
					{
                        document.getElementById('field_form_title').checked = true;
                    }
                }
            </xsl:if>
        }

        //addEvent(window ,'onload', event_setTitleRadio);
		window.onload = event_setTitleRadio;
     </xsl:template>

    <xsl:template name="formbuilder_body">
    	<input type="radio" name="field_form_title" id="field_form_title" style="display: none"/>
    	
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
            <xsl:call-template name="textarea">
                <xsl:with-param name="label" select="'%fldHelpText%:'"/>
                <xsl:with-param name="name" select="'field_helptext'"/>
                <xsl:with-param name="rows" select="5"/>
                <xsl:with-param name="cols" select="30"/>
                <xsl:with-param name="selectnode" select="''"/>
            </xsl:call-template>
        </tr>

        <script language="JavaScript" type="text/javascript">
			fillFields();
			formbuilder_setFocus();
        </script>
        
    </xsl:template>
</xsl:stylesheet>
