<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:template name="contentfield">
        <xsl:param name="label" select="'%txtContent%:'"/>
    <xsl:param name="name" select="'contentkey'"/>
    <xsl:param name="index"/>
    <xsl:param name="selectedkey"/>
    <xsl:param name="selectnode"/>
        <xsl:param name="cssClass"/>
    <xsl:param name="contenttypekeys"/>

    <xsl:variable name="contenttypekeys_js">
      <xsl:choose>
        <xsl:when test="count($contenttypekeys) = 0">
          <xsl:text>null</xsl:text>
        </xsl:when>
        <xsl:when test="count($contenttypekeys) = 1">
          <xsl:value-of select="$contenttypekeys"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>new Array(</xsl:text>
          <xsl:for-each select="$contenttypekeys">
            <xsl:value-of select="."/>
            <xsl:if test="not(position() = last())">
              <xsl:text>,</xsl:text>
            </xsl:if>
          </xsl:for-each>
          <xsl:text>)</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <td class="form_labelcolumn" valign="baseline" nowrap="true">
      <xsl:value-of select="$label"/>
    </td>
    <td nowrap="nowrap" valign="middle">
			<input type="text" readonly="readonly" size="40">
				<xsl:attribute name="class">
          <xsl:text>textfield</xsl:text>
          <xsl:if test="$cssClass != ''">
            <xsl:value-of select="concat(' ', $cssClass)"/>
          </xsl:if>
        </xsl:attribute>
				<xsl:attribute name="name">view<xsl:value-of select="$name"/></xsl:attribute>
				<xsl:attribute name="id">view<xsl:value-of select="$name"/></xsl:attribute>
        <xsl:attribute name="value"><xsl:value-of select="$selectnode"/></xsl:attribute>
      </input>

      <xsl:call-template name="button">
        <xsl:with-param name="name">btn<xsl:value-of select="$name"/></xsl:with-param>
        <xsl:with-param name="image" select="'images/icon_browse.gif'"/>
        <xsl:with-param name="tooltip" select="'%cmdSelectContent%'"/>
        <xsl:with-param name="onclick">
          <xsl:text>javascript:OpenContentPopup(</xsl:text>
          <xsl:text>-1, -1, 'contentfield', '</xsl:text>
          <xsl:value-of select="$name"/>
          <xsl:text>', -1, </xsl:text>
          <xsl:value-of select="$contenttypekeys_js"/>
          <xsl:text>);</xsl:text>
        </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="button">
        <xsl:with-param name="name" select="concat('edit', $name)"/>
        <xsl:with-param name="image" select="'images/icon_edit_small.gif'"/>
        <xsl:with-param name="tooltip" select="'%cmdEdit%'"/>
				<xsl:with-param name="disabled" select="$selectedkey = ''"/>
        <xsl:with-param name="onclick">
          <xsl:text>OpenEditContentPopup(document.getElementById('</xsl:text>
          <xsl:value-of select="$name"/>
          <xsl:text>').value, -1,'</xsl:text>
          <xsl:value-of select="$name"/>
          <xsl:text>', -1, 'callback_contentfield');</xsl:text>
        </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="button">
        <xsl:with-param name="name">remove
          <xsl:value-of select="$name"/>
        </xsl:with-param>
        <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
        <xsl:with-param name="tooltip" select="'%cmdRemove%'"/>
				<xsl:with-param name="disabled" select="$selectedkey = ''"/>
        <xsl:with-param name="onclick">
          <xsl:text>menuItem_removeContent();</xsl:text>
          <!-- disable edit button -->
          <xsl:text>setImageButtonEnabled(document.getElementById('edit</xsl:text>
          <xsl:value-of select="$name"/>
          <xsl:text>'), false);</xsl:text>
          <!-- disable remove (this) button -->
          <xsl:text>setImageButtonEnabled(document.getElementById('remove</xsl:text>
          <xsl:value-of select="$name"/>
          <xsl:text>'), false);</xsl:text>
        </xsl:with-param>
      </xsl:call-template>
      <input type="hidden">
        <xsl:attribute name="name">
          <xsl:value-of select="$name"/>
        </xsl:attribute>
        <xsl:attribute name="id">
          <xsl:value-of select="$name"/>
        </xsl:attribute>
        <xsl:attribute name="value">
          <xsl:value-of select="$selectedkey"/>
        </xsl:attribute>
      </input>
    </td>
  </xsl:template>
</xsl:stylesheet>