<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="html"/>

	<xsl:include href="common/button.xsl"/>

	<xsl:template match="/">

		<xsl:choose>
			<xsl:when test="count(/contents/content/relatedcontentkeys/relatedcontentkey[@level = -1]) != 0">

				<table class="full">
					<tr>
						<th class="title left">%fldTitle%</th>
						<th class="left">%fldContentType%</th>
						<th class="left">%fldRepositoryPath%</th>
						<th class="left">&nbsp;</th>
					</tr>
					<xsl:for-each select="/contents/content/relatedcontentkeys/relatedcontentkey[@level = -1]">
						<xsl:variable name="key" select="@key"/>
						<xsl:variable name="contentelem" select="/contents/relatedcontents/content[@key = $key]"/>
							<tr>
								<td id="inuseby_title_{position()}">
									<xsl:value-of select="$contentelem/title"/>
								</td>
								<td>
									<xsl:value-of select="$contentelem/@contenttype"/>
				 				</td>
								<td>
									<xsl:value-of select="$contentelem/@repositorypath"/>
								</td>
								<td class="right">
									<xsl:call-template name="button">
										<xsl:with-param name="name">editcontent</xsl:with-param>
										<xsl:with-param name="image" select="'images/icon_edit_small.gif'"/>
										<xsl:with-param name="tooltip" select="'%cmdEdit%'"/>
										<xsl:with-param name="onclick">
											<xsl:text>OpenEditContentPopup(</xsl:text>
											<xsl:value-of select="$contentelem/@key"/>
											<xsl:text>, -1, 'inuseby', getObjectIndex(this), 'callback_inuseby');</xsl:text>
										</xsl:with-param>
									</xsl:call-template>
								</td>
							</tr>
					</xsl:for-each>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>%msgNoRelatedContent%</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
