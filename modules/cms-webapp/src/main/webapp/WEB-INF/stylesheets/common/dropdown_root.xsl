<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:output method="html"/>

    <xsl:template name="dropdown_root">
	    <xsl:param name="label"/>
	    <xsl:param name="name"/>
	    <xsl:param name="selectedkey"/>
	    <xsl:param name="defaultkey"/>
	    <xsl:param name="selectnode"/>
	    <xsl:param name="colspan"/>
	    <xsl:param name="emptyrow"/>
		<xsl:param name="disabled" select="false()"/>
	
	    <td valign="baseline" nowrap="nowrap"><xsl:value-of select="$label"/></td>
	    <td nowrap="nowrap">
	      <select>
	         <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
			 <xsl:if test="$disabled">
				<xsl:attribute name="disabled">disabled</xsl:attribute>
			 </xsl:if>
	
	         <xsl:if test="$emptyrow!=''">
	         	<option value=""><xsl:value-of select="$emptyrow"/></option>
	         </xsl:if>
	
	         <xsl:for-each select="$selectnode">
				 <option>
					<xsl:if test="$create=0 and $selectedkey = .">
						<xsl:attribute name="selected">selected</xsl:attribute>
					</xsl:if>

					<xsl:if test="$create = 1 and string($defaultkey) = string(.)">
						<xsl:attribute name="selected">selected</xsl:attribute>
					</xsl:if>

					<xsl:attribute name="value"><xsl:value-of disable-output-escaping="yes" select="."/></xsl:attribute>
					<xsl:value-of select="."/>
				</option>
	         </xsl:for-each>
	      </select>
	    </td>
	</xsl:template>
    
    
</xsl:stylesheet>
