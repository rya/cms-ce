<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="xhtml" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:param name="errorcode"/>
  <xsl:param name="selectedloginuserstore"/>
  <xsl:param name="errormessage"/>
  <xsl:param name="site"/>
  <xsl:param name="username"/>
  <xsl:param name="password"/>
  <xsl:param name="languagecode"/>
  <xsl:param name="logintitle" select="'%headLogin%'"/>
  <xsl:param name="version"/>
  <xsl:param name="copyright"/>
  <xsl:param name="license_status"/>
  <xsl:param name="license_message"/>

  <xsl:template match="/">
    <html xml:lang="{$languagecode}" lang="{$languagecode}" xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title>Enonic CMS - Login</title>
        <link type="text/css" rel="stylesheet" media="screen" href="css/admin.css" />
        <link type="text/css" rel="stylesheet" media="screen" href="css/login.css" />
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

              if (typeof(browserns)=='undefined')
              {
                  document.getElementsByName('username')[0].focus();
              }
          }

          function submitLanguageForm(languageField) {
              document.location="login?lang="+languageField.value;
          }
          // -------------------------------------------------------------------------------------

          var g_userStoreErrorMessages = [];
          
          <xsl:for-each select="/data/userstores/userstore">
            <xsl:sort select="@name"/>
            g_userStoreErrorMessages[<xsl:value-of select="position() - 1"/>] = "<xsl:value-of select="connector/config/errors/error"/>";
          </xsl:for-each>

          function validateUserStore( selectElem )
          {
              var selectedIndex = selectElem.selectedIndex - 1;
              var errorMessage = g_userStoreErrorMessages[selectedIndex];
              var errorMessageContainerElem = document.getElementById('userstore-error');

              if ( selectElem.value !== '' )
              {
                errorMessageContainerElem.innerHTML = errorMessage;
              }
              else
              {
                errorMessageContainerElem.innerHTML = '';
              }
          }
        </script>
      </head>
      <body onload="bodyOnLoad();">
        <table id="wrapper" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td>
              <xsl:choose>
                <xsl:when test="$errorcode = ''">
                  <p class="cms-error">&nbsp;</p>
                </xsl:when>
                <xsl:when test="$errorcode = '401_missing_user_passwd'">
                  <p class="cms-error">%errMissingUsernamePassword%</p>
                </xsl:when>
                <xsl:when test="$errorcode = '401_user_passwd_wrong'">
                  <p class="cms-error">%errWrongUsernamePassword%</p>
                </xsl:when>
                <xsl:when test="$errorcode = '401_access_denied'">
                  <p class="cms-error">%errAccessDenied%</p>
                </xsl:when>
                <xsl:otherwise>
                  <p class="cms-error">%errUnexpectedError%:
                    <xsl:text> </xsl:text>
                    <xsl:value-of select="$errormessage"/>
                  </p>
                </xsl:otherwise>
              </xsl:choose>

              <p class="cms-error" id="userstore-error">
                <xsl:choose>
                  <xsl:when test="count(/data/userstores/userstore) = 1">
                    <xsl:if test="/data/userstores/userstore/connector/config/errors/error">
                      <xsl:value-of select="/data/userstores/userstore/connector/config/errors/error"/>
                    </xsl:if>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:comment>Empty</xsl:comment>
                  </xsl:otherwise>
                </xsl:choose>
              </p>

              <div id="inner">
                <h1>
                  <xsl:value-of select="$logintitle"/>
                </h1>
                <div id="form-container">
                  <form action="login" method="POST" id="formLogin" name="formLogin">
                    <input value="true" name="login" type="hidden"/>
                    <table border="0" cellpadding="0" cellspacing="0">
                      <tr>
                        <td class="label-container">
                          <label for="userstore">%fldUserstore%</label>
                        </td>
                        <td class="input-container">
                          <xsl:choose>
                            <xsl:when test="count(/data/userstores/userstore) = 1">
                              <select name="userstorekey" id="userstore">
                                <option value="{/data/userstores/userstore/@key}">
                                  <xsl:value-of select="/data/userstores/userstore/@name"/>
                                </option>
                              </select>
                            </xsl:when>
                            <xsl:otherwise>
                              <select name="userstorekey" id="userstore" onchange="validateUserStore(this)">
                                <option value="">%sysDropDownChoose%</option>
                                <xsl:for-each select="/data/userstores/userstore">
                                  <xsl:sort select="@name"/>
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
                      <tr>
                        <td class="label-container">
                          <label for="uname">%fldUserId%</label>
                        </td>
                        <td class="input-container">
                          <input value="" name="username" id="uname" maxlength="256" type="text"/>
                        </td>
                      </tr>
                      <tr>
                        <td class="label-container">
                          <label for="upasswd">%fldPassword%</label>
                        </td>
                        <td class="input-container">
                          <input value="" name="password" id="upasswd" maxlength="20" type="password"/>
                        </td>
                      </tr>
                      <tr>
                        <td class="label-container">
                          <br/>
                        </td>
                        <td class="input-container">
                          <xsl:call-template name="button">
                            <xsl:with-param name="name" select="'login'"/>
                            <xsl:with-param name="type" select="'submit'"/>
                            <xsl:with-param name="caption" select="'%cmdLogin%'"/>
                          </xsl:call-template>
                        </td>
                      </tr>
                    </table>
                  </form>
                </div>
                <div class="bottom-container">
                  <div id="lang-container">
                    <select id="lang" name="lang" onchange="javascript:submitLanguageForm(this);">
                      <xsl:for-each select="/data/languages/language">
                        <xsl:sort select="@description" data-type="text"/>
                        <option value="{@code}">
                          <xsl:if test="@code = $languagecode">
                            <xsl:attribute name="selected">selected</xsl:attribute>
                          </xsl:if>
                          <xsl:value-of select="@description"/>
                        </option>
                      </xsl:for-each>
                    </select>
                  </div>
                  <div id="fgpwd-container">
                    <a href="forgotpassword">
                      <xsl:text>%msgForgottenPassword%</xsl:text>
                    </a>
                  </div>
                </div>
              </div>
              <p class="version">
                <xsl:value-of select="concat('Enonic CMS ', $version)"/>
                <br/>
                <xsl:value-of select="$copyright"/>
                <xsl:text> All rights reserved.</xsl:text>
              </p>
              <xsl:if test="$license_status != 'ok'">
                <p class="license-{$license_status}">
                  <xsl:value-of select="$license_message"/>
                </p>
              </xsl:if>
            </td>
          </tr>
        </table>

        <!--
          Cahce the waitsplash image.
        -->
        <div style="display:none">
          <img src="images/waitsplash.gif" alt="," width="70" height="70"/>
        </div>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
