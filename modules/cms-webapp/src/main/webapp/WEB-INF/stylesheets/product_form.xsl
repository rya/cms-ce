<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
<!ENTITY Oslash "&#216;">
<!ENTITY oslash "&#248;">
<!ENTITY Aring  "&#197;">
<!ENTITY aring  "&#229;">
<!ENTITY AElig  "&#198;">
<!ENTITY aelig  "&#230;">
<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>

  <xsl:include href="common/standard_form_templates.xsl"/>

  <xsl:include href="common/textfield.xsl"/>
  <xsl:include href="common/textarea.xsl"/>
  <xsl:include href="common/generic_formheader.xsl"/>
  <xsl:include href="editor/xhtmleditor.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>

  <xsl:param name="create"/>
  <xsl:param name="contenttypekey"/>

  <xsl:template match="/">

    <html>
      <head>
        <title>CMS</title>

        <script type="text/javascript" src="javascript/cms/core.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Event.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Cookie.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Css.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Dimensions.js">//</script>
        <script type="text/javascript" src="javascript/cms/ui/SplitButton.js">//</script>

        <link type="text/css" rel="stylesheet" href="css/admin.css"/>
        <link type="text/css" rel="stylesheet" href="css/menu.css"/>
        <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>
        <link rel="stylesheet" type="text/css" href="css/calendar_picker.css"/>
        <link rel="stylesheet" type="text/css" href="javascript/cms/ui/style.css"/>

        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" src="javascript/accessrights.js">//</script>
        <script type="text/javascript" src="javascript/validate.js">//</script>
        <script type="text/javascript" src="javascript/tabpane.js">//</script>
        <script type="text/javascript" src="javascript/menu.js">//</script>
        <script type="text/javascript" src="javascript/calendar_picker.js">//</script>
        <script type="text/javascript" src="javascript/properties.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/cms/Util.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/cms/Editor.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/tiny_mce/tiny_mce.js">//</script>

        <script type="text/javascript" language="JavaScript">
          // variables used by menu.js
          var branchOpen = new Array();
          var cookiename = "contentform";

          // array with names og compulsory fields
          // array with friendlyname, fieldname and function for validated fields
          var validatedFields = new Array(1);
          validatedFields[0] = new Array("%fldName%", "contentdata_productname", validateRequired);
          validatedFields[1] = new Array("%fldArticleNumber%", "contentdata_productnumber", validateRequired);
          validatedFields[2] = new Array("%fldPrice%", "contentdata_price", validateRequired);

          function validateAll(formName) {
              var f = document.forms[formName];

              if ( !checkAll(formName, validatedFields) )
                  return;

              // special for instock and price
              var regExpInstock = /^[0-9]*$/;
              var regExpPrice = /^([0-9])*$|([0-9]+(,|\.)[0-9]+)$/;

              if (!regExpPrice.test(f.contentdata_price.value)) {
                  error(f.contentdata_price, "%errInvalidPrice%");
                  return;
              }
              f.contentdata_price.value = f.contentdata_price.value.replace(/\./, ",");

              disableFormButtons();
              f.submit();
          }

          // This method doesn't use the fieldName param
        function addRelatedFiles(fieldName, fieldRow, content_key, content_title ) {

          var keyField;
          var nameField;

          if (fieldRow == 'none') {
            keyField = document.getElementById('contentdata_file');
            nameField = document.getElementById('filename');
          }
          else {
            keyField = document.getElementsByName('contentdata_file')[fieldRow];
            nameField = document.getElementsByName('filename')[fieldRow];
          }
          keyField.value = content_key;
          nameField.value = content_title;
        }

        function insert_file_onclick(view, key, object) {
          var row = getObjectIndex(object);

          OpenContentPopupByHandler(-1, -1, 'relatedfiles', null, row, "com.enonic.vertical.adminweb.handlers.ContentFileHandlerServlet");
        }

        </script>
      </head>

      <xsl:call-template name="contentform"/>
    </html>

  </xsl:template>

    <xsl:template name="contenttypeform">
		<xsl:param name="readonly"/>

        <fieldset>
            <legend>&nbsp;%blockProduct%&nbsp;</legend>
            <table border="0" cellspacing="0" cellpadding="2">
                <tr><td class="form_labelcolumn"></td></tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'contentdata_productname'"/>
                        <xsl:with-param name="label" select="'%fldName%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/name"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						            <xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'contentdata_productnumber'"/>
                        <xsl:with-param name="label" select="'%fldArticleNumber%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/number"/>
                        <xsl:with-param name="size" select="'30'"/>
                        <xsl:with-param name="maxlength" select="'30'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						            <xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'contentdata_price'"/>
                        <xsl:with-param name="label" select="'%fldPrice%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/price"/>
                        <xsl:with-param name="size" select="'10'"/>
                        <xsl:with-param name="maxlength" select="'10'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
            </table>
        </fieldset>
        <fieldset>
            <legend>&nbsp;%blockProductInformation%&nbsp;</legend>
            <table border="0" cellspacing="0" cellpadding="2">
                <tr><td class="form_labelcolumn"></td></tr>
                <tr>
                    <xsl:call-template name="textarea">
                        <xsl:with-param name="name" select="'contentdata_teaser'"/>
                        <xsl:with-param name="label" select="'%fldTeaser%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/teaser/text"/>
                        <xsl:with-param name="rows" select="'5'"/>
                        <xsl:with-param name="cols" select="'60'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="enhancedimageselector">
                        <xsl:with-param name="label" select="'%fldTeaserImage%:'"/>
                        <xsl:with-param name="name" select="'contentdata_teaser_image'"/>
                        <xsl:with-param name="selectedkey" select="/contents/content/contentdata/teaser/teaser_image/binarydata/@key"/>
                        <xsl:with-param name="width" select="/contents/content/contentdata/teaser/teaser_image/width"/>
                        <xsl:with-param name="height" select="/contents/content/contentdata/teaser/teaser_image/height"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                        <xsl:with-param name="imagetype" select="'thumbnail'"/>
						            <xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                  <xsl:call-template name="xhtmleditor">
                    <xsl:with-param name="id" select="'contentdata_body'"/>
                    <xsl:with-param name="name" select="'contentdata_body'"/>
                    <xsl:with-param name="content" select="/contents/content/contentdata/body/text"/>
                    <xsl:with-param name="configxpath" select="/menus/htmleditorconfig"/>
                    <xsl:with-param name="config" select="'light'"/>
                    <xsl:with-param name="disabled" select="$readonly"/>
                    <xsl:with-param name="accessToHtmlSource" select="true()"/>
                  </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="enhancedimageselector">
                        <xsl:with-param name="label" select="'%fldBodyImage%:'"/>
                        <xsl:with-param name="name" select="'contentdata_body_image'"/>
                        <xsl:with-param name="selectedkey" select="/contents/content/contentdata/body/image/binarydata/@key"/>
                        <xsl:with-param name="width" select="/contents/content/contentdata/body/image/width"/>
                        <xsl:with-param name="height" select="/contents/content/contentdata/body/image/height"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                        <xsl:with-param name="imagetype" select="'body'"/>
						            <xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
            </table>
        </fieldset>
        <fieldset>
            <legend>&nbsp;%blockFiles%&nbsp;</legend>
            <table border="0" cellspacing="0" cellpadding="2">
                <tr><td class="form_labelcolumn"></td></tr>
                <xsl:call-template name="files">
                    <xsl:with-param name="files" select="/contents/content/contentdata/files"/>
					<xsl:with-param name="disabled" select="$readonly"/>
                </xsl:call-template>
            </table>
        </fieldset>

    </xsl:template>

    <xsl:template name="files">
        <xsl:param name="files"/>
		<xsl:param name="disabled" select="false()"/>

        <tr>
            <td>
            </td>
            <td>
                <table cellspacing="0" cellpadding="0" border="0">
					<tbody name="filetable" id="filetable">
	                	<xsl:choose>
	                        <xsl:when test="not(boolean($files/file))">
	                            <tr>
	                                <td>
	                                    <input type="text" name="filename" size="40" readonly="readonly" />
	                                    <input type="hidden" name="contentdata_file"/>
	                                    <xsl:call-template name="button">
											<xsl:with-param name="disabled" select="$disabled"/>
			                                <xsl:with-param name="name">choosefile</xsl:with-param>
			                                <xsl:with-param name="image" select="'images/icon_browse.gif'"/>
			                                <xsl:with-param name="onclick">
			                                    <xsl:text>insert_file_onclick('filename', 'contentdata_file', this);</xsl:text>
			                                </xsl:with-param>
			                            </xsl:call-template>
	                                    <xsl:text>&nbsp;</xsl:text>
	                                    <xsl:call-template name="button">
											<xsl:with-param name="disabled" select="$disabled"/>
			                                <xsl:with-param name="name">removefilerow</xsl:with-param>
			                                <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
			                                <xsl:with-param name="onclick">
			                                    <xsl:text>removeTableRow(this, 'filetable', 'document.formAdmin.filename.value=""; document.formAdmin.contentdata_file.value="";', 0);</xsl:text>
			                                </xsl:with-param>
			                            </xsl:call-template>
	                                </td>
	                            </tr>
	                        </xsl:when>
	                        <xsl:otherwise>
	                            <xsl:for-each select="$files/file">
	                                <xsl:variable name="key" select="@key"/>
	                                <tr>
	                                    <td>
	                                        <input type="text" name="filename" value="{/contents/relatedcontents/content[@key = $key]/contentdata/name}" size="40" readonly="readonly">
												<xsl:if test="$disabled">
													<xsl:attribute name="disabled">disabled</xsl:attribute>
												</xsl:if>
	                                        </input>
											<input type="hidden" name="contentdata_file" value="{@key}"/>
											<xsl:text>&nbsp;</xsl:text>
											<xsl:call-template name="button">
												<xsl:with-param name="disabled" select="$disabled"/>
												<xsl:with-param name="name">choosefile</xsl:with-param>
												<xsl:with-param name="image" select="'images/icon_browse.gif'"/>
												<xsl:with-param name="onclick">
													<xsl:text>insert_file_onclick('filename', 'contentdata_file', this);</xsl:text>
												</xsl:with-param>
											</xsl:call-template>
											<xsl:text>&nbsp;</xsl:text>
											<xsl:call-template name="button">
												<xsl:with-param name="disabled" select="$disabled"/>
												<xsl:with-param name="name">removefilerow</xsl:with-param>
												<xsl:with-param name="image" select="'images/icon_remove.gif'"/>
												<xsl:with-param name="onclick">
													<xsl:text>removeTableRow(this, 'filetable', 'document.formAdmin.filename.value=""; document.formAdmin.contentdata_file.value="";', 0);</xsl:text>
												</xsl:with-param>
											</xsl:call-template>
	                                    </td>
	                                </tr>
	                            </xsl:for-each>
	                        </xsl:otherwise>
	                    </xsl:choose>
					</tbody>
                </table>
				<xsl:call-template name="button">
					<xsl:with-param name="name">copyfilerow</xsl:with-param>
					<xsl:with-param name="caption" select="'%cmdNewFile%'"/>
					<xsl:with-param name="disabled" select="$disabled"/>
					<xsl:with-param name="onclick">
						<xsl:text>addTableRow('filetable', 0, true);</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
            </td>
        </tr>
    </xsl:template>

</xsl:stylesheet>