<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>

  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:include href="common/escapequotes.xsl"/>
  <xsl:include href="handlerconfigs/default.xsl"/>
  <xsl:include href="tree/displaytree.xsl"/>
  <xsl:include href="common/button.xsl"/>

  <xsl:param name="returnkey"/>
  <xsl:param name="returnview"/>
  <xsl:param name="returnrow" select="''"/>
  <xsl:param name="callback" select="''"/>

  <xsl:param name="filter" select="''"/>

  <xsl:template match="/">

	<title>%fldSelectPage%:</title>

        <script type="text/javascript" src="javascript/window.js"/>
        <script type="text/javascript">
         cms.window.attatchKeyEvent('close');
        </script>


        <script type="text/javascript" language="JavaScript">
        
        var branchOpen = new Array();
        var cookiename =	<xsl:text>'adminmenu</xsl:text>
                    <xsl:value-of select="$selecteddomainkey"/>
                    <xsl:text>';</xsl:text>
                    //var pos = allcookies.indexOf(cookiename + "=");
        
        
      function returnValue(key, returnkey, returnview) {
      	if (key == undefined)
      		return;

        var view = '';
      	if (key != -1) {
      		var tr = document.getElementById("menuitemText"+key);
      		view = tr.firstChild.nodeValue;
      	}
      	
      	<xsl:choose>
      		<xsl:when test="$returnkey">
		      	returnkey = '<xsl:value-of select="$returnkey"/>';
		    </xsl:when>
      		<xsl:otherwise>
      			returnkey = null;
      		</xsl:otherwise>
      	</xsl:choose>
      	<xsl:choose>
      		<xsl:when test="$returnview">
		      	returnview = '<xsl:value-of select="$returnview"/>';
		    </xsl:when>
      		<xsl:otherwise>
      			returnview = null;
      		</xsl:otherwise>
      	</xsl:choose>
      	
            <xsl:choose>
                <xsl:when test="$returnrow=''">
                	<xsl:choose>
                		<xsl:when test="$callback = 'true'">
                            window.top.opener.callback(key, view, returnkey, returnview);
                        </xsl:when>
                        <xsl:otherwise>
		                    window.top.opener.document.getElementById(returnkey).value = key;
		                    window.top.opener.document.getElementById(returnview).value = view;
		                </xsl:otherwise>
		            </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                    if( window.top.opener.document.getElementById(returnkey).length != null )
                    {
                    <xsl:choose>
                        <xsl:when test="$callback = 'true'">
                            window.top.opener.callback(key, view, <xsl:value-of select="$returnrow"/>);
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>window.top.opener.document.getElementById(returnkey)(</xsl:text>
                            <xsl:value-of select="$returnrow"/>
                            <xsl:text>).value = key;</xsl:text>
                            if (returnview.length &gt; 0) {
                            <xsl:text>window.top.opener.document.getElementById(returnview)(</xsl:text>
                            <xsl:value-of select="$returnrow"/>
                            <xsl:text>).value = view;</xsl:text>
                            }
                        </xsl:otherwise>
                    </xsl:choose>
                    
          }
          else
          {
                    <xsl:choose>
                        <xsl:when test="$callback = 'true'">
                            window.top.opener.callback(key, view);
                        </xsl:when>            
                        <xsl:otherwise>
                            window.top.opener.document.getElementById(returnkey).value = key;

                            if (returnview.length &gt; 0)
							{

                          window.top.opener.document.getElementById(returnview).value = view;
							}
                        </xsl:otherwise>
                    </xsl:choose>
          }
                </xsl:otherwise>
            </xsl:choose>
          window.close();
      }
        </script>

		<script src="javascript/menu.js"/>
        <link rel="stylesheet" type="text/css" href="css/menu.css"/>
        <link rel="stylesheet" type="text/css" href="css/admin.css"/>
        <link rel="stylesheet" type="text/css" href="javascript/xtree.css"/>


		<body>
			<xsl:apply-templates select="*" mode="displaytree">
				<xsl:with-param name="onclick" select="'returnValue'"/>
				<xsl:with-param name="topnode" select="true()"/>
				<xsl:with-param name="linkshaded" select="false()"/>
			</xsl:apply-templates>
		</body>
	</xsl:template>


  <xsl:template match="menutop" mode="shadeicon" priority="9.0">
	<xsl:text>true</xsl:text>
  </xsl:template>
  
  <xsl:template match="menuitem" mode="shadeicon" priority="9.0">
		<xsl:choose>
			<xsl:when test="$filter = 'anonymous' and not(@anonread = 'true')">
				<xsl:text>true</xsl:text>
			</xsl:when>
			<xsl:when test="@useradd = 'true' or @useradministrate = 'true' or @usercreate = 'true' or @userupdate = 'true' or @userpublish = 'true'">
				<xsl:text>false</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>true</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

  
</xsl:stylesheet>
