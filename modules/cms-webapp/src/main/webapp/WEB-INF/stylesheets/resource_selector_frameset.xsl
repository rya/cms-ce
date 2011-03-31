<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
        <!ENTITY nbsp "&#160;">
        ]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html"/>
  <xsl:param name="fieldname"/>
  <xsl:param name="mimetype"/>
  <xsl:param name="extension"/>
  <xsl:param name="user-agent"/>
  <xsl:template match="/">
    <html>
      <head>
        <title>&nbsp;%headChooseResource%&nbsp;
          <xsl:if test="not($mimetype = '') or not($extension = '')">
            <xsl:text>(</xsl:text>
            <xsl:if test="not($mimetype = '')">
              <xsl:text>mimetype:</xsl:text>
              <xsl:value-of select="$mimetype"/>
            </xsl:if>
            <xsl:if test="not($extension = '')">
              <xsl:if test="not($mimetype = '')">
                <xsl:text>,</xsl:text>
              </xsl:if>
              <xsl:text>extension:</xsl:text>
              <xsl:value-of select="$extension"/>
            </xsl:if>
            <xsl:text>)</xsl:text>
          </xsl:if>
        </title>
        <link rel="stylesheet" type="text/css" href="css/admin.css"/>
      </head>
      <frameset id="contentFrameset" name="contentFrameset" cols="200, *" framespacing="6" frameborder="NO">
        <xsl:if test="contains($user-agent,'Gecko')">
          <xsl:attribute name="bordercolor">#eeeeee</xsl:attribute>
          <xsl:attribute name="frameborder">YES</xsl:attribute>
          <xsl:attribute name="border">6</xsl:attribute>
        </xsl:if>
        <frame class="leftframe" name="list" id="list" marginwidth="4" marginheight="0">
          <xsl:attribute name="src">
            <xsl:text>adminpage?page=800&amp;op=menu</xsl:text>
            <xsl:text>&amp;fieldname=</xsl:text>
            <xsl:value-of select="$fieldname"/>
            <xsl:text>&amp;mimetype=</xsl:text>
            <xsl:value-of select="$mimetype"/>
            <xsl:text>&amp;extension=</xsl:text>
            <xsl:value-of select="$extension"/>
          </xsl:attribute>
        </frame>
        <frame name="mainFrame" id="action" src="empty.html"/>
      </frameset>
    </html>
  </xsl:template>
</xsl:stylesheet>
