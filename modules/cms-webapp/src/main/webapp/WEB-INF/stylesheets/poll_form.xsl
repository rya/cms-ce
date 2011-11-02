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

  <xsl:include href="common/generic_formheader.xsl"/>
  <xsl:include href="editor/xhtmleditor.xsl"/>
  <xsl:include href="common/textarea.xsl"/>
  <xsl:include href="common/textfield.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>

  <xsl:param name="create"/>
  <xsl:param name="contenttypekey"/>
  <xsl:param name="modulename"/>
  <xsl:param name="expertcontributor"/>
  <xsl:param name="developer"/>

  <xsl:param name="accessToHtmlSource">
    <xsl:choose>
      <xsl:when test="$expertcontributor = 'true' or $developer = 'true'">
        <xsl:value-of select="true()"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="false()"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:param>

  <xsl:template match="/">

    <html>
      <head>
        <link type="text/css" rel="stylesheet" href="css/admin.css"/>
        <link type="text/css" rel="stylesheet" href="css/menu.css"/>
        <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>
        <link rel="stylesheet" type="text/css" href="javascript/cms/ui/style.css"/>
        <link rel="stylesheet" type="text/css" href="css/assignment.css"/>
        <link rel="stylesheet" type="text/css" href="css/calendar_picker.css"/>

        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" src="javascript/accessrights.js">//</script>
        <script type="text/javascript" src="javascript/validate.js">//</script>
        <script type="text/javascript" src="javascript/tabpane.js">//</script>
        <script type="text/javascript" src="javascript/menu.js">//</script>
        <script type="text/javascript" src="javascript/calendar_picker.js">//</script>
        <script type="text/javascript" src="javascript/content_form.js">//</script>

        <script type="text/javascript" src="javascript/cms/core.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Event.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Cookie.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Css.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Dimensions.js">//</script>
        <script type="text/javascript" src="javascript/cms/ui/SplitButton.js">//</script>

        <script type="text/javascript" src="javascript/properties.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/cms/Util.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/cms/Editor.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/tiny_mce/tiny_mce.js">//</script>

        <script type="text/javascript" language="JavaScript">
          // variables used by menu.js
          var branchOpen = new Array();
          var cookiename = "contentform";

          /**
           * Add row
           */
          function poll_addTableRow( body )
          {
            var body = document.getElementById(body);
            var sourceRow = body.getElementsByTagName('tr')[0];

						body.appendChild( sourceRow.cloneNode(true));

            var newIndex = itemcount(document.forms['formAdmin']['choice']);

            document.formAdmin['choice'][newIndex - 1].value = '';
            document.formAdmin['choicecount'][newIndex - 1].value = '0';
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          /**
           * Remove row
           */
					function removeParamObject( body, objThis, name )
          {
            var count = itemcount( document.forms['formAdmin'][objThis.name]);

            if( count == 1 )
            {
              document.forms['formAdmin']['choice'].value = "";
              return;
            }

            var index = getObjectIndex( objThis);

						document.getElementById(body).deleteRow( index );
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          /**
           * itemcount
           */
          function itemcount( elName)
          {
            var lItems;

            if (elName.length!=null)
            {
              lItems = elName.length;
            }
            else
            {
              lItems = 1;
            }
            return lItems;
          }
          // -------------------------------------------------------------------------------------------------------------------------------

/*
function GetCurrentContentObjectIndex( objThis,ObjName)
{
var lNumRows = itemcount( document.forms['formAdmin'][ObjName])

if( lNumRows > 1)
{
for( var i=0; i &lt; lNumRows; i++)
{
if( document.forms['formAdmin'][ObjName][i] == objThis)
{
return i;
}
}
}
else
{
return 0;
}
}
*/

          var validatedFields = new Array(2);
          validatedFields[0] = new Array("%fldTitle%", "title", validateRequired);

          function validateAll( formName)
          {
            var f = document.forms[formName];

            <xsl:if test="not(/contents/userright) or /contents/userright/@publish = 'true'">
              var tabPagePublishing = document.getElementById( 'tab-page-publishing');
              if ( !checkAll(formName, extraValidatedFields, tabPagePublishing ) )
              {
                return;
              }
            </xsl:if>


            if ( !checkAll(formName, validatedFields) )
            {
              return;
            }


            disableFormButtons();
            f.submit();
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          function enable()
          {
            var lNumRows = itemcount( document.formAdmin['choicecount'])

            if( lNumRows > 1 )
            {
              for( var i=0; i &lt; lNumRows; i++ )
              {
                document.formAdmin['choicecount'][i].removeAttribute("disabled");
              }
            }
          }
          // -------------------------------------------------------------------------------------------------------------------------------

        </script>
        <xsl:call-template name="waitsplash"/>
      </head>

      <xsl:call-template name="contentform"/>

      <xsl:if test="$create = 1">
        <script type="text/javascript">waitsplash();</script>
      </xsl:if>
    </html>
  </xsl:template>

  <xsl:template name="contenttypeform">
    <xsl:param name="readonly"/>

    <input type="hidden" name="totalcount" value="{/contents/content/contentdata/alternatives/@count}"/>

    <fieldset>
      <legend>&nbsp;%blockArticle%&nbsp;</legend>
      <table border="0" cellspacing="0" cellpadding="2">
        <tr><td class="form_labelcolumn"><xsl:comment>//</xsl:comment></td></tr>
        <tr>
          <xsl:call-template name="textfield">
            <xsl:with-param name="name" select="'title'"/>
            <xsl:with-param name="label" select="'%fldTitle%:'"/>
            <xsl:with-param name="selectnode" select="/contents/content/contentdata/title"/>
            <xsl:with-param name="size" select="'60'"/>
            <xsl:with-param name="maxlength" select="'100'"/>
            <xsl:with-param name="colspan" select="'3'"/>
            <xsl:with-param name="disabled" select="$readonly"/>
          </xsl:call-template>
        </tr>
        <tr>
          <td valign="top">%fldDescription%:</td>
          <td valign="top">
            <xsl:call-template name="xhtmleditor">
              <xsl:with-param name="id" select="'description'"/>
              <xsl:with-param name="name" select="'description'"/>
              <xsl:with-param name="content" select="/contents/content/contentdata/description"/>
              <xsl:with-param name="config" select="'light'"/>
              <xsl:with-param name="disabled" select="$readonly"/>
              <xsl:with-param name="accessToHtmlSource" select="$accessToHtmlSource = 'true'"/>
            </xsl:call-template>
          </td>
        </tr>
      </table>
    </fieldset>
    <fieldset>
      <legend>&nbsp;%blockAlternatives%&nbsp;</legend>
      <table border="0" cellspacing="0" cellpadding="2">
        <tr><td class="form_labelcolumn"><xsl:comment>//</xsl:comment></td></tr>
        <tr>
          <td><xsl:comment>//</xsl:comment></td>
          <td colspan="3">
            <input type="checkbox" name="multiplechoice">
              <xsl:if test="$readonly">
                <xsl:attribute name="disabled">disabled</xsl:attribute>
              </xsl:if>
              <xsl:if test="/contents/content/contentdata/alternatives/@multiplechoice = 'yes'">
                <xsl:attribute name="checked">on</xsl:attribute>
              </xsl:if>
            </input>
            &nbsp;%fldAllowMultipleChoice%
          </td>
        </tr>
        <tr>
          <td><xsl:comment>//</xsl:comment></td>
          <td colspan="3">%fldNumberOfVotes%:&nbsp;  <xsl:value-of select="/contents/content/contentdata/alternatives/@count"/>
          </td>
        </tr>
        <tr>
          <td><xsl:comment>//</xsl:comment></td>
          <td colspan="3">
            <table border="0" cellspacing="2" cellpadding="0" width="100%">
              <tbody name="alttable" id="alttable">
                <xsl:choose>
                  <xsl:when test="count(/contents/content/contentdata/alternatives/alternative) &gt; 0">
                    <xsl:for-each select="/contents/content/contentdata/alternatives/alternative">
                      <tr>
                        <td align="baseline">
                          <input type="text" name="choice" value="{.}">
                            <xsl:if test="$readonly">
                              <xsl:attribute name="disabled">disabled</xsl:attribute>
                            </xsl:if>
                          </input>
                          <input type="hidden" name="count" value="{@count}"/>
                          <xsl:call-template name="button">
                            <xsl:with-param name="name" select="'btndel'"/>
                            <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                            <xsl:with-param name="type" select="'button'"/>
                            <xsl:with-param name="disabled" select="$readonly"/>
                            <xsl:with-param name="onclick">
                              <xsl:text>javascript:removeParamObject('alttable', this, 'btndel');</xsl:text>
                            </xsl:with-param>
                          </xsl:call-template>
                        </td>
                        <td>
                          &nbsp;&nbsp;%fldVotesCount%: <input type="label" name="choicecount" style="text-align: right;" size="5" value="{@count}" disabled="disabled"/>
                        </td>
                      </tr>
                    </xsl:for-each>
                  </xsl:when>
                  <xsl:otherwise>
                    <tr>
                      <td align="baseline">
                        <input type="text" name="choice" value="">
                          <xsl:if test="$readonly">
                            <xsl:attribute name="disabled">disabled</xsl:attribute>
                          </xsl:if>
                        </input>
                        <input type="hidden" name="count" value="0"/>
                        <xsl:call-template name="button">
                          <xsl:with-param name="name" select="'btndel'"/>
                          <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
                          <xsl:with-param name="type" select="'button'"/>
                          <xsl:with-param name="disabled" select="$readonly"/>
                          <xsl:with-param name="onclick">
                            <xsl:text>javascript:removeParamObject('alttable', this, 'btndel');</xsl:text>
                          </xsl:with-param>
                        </xsl:call-template>
                      </td>
                      <td>
                        &nbsp;&nbsp;%fldVotesCount%: <input type="label" name="choicecount" style="text-align: right;" size="5" value="0" disabled="disabled"/>
                      </td>
                    </tr>
                  </xsl:otherwise>
                </xsl:choose>
              </tbody>
            </table>
            <xsl:call-template name="button">
              <xsl:with-param name="type" select="'button'"/>
              <xsl:with-param name="caption" select="'%cmdAddAlternative%'"/>
              <xsl:with-param name="name" select="'addparameter'"/>
              <xsl:with-param name="disabled" select="$readonly"/>
              <xsl:with-param name="onclick">
                <xsl:text>javascript:poll_addTableRow('alttable')</xsl:text>
              </xsl:with-param>
            </xsl:call-template>
          </td>
        </tr>
      </table>
    </fieldset>
  </xsl:template>
</xsl:stylesheet>