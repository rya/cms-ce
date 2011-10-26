<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:template match="menuitem" mode="icon">

		<xsl:variable name="typestring">
			<xsl:text>menuitem</xsl:text>
			<xsl:choose>
				<xsl:when test="@type = 'shortcut'">
					<xsl:text>_shortcut</xsl:text>
				</xsl:when>
				<xsl:when test="@type = 'section'">
					<xsl:text>_section</xsl:text>
				</xsl:when>
				<xsl:when test="(@type = 'sectionpage' and not (page/@pagetemplatetype)) or page/@pagetemplatetype='6'">
					<xsl:text>_sectionpage</xsl:text>
				</xsl:when>
        <xsl:when test="@type = 'newsletter'">
					<xsl:text>_sectionpage</xsl:text>
				</xsl:when>
				<xsl:when test="@type = 'label'">
					<xsl:text>_label</xsl:text>
				</xsl:when>
				<xsl:when test="@type = 'url'">
					<xsl:text>_url</xsl:text>
				</xsl:when>
				<xsl:when test="(@type = 'content' and not(page/@pagetemplatetype)) or page/@pagetemplatetype='5'">
					<xsl:text>_content</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>_standard</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="inmenustring">
			<xsl:choose>
				<xsl:when test="@visible = 'true' or @visible = 'yes'">_show</xsl:when>
				<xsl:otherwise/>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="lockstring">
			<xsl:choose>
				<xsl:when test="@anonread = 'false'">_lock</xsl:when>
				<xsl:when test="@anonread = 'true'"/>
				<xsl:when test="not(boolean(accessrights/accessright[@grouptype = 7]))">_lock</xsl:when>
				<xsl:otherwise/>
			</xsl:choose>
        </xsl:variable>

		<xsl:variable name="shadeicon">
        	<xsl:apply-templates select="." mode="shadeicon"/>
        </xsl:variable>

		<xsl:variable name="shadestring">
			<xsl:if test="$shadeicon = 'true'">_shaded</xsl:if>
		</xsl:variable>

		<xsl:text>icon_</xsl:text>
		<xsl:value-of select="$typestring"/>
		<xsl:value-of select="$inmenustring"/>
		<xsl:value-of select="$lockstring"/>
		<xsl:value-of select="$shadestring"/>
	</xsl:template>

	<xsl:template match="menuitem" mode="shadeicon" priority="1.0">
		<xsl:variable name="xpathUserright" select="accessrights/userright"/>

		<xsl:choose>
			<xsl:when test="@userread = 'true' or @useradd = 'true' or @useradministrate = 'true' or @usercreate = 'true' or @userupdate = 'true' or @userpublish = 'true'">
				<xsl:text>false</xsl:text>
			</xsl:when>
			<xsl:when test="not(@userread) and (not($xpathUserright) or ($xpathUserright/@update = 'true' or $xpathUserright/@delete = 'true' or $xpathUserright/@create = 'true' or $xpathUserright/@administrate = 'true' or $xpathUserright/@add = 'true'))">
				<xsl:text>false</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>true</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="menuitem" mode="page">
		<xsl:text>850</xsl:text>
	</xsl:template>

	<xsl:template match="menuitem" mode="extraparams">
		<xsl:text>&amp;parentmi=</xsl:text>
		<xsl:value-of select="@key"/>
	</xsl:template>

	<xsl:template match="menuitem" mode="sortchildren">
		<xsl:text>@order</xsl:text>
	</xsl:template>

	<xsl:template match="menuitem" mode="sortchildrentype">
		<xsl:text>number</xsl:text>
	</xsl:template>

  <xsl:template match="menuitem" mode="tooltip">
    <xsl:value-of select="@displayname"/>
    <xsl:text> </xsl:text>
    <xsl:text>(%fldKey%:</xsl:text>
    <xsl:value-of select="@key"/>
    <xsl:text>)</xsl:text>
  </xsl:template>

</xsl:stylesheet>
