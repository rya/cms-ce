<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:output method="html" />

    <xsl:include href="tree/displaytree.xsl" />
    <xsl:include href="common/button.xsl" />

    <xsl:param name="fieldname"/>
    <xsl:param name="mimetype"/>
    <xsl:param name="extension"/>
    <xsl:param name="sourceKey"/>
    <xsl:param name="destinationKey"/>

    <!-- Necessary because of dependency issues (inclusion of button.xsl) -->
    <xsl:param name="page" select="'800'"/>
    <xsl:param name="cat" select="''"/>

    <xsl:variable name="load-main-frame" select="$path != '' and $subop != 'moveFolder' and $subop != 'moveFile'"/>
  
    <xsl:template match="/">
        <html>
            <head>
                <link rel="stylesheet" type="text/css" href="css/admin.css" />
                <link rel="stylesheet" type="text/css" href="css/menu.css" />
                <script type="text/javascript" src="javascript/menu.js">//</script>
                <script type="text/javascript" src="javascript/admin.js">//</script>
                <script type="text/javascript" src="javascript/window.js">//</script>
				<script type="text/javascript">
                    // Globals used by menu.js
                    var branchOpen = new Array;
                    var useCookies = true;
                    var cookiename = 'resourcePickerMenu';
                    var useCookieExpireDate = true;
                </script>

                <script type="text/javascript">
                    cms.window.attatchKeyEvent('close');
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

                <xsl:if test="$load-main-frame">
                    <script type="text/javascript">
                        var mainFrameUrl =  <xsl:text>"adminpage?page=800&amp;op=browse</xsl:text>
                        <xsl:text>&amp;fieldname=</xsl:text>
                        <xsl:value-of select="$fieldname"/>
                        <xsl:text>&amp;mimetype=</xsl:text>
                        <xsl:value-of select="$mimetype"/>
                        <xsl:text>&amp;extension=</xsl:text>
                        <xsl:value-of select="$extension"/>
                        <xsl:text>&amp;path=</xsl:text>
                        <xsl:value-of select="$path"/>
                        <xsl:text>";</xsl:text>
                        var mainFrame = window.top.frames["mainFrame"];
                        mainFrame.location.href = mainFrameUrl;
                    </script>
                </xsl:if>
            </head>
			
            <body>
                <xsl:if test="not($subop = 'moveFolder') and not($subop = 'moveFile')">
                    <xsl:attribute name="onload">
                        <xsl:text>javascript:</xsl:text>
                        <xsl:text>openMenuTree();</xsl:text>
                    </xsl:attribute>
                </xsl:if>
                <xsl:variable name="url">
                    <xsl:text>adminpage?</xsl:text>
                    <xsl:text>fieldname=</xsl:text><xsl:value-of select="$fieldname" />
                    <xsl:text>&amp;mimetype=</xsl:text><xsl:value-of select="$mimetype" />
                    <xsl:text>&amp;extension=</xsl:text><xsl:value-of select="$extension" />
                </xsl:variable>

                <div style="margin-top: 0">
                    <xsl:choose>
                        <xsl:when test="$subop = 'moveFolder' or $subop = 'moveFile'">
                            <xsl:apply-templates select="/*" mode="displaytree">
                                <xsl:with-param name="onclick_resources" select="'window.opener.refactor.move'"/>
                                <xsl:with-param name="sourceKey" select="$sourceKey"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="/*" mode="displaytree">
                                <xsl:with-param name="url" select="$url" />
                                <xsl:with-param name="sourceKey" select="$sourceKey"/>
                            </xsl:apply-templates>
                        </xsl:otherwise>
                    </xsl:choose>
                </div>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
