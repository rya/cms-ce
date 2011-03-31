<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:template name="textfield">
    <xsl:param name="label" select="''" />
    <xsl:param name="size"/>
    <xsl:param name="name"/>
    <xsl:param name="id"/>
    <xsl:param name="selectnode"/>
    <xsl:param name="colspan" select="''"/>
    <xsl:param name="maxlength"/>
    <xsl:param name="disabled"/>
    <xsl:param name="onblur"/>
    <xsl:param name="onchange"/>
    <xsl:param name="onpropertychange"/>
    <xsl:param name="onfocus"/>
    <xsl:param name="onclick"/>
    <xsl:param name="onkeyup"/>
    <xsl:param name="required" select="'false'"/>
    <xsl:param name="readonly" select="false()"/>
    <xsl:param name="align"/>
    <xsl:param name="lefttdwidth" select="'none'"/>
    <xsl:param name="postfield"/>
    <xsl:param name="helpelement"/>
    <xsl:param name="disableAutoComplete" select="false()"/>
    <xsl:param name="useIcon" select="false()"/>
    <xsl:param name="iconClass" select="''"/>
    <xsl:param name="iconText" select="''"/>
    <xsl:param name="lock" select="false()"/>
    <xsl:param name="lock-enabled" select="true()"/>
    <xsl:param name="lock-tooltip" select="''"/>
    <xsl:param name="lock-click-callback" select="''"/>
    <xsl:param name="extra-css-class" select="''"/>

    <xsl:variable name="_id">
      <xsl:choose>
        <xsl:when test="$id !=''">
          <xsl:value-of select="$id"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$name"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:if test="$label != ''">
      <xsl:call-template name="labelcolumn">
        <xsl:with-param name="width" select="$lefttdwidth"/>
        <xsl:with-param name="label" select="$label"/>
        <xsl:with-param name="required" select="$required"/>
        <xsl:with-param name="hide-required" select="$lock and string-length($selectnode) = 0"/>
        <xsl:with-param name="fieldname" select="$name"/>
        <xsl:with-param name="helpelement" select="$helpelement"/>
        <xsl:with-param name="useIcon" select="$useIcon"/>
        <xsl:with-param name="iconClass" select="$iconClass"/>
        <xsl:with-param name="iconText" select="$iconText"/>
        <xsl:with-param name="valign" select="'middle'"/>
      </xsl:call-template>
    </xsl:if>

    <td nowrap="nowrap" valign="top">
      <xsl:if test="$colspan != ''">
        <xsl:attribute name="colspan">
          <xsl:value-of select="$colspan"/>
        </xsl:attribute>
      </xsl:if>
      <table border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td>

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
                <xsl:with-param name="fieldname" select="$name"/>
                <xsl:with-param name="helpelement" select="$helpelement"/>
              </xsl:call-template>
            </xsl:if>
                
            <xsl:if test="exslt-common:node-set($errors)/errors/error[@name=$name]">
              <xsl:call-template name="displayerror">
                <xsl:with-param name="code" select="exslt-common:node-set($errors)/errors/error[@name=$name]/@code"/>
              </xsl:call-template>
            </xsl:if>
            <input type="text">
              <xsl:attribute name="class">
                <xsl:text>textfield</xsl:text>
                <xsl:if test="$extra-css-class != ''">
                  <xsl:value-of select="concat(' ', $extra-css-class)"/>
                </xsl:if>
              </xsl:attribute>

              <xsl:if test="$disableAutoComplete = true()">
                <xsl:attribute name="autocomplete">off</xsl:attribute>
              </xsl:if>
              <xsl:if test="$align != ''">
                <xsl:attribute name="style">
                  <xsl:text>text-align:</xsl:text>
                  <xsl:value-of select="$align"/>
                </xsl:attribute>
              </xsl:if>
              <xsl:attribute name="name">
                <xsl:value-of select="$name"/>
              </xsl:attribute>
              <xsl:attribute name="id">
                <xsl:value-of select="$_id"/>
              </xsl:attribute>
              <xsl:attribute name="value">
                <xsl:choose>
                  <xsl:when test="/*/errors/error[@name=$name]/value">
                    <xsl:value-of select="/*/errors/error[@name=$name]/value" disable-output-escaping="yes"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="$selectnode"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:attribute>
              <xsl:attribute name="size">
                <xsl:value-of select="$size"/>
              </xsl:attribute>
              <xsl:attribute name="maxlength">
                <xsl:value-of select="$maxlength"/>
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
              <xsl:if test="$onfocus != ''">
                <xsl:attribute name="onfocus">
                  <xsl:value-of select="$onfocus"/>
                </xsl:attribute>
              </xsl:if>
              <xsl:if test="$onpropertychange != ''">
                <xsl:attribute name="onpropertychange">
                  <xsl:value-of select="$onpropertychange"/>
                </xsl:attribute>
              </xsl:if>
              <xsl:if test="$onkeyup != ''">
                <xsl:attribute name="onkeyup">
                  <xsl:value-of select="$onkeyup"/>
                </xsl:attribute>
              </xsl:if>
              <xsl:if test="$disabled = 'true'">
                <xsl:attribute name="disabled">disabled</xsl:attribute>
              </xsl:if>
              <xsl:if test="$readonly">
                <xsl:attribute name="readonly">readonly</xsl:attribute>
              </xsl:if>
            </input>
            <xsl:if test="$postfield">
              <xsl:value-of select="$postfield"/>
            </xsl:if>
          </td>
          <xsl:if test="$lock = true()">
            <td valign="bottom">
              <div>
                <xsl:choose>
                  <xsl:when test="$lock-enabled">
                    <img id="{concat($_id, '_lock_icon')}" width="16" height="16" alt="%cmdEdit%" class="hand"
                         style="vertical-align: bottom">
                      <xsl:if test="string-length($lock-tooltip) &gt; 0">
                        <xsl:attribute name="title">
                          <xsl:value-of select="$lock-tooltip"/>
                        </xsl:attribute>
                      </xsl:if>
                      <xsl:attribute name="onclick">
                        <xsl:text>admin_lockUnlockTextInput(this,</xsl:text>
                        <xsl:text>'</xsl:text><xsl:value-of select="$_id"/><xsl:text>'</xsl:text>
                        <xsl:text>, 'readonly'</xsl:text>
                        <xsl:if test="string-length($lock-click-callback) &gt; 0">
                          <xsl:text>,</xsl:text>
                          <xsl:value-of select="$lock-click-callback"/>
                        </xsl:if>
                        <xsl:text>);</xsl:text>
                      </xsl:attribute>
                      <xsl:attribute name="src">
                        <xsl:choose>
                          <xsl:when test="$disabled = 'true' or $readonly = true()">
                            <xsl:text>images/icon_lock_closed.png</xsl:text>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:text>images/icon_lock_open.png</xsl:text>
                          </xsl:otherwise>
                        </xsl:choose>
                      </xsl:attribute>
                    </img>
                  </xsl:when>
                  <xsl:otherwise>
                    <img src="images/icon_lock_closed.png" id="{concat($_id, '_lock_icon')}" alt="{$lock-tooltip}" title="{$lock-tooltip}" width="16" height="16" style="vertical-align: bottom"/>
                  </xsl:otherwise>
                </xsl:choose>
              </div>
            </td>
          </xsl:if>
        </tr>
      </table>
    </td>
  </xsl:template>
</xsl:stylesheet>
