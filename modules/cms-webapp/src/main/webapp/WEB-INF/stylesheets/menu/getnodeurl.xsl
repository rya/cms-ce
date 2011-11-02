<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:include href="getpagekey.xsl"/>
	<xsl:include href="../common/seturlparameter.xsl"/>

	<xsl:template name="getnodeurl">
		<xsl:param name="node"/>
		<xsl:param name="url"/>
		
		<!-- set disabled parameter -->
		<xsl:variable name="disableurl">
			<xsl:choose>
				<xsl:when test="$node/@disable = 'true'">
					<xsl:call-template name="seturlparameter">
						<xsl:with-param name="url" select="$url"/>
						<xsl:with-param name="parameter" select="'disabled'"/>
						<xsl:with-param name="value" select="'true'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="seturlparameter">
						<xsl:with-param name="url" select="$url"/>
						<xsl:with-param name="parameter" select="'disabled'"/>
						<xsl:with-param name="value" select="'false'"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="pagekey">
			<xsl:call-template name="getpagekey">
				<xsl:with-param name="nodename" select="name($node)"/>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- set the correct handler (page) -->
		<xsl:variable name="handlerurl">
			<xsl:call-template name="seturlparameter">
				<xsl:with-param name="url" select="$disableurl"/>
				<xsl:with-param name="parameter" select="'page'"/>
				<xsl:with-param name="value" select="$pagekey"/>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- now add key parameter if present -->
		<xsl:variable name="keyurl">
			<xsl:choose>
				<xsl:when test="@key">
					<xsl:call-template name="seturlparameter">
						<xsl:with-param name="url" select="$handlerurl"/>
						<xsl:with-param name="parameter">
							<xsl:value-of select="name($node)"/>
							<xsl:text>key</xsl:text>
						</xsl:with-param>
						<xsl:with-param name="value" select="$node/@key"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$handlerurl"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:value-of select="$keyurl"/>
	</xsl:template>
	
</xsl:stylesheet>