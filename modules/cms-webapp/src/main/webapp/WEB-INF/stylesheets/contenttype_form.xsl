<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
 ]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:include href="common/displayerror.xsl"/>
    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/readonlyvalue.xsl"/>
    <xsl:include href="common/textfield.xsl"/>
    <xsl:include href="common/textarea.xsl"/>
    <xsl:include href="common/displaysystempath.xsl"/>
    <xsl:include href="common/serialize.xsl"/>
    <xsl:include href="common/dropdown_refresh.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/resourcefield.xsl"/>
    <xsl:include href="common/codearea.xsl"/>

    <xsl:param name="create"/>
    <xsl:param name="usehandlerxml"/>
    <xsl:param name="modulexml" select="''"/>
    <xsl:param name="contenthandlerkey"/>
    <xsl:param name="usehandlerindexing" select="'false'"/>
    <xsl:param name="generatectykey" select="'true'"/>
    <xsl:param name="cssname"/>
    <xsl:param name="cssexist"/>
    <xsl:param name="errorInConfig"/>

    <xsl:variable name="pageURL">
        <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
        <xsl:text>&amp;op=browse</xsl:text>
    </xsl:variable>

    <xsl:template match="/">

        <html>
            <head>
                <script type="text/javascript" src="javascript/tabpane.js">//</script>
                <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css" />
                <link rel="stylesheet" type="text/css" href="css/admin.css"/>
                <link rel="stylesheet" type="text/css" href="css/codearea.css"/>

                <script type="text/javascript" src="codemirror/js/codemirror.js">//</script>
                <script type="text/javascript" src="javascript/codearea.js">//</script>
                <script type="text/javascript" src="javascript/admin.js">//</script>
                <script type="text/javascript" src="javascript/validate.js">//</script>

                <script type="text/javascript" language="JavaScript">
                    <xsl:choose>
                        <xsl:when test="not(/contenttypes/contenttype/@contentcount) or /contenttypes/contenttype/@contentcount = 0">
                            var validateFields = new Array(2);
                            validateFields[0] = new Array("%fldContentHandler%", "contenthandlerkey", validateDropdown);
                            validateFields[1] = new Array("%fldName%", "name", validateRequired);
                            validateFields[2] = new Array("%fldName%", "name", validateAZ09_dot_dash_underscore);
                        </xsl:when>
                        <xsl:otherwise>
                            var validateFields = new Array(1);
                            validateFields[0] = new Array("%fldName%", "name", validateRequired);
                            validateFields[1] = new Array("%fldName%", "name", validateAZ09_dot_dash_underscore);
                        </xsl:otherwise>
                    </xsl:choose>

                    var g_codeMirror = null;

                    function validateAll(formName) {

                        if ( codeArea_module )
                        {
                          document.getElementById('module').value = codeArea_module.getCode();
                        }

                        var f = document.forms[formName];

                        if ( !checkAll(formName, validateFields) )
                          return;

                        f.submit();
                    }

                    function updateContentType() {
                        document.formAdmin.action = 'adminpage?page=<xsl:value-of select="$page"/>&amp;op=form&amp;reload=true';
                        document.formAdmin.submit();
                    }

                </script>

            </head>

            <body onload="setFocus()">

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
                    <input type="hidden" name="referer" value="{$referer}"/>

                    <h1>
                        <xsl:call-template name="displaysystempath">
                            <xsl:with-param name="page" select="$page"/>
                        </xsl:call-template>
                        <xsl:text>&nbsp;</xsl:text>
                        <span id='titlename'>
                            <xsl:if test="$create != 1">
                                <xsl:value-of select="concat('/ ', /contenttypes/contenttype/name)"/>
                            </xsl:if>
                        </span>
                    </h1>

                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td class="form_title_form_seperator"><img src="images/1x1.gif"/></td>
                        </tr>
                        <tr>
                            <td>
                                <xsl:call-template name="contenttypeform"/>
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
                                        <xsl:text>javascript:validateAll('formAdmin');</xsl:text>
                                    </xsl:with-param>
                                    <xsl:with-param name="disabled" select="$cssexist = 'false'"/>
                                </xsl:call-template>

                                <xsl:text>&nbsp;</xsl:text>

                                <xsl:call-template name="button">
                                    <xsl:with-param name="type" select="'cancel'"/>
                                    <xsl:with-param name="referer" select="$referer"/>
                                </xsl:call-template>
                            </td>
                        </tr>
                    </table>
                </form>
            </body>
        </html>

    </xsl:template>

    <xsl:template name="contenttypeform">

        <xsl:variable name="selectnode_temp" as="node()">
            <contenttype>
                <xsl:choose>
                    <xsl:when test="$usehandlerindexing = 'true'">
                        <xsl:copy-of select="/contenttypes/contenthandlers/contenthandler[@key=$contenthandlerkey]/xmlconfig/ctydefault/*"/>
                        <xsl:copy-of select="/contenttypes/contenthandlers/contenthandler[@key=$contenthandlerkey]/xmlconfig/indexparameters"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:copy-of select="/contenttypes/contenttype/moduledata/*"/>
                    </xsl:otherwise>
                </xsl:choose>
            </contenttype>
        </xsl:variable>

        <xsl:variable name="selectnode">
            <xsl:call-template name="serialize">
                <xsl:with-param name="xpath" select="$selectnode_temp"/>
                <xsl:with-param name="include-self" select="true()"/>
            </xsl:call-template>
        </xsl:variable>

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
                    <table width="100%" cellspacing="0" cellpadding="2" border="0">
                        <tr>
                            <td class="form_labelcolumn"> </td>
                        </tr>
                        <xsl:if test="$create = '1'">
                            <input type="hidden" name="create" value="true"/>
                        </xsl:if>
                        <xsl:if test="$create = '0'">
                            <xsl:call-template name="readonlyvalue">
                                <xsl:with-param name="name" select="'key'"/>
                                <xsl:with-param name="label" select="'%fldModuleNo%:'"/>
                                <xsl:with-param name="selectnode" select="/contenttypes/contenttype/@key"/>
                                <xsl:with-param name="colspan" select="'1'"/>
                            </xsl:call-template>
                        </xsl:if>
                        <tr>
                            <xsl:call-template name="dropdown_refresh">
                                <xsl:with-param name="name" select="'contenthandlerkey'"/>
                                <xsl:with-param name="label" select="'%fldContentHandler%:'"/>
                                <xsl:with-param name="selectedkey" select="$contenthandlerkey"/>
                                <xsl:with-param name="selectnode" select="/contenttypes/contenthandlers/contenthandler"/>
                                <xsl:with-param name="required" select="true()"/>
                                <xsl:with-param name="disabled" select="/contenttypes/contenttype/@contentcount &gt; 0 and $create = '0'"/>
                                <xsl:with-param name="onchangefunction" select="'updateContentType()'"/>
                            </xsl:call-template>
                        </tr>
                        <tr>
                            <xsl:call-template name="textfield">
                                <xsl:with-param name="name" select="'name'"/>
                                <xsl:with-param name="label" select="'%fldName%:'"/>
                                <xsl:with-param name="selectnode" select="/contenttypes/contenttype/name"/>
                                <xsl:with-param name="size" select="'60'"/>
                                <xsl:with-param name="maxlength" select="'31'"/>
                                <xsl:with-param name="colspan" select="'1'"/>
                                <xsl:with-param name="onkeyup">javascript: updateBreadCrumbHeader('titlename', this);</xsl:with-param>
                                <xsl:with-param name="required" select="'true'"/>
                            </xsl:call-template>
                        </tr>
                        <tr>
                            <xsl:call-template name="textarea">
                                <xsl:with-param name="name" select="'description'"/>
                                <xsl:with-param name="label" select="'%fldDescription%:'"/>
                                <xsl:with-param name="selectnode" select="/contenttypes/contenttype/description"/>
                                <xsl:with-param name="rows" select="'5'"/>
                                <xsl:with-param name="cols" select="'60'"/>
                                <xsl:with-param name="colspan" select="'1'"/>
                            </xsl:call-template>
                        </tr>
                        <tr>
                            <xsl:call-template name="resourcefield">
                                <xsl:with-param name="name" select="'csskey'"/>
                                <xsl:with-param name="mimetype" select="'text/css'"/>
                                <xsl:with-param name="label" select="'%fldCSS%:'"/>
                                <xsl:with-param name="value" select="/contenttypes/contenttype/@csskey"/>
                                <xsl:with-param name="exist" select="$cssexist"/>
                            </xsl:call-template>
                        </tr>
                        <xsl:if test="string-length($errorInConfig) > 0">
                            <tr>
                                <td><br/></td>
                                <td class="warning-message">
                                    <img src="images/form-error.png" style="vertical-align:middle"/>
                                    <xsl:text>&nbsp;</xsl:text>
                                    <xsl:value-of select="$errorInConfig"/>
                                </td>
                            </tr>
                        </xsl:if>
                        <tr>
                            <xsl:call-template name="codearea">
                                <xsl:with-param name="name" select="'module'"/>
                                <xsl:with-param name="label" select="'%fldConfig%:'"/>
                                <xsl:with-param name="width" select="'100%'"/>
                                <xsl:with-param name="height" select="'380px'"/>
                                <xsl:with-param name="line-numbers" select="true()"/>
                                <xsl:with-param name="read-only" select="false()"/>
                                <xsl:with-param name="selectnode" select="$selectnode"/>
                                <xsl:with-param name="buttons" select="'find,replace,indentall,indentselection,gotoline'"/>
                                <xsl:with-param name="status-bar" select="true()"/>
                            </xsl:call-template>
                        </tr>
                    </table>
                </fieldset>
            </div>
        </div>
        <script type="text/javascript" language="JavaScript">
            setupAllTabs();
        </script>
    </xsl:template>
</xsl:stylesheet>
