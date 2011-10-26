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

  <xsl:output method="html" />

  <xsl:include href="common/standard_form_templates.xsl"/>

  <xsl:include href="common/generic_formheader.xsl"/>
  <xsl:include href="common/textarea.xsl"/>
  <xsl:include href="common/textfield.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>

  <xsl:param name="create"/>
  <xsl:param name="contenttypekey"/>

  <xsl:template match="/">

    <html>
      <head>
        <link type="text/css" rel="stylesheet" href="css/admin.css"/>
        <link type="text/css" rel="stylesheet" href="css/menu.css"/>
        <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>
        <link rel="stylesheet" type="text/css" href="css/assignment.css"/>
        <link type="text/css" rel="stylesheet" href="css/calendar_picker.css"/>
        <link type="text/css" rel="stylesheet" href="javascript/cms/ui/style.css"/>

        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" src="javascript/accessrights.js">//</script>
        <script type="text/javascript" src="javascript/validate.js">//</script>
        <script type="text/javascript" src="javascript/tabpane.js">//</script>
        <script type="text/javascript" src="javascript/menu.js">//</script>
        <script type="text/javascript" src="javascript/calendar_picker.js">//</script>
        <script type="text/javascript" src="javascript/properties.js">//</script>
        <script type="text/javascript" src="javascript/content_form.js">//</script>

        <script type="text/javascript" src="javascript/cms/core.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Event.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Cookie.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Css.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Dimensions.js">//</script>
        <script type="text/javascript" src="javascript/cms/ui/SplitButton.js">//</script>
      </head>

      <xsl:call-template name="contentform">
        <xsl:with-param name="show-edit-content-button" select="false()"/>
      </xsl:call-template>
    </html>
  </xsl:template>

  <xsl:template name="contenttypeform">
    <xsl:param name="readonly"/>

    <fieldset>
      <legend>&nbsp;%blockForm%&nbsp;</legend>
      <table border="0" cellspacing="0" cellpadding="2" width="100%">
        <tr><td class="form_labelcolumn"><!-- Empty --></td></tr>
        <tr>
          <xsl:call-template name="textfield">
            <xsl:with-param name="label" select="'%fldTitle%:'"/>
            <xsl:with-param name="name" select="'form_title'"/>
            <xsl:with-param name="required" select="'true'"/>
            <xsl:with-param name="selectnode" select="/contents/content/contentdata/form/title"/>
            <xsl:with-param name="readonly" select="true()"/>
            <xsl:with-param name="disabled" select="true()"/>
          </xsl:call-template>
          <input type="hidden" name="title" value="{/contents/content/title}"/>
        </tr>
        <xsl:for-each select="/contents/content/contentdata/form/item">
          <xsl:variable name="input_name" select="concat(/contents/content/@key, concat('_form_', position()))"/>
          <tr>
            <xsl:choose>
              <xsl:when test="@type = 'separator'">
                <td colspan="2">
                  <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td style="width: 10px; white-space: nowrap;border-bottom: 1px solid #CCCCCC;" ><img height="5" width="10" src="images/shim.gif"/></td>
                      <td style="padding: 0px 5px 0px 5px; white-space: nowrap;text-transform: uppercase; color: #999999;" rowspan="2"><xsl:value-of select="@label"/></td>
                      <td style="width: 100%;white-space: nowrap;border-bottom: 1px solid #CCCCCC;" ><img class="shim" src="images/shim.gif"/></td>
                    </tr>
                    <tr>
                      <td><img height="5" width="1" src="images/shim.gif"/></td>
                      <td><img src="images/shim.gif"/></td>
                    </tr>
                  </table>
                </td>
              </xsl:when>
              <xsl:when test="@type = 'text'">
                <td class="form_labelcolumn">
                  <xsl:value-of select="@label"/>:
                  <xsl:if test="@required = 'true'">
                    <span class="requiredfield">*</span>
                  </xsl:if>
                </td>
                <td width="85%">
                  <input type="text" name="{$input_name}" value="{data}" readonly="readonly" disabled="disabled"/>
                </td>
              </xsl:when>
              <xsl:when test="@type = 'textarea'">
                <td valign="top">
                  <xsl:value-of select="@label"/>:
                  <xsl:if test="@required = 'true'">
                    <span class="requiredfield">*</span>
                  </xsl:if>
                </td>
                <td>
                  <textarea name="{$input_name}" readonly="readonly" disabled="disabled" rows="10" cols="70">
                    <xsl:if test="@width and @height">
                      <xsl:attribute name="style">
                        <xsl:if test="@width">
                          <xsl:text>width:</xsl:text>
                          <xsl:value-of select="concat(@width,'px;')"/>
                        </xsl:if>
                        <xsl:if test="@height">
                          <xsl:text>height:</xsl:text>
                          <xsl:value-of select="concat(@height,'px;')"/>
                        </xsl:if>
                      </xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="data"/>
                  </textarea>
                </td>
              </xsl:when>
              <xsl:when test="@type = 'checkbox'">
                <td valign="top">
                  <xsl:value-of select="@label"/>:
                </td>
                <td>
                  <table border="0" cellspacing="2" cellpadding="0">
                    <tr>
                      <td>
                        <input type="checkbox" name="{$input_name}" disabled="disabled">
                          <xsl:if test="data = '1'">
                            <xsl:attribute name="checked">checked</xsl:attribute>
                          </xsl:if>
                        </input>
                      </td>
                    </tr>
                  </table>
                </td>
              </xsl:when>
              <xsl:when test="@type = 'radiobuttons'">
                <td valign="top">
                  <xsl:value-of select="@label"/>:
                  <xsl:if test="@required = 'true'">
                    <span class="requiredfield">*</span>
                  </xsl:if>
                </td>
                <td>
                  <table border="0" cellspacing="2" cellpadding="0">
                    <xsl:for-each select="data/option">
                      <tr>
                        <td>
                          <input type="radio" name="{$input_name}" value="{@value}" readonly="readonly" disabled="disabled">
                            <xsl:if test="@selected = 'true'">
                              <xsl:attribute name="checked">checked</xsl:attribute>
                            </xsl:if>
                          </input>
                          <xsl:text>&nbsp;</xsl:text>
                          <xsl:value-of select="@value"/>
                        </td>
                      </tr>
                    </xsl:for-each>
                  </table>
                </td>
              </xsl:when>
              <xsl:when test="@type = 'dropdown'">
                <td valign="top">
                  <xsl:value-of select="@label"/>:
                  <xsl:if test="@required = 'true'">
                    <span class="requiredfield">*</span>
                  </xsl:if>
                </td>
                <td>
                  <select name="{$input_name}">
                    <xsl:if test="$readonly">
                      <xsl:attribute name="disabled">true</xsl:attribute>
                    </xsl:if>
                    <xsl:for-each select="data/option">
                      <option value="{value}">
                        <xsl:if test="@selected = 'true'">
                          <xsl:attribute name="selected">selected</xsl:attribute>
                        </xsl:if>
                        <xsl:value-of select="@value"/>
                      </option>
                    </xsl:for-each>
                  </select>
                </td>
              </xsl:when>
              <xsl:when test="@type = 'checkboxes'">
                <td valign="top">
                  <xsl:value-of select="@label"/>:
                </td>
                <td>
                  <table border="0" cellspacing="2" cellpadding="0">
                    <xsl:for-each select="data/option">
                      <tr>
                        <td>
                          <input type="checkbox" name="{$input_name}" value="{@value}" readonly="readonly" disabled="disabled">
                            <xsl:if test="@selected = 'true'">
                              <xsl:attribute name="checked">checked</xsl:attribute>
                            </xsl:if>
                          </input>
                          <xsl:text>&nbsp;</xsl:text>
                          <xsl:value-of select="@value"/>
                        </td>
                      </tr>
                    </xsl:for-each>
                  </table>
                </td>
              </xsl:when>
              <xsl:when test="@type = 'fileattachment'">
                <td valign="top">
                  <xsl:value-of select="@label"/>:
                </td>
                <td>
                  <a href="{concat('_attachment/', /contents/content/@key, '/binary/', binarydata/@key )}" target="_blank"><xsl:value-of select="binarydata"/></a>
                </td>
              </xsl:when>
            </xsl:choose>
          </tr>
        </xsl:for-each>
      </table>
    </fieldset>

    <fieldset>
      <legend>%blockRecipients%</legend>
      <table border="0" cellspacing="2" cellpadding="0">
        <tr>
          <td class="form_labelcolumn" valign="top">%fldSentTo%:</td>
          <td>
            <table border="0" cellspacing="2" cellpadding="0">
              <xsl:for-each select="/contents/content/contentdata/form/recipients/e-mail">
                <tr>
                  <td><xsl:value-of select="."/></td>
                </tr>
              </xsl:for-each>
            </table>
          </td>
        </tr>
      </table>
    </fieldset>
  </xsl:template>
</xsl:stylesheet>
