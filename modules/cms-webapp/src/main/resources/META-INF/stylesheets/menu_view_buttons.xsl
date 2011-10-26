<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:variable name="xpathUserright" select="/menus/menu/accessrights/userright"/>

    <xsl:template name="menu_view_buttons">
        <xsl:param name="menuelem"/>
        <xsl:param name="menuitemelem"/>
        <xsl:param name="highlight"/>
        <xsl:param name="createright"/>
        <xsl:param name="addright"/>
        <xsl:param name="publishright"/>
        <xsl:param name="updateright"/>
        <xsl:param name="deleteright"/>
        <xsl:param name="administrateright"/>
        <xsl:param name="menuadministrateright"/>
        <xsl:param name="parentadministrateright"/>
        <xsl:param name="browsemode" select="'menuitem'"/>
        <xsl:param name="parentkey"/>

        <xsl:variable name="menuitemkey" select ="$menuitemelem/@key"/>

        <xsl:variable name="page" select="850"/>

		<script type="text/javascript" language="JavaScript">
			function OpenMoveWindow( objThis, page, currentParentKey, key, menuitemname, width, height) {
                var currentRow = 0;
                var l = (screen.width - width) / 2;
                var t = (screen.height - height) / 2;
                newWindow = window.open("adminpage?page=" + page + "&amp;key="+ key + "&amp;cur_parent_key="+ currentParentKey +"&amp;menuitemname=" + menuitemname + "&amp;op=selectnewparent&amp;menukey="+ <xsl:value-of select="$menukey"/> +"", "", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" + height + ",top=" + t + ", left=" + l);
                newWindow.focus();
            }
		</script>

        <xsl:if test="$createright or $administrateright or ($browsemode = 'section' and ($addright or $publishright))">

            <xsl:choose>
                <xsl:when test="$browsemode = 'section'">

                    <xsl:variable name="disable_add">
                        <xsl:choose>
                            <xsl:when test="/contenttitles/section/contenttypes/contenttype">
                                <xsl:text>false</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>true</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>

                    <xsl:call-template name="button">
                        <xsl:with-param name="caption" select="'%cmdAdd%'"/>
                        <xsl:with-param name="disabled" select="$disable_add"/>
                        <xsl:with-param name="onclick">
                            <xsl:text>javascript:OpenContentPopup(</xsl:text>
                            <xsl:text>-1, -1, 'addcontenttosection', null, -1, contentTypes);</xsl:text>
                        </xsl:with-param>
                    </xsl:call-template>

                </xsl:when>
                <xsl:otherwise>

                    <xsl:variable name="disable_new">
                        <xsl:choose>
                            <xsl:when test="$highlight != -1">
                                <xsl:text>true</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>false</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>

                    <xsl:call-template name="button">
                        <xsl:with-param name="name" select="'cmdNew'"/>
                        <xsl:with-param name="type" select="'link'"/>
                        <xsl:with-param name="caption" select="'%cmdNew%'"/>
                        <xsl:with-param name="disabled" select="$disable_new"/>
                        <xsl:with-param name="href">
                            <xsl:text>adminpage?page=</xsl:text>
                            <xsl:value-of select="$page"/>
                            <xsl:text>&amp;op=form&amp;key=none</xsl:text>
                            <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
                            <xsl:text>&amp;browsemode=</xsl:text><xsl:value-of select="$browsemode"/>
                            <xsl:if test="$menuitemkey">
                                <xsl:text>&amp;insertbelow=</xsl:text><xsl:value-of select="$menuitemkey"/>
                            </xsl:if>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
        <xsl:text>&nbsp;</xsl:text>

        <xsl:if test="$menuitemelem and ($updateright or $administrateright)">
            <!--xsl:text>&nbsp;</xsl:text-->
            <xsl:variable name="disable_edit">
                <xsl:choose>
                    <xsl:when test="$highlight != -1">
                        <xsl:text>true</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>false</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:call-template name="button">
                <xsl:with-param name="type" select="'link'"/>
                <xsl:with-param name="name" select="'cmdEdit'"/>
                <xsl:with-param name="caption" select="'%cmdEdit%'"/>
                <xsl:with-param name="disabled" select="$disable_edit"/>
                <xsl:with-param name="href">
                    <xsl:text>adminpage?page=</xsl:text>
                    <xsl:value-of select="$page"/>
                    <xsl:text>&amp;op=form&amp;key=</xsl:text><xsl:value-of select="$menuitemkey"/>
                    <xsl:text>&amp;type=</xsl:text><xsl:value-of select="$menuitemelem/@type"/>
                    <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
                    <xsl:text>&amp;browsemode=</xsl:text><xsl:value-of select="$browsemode"/>
                    <xsl:if test="$menuitemkey">
                        <xsl:text>&amp;insertbelow=</xsl:text>
                        <xsl:value-of select="$menuitemkey"/>
                    </xsl:if>
                </xsl:with-param>
            </xsl:call-template>

            <xsl:if test="$administrateright">
                <xsl:text>&nbsp;</xsl:text>
                <xsl:variable name="disable_move">
                    <xsl:choose>
                        <xsl:when test="$highlight != -1">
                            <xsl:text>true</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>false</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:call-template name="button">
                    <xsl:with-param name="disabled" select="$disable_move"/>
                    <xsl:with-param name="type" select="'button'"/>
                    <xsl:with-param name="name" select="'cmdMove'"/>
                    <xsl:with-param name="caption" select="'%cmdMove%'"/>
                    <xsl:with-param name="onclick">
                        <xsl:text>javascript:OpenMoveWindow( this, 850, </xsl:text><xsl:value-of select="$parentkey"/><xsl:text>,</xsl:text><xsl:value-of select="$menuitemkey"/><xsl:text>,"</xsl:text>
                        <xsl:value-of select="$menuitemelem/name"/><xsl:text>", 250, 300, 'true');</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="useOnclick" select="'false'"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>

        <xsl:if test="$menuitemelem and $deleteright">

            <xsl:text>&nbsp;</xsl:text>
      
            <xsl:variable name="disable_delete">
                <xsl:choose>
                    <xsl:when test="$menuelem/@firstpage = $menuitemkey or $menuitemelem/@loginpage = $menuitemkey or $menuelem/@errorpage = $menuitemkey">
                        <xsl:text>true</xsl:text>
                    </xsl:when>
                    <xsl:when test="$highlight != -1">
                        <xsl:text>true</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>false</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:variable name="delete_href">
                <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
                <xsl:text>&amp;op=removeitem&amp;key=</xsl:text><xsl:value-of select="$menuitemkey"/>
                <xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
                <xsl:text>&amp;parentmi=</xsl:text><xsl:value-of select="$parentkey"/>
                <xsl:text>&amp;browsemode=</xsl:text><xsl:value-of select="$browsemode"/>
            </xsl:variable>

            <xsl:variable name="delete_condition">
                <xsl:if test="$highlight = -1">
                    <xsl:text>confirm('%alertDeletePage%')</xsl:text>
                </xsl:if>
            </xsl:variable>

            <xsl:call-template name="button">
                <xsl:with-param name="disabled" select="$disable_delete"/>
                <xsl:with-param name="name" select="'cmdDelete'"/>
                <xsl:with-param name="type" select="'link'"/>
                <xsl:with-param name="caption" select="'%cmdDelete%'"/>
                <xsl:with-param name="href" select="$delete_href"/>
                <xsl:with-param name="condition" select="$delete_condition"/>
            </xsl:call-template>
        </xsl:if>

        <xsl:if test="not($menuitemelem)">

            <xsl:text>&nbsp;</xsl:text>

            <!-- bare de med administratorrettigheter på menyen skal få opp denne-->

            <xsl:variable name="disablesettings">
                <xsl:choose>
                    <xsl:when test="$highlight != -1">true</xsl:when>
                    <xsl:when test="$menuadministrateright">false</xsl:when>
                    <xsl:otherwise>true</xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:if test="$disablesettings != 'true'">
                <xsl:call-template name="button">
                    <xsl:with-param name="type" select="'link'"/>
                    <xsl:with-param name="caption" select="'%cmdSettings%'"/>
                    <xsl:with-param name="href">
                        <xsl:text>adminpage?page=</xsl:text>
                        <xsl:value-of select="$page"/>
                        <xsl:text>&amp;op=setup</xsl:text>
                        <xsl:text>&amp;menukey=</xsl:text>
                        <xsl:value-of select="$menukey"/>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>

        <xsl:variable name="hide_open">
            <xsl:choose>
                <xsl:when test="$highlight != -1">
                    <xsl:text>true</xsl:text>
                </xsl:when>
                <!--xsl:when test="not($menuelem/@debug = 'true')">
                       <xsl:text>true</xsl:text>
                   </xsl:when-->
                <xsl:when test="$menuitemelem/@type = 'label'">
                    <xsl:text>true</xsl:text>
                </xsl:when>
                <!--xsl:when test="$menuitemelem/@type = 'url'">
                       <xsl:text>true</xsl:text>
                   </xsl:when-->
                <xsl:when test="$menuitemelem/@type = 'section'">
                    <xsl:text>true</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>false</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:if test="$hide_open = 'false'">
            <xsl:text>&nbsp;</xsl:text>

            <xsl:variable name="debugpathendswithslash" select="substring($debugpath, string-length($debugpath)) = '/'"/>
            <xsl:variable name="menuitempath">
                <xsl:choose>
                    <xsl:when test="$debugpathendswithslash and starts-with($menuitemelem/path, '/')">
                        <xsl:value-of select="substring($menuitemelem/path, 2)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$menuitemelem/path"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:variable name="open_href">
                <xsl:value-of select="$debugpath"/>
                <xsl:value-of select="$menuitempath"/>
            </xsl:variable>
            <xsl:call-template name="button">
                <xsl:with-param name="type" select="'link'"/>
                <xsl:with-param name="caption" select="'%btnOpenInIce%'"/>
                <xsl:with-param name="tooltip" select="'%btnOpenInIceTooltip%'"/>
                <xsl:with-param name="href" select="$open_href"/>
                <xsl:with-param name="target" select="'_blank'"/>
            </xsl:call-template>
        </xsl:if>

    </xsl:template>

</xsl:stylesheet>