<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <xsl:template name="enhancedimageselector">
    <xsl:param name="scalemax" select="230"/>
    <xsl:param name="label"/>
    <xsl:param name="name"/>
    <xsl:param name="selectedkey"/>
    <xsl:param name="width"/>
    <xsl:param name="height"/>
    <xsl:param name="colspan"/>
    <xsl:param name="imagetype"/>
    <xsl:param name="required" select="false()"/>
    <xsl:param name="helpelement"/>
    <xsl:param name="disabled" select="false()"/>

    <xsl:variable name="choosefunction">
      <xsl:text>javascript: OpenSelectorWindowImageE( this, 1045, &apos;</xsl:text>
      <xsl:value-of select="$imagetype"/>
      <xsl:text>&apos;, &apos;</xsl:text>
      <xsl:value-of select="$name"/>
      <xsl:text>&apos;, 990, 620 )</xsl:text>
    </xsl:variable>

    <xsl:variable name="deletefunction">
      <xsl:text>javascript: removeImageE( this, '</xsl:text>
      <xsl:value-of select="$name"/>
      <xsl:text>' );</xsl:text>
    </xsl:variable>

      
    <xsl:if test="not($disabled)">
			<script type="text/javascript" language="JavaScript">
				
				function addRelatedImage(fieldName, fieldRow, content_key, binary_key) {
          var divTag = document.getElementsByName('div'+fieldName)[fieldRow];
					var keyField = document.getElementsByName(fieldName)[fieldRow];
					
					divTag.innerHTML = "&lt;img src=\"_image/" + content_key + "/label/source?_filter=scalemax(<xsl:value-of select="$scalemax"/>)\">";
					keyField.value = content_key;
				}
				
				function OpenSelectorWindowImageE( obj, page, type, keyname, width, height ) {
          var idx = getObjectIndex(obj);
					OpenContentPopupByHandler(-1, -1, 'relatedimage', keyname, idx, "com.enonic.vertical.adminweb.handlers.ContentEnhancedImageHandlerServlet");
				}
	
				function removeImageE(obj, fieldName ) {
					var row = getObjectIndex(obj);
					window.document.getElementsByName(fieldName)[row].value = "";
					window.document.getElementsByName("div" + fieldName)[row].innerHTML = "";
				}
				
      </script>
    </xsl:if>

    <xsl:if test="$label != ''">
      <xsl:call-template name="labelcolumn">
        <xsl:with-param name="label" select="$label"/>
        <xsl:with-param name="required" select="$required"/>
        <xsl:with-param name="fieldname" select="$name"/>
        <xsl:with-param name="helpelement" select="$helpelement"/>
      </xsl:call-template>
    </xsl:if>
    <td>
      <xsl:attribute name="colspan"><xsl:value-of select="$colspan"/></xsl:attribute>
            
      <xsl:if test="$helpelement">
        <xsl:call-template name="displayhelp">
          <xsl:with-param name="fieldname" select="$name"/>
          <xsl:with-param name="helpelement" select="$helpelement"/>
        </xsl:call-template>
      </xsl:if>
            
      <table cellspacing="0" cellpadding="0" name="{$name}table" id="{$name}table">
        <tr>
          <td valign="top">
            <div id="div{$name}" name="div{$name}">

              <xsl:if test="$selectedkey">
                <img src="{concat('_image/', $selectedkey, '/label/source?_filter=scalemax(', $scalemax, ')')}">
                  <xsl:if test="$disabled">
                    <xsl:attribute name="class">
                      <xsl:text>disabled-element</xsl:text>
                    </xsl:attribute>
                  </xsl:if>
                </img>
              </xsl:if>
              
            </div>
          </td>
          <td valign="top">
            <xsl:attribute name="colspan"><xsl:value-of select="$colspan"/></xsl:attribute>
            <input type="hidden" name="{$name}" value="{$selectedkey}"/>

            <xsl:call-template name="button">
              <xsl:with-param name="type" select="'button'"/>
              <xsl:with-param name="caption" select="'%cmdSelectImage%'"/>
              <xsl:with-param name="name">
                <xsl:text>btnchoose</xsl:text><xsl:value-of select="$name"/>
              </xsl:with-param>
              <xsl:with-param name="onclick">
                <xsl:value-of select="$choosefunction"/>
              </xsl:with-param>
              <xsl:with-param name="disabled" select="$disabled"/>
            </xsl:call-template>
            <br/>
            <xsl:call-template name="button">
              <xsl:with-param name="type" select="'button'"/>
              <xsl:with-param name="caption" select="'%cmdRemoveImage%'"/>
              <xsl:with-param name="name">
                <xsl:text>btndelete</xsl:text><xsl:value-of select="$name"/>
              </xsl:with-param>
              <xsl:with-param name="onclick">
                <xsl:value-of select="$deletefunction"/>
              </xsl:with-param>
              <xsl:with-param name="disabled" select="$disabled"/>
            </xsl:call-template>
          </td>
        </tr>

      </table>
    </td>
  </xsl:template>
</xsl:stylesheet>
