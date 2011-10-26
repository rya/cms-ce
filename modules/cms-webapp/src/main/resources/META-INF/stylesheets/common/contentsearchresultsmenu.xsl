<?xml version="1.0" encoding="utf-8"?>

<!DOCTYPE xsl:stylesheet [<!ENTITY nbsp "&#160;">]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">

  <xsl:template name="contentsearchresultsmenu">
    <xsl:param name="op"/>
    <xsl:param name="subop"/>
    <xsl:param name="fieldname"/>
    <xsl:param name="fieldrow"/>
    <xsl:param name="page"/>
    <xsl:param name="cat"/>
    <xsl:param name="selectedunitkey"/>
    <!-- Used by simple search -->
    <xsl:param name="searchtext"/>
    <xsl:param name="searchtype"/>
    <xsl:param name="scope"/>
    <xsl:param name="minoccurrence"/>
    <xsl:param name="maxoccurrence"/>
    <!-- Used by advanced search -->
    <!--
    	<xsl:param name="asearchtext"/>
    	<xsl:param name="ascope"/>
    	-->
    <xsl:param name="subcategories"/>
    <xsl:param name="state"/>
    <xsl:param name="owner"/>
    <xsl:param name="created.op"/>
    <xsl:param name="created"/>
    <xsl:param name="modifier"/>
    <xsl:param name="modified.op"/>
    <xsl:param name="modified"/>
    <xsl:param name="acontentkey"/>
    <xsl:param name="filter"/>
    <xsl:param name="contenthandler"/>

    <form id="formSearch" name="formSearch" method="get" action="adminpage" style="margin-bottom:0;">
      <input type="hidden" name="op" value="{$op}"/>
      <input type="hidden" name="subop" value="{$subop}"/>
      <input type="hidden" name="page" value="{$page}"/>
      <input type="hidden" name="cat" value="{$cat}"/>
      <input type="hidden" name="fieldname" value="{$fieldname}"/>
      <input type="hidden" name="fieldrow" value="{$fieldrow}"/>
      <input type="hidden" name="contenttypestring" value="{$contenttypestring}"/>
      <input type="hidden" name="selectedunitkey" value="{$selectedunitkey}"/>
      <input type="hidden" name="searchtype" value="simple"/>
      <input type="hidden" name="scope" value="title"/>
      <input type="hidden" name="waitscreen" value="true"/>
      <input type="hidden" name="contenthandler" value="{$contenthandler}"/>
      <input type="hidden" name="minoccurrence" value="{$minoccurrence}"/>
      <input type="hidden" name="maxoccurrence" value="{$maxoccurrence}"/>

      <xsl:text>&nbsp;</xsl:text>

      <!-- Search field -->
      <input type="text" name="searchtext" id="searchtext" size="12" style="height: 20px" value="{$searchtext}"/>

      <!-- Search button -->
      <xsl:call-template name="button">
        <xsl:with-param name="type" select="'submit'"/>
        <xsl:with-param name="caption" select="'%cmdQuickSearch%'"/>
        <xsl:with-param name="name" select="'search'"/>
      </xsl:call-template>

      <xsl:text>&nbsp;</xsl:text>

      <!-- Advanced search button -->
      <xsl:call-template name="button">
        <xsl:with-param name="type" select="'link'"/>
        <xsl:with-param name="caption" select="'%cmdSearchDotDotDot%'"/>
        <xsl:with-param name="href">
          <xsl:text>adminpage?page=</xsl:text>
          <xsl:value-of select="$page"/>
          <xsl:text>&amp;op=searchform</xsl:text>
          <xsl:text>&amp;subop=</xsl:text><xsl:value-of select="$subop"/>
          <xsl:text>&amp;cat=</xsl:text>
          <xsl:value-of select="$cat"/>
          <xsl:text>&amp;selectedunitkey=</xsl:text>
          <xsl:value-of select="$selectedunitkey"/>
          <xsl:text>&amp;fieldname=</xsl:text><xsl:value-of select="$fieldname"/>
          <xsl:text>&amp;fieldrow=</xsl:text><xsl:value-of select="$fieldrow"/>
          <xsl:text>&amp;contenttypestring=</xsl:text><xsl:value-of select="$contenttypestring"/>
          <xsl:text>&amp;contenthandler=</xsl:text><xsl:value-of select="$contenthandler"/>
          <xsl:text>&amp;searchtype=advanced</xsl:text>
        </xsl:with-param>
      </xsl:call-template>

      <xsl:text>&nbsp;</xsl:text>

      <!-- Create report button -->
      <xsl:call-template name="button">
        <xsl:with-param name="type" select="'link'"/>
        <xsl:with-param name="caption" select="'%cmdCreateReport%'"/>
        <xsl:with-param name="name" select="'report'"/>
        <xsl:with-param name="href">
          <xsl:text>adminpage?page=</xsl:text>
          <xsl:value-of select="$page"/>
          <xsl:text>&amp;op=report&amp;subop=form</xsl:text>
          <xsl:text>&amp;selectedunitkey=</xsl:text>
          <xsl:value-of select="$selectedunitkey"/>
          <xsl:text>&amp;cat=</xsl:text>
          <xsl:value-of select="$cat"/>
          <xsl:text>&amp;searchtype=</xsl:text>
          <xsl:value-of select="$searchtype"/>
          <xsl:text>&amp;searchtext=</xsl:text>
          <xsl:value-of select="$searchtext"/>
          <xsl:text>&amp;scope=</xsl:text>
          <xsl:value-of select="$scope"/>
        </xsl:with-param>
      </xsl:call-template>

    </form>

  </xsl:template>

</xsl:stylesheet>