<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>
    
  <xsl:template name="dropdown">
    <xsl:param name="label"/>
    <xsl:param name="name"/>
    <xsl:param name="id"/>
    <xsl:param name="selectedkey"/>
    <xsl:param name="defaultkey"/>
    <xsl:param name="selectnode"/>
    <xsl:param name="colspan"/>
    <xsl:param name="emptyrow"/>
    <xsl:param name="onchangefunction"/>
    <xsl:param name="required" select="'false'"/>
    <xsl:param name="buttoncaption" select="''"/>
    <xsl:param name="buttonfunction" select="''"/>
    <xsl:param name="buttonhref" select="''"/>
    <xsl:param name="buttondisabled" select="false()"/>
    <xsl:param name="disabled" select="false()"/>
    <xsl:param name="sort-by-node-name" as="xs:string?" select="'.'"/> <!-- String '@some-name' -->
    <xsl:param name="sort-order" as="xs:string?" select="'ascending'"/>

    <td nowrap="nowrap" class="form_labelcolumn">
      <xsl:value-of select="$label"/>
      <xsl:if test="$required = 'true'">
        <span class="requiredfield">*</span>
      </xsl:if>
    </td>
    <td nowrap="nowrap">
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
       		
      <xsl:if test="exslt-common:node-set($errors)/errors/error[@name=$name]">
        <xsl:call-template name="displayerror">
          <xsl:with-param name="code" select="exslt-common:node-set($errors)/errors/error[@name=$name]/@code"/>
        </xsl:call-template>
      </xsl:if>
        
      <select>
        <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
        <xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
        <xsl:if test="$onchangefunction != ''">
          <xsl:attribute name="onchange"><xsl:value-of select="$onchangefunction"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="$disabled">
          <xsl:attribute name="disabled">disabled</xsl:attribute>
        </xsl:if>
                
        <xsl:if test="$emptyrow!=''">
          <option value=""><xsl:value-of select="$emptyrow"/></option>
        </xsl:if>
                
        <xsl:for-each select="$selectnode">
            <xsl:sort select="saxon:evaluate($sort-by-node-name)" order="{$sort-order}"/>

          <option>
            <xsl:choose>
              <xsl:when test="$selectedkey = @key">
                <xsl:attribute name="selected">selected</xsl:attribute>
              </xsl:when>
              <xsl:when test="$create = 1 and string($defaultkey) = string(@key)">
                <xsl:attribute name="selected">selected</xsl:attribute>
              </xsl:when>
            </xsl:choose>
                        
            <xsl:attribute name="value"><xsl:value-of disable-output-escaping="yes" select="@key"/></xsl:attribute>
            <xsl:choose>
              <xsl:when test="@name">
                <xsl:value-of select="@name"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="name"/>
              </xsl:otherwise>
            </xsl:choose>
          </option>
        </xsl:for-each>
      </select>
      <xsl:value-of select="$buttonhref"/>
      <xsl:choose>
        <xsl:when test="$buttoncaption != '' and $buttonhref != ''">
          <xsl:text>&nbsp;</xsl:text>
          <xsl:call-template name="button">
            <xsl:with-param name="type" select="'link'"/>
            <xsl:with-param name="caption" select="$buttoncaption"/>
            <xsl:with-param name="name" select="concat('dropdownbtn_', $name)"/>
            <xsl:with-param name="href" select="$buttonhref"/>
            <xsl:with-param name="target" select="'_top_'"/>
            <xsl:with-param name="disabled" select="$disabled or $buttondisabled"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="$buttoncaption != ''">
          <xsl:text>&nbsp;</xsl:text>
          <xsl:call-template name="button">
            <xsl:with-param name="type" select="'button'"/>
            <xsl:with-param name="caption" select="$buttoncaption"/>
            <xsl:with-param name="name" select="concat('dropdownbtn_', $name)"/>
            <xsl:with-param name="onclick" select="$buttonfunction"/>
            <xsl:with-param name="disabled" select="$disabled or $buttondisabled"/>
          </xsl:call-template>
        </xsl:when>
      </xsl:choose>
    </td>
  </xsl:template>

</xsl:stylesheet>
