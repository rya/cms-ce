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

    <xsl:template name="tablecolumnheader">

        <xsl:param name="width" />
        <xsl:param name="align" />

        <xsl:param name="sortable" select="'true'"/>
        <xsl:param name="caption" />
        <xsl:param name="pageURL" select="''"/>
        <xsl:param name="current-sortby" />
        <xsl:param name="current-sortby-direction" select="'ASC'"/>
        <xsl:param name="sortby" />
        <xsl:param name="asc_sign" select="'ascending'"/>
        <xsl:param name="desc_sign" select="'descending'"/>
        <xsl:param name="style"/>
        <xsl:param name="checkboxname"/>
        <xsl:param name="checkBoxOnClickFallback"/>

        <!-- Computing witch direction to sort next  -->
        <xsl:variable name="sortby-direction">
            <xsl:choose>
                <xsl:when test="$current-sortby = $sortby">
                    <xsl:choose>
                        <xsl:when test="$current-sortby-direction = $asc_sign"><xsl:value-of select="$desc_sign"/></xsl:when>
                        <xsl:otherwise><xsl:value-of select="$asc_sign"/></xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise><xsl:value-of select="$current-sortby-direction"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <!-- Computing whitch icon to show  -->
        <xsl:variable name="icon">
            <xsl:choose>
                <xsl:when test="$sortable = 'false'">images/icon_sortdirection_none.gif</xsl:when>
                <xsl:when test="$current-sortby = $sortby">
                    <xsl:choose>
                        <xsl:when test="$sortby-direction = $asc_sign">images/icon_sortdirection_descending.gif</xsl:when>
                        <xsl:otherwise>images/icon_sortdirection_ascending.gif</xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>images/icon_sortdirection_none.gif</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <td>
          <xsl:attribute name="class">
            <xsl:text>browsetablecolumnheader</xsl:text>
            <xsl:if test="$sortable = 'false'">
              <xsl:text> default-pointer</xsl:text>              
            </xsl:if>
          </xsl:attribute>
            <xsl:if test="not($checkboxname) and $sortable = 'true'">
                <xsl:attribute name="onclick">
                    <xsl:text>javascript:gotoLocation(this.getElementsByTagName('a')[0].href);</xsl:text>	
                </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="style">
                <xsl:if test="$sortable = 'false'">
                    <xsl:text>cursor: auto;</xsl:text>
                </xsl:if>
                <xsl:if test="$style">
                    <xsl:value-of select="$style"/>
                </xsl:if>
                <xsl:if test="$checkboxname">
                	<xsl:text>padding-left: 0px; padding-right: 1px; padding-top: 0px; padding-bottom: 1px;  align: center;</xsl:text>
                </xsl:if>
            </xsl:attribute>

            <xsl:attribute name="onmousedown"><xsl:if test="$sortable = 'true'">javascript:this.className='browsetablecolumnheader_pressed'</xsl:if></xsl:attribute>
            <xsl:attribute name="onmouseup"><xsl:if test="$sortable = 'true'">javascript:this.className='browsetablecolumnheader'</xsl:if></xsl:attribute>
            <xsl:attribute name="onmouseout"><xsl:if test="$sortable = 'true'">javascript:this.className='browsetablecolumnheader'</xsl:if></xsl:attribute>

            <xsl:if test="$width != ''">
                <xsl:attribute name="width">
                    <xsl:value-of select="$width" />
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$align != ''">
                <xsl:attribute name="align">
                    <xsl:value-of select="$align" />
                </xsl:attribute>
            </xsl:if>
            
            <xsl:if test="$checkboxname">
				<script>
					
					function checkbox_onclick( callBack ) 
					{
						var obj = document.getElementById('<xsl:value-of select="$checkboxname"/>_field');
						var img = document.getElementById('<xsl:value-of select="$checkboxname"/>_image');

            
            if (obj.value == 'false')
						{
							obj.value = 'true';
						}							
						else
						{
							obj.value = 'false';
						}
													
						var checked = (obj.value == 'true');

            setCheckboxValues('<xsl:value-of select="$checkboxname"/>', checked);
						
						if ( checked )
						{
							img.src = "images/checkbox_checked.gif";
						}							
						else
						{
							img.src = "images/checkbox_unchecked.gif";
						}

            if ( callBack )
            {
              callBack();
            }

            /*
            if (typeof setBatchButtonsEnabled != 'undefined') {
              setBatchButtonsEnabled();
            }
            */
          }
					
				</script>
				
				<a href="#" onclick="checkbox_onclick({$checkBoxOnClickFallback});" style="cursor: default;">
					<img id="{$checkboxname}_image" src="images/checkbox_unchecked.gif" border="0"/>
				</a>
				<input type="hidden" name="{$checkboxname}_field" id="{$checkboxname}_field" value="false"/>
            </xsl:if>

            <xsl:variable name="href">
                <xsl:if test="$sortable = 'true'">
                    <xsl:value-of select="$pageURL"/>
                    <xsl:text>&amp;sortby=</xsl:text>
                    <xsl:value-of select="$sortby"/>
                    <xsl:text>&amp;sortby-direction=</xsl:text><xsl:value-of select="$sortby-direction"/>
                </xsl:if>
            </xsl:variable>

            <xsl:if test="not($checkboxname)">
                <xsl:call-template name="tablecolumnheader_1">
                    <xsl:with-param name="caption" select="$caption"/>
                    <xsl:with-param name="href" select="$href"/>
                    <xsl:with-param name="image" select="$icon"/>
                </xsl:call-template>
            </xsl:if>
        </td>

    </xsl:template>

    <xsl:template name="tablecolumnheader_1">
        <xsl:param name="caption" select="''"/>
        <xsl:param name="image" select="''"/>
        <xsl:param name="href" select="''"/>

		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
			        <xsl:if test="$href != ''">
				            <a style="visibility: hidden">
				                <xsl:attribute name="href">
				                    <xsl:value-of select="$href"/>
				                </xsl:attribute>
				            </a>
			        </xsl:if>
				
			        <xsl:value-of select="$caption"/>
				</td>
				<td style="font-size:3px; padding-top:1px; padding-left:3px">	
			        <xsl:text> </xsl:text>
			        <xsl:if test="$href != '' or $caption = ''">
			           
				            <span align="right">
				                <img src="{$image}" align="center" alt="{$caption}" title="{$caption}"/>
				            </span>
			        </xsl:if>
				</td>
			</tr>
		</table>


    </xsl:template>

</xsl:stylesheet>