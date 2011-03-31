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
  
  <!-- Kan denne slettes?? -->

  <xsl:param name="filename"/>
  <xsl:param name="binarykey"/>
  <xsl:param name="mode"/>
  <xsl:param name="internallink"/>

  <xsl:template match="/">
        <xsl:call-template name="insertlink"/>
  </xsl:template>

  <xsl:template name="insertlink">
    <html>
        <head>
          <title>%headInsertAsLink%</title>

          <link rel="stylesheet" type="text/css" href="javascript/xtree.css"/>
          <script src="javascript/xtree.js"></script>

          <script language="Javascript">
				function MM_swapImgRestore() { //v3.0
				  var i,x,a=document.MM_sr; for(i=0;a&amp;&amp;i&lt;a.length&amp;&amp;(x=a[i])&amp;&amp;x.oSrc;i++) x.src=x.oSrc;
				}

				function MM_preloadImages() { //v3.0
				  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
					var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i&lt;a.length; i++)
					if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
				}

				function MM_findObj(n, d) { //v3.0
				  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&amp;&amp;parent.frames.length) {
					d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
				  if(!(x=d[n])&amp;&amp;d.all) x=d.all[n]; for (i=0;!x&amp;&amp;i&lt;d.forms.length;i++) x=d.forms[i][n];
				  for(i=0;!x&amp;&amp;d.layers&amp;&amp;i&lt;d.layers.length;i++) x=MM_findObj(n,d.layers[i].document); return x;
				}

				function MM_swapImage() { //v3.0
				  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i&lt;(a.length-2);i+=3)
				   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
				}


             function getSelectedText()
             {
               var sel = window.opener.tbContentElement.DOM.selection;
               var f = document.all('addLinkForm');

 				if(sel.type=="Control") {
 					var el=window.opener.tbContentElement.DOM.selection.createRange().commonParentElement();
 					var tr = window.opener.tbContentElement.DOM.body.createTextRange();
 					tr.moveToElementText(el);
 					tr.select();
 					linkType = "Control";
 					f.linktext.value = "Ikke tilgjenglig";
 					f.linktext.disabled = true;
 				} else linkType = "Text";

                 range = sel.createRange();
 				if( linkType != "Control" ) {
 	                f.linktext.value = range.text;
                 }
                 f.htmllinktext.value = range.htmlText;

                 f = document.all('addInternalLinkForm');
 				if( linkType != "Control" ) {
                 	f.linktext.value = range.text;
 				}
                 f.htmllinktext.value = range.htmlText;
             }

             function insertInternalLink()
             {
               var f = document.all('addInternalLinkForm');
               var sel = window.opener.tbContentElement.DOM.selection;
               var target = f.link_target.value;
               var title = f.alttext.value;
               var key = f.linkkey.value;
               var linktextChanged = f.linktext.getAttribute('changed');
 			  var range  = sel.createRange();
    			  var bodyrange=window.opener.tbContentElement.DOM.body.createTextRange();

               if (f.linktext.value == "") {
                 alert("Du m&aring; fylle inn feltet 'Link tekst'");
                 return;
               }

 				// Find out if we're going to change an existing link
 				var changeLink = false;
 				var coll=window.opener.tbContentElement.DOM.all.tags("A");
 				for(i=0;i&lt;coll.length;i++) {

 					bodyrange.moveToElementText(coll[i]);

 					if(range.compareEndPoints("EndToStart",bodyrange)==1 &amp;&amp;
 						range.compareEndPoints("StartToEnd",bodyrange)==-1)
 					{
 						changeLink = true;
 					}

 				}

 				if(changeLink == false)
 				{
 				  if (linktextChanged == 'true' || "None" == sel.type){
 					range  = sel.createRange();
 					range.pasteHTML('<a href="page?id='+key+'" title="' + title + '" target="' + target + '">' + document.addInternalLinkForm.linktext.value + '</a>');
 				  } else if ( "Text" == sel.type ) {
 					range = sel.createRange();
 					range.pasteHTML('<a href="page?id='+key+'" title="' + title + '" target="' + target + '">' + document.addInternalLinkForm.htmllinktext.value + '</a>');
 				  } else if ( sel.type  == "Control" ) {
 					range = sel.createRange();
 					range.pasteHTML('<a href="page?id='+key+'" title="' + title + '" target="' + target + '">' + document.addInternalLinkForm.htmllinktext.value + '</a>');
 				  }

 				}
 				else { //Update existing link or link plain text

 					var coll=window.opener.tbContentElement.DOM.all.tags("A");
 					for(i=0;i&lt;coll.length;i++) {

 						bodyrange.moveToElementText(coll[i]);

 						if((range.compareEndPoints("EndToStart",bodyrange)==1)&amp;&amp;
 							(range.compareEndPoints("StartToEnd",bodyrange)==-1))
 						{

 							coll[i].href= 'page?id=' + key;
 							if(target.length) coll[i].target=target;
 							else coll[i].removeAttribute("TARGET",0);

 							if(title.length) coll[i].title=title;
 							else coll[i].title="";

 						}

 					}
 				}

               window.close();
             }

             function insertExternalLink()
             {
               var f = document.all('addLinkForm');
               var sel = window.opener.tbContentElement.DOM.selection;
               var url = f.url.value;
               var target = f.link_target.value;
               var title = f.alttext.value;
               var linktextChanged = f.linktext.getAttribute('changed');
 			  var range  = sel.createRange();
    			  var bodyrange=window.opener.tbContentElement.DOM.body.createTextRange();

               if (linkType != "Control" &amp;&amp; f.linktext.value == "") {
                 alert("Du m&aring; fylle inn feltet 'Link tekst'");
                 return;
               }

               if (f.url.value == "" || f.url.value == "http://") {
                 alert("Du m&aring; fylle inn feltet 'URL'");
                 return;
               }

 				// Find out if we're going to change an existing link
 				var changeLink = false;
 				var coll=window.opener.tbContentElement.DOM.all.tags("A");
 				for(i=0;i&lt;coll.length;i++) {

 					bodyrange.moveToElementText(coll[i]);

 					if(range.compareEndPoints("EndToStart",bodyrange)==1 &amp;&amp;
 						range.compareEndPoints("StartToEnd",bodyrange)==-1)
 					{
 						changeLink = true;
 					}

 				}


                                  var urltype;
                                  for (var i = 0; i &lt; document.addLinkForm.urltype.length; ++i) {
                                    if( document.addLinkForm.urltype[i].selected) {
                                      urltype = document.addLinkForm.urltype[i].value;
                                      break;
                                    }
                                  }

                                  if (urltype == 'mailto:')
                                    target ='_self';


 				if(changeLink == false)
 				{
 				  if ((linktextChanged == 'true' &amp;&amp; sel.type == "Text" &amp;&amp; linkType != "Control") || "None" == sel.type){
 					range.pasteHTML('<a href="'+urltype+url+'" title="' + title + '" target="' + target + '">' + document.addLinkForm.linktext.value + '</a>');
 				  } else if ( sel.type == "Text" ) {
 					range = sel.createRange();
 					range.pasteHTML('<a href="'+urltype+url+'" title="' + title + '" target="' + target + '">' + document.addLinkForm.htmllinktext.value + '</a>');
 				  } else if ( sel.type  == "Control" ) {
 					range.pasteHTML('<a href="'+urltype+url+'" title="' + title + '" target="' + target + '">' + document.addLinkForm.htmllinktext.value + '</a>');
 				  }

 				}
 				else { //Update existing link or link plain text
 					var coll=window.opener.tbContentElement.DOM.all.tags("A");
 					for(i=0;i&lt;coll.length;i++) {

 						bodyrange.moveToElementText(coll[i]);

 						if((range.compareEndPoints("EndToStart",bodyrange)==1)&amp;&amp;
 							(range.compareEndPoints("StartToEnd",bodyrange)==-1))
 						{

 							coll[i].href=urltype + url;
 							if(target.length) coll[i].target=target;
 							else coll[i].removeAttribute("TARGET",0);

 							if(title.length) coll[i].title=title;
 							else coll[i].title="";

 						}

 					}
 				}

               window.close();
             }

            function swapTypes(s)
            {
              var externalState = document.all('externallink').style.display;
              var internalState = document.all('internallink').style.display;
              var menutreeState = document.all('menutree').style.display;

              if (externalState == 'inline') {
                document.all('externallink').style.display = 'none';
                document.all('menutree').style.display = 'inline';
              } else if (menutreeState == 'inline') {
                document.all('externallink').style.display = 'inline';
                document.all('menutree').style.display = 'none';
              } else {
                document.all('externallink').style.display = 'inline';
                document.all('internallink').style.display = 'none';
                document.all('addInternalLinkForm').linktext.value = '';
              }
            }


            function changeType(which)
            { 
              if (which == 'externallink') {
                document.all('externallink').style.display = 'inline';
                document.all('internallink').style.display = 'none';
                document.all('menutree').style.display = 'none';
              } else if (which == 'internallink') {
                document.all('externallink').style.display = 'none';
                document.all('menutree').style.display = 'inline';
              } else {
                document.all('externallink').style.display = 'none';
                document.all('internallink').style.display = 'none';
                document.all('menutree').style.display = 'none';
              }
            }

            function selectMenuitem(key, name)
            {
              var f = document.all('addInternalLinkForm');

              document.all('linkname').innerText = '';
              document.all('linkname').insertAdjacentText('afterBegin', name)
              if (f.linktext.value == '')
                f.linktext.value = name;
              f.linkkey.value = key;
              document.all('menutree').style.display = 'none';
              document.all('internallink').style.display = 'inline';
            }
          </script>
          <link href="css/admin.css" rel="stylesheet" type="text/css"/>
          <style type="text/css">
            .menuitem {
              border-top: 1px solid;
            }

            .menuitembottom {
              border-top: 1px solid;
              border-bottom: 1px solid;
            }
            
            td {
              font: icon;
            }
            
            input {
              font: icon;
            }

            select {
              font: icon;
            }
          </style>
        </head>
        <body onload="getSelectedText()" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" style="font: icon; padding: 0.7em;">
          <h1 style="padding-bottom: 0em; margin-bottom: 0em;">%headInsertAsLink%</h1>

          <table width="100%" border="0" cellspacing="3" cellpadding="3" style="font: icon;">
            <tr>
              <td>
                <xsl:if test="$internallink">
                  <table width="100%" border="0">
                    <tr>
                      <td>
                        <select name="link_type" onchange="javascript:changeType(this.value);">
                          <option value="">%sysDropDownChoose%</option>
                          <option value="externallink">%fldExternal%</option>
                          <option value="internallink">%fldInternal%</option>
                        </select>
                      </td>
                    </tr>
                  </table>
                </xsl:if>
                
                <xsl:call-template name="externallink"/>
                <xsl:if test="$internallink">
                  <xsl:call-template name="menutree"/>
                  <xsl:call-template name="internallink"/>
                </xsl:if>
              </td>
            </tr>
          </table>
          
          
          
        </body>
      </html>
    </xsl:template>
    
    <xsl:template name="externallink">
      <fieldset id="externallink">
        <xsl:if test="$internallink">
          <xsl:attribute name="style">
            <xsl:text>display: none;</xsl:text>
          </xsl:attribute>
        </xsl:if>
        <legend>&nbsp;%headExternalLink%&nbsp;</legend>

        <table width="100%" border="0">
          <form name="addLinkForm" method="post">
            <tr>
              <td width="25%">%fldLinkText%:</td>
              <td width="75%">
                <input type="text" size="44" name="linktext" onchange="javascript: this.setAttribute('changed', 'true');"/>
                <input type="hidden" name="htmllinktext"/>
              </td>
            </tr>
            <tr>
              <td>%fldURL%:</td>
              <td>
                <select name="urltype" onChange="javascript: if (this.value == 'mailto:') document.all['openlinkin'].style.display = 'none'; else document.all['openlinkin'].style.display = 'inline';">
                  <option value="http://">http://</option>
                  <option value="ftp://">ftp://</option>
                  <option value="mailto:">mailto:</option>
                  <option value="">-- None --</option>
                </select>
                <input type="text" size="44" name="url">
                </input>
              </td>
            </tr>
            <tr>
              <td>%fldAltText%:</td>
              <td>
                <input type="text" size="44" name="alttext">
                </input>
              </td>
            </tr>
            <tr id="openlinkin" name="openlinkin">
              <td>%fldOpenLinkIn%:</td>
              <td id="openlinkinbox">
                <select name="link_target">
                  <option value="_blank">%optOpenNewWindow%</option>
                  <option value="_self">%optOpenExistingWindow%</option>
                </select>
              </td>
            </tr>
            
            <tr>
              <td>&nbsp;</td>
              <td>
                <input type="button" class="button" value="%cmdInsert%" onclick="insertExternalLink()"/>
                <xsl:text> </xsl:text>
                <input type="button" class="button" value="%cmdCancel%" onclick="javascript: window.close();" />
              </td>
            </tr>
          </form>
        </table>
      </fieldset>
  </xsl:template>


  <xsl:template name="menutree">
    <xsl:variable name="menu" select="/menus/menu"/>
    <table width="100%" id="menutree" style="display: none;">
      <tr>
        <td><b>Velg siden du vil linke til!</b>
        	<br/>
				<img src="images/shim.gif" height="5" width="5"/>
        	<br/>
        </td>
      </tr>
      <tr>
        <td>
          <script language="Javascript">
            var menuTree = new WebFXTree("%mnuPageBuilder%");
            menuTree.icon = 'images/icon_menuitems.gif';
            menuTree.openIcon = 'images/icon_menuitems.gif';
            menuTree.setBehavior('explorer');
            document.write(menuTree);

            
            <xsl:for-each select="$menu/menuitems/menuitem">
              <xsl:if test="not(@deleted = 'deleted')">
                <xsl:call-template name="showmenuitem">
                  <xsl:with-param name="menuitem" select="."/>
                  <xsl:with-param name="parent" select="'menuTree'"/>
                </xsl:call-template>
              </xsl:if>
            </xsl:for-each>
          </script>
        </td>
      </tr>
    </table>
  </xsl:template>


  <xsl:template name="internallink">
    <fieldset id="internallink" style="display: none;">
      <legend>&nbsp;%headInternalLink%&nbsp;</legend>

      <table width="100%">
        <form name="addInternalLinkForm" method="post">
          <tr>
            <td width="25%">%fldPage%:</td>
            <td width="75%" id="linkname"/>
          </tr>
          <tr>
            <td>%fldLinkText%:</td>
            <td>
              <input type="text" size="44" name="linktext" onchange="javascript: this.setAttribute('changed', 'true');"/>
              <input type="hidden" name="htmllinktext"/>
              <input type="hidden" name="linkkey"/>
            </td>
          </tr>
          <tr>
            <td>%fldAltText%:</td>
            <td>
              <input type="text" size="44" name="alttext">
              </input>
            </td>
          </tr>
          <tr>
            <td>%fldOpenLinkIn%:</td>
            <td id="openlinkinbox">
              <select name="link_target">
                <option value="_blank">%optOpenNewWindow%</option>
                <option value="_self" selected="selected">%optOpenExistingWindow%</option>
              </select>
            </td>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td>
              <input type="button" class="button" value="%cmdInsert%" onclick="insertInternalLink()"/>
              <xsl:text> </xsl:text>
              <input type="button" class="button" value="%cmdCancel%" onclick="javascript: window.close();" />
            </td>
          </tr>
        </form>
      </table>
    </fieldset>
  </xsl:template>


  <xsl:template name="showmenuitem">
    <!--
         this template is used to display a single menuitem.

         it is called recursivly on all the menuitems that are
         children of the menuitem sent to this template.
    -->
    <xsl:param name="menuitem"/>
    <xsl:param name="parent"/>

    var item<xsl:value-of select="$menuitem/@key"/> = <xsl:text>new WebFXTreeItem("</xsl:text><xsl:value-of select="$menuitem/name"/><xsl:text>");</xsl:text>
    item.target = "";
    <xsl:choose>
      <xsl:when test="$menuitem/@visible = 'yes'">
        item<xsl:value-of select="$menuitem/@key"/>.icon = 'images/icon_page.gif';
        item<xsl:value-of select="$menuitem/@key"/>.openIcon = 'images/icon_page.gif';
      </xsl:when>
      <xsl:otherwise>
        item<xsl:value-of select="$menuitem/@key"/>.icon = 'images/icon_page_hidden.gif';
        item<xsl:value-of select="$menuitem/@key"/>.openIcon = 'images/icon_page_hidden.gif';
      </xsl:otherwise>
    </xsl:choose>
    item<xsl:value-of select="$menuitem/@key"/>.onclick = <xsl:text>"javascript: selectMenuitem('</xsl:text><xsl:value-of select="$menuitem/@key"/>
    <xsl:text>', '</xsl:text><xsl:value-of select="$menuitem/name"/><xsl:text>');"</xsl:text>;
    <xsl:value-of select="$parent"/>.add(item<xsl:value-of select="$menuitem/@key"/>);
        
    <xsl:for-each select="menuitems/menuitem">
      <xsl:call-template name="showmenuitem">
        <xsl:with-param name="menuitem" select="."/>
        <xsl:with-param name="parent">item<xsl:value-of select="$menuitem/@key"/></xsl:with-param>
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
