<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
 ]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html"/>

	<xsl:include href="common/button.xsl"/>
	<xsl:include href="common/generic_parameters.xsl"/>
	<xsl:include href="menu/header.xsl"/>

	<xsl:param name="key"/>
	<xsl:param name="versionkey"/>
	<xsl:param name="referer"/>
	<xsl:param name="page"/>
	<xsl:param name="contenttitle"/>
	<xsl:param name="notify"/>
	<xsl:param name="subop"/>
	<xsl:param name="fieldname"/>
    <xsl:param name="fieldrow"/>
	<xsl:param name="contenttypestring"/>
	<xsl:param name="selectedtabpage"/>
	<xsl:param name="feedback"/>

	<xsl:template match="/">

		<html>
			<head>
				<link type="text/css" rel="stylesheet" href="css/admin.css"/>
				<link type="text/css" rel="stylesheet" href="css/menu.css"/>
				<link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>

				<script type="text/javascript" language="JavaScript" src="javascript/admin.js"/>
				<script type="text/javascript" language="JavaScript" src="javascript/tabpane.js"/>
				<script type="text/javascript" language="JavaScript" src="javascript/validate.js"/>

				<script type="text/javascript" language="JavaScript">
					var validatedFields = new Array(0);
					<xsl:if test="not($notify = 'rejected')">
					validatedFields[0] = new Array("%blockRecipients%", "recipientkeys", validateAtLeastOne);
					</xsl:if>

					function validateAll(formName) {
						var f = document.forms[formName];

						if (!checkAll(formName, validatedFields))
							return;

						f.submit();
					}
				</script>

			</head>
			<body>
				<h1>
					<xsl:variable name="url">
						<xsl:text>adminpage?op=browse</xsl:text>
					</xsl:variable>

                    <xsl:apply-templates select="node()/site">
						<xsl:with-param name="url" select="$url"/>
					</xsl:apply-templates>
                </h1>
                <h2>%headNotify%</h2>

                <form method="get" action="adminpage" name="formAdmin">
                	<input type="hidden" name="page" value="{$page}"/>
					<input type="hidden" name="op" value="notify"/>
					<input type="hidden" name="referer" value="{$referer}"/>
					<input type="hidden" name="sendmail" value="true"/>

					<input type="hidden" name="key" value="{$key}"/>
					<input type="hidden" name="versionkey" value="{$versionkey}"/>
					<input type="hidden" name="cat" value="{$cat}"/>
					<input type="hidden" name="fieldname" value="{$fieldname}"/>
					<input type="hidden" name="fieldrow" value="{$fieldrow}"/>
					<input type="hidden" name="contenttypestring" value="{$contenttypestring}"/>
					<input type="hidden" name="subop" value="{$subop}"/>
					<input type="hidden" name="selectedtabpage" value="{$selectedtabpage}"/>
					<input type="hidden" name="referer" value="{$referer}"/>
					<input type="hidden" name="feedback" value="{$feedback}"/>

					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td class="form_title_form_seperator"><img src="images/1x1.gif"/></td>
						</tr>
						<tr>
							<td>

								<div class="tab-pane" id="tab-pane-1">
									<script type="text/javascript" language="JavaScript">
										var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
									</script>

									<div class="tab-page" id="tab-page-general">
										<span class="tab">%blockMessage%</span>

										<script type="text/javascript" language="JavaScript">
											tabPane1.addTabPage( document.getElementById( "tab-page-general" ) );
										</script>

										<fieldset>
											<legend>%blockRecipients%</legend>
											<table border="0" cellspacing="2" cellpadding="2">
												<xsl:for-each select="/usernames/username">
													<tr>
														<td width="20">
															<xsl:choose>
																<!-- When in "rejected mode", there is only one receiver (the owner) -->
																<xsl:when test="$notify = 'rejected'">
																	<input type="checkbox" name="dummy" checked="checked" disabled="true"/>
																	<input type="hidden" name="recipientkeys" value="{@key}"/>
																</xsl:when>
																<xsl:otherwise>
																	<input type="checkbox" name="recipientkeys" value="{@key}"/>
																</xsl:otherwise>
															</xsl:choose>
															<input type="hidden" name="name_{@key}" value="{.}"/>
															<input type="hidden" name="email_{@key}" value="{@email}"/>
														</td>
														<td width="400">
															<xsl:value-of select="."/> (<xsl:value-of select="@email"/>)
														</td>
													</tr>
												</xsl:for-each>
											</table>
										</fieldset>

										<fieldset>
											<legend>%blockMessage%</legend>
											<table border="0" cellspacing="2" cellpadding="2">
												<tr>
													<td colspan="3">%fldMessage%</td>
												</tr>
												<tr>
													<td colspan="3">
														<textarea rows="12" cols="80" name="body"/><br/><br/>
													</td>
												</tr>

											</table>
										</fieldset>
									</div>
								</div>
								<script type="text/javascript" language="JavaScript">
									setupAllTabs();
								</script>

							</td>
						</tr>

						<tr>
							<td class="form_form_buttonrow_seperator"><img src="images/1x1.gif"/></td>
						</tr>

						<tr>
							<td>
								<xsl:call-template name="button">
									<xsl:with-param name="type" select="'button'"/>
									<xsl:with-param name="caption" select="'%cmdSendEmail%'"/>
									<!--xsl:with-param name="name" select="'importbtn'"/-->
									<xsl:with-param name="onclick">
										<xsl:text>validateAll('formAdmin')</xsl:text>
									</xsl:with-param>
								</xsl:call-template>
								<xsl:text>&nbsp;</xsl:text>
								<xsl:call-template name="button">
									<xsl:with-param name="type" select="'button'"/>
									<xsl:with-param name="caption" select="'%cmdDontSendEmail%'"/>
									<!--xsl:with-param name="name" select="'importbtn'"/-->
									<xsl:with-param name="onclick">
										<xsl:text>document.formAdmin.sendmail.value='false';document.formAdmin.submit();</xsl:text>
									</xsl:with-param>
								</xsl:call-template>
							</td>
						</tr>
					</table>
				</form>
			</body>
		</html>

    </xsl:template>

</xsl:stylesheet>
