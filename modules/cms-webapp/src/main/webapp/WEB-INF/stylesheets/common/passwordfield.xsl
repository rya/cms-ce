<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <xsl:template name="passwordfield">
    <xsl:param name="label"/>
    <xsl:param name="size"/>
    <xsl:param name="id"/>
    <xsl:param name="name"/>
    <xsl:param name="selectnode"/>
    <xsl:param name="colspan"/>
    <xsl:param name="maxlength"/>
    <xsl:param name="required" select="false"/>
    <xsl:param name="tdwidth"/>
    <xsl:param name="disableAutoComplete" select="false()"/>
    <xsl:param name="useIcon" select="false()"/>
    <xsl:param name="iconClass" select="''"/>
    <xsl:param name="iconText" select="''"/>
    
    <td valign="baseline">
      <xsl:if test="$tdwidth">
      	<xsl:attribute name="width">
      		<xsl:value-of select="$tdwidth"/>
      	</xsl:attribute>
      </xsl:if>
      <div style="float: left">
        <xsl:value-of select="$label"/>
        <xsl:if test="$required = 'true'">
          <span class="requiredfield">*</span>
        </xsl:if>
      </div>

      <xsl:if test="$useIcon">
        <div style="float:right">
          <span class="{$iconClass}">
            <xsl:if test="$iconText !=''">
              <xsl:attribute name="title">
                <xsl:value-of select="$iconText"/>
              </xsl:attribute>
            </xsl:if>
          </span>
        </div>
      </xsl:if>


    </td>
    <td>
      <xsl:attribute name="colspan"><xsl:value-of select="$colspan"/></xsl:attribute>

      <input type="password">
        <xsl:if test="$disableAutoComplete = true()">
          <xsl:attribute name="autocomplete">off</xsl:attribute>
        </xsl:if>
        <xsl:if test="$id">
          <xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
        </xsl:if>
        <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
        <xsl:attribute name="value"><xsl:value-of select="$selectnode"/></xsl:attribute>
        <xsl:attribute name="size"><xsl:value-of select="$size"/></xsl:attribute>
        <xsl:attribute name="maxlength"><xsl:value-of select="$maxlength"/></xsl:attribute>
      </input>
    </td>
  </xsl:template>
  
</xsl:stylesheet>
