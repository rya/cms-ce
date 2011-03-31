<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="html"/>

	<xsl:param name="page"/>

	<xsl:template match="/">
		<xsl:call-template name="pagebrowse"/>
	</xsl:template>

	<xsl:template name="pagebrowse">

		<html>

		<head>

			<script type="text/javascript" language="JavaScript">
				function MM_swapImgRestore() { //v3.0
				  var i,x,a=document.MM_sr; for(i=0;a&amp;&amp;i&lt;a.length&amp;&amp;(x=a[i])&amp;&amp;x.oSrc;i++) x.src=x.oSrc;
				}

				function MM_preloadImages() { //v3.0
				  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
					var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i&lt;a.length; i++)
					if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
				}

				function MM_findObj(n, d) { //v3.0
				  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&amp;&amp;parent.frames.length) {
					d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
				  if(!(x=d[n])&amp;&amp;d.all) x=d.all[n]; for (i=0;!x&amp;&amp;i&lt;d.forms.length;i++) x=d.forms[i][n];
				  for(i=0;!x&amp;&amp;d.layers&amp;&amp;i&lt;d.layers.length;i++) x=MM_findObj(n,d.layers[i].document); return x;
				}

				function MM_swapImage() { //v3.0
				  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i&lt;(a.length-2);i+=3)
				   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
				}
			</script>

			<link href="css/admin.css" rel="stylesheet" type="text/css"/>

		</head>

		<body onload="MM_preloadImages('images/icon_edit.gif','images/icon_delete.gif')">

		<h1>%headDocumentPages%</h1>

		<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>
				<a>
					<xsl:attribute name="href">
						<xsl:text>adminpage?page=</xsl:text>
						<xsl:value-of select="$page"/>
						<xsl:text>&amp;op=form</xsl:text>
					</xsl:attribute>
					<xsl:text>%cmdNew%</xsl:text>
				</a>
				<p/>
			</td>
		</tr>

		<tr bgcolor="#CCCCCC">
			<td>
			  <p><img src="images/1x1.gif" width="1" height="1"/></p>
			</td>
		</tr>
		<tr>
			<td>
			  <br/>
			</td>
		</tr>

		<tr>
			<td>
				<table width="100%" border="0" cellspacing="0" cellpadding="4">
				<tr>
					<td><b>%fldModified%</b></td>
					<td><b>%fldName%</b></td>
					<td align="right"><b></b></td>
				</tr>
				<xsl:for-each select="/pages/page">
				<xsl:sort order="ascending" select="name"/>
				<tr>
				<xsl:if test="position() mod 2">
					<xsl:attribute name="bgcolor">#F7F7F7</xsl:attribute>
				</xsl:if>
					<td>
						<xsl:call-template name="formatdatetime">
							<xsl:with-param name="date" select="timestamp"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:value-of select="name"/>
					</td>

					<td align="right">
						<xsl:call-template name="operations">
							<xsl:with-param name="page" select="$page"/>
							<xsl:with-param name="key" select="@key"/>
						</xsl:call-template>
					</td>
				</tr>
				</xsl:for-each>
				</table>
			</td>
		</tr>
		<tr>
			<td>
			  <br/>
			</td>
		</tr>
		<tr bgcolor="#CCCCCC">
			<td>
			  <p><img src="images/1x1.gif" width="1" height="1"/></p>
			</td>
		</tr>
		</table>

		</body>
		</html>

	</xsl:template>

</xsl:stylesheet>