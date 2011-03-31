<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html"/>
    
    <xsl:include href="common/standard_form_templates.xsl"/>
    <xsl:include href="common/textfield.xsl"/>
    <xsl:include href="common/textarea.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>
    <xsl:include href="common/generic_formheader.xsl"/>
    <xsl:include href="common/serialize.xsl"/>
    
    <xsl:param name="create"/>
    <xsl:param name="replyposting"/>
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
                // variables use by menu.js
                var branchOpen = new Array();
                var cookiename = "contentform";

                  var validatedFields = new Array(4);
                  validatedFields[0] = new Array("%fldAuthor%", "author", validateRequired);
                  validatedFields[1] = new Array("%fldEmail%", "email", validateEmail);
                  validatedFields[2] = new Array("%fldTitle%", "title", validateRequired);
                  validatedFields[3] = new Array("%fldMessage%", "body", validateRequired);

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
            <legend>&nbsp;%fldMessage%&nbsp;</legend>
            <table border="0" cellspacing="0" cellpadding="2">
				
        <xsl:choose>
            <xsl:when test="$replyposting='true'">
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'author'"/>
                        <xsl:with-param name="label" select="'%fldAuthor%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/users/user/@fullname"/>
                        <xsl:with-param name="size" select="'30'"/>
                        <xsl:with-param name="maxlength" select="'50'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'email'"/>
                        <xsl:with-param name="label" select="'%fldEmail%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/users/user/block/email"/>
                        <xsl:with-param name="size" select="'30'"/>
                        <xsl:with-param name="maxlength" select="'50'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                        <xsl:with-param name="onchange">validateEmail(this)</xsl:with-param>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'title'"/>
                        <xsl:with-param name="label" select="'%fldTitle%:'"/>
                        <xsl:with-param name="selectnode" select="concat('SV: ',/contents/content/contentdata/title)"/>
                        <xsl:with-param name="size" select="'80'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                        <xsl:with-param name="onkeyup">
                            <xsl:text>javascript:document.getElementById('titlename').innerHTML = this.value;</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textarea">
                        <xsl:with-param name="name" select="'body'"/>
                        <xsl:with-param name="label" select="'%fldMessage%:'"/>
                        <xsl:with-param name="selectnode" select="concat('&#xD;&#xD;&#xD;&#xD;--- %fldOriginalMessage% ---&#xD;',/contents/content/contentdata/body)"/>
                        <xsl:with-param name="rows" select="'30'"/>
                        <xsl:with-param name="cols" select="'60'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="$create=1">
                        <tr>
                            <xsl:call-template name="textfield">
                                <xsl:with-param name="name" select="'author'"/>
                                <xsl:with-param name="label" select="'%fldAuthor%:'"/>
                                <xsl:with-param name="selectnode" select="/contents/users/user/@fullname"/>
                                <xsl:with-param name="size" select="'30'"/>
                                <xsl:with-param name="maxlength" select="'50'"/>
                                <xsl:with-param name="colspan" select="'3'"/>
								<xsl:with-param name="disabled" select="$readonly"/>
                            </xsl:call-template>
                        </tr>
                        <tr>
                            <xsl:call-template name="textfield">
                                <xsl:with-param name="name" select="'email'"/>
                                <xsl:with-param name="label" select="'%fldEmail%:'"/>
                                <xsl:with-param name="selectnode" select="/contents/users/user/block/email"/>
                                <xsl:with-param name="size" select="'30'"/>
                                <xsl:with-param name="maxlength" select="'50'"/>
                                <xsl:with-param name="colspan" select="'3'"/>
								<xsl:with-param name="disabled" select="$readonly"/>
                                <xsl:with-param name="onchange">validateEmail(this)</xsl:with-param>
                            </xsl:call-template>
                        </tr>
                    </xsl:when>
                    <xsl:otherwise>
                        <tr>
                            <xsl:call-template name="textfield">
                                <xsl:with-param name="name" select="'author'"/>
                                <xsl:with-param name="label" select="'%fldAuthor%'"/>
                                <xsl:with-param name="selectnode" select="/contents/content/contentdata/author"/>
                                <xsl:with-param name="size" select="'30'"/>
                                <xsl:with-param name="maxlength" select="'50'"/>
                                <xsl:with-param name="colspan" select="'3'"/>
								<xsl:with-param name="disabled" select="$readonly"/>
                            </xsl:call-template>
                        </tr>
                        <tr>
                            <xsl:call-template name="textfield">
                                <xsl:with-param name="name" select="'email'"/>
                                <xsl:with-param name="label" select="'%fldEmail%:'"/>
                                <xsl:with-param name="selectnode" select="/contents/content/contentdata/email"/>
                                <xsl:with-param name="size" select="'30'"/>
                                <xsl:with-param name="maxlength" select="'50'"/>
                                <xsl:with-param name="colspan" select="'3'"/>
								<xsl:with-param name="disabled" select="$readonly"/>
                            </xsl:call-template>
                        </tr>
                    </xsl:otherwise>
                </xsl:choose>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'title'"/>
                        <xsl:with-param name="label" select="'%fldTitle%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/title"/>
                        <xsl:with-param name="size" select="'80'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                        <xsl:with-param name="onkeyup">
                          <xsl:text>javascript:updateBreadCrumbHeader( 'titlename', this );</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textarea">
                        <xsl:with-param name="name" select="'body'"/>
                        <xsl:with-param name="label" select="'%fldMessage%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/body"/>
                        <xsl:with-param name="rows" select="'30'"/>
                        <xsl:with-param name="cols" select="'60'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
            </xsl:otherwise>
        </xsl:choose>
		
		</table>
		
		</fieldset>
        
    </xsl:template>

</xsl:stylesheet>