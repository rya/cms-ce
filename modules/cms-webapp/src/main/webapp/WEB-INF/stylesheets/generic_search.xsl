<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>

  <xsl:include href="common/serialize.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>
  <xsl:include href="common/displayerror.xsl"/>
  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="common/searchfield.xsl"/>
  <xsl:include href="common/formatdate.xsl"/>
  <xsl:include href="common/textfielddate.xsl"/>
  <xsl:include href="common/textfielddatetime.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/textarea.xsl"/>
  <xsl:include href="menu/header.xsl"/>
  <xsl:include href="common/advanced_search_form.xsl"/>
  <xsl:include href="common/user-picker-with-autocomplete.xsl"/>

  <xsl:param name="fieldname"/>
  <xsl:param name="fieldrow"/>
  <xsl:param name="contenttypestring"/>
  <xsl:param name="subop"/>

  <xsl:param name="contenttypekey"/>
  <xsl:param name="contentselector"/>
  <xsl:param name="contentselector_name"/>
  <xsl:param name="contentselector_contenttypekey"/>
  <xsl:param name="minoccurrence"/>
  <xsl:param name="maxoccurrence"/>
  <xsl:param name="contenthandler"/>

  <xsl:variable name="category" select="/data/contentcategories/contentcategory"/>

  <xsl:template match="/">
    <html>
      <head>
        <link href="css/admin.css" rel="stylesheet" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="css/calendar_picker.css"/>

        <link type="text/css" rel="stylesheet" href="javascript/lib/jquery/ui/autocomplete/css/cms/jquery-ui-1.8.1.custom.css"/>
        <link type="text/css" rel="stylesheet" href="javascript/lib/jquery/ui/autocomplete/css/cms/jquery-ui-1.8.1.overrides.css"/>
        <link type="text/css" rel="stylesheet" href="css/user-picker-with-autocomplete.css"/>
        <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>

        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" src="javascript/accessrights.js">//</script>
        <script type="text/javascript" src="javascript/calendar_picker.js">//</script>
        <script type="text/javascript" src="javascript/validate.js">//</script>
        <script type="text/javascript" src="javascript/content_form.js">//</script>
        <script type="text/javascript" src="javascript/content_advancedsearch.js">//</script>

        <script type="text/javascript" src="javascript/lib/jquery/jquery-1.4.2.min.js">//</script>
        <script type="text/javascript" src="javascript/lib/jquery/ui/autocomplete/js/jquery-ui-1.8.1.custom.min.js">//</script>
        <script type="text/javascript" src="javascript/user-picker-with-autocomplete.js">//</script>

        <script type="text/javascript" src="javascript/tabpane.js">//</script>
                
      </head>

      <body onload="document.getElementById('asearchtext').focus()" class="jquery-ui">

        <h1>
          <xsl:variable name="url">
            <xsl:text>adminpage?op=browse&amp;subop=</xsl:text>
            <xsl:value-of select="$subop"/>
							<xsl:if test="$fieldname">
                <xsl:text>&amp;fieldname=</xsl:text>
                <xsl:value-of select="$fieldname"/>
              </xsl:if>
            <xsl:if test="$fieldrow">
              <xsl:text>&amp;fieldrow=</xsl:text>
              <xsl:value-of select="$fieldrow"/>
            </xsl:if>
            <xsl:if test="$contenttypestring">
              <xsl:text>&amp;contenttypestring=</xsl:text>
              <xsl:value-of select="$contenttypestring"/>
            </xsl:if>
            <xsl:if test="$minoccurrence">
              <xsl:text>&amp;minoccurrence=</xsl:text>
              <xsl:value-of select="$minoccurrence"/>
            </xsl:if>
            <xsl:if test="$maxoccurrence">
              <xsl:text>&amp;maxoccurrence=</xsl:text>
              <xsl:value-of select="$maxoccurrence"/>
            </xsl:if>
          </xsl:variable>

          <xsl:choose>
            <xsl:when test="$subop = 'relatedcontent' or $subop = 'relatedimage' or $subop = 'relatedimages' or $subop = 'relatedfiles' or $subop = 'addcontenttosection'">
              <xsl:apply-templates select="/data/path/node()/node()">
                <xsl:with-param name="url" select="$url"/>
                <xsl:with-param name="usedisable" select="false()"/>
              </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select="/data/path/node()">
                <xsl:with-param name="url" select="$url"/>
                <xsl:with-param name="usedisable" select="false()"/>
              </xsl:apply-templates>
            </xsl:otherwise>
          </xsl:choose>

        </h1>

        <xsl:call-template name="advanced_search_form">
          <xsl:with-param name="page" select="$page"/>
          <xsl:with-param name="subop" select="$subop"/>
          <xsl:with-param name="fieldname" select="$fieldname"/>
          <xsl:with-param name="fieldrow" select="$fieldrow"/>
          <xsl:with-param name="cat" select="$cat"/>
          <xsl:with-param name="cancelUrl" select="$referer"/>
          <xsl:with-param name="contenttypestring" select="$contenttypestring"/>
          <xsl:with-param name="minoccurrence" select="$minoccurrence"/>
          <xsl:with-param name="maxoccurrence" select="$maxoccurrence"/>
          <xsl:with-param name="contenthandler" select="$contenthandler"/>
        </xsl:call-template>

      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>