<?xml version="1.0" encoding="utf-8"?>

<!DOCTYPE xsl:stylesheet [<!ENTITY nbsp "&#160;">]>

<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:param name="parentcategoryadministrate" select="'false'"/>

  <xsl:template name="contentbrowsemenu">
    <xsl:param name="op"/>
    <xsl:param name="subop"/>
    <xsl:param name="page"/>
    <xsl:param name="cat"/>
    <xsl:param name="fieldname"/>
    <xsl:param name="fieldrow"/>
    <xsl:param name="contenttypestring"/>
    <xsl:param name="selectedunitkey"/>
    <xsl:param name="contenttypeelem"/>
    <xsl:param name="searchonly" select="false()"/>
    <xsl:param name="contenthandler"/>
    <xsl:param name="user-has-categorycreate"/>
    <xsl:param name="user-has-categoryadministrate"/>
    <xsl:param name="minoccurrence"/>
    <xsl:param name="maxoccurrence"/>

    <xsl:variable name="newContentMenuItem">
      <xsl:apply-templates select="$contenttypeelem/@handler" mode="newbutton"/>
    </xsl:variable>

    <xsl:variable name="category" select="/data/category"/>
    <xsl:variable name="parentcategoryadministrate" select="$parentcategoryadministrate = 'true'"/>

    <script type="text/javascript">
      function emptyCategory()
      {
        var prompt = confirm('%alertEmptyCategory%\n\n%alertEmptyCategoryMsg%');
        if ( prompt )
        {
          waitsplash();
          return true;
        }
        else
        {
          return false;
        }
      }
    </script>

    <table border="0" cellpadding="0" cellspacing="0" class="contentbrowsemenu">
      <tr>
        <!-- New button if user has create right -->
        <xsl:if test="($newContentMenuItem = 'true' and $user-has-categorycreate) or ($user-has-categoryadministrate)">

          <td>

            <xsl:variable name="newContentUrl">
              <xsl:text>adminpage?page=</xsl:text>
              <xsl:value-of select="$page"/>
              <xsl:text>&amp;op=form</xsl:text>
              <xsl:text>&amp;cat=</xsl:text>
              <xsl:value-of select="$cat"/>
              <xsl:text>&amp;selectedunitkey=</xsl:text>
              <xsl:value-of select="$selectedunitkey"/>
              <xsl:text>&amp;subop=</xsl:text><xsl:value-of select="$subop"/>
              <xsl:text>&amp;fieldname=</xsl:text><xsl:value-of select="$fieldname"/>
              <xsl:text>&amp;fieldrow=</xsl:text><xsl:value-of select="$fieldrow"/>
              <xsl:text>&amp;contenttypestring=</xsl:text><xsl:value-of select="$contenttypestring"/>
              <xsl:if test="$minoccurrence">
                <xsl:text>&amp;minoccurrence=</xsl:text><xsl:value-of select="$minoccurrence"/>
              </xsl:if>
              <xsl:if test="$maxoccurrence">
                <xsl:text>&amp;maxoccurrence=</xsl:text><xsl:value-of select="$maxoccurrence"/>
              </xsl:if>
            </xsl:variable>

            <xsl:variable name="newCategoryUrl">
              <xsl:text>adminpage?page=200&amp;op=form</xsl:text>
              <xsl:text>&amp;modulepage=</xsl:text>
              <xsl:value-of select="$page"/>
              <xsl:text>&amp;cat=</xsl:text>
              <xsl:value-of select="$cat"/>
              <xsl:text>&amp;selectedunitkey=</xsl:text>
              <xsl:value-of select="$selectedunitkey"/>
              <xsl:if test="$minoccurrence">
                <xsl:text>&amp;minoccurrence=</xsl:text><xsl:value-of select="$minoccurrence"/>
              </xsl:if>
              <xsl:if test="$maxoccurrence">
                <xsl:text>&amp;maxoccurrence=</xsl:text><xsl:value-of select="$maxoccurrence"/>
              </xsl:if>
            </xsl:variable>

            <ul id="cmdNewMenuButton" title="%cmdNew%" class="cms-menu-button">
              <!-- New button if user has create right -->
              <xsl:if test="$newContentMenuItem = 'true' and $user-has-categorycreate">
                <li style="background-image:url(images/icon_state_unsaved_draft.gif)">
                  <a href="{$newContentUrl}">
                    <xsl:value-of select="concat($contenttypeelem/name, ' (%lblContent%)')"/>
                  </a>
                </li>
              </xsl:if>

              <!-- New category button if user has administrate on category -->
              <xsl:if test="$user-has-categoryadministrate">
                <li style="background-image:url(images/icon_folder.gif)">
                  <a href="{$newCategoryUrl}">%cmdNewCategory%</a>
                </li>
              </xsl:if>
            </ul>

            <script type="text/javascript" charset="utf-8">
              var menuButton = new cms.ui.MenuButton('cmdNewMenuButton');
              menuButton.insert();
            </script>
          </td>
          <td>&nbsp;</td>
        </xsl:if>

        <td>
          <form id="formSearch" name="formSearch" method="get" action="adminpage" style="margin:0; padding:0">
            <table border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td>
                  <input type="hidden" name="op" value="{$op}"/>
                  <input type="hidden" name="subop" value="{$subop}"/>
                  <input type="hidden" name="page" value="{$page}"/>
                  <input type="hidden" name="cat" value="{$cat}"/>
                  <input type="hidden" name="fieldname" value="{$fieldname}"/>
                  <input type="hidden" name="fieldrow" value="{$fieldrow}"/>
                  <input type="hidden" name="contenttypestring" value="{$contenttypestring}"/>
                  <input type="hidden" name="selectedunitkey" value="{$selectedunitkey}"/>
                  <input type="hidden" name="searchtype" value="simple"/>
                  <input type="hidden" name="scope" value="title"/>
                  <input type="hidden" name="waitscreen" value="true"/>
                  <input type="hidden" name="contenthandler" value="{$contenthandler}"/>
                  <input type="hidden" name="minoccurrence" value="{$minoccurrence}"/>
                  <input type="hidden" name="maxoccurrence" value="{$maxoccurrence}"/>
                  <!-- Search field -->
                  <input type="text" id="searchtext" name="searchtext" size="12" value="{$searchtext}"/>
                </td>
                <td>
                  <!-- Search button -->
                  <xsl:call-template name="button">
                    <xsl:with-param name="type" select="'submit'"/>
                    <xsl:with-param name="caption" select="'%cmdQuickSearch%'"/>
                    <xsl:with-param name="name" select="'search'"/>
                  </xsl:call-template>
                </td>
              </tr>
            </table>
          </form>
        </td>
        <td>&nbsp;</td>
        <td>
          <!-- Advanced search button -->
          <xsl:call-template name="button">
            <xsl:with-param name="type" select="'link'"/>
            <xsl:with-param name="caption" select="'%cmdSearchDotDotDot%'"/>
            <xsl:with-param name="href">
              <xsl:text>adminpage?page=</xsl:text>
              <xsl:value-of select="$page"/>
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
                <xsl:text>&amp;minoccurrence=</xsl:text><xsl:value-of select="$minoccurrence"/>
              </xsl:if>
              <xsl:if test="$maxoccurrence">
                <xsl:text>&amp;maxoccurrence=</xsl:text><xsl:value-of select="$maxoccurrence"/>
              </xsl:if>
              <xsl:if test="$contenthandler">
                <xsl:text>&amp;contenthandler=</xsl:text><xsl:value-of select="$contenthandler"/>
              </xsl:if>
            </xsl:with-param>
          </xsl:call-template>
        </td>
        <td>&nbsp;</td>

        <xsl:variable name="fileimport">
          <xsl:value-of select="$contenttypeelem/moduledata/config/fileimport/@operation"/>
        </xsl:variable>
        <xsl:if test="$contenttypeelem/moduledata/config/imports/import/@name">
          <td>
            <xsl:call-template name="button">
              <xsl:with-param name="type" select="'link'"/>
              <xsl:with-param name="caption" select="'%cmdFileImport%'"/>
              <xsl:with-param name="href">
                <xsl:text>adminpage?page=</xsl:text>
                <xsl:value-of select="$page"/>
                <xsl:text>&amp;op=fileimportform</xsl:text>
                <xsl:text>&amp;cat=</xsl:text>
                <xsl:value-of select="$cat"/>
                <xsl:text>&amp;selectedunitkey=</xsl:text>
                <xsl:value-of select="$selectedunitkey"/>
              </xsl:with-param>
            </xsl:call-template>
          </td>
          <td>&nbsp;</td>
        </xsl:if>

        <xsl:if
            test="$contenttypeelem/@handler = 'com.enonic.vertical.adminweb.handlers.ContentFileHandlerServlet' or $contenttypeelem/@handler = 'com.enonic.vertical.adminweb.handlers.ContentEnhancedImageHandlerServlet'">
          <td>
            <xsl:apply-templates select="$contenttypeelem/@handler" mode="custombuttons_center"/>
          </td>
          <td>&nbsp;</td>
        </xsl:if>

        <xsl:if test="not($searchonly)">

          <!-- Edit category button if user has administrate on category -->
          <xsl:if test="$user-has-categoryadministrate">
            <td>
              <xsl:variable name="buttontext">
                <xsl:choose>
                  <xsl:when test="$category/@supercategorykey">
                    <xsl:text>%cmdEditCategory%</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>%cmdEditContentRepository%</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:variable>
              <xsl:call-template name="button">
                <xsl:with-param name="type" select="'link'"/>
                <xsl:with-param name="caption">
                  <xsl:value-of select="$buttontext"/>
                </xsl:with-param>
                <xsl:with-param name="href">
                  <xsl:text>adminpage?</xsl:text>
                  <xsl:choose>
                    <xsl:when test="$category/@supercategorykey">
                      <xsl:text>page=200</xsl:text>
                      <xsl:text>&amp;cat=</xsl:text>
                      <xsl:value-of select="$cat"/>
                      <xsl:text>&amp;key=</xsl:text>
                      <xsl:value-of select="$cat"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:text>page=600</xsl:text>
                      <xsl:text>&amp;categorykey=</xsl:text>
                      <xsl:value-of select="$cat"/>
                      <xsl:text>&amp;key=</xsl:text>
                      <xsl:value-of select="$selectedunitkey"/>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:text>&amp;op=form&amp;modulepage=</xsl:text>
                  <xsl:value-of select="$page"/>
                  <xsl:text>&amp;selectedunitkey=</xsl:text>
                  <xsl:value-of select="$selectedunitkey"/>
                  <xsl:if test="$minoccurrence">
                    <xsl:text>&amp;minoccurrence=</xsl:text><xsl:value-of select="$minoccurrence"/>
                  </xsl:if>
                  <xsl:if test="$maxoccurrence">
                    <xsl:text>&amp;maxoccurrence=</xsl:text><xsl:value-of select="$maxoccurrence"/>
                  </xsl:if>
                </xsl:with-param>
              </xsl:call-template>
            </td>
            <td>&nbsp;</td>
          </xsl:if>

          <!-- Move category button if user has administrate on category -->
          <xsl:if test="$user-has-categoryadministrate and $parentcategoryadministrate">
            <td>
              <xsl:call-template name="button">
                <xsl:with-param name="type" select="'button'"/>
                <xsl:with-param name="caption" select="'%cmdMoveCategory%'"/>
                <xsl:with-param name="name" select="'movecategorybtn'"/>
                <xsl:with-param name="onclick">
                  <xsl:text>javascript:document.forms['formAdmin'].op.value = 'move_category';_OpenNewCategorySelector();</xsl:text>
                </xsl:with-param>
              </xsl:call-template>
            </td>
            <td>&nbsp;</td>
          </xsl:if>

          <xsl:variable name="buttontext">
            <xsl:choose>
              <xsl:when test="$category/@supercategorykey">
                <xsl:text>%cmdDeleteCategory%</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>%cmdRemoveContentRepository%</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>

          <!-- Delete category button if there is no content here and no subcategories, and user has administrate on category -->
          <xsl:if test="number(/data/contents/@totalcount) &lt;= 0 and $category/@subcategories != 'true' and $user-has-categoryadministrate">
            <td>
              <xsl:call-template name="button">
                <xsl:with-param name="type" select="'link'"/>
                <xsl:with-param name="caption">
                  <xsl:value-of select="$buttontext"/>
                </xsl:with-param>
                <xsl:with-param name="condition">confirm('%alertDeleteCategory%')</xsl:with-param>
                <xsl:with-param name="href">
                  <xsl:text>adminpage?</xsl:text>
                  <xsl:choose>
                    <xsl:when test="$category/@supercategorykey">
                      <xsl:text>page=200</xsl:text>
                      <xsl:text>&amp;key=</xsl:text>
                      <xsl:value-of select="$cat"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:text>page=600</xsl:text>
                      <xsl:text>&amp;key=</xsl:text>
                      <xsl:value-of select="$selectedunitkey"/>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:text>&amp;op=remove&amp;modulepage=</xsl:text>
                  <xsl:value-of select="$page"/>
                  <xsl:text>&amp;cat=</xsl:text>
                  <xsl:value-of select="$cat"/>
                  <xsl:text>&amp;selectedunitkey=</xsl:text>
                  <xsl:value-of select="$selectedunitkey"/>
                </xsl:with-param>
              </xsl:call-template>
            </td>
          </xsl:if>
          
        </xsl:if>
      </tr>
    </table>
  </xsl:template>
</xsl:stylesheet>