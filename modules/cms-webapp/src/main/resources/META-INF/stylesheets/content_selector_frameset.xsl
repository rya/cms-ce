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

 <xsl:output method="html" indent="yes" doctype-system="http://www.w3.org/TR/html4/loose.dtd" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
  <xsl:param name="returnkey"/>
  <xsl:param name="returnview"/>
  <xsl:param name="selectedunitkey"/>
  <xsl:param name="contenttypestring"/>
  <xsl:param name="fieldname" select="''"/>
  <xsl:param name="fieldrow" select="''"/>
  <xsl:param name="page"/>
  <xsl:param name="op"/>
  <xsl:param name="subop" select="''"/>
  <xsl:param name="dest" select="''"/>
  <xsl:param name="cat" select="''"/>
  <xsl:param name="user-agent" />
  <xsl:param name="unitfiltercontenttype" select="''"/>
  <xsl:param name="requirecategoryadmin" select="''"/>
  <xsl:param name="excludecategorykey" select="''"/>
  <xsl:param name="excludecategorykey_withchildren" select="''"/>
  <xsl:param name="contenthandler" select="''"/>
  <xsl:param name="minoccurrence" select="''"/>
  <xsl:param name="maxoccurrence" select="''"/>
  <xsl:template match="/">
    <html>
      <head>
        <title>%headVerticalSite% - %txtContentRepository%</title>
        <script type="text/javascript" src="tinymce/jscripts/tiny_mce/cms_frames_popup.js">//</script>
        <link rel="stylesheet" type="text/css" href="css/admin.css"/>
      </head>
      <frameset id="contentFrameset" name="contentFrameset" cols="200, *" framespacing="6" frameborder="NO">
         <xsl:if test="contains($user-agent,'Gecko')">
           <xsl:attribute name="bordercolor">#eeeeee</xsl:attribute>
           <xsl:attribute name="frameborder">YES</xsl:attribute>
           <xsl:attribute name="border">6</xsl:attribute>
         </xsl:if>
        <frame class="leftframe" name="leftFrame" id="list" marginwidth="4" marginheight="0">
          <xsl:attribute name="src">
            <xsl:text>adminpage?page=200</xsl:text>
            <xsl:text>&amp;op=menu</xsl:text>
            <xsl:text>&amp;subop=</xsl:text>
            <xsl:value-of select="$subop"/>
            <xsl:text>&amp;fieldname=</xsl:text>
            <xsl:value-of select="$fieldname"/>
            <xsl:text>&amp;fieldrow=</xsl:text>
            <xsl:value-of select="$fieldrow"/>
            <xsl:text>&amp;selectedunitkey=</xsl:text>
            <xsl:value-of select="$selectedunitkey"/>
            <xsl:text>&amp;contenttypestring=</xsl:text>
            <xsl:value-of select="$contenttypestring"/>
            <xsl:text>&amp;unitfiltercontenttype=</xsl:text>
            <xsl:value-of select="$unitfiltercontenttype"/>
            <xsl:text>&amp;requirecategoryadmin=</xsl:text>
            <xsl:value-of select="$requirecategoryadmin"/>
            <xsl:text>&amp;excludecategorykey=</xsl:text>
            <xsl:value-of select="$excludecategorykey"/>
            <xsl:text>&amp;excludecategorykey_withchildren=</xsl:text>
            <xsl:value-of select="$excludecategorykey_withchildren"/>
            <xsl:text>&amp;contenthandler=</xsl:text>
            <xsl:value-of select="$contenthandler"/>
            <xsl:if test="$minoccurrence != ''">
              <xsl:text>&amp;minoccurrence=</xsl:text>
              <xsl:value-of select="$minoccurrence"/>
            </xsl:if>
            <xsl:if test="$maxoccurrence != ''">
              <xsl:text>&amp;maxoccurrence=</xsl:text>
              <xsl:value-of select="$maxoccurrence"/>
            </xsl:if>
          </xsl:attribute>
        </frame>
        <frame name="mainFrame" id="action" src="empty.html"/>
      </frameset>
    </html>
  </xsl:template>
</xsl:stylesheet>
