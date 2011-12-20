<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="xhtml" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>

  <xsl:include href="common/button.xsl" />

  <xsl:param name="errorcode"/>
  <xsl:param name="errormessage"/>
  <xsl:param name="version"/>
  <xsl:param name="copyright"/>
  <xsl:param name="selectedloginuserstore"/>

  <xsl:template match="/">
    <html dir="ltr" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title>%headForgotPasswd%</title>
        <link type="text/css" rel="stylesheet" media="screen" href="css/admin.css"/>
        <link type="text/css" rel="stylesheet" media="screen" href="css/login.css"/>
        <xsl:comment><![CDATA[[if IE 6]>
          <link type="text/css" rel="stylesheet" media="screen" href="css/login_ie6.css" />
        <![endif]]]></xsl:comment>
        <xsl:comment><![CDATA[[if IE 7]>
          <link type="text/css" rel="stylesheet" media="screen" href="css/login_ie7.css" />
        <![endif]]]></xsl:comment>

        <link rel="shortcut icon" type="image/x-icon" href="favicon.ico" />

          <script type="text/javascript">
            function bodyOnLoad() {
              if (self != top)
              {
                  top.location = self.document.location;
              }
              document.getElementsByName('uid')[0].focus();

              var userstore = document.getElementById('userstore');
              validateUserStore(userstore);
            }

              var g_userStoreCanUpdatePassword = [];

              <xsl:for-each select="/data/userstores/userstore">
                g_userStoreCanUpdatePassword[<xsl:value-of select="@key"/>] = "<xsl:value-of select="connector/config/user-policy/@can-update-password"/>";
              </xsl:for-each>

              function validateUserStore( selectElem )
              {
                  var sendPasswordDisplay = g_userStoreCanUpdatePassword[selectElem.value] == "false" ? 'none' : '';
                  var sendPwdWarningDisplay = sendPasswordDisplay == 'none' ? '' : 'none';

                  var sendPasswordLink = document.getElementById('send-pass-link');
                  var emailEditField = document.getElementById('emailer');
                  var sendPasswordWarningBox = document.getElementById('send-passw-warning');

                  sendPasswordLink.style.display = sendPasswordDisplay;
                  emailEditField.style.display = sendPasswordDisplay;
                  sendPasswordWarningBox.style.display = sendPwdWarningDisplay;
              }
          </script>
      </head>
      <body onload="bodyOnLoad();">
          <table id="wrapper" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td>
              <xsl:choose>
                <xsl:when test="$errorcode = '400_user_not_found'">
                  <p class="cms-error">%errUnknownID%</p>
                </xsl:when>
                <xsl:when test="$errorcode = '400_missing_uid'">
                  <p class="cms-error">%errMissingUID%</p>
                </xsl:when>
                <xsl:when test="$errorcode = '500_failed_send_mail'">
                  <p class="cms-error">%errCouldNotSendEmail%</p>
                </xsl:when>
                <xsl:when test="$errorcode = '401_access_denied'">
                  <p class="cms-error">%errLDAPUpdateNotAllowed%</p>
                </xsl:when>
                <xsl:when test="$errorcode = '500_unexpected_error'">
                  <p class="cms-error">%errUnknownError%:
                    <xsl:value-of select="$errormessage"/>
                  </p>
                </xsl:when>
                <xsl:otherwise>
                  <p class="cms-error">&nbsp;</p>
                </xsl:otherwise>
              </xsl:choose>

                <p class="cms-error" id="send-passw-warning">%msgUserstoreDoesNotSupportUpdatingPasswords%</p>

                <div id="inner">
                <xsl:call-template name="mailform"/>
              </div>
              <p class="version">
                <xsl:value-of select="concat('Enonic CMS ', $version)"/>
                <br/>
                <xsl:value-of select="$copyright"/>
                <xsl:text> All rights reserved.</xsl:text>
              </p>
            </td>
          </tr>
        </table>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="passwordsent">

  </xsl:template>

  <xsl:template name="mailform">
    <h1>
      %headForgotPasswd%
    </h1>
    <div id="form-container">
      <form name="formAdmin" target="_top" method="post" action="forgotpassword">
        <table border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td class="label-container">
              <label for="userstore">%fldUserstore%</label>
            </td>
            <td class="input-container">
              <xsl:choose>
                <xsl:when test="count(/data/userstores/userstore) = 1">
                  <select id="userstore" name="userstorekey">
                    <option value="{/data/userstores/userstore/@key}">
                      <xsl:value-of select="/data/userstores/userstore/@name"/>
                    </option>
                  </select>
                </xsl:when>
                <xsl:otherwise>
                  <select id="userstore" name="userstorekey" onchange="validateUserStore(this)">
                    <option value="">%sysDropDownChoose%</option>
                    <xsl:for-each select="/data/userstores/userstore">
                      <option value="{@key}">
                        <xsl:choose>
                          <xsl:when test="@default = 'true' and $selectedloginuserstore = ''">
                            <xsl:attribute name="selected">selected</xsl:attribute>
                          </xsl:when>
                          <xsl:when test="@key = $selectedloginuserstore">
                            <xsl:attribute name="selected">selected</xsl:attribute>
                          </xsl:when>
                        </xsl:choose>
                        <xsl:value-of select="@name"/>
                      </option>
                    </xsl:for-each>
                  </select>
                </xsl:otherwise>
              </xsl:choose>
            </td>
          </tr>
          <tr id="emailer">
            <td class="label-container">
              <label for="uname">%fldUIDorEmail%</label>
            </td>
            <td class="input-container">
              <input type="text" size="20" maxlength="40" id="uname" name="uid"/>
            </td>
          </tr>
          <tr id="send-pass-link">
            <td class="label-container">
              <br/>
            </td>
            <td class="input-container">
              <xsl:call-template name="button">
                <xsl:with-param name="type" select="'submit'"/>
                <xsl:with-param name="caption" select="'%cmdMailPasswd%'"/>
              </xsl:call-template>
            </td>
          </tr>
        </table>
      </form>
    </div>
    <div class="bottom-container">
      <span id="back-container">
        <a href="login">
          <xsl:text>%cmdBack%</xsl:text><br/>
        </a>
      </span>
    </div>
  </xsl:template>
</xsl:stylesheet>