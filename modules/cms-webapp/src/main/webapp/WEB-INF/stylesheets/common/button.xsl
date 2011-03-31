<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:template name="button">
        <!--
        Enumeration of type:
        - button (built-in)
        - submit (built-in)
        - reset (built-in)
        - link (extra)

        Enumeration of text-alignment:
        - left
        - right
		-->

        <xsl:param name="type" select="'button'" />
        <xsl:param name="id" select="''" />
        <xsl:param name="name" select="''" />
        <xsl:param name="tooltip" select="''"/>
        <xsl:param name="caption" select="''" />
        <xsl:param name="text-alignment" select="'right'" />
        <xsl:param name="image" select="''" />
        <xsl:param name="image-disabled" select="''"/>
        <xsl:param name="href" select="''" />
        <xsl:param name="target" select="''" />
        <xsl:param name="onclick" select="''" />
        <xsl:param name="useOnClick" select="'true'" />
        <xsl:param name="hidden" select="'false'"/>
        <xsl:param name="disabled" select="'false'"/>
        <xsl:param name="style" select="'raised'"/>
        <xsl:param name="referer"/>

        <!-- The user can supply a condition, this must be true for the onclick event to be fired -->
        <xsl:param name="condition" select="''"/>

        <xsl:choose>
            <xsl:when test="$type = 'link'">
                <xsl:call-template name="button_link">
                    <xsl:with-param name="type" select="$type"/>
                    <xsl:with-param name="id" select="$id"/>
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="tooltip" select="$tooltip"/>
                    <xsl:with-param name="caption" select="$caption"/>
                    <xsl:with-param name="text-alignment" select="$text-alignment"/>
                    <xsl:with-param name="image" select="$image"/>
                    <xsl:with-param name="image-disabled" select="$image-disabled"/>
                    <xsl:with-param name="onclick" select="$onclick"/>
                    <xsl:with-param name="useOnClick" select="$useOnClick"/>
                    <xsl:with-param name="href" select="$href"/>
                    <xsl:with-param name="target" select="$target"/>
                    <xsl:with-param name="disabled" select="$disabled"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="condition" select="$condition"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$type = 'button' or $type = 'submit' or $type = 'reset'">
                <xsl:call-template name="button_button">
                    <xsl:with-param name="type" select="$type"/>
                    <xsl:with-param name="id" select="$id"/>
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="tooltip" select="$tooltip"/>
                    <xsl:with-param name="caption" select="$caption"/>
                    <xsl:with-param name="text-alignment" select="$text-alignment"/>
                    <xsl:with-param name="image" select="$image"/>
                    <xsl:with-param name="image-disabled" select="$image-disabled"/>
                    <xsl:with-param name="onclick" select="$onclick"/>
                    <xsl:with-param name="useOnClick" select="$useOnClick"/>
                    <xsl:with-param name="hidden" select="$hidden"/>
                    <xsl:with-param name="disabled" select="$disabled"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="condition" select="$condition"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$type = 'cancel'">
                <xsl:variable name="cancelcaption">
                    <xsl:choose>
                        <xsl:when test="not($caption = '')">
                            <xsl:value-of select="$caption"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>%cmdCancel%</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:call-template name="button_link">
                    <xsl:with-param name="href" select="$referer"/>
                    <xsl:with-param name="type" select="$type"/>
                    <xsl:with-param name="id" select="$id"/>
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="tooltip" select="$tooltip"/>
                    <xsl:with-param name="caption" select="$cancelcaption"/>
                    <xsl:with-param name="text-alignment" select="$text-alignment"/>
                    <xsl:with-param name="image" select="$image"/>
                    <xsl:with-param name="image-disabled" select="$image-disabled"/>
                    <xsl:with-param name="onclick" select="$onclick"/>
                    <xsl:with-param name="useOnClick" select="$useOnClick"/>
                    <xsl:with-param name="target" select="$target"/>
                    <xsl:with-param name="disabled" select="$disabled"/>
                    <xsl:with-param name="style" select="$style"/>
                    <xsl:with-param name="condition" select="$condition"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>Unknown button type!</xsl:text>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>

    <xsl:template name="button_link">
        <xsl:param name="type"/>
        <xsl:param name="id"/>
        <xsl:param name="name"/>
        <xsl:param name="tooltip"/>
        <xsl:param name="caption"/>
        <xsl:param name="text-alignment"/>
        <xsl:param name="image"/>
        <xsl:param name="image-disabled"/>
        <xsl:param name="href"/>
        <xsl:param name="target"/>
        <xsl:param name="onclick"/>
        <xsl:param name="useOnClick"/>
        <xsl:param name="disabled"/>
        <xsl:param name="style"/>
        <xsl:param name="condition"/>
        <a>
		<xsl:attribute name="class">
			<xsl:choose>
                <xsl:when test="$disabled = 'true'">
                    <xsl:text>button_link disabled</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>button_link</xsl:text>
                </xsl:otherwise>
			</xsl:choose>
			</xsl:attribute>
            <xsl:if test="$id != ''">
                <xsl:attribute name="id">
                    <xsl:value-of select="$id" />
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$href != '' and string($disabled) = 'false'">
                <xsl:attribute name="href">
                    <xsl:value-of select="$href"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$target != '' and string($disabled) = 'false'">
                <xsl:attribute name="target">
                    <xsl:value-of select="$target" />
                </xsl:attribute>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="$disabled = 'true'">
                    <xsl:attribute name="style">cursor: default; text-decoration:none</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="style">cursor: pointer;</xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>

            <xsl:variable name="overriding_onclick">
                <xsl:choose>
                    <xsl:when test="$useOnClick = 'false'">
                        <!-- omit onclick -->
                    </xsl:when>
                    <xsl:when test="$condition != '' and $disabled != 'true'">
					<xsl:text>javascript: if(</xsl:text>
					<xsl:value-of select="$condition"/>
					<xsl:text>)</xsl:text>
					<xsl:text> { </xsl:text>
					<xsl:choose>
						<xsl:when test="$target = '_blank'">
							<xsl:text>window.open("</xsl:text>
							<xsl:value-of select="$href"/>");
						</xsl:when>
						<xsl:otherwise>
							<!--
								Hack!!!
								Firefox < 2.0 is missing the click() method.
								https://bugzilla.mozilla.org/show_bug.cgi?id=148585
							-->
							<xsl:text>if( document.all) {</xsl:text>
							<xsl:text>this.parentNode.click();</xsl:text>
							<xsl:text> } else { document.location.href = this.parentNode.href; } return false;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:text> } else {</xsl:text>
					<xsl:text>return false;</xsl:text>
					<xsl:text>}</xsl:text>
					</xsl:when>
                    <xsl:otherwise>
						<!--xsl:text>javascript:this.parentNode.href = 'javascript:void(0)';</xsl:text-->
						<xsl:choose>
							<xsl:when test="$target = '_blank'">
								<xsl:text>javascript:this.parentNode.removeAttribute('target');this.parentNode.href='javascript:void(0)';window.open("</xsl:text>
								<xsl:value-of select="$href"/>");
							</xsl:when>
							<xsl:otherwise>
								<!--
									Hack!!!
									Firefox < 2.0 is missing the click() method.
									https://bugzilla.mozilla.org/show_bug.cgi?id=148585
								-->
								<xsl:text>javascript:if( document.all) {</xsl:text>
								<xsl:text>this.parentNode.click();</xsl:text>
								<xsl:text> } else { document.location.href = this.parentNode.href; } return false;</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:choose>
                <xsl:when test="$style = 'raised'">
                    <xsl:call-template name="button_raised">
                        <xsl:with-param name="tooltip" select="$tooltip"/>
                        <xsl:with-param name="disabled" select="$disabled"/>
                        <xsl:with-param name="type" select="'button'"/>
                        <xsl:with-param name="name" select="$name"/>
                        <xsl:with-param name="caption" select="$caption"/>
                        <xsl:with-param name="text-alignment" select="$text-alignment"/>
                        <xsl:with-param name="image" select="$image"/>
                        <xsl:with-param name="image-disabled" select="$image-disabled"/>
                        <xsl:with-param name="onclick" select="$overriding_onclick"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$style = 'flat'">

                    <xsl:variable name="onclick_1">
                        <xsl:if test="$condition != '' and $disabled != 'true'">
                            <xsl:text>javascript:</xsl:text>
                            <xsl:text>if (</xsl:text><xsl:value-of select="$condition"/><xsl:text> == false ) return false;</xsl:text>
                        </xsl:if>
                        <xsl:if test="$onclick != ''">
                            <xsl:if test="$condition = ''">
                                <xsl:text>javascript:</xsl:text>
                            </xsl:if>
                            <xsl:value-of select="$onclick"/>
                        </xsl:if>
                    </xsl:variable>

                    <xsl:if test="$onclick_1 != '' and $useOnClick = 'true'">
                        <xsl:attribute name="onclick">
                            <xsl:value-of select="$onclick_1" />
                        </xsl:attribute>
                    </xsl:if>

                    <xsl:call-template name="button_flat">
                        <xsl:with-param name="tooltip" select="$tooltip"/>
                        <xsl:with-param name="disabled" select="$disabled"/>
                        <xsl:with-param name="name" select="$name"/>
                        <xsl:with-param name="image" select="$image"/>
                        <xsl:with-param name="image-disabled" select="$image-disabled"/>
                        <xsl:with-param name="onclick" select="$overriding_onclick"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>Unknown button style: <xsl:value-of select="$style"/></xsl:otherwise>
            </xsl:choose>
        </a>
    </xsl:template>

    <xsl:template name="button_button">
        <xsl:param name="type"/>
        <xsl:param name="id"/>
        <xsl:param name="name"/>
        <xsl:param name="tooltip"/>
        <xsl:param name="caption"/>
        <xsl:param name="text-alignment"/>
        <xsl:param name="image"/>
        <xsl:param name="image-disabled"/>
        <xsl:param name="href"/>
        <xsl:param name="target"/>
        <xsl:param name="onclick"/>
        <xsl:param name="useOnClick"/>
        <xsl:param name="hidden" select="'false'"/>
        <xsl:param name="disabled"/>
        <xsl:param name="style"/>

        <xsl:choose>
            <xsl:when test="$style = 'raised'">
                <xsl:call-template name="button_raised">
                    <xsl:with-param name="tooltip" select="$tooltip"/>
                    <xsl:with-param name="hidden" select="$hidden"/>
                    <xsl:with-param name="disabled" select="$disabled"/>
                    <xsl:with-param name="type" select="$type"/>
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="id" select="$id"/>
                    <xsl:with-param name="caption" select="$caption"/>
                    <xsl:with-param name="text-alignment" select="$text-alignment"/>
                    <xsl:with-param name="image" select="$image"/>
                    <xsl:with-param name="image-disabled" select="$image-disabled"/>
                    <xsl:with-param name="onclick" select="$onclick"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$style = 'flat'">
                <xsl:text>button of type button with style flat is not implemented</xsl:text>
            </xsl:when>
            <xsl:otherwise>Unknown button style: <xsl:value-of select="$style"/></xsl:otherwise>
        </xsl:choose>
    </xsl:template>



    <!-- ************************ -->
    <!-- *** button_flat ****** -->
    <!-- ************************ -->
    <xsl:template name="button_flat">
        <xsl:param name="type"/>
        <xsl:param name="id"/>
        <xsl:param name="name"/>
        <xsl:param name="tooltip"/>
        <xsl:param name="caption"/>
        <xsl:param name="text-alignment"/>
        <xsl:param name="image"/>
        <xsl:param name="image-disabled"/>
        <xsl:param name="href"/>
        <xsl:param name="target"/>
        <xsl:param name="onclick"/>
        <xsl:param name="disabled"/>

        <xsl:variable name="image_1">
            <xsl:choose>
                <xsl:when test="$disabled = 'true' and $image-disabled != ''">
                    <xsl:value-of select="$image-disabled"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$image"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:if test="$image_1 != ''">
            <img src="{$image_1}" border="0">
                <xsl:if test="$name != ''">
                    <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
                </xsl:if>
                <xsl:if test="$disabled = 'true' and $image-disabled = ''">
                    <xsl:attribute name="style">filter: alpha(opacity=30); opacity: .3;</xsl:attribute>
                </xsl:if>
                <xsl:if test="$tooltip != ''">
                    <xsl:attribute name="title"><xsl:value-of select="$tooltip"/></xsl:attribute>
                </xsl:if>
            </img>
        </xsl:if>

    </xsl:template>

    <!-- ************************ -->
    <!-- *** button_raised ****** -->
    <!-- ************************ -->
    <xsl:template name="button_raised">
        <xsl:param name="tooltip"/>
        <xsl:param name="type" select="'button'"/>
        <xsl:param name="name"/>
        <xsl:param name="id"/>
        <xsl:param name="caption"/>
        <xsl:param name="text-alignment"/>
        <xsl:param name="image"/>
        <xsl:param name="image-disabled"/>
        <xsl:param name="onclick"/>
        <xsl:param name="hidden" select="'false'"/>
        <xsl:param name="disabled"/>

        <xsl:variable name="image_1">
            <xsl:choose>
                <xsl:when test="$disabled = 'true' and $image-disabled != ''">
                    <xsl:value-of select="$image-disabled"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$image"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <!--xsl:comment>
            <xsl:text>$hidden = </xsl:text>
            <xsl:value-of select="$hidden"/>
        </xsl:comment-->

        <button type="{$type}">
            <xsl:if test="$tooltip != ''">
                <xsl:attribute name="title"><xsl:value-of select="$tooltip"/></xsl:attribute>
            </xsl:if>

            <xsl:choose>
                <xsl:when test="$disabled = 'true'">
                    <xsl:attribute name="disabled">true</xsl:attribute>
                    <xsl:attribute name="style">cursor: default;</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="style">cursor: pointer;</xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>

            <xsl:choose>
                <xsl:when test="$hidden = 'true'">
                    <xsl:attribute name="class">button_text_hidden</xsl:attribute>
                </xsl:when>
                <xsl:when test="$caption != ''">
                    <xsl:attribute name="class">button_text</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="class">button_image_small</xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>

            <xsl:if test="$name != ''">
                <xsl:attribute name="name">
                    <xsl:value-of select="$name"/>
                </xsl:attribute>
                <xsl:attribute name="id">
                    <xsl:value-of select="$name"/>
                </xsl:attribute>
            </xsl:if>

            <xsl:if test="$onclick != ''">
                <xsl:attribute name="onclick">
                    <xsl:value-of select="$onclick"/>
                </xsl:attribute>
            </xsl:if>

            <xsl:if test="$text-alignment = 'left' and $caption != ''">
                <xsl:value-of select="$caption"/>
            </xsl:if>

            <xsl:if test="$image_1 != ''">
                <xsl:if test="$text-alignment = 'left' and $caption != ''">
                    <xsl:text> </xsl:text>
                </xsl:if>
                <img src="{$image_1}" border="0">
                	<xsl:if test="$disabled = 'true' and $image-disabled = ''">
                    	<xsl:attribute name="style">filter: alpha(opacity=30); opacity: .3</xsl:attribute>
                	</xsl:if>
                </img>
                <xsl:if test="$text-alignment = 'right' and $caption != ''">
                    <xsl:text> </xsl:text>
                </xsl:if>
            </xsl:if>

            <xsl:if test="$text-alignment = 'right' and $caption != ''">
                <xsl:value-of select="$caption"/>
            </xsl:if>
        </button>
    </xsl:template>


</xsl:stylesheet>
