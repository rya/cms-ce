<?xml version="1.0" encoding="utf-8"?>

<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" />

	<xsl:template name="form_dummy_login">
		<xsl:param name="user" />
		<xsl:param name="wizard" />

		<xsl:variable name="oid" select="'dummy'" />
		<xsl:variable name="datablock" select="$user/block[@oid = $oid]" />

		<fieldset>
			<legend>
				&nbsp;
				%blockLoginInfo%
				&nbsp;
			</legend>
			<table border="0" cellspacing="2" cellpadding="0" width="100%">
				<tr>
					<xsl:variable name="iname" select="concat('uid_', $oid)" />

					<xsl:choose>
						<xsl:when test="$datablock/uid and not($wizard)">
							<xsl:call-template name="readonlyvalue">
                                <xsl:with-param name="name" select="$iname"/>
	                            <xsl:with-param name="label" select="'%fldUID%:'"/>
       			                <xsl:with-param name="selectnode" select="$datablock/uid"/>
	                            <xsl:with-param name="colspan" select="'1'"/>
                            </xsl:call-template>
						</xsl:when>
						<xsl:when test="$wizard">
							<!-- Don't want onpropertychange in the wizard -->
							<xsl:call-template name="textfield">
								<xsl:with-param name="label" select="'%fldUID%:'" />
								<xsl:with-param name="name" select="$iname" />
								<xsl:with-param name="selectnode" select="$datablock/uid" />
								<xsl:with-param name="readonly" select="false()" />
								<xsl:with-param name="required" select="'true'" />
                <xsl:with-param name="disableAutoComplete" select="true()" />
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="textfield">
								<xsl:with-param name="label" select="'%fldUID%:'" />
								<xsl:with-param name="name" select="$iname" />
								<xsl:with-param name="selectnode" select="$datablock/uid" />
								<xsl:with-param name="readonly" select="false()" />
								<xsl:with-param name="required" select="'true'" />
								<xsl:with-param name="onkeyup">
                  <xsl:text>javaScript: updateBreadCrumbHeader('titlename', this);</xsl:text>
                </xsl:with-param>
                <xsl:with-param name="disableAutoComplete" select="true()" />
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>

					<script type="text/javascript" language="JavaScript">
						<xsl:text>validatedFields[idx] = new Array("%fldUID%", "</xsl:text>
						<xsl:value-of select="$iname" />
						<xsl:text>", validateRequired);</xsl:text>
						++idx;
					</script>
				</tr>

				<xsl:if test="$create = 1">
					<tr>
						<xsl:variable name="iname" select="concat('password_', $oid)" />

						<xsl:choose>
							<xsl:when test="not($wizard)">
								<xsl:call-template name="passwordfield">
									<xsl:with-param name="label" select="'%fldPassword%:'" />
									<xsl:with-param name="name" select="$iname" />
									<xsl:with-param name="selectnode" select="$datablock/password" />
									<xsl:with-param name="required" select="'true'" />
                  <xsl:with-param name="disableAutoComplete" select="true()" />
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="textfield">
									<xsl:with-param name="label" select="'%fldPassword%:'" />
									<xsl:with-param name="name" select="$iname" />
									<xsl:with-param name="selectnode" select="$datablock/password" />
									<xsl:with-param name="required" select="'true'" />
                  <xsl:with-param name="disableAutoComplete" select="true()" />
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>

						<script type="text/javascript" language="JavaScript">
							<xsl:text>validatedFields[idx] = new Array("%fldPassword%", "</xsl:text>
							<xsl:value-of select="$iname" />
							<xsl:text>", validateRequired);</xsl:text>
							++idx;
						</script>
					</tr>
					<xsl:if test="not($wizard)">
						<tr>
							<xsl:variable name="iname" select="concat('password2_', $oid)" />
	
							<xsl:call-template name="passwordfield">
								<xsl:with-param name="label" select="'%fldRepeatPassword%:'" />
								<xsl:with-param name="name" select="concat('password2_', $oid)" />
								<xsl:with-param name="selectnode" select="$datablock/password" />
								<xsl:with-param name="required" select="'true'" />
                <xsl:with-param name="disableAutoComplete" select="true()" />
							</xsl:call-template>
	
							<script type="text/javascript" language="JavaScript">
								<xsl:text>validatedFields[idx] = new Array("%fldRepeatPassword%", "</xsl:text>
								<xsl:value-of select="$iname" />
								<xsl:text>", validateRequired);</xsl:text>
								++idx;
							</script>
						</tr>
					</xsl:if>
				</xsl:if>
			</table>
		</fieldset>
	</xsl:template>

	<xsl:template name="form_dummy_general">
		<xsl:param name="user" />

		<xsl:variable name="oid" select="'dummy'" />
		<xsl:variable name="datablock" select="$user/block[@oid = $oid]" />

		<fieldset>
			<legend>
				&nbsp;
				%blockGeneral%
				&nbsp;
			</legend>
			<table border="0" cellspacing="2" cellpadding="0" width="100%">
				<tr>
					<xsl:variable name="iname" select="concat('firstname_', $oid)" />

					<xsl:call-template name="textfield">
						<xsl:with-param name="label" select="'%fldFirstname%:'" />
						<xsl:with-param name="name" select="$iname" />
						<xsl:with-param name="selectnode" select="$datablock/firstname" />
						<xsl:with-param name="required" select="'true'" />
					</xsl:call-template>

					<script type="text/javascript" language="JavaScript">
						<xsl:text>validatedFields[idx] = new Array("%fldFirstname%", "</xsl:text>
						<xsl:value-of select="$iname" />
						<xsl:text>", validateRequired);</xsl:text>
						++idx;
					</script>
				</tr>
				<tr>
					<xsl:variable name="iname" select="concat('surname_', $oid)" />

					<xsl:call-template name="textfield">
						<xsl:with-param name="label" select="'%fldSurname%:'" />
						<xsl:with-param name="name" select="$iname" />
						<xsl:with-param name="selectnode" select="$datablock/surname" />
						<xsl:with-param name="required" select="'true'" />
					</xsl:call-template>

					<script type="text/javascript" language="JavaScript">
						<xsl:text>validatedFields[idx] = new Array("%fldSurname%", "</xsl:text>
						<xsl:value-of select="$iname" />
						<xsl:text>", validateRequired);</xsl:text>
						++idx;
					</script>
				</tr>

				<tr>
					<xsl:variable name="iname" select="concat('email_', $oid)" />

					<xsl:call-template name="textfield">
						<xsl:with-param name="label" select="'%fldEmail%:'" />
						<xsl:with-param name="name" select="$iname" />
						<xsl:with-param name="selectnode" select="$datablock/email" />
						<xsl:with-param name="required" select="'true'" />
					</xsl:call-template>

					<script type="text/javascript" language="JavaScript">
						<xsl:text>validatedFields[idx] = new Array("%fldEmail%", "</xsl:text>
						<xsl:value-of select="$iname" />
						<xsl:text>", validateRequired);</xsl:text>
						++idx;
						<xsl:text>validatedFields[idx] = new Array("%fldEmail%", "</xsl:text>
						<xsl:value-of select="$iname" />
						<xsl:text>", validateEmail);</xsl:text>
						++idx;
					</script>
				</tr>

				<tr>
					<xsl:variable name="iname" select="concat('streetaddress_', $oid)" />

					<xsl:call-template name="textfield">
						<xsl:with-param name="label" select="'%fldAddress%:'" />
						<xsl:with-param name="name" select="$iname" />
						<xsl:with-param name="selectnode" select="$datablock/streetaddress" />
					</xsl:call-template>
				</tr>

				<tr>
					<xsl:variable name="iname" select="concat('postalcode_', $oid)" />

					<xsl:call-template name="textfield">
						<xsl:with-param name="label" select="'%fldPostalCode%:'" />
						<xsl:with-param name="name" select="$iname" />
						<xsl:with-param name="selectnode" select="$datablock/postalcode" />
						<xsl:with-param name="size" select="'5'" />
					</xsl:call-template>
				</tr>

				<tr>
					<xsl:variable name="iname" select="concat('location_', $oid)" />

					<xsl:call-template name="textfield">
						<xsl:with-param name="label" select="'%fldLocation%:'" />
						<xsl:with-param name="name" select="$iname" />
						<xsl:with-param name="selectnode" select="$datablock/location" />
					</xsl:call-template>
				</tr>

				<tr>
					<xsl:variable name="iname" select="concat('country_', $oid)" />

					<xsl:call-template name="textfield">
						<xsl:with-param name="label" select="'%fldCountry%:'" />
						<xsl:with-param name="name" select="$iname" />
						<xsl:with-param name="selectnode" select="$datablock/country" />
					</xsl:call-template>
				</tr>

				<tr>
					<xsl:variable name="iname" select="concat('phone_', $oid)" />

					<xsl:call-template name="textfield">
						<xsl:with-param name="label" select="'%fldPhone%:'" />
						<xsl:with-param name="name" select="$iname" />
						<xsl:with-param name="selectnode" select="$datablock/phone" />
					</xsl:call-template>
				</tr>

				<tr>
					<xsl:variable name="iname" select="concat('mobile_', $oid)" />

					<xsl:call-template name="textfield">
						<xsl:with-param name="label" select="'%fldMobile%:'" />
						<xsl:with-param name="name" select="$iname" />
						<xsl:with-param name="selectnode" select="$datablock/mobile" />
					</xsl:call-template>
				</tr>

				<tr>
					<xsl:variable name="iname" select="concat('fax_', $oid)" />

					<xsl:call-template name="textfield">
						<xsl:with-param name="label" select="'%fldFax%:'" />
						<xsl:with-param name="name" select="$iname" />
						<xsl:with-param name="selectnode" select="$datablock/fax" />
					</xsl:call-template>
				</tr>

				<tr>
					<xsl:variable name="iname" select="concat('homepageurl_', $oid)" />

					<xsl:call-template name="textfield">
						<xsl:with-param name="label" select="'%fldHomepageurl%:'" />
						<xsl:with-param name="name" select="$iname" />
						<xsl:with-param name="selectnode" select="$datablock/homepageurl" />
					</xsl:call-template>

					<script type="text/javascript" language="JavaScript">
						<xsl:text>validatedFields[idx] = new Array("%fldHomepageurl%", "</xsl:text>
						<xsl:value-of select="$iname" />
						<xsl:text>", validateURL);</xsl:text>
						++idx;
					</script>
				</tr>

			</table>
		</fieldset>

	</xsl:template>

</xsl:stylesheet>