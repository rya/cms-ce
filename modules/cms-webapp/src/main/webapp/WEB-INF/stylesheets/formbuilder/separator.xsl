<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    
    <xsl:output method="html"/>

    <xsl:include href="common.xsl"/>

    <xsl:template name="formbuilder_javascript">

        // constructor for the separator class
        function Separator() {
			this.addFields = Separator_addFields;
		}

        function Separator_addFields(ownerDoc, table, tr) {
			var title_td = ownerDoc.createElement('td');
			var br = ownerDoc.createElement('br');
			title_td.appendChild(br);
			
			// from name
			var fromname_td = ownerDoc.createElement('td');
      var fromname_br = ownerDoc.createElement('br');
      fromname_td.appendChild(fromname_br);

			// from email
			var fromemail_td = ownerDoc.createElement('td');
      var fromemail_br = ownerDoc.createElement('br');
      fromemail_td.appendChild(fromemail_br);

			// label
			//var label_td = tr.insertCell();
			//label_td.appendChild(ownerDoc.createTextNode(this.label));
			var label_td = ownerDoc.createElement('td');
			label_td.appendChild(ownerDoc.createTextNode(this.label));

			// type
			//var type_td = tr.insertCell();
			//type_td.appendChild(ownerDoc.createTextNode('%optFieldSeparator%'));
			var type_td = ownerDoc.createElement('td');
			type_td.appendChild(ownerDoc.createTextNode('%optFieldSeparator%'));

			// field type
			element = createNamedElement(ownerDoc, 'input', 'field_type');
			element.setAttribute('type','hidden');
			element.setAttribute('value',this.type);
			label_td.appendChild(element);

			// field label
			element = createNamedElement(ownerDoc, 'input', 'field_label');
			element.setAttribute('type','hidden');
			element.setAttribute('value',this.label);
			label_td.appendChild(element);

			// field helptext
			element = createNamedElement(ownerDoc, 'input', 'field_helptext');
			element.setAttribute('type','hidden');
			element.setAttribute('value',this.helptext);
			label_td.appendChild(element);

			/*************************************************************************************
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
			*************************************************************************************/
			
			tr.appendChild(title_td);
			tr.appendChild(fromname_td);
			tr.appendChild(fromemail_td);
			tr.appendChild(label_td);
			tr.appendChild(type_td);

      /*
      title_td.setAttribute('class',this.cssClassSeperator);
			label_td.setAttribute('class',this.cssClassSeperator);
			type_td.setAttribute('class',this.cssClassSeperator);
      */

			title_td.className = this.cssClassSeperator;
      fromname_td.className = this.cssClassSeperator;
      fromemail_td.className = this.cssClassSeperator;
			label_td.className = this.cssClassSeperator;
			type_td.className = this.cssClassSeperator;
			
			this.addButtons(ownerDoc, tr, 'seperator');
        }        

        

        // create a separator object
        function getObject() {
          Separator.prototype = new FieldPrototype(document.getElementsByName('fieldtype')[0].value,
                                                   document.getElementsByName('field_label')[0].value,
                                                   document.getElementsByName('field_helptext')[0].value);
          var sep = new Separator();
          return sep;
        }

     </xsl:template>

    <xsl:template name="formbuilder_body">
        <tr>
            <xsl:call-template name="textfield">
                <xsl:with-param name="label" select="'%fldLabel%:'"/>
                <xsl:with-param name="name" select="'field_label'"/>
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
