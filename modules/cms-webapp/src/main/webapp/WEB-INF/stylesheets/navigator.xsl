<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
        <!ENTITY nbsp "&#160;">
        ]>

<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

<xsl:output method="html"/>

  <xsl:param name="userkey"/>
  <xsl:param name="usergroupkey"/>
  <xsl:param name="userfullname"/>
  <xsl:param name="userstorekey"/>
  <xsl:param name="hasphoto"/>
  <xsl:param name="canUpdate"/>
  <xsl:param name="canUpdatePassword"/>
  <xsl:param name="languagecode"/>

  <xsl:variable name="URLToSupport" select="'http://www.enonic.com/'"/>
  <xsl:variable name="URLToDocumentation" select="'http://www.enonic.com/community/'"/>

  <xsl:template match="/">

    <html>
      <head>
        <link rel="stylesheet" type="text/css" href="css/admin.css"/>

        <script type="text/javascript" language="JavaScript">
          var leftFrameXOffset = 0;
          var leftFrameYOffset = 0;

          function setPageOffsetVal( win )
          {
            leftFrameXOffset = (document.all) ? win.document.body.scrollLeft : win.pageXOffset;
            leftFrameYOffset = (document.all) ? win.document.body.scrollTop : win.pageYOffset;
          }
          function MM_swapImgRestore()
          { //v3.0
            var i,x,a=document.MM_sr; for(i=0;a&amp;&amp;i&lt;a.length&amp;&amp;(x=a[i])&amp;&amp;x.oSrc;i++)
            x.src=x.oSrc;
          }

          function MM_preloadImages()
          { //v3.0
            var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
            var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i&lt;a.length; i++)
            if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
          }

          function MM_findObj( n, d )
          { //v3.0
            var p,i,x; if(!d) d=document; if((p=n.indexOf("?"))&gt;0&amp;&amp;parent.frames.length) {
            d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
            if(!(x=d[n])&amp;&amp;d.all) x=d.all[n]; for (i=0;!x&amp;&amp;i&lt;d.forms.length;i++) x=d.forms[i][n];
            for(i=0;!x&amp;&amp;d.layers&amp;&amp;i&lt;d.layers.length;i++) x=MM_findObj(n,d.layers[i].document); return
            x;
          }

          function MM_swapImage()
          { //v3.0
            var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i&lt;(a.length-2);i+=3)
            if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
          }

          function changePassword()
          {
            window.top.frames.mainFrame.location = <xsl:text>"adminpage?page=700&amp;op=changepassword&amp;userstorekey=</xsl:text><xsl:value-of select="$userstorekey"/><xsl:text>";</xsl:text>
          }

          function reload( languageCode, reloadMainFrame ) 
          {
            // reload navigator frame with language code
            var location = parent.frames[0].location.href;
            var idx = location.indexOf("&amp;lang");
            if (idx > 0) {
              var end = location.indexOf("&amp;", idx + 1);
              var tmp = location.substring(0, idx);
              if (end > 0)
                tmp = tmp + location.substring(end, location.length);

              location = tmp;
            }
            location = location + "&amp;lang=" + languageCode;
            parent.frames[0].location = location;

            // reload main menu frame
            parent.frames[1].refreshMenu();

            // reload main frame
            if (reloadMainFrame)
              parent.frames[2].location.href = parent.frames[2].location.href;
          }
        </script>
      </head>

      <body class="body_navigator"
            onload="MM_preloadImages('images/logout-active.gif','images/profile-active.gif','images/pwd-active.gif','images/user-passive.gif', 'images/logout-passive.gif', 'images/profile-passive.gif', 'images/pwd-passive.gif');">

        <form action="adminpage" name="reloadForm" method="get" target="_top">
          <input type="hidden" name="page" value="0"/>
          <input type="hidden" name="lang"/>
          <input type="hidden" name="mainframe"/>
          <input type="hidden" name="referer"/>
        </form>

        <div id="user-info">

          <table border="0" height="70">
            <tr>
              <td>

                <xsl:variable name="user-photo-src">
                  <xsl:choose>
                    <xsl:when test="$hasphoto = 'true'">
                      <xsl:value-of select="concat('_image/user/', $userkey, '?_filter=scalemax(42);rounded(5)')"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:text>images/dummy-user-small.png</xsl:text>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>

                <xsl:variable name="user-photo-tooltip">
                  <xsl:choose>
                    <xsl:when test="$hasphoto = 'true'">
                      <xsl:value-of select="$userfullname"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:text>%fldPhoto%</xsl:text>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>

                <div id="user-photo-container">
                    <xsl:choose>
                      <xsl:when test="$canUpdate = 'true'">
                        <a href="adminpage?page=700&amp;op=form&amp;key={$usergroupkey}&amp;userstorekey={$userstorekey}" target="mainFrame">
                          <img src="{$user-photo-src}" alt="{$user-photo-tooltip}" title="{$user-photo-tooltip}"/>
                        </a>
                      </xsl:when>
                      <xsl:otherwise>
                        <img src="{$user-photo-src}" alt="{$user-photo-tooltip}" title="{$user-photo-tooltip}"/>
                      </xsl:otherwise>
                    </xsl:choose>
                  <xsl:comment> // </xsl:comment>
                </div>
                
              </td>
              <td>

                <div>
                  <xsl:choose>
                    <xsl:when test="$canUpdate = 'true'">
                      <a href="adminpage?page=700&amp;op=form&amp;key={$usergroupkey}&amp;userstorekey={$userstorekey}" target="mainFrame" title="%headUsersYourProfile%">
                        <xsl:value-of select="$userfullname"/>
                      </a>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="$userfullname"/>
                    </xsl:otherwise>
                  </xsl:choose>

                  <xsl:text>&nbsp;|&nbsp;</xsl:text>

                  <xsl:if test="$userstorekey">
                    <xsl:if test="$canUpdatePassword = 'true'">
                      <a onmouseout="MM_swapImgRestore()" onmouseover="MM_swapImage('img_pwd','','images/pwd-active.gif',1)"
                         href="#" onclick="javascript:changePassword();">
                        <!--img name="img_pwd" border="0" src="images/pwd-passive.gif" style="margin-right: 5px" align="middle"/-->
                        <xsl:text>%cmdChangePassword%</xsl:text>
                      </a>
                      <xsl:text>&nbsp;|&nbsp;</xsl:text>
                    </xsl:if>
                  </xsl:if>
                  <a target="_blank">
                    <xsl:attribute name="href">
                      <xsl:value-of select="$URLToSupport"/>
                    </xsl:attribute>
                    <xsl:text>%cmdSupport%</xsl:text>
                  </a>
                  <xsl:text>&nbsp;|&nbsp;</xsl:text>
                  <a target="_blank">
                    <xsl:attribute name="href">
                      <xsl:value-of select="$URLToDocumentation"/>
                    </xsl:attribute>
                    <xsl:text>%cmdDocumentation%</xsl:text>
                  </a>
                  <xsl:text>&nbsp;|&nbsp;</xsl:text>
                  <a onmouseout="MM_swapImgRestore()"
                     onmouseover="MM_swapImage('img_logout','','images/logout-active.gif',1)" target="_top">
                    <xsl:attribute name="href">
                      <xsl:text>logout</xsl:text>
                    </xsl:attribute>
                    <!--img name="img_logout" border="0" src="images/logout-passive.gif" style="margin-right: 5px" align="middle"/-->
                    <xsl:text>%cmdLogOut%</xsl:text>
                  </a>
                </div>
              </td>
            </tr>
          </table>


        </div>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2006. Progress Software Corporation. All rights reserved.
<metaInformation>
<scenarios/><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition></MapperMetaTag>
</metaInformation>
-->
