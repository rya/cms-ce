<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html"/>
    
    <xsl:param name="returnkey"/>
    <xsl:param name="returnview"/>
    
    <xsl:template match="/">
        <xsl:call-template name="userlist"/>
    </xsl:template>
    
    <xsl:template name="userlist">
        <html>
            
            <title>%headSelectUser%:</title>

            <script type="text/javascript" language="JavaScript">
                function returnValue( view, key )
                {
                    window.top.opener.document.formAdmin('<xsl:value-of select="$returnkey"/>').value = key;
                    window.top.opener.document.formAdmin('<xsl:value-of select="$returnview"/>').value = view;
                    window.close();
                }  
            </script>
        
            <link rel="stylesheet" type="text/css" href="css/admin.css"/>
            
            <body>

                <table width="100%" border="0" cellspacing="2" cellpadding="2">
                    <xsl:for-each select="/users/user">
                        <xsl:sort select="surname"/>
                        <tr>
                            <td>
                                <img src="images/icon_usersgroups.gif"/>
                            </td>
                            <td nowrap="nowrap">
                                <a href="javascript: void 0">
                                    <xsl:attribute name="onclick">returnValue('<xsl:value-of select="@fullname"/>','<xsl:value-of select="@key"/>')</xsl:attribute>
                                    <xsl:choose>
                                        <xsl:when test="@enterpriseadmin = 'yes'">
                                            <xsl:value-of select="@fullname"/> (%administrator%)
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:value-of select="@fullname"/> (<xsl:value-of select="block/uid"/>)
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </a>
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
                
            </body>
            
        </html>
    </xsl:template>

</xsl:stylesheet>