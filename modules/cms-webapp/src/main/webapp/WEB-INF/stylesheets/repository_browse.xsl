<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

    <xsl:output method="html"/>


    <xsl:include href="common/accesslevel_parameters.xsl"/>

    <xsl:include href="common/generic_parameters.xsl" />
    <xsl:include href="common/operations_template.xsl" />
    <xsl:include href="common/javascriptPreload.xsl" />
    <xsl:include href="common/genericheader.xsl" />
    <xsl:include href="common/formatdate.xsl"/>
    <xsl:include href="common/tablecolumnheader.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/tablerowpainter.xsl"/>
    <xsl:include href="common/browse_table_js.xsl"/>


  <xsl:param name="op"/>
	<xsl:param name="subop"/>
	<xsl:param name="fieldname"/>
	<xsl:param name="fieldrow"/>
	<xsl:param name="contenttypestring"/>

    <xsl:param name="reload"/>
    <xsl:param name="mainframe"/>

    <xsl:param name="sortby" select="'name'"/>
    <xsl:param name="sortby-direction" select="'ascending'"/>
    <xsl:param name="minoccurrence"/>
    <xsl:param name="maxoccurrence"/>
    <xsl:param name="contenthandler"/>

	<xsl:variable name="disabled" select="$subop and $subop != '' and $subop != 'browse'"/>

    <xsl:variable name="pageURL">
        <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
        <xsl:text>&amp;op=browse</xsl:text>
		<xsl:if test="$subop">
			<xsl:text>&amp;subop=</xsl:text>
		    <xsl:value-of select="$subop"/>
		</xsl:if>
		<xsl:if test="$fieldname">
			<xsl:text>&amp;fieldname=</xsl:text>
			<xsl:value-of select="$fieldname"/>
    </xsl:if>
		<xsl:if test="$fieldrow">
			<xsl:text>&amp;fieldrow=</xsl:text>
			<xsl:value-of select="$fieldrow"/>
		</xsl:if>
		<xsl:if test="$contenttypestring">
			<xsl:text>&amp;contenttypestring=</xsl:text>
			<xsl:value-of select="$contenttypestring"/>
		</xsl:if>
		<xsl:if test="$disabled">
			<xsl:text>&amp;disabled=true</xsl:text>
		</xsl:if>
		<xsl:if test="$minoccurrence">
			<xsl:text>&amp;minoccurrence=</xsl:text>
      <xsl:value-of select="$minoccurrence"/>
		</xsl:if>
		<xsl:if test="$maxoccurrence">
			<xsl:text>&amp;maxoccurrence=</xsl:text>
      <xsl:value-of select="$maxoccurrence"/>
		</xsl:if>
    </xsl:variable>

    <xsl:template match="/">
        <xsl:call-template name="unitbrowse"/>
    </xsl:template>

    <xsl:template name="unitbrowse">

        <html>

            <head>

                <xsl:if test="$subop = 'relatedcontent' or
                                  $subop = 'relatedfiles' or
                                  $subop = 'relatedfile' or
                                  $subop = 'relatedimages' or
                                  $subop = 'relatedimage' or
                                  $subop = 'insert' or
                                  $subop = 'contentfield'">
                  <script type="text/javascript" src="javascript/window.js"/>
                  <script type="text/javascript">
                    cms.window.attatchKeyEvent('close');
                  </script>
                </xsl:if>

                <script type="text/javascript" language="JavaScript">
                    function reload(which,mainFrame) {
                      if ("page" == which)
                        top.location.href = "adminpage?page=0&amp;mainframe=" + mainFrame;
                      else if ("navigator" == which)
                        top.document.frames["topFrame"].location.href = "adminpage?page=1";
                      else if ("menu" == which)
                        parent['leftFrame'].location.href = <xsl:text>"adminpage?page=2&amp;op=browse";</xsl:text>
                    }

                </script>

                <link href="css/admin.css" rel="stylesheet" type="text/css"/>
				<script type="text/javascript" src="javascript/admin.js">//</script>

            </head>

            <body>

                <xsl:attribute name="onload">
                    <xsl:choose>
                        <xsl:when test="$reload = 'page'">
                            <xsl:text> reload("page", "</xsl:text>
                            <xsl:value-of select="$mainframe"/>
                            <xsl:text>");</xsl:text>
                        </xsl:when>
                        <xsl:when test="$reload != ''">
                            <xsl:call-template name="reloadfunction">
                                <xsl:with-param name="which" select="$reload"/>
                            </xsl:call-template>
                        </xsl:when>
                    </xsl:choose>
                </xsl:attribute>

                <xsl:if test="$reload = 'true'">
                    <script type="text/javascript" language="JavaScript">
                        window.top.frames['leftFrame'].refreshMenu();
                    </script>
                </xsl:if>
                <h1>
					<a href="{$pageURL}">
                        <xsl:text>%headContentRepositories%</xsl:text>
                    </a>
                </h1>

                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <tr>
                            <td class="browse_title_buttonrow_seperator"><img src="images/1x1.gif"/></td>
                        </tr>
                    </tr>
					<form id="formSearch" name="formSearch" method="get" action="adminpage" style="margin-bottom:0;">
						<tr>
							<td>
                

								<xsl:if test="$siteadmin = 'true' and not($disabled)">
									<xsl:call-template name="button">
										<xsl:with-param name="type" select="'link'"/>
										<xsl:with-param name="caption" select="'%cmdNew%'"/>
										<xsl:with-param name="href">
											<xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
											<xsl:text>&amp;op=form</xsl:text>
										</xsl:with-param>
									</xsl:call-template>

									<xsl:text>&nbsp;</xsl:text>
								</xsl:if>

								<input type="hidden" name="op" value="browse"/>
								<input type="hidden" name="subop" value="{$subop}"/>
								<input type="hidden" name="page" value="991"/>
								<input type="hidden" name="fieldname" value="{$fieldname}"/>
								<input type="hidden" name="fieldrow" value="{$fieldrow}"/>
								<input type="hidden" name="contenttypestring" value="{$contenttypestring}"/>
								<input type="hidden" name="searchtype" value="simple"/>
								<input type="hidden" name="scope" value="title"/>
								<input type="hidden" name="waitscreen" value="true"/>
								<input type="hidden" name="minoccurrence" value="{$minoccurrence}"/>
								<input type="hidden" name="maxoccurrence" value="{$maxoccurrence}"/>
								<input type="hidden" name="contenthandler" value="{$contenthandler}"/>

								<!-- Search field -->
								<input type="text" id="searchtext" name="searchtext" size="12" style="height: 20px"/>

								<!-- Search button -->
								<xsl:call-template name="button">
									<xsl:with-param name="type" select="'submit'"/>
									<xsl:with-param name="caption" select="'%cmdQuickSearch%'"/>
									<xsl:with-param name="name" select="'search'"/>
								</xsl:call-template>

								<xsl:text>&nbsp;</xsl:text>

								<!-- Advanced search button -->
								<xsl:call-template name="button">
									<xsl:with-param name="type" select="'link'"/>
									<xsl:with-param name="caption" select="'%cmdSearchDotDotDot%'"/>
									<xsl:with-param name="href">
										<xsl:text>adminpage?page=991</xsl:text>
										<xsl:text>&amp;op=searchform</xsl:text>
										<xsl:text>&amp;subop=</xsl:text><xsl:value-of select="$subop"/>
										<xsl:text>&amp;cat=</xsl:text>
										<xsl:value-of select="$cat"/>
										<xsl:text>&amp;selectedunitkey=</xsl:text>
										<xsl:value-of select="$selectedunitkey"/>
										<xsl:text>&amp;fieldname=</xsl:text><xsl:value-of select="$fieldname"/>
										<xsl:text>&amp;fieldrow=</xsl:text><xsl:value-of select="$fieldrow"/>
										<xsl:text>&amp;contenttypestring=</xsl:text><xsl:value-of select="$contenttypestring"/>
                    <xsl:if test="$minoccurrence">
                      <xsl:text>&amp;minoccurrence=</xsl:text>
                      <xsl:value-of select="$minoccurrence"/>
                    </xsl:if>
                    <xsl:if test="$maxoccurrence">
                      <xsl:text>&amp;maxoccurrence=</xsl:text>
                      <xsl:value-of select="$maxoccurrence"/>
                    </xsl:if>
                    <xsl:if test="$contenthandler">
                      <xsl:text>&amp;contenthandler=</xsl:text>
                      <xsl:value-of select="$contenthandler"/>
                    </xsl:if>
									</xsl:with-param>
								</xsl:call-template>

								<xsl:text>&nbsp;</xsl:text>

								<!-- Create content wizard button -->
								<xsl:call-template name="button">
									<xsl:with-param name="type" select="'link'"/>
									<xsl:with-param name="caption" select="'%createContentWizard%'"/>
									<xsl:with-param name="href">
										<xsl:text>adminpage?page=960&amp;op=createcontentwizard_step1&amp;source=archives</xsl:text>
										<xsl:if test="$subop">
											<xsl:text>&amp;subop=</xsl:text>
											<xsl:value-of select="$subop"/>
										</xsl:if>
										<xsl:if test="$fieldrow">
											<xsl:text>&amp;fieldrow=</xsl:text>
											<xsl:value-of select="$fieldrow"/>
										</xsl:if>
										<xsl:if test="$fieldname">
											<xsl:text>&amp;fieldname=</xsl:text>
											<xsl:value-of select="$fieldname"/>
										</xsl:if>
										<xsl:if test="$minoccurrence">
											<xsl:text>&amp;minoccurrence=</xsl:text>
											<xsl:value-of select="$minoccurrence"/>
										</xsl:if>
										<xsl:if test="$maxoccurrence">
											<xsl:text>&amp;maxoccurrence=</xsl:text>
											<xsl:value-of select="$maxoccurrence"/>
										</xsl:if>
                    <xsl:if test="$contenthandler">
                      <xsl:text>&amp;contenthandler=</xsl:text>
                      <xsl:value-of select="$contenthandler"/>
                    </xsl:if>
									</xsl:with-param>
								</xsl:call-template>
							</td>
						</tr>
					</form>
					<xsl:if test="not($disabled)">
						<tr>
							<tr>
								<td class="browse_buttonrow_datarows_seperator"><img src="images/1x1.gif"/></td>
							</tr>
						</tr>
						<tr>
							<td>
								<table width="100%" border="0" cellspacing="0" cellpadding="4" class="browsetable">
									<tr>

										<xsl:call-template name="tablecolumnheader">
											<xsl:with-param name="caption" select="'%fldName%'" />
											<xsl:with-param name="pageURL" select="$pageURL" />
											<xsl:with-param name="sortable" select="'false'" />
										</xsl:call-template>

										<xsl:call-template name="tablecolumnheader">
											<xsl:with-param name="caption" select="'%fldDefaultLanguage%'" />
											<xsl:with-param name="pageURL" select="$pageURL" />
											<xsl:with-param name="sortable" select="'false'" />
										</xsl:call-template>

										<xsl:call-template name="tablecolumnheader">
											<xsl:with-param name="width" select="'30'" />
											<xsl:with-param name="caption" select="''" />
											<xsl:with-param name="sortable" select="'false'" />
										</xsl:call-template>
									</tr>

									<xsl:variable name="sortby-data-type">text</xsl:variable>

									<xsl:for-each select="/unitnames/unitname">
										<xsl:sort data-type="{$sortby-data-type}" order="{$sortby-direction}" select="*[name() = $sortby] | @*[concat('@',name()) = $sortby]"/>
										<tr>
											<xsl:call-template name="tablerowpainter"/>

                      <xsl:variable name="className">
                        <xsl:text>browsetablecell</xsl:text>
                        <xsl:if test="position() = last()">
                          <xsl:text> row-last</xsl:text>
                        </xsl:if>
                      </xsl:variable>



                      <td class="{$className}">
												<xsl:if test="$siteadmin = 'true'">
													<xsl:attribute name="title">%msgClickToEdit%</xsl:attribute>
													<xsl:call-template name="addJSEvent">
														<xsl:with-param name="key" select="@key"/>
													</xsl:call-template>
												</xsl:if>
												<xsl:value-of select="@categoryname"/>
											</td>
											<td class="{$className}">
												<xsl:if test="$siteadmin = 'true'">
													<xsl:attribute name="title">%msgClickToEdit%</xsl:attribute>
													<xsl:call-template name="addJSEvent">
														<xsl:with-param name="key" select="@key"/>
													</xsl:call-template>
												</xsl:if>
												<xsl:value-of select="@language"/>
											</td>
											<td align="center" class="{$className}">
												<xsl:if test="$siteadmin = 'true'">
													<xsl:call-template name="operations">
														<xsl:with-param name="page" select="$page"/>
														<xsl:with-param name="key" select="@key"/>
														<xsl:with-param name="includecopy" select="'false'"/>
														<xsl:with-param name="copywarning" select="true()"/>
														<xsl:with-param name="includedelete" select="false()"/>
														<xsl:with-param name="includeparams">
															<xsl:text>&amp;categorykey=</xsl:text>
															<xsl:value-of select="@categorykey"/>
														</xsl:with-param>
													</xsl:call-template>
												</xsl:if>
											</td>
										</tr>
									</xsl:for-each>
								</table>
							</td>
						</tr>
					</xsl:if>
                </table>

				<script>
					var searchField = document.getElementById('searchtext');
          if (searchField)
						searchField.focus();
				</script>

            </body>

        </html>

    </xsl:template>

    <xsl:template name="reloadfunction">
        <xsl:param name="which" select="''"/>

        <xsl:choose>
            <xsl:when test="contains($which, ',')">
                <xsl:text> reload("</xsl:text>
                <xsl:value-of select="substring-before($which, ',')"/>
                <xsl:text>", "");</xsl:text>
                <xsl:call-template name="reloadfunction">
                    <xsl:with-param name="which" select="substring-after($which, ',')"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$which != ''">
                <xsl:text> reload("</xsl:text>
                <xsl:value-of select="$which"/>
                <xsl:text>", "");</xsl:text>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>