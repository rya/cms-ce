<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

    <xsl:output method="html"/>

    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/replacesubstring.xsl"/>
    <xsl:include href="common/textfield.xsl"/>
    <xsl:include href="common/textarea.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>
    <xsl:include href="common/displayerror.xsl"/>
	<xsl:include href="common/serialize.xsl"/>
    
    <xsl:param name="sender_name"/>
    <xsl:param name="sender_mail"/>
    
	<xsl:param name="callback" />
	<xsl:param name="modeselector"/>
	<xsl:param name="userstoreselector"/>
	<xsl:param name="excludekey" />
	<xsl:param name="mode"/>
	<xsl:param name="userstorekey" />


    <xsl:template match="/">
        <html>
            <head>
                <link href="css/admin.css" rel="stylesheet" type="text/css"/>
                <script type="text/javascript" src="javascript/tabpane.js"/>
                <script type="text/javascript" src="javascript/validate.js"/>
                <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css"/>
                <script type="text/javascript" language="JavaScript">

					var validatedFields = new Array(3);
					validatedFields[0] = new Array("%fldFromMail%", "from_mail", validateRequired);
					validatedFields[1] = new Array("%fldToMail%", "to_mail", validateRequired);
					validatedFields[2] = new Array("%fldMailBody%", "mail_body", validateRequired);
					
					function validateAll(formName) {
						var f = document.forms[formName];
						if ( !checkAll(formName, validatedFields) )
							return;
						f.submit();
					}

                </script>
            </head>
            <body>
                <h1>
                    %headUserNotification%
                </h1>
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td class="browse_buttonrow_datarows_seperator"><img src="images/1x1.gif"/></td>
                    </tr>
                </table>
                <form name="formAdmin" method="post">
                    <xsl:attribute name="action">
                        <xsl:text>adminpage?page=</xsl:text>
                        <xsl:value-of select="$page"/>
                        <xsl:text>&amp;op=sendnotification</xsl:text>
                        <xsl:text>&amp;callback=</xsl:text>
						<xsl:value-of select="$callback" />
						<xsl:text>&amp;mode=</xsl:text>
						<xsl:value-of select="$mode" />
						<xsl:text>&amp;userstorekey=</xsl:text>
						<xsl:value-of select="$userstorekey" />
						<xsl:if test="$modeselector">
							<xsl:text>&amp;modeselector=</xsl:text>
							<xsl:value-of select="$modeselector"/>
						</xsl:if>
						<xsl:if test="$userstoreselector">
							<xsl:text>&amp;userstoreselector=</xsl:text>
							<xsl:value-of select="$userstoreselector"/>
						</xsl:if>
						<xsl:if test="$excludekey">
							<xsl:text>&amp;excludekey=</xsl:text>
							<xsl:value-of select="$excludekey"/>
						</xsl:if>
                    </xsl:attribute>

                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td>
                            <div class="tab-pane" id="tab-pane-1">
                                <script type="text/javascript" language="JavaScript">
                                    var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                                </script>

                                <div class="tab-page" id="tab-page-1">
                                    <span class="tab">%blockUserNotification%</span>

                                    <script type="text/javascript" language="JavaScript">
                                        tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                                    </script>

                                    <fieldset>
                                        <table cellspacing="0" cellpadding="2" border="0">
                                            <tr>
                                                <xsl:call-template name="textfield">
                                                    <xsl:with-param name="label" select="'%fldFromName%:'"/>
                                                    <xsl:with-param name="name" select="'from_name'"/>
                                                    <xsl:with-param name="selectnode" select="$sender_name"/>
                                                </xsl:call-template>
                                            </tr>
                                            <tr>
                                                <xsl:call-template name="textfield">
                                                    <xsl:with-param name="label" select="'%fldFromMail%:'"/>
                                                    <xsl:with-param name="name" select="'from_mail'"/>
                                                    <xsl:with-param name="selectnode" select="$sender_mail"/>
                                                    <xsl:with-param name="required" select="'true'"/>
                                                    </xsl:call-template>
                                            </tr>
                                            <tr>
                                                <xsl:call-template name="textfield">
                                                    <xsl:with-param name="label" select="'%fldToName%:'"/>
                                                    <xsl:with-param name="name" select="'to_name'"/>
                                                    <xsl:with-param name="selectnode" select="/user/displayName"/>
                                                </xsl:call-template>
                                            </tr>
                                            <tr>
                                                <xsl:call-template name="textfield">
                                                    <xsl:with-param name="label" select="'%fldToMail%:'"/>
                                                    <xsl:with-param name="name" select="'to_mail'"/>
                                                    <xsl:with-param name="selectnode" select="/user/email"/>
                                                    <xsl:with-param name="required" select="'true'"/>
                                                  </xsl:call-template>
                                            </tr>
                                            <tr>
                                                <xsl:call-template name="textfield">
                                                    <xsl:with-param name="label" select="'%fldCC%:'"/>
                                                    <xsl:with-param name="name" select="'cc_mail'"/>
                                                    <xsl:with-param name="size" select="40"/>
                                                </xsl:call-template>
                                            </tr>
                                            <tr>
                                                <xsl:call-template name="textfield">
                                                    <xsl:with-param name="label" select="'%fldSubject%:'"/>
                                                    <xsl:with-param name="name" select="'subject'"/>
                                                    <xsl:with-param name="size" select="60"/>
                                                </xsl:call-template>
                                            </tr>
                                            <tr>
                                                <xsl:call-template name="textarea">
                                                    <xsl:with-param name="label" select="'%fldMailBody%:'"/>
                                                    <xsl:with-param name="name" select="'mail_body'"/>
                                                    <xsl:with-param name="cols" select="60"/>
                                                    <xsl:with-param name="rows" select="7"/>
                                                    <xsl:with-param name="required" select="'true'"/>
                                                </xsl:call-template>
                                            </tr>
                                        </table>
                                    </fieldset>
                                </div>
                            </div>
                            <script type="text/javascript" language="JavaScript">
                                setupAllTabs();
                            </script>
                        </td>
                    </tr>
                    <tr>
                        <td class="form_form_buttonrow_seperator"><img src="images/1x1.gif"/></td>
                    </tr>
                    
                    <tr>
                        <td colspan="2">
                            <xsl:call-template name="button">
                                <xsl:with-param name="name" select="'sendmail'"/>
                                <xsl:with-param name="type" select="'button'"/>
                                <xsl:with-param name="caption" select="'%cmdSendMail%'"/>
                                <xsl:with-param name="onclick">
                                    <xsl:text>javascript:validateAll('formAdmin');</xsl:text>
                                </xsl:with-param>
                            </xsl:call-template>
                            <xsl:text>&nbsp;</xsl:text>
                            <xsl:call-template name="button">
                                <xsl:with-param name="type" select="'link'"/>
                                <xsl:with-param name="name" select="'cancel'"/>
                                <xsl:with-param name="href">
                                    <xsl:text>adminpage?page=</xsl:text>
                                    <xsl:value-of select="$page"/>
                                    <xsl:text>&amp;op=browse</xsl:text>
                                    <xsl:text>&amp;callback=</xsl:text>
									<xsl:value-of select="$callback" />
									<xsl:text>&amp;mode=</xsl:text>
									<xsl:value-of select="$mode" />
									<xsl:text>&amp;userstorekey=</xsl:text>
									<xsl:value-of select="$userstorekey" />
									<xsl:if test="$modeselector">
										<xsl:text>&amp;modeselector=</xsl:text>
										<xsl:value-of select="$modeselector"/>
									</xsl:if>
									<xsl:if test="$userstoreselector">
										<xsl:text>&amp;userstoreselector=</xsl:text>
										<xsl:value-of select="$userstoreselector"/>
									</xsl:if>
									<xsl:if test="$excludekey">
										<xsl:text>&amp;excludekey=</xsl:text>
										<xsl:value-of select="$excludekey"/>
									</xsl:if>
                                </xsl:with-param>
                                <xsl:with-param name="caption" select="'%cmdCancel%'"/>
                            </xsl:call-template>
                        </td>
                    </tr>
                    
                </table>
            </form>

            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
