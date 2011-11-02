<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
	<!ENTITY Oslash "&#216;">
	<!ENTITY oslash "&#248;">
	<!ENTITY Aring  "&#197;">
	<!ENTITY aring  "&#229;">
	<!ENTITY AElig  "&#198;">
	<!ENTITY aelig  "&#230;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="html"/>

        <xsl:param name="width"/>
        <xsl:param name="height"/>
        <xsl:param name="binarykey"/>

	<xsl:template match="/">
		<xsl:call-template name="imagelist"/>
	</xsl:template>

	<xsl:template name="imagelist">
	<html>
          <script type="text/javascript" language="JavaScript">
            function insertImage()
            {
              var sel = window.opener.tbContentElement.DOM.selection;
              var range;
              var bkey = <xsl:value-of select="$binarykey"/>;
              var width = <xsl:value-of select="$width"/>;
              var height = <xsl:value-of select="$height"/>;
              var f = document.imageForm;
              var vmargin = f.vmargin.value;
              var hmargin = f.hmargin.value;
              var border = f.border.value;
              var textflow = f.textflow.value;
              var alttext = f.alttext.value;

              if ( "Text" == sel.type || "None" == sel.type ){
                range = sel.createRange();
                range.collapse();
                range.pasteHTML('<img src="binary?id='+bkey+'" width="'+width+'" height="'+height+'" vspace="'+vmargin+'" hspace="'+hmargin+'" border="'+border+'" align="'+textflow+'" alt="'+alttext+'"/>');
              }

              window.close();
            }
          </script>

          <link rel="stylesheet" type="text/css" href="css/admin.css"/>
          <body>

            <table width="100%" border="0">
              <form name="imageForm">
                <tr bgcolor="#FOFOFO">
                  <td colspan="4"><b>Egenskaper for bilde</b></td>
                </tr>
                <tr>
                  <td>Alt. tekst:</td>
                  <td colspan="3">
                    <input type="text" name="alttext" size="40"/>
                  </td>
                </tr>
                <tr>
                  <td>Vertikal marg:</td>
                  <td>
                    <input type="text" name="vmargin" size="4" value="0"/>
                  </td>
                  <td>Horisontal marg:</td>
                  <td>
                    <input type="text" name="hmargin" size="4" value="0"/>
                  </td>
                </tr>
                <tr>
                  <td>Ramme</td>
                  <td>
                    <input type="text" name="border" size="4" value="0"/>
                  </td>
                  <td>Justering:</td>
                  <td>
                    <select name="textflow">
                      <option value="">Ingen</option>
                      <option value="left">Venstre</option>
                      <option value="right">H&oslash;yre</option>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td colspan="4" align="right"><input type="button" class="button" value="Sett inn" onclick="javascript:insertImage()"/></td>
                </tr>
              </form>
            </table>
          </body>
        </html>
      </xsl:template>

  </xsl:stylesheet>
