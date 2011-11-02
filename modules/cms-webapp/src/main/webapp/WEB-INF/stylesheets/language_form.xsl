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

    <xsl:output method="html"/>

    <xsl:include href="common/readonlyvalue.xsl"/>
    <xsl:include href="common/textfield.xsl"/>
    <xsl:include href="common/textarea.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/displaysystempath.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>
    <xsl:include href="common/displayerror.xsl"/>
    <xsl:include href="common/serialize.xsl"/>

    <xsl:param name="create"/>
    <xsl:param name="page"/>

    <xsl:template match="/">

        <html>
            <script type="text/javascript" language="JavaScript" src="javascript/validate.js">
            </script>
            <link rel="stylesheet" type="text/css" href="css/admin.css"/>
            <script type="text/javascript" src="javascript/admin.js">//</script>
            <script type="text/javascript" src="javascript/tabpane.js">//</script>
            <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css" />
            <script type="text/javascript" language="JavaScript">

                var validatedFields = [];
                validatedFields[0] = new Array("%fldLanguageCode%", "languagecode", validateRequired);
                validatedFields[1] = new Array("%fldDescription%", "description", validateRequired);

                function validateAll(formName) {
                  var f = document.forms[formName];

                  if ( !checkAll(formName, validatedFields) )
                    return;

                  f.submit();
                }

            </script>
            <body>

                <form name="formAdmin" method="post">

                    <xsl:attribute name="action">
                        <xsl:if test="$create=1">
                            <xsl:text>adminpage?page=</xsl:text>
                            <xsl:value-of select="$page"/>
                            <xsl:text>&amp;op=create</xsl:text>
                        </xsl:if>
                        <xsl:if test="$create=0">
                            <xsl:text>adminpage?page=</xsl:text>
                            <xsl:value-of select="$page"/>
                            <xsl:text>&amp;op=update</xsl:text>
                        </xsl:if>
                    </xsl:attribute>

                  <xsl:choose>
                    <xsl:when test="$create=1">
                      <h1>
                        <xsl:call-template name="displaysystempath">
                          <xsl:with-param name="page" select="$page"/>
                        </xsl:call-template>
                        <xsl:text>&nbsp;</xsl:text>
                        <span id='titlename'> </span>
                      </h1>
                    </xsl:when>
                    <xsl:otherwise>
                      <h1>
                        <xsl:call-template name="displaysystempath">
                          <xsl:with-param name="page" select="$page"/>
                        </xsl:call-template>
                        <xsl:text>&nbsp;</xsl:text>
                        <span id='titlename'><xsl:value-of select="concat('/ ', /languages/language/@languagecode)"/></span>
                      </h1>
                    </xsl:otherwise>
                  </xsl:choose>

                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td class="form_title_form_seperator"><img src="images/1x1.gif"/></td>
                        </tr>
                        <tr>
                            <td>
                                <xsl:call-template name="form"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="form_form_buttonrow_seperator"><img src="images/1x1.gif"/></td>
                        </tr>
                        <tr>
                            <td>
                                <xsl:call-template name="button">
                                    <xsl:with-param name="type" select="'button'"/>
                                    <xsl:with-param name="caption" select="'%cmdSave%'"/>
                                    <xsl:with-param name="name" select="'lagre'"/>
                                    <xsl:with-param name="onclick">
                                        <xsl:text>javascript:validateAll( 'formAdmin' );</xsl:text>
                                    </xsl:with-param>
                                </xsl:call-template>

                                <xsl:text>&nbsp;</xsl:text>

                                <xsl:call-template name="button">
                                    <xsl:with-param name="type" select="'button'"/>
                                    <xsl:with-param name="caption" select="'%cmdCancel%'"/>
                                    <xsl:with-param name="name" select="'avbryt'"/>
                                    <xsl:with-param name="onclick">
                                        <xsl:text>javascript:history.back();</xsl:text>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </td>
                        </tr>
                    </table>
                </form>
            </body>
        </html>

    </xsl:template>

    <xsl:template name="form">
        <div class="tab-pane" id="tab-pane-1">
            <script type="text/javascript" language="JavaScript">
                var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
            </script>

            <div class="tab-page" id="tab-page-1">
                <span class="tab">%blockGeneral%</span>

                <script type="text/javascript" language="JavaScript">
                    tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                </script>

                <fieldset>
                    <legend>&nbsp;%blockGeneral%&nbsp;</legend>

                    <xsl:variable name="help-element">
                      <help>%hlpLanguageCode%</help>
                    </xsl:variable>

                    <xsl:if test="/languages/language/@key != ''">
                      <input type="hidden" name="key" value="{/languages/language/@key}"/>
                    </xsl:if>

                    <table cellspacing="0" cellpadding="2" border="0">

                        <tr>
                            <xsl:call-template name="textfield">
                                <xsl:with-param name="name" select="'languagecode'"/>
                                <xsl:with-param name="label" select="'%fldLanguageCode%:'"/>
                                <xsl:with-param name="selectnode" select="/languages/language/@languagecode"/>
                                <xsl:with-param name="helpelement" select="$help-element"/>
                                <xsl:with-param name="size" select="'32'"/>
                                <xsl:with-param name="maxlength" select="'32'"/>
                                <xsl:with-param name="colspan" select="'1'"/>
                                <xsl:with-param name="required" select="'true'"/>
                                <xsl:with-param name="onkeyup">javascript: updateBreadCrumbHeader('titlename', this)</xsl:with-param>
                            </xsl:call-template>
                        </tr>
                        <tr>
                            <xsl:call-template name="textfield">
                                <xsl:with-param name="name" select="'description'"/>
                                <xsl:with-param name="label" select="'%fldDescription%:'"/>
                                <xsl:with-param name="selectnode" select="/languages/language"/>
                                <xsl:with-param name="size" select="'32'"/>
                                <xsl:with-param name="maxlength" select="'32'"/>
                                <xsl:with-param name="colspan" select="'1'"/>
                                <xsl:with-param name="required" select="'true'"/>
                            </xsl:call-template>
                        </tr>
                    </table>
                </fieldset>
            </div>
            <script type="text/javascript" language="JavaScript">
                setupAllTabs();
            </script>
        </div>



    </xsl:template>

</xsl:stylesheet>
