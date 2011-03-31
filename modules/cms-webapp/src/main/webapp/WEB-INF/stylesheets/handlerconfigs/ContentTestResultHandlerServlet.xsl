<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:template match="attribute::handler[self::node() = 'com.enonic.vertical.adminweb.handlers.ContentTestResultHandlerServlet']" mode="newbutton">
		<xsl:value-of select="false()"/>
	</xsl:template>

</xsl:stylesheet>
