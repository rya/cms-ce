<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <xsl:template name="display-icon">
    <xsl:param name="filename"/>
    <xsl:param name="format"/>
    <xsl:param name="shaded" select="false()"/>
    <xsl:param name="title"/>

    <xsl:variable name="path" select="concat('images/fileicons/', $format)"/>

    <xsl:variable name="fname">
      <xsl:call-template name="getsuffix">
        <xsl:with-param name="fname" select="$filename"/>
      </xsl:call-template>
    </xsl:variable>

    <img border="0" width="32" height="32" align="absmiddle">
      <xsl:attribute name="src">
        <xsl:choose>
          <xsl:when test="$fname='zip' or $fname='gz' or $fname='tar' or $fname='arj' or $fname='rar'">
            <xsl:value-of select="concat($path, '/icon_file_zip')"/>
          </xsl:when>

          <xsl:when
              test="$fname='avi' or $fname='wmv' or $fname='mpeg' or $fname='mpg' or $fname='qt' or $fname='mov' or $fname='flv' or $fname='m4v' or $fname='divx'">
            <xsl:value-of select="concat($path, '/icon_file_video')"/>
          </xsl:when>

          <xsl:when test="$fname='ppt' or $fname='pps' or $fname='pptx' or $fname='potx' or $fname='pot' or $fname='keynote'">
            <xsl:value-of select="concat($path, '/icon_file_presentation')"/>
          </xsl:when>

          <xsl:when test="$fname='doc' or $fname='rtf' or $fname='docx' or $fname='dotx' or $fname='dot' or $fname='pages'">
            <xsl:value-of select="concat($path, '/icon_file_doc')"/>
          </xsl:when>

          <xsl:when test="$fname='xls' or $fname='xlsx' or $fname='numbers'">
            <xsl:value-of select="concat($path, '/icon_file_spreadsheet')"/>
          </xsl:when>

          <xsl:when test="$fname='swf' or $fname='fla'">
            <xsl:value-of select="concat($path, '/icon_file_flash')"/>
          </xsl:when>

          <xsl:when test="$fname='txt' or $fname='text' or $fname='js' or $fname='css'">
            <xsl:value-of select="concat($path, '/icon_file_txt')"/>
          </xsl:when>

          <xsl:when test="$fname='xsl' or $fname='xml'">
            <xsl:value-of select="concat($path, '/icon_file_xml')"/>
          </xsl:when>

          <xsl:when test="$fname='eps' or $fname='ai' or $fname='wmf' or $fname='emf'">
            <xsl:value-of select="concat($path, '/icon_file_vectorimage')"/>
          </xsl:when>

          <xsl:when test="$fname='pdf' or $fname='ps'">
            <xsl:value-of select="concat($path, '/icon_file_pdf')"/>
          </xsl:when>

          <xsl:when test="$fname='htm' or $fname='html'">
            <xsl:value-of select="concat($path, '/icon_file_html')"/>
          </xsl:when>

          <xsl:when test="$fname='exe' or $fname='app' or $fname='bat' or $fname='sh' or $fname='msi' or $fname='class'">
            <xsl:value-of select="concat($path, '/icon_file_executable')"/>
          </xsl:when>

          <xsl:when test="$fname='mp3' or $fname='wav' or $fname='mid' or $fname='midi' or $fname='av' or $fname='wma' or $fname='m4a'">
            <xsl:value-of select="concat($path, '/icon_file_sound')"/>
          </xsl:when>

          <xsl:otherwise>
            <xsl:value-of select="concat($path, '/icon_file_unknown')"/>
          </xsl:otherwise>
        </xsl:choose>

        <xsl:if test="$shaded">
          <xsl:text>_shaded</xsl:text>
        </xsl:if>

        <xsl:text>.gif</xsl:text>
      </xsl:attribute>

      <xsl:if test="$title != ''">
        <xsl:attribute name="title">
          <xsl:value-of select="$title"/>
        </xsl:attribute>
      </xsl:if>
    </img>
  </xsl:template>

</xsl:stylesheet>