<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
    ]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html" />

  <xsl:include href="common/generic_parameters.xsl" />
  <xsl:include href="common/operations_template.xsl" />
  <xsl:include href="common/javascriptPreload.xsl" />
  <xsl:include href="common/genericheader.xsl" />
  <xsl:include href="common/formatdate.xsl"/>
  <xsl:include href="common/tablecolumnheader.xsl"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="common/tablerowpainter.xsl"/>

  <xsl:param name="sortby" select="'name'"/>
  <xsl:param name="sortby-direction" select="'ascending'"/>
  <xsl:param name="returnkey"/>
  <xsl:param name="returnview"/>
  <xsl:param name="returnrow"/>
  <xsl:param name="objectdoc" select="''"/>

  <xsl:variable name="pageURL">
    <xsl:text>adminpage?page=</xsl:text>
    <xsl:value-of select="$page"/>
    <xsl:text>&amp;op=select</xsl:text>
    <xsl:text>&amp;menukey=</xsl:text>
    <xsl:value-of select="$menukey"/>
  </xsl:variable>

  <xsl:template match="/">
    <xsl:call-template name="contentobjectlist"/>
  </xsl:template>

  <xsl:template name="contentobjectlist">

    <html>
      <script type="text/javascript" src="javascript/admin.js">//</script>

      <script type="text/javascript" language="JavaScript">
	      function returnValue( key, view, returnkey, returnview, objectdoc)
	      {
          if (window.top.opener.contentObjectList[key] == 'added') {alert("%alertContentPortletAlreadyAdded%");} else {
		      <xsl:choose>
		        <xsl:when test="$returnrow=''">
              <xsl:if test="$objectdoc != ''">
                window.top.opener.document.forms['formAdmin']['<xsl:value-of select="$objectdoc"/>'].value = objectdoc;
              </xsl:if>

              window.top.opener.document.forms['formAdmin'][returnkey].value = key;
              window.top.opener.document.forms['formAdmin'][returnview].value = view;
      		  </xsl:when>
		        <xsl:otherwise>

			        if( window.top.opener.document.forms['formAdmin'][returnkey].length != null )
        			{
              <xsl:if test="$objectdoc != ''">
                window.top.opener.document.forms['formAdmin']['<xsl:value-of select="$objectdoc"/>'][<xsl:value-of select="$returnrow"/>].value = objectdoc;
              </xsl:if>
				      <xsl:text>window.top.opener.document.forms['formAdmin'][returnkey][</xsl:text>
				      <xsl:value-of select="$returnrow"/>
				      <xsl:text>].value = key;</xsl:text>

              <xsl:text>window.top.opener.document.forms['formAdmin'][returnview][</xsl:text>
              <xsl:value-of select="$returnrow"/>
              <xsl:text>].value = view;</xsl:text>
			        }
              else
              {
              <xsl:if test="$objectdoc != ''">
                window.top.opener.document.forms['formAdmin']['<xsl:value-of select="$objectdoc"/>'].value = objectdoc;
              </xsl:if>

              window.opener.document.forms['formAdmin'][returnkey].value = key;
              window.opener.document.forms['formAdmin'][returnview].value = view;
			      }
		        </xsl:otherwise>
          </xsl:choose>

          window.top.opener.contentObjectList[key] = "added";

		      window.close();
        }
	    }
    </script>

      <title>%headSelectPortlet%:</title>

      <link rel="stylesheet" type="text/css" href="css/admin.css"/>

      <xsl:variable name="pageURL">
        <xsl:text>adminpage?page=</xsl:text>
        <xsl:value-of select="$page"/>
        <xsl:text>&amp;op=select</xsl:text>
        <xsl:text>&amp;menukey=</xsl:text>
        <xsl:value-of select="$menukey"/>
        <xsl:text>&amp;returnkey=</xsl:text><xsl:value-of select="$returnkey"/>
        <xsl:text>&amp;returnview=</xsl:text><xsl:value-of select="returnview"/>
        <xsl:text>&amp;returnrow=</xsl:text><xsl:value-of select="returnrow"/>
        <xsl:text>&amp;objectdoc=</xsl:text><xsl:value-of select="objectdoc"/>
      </xsl:variable>

      <body id="popup">
        <h1>
          <xsl:call-template name="genericheader">
            <xsl:with-param name="links" select="false()"/>
          </xsl:call-template>
          <xsl:text>%headPortlets%</xsl:text>
        </h1>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td>
              <table width="100%" cellspacing="0" cellpadding="0" class="browsetable">
                <tr>

                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="caption" select="'%fldName%'" />
                    <xsl:with-param name="pageURL" select="$pageURL" />
                    <xsl:with-param name="current-sortby" select="$sortby" />
                    <xsl:with-param name="current-sortby-direction" select="$sortby-direction" />
                    <xsl:with-param name="sortby" select="'name'" />
                    <xsl:with-param name="sortable" select="'false'" />
                  </xsl:call-template>


                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="width" select="'100'" />
                    <xsl:with-param name="align" select="'center'" />
                    <xsl:with-param name="caption" select="'%fldCaching%'" />
                    <xsl:with-param name="sortable" select="'false'" />
                  </xsl:call-template>

                  <xsl:call-template name="tablecolumnheader">
                    <xsl:with-param name="width" select="'100'" />
                    <xsl:with-param name="align" select="'center'" />
                    <xsl:with-param name="caption" select="'%fldModified%'" />
                    <xsl:with-param name="pageURL" select="$pageURL" />
                    <xsl:with-param name="current-sortby" select="$sortby" />
                    <xsl:with-param name="current-sortby-direction" select="$sortby-direction" />
                    <xsl:with-param name="sortby" select="'timestamp'" />
                    <xsl:with-param name="sortable" select="'false'" />
                  </xsl:call-template>

                </tr>

                <xsl:variable name="sortby-data-type">text</xsl:variable>

                <xsl:for-each select="/contentobjects/contentobject">

                  <xsl:sort data-type="{$sortby-data-type}" order="{$sortby-direction}" select="*[name() = $sortby] | @*[concat('@',name()) = $sortby]"/>

                  <tr>

                    <xsl:call-template name="tablerowpainter"/>

                    <xsl:variable name="name">
                      <xsl:call-template name="replacesubstring">
                        <xsl:with-param name="stringsource" select="name"/>
                        <xsl:with-param name="substringsource" select='"&apos;"'/>
                        <xsl:with-param name="substringdest" select='"\&apos;"'/>
                      </xsl:call-template>
                    </xsl:variable>

                    <xsl:variable name="function">
                      <xsl:text>javascript: returnValue('</xsl:text>
                      <xsl:value-of select="@key"/>
                      <xsl:text>','</xsl:text>
                      <xsl:value-of select="$name"/>
                      <xsl:text>','</xsl:text>
                      <xsl:value-of select="$returnkey"/>
                      <xsl:text>','</xsl:text>
                      <xsl:value-of select="$returnview"/>
                      <xsl:text>','</xsl:text>
                      <xsl:value-of select="contentobjectdata/datasources/@objectdocument"/>
                      <xsl:text>');</xsl:text>
                    </xsl:variable>

                    <td class="browsetablecell" title="%msgClickToEdit%" onclick="{$function}">
                      <!--a id="operation_edit_{@key}" style="color: 000000; height: 18px">
                                        		<xsl:attribute name="onclick">javascript: returnValue('<xsl:value-of select="@key"/>','<xsl:value-of select="$name"/>','<xsl:value-of select="$returnkey"/>','<xsl:value-of select="$returnview"/>', '<xsl:value-of select="contentobjectdata/datasources/@objectdocument"/>')</xsl:attribute-->
                      <xsl:value-of select="name" />
                      <!--/a-->
                    </td>

                    <td class="browsetablecell" title="%msgClickToEdit%" onclick="{$function}" style="text-align: center">
                      <xsl:choose>
                        <xsl:when test="contentobjectdata/@cachedisabled = 'true'">
                          %cacheIsOff%
                        </xsl:when>
                        <xsl:when test="contentobjectdata/datasources/@sessioncontext or contentobjectdata/datasources/@ticket or contentobjectdata/datasources/@requestcontext or contentobjectdata/datasources/@usercontext or contentobjectdata/datasources/@cookiecontext or contentobjectdata/datasources/@ticketcontext">
                          %cacheIsDisabled%
                        </xsl:when>
                        <xsl:when test="contentobjectdata/@cachedisabled != 'true'">
                          <xsl:choose>
                            <xsl:when test="contentobjectdata/@cachetype = 'specified'">
                              <xsl:value-of select="contentobjectdata/@mincachetime"/> %seconds%
                            </xsl:when>
                            <xsl:when test="contentobjectdata/@cachetype = 'forever'">
                              %optCacheForever%
                            </xsl:when>
                            <xsl:when test="contentobjectdata/@cachetype = 'default'">
                              %optDefaultCacheTime%
                            </xsl:when>
                            <xsl:otherwise>
                              %optDefaultCacheTime%
                            </xsl:otherwise>
                          </xsl:choose>
                        </xsl:when>
                      </xsl:choose>
                    </td>

                    <td align="center" class="browsetablecell" title="%msgClickToEdit%" onclick="{$function}">
                      <xsl:call-template name="formatdate">
                        <xsl:with-param name="date" select="timestamp" />
                      </xsl:call-template>
                      <xsl:text>&nbsp;</xsl:text>
                      <xsl:call-template name="formattime">
                        <xsl:with-param name="date" select="timestamp" />
                      </xsl:call-template>
                    </td>
                  </tr>
                </xsl:for-each>
              </table>
            </td>
          </tr>
        </table>

      </body>

    </html>

  </xsl:template>

  <!-- Standard substring replace template -->
  <xsl:template name="replacesubstring">
    <xsl:param name="stringsource"/>
    <xsl:param name="substringsource"/>
    <xsl:param name="substringdest"/>

    <xsl:choose>

      <xsl:when test="contains($stringsource,$substringsource)">
        <xsl:value-of select="concat(substring-before($stringsource,$substringsource),$substringdest)"/>
        <xsl:call-template name="replacesubstring">
          <xsl:with-param name="stringsource" select="substring-after($stringsource,$substringsource)"/>
          <xsl:with-param name="substringsource" select="$substringsource"/>
          <xsl:with-param name="substringdest" select="$substringdest"/>
        </xsl:call-template>
      </xsl:when>

      <xsl:otherwise>
        <xsl:value-of select="$stringsource"/>
      </xsl:otherwise>

    </xsl:choose>

  </xsl:template>

</xsl:stylesheet>