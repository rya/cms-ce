<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
    ]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:template name="content_usedby">

    <xsl:param name="contentKey"/>

    <xsl:if test="$create = 0">
      <div class="tab-page" id="tab-page-usedby">
        <span class="tab">%blockUsedBy%</span>
        <script type="text/javascript" language="JavaScript">

          tabPane1.addTabPage( document.getElementById( "tab-page-usedby" ) );
          tabPane1.enablePageClickEvent();

          function handle_tabpane_onclick( pageIndex )
          {
            getUsedByAsHtml(<xsl:value-of select="$contentKey"/>);
          }

          function handleResponse_getUsedBy(relatedContent)
          {
            for( var i = 0; i &lt; relatedContent.length; i++ )
            {
              alert( relatedContent[i].key + ' ' +  relatedContent[i].title + ' ' + relatedContent[i].contentType + ' ' + relatedContent[i].path);
            }
          }

          function getUsedByAsHtml( contentKey )
          {
            document.getElementById('usedBy').innerHTML = "%headPleaseWait%";
            AjaxService.getUsedByAsHtml(contentKey, {callback:handleResponse_getUsedByAsHtml});
          }

          function handleResponse_getUsedByAsHtml( content )
          {
            document.getElementById('usedBy').innerHTML = content;
          }

        </script>
        <fieldset>
          <legend>&nbsp;%blockContents%&nbsp;</legend>

          <xsl:if test="/contents/relatedcontents/content[@current = 'false' and @versionkey = /contents/content/relatedcontentkeys/relatedcontentkey[@level = -1]/@versionkey]">
            <xsl:text>%msgContentInUseByNonCurrentVersion%</xsl:text>
            <br/><br/>
          </xsl:if>

          <span id="usedBy">
            &nbsp;
          </span>

        </fieldset>
      </div>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>