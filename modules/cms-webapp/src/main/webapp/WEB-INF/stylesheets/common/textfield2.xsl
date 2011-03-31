<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:template name="textfield2">
      <xsl:param name="label" select="''"/>
      <xsl:param name="prefix"/>
      <xsl:param name="size1"/>
      <xsl:param name="maxlength1"/>
      <xsl:param name="name1"/>
      <xsl:param name="selectnode1"/>
      <xsl:param name="separator"/>
      <xsl:param name="size2"/>
      <xsl:param name="maxlength2"/>
      <xsl:param name="name2"/>
      <xsl:param name="selectnode2"/>
      <xsl:param name="postfix"/>
      <xsl:param name="colspan" select="''"/>
      <xsl:param name="disabled"/>
      <xsl:param name="onblur"/>
      <xsl:param name="onchange"/>
      <xsl:param name="onkeyup"/>
      <xsl:param name="onpropertychange"/>
      <xsl:param name="required" select="'false'"/>
      <xsl:param name="readonly" select="false()"/>
      <xsl:param name="align"/>
      <xsl:param name="lefttdwidth" select="'none'"/>
      <xsl:param name="helpelement"/>
            
            <xsl:if test="$label != ''">
              <xsl:call-template name="labelcolumn">
                <xsl:with-param name="width" select="$lefttdwidth"/>
                <xsl:with-param name="label" select="$label"/>
                <xsl:with-param name="required" select="$required"/>
                <xsl:with-param name="fieldname" select="$name1"/>
                <xsl:with-param name="helpelement" select="$helpelement"/>
              </xsl:call-template>
            </xsl:if>

            <td nowrap="nowrap" valign="top">
                <xsl:if test="$colspan != ''">
                    <xsl:attribute name="colspan">
                        <xsl:value-of select="$colspan"/>
                    </xsl:attribute>
                </xsl:if>
                
                <xsl:variable name="errors">
                    <xsl:choose>
                        <xsl:when test="/*/errors">
                            <xsl:copy-of select="/*/errors"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:copy-of select="/*/*/errors"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                
                <xsl:if test="$helpelement">
                	<xsl:call-template name="displayhelp">
                		<xsl:with-param name="fieldname" select="$name1"/>
                		<xsl:with-param name="helpelement" select="$helpelement"/>
                	</xsl:call-template>
                </xsl:if>
                
                <xsl:if test="exslt-common:node-set($errors)/errors/error[@name = $name1]">
                    <xsl:call-template name="displayerror">
                        <xsl:with-param name="code" select="exslt-common:node-set($errors)/errors/error[@name = $name1]/@code"/>
                    </xsl:call-template>
                </xsl:if>
                
                <xsl:if test="exslt-common:node-set($errors)/errors/error[@name = $name2]">
                    <xsl:call-template name="displayerror">
                        <xsl:with-param name="code" select="exslt-common:node-set($errors)/errors/error[@name = $name2]/@code"/>
                    </xsl:call-template>
                </xsl:if>
                
                <xsl:if test="$prefix">
                  <xsl:value-of select="$prefix"/>
                </xsl:if>

                <input type="text" class="textfield">
                    <xsl:if test="$align != ''">
                        <xsl:attribute name="style">
                            <xsl:text>text-align:</xsl:text>
                            <xsl:value-of select="$align"/>
                        </xsl:attribute>
                    </xsl:if>

                    <xsl:attribute name="name">
                        <xsl:value-of select="$name1"/>
                    </xsl:attribute>

                    <xsl:attribute name="id">
                        <xsl:value-of select="$name1"/>
                    </xsl:attribute>

                    <xsl:attribute name="value">
                    	<xsl:choose>
                          <xsl:when test="/*/errors/error[@name = $name1]/value">
                            <xsl:value-of select="/*/errors/error[@name = $name1]/value" disable-output-escaping="yes"/>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:value-of select="$selectnode1"/>
                          </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>

                    <xsl:attribute name="size">
                        <xsl:value-of select="$size1"/>
                    </xsl:attribute>

                    <xsl:attribute name="maxlength">
                        <xsl:value-of select="$maxlength1"/>
                    </xsl:attribute>

                    <xsl:if test="$onblur != ''">
                        <xsl:attribute name="onblur">
                            <xsl:value-of select="$onblur"/>
                        </xsl:attribute>
                    </xsl:if>

                    <xsl:if test="$onchange != ''">
                        <xsl:attribute name="onchange">
                            <xsl:value-of select="$onchange"/>
                        </xsl:attribute>
                    </xsl:if>

                    <xsl:if test="$onkeyup != ''">
                        <xsl:attribute name="onkeyup">
                            <xsl:value-of select="$onkeyup"/>
                        </xsl:attribute>
                    </xsl:if>

                    <xsl:if test="$onpropertychange != ''">
                        <xsl:attribute name="onpropertychange">
                            <xsl:value-of select="$onpropertychange"/>
                        </xsl:attribute>
                    </xsl:if>

                    <xsl:if test="$disabled = 'true'">
                        <xsl:attribute name="disabled">disabled</xsl:attribute>
                    </xsl:if>

                    <xsl:if test="$readonly">
                        <xsl:attribute name="readonly">readonly</xsl:attribute>
                    </xsl:if>
                </input>

                <xsl:if test="$separator">
                    <xsl:value-of select="$separator"/>
                </xsl:if>

                <input type="text" class="textfield">
                    <xsl:if test="$align != ''">
                        <xsl:attribute name="style">
                            <xsl:text>text-align:</xsl:text>
                            <xsl:value-of select="$align"/>
                        </xsl:attribute>
                    </xsl:if>

                    <xsl:attribute name="name">
                        <xsl:value-of select="$name2"/>
                    </xsl:attribute>

                    <xsl:attribute name="id">
                        <xsl:value-of select="$name2"/>
                    </xsl:attribute>

                    <xsl:attribute name="value">
                    	<xsl:choose>
                          <xsl:when test="/*/errors/error[@name = $name2]/value">
                            <xsl:value-of select="/*/errors/error[@name = $name2]/value" disable-output-escaping="yes"/>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:value-of select="$selectnode2"/>
                          </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>

                    <xsl:attribute name="size">
                        <xsl:value-of select="$size2"/>
                    </xsl:attribute>

                    <xsl:attribute name="maxlength">
                        <xsl:value-of select="$maxlength2"/>
                    </xsl:attribute>

                    <xsl:if test="$onblur != ''">
                        <xsl:attribute name="onblur">
                            <xsl:value-of select="$onblur"/>
                        </xsl:attribute>
                    </xsl:if>

                    <xsl:if test="$onchange != ''">
                        <xsl:attribute name="onchange">
                            <xsl:value-of select="$onchange"/>
                        </xsl:attribute>
                    </xsl:if>

                    <xsl:if test="$onpropertychange != ''">
                        <xsl:attribute name="onpropertychange">
                            <xsl:value-of select="$onpropertychange"/>
                        </xsl:attribute>
                    </xsl:if>

                    <xsl:if test="$disabled = 'true'">
                        <xsl:attribute name="disabled">disabled</xsl:attribute>
                    </xsl:if>

                    <xsl:if test="$readonly">
                        <xsl:attribute name="readonly">readonly</xsl:attribute>
                    </xsl:if>
                </input>

                <xsl:if test="$postfix">
                    <xsl:value-of select="$postfix"/>
                </xsl:if>

            </td>
        </xsl:template>
</xsl:stylesheet>