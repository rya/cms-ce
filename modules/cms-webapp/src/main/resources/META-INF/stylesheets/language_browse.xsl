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

    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/operations_template.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/tablecolumnheader.xsl"/>
    <xsl:include href="common/tablerowpainter.xsl"/>
    <xsl:include href="common/displaysystempath.xsl"/>
    <xsl:include href="common/browse_table_js.xsl"/>

    <xsl:param name="sortby" select="'@languagecode'"/>
    <xsl:param name="sortby-direction" select="'ascending'"/>

    <xsl:variable name="pageURL">
        <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
        <xsl:text>&amp;op=browse</xsl:text>
    </xsl:variable>

    <xsl:template match="/">
        <xsl:call-template name="browse"/>
    </xsl:template>

    <xsl:template name="browse">

        <html>

            <head>
				<script type="text/javascript" src="javascript/admin.js">//</script>

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
                </script>

                <link href="css/admin.css" rel="stylesheet" type="text/css"/>

            </head>

            <body>
                <h1>
                    <xsl:call-template name="displaysystempath">
                        <xsl:with-param name="page" select="$page"/>
                    </xsl:call-template>
                </h1>
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td class="browse_title_buttonrow_seperator"><img src="images/1x1.gif"/></td>
                   </tr>
                    <tr>
                        <td>
                            <xsl:call-template name="button">
                                <xsl:with-param name="type" select="'link'" />
                                <xsl:with-param name="caption" select="'%cmdNew%'" />
                                <xsl:with-param name="href">
                                    <xsl:text>adminpage?page=</xsl:text>
                                    <xsl:value-of select="$page"/>
                                    <xsl:text>&amp;op=form</xsl:text>
                                </xsl:with-param>
                            </xsl:call-template>
                        </td>
                    </tr>
                    <tr>
                        <td class="browse_buttonrow_datarows_seperator"><img src="images/1x1.gif"/></td>
                    </tr>
                    <tr>
                        <td>
                            <table width="100%" cellspacing="0" cellpadding="0" class="browsetable">
                                <tr>
                                    <xsl:call-template name="tablecolumnheader">
                                        <xsl:with-param name="width" select="'160'" />
                                        <xsl:with-param name="caption" select="'%fldLanguageCode%'" />
                                        <xsl:with-param name="pageURL" select="$pageURL" />
                                        <xsl:with-param name="current-sortby" select="$sortby" />
                                        <xsl:with-param name="current-sortby-direction" select="$sortby-direction" />
                                        <xsl:with-param name="sortby" select="'@languagecode'" />
                                    </xsl:call-template>

                                    <xsl:call-template name="tablecolumnheader">
                                        <xsl:with-param name="caption" select="'%fldDescription%'" />
                                        <xsl:with-param name="pageURL" select="$pageURL" />
                                        <xsl:with-param name="current-sortby" select="$sortby" />
                                        <xsl:with-param name="current-sortby-direction" select="$sortby-direction" />
                                        <xsl:with-param name="sortby" select="'.'" />
                                    </xsl:call-template>

                                    <xsl:call-template name="tablecolumnheader">
                                        <xsl:with-param name="width" select="'100'" />
                                        <xsl:with-param name="align" select="'center'" />
                                        <xsl:with-param name="caption" select="'%fldKey%'" />
                                        <xsl:with-param name="pageURL" select="$pageURL" />
                                        <xsl:with-param name="current-sortby" select="$sortby" />
                                        <xsl:with-param name="current-sortby-direction" select="$sortby-direction" />
                                        <xsl:with-param name="sortby" select="'@key'" />
                                    </xsl:call-template>

                                    <xsl:call-template name="tablecolumnheader">
                                        <xsl:with-param name="width" select="'90'" />
                                        <xsl:with-param name="caption" select="''" />
                                        <xsl:with-param name="sortable" select="'false'" />
                                    </xsl:call-template>

                                </tr>

                                <xsl:variable name="sortby-data-type">
                                    <xsl:choose>
                                        <xsl:when test="$sortby = '@key'">number</xsl:when>
                                        <xsl:otherwise>text</xsl:otherwise>
                                    </xsl:choose>
                                </xsl:variable>

                                <xsl:for-each select="/languages/language">
                                    <xsl:sort data-type="{$sortby-data-type}" order="{$sortby-direction}"
                                        select="saxon:evaluate($sortby)"/>

                                  <xsl:variable name="css-class">
                                    <xsl:text>browsetablecell</xsl:text>
                                    <xsl:if test="position() = last()">
                                      <xsl:text> row-last</xsl:text>
                                    </xsl:if>
                                  </xsl:variable>

                                    <tr title="%msgClickToEdit%">
                                        <xsl:call-template name="tablerowpainter"/>
                                        <td class="{$css-class}" title="%msgClickToEdit%">
											<xsl:call-template name="addJSEvent">
												<xsl:with-param name="key" select="@key"/>
											</xsl:call-template>										
                                            <xsl:value-of select="@languagecode"/>
                                        </td>
                                        <td class="{$css-class}" title="%msgClickToEdit%">
											<xsl:call-template name="addJSEvent">
												<xsl:with-param name="key" select="@key"/>
											</xsl:call-template>
                                            <xsl:value-of select="."/>
                                        </td>
                                        <td align="center" class="{$css-class}" title="%msgClickToEdit%">
											<xsl:call-template name="addJSEvent">
												<xsl:with-param name="key" select="@key"/>
											</xsl:call-template>
                                            <xsl:value-of select="@key"/>
                                        </td>
                                        <td align="center" class="{$css-class}">
                                            <xsl:call-template name="operations">
                                                <xsl:with-param name="includecopy" select="'false'"/>
                                                <xsl:with-param name="page" select="$page"/>
                                                <xsl:with-param name="key" select="@key"/>
                                            </xsl:call-template>
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
</xsl:stylesheet>
