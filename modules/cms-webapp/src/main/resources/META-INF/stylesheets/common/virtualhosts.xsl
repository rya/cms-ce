<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>
    
    <xsl:template name="virtualhosts">

        <script type="text/javascript" language="JavaScript">
            function removeVirtualHost( table, objThis ) {
                count = itemcount(formAdmin[objThis.name]);
                if( count == 1 ) {
                    document.formAdmin["virtualhost"].value = "";
                    return;
                }

                var index = GetCurrentObjectIndex(objThis);
                document.all[table].deleteRow(index);
            }

            function addVirtualHost( table ) {
                addTableRow( table );

                var newIndex = itemcount( document.formAdmin["virtualhost"] );
                document.formAdmin["virtualhost"][newIndex-1].value = "";
            }

            function addTableRow( table ) {
                var destRow = document.all[table].insertRow();
                var sourceRow = document.all[table].rows[0];
            
                for( i=0; i&lt;sourceRow.cells.length;i++ ) {
                    var destCell = destRow.insertCell();
                    var sourceCell = sourceRow.cells[i];
                    destCell.insertAdjacentHTML( 'afterBegin', sourceCell.innerHTML );
                }
            }
            function itemcount(elName)
            {
                var lItems;

                if (elName.length!=null) {
                    lItems = elName.length;
                }
                else {
                    lItems = 1;
                }

                return lItems;
            }

            function GetCurrentObjectIndex(objThis)
            {
                var lNumRows = itemcount(document.formAdmin[objThis.name])

                if( lNumRows > 1 ) {
                    for( var i=0; i &lt; lNumRows; i++ ) {
                        if( document.formAdmin[objThis.name][i] == objThis ) {
                            return i;
                        }
                    }
                }
                else {
                    return 0;
                }
            }
        </script>

        <tr>
            <td>
                <table border="0" id="tblvirtualhosts" name="tblvirtualhosts">
                    <xsl:if test="not(/menus/menu/menudata/virtualhosts/virtualhost)">
                        <tr>
                            <td>
                                <input type="text" name="virtualhost" size="25" maxlength="50"/>
                            </td>
                            <td align="left">
                                <xsl:call-template name="button">
                                    <xsl:with-param name="name">removevirtualhost</xsl:with-param>
                                    <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                                    <xsl:with-param name="onclick">
                                        <xsl:text>removeVirtualHost('tblvirtualhosts',this)</xsl:text>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </td>
                        </tr>
                    </xsl:if>
                    <xsl:for-each select="/menus/menu/menudata/virtualhosts/virtualhost">
                        <tr>
                            <td>
                                <input type="text" name="virtualhost" size="25" maxlength="50">
                                    <xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
                                </input>
                            </td>
                            <td align="left">
                                <xsl:call-template name="button">
                                    <xsl:with-param name="name">removevirtualhost</xsl:with-param>
                                    <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                                    <xsl:with-param name="onclick">
                                        <xsl:text>removeVirtualHost('tblvirtualhosts',this)</xsl:text>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
            </td>
        </tr>
        <tr>
            <td>
                <xsl:call-template name="button">
                    <xsl:with-param name="type" select="'button'"/>
                    <xsl:with-param name="caption" select="'%cmdNewVirtualHost%'"/>
                    <xsl:with-param name="name" select="'addvirtualhost'"/>
                    <xsl:with-param name="onclick">
                        <xsl:text>javascript: addVirtualHost('tblvirtualhosts');</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>
            </td>
        </tr>
        
    </xsl:template>

</xsl:stylesheet>
