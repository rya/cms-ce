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
    <xsl:include href="common/textarea.xsl"/>
    <xsl:include href="common/textfield.xsl"/>
    <xsl:include href="common/dropdown_root.xsl"/>
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
                // variables use by menu.js
                var branchOpen = new Array();
                var cookiename = "contentform";

                  var validatedFields = new Array(4);
                  validatedFields[0] = new Array("%fldTitle%", "title", validateRequired);
                  validatedFields[1] = new Array("%fldURL%", "url", validateURL);
                  validatedFields[2] = new Array("%fldURL%", "url", validateRequired);
                  validatedFields[3] = new Array("%fldEMail%", "author_email", validateEmail);

                  function validateAll(formName)
                  {
                    var f = document.forms[formName];

                <xsl:if test="not(/contents/userright) or /contents/userright/@publish = 'true'">
                    var tabPagePublishing = document.getElementById( "tab-page-publishing" );
                    if ( !checkAll(formName, extraValidatedFields, tabPagePublishing ) )
                      return;
                </xsl:if>

                
                    if ( !checkAll(formName, validatedFields) )
                      return;

                    disableFormButtons();
		    f.submit();
                  }



			function OpenSelectorWindowSiteCty( page, width, height )
			{
				newWindow = window.open("adminpage?page=" + page + "&amp;op=select&amp;contenttypekey=" + document.formAdmin.contenttypekey.value, "Preview", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" + height);
				newWindow.focus();
			}
			function OpenSelectorWindowSiteUnit( page, keyname, viewname, width, height )
			{
				newWindow = window.open("adminpage?page=" + page + "&amp;returnkey=" + keyname + "&amp;returnview=" + viewname + "&amp;op=select&amp;unitkey=" + document.formAdmin.unitkey.value, "Preview", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" + height);
				newWindow.focus();
			}
            </script>
          </head>

            <xsl:call-template name="contentform"/>
    </html>
    
</xsl:template>

    <xsl:template name="contenttypeform">
		<xsl:param name="readonly"/>
		
        <fieldset>
            <legend>&nbsp;%blockLink%&nbsp;</legend>
            <table border="0" cellspacing="0" cellpadding="2">
                <tr><td class="form_labelcolumn"></td></tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'title'"/>
                        <xsl:with-param name="label" select="'%fldTitle%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/title"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						            <xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:choose>
                        <xsl:when test="$create=1">
                            <xsl:call-template name="textfield">
                                <xsl:with-param name="name" select="'url'"/>
                                <xsl:with-param name="label" select="'%fldURL%:'"/>
                                <xsl:with-param name="selectnode" select="'http://'"/>
                                <xsl:with-param name="size" select="'60'"/>
                                <xsl:with-param name="maxlength" select="'500'"/>
                                <xsl:with-param name="colspan" select="'3'"/>
								<xsl:with-param name="disabled" select="$readonly"/>
                            </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:call-template name="textfield">
                                <xsl:with-param name="name" select="'url'"/>
                                <xsl:with-param name="label" select="'%fldURL%:'"/>
                                <xsl:with-param name="selectnode" select="/contents/content/contentdata/url"/>
                                <xsl:with-param name="size" select="'60'"/>
                                <xsl:with-param name="maxlength" select="'500'"/>
                                <xsl:with-param name="colspan" select="'3'"/>
                                <xsl:with-param name="onchange">validateURL(this)</xsl:with-param>
								<xsl:with-param name="disabled" select="$readonly"/>
                            </xsl:call-template>
                        </xsl:otherwise>
                    </xsl:choose>
                </tr>
                <tr>
                    <xsl:call-template name="textarea">
                        <xsl:with-param name="name" select="'description'"/>
                        <xsl:with-param name="label" select="'%fldDescription%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/description"/>
                        <xsl:with-param name="rows" select="'5'"/>
                        <xsl:with-param name="cols" select="'60'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                
                <tr>
                    <td>%fldKeywords%:</td>
                    <td colspan="3">
                        <input type="text" name="contentdata_keywords" size="60" maxlength="100">
							<xsl:if test="$readonly">
								<xsl:attribute name="disabled">disabled</xsl:attribute>
							</xsl:if>
                            <xsl:attribute name="value">
                                <xsl:for-each select="/contents/content/contentdata/keywords/keyword">
                                    <xsl:sort select="."/>
                                    <xsl:value-of select="."/>
                                    <xsl:text> </xsl:text>
                                </xsl:for-each>
                            </xsl:attribute>
                        </input>
                    </td>
                </tr>
                <tr>
                    <xsl:choose>
                        <xsl:when test="$create=1">
                            <xsl:call-template name="dropdown_root">
                                <xsl:with-param name="name" select="'country'"/>
                                <xsl:with-param name="label" select="'%fldCountry%:'"/>
                                <xsl:with-param name="selectedkey" select="''"/>
                                <xsl:with-param name="selectnode" select="/contents/countries/country"/>
                                <xsl:with-param name="emptyrow" select="'%sysDropDownChoose%'"/>
								<xsl:with-param name="disabled" select="$readonly"/>
                            </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:call-template name="dropdown_root">
                                <xsl:with-param name="name" select="'country'"/>
                                <xsl:with-param name="label" select="'%fldCountry%:'"/>
                                <xsl:with-param name="selectedkey" select="/contents/content/contentdata/country"/>
                                <xsl:with-param name="selectnode" select="/contents/countries/country"/>
                                <xsl:with-param name="emptyrow" select="'%sysDropDownChoose%'"/>
								<xsl:with-param name="disabled" select="$readonly"/>
                            </xsl:call-template>
                        </xsl:otherwise>
                    </xsl:choose>
                </tr>
            </table>
        </fieldset>
        <fieldset>
            <legend>&nbsp;%blockAuthor%&nbsp;</legend>
            <table border="0" cellspacing="0" cellpadding="2">
                <tr><td class="form_labelcolumn"></td></tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'author_name'"/>
                        <xsl:with-param name="label" select="'%fldName%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/author/name"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'author_phone'"/>
                        <xsl:with-param name="label" select="'%fldPhone%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/author/phone"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'author_email'"/>
                        <xsl:with-param name="label" select="'%fldEMail%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/author/email"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                        <xsl:with-param name="onchange">validateEmail(this)</xsl:with-param>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'author_org'"/>
                        <xsl:with-param name="label" select="'%fldOrganization%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/author/org"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
            </table>
        </fieldset>          
    </xsl:template>

</xsl:stylesheet>