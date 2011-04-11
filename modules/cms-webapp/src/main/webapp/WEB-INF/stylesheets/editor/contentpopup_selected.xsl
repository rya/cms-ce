<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
        <!ENTITY nbsp "&#160;">
        ]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>

  <xsl:include href="../common/genericheader.xsl"/>
  <xsl:include href="../common/generic_parameters.xsl"/>
  <xsl:include href="../common/categoryheader.xsl"/>
  <xsl:include href="../common/button.xsl"/>

  <xsl:param name="image" select="'false'"/>
  <xsl:param name="flash" select="'false'"/>
  <xsl:param name="content" select="'false'"/>
  
  <xsl:param name="subop"/>

  <xsl:variable name="key">
    <xsl:value-of select="/contents/content/@key"/>
  </xsl:variable>

  <xsl:variable name="binaryURL">
    <xsl:value-of select="concat('_attachment/', /contents/content/@key, '/binary/', /contents/content/binaries/binary/@key)"/>
  </xsl:variable>

  <xsl:template match="/">
    <html>
      <head>
        <title>%headInsertFile%</title>
        <script language="Javascript">
          // Globals
          var g_contenttype;
          <xsl:choose>
            <xsl:when test="$image = 'true'">g_contenttype = 'image';</xsl:when>
            <xsl:when test="$flash = 'true'">g_contenttype = 'flash';</xsl:when>
            <xsl:when test="$content = 'true'">g_contenttype = 'content';</xsl:when>
            <xsl:otherwise>g_contenttype = 'link';</xsl:otherwise>
          </xsl:choose>
        </script>
        <link rel="stylesheet" type="text/css" href="javascript/tab.webfx.css"/>
        <link rel="stylesheet" type="text/css" href="css/admin.css"/>
        <link rel="stylesheet" type="text/css" href="tinymce/jscripts/tiny_mce/themes/advanced/skins/cms/dialog.css"/>
        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" src="javascript/tabpane.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/cms/cmsWindowHelper.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/tiny_mce/plugins/cmsbinary/js/cmsbinary.js?v=0.3">//</script>
        <script type="text/javascript" src="tinymce/jscripts/tiny_mce/utils/form_utils.js">//</script>
        <script type="text/javascript">
          var cmslang = {
            sysMsgRequiredFields : '%sysMsgRequiredFields%'
          }
        </script>

      </head>
      <body>
        <h1>
          <xsl:call-template name="genericheader">
            <xsl:with-param name="sitelink" select="true()"/>
            <xsl:with-param name="endslash" select="false()"/>
          </xsl:call-template>

          <xsl:call-template name="categoryheader">
            <xsl:with-param name="subop" select="$subop"/>
          </xsl:call-template>
          <xsl:text>&nbsp;</xsl:text>

          <span id="titlename">
            <xsl:value-of select="concat('/ ',/contents/content/title)"/>
          </span>
        </h1>

        <form name="link-attributes-form" style="display:none">
          <input type="hidden" name="l-id" value=""/>
          <input type="hidden" name="l-class" value=""/>
          <input type="hidden" name="l-rel" value=""/>
        </form>

        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td class="form_title_form_seperator">
              <img src="images/1x1.gif"/>
            </td>
          </tr>
          <tr>
            <td>
              <div class="tab-pane" id="tab-pane-1">
                <script type="text/javascript" language="JavaScript">
                  var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                </script>

                <xsl:choose>
                  <xsl:when test="$flash = 'true'">
                    <div class="tab-page" id="tab-page-1">
                      <span class="tab">%blockInsertFlash%</span>

                      <script type="text/javascript" language="JavaScript">
                        tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                      </script>

                      <xsl:call-template name="insertflash"/>
                    </div>
                  </xsl:when>
                  <xsl:when test="$image = 'true'">
                    <div class="tab-page" id="tab-page-1">
                      <span class="tab">%blockInsertImage%</span>

                      <script type="text/javascript" language="JavaScript">
                        tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                      </script>

                      <xsl:call-template name="insertimage"/>
                    </div>
                  </xsl:when>
                  <xsl:when test="$content = 'true'">
                    <div class="tab-page" id="tab-page-1">
                      <span class="tab">%blockInsertContent%</span>

                      <script type="text/javascript" language="JavaScript">
                        tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                      </script>

                      <xsl:call-template name="insertcontent"/>
                    </div>
                  </xsl:when>
                </xsl:choose>

                <xsl:if test="$content = 'false'">
                  <div class="tab-page" id="tab-page-2">
                    <span class="tab">%blockInsertFile%</span>

                    <script type="text/javascript" language="JavaScript">
                      tabPane1.addTabPage( document.getElementById( "tab-page-2" ) );
                    </script>

                    <xsl:call-template name="insertfile"/>
                  </div>
                </xsl:if>

              </div>
              <script type="text/javascript" language="JavaScript">
                setupAllTabs();
              </script>
            </td>
          </tr>
          <tr>
            <td colspan="2" class="form_form_buttonrow_seperator">
              <img src="images/1x1.gif"/>
            </td>
          </tr>
          <tr>
            <td>
              <xsl:variable name="onclick">
                <xsl:text>javascript:CMSBinary.insert(</xsl:text>
                <xsl:value-of select="/contents/content/@key"/>
                <xsl:text>, '</xsl:text>
                <xsl:value-of select="/contents/content/title"/>
                <xsl:text>');</xsl:text>
              </xsl:variable>
              <xsl:call-template name="button">
                <xsl:with-param name="caption" select="'%cmdInsert%'"/>
                <xsl:with-param name="onclick" select="$onclick"/>
              </xsl:call-template>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:call-template name="button">
                <xsl:with-param name="caption" select="'%cmdCancel%'"/>
                <xsl:with-param name="onclick" select="'history.back()'"/>
              </xsl:call-template>
            </td>
          </tr>
        </table>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="insertfile">
    <fieldset>
      <legend>%blockInsertFile%</legend>
      <table border="0">

        <form name="filelinkform" method="post">
          <tr>
            <td width="140">%fldFilename%:</td>
            <td>
              <xsl:value-of select="/contents/content/contentdata/name"/>
            </td>
          </tr>
          <tr>
            <td>%fldLinkText%:
              <span class="requiredfield">*</span>
            </td>
            <td>
              <input type="text" size="40" name="linktext" onchange="javascript: this.setAttribute('changed', 'true');"/>
              <input type="hidden" name="htmllinktext"/>
            </td>
          </tr>
          <tr>
            <td>%fldTitleAttribute%:</td>
            <td>
              <input type="text" size="40" name="titletext" value="{/contents/content/contentdata/name}"
                     onkeyup="javascript: updateBreadCrumbHeader('titlename', this);"/>
            </td>
          </tr>
          <tr>
            <td>%fldLinkAction%:</td>
            <td>
              <select name="linktarget">
                <option value="download">%optDownloadFile%</option>
                <option value="_self">%optOpenInExistingWindow%</option>
                <option value="_blank">%optOpenInNewWindow%</option>
              </select>
            </td>
          </tr>
        </form>
      </table>
    </fieldset>
  </xsl:template>

  <!-- NOT IN USE -->
  <xsl:template name="insertimage">
    <fieldset>
      <legend>%blockInsertImage%</legend>
      <table border="0">

        <form name="imageform" method="post">
          <tr>
            <td width="140">%fldFilename%:</td>
            <td>
              <xsl:value-of select="/contents/content/contentdata/name"/>
            </td>
          </tr>
          <tr>
            <td>%fldAltText%:<span class="requiredfield">*</span></td>
            <td>
              <input type="text" name="alttext" size="40"/>
            </td>
          </tr>
          <tr>
            <td>%fldTitleText%:</td>
            <td colspan="2">
              <input type="text" size="40" name="titletext" value="{/contents/content/title}" onkeyup="javascript: updateBreadCrumbHeader('titlename', this);"/>
              <input type="checkbox" name="checkbox1" id="checkbox1"
                     value="true" title="%txtUseAltText%"
                     onclick="javascript:CMSBinary.copyText(this, document.getElementsByName('alttext')[0], document.getElementsByName('titletext')[0])"/>
              <label for="checkbox1">%txtUseAltText%</label>
            </td>
          </tr>
          <tr>
            <td>%fldVerticalMargin%:</td>
            <td>
              <input type="text" name="vmargin" size="4"/>
            </td>
          </tr>
          <tr>
            <td>%fldHorisontalMargin%:</td>
            <td>
              <input type="text" name="hmargin" size="4"/>
            </td>
          </tr>
          <tr>
            <td>%fldBorder%:</td>
            <td>
              <input type="text" name="border" size="4" value="0"/>
            </td>
          </tr>
          <tr>
            <td>%fldAlignment%:</td>
            <td>
              <select name="textflow">
                <option value="" selected="selected">%optAlignmentNone%</option>
                <option value="left">%optAlignmentLeft%</option>
                <option value="right">%optAlignmentRight%</option>
              </select>
            </td>
          </tr>
          <tr>
            <td valign="top">
              %fldPreview%:
            </td>
            <td>
              <img src="{$binaryURL}"/>
            </td>
          </tr>
        </form>
      </table>
    </fieldset>
  </xsl:template>

  <xsl:template name="insertflash">
    <xsl:variable name="key" select="/contents/content/contentdata/binarydata/@key"/>

    <form name="flashform" method="post">
      <fieldset>
        <legend>%blockInsertFlash%</legend>
        <table border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td valign="top">
              <input type="hidden" name="src" value="{$binaryURL}"/>
              <table border="0">
                <tr>
                  <td>%fldWidth%:<span class="requiredfield">*</span></td>
                  <td>
                    <input type="text" name="image_width" size="5"/>
                  </td>
                </tr>
                <tr>
                  <td>%fldHeight%:<span class="requiredfield">*</span></td>
                  <td>
                    <input type="text" name="image_height" size="5"/>
                  </td>
                </tr>
                <tr>
                  <td>%fldBgColor%:</td>
                  <td>
                    <table border="0" cellpadding="0" cellspacing="0">
                      <tr>
                        <td>
                          <input type="text" id="bgcolor" name="bgcolor" size="5" onchange="updateColor('bgcolor_pick','bgcolor');"/>
                        </td>
                        <td id="bgcolor_pickcontainer">
                         <!-- // -->
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
                <tr>
                  <td>%fldAlignment%:</td>
                  <td>
                    <select name="align">
                      <option value="" selected="selected">%optAlignmentNone%</option>
                      <option value="left">%optAlignmentLeft%</option>
                      <option value="right">%optAlignmentRight%</option>
                    </select>
                  </td>
                </tr>
              </table>
            </td>
            <td valign="top">
              <table border="0">
                <tr>
                  <td>%fldId%:</td>
                  <td>
                    <input type="text" name="elem_id" size="5"/>
                  </td>
                </tr>
                <tr>
                  <td>%fldWmode%:</td>
                  <td>
                    <select id="flash_wmode" name="flash_wmode">
                      <option value="">none</option>
                      <option value="window">window</option>
                      <option value="opaque">opaque</option>
                      <option value="transparent">transparent</option>
                    </select>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>

        <table border="0">
          <tr>
            <td>
              %fldPreview%:
            </td>
          </tr>
          <tr>
            <td>
              <div>
                <object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000"
                        codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0"
                        width="300" height="260">
                  <param name="src" value="{$binaryURL}"/>
                  <param name="wmode" value="opaque"/>
                  <embed wmode="opaque" src="{$binaryURL}" width="300" height="260"
                         type="application/x-shockwave-flash"/>
                </object>
              </div>
            </td>
          </tr>
        </table>
      </fieldset>
    </form>

  </xsl:template>
  <xsl:template name="insertcontent">
    <table border="0">

      <form name="filelinkform" method="post">
        <tr>
          <td width="140">%fldTitleText%:</td>
          <td>
            <xsl:value-of select="/contents/content/title"/>
          </td>
        </tr>
        <tr>
          <td>%fldLinkText%:<span class="requiredfield">*</span></td>
          <td>
            <input type="text" size="40" name="linktext" value="{/contents/content/title}" onchange="javascript: this.setAttribute('changed', 'true');"/>
            <input type="hidden" name="htmllinktext"/>
          </td>
        </tr>
        <tr>
          <td>%fldTitleAttribute%:</td>
          <td>
            <input type="text" size="40" name="titletext" value="{/contents/content/title}" onkeyup="javascript: updateBreadCrumbHeader('titlename', this);"/>
          </td>
        </tr>
        <tr>
          <td>%fldLinkAction%:</td>
          <td>
            <select name="linktarget">
              <option value="_self">%optOpenInExistingWindow%</option>
              <option value="_blank">%optOpenInNewWindow%</option>
            </select>
          </td>
        </tr>
      </form>
    </table>
  </xsl:template>

</xsl:stylesheet>
