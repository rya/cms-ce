<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
        <!ENTITY nbsp "&#160;">
        ]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output doctype-public="-//W3C//DTD HTML 4.01 Strict//EN" doctype-system="http://www.w3.org/TR/html4/strict.dtd" indent="yes" method="html"/>

  <xsl:include href="../common/genericheader.xsl"/>
  <xsl:include href="../common/generic_parameters.xsl"/>
  <xsl:include href="../common/categoryheader.xsl"/>
  <xsl:include href="../common/button.xsl"/>

  <xsl:param name="subop"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>%headInsertImage%</title>
        <link rel="stylesheet" type="text/css" href="javascript/tab.webfx.css"/>
        <link rel="stylesheet" type="text/css" href="css/admin.css"/>
        <link rel="stylesheet" type="text/css" href="javascript/lib/mootools/slider/evs-slider.css"/>

        <style type="text/css">
          .table-util td, .select-sm {
            font-size: 9px;
          }

          .select-sm {
            margin: 0
          }

          #preview-image-container {
            position: relative
          }

          #preview-loader-message {
            background-color: #36393D;
            color: #fff;
            left: 2px;
            opacity: .8;
            padding: 2px 4px;
            position: absolute;
            top: 2px;
          }
        </style>

        <script type="text/javascript">
          var cmslang = {
            sysMsgRequiredFields : '%sysMsgRequiredFields%'
          }
        </script>

        <xsl:if test="$subop = 'update'">
          <script type="text/javascript" src="tinymce/jscripts/tiny_mce/tiny_mce_popup.js">//</script>
        </xsl:if>
        
        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" src="javascript/tabpane.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/cms/cmsWindowHelper.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/tiny_mce/plugins/cmsimage/js/cmsimage.js">//</script>
        <script type="text/javascript" src="javascript/lib/mootools/mootools.js">//</script>
        <script type="text/javascript" src="javascript/lib/mootools/slider/evs-slider.js">//</script>
      </head>
      <body>

        <h1>
          <xsl:choose>
            <xsl:when test="$subop = 'update'">
              <span id='titlename'><xsl:value-of select="/contents/content/title"/></span>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name="genericheader">
                <xsl:with-param name="sitelink" select="true()"/>
                <xsl:with-param name="endslash" select="false()"/>
                <xsl:with-param name="links" select="$subop = 'insert'"/>
              </xsl:call-template>

              <xsl:call-template name="categoryheader">
                <xsl:with-param name="nolinks" select="$subop = 'update'"/>
                <xsl:with-param name="subop" select="$subop"/>
              </xsl:call-template>
              <xsl:text>&nbsp;</xsl:text>
              <span id='titlename'><xsl:value-of select="concat('/ ', /contents/content/title)"/></span>
            </xsl:otherwise>
          </xsl:choose>
        </h1>

        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td>
              <xsl:call-template name="buttons"/>
            </td>
          </tr>
          <tr>
            <td class="form_title_form_seperator">
              <br/>
            </td>
          </tr>
          <tr>
            <td>
              <div class="tab-pane" id="tab-pane-1">
                <form name="formAdmin" method="post">
                  <script type="text/javascript" language="JavaScript">
                    var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                  </script>
                  <div class="tab-page" id="tab-page-1">
                    <span class="tab">%blockInsertImage%</span>
                    <script type="text/javascript" language="JavaScript">
                      tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                    </script>
                    <xsl:call-template name="form-tab-1"/>
                  </div>
                </form>
              </div>
            </td>
          </tr>
          <tr>
            <td>
              <br/>
            </td>
          </tr>
          <tr>
            <td>
              <xsl:call-template name="buttons"/>
            </td>
          </tr>
        </table>

        <script type="text/javascript" language="JavaScript">
          setupAllTabs();
        </script>

      </body>
    </html>
  </xsl:template>

  <xsl:template name="form-tab-1">
    <fieldset>
      <legend>%blockInsertImage%</legend>
      
      <xsl:variable name="selectedBinary" select="contents/content/binaries/binary[@label = 'source']"/>
      <xsl:variable name="selectedBinaryName" select="$selectedBinary/@filename"/>

      <input type="hidden" name="subop" value="{$subop}"/>

      <input type="hidden" name="id" value=""/>
      <input type="hidden" name="name" value=""/>
      <input type="hidden" name="longdesc" value=""/>
      <input type="hidden" name="usemap" value=""/>
      <input type="hidden" name="ismap" value=""/>
      <input type="hidden" name="border" value=""/>
      <input type="hidden" name="align" value=""/>
      <input type="hidden" name="style" value=""/>
      <input type="hidden" name="cssclass" value=""/>

      <input type="hidden" name="selectedcontentkey" id="selectedcontentkey" value="{contents/content/@key}" />
      <input type="hidden" name="selectedbinaryname" id="selectedbinaryname" value="{$selectedBinaryName}" />

      <table border="0" cellspacing="1">
        <tr>
          <td>
            %fldImage%:
          </td>
          <td>
            <xsl:value-of select="/contents/content/contentdata/name"/>
          </td>
        </tr>
        <tr>
          <td>%fldAltText%:<span class="requiredfield">*</span>
          </td>
          <td colspan="2">
            <input type="text" style="width:234px" name="alt" value="{/contents/content/contentdata/name}" onkeyup="CMSImage.copyAltValueToTitleValue(document.getElementById('checkbox1'), this, document.getElementsByName('title')[0])"/>
          </td>
        </tr>
        <tr>
          <td>%fldTitleText%:</td>
          <td colspan="2">
            <table border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td>
                  <input type="text" style="width:234px" name="title" value="{/contents/content/contentdata/name}" onkeyup="updateBreadCrumbHeader( 'titlename', this, false );"/>
                </td>
                <td>
                  <input type="checkbox" name="checkbox1" id="checkbox1"
                         checked="true"
                         value="true" title="%txtUseAltText%"
                         onclick="CMSImage.copyAltValueToTitleValue(this, document.getElementsByName('alt')[0], document.getElementsByName('title')[0]); document.getElementsByName('title')[0].focus();"/>
                </td>
                <td>
                  <label for="checkbox1">%txtUseAltText%</label>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td valign="top">
            %fldSize%:<span class="requiredfield">*</span>
          </td>
          <td>
            <select name="size" id="size" onchange="CMSImage.changeSizeAction(this.value);">
              <option value="">%sysDropDownChoose%</option>
              <optgroup label="-- %imageSizeGroupRelativeSizes% --"/>
              <option value="full">%imageSizeOptFull%</option>
              <option value="wide">%imageSizeOptWide%</option>
              <option value="regular">%imageSizeOptRegular%</option>
              <option value="square">%imageSizeOptSquare%</option>
              <option value="list">%imageSizeOptList%</option>
              <option value="thumbnail">%imageSizeOptThumbnail%</option>
              <optgroup label="-- %imageSizeGroupExactSizes% --"/>
              <option value="original">%imageSizeOptOriginal%</option>
              <option value="custom">%imageSizeOptCustom%...</option>
            </select>
          </td>
        </tr>
        <tr id="custom-width-properties-container" style="display:none">
          <td>
            <xsl:comment>empty</xsl:comment>
          </td>
          <td>
            <table border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td>
                  <div id="custom-width-slider" class="slider">
                    <div class="slit">
                    </div>
                    <div class="knob">
                    </div>
                  </div>
                </td>
                <td>
                  <input type="text" name="customwidth" id="customwidth" value="150" style="margin-left:3px;width:40px; text-align:center;" onkeyup="restrictInt(this); CMSImage.changeSizeAction(document.getElementById('size').value);"/> pixels
                </td>
              </tr>
            </table>
          </td>
        </tr>
       
        <tr>
          <td valign="top">
            %fldPreview%:
          </td>
          <td>
            <div id="preview-image-container">
              <div id="preview-loader-message" style="display:none;">
                <img id="loader-image" src="images/loader-grey-white.gif" alt="" style="vertical-align:middle"/>
                Updating preview
              </div>
              <img id="preview-image" src="images/shim.gif" alt=""/>
            </div>
          </td>
        </tr>
        <tr>
          <td>
            <img src="images/shim.gif" style="width:120px; height:1px;" alt=""/>
          </td>
          <td>
            <xsl:comment>empty</xsl:comment>            
          </td>
        </tr>
      </table>
    </fieldset>
  </xsl:template>

  <xsl:template name="form-tab-2">
    <fieldset>
      <legend>%blockAppearance%</legend>
      <table border="0">
        <tr>
          <td>%fldBorder%:</td>
          <td>
            <input type="text" size="4" name="border"/>
          </td>
        </tr>
        <tr>
          <td>%fldAlignment%:</td>
          <td>
            <select name="align">
              <option value="" selected="selected">
                %optAlignmentNone%
              </option>
              <option value="left">
                %optAlignmentLeft%
              </option>
              <option value="right">
                %optAlignmentRight%
              </option>
            </select>
          </td>
        </tr>
        <tr>
          <td valign="top">%fldMargin%:</td>
          <td valign="top">
            <table cellpadding="0" cellspacing="0" border="0">
              <tr>
                <td>
                  <table cellpadding="1" cellspacing="0" border="0" class="table-util">
                    <tr>
                      <td>t:&nbsp;</td>
                      <td>
                        <input type="text" size="4" id="margint" name="margint"/>
                      </td>
                      <td>
                        <xsl:call-template name="unitselect">
                          <xsl:with-param name="name" select="'mtunit'"/>
                        </xsl:call-template>
                      </td>
                      <td>&nbsp;r:&nbsp;</td>
                      <td>
                        <input type="text" size="4" id="marginr" name="marginr"/>
                      </td>
                      <td>
                        <xsl:call-template name="unitselect">
                          <xsl:with-param name="name" select="'mrunit'"/>
                        </xsl:call-template>
                      </td>
                    </tr>
                    <tr>
                      <td>b:&nbsp;</td>
                      <td>
                        <input type="text" size="4" id="marginb" name="marginb"/>
                      </td>
                      <td>
                        <xsl:call-template name="unitselect">
                          <xsl:with-param name="name" select="'mbunit'"/>
                        </xsl:call-template>
                      </td>
                      <td>&nbsp;l:&nbsp;</td>
                      <td>
                        <input type="text" size="4" id="marginl" name="marginl"/>
                      </td>
                      <td>
                        <xsl:call-template name="unitselect">
                          <xsl:with-param name="name" select="'mlunit'"/>
                        </xsl:call-template>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
            <input type="hidden" name="marginunit" value="px"/>
            <!--input type="text" size="4" name="vmargin"/-->
          </td>
        </tr>
        <tr>
          <td>
            <br/>
          </td>
        </tr>
      </table>
    </fieldset>
  </xsl:template>

  <xsl:template name="unitselect">
    <xsl:param name="name"/>
    <select name="{$name}" class="select-sm">
      <option value="px">px</option>
      <option value="em">em</option>
      <option value="ex">ex</option>
      <option value="%">%</option>
    </select>
  </xsl:template>

  <xsl:variable name="cancelFunction">
    <xsl:choose>
      <xsl:when test="$subop = 'update'">
        <xsl:text>window.close()</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>CMSImage.cancel()</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="insertUpdateText">
    <xsl:choose>
      <xsl:when test="$subop = 'update'">
        <xsl:text>%cmdUpdate%</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>%cmdInsert%</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:template name="buttons">
    <xsl:call-template name="button">
      <xsl:with-param name="type" select="'button'"/>
      <xsl:with-param name="name" select="'insert'"/>
      <xsl:with-param name="caption" select="$insertUpdateText"/>
      <xsl:with-param name="onclick" select="'javascript:CMSImage.insert();'"/>
    </xsl:call-template>&nbsp;<xsl:call-template name="button">
    <xsl:with-param name="type" select="'button'"/>
    <xsl:with-param name="caption" select="'%cmdCancel%'"/>
    <xsl:with-param name="onclick" select="$cancelFunction"/>
  </xsl:call-template>
  </xsl:template>

</xsl:stylesheet>
