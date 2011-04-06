<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
	<!ENTITY Oslash "&#216;">
	<!ENTITY oslash "&#248;">
	<!ENTITY Aring  "&#197;">
	<!ENTITY aring  "&#229;">
	<!ENTITY AElig  "&#198;">
	<!ENTITY aelig  "&#230;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:x="mailto:vro@enonic.com?subject=foobar"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html" doctype-system="http://www.w3.org/TR/html4/loose.dtd" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>

  <xsl:include href="../common/generic_parameters.xsl"/>
  <xsl:include href="../common/button.xsl"/>
  <xsl:include href="../common/escapequotes.xsl"/>
  <xsl:include href="../handlerconfigs/default.xsl"/>
  <xsl:include href="../common/labelcolumn.xsl"/>
  <xsl:include href="../common/displayerror.xsl"/>
  <xsl:include href="../common/displayhelp.xsl"/>
  <xsl:include href="../common/textfield.xsl"/>
  <xsl:include href="../common/searchfield.xsl"/>

  <xsl:param name="filename"/>
  <xsl:param name="binarykey"/>
  <xsl:param name="mode"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>%txtInsertEditLink%</title>
        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" src="javascript/menu.js">//</script>
        <script type="text/javascript" src="javascript/tabpane.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/tiny_mce/tiny_mce_popup.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/tiny_mce/utils/mctabs.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/tiny_mce/utils/form_utils.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/tiny_mce/utils/validate.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/tiny_mce/plugins/cmslink/js/cms_link_dialog.js">//</script>
        <script type="text/javascript">
          var cmslang = {
            alertIsEmail : "%alertIsEmail%",
            cmdUpdateLink : "%cmdUpdateLink%",
            errInvalidLink : "%errInvalidLink%",
            contentRepositories : '%headContentRepositories%',
            sites : '%headMenus%'
          }

          // Needed for the tree menu which is displayed in portal context(page templates, portlets and menu items).
          var branchOpen = new Array();
        </script>
        <link href="css/menu.css" rel="stylesheet" type="text/stylesheet"/>
        <link href="tinymce/jscripts/tiny_mce/plugins/cmslink/css/cms_link_dialog.css" rel="stylesheet" type="text/stylesheet"/>
        <link href="css/admin.css" rel="stylesheet" type="text/stylesheet"/>
      </head>
      <body>
        <form name="formAdmin" onsubmit="CMSLinkDialog.insertLinkAction();return false;" action="#">
          <div class="tabs">
            <ul>
              <li id="general_tab" class="current">
                <span>
                  <a href="javascript:;">
                    %txtInsertEditLink%
                  </a>
                </span>
              </li>
            </ul>
          </div>
          <div class="panel_wrapper">
            <div id="general_panel">
              <fieldset>
                <legend>%blockLink%&nbsp;</legend>
                <table border="0" cellpadding="4" cellspacing="0" style="width:100%">
                  <tbody>
                    
                    <tr id="link-text-container" style="display:none">
                      <td nowrap="nowrap">
                        <label for="link-text">%fldText%:</label>
                      </td>
                      <td>
                        <input type="text" name="link-text" value="" id="link-text" size="48"/>
                      </td>
                    </tr>

                    <tr>
                      <td nowrap="nowrap">
                        <label id="hreflabel" for="href">%fldType%:</label>
                      </td>
                      <td>
                        <select mame="link-type" id="link-type" onchange="CMSLinkDialog.onChangeLinkType(this.value)">
                          <option value="standard">
                            <xsl:text>%fldURL%</xsl:text>
                          </option>
                          <option value="mail">
                            <xsl:text>%fldEmail%</xsl:text>
                          </option>
                          <option value="anchor">
                            <xsl:text>%fldAnchor%</xsl:text>
                          </option>
                          <option value="file">
                            <xsl:text>%fldFile%</xsl:text>
                          </option>
                          <option value="page">
                            <xsl:text>%txtPage%</xsl:text>
                          </option>
                          <option value="content">
                            <xsl:text>%fldCustomContent%</xsl:text>
                          </option>
                        </select>
                      </td>
                    </tr>
                  </tbody>
                  <tbody id="url-field-containers">
                    <tr id="standard-field-container">
                      <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'input-standard'"/>
                        <xsl:with-param name="id" select="'input-standard'"/>
                        <xsl:with-param name="selectnode" select="'http://'"/>
                        <xsl:with-param name="required" select="'true'"/>
                        <xsl:with-param name="label" select="'%fldURL%:'"/>
                        <xsl:with-param name="size" select="48"/>
                      </xsl:call-template>
                    </tr>
                    <tr id="mail-field-container" style="display:none">
                      <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'input-mail'"/>
                        <xsl:with-param name="id" select="'input-mail'"/>
                        <xsl:with-param name="selectnode" select="'mailto:'"/>
                        <xsl:with-param name="required" select="'true'"/>
                        <xsl:with-param name="label" select="'%fldEmail%:'"/>
                        <xsl:with-param name="size" select="48"/>
                      </xsl:call-template>
                    </tr>
                    <tr id="anchor-field-container" style="display:none">
                        <td class="column1">
                            <label for="anchorlist">%fldAnchor%:</label>
                        </td>
                        <td colspan="2" id="anchorlistcontainer">&nbsp;</td>
                    </tr>
                    <tr id="file-field-container" style="display:none">
                      <xsl:call-template name="searchfield">
                        <xsl:with-param name="name" select="'input-file'"/>
                        <xsl:with-param name="id" select="'input-file'"/>
                        <xsl:with-param name="required" select="'true'"/>
                        <xsl:with-param name="label" select="'%fldFile%:'"/>
                        <xsl:with-param name="size" select="41"/>
                        <xsl:with-param name="buttonfunction">
                          <xsl:text>CMSLinkDialog.openPickerWindow(</xsl:text>
                          <xsl:text>'adminpage?page=600&amp;op=popup&amp;subop=insert</xsl:text>
                          <xsl:text>&amp;selectedunitkey=-1&amp;fieldname=null&amp;fieldrow=null</xsl:text>
                          <xsl:text>&amp;handler=com.enonic.vertical.adminweb.handlers.ContentFileHandlerServlet&amp;contenthandler=attachment'</xsl:text>
                          <xsl:text>);</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="add-nowrap-on-label-column" select="'false'"/>
                      </xsl:call-template>
                    </tr>
                    <tr id="page-field-container" style="display:none">
                      <xsl:call-template name="searchfield">
                        <xsl:with-param name="name" select="'input-page'"/>
                        <xsl:with-param name="id" select="'input-page'"/>
                        <xsl:with-param name="required" select="'true'"/>
                        <xsl:with-param name="label" select="'%txtPage%:'"/>
                        <xsl:with-param name="size" select="41"/>
                        <xsl:with-param name="buttonfunction">
                          <xsl:text>CMSLinkDialog.openPickerWindow(</xsl:text>
                          <xsl:text>'adminpage?page=850&amp;op=menuitem_selector_multisite'</xsl:text>
                          <xsl:text>, 250, 300);</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="add-nowrap-on-label-column" select="'false'"/>
                      </xsl:call-template>
                    </tr>
                    <tr id="content-field-container" style="display:none">
                      <xsl:call-template name="searchfield">
                        <xsl:with-param name="name" select="'input-content'"/>
                        <xsl:with-param name="id" select="'input-content'"/>
                        <xsl:with-param name="required" select="'true'"/>
                        <xsl:with-param name="label" select="'%fldCustomContent%:'"/>
                        <xsl:with-param name="size" select="41"/>
                        <xsl:with-param name="buttonfunction">
                          <xsl:text>CMSLinkDialog.openPickerWindow(</xsl:text>
                          <xsl:text>'adminpage?page=600&amp;op=popup&amp;subop=insert</xsl:text>
                          <xsl:text>&amp;fieldname=null&amp;fieldrow=null&amp;contenthandler=any'</xsl:text>
                          <xsl:text>);</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="add-nowrap-on-label-column" select="'false'"/>
                      </xsl:call-template>
                    </tr>
                  </tbody>
                  <tr>
                    <td>
                      <label id="targetlistlabel" for="targetlist">%fldOpenLinkIn%:</label>
                    </td>
                    <td id="targetlistcontainer">
                      <select name="target" id="targetlist">
                        <option value="">%optOpenExistingWindow%</option>
                        <option value="_blank" selected="selected">%optDownloadFile%</option>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td nowrap="nowrap">
                      <label id="titlelabel" for="title">%fldAltText%:</label>
                    </td>
                    <td>
                      <input type="text" name="title" value="" id="title" size="48"/>
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <label id="targetlistlabel" for="targetlist">%fldRel%:</label>
                    </td>
                    <td id="relistcontainer">
                      <select name="rel" id="rel">
                        <option value="">%sysDropDownNone%</option>
                        <option value="custom">Custom</option>
                        <option value="lightbox">Lightbox</option>
                        <option value="alternate">Alternate</option>
                        <option value="designates">Designates</option>
                        <option value="stylesheet">Stylesheet</option>
                        <option value="start">Start</option>
                        <option value="next">Next</option>
                        <option value="prev">Prev</option>
                        <option value="contents">Contents</option>
                        <option value="index">Index</option>
                        <option value="glossary">Glossary</option>
                        <option value="copyright">Copyright</option>
                        <option value="chapter">Chapter</option>
                        <option value="subsection">Subsection</option>
                        <option value="appendix">Appendix</option>
                        <option value="help">Help</option>
                        <option value="bookmark">Bookmark</option>
                        <option value="nofollow">No Follow</option>
                        <option value="tag">Tag</option>
                      </select>
                      &nbsp;
                      <input type="text" name="relcustom" id="relcustom" style="width: 80px; display: none"/>
                    </td>
                  </tr>
                </table>
              </fieldset>
            </div>
          </div>
          <div class="mceActionPanel">
            <div style="float: left">
              <input type="submit" id="insert" name="insert" value="%cmdInsertLink%"/>
            </div>
            <div style="float: right">
              <input type="button" id="cancel" name="cancel" value="%cmdCancel%" onclick="tinyMCEPopup.close();"/>
            </div>
          </div>
        </form>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
