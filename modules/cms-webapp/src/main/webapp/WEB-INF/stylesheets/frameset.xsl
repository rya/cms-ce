<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:x="mailto:vro@enonic.com?subject=foobar"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html" doctype-public="-//W3C//DTD HTML 4.01 Frameset//EN"
              doctype-system="http://www.w3.org/TR/html4/frameset.dtd"/>

  <xsl:param name="mainframe" select="''"/>
  <xsl:param name="selecteddomainkey" select="''"/>
  <xsl:param name="selectedmenukey" select="''"/>
  <xsl:param name="rightframe"/>
  <xsl:param name="referer"/>
  <xsl:param name="user-agent"/>

  <xsl:template match="/">
       <xsl:call-template name="frameset"/>
  </xsl:template>

  <xsl:template name="frameset">
    <html>
      <head>
        <title>
          %headEnonicCMSAdministration%
        </title>
        <link rel="stylesheet" type="text/css" href="css/admin.css"/>
      </head>

      <xsl:variable name="loadmainstartpage">
        <xsl:if test="not($rightframe)">
          <xsl:text>%26loadmainstartpage%3Dtrue</xsl:text>
        </xsl:if>
      </xsl:variable>
      <xsl:variable name="rightframeurl">
        <xsl:choose>
          <xsl:when test="$rightframe">
            <xsl:value-of select="$rightframe"/>
            <xsl:if test="$referer">
              <xsl:text>&amp;referer=</xsl:text>
              <xsl:value-of select="admin:encode($referer)"/>
            </xsl:if>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>empty.html</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <frameset rows="70,*" cols="*">
        <!-- Weird Fx and MSIE hack for removing the border.(No W3C support -->
        <xsl:if test="contains($user-agent, 'Gecko') or contains($user-agent, 'MSIE')">
          <xsl:attribute name="framespacing">0</xsl:attribute>
          <xsl:attribute name="frameborder">NO</xsl:attribute>
        </xsl:if>

        <frame name="topFrame" scrolling="no" noresize="noresize"
               src="adminpage?page=1&amp;selecteddomainkey={$selecteddomainkey}"/>

        <frameset cols="305,*" rows="*" framespacing="6">
          <xsl:if test="contains($user-agent,'Gecko')">
            <xsl:attribute name="bordercolor">#eeeeee</xsl:attribute>
            <xsl:attribute name="frameborder">YES</xsl:attribute>
            <xsl:attribute name="border">6</xsl:attribute>
          </xsl:if>

          <frame class="leftframe" name="leftFrame" scrolling="auto"
          src="adminpage?page=5&amp;redirect=adminpage%3Fpage%3D2%26op%3Dbrowse%26selecteddomainkey%3D{$selecteddomainkey}%26selectedmenukey%3D{$selectedmenukey}{$loadmainstartpage}"/>
          <frame name="mainFrame" src="{$rightframeurl}"> </frame>

          <!--
          <frame name="mainFrame" src="adminpage?page=2000&amp;op=form&amp;key=143&amp;cat=35&amp;selectedunitkey=9"> </frame>
          -->


        </frameset>

      </frameset>


    </html>
  </xsl:template>

</xsl:stylesheet>