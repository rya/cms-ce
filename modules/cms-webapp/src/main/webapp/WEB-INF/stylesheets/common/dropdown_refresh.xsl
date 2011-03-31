<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>  
    
    <xsl:template name="dropdown_refresh">
        <xsl:param name="label" select="''"/>
        <xsl:param name="name"/>
        <xsl:param name="selectedkey"/>
        <xsl:param name="selectnode"/>
        <xsl:param name="colspan"/>
        <xsl:param name="emptyrow"/>
        <xsl:param name="required" select="'false'"/>
        <xsl:param name="disabled" select="false()"/>
        <xsl:param name="lefttdwidth" select="'none'"/>
        <xsl:param name="onchangefunction" select="''"/>

        <xsl:if test="$label != ''">
            <td class="form_labelcolumn" valign="baseline" nowrap="nowrap">
                <xsl:if test="$lefttdwidth != 'none'">
                    <xsl:attribute name="width">
                        <xsl:value-of select="$lefttdwidth"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:value-of select="$label" disable-output-escaping="yes"/>
                <xsl:if test="$required = 'true'">
                    <span class="requiredfield">*</span>
                </xsl:if>
            </td>
        </xsl:if>
        <td>
            <xsl:choose>
                <xsl:when test="not($disabled)">
                    <select>
                        <xsl:attribute name="onchange">
                            <xsl:value-of select="$onchangefunction"/>
                        </xsl:attribute>
                        
                        <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
                        
                        <xsl:if test="$emptyrow!=''">
                            <option value=""><xsl:value-of select="$emptyrow"/></option>
                        </xsl:if>
                        
                        <xsl:for-each select="$selectnode">
                            <option>
                                <xsl:if test="$selectedkey = @key">
                                    <xsl:attribute name="selected">selected</xsl:attribute>
                                </xsl:if>
                                <xsl:attribute name="value"><xsl:value-of disable-output-escaping="yes" select="@key"/></xsl:attribute>
                                <xsl:value-of select="name"/>
                            </option>
                        </xsl:for-each>
                    </select>
                </xsl:when>
                <xsl:otherwise>
                    <input type="hidden">
                        <xsl:attribute name="name">
                            <xsl:value-of select="$name"/>
                        </xsl:attribute>
                        <xsl:attribute name="value">
                            <xsl:value-of select="$selectedkey"/>
                        </xsl:attribute>
                    </input>
                    <xsl:value-of select="$selectnode[$selectedkey = @key]/name"/>
                </xsl:otherwise>
            </xsl:choose>
        </td>
    </xsl:template>
    
</xsl:stylesheet>