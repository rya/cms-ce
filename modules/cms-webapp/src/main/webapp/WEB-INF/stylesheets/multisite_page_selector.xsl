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

  <xsl:template match="/">
      <xsl:call-template name="documentlist"/>
  </xsl:template>

  <xsl:template name="documentlist">
    <html>
      <head>
        <title><xsl:value-of select="$menuname"/></title>
        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" src="javascript/menu.js">//</script>
        <script type="text/javascript" src="javascript/xtree.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Cookie.js">//</script>
        <script type="text/javascript" src="javascript/window.js"/>
        <script type="text/javascript">
          cms.window.attatchKeyEvent('close');
        </script>

        <script type="text/javascript" language="JavaScript">
          /*menu.js*/ var useCookies = false;
          /*menu.js*/ var branchOpen = new Array;

          function menuItem_Click( meiKey )
          {
            <xsl:if test="$menukey != ''">
              cms.utils.Cookie.create( 'multiSitePagePickerSelectedSite', '<xsl:value-of select="$menukey"/>', 0 )
            </xsl:if>

            window.parent.opener.setPickerFieldValues( 'input-page', 'page://' + meiKey, null );
            window.close();
          }

          function multi_page_selector_changeSite( menuKey )
          {
            if ( menuKey === '' ) return;

            document.location.href = 'adminpage?page=850&amp;op=menuitem_selector_multisite&amp;menukey=' + menuKey;
          }
        </script>

        <script type="text/javascript" src="javascript/menu.js">//</script>

        <link rel="stylesheet" type="text/css" href="css/menu.css"/>
        <link rel="stylesheet" type="text/css" href="css/admin.css"/>
        <link rel="stylesheet" type="text/css" href="javascript/xtree.css"/>
      </head>

      <body id="popup" style="padding-top:0">
        
        <xsl:call-template name="site-selector"/>

        <div>
          <xsl:apply-templates select="/multisiteselector/menus/menutop[@key = $menukey]" mode="displaytree">
            <xsl:with-param name="onclick" select="'menuItem_Click'"/>
            <xsl:with-param name="topnode" select="true()"/>
            <xsl:with-param name="top-node-is-clickable" select="false()"/>
            <xsl:with-param name="linkshaded" select="false()"/>
          </xsl:apply-templates>
        </div>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="menuitem" mode="text">
    <xsl:value-of select="@name"/>
  </xsl:template>

  <xsl:template name="site-selector">
    <p style="background-color:#eeeeee;padding:8px 2px; text-align:center; border-bottom: 1px solid #B8B8B8">
      <label for="menukey">%fldSite%: </label>
      <select name="menukey" id="menukey" onchange="multi_page_selector_changeSite( this.value )" style="width: 150px">
        <option value="">%sysDropDownChoose%</option>
        <xsl:for-each select="/multisiteselector/sites/site">
          <xsl:sort select="@name" order="ascending"/>
          <option value="{@key}">
            <xsl:if test="@key = $menukey">
              <xsl:attribute name="selected">true</xsl:attribute>
            </xsl:if>
            <xsl:value-of select="@name"/>
          </option>
        </xsl:for-each>
      </select>
    </p>
  </xsl:template>
</xsl:stylesheet>
