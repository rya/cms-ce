<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:template name="tablerowpainter">
    	<xsl:param name="usemouseover" select="true()"/>
    	<xsl:param name="firstclass"/>
    	<xsl:param name="secondclass"/>
    	<xsl:param name="mouseoverclass"/>
    	
    	<xsl:variable name="fclass">
    		<xsl:choose>
    			<xsl:when test="$firstclass"><xsl:value-of select="$firstclass"/></xsl:when>
    			<xsl:otherwise>
    				<xsl:text>tablerowpainter_darkrow</xsl:text>
    			</xsl:otherwise>
    		</xsl:choose>
    	</xsl:variable>
    	
    	<xsl:variable name="sclass">
    		<xsl:choose>
    			<xsl:when test="$secondclass">
    				<xsl:value-of select="$secondclass"/>
    			</xsl:when>
    			<xsl:when test="$firstclass">
    				<xsl:value-of select="$firstclass"/>
    			</xsl:when>
    			<xsl:otherwise>
    				<xsl:text>tablerowpainter_lightrow</xsl:text>
    			</xsl:otherwise>
    		</xsl:choose>
    	</xsl:variable>
    	
    	<xsl:variable name="mclass">
    		<xsl:choose>
    			<xsl:when test="$mouseoverclass">
    				<xsl:value-of select="$mouseoverclass"/>
    			</xsl:when>
    			<xsl:otherwise>
    				<xsl:text>tablerowpainter_mouseoverrow</xsl:text>
    			</xsl:otherwise>
    		</xsl:choose>
    	</xsl:variable>
        
        <xsl:choose>
            <xsl:when test="position() mod 2">
                <xsl:attribute name="class"><xsl:value-of select="$fclass"/></xsl:attribute>
                <xsl:if test="$usemouseover">
					<xsl:attribute name="onmouseover">javascript:this.className='<xsl:value-of select="$mclass"/>'</xsl:attribute>
					<xsl:attribute name="onmouseout">javascript:this.className='<xsl:value-of select="$fclass"/>'</xsl:attribute>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <xsl:attribute name="class">tablerowpainter_lightrow</xsl:attribute>
                <xsl:if test="$usemouseover">
	                <xsl:attribute name="onmouseover">javascript:this.className='<xsl:value-of select="$mclass"/>'</xsl:attribute>
    	            <xsl:attribute name="onmouseout">javascript:this.className='<xsl:value-of select="$sclass"/>'</xsl:attribute>
    	        </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


</xsl:stylesheet>