<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp   "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

	<xsl:template name="sectioncommands">
  		<xsl:param name="disableall" select="'false'"/>

		<xsl:param name="hidenew" select="'false'"/>
		<xsl:param name="hideedit" select="'false'"/>
		<xsl:param name="hideremove" select="'false'"/>
		<xsl:param name="hideadd" select="'false'"/>
		<xsl:param name="hidecopy" select="'true'"/>
		<xsl:param name="disablenew" select="'false'"/>
		<xsl:param name="disableedit" select="'false'"/>
		<xsl:param name="disableremove" select="'false'"/>
		<xsl:param name="disableadd" select="'false'"/>
		<xsl:param name="disablecopy" select="'true'"/>

		<xsl:if test="not($hideadd = 'false')">
			<xsl:call-template name="button">
				<xsl:with-param name="caption" select="'%cmdAdd%'"/>
				<xsl:with-param name="disabled" select="$disableadd"/>
				<xsl:with-param name="onclick">
					<xsl:text>javascript:OpenContentPopup(</xsl:text>
					<xsl:text>-1, -1, 'addcontenttosection', null, -1, contentTypes);</xsl:text>
				</xsl:with-param>
			  </xsl:call-template>
			  <xsl:text>&nbsp;</xsl:text>
          </xsl:if>

	  <xsl:if test="not($hidenew = 'false')">
		  <xsl:call-template name="button">
			  <xsl:with-param name="type" select="'link'"/>
			  <xsl:with-param name="caption" select="'%cmdNewSection%'"/>
			  <xsl:with-param name="disabled" select="$disablenew"/>
			  <xsl:with-param name="href">
				  <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
				  <xsl:text>&amp;selecteddomainkey=</xsl:text>
				  <xsl:value-of select="$selecteddomainkey"/>
				  <xsl:text>&amp;menukey=</xsl:text>
				  <xsl:value-of select="$menukey"/>
				  <xsl:text>&amp;supersectionkey=</xsl:text>
				  <xsl:value-of select="$sec"/>
			  </xsl:with-param>
		  </xsl:call-template>
		  <xsl:text>&nbsp;</xsl:text>
      </xsl:if>

      <xsl:if test="$sec">
      	  <xsl:if test="not($hideedit = 'false')">
			  <xsl:call-template name="button">
				  <xsl:with-param name="type" select="'link'"/>
				  <xsl:with-param name="caption" select="'%cmdEditSection%'"/>
				  <xsl:with-param name="disabled" select="$disableedit"/>
				  <xsl:with-param name="href">
					  <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
					  <xsl:text>&amp;op=form</xsl:text>
					  <xsl:text>&amp;selecteddomainkey=</xsl:text>
					  <xsl:value-of select="$selecteddomainkey"/>
					  <xsl:text>&amp;menukey=</xsl:text>
					  <xsl:value-of select="$menukey"/>
					  <xsl:text>&amp;key=</xsl:text>
					  <xsl:value-of select="$sec"/>
				  </xsl:with-param>
			  </xsl:call-template>
			  <xsl:text>&nbsp;</xsl:text>
          </xsl:if>

          <xsl:if test="not($hideremove = 'false')">
          	  <xsl:variable name="tooltip">
          	  	  <xsl:if test="$disableremove = 'true'">%altCannotRemoveSection%</xsl:if>
          	  </xsl:variable>

			  <xsl:call-template name="button">
				  <xsl:with-param name="type" select="'link'"/>
				  <xsl:with-param name="caption" select="'%cmdRemoveSection%'"/>
				  <xsl:with-param name="disabled" select="$disableremove"/>
				  <xsl:with-param name="tooltip" select="$tooltip"/>
				  <xsl:with-param name="condition">
					  <xsl:text>confirm('%msgConfirmRemoveSection%')</xsl:text>
				  </xsl:with-param>
				  <xsl:with-param name="href">
					  <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
					  <xsl:text>&amp;op=remove</xsl:text>
					  <xsl:text>&amp;selecteddomainkey=</xsl:text>
					  <xsl:value-of select="$selecteddomainkey"/>
					  <xsl:text>&amp;menukey=</xsl:text>
					  <xsl:value-of select="$menukey"/>
					  <xsl:text>&amp;key=</xsl:text>
					  <xsl:value-of select="$sec"/>
				  </xsl:with-param>
			  </xsl:call-template>
			  <xsl:text>&nbsp;</xsl:text>
          </xsl:if>

          <xsl:if test="not($hidecopy = 'false')">
          	  <xsl:variable name="tooltip">
          	  	  <xsl:if test="$disablecopy = 'true'">%altCannotCopySection%</xsl:if>
          	  </xsl:variable>

			  <xsl:call-template name="button">
				  <xsl:with-param name="type" select="'link'"/>
				  <xsl:with-param name="caption" select="'%cmdCopySection%'"/>
				  <xsl:with-param name="disabled" select="$disablecopy"/>
				  <xsl:with-param name="tooltip" select="$tooltip"/>
				  <xsl:with-param name="condition">
					  <xsl:text>confirm('%msgConfirmCopySection%')</xsl:text>
				  </xsl:with-param>
				  <xsl:with-param name="href">
					  <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
					  <xsl:text>&amp;op=copy</xsl:text>
					  <xsl:text>&amp;selecteddomainkey=</xsl:text>
					  <xsl:value-of select="$selecteddomainkey"/>
					  <xsl:text>&amp;menukey=</xsl:text>
					  <xsl:value-of select="$menukey"/>
					  <xsl:text>&amp;key=</xsl:text>
					  <xsl:value-of select="$sec"/>
				  </xsl:with-param>
			  </xsl:call-template>
			  <xsl:text>&nbsp;</xsl:text>
          </xsl:if>

      </xsl:if>

  </xsl:template>

</xsl:stylesheet>
