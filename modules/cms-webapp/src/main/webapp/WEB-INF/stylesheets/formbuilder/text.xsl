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
        function Text() {
			this.addFields = Text_addFields;
        }
		
        function Text_addFields(ownerDoc, table, tr) {
			var title_td = ownerDoc.createElement('td');
			title_td.setAttribute('align', 'center');
			
			var element;

			element = createNamedElement(ownerDoc, 'input','field_form_title');
			element.setAttribute('type','radio');
			element.setAttribute('value',this.label);
			if (document.getElementById('field_form_title').checked)
			{
				element.setAttribute('defaultChecked', 'checked'); // IE 
				element.checked = true; // others 
			}
			title_td.appendChild(element);
			
			// from name
			var fromname_td = ownerDoc.createElement('td');
			fromname_td.setAttribute('align', 'center');

			element = createNamedElement(ownerDoc, 'input','field_form_fromname');
			element.setAttribute('type','radio');
			element.setAttribute('value',this.label);
			if (document.getElementById('field_form_fromname').checked)
			{
				element.setAttribute('defaultChecked', 'checked'); // IE 
				element.checked = true; // others 
			}
			fromname_td.appendChild(element);
			
			// from email
			var fromemail_td = ownerDoc.createElement('td');
			fromemail_td.setAttribute('align', 'center');
			var validation = document.getElementsByName('field_validation')[0].value;
			if (validation == 'email') {
				element = createNamedElement(ownerDoc, 'input','field_form_fromemail');
				element.setAttribute('type','radio');
				element.setAttribute('value',this.label);
				if (document.getElementById('field_form_fromemail').checked)
				{
					element.setAttribute('defaultChecked', 'checked'); // IE 
					element.checked = true; // others 
				}
				fromemail_td.appendChild(element);
			}
			
			// type td
			var type_td = ownerDoc.createElement('td');
			type_td.appendChild(ownerDoc.createTextNode('%optFieldText%'));
			
			// label td
			var label_td = ownerDoc.createElement('td');
			label_td.appendChild(ownerDoc.createTextNode(this.label));
			
			if (document.getElementsByName('field_required')[0].checked) 
			{
				var req_elem = ownerDoc.createElement('span');
				req_elem.className = 'requiredfield';
				req_elem.appendChild(ownerDoc.createTextNode(' *'));
				label_td.appendChild(req_elem);
			}
			
			// field type
			element = createNamedElement(ownerDoc, 'input','field_type');
			element.setAttribute('type','hidden');
			element.setAttribute('value',this.type);
			label_td.appendChild(element);

			// field label
			element = createNamedElement(ownerDoc, 'input','field_label');
			element.setAttribute('type','hidden');
			element.setAttribute('value',this.label);
			label_td.appendChild(element);

			// field required
			element = createNamedElement(ownerDoc, 'input','field_required');
			element.setAttribute('type','hidden');
			element.setAttribute('value',document.getElementsByName('field_required')[0].checked);
			label_td.appendChild(element);

			// field width
			element = createNamedElement(ownerDoc, 'input','field_width');
			element.setAttribute('type','hidden');
			element.setAttribute('value',document.getElementsByName('field_width')[0].value);
			label_td.appendChild(element);

			// default value
			element = createNamedElement(ownerDoc, 'input','field_defaultvalue');
			element.setAttribute('type','hidden');
			element.setAttribute('value',document.getElementsByName('field_defaultvalue')[0].value);
			label_td.appendChild(element);
			
			// field validation
			element = createNamedElement(ownerDoc, 'input','field_validation');
			element.setAttribute('type','hidden');
			element.setAttribute('value',document.getElementsByName('field_validation')[0].value);
			label_td.appendChild(element);
			
			// field regexp
			element = createNamedElement(ownerDoc, 'input','field_regexp');
			element.setAttribute('type','hidden');
			element.setAttribute('value',document.getElementsByName('field_regexp')[0].value);
			label_td.appendChild(element);

			// field helptext
			element = createNamedElement(ownerDoc, 'input','field_helptext');
			element.setAttribute('type','hidden');
			element.setAttribute('value',this.helptext);
			label_td.appendChild(element);

			/*********************************************************************************
			// type
			var type_td = tr.insertCell();
			type_td.appendChild(ownerDoc.createTextNode('%optFieldText%'));

			// add hidden form elements for storing data ------------------------------------
			// type
			element = ownerDoc.createElement('<input type="hidden" name="field_type" value="'+
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

			// default value
			element = ownerDoc.createElement('<input type="hidden" name="field_defaultvalue" value="'+
                                            document.getElementsByName('field_defaultvalue')[0].value +'" />');
			label_td.appendChild(element);

			// validation
			element = ownerDoc.createElement('<input type="hidden" name="field_validation" value="'+
                                            document.getElementById('field_validation').value +'" />');
			label_td.appendChild(element);

			// regular expression
			element = ownerDoc.createElement('<input type="hidden" name="field_regexp" value="'+
                                            document.getElementsByName('field_regexp')[0].value +'" />');
			label_td.appendChild(element);

			// helptext
			element = ownerDoc.createElement('<input type="hidden" name="field_helptext" value="'+
                                           this.helptext +'" />');
			label_td.appendChild(element);
			*********************************************************************************************/
			
			tr.appendChild(title_td);
			tr.appendChild(fromname_td);
			tr.appendChild(fromemail_td);
			tr.appendChild(label_td);
			tr.appendChild(type_td);

			this.addButtons(ownerDoc, tr);
		}        

        // create a Text object
        function getObject() {
			Text.prototype = new FieldPrototype(document.getElementById('fieldtype').value,
                                              document.getElementsByName('field_label')[0].value,
                                              document.getElementsByName('field_helptext')[0].value);
			var text = new Text();
			return text;
        }

        function setRegexp(noReset) {
                var emailRegexp = '^.+\@.+\..+$';
                var intRegexp = '^[0-9]+$';

                var value = document.getElementById('field_regexp').value;
                if (value == emailRegexp) {
                        document.getElementsByName('field_regexp')[0].value = emailRegexp;
                        document.getElementById('regexprow').style.display = 'none';

                        document.getElementById('field_validation').options[1].selected = true;
                }
                else if (value == intRegexp) {
                        document.getElementsByName('field_regexp')[0].value = intRegexp;
                        document.getElementById('regexprow').style.display = 'none';

                        document.getElementById('field_validation').options[2].selected = true;
                }
                else if (value.length &gt; 0) {
                        if (!noReset)
                                document.getElementsByName('field_regexp')[0].value = '';
                        document.getElementById('regexprow').style.display = '';

                        document.getElementById('field_validation').options[3].selected = true;
                }
                else {
                        document.getElementsByName('field_regexp')[0].value = '';
                        document.getElementById('regexprow').style.display = 'none';

                        document.getElementById('field_validation').options[0].selected = true;
                }
        }

        function changeRegexp(value, noreset) {
                var emailRegexp = '^.+\@.+\..+$';
                var intRegexp = '^[0-9]+$';

                if (value == 'email') {
                        document.getElementsByName('field_regexp')[0].value = emailRegexp;
                        document.getElementById('regexprow').style.display = 'none';

                        document.getElementById('field_validation').options[1].selected = true;
                }
                else if (value == 'integer') {
                        document.getElementsByName('field_regexp')[0].value = intRegexp;
                        document.getElementById('regexprow').style.display = 'none';

                        document.getElementById('field_validation').options[2].selected = true;
                }
                else if (value == 'regexp') {
                        document.getElementsByName('field_regexp')[0].value = '';
                        document.getElementById('regexprow').style.display = '';

                        document.getElementById('field_validation').options[3].selected = true;
                }
                else if (value == 'none') {
                        document.getElementsByName('field_regexp')[0].value = '';
                        document.getElementById('regexprow').style.display = 'none';

                        document.getElementById('field_validation').options[0].selected = true;
                }
        }      

        // example regexp: ^[a-b]+$
		function checkRegexp() {
			var regexp = new RegExp(document.getElementsByName('field_regexp')[0].value);
			var regexpTest = regexp.test(document.getElementsByName('field_defaultvalue')[0].value);

			if (regexpTest)
			{
            	alert('%alertRegexpMatchOK%');
			}
			else
			{
	            alert('%alertRegexpMatchFailed%');
			}
        }

        function event_setRegexp() 
		{
			setRegexp(true);
		}
		//addEvent(window, 'onload', event_setRegexp);
        window.onload = event_setRegexp;
		function event_setTitleRadio() 
		{
            <xsl:if test="$row != ''">
                var doc = window.opener.document;
                var titles = doc.getElementsByName('field_form_title');
                for (var i = 0; i &lt; titles.length; ++i) {
                    if (titles[i].checked &amp;&amp; titles[i].value == document.getElementsByName('field_label')[0].value) {
                        document.getElementById('field_form_title').checked = true;
                    }
                }
                
                var fromNames = doc.getElementsByName('field_form_fromname');
                for (var i = 0; i &lt; fromNames.length; ++i) {
                    if (fromNames[i].checked &amp;&amp; fromNames[i].value == document.getElementsByName('field_label')[0].value) {
                        document.getElementById('field_form_fromname').checked = true;
                    }
                }
                
                var fromEmails = doc.getElementsByName('field_form_fromemail');
                for (var i = 0; i &lt; fromEmails.length; ++i) {
                    if (fromEmails[i].checked &amp;&amp; fromEmails[i].value == document.getElementsByName('field_label')[0].value) {
                        document.getElementById('field_form_fromemail').checked = true;
                    }
                }
            </xsl:if>
        }

		//addEvent(window, 'onload', event_setTitleRadio);
        window.onload = event_setTitleRadio;
     </xsl:template>

    <xsl:template name="formbuilder_body">
        <input type="radio" name="field_form_title" id="field_form_title" style="display: none"/>
        <input type="radio" name="field_form_fromname" id="field_form_fromname" style="display: none"/>
        <input type="radio" name="field_form_fromemail" id="field_form_fromemail" style="display: none"/>

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
                <xsl:with-param name="label" select="'%fldDefaultValue%:'"/>
                <xsl:with-param name="name" select="'field_defaultvalue'"/>
                <xsl:with-param name="selectnode" select="''"/>
            </xsl:call-template>
        </tr>
        <tr>
            <td class="form_labelcolumn">%fldValidation%:</td>
            <td>
                <select name="field_validation" id="field_validation" onchange="javascript:changeRegexp(this.value);">
                    <option value="none">%optNone%</option>
                    <option value="email">%optEmail%</option>
                    <option value="integer">%optInteger%</option>
                    <option value="regexp">%optRegexp%</option>
                </select>
            </td>
        </tr>

        <tr id="regexprow" style="display: none;">
            <td class="form_labelcolumn">%fldRegexp%:</td>
            <td>
                <input type="text" class="textfield" id="field_regexp" name="field_regexp"/>
                <xsl:text>&nbsp;</xsl:text>
                <xsl:call-template name="button">
                    <xsl:with-param name="caption" select="'%cmdCheckDefaultValue%'"/>
                    <xsl:with-param name="onclick">
                        <xsl:text>javascript:checkRegexp();</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>
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
			setRegexp();
			formbuilder_setFocus();
        </script>
    </xsl:template>


</xsl:stylesheet>
