<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp   "&#160;">
]>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html"/>
    
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/textfield.xsl"/>
    <xsl:include href="common/tablecolumnheader.xsl"/>
    <xsl:include href="common/tablerowpainter.xsl"/>
    <xsl:include href="common/publishstatus.xsl"/>
    <xsl:include href="common/formatdate.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>

    <!-- parameter for all steps -->
    <xsl:param name="contenttitle"/>
    <xsl:param name="contenttypekey"/>
        
    <!-- parameter for step 2 -->
    <xsl:param name="currentsectionkey"/>
    <xsl:param name="currentsectionpath"/>

    <xsl:template name="step0">
        <div class="tab-pane" id="tab-pane-1">
            <script type="text/javascript" language="JavaScript">
                var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );

                <xsl:variable name="totalsectionscount">
                    <xsl:value-of select="count(/wizarddata/wizardstate/stepstate[@id = /wizarddata/wizardstate/@currentstepstateid]/section)"/>
                </xsl:variable>
                
                <xsl:if test="number($totalsectionscount) &gt; 0">
                    <xsl:text>totalSectionsCount = </xsl:text>
                    <xsl:value-of select="$totalsectionscount"/>
                    <xsl:text>;</xsl:text>
                </xsl:if>
            </script>
            
            <div class="tab-page" id="tab-page-step">
                <span class="tab">%headStep% 1 %of% 3: %headChooseSections%</span>
                
                <script type="text/javascript" language="JavaScript">
                    tabPane1.addTabPage( document.getElementById( "tab-page-step" ) );

                    var branchOpen = new Array;
                </script>

                <fieldset>
                    <legend>&nbsp;%blockDescription%&nbsp;</legend>
                    <img src="images/shim.gif" height="4" class="shim" border="0"/>
                    <br/>
                    <xsl:text>%txtDescChooseSections%</xsl:text>
                </fieldset>
                
                <fieldset>
                    <legend>&nbsp;%blockSections%&nbsp;</legend>
                    <img src="images/shim.gif" height="4" class="shim" border="0"/>
                    <br/>
                        
                    <xsl:variable name="menucount" select="count(/wizarddata/menus/menu[@key = /wizarddata/sections/section/@menukey])"/>

                    <xsl:choose>
                        <xsl:when test="$menucount &gt; 0">
                            <table cellspacing="0" cellpadding="0" border="0" class="menuItem">
                                
                                <xsl:for-each select="/wizarddata/menus/menu[@key = /wizarddata/sections/section/@menukey]">
                                    
                                    <xsl:variable name="menukey">
                                        <xsl:value-of select="@key"/>
                                    </xsl:variable>
                                    
                                    <tr style="height: 16px;" valign="middle">
                                        <td width="16">
                                            <a>
                                                <xsl:attribute name="href">
                                                    <xsl:text>javaScript:openBranch('-menu</xsl:text>
                                                    <xsl:value-of select="@key"/>
                                                    <xsl:text>');</xsl:text>
                                                </xsl:attribute>
                                                <xsl:choose>
                                                    <xsl:when test="$menucount &gt; 1">
                                                        <xsl:choose>
                                                            <xsl:when test="position() = last()">
                                                                <img id="img-menu{@key}" src="javascript/images/Lplus.png" border="0"/>
                                                            </xsl:when>
                                                            <xsl:otherwise>
                                                                <img id="img-menu{@key}" src="javascript/images/Tplus.png" border="0"/>
                                                            </xsl:otherwise>
                                                        </xsl:choose>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:choose>
                                                            <xsl:when test="position() = last()">
                                                                <img id="img-menu{@key}" src="javascript/images/Lminus.png" border="0"/>
                                                            </xsl:when>
                                                            <xsl:otherwise>
                                                                <img id="img-menu{@key}" src="javascript/images/Tminus.png" border="0"/>
                                                            </xsl:otherwise>
                                                        </xsl:choose>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </a>
                                        </td>
                                        <td>
                                            <img src="images/icon_site.gif" border="0"/>
                                            <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                                            <xsl:value-of select="name"/>
                                        </td>
                                    </tr>
                                    
                                    <tr valign="top">
                                        <xsl:attribute name="id">
                                            <xsl:text>id-menu</xsl:text>
                                            <xsl:value-of select="@key"/>
                                        </xsl:attribute>
                                        <xsl:if test="$menucount &gt; 1">
                                            <xsl:attribute name="style">
                                                <xsl:text>display: none</xsl:text>
                                            </xsl:attribute>
                                        </xsl:if>
                                        
                                        <td width="16">
                                            <xsl:if test="position() != last()">
                                                <xsl:attribute name="background">javascript/images/I.png</xsl:attribute>
                                            </xsl:if>
                                            <img border="0" src="images/shim.gif"/>
                                        </td>
                                        
                                        <td>
                                            <table cellspacing="0" cellpadding="0" class="menuItem">
                                                <xsl:for-each select="/wizarddata/sections/section[@menukey = $menukey]">
                                                    <xsl:call-template name="sectionlist">
                                                        <xsl:with-param name="element" select="."/>
                                                        <xsl:with-param name="sectionlast" select="position() = last()"/>
                                                    </xsl:call-template>
                                                </xsl:for-each>
                                            </table>
                                        </td>
                                    </tr>
                                    
                                </xsl:for-each>
                            </table>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>%msgNoSectionsAvailableForPublishing%</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                            
                </fieldset>
            </div>
        </div>
        
        <script type="text/javascript" language="JavaScript">
            setupAllTabs();
        </script>
    </xsl:template>

  <xsl:template name="step1">
      <div class="tab-pane" id="tab-pane-1">
          <script type="text/javascript" language="JavaScript">
              var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );

              <xsl:variable name="totalsectionscount">
                  <xsl:value-of select="count(/wizarddata/wizardstate/stepstate[@id = /wizarddata/wizardstate/@currentstepstateid]/section)"/>
              </xsl:variable>

              <xsl:if test="number($totalsectionscount) &gt; 0">
                  <xsl:text>totalSectionsCount = </xsl:text>
                  <xsl:value-of select="$totalsectionscount"/>
                  <xsl:text>;</xsl:text>
              </xsl:if>

              <xsl:variable name="orderedsectionscount">
                  <xsl:value-of select="count(/wizarddata/wizardstate/stepstate[@id = /wizarddata/wizardstate/@currentstepstateid]/section[@ordered = 'true'])"/>
              </xsl:variable>

              <xsl:if test="number($orderedsectionscount) &gt; 0">
                  <xsl:text>orderedSectionsCount = </xsl:text>
                  <xsl:value-of select="$orderedsectionscount"/>
                  <xsl:text>;</xsl:text>
              </xsl:if>
          </script>
          
          <div class="tab-page" id="tab-page-step">
              <span class="tab">%headStep% 2 %of% 3: %headApproveSections%</span>
              
              <script type="text/javascript" language="JavaScript">
                  tabPane1.addTabPage( document.getElementById( "tab-page-step" ) );

                  var branchOpen = new Array;
              </script>

              <fieldset>
                  <legend>&nbsp;%blockDescription%&nbsp;</legend>
                  <img src="images/shim.gif" height="4" class="shim" border="0"/>
                  <br/>
                  <xsl:text>%txtDescApproveSections%</xsl:text>
              </fieldset>
              
              <fieldset>
                  <legend>&nbsp;%blockSections%&nbsp;</legend>
                  <img src="images/shim.gif" height="4" class="shim" border="0"/>
                  <br/>
                  <table cellspacing="0" cellpadding="0" class="menuItem">

                      <xsl:variable name="menucount" select="count(/wizarddata/menus/menu[@key = /wizarddata/sections/section/@menukey])"/>
                      
                      <xsl:for-each select="/wizarddata/menus/menu[@key = /wizarddata/sections/section/@menukey]">
                          
                          <xsl:variable name="menukey">
                              <xsl:value-of select="@key"/>
                          </xsl:variable>
                          
                          <tr style="height: 16px;" valign="middle">
                              <td width="16">
                                  <a>
                                      <xsl:attribute name="href">
                                          <xsl:text>javaScript:openBranch('-menu</xsl:text>
                                          <xsl:value-of select="@key"/>
                                          <xsl:text>');</xsl:text>
                                      </xsl:attribute>
                                      <xsl:choose>
                                          <xsl:when test="position() = last()">
                                              <img id="img-menu{@key}" src="javascript/images/Lminus.png" border="0"/>
                                          </xsl:when>
                                          <xsl:otherwise>
                                              <img id="img-menu{@key}" src="javascript/images/Tminus.png" border="0"/>
                                          </xsl:otherwise>
                                      </xsl:choose>
                                  </a>
                              </td>
                              <td>
                                  <img src="images/icon_site.gif" border="0"/>
                                  <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                                  <xsl:value-of select="name"/>
                              </td>
                          </tr>
                          
                          <tr valign="top">
                              <xsl:attribute name="id">
                                  <xsl:text>id-menu</xsl:text>
                                  <xsl:value-of select="@key"/>
                              </xsl:attribute>
                              
                              <td width="16">
                                  <xsl:if test="position() != last()">
                                      <xsl:attribute name="background">javascript/images/I.png</xsl:attribute>
                                  </xsl:if>
                                  <img border="0" src="images/shim.gif"/>
                              </td>
                              
                              <td>
                                  <table cellspacing="0" cellpadding="0" class="menuItem">
                                      <xsl:for-each select="/wizarddata/sections/section[@menukey = $menukey]">
                                          <xsl:call-template name="sectionlist">
                                              <xsl:with-param name="element" select="."/>
                                              <xsl:with-param name="stepstate" select="/wizarddata/wizardstate/stepstate[1]"/>
                                              <xsl:with-param name="sectionlast" select="position() = last()"/>
                                              <xsl:with-param name="approveRequired" select="true()"/>
                                          </xsl:call-template>
                                      </xsl:for-each>
                                  </table>
                              </td>
                          </tr>
                          
                      </xsl:for-each>
                  </table>
              </fieldset>
          </div>
      </div>
        
      <script type="text/javascript" language="JavaScript">
          setupAllTabs();
      </script>
  </xsl:template>

  <xsl:template name="step2">
      <div class="tab-pane" id="tab-pane-1">
          <script type="text/javascript" language="JavaScript">
              var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
          </script>
          
          <div class="tab-page" id="tab-page-step">
              <span class="tab">
                  <xsl:text>%headStep% 2-</xsl:text>
                  <xsl:call-template name="subsectionnumber"/>
                  <xsl:text> of 3: %headPositionContent% </xsl:text>
              </span>
              
              <script type="text/javascript" language="JavaScript">
                  tabPane1.addTabPage( document.getElementById( "tab-page-step" ) );
              </script>

              <fieldset>
                  <legend>&nbsp;%blockDescription%&nbsp;</legend>
                  <img src="images/shim.gif" height="4" class="shim" border="0"/>
                  <br/>
                  <xsl:text>%txtDescPositionContent%</xsl:text>
              </fieldset>
              
              <fieldset>
                  <legend>
                      <xsl:text>&nbsp;%blockSection%: </xsl:text>
                      <xsl:value-of select="$currentsectionpath"/>
                      <xsl:text>&nbsp;</xsl:text>
                  </legend>
                  <img src="images/shim.gif" height="4" class="shim" border="0"/>
                  <br/>
                  
                  <input type="hidden" name="sectionkey">
                      <xsl:attribute name="value">
                          <xsl:value-of select="$currentsectionkey"/>
                      </xsl:attribute>
                  </input>
                  
                  <input type="hidden" name="contentidx"/>
                  
                  <table width="99%" cellspacing="0" cellpadding="0" class="browsetable">
                      <tr>
                          <th align="left">%fldTitle%</th>
                          <th align="center" width="100">%fldStatus%</th>
                          <th align="center" width="100">%fldLastModified%</th>
                          <th width="90"/>
                      </tr>
                      
                      <xsl:for-each select="/wizarddata/contenttitles/contenttitle">
                          <xsl:variable name="key" select="@key"/>
                          
                          <tr>
                              <xsl:attribute name="onmouseover">javascript:this.style.backgroundColor="#FFFFFF"</xsl:attribute>
                              <xsl:attribute name="onmouseout">javascript:this.style.backgroundColor="#EEEEEE"</xsl:attribute>
                              
                              <td class="browsetablecell" title="%msgClickToEdit%">
                                  <xsl:choose>
                                      <xsl:when test="@key = /wizarddata/wizardstate/stepstate[1]/content/@key">
                                          <b><xsl:value-of select="."/></b>
                                      </xsl:when>
                                      <xsl:otherwise>
                                          <xsl:value-of select="."/>
                                      </xsl:otherwise>
                                  </xsl:choose>
                              </td>
                              
                              <td class="browsetablecell" title="%msgClickToEdit%" align="center">
                                  <xsl:call-template name="publishstatus">
                                      <xsl:with-param name="key" select="@key"/>
                                      <xsl:with-param name="state" select="@state"/>
                                  </xsl:call-template>
                              </td>
                              
                              <td class="browsetablecell" title="%msgClickToEdit%" align="center">
                                  <xsl:value-of select="@timestamp"/>
                              </td>
                              
                              <td class="browsetablecell" align="center">
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
        
          <script type="text/javascript" language="JavaScript">
              setupAllTabs();
          </script>
      </div>
  </xsl:template>

   <xsl:template name="step3">
       <div class="tab-pane" id="tab-pane-1">
           <script type="text/javascript" language="JavaScript">
               var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
           </script>
           
           <div class="tab-page" id="tab-page-step">
               <span class="tab">%headStep% 3 %of% 3: %headConfirmPublishing%</span>
               
               <script type="text/javascript" language="JavaScript">
                   tabPane1.addTabPage( document.getElementById( "tab-page-step" ) );

                   var branchOpen = new Array;
               </script>

               <fieldset>
                   <legend>&nbsp;%blockDescription%&nbsp;</legend>
                   <img src="images/shim.gif" height="4" class="shim" border="0"/>
                   <br/>
                   <xsl:text>%txtDescSummary%</xsl:text>
               </fieldset>

               <fieldset>
                   <legend>&nbsp;%blockSections%&nbsp;</legend>
                   <img src="images/shim.gif" height="4" class="shim" border="0"/>
                   <br/>

                   <table width="99%" cellspacing="0" cellpadding="0" class="menuItem">
                       
                       <xsl:for-each select="/wizarddata/menus/menu[@key = /wizarddata/sections/section/@menukey]">
                           
                           <xsl:variable name="menukey">
                               <xsl:value-of select="@key"/>
                           </xsl:variable>
                           
                           <tr style="height: 16px;" valign="middle">
                               <td width="16">
                                   <a>
                                       <xsl:attribute name="href">
                                           <xsl:text>javaScript:openBranch('-menu</xsl:text>
                                           <xsl:value-of select="@key"/>
                                           <xsl:text>');</xsl:text>
                                       </xsl:attribute>
                                       <xsl:choose>
                                           <xsl:when test="position() = last()">
                                               <img id="img-menu{@key}" src="javascript/images/Lminus.png" border="0"/>
                                           </xsl:when>
                                           <xsl:otherwise>
                                               <img id="img-menu{@key}" src="javascript/images/Tminus.png" border="0"/>
                                           </xsl:otherwise>
                                       </xsl:choose>
                                   </a>
                               </td>
                               <td>
                                   <img src="images/icon_site.gif" border="0"/>
                                   <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                                   <xsl:value-of select="name"/>
                               </td>
                           </tr>
                           
                           <tr valign="top">
                               <xsl:attribute name="id">
                                   <xsl:text>id-menu</xsl:text>
                                   <xsl:value-of select="@key"/>
                               </xsl:attribute>
                               
                               <td width="16">
                                   <xsl:if test="position() != last()">
                                       <xsl:attribute name="background">javascript/images/I.png</xsl:attribute>
                                   </xsl:if>
                                   <img border="0" src="images/shim.gif"/>
                               </td>
                               
                               <td>
                                   <table cellspacing="0" cellpadding="0" class="menuItem">
                                       <xsl:for-each select="/wizarddata/sections/section[@menukey = $menukey]">
                                           <xsl:call-template name="sectionlist">
                                               <xsl:with-param name="element" select="."/>
                                               <xsl:with-param name="stepstate" select="/wizarddata/wizardstate/stepstate[1]"/>
                                               <xsl:with-param name="sectionlast" select="position() = last()"/>
                                               <xsl:with-param name="checkbox" select="false()"/>
                                               <xsl:with-param name="markapproved" select="true()"/>
                                           </xsl:call-template>
                                       </xsl:for-each>
                                   </table>
                               </td>
                           </tr>
                           
                       </xsl:for-each>
                   </table>
               </fieldset>
           </div>
        
           <script type="text/javascript" language="JavaScript">
               setupAllTabs();
           </script>
       </div>
   </xsl:template>

  <xsl:template name="sectionlist">
      <xsl:param name="element"/>
      <xsl:param name="indent" select="''"/>
      <xsl:param name="stepstate"/>
      <xsl:param name="sectionlast" select="false()"/>
      <xsl:param name="approveRequired" select="false()"/>
      <xsl:param name="checkbox" select="true()"/>
      <xsl:param name="markapproved" select="false()"/>
      
      <xsl:if test="$element/@marked = 'true' and ( not($stepstate) or $stepstate/section[@key = $element/@key] ) and ( not($element/accessrights/userright) or $element/accessrights/userright/@publish = 'true' ) and $element/contenttypes/contenttype">
          <tr style="height: 16px;" valign="middle">

              <td>
                  <xsl:choose>
                      <xsl:when test="$sectionlast">
                          <img id="img-site{@key}" src="javascript/images/L.png" border="0"/>
                      </xsl:when>
                      <xsl:otherwise>
                          <img id="img-site{@key}" src="javascript/images/T.png" border="0"/>
                      </xsl:otherwise>
                  </xsl:choose>
              </td>

              <xsl:variable name="disabled" select="( $element/accessrights/userright and  $element/accessrights/userright/@publish = 'false' ) or ( $approveRequired and $element/accessrights/userright and  $element/accessrights/userright/@approve = 'false' ) or $element/@filtered = 'true'"/>
              
              <xsl:variable name="anonymous" select="$element/accessrights/accessright[@grouptype = 7 and @read = 'true']"/>

              <xsl:if test="$checkbox">
                  <td width="16">
                      <xsl:variable name="currentstepstate">
                          <xsl:copy-of select="/wizarddata/wizardstate/stepstate[@id = /wizarddata/wizardstate/@currentstepstateid]"/>
                      </xsl:variable>
                      
                      <input type="checkbox" name="sectionkey">
                          <xsl:attribute name="value">
                              <xsl:value-of select="$element/@key"/>
                          </xsl:attribute>
                          
                          <xsl:if test="/wizarddata/wizardstate/stepstate[@id = /wizarddata/wizardstate/@currentstepstateid]/section[@key = $element/@key]">
                              <xsl:attribute name="checked">
                                  <xsl:text>checked</xsl:text>
                              </xsl:attribute>
                          </xsl:if>
                          	
                          <xsl:if test="$disabled">
                              <xsl:attribute name="disabled">
                                  <xsl:text>disabled</xsl:text>
                              </xsl:attribute>
                              
                              <xsl:if test="$element/@filtered = 'true'">
								  <xsl:attribute name="checked">
									  <xsl:text>checked</xsl:text>
								  </xsl:attribute>
							  </xsl:if>
                          </xsl:if>
                          
                          <xsl:choose>
                              <xsl:when test="$approveRequired and $element/@ordered = 'true'">
                                  <xsl:attribute name="onclick">
                                      <xsl:text>sectionClicked(this, true)</xsl:text>
                                  </xsl:attribute>
                              </xsl:when>
                              <xsl:otherwise>
                                  <xsl:attribute name="onclick">
                                      <xsl:text>sectionClicked(this, false)</xsl:text>
                                  </xsl:attribute>
                              </xsl:otherwise>
                          </xsl:choose>
                      </input>
                  </td>
              </xsl:if>

              <td>
                  <img border="0">
                      <xsl:attribute name="src">
                          <xsl:text>images/icon_section</xsl:text>
                          <xsl:if test="$element/@ordered = 'true'">
                              <xsl:text>_ordered</xsl:text>
                          </xsl:if>
                          <xsl:if test="not($anonymous)">
                              <xsl:text>_lock</xsl:text>
                          </xsl:if>
                          <xsl:if test="$disabled">
                              <xsl:text>_shaded</xsl:text>
                          </xsl:if>
                          <xsl:text>.gif</xsl:text>
                      </xsl:attribute>
                  </img>
                  <img src="images/shim.gif" width="3" height="1" class="shim" border="0"/>
                  <xsl:value-of select="$indent"/>
                  <xsl:value-of select="$element/@name"/>
                   <xsl:if test="$markapproved and /wizarddata/wizardstate/stepstate[2]/section[@key = $element/@key]">
                       <span class="requiredfield"> *</span>
                   </xsl:if>
              </td>
          </tr>
      </xsl:if>
          
      <xsl:for-each select="$element/sections/section">
          <xsl:call-template name="sectionlist">
              <xsl:with-param name="element" select="."/>
              <xsl:with-param name="indent">
                  <xsl:value-of select="$indent"/>
                  <xsl:value-of select="$element/@name"/>
                  <xsl:text> / </xsl:text>
              </xsl:with-param>
              <xsl:with-param name="stepstate" select="$stepstate"/>
              <xsl:with-param name="sectionlast" select="$sectionlast"/>
              <xsl:with-param name="approveRequired" select="$approveRequired"/>
              <xsl:with-param name="checkbox" select="$checkbox"/>
              <xsl:with-param name="markapproved" select="$markapproved"/>
          </xsl:call-template>
      </xsl:for-each>
  </xsl:template>

  <xsl:template name="sectionsummary">
       <xsl:param name="element"/>
       <xsl:param name="indent"/>
       
       <xsl:if test="/wizarddata/wizardstate/stepstate[1]/section[@key = $element/@key]">
           <tr>
               <td class="browsetablecell" width="15">
                   <xsl:choose>
                       <xsl:when test="$element/@ordered = 'true'">
                           <img border="0" src="images/icon_section_ordered.gif"/>
                       </xsl:when>
                       <xsl:otherwise>
                           <img border="0" src="images/icon_section.gif"/>
                       </xsl:otherwise>
                   </xsl:choose>
               </td>
               <td class="browsetablecell">
                   <xsl:value-of select="$indent"/>
                   <xsl:value-of select="$element/@name"/>
               </td>
               <td class="browsetablecell">
                   <xsl:if test="/wizarddata/wizardstate/stepstate[2]/section[@key = $element/@key]">
                       <xsl:text>%msgApproved%</xsl:text>
                   </xsl:if>
               </td>
           </tr>
       </xsl:if>
       
       <xsl:for-each select="$element/sections/section">
           <xsl:call-template name="sectionsummary">
               <xsl:with-param name="element" select="."/>
               <xsl:with-param name="indent">
                   <xsl:value-of select="$indent"/>
                   <xsl:value-of select="$element/@name"/>
                   <xsl:text> / </xsl:text>
               </xsl:with-param>
           </xsl:call-template>
       </xsl:for-each>
   </xsl:template>
   
   <xsl:template name="sectionname">
       <xsl:value-of select="$currentsectionpath"/>
   </xsl:template>
   
   <xsl:template name="subsectionnumber">
       <xsl:param name="stepstate" select="/wizarddata/wizardstate/stepstate[@id = number(/wizarddata/wizardstate/@currentstepstateid) - 1]"/>

       <xsl:choose>
           <xsl:when test="$stepstate/@stepid = 1">
               <xsl:text>1</xsl:text>
           </xsl:when>
           <xsl:otherwise>
               <xsl:variable name="index">
                   <xsl:call-template name="subsectionnumber">
                       <xsl:with-param name="stepstate" select="$stepstate/preceding-sibling::node()"/>
                   </xsl:call-template>
               </xsl:variable>
               <xsl:value-of select="number($index) + 1"/>
           </xsl:otherwise>
       </xsl:choose>
   </xsl:template>

   <xsl:template name="categoryheader_section">
	   <xsl:call-template name="categoryheader">
		   <xsl:with-param name="rootelem" select="/wizarddata"/>
	   </xsl:call-template>
   </xsl:template>

</xsl:stylesheet>
