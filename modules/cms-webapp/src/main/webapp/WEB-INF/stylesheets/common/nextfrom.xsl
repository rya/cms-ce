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

  <xsl:template name="nextfrom">
    <xsl:param name="url"/>
    <xsl:param name="from" select="0"/>
    <xsl:param name="to" select="19"/>
    <xsl:param name="count" select="count(/contents/content)"/>
    <xsl:param name="totalcount" select="/contents/@totalcount"/>
    <xsl:param name="sortby" select="''"/>
    <xsl:param name="sortby-direction" select="''"/>
    <xsl:param name="trclass"/>
    <xsl:param name="tdclass"/>
    <xsl:param name="shimheight"/>
    <xsl:param name="archivemode" select="false()"/>

    <xsl:variable name="range" select="$to - $from + 1"/>

    <tr>
      <xsl:if test="$trclass">
        <xsl:attribute name="class">
          <xsl:value-of select="$trclass"/>
        </xsl:attribute>
      </xsl:if>
      <td colspan="10">
        <xsl:if test="$tdclass">
          <xsl:attribute name="class">
            <xsl:value-of select="$tdclass"/>
          </xsl:attribute>
        </xsl:if>
        <table border="0" cellpadding="0" cellspacing="0" width="100%">
          <tr>
            <td align="center">
              <xsl:choose>
                <xsl:when test="$from &gt; 0">
                  <a>
                    <xsl:attribute name="href">
                      <xsl:value-of select="$url"/>
                      <xsl:text>&amp;from=</xsl:text>
                      <xsl:value-of select="$from - $range"/>
                      <xsl:text>&amp;to=</xsl:text>
                      <xsl:value-of select="$from - 1"/>

                      <xsl:text>&amp;sortby=</xsl:text>
                      <xsl:value-of select="$sortby"/>
                      <xsl:text>&amp;sortby-direction=</xsl:text>
                      <xsl:value-of select="$sortby-direction"/>
                      <xsl:if test="$archivemode">
                        <xsl:text>&amp;cat=</xsl:text>
                        <xsl:value-of select="$cat"/>
                        <xsl:text>&amp;selectedunitkey=</xsl:text>
                        <xsl:value-of select="$selectedunitkey"/>
                      </xsl:if>
                    </xsl:attribute>
                    <xsl:text>%cmdPrevious%</xsl:text>
                  </a>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:text>&nbsp;</xsl:text>
                  <img src="images/icon_previous.gif"/>
                </xsl:when>
                <xsl:otherwise>
                  <span style="color: #C0C0C0">%cmdPrevious%</span>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:text>&nbsp;</xsl:text>
                  <img src="images/icon_previous.gif" style="filter: alpha(opacity=30);"/>
                </xsl:otherwise>
              </xsl:choose>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:choose>
                <xsl:when test="$count = 0">
                  <xsl:text>0 - 0</xsl:text>
                </xsl:when>
                <xsl:when test="$count &lt; $range">
                  <xsl:text></xsl:text>
                  <xsl:value-of select="$from + 1"/>
                  -
                  <xsl:value-of select="$totalcount"/>
                  <xsl:text>&nbsp;&nbsp;%of%&nbsp;&nbsp;</xsl:text>
                  <xsl:value-of select="$totalcount"/>
                  <xsl:text></xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text></xsl:text>
                  <xsl:value-of select="$from + 1"/>
                  -
                  <xsl:value-of select="$to + 1"/>
                  <xsl:text>&nbsp;&nbsp;%of%&nbsp;&nbsp;</xsl:text>
                  <xsl:value-of select="$totalcount"/>
                  <xsl:text></xsl:text>
                </xsl:otherwise>
              </xsl:choose>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:choose>
                <xsl:when test="$totalcount &gt; $to + 1">
                  <img src="images/icon_next.gif"/>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:text>&nbsp;</xsl:text>
                  <a>
                    <xsl:attribute name="href">
                      <xsl:value-of select="$url"/>
                      <xsl:text>&amp;from=</xsl:text>
                      <xsl:value-of select="$from + $range"/>
                      <xsl:text>&amp;to=</xsl:text>
                      <xsl:value-of select="$to + $range"/>
                      <xsl:text>&amp;sortby=</xsl:text>
                      <xsl:value-of select="$sortby"/>
                      <xsl:text>&amp;sortby-direction=</xsl:text>
                      <xsl:value-of select="$sortby-direction"/>
                      <xsl:if test="$archivemode">
                        <xsl:text>&amp;cat=</xsl:text>
                        <xsl:value-of select="$cat"/>
                        <xsl:text>&amp;selectedunitkey=</xsl:text>
                        <xsl:value-of select="$selectedunitkey"/>
                      </xsl:if>
                    </xsl:attribute>
                    <xsl:text>%cmdNext%</xsl:text>
                  </a>
                </xsl:when>
                <xsl:otherwise>
                  <img src="images/icon_next.gif" style="filter: alpha(opacity=30);"/>
                  <xsl:text>&nbsp;</xsl:text>
                  <xsl:text>&nbsp;</xsl:text>
                  <span style="color: #C0C0C0">%cmdNext%</span>
                </xsl:otherwise>
              </xsl:choose>
              <xsl:if test="$shimheight">
                <img src="images/shim.gif" width="1" align="absmiddle">
                  <xsl:attribute name="height">
                    <xsl:value-of select="$shimheight"/>
                  </xsl:attribute>
                </img>
              </xsl:if>
            </td>


          </tr>
        </table>
      </td>
    </tr>
  </xsl:template>

</xsl:stylesheet>