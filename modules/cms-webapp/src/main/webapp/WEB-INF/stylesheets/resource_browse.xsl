<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="html"/>

    <xsl:include href="common/javascriptPreload.xsl"/>
    <xsl:include href="common/operations_template.xsl"/>
    <xsl:include href="common/formatdate.xsl"/>
    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/tablecolumnheader.xsl"/>
    <xsl:include href="common/tablerowpainter.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/genericheader.xsl"/>
    <xsl:include href="common/escapequotes.xsl"/>
    <xsl:include href="common/browse_table_js.xsl"/>
    <xsl:include href="common/chooseicon.xsl"/>
    <xsl:include href="common/getsuffix.xsl"/>
    <xsl:include href="common/accesslevel_parameters.xsl"/>

    <xsl:param name="path"/>
    <xsl:param name="search"/>
    <xsl:param name="searchtext"/>
    <xsl:param name="fieldname" />
    <xsl:param name="mimetype" />
    <xsl:param name="extension" />
    <xsl:param name="sortby"/>
    <xsl:param name="sortby-direction"/>
    <xsl:param name="reload"/>
    <xsl:param name="move"/>

    <xsl:variable name="pageURL">
      <xsl:text>adminpage?page=800&amp;op=browse</xsl:text>
      <xsl:text>&amp;fieldname=</xsl:text><xsl:value-of select="$fieldname"/>
      <xsl:text>&amp;mimetype=</xsl:text><xsl:value-of select="$mimetype"/>
      <xsl:text>&amp;extension=</xsl:text><xsl:value-of select="$extension"/>
      <xsl:text>&amp;move=</xsl:text><xsl:value-of select="$move"/>
    </xsl:variable>

    <xsl:variable name="pageURLWithSearch">
        <xsl:value-of select="$pageURL"/>
        <xsl:text>&amp;path=</xsl:text><xsl:value-of select="$path"/>
        <xsl:if test="$search = 'true'">
        	<xsl:text>&amp;search=true&amp;searchtext=</xsl:text><xsl:value-of select="$searchtext"/>
        </xsl:if>
    </xsl:variable>

    <xsl:template match="/">
        <html>
            <head>
                <script type="text/javascript" src="javascript/admin.js">//</script>
                <link href="css/admin.css" rel="stylesheet" type="text/css"/>
                <script type="text/javascript" src="javascript/window.js"/>
                <script type="text/javascript">
                  <xsl:text>cms.window.attatchKeyEvent('close');</xsl:text>
                </script>

                <script type="text/javascript">
                  function clickRow(path) {
                    <xsl:choose>
                      <!-- pagetemplate or portlet/object user parameters --> 
                      <xsl:when test="$fieldname = 'parameter_value' or $fieldname = 'xslparam_value'">
                        <xsl:text>window.top.opener.addUserParamResource('</xsl:text>
                        <xsl:value-of select="$fieldname"/>
                        <xsl:text>', path);</xsl:text>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:text>window.top.opener.addResource('</xsl:text>
                        <xsl:value-of select="$fieldname"/>
                        <xsl:text>', path);</xsl:text>
                      </xsl:otherwise>
                    </xsl:choose>
                    window.top.close()
                  }
                  <xsl:if test="$move = true()">
                    var refactor = {
                      <xsl:text>url : 'adminpage?page=800&amp;op=menu&amp;fieldname=</xsl:text>
                      <xsl:if test="$fieldname !=''">
                        <xsl:value-of select="$fieldname"/>
                      </xsl:if>
                      <xsl:text>&amp;mimetype=</xsl:text>
                      <xsl:if test="$mimetype !=''">
                        <xsl:value-of select="$mimetype"/>
                      </xsl:if>
                      <xsl:text>&amp;extension=</xsl:text>
                      <xsl:if test="$extension !=''">
                        <xsl:value-of select="$extension"/>
                      </xsl:if>
                      <xsl:text>',</xsl:text>

                      openResourceWindow : function(key, subop) {
                        if (!key || !subop) return;
                        var url = this.url + '&amp;subop=' + subop + '&amp;sourceKey=' + key;
                        this.openWindow(url);
                      },

                      move : function(sourceKey, destinationKey, op) {

                        if (sourceKey == '' &amp;&amp; destinationKey == '' &amp;&amp; op == '')
                          return;

                        <xsl:text>var _url = 'adminpage?page=800&amp;op=' + op + '&amp;fieldname=</xsl:text>
                        <xsl:choose>
                          <xsl:when test="$fieldname !=''">
                            <xsl:value-of select="$fieldname"/>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:text> </xsl:text>
                          </xsl:otherwise>
                        </xsl:choose>
                        <xsl:text>&amp;mimetype=</xsl:text>
                        <xsl:if test="$mimetype !=''">
                          <xsl:value-of select="$mimetype"/>
                        </xsl:if>
                        <xsl:text>&amp;extension=</xsl:text>
                        <xsl:if test="$extension !=''">
                          <xsl:value-of select="$extension"/>
                        </xsl:if>
                        <xsl:text>&amp;path=</xsl:text>
                        <xsl:value-of select="$path"/>
                        <xsl:text>&amp;searchtext=</xsl:text>
                        <xsl:value-of select="$searchtext"/>
                        <xsl:text>&amp;sortby=</xsl:text>
                        <xsl:value-of select="$sortby"/>
                        <xsl:text>&amp;sortby-direction=</xsl:text>
                        <xsl:value-of select="$sortby-direction"/>
                        <xsl:text>&amp;reload=true</xsl:text>
                        <xsl:text>&amp;sourceKey=' + sourceKey + '</xsl:text>
                        <xsl:text>&amp;destinationKey=' + destinationKey + '</xsl:text>
                        <xsl:text>';</xsl:text>

                        <xsl:text>document.location.href = _url;</xsl:text>
                      },

                      openWindow : function(url) {
                        if (!url) return;
                        var width = 300;
                        var height = 600;
                        var x = (screen.width - width) / 2;
                        var y = (screen.height - height) / 2;
                        var newWindow = window.open(url, '_blank', 'toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=' + width + ',height=' + height + ',top=' + y + ',left=' + x + '');
                        newWindow.focus();
                      }
                    }
                  </xsl:if>
                </script>
            </head>

          <body>
            <xsl:if test="string($reload) = 'true'">
              <script type="text/javascript">
                  <xsl:text>window.top.frames['leftFrame'].refreshMenu();</xsl:text>
              </script>
            </xsl:if>

            <h1>
              <a href="{$pageURL}">%mnuResources%</a>
              <xsl:if test="$path != '/'">
                <xsl:text> / </xsl:text>
                <xsl:call-template name="resourcePath">
                  <xsl:with-param name="list" select="substring($path, 2)"/>
                  <xsl:with-param name="url" select="$pageURL"/>
                </xsl:call-template>
              </xsl:if>
            </h1>

            <table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td class="browse_title_buttonrow_seperator">
                  <img src="images/1x1.gif"/>
                </td>
              </tr>
              <tr>
                <td>
                  <form id="formSearch" name="formSearch" method="get" action="adminpage" style="margin-bottom:0;">
                    <input type="hidden" name="page" value="{$page}"/>
                    <input type="hidden" name="op" value="browse"/>
                    <input type="hidden" name="fieldname" value="{$fieldname}"/>
                    <input type="hidden" name="mimetype" value="{$mimetype}"/>
                    <input type="hidden" name="extension" value="{$extension}"/>
                    <input type="hidden" name="path" value="{$path}"/>
                    <input type="hidden" name="search" value="true"/>

                    <!-- Search field -->
                    <input type="text" name="searchtext" id="searchtext" size="12" style="height: 20px"
                           value="{$searchtext}"/>

                    <!-- Search button -->
                    <xsl:call-template name="button">
                      <xsl:with-param name="type" select="'submit'"/>
                      <xsl:with-param name="caption" select="'%cmdQuickSearch%'"/>
                      <xsl:with-param name="name" select="'search'"/>
                    </xsl:call-template>

                    <xsl:text>&nbsp;</xsl:text>

                    <!-- Move resource button -->
                    <xsl:if test="$developer = 'true' and $move = true()">

                      <xsl:if test="not($path = '/') and not($path = '/_public')">
                        <xsl:call-template name="button">
                          <xsl:with-param name="type" select="'button'"/>
                          <xsl:with-param name="caption" select="'%cmdMove%'"/>
                          <xsl:with-param name="name" select="'movefolderbtn'"/>
                          <xsl:with-param name="onclick">
                            <xsl:text>refactor.openResourceWindow('</xsl:text>
                            <xsl:value-of select="$path"/>
                            <xsl:text>', 'moveFolder');</xsl:text>
                          </xsl:with-param>
                        </xsl:call-template>
                      </xsl:if>
                    </xsl:if>
                  </form>
                </td>
              </tr>
              <tr>
                <td class="browse_buttonrow_datarows_seperator">
                  <img src="images/1x1.gif"/>
                </td>
              </tr>
              <xsl:call-template name="browselist"/>
            </table>

            <script type="text/javascript">
              var searchField = document.getElementById('searchtext');
              if (searchField) {
                searchField.focus();
              }
            </script>
          </body>
        </html>
    </xsl:template>

    <xsl:template name="browselist">
        <tr>
            <td>
                <table width="100%" cellspacing="0" cellpadding="0" class="browsetable">
                  <tr>
                    <xsl:call-template name="tablecolumnheader">
                      <xsl:with-param name="caption" select="'%fldName%'"/>
                      <xsl:with-param name="pageURL" select="$pageURLWithSearch"/>
                      <xsl:with-param name="current-sortby" select="$sortby"/>
                      <xsl:with-param name="current-sortby-direction" select="$sortby-direction"/>
                      <xsl:with-param name="sortby" select="'@name'"/>
                    </xsl:call-template>

                    <xsl:call-template name="tablecolumnheader">
                      <xsl:with-param name="caption" select="'%fldLastModified%'"/>
                      <xsl:with-param name="pageURL" select="$pageURLWithSearch"/>
                      <xsl:with-param name="width" select="'120'"/>
                      <xsl:with-param name="align" select="'center'"/>
                      <xsl:with-param name="current-sortby" select="$sortby"/>
                      <xsl:with-param name="current-sortby-direction" select="$sortby-direction"/>
                      <xsl:with-param name="sortby" select="'@lastModified'"/>
                    </xsl:call-template>

                    <xsl:call-template name="tablecolumnheader">
                      <xsl:with-param name="caption" select="'%fldUsageCount%'"/>
                      <xsl:with-param name="pageURL" select="$pageURLWithSearch"/>
                      <xsl:with-param name="width" select="'70'"/>
                      <xsl:with-param name="align" select="'center'"/>
                      <xsl:with-param name="current-sortby" select="$sortby"/>
                      <xsl:with-param name="current-sortby-direction" select="$sortby-direction"/>
                      <xsl:with-param name="sortby" select="'@usageCount'"/>
                    </xsl:call-template>

                    <xsl:call-template name="tablecolumnheader">
                      <xsl:with-param name="caption" select="'%fldSize%'"/>
                      <xsl:with-param name="pageURL" select="$pageURLWithSearch"/>
                      <xsl:with-param name="width" select="'50'"/>
                      <xsl:with-param name="align" select="'right'"/>
                      <xsl:with-param name="current-sortby" select="$sortby"/>
                      <xsl:with-param name="current-sortby-direction" select="$sortby-direction"/>
                      <xsl:with-param name="sortby" select="'@size'"/>
                    </xsl:call-template>

                    <xsl:call-template name="tablecolumnheader">
                      <xsl:with-param name="caption" select="'%fldMimeType%'"/>
                      <xsl:with-param name="pageURL" select="$pageURLWithSearch"/>
                      <xsl:with-param name="width" select="'80'"/>
                      <xsl:with-param name="align" select="'center'"/>
                      <xsl:with-param name="current-sortby" select="$sortby"/>
                      <xsl:with-param name="current-sortby-direction" select="$sortby-direction"/>
                      <xsl:with-param name="sortby" select="'@mimeType'"/>
                    </xsl:call-template>

                    <!-- operation column -->
                    <xsl:if test="$developer = 'true' and $move = true()">
                      <xsl:call-template name="tablecolumnheader">
                        <xsl:with-param name="width" select="'40'"/>
                        <xsl:with-param name="caption" select="''"/>
                        <xsl:with-param name="sortable" select="'false'"/>
                      </xsl:call-template>
                    </xsl:if>
                  </tr>

                  <xsl:variable name="sortby-data-type">
                    <xsl:choose>
                      <xsl:when test="$sortby = '@size' or $sortby = '@usageCount'">number</xsl:when>
                      <xsl:otherwise>text</xsl:otherwise>
                    </xsl:choose>
                  </xsl:variable>


                  <xsl:variable name="resource-count" select="count(/resources//resource)"/>

                  <xsl:for-each select="/resources//resource">
                    <xsl:sort data-type="{$sortby-data-type}" order="{$sortby-direction}"
                              select="*[name() = $sortby] | @*[concat('@',name()) = $sortby]"/>
                    <tr>
                      <xsl:call-template name="tablerowpainter"/>
                      <xsl:apply-templates select="." mode="tablerow">
                        <xsl:with-param name="is-last-resource" select="$resource-count = position()"/>
                      </xsl:apply-templates>
                    </tr>
                  </xsl:for-each>
                </table>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="resource" mode="tablerow">
      <xsl:param name="is-last-resource"/>

    	<xsl:variable name="filtered">
    		<xsl:choose>
    			<xsl:when test="ends-with(@name, concat('.','xslt'))">false</xsl:when>
    			<xsl:when test="not($extension = '') and not(ends-with(@name, concat('.',$extension)))">true</xsl:when>
          <xsl:when test="not($mimetype = '') and not($mimetype = @mimeType)">true</xsl:when>
    			<xsl:otherwise>false</xsl:otherwise>
    		</xsl:choose>
    	</xsl:variable>

    	<xsl:variable name="toolTip">
    		<xsl:choose>
    			<xsl:when test="$filtered = 'true'">%resourceCannotBeSelected%</xsl:when>
    			<xsl:otherwise>
            <xsl:if test="$move = false() and $search != 'true'">
              <xsl:text>%cmdAdd%&nbsp;</xsl:text>
            </xsl:if>
            <xsl:value-of select="@fullPath"/>
          </xsl:otherwise>
    		</xsl:choose>
    	</xsl:variable>

    	<xsl:variable name="action">
    		<xsl:choose>
    			<xsl:when test="$filtered = 'true' or $move = true() or $search = 'true' and $fieldname = ''">
            <xsl:text>void(0);</xsl:text>
          </xsl:when>
    			<xsl:otherwise>
    				<xsl:text>clickRow('</xsl:text>
    				<xsl:value-of select="@fullPath"/>
    				<xsl:text>');</xsl:text>
    			</xsl:otherwise>
    		</xsl:choose>
    	</xsl:variable>

    	<xsl:variable name="class">
    		<xsl:text>browsetablecell</xsl:text>
    		<xsl:if test="$filtered = 'true'">
    			<xsl:text>disabled</xsl:text>
    		</xsl:if>
        <xsl:if test="$filtered = 'true' or $move = true() or $search = 'true' and $fieldname = ''">
    			<xsl:text> no-action</xsl:text>
    		</xsl:if>
        <xsl:if test="position() = last() and $is-last-resource">
          <xsl:text> row-last</xsl:text>
        </xsl:if>
      </xsl:variable>

      <td title="{$toolTip}" class="{$class}" onclick="{$action}">
        <table border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td>
              <xsl:call-template name="chooseicon">
                <xsl:with-param name="filename" select="@name"/>
              </xsl:call-template>
            </td>
            <td>
              <xsl:choose>
                <xsl:when test="$search = 'true'">
                  <xsl:value-of select="@fullPath"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="@name"/>
                </xsl:otherwise>
              </xsl:choose>
            </td>
          </tr>
        </table>
      </td>
    	<td align="center" title="{$toolTip}" class="{$class}" onclick="{$action}">
    		<xsl:value-of select="@lastModified"/>
    	</td>
    	<td align="center" title="{$toolTip}" class="{$class}" onclick="javascript:OpenResourceInUseByPopup('{@fullPath}');">
    		<xsl:choose>
          <xsl:when test="@usageCount = 0">
            <xsl:value-of select="@usageCount"/>
          </xsl:when>
          <xsl:otherwise>
            <a href="javascript:;" class="resource-in-use-link">
              <xsl:value-of select="@usageCount"/>
            </a>
          </xsl:otherwise>
    		</xsl:choose>
    	</td>
    	<td align="right" title="{$toolTip}" class="{$class}" onmouseup="{$action}">
    		<xsl:value-of select="@size"/>
    	</td>
    	<td align="center" title="{$toolTip}" class="{$class}" onclick="{$action}">
    		<xsl:value-of select="@mimeType"/>
    	</td>
      <!-- operation column -->
      <xsl:if test="$developer = 'true' and $move = true()">
        <td align="center" class="{$class}">
           <a href="javascript:;" onclick="javascript:refactor.openResourceWindow('{@fullPath}', 'moveFile');">
             <img src="images/icon_resource_move.gif" alt="%cmdMove%" title="%cmdMove%" style="border:0"/>
           </a>
        </td>
      </xsl:if>
    </xsl:template>
  
    <xsl:template name="resourcePath">
	    <xsl:param name="list" />
	    <xsl:param name="prevPath" select="''" />
	    <xsl:param name="url"/>
	    
	    <xsl:variable name="newlist" select="normalize-space($list)" />
	    <xsl:variable name="first" select="substring-before($newlist, '/')" />
	    <xsl:variable name="remaining" select="substring-after($newlist, '/')" />
	    
	    <xsl:choose>
		    <xsl:when test="$remaining">
		    	<xsl:variable name="_path" select="concat(concat($prevPath, '/'), $first)"/>
		    	<a href="{$url}&amp;path={$_path}">
		    		<xsl:value-of select="$first" />
		    	</a>
		    	<xsl:text> / </xsl:text>
		  	    <xsl:call-template name="resourcePath">
		  		    <xsl:with-param name="list" select="$remaining" />
		  		    <xsl:with-param name="prevPath" select="$_path" />
		  		    <xsl:with-param name="url" select="$url" />
		  	    </xsl:call-template>
		    </xsl:when>
			<xsl:otherwise>
				<xsl:variable name="_path" select="concat(concat($prevPath, '/'), $list)"/>
				<a href="{$url}&amp;path={$_path}">
		    		<xsl:value-of select="$list" />
		    	</a>
			</xsl:otherwise>
		</xsl:choose>
    </xsl:template>

</xsl:stylesheet>
