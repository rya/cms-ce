<?xml version="1.0" encoding="utf-8"?>

<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
 ]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
  <xsl:output method="html"/>

  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:include href="common/generalhiddenfields.xsl"/>
  <xsl:include href="common/genericheader.xsl"/>
  <xsl:include href="common/categoryheader.xsl"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="common/readonlyvalue.xsl"/>
  <xsl:include href="common/textfield.xsl"/>
  <xsl:include href="common/textarea.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>
  <xsl:include href="common/displayerror.xsl"/>
  <xsl:include href="common/serialize.xsl"/>
    
  <xsl:param name="create"/>
  <xsl:param name="contenttypekey"/>
  <xsl:param name="modulename"/>
  <xsl:param name="user_fullname"/>
  <xsl:param name="user_email"/>
  <xsl:param name="user_userstorekey"/>

  <xsl:variable name="enableform" select="true()"/>

  <xsl:template match="/">
    <html>
      <head>
        <link href="css/admin.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" src="javascript/validate.js"/>
        <script type="text/javascript" src="javascript/tabpane.js">//</script>
        <script type="text/javascript" src="javascript/groups.js">//</script>
        <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css" />
        <script type="text/javascript" language="JavaScript">
          var validatedFields = new Array(3);
          validatedFields[0] = new Array("%fldName%", "sender_name", validateRequired);
          validatedFields[1] = new Array("%fldEmail%", "sender_email", validateRequired);
          validatedFields[2] = new Array("%fldEmail%", "sender_email", validateEmail);

          function validateAll(formName)
          {
            if ( !checkAll(formName, validatedFields) )
              return false;
            var f = document.forms[formName];
            f.submit();
          }
        </script>

      </head>

      <body>
        <xsl:call-template name="cat_formheader"/>
                
        <form name="formAdmin" method="post">
          <xsl:attribute name="action">
            <xsl:text>adminpage?page=</xsl:text>
            <xsl:value-of select="$page"/>
            <xsl:text>&amp;op=confirmsend</xsl:text>
            <xsl:text>&amp;cat=</xsl:text>
            <xsl:value-of select="$cat"/>
          </xsl:attribute>

          <xsl:call-template name="generalhiddenfields"/>

          <table width="100%" border="0" cellspacing="0" cellpadding="2">
            <!-- separator -->
            <tr>
              <td class="form_title_form_seperator"><img src="images/1x1.gif"/></td>
            </tr>
                        
            <!-- form -->
            <tr>
              <td>
                <div class="tab-pane" id="tab-pane-1">
                  <script type="text/javascript" language="JavaScript">
                    var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                  </script>
                                    
                  <div class="tab-page" id="tab-page-1">
                    <span class="tab">%blockSendNewsletter%</span>
                                        
                    <script type="text/javascript" language="JavaScript">
                      tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                    </script>

                    <fieldset>
                      <legend>&nbsp;%blockSender%&nbsp;</legend>
                      <table>
                        <tr>
                          <xsl:call-template name="textfield">
                            <xsl:with-param name="name" select="'sender_name'"/>
                            <xsl:with-param name="label" select="'%fldName%:'"/>
                            <xsl:with-param name="selectnode" select="$user_fullname"/>
                            <xsl:with-param name="size" select="'40'"/>
                            <xsl:with-param name="maxlength" select="'255'"/>
                            <xsl:with-param name="colspan" select="'1'"/>
                            <xsl:with-param name="required" select="'true'"/>
                          </xsl:call-template>
                        </tr>
                        <tr>
                          <xsl:call-template name="textfield">
                            <xsl:with-param name="name" select="'sender_email'"/>
                            <xsl:with-param name="label" select="'%fldEmail%:'"/>
                            <xsl:with-param name="selectnode" select="$user_email"/>
                            <xsl:with-param name="size" select="'40'"/>
                            <xsl:with-param name="maxlength" select="'255'"/>
                            <xsl:with-param name="colspan" select="'1'"/>
                            <xsl:with-param name="required" select="'true'"/>
                          </xsl:call-template>
                        </tr>
                      </table>
                    </fieldset>

                    <fieldset>
                      <legend>&nbsp;%blockRecipients%&nbsp;</legend>
                      <table border="0" cellspacing="2" cellpadding="0" width="100%">
                        <tr>
                          <td colspan="2" style="padding-top: 1em;">
                            <table border="0" cellspacing="0" cellpadding="2"  style="width: 60%">
                              <tbody id="memberstable">
                              </tbody>
                            </table>
                          </td>
                        </tr>
                        <tr>
                          <td colspan="2">
                            <br/>

                            <xsl:call-template name="button">
                              <xsl:with-param name="name" select="'butAddRecipient'"/>
                              <xsl:with-param name="type" select="'button'"/>
                              <xsl:with-param name="caption" select="'%cmdAdd%'"/>
                              <xsl:with-param name="onclick">
                                <xsl:text>javascript:showUserAndGroupsPopup('', 'users', true, '', '', true);</xsl:text>
                              </xsl:with-param>
                            </xsl:call-template>
                          </td>
                        </tr>
                        <tr>
                          <td colspan="2" style="padding-top: 1em;">
                            <table border="0" cellspacing="0" cellpadding="2" id="memberstable" style="width: 60%">
                            </table>
                          </td>
                        </tr>
                        <tr>
                          <xsl:call-template name="textarea">
                            <xsl:with-param name="name" select="'other_recipients'"/>
                            <xsl:with-param name="label" select="'%fldOtherRecipients%:'"/>
                            <xsl:with-param name="selectnode" select="''"/>
                            <xsl:with-param name="rows" select="'10'"/>
                            <xsl:with-param name="cols" select="'60'"/>
                            <xsl:with-param name="colspan" select="'1'"/>
                          </xsl:call-template>
                        </tr>
                        <tr>
                          <xsl:call-template name="textarea">
                            <xsl:with-param name="name" select="'config'"/>
                            <xsl:with-param name="label" select="'%fldConfig%:'"/>
                            <xsl:with-param name="selectnode" select="''"/>
                            <xsl:with-param name="rows" select="'30'"/>
                            <xsl:with-param name="cols" select="'90'"/>
                            <xsl:with-param name="colspan" select="'1'"/>
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
              <td >
                <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'button'"/>
                  <xsl:with-param name="caption" select="'%cmdConfirmSend%'"/>
                  <xsl:with-param name="name" select="'sendnewsletter'"/>
                  <xsl:with-param name="onclick">
                    <xsl:text>javascript:validateAll('formAdmin');</xsl:text>
                  </xsl:with-param>
                </xsl:call-template>

                <xsl:text>&nbsp;</xsl:text>

                <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'link'"/>
                  <xsl:with-param name="caption" select="'%cmdCancelSend%'"/>
                  <xsl:with-param name="name" select="'cancelsend'"/>
                  <xsl:with-param name="href" select="'javascript:history.back()'"/>
                </xsl:call-template>

                <xsl:text>&nbsp;</xsl:text>

                <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'link'"/>
                  <xsl:with-param name="name" select="'previewnewsletter'"/>
                  <xsl:with-param name="tooltip" select="'%tooltipPreviewNewsletter%'"/>
                  <xsl:with-param name="caption" select="'%cmdPreviewNewsletter%'"/>
                  <xsl:with-param name="href">
                    <xsl:text>adminpage?page=</xsl:text>
                    <xsl:value-of select="$page"/>
                    <xsl:text>&amp;op=preview</xsl:text>
                  </xsl:with-param>
                  <xsl:with-param name="target" select="'_blank'"/>
                </xsl:call-template>
              </td>
            </tr>
          </table>
        </form>

      </body>
    </html>
  </xsl:template>

  <xsl:template name="cat_formheader">
    <xsl:param name="name" select="$modulename"/>
    <xsl:param name="withsitename" select="false()"/>

    <h1>
      <xsl:call-template name="genericheader">
        <xsl:with-param name="endslash" select="false()"/>
      </xsl:call-template>
      <xsl:call-template name="categoryheader"/>
    </h1>
    <h2>%headSend%: <span id="titlename"><xsl:value-of select="/contents/content/title"/></span></h2>
  </xsl:template>

</xsl:stylesheet>