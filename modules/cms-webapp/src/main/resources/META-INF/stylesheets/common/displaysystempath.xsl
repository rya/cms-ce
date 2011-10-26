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

  <xsl:template name="displaysystempath">
    <xsl:param name="page"/>
    <xsl:param name="mode"/>
    <xsl:param name="option" select="''"/>
    <xsl:param name="domainkey" select="''"/>
    <xsl:param name="domainname" select="''"/>
    <xsl:param name="nolinks" select="false()"/>

    <xsl:text>%mnuAdmin%</xsl:text>

    <xsl:choose>
      <xsl:when test="$page = 10">
        <xsl:if test="$mode != ''">
          <xsl:text>&nbsp;/&nbsp;</xsl:text>
          <xsl:choose>
            <xsl:when test="$mode = 'system'">
              <a href="adminpage?page={$page}&amp;op=page&amp;mode={$mode}">
                <xsl:text>%headSystem%</xsl:text>
              </a>
            </xsl:when>
            <xsl:when test="$mode = 'java_properties'">
              <a href="adminpage?page={$page}&amp;op=page&amp;mode=system">
                <xsl:text>%headSystem%</xsl:text>
              </a>
              <xsl:text>&nbsp;/&nbsp;</xsl:text>
              <a href="adminpage?page={$page}&amp;op=page&amp;mode={$mode}">
                <xsl:text>%headProperties%</xsl:text>
              </a>
            </xsl:when>
            <xsl:when test="$mode = 'system_cache'">
              <a href="adminpage?page={$page}&amp;op=page&amp;mode={$mode}">
                <xsl:text>%headSystemCache%</xsl:text>
              </a>
            </xsl:when>
          </xsl:choose>
        </xsl:if>
      </xsl:when>

      <xsl:when test="$page = 280">
        <xsl:text>&nbsp;/&nbsp;</xsl:text>
        <xsl:choose>
          <xsl:when test="$nolinks">
            <xsl:text>%headLDAPServers%</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <a>
              <xsl:attribute name="href">
                <xsl:text>adminpage?op=browse&amp;page=</xsl:text><xsl:value-of select="$page"/>
              </xsl:attribute>
              <xsl:text>%headLDAPServers%</xsl:text>
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="$page = 275">
        <xsl:text>&nbsp;/&nbsp;</xsl:text>
        <xsl:choose>
          <xsl:when test="$nolinks">
            <xsl:text>%headObjectClasses%</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <a>
              <xsl:attribute name="href">
                <xsl:text>adminpage?op=browse&amp;page=</xsl:text><xsl:value-of select="$page"/>
              </xsl:attribute>
              <xsl:text>%headObjectClasses%</xsl:text>
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="$page = 300">
        <xsl:text>&nbsp;/&nbsp;</xsl:text>
        <xsl:choose>
          <xsl:when test="$nolinks">
            <xsl:text>%headDomains%</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <a href="adminpage?op=browse&amp;page={$page}">
              <xsl:text>%headDomains%</xsl:text>
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="$page= 700  or ($page = 350 and $domainkey &gt;= 0)">
        <xsl:choose>
          <xsl:when test="$nolinks">
            <xsl:text>%headUserstores%</xsl:text>
            <xsl:text>&nbsp;/&nbsp;</xsl:text>
            <xsl:value-of select="$domainname"/>
            <xsl:text>&nbsp;/&nbsp;</xsl:text>
            <xsl:text>%headUsers%</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <a href="adminpage?op=browse&amp;page=290">
              <xsl:text>%headUserstores%</xsl:text>
            </a>
            <xsl:text>&nbsp;/&nbsp;</xsl:text>
            <a href="adminpage?op=page&amp;page=290&amp;key={$domainkey}">
              <xsl:value-of select="$domainname"/>
            </a>
            <xsl:text>&nbsp;/&nbsp;</xsl:text>
            <a href="adminpage?page={$page}&amp;op=browse&amp;domainkey={$domainkey}&amp;toplevel=true">
              <xsl:text>%headUsers%</xsl:text>
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="$page='701'">
        <xsl:text>&nbsp;/&nbsp;</xsl:text>
        <xsl:choose>
          <xsl:when test="$option = 'userstore'">
            <xsl:choose>
              <xsl:when test="$nolinks">
                <xsl:text>%headUserstores%</xsl:text>
                <xsl:text>&nbsp;/&nbsp;</xsl:text>
                <xsl:value-of select="$domainname"/>
                <xsl:text>&nbsp;/&nbsp;</xsl:text>
                <xsl:text>%headGroups%</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <a href="adminpage?op=browse&amp;page=290">
                  <xsl:text>%headUserstores%</xsl:text>
                </a>
                <xsl:text>&nbsp;/&nbsp;</xsl:text>
                <a href="adminpage?op=page&amp;page=290&amp;key={$domainkey}">
                  <xsl:value-of select="$domainname"/>
                </a>
                <xsl:text>&nbsp;/&nbsp;</xsl:text>
                <a href="adminpage?page={$page}&amp;op=browse&amp;selecteddomainkey={$domainkey}&amp;grouptype=1">
                  <xsl:text>%headGroups%</xsl:text>
                </a>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="$nolinks">
                <xsl:text>%headGlobalGroups%</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <a>
                  <xsl:attribute name="href">
                    <xsl:text>adminpage?op=browse&amp;grouptype=0&amp;page=</xsl:text><xsl:value-of select="$page"/>
                  </xsl:attribute>
                  <xsl:text>%headGlobalGroups%</xsl:text>
                </a>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="$page='290'">
        <xsl:text>&nbsp;/&nbsp;</xsl:text>
        <xsl:choose>
          <xsl:when test="$nolinks">
            <xsl:text>%headUserstores%</xsl:text>
            <xsl:if test="$option = 'userstore'">
              <xsl:text>&nbsp;/&nbsp;</xsl:text>
              <xsl:value-of select="$domainname"/>
            </xsl:if>
          </xsl:when>
          <xsl:otherwise>
            <a href="adminpage?op=browse&amp;page=290">
              <xsl:text>%headUserstores%</xsl:text>
            </a>
            <xsl:if test="$option = 'userstore'">
              <xsl:text>&nbsp;/&nbsp;</xsl:text>
              <a href="adminpage?op=page&amp;page=290&amp;key={$domainkey}">
                <xsl:value-of select="$domainname"/>
              </a>
            </xsl:if>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="$page='450'">
        <xsl:text>&nbsp;/&nbsp;</xsl:text>
        <xsl:choose>
          <xsl:when test="$nolinks">
            <xsl:text>%headMediaTypes%</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <a>
              <xsl:attribute name="href">
                <xsl:text>adminpage?op=browse&amp;page=</xsl:text><xsl:value-of select="$page"/>
              </xsl:attribute>
              <xsl:text>%headMediaTypes%</xsl:text>
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="$page='400'">
        <xsl:text>&nbsp;/&nbsp;</xsl:text>
        <xsl:choose>
          <xsl:when test="$nolinks">
            <xsl:text>%headGlobalContentTypes%</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <a>
              <xsl:attribute name="href">
                <xsl:text>adminpage?op=browse&amp;page=</xsl:text><xsl:value-of select="$page"/>
              </xsl:attribute>
              <xsl:text>%headGlobalContentTypes%</xsl:text>
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="$page='360'">
        <xsl:text>&nbsp;/&nbsp;</xsl:text>
        <xsl:choose>
          <xsl:when test="$nolinks">
            <xsl:text>%headLanguages%</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <a>
              <xsl:attribute name="href">
                <xsl:text>adminpage?op=browse&amp;page=</xsl:text><xsl:value-of select="$page"/>
              </xsl:attribute>
              <xsl:text>%headLanguages%</xsl:text>
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="$page='350'">
        <xsl:text>&nbsp;/&nbsp;</xsl:text>
        <xsl:choose>
          <xsl:when test="$nolinks">
            <xsl:text>%headEventLog%</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <a>
              <xsl:attribute name="href">
                <xsl:text>adminpage?op=browse&amp;from_system=true&amp;page=</xsl:text><xsl:value-of select="$page"/>
              </xsl:attribute>
              <xsl:text>%headEventLog%</xsl:text>
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="$page='1050'">
        <xsl:text>&nbsp;/&nbsp;</xsl:text>
        <xsl:choose>
          <xsl:when test="$nolinks">
            <xsl:text>%headContentHandlers%</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <a>
              <xsl:attribute name="href">
                <xsl:text>adminpage?op=browse&amp;page=</xsl:text><xsl:value-of select="$page"/>
              </xsl:attribute>
              <xsl:text>%headContentHandlers%</xsl:text>
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="$page='370'">
        <xsl:text>&nbsp;/&nbsp;</xsl:text>
        <xsl:choose>
          <xsl:when test="$nolinks">
            <xsl:text>%headScheduler%</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <a>
              <xsl:attribute name="href">
                <xsl:text>adminpage?op=browse&amp;page=</xsl:text><xsl:value-of select="$page"/>
              </xsl:attribute>
              <xsl:text>%headScheduler%</xsl:text>
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
    </xsl:choose>

  </xsl:template>

</xsl:stylesheet>