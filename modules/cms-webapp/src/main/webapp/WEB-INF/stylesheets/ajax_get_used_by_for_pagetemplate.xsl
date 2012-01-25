<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html"/>

  <xsl:template match="/">

    <xsl:choose>
      <xsl:when test="count(//menuitems/menuitem) != 0">
        <table border="0" cellspacing="2" cellpadding="2">
          <xsl:for-each select="//menuitems/menuitem">
            <tr><td>
            <xsl:text>&nbsp;&nbsp;</xsl:text>
            <a>
              <xsl:attribute name="href">
                <xsl:text>adminpage?page=850&amp;op=form&amp;type=content&amp;browsemode=menuitem</xsl:text>
                <xsl:text>&amp;menukey=</xsl:text>
                <xsl:value-of select="@menukey"/>
                <xsl:text>&amp;key=</xsl:text>
                <xsl:value-of select="@key"/>
                <xsl:text>&amp;insertbelow=</xsl:text>
                <xsl:value-of select="@key"/>
              </xsl:attribute>
              <xsl:value-of select="@path-to-menu"/>
            </a>
            </td></tr>
          </xsl:for-each>
        </table>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>%msgNoMenuItemsIsUsingThisPageTemplate%</xsl:text>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

</xsl:stylesheet>
