<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html"/>

    <xsl:include href="common/standard_form_templates.xsl"/>

    <xsl:include href="common/generic_formheader.xsl"/>
    <xsl:include href="common/textarea.xsl"/>
    <xsl:include href="common/textfield.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>
    <xsl:include href="common/serialize.xsl"/>


    <xsl:param name="create"/>
    <xsl:param name="contenttypekey"/>
    <xsl:param name="modulename"/>

    <xsl:template match="/">

        <html>
            <head>
                <title>CMS</title>

                <script type="text/javascript" src="javascript/cms/core.js">//</script>
                <script type="text/javascript" src="javascript/cms/utils/Event.js">//</script>
                <script type="text/javascript" src="javascript/cms/utils/Cookie.js">//</script>
                <script type="text/javascript" src="javascript/cms/element/Css.js">//</script>
                <script type="text/javascript" src="javascript/cms/element/Dimensions.js">//</script>
                <script type="text/javascript" src="javascript/cms/ui/SplitButton.js">//</script>

                <link type="text/css" rel="stylesheet" href="css/admin.css"/>
                <link type="text/css" rel="stylesheet" href="css/menu.css"/>
                <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>
                <link rel="stylesheet" type="text/css" href="css/calendar_picker.css"/>
                <link rel="stylesheet" type="text/css" href="javascript/cms/ui/style.css"/>

                <script type="text/javascript" language="JavaScript" src="javascript/admin.js"/>
                <script type="text/javascript" language="JavaScript" src="javascript/accessrights.js"/>
                <script type="text/javascript" language="JavaScript" src="javascript/validate.js"/>
                <script type="text/javascript" language="JavaScript" src="javascript/tabpane.js"/>
                <script type="text/javascript" language="JavaScript" src="javascript/menu.js"/>
                <script type="text/javascript" language="JavaScript" src="javascript/calendar_picker.js"/>
                <script type="text/javascript" language="JavaScript" src="javascript/properties.js"/>

                <script type="text/javascript" language="JavaScript">
                    // variables used by menu.js
                    var branchOpen = new Array();
                    var cookiename = "contentform";

                    var validatedFields = new Array(4);
                    validatedFields[0] = new Array("%fldName%", "contentdata_name", validateRequired);
                    validatedFields[2] = new Array("%fldEMail%", "contentdata_mail", validateRequired);
                    validatedFields[3] = new Array("%fldEMail%", "contentdata_mail", validateEmail);
                    
                    function validateAll(formName) {
	                    var f = document.forms[formName];
                    
		                if ( !checkAll(formName, validatedFields) )
				            return;
                    
					    disableFormButtons();
					    f.submit();
                    }

                </script>
            </head>

            <xsl:call-template name="contentform"/>
        </html>

    </xsl:template>

    <xsl:template name="contenttypeform">
		<xsl:param name="readonly"/>
		
        <fieldset>
            <legend>&nbsp;%blockLead%&nbsp;</legend>
            <table border="0" cellspacing="0" cellpadding="2">
                <tr><td class="form_labelcolumn"></td></tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'contentdata_name'"/>
                        <xsl:with-param name="label" select="'%fldName%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/name"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                        <xsl:with-param name="required" select="'true'"/>
            						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>

                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'contentdata_title'"/>
                        <xsl:with-param name="label" select="'%fldTitle%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/title"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'contentdata_mail'"/>
                        <xsl:with-param name="label" select="'%fldEMail%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/mail"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                        <xsl:with-param name="required" select="'true'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'contentdata_phone'"/>
                        <xsl:with-param name="label" select="'%fldPhone%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/phone"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'contentdata_fax'"/>
                        <xsl:with-param name="label" select="'%fldFax%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/fax"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'contentdata_address'"/>
                        <xsl:with-param name="label" select="'%fldAddress%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/address"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'contentdata_postalcode'"/>
                        <xsl:with-param name="label" select="'%fldPostalCode%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/postalcode"/>
                        <xsl:with-param name="size" select="'10'"/>
                        <xsl:with-param name="maxlength" select="'20'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'contentdata_location'"/>
                        <xsl:with-param name="label" select="'%fldLocation%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/location"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'contentdata_state'"/>
                        <xsl:with-param name="label" select="'%fldStat%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/state"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'1000'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'contentdata_country'"/>
                        <xsl:with-param name="label" select="'%fldCountry%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/country"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'1000'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'contentdata_organization'"/>
                        <xsl:with-param name="label" select="'%fldOrganization%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/organization"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'1000'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'contentdata_subject'"/>
                        <xsl:with-param name="label" select="'%fldConcerning%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/subject"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'1000'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textarea">
                        <xsl:with-param name="name" select="'contentdata_description'"/>
                        <xsl:with-param name="label" select="'%fldBody%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/description"/>
                        <xsl:with-param name="rows" select="'5'"/>
                        <xsl:with-param name="cols" select="'60'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'contentdata_recipientmail'"/>
                        <xsl:with-param name="label" select="'%fldRecipientEMail%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/recipientmail"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'1000'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
            </table>
        </fieldset>
    </xsl:template>

</xsl:stylesheet>
