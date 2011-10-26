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

	<xsl:template name="relatedlinks">
		<xsl:param name="validateurls" select="'true'"/>
		<xsl:param name="disabled" select="false()"/>

		<xsl:if test="not($disabled)">
			<script type="text/javascript" language="JavaScript">
	
				function addRelatedLink( table ) {
					addTableRow( table, 0, true );
				}
				
				function removeRelatedLink( table, objThis ) {
					var count = itemcount(document.getElementsByName(objThis.name));
					if( count == 1 ) {
            document.getElementsByName('contentdata_relatedlinks_url')[0].value = "";
            document.getElementsByName('contentdata_relatedlinks_description')[0].value = "";
						return;
					}

          var index = getObjectIndex(objThis);
          var r = document.getElementById(table).getElementsByTagName('tr')[index];
          if (r)
            document.getElementById(table).removeChild(r);
    		}
	
			</script>
		</xsl:if>

		<tr>
			<td>
				&nbsp;
			</td>
			<td colspan="3">
				<table width="100%">
          <tbody  id="tblrelatedlinks" name="tblrelatedlinks">

          <xsl:if test="not(/contents/content/contentdata/relatedlinks/relatedlink)">
						<tr>
							<td nowrap="nowrap">%fldURL%:</td>
							<td>
								<input type="text" name="contentdata_relatedlinks_url" size="25">
									<xsl:if test="$disabled">
										<xsl:attribute name="disabled">disabled</xsl:attribute>
									</xsl:if>
									<xsl:if test="$validateurls = 'true'">
										<xsl:attribute name="onchange">javascript: validateURL(this);</xsl:attribute>
									</xsl:if>
								</input>
							</td>
							<td nowrap="nowrap">%fldDescription%:</td>
							<td>
								<input type="text" name="contentdata_relatedlinks_description" size="25"/>
                            </td>
                            <td>
                                <xsl:call-template name="button">
									<xsl:with-param name="disabled" select="$disabled"/>
                                    <xsl:with-param name="name"><xsl:text>removerellink</xsl:text></xsl:with-param>
                                    <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                                    <xsl:with-param name="onclick">
                                        <xsl:text>removeRelatedLink('tblrelatedlinks',this);</xsl:text>
                                    </xsl:with-param>
                                </xsl:call-template>
							</td>
						</tr>
					</xsl:if>
					<xsl:for-each select="/contents/content/contentdata/relatedlinks/relatedlink">
						<tr>
							<td nowrap="nowrap">%fldURL%:</td>
							<td>
								<input type="text" name="contentdata_relatedlinks_url" size="25">
								   <xsl:attribute name="value"><xsl:value-of select="url"/></xsl:attribute>
								   <xsl:if test="$disabled">
										<xsl:attribute name="disabled">disabled</xsl:attribute>
								   </xsl:if>
								</input>
							</td>
							<td nowrap="nowrap">%fldDescription%:</td>
							<td>
								<input type="text" name="contentdata_relatedlinks_description" size="25">
									<xsl:attribute name="value"><xsl:value-of select="description"/></xsl:attribute>
									<xsl:if test="$disabled">
										<xsl:attribute name="disabled">disabled</xsl:attribute>
								   </xsl:if>
								</input>
							</td>
							<td>
								<xsl:call-template name="button">
									<xsl:with-param name="disabled" select="$disabled"/>
									<xsl:with-param name="name"><xsl:text>removerellink</xsl:text></xsl:with-param>
									<xsl:with-param name="image" select="'images/icon_remove.gif'"/>
									<xsl:with-param name="onclick">
										<xsl:text>removeRelatedLink('tblrelatedlinks',this);</xsl:text>
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
			<td>
				&nbsp;
			</td>
			<td colspan="3">
				<xsl:call-template name="button">
					<xsl:with-param name="name">addrellink</xsl:with-param>
					<xsl:with-param name="caption" select="'%cmdNewLink%'"/>
					<xsl:with-param name="disabled" select="$disabled"/>
					<xsl:with-param name="onclick">
						<xsl:text>addRelatedLink('tblrelatedlinks');</xsl:text>
					</xsl:with-param>
				</xsl:call-template>
			</td>
		</tr>

	</xsl:template>


</xsl:stylesheet>
