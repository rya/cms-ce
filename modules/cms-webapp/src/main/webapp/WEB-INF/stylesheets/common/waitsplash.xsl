<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <xsl:template name="waitsplash">
    <script type="text/javascript">
		function waitsplash()
	  {
      // ***********************************************************************************************************************************
      // *** Variables
      // ***********************************************************************************************************************************
      var waitSplashImg, html, body, table, tBody, tr, td;

      body = document.getElementsByTagName('html')[0];
      html = document.getElementsByTagName('body')[0];

      // html and body element height needs to be ~100%
      body.style.height = '100%';
      html.style.height = '100%';

      waitSplashImg = new Image();
      waitSplashImg.src = 'images/waitsplash.gif';

      body = document.getElementsByTagName( 'body' )[0];

      table = document.createElement( 'table' );
      table.id = 'cmsWaitSplash';


      table.style.width = (document.all) ? '102%' : '100%';
      table.style.height = (document.all) ? '110%' : '100%';

      table.style.position = 'absolute';
      table.style.zIndex = 100000000;
      table.style.top = 0;
      table.style.left = 0;

      tBody = document.createElement( 'tbody' );

      tr = document.createElement( 'tr' );

      td = document.createElement( 'td' );
      td.align = 'center';
      td.height = '100%';
      td.style.backgroundColor = '#ffffff';
      td.innerHTML = '&lt;div style="margin: 5px 0pt; width: 64px; height: 64px; background-image: url(images/waitsplash.gif);"&gt;&lt;/div&gt;';
      td.innerHTML += '%sysPleaseWait%';
      
      table.appendChild( tBody );
      tBody.appendChild( tr );
      tr.appendChild( td );

      body.appendChild( table );
    }
    // -------------------------------------------------------------------------------------------------------------------------------------

    function removeWaitsplash()
    {
      // ***********************************************************************************************************************************
      // *** Variables
      // ***********************************************************************************************************************************
      var html, body, waitSplashElement;

      body = document.getElementsByTagName( 'body' )[0];
      html = document.getElementsByTagName( 'html' )[0];
      waitSplashElement = document.getElementById( 'cmsWaitSplash' );

      if ( waitSplashElement )
      {
        body.removeChild( waitSplashElement );
        html.style.height = '';
        body.style.height = '';
      }
    }
    </script>

    <!-- Cache the image -->
    <div style="display:none">
      <img src="images/waitsplash.gif" alt="." width="64" height="64"/>
    </div>
  </xsl:template>

</xsl:stylesheet>
