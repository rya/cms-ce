<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <xsl:template name="textarea">
    <xsl:param name="label"/>
    <xsl:param name="name"/>
    <xsl:param name="id"/>
    <xsl:param name="class"/>
    <xsl:param name="selectnode"/>
    <xsl:param name="colspan"/>
    <xsl:param name="rows"/>
    <xsl:param name="width" select="''"/>
    <xsl:param name="cols"/>
    <xsl:param name="withoutlabel" select="'false'"/>
    <xsl:param name="lefttdwidth" select="'none'"/>
    <xsl:param name="disable-output-escaping" select="false()"/>
    <xsl:param name="required" select="'false'"/>
    <xsl:param name="helpelement"/>
    <xsl:param name="disabled" select="false()"/>
    <xsl:param name="wrap"/>
    <xsl:param name="xml" select="'false'"/>
    <xsl:param name="default" select="''"/>
    <xsl:param name="readonly" select="false()"/>
    <xsl:param name="wrapWithDiv" select="'false'"/>
    <xsl:param name="useIcon" select="false()"/>
    <xsl:param name="iconClass" select="''"/>
    <xsl:param name="iconText" select="''"/>

    <!-- Firefox only -->
    <xsl:param name="spellcheck" select="'true'"/>

    <xsl:if test="$withoutlabel != 'true'">
      <xsl:call-template name="labelcolumn">
        <xsl:with-param name="width" select="$lefttdwidth"/>
        <xsl:with-param name="label" select="$label"/>
        <xsl:with-param name="required" select="$required"/>
        <xsl:with-param name="fieldname" select="$name"/>
        <xsl:with-param name="helpelement" select="$helpelement"/>
        <xsl:with-param name="useIcon" select="$useIcon"/>
        <xsl:with-param name="iconClass" select="$iconClass"/>
        <xsl:with-param name="iconText" select="$iconText"/>
        <xsl:with-param name="valign" select="'top'"/>
      </xsl:call-template>
    </xsl:if>
   
    <td nowrap="nowrap" valign="top">
      <xsl:if test="$colspan != ''">
        <xsl:attribute name="colspan">
          <xsl:value-of select="$colspan"/>
        </xsl:attribute>
      </xsl:if>
     
      <xsl:variable name="errors">
        <xsl:choose>
          <xsl:when test="/*/errors">
            <xsl:copy-of select="/*/errors"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:copy-of select="/*/*/errors"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
     
      <xsl:if test="$helpelement">
        <xsl:call-template name="displayhelp">
          <xsl:with-param name="fieldname" select="$name"/>
          <xsl:with-param name="helpelement" select="$helpelement"/>
        </xsl:call-template>
      </xsl:if>
                
      <xsl:if test="exslt-common:node-set($errors)/errors/error[@name = $name]">
        <xsl:call-template name="displayerror">
          <xsl:with-param name="code" select="exslt-common:node-set($errors)/errors/error[@name = $name]/@code"/>
          <xsl:with-param name="error" select="exslt-common:node-set($errors)/errors/error[@name = $name]"/>
        </xsl:call-template>
      </xsl:if>

      <xsl:if test="$wrapWithDiv = 'true'">
        <xsl:text disable-output-escaping="yes">
          &lt;div style="background-color: #fff; border:1px solid #aeaeae;"&gt;
        </xsl:text>
      </xsl:if>

      <textarea>
        <xsl:if test="$wrap">
          <xsl:attribute name="wrap"><xsl:value-of select="$wrap"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="$disabled">
          <xsl:attribute name="disabled">disabled</xsl:attribute>
        </xsl:if>
        <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
        <xsl:if test="$id !=''">
          <xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="$class !=''">
          <xsl:attribute name="class"><xsl:value-of select="$class"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="$readonly">
          <xsl:attribute name="readonly">true</xsl:attribute>
        </xsl:if>
        <xsl:attribute name="rows"><xsl:value-of select="$rows"/></xsl:attribute>
        <xsl:attribute name="cols"><xsl:value-of select="$cols"/></xsl:attribute>

        <xsl:if test="$width != ''">
          <xsl:attribute name="style">width: <xsl:value-of select="$width"/></xsl:attribute>
        </xsl:if>

        <!-- Firefox only -->
        <xsl:attribute name="spellcheck">
          <xsl:value-of select="$spellcheck"/>
        </xsl:attribute>

        <xsl:choose>
          <xsl:when test="$xml = 'true'">
            <xsl:call-template name="serialize">
              <xsl:with-param name="xpath" select="$selectnode"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="/*/errors/error[@name=$name]/value">
            <xsl:value-of select="/*/errors/error[@name=$name]/value" disable-output-escaping="yes"/>
          </xsl:when>
          <xsl:when test="$xml = 'false' and $default !='' and not($selectnode)">
            <xsl:value-of select="$default"/>
          </xsl:when>
          <xsl:when test="$disable-output-escaping">
            <xsl:value-of select="$selectnode" disable-output-escaping="yes"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$selectnode"/>
          </xsl:otherwise>
        </xsl:choose>

      </textarea>

      <xsl:if test="$wrapWithDiv = 'true'">
        <xsl:text disable-output-escaping="yes">
          &lt;/div&gt;
        </xsl:text>
      </xsl:if>

    </td>
  </xsl:template>

</xsl:stylesheet>
