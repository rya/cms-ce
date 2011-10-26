<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>

  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:include href="handlerconfigs/default.xsl"/>
  <xsl:include href="tree/displaytree.xsl"/>
  <xsl:include href="common/button.xsl"/>

  <xsl:param name="cur_parent_key"/>
  <xsl:param name="key"/>
  <xsl:param name="menuitemname"/>

  <xsl:variable name="lcletters">abcdefghijklmnopqrstuvwxyz</xsl:variable>
  <xsl:variable name="ucletters">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>

  <xsl:variable name="lcmenuitemname" select="translate($menuitemname, $ucletters, $lcletters)"/>

  <xsl:template match="/">
      <xsl:call-template name="documentlist"/>
  </xsl:template>

  <xsl:template name="documentlist">
    <html>
      <head>
        <title><xsl:value-of select="$menuname"/></title>
        <script type="text/javascript" language="JavaScript" src="javascript/admin.js">//</script>
        <script type="text/javascript" language="JavaScript" src="javascript/menu.js">//</script>
        <script type="text/javascript" language="JavaScript" src="javascript/xtree.js">//</script>
        <script type="text/javascript" src="javascript/window.js"/>
        <script type="text/javascript">
          cms.window.attatchKeyEvent('close');
        </script>

        <link rel="stylesheet" type="text/css" href="css/admin.css"/>
        <link rel="stylesheet" type="text/css" href="css/menu.css"/>
        <link rel="stylesheet" type="text/css" href="javascript/xtree.css"/>

        <script type="text/javascript" language="JavaScript">
          var branchOpen = new Array;
          var allcookies = document.cookie;
          var cookiename =	<xsl:text>'adminmenu</xsl:text>
          <xsl:value-of select="$selecteddomainkey"/>
          <xsl:text>';</xsl:text>
          var pos = allcookies.indexOf(cookiename + "=");
          if (pos != -1 ){
              var start = pos + cookiename.length + 1;
              var end = allcookies.indexOf(";", start);
              if (end == -1)
                  end = allcookies.length;
              var values = allcookies.substring(start, end).split(',');

              for ( i in values ){
                  branchOpen[values[i]] = true;
              }
          }
        </script>

        <script type="text/javascript" language="JavaScript">
          function moveMenuItem(key) {
            if (key == undefined)
              key = -1;

            selectedMenuItem = key;

            move();
          }

          function move()
          {
            window.top.opener.location =
            <xsl:text>"adminpage?op=movebelow&amp;page=</xsl:text><xsl:value-of select="$page"/>
            <xsl:text>&amp;selectedunitkey=</xsl:text><xsl:value-of select="$selectedunitkey"/>
            <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
            <xsl:text>&amp;key=</xsl:text><xsl:value-of select="$key"/>
            <xsl:text>&amp;belowkey=" + selectedMenuItem;</xsl:text>
            window.close();
          }

          var selectedMenuItem;
          var counter = 0;
          var expandArray = new Array(<xsl:value-of select="count(//menuitem)"/>);
        </script>


        <script type="text/javascript" src="javascript/menu.js">//</script>

        <link rel="stylesheet" type="text/css" href="css/menu.css"/>
        <link rel="stylesheet" type="text/css" href="css/admin.css"/>
        <link rel="stylesheet" type="text/css" href="javascript/xtree.css"/>
      </head>

      <body id="popup">
        <xsl:apply-templates select="*" mode="displaytree">
          <xsl:with-param name="onclick" select="'moveMenuItem'"/>
          <xsl:with-param name="topnode" select="true()"/>
          <xsl:with-param name="linkshaded" select="false()"/>
        </xsl:apply-templates>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="menuitem" mode="text" priority="1.0">
    <xsl:value-of select="@name"/>
  </xsl:template>

  <xsl:template match="menutop" mode="shadeicon" priority="1.0">
    <xsl:choose>
      <xsl:when test="@useradministrate = 'true' and not($cur_parent_key = -1)">false</xsl:when>
      <xsl:otherwise>
        <xsl:text>true</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--  legg inn sjekk her + match for menutop? -->
  <xsl:template match="menuitem" mode="shadeicon" priority="9.0">
    <xsl:variable name="xpathUserright" select="accessrights/userright"/>
    <xsl:variable name="is-descendant-of-selected" select="ancestor::*[@key = $key]"/>
    <xsl:choose>
      <xsl:when test="$is-descendant-of-selected">true</xsl:when>
      <xsl:when test="normalize-space(translate(menuitem[translate(@name, $ucletters, $lcletters) = $lcmenuitemname]/@name, $ucletters , $lcletters)) = normalize-space($lcmenuitemname)">true</xsl:when>
      <xsl:when test="@useradministrate = 'true' and not(@key = $key) and not(@key = $cur_parent_key)">false</xsl:when>
      <xsl:otherwise>
        <xsl:text>true</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
