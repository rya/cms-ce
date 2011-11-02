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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:output method="html"/>

  <xsl:param name="type"/>
  <xsl:param name="sub"/>
  <xsl:param name="categorykey"/>
  <xsl:param name="mode"/>
  <xsl:param name="returnkey"/>
  <xsl:param name="returnview"/>
  <xsl:param name="row" select="none"/>
  <xsl:param name="subop" select="none"/>
  <xsl:param name="reloadcategories" select="'false'"/>
  <xsl:param name="page"/>

  <xsl:template match="/">
    <html>
      <head>
        <script type="text/javascript" language="JavaScript">
          <xsl:if test="$reloadcategories = 'true'">
            window.top.frames.categories.location = window.top.frames.categories.location;
          </xsl:if>


				  function modifyFile(key)
				  {
					var f = document.selectFile;

                                        f.action = <xsl:text>"adminpage?page=1029&amp;op=modify&amp;key=" + key + "&amp;mode=</xsl:text><xsl:value-of select="$mode"/><xsl:text>&amp;categorykey=</xsl:text><xsl:value-of select="$categorykey"/><xsl:text>";</xsl:text>
					f.redirecturl.value = window.location;
					f.previouspage.value = window.location;
					f.submit();
				  }

				  function selectFile(key, op, fname)
				  {
        <xsl:choose>
          <xsl:when test="$mode = 'module'">
            <xsl:choose>
              <xsl:when test="$row != 'none'">
                <xsl:text>window.top.opener.document.all('</xsl:text><xsl:value-of select="$returnkey"/><xsl:text>')[</xsl:text><xsl:value-of select="$row"/><xsl:text>].value = key;</xsl:text>
                <xsl:text>window.top.opener.document.all('</xsl:text><xsl:value-of select="$returnview"/><xsl:text>')[</xsl:text><xsl:value-of select="$row"/><xsl:text>].value = fname;</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>window.top.opener.document.all('</xsl:text><xsl:value-of select="$returnkey"/><xsl:text>').value = key;</xsl:text>
              <xsl:text>window.top.opener.document.all('</xsl:text><xsl:value-of select="$returnview"/><xsl:text>').value = fname;</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
          window.top.close();
        </xsl:when>
        <xsl:otherwise>
					var f = document.selectFile;

                                        <xsl:text>f.action = "adminpage?page=1029&amp;op=insert&amp;key="+ key + "&amp;mode=</xsl:text><xsl:value-of select="$mode"/><xsl:text>&amp;subop="+ op;</xsl:text>
					f.submit();
        </xsl:otherwise>
      </xsl:choose>
                                      }


				  function deleteFile(key)
				  {
					var f = document.selectFile;

					if (confirm('%alertAreYouSure%') == false) {
					  return;
					}

                                        f.action = "adminpage?page="+ <xsl:value-of select="$page"/> +"&amp;op=remove&amp;key=" + key +"&amp;cat="+ <xsl:value-of select="$categorykey"/>;
					f.redirecturl.value = window.location;
                                        f.previouspage.value = window.location;
					f.submit();
				  }

				function MM_swapImgRestore()
				{ //v3.0
					var i,x,a=document.MM_sr; for(i=0;a&amp;&amp;i&lt;a.length&amp;&amp;(x=a[i])&amp;&amp;x.oSrc;i++) x.src=x.oSrc;
				}

				function MM_swapImage()
				{ //v3.0
					  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i&lt;(a.length-2);i+=3)
					   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
				}

				function MM_preloadImages()
				{ //v3.0
				  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
					var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i&lt;a.length; i++)
					if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
				}

				function MM_findObj(n, d)
				{ //v3.0
				  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&amp;&amp;parent.frames.length) {
					d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
				  if(!(x=d[n])&amp;&amp;d.all) x=d.all[n]; for (i=0;!x&amp;&amp;i&lt;d.forms.length;i++) x=d.forms[i][n];
				  for(i=0;!x&amp;&amp;d.layers&amp;&amp;i&lt;d.layers.length;i++) x=MM_findObj(n,d.layers[i].document); return x;
				}

        </script>
		<link rel="stylesheet" type="text/css" href="css/admin.css"/>
      </head>

     <body>

        <table width="100%" border="0" cellpadding="1" cellspacing="0">

          <form name="newfile" method="POST" action="">
            <input type="hidden" name="previouspage"/>
          </form>

          <form name="newcategory" method="POST" action="">
            <input type="hidden" name="previouspage"/>
          </form>

          <form name="changecat">
            <input type="hidden" name="newcat"
              onpropertychange="javascript: selectCategory(this.value);"/>
          </form>

          <form name="selectFile" method="post">
            <xsl:attribute name="action"></xsl:attribute>
            <input type="hidden" name="redirecturl"/>
            <input type="hidden" name="previouspage"/>
          </form>
          <xsl:call-template name="items"/>

          <xsl:if test="$subop = 'search'">
            <xsl:if test="count(/contents/content) = 0">
              <div style="margin-left: 30%; margin-right: 15%; margin-top: 20%;">
                <b>%msgNoImagesFound%</b>
              </div>
            </xsl:if>
          </xsl:if>

        </table>

        <div id="wait" style="z-index: 2; display: none; position: absolute; width: 100%; height: 100%; top: 0px; left: 3px; background-color: #ffffff;">
          <table width="100%" height="100%">
            <tr>
              <td valign="center" align="center">%msgUploadingFile%</td>
            </tr>
          </table>
        </div>

      </body>
    </html>
  </xsl:template>

  <xsl:template name="items">
      <tr>
        <td colspan="5">
          <table border="0" cellspacing="0" cellpadding="0" width="100%">
            <xsl:for-each select="/contents/content">
              <xsl:sort select="contentdata/name"/>

              <xsl:variable name="allowinsert">
                <xsl:call-template name="getsuffix">
                  <xsl:with-param name="fname" select="contentdata/name"/>
                </xsl:call-template>
              </xsl:variable>


              <tr>
                <td width="72%" align="left">

                  <a class="file" target="_blank" title="%tipPreview%" style="font: icon">
                    <xsl:attribute name="href">binary?id=<xsl:value-of select="contentdata/binarydata/@key"/></xsl:attribute>
                    <xsl:call-template name="chooseicon">
                      <xsl:with-param name="filename" select="contentdata/name"/>
                    </xsl:call-template>
                    <xsl:value-of select="contentdata/name"/>
                  </a>
                </td>

                <td width="5%" align="right" nowrap="nowrap" class="file" style="font: icon">
                  <xsl:call-template name="convert_filesize">
                    <xsl:with-param name="fsize" select="contentdata/filesize"/>
                  </xsl:call-template>
                </td>

                <td width="23%" nowrap="nowrap" align="right">
                  <xsl:call-template name="fileoperations">
                    <xsl:with-param name="allowinsert"><xsl:value-of select="$allowinsert"/></xsl:with-param>
                    <xsl:with-param name="key"><xsl:value-of select="@key"/></xsl:with-param>
                    <xsl:with-param name="binarykey"><xsl:value-of select="contentdata/binarydata/@key"/></xsl:with-param>
                  </xsl:call-template>
                </td>
      </tr>
    </xsl:for-each>
  </table>
</td>
</tr>
</xsl:template>




  <xsl:template name="chooseicon">
    <xsl:param name="filename"/>

    <xsl:variable name="fname">
      <xsl:call-template name="getsuffix">
        <xsl:with-param name="fname" select="contentdata/name"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$fname='zip' or $fname='gz' or $fname='tar' or $fname='arj' or $fname='rar'">
        <img border="0" width="23" height="16" src="images/icon_zip.gif"/>
      </xsl:when>

      <xsl:when test="$fname='avi' or $fname='mp3' or $fname='mpeg' or $fname='mpg' or $fname='midi' or $fname='mid' or $fname='av' or $fname='wav'">
        <img border="0" width="23" height="16" src="images/icon_avi.gif"/>
      </xsl:when>

      <xsl:when test="$fname='ppt'">
        <img border="0" width="23" height="16" src="images/icon_ppt.gif"/>
      </xsl:when>

      <xsl:when test="$fname='doc' or $fname='rtf'">
	     <img border="0" width="23" height="16" src="images/icon_doc.gif"/>
	  </xsl:when>

      <xsl:when test="$fname='xls'">
	     <img border="0" width="23" height="16" src="images/icon_xls.gif"/>
	  </xsl:when>

      <xsl:when test="$fname='swf'">
        <img border="0" width="23" height="16" src="images/icon_swf.gif"/>
      </xsl:when>

      <xsl:when test="$fname='txt' or $fname='text' or $fname='xml'">
        <img border="0" width="23" height="16" src="images/icon_txt.gif"/>
      </xsl:when>

      <xsl:when test="$fname='tif' or $fname='tiff'">
        <img border="0" width="23" height="16" src="images/icon_tif.gif"/>
      </xsl:when>

      <xsl:when test="$fname='bmp'">
        <img border="0" width="23" height="16" src="images/icon_bmp.gif"/>
      </xsl:when>

      <xsl:when test="$fname='xsl'">
        <img border="0" width="23" height="16" src="images/icon_xsl.gif"/>
      </xsl:when>

	  <xsl:when test="$fname='eps'">
        <img border="0" width="23" height="16" src="images/icon_eps.gif"/>
      </xsl:when>

      <xsl:when test="$fname='pdf'">
        <img border="0" width="23" height="16" src="images/icon_pdf.gif"/>
      </xsl:when>

      <xsl:when test="$fname='jpg' or $fname='jpeg'">
        <img border="0" width="23" height="16" src="images/icon_jpg.gif"/>
      </xsl:when>

      <xsl:when test="$fname='jpeg'">
        <img border="0" width="23" height="16" src="images/icon_jpg.gif"/>
      </xsl:when>

      <xsl:when test="$fname='gif' or $fname='png'">
        <img border="0" width="23" height="16" src="images/icon_gif.gif"/>
      </xsl:when>

      <xsl:when test="$fname='htm' or $fname='html'">
	    <img border="0" width="23" height="16" src="images/icon_htm.gif"/>
	  </xsl:when>

      <xsl:when test="$fname='qt'">
	     <img border="0" width="23" height="16" src="images/icon_htm.gif"/>
	  </xsl:when>

      <xsl:otherwise>
        <img border="0" width="23" height="16" src="images/icon_sys.gif"/>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>


  <xsl:template name="getsuffix">
    <xsl:param name="fname"/>
    <xsl:variable name="lcletters">abcdefghijklmnopqrstuvwxyz</xsl:variable>
    <xsl:variable name="ucletters">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
    <xsl:variable name="suffix"><xsl:value-of select="substring-after($fname, '.')"/></xsl:variable>
    <xsl:choose>
      <xsl:when test="$suffix!=''">
        <xsl:call-template name="getsuffix">
          <xsl:with-param name="fname" select="$suffix"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="translate($fname,$ucletters,$lcletters)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- xsl:template name="convert_filesize">
    <xsl:param name="fsize"/>
    <xsl:choose>
      <xsl:when test="$fsize &gt;= 1073741824">
        <xsl:value-of select="format-number($fsize div 1073741824, '###0')"/> GB
      </xsl:when>
      <xsl:when test="$fsize &gt;= 1048576">
        <xsl:value-of select="format-number($fsize div 1048576, '###0')"/> MB
      </xsl:when>
      <xsl:when test="$fsize &gt;= 1024">
        <xsl:value-of select="format-number($fsize div 1024, '###0')"/> KB
      </xsl:when>
      <xsl:otherwise>
        1 KB
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template -->


  	<xsl:template name="fileoperations">
  	    <xsl:param name="allowinsert"/>
  	    <xsl:param name="binarykey"/>
  	    <xsl:param name="key"/>



  	    <xsl:if test="($allowinsert = 'jpg' or $allowinsert = 'jpeg' or $allowinsert = 'gif' or $allowinsert = 'png' or $allowinsert = 'bmp') and $mode != 'module'">
  	      <a onmouseout="MM_swapImgRestore()">
  	        <xsl:attribute name="onmouseover">
  	          <xsl:text>MM_swapImage('</xsl:text>insertimage<xsl:value-of select="$key"/><xsl:text>','','images/icon_insertimage.gif',1)</xsl:text>
  	        </xsl:attribute>
  	        <xsl:attribute name="href">
  	          <xsl:text>javascript:selectFile('</xsl:text>
  	          <xsl:value-of select="@key"/>
  	          <xsl:text>', 'image', '</xsl:text>
  	          <xsl:value-of select="contentdata/name"/>
  	          <xsl:text>')</xsl:text>
  	        </xsl:attribute>
  	        <img src="images/icon_insertimage_shaded.gif" width="23" height="16" border="0" alt="Sett inn som bilde">
  	          <xsl:attribute name="name">
  	            <xsl:text>insertimage</xsl:text><xsl:value-of select="$key"/>
  	          </xsl:attribute>
  	        </img>
  	      </a>
  	    </xsl:if>

  	    <a onmouseout="MM_swapImgRestore()">
		  <xsl:attribute name="onmouseover">
			<xsl:text>MM_swapImage('</xsl:text>insertlink<xsl:value-of select="$key"/><xsl:text>','','images/icon_link.gif',1)</xsl:text>
		  </xsl:attribute>
		  <xsl:attribute name="href">
			<xsl:text>javascript:selectFile('</xsl:text>
                        <xsl:value-of select="@key"/>
			<xsl:text>', 'link', '</xsl:text>
			<xsl:value-of select="contentdata/name"/>
			<xsl:text>')</xsl:text>
		  </xsl:attribute>
		  <img src="images/icon_link_shaded.gif" width="23" height="16" border="0">
                    <xsl:choose>
                      <xsl:when test="$type = 'module'">
                        <xsl:attribute name="alt">%tipInsertImage%</xsl:attribute>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:attribute name="alt">%tipInsertAsLink%</xsl:attribute>
                      </xsl:otherwise>
                    </xsl:choose>
                    <xsl:attribute name="name">
                      <xsl:text>insertlink</xsl:text><xsl:value-of select="$key"/>
                    </xsl:attribute>
		  </img>
		</a>

                  <a onmouseout="MM_swapImgRestore()">
                    <xsl:attribute name="onmouseover">
                      <xsl:text>MM_swapImage('</xsl:text>edit<xsl:value-of select="$key"/><xsl:text>','','images/icon_edit.gif',1)</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="href">
                      <xsl:text>javascript:modifyFile('</xsl:text>
                      <xsl:value-of select="@key"/>
                      <xsl:text>')</xsl:text>
                    </xsl:attribute>
                    <img src="images/icon_edit_over.gif" width="16" height="16" border="0" alt="%tipEditFile%">
                      <xsl:attribute name="name">
                        <xsl:text>edit</xsl:text><xsl:value-of select="$key"/>
                      </xsl:attribute>
                    </img>
                  </a>

  	    <a onmouseout="MM_swapImgRestore()">
		  <xsl:attribute name="onmouseover">
			<xsl:text>MM_swapImage('</xsl:text>del<xsl:value-of select="$key"/><xsl:text>','','images/icon_delete.gif',1)</xsl:text>
		  </xsl:attribute>
		  <xsl:attribute name="href">
			<xsl:text>javascript:deleteFile('</xsl:text>
			<xsl:value-of select="@key"/>
			<xsl:text>')</xsl:text>
		  </xsl:attribute>
		  <img src="images/icon_delete_over.gif" width="16" height="16" border="0" alt="%tipDeleteFile%">
			<xsl:attribute name="name">
			  <xsl:text>del</xsl:text><xsl:value-of select="$key"/>
			</xsl:attribute>
		  </img>
  	    </a>
    </xsl:template>

    <xsl:template name="chooseimage">
      <xsl:param name="images"/>
      <xsl:param name="width" select="'230'"/>

      <xsl:choose>
        <xsl:when test="boolean($images/image[width = $width])">
          <xsl:value-of select="$images/image[width = $width]/binarydata/@key"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$images/image[@type = 'original']/binarydata/@key"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:template>

    <xsl:template name="chooseimage2">
      <xsl:param name="images"/>
      <xsl:param name="width" select="'100'"/>

      <xsl:choose>
        <xsl:when test="boolean($images/image[width = $width])">
          <xsl:value-of select="$images/image[width = $width]/binarydata/@key"/>
        </xsl:when>
        <xsl:when test="boolean($images/image[height = $width])">
          <xsl:value-of select="$images/image[height = $width]/binarydata/@key"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$images/image[@type = 'original']/binarydata/@key"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:template>


  <xsl:template name="convert_filesize">
    <xsl:param name="fsize"/>
    <xsl:choose>
      <xsl:when test="$fsize &gt;= 1073741824">
        <xsl:value-of select="format-number($fsize div 1073741824, '###0')"/> GB
      </xsl:when>
      <xsl:when test="$fsize &gt;= 1048576">
        <xsl:value-of select="format-number($fsize div 1048576, '###0')"/> MB
      </xsl:when>
      <xsl:when test="$fsize &gt;= 1024">
        <xsl:value-of select="format-number($fsize div 1024, '###0')"/> KB
      </xsl:when>
      <xsl:otherwise>
        1 KB
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
