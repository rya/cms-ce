<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp   "&#160;">
    ]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:x="mailto:vro@enonic.com?subject=foobar"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html" />

  <xsl:include href="common/genericheader.xsl" />
  <xsl:include href="common/categoryheader.xsl" />
  <xsl:include href="common/textfield.xsl" />
  <xsl:include href="common/textfielddatetime.xsl" />
  <xsl:include href="common/readonlydatetime.xsl" />
  <xsl:include href="common/readonlyvalue.xsl" />
  <xsl:include href="common/formatdate.xsl" />
  <xsl:include href="common/tablecolumnheader.xsl" />
  <xsl:include href="common/tablerowpainter.xsl" />
  <xsl:include href="common/labelcolumn.xsl" />
  <xsl:include href="common/displayhelp.xsl" />
  <xsl:include href="common/displayerror.xsl" />
  <xsl:include href="common/checkbox_boolean.xsl" />
  <xsl:include href="common/button.xsl" />
  <xsl:include href="common/dropdown.xsl" />
  <xsl:include href="common/publishstatus.xsl" />
  <xsl:include href="common/string.xsl" />

  <!-- parameter for all steps -->
  <xsl:param name="contenttitle" />
  <xsl:param name="contenttypekey" />

  <!-- parameter for step 1 -->
  <xsl:param name="notify"/>

  <!-- parameters for step 2 -->
  <xsl:param name="sectionnumber" />
  <xsl:param name="menuitemkey" />
  <xsl:param name="path" />

	<xsl:variable name="create" select="'1'"/>

	<xsl:key name="menuitemkey" match="section" use="@menuitemkey"/>

  <xsl:variable name="status">
    <xsl:choose>
			<xsl:when test="/wizarddata/wizardstate/stepstate[1]/status">
				<xsl:value-of select="/wizarddata/wizardstate/stepstate[1]/status"/>
			</xsl:when>
      <xsl:otherwise></xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <xsl:variable name="originalstatus">
		<xsl:value-of select="/wizarddata/contents/content/@status"/>
	</xsl:variable>

  <xsl:variable name="categorypublish" select="/wizarddata/contents/content/accessrights/userright/@categorypublish = 'true'"/>

  <xsl:variable name="publishingdisabled" select="$originalstatus = 2 and not($categorypublish)"/>

  <xsl:variable name="stepState1" select="/wizarddata/wizardstate/stepstate[@stepid = '1']"/>

  <xsl:template name="step0">
    <xsl:variable name="menucount">
      <xsl:choose>
        <xsl:when test="/wizarddata/wizardstate/stepstate[1]/menu">
          <xsl:value-of select="count(/wizarddata/wizardstate/stepstate[1]/menu/@key)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="count(/wizarddata/menus/menu[@key = /wizarddata/contents/content/sectionnames/sectionname/@menukey])"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <div class="tab-pane" id="tab-pane-1">
      <script type="text/javascript" language="JavaScript">
        var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );

        countMenuSelected = <xsl:value-of select="$menucount"/>;
      </script>

      <div class="tab-page" id="tab-page-step">
        <span class="tab">
          <xsl:text>%headStep% 1 %of%</xsl:text>
          <xsl:choose>
            <xsl:when test="$menucount = 0">
              <xsl:text> 1 </xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text> 3 </xsl:text>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:text>: %headChooseAvailabilityAndSites%</xsl:text>
        </span>

        <script type="text/javascript" language="JavaScript">tabPane1.addTabPage( document.getElementById( "tab-page-step" ) );</script>

        <fieldset id="publishing">
          <legend>&nbsp;%blockAvailability%&nbsp;</legend>
          <img src="images/shim.gif" height="4" class="shim" border="0" />
          <br />

          <input type="hidden" name="status" value="2"/>

          <table border="0" cellspacing="2" cellpadding="2">
            <tr>
              <xsl:variable name="publishfrom">
                <xsl:choose>
                  <xsl:when test="/wizarddata/wizardstate/stepstate[1]/publishing/@from">
                    <xsl:value-of select="/wizarddata/wizardstate/stepstate[1]/publishing/@from"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="/wizarddata/contents/content/@publishfrom"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:variable>

              <xsl:call-template name="textfielddatetime">
                <xsl:with-param name="name" select="'publishfrom'"/>
                <xsl:with-param name="label" select="'%fldOnlineFrom%:'"/>
                <xsl:with-param name="selectnode" select="$publishfrom"/>
                <xsl:with-param name="colspan" select="'1'"/>
                <xsl:with-param name="disabled" select="$publishingdisabled"/>
              </xsl:call-template>
            </tr>
            <tr>
              <xsl:variable name="publishto">
                <xsl:choose>
                  <xsl:when test="/wizarddata/wizardstate/stepstate[1]/publishing/@to">
                    <xsl:value-of select="/wizarddata/wizardstate/stepstate[1]/publishing/@to"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="/wizarddata/contents/content/@publishto"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:variable>

              <xsl:call-template name="textfielddatetime">
                <xsl:with-param name="name" select="'publishto'"/>
                <xsl:with-param name="label" select="'%fldOnlineTo%:'"/>
                <xsl:with-param name="selectnode" select="$publishto"/>
                <xsl:with-param name="colspan" select="'3'"/>
                <xsl:with-param name="disabled" select="$publishingdisabled"/>
              </xsl:call-template>
            </tr>
          </table>
        </fieldset>

        <fieldset id="sites">
          <legend>&nbsp;%blockSites%&nbsp;</legend>
          <img src="images/shim.gif" height="4" class="shim" border="0" />
          <table width="100%" border="0" cellspacing="2" cellpadding="0">

            <xsl:choose>
              <xsl:when test="boolean(/wizarddata/menus/menu)">

                <xsl:for-each select="/wizarddata/menus/menu">
                  <xsl:sort select="name"/>
                  <xsl:variable name="menukey" select="@key"/>

                  <tr>
                    <td nowrap="nowrap">
                      <xsl:call-template name="checkbox_site">
                        <xsl:with-param name="label" select="name"/>
                        <xsl:with-param name="name" select="'menukey'"/>
                        <xsl:with-param name="value" select="@key"/>
                        <xsl:with-param name="selectnode">
                          <xsl:choose>
                            <xsl:when test="/wizarddata/wizardstate/stepstate[1]/menu">
                              <xsl:value-of select="/wizarddata/wizardstate/stepstate[1]/menu[@key = $menukey]/@key"/>
                            </xsl:when>
                            <xsl:otherwise>
                              <xsl:value-of select="/wizarddata/contents/content/sectionnames/sectionname[@menukey = $menukey]/@menukey"/>
                            </xsl:otherwise>
                          </xsl:choose>
                        </xsl:with-param>
                      </xsl:call-template>
                    </td>
                  </tr>

                </xsl:for-each>
              </xsl:when>
              <xsl:otherwise>
                <tr>
                  <td nowrap="nowrap">
                    %msgNoSiteToPublishContentToButContentStillGoingToBeApproved%
                  </td>
                </tr>
              </xsl:otherwise>
            </xsl:choose>

          </table>
        </fieldset>
      </div>
    </div>

    <script type="text/javascript" language="JavaScript">setupAllTabs();</script>
  </xsl:template>

  <xsl:template name="step1">
    <div class="tab-pane" id="tab-pane-1">
      <script type="text/javascript" language="JavaScript">
        var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
      </script>

      <div class="tab-page" id="tab-page-step">
        <span class="tab">%headStep% 2 %of% 3: %headPublishing%</span>

        <script type="text/javascript" language="JavaScript">
          tabPane1.addTabPage( document.getElementById( "tab-page-step" ) );
          var branchOpen = new Array;
				</script>

        <fieldset>
          <legend>&nbsp;%blockDescription%&nbsp;</legend>
          <img src="images/shim.gif" height="4" class="shim" border="0" />
          <br />
          <xsl:text>%txtHelpPublishing%</xsl:text>
        </fieldset>


        <xsl:for-each select="/wizarddata/menus/menu[boolean(menuitem)]">
          <xsl:sort select="@name"/>

          <fieldset>
            <legend>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:value-of select="@name"/>
              <xsl:text>&nbsp;</xsl:text>
            </legend>
            <img src="images/shim.gif" height="4" class="shim" border="0" />

            <br />

            <xsl:variable name="menukey">
              <xsl:value-of select="@key"/>
            </xsl:variable>

            <table border="0" cellspacing="2" cellpadding="2">
              <tr>
                <xsl:call-template name="dropdown">
                  <xsl:with-param name="label" select="'%fldContentFramework%:'"/>
                  <xsl:with-param name="name">
                    <xsl:text>contentframework_</xsl:text>
                    <xsl:value-of select="$menukey"/>
                  </xsl:with-param>
                  <xsl:with-param name="id">
                    <xsl:text>contentframework_</xsl:text>
                    <xsl:value-of select="$menukey"/>
                  </xsl:with-param>
                  <xsl:with-param name="selectedkey">
                    <xsl:choose>
                      <xsl:when test="$stepState1/menu[@key = $menukey]">
                        <xsl:value-of select="$stepState1/menu[@key = $menukey]/pagetemplate/@key"/>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:value-of select="/wizarddata/contenthomes/contenthome[@menukey = $menukey]/@pagetemplatekey"/>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:with-param>
                  <xsl:with-param name="selectnode" select="/wizarddata/pagetemplates/pagetemplate[@menukey = $menukey and contenttypes/contenttype/@key = /wizarddata/contents/content/@contenttypekey]"/>
                  <xsl:with-param name="emptyrow" select="'%optNone%'"/>
                  <xsl:with-param name="required" select="'false'"/>
                  <xsl:with-param name="onchangefunction">
                    <xsl:text>dropdownChanged(this,</xsl:text>
                    <xsl:value-of select="$page"/>
                    <xsl:text>,</xsl:text>
                    <xsl:value-of select="$menukey"/>
                    <xsl:text>,</xsl:text>
                    <xsl:value-of select="/wizarddata/contents/content/@key"/>
                    <xsl:text>)</xsl:text>
                  </xsl:with-param>
                  <xsl:with-param name="disabled" select="not($categorypublish)"/>
                </xsl:call-template>
                <td>
                  <xsl:call-template name="button">
                    <xsl:with-param name="type" select="'link'"/>
                    <xsl:with-param name="caption" select="'%cmdPreview%'"/>
                    <xsl:with-param name="name" select="concat('button_', $menukey)"/>
                    <xsl:with-param name="href">
                      <xsl:text>adminpage?page=</xsl:text>
                      <xsl:value-of select="$page"/>
                      <xsl:text>&amp;op=preview&amp;menukey=</xsl:text>
                      <xsl:value-of select="$menukey"/>
                      <xsl:text>&amp;contentkey=</xsl:text>
                      <xsl:value-of select="/wizarddata/contents/content/@key"/>
                      <xsl:text>&amp;versionkey=</xsl:text>
                      <xsl:value-of select="/wizarddata/contents/content/@versionkey"/>
                      <xsl:if test="$stepState1/menu[@key = $menukey]/pagetemplate/@key or /wizarddata/contenthomes/contenthome[@menukey = $menukey]/@pagetemplatekey">
                        <xsl:text>&amp;pagetemplatekey=</xsl:text>
                        <xsl:choose>
                          <xsl:when test="$stepState1/menu[@key = $menukey]">
                            <xsl:value-of select="$stepState1/menu[@key = $menukey]/pagetemplate/@key"/>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:value-of select="/wizarddata/contenthomes/contenthome[@menukey = $menukey]/@pagetemplatekey"/>
                          </xsl:otherwise>
                        </xsl:choose>
                      </xsl:if>
                      <xsl:if test="$stepState1/menu[@key = $menukey]/home/@key or /wizarddata/contenthomes/contenthome[@menukey = $menukey]/@menuitemkey">
                        <xsl:text>&amp;menuitemkey=</xsl:text>
                        <xsl:choose>
                          <xsl:when test="$stepState1/menu[@key = $menukey]">
                            <xsl:value-of select="$stepState1/menu[@key = $menukey]/home/@key"/>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:value-of select="/wizarddata/contenthomes/contenthome[@menukey = $menukey]/@menuitemkey"/>
                          </xsl:otherwise>
                        </xsl:choose>
                      </xsl:if>
                    </xsl:with-param>
                    <xsl:with-param name="target" select="'_blank'"/>
                  </xsl:call-template>
                </td>
              </tr>
            </table>

            <br />

            <xsl:variable name="errors">
              <xsl:choose>
                <xsl:when test="/*/errors">
                  <xsl:copy-of select="/*/errors"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:copy-of select="/*/*/errors"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <xsl:variable name="name">
              <xsl:text>menuitem_home_</xsl:text>
              <xsl:value-of select="$menukey"/>
            </xsl:variable>

            <xsl:if test="exslt-common:node-set($errors)/errors/error[@name=$name]">
              <xsl:call-template name="displayerror">
                <xsl:with-param name="code" select="exslt-common:node-set($errors)/errors/error[@name=$name]/@code"/>
              </xsl:call-template>
            </xsl:if>

            <table width="100%" border="0" cellspacing="2" cellpadding="2">
              <tr>
                <th align="center" width="40">%fldSelect%</th>
                <th align="center" width="50">%lblManuallyOrder%</th>
                <th align="center" width="40">%fldHome%</th>
                <th align="left">%fldSection%</th>
              </tr>

              <xsl:call-template name="sectionradiobuttons">
                <xsl:with-param name="menukey" select="$menukey"/>
                <xsl:with-param name="root" select="."/>
                <xsl:with-param name="summary" select="false()"/>
              </xsl:call-template>
            </table>
          </fieldset>
        </xsl:for-each>
      </div>
    </div>

    <script type="text/javascript" language="JavaScript">setupAllTabs();</script>
  </xsl:template>

  <xsl:template name="sectionradiobuttons">
    <xsl:param name="menukey"/>
    <xsl:param name="root"/>
    <xsl:param name="summary"/>

    <xsl:for-each select="$root/menuitem">
			<xsl:sort select="path"/>

      <xsl:variable name="menuitemkey">
        <xsl:value-of select="@key"/>
      </xsl:variable>

      <xsl:variable name="section" select="key('menuitemkey',$menuitemkey)"/>

      <xsl:variable name="selectedmenuitemkey">
        <xsl:choose>
          <xsl:when test="$section/@filtered = 'true'">
            <xsl:value-of select="$menuitemkey"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$stepState1/menu[@key = $menukey]/menuitem[@key = $menuitemkey]/@key"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:variable name="selectedhomekey">
        <xsl:choose>
          <xsl:when test="$stepState1/menu[@key = $menukey]">
            <xsl:value-of select="$stepState1/menu[@key = $menukey]/home/@key"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="/wizarddata/contenthomes/contenthome[@menukey = $menukey]/@menuitemkey"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:variable name="menuInStepState" select="$stepState1/menu[@key = $menukey]"/>



      <xsl:if test="boolean($section) and (not($summary) or @key = $selectedmenuitemkey or @key = $selectedhomekey)">

        <tr>
          <xsl:variable name="onclick">
              <xsl:text>selectCheckBoxChanged(this, "menuitem_manually_order_</xsl:text>
              <xsl:value-of select="$menukey"/>
              <xsl:text>", "menuitem_home_</xsl:text>
              <xsl:value-of select="$menukey"/>
              <xsl:text>", </xsl:text>
              <xsl:value-of select="$menukey"/>
              <xsl:text>)</xsl:text>
          </xsl:variable>
          <xsl:call-template name="checkbox_nolabel">
            <xsl:with-param name="name" select="concat('menuitem_select_', $menukey)"/>
            <xsl:with-param name="value" select="$menuitemkey"/>
            <xsl:with-param name="selectnode">
              <xsl:choose>
                <xsl:when test="$section/@filtered = 'true'">
                  <xsl:value-of select="$menuitemkey"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="$menuInStepState/menuitem[@key = $menuitemkey]/@key"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="onclick" select="$onclick"/>
            <xsl:with-param name="disabled" select="$summary or not($section/@marked = 'true') or $section/@filtered = 'true'"/>
          </xsl:call-template>

          <xsl:call-template name="checkbox_nolabel">
            <xsl:with-param name="name" select="concat('menuitem_manually_order_', $menukey)"/>
            <xsl:with-param name="value" select="$menuitemkey"/>
            <xsl:with-param name="visible" select="$section/@ordered = 'true'"/>
            <xsl:with-param name="selectnode">
              <xsl:choose>
                <xsl:when test="$menuInStepState">
                  <xsl:value-of select="$menuInStepState/menuitem[@key = $menuitemkey and @manuallyOrder = 'true']/@key"/>
                </xsl:when>
                <xsl:otherwise>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="disabled" select="$summary or (not($menuInStepState/menuitem/@key = @key) and (not($section/@filtered = 'true') or not(/wizarddata/contents/content/sectionnames/sectionname[@menuitemkey = $menuitemkey]/@approved = 'false')))"/>
          </xsl:call-template>

        <!--  <xsl:variable name="disabled">
            <xsl:choose>
              <xsl:when test="$selectedmenuitemkey = $menuitemkey">
                <xsl:value-of select="false()"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="true()"/>
              </xsl:otherwise>
            </xsl:choose>

          </xsl:variable>-->
<!--
          <textarea rows="" cols="">
            <xsl:value-of select="$disabled"/>
          </textarea>-->

          <xsl:call-template name="radiobutton_nolabel">
            <xsl:with-param name="name" select="concat('menuitem_home_', $menukey)"/>
            <xsl:with-param name="value" select="$menuitemkey"/>
            <xsl:with-param name="selectnode">
              <xsl:choose>
                <xsl:when test="$menuInStepState">
                  <xsl:value-of select="$menuInStepState/home/@key"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="/wizarddata/contenthomes/contenthome[@menukey = $menukey]/@menuitemkey"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="disabled" select="not($selectedmenuitemkey = $menuitemkey) or $summary"/>
            <xsl:with-param name="onclick">
              <xsl:text>radiobuttonChanged(this, </xsl:text>
              <xsl:value-of select="$page"/>
              <xsl:text>,</xsl:text>
              <xsl:value-of select="$menukey"/>
              <xsl:text>,</xsl:text>
              <xsl:value-of select="/wizarddata/contents/content/@key"/>
              <xsl:text>)</xsl:text>
            </xsl:with-param>
          </xsl:call-template>
          <td>
            <img border="0">
              <xsl:attribute name="src">
                <xsl:text>images/icon_menuitem</xsl:text>
                <xsl:choose>
                  <xsl:when test="@type = 'section'">
                    <xsl:text>_section</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>_sectionpage</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="@visible = 'yes'">_show</xsl:if>
                <xsl:text>.gif</xsl:text>
							</xsl:attribute>
						</img>
            			<img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
						<xsl:call-template name="string-replace-all">
							<xsl:with-param name="text" select="path"/>
							<xsl:with-param name="replace" select="'/'"/>
							<xsl:with-param name="by" select="' / '" />
						</xsl:call-template>
					</td>
				</tr>
			</xsl:if>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="step2">
    <div class="tab-pane" id="tab-pane-1">
      <script type="text/javascript" language="JavaScript">
        var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
      </script>

      <div class="tab-page" id="tab-page-step">
        <span class="tab">
          <xsl:text>%headStep% 2-</xsl:text>
          <xsl:value-of select="$sectionnumber" />
          <xsl:text> of 3: %headPositionContenInSection%</xsl:text>
        </span>

        <script type="text/javascript" language="JavaScript">tabPane1.addTabPage( document.getElementById( "tab-page-step" ) );</script>

        <fieldset>
          <legend>
            <xsl:text>&nbsp;%blockSection%: </xsl:text>
            <xsl:value-of select="$path"/>
            <xsl:text>&nbsp;</xsl:text>
          </legend>
          <img src="images/shim.gif" height="4" class="shim" border="0"/>
          <br/>

          <input type="hidden" name="menuitemkey">
            <xsl:attribute name="value">
              <xsl:value-of select="$menuitemkey"/>
            </xsl:attribute>
          </input>

          <input type="hidden" name="contentidx"/>

          <table width="99%" cellspacing="0" cellpadding="0" class="browsetable">
            <tr>
              <td class="browsetablecolumnheader default-cursor" align="left">%fldTitle%</td>
              <td class="browsetablecolumnheader default-cursor" align="center" width="100">%fldStatus%</td>
              <td class="browsetablecolumnheader default-cursor" align="center" width="100">%fldLastModified%</td>
              <td class="browsetablecolumnheader default-cursor" width="90">&nbsp;</td>
            </tr>

            <xsl:for-each select="/wizarddata/contenttitles/contenttitle">
              <xsl:variable name="key" select="@key"/>

              <xsl:variable name="td-css-class">
                <xsl:text>browsetablecell</xsl:text>
                <xsl:if test="position() = last()">
                  <xsl:text> row-last</xsl:text>
                </xsl:if>
              </xsl:variable>

              <tr>
                <xsl:choose>
                  <xsl:when test="position() mod 2 = 1">
                    <xsl:attribute name="class">tablerowpainter_darkrow</xsl:attribute>
                    <xsl:attribute name="onmouseover">javascript:this.className='tablerowpainter_mouseoverrow';</xsl:attribute>
                    <xsl:attribute name="onmouseout">javascript:this.className='tablerowpainter_darkrow';</xsl:attribute>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:attribute name="class">tablerowpainter_lightrow</xsl:attribute>
                    <xsl:attribute name="onmouseover">javascript:this.className='tablerowpainter_mouseoverrow'</xsl:attribute>
                    <xsl:attribute name="onmouseout">javascript:this.className='tablerowpainter_lightrow';</xsl:attribute>
                  </xsl:otherwise>
                </xsl:choose>

                <!--xsl:attribute name="onmouseover">javascript:this.style.backgroundColor="#FFFFFF"</xsl:attribute>
                              	<xsl:attribute name="onmouseout">javascript:this.style.backgroundColor="#EEEEEE"</xsl:attribute-->

                <td class="{$td-css-class}" title="%msgClickToEdit%">
                  <xsl:choose>
                    <xsl:when test="@key = /wizarddata/wizardstate/stepstate[1]/content/@key">
                      <b><xsl:value-of select="."/></b>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="."/>
                    </xsl:otherwise>
                  </xsl:choose>
                </td>

                <td class="{$td-css-class}" title="%msgClickToEdit%" align="center">
                  <xsl:call-template name="publishstatus">
                    <xsl:with-param name="key" select="@key"/>
                    <xsl:with-param name="state" select="@state"/>
                  </xsl:call-template>
                </td>

                <td class="browsetablecell" title="%msgClickToEdit%" align="center">
                  <xsl:value-of select="@timestamp"/>
                </td>

                <td class="{$td-css-class}" align="center">
                  <input type="hidden" name="content">
                    <xsl:attribute name="value">
                      <xsl:value-of select="@key"/>
                    </xsl:attribute>
                  </input>

                  <xsl:call-template name="button">
                    <xsl:with-param name="style" select="'flat'"/>
                    <xsl:with-param name="type" select="'link'"/>
                    <xsl:with-param name="image" select="'images/icon_move_up.gif'"/>
                    <xsl:with-param name="image-disabled" select="'images/icon_move_up-disabled.gif'"/>
                    <xsl:with-param name="href">
                      <xsl:text>javascript:moveContent(</xsl:text>
                      <xsl:value-of select="position() - 1"/>
                      <xsl:text>, "moveup")</xsl:text>
                    </xsl:with-param>
                  </xsl:call-template>

                  <img border="0" src="images/1x1.gif" height="1" width="20"/>

                  <xsl:call-template name="button">
                    <xsl:with-param name="style" select="'flat'"/>
                    <xsl:with-param name="type" select="'link'"/>
                    <xsl:with-param name="image" select="'images/icon_move_down.gif'"/>
                    <xsl:with-param name="image-disabled" select="'images/icon_move_down-disabled.gif'"/>
                    <xsl:with-param name="href">
                      <xsl:text>javascript:moveContent(</xsl:text>
                      <xsl:value-of select="position() - 1"/>
                      <xsl:text>, "movedown")</xsl:text>
                    </xsl:with-param>
                  </xsl:call-template>
                </td>

              </tr>
            </xsl:for-each>
          </table>
        </fieldset>
      </div>

      <script type="text/javascript" language="JavaScript">setupAllTabs();</script>
    </div>
  </xsl:template>

  <xsl:template name="step3">
    <div class="tab-pane" id="tab-pane-1">
      <script type="text/javascript" language="JavaScript">var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );</script>

      <div class="tab-page" id="tab-page-step">
        <span class="tab">%headStep% 3 %of% 3: %headConfirmPublishing%</span>

        <script type="text/javascript" language="JavaScript">
          tabPane1.addTabPage( document.getElementById( "tab-page-step" ) );
					var branchOpen = new Array;
				</script>

        <input type="hidden" name="status" value="2"/>
        
        <fieldset>
          <xsl:if test="not($originalstatus = 2) and not($status = 2)">
            <xsl:attribute name="style">
              <xsl:text>display: none;</xsl:text>
            </xsl:attribute>
          </xsl:if>

          <legend>&nbsp;%blockAvailability%&nbsp;</legend>
          <img src="images/shim.gif" height="4" class="shim" border="0" />
          <br />
          <table border="0" cellspacing="2" cellpadding="2">
            <tr>
              <xsl:call-template name="readonlydatetime">
                <xsl:with-param name="name" select="'publishfrom'" />
                <xsl:with-param name="label" select="'%fldOnlineFrom%:'" />
                <xsl:with-param name="selectnode" select="/wizarddata/wizardstate/stepstate[1]/publishing/@from" />
                <xsl:with-param name="colspan" select="'1'" />
              </xsl:call-template>
            </tr>
            <tr>
              <xsl:call-template name="readonlydatetime">
                <xsl:with-param name="name" select="'publishto'" />
                <xsl:with-param name="label" select="'%fldOnlineTo%:'" />
                <xsl:with-param name="selectnode" select="/wizarddata/wizardstate/stepstate[1]/publishing/@to" />
                <xsl:with-param name="colspan" select="'3'" />
              </xsl:call-template>
            </tr>
          </table>
        </fieldset>

        <xsl:if test="($originalstatus = 0 and $status = 1) or ($originalstatus = 1 and $status = 0)">
          <fieldset id="message">
            <legend>&nbsp;%blockMessage%&nbsp;</legend>
            <img src="images/shim.gif" height="4" class="shim" border="0" />
            <br />

            <table border="0" cellspacing="2" cellpadding="2">
              <tr>
                <td>%fldRecipients%</td>
              </tr>
              <xsl:for-each select="/wizarddata/wizardstate/stepstate[1]/recipients/recipient">
                <tr>
                  <td width="20">
                    <input type="checkbox" name="recipientkeys" value="{@key}" disabled="disabled" checked="checked"/>
                  </td>
                  <td width="400">
                    <xsl:value-of select="@name"/> (<xsl:value-of select="@email"/>)
                  </td>
                </tr>
              </xsl:for-each>
              
              <tr>
                <td colspan="3">%fldMessage%</td>
              </tr>
              <tr>
                <td colspan="3">
                  <textarea rows="12" cols="100" name="body" disabled="disabled">
                    <xsl:value-of select="/wizarddata/wizardstate/stepstate[1]/message" disable-output-escaping="yes"/>
                  </textarea>
                  <br/><br/>
                </td>
              </tr>
            </table>
          </fieldset>
        </xsl:if>

        <xsl:for-each select="/wizarddata/menus/menu[boolean(menuitem)]">
          <xsl:sort select="@name"/>

          <fieldset>
            <legend>
              <xsl:text>&nbsp;</xsl:text>
              <xsl:value-of select="@name"/>
              <xsl:text>&nbsp;</xsl:text>
            </legend>
            <img src="images/shim.gif" height="4" class="shim" border="0" />
            <br />

            <xsl:variable name="menukey">
              <xsl:value-of select="@key"/>
            </xsl:variable>

            <table width="100%" border="0" cellspacing="2" cellpadding="2">
              <tr>
                <xsl:call-template name="readonlyvalue">
                  <xsl:with-param name="name">
                    <xsl:text>contentframework_</xsl:text>
                    <xsl:value-of select="$menukey"/>
                  </xsl:with-param>
                  <xsl:with-param name="label" select="'%fldContentFramework%:'" />
                  <xsl:with-param name="selectnode">
                    <xsl:choose>
                      <xsl:when test="/wizarddata/pagetemplates/pagetemplate[@menukey = $menukey]">
                        <xsl:value-of select="/wizarddata/pagetemplates/pagetemplate[@menukey = $menukey]/name" />
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:text>%optNone%</xsl:text>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:with-param>
                  <xsl:with-param name="colspan" select="'1'" />
                </xsl:call-template>
              </tr>
            </table>

            <br/>

            <table width="100%" border="0" cellspacing="2" cellpadding="2">
              <tr>
                <th align="center" width="40">%fldSelect%</th>
                <th align="center" width="50">%lblManuallyOrder%</th>
                <th align="center" width="40">%fldHome%</th>
                <th align="left">%fldSection%</th>
              </tr>

              <xsl:call-template name="sectionradiobuttons">
                <xsl:with-param name="menukey" select="@key"/>
                <xsl:with-param name="root" select="."/>
                <xsl:with-param name="summary" select="true()"/>
              </xsl:call-template>
            </table>
          </fieldset>
        </xsl:for-each>
      </div>

      <script type="text/javascript" language="JavaScript">setupAllTabs();</script>
    </div>
  </xsl:template>

  <xsl:template name="categoryheader_section">
    <xsl:text>%headPublishingWizard% : </xsl:text>
    <xsl:text>%headContentRepositories% </xsl:text>
    <xsl:call-template name="categoryheader">
      <xsl:with-param name="rootelem" select="/wizarddata" />
      <xsl:with-param name="nolinks" select="true()" />
    </xsl:call-template>
    <xsl:text> / </xsl:text>
    <xsl:value-of select="$contenttitle" />

  </xsl:template>

  <xsl:template name="checkbox_site">
    <xsl:param name="label" />
    <xsl:param name="name" />
    <xsl:param name="href" select="''"/>
    <xsl:param name="value"/>
    <xsl:param name="selectnode" />
    <xsl:param name="disabled" select="false()" />


    <input type="checkbox">
      <xsl:if test="$selectnode = $value">
        <xsl:attribute name="checked">
          <xsl:value-of select="'checked'" />
        </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="value">
        <xsl:value-of select="$value" />
      </xsl:attribute>
      <xsl:attribute name="name">
        <xsl:value-of select="$name" />
      </xsl:attribute>
      <xsl:attribute name="id">
        <xsl:value-of select="$name" />
      </xsl:attribute>
      <!--xsl:attribute name="onchange">
					<xsl:value-of select="'checkBoxSiteChanged(this)'" />
        </xsl:attribute-->
      <xsl:attribute name="onclick">
        <xsl:value-of select="'checkBoxSiteChanged(this)'" />
      </xsl:attribute>
      <xsl:if test="$disabled">
        <xsl:attribute name="disabled">
          <xsl:text>disabled</xsl:text>
        </xsl:attribute>
      </xsl:if>
      <xsl:text>&nbsp;</xsl:text>
      <xsl:choose>
        <xsl:when test="$href != ''">
          <a href="{$href}" target="_blank">
            <img src="images/icon_site.gif" border="0"/>
            <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
            <xsl:value-of select="$label" />
          </a>
        </xsl:when>
        <xsl:otherwise>
          <img src="images/icon_site.gif" border="0"/>
          <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
          <xsl:value-of select="$label" />
        </xsl:otherwise>
      </xsl:choose>
    </input>
  </xsl:template>

  <xsl:template name="checkbox_nolabel">
    <xsl:param name="name"/>
    <xsl:param name="value"/>
    <xsl:param name="selectnode"/>
    <xsl:param name="onpropertychange" select="''"/>
    <xsl:param name="onchange" select="''"/>
    <xsl:param name="onclick" select="''"/>
    <xsl:param name="disabled" select="false()"/>
    <xsl:param name="align" select="'center'"/>
    <xsl:param name="visible" select="true()"/>

    <td valign="baseline" nowrap="nowrap">
      <xsl:attribute name="align">
        <xsl:value-of select="$align"/>
      </xsl:attribute>
      <input type="checkbox">
        <xsl:if test="$selectnode = $value">
          <xsl:attribute name="checked">
            <xsl:value-of select="'checked'" />
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="$visible = false()">
          <xsl:attribute name="style">
            <xsl:text>display:none</xsl:text>
          </xsl:attribute>
        </xsl:if>
        <xsl:attribute name="value">
          <xsl:value-of select="$value" />
        </xsl:attribute>
        <xsl:attribute name="name">
          <xsl:value-of select="$name" />
        </xsl:attribute>
        <xsl:attribute name="id">
          <xsl:value-of select="$name" />
        </xsl:attribute>
        <xsl:if test="$onpropertychange != ''">
          <xsl:attribute name="onpropertychange">
            <xsl:value-of select="$onpropertychange" />
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="$onchange != ''">
          <xsl:attribute name="onchange">
            <xsl:value-of select="$onchange" />
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="$onclick != ''">
          <xsl:attribute name="onclick">
            <xsl:value-of select="$onclick" />
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="$disabled">
          <xsl:attribute name="disabled">
            <xsl:text>disabled</xsl:text>
          </xsl:attribute>
        </xsl:if>
      </input>
    </td>
  </xsl:template>

  <xsl:template name="radiobutton_nolabel">
    <xsl:param name="name"/>
    <xsl:param name="value"/>
    <xsl:param name="selectnode"/>
    <xsl:param name="disabled" select="false()"/>
    <xsl:param name="align" select="'center'"/>
    <xsl:param name="onpropertychange" select="''"/>
    <xsl:param name="onchange" select="''"/>
    <xsl:param name="onclick" select="''"/>

    <td valign="baseline" nowrap="nowrap">
      <xsl:attribute name="align">
        <xsl:value-of select="$align"/>
      </xsl:attribute>
      <input type="radio">
        <xsl:if test="$selectnode = $value">
          <xsl:attribute name="checked">
            <xsl:value-of select="'checked'" />
          </xsl:attribute>
        </xsl:if>
        <xsl:attribute name="value">
          <xsl:value-of select="$value" />
        </xsl:attribute>
        <xsl:attribute name="name">
          <xsl:value-of select="$name" />
        </xsl:attribute>
        <xsl:attribute name="id">
          <xsl:value-of select="$name" />
        </xsl:attribute>
        <xsl:if test="$onpropertychange != ''">
          <xsl:attribute name="onpropertychange">
            <xsl:value-of select="$onpropertychange" />
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="$onchange != ''">
          <xsl:attribute name="onchange">
            <xsl:value-of select="$onchange" />
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="$onclick != ''">
          <xsl:attribute name="onclick">
            <xsl:value-of select="$onclick" />
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="$disabled">
          <xsl:attribute name="disabled">
            <xsl:text>disabled</xsl:text>
          </xsl:attribute>
        </xsl:if>
      </input>
    </td>
  </xsl:template>

  <xsl:template name="wizardheader">
    <xsl:text>%headPublishingWizard%: </xsl:text>
    <xsl:call-template name="publishstatus">
      <xsl:with-param name="state" select="/wizarddata/contents/content/@state"/>
      <xsl:with-param name="publishfrom" select="/wizarddata/contents/content/@publishfrom"/>
      <xsl:with-param name="publishto" select="/wizarddata/contents/content/@publishto"/>
    </xsl:call-template>
    <xsl:text>&nbsp;</xsl:text>
    <xsl:value-of select="$contenttitle" />
  </xsl:template>

</xsl:stylesheet>