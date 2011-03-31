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

  <xsl:output method="html"/>

  <xsl:include href="common/standard_form_templates.xsl"/>
  <xsl:include href="common/textarea.xsl"/>
  <xsl:include href="common/textfield.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>

  <xsl:param name="create"/>
  <xsl:param name="contenttypekey"/>
  <xsl:param name="cistype"/>
  <xsl:param name="cisvalue"/>

  <xsl:variable name="disabled">
    <xsl:choose>
      <xsl:when test="boolean(/contents/content/contentdata)">
        <xsl:text>yes</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>no</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="scalemax" select="500"/>

  <xsl:variable name="current-version-key" select="/contents/content/@versionkey"/>
  
  <xsl:template match="/">
    <html>
      <head>
        <link type="text/css" rel="stylesheet" href="css/admin.css"/>
        <link type="text/css" rel="stylesheet" href="css/menu.css"/>
        <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>
        <link rel="stylesheet" type="text/css" href="css/assignment.css"/>
        <link rel="stylesheet" type="text/css" href="css/calendar_picker.css"/>
        <link rel="stylesheet" type="text/css" href="javascript/cms/ui/style.css"/>

        <xsl:if test="$subop = 'relatedcontent' or
                      $subop = 'relatedfiles' or
                      $subop = 'relatedfile' or
                      $subop = 'relatedimages' or
                      $subop = 'relatedimage' or
                      $subop = 'addcontenttosection' or
                      $subop = 'insert'">
          <script type="text/javascript" src="javascript/window.js"/>
          <script type="text/javascript">
            cms.window.attatchKeyEvent('close');
          </script>
        </xsl:if>
                
        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" src="javascript/accessrights.js">//</script>
        <script type="text/javascript" src="javascript/validate.js">//</script>
        <script type="text/javascript" src="javascript/tabpane.js">//</script>
        <script type="text/javascript" src="javascript/menu.js">//</script>
        <script type="text/javascript" src="javascript/calendar_picker.js">//</script>
        <script type="text/javascript" src="javascript/properties.js">//</script>
        <script type="text/javascript" src="javascript/browserDetect.js">//</script>
        <script type="text/javascript" src="javascript/content_form.js">//</script>

        <script type="text/javascript" src="javascript/cms/core.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Event.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Cookie.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Css.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Dimensions.js">//</script>
        <script type="text/javascript" src="javascript/cms/ui/SplitButton.js">//</script>

        <xsl:call-template name="waitsplash"/>

        <script type="text/javascript" language="JavaScript">
          // Globals
          var g_validImageFileTypesPattern = /\.(jpg|jpeg|png|gif|tif|tiff|bmp)$/i;

          // variables used by menu.js
          var branchOpen = new Array();
          var cookiename = "contentform";

          function isIE()
          {
            var browserName = BrowserDetect.browser.toLowerCase();
            var browserVersion = BrowserDetect.version;
						if ( browserName.indexOf('explorer') &gt; -1 )
						{
							return true;
						}
						else 
						{
							return false;
						}
					}
          // -------------------------------------------------------------------------------------------------------------------------------

          var validatedFields = new Array();
          var g_validatedFields_index = 0;

          <xsl:if test="$create = 1">
            <xsl:text>validatedFields[g_validatedFields_index++] = new Array("%fldFile%", "origimagefilename", validateRequired);</xsl:text>
          </xsl:if>

          validatedFields[g_validatedFields_index++] = new Array("%fldChooseFile%", "origimagefilename", validateRegexp, g_validImageFileTypesPattern, "%msgNotValidImageFileType%");
          validatedFields[g_validatedFields_index++] = new Array("%fldName%", "name", validateRequired);

          // -------------------------------------------------------------------------------------------------------------------------------

          function validateAll(formName)
          {
            enableAll();
            var f = document.forms[formName];

            <xsl:if test="not(/contents/userright) or /contents/userright/@publish = 'true'">
              if ( !checkAll(formName, extraValidatedFields) )
                return;
            </xsl:if>

            if ( !checkAll(formName, validatedFields) )
              return;

            tabPane1.element.style.display="none";
            waitsplash();
            disableFormButtons();
            f.submit();
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          var maxImageWidth = 500;
          var ratio = 0;
          var orgwidth = 0;
          var orgheight = 0;
          var theImageFile = "";
          var borderSize= "1";

          function updateImage(where, imagefile) {
            document.getElementsByName('originalwidth')[0].value = '';
            document.getElementsByName('originalheight')[0].value = '';

            enableAll();

            var ext = imagefile;
            ext = ext.substring(ext.length-3,ext.length);
            ext = ext.toLowerCase();
            var buttons = new Array( "undraftbtn", "rejectbtn", "previewbtn" );

            if( !imagefile.match(g_validImageFileTypesPattern) )
            {
              document.getElementById("origimagefilename").style.color = "#FF0000";
              document.getElementById("name").value = '';
              where.innerHTML = '';
              return;
             } else {
              document.getElementById("origimagefilename").style.color = "#000000";
            }


          
            <xsl:if test="$create = '1'">
              if (document.getElementById('publishbtn') != null) {
                setTextButtonEnabled( document.getElementById('publishbtn'), true );
              }

              if (document.getElementById('undraftbtn') != null) {
                  setTextButtonEnabled( document.getElementById('undraftbtn'), true );
              }
            </xsl:if>

            theImageFile = imagefile;

            // This only works in IE &lt; 7 due to security fixes in &gt; 7
            if ( isIE() ) {
              where.innerHTML = "&lt;img name='img" + where.name + "' src=\"file://" + imagefile + "\" onload=\"javascript:imageLoaded('"+where.name+"');\">";
            }

            document.getElementById('newimage').value = "true";

            var proposedName = getFilenameWithoutExtension(imagefile);
            document.getElementById('name').value = proposedName;
            document.getElementsByName('description')[0].focus();
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          function getFilenameWithoutExtension(fullPath) {
            var proposedName = fullPath.match(/[^\/\\]+$/);
            var dotIndex = proposedName.toString().indexOf('.');
            if(dotIndex > -1) {
              var temp = proposedName.toString().split('.');
              proposedName = temp[0];
            }

            // Mac
            var forwardSlashIndex = proposedName.lastIndexOf('/');
            if ( forwardSlashIndex > 0) {
              proposedName = proposedName.substr(0, forwardSlashIndex);
            }
            return proposedName;
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          function imageLoaded( name )
          {
            document.getElementById('trorgimage').style.display = '';

            document.getElementById('widthheight').innerHTML = "%msgOriginalImageSize%: "+ document.getElementById('img'+ name).width + "x"+ document.getElementById('img'+ name).height;

            document.getElementById('originalwidth').value = document.getElementById('img'+ name).width;
            document.getElementById('originalheight').value = document.getElementById('img'+ name).height;
            orgwidth = document.getElementById('img'+ name).width;
            orgheight = document.getElementById('img'+ name).height;
                    
            if (document.getElementById('img'+ name).width &gt; 500)
              document.getElementById('img'+ name).width = 500;

            enableAll();
          }
                    
          function enableAll()
					{
            document.getElementById('rotate1').removeAttribute("disabled");
            document.getElementById('rotate2').removeAttribute("disabled");
            document.getElementById('rotate3').removeAttribute("disabled");
            document.getElementById('rotate4').removeAttribute("disabled");
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          function warpWidthAndHeight()
          {
            var array = document.getElementByName['rotate'];
            for( var i = 0; i &lt; array.length; ++i )
            {
              if ( array[i].checked ) {
                if (array[i].value == "90left")  {
                  return true;
                }
                else if ( array[i].value == "90right" )
                {
                  return true;
                }
                else
                {
                  return false;
                }
              }
            }
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          function changeWidth( imagename )
          {
            if(warpWidthAndHeight())
            {
              ratio = window.document.getElementById(imagename +"width").value / orgheight;

              // Update width
              var newWidth = Math.round(ratio * orgwidth);
              window.document.getElementById(imagename + "height").value = newWidth;
            }
            else
            {
              ratio = window.document.getElementById(imagename +"width").value / orgwidth;

              // Update height
              var newHeight = Math.round(ratio * orgheight);
              window.document.getElementById(imagename + "height").value = newHeight;
            }
          }

          function changeHeight( imagename )
          {
            if( warpWidthAndHeight() ) {
              ratio = window.document.getElementById(imagename +"height").value / orgwidth;

              // Update height
              var newHeight = Math.round(ratio * orgheight);
              window.document.getElementById(imagename + "width").value = newHeight;
            }
            else
            {
              ratio = window.document.getElementById(imagename +"height").value / orgheight;

              // Update width
              var newWidth = Math.round(ratio * orgwidth);
              window.document.getElementById(imagename + "width").value = newWidth;
            }
          }
          // -------------------------------------------------------------------------------------------------------------------------------
                    
					function insert_file_onclick( view, key, object, subop )
          {
						var row = getObjectIndex(object);
						OpenContentPopupByHandler(-1, -1, subop, view, row, "com.enonic.vertical.adminweb.handlers.ContentFileHandlerServlet");
					}                           
          // -------------------------------------------------------------------------------------------------------------------------------
					
					function addRelatedFiles(fieldName, fieldRow, content_key, content_title )
          {
						
						var keyField;
						var nameField;
						
						if ( fieldRow == 'none' )
            {
							keyField = document.getElementById('relatedfile');
							nameField = document.getElementById('relatedfilename');
						}
						else
            {
							keyField = document.getElementsByName('relatedfile')[fieldRow];
							nameField = document.getElementsByName('relatedfilename')[fieldRow];
						}
						keyField.value = content_key;
						nameField.value = content_title;
					}
          // -------------------------------------------------------------------------------------------------------------------------------

          function removeFileRow( tableName, object, viewname, keyname  )
          {
            document.formAdmin[keyname].value = "";
            document.formAdmin[viewname].value = "";
            return;
          }
        </script>
      </head>
        <body>
            <script type="text/javascript">waitsplash();</script>

            <xsl:call-template name="contentform">
                <xsl:with-param name="multipart" select="true()"/>
            </xsl:call-template>
            <script type="text/javascript">removeWaitsplash();</script>
        </body>

        <xsl:if test="$create = '1'">
            <script type="text/javascript" language="JavaScript">
                // Disable buttons
                if (document.getElementById('savebtn') != null)
                setTextButtonEnabled( document.getElementById('savebtn'), false );
                if (document.getElementById('publishbtn') != null)
                setTextButtonEnabled( document.getElementById('publishbtn'), false );
                if (document.getElementById('undraftbtn') != null)
                setTextButtonEnabled( document.getElementById('undraftbtn'), false );
            </script>
        </xsl:if>
    </html>

  </xsl:template>

    <xsl:template name="contenttypeform">
        <xsl:param name="readonly"/>
		
        <xsl:if test="$create = 0">
            <input type="hidden" name="originalbinarydatakey">
                <xsl:attribute name="value"><xsl:value-of select="contents/content/contentdata/images/image[@type='original']/binarydata/@key"/></xsl:attribute>
            </input>
            <input type="hidden" name="customwidth">
                <xsl:attribute name="value"><xsl:value-of select="contents/content/contentdata/images/image[@type='custom']/width"/></xsl:attribute>
            </input>
            <input type="hidden" name="customheight">
                <xsl:attribute name="value"><xsl:value-of select="contents/content/contentdata/images/image[@type='custom']/height"/></xsl:attribute>
            </input>
            <input type="hidden" name="custombinarydatakey">
                <xsl:attribute name="value"><xsl:value-of select="contents/content/contentdata/images/image[@type='custom']/binarydata/@key"/></xsl:attribute>
            </input>
            <input type="hidden" name="sourceimagekey">
                <xsl:attribute name="value"><xsl:value-of select="contents/content/contentdata/sourceimage/binarydata/@key"/></xsl:attribute>
            </input>
        </xsl:if>


        <xsl:variable name="help">
            <help alwayson="false">%txtDescSupportedImgFormats%</help>
        </xsl:variable>

        <!-- Set to true if a new image is loaded. -->
        <input type="hidden" name="newimage" id="newimage" value="false"/>

        <fieldset>
            <legend>&nbsp;%blockGeneral%&nbsp;</legend>
            <table border="0" cellspacing="0" cellpadding="0" class="formtable">
                <tr>
                    <xsl:call-template name="filefield">
                        <xsl:with-param name="label" select="'%fldChooseFile%:'"/>
                        <xsl:with-param name="name" select="'origimagefilename'"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'256'"/>
                        <xsl:with-param name="imagekey" select="'0'"/>
                        <xsl:with-param name="disabled" select="$readonly"/>
                        <xsl:with-param name="required" select="$create = 1"/>
                        <xsl:with-param name="onchange">updateImage(document.getElementById('orgimage'), this.value);</xsl:with-param>
                        <xsl:with-param name="helpelement" select="$help"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'name'"/>
                        <xsl:with-param name="label" select="'%fldName%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/name"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                        <xsl:with-param name="required" select="'true'"/>
                        <xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textarea">
                        <xsl:with-param name="name" select="'description'"/>
                        <xsl:with-param name="label" select="'%fldDescription%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/description"/>
                        <xsl:with-param name="rows" select="'5'"/>
                        <xsl:with-param name="cols" select="'60'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                        <xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'photographername'"/>
                        <xsl:with-param name="label" select="'%fldPhotographerName%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/photographer/@name"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                        <xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'photographeremail'"/>
                        <xsl:with-param name="label" select="'%fldPhotographerEmail%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/photographer/@email"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                        <xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'copyright'"/>
                        <xsl:with-param name="label" select="'%fldCopyright%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/copyright"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                        <xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>

                    <xsl:variable name="keywords-help-element">
                        <xsl:element name="help">
                            <xsl:text>%hlpImageKeyWords%</xsl:text>
                        </xsl:element>
                    </xsl:variable>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'keywords'"/>
                        <xsl:with-param name="label" select="'%fldKeywords%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/keywords"/>
                        <xsl:with-param name="helpelement" select="$keywords-help-element"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'100'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                        <xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
            </table>
        </fieldset>
        <fieldset>
            <legend>&nbsp;%blockImage%&nbsp;</legend>

            <table border="0" cellspacing="0" cellpadding="0" class="formtable">
                <tr>
                    <td class="form_labelcolumn" valign="baseline">
                        %fldRotateImage%:
                    </td>
                    <td valign="baseline">
                        <table cellspacing="0" callpadding="0" border="0">
                            <tr>
                                <td>
                                    <input type="radio" name="rotate" id="rotate1" value="none" checked="checked">
                                        <xsl:if test="$disabled = 'yes' or $readonly">
                                            <xsl:attribute name="disabled">disabled</xsl:attribute>
                                        </xsl:if>
                                    </input>
                                    %fldRotNone% &nbsp;
                                </td>
                                <td>
                                    <input type="radio" name="rotate" id="rotate2" value="90left">
                                        <xsl:if test="$disabled = 'yes' or $readonly">
                                            <xsl:attribute name="disabled">disabled</xsl:attribute>
                                        </xsl:if>
                                        <xsl:if test="/contents/content/contentdata/images/image[@type='original']/@rotation='90left'">
                                            <xsl:attribute name="checked">
                                                <xsl:text>checked</xsl:text>
                                            </xsl:attribute>
                                        </xsl:if>
                                    </input>
                                    %fldRot90Left% &nbsp;
                                </td>
                                <td>
                                    <input type="radio" name="rotate" id="rotate3" value="90right">
                                        <xsl:if test="$disabled = 'yes' or $readonly">
                                            <xsl:attribute name="disabled">disabled</xsl:attribute>
                                        </xsl:if>
                                        <xsl:if test="/contents/content/contentdata/images/image[@type='original']/@rotation='90right'">
                                            <xsl:attribute name="checked">
                                                <xsl:text>checked</xsl:text>
                                            </xsl:attribute>
                                        </xsl:if>
                                    </input>
                                    %fldRot90Right% &nbsp;
                                </td>
                                <td>
                                    <input type="radio" name="rotate" id="rotate4" value="180">
                                        <xsl:if test="$disabled = 'yes' or $readonly">
                                            <xsl:attribute name="disabled">disabled</xsl:attribute>
                                        </xsl:if>
                                        <xsl:if test="/contents/content/contentdata/images/image[@type='original']/@rotation='180'">
                                            <xsl:attribute name="checked">
                                                <xsl:text>checked</xsl:text>
                                            </xsl:attribute>
                                        </xsl:if>
                                    </input>
                                    %fldRot180%
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>

                <tr id="trorgimage">
                    <xsl:if test="$create = 1">
                        <xsl:attribute name="style">display:none</xsl:attribute>
                    </xsl:if>
                    <td class="form_labelcolumn" valign="top">%fldPreview%:</td>
                    <td>
                        <div id="orgimage" name="orgimage">
                            <xsl:if test="$create = 0">

                                <xsl:variable name="label-string-for-preview-image">
                                    <xsl:choose>
                                        <xsl:when test="/contents/content/binaries/binary[@label = 'medium']">
                                            <xsl:text>/label/medium</xsl:text>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:text>/label/source</xsl:text>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:variable>

                                <img alt="original image" name="imgorgimage" id="imgorgimage">
                                    <xsl:attribute name="src">
                                        <xsl:text>_image/</xsl:text>
                                        <xsl:value-of select="/contents/content/@key"/>
                                        <xsl:value-of select="$label-string-for-preview-image"/>
                                        <xsl:text>?_filter=scalemax(</xsl:text>
                                        <xsl:value-of select="$scalemax"/>
                                        <xsl:text>)&amp;_version=</xsl:text>
                                        <xsl:value-of select="$current-version-key"/>
                                    </xsl:attribute>
                                </img>
                            </xsl:if>
                        </div>
                        <div name="widthheight" id="widthheight">
                            <xsl:if test="$disabled = 'yes' or $readonly">
                                <xsl:text>%msgOriginalImageSize%: </xsl:text>
                                <xsl:value-of select="/contents/content/contentdata/images/image[@type='original']/width"/>
                                <xsl:text>px</xsl:text>
                                <xsl:text> * </xsl:text>
                                <xsl:value-of select="/contents/content/contentdata/images/image[@type='original']/height"/>
                                <xsl:text>px</xsl:text>
                            </xsl:if>
                        </div>
                        <input type="hidden" name="originalwidth" value="{/contents/content/contentdata/images/image[@type = 'original']/width}"/>
                        <input type="hidden" name="originalheight" value="{/contents/content/contentdata/images/image[@type = 'original']/height}"/>
                    </td>
                </tr>
                <xsl:if test="/contents/content/contentdata/sourceimage">
                    <tr>
                        <td colspan="2">
                            <br/>
                        </td>
                    </tr>
                    <tr>
                        <td class="form_labelcolumn">
                            %fldSourceImage%:
                        </td>
                        <td>
                            <a title="%fldKey%: {/contents/content/@key}"
                               href="_image/{/contents/content/@key}/label/source?_version={$current-version-key}" target="_blank">
                                <xsl:value-of select="/contents/content/contentdata/sourceimage/@width"/>
                                <xsl:text>px</xsl:text>
                                <xsl:text> * </xsl:text>
                                <xsl:value-of select="/contents/content/contentdata/sourceimage/@height"/>
                                <xsl:text>px</xsl:text>
                            </a>
                        </td>
                    </tr>
                </xsl:if>
                <xsl:if test="/contents/content/binaries/binary[@label]">
                    <tr>
                        <td colspan="2">
                            <br/>
                        </td>
                    </tr>
                    <tr>
                        <td class="form_labelcolumn" valign="top">
                            <xsl:text>Labels:</xsl:text>
                        </td>
                        <td valign="top">
                            <ul style="list-style-type:none; padding:0; padding:0">
                                <xsl:if test="/contents/content/binaries/binary[@label = 'source']">
                                    <li>
                                        <a href="_image/{/contents/content/@key}/label/source?_version={$current-version-key}" target="_blank">
                                            <xsl:text>source</xsl:text>
                                        </a>
                                    </li>
                                </xsl:if>
                                <xsl:if test="/contents/content/binaries/binary[@label = 'small']">
                                    <li>
                                        <a href="_image/{/contents/content/@key}/label/small?_version={$current-version-key}" target="_blank">
                                            <xsl:text>small</xsl:text>
                                        </a>
                                    </li>
                                </xsl:if>
                                <xsl:if test="/contents/content/binaries/binary[@label = 'medium']">
                                    <li>
                                        <a href="_image/{/contents/content/@key}/label/medium?_version={$current-version-key}" target="_blank">
                                            <xsl:text>medium</xsl:text>
                                        </a>
                                    </li>
                                </xsl:if>
                                <xsl:if test="/contents/content/binaries/binary[@label = 'large']">
                                    <li>
                                        <a href="_image/{/contents/content/@key}/label/large?_version={$current-version-key}" target="_blank">
                                            <xsl:text>large</xsl:text>
                                        </a>
                                    </li>
                                </xsl:if>
                            </ul>
                        </td>
                    </tr>
                </xsl:if>
            </table>
        </fieldset>
        <fieldset>
            <legend>&nbsp;%blockRelatedFile%&nbsp;</legend>
            <table border="0" cellspacing="0" cellpadding="0" class="formtable" name="filetablerelatedfile">
                <tr>
                    <td class="form_labelcolumn">
                        %fldFile%:
                    </td>
                    <td>
                        <table cellspacing="0" cellpadding="0" border="0">
                            <tbody name="filetablerelatedfile" id="filetablerelatedfile">

                                <xsl:variable name="relatedfile_key">
                                    <xsl:choose>
                                        <xsl:when test="/contents/content/contentdata/file"><xsl:value-of select="/contents/content/contentdata/file/@key"/></xsl:when>
                                        <xsl:otherwise></xsl:otherwise>
                                    </xsl:choose>
                                </xsl:variable>

                                <tr>
                                    <td>
                                        <input type="text" readonly="readonly" size="60" name="relatedfilename" value="{//content[@key = $relatedfile_key]/contentdata/name}"/>
                                        <input type="hidden" name="relatedfile" value="{$relatedfile_key}"/>
                                        <xsl:call-template name="button">
                                            <xsl:with-param name="name" select="'choosefilerelatedfile'"/>
                                            <xsl:with-param name="type" select="'button'"/>
                                            <xsl:with-param name="image" select="'images/icon_browse.gif'"/>
                                            <xsl:with-param name="disabled" select="$readonly"/>
                                            <xsl:with-param name="onclick">
                                                <xsl:text>javascript:insert_file_onclick('relatedfile', 'relatedfile', this, 'relatedfile');</xsl:text>
                                            </xsl:with-param>
                                        </xsl:call-template>
                                        <xsl:call-template name="button">
                                            <xsl:with-param name="name" select="'removefilerowrelatedfile'"/>
                                            <xsl:with-param name="type" select="'button'"/>
                                            <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                                            <xsl:with-param name="disabled" select="$readonly"/>
                                            <xsl:with-param name="onclick">
                                                <xsl:text>javascript:removeFileRow('filetablerelatedfile', this, 'relatedfilename', 'relatedfile');</xsl:text>
                                            </xsl:with-param>
                                        </xsl:call-template>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
            </table>
        </fieldset>
    </xsl:template>
</xsl:stylesheet>