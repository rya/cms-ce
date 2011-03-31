<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>
    <xsl:include href="handlerconfigs/default.xsl"/>

    <xsl:include href="tree/displaytree.xsl"/>
    <xsl:include href="common/button.xsl"/>

    <xsl:param name="page"/>
    <xsl:param name="cat"/>
    <xsl:param name="fieldname" select="''"/>
    <xsl:param name="fieldrow" select="''"/>
    <xsl:param name="name" select="''"/>
    <xsl:param name="dest" select="''"/>
    <xsl:param name="contenttypestring"/>
    <xsl:param name="selectedcategorykey"/>
    <xsl:param name="selectedparentcategorykey" select="-1"/>
    <xsl:param name="redirect"/>
    <xsl:param name="contenthandler"/>
    <xsl:param name="minoccurrence"/>
    <xsl:param name="maxoccurrence"/>

    <xsl:variable name="is-related-content-popup" select="$subop = 'relatedcontent' or $subop = 'relatedfile' or $subop = 'relatedfiles' or $subop = 'relatedimages' or $subop = 'relatedimage'"/>
    <xsl:variable name="is-editor-image-picker" select="$subop = 'insert'"/>

    <xsl:key name="categories" match="categories/category" use="@key"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>%titleChooseDestination%</title>
                <link rel="stylesheet" type="text/css" href="css/admin.css"/>
                <link rel="stylesheet" type="text/css" href="css/menu.css"/>
                <script type="text/javascript" src="javascript/menu.js">//</script>
                <script type="text/javascript" src="javascript/admin.js">//</script>
                <script type="text/javascript">
                    var branchOpen = new Array;
                    var mainFrameUrl = <xsl:text>"adminpage?page=</xsl:text>
                    <xsl:choose>
                        <xsl:when test="$selectedcategorykey = -1">600</xsl:when>
                        <xsl:otherwise>991</xsl:otherwise>
                    </xsl:choose>
                    <xsl:text>&amp;op=browse</xsl:text>
                    <xsl:text>&amp;subop=</xsl:text>
                    <xsl:value-of select="$subop"/>
                    <xsl:text>&amp;fieldname=</xsl:text>
                    <xsl:value-of select="$fieldname"/>
                    <xsl:text>&amp;fieldrow=</xsl:text>
                    <xsl:value-of select="$fieldrow"/>
                    <xsl:text>&amp;cat=</xsl:text>
                    <xsl:value-of select="$selectedcategorykey"/>
                    <xsl:text>&amp;contenttypestring=</xsl:text>
                    <xsl:value-of select="$contenttypestring"/>
                    <xsl:text>&amp;contenthandler=</xsl:text>
                    <xsl:value-of select="$contenthandler"/>
                    <xsl:if test="$minoccurrence != ''">
                        <xsl:text>&amp;minoccurrence=</xsl:text>
                        <xsl:value-of select="$minoccurrence"/>
                    </xsl:if>
                    <xsl:if test="$maxoccurrence != ''">
                        <xsl:text>&amp;maxoccurrence=</xsl:text>
                        <xsl:value-of select="$maxoccurrence"/>
                    </xsl:if>
                    <xsl:text>";</xsl:text>

                      var cookiename = 'contentPickerMenu';
                      var useCookieExpireDate = true;

                      function refreshMenu()
                      {
                        document.splash.submit();
                      }

                      function onLoad() {
                        <xsl:if test="$redirect = 'true' and $is-related-content-popup or $is-editor-image-picker">

                        var disabledParam =
                        <xsl:text>"</xsl:text>
                        <xsl:text>&amp;disabled=</xsl:text>
                        <xsl:value-of select="/categories//category[@key=$selectedcategorykey]/@disabled"/>
                        <xsl:text>";</xsl:text>

                          var mainFrame = parent.frames["mainFrame"];
                          mainFrame.location.href = mainFrameUrl + disabledParam;
                          var key = '-category<xsl:value-of select="$selectedparentcategorykey"/>';

                          if (isBranchClosed(key))
                          {
                            openBranch(key);
                          }
                        </xsl:if>
                      }

                      <xsl:if test="$subop = 'insert'">
                        document.onkeypress = function(e) {
                          var e = e || event;
                          if (e.keyCode == 27 &amp;&amp; window.parent)
                            window.parent.close();
                          }
                      </xsl:if>
                    </script>

                    <script type="text/javascript" language="JavaScript">
                      function openMenuTree()
                      {
                        var allCookies = document.cookie;
                        var pos = allCookies.indexOf(cookiename + "=");

                        if ( pos != -1 )
                        {
                          var start = pos + cookiename.length + 1;
                          var end = allCookies.indexOf(";", start);

                          if (end == -1)
                          {
                            end = allCookies.length;
                          }

                          var values = allCookies.substring(start, end).split(',');

                          for ( i in values )
                          {
                            branchOpen[values[i]] = true;
                          }
                        }

                        openTree();
                      }
                    </script>

                    <xsl:if test="$subop = 'relatedcontent' or
                            $subop = 'relatedfiles' or
                            $subop = 'relatedfile' or
                            $subop = 'relatedimages' or
                            $subop = 'relatedimage' or
                            $subop = 'insert' or
                            $subop = 'callback' or
                            $subop = 'contentfield'">
                      <script type="text/javascript" src="javascript/window.js"/>
                      <script type="text/javascript">
                        cms.window.attatchKeyEvent('close');
                      </script>
                    </xsl:if>

            </head>

            <body id="popup">
                <xsl:if test="$subop != 'callback'">
                    <xsl:attribute name="onload">
                        <xsl:text>javascript:openMenuTree();</xsl:text>
                        <xsl:text>onLoad();</xsl:text>
                    </xsl:attribute>
                </xsl:if>

                <form method="post" action="adminpage?page=5" name="splash">
                    <input type="hidden" name="redirect"/>
                </form>

                <script type="text/javascript" language="JavaScript">
                    document.splash.redirect.value = document.location;
                </script>

                <xsl:call-template name="button">
                    <xsl:with-param name="name" select="'refreshbutton'"/>
                    <xsl:with-param name="type" select="'button'"/>
                    <xsl:with-param name="hidden" select="true()"/>
                    <xsl:with-param name="caption" select="'%cmdRefreshMenu%'"/>
                    <xsl:with-param name="onclick">
                        <xsl:text>javascript:refreshMenu();</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>

                <xsl:variable name="url">
                    <xsl:text>adminpage?subop=</xsl:text><xsl:value-of select="$subop"/>
                    <xsl:text>&amp;fieldname=</xsl:text><xsl:value-of select="$fieldname"/>
                    <xsl:text>&amp;fieldrow=</xsl:text><xsl:value-of select="$fieldrow"/>
                    <xsl:if test="$contenttypestring">
                        <xsl:text>&amp;contenttypestring=</xsl:text><xsl:value-of select="$contenttypestring"/>
                    </xsl:if>
                    <xsl:if test="$contenthandler">
                        <xsl:text>&amp;contenthandler=</xsl:text><xsl:value-of select="$contenthandler"/>
                    </xsl:if>
                    <xsl:if test="$minoccurrence">
                        <xsl:text>&amp;minoccurrence=</xsl:text>
                        <xsl:value-of select="$minoccurrence"/>
                    </xsl:if>
                    <xsl:if test="$maxoccurrence">
                        <xsl:text>&amp;maxoccurrence=</xsl:text>
                        <xsl:value-of select="$maxoccurrence"/>
                    </xsl:if>

                </xsl:variable>
                <p style="margin-top: 5px; margin-left: 5px;">
                    <xsl:choose>
                        <xsl:when test="$subop = 'callback'">
                            <xsl:apply-templates select="/*" mode="displaytree">
                                <xsl:with-param name="callback" select="'callback_newCategorySelector'"/>
                                <xsl:with-param name="linkshaded" select="false()"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="/*" mode="displaytree">
                                <xsl:with-param name="url" select="$url"/>
                            </xsl:apply-templates>
                        </xsl:otherwise>
                    </xsl:choose>
                </p>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
