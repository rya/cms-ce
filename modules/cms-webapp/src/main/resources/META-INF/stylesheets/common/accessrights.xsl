<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:template name="accessrights">
    <xsl:param name="readonly" select="false()"/>
    <xsl:param name="right_adminread_available" select="false()"/>
    <xsl:param name="right_read_available" select="true()"/>
    <xsl:param name="right_update_available" select="true()"/>
    <xsl:param name="right_delete_available" select="true()"/>
    <xsl:param name="right_create_available" select="true()"/>
    <xsl:param name="right_publish_available" select="true()"/>
    <xsl:param name="right_administrate_available" select="true()"/>
  	<xsl:param name="allowauthenticated" select="false()"/>
    <xsl:param name="archive_hack" select="false()"/>

    <!-- approve defaults to _off_ -->
    <xsl:param name="right_add_available" select="false()"/>
    <xsl:param name="right_approve_available" select="false()"/>
    <xsl:param name="keep_anonymous" select="false()"/>
    <xsl:param name="dataxpath"/>

    <xsl:param name="read_disabled" select="true()"/>

    <!-- Initialize available rights -->
    <script type="text/javascript" language="JavaScript">
      setRightAvailable( 'adminread', <xsl:value-of select="$right_adminread_available"/> );
      setRightAvailable( 'read', <xsl:value-of select="$right_read_available"/> );
      setRightAvailable( 'update', <xsl:value-of select="$right_update_available"/> );
      setRightAvailable( 'delete', <xsl:value-of select="$right_delete_available"/> );
      setRightAvailable( 'create', <xsl:value-of select="$right_create_available"/> );
      setRightAvailable( 'publish', <xsl:value-of select="$right_publish_available"/> );
      setRightAvailable( 'add', <xsl:value-of select="$right_add_available"/> );
      setRightAvailable( 'administrate', <xsl:value-of select="$right_administrate_available"/> );
      setRightAvailable( 'approve', <xsl:value-of select="$right_approve_available"/> );
    </script>

    <input type="hidden" name="updateaccessrights" value="{not($readonly)}"/>

    <script type="text/javascript">
      // Used by accessrights.js
      var g_readDisabled = <xsl:value-of select="$archive_hack"/>;
    </script>

    <table border="0" cellspacing="0" cellpadding="2">
      <tbody id="accessRightTable">
        <tr>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <xsl:if test="$right_read_available">
            <td>%columnRead%</td>
          </xsl:if>
          <xsl:if test="$right_adminread_available">
            <td>%columnAdminRead%</td>
          </xsl:if>
          <xsl:if test="$right_add_available">
            <td><span title="%tooltipAddContent%">%columnAddContent%</span></td>
          </xsl:if>
          <xsl:if test="$right_create_available">
            <td>%columnCreate%</td>
          </xsl:if>
          <xsl:if test="$right_publish_available and not($archive_hack)">
            <td><span title="%tooltipPublishContentInMenu%">%columnPublishContentInMenu%</span></td>
          </xsl:if>
          <xsl:if test="$right_publish_available and $archive_hack">
            <td>%columnPublish1%</td>
          </xsl:if>
          <xsl:if test="$right_update_available">
            <td>%columnUpdate%</td>
          </xsl:if>
          <xsl:if test="$right_delete_available">
            <td>%columnDelete%</td>
          </xsl:if>
          <xsl:if test="$right_approve_available">
            <td>%columnApprove%</td>
          </xsl:if>
          <xsl:if test="$right_administrate_available">
            <td>%columnAdministrate%</td>
          </xsl:if>
          <td><br/></td>
        </tr>
        <xsl:variable name="sortby" select="'@grouptype'"/>
        <xsl:variable name="sortby-direction" select="'ascending'"/>
        <xsl:variable name="sortby-data-type" select="'text'"/>
        <xsl:for-each select="$dataxpath/accessrights/accessright">
          <xsl:sort data-type="{$sortby-data-type}" order="{$sortby-direction}" select="*[name() = $sortby] | @*[concat('@',name()) = $sortby]"/>
          <xsl:variable name="adminreadright" select="@adminread = 'true'"/>
          <xsl:variable name="readright" select="@read = 'true'"/>
          <xsl:variable name="updateright" select="@update = 'true'"/>
          <xsl:variable name="deleteright" select="@delete = 'true'"/>
          <xsl:variable name="createright" select="@create = 'true'"/>
          <xsl:variable name="publishright" select="@publish = 'true'"/>
          <xsl:variable name="administrateright" select="@administrate = 'true'"/>
          <xsl:variable name="approveright" select="@approve = 'true'"/>
          <xsl:variable name="addright" select="@add = 'true'"/>
          <xsl:variable name="disabledgrouptype" select="@grouptype = '5' or ($keep_anonymous = 'true' and @grouptype = '7')"/>
          <xsl:variable name="isAdministrator" select="@grouptype = 5"/>
          <xsl:variable name="name">
            <xsl:choose>
              <xsl:when test="@displayname">
                <xsl:value-of select="@displayname"/>
              </xsl:when>
              <xsl:when test="@grouptype = '6'">
                <xsl:value-of select="@fullname"/>
              </xsl:when>
              <xsl:when test="@grouptype = '7'">
                <xsl:value-of select="'anonymous'"/>
                <xsl:text> (</xsl:text>
                <xsl:value-of select="'anonymous'"/>
                <xsl:text>)</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="@groupname"/>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="@qualifiedName">
              <xsl:text> (</xsl:text>
              <xsl:value-of select="@qualifiedName"/>
              <xsl:text>)</xsl:text>
            </xsl:if>
          </xsl:variable>
          <tr>
            <td>
              <!-- Register the accessright as choosen -->
              <script type="text/javascript" language="JavaScript">
                addChoosen('<xsl:value-of select="@groupkey"/>');
              </script>
              <xsl:choose>
                <xsl:when test="@grouptype = '6' or @grouptype = '7'">
                  <img src="images/icon_user.gif"/>
                </xsl:when>
                <xsl:otherwise>
                  <img src="images/icon_groups.gif"/>
                </xsl:otherwise>
              </xsl:choose>
            </td>

            <td style="width: 300px">

              <xsl:value-of select="$name"/>
              <input type="hidden" id="original_accessright[key={@groupkey}]" name="original_accessright[key={@groupkey}]" value='[adminread={$adminreadright};read={$readright};update={$updateright};delete={$deleteright};create={$createright};publish={$publishright};approve={$approveright};add={$addright};administrate={$administrateright};name={$name};grouptype={@grouptype}]'/>
              <input type="hidden" id="accessright[key={@groupkey}]" name="accessright[key={@groupkey}]" value='[adminread={$adminreadright};read={$readright};update={$updateright};delete={$deleteright};create={$createright};publish={$publishright};approve={$approveright};add={$addright};administrate={$administrateright};name={$name};grouptype={@grouptype}]'/>
              <input type="hidden" id="grouptype_{@groupkey}" name="grouptype_{@groupkey}" value="{@grouptype}"/>
              <input type="hidden" id="groupname_{@groupkey}" name="groupname_{@groupkey}" value="{$name}"/>
            </td>
            <xsl:if test="$right_read_available">
              <td align="center">
                <xsl:call-template name="accessright_checkbox">
                  <xsl:with-param name="name">chkAccessRight[key=<xsl:value-of select="@groupkey"/>;right=read]</xsl:with-param>
                  <xsl:with-param name="value" select="@read"/>
                  <xsl:with-param name="disabled" select="($archive_hack and $isAdministrator) or not($archive_hack)"/>
                </xsl:call-template>
              </td>
            </xsl:if>
            <xsl:if test="$right_adminread_available">
              <td align="center">
                <xsl:call-template name="accessright_checkbox">
                  <xsl:with-param name="name">chkAccessRight[key=<xsl:value-of select="@groupkey"/>;right=adminread]</xsl:with-param>
                  <xsl:with-param name="value" select="@adminread"/>
                  <xsl:with-param name="disabled" select="$administrateright or $disabledgrouptype or $readonly"/>
                </xsl:call-template>
              </td>
            </xsl:if>
            <xsl:if test="$right_add_available">
              <td align="center">
                <xsl:call-template name="accessright_checkbox">
                  <xsl:with-param name="name">chkAccessRight[key=<xsl:value-of select="@groupkey"/>;right=add]</xsl:with-param>
                  <xsl:with-param name="value" select="@add"/>
                  <xsl:with-param name="disabled" select="$administrateright or $disabledgrouptype or $readonly"/>
                </xsl:call-template>
              </td>
            </xsl:if>
            <xsl:if test="$right_create_available">
              <td align="center">        
                <xsl:call-template name="accessright_checkbox">
                  <xsl:with-param name="name">chkAccessRight[key=<xsl:value-of select="@groupkey"/>;right=create]</xsl:with-param>
                  <xsl:with-param name="value" select="@create"/>
                  <xsl:with-param name="disabled" select="$administrateright or $disabledgrouptype or $readonly"/>
                </xsl:call-template>
              </td>
            </xsl:if>
            <xsl:if test="$right_publish_available">
              <td align="center">
                <xsl:call-template name="accessright_checkbox">
                  <xsl:with-param name="name">chkAccessRight[key=<xsl:value-of select="@groupkey"/>;right=publish]</xsl:with-param>
                  <xsl:with-param name="value" select="@publish"/>
                  <xsl:with-param name="disabled" select="$administrateright or $disabledgrouptype or $readonly"/>
                </xsl:call-template>
              </td>
            </xsl:if>
            <xsl:if test="$right_update_available">
              <td align="center">
                <xsl:call-template name="accessright_checkbox">
                  <xsl:with-param name="name">chkAccessRight[key=<xsl:value-of select="@groupkey"/>;right=update]</xsl:with-param>
                  <xsl:with-param name="value" select="@update"/>
                  <xsl:with-param name="disabled" select="$administrateright or $disabledgrouptype or $readonly"/>
                </xsl:call-template>
              </td>
            </xsl:if>
            <xsl:if test="$right_delete_available">
              <td align="center">
                <xsl:call-template name="accessright_checkbox">
                  <xsl:with-param name="name">chkAccessRight[key=<xsl:value-of select="@groupkey"/>;right=delete]</xsl:with-param>
                  <xsl:with-param name="value" select="@delete"/>
                  <xsl:with-param name="disabled" select="$administrateright or $disabledgrouptype or $readonly"/>
                </xsl:call-template>
              </td>
            </xsl:if>
            <xsl:if test="$right_approve_available">
              <td align="center">
                <xsl:call-template name="accessright_checkbox">
                  <xsl:with-param name="name">chkAccessRight[key=<xsl:value-of select="@groupkey"/>;right=approve]</xsl:with-param>
                  <xsl:with-param name="value" select="@approve"/>
                  <xsl:with-param name="disabled" select="$administrateright or $disabledgrouptype or $readonly"/>
                </xsl:call-template>
              </td>
            </xsl:if>
            <xsl:if test="$right_administrate_available">
              <td align="center">
                <xsl:call-template name="accessright_checkbox">
                  <xsl:with-param name="name">chkAccessRight[key=<xsl:value-of select="@groupkey"/>;right=administrate]</xsl:with-param>
                  <xsl:with-param name="value" select="@administrate"/>
                  <xsl:with-param name="disabled" select="$disabledgrouptype or $readonly"/>
                </xsl:call-template>
              </td>
            </xsl:if>
            <td align="center">
              <xsl:if test="not($readonly)">
                <xsl:call-template name="button">
                  <xsl:with-param name="name">removeAccessRight[key=<xsl:value-of select="@groupkey"/>]</xsl:with-param>
                  <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                  <xsl:with-param name="onclick">
                    <xsl:text>javascript:handle_AccessRightRemove_onclick( this );</xsl:text>
                  </xsl:with-param>
                  <xsl:with-param name="disabled">
                    <xsl:choose>
                      <xsl:when test="$disabledgrouptype">true</xsl:when>
                      <xsl:when test="$readonly">true</xsl:when>
                      <xsl:otherwise>false</xsl:otherwise>
                    </xsl:choose>
                  </xsl:with-param>
                </xsl:call-template>
                <xsl:if test="$disabledgrouptype">
                  <script type="text/javascript" language="JavaScript">
                    var button = document.getElementById( "removeAccessRight[key=<xsl:value-of select="@groupkey"/>]" );
                    setImageButtonEnabled( button, false );
                  </script>
                </xsl:if>
              </xsl:if>
            </td>
          </tr>
        </xsl:for-each>
      </tbody>
    </table>

    <xsl:if test="not($readonly)">
    	<xsl:if test="$allowauthenticated = true()">
    		<xsl:call-template name="button">
    			<xsl:with-param name="name" select="'butAddAccesRightRow'"/>
    			<xsl:with-param name="type" select="'button'"/>
    			<xsl:with-param name="caption" select="'%cmdAdd%'"/>
    			<xsl:with-param name="onclick">
    				<xsl:text>javascript:showUserAndGroupsPopup(false, null, true);</xsl:text>
    			</xsl:with-param>
    		</xsl:call-template>
    	</xsl:if>
    	<xsl:if test="$allowauthenticated = false()">
    		<xsl:call-template name="button">
    			<xsl:with-param name="name" select="'butAddAccesRightRow'"/>
    			<xsl:with-param name="type" select="'button'"/>
    			<xsl:with-param name="caption" select="'%cmdAdd%'"/>
    			<xsl:with-param name="onclick">
    				<xsl:text>javascript:showUserAndGroupsPopup(false, null, false );</xsl:text>
    			</xsl:with-param>
    		</xsl:call-template>
    	</xsl:if>
    </xsl:if>

  </xsl:template>

  <xsl:template name="accessright_checkbox">
    <xsl:param name="name"/>
    <xsl:param name="value" select="'false'"/>
    <xsl:param name="disabled" select="false()"/>

    <input type="checkbox" name="{$name}" value="checkbox" onclick="javascript:handle_AccessRightCheckbox_onclick(this)">
      <xsl:if test="$value = 'true'">
        <xsl:attribute name="checked">true</xsl:attribute>
      </xsl:if>
      <xsl:if test="$disabled">
        <xsl:attribute name="disabled">true</xsl:attribute>
      </xsl:if>
    </input>

  </xsl:template>

</xsl:stylesheet>