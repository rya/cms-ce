<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <xsl:param name="fieldname"/>
  <xsl:param name="fieldrow"/>
  <xsl:param name="contenttypestring"/>
  <xsl:param name="minoccurrence"/>
  <xsl:param name="maxoccurrence"/>
  <xsl:param name="selectedtabpage"/>
  <xsl:param name="modulename"/>

  <xsl:template name="generalhiddenfields">

    <input type="hidden" name="category_key">
      <xsl:attribute name="value">
        <xsl:choose>
          <xsl:when test="boolean($cat) and $create = '1'">
            <xsl:value-of select="$cat"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="/contents/content/categoryname/@key"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
    </input>

    <input type="hidden" name="selectedunitkey" value="{$selectedunitkey}"/>
    <input type="hidden" name="fieldname" value="{$fieldname}"/>
    <input type="hidden" name="fieldrow" value="{$fieldrow}"/>
    <input type="hidden" name="minoccurrence" value="{$minoccurrence}"/>
    <input type="hidden" name="maxoccurrence" value="{$maxoccurrence}"/>
    <input type="hidden" name="contenttypestring" value="{$contenttypestring}"/>
    <input type="hidden" name="subop" value="{$subop}"/>
    <input type="hidden" name="selectedtabpage" value=""/>
    <input type="hidden" name="referer" value="{$referer}"/>
    <input type="hidden" name="formdisabled" value="{not($enableform)}"/>

    <xsl:if test="$editlockedversionmode=true()">
      <input type="hidden" name="editlockedversionmode" value="true"/>
    </xsl:if>

    <xsl:if test="$create=0">
      <input type="hidden" name="key">
        <xsl:attribute name="value"><xsl:value-of select="contents/content/@key"/></xsl:attribute>
      </input>
      <input type="hidden" name="versionkey">
        <xsl:attribute name="value">
          <xsl:value-of select="/contents/content/@versionkey"/>
        </xsl:attribute>
      </input>
      <input type="hidden" name="contenttypekey">
        <xsl:attribute name="value"><xsl:value-of select="contents/content/@contenttypekey"/></xsl:attribute>
      </input>
      <input type="hidden" name="unitkey">
        <xsl:attribute name="value"><xsl:value-of select="contents/content/ownerunit/@key"/></xsl:attribute>
      </input>
      <input type="hidden" name="metakey">
        <xsl:attribute name="value"><xsl:value-of select="contents/content/@metakey"/></xsl:attribute>
      </input>
      <input type="hidden" name="views">
        <xsl:attribute name="value"><xsl:value-of select="contents/content/views"/></xsl:attribute>
      </input>
      <input type="hidden" name="publisheruid">
        <xsl:attribute name="value"><xsl:value-of select="/contents/content/publisheruid"/></xsl:attribute>
      </input>
      <input type="hidden" name="approveruid">
        <xsl:attribute name="value"><xsl:value-of select="/contents/content/approveruid"/></xsl:attribute>
      </input>
    </xsl:if>

    <xsl:if test="$create=1">
      <input type="hidden" name="contenttypekey">
        <xsl:attribute name="value"><xsl:value-of select="$contenttypekey"/></xsl:attribute>
      </input>
      <input type="hidden" name="unitkey">
        <xsl:attribute name="value"><xsl:value-of select="$selectedunitkey"/></xsl:attribute>
      </input>
    </xsl:if>

  </xsl:template>

</xsl:stylesheet>