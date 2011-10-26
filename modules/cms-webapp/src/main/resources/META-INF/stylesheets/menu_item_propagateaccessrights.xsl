<?xml version="1.0" encoding="utf-8"?>

<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
 ]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html"/>

    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/genericheader.xsl"/>
    <xsl:include href="common/categoryheader.xsl"/>
    <xsl:include href="common/accessrights.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/tablecolumnheader.xsl"/>
    <xsl:include href="common/tablerowpainter.xsl"/>

    <xsl:param name="menuitemkey"/>
    <xsl:param name="menuitemname"/>
    <xsl:param name="insertbelow"/>
    <xsl:param name="parentmi"/>

    <xsl:template match="/">
        <html>
            <link type="text/css" rel="stylesheet" href="css/admin.css"/>
            <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>
            <script type="text/javascript" language="JavaScript" src="javascript/admin.js"/>
            <script type="text/javascript" language="JavaScript" src="javascript/accessrights.js"/>
            <script type="text/javascript" language="JavaScript" src="javascript/tabpane.js"/>

            <script type="text/javascript" language="JavaScript">
                function changeSelection( key )
                {
                    var chk = document.getElementById( "chkPropagate[key="+key+"]" );
                    chk.checked = !chk.checked;
                }

                function submitForm( subop )
                {
					var f = document.forms["formAdmin"];
                    document.getElementById("subop").value = subop;
                    f.submit();
                }
            </script>

            <body>

                <form method="POST" name="formAdmin">
                    <xsl:attribute name="action">
                        <xsl:text>adminpage</xsl:text>
                        <xsl:text>?page=</xsl:text><xsl:value-of select="$page"/>
                        <xsl:text>&amp;op=propagateaccessrights</xsl:text>
                        <xsl:text>&amp;selectedunitkey=</xsl:text><xsl:value-of select="$selectedunitkey"/>
                        <xsl:text>&amp;key=</xsl:text><xsl:value-of select="$menuitemkey"/>
                        <xsl:text>&amp;menuitemkey=</xsl:text><xsl:value-of select="$menuitemkey"/>
                        <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
                        <xsl:text>&amp;insertbelow=</xsl:text><xsl:value-of select="$insertbelow"/>
                    </xsl:attribute>
                    <input type="hidden" id="subop" name="subop" value="dontpropagate"/>
                    <input type="hidden" id="reload" name="reload" value="true"/>

                    <xsl:for-each select="/data/accessrights/accessright">
                        <input type="hidden" id="accessright[key={@groupkey}]" name="accessright[key={@groupkey}]" value="[read={@read='true'};update={@update='true'};delete={@delete='true'};create={@create='true'};publish={@publish='true'};add={@add='true'};administrate={@administrate='true'}]"/>
                    </xsl:for-each>

                    <h1>
                        <xsl:call-template name="genericheader"/>
                        <a href="adminpage?op=browse&amp;page={$page}&amp;selectedunitkey={$selectedunitkey}&amp;menukey={$menukey}">%headPageBuilder%</a>
                        <xsl:if test="number($insertbelow) &gt; -1">
                            <xsl:call-template name="generateheader">
                                <xsl:with-param name="menuitem" select="//menuitem[@key = $insertbelow]"/>
                            </xsl:call-template>
                        </xsl:if>
                    </h1>
                    <h2>
                        <xsl:text>%headEdit%: </xsl:text>
                        <span id="titlename">
                            <xsl:value-of select="$menuitemname"/>
                        </span>
                    </h2>

                    <table border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td class="form_title_form_seperator"><img src="images/1x1.gif"/></td>
                        </tr>
                    </table>

                    <div class="tab-pane" id="tab-pane-1">
                        <script type="text/javascript" language="JavaScript">
                            var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                        </script>

                        <div class="tab-page" id="tab-page-1">
                            <span class="tab">%blockPropagateSecurity%</span>

                            <script type="text/javascript" language="JavaScript">
                                tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                            </script>

                            <fieldset>
                                <legend>&nbsp;%blockPages%&nbsp;</legend>
                                <br/>
                                <table width="100%" cellspacing="0" cellpadding="0" class="browsetable">
                                    <tr>
                                        <td width="25" align="center" valign="top" class="browsetablecell">
                                            <input type="hidden" name="chkPropagate[key={$menuitemkey}]" value="true"/>
                                        </td>
                                        <td class="browsetablecell">
                                            <xsl:attribute name="style">
                                                <xsl:text>cursor: default;</xsl:text>
                                            </xsl:attribute>
                                            <span>
                                                <xsl:value-of select="/data/menus/menu//menuitem[@key = $menuitemkey]/@name"/>
                                            </span>
                                        </td>
                                    </tr>
                                    <xsl:for-each select="/data/menus/menu//menuitem[@key = $menuitemkey]//menuitem">
                                        <xsl:variable name="userHaveRightToAdministrate" select="@useradministrate = 'true' or not(@useradministrate)"/>
                                        <tr>
                                            <xsl:if test="$userHaveRightToAdministrate">
                                                <xsl:attribute name="onmouseover">javascript:this.style.backgroundColor="#FFFFFF"</xsl:attribute>
                                                <xsl:attribute name="onmouseout">javascript:this.style.backgroundColor="#EEEEEE"</xsl:attribute>
                                            </xsl:if>

                                            <td width="25" align="center" valign="top" class="browsetablecell">
                                                <xsl:attribute name="style">
                                                    <xsl:text>cursor: default;</xsl:text>
                                                </xsl:attribute>
                                                <xsl:if test="$userHaveRightToAdministrate">
                                                    <input type="checkbox" name="chkPropagate[key={@key}]" id="chkPropagate[key={@key}]" checked="true"/>
                                                </xsl:if>
                                            </td>
                                            <td class="browsetablecell">
                                                <xsl:choose>
                                                    <xsl:when test="$userHaveRightToAdministrate">
                                                        <xsl:attribute name="onclick">
                                                            <xsl:text>changeSelection(</xsl:text><xsl:value-of select="@key"/><xsl:text>)</xsl:text>
                                                        </xsl:attribute>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:attribute name="style">
                                                            <xsl:text>cursor: default;</xsl:text>
                                                        </xsl:attribute>
                                                    </xsl:otherwise>
                                                </xsl:choose>

                                                <span>
                                                    <xsl:if test="not($userHaveRightToAdministrate = 'true')">
                                                        <xsl:attribute name="style">
                                                            <xsl:text>color: #888888;</xsl:text>
                                                        </xsl:attribute>
                                                    </xsl:if>
                                                    <xsl:text>... </xsl:text>
                                                    <xsl:call-template name="displaymenupath_">
                                                        <xsl:with-param name="xpath" select="."/>
                                                        <xsl:with-param name="supermenuitemkey" select="$menuitemkey"/>
                                                        <xsl:with-param name="leaf" select="true()"/>
                                                    </xsl:call-template>
                                                </span>
                                            </td>
                                        </tr>
                                    </xsl:for-each>
                                </table>
                            </fieldset>
                            <fieldset>
                                <legend>&nbsp;%blockOptions%&nbsp;</legend>
                                <input type="checkbox" name="applyonlychanges" checked="true"/>&nbsp;&nbsp;%applyOnlyChanges%
                            </fieldset>
                            <br/>

                            <xsl:call-template name="button">
                                <xsl:with-param name="type" select="'button'"/>
                                <xsl:with-param name="caption" select="'%cmdPropagate%'"/>
                                <xsl:with-param name="onclick">
                                    <xsl:text>javascript:submitForm('propagate');</xsl:text>
                                </xsl:with-param>
                            </xsl:call-template>

                            <xsl:text>&nbsp;</xsl:text>

                            <xsl:call-template name="button">
                                <xsl:with-param name="type" select="'button'"/>
                                <xsl:with-param name="caption" select="'%cmdDontPropagate%'"/>
                                <xsl:with-param name="onclick">
                                    <xsl:text>javascript:submitForm('dontpropagate');</xsl:text>
                                </xsl:with-param>
                            </xsl:call-template>

                        </div>
                        <div class="tab-page" id="tab-page-2">
                            <span class="tab">%blockViewChanges%</span>

                            <script type="text/javascript" language="JavaScript">
                                tabPane1.addTabPage( document.getElementById( "tab-page-2" ) );
                            </script>

                            <xsl:if test="/data/changedaccessrights/accessright">
                                <fieldset>
                                    <legend>&nbsp;%changes%&nbsp;</legend>
                                    <table border="0" cellspacing="0" cellpadding="3" width="100%">
                                        <tr>
                                            <td></td>
                                            <td></td>
                                            <td align="center" width="120">
                                                %columnRead%
                                            </td>
                                            <td align="center" width="120">
                                                <span title="%tooltipAddContent%">%columnAddContent%</span>
                                            </td>
                                            <td align="center" width="120">
                                                <span title="%tooltipPublishContentInMenu%">%columnPublishContentInMenu%</span>
                                            </td>
                                            <td align="center" width="120">
                                                %columnCreate%
                                            </td>
                                            <td align="center" width="120">
                                                %columnUpdate%
                                            </td>
                                            <td align="center" width="120">
                                                %columnDelete%
                                            </td>
                                            <td align="center" width="120">
                                                %columnAdministrate%
                                            </td>
                                            <td></td>
                                        </tr>
                                        <xsl:for-each select="/data/changedaccessrights/accessright">
                                            <tr>
                                                <xsl:attribute name="bgcolor">
                                                    <xsl:choose>
                                                        <xsl:when test="@diffinfo = 'removed'">
                                                            <xsl:text>#FFEEEE</xsl:text>
                                                        </xsl:when>
                                                        <xsl:when test="@diffinfo = 'modified'">
                                                            <xsl:text>#EEEEFF</xsl:text>
                                                        </xsl:when>
                                                        <xsl:when test="@diffinfo = 'added'">
                                                            <xsl:text>#EEFFEE</xsl:text>
                                                        </xsl:when>
                                                    </xsl:choose>
                                                </xsl:attribute>
                                                <td width="16">
                                                    <xsl:choose>
                                                        <xsl:when test="@grouptype = '6'">
                                                            <img src="images/icon_user.gif"/>
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            <img src="images/icon_groups.gif"/>
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </td>
                                                <td align="left">
                                                    <xsl:value-of select="@displayname"/>
                                                    <input type="hidden" name="arc[key={@groupkey}]" value="[diffinfo={@diffinfo};grouptype={@grouptype};read={@read};update={@update};delete={@delete};create={@create};publish={@publish};add={@add};administrate={@administrate}]"/>
                                                </td>
                                                <td align="center">
                                                    <input type="checkbox" disabled="true">
                                                        <xsl:if test="@read = 'true'">
                                                            <xsl:attribute name="checked"><xsl:text>true</xsl:text></xsl:attribute>
                                                        </xsl:if>
                                                    </input>
                                                </td>
                                                <td align="center">
                                                    <input type="checkbox" disabled="true">
                                                        <xsl:if test="@add = 'true'">
                                                            <xsl:attribute name="checked"><xsl:text>true</xsl:text></xsl:attribute>
                                                        </xsl:if>
                                                    </input>
                                                </td>
                                                <td align="center">
                                                    <input type="checkbox" disabled="true">
                                                        <xsl:if test="@publish = 'true'">
                                                            <xsl:attribute name="checked"><xsl:text>true</xsl:text></xsl:attribute>
                                                        </xsl:if>
                                                    </input>
                                                </td>
                                                <td align="center">
                                                    <input type="checkbox" disabled="true">
                                                        <xsl:if test="@create = 'true'">
                                                            <xsl:attribute name="checked"><xsl:text>true</xsl:text></xsl:attribute>
                                                        </xsl:if>
                                                    </input>
                                                </td>
                                                <td align="center">
                                                    <input type="checkbox" disabled="true">
                                                        <xsl:if test="@update = 'true'">
                                                            <xsl:attribute name="checked"><xsl:text>true</xsl:text></xsl:attribute>
                                                        </xsl:if>
                                                    </input>
                                                </td>
                                                <td align="center">
                                                    <input type="checkbox" disabled="true">
                                                        <xsl:if test="@delete = 'true'">
                                                            <xsl:attribute name="checked"><xsl:text>true</xsl:text></xsl:attribute>
                                                        </xsl:if>
                                                    </input>
                                                </td>
                                                <td align="center">
                                                    <input type="checkbox" disabled="true">
                                                        <xsl:if test="@administrate = 'true'">
                                                            <xsl:attribute name="checked"><xsl:text>true</xsl:text></xsl:attribute>
                                                        </xsl:if>
                                                    </input>
                                                </td>
                                                <td width="80" align="center" nospan="true">
                                                    <xsl:call-template name="displaydiffname">
                                                    </xsl:call-template>
                                                </td>
                                            </tr>
                                        </xsl:for-each>
                                    </table>
                                </fieldset>
                            </xsl:if>
                            <fieldset>
                                <legend>&nbsp;%blockNewSecurity%&nbsp;</legend>
                                <table border="0" cellspacing="0" cellpadding="3" width="100%">
                                    <tr>
                                        <td></td>
                                        <td></td>
                                        <td align="center" width="120">
                                            %columnRead%
                                        </td>
                                        <td align="center" width="120">
                                            <span title="%tooltipAddContent%">%columnAddContent%</span>
                                        </td>
                                        <td align="center" width="120">
                                            <span title="%tooltipPublishContentInMenu%">%columnPublishContentInMenu%</span>
                                        </td>
                                        <td align="center" width="120">
                                            %columnCreate%
                                        </td>
                                        <td align="center" width="120">
                                            %columnUpdate%
                                        </td>
                                        <td align="center" width="120">
                                            %columnDelete%
                                        </td>
                                        <td align="center" width="120">
                                            %columnAdministrate%
                                        </td>
                                        <td></td>
                                    </tr>
                                    <xsl:for-each select="/data/accessrights/accessright">
                                        <tr>
                                            <td width="16">
                                                <xsl:choose>
                                                    <xsl:when test="@grouptype = '6'">
                                                        <img src="images/icon_user.gif"/>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <img src="images/icon_groups.gif"/>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </td>
                                            <td>
                                                <xsl:value-of select="@displayname"/>
                                            </td>
                                            <td align="center">
                                                <input type="checkbox" disabled="true">
                                                    <xsl:if test="@read='true'">
                                                        <xsl:attribute name="checked">true</xsl:attribute>
                                                    </xsl:if>
                                                </input>
                                            </td>
                                            <td align="center">
                                                <input type="checkbox" disabled="true">
                                                    <xsl:if test="@add='true'">
                                                        <xsl:attribute name="checked">true</xsl:attribute>
                                                    </xsl:if>
                                                </input>
                                            </td>
                                            <td align="center">
                                                <input type="checkbox" disabled="true">
                                                    <xsl:if test="@publish='true'">
                                                        <xsl:attribute name="checked">true</xsl:attribute>
                                                    </xsl:if>
                                                </input>
                                            </td>
                                            <td align="center">
                                                <input type="checkbox" disabled="true">
                                                    <xsl:if test="@create='true'">
                                                        <xsl:attribute name="checked">true</xsl:attribute>
                                                    </xsl:if>
                                                </input>
                                            </td>
                                            <td align="center">
                                                <input type="checkbox" disabled="true">
                                                    <xsl:if test="@update='true'">
                                                        <xsl:attribute name="checked">true</xsl:attribute>
                                                    </xsl:if>
                                                </input>
                                            </td>
                                            <td align="center">
                                                <input type="checkbox" disabled="true">
                                                    <xsl:if test="@delete='true'">
                                                        <xsl:attribute name="checked">true</xsl:attribute>
                                                    </xsl:if>
                                                </input>
                                            </td>
                                            <td align="center">
                                                <input type="checkbox" disabled="true">
                                                    <xsl:if test="@administrate='true'">
                                                        <xsl:attribute name="checked">true</xsl:attribute>
                                                    </xsl:if>
                                                </input>
                                            </td>
                                            <td width="80">
                                            </td>
                                        </tr>
                                    </xsl:for-each>
                                </table>
                            </fieldset>


                        </div>

                    </div>
                    <script type="text/javascript" language="JavaScript">
                        setupAllTabs();
                    </script>

                    <table border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td class="form_form_buttonrow_seperator"><img src="images/1x1.gif"/></td>
                        </tr>
                    </table>

                </form>

            </body>
        </html>

    </xsl:template>

    <xsl:template name="displaymenupath_">
        <xsl:param name="xpath"/>
        <xsl:param name="supermenuitemkey"/>
        <xsl:param name="leaf" select="false()"/>

        <xsl:if test="not($xpath/@key = $supermenuitemkey)">
            <xsl:call-template name="displaymenupath_">
                <xsl:with-param name="xpath" select="$xpath/parent::node()"/>
                <xsl:with-param name="supermenuitemkey" select="$supermenuitemkey"/>
            </xsl:call-template>
        </xsl:if>

        <xsl:value-of select="$xpath/@name"/>
        <xsl:if test="not($leaf)">
            <xsl:text>&nbsp;/&nbsp;</xsl:text>
        </xsl:if>

    </xsl:template>

   <xsl:template name="generateheader">
        <xsl:param name="menuitem"/>

        <xsl:if test="boolean($menuitem/parent::node())">
            <xsl:call-template name="generateheader">
                <xsl:with-param name="menuitem" select="$menuitem/parent::node()"/>
            </xsl:call-template>
        </xsl:if>

        <xsl:if test="$menuitem/self::menuitem">
            <xsl:text> / </xsl:text>
            <a href="adminpage?op=browse&amp;page={$page}&amp;parentmi={$menuitem/@key}&amp;selectedunitkey={$selectedunitkey}&amp;menukey={$menukey}">
            	<xsl:choose>
	            	<xsl:when test="$menuitem/name">
		                <xsl:value-of select="$menuitem/name"/>
		            </xsl:when>
		            <xsl:otherwise>
		            	<xsl:value-of select="$menuitem/@name"/>
		            </xsl:otherwise>
		        </xsl:choose>
            </a>
        </xsl:if>

    </xsl:template>

    <xsl:template name="displaydiffname">
        <xsl:choose>
            <xsl:when test="@diffinfo = 'removed'">%removed%</xsl:when>
            <xsl:when test="@diffinfo = 'added'">%added%</xsl:when>
            <xsl:when test="@diffinfo = 'modified'">%modified%</xsl:when>
        </xsl:choose>
    </xsl:template>


</xsl:stylesheet>