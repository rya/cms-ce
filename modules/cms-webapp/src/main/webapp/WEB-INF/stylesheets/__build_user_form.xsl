<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
    ]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:x="mailto:vro@enonic.com?subject=foobar"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:namespace-alias stylesheet-prefix="x" result-prefix="xsl"/>
  <xsl:output method="xml"/>

  <xsl:param name="xslpath"/>

  <xsl:template match="/">
    <x:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

      <x:output method="html"/>

      <x:include href="{concat($xslpath, 'common/generic_parameters.xsl')}"/>
      <x:include href="{concat($xslpath, 'common/formatdate.xsl')}"/>
      <x:include href="{concat($xslpath, 'common/dropdown.xsl')}"/>
      <x:include href="{concat($xslpath, 'common/textfield.xsl')}"/>
      <x:include href="{concat($xslpath, 'common/textfielddate.xsl')}"/>
      <x:include href="{concat($xslpath, 'common/filefield.xsl')}"/>
      <x:include href="{concat($xslpath, 'common/textarea.xsl')}"/>
      <x:include href="{concat($xslpath, 'common/checkbox_boolean.xsl')}"/>
      <x:include href="{concat($xslpath, 'common/passwordfield.xsl')}"/>
      <x:include href="{concat($xslpath, 'common/button.xsl')}"/>
      <x:include href="{concat($xslpath, 'common/displaypath.xsl')}"/>
      <x:include href="{concat($xslpath, 'common/displayuserstorepath.xsl')}"/>
      <x:include href="{concat($xslpath, 'common/displayhelp.xsl')}"/>
      <x:include href="{concat($xslpath, 'common/displayerror.xsl')}"/>
      <x:include href="{concat($xslpath, 'common/labelcolumn.xsl')}"/>
      <x:include href="{concat($xslpath, 'common/readonlyvalue.xsl')}"/>
      <x:include href="{concat($xslpath, 'common/serialize.xsl')}"/>

      <x:include>
        <xsl:attribute name="href">
          <xsl:value-of select="concat($xslpath, 'common/tablecolumnheader.xsl')"/>
        </xsl:attribute>
      </x:include>

      <x:include>
        <xsl:attribute name="href">
          <xsl:value-of select="concat($xslpath, 'common/tablerowpainter.xsl')"/>
        </xsl:attribute>
      </x:include>

      <x:include>
        <xsl:attribute name="href">
          <xsl:value-of select="concat($xslpath, 'common/logentryutils.xsl')"/>
        </xsl:attribute>
      </x:include>

      <xsl:variable name="pathprefix" select="concat($xslpath, 'objectclass_stylesheets/')"/>
      <xsl:for-each select="/objectclasses/objectclass">
        <x:include href="{concat(concat($pathprefix, oid), '.xsl')}"/>
      </xsl:for-each>

      <x:param name="create"/>
      <x:param name="domainkey"/>
      <x:param name="userstorekey"/>
      <x:param name="userstorename"/>
      <x:param name="isadmin" select="'false'"/>
      <x:param name="xslpath"/>
      <x:param name="searchtext"/>
      <x:param name="searchtype"/>
      <x:param name="step"/>
      <x:param name="wizard" select="false()"/>
      <x:param name="uid"/>

      <x:param name="profile" select="false()"/>

      <x:param name="canUpdateUser" select="'false'"/>
      <x:param name="showdn"/>

      <!-- params for the notification form -->
      <x:param name="notification"/>
      <x:param name="from_name"/>
      <x:param name="from_mail"/>
      <x:param name="to_name"/>
      <x:param name="to_mail"/>
      <x:param name="subject" select="'%notifyMailSubject%'"/>
      <x:param name="mail_body"/>

      <x:param name="languagecode"/>

      <x:param name="mode"/>
      <x:param name="callback"/>
      <x:param name="modeselector"/>
      <x:param name="userstoreselector"/>
      <x:param name="excludekey"/>

      <x:param name="isRemote" select="/users/userstore/@remote = 'true'"/>

      <x:param name="generated-display-name"/>

      <x:variable name="browsepageURL">
        <x:text>adminpage?page=</x:text>
        <x:value-of select="$page"/>
        <x:text>&amp;op=browse</x:text>
        <x:text>&amp;userstorekey=</x:text>
        <x:value-of select="$userstorekey"/>
        <x:text>&amp;callback=</x:text>
        <x:value-of select="$callback"/>
        <x:text>&amp;mode=</x:text>
        <x:value-of select="$mode"/>
        <x:if test="$modeselector">
          <x:text>&amp;modeselector=</x:text>
          <x:value-of select="$modeselector"/>
        </x:if>
        <x:if test="$userstoreselector">
          <x:text>&amp;userstoreselector=</x:text>
          <x:value-of select="$userstoreselector"/>
        </x:if>
        <x:if test="$excludekey">
          <x:text>&amp;excludekey=</x:text>
          <x:value-of select="$excludekey"/>
        </x:if>
      </x:variable>

      <x:variable name="user" select="/users/user[1]"/>
      <x:variable name="configuration" select="/users/userstore/config/user-fields"/>

      <x:variable name="one-or-more-name-fields-is-configured"
                  select="boolean($configuration/prefix) or boolean($configuration/first-name) or boolean($configuration/middle-name) or boolean($configuration/last-name) or boolean($configuration/suffix) or boolean($configuration/initials)  or boolean($configuration/nickname)"/>

      <x:template match="/">
        <html>
          <script type="text/javascript" src="javascript/admin.js">//</script>
          <script type="text/javascript" src="javascript/validate.js">//</script>
          <script type="text/javascript" src="javascript/tabpane.js">//</script>
          <script type="text/javascript" src="javascript/groups.js">//</script>
          <script type="text/javascript" src="javascript/properties.js">//</script>
          <script type="text/javascript" src="javascript/calendar_picker.js">//</script>
          <script type="text/javascript" src="javascript/lib/jquery/jquery-1.3.2.min.js">//</script>
          <script type="text/javascript" src="javascript/lib/jquery-ui-1.7.2.custom/js/jquery-ui-1.7.2.custom.min.js">//</script>
          <script type="text/javascript" src="javascript/lib/jquery/autocomplete/lib/jquery.bgiframe.min.js">//</script>
          <script type="text/javascript" src="javascript/lib/jquery/autocomplete/jquery.autocomplete.min.js">//</script>
          <script type="text/javascript" src="javascript/lib/jquery/crypt/jquery.crypt.js">//</script>
          <script type="text/javascript" src="javascript/user.js">//</script>

          <script type="text/javascript" src="javascript/cms/core.js">//</script>
          <script type="text/javascript" src="javascript/cms/utils/Event.js">//</script>
          <script type="text/javascript" src="javascript/cms/element/Css.js">//</script>
          <script type="text/javascript" src="javascript/cms/element/Dimensions.js">//</script>
          <script type="text/javascript" src="javascript/cms/ui/SplitButton.js">//</script>
          <script type="text/javascript" src="javascript/cms/ui/UserPreferencesTable.js">//</script>

          <link rel="Stylesheet" type="text/css" href="javascript/lib/jquery-ui-1.7.2.custom/css/cms/jquery-ui-1.7.2.custom.css"/>
          <link rel="stylesheet" type="text/css" href="javascript/lib/jquery/autocomplete/jquery.autocomplete.css"/>

          <link rel="stylesheet" type="text/css" href="css/admin.css"/>
          <link rel="StyleSheet" type="text/css" href="javascript/tab.webfx.css"/>
          <link rel="stylesheet" type="text/css" href="css/calendar_picker.css"/>
          <link rel="stylesheet" type="text/css" href="javascript/cms/ui/style.css"/>

          <script type="text/javascript">
            var g_isRemote = <x:value-of select="$isRemote"/>;
            var g_addressIsRequired = <x:value-of select="boolean($configuration/address/@required = 'true')"/>;
          </script>

          <x:if test="$callback">
            <script type="text/javascript" src="javascript/window.js">//</script>
            <script type="text/javascript">
              cms.window.attatchKeyEvent('close');
            </script>
          </x:if>

          <script type="text/javascript" language="JavaScript">
            // scale array for maximum number of fields
            // var validatedFields = new Array(<xsl:value-of select="count(//attribute)"/>);
            var validatedFields = [];
            var idx = 0;
            var op = 'form';

            function updateNotifyInputField(bNotify)
            {
              document.getElementById('notification').value = bNotify;
            }

            function validateUserGroupAdd(key, type, name, userstorename)
            {
              if (type == 3)
              {
                return "%alertUserGroupCannotBeAdded%";
              }
              return null;
            }

            function validateAll(formName) {

            <x:if test="not($wizard) or $step = 1">
              <x:if test="boolean($configuration/address)">
                orderTabPanels();
              </x:if>
            </x:if>

            var f = document.forms[formName];

            <x:if test="$wizard">
              <x:text>f.action = "adminpage?page=</x:text>
              <x:value-of select="$page"/>
              <x:text>&amp;op="</x:text>+op
              <x:text>+"&amp;wizard=true</x:text>
              <x:text>&amp;prevstep=</x:text>
              <x:value-of select="$step"/>
              <x:text>"</x:text>
              <x:if test="$searchtype">
                <x:text>+"&amp;searchtype=</x:text>
                <x:value-of select="$searchtype"/>
                <x:text>&amp;searchtext=</x:text>
                <x:value-of select="$searchtext"/>
                <x:text>"</x:text>
              </x:if>
              <x:text>;</x:text>
            </x:if>

            if ( !checkAll(formName, validatedFields) )
            {
              return;
            }

            <x:if test="not($wizard) or $step = 1">
              <x:if test="$configuration/address/@required = 'true'">
                if ( !validateAddressTab() )
                {
                  return;
                }
              </x:if>
            </x:if>

            <x:if test="$create = 1 and not($wizard)">
              if (f['password_dummy'].value != f['password2_dummy'].value)
              {
                alert("%errPasswordsDontMatch%");
                return;
              }
            </x:if>

              f.submit();
            }
            // -----------------------------------------------------------------------------------------------------------------------------

            function setReadOnly()
            {
              var form = document.forms['formAdmin'];
              var formElements = form.elements;
              var elementsLn = formElements.length;

              for ( var i = 0; i &lt; elementsLn; i++ )
              {
                // NB: Display name must always be writeable
                if ( formElements[i].id != 'avbryt' &amp;&amp; formElements[i].id != 'display_name' )
                {
                  formElements[i].readOnly = true;
                }
              }
            }
            // -----------------------------------------------------------------------------------------------------------------------------

          </script>

          <body>
            <x:if test="$canUpdateUser = 'false'">
              <x:attribute name="onload">javascript:setReadOnly();</x:attribute>
            </x:if>

            <h1>
              <x:choose>
                <x:when test="$uid = /users/user[1]/block/uid or $domainkey = -1">
                  <x:text>%headUsersYourProfile%</x:text>
                </x:when>
                <x:otherwise>
                  <x:call-template name="displayuserstorepath">
                    <x:with-param name="mode" select="'users'"/>
                    <x:with-param name="userstorekey" select="$userstorekey"/>
                    <x:with-param name="userstorename" select="$userstorename"/>
                    <x:with-param name="disabled" select="not($callback = '')"/>
                  </x:call-template>
                </x:otherwise>
              </x:choose>
              <xsl:text>&nbsp;</xsl:text>
              <span id="titlename">
                <x:if test="$create != 1">
                  <x:choose>
                    <x:when test="$user/block/uid">
                      <x:value-of select="concat('/ ', $user/block/uid)"/>
                    </x:when>
                    <x:otherwise>
                      <x:value-of select="concat('/ ', $user/uid)"/>
                    </x:otherwise>
                  </x:choose>
                </x:if>
              </span>
            </h1>

            <x:choose>
              <x:when test="$wizard">
                <x:call-template name="wizard">
                  <x:with-param name="step" select="$step"/>
                  <x:with-param name="backurl">
                    <x:value-of select="$browsepageURL"/>
                  </x:with-param>
                  <x:with-param name="notification" select="$notification"/>
                  <x:with-param name="from_name" select="$from_name"/>
                  <x:with-param name="from_mail" select="$from_mail"/>
                  <x:with-param name="to_name" select="$to_name"/>
                  <x:with-param name="to_mail" select="$to_mail"/>
                  <x:with-param name="subject" select="$subject"/>
                  <x:with-param name="mail_body" select="$mail_body"/>
                </x:call-template>
              </x:when>
              <x:otherwise>
                <x:call-template name="userform"/>
              </x:otherwise>
            </x:choose>

          </body>
        </html>
      </x:template>

      <x:template name="wizard">
        <x:param name="step"/>
        <x:param name="backurl"/>
        <x:param name="from_name"/>
        <x:param name="from_mail"/>
        <x:param name="to_name"/>
        <x:param name="to_mail"/>
        <x:param name="subject"/>
        <x:param name="mail_body"/>
        <x:param name="notification"/>

        <h2>
          %headUserWizard%
        </h2>

        <form name="formAdmin" method="post" enctype="multipart/form-data">

          <input type="hidden" name="step">
            <x:attribute name="value">
              <x:value-of select="$step"/>
            </x:attribute>
          </input>

          <input type="hidden" name="userstorekey">
            <x:attribute name="value">
              <x:value-of select="$userstorekey"/>
            </x:attribute>
          </input>

          <input type="hidden" name="callback">
            <x:attribute name="value">
              <x:value-of select="$callback"/>
            </x:attribute>
          </input>

          <input type="hidden" name="modeselector">
            <x:attribute name="value">
              <x:value-of select="$modeselector"/>
            </x:attribute>
          </input>

          <input type="hidden" name="mode">
            <x:attribute name="value">
              <x:value-of select="$mode"/>
            </x:attribute>
          </input>

          <input type="hidden" name="userstoreselector">
            <x:attribute name="value">
              <x:value-of select="$userstoreselector"/>
            </x:attribute>
          </input>

          <input type="hidden" name="excludekey">
            <x:attribute name="value">
              <x:value-of select="$excludekey"/>
            </x:attribute>
          </input>

          <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td class="form_title_form_seperator">
                <img src="images/1x1.gif"/>
              </td>
            </tr>
            <tr>
              <td>

                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td colspan="10">
                      <div class="tab-pane" id="tab-pane-1">
                        <script type="text/javascript" language="JavaScript">var tabPane1 = new WebFXTabPane( document.getElementById("tab-pane-1" ), true );
                        </script>

                        <x:choose>
                          <!-- General -->
                          <x:when test="$step = '1'">
                            <div class="tab-page" id="{concat('tab-page-', position())}">
                              <span class="tab">%wizardStepOne%</span>

                              <script type="text/javascript" language="JavaScript">
                                <xsl:text>tabPane1.addTabPage( document.getElementById( "</xsl:text>
                                <xsl:value-of select="concat('tab-page-', position())"/>
                                <xsl:text>") );</xsl:text>
                              </script>

                              <x:call-template name="form-fieldsets"/>

                            </div>
                          </x:when>

                          <!-- Username / password -->
                          <x:when test="$step = 2">
                            <div class="tab-page" id="tab-page-login">
                              <span class="tab">%wizardStepTwo%</span>

                              <script type="text/javascript" language="JavaScript">
                                <xsl:text>tabPane1.addTabPage( document.getElementById("tab-page-login") );</xsl:text>
                              </script>

                              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                  <x:call-template name="textfield">
                                    <x:with-param name="label" select="'%fldUID%:'"/>
                                    <x:with-param name="name" select="'uid_dummy'"/>
                                    <x:with-param name="selectnode" select="/users/user/block/uid"/>
                                    <x:with-param name="readonly" select="false()"/>
                                    <x:with-param name="required" select="'true'"/>
                                    <x:with-param name="disableAutoComplete" select="true()"/>
                                    <x:with-param name="onkeyup">javascript: updateBreadCrumbHeader('titlename', this);</x:with-param>
                                  </x:call-template>
                                </tr>
                                <tr>
                                  <x:call-template name="textfield">
                                    <x:with-param name="label" select="'%fldPassword%:'"/>
                                    <x:with-param name="name" select="'password_dummy'"/>
                                    <x:with-param name="selectnode" select="/users/user/block/password"/>
                                    <x:with-param name="readonly" select="false()"/>
                                    <x:with-param name="required" select="'true'"/>
                                    <x:with-param name="disableAutoComplete" select="true()"/>
                                  </x:call-template>
                                </tr>
                              </table>

                            </div>
                          </x:when>

                          <!-- Group membership -->
                          <x:when test="$step = 3">
                            <x:if test="$create = 0 or $isadmin = 'true'">
                              <x:call-template name="groupmembershipform">
                                <x:with-param name="name" select="'%wizardStepThree%'"/>
                              </x:call-template>
                            </x:if>
                          </x:when>

                          <!-- Mail form -->
                          <x:when test="$step = 4">
                            <x:call-template name="mail-form">
                              <x:with-param name="notification" select="$notification"/>
                              <x:with-param name="from_name" select="$from_name"/>
                              <x:with-param name="from_mail" select="$from_mail"/>
                              <x:with-param name="to_name" select="$to_name"/>
                              <x:with-param name="to_mail" select="$to_mail"/>
                              <x:with-param name="subject" select="$subject"/>
                              <x:with-param name="mail_body" select="$mail_body"/>
                            </x:call-template>
                          </x:when>

                        </x:choose>

                      </div>
                      <script type="text/javascript" language="JavaScript">setupAllTabs();form_setFocus(document.formAdmin);</script>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td class="form_form_buttonrow_seperator">
                <img src="images/1x1.gif"/>
              </td>
            </tr>
            <tr>
              <td>
                <x:if test="$step &gt; '1'">
                  <x:call-template name="button">
                    <x:with-param name="type" select="'button'"/>
                    <x:with-param name="caption" select="'&lt;-- %cmdPrevious%'"/>
                    <x:with-param name="name" select="'lagre'"/>
                    <x:with-param name="onclick">
                      <xsl:text>javascript:document.formAdmin.step.value=</xsl:text>
                      <x:value-of select="$step"/>
                      <xsl:text>-1;validatedFields = new Array(0); validateAll('formAdmin');</xsl:text>
                    </x:with-param>
                  </x:call-template>
                  <xsl:text>&nbsp;</xsl:text>
                </x:if>
                <x:if test="$step &lt; '4'">
                  <x:call-template name="button">
                    <x:with-param name="type" select="'button'"/>
                    <x:with-param name="caption" select="'%cmdNext% --&gt;'"/>
                    <x:with-param name="name" select="'lagre'"/>
                    <x:with-param name="onclick">
                      <xsl:text>javascript:document.formAdmin.step.value=</xsl:text>
                      <x:value-of select="$step"/>
                      <xsl:text>+1;validateAll('formAdmin');</xsl:text>
                    </x:with-param>
                  </x:call-template>
                  <xsl:text>&nbsp;</xsl:text>
                </x:if>
                <x:if test="$step = '4'">
                  <input type="hidden" name="finish" value="true"/>
                  <x:call-template name="button">
                    <x:with-param name="type" select="'button'"/>
                    <x:with-param name="caption" select="'%cmdFinish%'"/>
                    <x:with-param name="name" select="'lagre'"/>
                    <x:with-param name="onclick">
                      <xsl:text>javascript:op='create';if(!document.formAdmin.notification.checked) {validatedFields = new Array(0);}validateAll('formAdmin');</xsl:text>
                    </x:with-param>
                  </x:call-template>
                  <xsl:text>&nbsp;</xsl:text>
                </x:if>

                <x:call-template name="button">
                  <x:with-param name="type" select="'link'"/>
                  <x:with-param name="caption" select="'%cmdCancel%'"/>
                  <x:with-param name="name" select="'avbryt'"/>
                  <x:with-param name="href" select="$backurl"/>
                </x:call-template>
              </td>
            </tr>
          </table>
        </form>

      </x:template>

      <x:template name="userform">

        <form name="formAdmin" method="post" enctype="multipart/form-data">
          <x:attribute name="action">
            <x:if test="$create=1">
              <x:text>adminpage?page=</x:text>
              <x:value-of select="$page"/>
              <x:text>&amp;op=create</x:text>
            </x:if>
            <x:if test="$create=0">
              <x:text>adminpage?page=</x:text>
              <x:value-of select="$page"/>
              <x:text>&amp;op=update</x:text>
            </x:if>

            <x:if test="$callback">
              <x:text>&amp;callback=</x:text>
              <x:value-of select="$callback"/>
            </x:if>
            <x:if test="$modeselector">
              <x:text>&amp;modeselector=</x:text>
              <x:value-of select="$modeselector"/>
            </x:if>
            <x:if test="$userstoreselector">
              <x:text>&amp;userstoreselector=</x:text>
              <x:value-of select="$userstoreselector"/>
            </x:if>
            <x:if test="$excludekey">
              <x:text>&amp;excludekey=</x:text>
              <x:value-of select="$excludekey"/>
            </x:if>
          </x:attribute>

          <input type="hidden" name="notification" id="notification" value="false"/>

          <input type="hidden" name="userstorekey">
            <x:attribute name="value">
              <x:value-of select="$userstorekey"/>
            </x:attribute>
          </input>

          <input type="hidden" name="mode">
            <x:attribute name="value">
              <x:value-of select="$mode"/>
            </x:attribute>
          </input>

          <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td>
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td colspan="10">

                      <div class="tab-pane" id="tab-pane-1">
                        <script type="text/javascript">var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                        </script>

                        <div class="tab-page" id="tab-page-1">
                          <span class="tab">%blockUser%</span>

                          <script type="text/javascript" language="JavaScript">
                            <xsl:text>tabPane1.addTabPage( document.getElementById( "tab-page-1") );</xsl:text>
                          </script>

                          <x:call-template name="form-fieldsets"/>
                        </div>

                        <x:if test="$create = 0">
                          <x:call-template name="properties"/>

                          <x:if test="$user/block">
                            <x:call-template name="groupmembershipform"/>
                          </x:if>
                          <x:if test="$isadmin = 'true'">
                            <x:call-template name="preferences"/>
                          </x:if>
                        </x:if>

                      </div>
                      <script type="text/javascript">setupAllTabs();form_setFocus(document.formAdmin);</script>
                    </td>
                  </tr>

                </table>
              </td>
            </tr>
            <tr>
              <td>
                <br/>
              </td>
            </tr>
            <tr>
              <td>
                <x:if test="$canUpdateUser != 'false'">
                  <x:variable name="savescript">
                    <x:choose>
                      <x:when test="$user/block or $create = 1">
                        <xsl:text>javascript:updateNotifyInputField(false); op='create';validateAll('formAdmin');</xsl:text>
                      </x:when>
                      <x:otherwise>
                        <xsl:text>javascript:updateLanguage();history.back();parent.frames[2].location.href = parent.frames[2].location.href;</xsl:text>
                      </x:otherwise>
                    </x:choose>
                  </x:variable>

                  <x:choose>
                    <x:when test="not($wizard) and not($profile) and not($uid = /users/user[1]/block/uid)">
                      <ul id="save-split-button" title="%cmdSave%" class="cms-split-button">
                        <li>
                          <a>
                            <x:attribute name="href">
                              <x:value-of select="$savescript"/>
                            </x:attribute>
                            <x:text>%cmdSave%</x:text>
                          </a>
                        </li>
                        <li>
                          <a>
                            <x:attribute name="href">
                              <x:choose>
                                <x:when test="$user/block or $create = 1">
                                  <xsl:text>javascript:updateNotifyInputField(true); op = 'create'; validateAll('formAdmin');</xsl:text>
                                </x:when>
                                <x:otherwise>
                                  <xsl:text>javascript:updateLanguage();history.back();parent.frames[2].location.href = parent.frames[2].location.href;</xsl:text>
                                </x:otherwise>
                              </x:choose>
                            </x:attribute>
                            <x:text>%cmdSaveAndNotify%</x:text>
                          </a>
                        </li>
                      </ul>

                      <script type="text/javascript" charset="utf-8">
                        var splitButton = new cms.ui.SplitButton('save-split-button');
                        splitButton.insert();
                      </script>
                    </x:when>
                    <x:otherwise>
                      <x:call-template name="button">
                        <x:with-param name="type" select="'button'"/>
                        <x:with-param name="caption" select="'%cmdSave%'"/>
                        <x:with-param name="name" select="'lagre'"/>
                        <x:with-param name="onclick" select="$savescript"/>
                      </x:call-template>
                    </x:otherwise>
                  </x:choose>

                  <x:text>&nbsp;</x:text>

                </x:if>

                <x:variable name="buttonCaption">
                  <x:choose>
                    <x:when test="$canUpdateUser != 'false'">%cmdCancel%</x:when>
                    <x:otherwise>%cmdClose%</x:otherwise>
                  </x:choose>
                </x:variable>

                <x:call-template name="button">
                  <x:with-param name="type" select="'button'"/>
                  <x:with-param name="caption" select="$buttonCaption"/>
                  <x:with-param name="name" select="'avbryt'"/>
                  <x:with-param name="onclick">
                    <xsl:text>javascript:history.back();</xsl:text>
                  </x:with-param>
                </x:call-template>
              </td>
            </tr>
          </table>
        </form>
      </x:template>

      <x:template name="properties">

        <div class="tab-page" id="tab-page-properties">
          <span class="tab">
            <x:value-of select="'%blockProperties%'"/>
          </span>
          <script type="text/javascript" language="JavaScript">tabPane1.addTabPage( document.getElementById( "tab-page-properties") );
          </script>

          <!--fieldset>
            <legend>&nbsp;%blockRemoteSynchKey%&nbsp;</legend>

            <table width="100%" cellspacing="2" cellpadding="2">
              <tr>
                <x:call-template name="readonlyvalue">
                  <x:with-param name="label" select="'%fldRemoteSynchKey%:'"/>
                  <x:with-param name="selectnode" select="missing/path"/>
                </x:call-template>
              </tr>
            </table>
          </fieldset-->

          <x:if test="$showdn and $user/@dn">
            <fieldset>
              <legend>%fldLDAPInfo%</legend>
              <table width="100%" cellspacing="2" cellpadding="2">
                <tr>
                  <x:call-template name="readonlyvalue">
                    <x:with-param name="name" select="'ldapdn'"/>
                    <x:with-param name="label" select="'%fldDN%:'"/>
                    <x:with-param name="selectnode" select="$user/@dn"/>
                    <x:with-param name="colspan" select="'1'"/>
                  </x:call-template>
                </tr>
              </table>
            </fieldset>
          </x:if>

          <x:if test="$isadmin = 'true' and $create = 0">
            <fieldset>
              <legend>%blockProperties%</legend>
              <table width="100%" cellspacing="2" cellpadding="2">
                <tr>
                  <x:call-template name="readonlyvalue">
                    <x:with-param name="name" select="'_gui_user_key_'"/>
                    <x:with-param name="label" select="'%fldKey%:'"/>
                    <x:with-param name="selectnode" select="/users/user/@key"/>
                    <x:with-param name="colspan" select="'1'"/>
                  </x:call-template>
                </tr>
              </table>
            </fieldset>
          </x:if>

          <fieldset>
            <legend>&nbsp;%blockEventLog%&nbsp;</legend>
            <table width="99%" cellspacing="2" cellpadding="2">
              <x:if test="/users/user/@key">
                <tr>
                  <td>
                    <x:call-template name="button">
                      <x:with-param name="name" select="'vieweventlog'"/>
                      <x:with-param name="type" select="'button'"/>
                      <x:with-param name="caption" select="'%cmdViewEventLog%'"/>
                      <x:with-param name="onclick">
                        <x:text>viewEventLog(-1,</x:text>
                        <x:text>-1,</x:text>
                        <x:text>-1, '</x:text>
                        <x:value-of select="/users/user/@key"/>
                        <x:text>')</x:text>
                      </x:with-param>
                    </x:call-template>
                  </td>
                </tr>
              </x:if>
            </table>
          </fieldset>
        </div>
      </x:template>

      <x:template name="groupmembershipform">
        <x:param name="name" select="'%blockMemberOfGroups%'"/>
        
        <div class="tab-page" id="tab-page-groups">
          <span class="tab">
            <x:value-of select="$name"/>
          </span>
          <script type="text/javascript" language="JavaScript">tabPane1.addTabPage( document.getElementById( "tab-page-groups") );</script>
          <fieldset>
            <legend>
              <x:text>&nbsp;%blockGroups%&nbsp;</x:text>
            </legend>
            <table border="0" cellspacing="2" cellpadding="0" width="50%">
              <x:if test="count(/users/user/memberOf/group[@type != 6]) &gt; 0">
                <x:if test="$isadmin = 'true' and $canUpdateUser = 'true'">
                  <tr>
                    <td>
                      <x:call-template name="button">
                        <x:with-param name="name" select="'butAddAccesRightRow1'"/>
                        <x:with-param name="type" select="'button'"/>
                        <x:with-param name="caption" select="'%cmdAdd%'"/>
                        <x:with-param name="onclick">
                          <x:text>javascript:showUserAndGroupsPopup(</x:text>
                          <x:value-of select="$userstorekey"/>
                          <x:text>, 'groups', false, null, true, true, false);</x:text>
                        </x:with-param>
                      </x:call-template>
                    </td>
                  </tr>
                </x:if>
              </x:if>
              <tr>
                <td>
                  <table border="0" cellspacing="2" cellpadding="0" width="60%">
                    <tbody id="memberstable">
                      <x:for-each select="/users/user/memberOf/group[@type != 6]">
                        <tr>
                          <td nowrap="nowrap">
                            <img src="images/icon_groups.gif" style="vertical-align: middle;"/>
                            <x:text>&nbsp;</x:text>
                            <x:value-of select="concat(name, ' (', qualifiedName, ')')"/>
                            <input type="hidden" name="member">
                              <x:attribute name="value">
                                <x:value-of select="@key"/>
                              </x:attribute>
                            </input>
                          </td>

                          <x:if test="$isadmin = 'true' and $canUpdateUser = 'true'">
                            <td width="20">
                              <x:call-template name="button">
                                <x:with-param name="name">
                                  <x:text>foo[key=</x:text>
                                  <x:value-of select="@key"/>
                                  <x:text>]</x:text>
                                </x:with-param>
                                <x:with-param name="image" select="'images/icon_remove.gif'"/>
                                <x:with-param name="onclick">
                                  <x:text>javascript:handle_groupRemove_onclick( this );</x:text>
                                </x:with-param>
                              </x:call-template>
                            </td>
                          </x:if>
                          <script type="text/javascript" language="JavaScript">
                            <x:text>addChoosen('</x:text>
                            <x:value-of select="@key"/>
                            <x:text>');</x:text>
                          </script>
                        </tr>
                      </x:for-each>
                    </tbody>
                  </table>
                </td>
              </tr>
              <x:if test="$isadmin = 'true' and $canUpdateUser = 'true'">
                <tr>
                  <td>
                    <x:call-template name="button">
                      <x:with-param name="name" select="'butAddAccesRightRow2'"/>
                      <x:with-param name="type" select="'button'"/>
                      <x:with-param name="caption" select="'%cmdAdd%'"/>
                      <x:with-param name="onclick">
                        <x:text>javascript:showUserAndGroupsPopup(</x:text>
                        <x:value-of select="$userstorekey"/>
                        <x:text>, 'groups', false, null, true, false, false);</x:text>
                      </x:with-param>
                    </x:call-template>
                  </td>
                </tr>
              </x:if>
            </table>
          </fieldset>
        </div>
      </x:template>

      <x:template name="preferences">
        <div class="tab-page" id="tab-page-preferences">
          <span class="tab">
            <x:value-of select="'%blockPreferences%'"/>
          </span>
          <script type="text/javascript" language="JavaScript">
            tabPane1.addTabPage( document.getElementById( "tab-page-preferences") );
            tabPane1.enablePageClickEvent();

            function handle_tabpane_onclick( pageIndex )
            {
              UserPreferenceTable.init('<x:value-of select="/users/user/@key"/>');
            }
          </script>
          <fieldset>
            <legend>%fldPreferences%</legend>
            <div id="preferences-table-container">
              <xsl:comment>Populated by UserPreferencesTable.js</xsl:comment>
            </div>
          </fieldset>
        </div>
      </x:template>

      <x:template name="form-fieldsets">
        <x:variable name="show-fieldset-user" select="true()"/>
        <x:variable name="show-fieldset-name" select="true()"/>
        <x:variable name="show-fieldset-photo" select="boolean($configuration/photo)"/>
        <x:variable name="show-fieldset-details"
                    select="boolean($configuration/personal-id) or boolean($configuration/member-id) or boolean($configuration/birthday) or boolean($configuration/gender) or boolean($configuration/title) or boolean($configuration/description) or boolean($configuration/html-email) or boolean($configuration/home-page)"/>
        <x:variable name="show-fieldset-location"
                    select="boolean($configuration/time-zone) or boolean($configuration/locale) or boolean($configuration/country) or boolean($configuration/global-position)"/>
        <x:variable name="show-fieldset-communication"
                    select="boolean($configuration/phone) or boolean($configuration/mobile) or boolean($configuration/fax)"/>
        <x:variable name="show-fieldset-address" select="boolean($configuration/address)"/>

        <x:if test="$show-fieldset-user">
          <x:call-template name="fieldset-user"/>
        </x:if>

        <x:if test="$show-fieldset-name">
          <x:call-template name="fieldset-name"/>
        </x:if>

        <x:if test="$show-fieldset-photo">
          <x:call-template name="fieldset-photo"/>
        </x:if>

        <x:if test="$show-fieldset-details">
          <x:call-template name="fieldset-details"/>
        </x:if>

        <x:if test="$show-fieldset-location">
          <x:call-template name="fieldset-location"/>
        </x:if>

        <x:if test="$show-fieldset-communication">
          <x:call-template name="fieldset-communication"/>
        </x:if>

        <x:if test="$show-fieldset-address">
          <x:call-template name="fieldset-address"/>
        </x:if>
      </x:template>

      <x:template name="fieldset-user">
        <fieldset>
          <legend>%blockUser%</legend>

          <table border="0" cellspacing="2" cellpadding="0" width="100%">

            <x:choose>
              <x:when test="$create = 1">
                <x:if test="not($wizard)">
                  <tr>
                    <x:call-template name="textfield">
                      <x:with-param name="name" select="'uid_dummy'"/>
                      <x:with-param name="label" select="'%fldUID%:'"/>
                      <x:with-param name="readonly" select="false()"/>
                      <x:with-param name="required" select="true()"/>
                      <x:with-param name="useIcon" select="$isRemote"/>
                      <x:with-param name="iconClass" select="'icon-remote'"/>
                      <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                    </x:call-template>
                  </tr>
                  <script type="text/javascript">
                    validatedFields[idx] = new Array("%fldUID%", "uid_dummy", validateRequired);
                    ++idx;
                  </script>

                  <script type="text/javascript">
                    $('input[id=uid_dummy]').bind('keyup', function( event )
                    {
                      updateBreadCrumbHeader('titlename', this);
                      updateDisplayNameValue(event);
                    });
                  </script>

                  <tr>
                    <x:call-template name="passwordfield">
                      <x:with-param name="name" select="'password_dummy'"/>
                      <x:with-param name="label" select="'%fldPassword%:'"/>
                      <x:with-param name="disableAutoComplete" select="true()"/>
                      <x:with-param name="required" select="true()"/>
                      <x:with-param name="useIcon" select="$isRemote"/>
                      <x:with-param name="iconClass" select="'icon-remote'"/>
                      <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                    </x:call-template>

                    <script type="text/javascript">
                      validatedFields[idx] = new Array("%fldPassword%", "password_dummy", validateRequired);
                      ++idx;
                    </script>

                  </tr>
                  <tr>
                    <x:call-template name="passwordfield">
                      <x:with-param name="name" select="'password2_dummy'"/>
                      <x:with-param name="label" select="'%fldRepeatPassword%:'"/>
                      <x:with-param name="disableAutoComplete" select="true()"/>
                      <x:with-param name="required" select="true()"/>
                    </x:call-template>

                    <script type="text/javascript">
                      validatedFields[idx] = new Array("%fldRepeatPassword%", "password2_dummy", validateRequired);
                      ++idx;
                    </script>

                  </tr>
                </x:if>
              </x:when>
              <x:otherwise>
                <tr>
                  <td class="form_labelcolumn" nowrap="true">
                    <x:text>%fldUID%:</x:text>
                  </td>
                  <td nowrap="true" style="height: 20px">
                    <x:value-of select="$user/block/uid"/>
                    <input type="hidden" name="uid_dummy">
                      <x:attribute name="value">
                        <x:value-of select="$user/block/uid"/>
                      </x:attribute>
                    </input>
                  </td>
                </tr>
              </x:otherwise>
            </x:choose>

            <tr>
              <x:call-template name="textfield">
                <x:with-param name="name" select="'email'"/>
                <x:with-param name="label" select="'%fldEmail%:'"/>
                <x:with-param name="selectnode" select="$user/block/email"/>
                <x:with-param name="readonly" select="false()"/>
                <x:with-param name="required" select="true()"/>
                <x:with-param name="useIcon" select="$isRemote"/>
                <x:with-param name="iconClass" select="'icon-remote'"/>
                <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
              </x:call-template>
            </tr>
          </table>

          <script type="text/javascript">
            validatedFields[idx] = new Array("%fldEmail%", "email", validateRequired);
            ++idx;
            validatedFields[idx] = new Array("%fldEmail%", "email", validateEmail);
            ++idx;
          </script>

        </fieldset>
      </x:template>

      <x:template name="fieldset-name">
        <fieldset>
          <legend>%blockName%</legend>

          <x:variable name="value-for-display-name">
            <x:choose>
              <x:when test="$wizard = true() and $step = 1">
                <x:value-of select="$user/block/displayName"/>
              </x:when>
              <x:otherwise>
                <x:value-of select="$user/displayName"/>
              </x:otherwise>
            </x:choose>
          </x:variable>

          <x:variable name="generated-display-name-is-same" select="$generated-display-name = $value-for-display-name"/>
          <x:variable name="editable-display-name" select="( $wizard and not($one-or-more-name-fields-is-configured) ) or ( $create = 0 and $one-or-more-name-fields-is-configured and not($generated-display-name-is-same) ) or ( $create = 0 and not( $one-or-more-name-fields-is-configured ) )"/>

          <table border="0" cellspacing="2" cellpadding="0">
            <x:if test="boolean($configuration/prefix)">
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="name" select="'prefix'"/>
                  <x:with-param name="label" select="'%fldPrefix%:'"/>
                  <x:with-param name="selectnode" select="$user/block/prefix"/>
                  <x:with-param name="readonly" select="$configuration/prefix/@readonly = 'true'"/>
                  <x:with-param name="required" select="$configuration/prefix/@required = 'true'"/>
                  <x:with-param name="useIcon" select="$configuration/prefix/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                </x:call-template>
              </tr>

              <script type="text/javascript">
                $('input[id=prefix]').bind('keyup', function( event )
                {
                  updateDisplayNameValue(event);
                });
              </script>

              <x:if test="$configuration/prefix/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldPrefix%", "prefix", validateRequired);
                  ++idx;
                </script>
              </x:if>

            </x:if>

            <x:if test="boolean($configuration/first-name)">
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="name" select="'first_name'"/>
                  <x:with-param name="label" select="'%fldFirstName%:'"/>
                  <x:with-param name="selectnode" select="$user/block/first-name"/>
                  <x:with-param name="readonly" select="$configuration/first-name/@readonly = 'true'"/>
                  <x:with-param name="required" select="$configuration/first-name/@required = 'true'"/>
                  <x:with-param name="useIcon" select="$configuration/first-name/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                </x:call-template>
              </tr>

              <script type="text/javascript">
                $('input[id=first_name]').bind('keyup', function( event )
                {
                  updateDisplayNameValue(event);
                });
              </script>

              <x:if test="$configuration/first-name/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldFirstName%", "first_name", validateRequired);
                  ++idx;
                </script>
              </x:if>

            </x:if>

            <x:if test="boolean($configuration/middle-name)">
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="name" select="'middle_name'"/>
                  <x:with-param name="label" select="'%fldMiddleName%:'"/>
                  <x:with-param name="selectnode" select="$user/block/middle-name"/>
                  <x:with-param name="readonly" select="$configuration/middle-name/@readonly = 'true'"/>
                  <x:with-param name="required" select="$configuration/middle-name/@required = 'true'"/>
                  <x:with-param name="useIcon" select="$configuration/middle-name/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                </x:call-template>
              </tr>

              <script type="text/javascript">
                $('input[id=middle_name]').bind('keyup', function( event )
                {
                  updateDisplayNameValue(event);
                });
              </script>

              <x:if test="$configuration/middle-name/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldMiddleName%", "middle_name", validateRequired);
                  ++idx;
                </script>
              </x:if>

            </x:if>

            <x:if test="boolean($configuration/last-name)">
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="name" select="'last_name'"/>
                  <x:with-param name="label" select="'%fldLastName%:'"/>
                  <x:with-param name="selectnode" select="$user/block/last-name"/>
                  <x:with-param name="readonly" select="$configuration/last-name/@readonly = 'true'"/>
                  <x:with-param name="required" select="$configuration/last-name/@required = 'true'"/>
                  <x:with-param name="useIcon" select="$configuration/last-name/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                </x:call-template>
              </tr>

              <script type="text/javascript">
                $('input[id=last_name]').bind('keyup', function( event )
                {
                  updateDisplayNameValue(event);
                });
              </script>

              <x:if test="$configuration/last-name/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldLastName%", "last_name", validateRequired);
                  ++idx;
                </script>
              </x:if>

            </x:if>

            <x:if test="boolean($configuration/suffix)">
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="name" select="'suffix'"/>
                  <x:with-param name="label" select="'%fldSuffix%:'"/>
                  <x:with-param name="selectnode" select="$user/block/suffix"/>
                  <x:with-param name="readonly" select="$configuration/suffix/@readonly = 'true'"/>
                  <x:with-param name="required" select="$configuration/suffix/@required = 'true'"/>
                  <x:with-param name="useIcon" select="$configuration/suffix/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                </x:call-template>

                <script type="text/javascript">
                  $('input[id=suffix]').bind('keyup', function( event )
                  {
                    updateDisplayNameValue(event);
                  });
                </script>

                <x:if test="$configuration/suffix/@required = 'true'">
                  <script type="text/javascript">
                    validatedFields[idx] = new Array("%fldSuffix%", "suffix", validateRequired);
                    ++idx;
                  </script>
                </x:if>

              </tr>
            </x:if>

            <x:if test="boolean($configuration/initials)">
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="name" select="'initials'"/>
                  <x:with-param name="label" select="'%fldInitials%:'"/>
                  <x:with-param name="selectnode" select="$user/block/initials"/>
                  <x:with-param name="readonly" select="$configuration/initials/@readonly = 'true'"/>
                  <x:with-param name="required" select="$configuration/initials/@required = 'true'"/>
                  <x:with-param name="useIcon" select="$configuration/initials/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                </x:call-template>
              </tr>

              <script type="text/javascript">
                $('input[id=initials]').bind('keyup', function( event )
                {
                  updateDisplayNameValue(event);
                });
              </script>

              <x:if test="$configuration/initials/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldInitials%", "initials", validateRequired);
                  ++idx;
                </script>
              </x:if>

            </x:if>

            <x:if test="boolean($configuration/nick-name)">
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="name" select="'nick_name'"/>
                  <x:with-param name="label" select="'%fldNickname%:'"/>
                  <x:with-param name="selectnode" select="$user/block/nick-name"/>
                  <x:with-param name="readonly" select="$configuration/nick-name/@readonly = 'true'"/>
                  <x:with-param name="required" select="$configuration/nick-name/@required = 'true'"/>
                  <x:with-param name="useIcon" select="$configuration/nick-name/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                </x:call-template>
              </tr>

              <script type="text/javascript">
                $('input[id=nick_name]').bind('keyup', function( event )
                {
                  updateDisplayNameValue(event);
                });
              </script>

              <x:if test="$configuration/nick-name/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldNickname%", "nick_name", validateRequired);
                  ++idx;
                </script>
              </x:if>
            </x:if>

            <tr>
              <td class="form_labelcolumn" nowrap="true">
                <div style="float:left">
                  %fldDisplayName%:
                  <span class="requiredfield">*</span>
                </div>
                <x:if test="$isRemote">
                  <div style="float:right">
                    <x:call-template name="remote-icon"/>
                  </div>
                </x:if>
              </td>
              <td nowrap="true">

                <input type="text" name="display_name" id="display_name" size="30">
                  <x:if test="not($editable-display-name)">
                    <x:attribute name="readonly">
                      <x:text>true</x:text>
                    </x:attribute>
                  </x:if>
                  <x:attribute name="value">
                    <x:value-of select="$value-for-display-name"/>
                  </x:attribute>
                </input>

                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldDisplayName%", "display_name", validateRequired);
                  ++idx;
                </script>
              </td>
              <td>
                <img width="16" height="16" onclick="admin_lockUnlockTextInput(this,'display_name', 'readonly');" alt="%tooltipUnlockToEditManually%" title="%tooltipUnlockToEditManually%" id="display_name_lock_icon" class="hand" style="vertical-align: bottom;">
                  <x:attribute name="src">
                    <x:choose>
                      <x:when test="$editable-display-name">
                        <x:text>images/icon_lock_open.png</x:text>
                      </x:when>
                      <x:otherwise>
                        <x:text>images/icon_lock_closed.png</x:text>
                      </x:otherwise>
                    </x:choose>
                  </x:attribute>
                </img>
              </td>
            </tr>
          </table>
        </fieldset>
      </x:template>

      <x:template name="fieldset-photo">
        <fieldset>
          <legend>%blockPhoto%</legend>

          <x:if test="boolean($configuration/photo)">
            <table border="0" cellspacing="2" cellpadding="0" width="100%">
              <tr>
                <td class="form_labelcolumn" valign="top" nowrap="true">

                  <div style="float:left">
                    <x:text>%fldPhoto%:</x:text>
                    <x:if test="$configuration/photo/@required = 'true'">
                      <span class="requiredfield">*</span>
                    </x:if>
                  </div>
                  <x:if test="$configuration/photo/@remote = 'true'">
                    <div style="float:right">
                      <x:call-template name="remote-icon"/>
                    </div>
                  </x:if>
                </td>
                <td valign="top" nowrap="true">

                  <x:if test="$user/block/photo/@exists = 'true'">
                    <div id="photo_wrapper">
                      <x:choose>
                        <x:when test="$wizard = true() and $step = 1">
                          %msgPhotoIsChoosen%
                        </x:when>
                        <x:otherwise>
                          <img alt="">
                            <x:attribute name="src">
                              <x:text>_image/user/</x:text>
                              <x:value-of select="/users/user[1]/@key"/>
                              <x:text>?_filter=scalemax(256)</x:text>
                            </x:attribute>
                          </img>
                        </x:otherwise>
                      </x:choose>
                    </div>
                  </x:if>
                  <p>
                    <table border="0" cellpadding="0" cellspacing="0">
                      <tr>
                        <td valign="top">
                          <input type="file" name="photo" onchange="removePhoto(false);">
                            <x:if test="$configuration/photo/@readonly = 'true'">
                              <x:attribute name="readonly">true</x:attribute>
                            </x:if>
                          </input>
                          <input type="hidden" name="remove_photo" id="remove_photo" value="false"/>
                        </td>
                        <td valign="top">
                          <x:call-template name="button">
                            <x:with-param name="image" select="'images/icon_remove.gif'"/>
                            <x:with-param name="name" select="'photo_removebutton'"/>
                            <x:with-param name="disabled" select="$canUpdateUser = 'false' or $configuration/photo/@readonly = 'true'"/>
                            <x:with-param name="onclick">
                              <xsl:text>javascript:removePhoto(true);</xsl:text>
                            </x:with-param>
                          </x:call-template>
                        </td>
                      </tr>
                    </table>
                  </p>

                  <x:if test="$configuration/photo/@required = 'true' and not($user/block/photo/@exists)">
                    <script type="text/javascript">
                      validatedFields[idx] = new Array("%fldPhoto%", "photo", validateRequired);
                      ++idx;
                    </script>
                  </x:if>

                </td>
              </tr>
            </table>
          </x:if>

        </fieldset>
      </x:template>

      <x:template name="fieldset-details">
        <fieldset>
          <legend>%blockPersonalDetails%</legend>
          <table border="0" cellspacing="2" cellpadding="0" width="100%">

            <x:if test="boolean($configuration/personal-id)">
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="name" select="'personal_id'"/>
                  <x:with-param name="label" select="'%fldPersonalId%:'"/>
                  <x:with-param name="selectnode" select="$user/block/personal-id"/>
                  <x:with-param name="readonly" select="$configuration/personal-id/@readonly = 'true'"/>
                  <x:with-param name="required" select="$configuration/personal-id/@required = 'true'"/>
                  <x:with-param name="useIcon" select="$configuration/personal-id/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                </x:call-template>
              </tr>

              <x:if test="$configuration/personal-id/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldPersonalId%", "personal_id", validateRequired);
                  ++idx;
                </script>
              </x:if>
            </x:if>

            <x:if test="boolean($configuration/member-id)">
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="name" select="'member_id'"/>
                  <x:with-param name="label" select="'%fldMemberId%:'"/>
                  <x:with-param name="selectnode" select="$user/block/member-id"/>
                  <x:with-param name="readonly" select="$configuration/member-id/@readonly = 'true'"/>
                  <x:with-param name="required" select="$configuration/member-id/@required = 'true'"/>
                  <x:with-param name="useIcon" select="$configuration/member-id/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                </x:call-template>

                <x:if test="$configuration/member-id/@required = 'true'">
                  <script type="text/javascript">
                    validatedFields[idx] = new Array("%fldMemberId%", "member_id", validateRequired);
                    ++idx;
                  </script>
                </x:if>

              </tr>
            </x:if>

            <x:if test="boolean($configuration/organization)">
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="name" select="'organization'"/>
                  <x:with-param name="label" select="'%fldOrganization%:'"/>
                  <x:with-param name="selectnode" select="$user/block/organization"/>
                  <x:with-param name="readonly" select="$configuration/organization/@readonly = 'true'"/>
                  <x:with-param name="required" select="$configuration/organization/@required = 'true'"/>
                  <x:with-param name="useIcon" select="$configuration/organization/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                </x:call-template>
              </tr>

              <x:if test="$configuration/organization/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldOrganization%", "organization", validateRequired);
                  ++idx;
                </script>
              </x:if>
            </x:if>

            <x:if test="boolean($configuration/birthday)">
              <tr>
                <x:call-template name="textfielddate">
                  <x:with-param name="name" select="'birthday'"/>
                  <x:with-param name="use-date-prefix" select="false()"/>
                  <x:with-param name="label" select="'%fldBirthday%:'"/>
                  <x:with-param name="size" select="'100'"/>
                  <x:with-param name="selectnode" select="$user/block/birthday"/>
                  <x:with-param name="readonly" select="$configuration/birthday/@readonly = 'true'"/>
                  <x:with-param name="required" select="$configuration/birthday/@required = 'true'"/>
                  <x:with-param name="useIcon" select="$configuration/birthday/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                </x:call-template>
              </tr>

              <x:if test="$configuration/birthday/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldBirthday%", "birthday", validateRequired);
                  ++idx;
                </script>
              </x:if>

              <script type="text/javascript">
                validatedFields[idx] = new Array("%fldBirthday%", "birthday", validateDate);
                ++idx;
              </script>
            </x:if>

            <x:if test="boolean($configuration/gender)">
              <tr>

                <td class="form_labelcolumn" nowrap="true" style="height: 20px">

                  <div style="float:left">
                    <x:text>%fldGender%:</x:text>
                    <x:if test="$configuration/gender/@required = 'true'">
                      <span class="requiredfield">*</span>
                    </x:if>
                  </div>
                  <x:if test="$configuration/gender/@remote = 'true'">
                    <div style="float:right">
                      <x:call-template name="remote-icon"/>
                    </div>
                  </x:if>

                </td>
                <td nowrap="true">
                  <input type="radio" name="gender" id="gender_m" value="male">
                    <x:if test="$user/block/gender = 'male'">
                      <x:attribute name="checked">
                        <x:text>true</x:text>
                      </x:attribute>
                    </x:if>
                    <x:if test="$configuration/gender/@readonly = 'true'">
                      <x:attribute name="disabled">
                        <x:text>true</x:text>
                      </x:attribute>
                    </x:if>
                  </input>
                  <label for="gender_m">
                    <x:text>%fldMale%</x:text>
                  </label>
                  <input type="radio" name="gender" id="gender_f" value="female">
                    <x:if test="$user/block/gender = 'female'">
                      <x:attribute name="checked">
                        <x:text>true</x:text>
                      </x:attribute>
                    </x:if>
                    <x:if test="$configuration/gender/@readonly = 'true'">
                      <x:attribute name="disabled">
                        <x:text>true</x:text>
                      </x:attribute>
                    </x:if>
                  </input>
                  <label for="gender_f">
                    <x:text>%fldFemale%</x:text>
                  </label>
                </td>
              </tr>

              <x:if test="$configuration/gender/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldGender%", "gender", validateRadioButtons);
                  ++idx;
                </script>
              </x:if>

            </x:if>

            <x:if test="boolean($configuration/title)">
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="name" select="'title'"/>
                  <x:with-param name="label" select="'%fldTitle%:'"/>
                  <x:with-param name="selectnode" select="$user/block/title"/>
                  <x:with-param name="readonly" select="$configuration/title/@readonly = 'true'"/>
                  <x:with-param name="required" select="$configuration/title/@required = 'true'"/>
                  <x:with-param name="useIcon" select="$configuration/title/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                </x:call-template>
              </tr>

              <x:if test="$configuration/title/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldTitle%", "title", validateRequired);
                  ++idx;
                </script>
              </x:if>
            </x:if>

            <x:if test="boolean($configuration/description)">
              <tr>
                <x:call-template name="textarea">
                  <x:with-param name="name" select="'description'"/>
                  <x:with-param name="label" select="'%fldDescription%:'"/>
                  <x:with-param name="selectnode" select="$user/block/description"/>
                  <x:with-param name="cols" select="'60'"/>
                  <x:with-param name="rows" select="'5'"/>
                  <x:with-param name="readonly" select="$configuration/description/@readonly = 'true'"/>
                  <x:with-param name="required" select="$configuration/description/@required = 'true'"/>
                  <x:with-param name="useIcon" select="$configuration/title/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                </x:call-template>
              </tr>

              <x:if test="$configuration/description/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldDescription%", "description", validateRequired);
                  ++idx;
                </script>
              </x:if>
            </x:if>

            <x:if test="boolean($configuration/html-email)">
              <tr>
                <x:call-template name="checkbox_boolean">
                  <x:with-param name="name" select="'html_email'"/>
                  <x:with-param name="label" select="'%fldHtmlEmail%:'"/>
                  <x:with-param name="selectnode" select="$user/block/html-email"/>
                  <x:with-param name="useIcon" select="$configuration/html-email/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                  <x:with-param name="disabled" select="$configuration/html-email/@readonly = 'true'"/>
                  <!-- We want to disable the checkbox when readonly = true -->
                </x:call-template>
              </tr>
            </x:if>

            <x:if test="boolean($configuration/home-page)">
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="name" select="'home_page'"/>
                  <x:with-param name="label" select="'%fldHomePage%:'"/>
                  <x:with-param name="selectnode" select="$user/block/home-page"/>
                  <x:with-param name="readonly" select="$configuration/home-page/@readonly = 'true'"/>
                  <x:with-param name="required" select="$configuration/home-page/@required = 'true'"/>
                  <x:with-param name="useIcon" select="$configuration/home-page/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                </x:call-template>
              </tr>

              <x:if test="$configuration/home-page/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldHomePage%", "home_page", validateRequired);
                  ++idx;
                </script>
              </x:if>

              <script type="text/javascript">
                validatedFields[idx] = new Array("%fldHomePage%", "home_page", validateURLSimple);
                ++idx;
              </script>
            </x:if>

          </table>
        </fieldset>
      </x:template>

      <x:template name="fieldset-location">
        <fieldset>
          <legend>%blockLocation%</legend>

          <table border="0" cellspacing="2" cellpadding="0" width="100%">

            <x:if test="boolean($configuration/time-zone)">
              <tr>
                <x:choose>
                  <x:when test="true()">
                    <x:call-template name="time-zone-combo">
                      <x:with-param name="selectnode" select="$user/block/time-zone"/>
                      <x:with-param name="required" select="$configuration/time-zone/@required = 'true'"/>
                      <x:with-param name="remoteIcon" select="$configuration/time-zone/@remote = 'true'"/>
                    </x:call-template>
                  </x:when>
                  <x:otherwise>
                    <x:call-template name="textfield">
                      <x:with-param name="name" select="'time_zone'"/>
                      <x:with-param name="label" select="'%fldTimeZone%:'"/>
                      <x:with-param name="selectnode" select="$user/block/time-zone"/>
                      <x:with-param name="readonly" select="true()"/>
                      <x:with-param name="required" select="$configuration/time-zone/@required = 'true'"/>
                      <x:with-param name="useIcon" select="$configuration/time-zone/@remote = 'true'"/>
                      <x:with-param name="iconClass" select="'icon-remote'"/>
                      <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                    </x:call-template>
                  </x:otherwise>
                </x:choose>
              </tr>

              <x:if test="$configuration/time-zone/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldTimeZone%", "time_zone", validateDropdown);
                  ++idx;
                </script>
              </x:if>
            </x:if>

            <x:if test="boolean($configuration/locale)">
              <tr>
                <x:choose>
                  <x:when test="true()">
                    <x:call-template name="locale-combo">
                      <x:with-param name="selectnode" select="$user/block/locale"/>
                      <x:with-param name="required" select="$configuration/locale/@required = 'true'"/>
                      <x:with-param name="remoteIcon" select="$configuration/locale/@remote = 'true'"/>
                    </x:call-template>
                  </x:when>
                  <x:otherwise>
                    <x:call-template name="textfield">
                      <x:with-param name="name" select="'locale'"/>
                      <x:with-param name="label" select="'%fldLocale%:'"/>
                      <x:with-param name="selectnode" select="$user/block/locale"/>
                      <x:with-param name="readonly" select="false()"/>
                      <x:with-param name="required" select="$configuration/locale/@required = 'true'"/>
                      <x:with-param name="useIcon" select="$configuration/locale/@remote = 'true'"/>
                      <x:with-param name="iconClass" select="'icon-remote'"/>
                      <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                    </x:call-template>
                  </x:otherwise>
                </x:choose>
              </tr>

              <x:if test="$configuration/locale/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldLocale%", "locale", validateDropdown);
                  ++idx;
                </script>
              </x:if>
            </x:if>

            <x:if test="boolean($configuration/country)">
              <tr>
                <x:variable name="countryIsReadOnly">
                  <x:value-of select="$configuration/country/@readonly = 'true'"/>
                </x:variable>

                <x:choose>
                  <x:when test="not($configuration/country/@iso) or $configuration/country/@iso = 'true'">
                    <x:call-template name="country-combo">
                      <x:with-param name="name" select="'country'"/>
                      <x:with-param name="label" select="'%fldCountry%:'"/>
                      <x:with-param name="selectnode" select="$user/block/country"/>
                      <x:with-param name="required" select="$configuration/country/@required = 'true'"/>
                      <x:with-param name="readonly" select="$configuration/country/@readonly = 'true'"/>
                      <x:with-param name="remoteIcon" select="$configuration/country/@remote = 'true'"/>
                    </x:call-template>
                  </x:when>
                  <x:otherwise>
                    <x:call-template name="textfield">
                      <x:with-param name="name" select="'country'"/>
                      <x:with-param name="label" select="'%fldCountry%:'"/>
                      <x:with-param name="selectnode" select="$user/block/country"/>
                      <x:with-param name="readonly" select="$configuration/country/@readonly = 'true'"/>
                      <x:with-param name="required" select="$configuration/country/@required = 'true'"/>
                      <x:with-param name="useIcon" select="$configuration/remote/@remote = 'true'"/>
                      <x:with-param name="iconClass" select="'icon-remote'"/>
                      <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                    </x:call-template>
                  </x:otherwise>
                </x:choose>
              </tr>

              <x:if test="$configuration/country/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldCountry%", "country", validateDropdown);
                  ++idx;
                </script>
              </x:if>
            </x:if>

            <x:if test="boolean($configuration/global-position)">
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="name" select="'global_position'"/>
                  <x:with-param name="label" select="'%fldGlobalPosition%:'"/>
                  <x:with-param name="selectnode" select="$user/block/global-position"/>
                  <x:with-param name="readonly" select="$configuration/global-position/@readonly = 'true'"/>
                  <x:with-param name="required" select="$configuration/global-position/@required = 'true'"/>
                  <x:with-param name="useIcon" select="$configuration/global-position/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                </x:call-template>
              </tr>

              <x:if test="$configuration/global-position/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldGlobalPosition%", "global_position", validateRequired);
                  ++idx;
                </script>
              </x:if>
            </x:if>

          </table>
        </fieldset>
      </x:template>

      <x:template name="fieldset-communication">
        <fieldset>
          <legend>%blockCommunication%</legend>

          <script type="text/javascript">
            var g_fieldsThatUseCountryCallingCodes = '';
          </script>

          <table border="0" cellspacing="2" cellpadding="0" width="100%">

            <x:if test="boolean($configuration/phone)">
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="name" select="'phone'"/>
                  <x:with-param name="label" select="'%fldPhone%:'"/>
                  <x:with-param name="selectnode" select="$user/block/phone"/>
                  <x:with-param name="readonly" select="$configuration/phone/@readonly = 'true'"/>
                  <x:with-param name="required" select="$configuration/phone/@required = 'true'"/>
                  <x:with-param name="useIcon" select="$configuration/phone/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                </x:call-template>
              </tr>
              <script type="text/javascript">
                g_fieldsThatUseCountryCallingCodes += 'input#phone';
              </script>

              <x:if test="$configuration/phone/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldPhone%", "phone", validateRequired);
                  ++idx;
                </script>
              </x:if>
            </x:if>

            <x:if test="boolean($configuration/mobile)">
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="name" select="'mobile'"/>
                  <x:with-param name="label" select="'%fldMobile%:'"/>
                  <x:with-param name="selectnode" select="$user/block/mobile"/>
                  <x:with-param name="readonly" select="$configuration/mobile/@readonly = 'true'"/>
                  <x:with-param name="required" select="$configuration/mobile/@required = 'true'"/>
                  <x:with-param name="useIcon" select="$configuration/mobile/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                </x:call-template>
              </tr>
              <script type="text/javascript">
                g_fieldsThatUseCountryCallingCodes += ',input#mobile';
              </script>

              <x:if test="$configuration/mobile/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldMobile%", "mobile", validateRequired);
                  ++idx;
                </script>
              </x:if>
            </x:if>

            <x:if test="boolean($configuration/fax)">
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="name" select="'fax'"/>
                  <x:with-param name="label" select="'%fldFax%:'"/>
                  <x:with-param name="selectnode" select="$user/block/fax"/>
                  <x:with-param name="readonly" select="$configuration/fax/@readonly = 'true'"/>
                  <x:with-param name="required" select="$configuration/fax/@required = 'true'"/>
                  <x:with-param name="useIcon" select="$configuration/fax/@remote = 'true'"/>
                  <x:with-param name="iconClass" select="'icon-remote'"/>
                  <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                </x:call-template>
              </tr>
              <script type="text/javascript">
                g_fieldsThatUseCountryCallingCodes += ',input#fax';
              </script>

              <x:if test="$configuration/fax/@required = 'true'">
                <script type="text/javascript">
                  validatedFields[idx] = new Array("%fldFax%", "fax", validateRequired);
                  ++idx;
                </script>
              </x:if>
            </x:if>
          </table>

          <script type="text/javascript">
            var g_countryCallingCodes = [
            <x:for-each select="/users/countries/country[calling-code != '']">
              <x:text>{ "code": "</x:text>
              <x:value-of select="calling-code"/>
              <x:text>", "country": "</x:text>
              <x:value-of select="english-name"/>
              <x:if test="english-name != local-name">
                <x:value-of select="concat(' (',local-name,')')"/>
              </x:if>
              <x:text>" }</x:text>
              <x:if test="position() != last()">
                <x:text>,
                </x:text>
              </x:if>
            </x:for-each>
            ];
            // -----------------------------------------------------------------------------------------------------------------------------

            $(g_fieldsThatUseCountryCallingCodes).autocomplete(g_countryCallingCodes, {
              minChars: 0,
              width: 310,
              max: 1000,
              formatItem: function( row, i, max )
              {
                return '+' + row.code + ' (' + row.country + ')';
              },

              formatMatch: function( row, i, max )
              {
                return '+' + row.code + function() { };
              },

              formatResult: function( row )
              {
                return '+' + row.code + ' ';
              }
            });
            // -----------------------------------------------------------------------------------------------------------------------------

            function disableAutoComplete()
            {
              $("input#phone").unautocomplete();
            }
          </script>

        </fieldset>
      </x:template>

      <x:template name="fieldset-address">
        <fieldset>
          <legend>%blockAddress%</legend>
          <x:if test="$canUpdateUser = 'true'">
            <p>
              <a href="javascript:;" onclick="openNewAddresTab();" class="text-link-button">%cmdAddAddressTab%</a>
            </p>
          </x:if>

          <x:variable name="use-address-iso" select="not($configuration/address/@iso) or $configuration/address/@iso = 'true'"/>

          <div id="address-tabs">
            <x:choose>
              <x:when test="count($user/block/addresses/address) &gt; 1">

                <ul class="ui-tabs-nav">
                  <x:for-each select="$user/block/addresses/address">
                    <x:call-template name="address-tab-template">
                      <x:with-param name="urlFragment" select="concat('#address-tab-', position())"/>
                      <x:with-param name="addressName" select="label"/>
                      <x:with-param name="primary" select="position() = 1 and $isRemote"/>
                      <x:with-param name="displayCloseIcon" select="true()"/>
                      <x:with-param name="required" select="position() = 1 and $configuration/address/@required = 'true'"/>
                    </x:call-template>
                  </x:for-each>
                </ul>

                <x:for-each select="$user/block/addresses/address">

                  <x:variable name="country">
                    <x:choose>
                      <x:when test="$use-address-iso">
                        <x:value-of select="iso-country"/>
                      </x:when>
                      <x:otherwise>
                        <x:value-of select="country"/>
                      </x:otherwise>
                    </x:choose>
                  </x:variable>

                  <x:variable name="region">
                    <x:choose>
                      <x:when test="$use-address-iso">
                        <x:value-of select="iso-region"/>
                      </x:when>
                      <x:otherwise>
                        <x:value-of select="region"/>
                      </x:otherwise>
                    </x:choose>
                  </x:variable>

                  <x:call-template name="address-tab-panel-template">
                    <x:with-param name="id" select="concat('address-tab-', position())"/>
                    <x:with-param name="addressName" select="label"/>
                    <x:with-param name="street" select="street"/>
                    <x:with-param name="postalCode" select="postal-code"/>
                    <x:with-param name="postalAddress" select="postal-address"/>
                    <x:with-param name="country" select="$country"/>
                    <x:with-param name="region" select="$region"/>
                    <x:with-param name="remoteIcon" select="false()"/>
                  </x:call-template>

                  <x:variable name="update-region" select="$use-address-iso and ( $create = 0 or ( $wizard = true() and $step = 1 ) )"/>

                  <x:if test="$update-region">
                    <script type="text/javascript">
                      <x:text>updateRegion( $('.user-country-combo:eq(</x:text>
                      <x:value-of select="position() - 1"/>
                      <x:text>)')[0] );</x:text>
                    </script>
                  </x:if>

                </x:for-each>
              </x:when>
              <x:otherwise>

                <x:variable name="country">
                  <x:choose>
                    <x:when test="$use-address-iso">
                      <x:value-of select="$user/block/addresses/address/iso-country"/>
                    </x:when>
                    <x:otherwise>
                      <x:value-of select="$user/block/addresses/address/country"/>
                    </x:otherwise>
                  </x:choose>
                </x:variable>

                <x:variable name="region">
                  <x:choose>
                    <x:when test="$use-address-iso">
                      <x:value-of select="$user/block/addresses/address/iso-region"/>
                    </x:when>
                    <x:otherwise>
                      <x:value-of select="$user/block/addresses/address/region"/>
                    </x:otherwise>
                  </x:choose>
                </x:variable>

                <ul class="ui-tabs-nav">
                  <x:call-template name="address-tab-template">
                    <x:with-param name="addressName" select="$user/block/addresses/address/label"/>
                    <x:with-param name="urlFragment" select="'#address-tab-1'"/>
                    <x:with-param name="primary" select="$isRemote"/>
                    <x:with-param name="displayCloseIcon" select="false()"/>
                    <x:with-param name="required" select="$configuration/address/@required = 'true'"/>
                  </x:call-template>
                </ul>
                <x:call-template name="address-tab-panel-template">
                  <x:with-param name="id" select="'address-tab-1'"/>
                  <x:with-param name="addressName" select="$user/block/addresses/address/label"/>
                  <x:with-param name="street" select="$user/block/addresses/address/street"/>
                  <x:with-param name="postalCode" select="$user/block/addresses/address/postal-code"/>
                  <x:with-param name="postalAddress" select="$user/block/addresses/address/postal-address"/>
                  <x:with-param name="country" select="$country"/>
                  <x:with-param name="region" select="$region"/>
                  <x:with-param name="remoteIcon" select="false()"/>
                </x:call-template>

                <x:variable name="update-region" select="$use-address-iso and ( $create = 0 or ( $wizard = true() and $step = 1 ) )"/>

                <x:if test="$update-region">
                  <script type="text/javascript">
                    <x:text>updateRegion( $('.user-country-combo:eq(0)')[0] );</x:text>
                  </script>
                </x:if>

              </x:otherwise>
            </x:choose>

          </div>
        </fieldset>

        <!-- Hidden panel template used by the jQuery Tab JS -->
        <div id="address-tab-panel-template" style="display:none">
          <x:call-template name="address-tab-panel-template">
            <x:with-param name="remoteIcon" select="false()"/>
          </x:call-template>
        </div>
      </x:template>

      <x:template name="address-tab-template">
        <x:param name="urlFragment" select="''"/>
        <x:param name="addressName" select="''"/>
        <x:param name="primary" select="false()"/>
        <x:param name="displayCloseIcon" select="false()"/>
        <x:param name="required" select="false()"/>

        <li dragable="true">
          <a>
            <x:attribute name="href">
              <x:value-of select="$urlFragment"/>
            </x:attribute>
            <span>
              <x:attribute name="class">
                <x:text>icon-remote ui-tabs-address-primary-icon</x:text>
                <x:if test="$primary = false()">
                  <x:text> primary-icon-hidden</x:text>
                </x:if>
              </x:attribute>
              <x:if test="$primary">
                <x:attribute name="title">
                  <x:text>%txtPrimaryAddress%</x:text>
                </x:attribute>
              </x:if>
              <x:comment>Empty</x:comment>
            </span>
            <span class="ui-tabs-address-label">
              <x:choose>
                <x:when test="$addressName != ''">
                  <x:value-of select="$addressName"/>
                </x:when>
                <x:otherwise>
                  <x:text>[%txtNoLabel%]</x:text>
                </x:otherwise>
              </x:choose>
            </span>
            <span>
              <x:attribute name="class">
                <x:choose>
                  <x:when test="$required = true()">
                    <x:text>ui-tabs-address-required</x:text>
                  </x:when>
                  <x:otherwise>
                    <x:text>ui-tabs-address-required ui-tabs-address-required-hidden</x:text>
                  </x:otherwise>
                </x:choose>
              </x:attribute>
              <x:text>*</x:text>
            </span>
            <span>
              <x:attribute name="class">
                <x:text>ui-tabs-address-remove-icon</x:text>
                <x:if test="$canUpdateUser = 'false'">
                  <x:text> cursor-move</x:text>
                </x:if>
              </x:attribute>
              <x:if test="$displayCloseIcon">
                <img src="images/icon_close.gif">
                  <x:choose>
                    <x:when test="$canUpdateUser = 'true'">
                      <x:attribute name="onclick">
                        <x:text>closeAddressTab(this);</x:text>
                      </x:attribute>
                      <x:attribute name="onmouseover">
                        <x:text>iconCloseMouseOverOut(this, 'hover');</x:text>
                      </x:attribute>
                      <x:attribute name="onmouseout">
                        <x:text>iconCloseMouseOverOut(this, 'out');</x:text>
                      </x:attribute>
                      <x:attribute name="alt">
                        <x:text>%cmdRemoveAddress%</x:text>
                      </x:attribute>
                      <x:attribute name="title">
                        <x:text>%cmdRemoveAddress%</x:text>
                      </x:attribute>
                    </x:when>
                    <x:otherwise>
                      <x:attribute name="class">
                        <x:text>disabled-element</x:text>
                      </x:attribute>
                    </x:otherwise>
                  </x:choose>
                </img>
              </x:if>
            </span>
          </a>
        </li>
      </x:template>

      <x:template name="address-tab-panel-template">
        <x:param name="id" select="''"/>
        <x:param name="addressName"/>
        <x:param name="street"/>
        <x:param name="postalCode"/>
        <x:param name="postalAddress"/>
        <x:param name="country"/>
        <x:param name="region"/>
        <x:param name="remoteIcon" select="false()"/>

        <div>
          <x:if test="$id != ''">
            <x:attribute name="id">
              <x:value-of select="$id"/>
            </x:attribute>
          </x:if>

          <table border="0" cellspacing="2" cellpadding="0" width="100%">

            <tr>
              <x:call-template name="textfield">
                <x:with-param name="name" select="'address[].label'"/>
                <x:with-param name="label" select="'%fldAddressLabel%:'"/>
                <x:with-param name="selectnode" select="$addressName"/>
                <x:with-param name="onkeyup" select="'updateAddressTabLabel(this);'"/>
                <x:with-param name="readonly" select="false()"/>
                <x:with-param name="useIcon" select="$remoteIcon"/>
                <x:with-param name="iconClass" select="'icon-remote'"/>
                <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
              </x:call-template>
            </tr>

            <tr>
              <x:call-template name="textfield">
                <x:with-param name="name" select="'address[].street'"/>
                <x:with-param name="label" select="'%fldAddressStreet%:'"/>
                <x:with-param name="selectnode" select="$street"/>
                <x:with-param name="readonly" select="false()"/>
                <x:with-param name="useIcon" select="$remoteIcon"/>
                <x:with-param name="iconClass" select="'icon-remote'"/>
                <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
              </x:call-template>
            </tr>

            <tr>
              <x:call-template name="textfield">
                <x:with-param name="name" select="'address[].postal_code'"/>
                <x:with-param name="label" select="'%fldAddressPostalCode%:'"/>
                <x:with-param name="selectnode" select="$postalCode"/>
                <x:with-param name="readonly" select="false()"/>
                <x:with-param name="useIcon" select="$remoteIcon"/>
                <x:with-param name="iconClass" select="'icon-remote'"/>
                <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
              </x:call-template>
            </tr>

            <tr>
              <x:call-template name="textfield">
                <x:with-param name="name" select="'address[].postal_address'"/>
                <x:with-param name="label" select="'%fldAddressPostalAddress%:'"/>
                <x:with-param name="selectnode" select="$postalAddress"/>
                <x:with-param name="readonly" select="false()"/>
                <x:with-param name="useIcon" select="$remoteIcon"/>
                <x:with-param name="iconClass" select="'icon-remote'"/>
                <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
              </x:call-template>
            </tr>

            <x:choose>
              <x:when test="not($configuration/address/@iso) or $configuration/address/@iso = 'true'">
                <tr>
                  <x:call-template name="country-combo">
                    <x:with-param name="name" select="'address[].iso_country'"/>
                    <x:with-param name="label" select="'%fldAddressCountry%:'"/>
                    <x:with-param name="class" select="'user-country-combo'"/>
                    <x:with-param name="selectnode" select="$country"/>
                    <x:with-param name="on-change-callback">
                      <x:text>javascript:updateRegion(this);</x:text>
                    </x:with-param>
                    <x:with-param name="remoteIcon" select="$remoteIcon"/>
                  </x:call-template>
                </tr>
                <tr>
                  <td class="form_labelcolumn" nowrap="true">
                    <div style="float: left">
                      <x:text>%fldAddressRegion%:</x:text>
                    </div>

                    <x:if test="$remoteIcon">
                      <div style="float: right">
                        <x:call-template name="remote-icon"/>
                      </div>
                    </x:if>

                  </td>
                  <td nowrap="true">
                    <select>
                      <x:attribute name="class">
                        <x:text>user-region-combo</x:text>
                      </x:attribute>
                      <x:attribute name="id">
                        <x:text>address[].iso_region</x:text>
                      </x:attribute>
                      <x:attribute name="name">
                        <x:text>address[].iso_region</x:text>
                      </x:attribute>
                      <option value="">
                        <x:if test="$create = 1">
                          <x:attribute name="selected">true</x:attribute>
                        </x:if>
                        <x:text>-- %txtSelect% --</x:text>
                      </option>
                    </select>

                    <input type="hidden" name="temp_region" class="temp-region">
                      <x:attribute name="value">
                        <x:value-of select="$region"/>
                      </x:attribute>
                    </input>

                    <span style="display:none">
                      <x:attribute name="id">
                        <x:value-of select="concat('', '-loader')"/>
                      </x:attribute>
                      %msgLoading% ...
                    </span>

                  </td>
                </tr>
              </x:when>
              <x:otherwise>
                <tr>
                  <x:call-template name="textfield">
                    <x:with-param name="name" select="'address[].country'"/>
                    <x:with-param name="label" select="'%fldAddressCountry%:'"/>
                    <x:with-param name="selectnode" select="$country"/>
                    <x:with-param name="readonly" select="false()"/>
                    <x:with-param name="useIcon" select="$remoteIcon"/>
                    <x:with-param name="iconClass" select="'icon-remote'"/>
                    <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                  </x:call-template>
                </tr>
                <tr>
                  <x:call-template name="textfield">
                    <x:with-param name="name" select="'address[].region'"/>
                    <x:with-param name="label" select="'%fldAddressRegion%:'"/>
                    <x:with-param name="selectnode" select="$region"/>
                    <x:with-param name="readonly" select="false()"/>
                    <x:with-param name="useIcon" select="$remoteIcon"/>
                    <x:with-param name="iconClass" select="'icon-remote'"/>
                    <x:with-param name="iconText" select="'%hlpIsRemote%'"/>
                  </x:call-template>
                </tr>
              </x:otherwise>
            </x:choose>

          </table>

        </div>

      </x:template>

      <x:template name="mail-form">
        <x:param name="notification" select="'true'"/>
        <x:param name="from_name"/>
        <x:param name="from_mail"/>
        <x:param name="to_name"/>
        <x:param name="to_mail"/>
        <x:param name="subject"/>
        <x:param name="mail_body"/>

        <div class="tab-page" id="tab-page-mail-form">
          <span class="tab">%wizardStepFour%</span>

          <script type="text/javascript" language="JavaScript">
            tabPane1.addTabPage( document.getElementById( "tab-page-mail-form" ) );

            function notify_checkbox( checked )
            {
              var f = document.formAdmin;
              var disabledColor = "#CCCCCC";
              var enabledColor = "#FFFFFF";

              if ( checked )
              {
                f.from_name.disabled = false;
                f.from_mail.disabled = false;
                f.to_name.disabled = false;
                f.to_mail.disabled = false;
                f.subject.disabled = false;
                f.mail_body.disabled = false;
              }
              else
              {
                f.from_name.disabled = true;
                f.from_mail.disabled = true;
                f.to_name.disabled = true;
                f.to_mail.disabled = true;
                f.subject.disabled = true;
                f.mail_body.disabled = true;
              }
            }
          </script>

          <fieldset>
            <legend>
              &nbsp;%blockUserNotification%&nbsp;
            </legend>
            <table cellspacing="0" cellpadding="2" border="0">
              <tr>
                <x:call-template name="checkbox_boolean">
                  <x:with-param name="label" select="'%fldSendNotification%:'"/>
                  <x:with-param name="name" select="'notification'"/>
                  <x:with-param name="selectnode" select="$notification"/>
                  <x:with-param name="onclick">
                    <x:text>javascript:notify_checkbox(document.formAdmin.notification.checked);</x:text>
                  </x:with-param>
                </x:call-template>
              </tr>
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="label" select="'%fldFromName%:'"/>
                  <x:with-param name="name" select="'from_name'"/>
                  <x:with-param name="selectnode" select="$from_name"/>
                </x:call-template>
              </tr>
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="label" select="'%fldFromMail%:'"/>
                  <x:with-param name="name" select="'from_mail'"/>
                  <x:with-param name="selectnode" select="$from_mail"/>
                </x:call-template>
              </tr>
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="label" select="'%fldToName%:'"/>
                  <x:with-param name="name" select="'to_name'"/>
                  <x:with-param name="selectnode" select="$to_name"/>
                </x:call-template>
              </tr>
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="label" select="'%fldToMail%:'"/>
                  <x:with-param name="name" select="'to_mail'"/>
                  <x:with-param name="selectnode" select="$to_mail"/>
                  <x:with-param name="required" select="'true'"/>
                </x:call-template>
              </tr>
              <tr>
                <x:call-template name="textfield">
                  <x:with-param name="label" select="'%fldSubject%:'"/>
                  <x:with-param name="name" select="'subject'"/>
                  <x:with-param name="size" select="30"/>
                  <x:with-param name="selectnode" select="$subject"/>
                </x:call-template>
              </tr>
              <tr>
                <x:call-template name="textarea">
                  <x:with-param name="label" select="'%fldMailBody%:'"/>
                  <x:with-param name="name" select="'mail_body'"/>
                  <x:with-param name="cols" select="60"/>
                  <x:with-param name="rows" select="7"/>
                  <x:with-param name="selectnode" select="$mail_body"/>
                  <x:with-param name="required" select="'true'"/>
                </x:call-template>
              </tr>
              <script type="text/javascript" language="JavaScript">
                <xsl:text>validatedFields[idx] = new Array("%fldToMail%", "</xsl:text>
                <xsl:value-of select="'to_mail'"/>
                <xsl:text>", validateRequired);</xsl:text>
                ++idx;
                <xsl:text>validatedFields[idx] = new Array("%fldToMail%", "</xsl:text>
                <xsl:value-of select="'to_mail'"/>
                <xsl:text>", validateEmail);</xsl:text>
                ++idx;
                <xsl:text>validatedFields[idx] = new Array("%fldMailBody%", "</xsl:text>
                <xsl:value-of select="'mail_body'"/>
                <xsl:text>", validateRequired);</xsl:text>
                ++idx;

                notify_checkbox(document.formAdmin.notification.checked);
              </script>
            </table>
          </fieldset>
        </div>
      </x:template>

      <x:template name="country-combo">
        <x:param name="name" select="''"/>
        <x:param name="label" select="''"/>
        <x:param name="class" select="''"/>
        <x:param name="selectnode"/>
        <x:param name="on-change-callback"/>
        <x:param name="required" select="false()"/>
        <x:param name="readonly" select="false()"/>
        <x:param name="remoteIcon" select="false()"/>
        <x:choose>
          <x:when test="$readonly">
            <x:call-template name="textfield">
              <x:with-param name="label" select="$label"/>
              <x:with-param name="name" select="$name"/>
              <x:with-param name="size"/>
              <x:with-param name="selectnode" select="$selectnode"/>
              <x:with-param name="required" select="$required"/>
              <x:with-param name="readonly" select="$readonly"/>
            </x:call-template>
          </x:when>
          <x:otherwise>
            <td class="form_labelcolumn" nowrap="true">
              <div style="float: left">
                <x:value-of select="$label"/>
                <x:if test="$required">
                  <span class="requiredfield">*</span>
                </x:if>
              </div>

              <x:if test="$remoteIcon = 'true'">
                <div style="float: right">
                  <x:call-template name="remote-icon"/>
                </div>
              </x:if>

            </td>
            <td nowrap="true">
              <select>
                <x:if test="$class != ''">
                  <x:attribute name="class">
                    <x:value-of select="$class"/>
                  </x:attribute>
                </x:if>
                <x:attribute name="name">
                  <x:value-of select="$name"/>
                </x:attribute>
                <x:if test="$on-change-callback != ''">
                  <x:attribute name="onchange">
                    <x:value-of select="$on-change-callback"/>
                  </x:attribute>
                </x:if>
                <option value="">
                  <x:if test="$selectnode = ''">
                    <x:attribute name="selected">true</x:attribute>
                  </x:if>
                  <x:text>-- %txtSelect% --</x:text>
                </option>
                <x:for-each select="/users/countries/country">
                  <option>
                    <x:attribute name="value">
                      <x:value-of select="@code"/>
                    </x:attribute>
                    <x:if test="$selectnode = @code">
                      <x:attribute name="selected">true</x:attribute>
                    </x:if>
                    <x:variable name="country-name">
                      <x:choose>
                        <x:when test="local-name != english-name">
                          <x:value-of select="concat(english-name, ' (', local-name , ')')"/>
                        </x:when>
                        <x:otherwise>
                          <x:value-of select="english-name"/>
                        </x:otherwise>
                      </x:choose>
                    </x:variable>
                    <x:value-of select="$country-name"/>
                  </option>
                </x:for-each>
              </select>
            </td>
          </x:otherwise>
        </x:choose>
      </x:template>

      <x:template name="time-zone-combo">
        <x:param name="selectnode"/>
        <x:param name="required" select="false()"/>
        <x:param name="remoteIcon" select="false()"/>

        <td class="form_labelcolumn" nowrap="true">
          <div style="float: left">
            <xsl:text>%fldTimeZone%:</xsl:text>
            <x:if test="$required">
              <span class="requiredfield">*</span>
            </x:if>
          </div>
          <x:if test="$remoteIcon != ''">
            <div style="float: right">
              <x:call-template name="remote-icon"/>
            </div>
          </x:if>
        </td>
        <td nowrap="true">
          <select name="time_zone" id="time_zone">
            <option value="">
              <x:if test="$create = 1">
                <x:attribute name="selected">true</x:attribute>
              </x:if>
              <x:text>-- %txtSelect% --</x:text>
            </option>
            <x:for-each select="/users/time-zones/time-zone">
              <x:variable name="value" select="@ID"/>
              <x:variable name="display" select="concat( display-name, ' (', hours-from-utc-as-human-readable, ')' ) "/>
              <option>
                <x:attribute name="value">
                  <x:value-of select="$value"/>
                </x:attribute>
                <x:if test="$user/block/time-zone = $value">
                  <x:attribute name="selected">true</x:attribute>
                </x:if>
                <x:value-of select="$display"/>
              </option>
            </x:for-each>
          </select>

        </td>
      </x:template>

      <x:template name="locale-combo">
        <x:param name="selectnode"/>
        <x:param name="required" select="false()"/>
        <x:param name="remoteIcon" select="false()"/>
        <td class="form_labelcolumn" valign="top" nowrap="true">
          <div style="float: left">
            <x:text>%fldLocale%:</x:text>
            <x:if test="$required">
              <span class="requiredfield">*</span>
            </x:if>
          </div>
          <x:if test="$remoteIcon != ''">
            <div style="float: right">
              <x:call-template name="remote-icon"/>
            </div>
          </x:if>
        </td>
        <td nowrap="true">
          <select name="locale" id="locale">
            <option value="">
              <x:if test="$create = 1">
                <x:attribute name="selected">true</x:attribute>
              </x:if>
              <x:text>-- %txtSelect% --</x:text>
            </option>
            <x:for-each select="/users/locales/locale">
              <x:variable name="name" select="name"/>
              <x:variable name="display-name" select="display-name"/>
              <option>
                <x:attribute name="value">
                  <x:value-of select="$name"/>
                </x:attribute>
                <x:if test="$selectnode = $name">
                  <x:attribute name="selected">true</x:attribute>
                </x:if>
                <x:value-of select="$display-name"/>
              </option>
            </x:for-each>
          </select>
        </td>
      </x:template>

      <x:template name="dropdown-language">
        <x:param name="label"/>
        <x:param name="name"/>
        <x:param name="selectedkey"/>
        <x:param name="defaultkey"/>
        <x:param name="selectnode"/>
        <x:param name="colspan"/>

        <td class="form_labelcolumn">
          <x:value-of select="$label"/>
        </td>
        <td>
          <select>
            <x:attribute name="name">
              <x:value-of select="$name"/>
            </x:attribute>
            <x:attribute name="id">
              <x:value-of select="$name"/>
            </x:attribute>

            <x:for-each select="$selectnode">
              <option>
                <x:if test="$selectedkey = @code">
                  <x:attribute name="selected">selected</x:attribute>
                </x:if>
                <x:attribute name="value">
                  <x:value-of select="@code"/>
                </x:attribute>
                <x:value-of select="@description"/>
              </option>
            </x:for-each>
          </select>
        </td>
      </x:template>

      <x:template name="remote-icon">
        <span class="icon-remote" title="%hlpIsRemote%">
          <x:comment>Empty</x:comment>
        </span>
      </x:template>

    </x:stylesheet>

  </xsl:template>

</xsl:stylesheet>