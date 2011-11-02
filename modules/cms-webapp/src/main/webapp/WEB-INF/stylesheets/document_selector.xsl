<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>

  <xsl:param name="returnkey"/>
  <xsl:param name="returnview"/>

  <xsl:template match="/">
    <xsl:call-template name="documentlist"/>
  </xsl:template>
 
  <xsl:template name="documentlist">

    <script type="text/javascript" language="JavaScript">
	function returnValue( key, view, returnkey, returnview )
	{
		window.top.opener.document.formAdmin(returnkey).value = key;
		window.top.opener.document.formAdmin(returnview).value = view;
		window.close();
	}
    </script>

    <link rel="stylesheet" type="text/css" href="css/admin.css"/>

    <table width="100%" border="0" cellspacing="2" cellpadding="2">
      <tr bgcolor="#003b9f"><td colspan="4"><font color="white"><b>Velg dokument:</b></font></td></tr>
        <xsl:for-each select="/contents/content">
	  <xsl:sort select="contentdata/title"/>
          <tr>
            <td>

            <xsl:variable name="name">
              <xsl:call-template name="replacesubstring">
                <xsl:with-param name="stringsource" select="contentdata/title"/>
                <xsl:with-param name="substringsource" select='"&apos;"'/>
                <xsl:with-param name="substringdest" select='"\&apos;"'/>
              </xsl:call-template>
            </xsl:variable>

              <a href="javascript: void 0">
                 <xsl:attribute name="onclick">returnValue('<xsl:value-of select="@key"/>','<xsl:value-of select="$name"/>','<xsl:value-of select="$returnkey"/>','<xsl:value-of select="$returnview"/>')</xsl:attribute>  
		 <xsl:value-of select="contentdata/title"/> 
	      </a> 
            </td>
          </tr>
        </xsl:for-each>
    </table>
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