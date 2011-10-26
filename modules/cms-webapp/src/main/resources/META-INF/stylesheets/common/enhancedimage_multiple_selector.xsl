<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <xsl:template name="enhancedmultipleimageselector">
    <xsl:param name="scalemax" select="230"/>
    <xsl:param name="label"/>
    <xsl:param name="name"/>
    <xsl:param name="selected"/>
    <xsl:param name="colspan"/>
    <xsl:param name="imagetype"/>
    <xsl:param name="contenttest" select="'false'"/>
    <xsl:param name="required" select="false()"/>
    <xsl:param name="helpelement"/>
    <xsl:param name="disabled" select="false()"/>

    <xsl:variable name="choosefunction">
      <xsl:text>javascript: OpenSelectorWindowImageEM( 1045, &apos;</xsl:text>
      <xsl:value-of select="$imagetype"/>
      <xsl:text>&apos;, &apos;</xsl:text>
      <xsl:value-of select="$name"/>
      <xsl:text>&apos;, 990, 620, this )</xsl:text>
    </xsl:variable>

    <xsl:variable name="deletefunction">
      <xsl:text>javascript: removeImageEM( '</xsl:text>
      <xsl:value-of select="$name"/>
      <xsl:text>', this ); removeRow('</xsl:text>
      <xsl:value-of select="$name"/><xsl:text>table', this);</xsl:text>
    </xsl:variable>

    <xsl:if test="not($disabled)">
      <script type="text/javascript" language="JavaScript">
        /*
          Method: addRelatedImages
        */
        function addRelatedImages(fieldName, fieldRow, content_key, binary_key) {
					var divTag;
					var keyField;
					
					if (fieldRow == 'none') {
						divTag = document.getElementById('div'+fieldName);
						keyField = document.getElementById(fieldName);
					} else {
						divTag = document.getElementsByName('div'+fieldName)[fieldRow];
						keyField = document.getElementsByName(fieldName)[fieldRow];
					}
					
					divTag.innerHTML = "&lt;img name='img"+ fieldName + "' src=\"_image/" + content_key + "/label/source?_filter=scalemax(<xsl:value-of select="$scalemax"/>)\">";
					keyField.value = content_key;
				}
        
        /*
          Method: OpenSelectorWindowImageEM
        */
        function OpenSelectorWindowImageEM( page, type, keyname, width, height, object ) {
					var idx = getObjectIndex(object);
					OpenContentPopupByHandler(-1, -1, 'relatedimages', keyname, idx, "com.enonic.vertical.adminweb.handlers.ContentEnhancedImageHandlerServlet");
				}
	
        /*
          Method: removeImageEM
        */
				function removeImageEM( fieldName, object ) {
					if (itemcount(document.getElementsByName(object.name)) != 1) {
            var index = getObjectIndex(object);
            window.document.getElementsByName(fieldName)[index].value = "";
						window.document.getElementsByName("div" + fieldName)[index].innerHTML = "";
					} else {
            if (isArray(window.document.getElementsByName(fieldName))) {
							window.document.getElementsByName(fieldName)[0].value = "";
							window.document.getElementsByName("div" + fieldName)[0].innerHTML = "";
						} else {
							window.document.getElementsByName(fieldName).value = "";
							window.document.getElementsByName("div" + fieldName).innerHTML = "";
						}
					}
				}
        // -------------------------------------------------------------------------------------------------------------

        /*
          Method: moveTableRowUpEM
        */
        function moveTableRowUpEM(tbName, object) {
          var index = getObjectIndex(object);
          var tBody = document.getElementById(tbName);
          if (!tBody)
            return;

          moveTableRowUp(tbName, index);
        }
        // -------------------------------------------------------------------------------------------------------------

        /*
          Method: moveTableRowDownEM
        */
        function moveTableRowDownEM(tbName, object) {
          var index = getObjectIndex(object);
          var tBody = document.getElementById(tbName);
          if (!tBody)
            return;

          moveTableRowDown(tbName, index);
        }
        // -------------------------------------------------------------------------------------------------------------

        /*
          Method: removeRow
        */
        function removeRow( tableName, object ) {
					var index = getObjectIndex(object);
					if (index != 0) {
						document.getElementById(tableName).deleteRow(index);
					}						
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
      <xsl:attribute name="colspan">
        <xsl:value-of select="$colspan"/>
      </xsl:attribute>
      <xsl:if test="$helpelement">
        <xsl:call-template name="displayhelp">
          <xsl:with-param name="fieldname" select="$name"/>
          <xsl:with-param name="helpelement" select="$helpelement"/>
        </xsl:call-template>
      </xsl:if>
      <table cellspacing="4">
        <tbody name="{$name}table" id="{$name}table">
          <tr style="display: none; padding-bottom: 2em;">
            <td valign="top">
              <span id="div{$name}" name="div{$name}"/>
              <br/>
              <br/>
            </td>
            <td valign="top" align="left" colspan="{$colspan}">
              <input type="hidden" name="{$name}"/>
              %fldImageText%:
              <br/>
              <textarea name="{$name}text" rows="7" cols="28">
              </textarea>
            </td>
            <td valign="bottom">
              <xsl:call-template name="button">
                <xsl:with-param name="name">
                  <xsl:text>btnchoose</xsl:text>
                  <xsl:value-of select="$name"/>
                </xsl:with-param>
                <xsl:with-param name="onclick">
                  <xsl:value-of select="$choosefunction"/>
                </xsl:with-param>
                <xsl:with-param name="image" select="'images/icon_browse.gif'"/>
                <xsl:with-param name="type" select="'button'"/>
                <xsl:with-param name="disabled" select="$disabled"/>
                <xsl:with-param name="tooltip" select="'%cmdSelectImage%'"/>
              </xsl:call-template>
              <xsl:call-template name="button">
                <xsl:with-param name="name" select="concat('moverupper_', $name)"/>
                <xsl:with-param name="image" select="'images/icon_move_up.gif'"/>
                <xsl:with-param name="type" select="'button'"/>
                <xsl:with-param name="disabled" select="$disabled"/>
                <xsl:with-param name="tooltip" select="'%altContentMoveUp%'"/>
                <xsl:with-param name="onclick">
                  <xsl:text>javascript:moveTableRowUpEM('</xsl:text><xsl:value-of select="concat($name, 'table')"/>
                  <xsl:text>', this)</xsl:text>
                </xsl:with-param>
              </xsl:call-template>
              <xsl:call-template name="button">
                <xsl:with-param name="name" select="concat('moverdowner_', $name)"/>
                <xsl:with-param name="image" select="'images/icon_move_down.gif'"/>
                <xsl:with-param name="type" select="'button'"/>
                <xsl:with-param name="disabled" select="$disabled"/>
                <xsl:with-param name="tooltip" select="'%altContentMoveDown%'"/>
                <xsl:with-param name="onclick">
                  <xsl:text>javascript:moveTableRowDownEM('</xsl:text><xsl:value-of select="concat($name, 'table')"/>
                  <xsl:text>', this)</xsl:text>
                </xsl:with-param>
              </xsl:call-template>
              <xsl:call-template name="button">
                <xsl:with-param name="name">
                  <xsl:text>btndelete</xsl:text>
                  <xsl:value-of select="$name"/>
                </xsl:with-param>
                <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                <xsl:with-param name="type" select="'button'"/>
                <xsl:with-param name="disabled" select="$disabled"/>
                <xsl:with-param name="tooltip" select="'%cmdRemoveImage%'"/>
                <xsl:with-param name="onclick">
                  <xsl:value-of select="$deletefunction"/>
                </xsl:with-param>
              </xsl:call-template>
            </td>
          </tr>

          <xsl:choose>
            <xsl:when test="boolean($selected)">
              <xsl:for-each select="$selected">
                <xsl:if test="($contenttest = 'true' and /contents/relatedcontents/content[@key = $selected/@key]) or $contenttest != 'true'">
                  <tr>
                    <td valign="top">
                      <span id="div{$name}" name="div{$name}">

                        <img src="_image/{@key}/label/source?_filter=scalemax({$scalemax})" alt="">
                          <xsl:if test="$disabled">
                            <xsl:attribute name="class">
                              <xsl:text>disabled-element</xsl:text>
                            </xsl:attribute>
                          </xsl:if>
                        </img>

                      </span>
                      <br/>
                      <br/>
                    </td>

                    <td valign="top" align="left">
                      <xsl:attribute name="colspan">
                        <xsl:value-of select="$colspan"/>
                      </xsl:attribute>
                      <input type="hidden" name="{$name}" value="{@key}"/>
                      %fldImageText%:
                      <br/>
                      <textarea name="{$name}text" rows="7" cols="28">
                        <xsl:if test="$disabled">
                          <xsl:attribute name="disabled">disabled</xsl:attribute>
                        </xsl:if>
                        <xsl:value-of select="text"/>
                      </textarea>
                    </td>

                    <td valign="bottom">
                      <xsl:call-template name="button">
                        <xsl:with-param name="name">
                          <xsl:text>btnchoose</xsl:text>
                          <xsl:value-of select="$name"/>
                        </xsl:with-param>
                        <xsl:with-param name="onclick">
                          <xsl:value-of select="$choosefunction"/>
                        </xsl:with-param>
                        <xsl:with-param name="image" select="'images/icon_browse.gif'"/>
                        <xsl:with-param name="type" select="'button'"/>
                        <xsl:with-param name="disabled" select="$disabled"/>
                        <xsl:with-param name="tooltip" select="'%cmdSelectImage%'"/>
                      </xsl:call-template>
                      <xsl:call-template name="button">
                        <xsl:with-param name="name" select="concat('moverupper_', $name)"/>
                        <xsl:with-param name="image" select="'images/icon_move_up.gif'"/>
                        <xsl:with-param name="type" select="'button'"/>
                        <xsl:with-param name="disabled" select="$disabled"/>
                        <xsl:with-param name="tooltip" select="'%altContentMoveUp%'"/>
                        <xsl:with-param name="onclick">
                          <xsl:text>javascript:moveTableRowUpEM('</xsl:text><xsl:value-of select="concat($name, 'table')"/>
                          <xsl:text>', this)</xsl:text>
                        </xsl:with-param>
                      </xsl:call-template>
                      <xsl:call-template name="button">
                        <xsl:with-param name="name" select="concat('moverdowner_', $name)"/>
                        <xsl:with-param name="image" select="'images/icon_move_down.gif'"/>
                        <xsl:with-param name="type" select="'button'"/>
                        <xsl:with-param name="disabled" select="$disabled"/>
                        <xsl:with-param name="tooltip" select="'%altContentMoveDown%'"/>
                        <xsl:with-param name="onclick">
                          <xsl:text>javascript:moveTableRowDownEM('</xsl:text><xsl:value-of select="concat($name, 'table')"/>
                          <xsl:text>', this)</xsl:text>
                        </xsl:with-param>
                      </xsl:call-template>
                      <xsl:call-template name="button">
                        <xsl:with-param name="name">
                          <xsl:text>btndelete</xsl:text>
                          <xsl:value-of select="$name"/>
                        </xsl:with-param>
                        <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                        <xsl:with-param name="type" select="'button'"/>
                        <xsl:with-param name="disabled" select="$disabled"/>
                        <xsl:with-param name="tooltip" select="'%cmdRemoveImage%'"/>
                        <xsl:with-param name="onclick">
                          <xsl:value-of select="$deletefunction"/>
                        </xsl:with-param>
                      </xsl:call-template>

                    </td>
                  </tr>
                </xsl:if>
              </xsl:for-each>
            </xsl:when>
          </xsl:choose>
        </tbody>
      </table>
    </td>
  </xsl:template>
</xsl:stylesheet>