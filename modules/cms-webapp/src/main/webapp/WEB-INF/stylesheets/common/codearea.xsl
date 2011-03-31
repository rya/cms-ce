<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">
  <!--
    Requires:

        XSL:

        <xsl:include href="common/labelcolumn.xsl"/>

        CSS:

        <link rel="stylesheet" type="text/css" href="css/admin.css"/>
        <link rel="stylesheet" type="text/css" href="css/codearea.css"/>

        JS:

        <script type="text/javascript" src="codemirror/js/codemirror.js">//</script>
        <script type="text/javascript" src="javascript/codearea.js">//</script>
        <script type="text/javascript" src="javascript/admin.js">//</script>
  -->

  <xsl:template name="codearea">
    <xsl:param name="name" select="''"/>
    <xsl:param name="label" select="''"/>
    <xsl:param name="required" select="''"/>
    <xsl:param name="selectnode"/>
    <xsl:param name="width" select="'600px'"/>
    <xsl:param name="height" select="'500px'"/>
    <xsl:param name="buttons" select="''"/>
    <xsl:param name="line-numbers" select="true()"/>
    <xsl:param name="status-bar" select="true()"/>
    <xsl:param name="read-only" select="false()"/>
    <xsl:param name="editable" select="true()"/>

    <xsl:if test="string-length($label) &gt; 0">
      <xsl:call-template name="labelcolumn">
        <xsl:with-param name="label" select="$label"/>
        <xsl:with-param name="required" select="$required"/>
        <xsl:with-param name="fieldname" select="$name"/>
      </xsl:call-template>
    </xsl:if>
    <td valign="top">
      
      <table id="{concat('code-area-container-', $name)}" class="code-area-container" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td id="{concat('code-area-buttons-container-', $name)}" class="code-area-buttons-container">
            <xsl:comment>Buttons</xsl:comment>
          </td>
        </tr>
        <tr>
          <td id="{concat('code-area-document-container-', $name)}" class="code-area-document-container">

            <textarea>
              <xsl:attribute name="name">
                <xsl:value-of select="$name"/>
              </xsl:attribute>
              <xsl:attribute name="id">
                <xsl:value-of select="$name"/>
              </xsl:attribute>
              <xsl:attribute name="style">
                <xsl:value-of select="concat('width:', $width, ';height:', $height)"/>
              </xsl:attribute>
              <xsl:value-of select="$selectnode"/>
            </textarea>

          </td>
        </tr>
        <tr>
          <td id="{concat('code-area-statusbar-container-', $name)}" class="code-area-statsubar-container">
            <xsl:attribute name="style">
              <xsl:choose>
                <xsl:when test="$status-bar">display: block</xsl:when>
                <xsl:otherwise>display: none</xsl:otherwise>
              </xsl:choose>
            </xsl:attribute>

            <div id="{concat('ca-caret-info-', $name)}" class="ca-caret-info">
              <span id="{concat('ca-current-line-number-', $name)}" class="ca-current-line-number">0</span>
              <span>:</span>
              <span id="{concat('ca-current-column-number-', $name)}" class="ca-current-column-number">0</span>
            </div>

          </td>
        </tr>
      </table>

      <script type="text/javascript">
        var codeArea_<xsl:value-of select="$name"/> = new cms.ui.CodeArea({
        'textareaId' : '<xsl:value-of select="$name"/>',
        'width' : '<xsl:value-of select="$width"/>',
        'height' : '<xsl:value-of select="$height"/>',
        'lineNumbers' : <xsl:value-of select="$line-numbers"/>,
        'statusBar' : <xsl:value-of select="$status-bar"/>,
        'readOnly' : <xsl:value-of select="$read-only"/>,
        'editable' : <xsl:value-of select="$editable"/>,
        'buttons' : '<xsl:value-of select="$buttons"/>'
        });

        codeArea_<xsl:value-of select="$name"/>.init();
      </script>

    </td>
  </xsl:template>
</xsl:stylesheet>