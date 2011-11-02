<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
 ]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

    <xsl:output method="html"/>

	<xsl:include href="common/displayerror.xsl"/>
	<xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/readonlyvalue.xsl"/>
    <xsl:include href="common/textfield.xsl"/>
    <xsl:include href="common/textarea.xsl"/>
    <xsl:include href="common/genericheader.xsl"/>
    <xsl:include href="common/serialize.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>
	<xsl:include href="common/button.xsl"/>
    <xsl:include href="common/searchfield.xsl"/>

    <xsl:variable name="create" select="not(/data/service/@key != '')"/>

    <xsl:variable name="pageURL">
        <xsl:text>adminpage?page=</xsl:text><xsl:value-of select="$page"/>
        <xsl:text>&amp;op=browse</xsl:text>
		<xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
    </xsl:variable>

    <xsl:template match="/">
        <html>
            <head>
                <link rel="stylesheet" type="text/css" href="javascript/tab.webfx.css" />
                <link rel="stylesheet" type="text/css" href="css/admin.css"/>
                <script type="text/javascript" src="javascript/tabpane.js"></script>
                <script type="text/javascript" language="JavaScript" src="javascript/admin.js"/>
                <script type="text/javascript" language="JavaScript" src="javascript/validate.js"/>

                <script type="text/javascript" language="JavaScript">
                    var validateFields = new Array(2);
                    validateFields[0] = new Array("%fldName%", "name", validateRequired);
					validateFields[1] = new Array("%fldClass%", "class", validateRequired);

                    function validateAll(formName) {

                        var f = document.forms[formName];

                        if ( !checkAll(formName, validateFields) )
                            return;

                        f.submit();
                    }

                </script>

            </head>

            <body>
                <form name="formAdmin" method="post">
                    <xsl:attribute name="action">
						<xsl:choose>
							<xsl:when test="$create">
								<xsl:text>adminpage?page=</xsl:text>
								<xsl:value-of select="$page"/>
								<xsl:text>&amp;op=create</xsl:text>
								<xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>adminpage?page=</xsl:text>
								<xsl:value-of select="$page"/>
								<xsl:text>&amp;op=update</xsl:text>
								<xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/>
							</xsl:otherwise>
						</xsl:choose>
                    </xsl:attribute>
                    <input type="hidden" name="referer" value="{$pageURL}"/>

					<h1>
						<xsl:call-template name="genericheader"/>
						<a href="adminpage?page={$page}&amp;op=browse&amp;menukey={$menukey}">
							%headServices%
						</a>
			        </h1>
			        <h2>
	                    <xsl:choose>
	                        <xsl:when test="$create">
	                        	%headNew%: <span id='titlename'></span>
	                        </xsl:when>
	                        <xsl:otherwise>
								%headEdit%: <span id='titlename'><xsl:value-of select="/data/service/name"/></span>
	                        </xsl:otherwise>
	                    </xsl:choose>
	                </h2>

                    <table width="100%" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                            <td class="form_title_form_seperator"><img src="/images/1x1.gif"/></td>
                        </tr>
                        <tr>
                            <td>
                                <div class="tab-pane" id="tab-pane-1">
									<script type="text/javascript" language="JavaScript">
										var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
									</script>

									<div class="tab-page" id="tab-page-1">
										<span class="tab">%blockService%</span>

										<script type="text/javascript" language="JavaScript">
											tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
										</script>

										<fieldset>
											<legend>&nbsp;%blockGeneral%&nbsp;</legend>
											<table width="100%" cellspacing="0" cellpadding="2" border="0">
												<tr>
													<td class="form_labelcolumn"></td>
												</tr>
												<xsl:if test="not($create)">
													<input type="hidden" name="key" value="{/data/service/@key}"/>
												</xsl:if>
												<tr>
													<xsl:call-template name="textfield">
														<xsl:with-param name="name" select="'name'"/>
														<xsl:with-param name="label" select="'%fldName%:'"/>
														<xsl:with-param name="selectnode" select="/data/service/name"/>
														<xsl:with-param name="size" select="'60'"/>
														<xsl:with-param name="maxlength" select="'100'"/>
														<xsl:with-param name="colspan" select="'1'"/>
														<xsl:with-param name="onkeyup">javascript:document.getElementById('titlename').innerHTML = this.value;</xsl:with-param>
														<xsl:with-param name="required" select="'true'"/>
													</xsl:call-template>
												</tr>
												<tr>
													<xsl:call-template name="textarea">
														<xsl:with-param name="name" select="'description'"/>
														<xsl:with-param name="label" select="'%fldDescription%:'"/>
														<xsl:with-param name="selectnode" select="/data/service/description"/>
														<xsl:with-param name="rows" select="'5'"/>
														<xsl:with-param name="cols" select="'60'"/>
														<xsl:with-param name="colspan" select="'1'"/>
													</xsl:call-template>
												</tr>
												<tr>
													<xsl:variable name="selectnode">
														<xsl:choose>
															<xsl:when test="/data/service/@encoding">
																<xsl:value-of select="/data/service/@encoding"/>
															</xsl:when>
															<xsl:otherwise>
																<xsl:text>UTF-8</xsl:text>
															</xsl:otherwise>
														</xsl:choose>
													</xsl:variable>
													<xsl:call-template name="textfield">
														<xsl:with-param name="name" select="'encoding'"/>
														<xsl:with-param name="label" select="'%fldEncoding%:'"/>
														<xsl:with-param name="selectnode" select="$selectnode"/>
														<xsl:with-param name="size" select="'60'"/>
														<xsl:with-param name="maxlength" select="'100'"/>
														<xsl:with-param name="colspan" select="'1'"/>
													</xsl:call-template>
												</tr>
											</table>
										</fieldset>
										<br/>
										<fieldset>
											<legend>&nbsp;%blockXlet%&nbsp;</legend>
											<table width="100%" cellspacing="0" cellpadding="2" border="0">
												<tr>
													<xsl:call-template name="textfield">
														<xsl:with-param name="name" select="'class'"/>
														<xsl:with-param name="label" select="'%fldClass%:'"/>
														<xsl:with-param name="selectnode" select="/data/service/class"/>
														<xsl:with-param name="size" select="'60'"/>
														<xsl:with-param name="maxlength" select="'100'"/>
														<xsl:with-param name="colspan" select="'1'"/>
														<xsl:with-param name="required" select="'true'"/>
													</xsl:call-template>
												</tr>
												<tr>
													<xsl:call-template name="textfield">
														<xsl:with-param name="name" select="'namespace'"/>
														<xsl:with-param name="label" select="'%fldSessionNamespace%:'"/>
														<xsl:with-param name="selectnode" select="/data/service/@namespace"/>
														<xsl:with-param name="size" select="'60'"/>
														<xsl:with-param name="maxlength" select="'100'"/>
														<xsl:with-param name="colspan" select="'1'"/>
													</xsl:call-template>
												</tr>
												<tr>
													<xsl:variable name="selectnode">
														<xsl:choose>
															<xsl:when test="/data/service/data/parameters">
																<xsl:call-template name="serialize">
																	<xsl:with-param name="xpath" select="/data/service/data/parameters"/>
																	<xsl:with-param name="include-self" select="true()"/>
																</xsl:call-template>
															</xsl:when>
															<xsl:otherwise>
																<xsl:text>&lt;parameters/&gt;</xsl:text>
															</xsl:otherwise>
														</xsl:choose>
													</xsl:variable>
													<xsl:call-template name="textarea">
														<xsl:with-param name="name" select="'parameters'"/>
														<xsl:with-param name="label" select="'%fldParameters%:'"/>
														<xsl:with-param name="selectnode" select="$selectnode"/>
														<xsl:with-param name="rows" select="'12'"/>
														<xsl:with-param name="cols" select="'80'"/>
														<xsl:with-param name="colspan" select="'1'"/>
														<xsl:with-param name="width" select="'100%'"/>
													</xsl:call-template>
												</tr>
												<tr>
													<xsl:variable name="selectnode">
														<xsl:choose>
															<xsl:when test="/data/service/data/config">
																<xsl:call-template name="serialize">
																	<xsl:with-param name="xpath" select="/data/service/data/config"/>
																	<xsl:with-param name="include-self" select="true()"/>
																</xsl:call-template>
															</xsl:when>
															<xsl:otherwise>
																<xsl:text>&lt;config/&gt;</xsl:text>
															</xsl:otherwise>
														</xsl:choose>
													</xsl:variable>
													<xsl:call-template name="textarea">
														<xsl:with-param name="name" select="'config'"/>
														<xsl:with-param name="label" select="'%fldConfig%:'"/>
														<xsl:with-param name="selectnode" select="$selectnode"/>
														<xsl:with-param name="rows" select="'12'"/>
														<xsl:with-param name="cols" select="'80'"/>
														<xsl:with-param name="colspan" select="'1'"/>
														<xsl:with-param name="width" select="'100%'"/>
													</xsl:call-template>
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
                            <td class="form_form_buttonrow_seperator"><img src="/images/1x1.gif"/></td>
                        </tr>
                        <tr>
                            <td>
                                <xsl:call-template name="button">
                                    <xsl:with-param name="type" select="'button'"/>
                                    <xsl:with-param name="caption" select="'%cmdSave%'"/>
                                    <xsl:with-param name="name" select="'lagre'"/>
                                    <xsl:with-param name="onclick">
                                        <xsl:text>javascript:validateAll('formAdmin');</xsl:text>
                                    </xsl:with-param>
                                </xsl:call-template>

                                <xsl:text>&nbsp;</xsl:text>

								<xsl:call-template name="button">
				                	<xsl:with-param name="type" select="'cancel'"/>
				                    <xsl:with-param name="referer" select="$pageURL"/>
                                </xsl:call-template>
                            </td>
                        </tr>
                    </table>
                </form>
            </body>
        </html>
    </xsl:template>


</xsl:stylesheet>