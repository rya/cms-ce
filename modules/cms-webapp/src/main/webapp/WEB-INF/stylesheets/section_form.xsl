<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:x="mailto:vro@enonic.com?subject=foobar"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>
    
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>
    <xsl:include href="common/sectionheader.xsl"/>
    <xsl:include href="common/serialize.xsl"/>

    <xsl:param name="key"/>
    <xsl:param name="menukey"/>
    <xsl:param name="supersectionkey"/>
    

    <xsl:variable name="section">
        <xsl:choose>
            <xsl:when test="/wizarddata/wizardstate/stepstate[@id = '0']/section">
                <xsl:copy-of select="/wizarddata/wizardstate/stepstate[@id = '0']/section"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="/wizarddata/sections/section"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:template name="section_form_header">
        <a href="adminpage?op=browse&amp;page={$page}&amp;menukey={$menukey}">%headSections%</a>
        <xsl:call-template name="sectionheader" >
            <xsl:with-param name="rootelem" select="/wizarddata"/>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template name="section_form_title">
        <xsl:choose>
            <xsl:when test="/wizarddata/sections/section">
                %headEdit%:
            </xsl:when>
            <xsl:otherwise>
                %headCreate%:
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text> </xsl:text><span id="titlename"><xsl:value-of select="exslt-common:node-set($section)/section/@name"/></span>
    </xsl:template>

    <xsl:template name="step0">
        <script language="Javascript" type="text/javascript">
            function checkAccessRightPropagation() {
            <xsl:if test="exslt-common:node-set($section)/section/@key and exslt-common:node-set($section)/section/@childcount and exslt-common:node-set($section)/section/@childcount &gt; 0">
                <xsl:if test="count(/wizarddata/wizardstate/stepstate) = 1">
                    if( isAccessRightsChanged() ) {
                </xsl:if>
                      // Reset propagate flag
                      document.getElementById("propagate").value = "false";

                      if( confirm("%askPropagateSecurity%") ) {
                          document.getElementById("propagate").value = "true";
                      }

                <xsl:if test="count(/wizarddata/wizardstate/stepstate) = 1">
                    }
                </xsl:if>
            </xsl:if>
            }

            function msgNoContentTypes() {
              if (document.getElementById('contenttypekey').options.length == 0) {
                return confirm('%alertNoContentTypeSelected%');
              }
              else {
                return true;
              }
            }

        </script>

        <div class="tab-pane" id="tab-pane-1">
            <script type="text/javascript" language="JavaScript">
                var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
            </script>

            <div class="tab-page" id="tab-page-general">
                <span class="tab">%blockGeneral%</span>
                
                <script type="text/javascript" language="JavaScript">
                    tabPane1.addTabPage( document.getElementById( "tab-page-general" ) );
                </script>
                
                <fieldset>
                    <legend>%blockGeneral%</legend>

                    <table width="100%" border="0" cellspacing="2" cellpadding="2">
                        <tr>

                            <input type="hidden" name="propagate" value="false"/>

                            <xsl:if test="/wizarddata/sections/section and exslt-common:node-set($section)/section/@supersectionkey">
                                <input type="hidden" name="supersectionkey" value="{exslt-common:node-set($section)/section/@supersectionkey}"/>
                            </xsl:if>

                            <xsl:if test="exslt-common:node-set($section)/section/@childcount">
                                <input type="hidden" name="childcount" value="{exslt-common:node-set($section)/section/@childcount}"/>
                            </xsl:if>
                                
                            <xsl:call-template name="textfield">
                                <xsl:with-param name="name" select="'stepstate_section_name'"/>
                                <xsl:with-param name="label" select="'%fldName%:'"/>
                                <xsl:with-param name="selectnode" select="exslt-common:node-set($section)/section/@name"/>
                                <xsl:with-param name="size" select="'40'"/>
                                <xsl:with-param name="maxlength" select="'255'"/>
                                <xsl:with-param name="colspan" select="'1'"/>
                                <xsl:with-param name="required" select="'true'"/>
                                <xsl:with-param name="onkeyup">javascript:document.getElementById('titlename').innerHTML = this.value;</xsl:with-param>
                            </xsl:call-template>
                        </tr>
                        
                        <tr>
                            <xsl:call-template name="textarea">
                                <xsl:with-param name="name" select="'stepstate_section_description'"/>
                                <xsl:with-param name="label" select="'%fldDescription%:'"/>
                                <xsl:with-param name="selectnode" select="exslt-common:node-set($section)/section/description"/>
                                <xsl:with-param name="rows" select="'10'"/>
                                <xsl:with-param name="cols" select="'60'"/>
                                <xsl:with-param name="colspan" select="'1'"/>
                            </xsl:call-template>
                        </tr>
                    </table>
                    
                </fieldset>
                
            </div>
            
            <div class="tab-page" id="tab-page-settings">
                <span class="tab">%blockSettings%</span>
                
                <script type="text/javascript" language="JavaScript">
                    tabPane1.addTabPage( document.getElementById( "tab-page-settings" ) );
                </script>
                
                <fieldset>
                    <legend>%blockAllowedContentTypes%</legend>
                    <table cellspacing="0" cellpadding="2" border="0">
                        <tr>
                            <td width="130"></td>
                        </tr>
                        <tr>
                            <td colspan="2">

                                <xsl:variable name="selected_ctys">
                                    <xsl:choose>
                                        <xsl:when test="/wizarddata/parentcontenttypes/contenttypes">
                                            <xsl:copy-of select="/wizarddata/parentcontenttypes/contenttypes"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:copy-of select="exslt-common:node-set($section)/section/contenttypes"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:variable>

                                <table border="0" cellspacing="2" cellpadding="0" width="100%">
                                    <tr>
                                        <td>
                                            <div style="padding-bottom: 1em;">
                                                %fldAvailableContentTypes%:
                                            </div>
                                            
                                            <select multiple="multiple" style="width: 13em; height: 10em;" name="availablect">
                                                <xsl:for-each select="/wizarddata/contenttypes/contenttype">
                                                    <xsl:sort select="name"/>
                                                    
                                                    <xsl:variable name="varkey">
                                                        <xsl:value-of select="@key"/>
                                                    </xsl:variable>
                                                    
                                                    <xsl:if test="not(exslt-common:node-set($selected_ctys)/contenttypes/contenttype[@key = $varkey])">
                                                        <option value="{@key}" ondblclick="moveOptions('availablect', 'contenttypekey');"><xsl:value-of select="name"/></option>
                                                    </xsl:if>
                                                    
                                                </xsl:for-each>
                                            </select>
                                        </td>
                                        
                                        <td style="padding: 0.5em;">
                                            <xsl:call-template name="button">
                                                <xsl:with-param name="type" select="'button'"/>
                                                <xsl:with-param name="image" select="'images/icon_move_right.gif'"/>
                                                <xsl:with-param name="onclick">
                                                    <xsl:text>javascript:moveOptions('availablect', 'contenttypekey');</xsl:text>
                                                </xsl:with-param>
                                            </xsl:call-template>
                                            <br/>
                                            <xsl:call-template name="button">
                                                <xsl:with-param name="type" select="'button'"/>
                                                <xsl:with-param name="image" select="'images/icon_move_left.gif'"/>
                                                <xsl:with-param name="onclick">
                                                    <xsl:text>javascript:moveOptions('contenttypekey', 'availablect');</xsl:text>
                                                </xsl:with-param>
                                            </xsl:call-template>
                                        </td>
                                        
                                        <td>
                                            <div style="padding-bottom: 1em;">
                                                %fldSelectedContentTypes%:
                                            </div>
                                            
                                            <select multiple="multiple" style="width: 13em; height: 10em;" name="contenttypekey" id="contenttypekey">
                                                <xsl:for-each select="exslt-common:node-set($selected_ctys)/contenttypes/contenttype">
                                                    <xsl:sort select="name"/>
                                                    
                                                    <xsl:variable name="varkey">
                                                        <xsl:value-of select="@key"/>
                                                    </xsl:variable>
                                                    
                                                    <option value="{@key}" ondblclick="moveOptions('contenttypekey', 'availablect');"><xsl:value-of select="name"/></option>
                                                    
                                                </xsl:for-each>
                                            </select>
                                            
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                    
                </fieldset>

                <fieldset>
                    <legend>%blockSectionType%</legend>

                    <!-- The 'ordered' variable specifies which radio button should be checked. 
                         It is set to "true" if the section already is ordered, or if we are creating
                         a new section and the parent section is ordered.
                         -->
                    <xsl:variable name="ordered">
                        <xsl:choose>
                            <xsl:when test="exslt-common:node-set($section)/section/@ordered = 'true' or (not(exslt-common:node-set($section)/section) and /wizarddata/parentcontenttypes/@ordered = 'true')">true</xsl:when>
                            <xsl:otherwise>false</xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>

                    <table cellspacing="0" cellpadding="2" border="0">
                        <tr>
                            <td width="130"></td>
                        </tr>
                        <tr>
                            <td>
                                <input type="radio" name="stepstate_section_ordered" value="on">
                                    <xsl:if test="$ordered = 'true'">
                                        <xsl:attribute name="checked">checked</xsl:attribute>
                                    </xsl:if>
                                </input> %optOrdered%
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <input type="radio" name="stepstate_section_ordered" value="off">
                                    <xsl:if test="$ordered = 'false'">
                                        <xsl:attribute name="checked">checked</xsl:attribute>
                                    </xsl:if>
                                </input> %optUnordered%
                            </td>
                        </tr>
                    </table>
                </fieldset>

            </div>
            
            <div class="tab-page" id="tab-page-security">
                <span class="tab">%blockSecurity%</span>
                
                <script type="text/javascript" language="JavaScript">
                    tabPane1.addTabPage( document.getElementById( "tab-page-security" ) );
                </script>
                
                <fieldset>
                    <legend>%blockSecurity%</legend>

                    <xsl:variable name="ar">
                        <xsl:choose>
                            <xsl:when test="exslt-common:node-set($section)/section/accessrights">
                                <xsl:copy-of select="exslt-common:node-set($section)/section/accessrights"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:copy-of select="/wizarddata/accessrights"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>

                    <xsl:call-template name="accessrights">
                        <xsl:with-param name="right_create_available" select="false()"/>
                        <xsl:with-param name="right_update_available" select="false()"/>
                        <xsl:with-param name="right_delete_available" select="false()"/>
                        <xsl:with-param name="right_adminread_available" select="false()"/>
                        <xsl:with-param name="right_publish_available" select="false()"/>
                        <xsl:with-param name="right_add_available" select="true()"/>
                        <xsl:with-param name="right_approve_available" select="true()"/>
                        <xsl:with-param name="dataxpath" select="exslt-common:node-set($ar)"/>
                    </xsl:call-template>

                </fieldset>
            </div>
            
        </div>
        
        <script type="text/javascript" language="JavaScript">
            setupAllTabs();
        </script>
    </xsl:template>


    <xsl:template name="step1">
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
                    <legend>&nbsp;%blockSections%&nbsp;</legend>

                    <xsl:for-each select="exslt-common:node-set($section)/section/accessrights/accessright">
                        <input type="hidden" id="accessright[key={@groupkey}]" name="accessright[key={@groupkey}]" value="[read={@read='true'};approve={@approve='true'};publish={@publish='true'};administrate={@administrate='true'}]"/>
                    </xsl:for-each>
                    
                    <br/>

                    <table width="100%" cellspacing="0" cellpadding="0" class="browsetable">
                        <xsl:call-template name="display_section">
                            <xsl:with-param name="xpathSection" select="exslt-common:node-set($section)/section"/>
                        </xsl:call-template>
                    </table>
                </fieldset>
                <fieldset>
                    <legend>&nbsp;%blockOptions%&nbsp;</legend>
                    <input type="checkbox" name="applyonlychanges" checked="true"/>&nbsp;&nbsp;%applyOnlyChanges%
                </fieldset>
                <br/>
            </div>

            <div class="tab-page" id="tab-page-2">
                <span class="tab">%blockViewChanges%</span>
                
                <script type="text/javascript" language="JavaScript">
                    tabPane1.addTabPage( document.getElementById( "tab-page-2" ) );
                </script>
                
                <xsl:if test="/wizarddata/changedaccessrights/accessright">
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
                                    <span title="%tooltipPublishContentInMenu%">%columnPublishContentInMenu%</span>
                                </td>
                                
                                <td align="center" width="120">
                                    %columnApprove%
                                </td>
                                <td align="center" width="120">
                                    %columnAdministrate%
                                </td>
                                <td></td>
                            </tr>
                            <xsl:for-each select="/wizarddata/changedaccessrights/accessright">
                                <xsl:sort select="@groupname"/>

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
                                        <!-- This is for supplying servlet information about changed accessrights -->
                                        <input type="hidden" name="arc[key={@groupkey}]" value="[diffinfo={@diffinfo};grouptype={@grouptype};approve={@approve};read={@read};publish={@publish};administrate={@administrate}]"/>
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
                                            <xsl:if test="@publish = 'true'">
                                                <xsl:attribute name="checked"><xsl:text>true</xsl:text></xsl:attribute>
                                            </xsl:if>
                                        </input>
                                    </td>
                                    <td align="center">
                                        <input type="checkbox" disabled="true">
                                            <xsl:if test="@approve = 'true'">
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
                                <span title="%tooltipPublishContentInMenu%">%columnPublishContentInMenu%</span>
                            </td>
                            <td align="center" width="120">
                                %columnApprove%
                            </td>
                            <td align="center" width="120">
                                %columnAdministrate%
                            </td>
                            <td></td>
                        </tr>
                        <xsl:for-each select="exslt-common:node-set($section)/section/accessrights/accessright">
                            <xsl:sort select="@groupname"/>

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
                                        <xsl:if test="@publish='true'">
                                            <xsl:attribute name="checked">true</xsl:attribute>
                                        </xsl:if>
                                    </input>
                                </td>
                                <td align="center">
                                    <input type="checkbox" disabled="true">
                                        <xsl:if test="@approve='true'">
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
    </xsl:template>

    <xsl:template name="display_section">
        <xsl:param name="xpathSection"/>

        <tr>

            <xsl:if test="($xpathSection/accessrights/userright/@administrate = 'true' or not($xpathSection/accessrights/userright)) and not($xpathSection/@key = $key)">
                <xsl:attribute name="onmouseover">javascript:this.style.backgroundColor="#FFFFFF"</xsl:attribute>
                <xsl:attribute name="onmouseout">javascript:this.style.backgroundColor="#EEEEEE"</xsl:attribute>
            </xsl:if>

            <td width="25" align="center" valign="top" class="browsetablecell">
                <xsl:attribute name="style">
                    <xsl:text>cursor: default;</xsl:text>
                </xsl:attribute>
                <xsl:choose>
                    <xsl:when test="$xpathSection/@key = $key">
                        <input type="hidden" name="chkPropagate[key={$xpathSection/@key}]" value="true"/>
                    </xsl:when>
                    <xsl:when test="$xpathSection/accessrights/userright/@administrate = 'true' or not($xpathSection/accessrights/userright)">
                        <input type="checkbox" name="chkPropagate[key={$xpathSection/@key}]" checked="true"/>
                    </xsl:when>
                    <xsl:otherwise>
                    </xsl:otherwise>
                </xsl:choose>
            </td>

            <td class="browsetablecell">
                <xsl:choose>
                    <xsl:when test="not($xpathSection/@key = $key) and ($xpathSection/accessrights/userright/@administrate = 'true' or not($xpathSection/accessrights/userright))">
                        <xsl:attribute name="onclick">
                            <xsl:text>changeSelection(</xsl:text><xsl:value-of select="$xpathSection/@key"/><xsl:text>)</xsl:text>
                        </xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="style">
                            <xsl:text>cursor: default;</xsl:text>
                        </xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
                <span>
                    <xsl:if test="not($xpathSection/accessrights/userright/@administrate = 'true' or not($xpathSection/accessrights/userright))">
                        <xsl:attribute name="style">
                            <xsl:text>color: #888888;</xsl:text>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:text>... </xsl:text>
                    <xsl:call-template name="displaysecpath_">
                        <xsl:with-param name="xpath" select="$xpathSection"/>
                        <xsl:with-param name="supercategory" select="$key"/>
                        <xsl:with-param name="leaf" select="true()"/>
                    </xsl:call-template>
                </span>
            </td>
        </tr>

        <xsl:choose>
            <xsl:when test="$xpathSection/@key = $key">
                <xsl:for-each select="/wizarddata/sections/section">
                    <xsl:call-template name="display_section">
                        <xsl:with-param name="xpathSection" select="."/>
                    </xsl:call-template>
                </xsl:for-each>                
            </xsl:when>
            <xsl:otherwise>
                <xsl:for-each select="$xpathSection/sections/section">
                    <xsl:call-template name="display_section">
                        <xsl:with-param name="xpathSection" select="."/>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:otherwise>
        </xsl:choose>


    </xsl:template>

    
    <xsl:template name="displaysecpath_">
        <xsl:param name="xpath"/>
        <xsl:param name="supersection"/>
        <xsl:param name="leaf" select="false()"/>

        <xsl:choose>
            <xsl:when test="$xpath/@supersectionkey = $key">
                <xsl:value-of select="exslt-common:node-set($section)/section/@name"/>
                <xsl:text>&nbsp;/&nbsp;</xsl:text>
            </xsl:when>

            <xsl:when test="$xpath/@key != $key">
                <xsl:call-template name="displaysecpath_">
                    <xsl:with-param name="xpath" select="$xpath/parent::node()/parent::node()"/>
                    <xsl:with-param name="supersection" select="$supersection"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>

        <xsl:value-of select="$xpath/@name"/>
        <xsl:if test="not($leaf)">
            <xsl:text>&nbsp;/&nbsp;</xsl:text>
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
