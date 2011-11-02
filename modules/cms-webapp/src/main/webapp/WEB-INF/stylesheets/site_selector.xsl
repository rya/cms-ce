<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>

  <xsl:template match="/">
    <xsl:call-template name="sitelist"/>
  </xsl:template>

  <xsl:template name="sitelist">
    <html>

	<title>%headSelectDomain%:</title>

    <script type="text/javascript" language="JavaScript">
	function returnValue( siteKey, siteKeyView, unitKey, unitKeyView )
	{
		window.top.opener.document.formAdmin.sitekey.value = siteKey;
		window.top.opener.document.formAdmin.viewsitekey.value = siteKeyView;
		window.top.opener.document.formAdmin.unitkey.value = unitKey;
		//window.top.opener.document.formAdmin.viewunitkey.value = unitKeyView;
		window.top.opener.rebuildMenu( siteKey, unitKey );
		window.top.opener.location.reload(true);
		window.close();
	}
    </script>

    <link rel="stylesheet" type="text/css" href="css/admin.css"/>

    <body>

    <table width="100%" border="0" cellspacing="2" cellpadding="2">
        <xsl:for-each select="/sites/site">
		  <xsl:sort select="name"/>
          <tr>
            <td>

            <xsl:variable name="name">
              <xsl:call-template name="replacesubstring">
                <xsl:with-param name="stringsource" select="name"/>
                <xsl:with-param name="substringsource" select='"&apos;"'/>
                <xsl:with-param name="substringdest" select='"\&apos;"'/>
              </xsl:call-template>
            </xsl:variable>

              <a href="javascript: void 0">
                 <xsl:attribute name="onclick">returnValue('<xsl:value-of select="@key"/>','<xsl:value-of select="$name"/>','<xsl:value-of select="rootunit/@key"/>','<xsl:value-of select="rootunit"/>')</xsl:attribute>
		 <xsl:value-of select="name"/>
	      </a>
            </td>
          </tr>
        </xsl:for-each>
    </table>

    </body>

    </html>

  </xsl:template>


        <!-- Standard substring replace template -->
	<xsl:template name="replacesubstring">
		<xsl:param name="stringsource"/>
		<xsl:param name="substringsource"/>
		<xsl:param name="substringdest"/>

		<xsl:choose>

			<xsl:when test="contains($stringsource,$substringsource)">
				<xsl:value-of select="concat(substring-before($stringsource,$substringsource),$substringdest)"/>
				<xsl:call-template name="replacesubstring">
					<xsl:with-param name="stringsource" select="substring-after($stringsource,$substringsource)"/>
					<xsl:with-param name="substringsource" select="$substringsource"/>
					<xsl:with-param name="substringdest" select="$substringdest"/>
				</xsl:call-template>
			</xsl:when>

			<xsl:otherwise>
				<xsl:value-of select="$stringsource"/>
			</xsl:otherwise>

		</xsl:choose>

	</xsl:template>


</xsl:stylesheet>