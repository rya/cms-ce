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
    
    <xsl:template name="dropdown_date">
        <xsl:param name="label"/>
        <xsl:param name="name"/>
        <xsl:param name="selectnode" select="''"/>
        <xsl:param name="currentdate" select="''"/>
        <xsl:param name="colspan"/>
        <xsl:param name="emptyrow"/>
        <xsl:param name="onchange"/>
        <xsl:param name="required" select="'false'"/>
        <xsl:param name="buttoncaption" select="''"/>
        <xsl:param name="buttonfunction" select="''"/>
        <xsl:param name="disabled" select="false()"/>
        
        <td valign="baseline" nowrap="nowrap" class="form_labelcolumn">
            <xsl:value-of select="$label"/>
            <xsl:if test="$required = 'true'">
                <span class="requiredfield">*</span>
            </xsl:if>
        </td>
        <td nowrap="nowrap">
            <select>
                <xsl:attribute name="name">
                  <xsl:value-of select="$name"/>
                  <xsl:text>_month</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="id">
                  <xsl:value-of select="$name"/>
                  <xsl:text>_month</xsl:text>
                </xsl:attribute>

                <xsl:if test="$onchange != ''">
                    <xsl:attribute name="onchange"><xsl:value-of select="$onchange"/></xsl:attribute>
                </xsl:if>

                <xsl:if test="$disabled">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>

                <xsl:variable name="month">
                  <xsl:value-of select="substring($selectnode, 6, 2)"/>
                </xsl:variable>

                <xsl:if test="$emptyrow!=''">
                    <option value=""><xsl:value-of select="$emptyrow"/></option>
                </xsl:if>

                <option>
                  <xsl:if test="number($month) = 1">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">1</xsl:attribute>
                  <xsl:text>%monthJanuary%</xsl:text>
                </option>

                <option>
                  <xsl:if test="number($month) = 2">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">2</xsl:attribute>
                  <xsl:text>%monthFebruary%</xsl:text>
                </option>

                <option>
                  <xsl:if test="number($month) = 3">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">3</xsl:attribute>
                  <xsl:text>%monthMarch%</xsl:text>
                </option>

                <option>
                  <xsl:if test="number($month) = 4">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">4</xsl:attribute>
                  <xsl:text>%monthApril%</xsl:text>
                </option>

                <option>
                  <xsl:if test="number($month) = 5">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">5</xsl:attribute>
                  <xsl:text>%monthMay%</xsl:text>
                </option>

                <option>
                  <xsl:if test="number($month) = 6">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">6</xsl:attribute>
                  <xsl:text>%monthJune%</xsl:text>
                </option>

                <option>
                  <xsl:if test="number($month) = 7">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">7</xsl:attribute>
                  <xsl:text>%monthJuly%</xsl:text>
                </option>

                <option>
                  <xsl:if test="number($month) = 8">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">8</xsl:attribute>
                  <xsl:text>%monthAugust%</xsl:text>
                </option>

                <option>
                  <xsl:if test="number($month) = 9">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">9</xsl:attribute>
                  <xsl:text>%monthSeptember%</xsl:text>
                </option>

                <option>
                  <xsl:if test="number($month) = 10">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">10</xsl:attribute>
                  <xsl:text>%monthOctober%</xsl:text>
                </option>

                <option>
                  <xsl:if test="number($month) = 11">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">11</xsl:attribute>
                  <xsl:text>%monthNovember%</xsl:text>
                </option>

                <option>
                  <xsl:if test="number($month) = 12">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">12</xsl:attribute>
                  <xsl:text>%monthDecember%</xsl:text>
                </option>
            </select>

            <xsl:text>&nbsp;/&nbsp;</xsl:text>

            <select>
                <xsl:attribute name="name">
                  <xsl:value-of select="$name"/>
                  <xsl:text>_year</xsl:text>
                </xsl:attribute>
                <xsl:attribute name="id">
                  <xsl:value-of select="$name"/>
                  <xsl:text>_year</xsl:text>
                </xsl:attribute>
                
                <xsl:if test="$onchange != ''">
                    <xsl:attribute name="onchange"><xsl:value-of select="$onchange"/></xsl:attribute>
                </xsl:if>
                
                <xsl:if test="$disabled">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>

                <xsl:variable name="year">
                  <xsl:value-of select="substring($selectnode, 1, 4)"/>
                </xsl:variable>

                <xsl:variable name="currentyear">
                  <xsl:value-of select="substring($currentdate, 1, 4)"/>
                </xsl:variable>

                <xsl:if test="$emptyrow!=''">
                    <option value=""><xsl:value-of select="$emptyrow"/></option>
                </xsl:if>

                <option>
                  <xsl:if test="number($year) = number($currentyear) - 5">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">
                    <xsl:value-of select="number($currentyear) - 5"/>
                  </xsl:attribute>
                  <xsl:value-of select="number($currentyear) - 5"/>
                </option>

                <option>
                  <xsl:if test="number($year) = number($currentyear) - 4">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">
                    <xsl:value-of select="number($currentyear) - 4"/>
                  </xsl:attribute>
                  <xsl:value-of select="number($currentyear) - 4"/>
                </option>

                <option>
                  <xsl:if test="number($year) = number($currentyear) - 3">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">
                    <xsl:value-of select="number($currentyear) - 3"/>
                  </xsl:attribute>
                  <xsl:value-of select="number($currentyear) - 3"/>
                </option>

                <option>
                  <xsl:if test="number($year) = number($currentyear) - 2">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">
                    <xsl:value-of select="number($currentyear) - 2"/>
                  </xsl:attribute>
                  <xsl:value-of select="number($currentyear) - 2"/>
                </option>

                <option>
                  <xsl:if test="number($year) = number($currentyear) - 1">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">
                    <xsl:value-of select="number($currentyear) - 1"/>
                  </xsl:attribute>
                  <xsl:value-of select="number($currentyear) - 1"/>
                </option>

                <option>
                  <xsl:if test="number($year) = number($currentyear)">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">
                    <xsl:value-of select="number($currentyear)"/>
                  </xsl:attribute>
                  <xsl:value-of select="number($currentyear)"/>
                </option>

                <option>
                  <xsl:if test="number($year) = number($currentyear) + 1">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">
                    <xsl:value-of select="number($currentyear) + 1"/>
                  </xsl:attribute>
                  <xsl:value-of select="number($currentyear) + 1"/>
                </option>

                <option>
                  <xsl:if test="number($year) = number($currentyear) + 2">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">
                    <xsl:value-of select="number($currentyear) + 2"/>
                  </xsl:attribute>
                  <xsl:value-of select="number($currentyear) + 2"/>
                </option>

                <option>
                  <xsl:if test="number($year) = number($currentyear) + 3">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">
                    <xsl:value-of select="number($currentyear) + 3"/>
                  </xsl:attribute>
                  <xsl:value-of select="number($currentyear) + 3"/>
                </option>

                <option>
                  <xsl:if test="number($year) = number($currentyear) + 4">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">
                    <xsl:value-of select="number($currentyear) + 4"/>
                  </xsl:attribute>
                  <xsl:value-of select="number($currentyear) + 4"/>
                </option>

                <option>
                  <xsl:if test="number($year) = number($currentyear) + 5">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                  </xsl:if>
                  <xsl:attribute name="value">
                    <xsl:value-of select="number($currentyear) + 5"/>
                  </xsl:attribute>
                  <xsl:value-of select="number($currentyear) + 5"/>
                </option>
            </select>

            <input type="hidden" value="1">
              <xsl:attribute name="name">
                <xsl:value-of select="$name"/>
                <xsl:text>_date</xsl:text>
              </xsl:attribute>
            </input>

            <xsl:if test="$buttoncaption != ''">
                <xsl:text>&nbsp;</xsl:text>
                <xsl:call-template name="button">
                    <xsl:with-param name="type" select="'button'"/>
                    <xsl:with-param name="caption" select="$buttoncaption"/>
                    <xsl:with-param name="name" select="'dropdownbtn'"/>
                    <xsl:with-param name="onclick" select="$buttonfunction"/>
                    <xsl:with-param name="disabled" select="$disabled"/>
                </xsl:call-template>
            </xsl:if>
        </td>
    </xsl:template>
    
</xsl:stylesheet>
