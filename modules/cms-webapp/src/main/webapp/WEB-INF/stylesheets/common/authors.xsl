<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

	<xsl:template name="authors">
		<xsl:param name="disabled" select="false()"/>

		<xsl:if test="not($disabled)">
			<script type="text/javascript" language="JavaScript">
				
				function removeAuthor( table, objThis ) {
          var count = itemcount(document.getElementsByName(objThis.name));

          if( count == 1 ) {
						document.getElementsByName('contentdata_author')[0].value = "";
						return;
					}

					var index = getObjectIndex(objThis);
          var r = document.getElementById(table).getElementsByTagName('tr')[index];
          if (r)
            document.getElementById(table).removeChild(r);
				}
				
				function addAuthor( table ) {
					addTableRow( table, 0, true );
				}
				
			</script>
		</xsl:if>

		<tr>
			<td valign="top">%fldAuthors%:</td>
			<td colspan="3">
				<table border="0">
          <tbody id="tblauthors" name="tblauthors">
        <xsl:if test="not(/contents/content/contentdata/authors/author)">
					<tr>
						<td>
							<input type="text" name="contentdata_author" size="25" maxlength="50">
								<xsl:if test="$disabled">
									<xsl:attribute name="disabled">disabled</xsl:attribute>
							   </xsl:if>
							</input>
						</td>
						<td align="left">
                            <xsl:call-template name="button">
                                <xsl:with-param name="name">removeauthor</xsl:with-param>
                                <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
								<xsl:with-param name="disabled" select="$disabled"/>
                                <xsl:with-param name="onclick">
                                    <xsl:text>removeAuthor('tblauthors',this)</xsl:text>
                                </xsl:with-param>
                            </xsl:call-template>
						</td>
					</tr>
				</xsl:if>
				<xsl:for-each select="/contents/content/contentdata/authors/author">
					<tr>
						<td>
							<input type="text" name="contentdata_author" size="25" maxlength="50">
							   <xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
							   <xsl:if test="$disabled">
									<xsl:attribute name="disabled">disabled</xsl:attribute>
							   </xsl:if>
							</input>
						</td>
						<td align="left">
							<xsl:call-template name="button">
                                <xsl:with-param name="name">removeauthor</xsl:with-param>
                                <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
								<xsl:with-param name="disabled" select="$disabled"/>
                                <xsl:with-param name="onclick">
                                    <xsl:text>removeAuthor('tblauthors',this)</xsl:text>
                                </xsl:with-param>
                            </xsl:call-template>
						</td>
					</tr>
				</xsl:for-each>

          </tbody>

        </table>
			</td>
		</tr>
		<tr>
			<td></td>
			<td colspan="4">
				<xsl:call-template name="button">
					<xsl:with-param name="type" select="'button'"/>
					<xsl:with-param name="caption" select="'%cmdNewAuthor%'"/>
					<xsl:with-param name="name" select="'addauthor'"/>
					<xsl:with-param name="disabled" select="$disabled"/>
					<xsl:with-param name="onclick">
						<xsl:text>javascript: addAuthor('tblauthors');</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
			</td>
		</tr>

	</xsl:template>

</xsl:stylesheet>
