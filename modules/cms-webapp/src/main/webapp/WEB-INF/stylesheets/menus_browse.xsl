<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

    <xsl:output method="html"/>

    <xsl:include href="common/accesslevel_parameters.xsl"/>

    <xsl:include href="common/generic_parameters.xsl" />
    <xsl:include href="common/operations_template.xsl" />
    <xsl:include href="common/javascriptPreload.xsl" />
    <xsl:include href="common/genericheader.xsl" />
    <xsl:include href="common/formatdate.xsl"/>
    <xsl:include href="common/tablecolumnheader.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/tablerowpainter.xsl"/>
    <xsl:include href="common/browse_table_js.xsl"/>

    <xsl:param name="reload"/>
    <xsl:param name="mainframe"/>

    <xsl:variable name="pageURL">
        <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
        <xsl:text>&amp;op=browse</xsl:text>
    </xsl:variable>

    <xsl:template match="/">
        <xsl:call-template name="unitbrowse"/>
    </xsl:template>

    <xsl:template name="unitbrowse">

        <html>

            <head>

                <script type="text/javascript" language="JavaScript">
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

                    function reload(which,mainFrame)
                    {
                    if ("page" == which)
                    top.location.href = "adminpage?page=0&amp;mainframe=" + mainFrame;
                    else if ("navigator" == which)
                    top.document.frames["topFrame"].location.href = "adminpage?page=1";
                    else if ("menu" == which)
                    window.top.frames['leftFrame'].location.href = <xsl:text>"adminpage?page=2&amp;op=browse";</xsl:text>
                    }

                    <xsl:if test="$reload = 'true'">
                        window.top.frames['leftFrame'].refreshMenu();
                    </xsl:if>
                </script>
				<script type="text/javascript" src="javascript/admin.js">//</script>
                <link href="css/admin.css" rel="stylesheet" type="text/css"/>
            </head>

            <body>


              <xsl:attribute name="onload">
                    <xsl:text>MM_preloadImages('images/icon_edit.gif','images/icon_delete.gif');</xsl:text>
                    <xsl:choose>
                        <xsl:when test="$reload = 'page'">
                            <xsl:text> reload("page", "</xsl:text>
                            <xsl:value-of select="$mainframe"/>
                            <xsl:text>");</xsl:text>
                        </xsl:when>
                        <xsl:when test="$reload != ''">
                            <xsl:call-template name="reloadfunction">
                                <xsl:with-param name="which" select="$reload"/>
                            </xsl:call-template>
                        </xsl:when>
                    </xsl:choose>
                </xsl:attribute>

                <h1>
		    <a href="adminpage?page=851&amp;op=listmenus">
                        <xsl:text>%headMenus%</xsl:text>
                    </a>
                </h1>

                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <tr>
                            <td class="browse_title_buttonrow_seperator"><img src="images/1x1.gif"/></td>
                        </tr>
                    </tr>
                    <xsl:if test="$siteadmin = 'true'">
                        <tr>
                            <td>
                                <xsl:call-template name="button">
                                    <xsl:with-param name="type" select="'link'"/>
                                    <xsl:with-param name="caption" select="'%cmdNew%'"/>
                                    <xsl:with-param name="href">
                                        <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
                                        <xsl:text>&amp;op=createmenuform</xsl:text>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </td>
                        </tr>
                    </xsl:if>
                    <tr>
                        <tr>
                            <td class="browse_buttonrow_datarows_seperator"><img src="images/1x1.gif"/></td>
                        </tr>
                    </tr>
                    <tr>
                        <td>
                            <table width="100%" border="0" cellspacing="0" cellpadding="4" class="browsetable">
                                <tr>

                                    <xsl:call-template name="tablecolumnheader">
                                        <xsl:with-param name="caption" select="'%fldName%'" />
                                        <xsl:with-param name="pageURL" select="$pageURL" />
                                        <xsl:with-param name="sortable" select="'false'" />
                                    </xsl:call-template>

                                    <xsl:call-template name="tablecolumnheader">
                                        <xsl:with-param name="width" select="'100'" />
                                        <xsl:with-param name="caption" select="'%fldDefaultLanguage%'" />
                                        <xsl:with-param name="pageURL" select="$pageURL" />
                                        <xsl:with-param name="sortable" select="'false'" />
                                    </xsl:call-template>

                                    <xsl:call-template name="tablecolumnheader">
                                        <xsl:with-param name="width" select="'90'" />
                                        <xsl:with-param name="caption" select="''" />
                                        <xsl:with-param name="sortable" select="'false'" />
                                    </xsl:call-template>
                                </tr>

                                <xsl:variable name="sortby-data-type">text</xsl:variable>

                              <xsl:for-each select="/menus/menu">
                                <xsl:sort select="@name"/>

                                <xsl:variable name="className">
                                  <xsl:choose>
                                    <xsl:when test="@defaultcssexists = 'false'">
                                      <xsl:text>browsetablecellred</xsl:text>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <xsl:text>browsetablecell</xsl:text>
                                    </xsl:otherwise>
                                  </xsl:choose>
                                  <xsl:if test="position() = last()">
                                    <xsl:text> row-last</xsl:text>
                                  </xsl:if>
                                </xsl:variable>

                                <tr>
                                  <xsl:call-template name="tablerowpainter"/>
                                  <td class="{$className}">
                                    <xsl:if test="$siteadmin = 'true'">
                                      <xsl:attribute name="title">%msgClickToEdit%</xsl:attribute>
                                      <xsl:call-template name="addJSEvent">
                                        <xsl:with-param name="key" select="@key"/>
                                      </xsl:call-template>
                                    </xsl:if>
                                    <xsl:value-of select="@name"/>
                                  </td>
                                  <td class="{$className}">
                                    <xsl:if test="$siteadmin = 'true'">
                                      <xsl:attribute name="title">%msgClickToEdit%</xsl:attribute>
                                      <xsl:call-template name="addJSEvent">
                                        <xsl:with-param name="key" select="@key"/>
                                      </xsl:call-template>
                                    </xsl:if>
                                    <xsl:value-of select="@language"/>
                                  </td>
                                  <td align="center" class="{$className}">
                                    <xsl:if test="$siteadmin = 'true'">
                                      <xsl:variable name="edithref">
                                        <xsl:text>adminpage?page=</xsl:text>
                                        <xsl:value-of select="$page"/>
                                        <xsl:text>&amp;op=form&amp;key=</xsl:text>
                                        <xsl:value-of select="@key"/>
                                        <xsl:text>&amp;returnop=listmenus</xsl:text>
                                      </xsl:variable>

                                      <xsl:variable name="copyhref">
                                        <xsl:text>adminpage?page=5&amp;redirect=adminpage%3Fpage=</xsl:text>
                                        <xsl:value-of select="$page"/>
                                        <xsl:text>%26op=copy%26returnop=listmenus%26key=</xsl:text>
                                        <xsl:value-of select="@key"/>
                                      </xsl:variable>

                                      <xsl:call-template name="operations">
                                        <xsl:with-param name="page" select="$page"/>
                                        <xsl:with-param name="key" select="@key"/>
                                        <xsl:with-param name="edithref" select="$edithref"/>
                                        <xsl:with-param name="includecopy" select="'true'"/>
                                        <xsl:with-param name="copywarning" select="true()"/>
                                        <xsl:with-param name="copyhref" select="$copyhref"/>
                                        <xsl:with-param name="copycondition">
                                          <xsl:text>confirm('%alertCopySiteCustom%</xsl:text>
                                          <xsl:value-of select="@name"/>
                                          <xsl:text>?')</xsl:text>
                                        </xsl:with-param>
                                      </xsl:call-template>
                                    </xsl:if>
                                  </td>
                                </tr>
                              </xsl:for-each>
                            </table>
                        </td>
                    </tr>
                </table>

            </body>

        </html>

    </xsl:template>

    <xsl:template name="reloadfunction">
        <xsl:param name="which" select="''"/>

        <xsl:choose>
            <xsl:when test="contains($which, ',')">
                <xsl:text> reload("</xsl:text>
                <xsl:value-of select="substring-before($which, ',')"/>
                <xsl:text>", "");</xsl:text>
                <xsl:call-template name="reloadfunction">
                    <xsl:with-param name="which" select="substring-after($which, ',')"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$which != ''">
                <xsl:text> reload("</xsl:text>
                <xsl:value-of select="$which"/>
                <xsl:text>", "");</xsl:text>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>