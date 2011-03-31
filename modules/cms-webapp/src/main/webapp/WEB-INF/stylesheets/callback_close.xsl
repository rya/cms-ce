<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html"/>

	<xsl:include href="common/escapequotes.xsl"/>
	
	<xsl:param name="fieldname"/>
    <xsl:param name="fieldrow"/>
	<xsl:param name="key"/>
	<xsl:param name="versionkey"/>
    <xsl:param name="title"/>
	<xsl:param name="current"/>
	<xsl:param name="callback"/>

	<xsl:template match="/">
		<html>
			<head>
				<script type="text/javascript" language="JavaScript">
					
					function callback() {
						<xsl:text>window.top.opener.</xsl:text>
						<xsl:value-of select="$callback"/>
						<xsl:text>('</xsl:text>
						<xsl:value-of select="$fieldname"/>
						<xsl:text>',</xsl:text>
						<xsl:value-of select="$fieldrow"/>
						<xsl:text>,</xsl:text>
						<xsl:value-of select="$key"/>
						<xsl:text>, '</xsl:text>
						<xsl:call-template name="escapequotes">
							<xsl:with-param name="string" select="$title"/>
						</xsl:call-template>
						<xsl:text>',</xsl:text>
						<xsl:value-of select="$current"/>
						<xsl:text>);</xsl:text>
						window.close();
					}
					
				</script>
			</head>
			<body onload="javascript: callback();"/>
		</html>
	</xsl:template>

</xsl:stylesheet>
