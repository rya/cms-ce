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
    
    <xsl:param name="key"/>

    <xsl:variable name="workentry">
        <xsl:choose>
            <xsl:when test="/wizarddata/wizardstate/stepstate[@id = '0']/workentry">
                <xsl:copy-of select="/wizarddata/wizardstate/stepstate[@id = '0']/workentry"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:copy-of select="/wizarddata/workentries/workentry"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="runtype">
      <xsl:choose>
        <!-- run once -->
        <xsl:when test="not(exslt-common:node-set($workentry)/workentry) or exslt-common:node-set($workentry)/workentry/trigger/@type = 'simple' and exslt-common:node-set($workentry)/workentry/trigger/repeat/@count = 0">
          <xsl:text>once</xsl:text>
        </xsl:when>

        <!-- run infinite -->
        <xsl:when test="exslt-common:node-set($workentry)/workentry/trigger/@type = 'simple' and exslt-common:node-set($workentry)/workentry/trigger/repeat/@count = -1">
          <xsl:text>infinite</xsl:text>
        </xsl:when>
        
        <!-- run repeatedly -->
        <xsl:when test="exslt-common:node-set($workentry)/workentry/trigger/@type = 'simple'">
          <xsl:text>repeatedly</xsl:text>
        </xsl:when>
        
        <!-- run hourly -->
        <xsl:when test="exslt-common:node-set($workentry)/workentry/trigger/@type = 'cron' and exslt-common:node-set($workentry)/workentry/trigger/hourly">
          <xsl:text>hourly</xsl:text>
        </xsl:when>
        
        <!-- run daily -->
        <xsl:when test="exslt-common:node-set($workentry)/workentry/trigger/@type = 'cron' and exslt-common:node-set($workentry)/workentry/trigger/daily">
          <xsl:text>daily</xsl:text>
        </xsl:when>
        
        <!-- run custom -->
        <xsl:otherwise>
          <xsl:text>custom</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:template name="workentryheader">
        <xsl:call-template name="displaysystempath">
          <xsl:with-param name="page" select="$page"/>
        </xsl:call-template>
        <xsl:text>&nbsp;</xsl:text>
        <span id="titlename">
          <xsl:if test="exslt-common:node-set($workentry)/workentry/name != ''">
            <xsl:value-of select="concat(' /', exslt-common:node-set($workentry)/workentry/name)"/>
          </xsl:if>
        </span>
    </xsl:template>

    <xsl:template name="step0">
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

                    <input type="hidden" name="stepstate_workentry_@key" value="{/wizarddata/workentries/workentry/@key}"/>

                    <table width="100%" border="0" cellspacing="2" cellpadding="2">
                        <tr>
                            <xsl:call-template name="textfield">
                                <xsl:with-param name="label" select="'%fldName%:'"/>
                                <xsl:with-param name="name" select="'stepstate_workentry_name'"/>
                                <xsl:with-param name="size" select="'70'"/>
                                <xsl:with-param name="maxlength" select="'220'"/>
                                <xsl:with-param name="selectnode" select="exslt-common:node-set($workentry)/workentry/name"/>
                                <xsl:with-param name="required" select="'true'"/>
                                <xsl:with-param name="onkeyup">javascript: updateBreadCrumbHeader('titlename', this);</xsl:with-param>
                            </xsl:call-template>
                        </tr>
                        <tr>
                            <xsl:call-template name="labelcolumn">
                                <xsl:with-param name="label" select="'%fldPlugin%:'"/>
                                <xsl:with-param name="required" select="'true'"/>
                            </xsl:call-template>
                            <td>
                                <xsl:call-template name="task-plugin-dropdown">
                                    <xsl:with-param name="field-name" select="'stepstate_workentry_workclass'"/>
                                    <xsl:with-param name="task-plugins" select="/wizarddata/task-plugins"/>
                                    <xsl:with-param name="class" select="/wizarddata/workentries/workentry/workclass"/>
                                </xsl:call-template>
                            </td>
                        </tr>
                    </table>
                </fieldset>

                <xsl:call-template name="properties">
                  <xsl:with-param name="workentry" select="exslt-common:node-set($workentry)/workentry"/>
                </xsl:call-template>
                
                <fieldset>
                    <legend>%blockTrigger%</legend>

                    <script type="text/javascript">
                      var dateTimeRangeValidator = new DateTimeRangeValidator('start', 'end', true, {
                        startDatetimeIsLaterThanEndDatetime: '%errStartExecutionIsLaterThanEndExecutionTo%',
                        startLabel: '%fldStart%',
                        endLabel: '%fldEnd%'
                      });
                    </script>

                    <div id="textfielddatetime-error-message" style="display:none;"><xsl:comment>//</xsl:comment></div>

                    <table width="100%" border="0" cellspacing="2" cellpadding="2">
                      <tr>
                        <xsl:call-template name="textfielddatetime">
                          <xsl:with-param name="name" select="'start'"/>
                          <xsl:with-param name="label" select="'%fldStart%:'"/>
                          <xsl:with-param name="selectnode" select="exslt-common:node-set($workentry)/workentry/trigger/time/@start"/>
                          <xsl:with-param name="colspan" select="'1'"/>
                          <xsl:with-param name="includeseconds" select="true()"/>
                          <xsl:with-param name="onbluroverridefunction">
                            <xsl:text>dateTimeRangeValidator.validate();</xsl:text>
                          </xsl:with-param>
                        </xsl:call-template>
                      </tr>
                      <tr>
                        <xsl:call-template name="textfielddatetime">
                          <xsl:with-param name="name" select="'end'"/>
                          <xsl:with-param name="label" select="'%fldEnd%:'"/>
                          <xsl:with-param name="selectnode" select="exslt-common:node-set($workentry)/workentry/trigger/time/@end"/>
                          <xsl:with-param name="colspan" select="'1'"/>
                          <xsl:with-param name="includeseconds" select="true()"/>
                          <xsl:with-param name="onbluroverridefunction">
                            <xsl:text>dateTimeRangeValidator.validate();</xsl:text>
                          </xsl:with-param>
                        </xsl:call-template>
                      </tr>
                      <tr>
                        <xsl:call-template name="dropdown_types"/>
                      </tr>

                      <!-- run type: once -->
                      <!-- no fields      -->

                      <!-- run type: infinite, repeatedly -->
                      <tr id="tr_repeat_interval">
                        <xsl:if test="not( contains('infinite repeatedly', $runtype) )">
                          <xsl:attribute name="style">display: none;</xsl:attribute>
                        </xsl:if>
                        <xsl:call-template name="textfield">
                          <xsl:with-param name="name" select="'stepstate_workentry_trigger_repeat_@interval'"/>
                          <xsl:with-param name="label" select="'%fldRepeatInterval%:'"/>
                          <xsl:with-param name="size" select="'10'"/>
                          <xsl:with-param name="maxlength" select="'10'"/>
                          <xsl:with-param name="selectnode" select="exslt-common:node-set($workentry)/workentry/trigger/repeat/@interval"/>
                          <xsl:with-param name="colspan" select="'1'"/>
                          <xsl:with-param name="required" select="'true'"/>
                          <xsl:with-param name="postfield" select="' s'"/>
                          <xsl:with-param name="helpelement">
                            <xsl:element name="help">
                              <xsl:text>%tooltipRepeatInterval%</xsl:text>
                            </xsl:element>
                          </xsl:with-param>
                        </xsl:call-template>
                      </tr>
                      <tr id="tr_repeat_count">
                        <xsl:if test="not( $runtype = 'repeatedly' )">
                          <xsl:attribute name="style">display: none;</xsl:attribute>
                        </xsl:if>
                        <xsl:call-template name="textfield">
                          <xsl:with-param name="name" select="'stepstate_workentry_trigger_repeat_@count'"/>
                          <xsl:with-param name="label" select="'%fldCount%:'"/>
                          <xsl:with-param name="size" select="'10'"/>
                          <xsl:with-param name="maxlength" select="'10'"/>
                          <xsl:with-param name="selectnode" select="exslt-common:node-set($workentry)/workentry/trigger/repeat/@count"/>
                          <xsl:with-param name="required" select="'true'"/>
                          <xsl:with-param name="colspan" select="'1'"/>
                          <xsl:with-param name="helpelement">
                            <xsl:element name="help">
                              <xsl:text>%tooltipRepeatCount%</xsl:text>
                            </xsl:element>
                          </xsl:with-param>
                        </xsl:call-template>
                      </tr>

                      <!-- run type: hourly -->
                      <tr id="tr_hourly">
                        <xsl:if test="not( $runtype = 'hourly' )">
                          <xsl:attribute name="style">display: none</xsl:attribute>
                        </xsl:if>
                        <xsl:call-template name="textfield">
                          <xsl:with-param name="name" select="'stepstate_workentry_trigger_hourly_@minutes'"/>
                          <xsl:with-param name="label" select="'%fldMinutes%:'"/>
                          <xsl:with-param name="selectnode" select="exslt-common:node-set($workentry)/workentry/trigger/hourly/@minutes"/>
                          <xsl:with-param name="colspan" select="'1'"/>
                          <xsl:with-param name="size" select="'3'"/>
                          <xsl:with-param name="maxlength" select="'2'"/>
                          <xsl:with-param name="required" select="'true'"/>
                        </xsl:call-template>
                      </tr>

                      <!-- run type: daily -->
                      <tr id="tr_daily">
                        <xsl:if test="not( $runtype = 'daily' )">
                          <xsl:attribute name="style">display: none</xsl:attribute>
                        </xsl:if>
                        <xsl:call-template name="textfield">
                          <xsl:with-param name="name" select="'stepstate_workentry_trigger_daily_@time'"/>
                          <xsl:with-param name="label" select="'%fldTime%:'"/>
                          <xsl:with-param name="selectnode" select="exslt-common:node-set($workentry)/workentry/trigger/daily/@time"/>
                          <xsl:with-param name="colspan" select="'1'"/>
                          <xsl:with-param name="size" select="'6'"/>
                          <xsl:with-param name="maxlength" select="'5'"/>
                          <xsl:with-param name="required" select="'true'"/>
                          <xsl:with-param name="onchange" select="'validateTimeSeconds(this)'"/>
                          <xsl:with-param name="postfield" select="' hh:mm'"/>
                        </xsl:call-template>
                      </tr>

                      <!-- run type: custom -->
                      <tr id="tr_cron">
                        <xsl:if test="not( $runtype = 'custom' )">
                          <xsl:attribute name="style">display: none</xsl:attribute>
                        </xsl:if>
                        <xsl:call-template name="textfield">
                          <xsl:with-param name="name" select="'stepstate_workentry_trigger_cron'"/>
                          <xsl:with-param name="label" select="'%fldCronExpression%:'"/>
                          <xsl:with-param name="selectnode" select="exslt-common:node-set($workentry)/workentry/trigger/cron"/>
                          <xsl:with-param name="colspan" select="'1'"/>
                          <xsl:with-param name="size" select="'70'"/>
                          <xsl:with-param name="maxlength" select="'220'"/>
                          <xsl:with-param name="required" select="'true'"/>
                          <xsl:with-param name="helpelement">
                            <xsl:element name="help">
                              <xsl:text>%tooltipCronExpression%</xsl:text>
                            </xsl:element>
                          </xsl:with-param>
                        </xsl:call-template>
                      </tr>
                    </table>
                </fieldset>
                
                <xsl:if test="/wizarddata/workentries/workentry">
                  <fieldset>
                    <legend>%blockDetails%</legend>
                    
                    <table width="100%" border="0" cellspacing="2" cellpadding="2">
                      <tr>
                        <xsl:call-template name="readonlydatetime">
                          <xsl:with-param name="name" select="'_previous'"/>
                          <xsl:with-param name="label" select="'%fldPrevious%:'"/>
                          <xsl:with-param name="selectnode" select="/wizarddata/workentries/workentry/trigger/time/@previous"/>
                          <xsl:with-param name="colspan" select="'1'"/>
                          <xsl:with-param name="includeseconds" select="true()"/>
                        </xsl:call-template>
                      </tr>
                      <tr>
                        <xsl:call-template name="readonlydatetime">
                          <xsl:with-param name="name" select="'_next'"/>
                          <xsl:with-param name="label" select="'%fldNext%:'"/>
                          <xsl:with-param name="selectnode" select="/wizarddata/workentries/workentry/trigger/time/@next"/>
                          <xsl:with-param name="colspan" select="'1'"/>
                          <xsl:with-param name="includeseconds" select="true()"/>
                        </xsl:call-template>
                      </tr>
                      <xsl:if test="/wizarddata/workentries/workentry/trigger/time/@final">
                        <tr>
                          <xsl:call-template name="readonlydatetime">
                            <xsl:with-param name="name" select="'_final'"/>
                            <xsl:with-param name="label" select="'%fldFinal%:'"/>
                            <xsl:with-param name="selectnode" select="/wizarddata/workentries/workentry/trigger/time/@final"/>
                            <xsl:with-param name="colspan" select="'1'"/>
                            <xsl:with-param name="includeseconds" select="true()"/>
                          </xsl:call-template>
                        </tr>
                      </xsl:if>
                    </table>
                  </fieldset>
                </xsl:if>
            </div>
        </div>

        <xsl:variable name="setValidatedFieldParameters">
          <xsl:choose>

            <!-- run once -->
            <xsl:when test="$runtype = 'once'">
              <xsl:text>0, 0</xsl:text>
            </xsl:when>

            <!-- run infinite -->
            <xsl:when test="$runtype = 'infinite'">
              <xsl:text>1, 1</xsl:text>
            </xsl:when>

            <!-- run repeatedly -->
            <xsl:when test="$runtype = 'repeatedly'">
              <xsl:text>1, 2</xsl:text>
            </xsl:when>

            <!-- run hourly -->
            <xsl:when test="$runtype = 'hourly'">
              <xsl:text>3, 1</xsl:text>
            </xsl:when>

            <!-- run daily -->
            <xsl:when test="$runtype = 'daily'">
              <xsl:text>4, 1</xsl:text>
            </xsl:when>

            <!-- run custom -->
            <xsl:otherwise>
              <xsl:text>5, 1</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        
        <script type="text/javascript" language="JavaScript">
            setupAllTabs();

            // set correct validation field based on selected drop down option
            setValidatedFields(<xsl:value-of select="$setValidatedFieldParameters"/>);
        </script>
    </xsl:template>


    <xsl:template name="dropdown_types">
        <td valign="baseline" nowrap="nowrap">
          <xsl:text>%fldRun%:</xsl:text>
        </td>
        <td nowrap="nowrap">
            <input type="hidden" name="stepstate_workentry_trigger_@type" id="idtype" value="{exslt-common:node-set($workentry)/workentry/trigger/@type}"/>

            <select name="__type" onchange="typeChange(this)">

              <option value="once">
                <xsl:if test="$runtype = 'once'">
                  <xsl:attribute name="selected">selected</xsl:attribute>
                </xsl:if>
                <xsl:text>%optRunOnce%</xsl:text>
              </option>

              <option value="infinite">
                <xsl:if test="$runtype = 'infinite'">
                  <xsl:attribute name="selected">selected</xsl:attribute>
                </xsl:if>
                <xsl:text>%optRunInfinite%</xsl:text>
              </option>

              <option value="repeatedly">
                <xsl:if test="$runtype = 'repeatedly'">
                  <xsl:attribute name="selected">selected</xsl:attribute>
                </xsl:if>
                <xsl:text>%optRunRepeatedly%</xsl:text>
              </option>

              <option value="hourly">
                <xsl:if test="$runtype = 'hourly'">
                  <xsl:attribute name="selected">selected</xsl:attribute>
                </xsl:if>
                <xsl:text>%optRunHourly%</xsl:text>
              </option>

              <option value="daily">
                <xsl:if test="$runtype = 'daily'">
                  <xsl:attribute name="selected">selected</xsl:attribute>
                </xsl:if>
                <xsl:text>%optRunDaily%</xsl:text>
              </option>

              <option value="custom">
                <xsl:if test="$runtype = 'custom'">
                  <xsl:attribute name="selected">selected</xsl:attribute>
                </xsl:if>
                <xsl:text>%optRunCustom%</xsl:text>
              </option>
            </select>
        </td>
    </xsl:template>

    <xsl:template name="properties">
        <xsl:param name="workentry"/>

        <fieldset>
            <legend>&nbsp;%blockProperties%&nbsp;</legend>

            <table width="100%" colspace="0" colpadding="2" border="0">
				<tbody name="tblproperties" id="tblproperties">
	              <xsl:choose>
	                <xsl:when test="$workentry/properties/property">
	                  <xsl:for-each select="$workentry/properties/property">
	                    <tr>
	                      <td>
	                        <xsl:text>%fldName%:&nbsp;</xsl:text>
	                        <input type="text" name="stepstate_workentry_properties_property_@name" value="{@name}"/>
	                        <xsl:text>&nbsp;&nbsp;%fldValue%:&nbsp;</xsl:text>
	                        <input type="text" name="stepstate_workentry_properties_property_@value" value="{@value}"/>
	                        <xsl:text>&nbsp;&nbsp;</xsl:text>
	                        <xsl:call-template name="button">
	                          <xsl:with-param name="type" select="'button'"/>
	                          <xsl:with-param name="name">btndel</xsl:with-param>
	                          <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
	                          <xsl:with-param name="onclick">
	                            <xsl:text>javascript:removeProperty('tblproperties', this);</xsl:text>
	                          </xsl:with-param>
	                        </xsl:call-template>
	                      </td>
	                    </tr>
	                  </xsl:for-each>
	                </xsl:when>
	                <xsl:otherwise>
	                  <tr>
	                    <td>
	                      <xsl:text>%fldName%:&nbsp;</xsl:text>
	                      <input type="text" name="stepstate_workentry_properties_property_@name"/>
	                      <xsl:text>&nbsp;&nbsp;%fldValue%:&nbsp;</xsl:text>
	                      <input type="text" name="stepstate_workentry_properties_property_@value"/>
	                      <xsl:text>&nbsp;&nbsp;</xsl:text>
	                      <xsl:call-template name="button">
	                        <xsl:with-param name="type" select="'button'"/>
	                        <xsl:with-param name="name">removeproperty</xsl:with-param>
	                        <xsl:with-param name="image" select="'images/icon_remove.gif'"/>
	                        <xsl:with-param name="onclick">
	                          <xsl:text>javascript:removeProperty('tblproperties', this);</xsl:text>
	                        </xsl:with-param>
	                      </xsl:call-template>
	                    </td>
	                  </tr>
	                </xsl:otherwise>
	              </xsl:choose>
			  </tbody>
            </table>

            <xsl:call-template name="button">
                <xsl:with-param name="type" select="'button'"/>
                <xsl:with-param name="caption" select="'%cmdNewProperty%'"/>
                <xsl:with-param name="name" select="'addproperty'"/>
                <xsl:with-param name="onclick">
                    <xsl:text>javascript: addProperty('tblproperties');</xsl:text>
                </xsl:with-param>
            </xsl:call-template>

        </fieldset>
    </xsl:template>

    <xsl:template name="task-plugin-dropdown">
        <xsl:param name="task-plugins"/>
        <xsl:param name="field-name"/>
        <xsl:param name="class"/>

        <select name="{$field-name}">
        <option value="">%sysDropDownChoose%</option>
            <xsl:for-each select="$task-plugins/task-plugin">
              <xsl:choose>
                <xsl:when test="$class = @class">
                  <option value="{@class}" selected="true"><xsl:value-of select="@display-name"/></option>
                </xsl:when>
                <xsl:otherwise>
                  <option value="{@class}"><xsl:value-of select="@display-name"/></option>
                </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </select>
    </xsl:template>

</xsl:stylesheet>
