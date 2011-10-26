<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <!--xsl:include href="../../handlerconfigs/default.xsl"/-->

    <xsl:template match="category" mode="icon">
		
        <xsl:variable name="cattypestring">
            <xsl:choose>
                <xsl:when test="@handler">
                    <xsl:apply-templates select="@handler" mode="icon"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>folder</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
		
        <xsl:variable name="lockstring">
            <xsl:if test="not(@anonaccess = 'true')">_lock</xsl:if>
        </xsl:variable>
        
        <xsl:variable name="shadeicon">
            <xsl:apply-templates select="." mode="shadeicon"/>
        </xsl:variable>

        <xsl:variable name="shadestring">
            <xsl:if test="$shadeicon = 'true'">_shaded</xsl:if>
        </xsl:variable>
                   	
        <img src="images/icon_{$cattypestring}{$lockstring}{$shadestring}.gif" border="0"/>
		
        <xsl:text>icon_</xsl:text>
        <xsl:value-of select="$cattypestring"/>
        <xsl:value-of select="$lockstring"/>
        <xsl:value-of select="$shadestring"/>
    </xsl:template>
	
    <xsl:template match="category" mode="shadeicon">
        <xsl:choose>
            <xsl:when test="@disabled = 'true'">
                <xsl:text>true</xsl:text>
            </xsl:when>
            <xsl:when test="@useraccess = 'false'">
                <xsl:text>true</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>false</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
	
    <xsl:template match="category" mode="page">
        <xsl:choose>
            <xsl:when test="@contenttypekey">
                <xsl:value-of select="(@contenttypekey + 999)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>991</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
	
    <xsl:template match="category" mode="extraparams">
        <xsl:text>&amp;categorykey=</xsl:text>
        <xsl:value-of select="@key"/>
        <xsl:if test="@useraccess = 'false' or @disable = 'true'">
            <xsl:text>&amp;disabled=true</xsl:text>
        </xsl:if>
    </xsl:template>
	
    <xsl:template match="category" mode="loadurl">
		<xsl:if test="not(name(parent::node()) = name(.)) and @haschildren = 'true'">
			<xsl:text>javascript:loadTopCategory(</xsl:text>
			<xsl:value-of select="@key"/>
			<xsl:text>);</xsl:text>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="category" mode="tooltip">
        <xsl:value-of select="@name"/>
        <xsl:text> (%fldKey%:</xsl:text>
        <xsl:value-of select="@key"/>
        <xsl:text>)</xsl:text>
	</xsl:template>

</xsl:stylesheet>
