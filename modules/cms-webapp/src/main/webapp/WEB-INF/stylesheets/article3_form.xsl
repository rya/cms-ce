<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:x="mailto:vro@enonic.com?subject=foobar"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html" />

  <xsl:include href="common/standard_form_templates.xsl"/>

  <xsl:include href="common/authors.xsl"/>
  <xsl:include href="common/generic_formheader.xsl"/>
  <xsl:include href="editor/xhtmleditor.xsl"/>
  <xsl:include href="common/textarea.xsl"/>
  <xsl:include href="common/textfield.xsl"/>
  <xsl:include href="common/relatedlinks.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>

  <xsl:param name="create"/>
  <xsl:param name="contenttypekey"/>
  <xsl:param name="modulename"/>
  <xsl:param name="csskey"/>
  <xsl:param name="expertcontributor"/>
  <xsl:param name="developer"/>

    <xsl:param name="accessToHtmlSource">
        <xsl:choose>
            <xsl:when test="$expertcontributor = 'true' or $developer = 'true'">
                <xsl:value-of select="true()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="false()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:param>

  <xsl:template match="/">

    <html>
        <head>
            <link type="text/css" rel="stylesheet" href="css/admin.css"/>
            <link type="text/css" rel="stylesheet" href="css/menu.css"/>
            <link rel="stylesheet" type="text/css" href="javascript/cms/ui/style.css"/>
            <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>
            <link rel="stylesheet" type="text/css" href="css/calendar_picker.css"/>

            <script type="text/javascript" language="JavaScript" src="javascript/admin.js">//</script>
            <script type="text/javascript" language="JavaScript" src="javascript/relatedcontent.js">//</script>
            <script type="text/javascript" language="JavaScript" src="javascript/accessrights.js">//</script>
            <script type="text/javascript" language="JavaScript" src="javascript/validate.js">//</script>
            <script type="text/javascript" language="JavaScript" src="javascript/tabpane.js">//</script>
            <script type="text/javascript" language="JavaScript" src="javascript/menu.js">//</script>
            <script type="text/javascript" language="JavaScript" src="javascript/calendar_picker.js">//</script>
            <script type="text/javascript" language="JavaScript" src="javascript/properties.js">//</script>

            <script type="text/javascript" src="javascript/cms/core.js">//</script>
            <script type="text/javascript" src="javascript/cms/utils/Event.js">//</script>
            <script type="text/javascript" src="javascript/cms/utils/Cookie.js">//</script>
            <script type="text/javascript" src="javascript/cms/element/Css.js">//</script>
            <script type="text/javascript" src="javascript/cms/element/Dimensions.js">//</script>
            <script type="text/javascript" src="javascript/cms/ui/SplitButton.js">//</script>

            <script type="text/javascript" src="tinymce/jscripts/cms/Util.js">//</script>
            <script type="text/javascript" src="tinymce/jscripts/cms/Editor.js">//</script>
            <script type="text/javascript" src="tinymce/jscripts/tiny_mce/tiny_mce.js">//</script>

            <xsl:call-template name="waitsplash"/>

            <script type="text/javascript" language="JavaScript">
                // variables use by menu.js
                var branchOpen = new Array();
                var cookiename = "contentform";

                // array with names og compulsory fields
                // array with friendlyname, fieldname and function for validated fields
                var validatedFields = new Array(2);
                validatedFields[0] = new Array("%fldHeading%", "contentdata_heading", validateRequired);
                validatedFields[1] = new Array("%fldBody%", "contentdata_body", validateRequired);

                function validateAll(formName) {
                  // Copy the content from all editor instances to their textareas.
                  tinyMCE.triggerSave();



                  var f = document.forms[formName];

					if ( !checkAll(formName, validatedFields) )
    		            return;

					// special check for teaser1 and teaser2
					if ( f['contentdata_teaser1'].value == "" &amp;&amp; f['contentdata_teaser2'].value == "" ) {
						alert('%errInvalidValueFrontPageOrTeaser%');
						return;
					}
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

                function insert_file_onclick(view, key, object, p) {
               		var row = getObjectIndex(object);
                  // OpenContentPopupByHandler(unitKey, categoryKey, mode, fieldName, fieldRow, handler)

	                OpenContentPopupByHandler(-1, -1, 'relatedfiles', p, row, "com.enonic.vertical.adminweb.handlers.ContentFileHandlerServlet");
                }

                function _removeFiles(table, objThis) {
                  var count = itemcount(document.getElementsByName(objThis.name));
                  if( count == 1 ) {
                    document.getElementsByName('filename')[0].value = "";
                    document.getElementsByName('contentdata_file')[0].value = "";
                    return;
                  }

                  var index = getObjectIndex(objThis);
                  var r = document.getElementById(table).getElementsByTagName('tr')[index];
                  if (r)
                    document.getElementById(table).removeChild(r);
                }
            </script>
          <xsl:call-template name="waitsplash"/>

        </head>
        <xsl:call-template name="contentform"/>
    </html>

  </xsl:template>

    <xsl:template name="contenttypeform">
		<xsl:param name="readonly"/>


      <xsl:if test="not($readonly)">
        <script type="text/javascript">waitsplash();</script>
      </xsl:if>


        <fieldset>
            <legend>&nbsp;%blockArticle%&nbsp;</legend>
            <table border="0" cellspacing="0" cellpadding="2">
                <tr><td class="form_labelcolumn"></td></tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'contentdata_heading'"/>
                        <xsl:with-param name="label" select="'%fldHeading%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/heading"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
						<xsl:with-param name="required" select="true()"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textarea">
                        <xsl:with-param name="name" select="'contentdata_teaser1'"/>
                        <xsl:with-param name="label" select="'%fldFrontpageTeaser%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/teaser/text1"/>
                        <xsl:with-param name="rows" select="'5'"/>
                        <xsl:with-param name="cols" select="'60'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="required" select="true()"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textarea">
                        <xsl:with-param name="name" select="'contentdata_teaser2'"/>
                        <xsl:with-param name="label" select="'%fldArticleTeaser%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/teaser/text2"/>
                        <xsl:with-param name="rows" select="'5'"/>
                        <xsl:with-param name="cols" select="'60'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="required" select="true()"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="enhancedimageselector">
                        <xsl:with-param name="label" select="'%fldTeaserImage%:'"/>
                        <xsl:with-param name="name" select="'contentdata_teaser_image'"/>
                        <xsl:with-param name="selectedkey" select="/contents/content/contentdata/teaser/image/@key"/>
                        <xsl:with-param name="width" select="/contents/content/contentdata/teaser/image/width"/>
                        <xsl:with-param name="height" select="/contents/content/contentdata/teaser/image/height"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                        <xsl:with-param name="imagetype" select="'thumbnail'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <td width="120" class="form_labelcolumn" valign="top" nowrap="nowrap" >
						<xsl:text>%fldBody%:</xsl:text>
						<span class="requiredfield">*</span>
					</td>
          <td colspan="2">
                    <xsl:call-template name="xhtmleditor">
                      <xsl:with-param name="id" select="'contentdata_body'"/>
                      <xsl:with-param name="name" select="'contentdata_body'"/>
                      <xsl:with-param name="content" select="/contents/content/contentdata/body/text"/>
                      <xsl:with-param name="configxpath" select="/node()/htmleditorconfig"/>
                      <xsl:with-param name="config" select="'document'"/>
                      <xsl:with-param name="customcss" select="$csskey"/>
                      <!--xsl:with-param name="menukey" select="$menukey"/-->
                      <xsl:with-param name="disabled" select="$readonly"/>
                      <xsl:with-param name="accessToHtmlSource" select="$accessToHtmlSource = 'true'"/>
                      <xsl:with-param name="required" select="true()"/>
                    </xsl:call-template>
					  </td>
                </tr>
                <tr name="imagerow">
                    <xsl:call-template name="enhancedmultipleimageselector">
                        <xsl:with-param name="label" select="'%fldBodyImages%:'"/>
                        <xsl:with-param name="name" select="'contentdata_body_image'"/>
                        <xsl:with-param name="selected" select="/contents/content/contentdata/body/image"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                        <xsl:with-param name="imagetype" select="'body'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
				<tr>
					<td>
						<table cellspacing="4">
							<tr>
								<td>
									<xsl:call-template name="button">
										<xsl:with-param name="type" select="'button'"/>
										<xsl:with-param name="caption" select="'%cmdNewImage%'"/>
										<xsl:with-param name="onclick">
                          <xsl:text>javascript:addTableRow('contentdata_body_imagetable', 0, true);
                            relatedImagesDisplay('contentdata_body_imagetable');
                          </xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="disabled" select="$readonly"/>
                      </xsl:call-template>
                      <!--xsl:if test="count(/contents/content/contentdata/body/image) = 0">
                        <script language="javascript">
                          addTableRow('contentdata_body_imagetable', 0, true); relatedImagesDisplay('contentdata_body_imagetable');
                        </script>
                      </xsl:if-->
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td>%fldKeywords%:</td>
              <td colspan="3">
                <input type="text" name="contentdata_keywords" size="60" maxlength="100">
                  <xsl:attribute name="value">
                    <xsl:for-each select="/contents/content/contentdata/keywords/keyword">
                      <xsl:sort select="."/>
                      <xsl:value-of select="."/>
                                    <xsl:text> </xsl:text>
                                </xsl:for-each>
                            </xsl:attribute>
							<xsl:if test="$readonly">
								<xsl:attribute name="disabled">disabled</xsl:attribute>
							</xsl:if>
                        </input>
                    </td>
                </tr>
            </table>
        </fieldset>

        <fieldset>
            <legend>&nbsp;%blockAuthors%&nbsp;</legend>
            <table border="0" cellspacing="0" cellpadding="2">
                <tr><td class="form_labelcolumn"></td></tr>
                <xsl:call-template name="authors">
					 <xsl:with-param name="disabled" select="$readonly"/>
                </xsl:call-template>
            </table>
        </fieldset>

        <fieldset>
            <legend>&nbsp;%blockRelatedLinks%&nbsp;</legend>
            <table border="0" cellspacing="0" cellpadding="2">
                <tr><td class="form_labelcolumn"></td></tr>
                <xsl:call-template name="relatedlinks">
                    <xsl:with-param name="validateurls">false</xsl:with-param>
					<xsl:with-param name="disabled" select="$readonly"/>
                </xsl:call-template>
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
		                                    <xsl:text>insert_file_onclick('filename', 'contentdata_file', this, '');</xsl:text>
		                                </xsl:with-param>
		                            </xsl:call-template>
                                    <xsl:text>&nbsp;</xsl:text>
                                    <xsl:call-template name="button">
										<xsl:with-param name="disabled" select="$disabled"/>
		                                <xsl:with-param name="name">removefilerow</xsl:with-param>
		                                <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
		                                <xsl:with-param name="onclick">
                      <xsl:text>_removeFiles('filetable', this);</xsl:text>
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
                          <xsl:text>_removeFiles('filetable', this);</xsl:text>
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