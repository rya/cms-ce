<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:output method="html" />

	<xsl:template name="displayuserstorepath">
		<xsl:param name="userstorekey" />
		<xsl:param name="userstorename" />
		<xsl:param name="mode" />
		<xsl:param name="isGroups" />
		<xsl:param name="popupMode" select="false()" />
		<xsl:param name="disabled" select="false()"/>
		<xsl:param name="edit" select="false()"/>

		<xsl:choose>
			<xsl:when test="$disabled">
				<xsl:text>%mnuUserstores%</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<a href="adminpage?page=290&amp;op=browse">%mnuUserstores%</a>
			</xsl:otherwise>
		</xsl:choose>


		<xsl:choose>
			<xsl:when test="$userstorekey = '' and $isGroups">
				<xsl:choose>
					<xsl:when test="$page = 700 or $page = 701">
						<xsl:variable name="text">
							<xsl:choose>
								<xsl:when test="$popupMode">%msgAllGroups%</xsl:when>
								<xsl:otherwise>%mnuGlobalGroups%</xsl:otherwise>							
							</xsl:choose>
						</xsl:variable>
					
						<xsl:text> / </xsl:text>
						<xsl:choose>
							<xsl:when test="$disabled">
								<xsl:value-of select="$text"/>
							</xsl:when>
							<xsl:otherwise>
								<a href="adminpage?page=700&amp;op=browse&amp;mode={$mode}"><xsl:value-of select="$text"/></a>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise/>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="$userstorekey = '' and $mode = 'users'">
				<xsl:text> / %optAllUsers%</xsl:text>
			</xsl:when>

			<xsl:otherwise>
        <xsl:text> </xsl:text>
          <xsl:choose>
            <xsl:when test="$disabled">
              <xsl:value-of select="concat('/ ',$userstorename)"/>
            </xsl:when>
            <xsl:when test="$edit">
              <span id="titlename"><xsl:value-of select="concat('/ ',$userstorename)"/></span>
            </xsl:when>
            <xsl:when test="$userstorename">
              <xsl:text> / </xsl:text><a href="adminpage?page=290&amp;op=page&amp;key={$userstorekey}">
                <xsl:value-of select="$userstorename"/>
              </a>
            </xsl:when>
            <xsl:otherwise>
            </xsl:otherwise>
          </xsl:choose>


				<xsl:if test="not($mode = '')">
					<xsl:text> / </xsl:text>
					<xsl:variable name="text">
						<xsl:choose>
							<xsl:when test="$isGroups">
								<xsl:text>%mnuGroups%</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>%mnuUsers%</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					
					<xsl:choose>
						<xsl:when test="$disabled">
							<xsl:value-of select="$text"/>
						</xsl:when>
						<xsl:otherwise>
							<a href="adminpage?page=700&amp;op=browse&amp;mode={$mode}&amp;userstorekey={$userstorekey}">
								<xsl:value-of select="$text"/>
							</a>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

</xsl:stylesheet>
