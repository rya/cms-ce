<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp   "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:x="mailto:glu@enonic.com?subject=foobar">

  <xsl:namespace-alias stylesheet-prefix="x" result-prefix="xsl"/>

  <xsl:output method="xml"/>

  <xsl:include href="common/button.xsl"/>

  <xsl:param name="xsl_prefix"/>
  <xsl:param name="wizard_stepid"/>

  <xsl:template match="/">
    <x:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

      <x:output method="html"/>

      <x:include>
          <xsl:attribute name="href">
              <xsl:value-of select="concat($xsl_prefix, 'common/generic_parameters.xsl')"/>
          </xsl:attribute>
      </x:include>

      <xsl:for-each select="/wizardconfig/displayconfig/includes/include">
          <x:include>
              <xsl:attribute name="href">
                  <xsl:value-of select="concat($xsl_prefix, @href)"/>
              </xsl:attribute>
          </x:include>
      </xsl:for-each>

      <xsl:variable name="wizard_js" select="/wizardconfig/steps/step[@id=$wizard_stepid]/form/javascript/@src"/>

      <x:template match="/">
        <html>
          <head>
            <xsl:apply-templates select="/wizardconfig/steps/step[@id=$wizard_stepid]/head/call-template"/>

            <link rel="stylesheet" type="text/css" href="css/admin.css"/>
            <link rel="stylesheet" type="text/css" href="css/calendar_picker.css"/>

            <xsl:for-each select="/wizardconfig/steps/step[@id=$wizard_stepid]/form/css/include">
                <link type="text/css" rel="StyleSheet">
                    <xsl:attribute name="href">
                        <xsl:value-of select="@src"/>
                    </xsl:attribute>
                </link>
            </xsl:for-each>

            <script type="text/javascript" language="JavaScript" src="javascript/admin.js"/>
            <script type="text/javascript" language="JavaScript" src="javascript/validate.js"/>
            <script type="text/javascript" language="JavaScript" src="javascript/wizard.js"/>

            <xsl:for-each select="/wizardconfig/steps/step[@id=$wizard_stepid]/form/javascript/include">
                <script type="text/javascript" language="JavaScript">
                    <xsl:attribute name="src">
                        <xsl:text>javascript/</xsl:text>
                        <xsl:value-of select="@src"/>
                    </xsl:attribute>
                </script>
            </xsl:for-each>

            <!-- button events -->
            <script type="text/javascript" language="JavaScript">
                function onPrevious(button)
                {
                    <xsl:for-each select="/wizardconfig/steps/step[@id=$wizard_stepid]/form/javascript/events/event[@name = 'onPrevious']">
                        <xsl:value-of select="@function"/>
                        <xsl:text>;
                    </xsl:text>
                    </xsl:for-each>
                }

                function onNext(button)
                {
                    <xsl:for-each select="/wizardconfig/steps/step[@id=$wizard_stepid]/form/javascript/events/event[@name = 'onNext']">
                        <xsl:if test="@function">
                            if ( <xsl:value-of select="@function"/> == false )
                              return false;
                        </xsl:if>
                    </xsl:for-each>

                    <!-- Generic javascript is best placed after boolean javascript -->
                    <xsl:for-each select="/wizardconfig/steps/step[@id=$wizard_stepid]/form/javascript/events/event[@name = 'onNext']">
                        <xsl:if test=".">
                            <xsl:value-of select="."/>
                        </xsl:if>
                    </xsl:for-each>

                    var configTextArea = document.getElementById("config");
                    if ( configTextArea &amp;&amp; codeArea_config )
                    {
                      configTextArea.value = codeArea_config.getCode();
                    }

                    return true;
                }

                function onProcess(button)
                {
                    <xsl:for-each select="/wizardconfig/steps/step[@id=$wizard_stepid]/form/javascript/events/event[@name = 'onProcess']">
                        <xsl:if test="@function">
                            if ( <xsl:value-of select="@function"/> == false )
                              return false;
                        </xsl:if>
                    </xsl:for-each>

                    <!-- Generic javascript is best placed after boolean javascript -->
                    <xsl:for-each select="/wizardconfig/steps/step[@id=$wizard_stepid]/form/javascript/events/event[@name = 'onProcess']">
                        <xsl:if test=".">
                            <xsl:value-of select="."/>
                        </xsl:if>
                    </xsl:for-each>

                    return true;
                }

                function onCancel(button)
                {
                    <xsl:for-each select="/wizardconfig/steps/step[@id=$wizard_stepid]/form/javascript/events/event[@name = 'onCancel']">
                        <xsl:value-of select="@function"/>
                        <xsl:text>;
                    </xsl:text>
                    </xsl:for-each>
                }

                function onClose(button)
                {
                    <xsl:for-each select="/wizardconfig/steps/step[@id=$wizard_stepid]/form/javascript/events/event[@name = 'onClose']">
                        <xsl:if test="@function">
                            if ( <xsl:value-of select="@function"/> == false )
                                return false;
                        </xsl:if>
                    </xsl:for-each>

                    <!-- Generic javascript is best placed after boolean javascript -->
                    <xsl:for-each select="/wizardconfig/steps/step[@id=$wizard_stepid]/form/javascript/events/event[@name = 'onClose']">
                        <xsl:if test=".">
                            <xsl:value-of select="."/>
                        </xsl:if>
                    </xsl:for-each>

                    return true;
                }
            </script>
            <title>Wizard form</title>
          </head>

          <body>
              <xsl:if test="/wizardconfig/steps/step[@id = $wizard_stepid]/form/javascript/events/event[@name = 'onLoad']">
                  <xsl:attribute name="onload">
                      <xsl:value-of select="/wizardconfig/steps/step[@id = $wizard_stepid]/form/javascript/events/event[@name = 'onLoad']/@function"/>
                  </xsl:attribute>
              </xsl:if>

              <h1>
                <xsl:apply-templates select="/wizardconfig/displayconfig/header/call-template"/>
              </h1>

              <form id="formAdmin" name="formAdmin" method="post">
                <xsl:if test="/wizardconfig/steps/step[@id = $wizard_stepid]/form/@enctype">
                  <xsl:attribute name="enctype">
                    <xsl:value-of select="/wizardconfig/steps/step[@id = $wizard_stepid]/form/@enctype"/>
                  </xsl:attribute>
                </xsl:if>

                  <x:call-template name="title"/>

                  <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td>
                        <x:call-template name="buttons"/>
                      </td>
                    </tr>
                    <tr><td class="form_title_form_seperator"><br/></td></tr>
                      <tr>
                          <td>
                              <xsl:apply-templates select="/wizardconfig/steps/step[@id=$wizard_stepid]/form/call-template"/>
                          </td>
                      </tr>
                      <tr><td><br/></td></tr>
                      <tr>
                        <td>
                          <x:call-template name="buttons"/>
                        </td>
                      </tr>
                  </table>
              </form>

              <form id="formCancel" name="formCancel" method="post" target="mainFrame">
                  <input type="hidden" name="__wizard_button"/>
              </form>
          </body>
        </html>
      </x:template>

      <x:template name="buttons">

              <input type="hidden" name="__wizard_button" id="__wizard_button"/>

              <xsl:for-each select="/wizardconfig/steps/step[@id = $wizard_stepid]/buttons/button">

                  <!--xsl:comment>
                      <xsl:text>@type = </xsl:text>
                      <xsl:value-of select="@type"/>
                      <xsl:text>; @caption = </xsl:text>
                      <xsl:value-of select="@caption"/>
                      <xsl:text>; @visible = </xsl:text>
                      <xsl:value-of select="@visible"/>
                      <xsl:text>; @disabled = </xsl:text>
                      <xsl:value-of select="@disabled"/>
                  </xsl:comment-->

                  <x:call-template name="button">
                      <x:with-param name="type">
                        <xsl:attribute name="select">
                          <xsl:text>'</xsl:text>
                          <xsl:choose>
                            <xsl:when test="@type = 'reset'">
                              <xsl:text>reset</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                              <xsl:text>button</xsl:text>
                            </xsl:otherwise>
                          </xsl:choose>
                          <xsl:text>'</xsl:text>
                        </xsl:attribute>
                      </x:with-param>
                      <x:with-param name="caption" select="'{@caption}'"/>
                      <x:with-param name="name">
                          <xsl:attribute name="select">
                              <xsl:text>'</xsl:text>
                              <xsl:choose>
                                  <xsl:when test="@name">
                                      <xsl:value-of select="@name"/>
                                  </xsl:when>
                                  <xsl:when test="@type = 'previous'">
                                      <xsl:text>previous</xsl:text>
                                  </xsl:when>
                                  <xsl:when test="@type = 'cancel'">
                                      <xsl:text>cancel</xsl:text>
                                  </xsl:when>
                                  <xsl:when test="@type = 'close'">
                                      <xsl:text>close</xsl:text>
                                  </xsl:when>
                                  <xsl:when test="@type = 'reset'">
                                      <xsl:text>reset</xsl:text>
                                  </xsl:when>
                                  <xsl:otherwise>
                                      <xsl:text>next</xsl:text>
                                  </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>'</xsl:text>
                          </xsl:attribute>
                      </x:with-param>

                      <xsl:choose>
                          <xsl:when test="@visible = 'false'">
                              <x:with-param name="hidden" select="'true'"/>
                          </xsl:when>
                          <xsl:when test="@disabled">
                              <x:with-param name="disabled">
                                  <xsl:attribute name="select">
                                      <xsl:choose>
                                          <xsl:when test="substring(@disabled, 1, 1) = '$'">
                                              <xsl:value-of select="@disabled"/>
                                          </xsl:when>
                                          <xsl:otherwise>
                                              <xsl:text>'</xsl:text>
                                              <xsl:value-of select="@disabled"/>
                                              <xsl:text>'</xsl:text>
                                          </xsl:otherwise>
                                      </xsl:choose>
                                  </xsl:attribute>
                              </x:with-param>
                          </xsl:when>
                      </xsl:choose>

                      <xsl:if test="not(@type = 'reset')">
                        <x:with-param name="onclick">
                          <xsl:attribute name="select">
                              <xsl:text>'</xsl:text>
                              <xsl:choose>
                                  <xsl:when test="@type = 'previous'">
                                      <xsl:text>onPrevious(this); buttonClick(this);</xsl:text>
                                  </xsl:when>
                                  <xsl:when test="@type = 'cancel'">
                                      <xsl:text>onCancel(this); cancelClick(this);</xsl:text>
                                  </xsl:when>
                                  <xsl:when test="@type = 'close'">
                                      <xsl:text>if ( onClose(this) ) closeClick(this);</xsl:text>
                                  </xsl:when>
                                  <xsl:when test="@type = 'process'">
                                      <xsl:text>if ( onProcess(this) ) buttonClick(this);</xsl:text>
                                  </xsl:when>
                                  <!--xsl:when test="@type = 'reset'">
                                    <xsl:text>resetForm()</xsl:text>
                                  </xsl:when-->
                                  <xsl:otherwise>
                                      <xsl:text>if ( onNext(this) ) buttonClick(this);</xsl:text>
                                  </xsl:otherwise>
                              </xsl:choose>
                              <xsl:text>'</xsl:text>
                          </xsl:attribute>
                        </x:with-param>
                      </xsl:if>
                  </x:call-template>

                  <xsl:if test="position() != last() and not( @visible = 'false' )">
                      <xsl:text>&nbsp;</xsl:text>
                  </xsl:if>
              </xsl:for-each>

              <xsl:if test="not( /wizardconfig/steps/step[@id = $wizard_stepid]/buttons/button[@type = 'cancel'] ) and not( /wizardconfig/steps/step[@id = $wizard_stepid]/buttons/button[@type = 'close'] )">
                  <xsl:text>&nbsp;</xsl:text>
                  <x:call-template name="button">
                      <x:with-param name="type" select="'button'"/>
                      <x:with-param name="caption" select="'%cmdCancel%'"/>
                      <x:with-param name="name" select="'cancel'"/>
                      <x:with-param name="onclick" select="'onCancel(this); cancelClick(this);'"/>
                  </x:call-template>
              </xsl:if>
      </x:template>

      <x:template name="title">
        <!--h2>
          <xsl:apply-templates select="/wizardconfig/displayconfig/title/main/*"/>
          <xsl:apply-templates select="/wizardconfig/steps/step[@id=$wizard_stepid]/title/main/*"/>
        </h2>
        <xsl:if test="/wizardconfig/steps/step[@id=$wizard_stepid]/title/sub">
          <xsl:apply-templates select="/wizardconfig/displayconfig/title/sub/*"/>
          <xsl:apply-templates select="/wizardconfig/steps/step[@id=$wizard_stepid]/title/sub/*"/>
        </xsl:if-->
      </x:template>

    </x:stylesheet>
  </xsl:template>

  <xsl:template match="text">
      <x:text disable-output-escaping="yes">
          <xsl:value-of select="."/>
      </x:text>
  </xsl:template>

  <xsl:template match="value-of">
      <x:value-of>
          <xsl:attribute name="select">
              <xsl:value-of select="@select"/>
          </xsl:attribute>
      </x:value-of>
  </xsl:template>

  <xsl:template match="call-template">
      <x:call-template>
          <xsl:attribute name="name">
              <xsl:value-of select="@name"/>
          </xsl:attribute>
          <xsl:for-each select="with-param">
              <x:with-param>
                  <xsl:attribute name="name">
                      <xsl:value-of select="@name"/>
                  </xsl:attribute>
                  <xsl:attribute name="select">
                      <xsl:value-of select="@select"/>
                  </xsl:attribute>
              </x:with-param>
          </xsl:for-each>
      </x:call-template>
  </xsl:template>

</xsl:stylesheet>