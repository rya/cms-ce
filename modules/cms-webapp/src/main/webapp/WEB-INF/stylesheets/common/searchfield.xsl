<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:template name="searchfield">
        <xsl:param name="label" select="''"/>
        <xsl:param name="size"/>
        <xsl:param name="name"/>
        <xsl:param name="id"/>
        <xsl:param name="disabled" select="false()"/>
        <xsl:param name="selectedkey"/>
        <xsl:param name="selectnode"/>
        <xsl:param name="colspan"/>
        <xsl:param name="buttonfunction"/>
        <xsl:param name="removefunction" select="''"/>
        <xsl:param name="maxlength"/>
        <xsl:param name="onpropertychange" select="''"/>
        <xsl:param name="onchange" select="''"/>
        <xsl:param name="removebutton" select="true()"/>
        <xsl:param name="lefttdwidth" select="'none'"/>
        <xsl:param name="type" select="'textfield'"/>
        <xsl:param name="formname" select="''"/>
        <xsl:param name="required" select="'false'"/>
        <xsl:param name="helpelement" as="node()?"/>
        <xsl:param name="add-nowrap-on-label-column" select="'true'"/>

        <xsl:variable name="_formname">
          <xsl:choose>
            <xsl:when test="$formname != ''">
              <xsl:value-of select="$formname"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>formAdmin</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <xsl:if test="$label != ''">
              <xsl:call-template name="labelcolumn">
                <xsl:with-param name="width" select="$lefttdwidth"/>
                <xsl:with-param name="label" select="$label"/>
                <xsl:with-param name="required" select="$required"/>
                <xsl:with-param name="fieldname" select="$name"/>
                <xsl:with-param name="helpelement" select="$helpelement"/>
                <xsl:with-param name="nowrap" select="$add-nowrap-on-label-column"/>
              </xsl:call-template>
            </xsl:if>

        <td nowrap="nowrap">
            <xsl:attribute name="colspan"><xsl:value-of select="$colspan"/></xsl:attribute>

            <xsl:if test="$helpelement">
            	<xsl:call-template name="displayhelp">
            		<xsl:with-param name="fieldname" select="$name"/>
            		<xsl:with-param name="helpelement" select="$helpelement"/>
            	</xsl:call-template>
            </xsl:if>

            <xsl:choose>
                <xsl:when test="$type = 'textfield'">
                    <input type="text" readonly="readonly" class="textfield">
                        <xsl:attribute name="name">view<xsl:value-of select="$name"/></xsl:attribute>
                        <xsl:attribute name="id"><xsl:text>view</xsl:text>
                          <xsl:choose>
                            <xsl:when test="$id !=''">
                              <xsl:value-of select="$id"/>
                            </xsl:when>
                            <xsl:otherwise>
                              <xsl:value-of select="$name"/>
                            </xsl:otherwise>
                          </xsl:choose>
                        </xsl:attribute>
                      <xsl:attribute name="value"><xsl:value-of select="$selectnode"/></xsl:attribute>
                        <xsl:attribute name="size"><xsl:value-of select="$size"/></xsl:attribute>
                        <xsl:attribute name="maxlength"><xsl:value-of select="$maxlength"/></xsl:attribute>
                        <xsl:if test="$onchange != ''">
                            <xsl:attribute name="onchange"><xsl:value-of select="$onchange"/></xsl:attribute>
                        </xsl:if>
                      <xsl:if test="string-length($selectnode) &gt; 0">
                        <xsl:attribute name="title"><xsl:value-of select="$selectnode"/></xsl:attribute>
                      </xsl:if>
                    </input>
                </xsl:when>
                <xsl:when test="$type = 'span'">
                    <span name="{concat('view', $name)}" id="{concat('view', $name)}">
                    <xsl:copy-of select="$selectnode"/>
                    </span>
                    <xsl:text>&nbsp;</xsl:text>
                </xsl:when>
            </xsl:choose>

			<xsl:call-template name="button">
                <xsl:with-param name="name">btn<xsl:value-of select="$name"/></xsl:with-param>
                <xsl:with-param name="disabled" select="$disabled"/>
                <xsl:with-param name="image" select="'images/icon_browse.gif'"/>
              <xsl:with-param name="tooltip" select="'%cmdChoose%'"/>
                <xsl:with-param name="onclick"><xsl:value-of select="$buttonfunction"/></xsl:with-param>
            </xsl:call-template>
            <xsl:if test="$removebutton">
                <xsl:call-template name="button">
                    <xsl:with-param name="name">remove<xsl:value-of select="$name"/></xsl:with-param>
                    <xsl:with-param name="disabled" select="$disabled"/>
                    <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                    <xsl:with-param name="tooltip" select="'%cmdRemove%'"/>

                  <xsl:with-param name="onclick">
                        <xsl:choose>
                            <xsl:when test="$removefunction = ''">
                                <xsl:choose>
                                    <xsl:when test="$type = 'textfield'">
                                        <xsl:text>document.forms['</xsl:text><xsl:value-of select="$_formname"/><xsl:text>']['</xsl:text>
                                        <xsl:value-of select="$name"/>
                                        <xsl:text>'].value = ''; document.forms['</xsl:text><xsl:value-of select="$_formname"/><xsl:text>']['view</xsl:text>
                                        <xsl:value-of select="$name"/>
                                        <xsl:text>'].value = '';</xsl:text>

                                        <xsl:text>document.forms['</xsl:text><xsl:value-of select="$_formname"/><xsl:text>']['view</xsl:text>
                                        <xsl:value-of select="$name"/>
                                        <xsl:text>'].title = '';</xsl:text>
                                      
                    										<xsl:if test="$onchange !=''">
	                                          <xsl:text>document.forms['</xsl:text><xsl:value-of select="$_formname"/><xsl:text>']['</xsl:text>
	                                          <xsl:value-of select="$name"/>
	                                          <xsl:text>'].onchange();</xsl:text>
										                    </xsl:if>
                                    </xsl:when>
                                    <xsl:when test="$type = 'span'">
                                        document.getElementById('<xsl:value-of select="concat('view', $name)"/>').innerHTML = '';
                                        document.forms['<xsl:value-of select="$_formname"/>']['<xsl:value-of select="$name"/>'].value = '';
                                    </xsl:when>
                                </xsl:choose>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$removefunction"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
            <input type="hidden">
                <xsl:attribute name="id">
                  <xsl:choose>
                    <xsl:when test="$id !=''">
                      <xsl:value-of select="$id"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="$name"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:attribute>
                <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
                <xsl:attribute name="value"><xsl:value-of select="$selectedkey"/></xsl:attribute>
				<xsl:if test="$onchange != ''">
					<xsl:attribute name="onchange"><xsl:value-of select="$onchange"/></xsl:attribute>
				</xsl:if>
            </input>
        </td>
    </xsl:template>
</xsl:stylesheet>