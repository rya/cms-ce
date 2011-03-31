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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>

  <xsl:include href="common/standard_form_templates.xsl"/>

  <xsl:include href="common/generic_formheader.xsl"/>
  <xsl:include href="editor/htmleditor.xsl"/>
  <xsl:include href="common/textfield.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>

  <xsl:param name="create"/>
  <xsl:param name="contenttypekey"/>

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

                    var validatedFields = new Array(3);
                    validatedFields[0] = new Array("%fldFirstName%", "firstname", validateRequired);
                    validatedFields[1] = new Array("%fldLastName%", "lastname", validateRequired);
                    validatedFields[2] = new Array("%fldEMail%", "mail", validateEmail);
                    
                    function validateAll(formName)
                    {
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
				<legend>%blockPerson%</legend>
            <table border="0" cellspacing="0" cellpadding="2">
                <tr><td class="form_labelcolumn"></td></tr>            
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'firstname'"/>
                        <xsl:with-param name="label" select="'%fldFirstName%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/firstname"/>
                        <xsl:with-param name="size" select="'30'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						            <xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'lastname'"/>
                        <xsl:with-param name="label" select="'%fldLastName%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/lastname"/>
                        <xsl:with-param name="size" select="'30'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'title'"/>
                        <xsl:with-param name="label" select="'%fldTitle%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/title"/>
                        <xsl:with-param name="size" select="'30'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'telephone'"/>
                        <xsl:with-param name="label" select="'%fldPhone%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/telephone"/>
                        <xsl:with-param name="size" select="'15'"/>
                        <xsl:with-param name="maxlength" select="'20'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'mobile'"/>
                        <xsl:with-param name="label" select="'%fldMobilePhone%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/mobile"/>
                        <xsl:with-param name="size" select="'15'"/>
                        <xsl:with-param name="maxlength" select="'20'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'fax'"/>
                        <xsl:with-param name="label" select="'%fldFax%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/fax"/>
                        <xsl:with-param name="size" select="'15'"/>
                        <xsl:with-param name="maxlength" select="'20'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'mail'"/>
                        <xsl:with-param name="label" select="'%fldEMail%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/mail"/>
                        <xsl:with-param name="size" select="'40'"/>
                        <xsl:with-param name="maxlength" select="'40'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                        <xsl:with-param name="onchange">validateEmail(this)</xsl:with-param>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="enhancedimageselector">
                        <xsl:with-param name="label" select="'%fldImage%:'"/>
                        <xsl:with-param name="name" select="'person_image'"/>
                        <xsl:with-param name="selectedkey" select="/contents/content/contentdata/image/@key"/>
                        <xsl:with-param name="width" select="/contents/content/contentdata/image/width"/>
                        <xsl:with-param name="height" select="/contents/content/contentdata/image/height"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                        <xsl:with-param name="imagetype" select="'body'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                
                <tr>
                    <xsl:call-template name="simplehtmleditor">
                        <xsl:with-param name="name" select="'description'"/>
                        <xsl:with-param name="label" select="'%fldDescription%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/description"/>
                        <xsl:with-param name="rows" select="'20'"/>
                        <xsl:with-param name="cols" select="'60'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
            </table>
			</fieldset>
            
        </xsl:template>
 
</xsl:stylesheet>