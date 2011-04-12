<#assign functionName = "showError${uniqueId}">
<div id="error:${uniqueId}">
    <script type="text/javascript">
        function ${functionName}()
        {
            var data = "${errorPage}";
            var errorPage = window.open( "", "", "location=0,status=0,scrollbars=1" );
            var b64_map = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
            var byte1, byte2, byte3;
            var ch1, ch2, ch3, ch4;

            for ( var i = 0; i < data.length; i += 4 )
            {
                ch1 = b64_map.indexOf( data.charAt( i ) );
                ch2 = b64_map.indexOf( data.charAt( i + 1 ) );
                ch3 = b64_map.indexOf( data.charAt( i + 2 ) );
                ch4 = b64_map.indexOf( data.charAt( i + 3 ) );

                byte1 = (ch1 << 2) | (ch2 >> 4);
                byte2 = ((ch2 & 15) << 4) | (ch3 >> 2);
                byte3 = ((ch3 & 3) << 6) | ch4;

                errorPage.document.write( String.fromCharCode( byte1 ) );
                if ( ch3 != 64 ) errorPage.document.write( String.fromCharCode( byte2 ) );
                if ( ch4 != 64 ) errorPage.document.write( String.fromCharCode( byte3 ) );
            }

            errorPage.document.close();
        }
    </script>
    <div style="font-size: 9pt; border: 2px solid #000000; padding: 4px; background-color: #FFC0C0; cursor:pointer;"
         onclick="${functionName}()">
    ${title}
    </div>
</div>
