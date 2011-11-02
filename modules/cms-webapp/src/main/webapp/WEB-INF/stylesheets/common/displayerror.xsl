<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:template name="displayerror">
    <xsl:param name="code"/>
    <xsl:param name="error"/>

    <p style="margin: 0.6em 0">
      <span class="requiredfield">
        <img src="images/form-error.png" style="vertical-align:middle"/>
        <xsl:text> </xsl:text>
        <xsl:if test="$code">
          <xsl:call-template name="errorcodes">
            <xsl:with-param name="code" select="$code"/>
          </xsl:call-template>
          <xsl:text>: </xsl:text>
        </xsl:if>
        <xsl:if test="$error">
          <xsl:for-each select="$error/part">
            <xsl:value-of select="."/>
            <xsl:if test="position() != last()">
              <xsl:text>, </xsl:text>
            </xsl:if>
          </xsl:for-each>
        </xsl:if>
      </span>
    </p>

  </xsl:template>
  
  <xsl:template name="errorcodes">
    <xsl:param name="code"/>
    
    <xsl:choose>
      <xsl:when test="$code = '1'">%errorKeyInUse%</xsl:when>
      <xsl:when test="$code = '2'">%errorXMLParsing%</xsl:when>
      <xsl:when test="$code = '3'">%errorTooLow%</xsl:when>
      <xsl:when test="$code = '4'">%errorEmailInUse%</xsl:when>
      <xsl:when test="$code = '5'">%errorUIDInUse%</xsl:when>
      <xsl:when test="$code = '6'">%errorFieldRequired%</xsl:when>
      <xsl:when test="$code = '7'">%errorNameInUse%</xsl:when>
      <xsl:when test="$code = '8'">%errorMissingIncludes%</xsl:when>
      <xsl:when test="$code = '9'">%errorValidateXSL%</xsl:when>
      <xsl:when test="$code = '10'">%errorAtLeastOneFileRequired%</xsl:when>
      <xsl:when test="$code = '11'">%errorIncorrectFileType%</xsl:when>
      <xsl:when test="$code = '12'">%errorFailedToInflateZipFile%</xsl:when>
      <xsl:when test="$code = '13'">%errorIncorrectInterval%</xsl:when>
      <xsl:when test="$code = '14'">%errorIncorrectCount%</xsl:when>
      <xsl:when test="$code = '15'">%errorIncorrectMinutes%</xsl:when>
      <xsl:when test="$code = '16'">%errorIncorrectTime%</xsl:when>
      <xsl:when test="$code = '17'">%errorIncorrectExpression%</xsl:when>
      <xsl:when test="$code = '18'">%errorHomeNotSelected%</xsl:when>
      <xsl:when test="$code = '19'">%errorNameIllegalChars%</xsl:when>
      <xsl:when test="$code = '20'">%errorConnector%</xsl:when>
    </xsl:choose>
    
  </xsl:template>

</xsl:stylesheet>
