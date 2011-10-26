<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:template match="attribute::handler[self::node() = 'com.enonic.vertical.adminweb.handlers.ContentNewsletterHandlerServlet']" mode="operations">
		<xsl:param name="contentelem"/>
		
		<xsl:if test="not($contentelem/accessrights/userright) or $contentelem/accessrights/userright/@categorypublish = 'true'">
			<td align="center">
				<xsl:call-template name="button">
					<xsl:with-param name="style" select="'flat'"/>
					<xsl:with-param name="type" select="'link'"/>
					<xsl:with-param name="image" select="'images/icon_mail.gif'"/>
					<xsl:with-param name="tooltip" select="'%altSendNewsletter%'"/>
					<xsl:with-param name="href">
						<xsl:text>adminpage?page=</xsl:text>
						<xsl:value-of select="number($contentelem/@contenttypekey) + 999"/>
						<xsl:text>&amp;op=send&amp;key=</xsl:text>
						<xsl:value-of select="$contentelem/@key"/>
						<xsl:text>&amp;selectedunitkey=</xsl:text>
						<xsl:value-of select="$contentelem/@unitkey"/>
						<xsl:text>&amp;cat=</xsl:text>
						<xsl:value-of select="$contentelem/categoryname/@key"/>
					</xsl:with-param>
				</xsl:call-template>
			</td>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
