<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:param name="creatednewversion" select="'false'"/>

	<xsl:template name="versionform">

    <script type="text/javascript" src="javascript/admin.js">//</script>

		<script type="text/javascript" language="JavaScript">

			function changeVersion(versionKey) {
				document.versionForm.versionkey.value = versionKey;
				document.versionForm.submit();
			}

			function deleteVersion(versionKey) {
				if (confirm("%alertDeleteVersion%")) {
					AjaxService.deleteContentVersion(versionKey, {
						callback:function(error) {
					 	   handleDeleteVersion(error, versionKey);
						}
					});
				}
			}

 			function handleDeleteVersion(error, versionKey) {
 				if (error != undefined) {
					alert(error);
				}
				else {
					displayFeedback("%feedbackVersionDeleted%");
					removeVersionRow(versionKey);
				}
			}

			function removeVersionRow(versionKey) {
				var row = document.getElementById("row-"+versionKey);
				if (row != undefined) {
					row.style.display = "none";
				}
				else {
					alert("Row not found: row-"+versionKey);
				}
			}

		</script>

		<form name="versionForm" method="get" action="adminpage">
			<input type="hidden" name="op" value="form"/>
			<input type="hidden" name="page" value="{$page}"/>
			<input type="hidden" name="cat" value="{/contents[1]/content[1]/categoryname/@key}"/>
			<input type="hidden" name="referer" value="{$referer}"/>
			<input type="hidden" name="key" value="{/contents[1]/content[1]/@key}"/>
			<input type="hidden" name="versionkey"/>
			<input type="hidden" name="selectedtabpage" value="tab-page-version"/>
		</form>

	</xsl:template>
	
  <xsl:template name="versionpage">

    <xsl:variable name="hasversions" select="count(/contents[1]/content[1]/versions/version) > 1"/>
		
		<!--xsl:if test="$hasversions"-->
			<div class="tab-page" id="tab-page-version">
				<span class="tab">%blockVersions%</span>
				<script type="text/javascript" language="JavaScript">
					tabPane1.addTabPage( document.getElementById( "tab-page-version" ) );
				</script>
				
        <!-- Working Version -->
        <xsl:call-template name="versionTable">
          <xsl:with-param name="legendText" select="'%blockDrafts%'"/>
          <xsl:with-param name="nodeSet" select="/contents[1]/content[1]/versions/version[@state = 0 or @state = 1]"/>
        </xsl:call-template>

        <!-- Approved Version -->
        <xsl:call-template name="versionTable">
          <xsl:with-param name="legendText" select="'%blockApprovedVersion%'"/>
          <xsl:with-param name="nodeSet" select="/contents[1]/content[1]/versions/version[@current = 'true' and @status = 2]"/>
        </xsl:call-template>

        <!-- History -->
        <xsl:call-template name="versionTable">
          <xsl:with-param name="legendText" select="'%blockHistory%'"/>
          <xsl:with-param name="nodeSet" select="/contents[1]/content[1]/versions/version[ ( @status = 2 and not(@current = 'true') ) or @status = 3 ]"/>
        </xsl:call-template>
      </div>
    <!--/xsl:if-->
  </xsl:template>

  <xsl:template name="versionTable">
    <xsl:param name="legendText"/>
    <xsl:param name="nodeSet"/>
    <fieldset>
      <legend><xsl:value-of select="$legendText"/></legend>
      <xsl:choose>
        <xsl:when test="count($nodeSet) &gt; 0">
          <table width="100%" border="0" cellspacing="0" cellpadding="3">
            <tr>
              <td width="30">&nbsp;</td>
              <td width="100"><b>%fldCreated%</b></td>
              <td><b>%fldTitle%</b></td>
              <td width="100"><b>%fldModified%</b></td>
              <td width="150"><b>%fldModifiedBy%</b></td>
              <td width="50">&nbsp;</td>
            </tr>
            <xsl:apply-templates select="$nodeSet" mode="versionfilter">
              <xsl:sort select="@created" order="descending"/>
            </xsl:apply-templates>
          </table>
        </xsl:when>
        <xsl:otherwise>%txtNone%</xsl:otherwise>
      </xsl:choose>
    </fieldset>
  </xsl:template>

  <xsl:template match="version" mode="versionfilter">

    <xsl:variable name="current" select="@current = 'true'"/>
    <xsl:variable name="selected" select="/contents[1]/content[1]/@versionkey = @key"/>

    <tr>
      <xsl:attribute name="id">row-<xsl:value-of select="@key"/></xsl:attribute>
      <xsl:attribute name="onmouseover">javascript:this.className='background_white'</xsl:attribute>
      <xsl:attribute name="onmouseout">javascript:this.className=''</xsl:attribute>
      <xsl:if test="$creatednewversion = 'true' and $selected">
        <xsl:attribute name="bgcolor">white</xsl:attribute>
      </xsl:if>
      <xsl:if test="$selected">
        <xsl:attribute name="style">font-weight: bold;</xsl:attribute>
      </xsl:if>

      <!-- status icon column -->
      <td align="center">
        <xsl:call-template name="_addCommonCellAttributes">
          <xsl:with-param name="selected" select="$selected"/>
        </xsl:call-template>

        <xsl:call-template name="publishstatus">
          <xsl:with-param name="state" select="@state"/>
          <xsl:with-param name="publishfrom" select="@publishfrom"/>
          <xsl:with-param name="publishto" select="@publishto"/>
        </xsl:call-template>
      </td>

      <!-- created date column -->
      <td style="white-space: nowrap">
        <xsl:call-template name="_addCommonCellAttributes">
          <xsl:with-param name="selected" select="$selected"/>
        </xsl:call-template>

        <xsl:call-template name="formatdatetime">
          <xsl:with-param name="date" select="@created"/>
        </xsl:call-template>
      </td>

      <!-- title column -->
      <td>
        <xsl:call-template name="_addCommonCellAttributes">
          <xsl:with-param name="selected" select="$selected"/>
        </xsl:call-template>

        <xsl:value-of select="@title"/>
      </td>

      <!-- modified column -->
      <td style="white-space: nowrap">
        <xsl:call-template name="_addCommonCellAttributes">
          <xsl:with-param name="selected" select="$selected"/>
        </xsl:call-template>

        <xsl:call-template name="formatdatetime">
          <xsl:with-param name="date" select="@timestamp"/>
        </xsl:call-template>
      </td>

      <!-- modified by column -->
      <td style="white-space: nowrap">
        <xsl:call-template name="_addCommonCellAttributes">
          <xsl:with-param name="selected" select="$selected"/>
        </xsl:call-template>


        <xsl:value-of select="@modifierFullName"/>
      </td>

      <!-- action buttons column -->
      <td style="white-space: nowrap">
        <xsl:choose>
          <xsl:when test="$selected">
            <xsl:attribute name="title">
              <xsl:value-of select="concat('%msgWorkingVersion% (', @key, ')')"/>
            </xsl:attribute>
            <img src="images/icon_edit.gif" border="0" style="filter: alpha(opacity=30);opacity:.3">
              <xsl:attribute name="title">
                <xsl:value-of select="concat('%msgWorkingVersion% (', @key, ')')"/>
              </xsl:attribute>
            </img>
          </xsl:when>
          <xsl:otherwise>
            <a href="javascript:changeVersion({@key})" id="operation_edit_{@key}" title="%msgClickToSelectVersion%">
              <xsl:attribute name="onclick">
                <xsl:text>javascript:changeVersion(</xsl:text>
                <xsl:value-of select="@key"/>
                <xsl:text>);</xsl:text>
              </xsl:attribute>
              <img src="images/icon_edit.gif" border="0"/>
            </a>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:text>&nbsp;</xsl:text>
        <xsl:choose>
          <xsl:when test="($current or @state > 1 or $selected)">
            <img src="images/icon_delete.gif" border="0" style="filter: alpha(opacity=30);opacity:.3">
              <xsl:choose>
                <xsl:when test="$current">
                  <xsl:attribute name="title">%msgCannotDeleteCurrentVersion%</xsl:attribute>
                </xsl:when>
                <xsl:when test="@state > 1">
                  <xsl:attribute name="title">%msgCannotDeletePublishedVersion%</xsl:attribute>
                </xsl:when>
                <xsl:when test="$selected">
                  <xsl:attribute name="title">%msgCannotDeleteSelectedVersion%</xsl:attribute>
                </xsl:when>
                <xsl:otherwise/>
              </xsl:choose>
            </img>
          </xsl:when>
          <xsl:otherwise>
            <a href="#" title="%msgClickToDeleteVersion%">
              <xsl:attribute name="onclick">
                <xsl:text>javascript:deleteVersion(</xsl:text>
                <xsl:value-of select="@key"/>
                <xsl:text>);</xsl:text>
              </xsl:attribute>
              <img src="images/icon_delete.gif" border="0"/>
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="_addCommonCellAttributes">
    <xsl:param name="selected"/>

    <xsl:variable name="rowToolTip">
      <xsl:choose>
        <xsl:when test="$selected">
          <xsl:value-of select="concat('%msgWorkingVersion% (', @key, ')')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="concat('%msgClickToSelectVersion% (', @key, ')')"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:attribute name="title">
      <xsl:value-of select="$rowToolTip"/>
    </xsl:attribute>

    <xsl:if test="not($selected)">
      <xsl:call-template name="addJSEvent">
        <xsl:with-param name="key" select="@key"/>
      </xsl:call-template>
      <xsl:attribute name="class">button_pointer</xsl:attribute>
    </xsl:if>

  </xsl:template>

</xsl:stylesheet>