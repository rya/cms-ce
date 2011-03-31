<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>
    
    <xsl:template name="dropdown_language">
        <xsl:param name="label"/>
        <xsl:param name="name"/>
        <xsl:param name="selectedkey"/>
        <xsl:param name="defaultkey"/>
        <xsl:param name="selectnode"/>
        <xsl:param name="colspan"/>
        <xsl:param name="emptyrow"/>
        
        <td valign="baseline"><xsl:value-of select="$label"/></td>
        <td>
            <select >
                <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
                
                <xsl:if test="$emptyrow!=''">
                    <option value=""><xsl:value-of select="$emptyrow"/></option>
                </xsl:if>
                
                <xsl:for-each select="$selectnode">
                    <option>
                        <xsl:if test="not($create = 1) and $selectedkey = @key">
                            <xsl:attribute name="selected">selected</xsl:attribute>
                        </xsl:if>
                        <xsl:if test="$create = 1 and $defaultkey = @key">
                            <xsl:attribute name="selected">selected</xsl:attribute>
                        </xsl:if>
                        <xsl:attribute name="value"><xsl:value-of disable-output-escaping="yes" select="@key"/></xsl:attribute>
                        <xsl:value-of select="."/>
                    </option>
                </xsl:for-each>
            </select>
        </td>
    </xsl:template>
    
</xsl:stylesheet>
