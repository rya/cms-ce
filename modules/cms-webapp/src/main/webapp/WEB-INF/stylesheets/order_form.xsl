<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp "&#160;">
  <!ENTITY Oslash "&#216;">
  <!ENTITY oslash "&#248;">
  <!ENTITY Aring  "&#197;">
  <!ENTITY aring  "&#229;">
  <!ENTITY AElig  "&#198;">
  <!ENTITY aelig  "&#230;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>

  <xsl:include href="common/standard_form_templates.xsl"/>

  <xsl:include href="common/dropdown_root.xsl"/>
  <xsl:include href="common/generic_formheader.xsl"/>
  <xsl:include href="common/textarea.xsl"/>
  <xsl:include href="common/textfield.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>

  <xsl:param name="create"/>
  <xsl:param name="contenttypekey"/>

  <xsl:variable name="autoapprove" select="true()"/>

  <xsl:template match="/">

    <html>
      <head>
        <link type="text/css" rel="stylesheet" href="css/admin.css"/>
        <link type="text/css" rel="stylesheet" href="css/menu.css"/>
        <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>
        <link rel="stylesheet" type="text/css" href="css/assignment.css"/>
        <link type="text/css" rel="stylesheet" href="css/calendar_picker.css"/>
        <link type="text/css" rel="stylesheet" href="javascript/cms/ui/style.css"/>

        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" src="javascript/accessrights.js">//</script>
        <script type="text/javascript" src="javascript/validate.js">//</script>
        <script type="text/javascript" src="javascript/tabpane.js">//</script>
        <script type="text/javascript" src="javascript/menu.js">//</script>
        <script type="text/javascript" src="javascript/calendar_picker.js">//</script>
        <script type="text/javascript" src="javascript/properties.js">//</script>
        <script type="text/javascript" src="javascript/content_form.js">//</script>

        <script type="text/javascript" src="javascript/cms/core.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Event.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Cookie.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Css.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Dimensions.js">//</script>
        <script type="text/javascript" src="javascript/cms/ui/SplitButton.js">//</script>


        <script type="text/javascript" language="JavaScript">
            // variables used by menu.js
            var branchOpen = new Array();
            var cookiename = "contentform";
            
          // array with names og compulsory fields
          // array with friendlyname, fieldname and function for validated fields
          var validatedFields = new Array(7);
          validatedFields[0] = new Array("%fldFirstName%", "customer_firstname", validateRequired);
          validatedFields[1] = new Array("%fldLastName%", "customer_surname", validateRequired);
          validatedFields[2] = new Array("%fldEMail%", "customer_email", validateRequired);
          validatedFields[3] = new Array("%blockDeliveryAddress% - %fldAddress%", "shipping_postaladdress", validateRequired);
          validatedFields[4] = new Array("%blockDeliveryAddress% - %fldPostalCode%", "shipping_postalcode", validateRequired);
          //validatedFields[5] = new Array("%blockDeliveryAddress% - %fldLocation%", "shipping_location", validateRequired);
          //validatedFields[6] = new Array("%blockDeliveryAddress% - %fldCountry%", "shipping_country", validateRequired);


          function validateAll(formName)
          {
              var f = document.forms[formName];

              if ( !checkAll(formName, validatedFields) )
                  return;

              disableFormButtons();
              f.submit();
          }

        function order_addTableRow( body)
        {
			var body = document.getElementById(body);
			var sourceRow = body.getElementsByTagName('tr')[0];
			body.appendChild( sourceRow.cloneNode(true));
        }

          /********************************************************************
		  function addTableRow( table )
          {
              var length = document.all[table].rows.length;
              var destRow = document.all[table].insertRow(length - 2);
              var sourceRow = document.all[table].rows[1];

              for( i=0; i&lt;sourceRow.cells.length;i++ )
              {
                  var destCell = destRow.insertCell();
                  var sourceCell = sourceRow.cells[i];
                  destCell.insertAdjacentHTML( 'afterBegin', sourceCell.innerHTML );
              }
          }
          ********************************************************************/

          function itemcount(elName)
          {
              var lItems;

              if (elName.length!=null)
                  lItems = elName.length;
              else
                  lItems = 1;

              return lItems;
          }

          function GetCurrentObjectIndex(objThis)
          {
              var lNumRows = itemcount(document.forms[formAdmin][objThis.name])

              if( lNumRows > 1 )
              {
                  for (var i=0; i &lt; lNumRows; i++)
                  {
                      if (document.forms['formAdmin'][objThis.name][i] == objThis)
                          return i;
                  }
              }
              else
                  return 0;
          }

          function removeItem(table, objThis)
          {
              var count = itemcount(document.forms['formAdmin'][objThis.name]);
              if( count == 1 )
              {
                  document.forms['formAdmin']['item_productid'].value = '';
                  document.forms['formAdmin']['item_productnumber'].value = '';
                  document.forms['formAdmin']['item_title'].value = '';
                  document.forms['formAdmin']['item_price'].value = '0.00';
                  document.forms['formAdmin']['item_count'].value = '0';
                  document.forms['formAdmin']['btn_priceupdate'].disabled = true;
                  updateTotal(document.forms['formAdmin']['item_price']);
              }
              else {
                  var index = GetCurrentObjectIndex(objThis)
                  document.all[table].deleteRow(index + 1);
              }
          }


          function selectProduct(ojbThis) {
              alert("selectProduct");
          }

          function priceUpdate(objThis) {
          }

          function updateTotal(objThis) {
              if (objThis != null)
              {
                  var count = itemcount(document.forms['formAdmin'][objThis.name]);

                  if (count == 1)
                  {
                      var price,count;
                      price = parseFloat(document.forms['formAdmin']['item_price'].value);
                      if (isNaN(price))
                      {
                          price = parseFloat(document.forms['formAdmin']['item_price'][0].value);
                          count = parseInt(document.forms['formAdmin']['item_count'][0].value);
                      }
                      else
                          count = parseInt(document.forms['formAdmin']['item_count'].value);
                      var subtotal = price * count;

                      if (subtotal == 0)
                          document.forms['formAdmin']['item_subtotal'].value = "0.00";
                      else if (subtotal == Math.floor(subtotal))
                          document.forms['formAdmin']['item_subtotal'].value = subtotal.toString() + ".00";
                      else
                      {
                          var x = Math.floor(subtotal);
                          var y = Math.round(Math.abs(subtotal - x) * 100);
                          if (y == 0)
                              document.forms['formAdmin']['item_subtotal'].value = x.toString() + ".00";
                          else if (y == 100)
                              document.forms['formAdmin']['item_subtotal'].value = x.toString() + ".99";
                          else if (y &lt; 10)
                              document.forms['formAdmin']['item_subtotal'].value = x.toString() + ".0" + y.toString();
                          else
                              document.forms['formAdmin']['item_subtotal'].value = x.toString() + "." + y.toString();
                      }
                  }
                  else {
                      var index = GetCurrentObjectIndex(objThis);
                      var price = parseFloat(document.forms['formAdmin']['item_price'][index].value);
                      var count = parseInt(document.forms['formAdmin']['item_count'][index].value);
                      var subtotal = price * count;

                      if (subtotal == 0)
                          document.forms['formAdmin']['item_subtotal'][index].value = "0.00";
                      else if (subtotal == Math.floor(subtotal))
                          document.forms['formAdmin']['item_subtotal'][index].value = subtotal.toString() + ".00";
                      else
                      {
                          var x = Math.floor(subtotal);
                          var y = Math.round(Math.abs(subtotal - x) * 100);
                          if (y == 0)
                              document.forms['formAdmin']['item_subtotal'][index].value = x.toString() + ".00";
                          else if (y == 100)
                              document.forms['formAdmin']['item_subtotal'][index].value = x.toString() + ".99";
                          else if (y &lt; 10)
                              document.forms['formAdmin']['item_subtotal'][index].value = x.toString() + ".0" + y.toString();
                          else
                              document.forms['formAdmin']['item_subtotal'][index].value = x.toString() + "." + y.toString();
                      }
                  }
              }

              var count = itemcount(document.forms['formAdmin']['item_subtotal']);
              var total;

              if (count == 1)
              {
                  total = parseFloat(document.forms['formAdmin']['item_subtotal'].value);
                  if (isNaN(total))
                      total = parseFloat(document.forms['formAdmin']['item_subtotal'][0].value);
              }
              else
              {
                  total = 0;
                  var i;
                  for (i = 0; i &lt; document.forms['formAdmin']['item_subtotal'].length; i++)
                  {
                      total += parseFloat(document.forms['formAdmin']['item_subtotal'][i].value);
                  }
              }

              if (total == 0)
                  document.forms['formAdmin']['total'].value = "0.00";
              else if (total == Math.floor(total))
                  document.forms['formAdmin']['total'].value = total.toString() + ".00";
              else if (Math.abs(total) &gt;= 0)
              {
                  var x = Math.floor(total);
                  var y = Math.round(Math.abs(total - x) * 100);
                  if (y == 0)
                      document.forms['formAdmin']['total'].value = x.toString() + ".00";
                  else if (y == 100)
                      document.forms['formAdmin']['total'].value = x.toString() + ".99";
                  else if (y &lt; 10)
                      document.forms['formAdmin']['total'].value = x.toString() + ".0" + y.toString();
                  else
                      document.forms['formAdmin']['total'].value = x.toString() + "." + y.toString();
              }
          }
        </script>

        <xsl:call-template name="waitsplash"/>

      </head>

      <xsl:call-template name="contentform"/>
    </html>

  </xsl:template>

  <xsl:template name="contenttypeform">
		<xsl:param name="readonly"/>

      <xsl:if test="not($readonly)">
        <script type="text/javascript">waitsplash();</script>
      </xsl:if>

      <fieldset>
          <legend>&nbsp;%blockOrder%&nbsp;</legend>
          <table border="0" cellspacing="0" cellpadding="2">
              <tr><td class="form_labelcolumn"><xsl:comment>Empty</xsl:comment></td></tr>
              
              <tr>
                  <xsl:call-template name="readonlyvalue">
                      <xsl:with-param name="label" select="'%fldOrderNumber%'"/>
                      <xsl:with-param name="selectnode" select="/contents/content/@key"/>
                  </xsl:call-template>
              </tr>
              <tr>
                  <xsl:call-template name="readonlyvalue">
                      <xsl:with-param name="label" select="'%fldReceivedDate%'"/>
                      <xsl:with-param name="selectnode" select="/contents/content/@created"/>
                  </xsl:call-template>
              </tr>
              <tr>
                  <xsl:call-template name="readonlyvalue">
                      <xsl:with-param name="name" select="'guid'"/>
                      <xsl:with-param name="label" select="'%fldGUID%'"/>
                      <xsl:with-param name="selectnode" select="/contents/content/contentdata/guid"/>
                  </xsl:call-template>
              </tr>
              <tr>
                  <xsl:call-template name="dropdown_root">
                      <xsl:with-param name="name" select="'status'"/>
                      <xsl:with-param name="label" select="'%fldStatus%'"/>
                      <xsl:with-param name="selectedkey" select="/contents/content/contentdata/status"/>
                      <xsl:with-param name="selectnode" select="/contents/statuses/status"/>
					  <xsl:with-param name="disabled" select="$readonly"/>
                  </xsl:call-template>
              </tr>
          </table>
      </fieldset>
        <fieldset>
            <legend>&nbsp;%blockCustomerInfo%&nbsp;</legend>
            <table border="0" cellspacing="0" cellpadding="2">
                <tr><td class="form_labelcolumn"></td></tr>
                
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'customer_firstname'"/>
                        <xsl:with-param name="label" select="'%fldFirstName%'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/customer/firstname"/>
                        <xsl:with-param name="size" select="'40'"/>
                        <xsl:with-param name="maxlength" select="'40'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
                        <xsl:with-param name="required" select="'true'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'customer_surname'"/>
                        <xsl:with-param name="label" select="'%fldLastName%'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/customer/surname"/>
                        <xsl:with-param name="size" select="'40'"/>
                        <xsl:with-param name="maxlength" select="'40'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
                        <xsl:with-param name="required" select="'true'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'customer_company'"/>
                        <xsl:with-param name="label" select="'%fldCompany%'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/customer/company"/>
                        <xsl:with-param name="size" select="'40'"/>
                        <xsl:with-param name="maxlength" select="'40'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'customer_email'"/>
                        <xsl:with-param name="label" select="'%fldEMail%'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/customer/email"/>
                        <xsl:with-param name="size" select="'40'"/>
                        <xsl:with-param name="maxlength" select="'40'"/>
                        <xsl:with-param name="onchange">validateEmail(this)</xsl:with-param>
                        <xsl:with-param name="colspan" select="'1'"/>
                        <xsl:with-param name="required" select="'true'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'customer_telephone'"/>
                        <xsl:with-param name="label" select="'%fldPhone%'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/customer/telephone"/>
                        <xsl:with-param name="size" select="'15'"/>
                        <xsl:with-param name="maxlength" select="'20'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'customer_mobile'"/>
                        <xsl:with-param name="label" select="'%fldMobilePhone%'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/customer/mobile"/>
                        <xsl:with-param name="size" select="'15'"/>
                        <xsl:with-param name="maxlength" select="'20'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'customer_fax'"/>
                        <xsl:with-param name="label" select="'%fldFax%'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/customer/fax"/>
                        <xsl:with-param name="size" select="'15'"/>
                        <xsl:with-param name="maxlength" select="'20'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
            </table>
        </fieldset>
        <fieldset>
            <legend>&nbsp;%blockDeliveryAddress%&nbsp;</legend>
            <table border="0" cellspacing="0" cellpadding="2">
                <tr><td class="form_labelcolumn"></td></tr>

                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'shipping_postaladdress'"/>
                        <xsl:with-param name="label" select="'%fldAddress%'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/customer/shippingaddress/postaladdress"/>
                        <xsl:with-param name="size" select="'40'"/>
                        <xsl:with-param name="maxlength" select="'40'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
                        <xsl:with-param name="required" select="'true'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'shipping_postalcode'"/>
                        <xsl:with-param name="label" select="'%fldPostalCode%'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/customer/shippingaddress/postalcode"/>
                        <xsl:with-param name="size" select="'7'"/>
                        <xsl:with-param name="maxlength" select="'7'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
                        <xsl:with-param name="required" select="'true'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'shipping_location'"/>
                        <xsl:with-param name="label" select="'%fldLocation%'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/customer/shippingaddress/location"/>
                        <xsl:with-param name="size" select="'30'"/>
                        <xsl:with-param name="maxlength" select="'30'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
                        <xsl:with-param name="required" select="'true'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'shipping_state'"/>
                        <xsl:with-param name="label" select="'%fldLocation%'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/customer/shippingaddress/state"/>
                        <xsl:with-param name="size" select="'10'"/>
                        <xsl:with-param name="maxlength" select="'10'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
                        <xsl:with-param name="required" select="'false'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'shipping_country'"/>
                        <xsl:with-param name="label" select="'%fldCountry%'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/customer/shippingaddress/country"/>
                        <xsl:with-param name="size" select="'30'"/>
                        <xsl:with-param name="maxlength" select="'30'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
                        <xsl:with-param name="required" select="'false'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
            </table>
        </fieldset>
        <fieldset>
            <legend>&nbsp;%blockInvoiceAddress%&nbsp;</legend>
            <table border="0" cellspacing="0" cellpadding="2">
                <tr><td class="form_labelcolumn"></td></tr>

                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'billing_postaladdress'"/>
                        <xsl:with-param name="label" select="'%fldAddress%'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/customer/billingaddress/postaladdress"/>
                        <xsl:with-param name="size" select="'40'"/>
                        <xsl:with-param name="maxlength" select="'40'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'billing_postalcode'"/>
                        <xsl:with-param name="label" select="'%fldPostalCode%'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/customer/billingaddress/postalcode"/>
                        <xsl:with-param name="size" select="'7'"/>
                        <xsl:with-param name="maxlength" select="'7'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'billing_location'"/>
                        <xsl:with-param name="label" select="'%fldLocation%'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/customer/billingaddress/location"/>
                        <xsl:with-param name="size" select="'30'"/>
                        <xsl:with-param name="maxlength" select="'30'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'billing_state'"/>
                        <xsl:with-param name="label" select="'%fldLocation%'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/customer/billingaddress/state"/>
                        <xsl:with-param name="size" select="'10'"/>
                        <xsl:with-param name="maxlength" select="'10'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'billing_country'"/>
                        <xsl:with-param name="label" select="'%fldCountry%'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/customer/billingaddress/country"/>
                        <xsl:with-param name="size" select="'30'"/>
                        <xsl:with-param name="maxlength" select="'30'"/>
                        <xsl:with-param name="colspan" select="'1'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
            </table>
        </fieldset>
        <fieldset>
            <legend>&nbsp;%blockOrderDetails%&nbsp;</legend>
            <table border="0" cellspacing="0" cellpadding="2">
                <tr><td class="form_labelcolumn"></td></tr>

                <tr>
                    <xsl:call-template name="textarea">
                        <xsl:with-param name="name" select="'details_comments'"/>
                        <xsl:with-param name="label" select="'%fldComments%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/details/comments"/>
                        <xsl:with-param name="rows" select="'5'"/>
                        <xsl:with-param name="cols" select="'60'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'details_shippingoptions'"/>
                        <xsl:with-param name="label" select="'%fldShippingOptions%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/details/shippingoptions"/>
                        <xsl:with-param name="size" select="'40'"/>
                        <xsl:with-param name="maxlength" select="'255'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
						<xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>
            </table>
        </fieldset>
        <fieldset>
            <legend>&nbsp;%blockProducts%&nbsp;</legend>
            <table border="0" cellspacing="0" cellpadding="2">
                <tr><td class="form_labelcolumn"></td></tr>
                <tr>
                    <td colspan="2">
                        <table border="0" id="tblitems" cellspacing="2" cellpadding="0">
                            <tr>
                                <td><b>%fldProductId%</b></td>
                                <td><b>%fldProductNumber%</b></td>
                                <td><b>%fldName%</b></td>
                                <td align="center"><b>%fldPrice%</b></td>
                                <td align="center"><b>%fldCount%</b></td>
                                <td align="center"><b>%fldTotalPrice%</b></td>
                                <!--td align="right"><b></b></td-->
                            </tr>
                            <xsl:variable name="validateint" select="'if (validateInt(this)) updateTotal(this);'"/>
                            <xsl:variable name="validatedecimal" select="'if (validateDecimal(this)) updateTotal(this);'"/>
                            <xsl:choose>
                                <xsl:when test="/contents/content/contentdata/items/item">
                                    <xsl:for-each select="/contents/content/contentdata/items/item">
                                        <tr>
                                            <xsl:call-template name="textfield">
                                                <xsl:with-param name="name" select="'item_productid'"/>
                                                <xsl:with-param name="selectnode" select="@productid"/>
                                                <xsl:with-param name="size" select="'10'"/>
                                                <xsl:with-param name="maxlength" select="'10'"/>
                                                <xsl:with-param name="readonly" select="true()"/>
                                            </xsl:call-template>
                                            <xsl:call-template name="textfield">
                                                <xsl:with-param name="name" select="'item_productnumber'"/>
                                                <xsl:with-param name="selectnode" select="@productnumber"/>
                                                <xsl:with-param name="size" select="'15'"/>
                                                <xsl:with-param name="maxlength" select="'32'"/>
                                                <xsl:with-param name="readonly" select="true()"/>
                                            </xsl:call-template>
                                            <xsl:call-template name="textfield">
                                                <xsl:with-param name="name" select="'item_title'"/>
                                                <xsl:with-param name="selectnode" select="."/>
                                                <xsl:with-param name="size" select="'50'"/>
                                                <xsl:with-param name="maxlength" select="'255'"/>
                                                <xsl:with-param name="readonly" select="true()"/>
                                            </xsl:call-template>
                                            <xsl:call-template name="textfield">
                                                <xsl:with-param name="name" select="'item_price'"/>
                                                <xsl:with-param name="selectnode" select="format-number(round(100*number(@price)) div 100, '0.00')"/>
                                                <xsl:with-param name="size" select="'10'"/>
                                                <xsl:with-param name="maxlength" select="'10'"/>
                                                <xsl:with-param name="onblur" select="$validatedecimal"/>
                                                <xsl:with-param name="onchange" select="$validatedecimal"/>
                                                <xsl:with-param name="align" select="'right'"/>
												<xsl:with-param name="disabled" select="$readonly"/>
                                            </xsl:call-template>
                                            <xsl:call-template name="textfield">
                                                <xsl:with-param name="name" select="'item_count'"/>
                                                <xsl:with-param name="selectnode" select="@count"/>
                                                <xsl:with-param name="size" select="'5'"/>
                                                <xsl:with-param name="maxlength" select="'5'"/>
                                                <xsl:with-param name="onblur" select="$validateint"/>
                                                <xsl:with-param name="onchange" select="$validateint"/>
                                                <xsl:with-param name="align" select="'right'"/>
												<xsl:with-param name="disabled" select="$readonly"/>
                                            </xsl:call-template>
                                            <xsl:call-template name="textfield">
                                                <xsl:with-param name="name" select="'item_subtotal'"/>
                                                <xsl:with-param name="selectnode" select="format-number(round(100*number(@price) * number(@count)) div 100, '0.00')"/>
                                                <xsl:with-param name="size" select="'15'"/>
                                                <xsl:with-param name="maxlength" select="'15'"/>
                                                <xsl:with-param name="readonly" select="true()"/>
                                                <xsl:with-param name="align" select="'right'"/>
                                            </xsl:call-template>
                                            <!--td>
                                                <input type="hidden" name="item_productid">
                                                    <xsl:attribute name="value">
                                                        <xsl:value-of select="@productid"/>
                                                    </xsl:attribute>
                                                </input>
                                                <input type="button" class="button" name="btn_productselect" value="..." onclick="selectProduct(this)" disabled="disabled"/>
                                                <input type="button" class="button" name="btn_priceupdate" value="P" onclick="priceUpdate(this)" disabled="disabled"/>
                                                <input type="button" class="button" name="btn_clear" value="X" onclick="removeItem('tblitems', this); updateTotal(null)" disabled="disabled"/>
                                            </td-->
                                        </tr>
                                    </xsl:for-each>
                                </xsl:when>
                                <xsl:otherwise>
                                    <tr>
                                        <xsl:call-template name="textfield">
                                            <xsl:with-param name="name" select="'item_productid'"/>
                                            <xsl:with-param name="selectnode" select="''"/>
                                            <xsl:with-param name="size" select="'10'"/>
                                            <xsl:with-param name="maxlength" select="'10'"/>
                                            <xsl:with-param name="readonly" select="true()"/>
                                        </xsl:call-template>
                                        <xsl:call-template name="textfield">
                                            <xsl:with-param name="name" select="'item_productnumber'"/>
                                            <xsl:with-param name="selectnode" select="''"/>
                                            <xsl:with-param name="size" select="'15'"/>
                                            <xsl:with-param name="maxlength" select="'32'"/>
                                            <xsl:with-param name="readonly" select="true()"/>
                                        </xsl:call-template>
                                        <xsl:call-template name="textfield">
                                            <xsl:with-param name="name" select="'item_title'"/>
                                            <xsl:with-param name="selectnode" select="''"/>
                                            <xsl:with-param name="size" select="'50'"/>
                                            <xsl:with-param name="maxlength" select="'255'"/>
                                            <xsl:with-param name="readonly" select="true()"/>
                                        </xsl:call-template>
                                        <xsl:call-template name="textfield">
                                            <xsl:with-param name="name" select="'item_price'"/>
                                            <xsl:with-param name="selectnode" select="'0.00'"/>
                                            <xsl:with-param name="size" select="'10'"/>
                                            <xsl:with-param name="maxlength" select="'10'"/>
                                            <xsl:with-param name="onblur" select="$validatedecimal"/>
                                            <xsl:with-param name="align" select="'right'"/>
											<xsl:with-param name="disabled" select="$readonly"/>
                                        </xsl:call-template>
                                        <xsl:call-template name="textfield">
                                            <xsl:with-param name="name" select="'item_count'"/>
                                            <xsl:with-param name="selectnode" select="'0'"/>
                                            <xsl:with-param name="size" select="'5'"/>
                                            <xsl:with-param name="maxlength" select="'5'"/>
                                            <xsl:with-param name="onblur" select="$validateint"/>
                                            <xsl:with-param name="align" select="'right'"/>
											<xsl:with-param name="disabled" select="$readonly"/>
                                        </xsl:call-template>
                                        <xsl:call-template name="textfield">
                                            <xsl:with-param name="name" select="'item_subtotal'"/>
                                            <xsl:with-param name="selectnode" select="'0.00'"/>
                                            <xsl:with-param name="size" select="'15'"/>
                                            <xsl:with-param name="maxlength" select="'15'"/>
                                            <xsl:with-param name="readonly" select="true()"/>
                                            <xsl:with-param name="align" select="'right'"/>
                                        </xsl:call-template>
                                        <!--td>
                                            <input type="hidden" name="item_productid">
                                                <xsl:attribute name="value">
                                                    <xsl:value-of select="''"/>
                                                </xsl:attribute>
                                            </input>
                                            <input type="button" class="button" name="btn_productselect" value="..." onclick="selectProduct(this)" disabled="disabled"/>
                                            <input type="button" class="button" name="btn_priceupdate" value="P" onclick="priceUpdate(this)" disabled="disabled"/>
                                            <input type="button" class="button" name="btn_clear" value="X" onclick="removeItem('tblitems', this); updateTotal(null)" disabled="disabled"/>
                                        </td-->
                                    </tr>
                                </xsl:otherwise>
                            </xsl:choose>
                            <tr bgcolor="#000000">
                                <td colspan="6">
                                    <p><img src="images/1x1.gif" width="1" height="1"/></p>
                                </td>
                            </tr>

                            <tr>
                                <td colspan="5"><b>%fldTotalPrice%</b></td>
                                <xsl:call-template name="textfield">
                                    <xsl:with-param name="name" select="'total'"/>
                                    <xsl:with-param name="selectnode" select="-1"/>
                                    <xsl:with-param name="size" select="'15'"/>
                                    <xsl:with-param name="maxlength" select="'15'"/>
                                    <xsl:with-param name="readonly" select="true()"/>
                                    <xsl:with-param name="align" select="'right'"/>
                                </xsl:call-template>
                            </tr>
							<script type="text/javascript" language="JavaScript">
								updateTotal();
							</script>
                        </table>
                    </td>
                </tr>
            </table>
        </fieldset>

        <script type="text/javascript">removeWaitsplash();</script>

  </xsl:template>

</xsl:stylesheet>
