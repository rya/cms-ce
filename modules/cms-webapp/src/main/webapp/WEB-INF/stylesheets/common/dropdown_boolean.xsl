<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>  
    
    <xsl:template name="dropdown_boolean">
        <xsl:param name="label"/>
        <xsl:param name="name"/>
        <xsl:param name="id"/>
        <xsl:param name="selectedkey"/>
        <xsl:param name="colspan"/>
        <xsl:param name="onchange"/>
        
        <td class="form_labelcolumn" valign="baseline" nowrap="nowrap"><xsl:value-of select="$label"/>:</td>
        <td nowrap="nowrap">
            <select>
                <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
                <xsl:if test="$id !=''">
					<xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
				</xsl:if>
                <xsl:if test="$onchange">
                    <xsl:attribute name="onchange"><xsl:value-of select="$onchange"/></xsl:attribute>
                </xsl:if>
                <xsl:choose>
                    <xsl:when test="$selectedkey='true' or $selectedkey='yes'">
                        <option selected="selected" value="true">%optYes%</option>
                        <option value="false">%optNo%</option>
                    </xsl:when>
                    <xsl:otherwise>
                        <option value="true">%optYes%</option>
                        <option selected="selected" value="false">%optNo%</option>
                    </xsl:otherwise>
                </xsl:choose>
            </select>
        </td>
    </xsl:template>
    
</xsl:stylesheet>
