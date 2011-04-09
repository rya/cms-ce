<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:template name="xhtmleditor">
    <!-- String, Required -->
    <xsl:param name="id"/>
    <!-- String, Required -->
    <xsl:param name="name"/>
    <!-- Node -->
    <xsl:param name="content"/>
    <!-- String, Required -->
    <xsl:param name="config" select="'document'"/>
    <!-- String -->
    <xsl:param name="buttonRows"/>
    <!-- String -->
    <xsl:param name="customcss"/>
    <!-- Integer -->
    <xsl:param name="width" select="600"/>
    <!-- Integer -->
    <xsl:param name="height" select="500"/>
    <!-- Integer -->
    <xsl:param name="menukey"/>
    <!-- Boolean -->
    <xsl:param name="disabled" select="false()"/>
    <!-- Boolean -->
    <xsl:param name="readonly" select="false()"/>
    <!-- Boolean -->
    <xsl:param name="accessToHtmlSource" select="false()"/>
    <!-- Boolean -->
    <xsl:param name="inlinePopups" select="false()"/>
    <!-- Boolean -->
    <xsl:param name="required" select="false()"/>
    <!-- String -->
    <xsl:param name="helpelement"/>
    <!-- Boolean -->
    <xsl:param name="fullpage" select="false()"/>
    <!-- Boolean -->
    <xsl:param name="classfilter" select="false()"/>
    <!-- String -->
    <xsl:param name="block-format-elements" select="''"/>

    <xsl:variable name="disabledMode" select="$disabled = true() or $readonly = true()"/>

    <!-- Default edit area CSS -->
    <xsl:variable name="defaultcss" select="'./tinymce/jscripts/tiny_mce/themes/advanced/skins/cms/content.css'"/>
    <!-- Custom CSS -->
    <xsl:variable name="css" select="concat('css?id=', $customcss)"/>

    <!-- Create uniqe id -->
    <xsl:variable name="javaRandom" select="admin:random()"/>
    <xsl:variable name="javaRandomInt" select="substring-after($javaRandom,'.')"/>
    <xsl:variable name="edKey" select="concat('id_', $javaRandomInt)"/>

    <xsl:variable name="block-format-elements-normalized" select="normalize-space($block-format-elements)"/>

    <xsl:variable name="block-format-elements-list">
      <xsl:choose>
        <xsl:when test="$block-format-elements-normalized !=''">
          <xsl:value-of select="concat('p,', $block-format-elements-normalized)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>p,h1,h2,h3,h4,h5,h6,blockquote,pre</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- Internal debuging -->
    <!--div style="padding:10px;background-color:#1C70B1; color:#fff; border:1px solid #000;margin:1em 0;">
      <xsl:value-of select="concat('Config: ', $config)"/>
      <xsl:if test="$config = 'custom'">
        <br/><xsl:value-of select="translate($buttonRows, ' ' , '')"/>
      </xsl:if>
      <br/>
      <xsl:value-of select="concat('Name: ', $name)"/>
      <br/>
      <xsl:value-of select="concat('ID: ', $edKey)"/>
      <br/>
      <xsl:value-of select="concat('Custom CSS: ', $customcss)"/>
      <br/>
      <xsl:value-of select="concat('Required: ', $required)"/>
      <br/>
      <xsl:value-of select="concat('Disabled: ', $disabled)"/>
    <div>
      <br/>
      <xsl:value-of select="concat('Expert contributor: ', $accessToHtmlSource)"/>
    </div>
    -->

    <xsl:choose>
      <xsl:when test="$name = '' or $id = '' or $config = '' or ($config = 'custom' and $buttonRows = '')">
        <p>
          <strong style="color:red;font-weight:bold">A configuration error occurred!</strong>
          <ul>
            <xsl:if test="$name = ''">
              <li>input/@name is not defined properly. Please check the content type configuration.</li>
            </xsl:if>
            <xsl:if test="$config = ''">
              <li>input/@config is not properly defined. Please check the content type configuration.</li>
            </xsl:if>
            <xsl:if test="$config = 'custom' and $buttonRows = ''">
              <li>The input element has no button:child elements. Please check the content type configuration.</li>
            </xsl:if>
          </ul>
        </p>
      </xsl:when>
      <xsl:otherwise>

        <!--
          Help element.
        -->
        <xsl:if test="$helpelement">
          <xsl:call-template name="displayhelp">
            <xsl:with-param name="fieldname" select="$name"/>
            <xsl:with-param name="helpelement" select="$helpelement"/>
          </xsl:call-template>
        </xsl:if>

        <!--
          Textarea to be converted to an editor.
        -->
        <div id="editor_cms_init_msg_{$edKey}" style="display:block;color:#000;padding-top:4px">
          %txtEditorPreInit% ...
        </div>

        <div id="editor_cms_container_{$edKey}" style="visibility:hidden">
          <textarea name="{$name}" id="{$edKey}" class="{concat('editor-textarea editor_settings_', $edKey)}" style="width:{$width}px;height:{$height}px">
            <xsl:if test="$content !=''">
              <xsl:call-template name="serialize">
                <xsl:with-param name="xpath" select="$content"/>
              </xsl:call-template>
            </xsl:if>
          </textarea>
        </div>

        <!--
          Instantiate the editor.
         -->
        <script type="text/javascript">
          var editor_<xsl:value-of select="$edKey"/> = new Editor('<xsl:value-of select="$edKey"/>');
        </script>

        <!--
          Configuration for the instance.
        -->

        <script type="text/javascript">
          var cmsutil = new CMSUtil();
          var langCode = cmsutil.getLanguageCode();

          var editor_settings_<xsl:value-of select="$edKey"/> = {

            <xsl:if test="$accessToHtmlSource = true()">
              <xsl:text>accessToHtmlSource : </xsl:text><xsl:value-of select="$accessToHtmlSource"/>,
            </xsl:if>

            plugins : "internallinkplugin,<xsl:if test="not($disabledMode)">cmsstatusbar,</xsl:if>cmscodeformater,cmslink,cmsimage,media,table,save,searchreplace,cmscontextmenu,paste,directionality,nonbreaking,xhtmlxtras,advlink,fullscreen,safari<xsl:if test="$fullpage = true()">,fullpage</xsl:if><xsl:if test="$inlinePopups = true()">,inlinepopups</xsl:if>,advlist,autolink",
            mode : 'textareas',

            <xsl:if test="$disabledMode">
              readonly : true,
            </xsl:if>

            language : langCode,
            theme : 'advanced',
            initial_width : '<xsl:value-of select="$width"/>',

            <xsl:if test="$fullpage">
              <xsl:text>fullpage_doctypes: 'HTML 4.01 Transitional=&lt;!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"&gt;',</xsl:text>
            </xsl:if>

            <xsl:if test="not($disabledMode)">
              theme_advanced_statusbar_location : 'bottom',
            </xsl:if>

            ﻿theme_advanced_layout_manager : 'RowLayout',
            <xsl:choose>
              <xsl:when test="not($disabledMode)">

                <!--
                  Button configuration.
                -->
                <xsl:choose>
                  <xsl:when test="$config = 'document' or $config = 'full'">
                    <xsl:call-template name="getDocumentConfig">
                      <xsl:with-param name="accessToHtmlSource" select="$accessToHtmlSource"/>
                      <xsl:with-param name="hasCSS" select="boolean($customcss !='')"/>
                      <xsl:with-param name="fullpage" select="$fullpage"/>
                    </xsl:call-template>
                  </xsl:when>
                  <xsl:when test="$config = 'light'">
                    <xsl:call-template name="getLightConfig">
                      <xsl:with-param name="accessToHtmlSource" select="$accessToHtmlSource"/>
                      <xsl:with-param name="hasCSS" select="boolean($customcss !='')"/>
                    </xsl:call-template>
                  </xsl:when>
                  <xsl:when test="$config = 'lightwithtable' or $config = 'heading' or $config = 'standalone' or $config = 'normal'">
                    <xsl:call-template name="getLightWithTableConfig">
                      <xsl:with-param name="accessToHtmlSource" select="$accessToHtmlSource"/>
                      <xsl:with-param name="hasCSS" select="boolean($customcss !='')"/>
                    </xsl:call-template>
                  </xsl:when>
                  <xsl:when test="$config = 'custom'">
                    <xsl:call-template name="getCustomConfig">
                      <xsl:with-param name="buttonRows" select="translate($buttonRows, ' ' , '')"/>
                      <xsl:with-param name="accessToHtmlSource" select="$accessToHtmlSource"/>
                      <xsl:with-param name="hasCSS" select="boolean($customcss !='')"/>
                    </xsl:call-template>
                  </xsl:when>
                  <xsl:otherwise>
                    <!-- empty -->
                    <xsl:call-template name="getEmptyConfig">
                      <xsl:with-param name="accessToHtmlSource" select="$accessToHtmlSource"/>
                      <xsl:with-param name="hasCSS" select="boolean($customcss !='')"/>
                    </xsl:call-template>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:when>
              <xsl:otherwise>
                ﻿theme_advanced_containers : 'editorcontainer',
                theme_advanced_container_editorcontainer : 'mceEditor',
              </xsl:otherwise>
            </xsl:choose>

            skin : 'cms',
            content_css : '<xsl:value-of select="$defaultcss"/>

            <!--
              Has custom css?
            -->
            <xsl:choose>
              <xsl:when test="$customcss != ''"><xsl:text>,</xsl:text><xsl:value-of select="$css"/>',</xsl:when>
              <xsl:otherwise>',</xsl:otherwise>
            </xsl:choose>
            class_filter : function(cls) {
              <xsl:if test="$classfilter = true()">
                if ( cls.substring(0,4) != 'sys-' )
                  return false;
              </xsl:if>

                if (/^editor-(p-center|p-block|image-left|image-right)$/.test(cls))
                  return false;

                return cls;
            },
            convert_urls : false,
            gecko_spellcheck : true,
            inlinepopups_skin : 'cms',
            <xsl:text>cms_menu_key : '</xsl:text><xsl:value-of select="$menukey"/><xsl:text>',</xsl:text>
            auto_reset_designmode : true,
            extended_valid_elements : 'style[type|media|title],script[type|src|charset]',
            theme_advanced_blockformats : '<xsl:value-of select="$block-format-elements-list"/>',
            paste_use_dialog : true,
            paste_auto_cleanup_on_paste : true,
            paste_convert_middot_lists : true,
            paste_strip_class_attributes : 'all',
            fix_list_elements : true,
            fix_table_elements : true,
            fix_nesting : true,
            fix_content_duplication : true,
            theme_advanced_source_editor_width : 780,
            theme_advanced_source_editor_height : 520,
            setup : function(ed) {
              ed.onBeforeSetContent.add(function(ed, o)
              {
                // Fx has problems with selecting singleton td tags. This inserts some bogus and closes the tag.
                o.content = o.content.replace(/&lt;td\/&gt;/g, '&lt;td&gt;&nbsp;&lt;\/td&gt;');
                // Fx and IE has problems when the textarea is loaded as an singleton tag.
                o.content = o.content.replace(/&lt;textarea\s+(.+)\/&gt;/g, '&lt;textarea $1&gt;&lt;\/textarea&gt;');
                // Fx and IE has problems when the label is loaded as an singleton tag.
                o.content = o.content.replace(/&lt;label\s+(.+)\/&gt;/g, '&lt;label $1&gt;&lt;\/label&gt;');
                // Fx needs @_moz_dirty on images so the ui works properly.
                o.content = o.content.replace(/&lt;img\s+(.+)\/&gt;/g, '&lt;img $1 _moz_dirty="" \/&gt;');
                // IE crashes the editor if the embed is a singleton tag. This inserts some bogus and closes the tag.
                o.content = o.content.replace(/&lt;embed(.+\n).+\/&gt;/g, '&lt;embed$1&gt;.&lt;/embed&gt;');
              });
              // *********************************************************************************************************************
              ed.onGetContent.add(function(ed, o) {
                  // Make sure the iframe element has content.
                  o.content = o.content.replace(/&lt;iframe(.+?)&gt;(|\s+)&lt;\/iframe&gt;/g, '&lt;iframe$1&gt;cms_content&lt;\/iframe&gt;');
              });

              ed.onKeyDown.add(function( ed, e ) {
                // Workaround for Fx which adds empty p elements when the user removes an img element.
                if ( tinymce.isGecko &amp;&amp; e.keyCode == 8 &amp;&amp; ed.selection.getNode().nodeName == 'IMG' )
                {
                  ed.execCommand( 'mceReplaceContent', false, ' ');
                }
              });
              // *********************************************************************************************************************

              // g_htmlAreaTotalCount is used by the waitsplash.
              ed.onPreInit.add(function( ed ) {
                if ( typeof window.g_htmlAreaTotalCount != 'undefined' )
                  window.g_htmlAreaTotalCount++;
                else
                  window.g_htmlAreaTotalCount = 1;

              });
              // *********************************************************************************************************************

              ed.onInit.add(function( ed, o ) {
                // *********************************************************************************************************************
                // *** Create an overlay if mode is disabled.
                // *********************************************************************************************************************
                <xsl:if test="$disabled">
                  editor_<xsl:value-of select="$edKey"/>.createOverlayForDisabledMode();
                </xsl:if>

                // *********************************************************************************************************************
                // *** Show/hide Init message
                // *********************************************************************************************************************
                var editorInitMsg = document.getElementById( 'editor_cms_init_msg_' + ed.id ) || null;
                if (editorInitMsg)
                  editorInitMsg.style.display = 'none';

                var editorContainer = document.getElementById( 'editor_cms_container_' + ed.id ) || null;
                if (editorContainer)
                  editorContainer.style.visibility = 'visible';

                // Check if all HTMLAreas is initalized.
                if ( typeof window.g_htmlAreaCount != 'undefined' )
                {
                  window.g_htmlAreaCount++;
                  _checkIfAllHtmlAreasIsInitialized();
                }
                else
                {
                  window.g_htmlAreaCount = 1;
                  _checkIfAllHtmlAreasIsInitialized();
                }

                // *********************************************************************************************************************
                // *** Table commands for context menu.
                // *********************************************************************************************************************
                if (ed &amp;&amp; ed.plugins.cmscontextmenu &amp;&amp; ( ed.controlManager.get('table') || ed.controlManager.get('tablecontrols') ) ) {
                  ed.plugins.cmscontextmenu.onContextMenu.add(function(th, m, e) {
                    var sm, se = ed.selection, el = se.getNode() || ed.getBody();

                    if (ed.dom.getParent(e, 'td') || ed.dom.getParent(e, 'th')) {
                      m.removeAll();

                      if ( el.nodeName == 'A' &amp;&amp; !ed.dom.getAttrib(el, 'name') &amp;&amp; ed.controlManager.get('cmslink') ) {
                        m.add({title : 'advanced.link_desc', icon : 'link', cmd : 'cmslink', ui : true});
                        m.add({title : 'advanced.unlink_desc', icon : 'unlink', cmd : 'UnLink'});
                        m.addSeparator();
                      }

                      if (el.nodeName == 'IMG' &amp;&amp; el.className.indexOf('mceItem') == -1) {
                        m.add({title : 'advanced.image_desc', icon : 'image', cmd : ed.plugins.advimage ? 'mceAdvImage' : 'mceImage', ui : true});
                        m.addSeparator();
                      }

                      m.add({title : 'table.desc', icon : 'table', cmd : 'mceInsertTable', ui : true, value : {action : 'insert'}});
                      m.add({title : 'table.props_desc', icon : 'table_props', cmd : 'mceInsertTable', ui : true});
                      m.add({title : 'table.del', icon : 'delete_table', cmd : 'mceTableDelete', ui : true});
                      m.addSeparator();

                      // Cell menu
                      sm = m.addMenu({title : 'table.cell'});
                      sm.add({title : 'table.cell_desc', icon : 'cell_props', cmd : 'mceTableCellProps', ui : true});
                      sm.add({title : 'table.split_cells_desc', icon : 'split_cells', cmd : 'mceTableSplitCells', ui : true});
                      sm.add({title : 'table.merge_cells_desc', icon : 'merge_cells', cmd : 'mceTableMergeCells', ui : true});

                      // Row menu
                      sm = m.addMenu({title : 'table.row'});
                      sm.add({title : 'table.row_desc', icon : 'row_props', cmd : 'mceTableRowProps', ui : true});
                      sm.add({title : 'table.row_before_desc', icon : 'row_before', cmd : 'mceTableInsertRowBefore'});
                      sm.add({title : 'table.row_after_desc', icon : 'row_after', cmd : 'mceTableInsertRowAfter'});
                      sm.add({title : 'table.delete_row_desc', icon : 'delete_row', cmd : 'mceTableDeleteRow'});
                      sm.addSeparator();
                      sm.add({title : 'table.cut_row_desc', icon : 'cut', cmd : 'mceTableCutRow'});
                      sm.add({title : 'table.copy_row_desc', icon : 'copy', cmd : 'mceTableCopyRow'});
                      sm.add({title : 'table.paste_row_before_desc', icon : 'paste', cmd : 'mceTablePasteRowBefore'});
                      sm.add({title : 'table.paste_row_after_desc', icon : 'paste', cmd : 'mceTablePasteRowAfter'});

                      // Column menu
                      sm = m.addMenu({title : 'table.col'});
                      sm.add({title : 'table.col_before_desc', icon : 'col_before', cmd : 'mceTableInsertColBefore'});
                      sm.add({title : 'table.col_after_desc', icon : 'col_after', cmd : 'mceTableInsertColAfter'});
                      sm.add({title : 'table.delete_col_desc', icon : 'delete_col', cmd : 'mceTableDeleteCol'});
                    } else
                      m.add({title : 'table.desc', icon : 'table', cmd : 'mceInsertTable', ui : true});
                  });
                }

                // *********************************************************************************************************************
                // *** Private methods.
                // *********************************************************************************************************************
                function _checkIfAllHtmlAreasIsInitialized()
                {
                  if ( window.g_htmlAreaCount == window.g_htmlAreaTotalCount )
                  {
                    if ( typeof removeWaitsplash == 'function' ) removeWaitsplash();
                  }
                }

              });
            }
          };
          editor_<xsl:value-of select="$edKey"/>.create(editor_settings_<xsl:value-of select="$edKey"/>);
        </script>

      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--
    ********************************************************************************************************************
    *** Custom setup.
    ********************************************************************************************************************
  -->
  <xsl:template name="getCustomConfig">
    <xsl:param name="buttonRows"/>
    <xsl:param name="accessToHtmlSource" select="false()"/>
    <xsl:param name="hasCSS" select="false()"/>

    <!--
      Row setup.
    -->
    <xsl:variable name="noOfRows">
      <xsl:call-template name="countRows">
        <xsl:with-param name="str" select="$buttonRows"/>
        <xsl:with-param name="count" select="0"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="customContainers">
      <xsl:call-template name="getContainers">
        <xsl:with-param name="count" select="1"/>
        <xsl:with-param name="to" select="$noOfRows"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:text>﻿theme_advanced_containers : '</xsl:text><xsl:value-of select="$customContainers"/>,editorcontainer,statusbarcontainer<xsl:text>',</xsl:text>

    <xsl:call-template name="getButtonRows">
      <xsl:with-param name="str" select="$buttonRows"/>
    </xsl:call-template>

    <xsl:text>theme_advanced_container_editorcontainer : 'mceEditor',</xsl:text>
    <xsl:text>theme_advanced_container_statusbarcontainer : 'mceElementpath',</xsl:text>

    <xsl:call-template name="getContainersAlign">
      <xsl:with-param name="noOfRows" select="$noOfRows"/>
      <xsl:with-param name="count" select="1"/>
    </xsl:call-template>
  </xsl:template>

  <!--
    customContainersAlign
  -->
  <xsl:template name="getContainersAlign">
    <xsl:param name="noOfRows"/>
    <xsl:param name="count" select="1"/>
    <xsl:text>﻿theme_advanced_container_row</xsl:text>
    <xsl:value-of select="$count"/>
    <xsl:text>_align : 'left',</xsl:text>
    <xsl:if test="$count &lt; $noOfRows">
      <xsl:call-template name="getContainersAlign">
        <xsl:with-param name="noOfRows" select="$noOfRows"/>
        <xsl:with-param name="count" select="$count + 1"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template name="getContainers">
    <xsl:param name="count" select="1"/>
    <xsl:param name="to"/>
    <xsl:param name="returnStr"/>

    <xsl:choose>
      <xsl:when test="$count &lt;= $to">
        <xsl:variable name="temp">
          <xsl:choose>
            <xsl:when test="$count != $to">
              <xsl:value-of select="concat($returnStr, 'row', $count, ',')"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="concat($returnStr, 'row', $count)"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:call-template name="getContainers">
          <xsl:with-param name="count" select="$count + 1"/>
          <xsl:with-param name="to" select="$to"/>
          <xsl:with-param name="returnStr" select="$temp"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$returnStr"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="countRows">
    <xsl:param name="str"/>
    <xsl:param name="count"/>
    <xsl:variable name="newStr" select="normalize-space($str)"/>
    <xsl:variable name="current" select="substring-before($newStr, ';')"/>
    <xsl:variable name="rest" select="substring-after($newStr, ';')"/>
    <xsl:choose>
      <xsl:when test="contains($newStr, ';')">
        <xsl:call-template name="countRows">
          <xsl:with-param name="str" select="$rest"/>
          <xsl:with-param name="count" select="$count + 1"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$count + 1"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="getButtonRows">
    <xsl:param name="str"/>
    <xsl:variable name="newStr" select="normalize-space($str)"/>
    <xsl:variable name="current" select="substring-before($newStr, ';')"/>
    <xsl:variable name="rest" select="substring-after($newStr, ';')"/>
    <xsl:choose>
      <xsl:when test="contains($newStr, ';')">
        <xsl:value-of select="substring-before($current, ':')"/>
        <xsl:text>: '</xsl:text>
        <xsl:variable name="tempstr">
          <xsl:call-template name="translateRow">
            <xsl:with-param name="row" select="substring-after($current,':')"/>
          </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="substring($tempstr, 1, string-length($tempstr)-1)"/>
        <xsl:text>', </xsl:text>
        <xsl:call-template name="getButtonRows">
          <xsl:with-param name="str" select="$rest"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="substring-before($newStr, ':')"/>
        <xsl:text>: '</xsl:text>
        <xsl:variable name="tempstr">
          <xsl:call-template name="translateRow">
            <xsl:with-param name="row" select="substring-after($newStr,':')"/>
          </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="substring($tempstr, 1, string-length($tempstr)-1)"/>
        <xsl:text>', </xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="translateRow">
    <xsl:param name="row"/>
    <xsl:variable name="newRow" select="normalize-space($row)"/>
    <xsl:variable name="current" select="substring-before($newRow, ',')"/>
    <xsl:variable name="rest" select="substring-after($newRow, ',')"/>
    <xsl:choose>
      <xsl:when test="contains($newRow, ',')">
        <xsl:call-template name="customConfigGetGroup">
          <xsl:with-param name="group" select="$current"/>
        </xsl:call-template>
        <xsl:text>,</xsl:text>
        <xsl:call-template name="translateRow">
          <xsl:with-param name="row" select="$rest"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="customConfigGetGroup">
          <xsl:with-param name="group" select="$newRow"/>
        </xsl:call-template>
        <xsl:text>,</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="customConfigGetGroup">
    <xsl:param name="group"/>
    <xsl:choose>
      <xsl:when test="$group = 'textformat'">
        <xsl:call-template name="getTextFormatControls"/>
      </xsl:when>
      <xsl:when test="$group = 'phrase'">
        <xsl:call-template name="getPhraseControls"/>
      </xsl:when>
      <xsl:when test="$group = 'tablecontrols'">
        <xsl:call-template name="getTableControls"/>
      </xsl:when>
      <xsl:when test="$group = 'table'">
        <xsl:call-template name="getTableLightControls"/>
      </xsl:when>
      <xsl:when test="$group = 'alignment'">
        <xsl:call-template name="getAlignmentControls"/>
      </xsl:when>
      <xsl:when test="$group = 'indent'">
        <xsl:call-template name="getIndentControls"/>
      </xsl:when>
      <xsl:when test="$group = 'hyperlink'">
        <xsl:call-template name="getLinkControls"/>
      </xsl:when>
      <xsl:when test="$group = 'list'">
        <xsl:call-template name="getListControls"/>
      </xsl:when>
      <xsl:when test="$group = 'subsup'">
        <xsl:call-template name="getSubSupControls"/>
      </xsl:when>
      <xsl:when test="$group = 'edit'">
        <xsl:call-template name="getEditControls"/>
      </xsl:when>
      <xsl:when test="$group = 'searchreplace'">
        <xsl:call-template name="getSearchReplaceControls"/>
      </xsl:when>
      <xsl:when test="$group = 'image'">
        <xsl:call-template name="getImageControls"/>
      </xsl:when>
      <xsl:when test="$group = 'attribute'">
        <xsl:call-template name="getAttributeControls"/>
      </xsl:when>
      <xsl:when test="$group = 'charmap'">
        <xsl:call-template name="getCharmapControls"/>
      </xsl:when>
      <xsl:when test="$group = 'history'">
        <xsl:call-template name="getHistoryControls"/>
      </xsl:when>
      <xsl:when test="$group = 'hr'">
        <xsl:call-template name="getHrControls"/>
      </xsl:when>
      <xsl:when test="$group = 'code'">
        <xsl:call-template name="getSourceControls"/>
      </xsl:when>
      <xsl:when test="$group = 'removeformat'">
        <xsl:call-template name="getRemoveFormatControls"/>
      </xsl:when>
      <xsl:when test="$group = 'blockformat'">
        <xsl:call-template name="getBlockFormatControls"/>
      </xsl:when>
      <xsl:when test="$group = 'styleselect'">
        <xsl:call-template name="getStyleSelectControls"/>
      </xsl:when>
      <xsl:when test="$group = 'fullscreen'">
        <xsl:call-template name="getFullScreenControls"/>
      </xsl:when>
      <xsl:when test="$group = 'color'">
        <xsl:call-template name="getColorControls"/>
      </xsl:when>
      <xsl:when test="$group = 'embed' or $group = 'media'">
        <xsl:call-template name="getEmbedControls"/>
      </xsl:when>
      <xsl:when test="$group = '|' or $group = 'separator'">
        <xsl:call-template name="getSeparatorControls"/>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!--
    ********************************************************************************************************************
    *** Document.
    ***  Full configuration.
    ********************************************************************************************************************
  -->
  <xsl:template name="getDocumentConfig">
    <!--
      Used for wether we should render admin controls only (eg. HTML source).
      -->
    <xsl:param name="accessToHtmlSource" select="false()"/>
    <!--
      Used for wether we should render the css dropdown or not.
    -->
    <xsl:param name="hasCSS" select="false()"/>
    <!--
      Accepts html, head and boyd structure.
    -->
    <xsl:param name="fullpage" select="false()"/>

    <!--
      Row setup.
    -->
    ﻿theme_advanced_containers : 'row1,row2,row3,editorcontainer,statusbarcontainer',
    <!--
      Row 1.
    -->
    <xsl:text>﻿theme_advanced_container_row1 : '</xsl:text>
    <xsl:if test="$fullpage">
      <xsl:call-template name="getFullPageControls"/>
      <xsl:text>,</xsl:text>
    </xsl:if>
    <xsl:call-template name="getEditControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSearchReplaceControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getHistoryControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getHrControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getCharmapControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getPhraseControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getAttributeControls"/>
    <xsl:text>',</xsl:text>
    <!--
      Row 2.
     -->
    <xsl:text>﻿theme_advanced_container_row2 : '</xsl:text>
    <xsl:call-template name="getLinkControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getImageControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getTableControls"/>
    <xsl:text>',</xsl:text>
    <!--
      Row 3.
    -->
    <xsl:text>﻿theme_advanced_container_row3 : '</xsl:text>
    <xsl:call-template name="getBlockFormatControls"/>
    <xsl:text>,</xsl:text>
    <xsl:if test="$hasCSS = true()">
      <xsl:call-template name="getStyleSelectControls"/>
      <xsl:text>,</xsl:text>
    </xsl:if>
    <xsl:call-template name="getTextFormatControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getRemoveFormatControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getAlignmentControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getListControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getIndentControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSubSupControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getColorControls"/>
    <xsl:text>',</xsl:text>

    <xsl:text>theme_advanced_container_editorcontainer : 'mceEditor',</xsl:text>
    <xsl:text>theme_advanced_container_statusbarcontainer : 'mceElementpath',</xsl:text>

    <!--xsl:if test="$expertContributor">
      <xsl:call-template name="createBottomRow"/>
    </xsl:if-->

    <xsl:text>﻿theme_advanced_container_row1_align : 'left',</xsl:text>
    <xsl:text>﻿theme_advanced_container_row2_align : 'left',</xsl:text>
    <xsl:text>﻿theme_advanced_container_row3_align : 'left',</xsl:text>
  </xsl:template>

  <!--
    ********************************************************************************************************************
    *** Light.
    ********************************************************************************************************************
  -->
  <xsl:template name="getLightConfig">
    <!--
      Used for wether we should render admin controls only (eg. HTML source).
    -->
    <xsl:param name="accessToHtmlSource" select="false()"/>
    <!--
      Used for wether we should render the css dropdown or not.
    -->
    <xsl:param name="hasCSS" select="false()"/>
    <!--
      Row setup.
    -->
    ﻿theme_advanced_containers : 'row1,row2,editorcontainer,statusbarcontainer',
    <!--
      Row 1.
    -->
    <xsl:text>﻿theme_advanced_container_row1 : '</xsl:text>
    <xsl:call-template name="getEditControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSearchReplaceControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getHistoryControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getLinkControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getHrControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getCharmapControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getPhraseControls"/>
    <xsl:text>',</xsl:text>
    <!--
      Row 2.
    -->
    <xsl:text>﻿theme_advanced_container_row2 : '</xsl:text>
    <xsl:call-template name="getTextFormatControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getRemoveFormatControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getAlignmentControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getListControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getIndentControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSubSupControls"/>
    <xsl:text>',</xsl:text>

    <xsl:text>theme_advanced_container_editorcontainer : 'mceEditor',</xsl:text>
    <xsl:text>theme_advanced_container_statusbarcontainer : 'mceElementpath',</xsl:text>

    <xsl:text>﻿theme_advanced_container_row1_align : 'left',</xsl:text>
    <xsl:text>﻿theme_advanced_container_row2_align : 'left',</xsl:text>
  </xsl:template>

  <!--
    ********************************************************************************************************************
    *** Light with table.
    ********************************************************************************************************************
  -->
  <xsl:template name="getLightWithTableConfig">
    <!--
      Used for wether we should render admin controls only (eg. HTML source).
    -->
    <xsl:param name="accessToHtmlSource" select="false()"/>
    <!--
      Used for wether we should render the css dropdown or not.
    -->
    <xsl:param name="hasCSS" select="false()"/>
    <!--
      Row setup.
    -->
    ﻿theme_advanced_containers : 'row1,row2,row3,editorcontainer,statusbarcontainer',
    <!--
      Row 1.
    -->
    <xsl:text>﻿theme_advanced_container_row1 : '</xsl:text>
    <xsl:call-template name="getEditControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSearchReplaceControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getHistoryControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getHrControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getCharmapControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getPhraseControls"/>
    <xsl:text>',</xsl:text>
    <!--
      Row 2.
    -->
    <xsl:text>﻿theme_advanced_container_row2 : '</xsl:text>
    <xsl:call-template name="getLinkControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getTableControls"/>
    <xsl:text>',</xsl:text>
    <!--
      Row 3.
    -->
    <xsl:text>﻿theme_advanced_container_row3 : '</xsl:text>
    <xsl:call-template name="getTextFormatControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getRemoveFormatControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getAlignmentControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getListControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getIndentControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSeparatorControls"/>
    <xsl:text>,</xsl:text>
    <xsl:call-template name="getSubSupControls"/>
    <xsl:text>',</xsl:text>

    <xsl:text>theme_advanced_container_editorcontainer : 'mceEditor',</xsl:text>
    <xsl:text>theme_advanced_container_statusbarcontainer : 'mceElementpath',</xsl:text>

    <xsl:text>﻿theme_advanced_container_row1_align : 'left',</xsl:text>
    <xsl:text>﻿theme_advanced_container_row2_align : 'left',</xsl:text>
    <xsl:text>﻿theme_advanced_container_row3_align : 'left',</xsl:text>
  </xsl:template>

  <!--
    ********************************************************************************************************************
    *** Empty.
    ********************************************************************************************************************
  -->
  <xsl:template name="getEmptyConfig">
    <xsl:text>﻿theme_advanced_containers : 'editorcontainer,statusbarcontainer',</xsl:text>
    <xsl:text>theme_advanced_container_editorcontainer : 'mceEditor',</xsl:text>
    <xsl:text>theme_advanced_container_statusbarcontainer : 'mceElementpath',</xsl:text>

  </xsl:template>

  <!--
    ********************************************************************************************************************
    *** Buttons groups.
    ********************************************************************************************************************
  -->
  <xsl:template name="getTextFormatControls">
    <xsl:text>bold,italic,strikethrough</xsl:text>
  </xsl:template>

  <xsl:template name="getPhraseControls">
    <xsl:text>blockquote,abbr,acronym,cite,ins,del</xsl:text>
  </xsl:template>

  <xsl:template name="getColorControls">
    <xsl:text>forecolor,backcolor</xsl:text>
  </xsl:template>

  <xsl:template name="getTableControls">
    <xsl:text>tablecontrols</xsl:text>
  </xsl:template>

  <xsl:template name="getTableLightControls">
    <xsl:text>table</xsl:text>
  </xsl:template>

  <xsl:template name="getAlignmentControls">
    <xsl:text>justifyleft,justifycenter,justifyright,justifyfull</xsl:text>
  </xsl:template>

  <xsl:template name="getIndentControls">
    <xsl:text>outdent,indent</xsl:text>
  </xsl:template>

  <xsl:template name="getHyperlinkControls">
    <xsl:text>advlink,unlink,anchor</xsl:text>
  </xsl:template>

  <xsl:template name="getListControls">
    <xsl:text>bullist,numlist</xsl:text>
  </xsl:template>

  <xsl:template name="getSubSupControls">
    <xsl:text>sup,sub</xsl:text>
  </xsl:template>

  <xsl:template name="getEditControls">
    <xsl:text>cut,copy,paste,pastetext,pasteword</xsl:text>
  </xsl:template>

  <xsl:template name="getLinkControls">
    <xsl:text>cmslink,unlink,anchor</xsl:text>
  </xsl:template>

  <xsl:template name="getHistoryControls">
    <xsl:text>undo,redo</xsl:text>
  </xsl:template>

  <xsl:template name="getSearchReplaceControls">
    <xsl:text>search,replace</xsl:text>
  </xsl:template>

  <xsl:template name="getImageControls">
    <xsl:text>cmsimage</xsl:text>
  </xsl:template>

  <xsl:template name="getAttributeControls">
    <xsl:text>attribs</xsl:text>
  </xsl:template>

  <xsl:template name="getCharmapControls">
    <xsl:text>charmap</xsl:text>
  </xsl:template>

  <xsl:template name="getHrControls">
    <xsl:text>hr</xsl:text>
  </xsl:template>

  <xsl:template name="getSourceControls">
    <xsl:text>code</xsl:text>
  </xsl:template>

  <xsl:template name="getRemoveFormatControls">
    <xsl:text>removeformat</xsl:text>
  </xsl:template>

  <xsl:template name="getBlockFormatControls">
    <xsl:text>formatselect</xsl:text>
  </xsl:template>

  <xsl:template name="getStyleSelectControls">
    <xsl:text>styleselect</xsl:text>
  </xsl:template>

  <xsl:template name="getFullScreenControls">
    <xsl:text>fullscreen</xsl:text>
  </xsl:template>

  <xsl:template name="getFullPageControls">
    <xsl:text>fullpage</xsl:text>
  </xsl:template>

  <xsl:template name="getEmbedControls">
    <xsl:text>media</xsl:text>
  </xsl:template>

  <xsl:template name="getSeparatorControls">
    <xsl:text>|</xsl:text>
  </xsl:template>
</xsl:stylesheet>
