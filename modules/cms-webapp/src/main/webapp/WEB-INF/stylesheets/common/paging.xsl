<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
        <!ENTITY nbsp "&#160;">
        ]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <!--
    Template: paging
  -->
  <xsl:template name="paging">
    <xsl:param name="url"/>
    <xsl:param name="index" select="0"/>
    <xsl:param name="count" select="20"/>
    <xsl:param name="totalcount"/>

    <xsl:variable name="prevIndex">
      <xsl:call-template name="max">
        <xsl:with-param name="a" select="$index - $count"/>
        <xsl:with-param name="b" select="0"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="nextIndex">
      <xsl:choose>
        <xsl:when test="($index + $count) &gt;= $totalcount">
          <xsl:value-of select="$index"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="($index + $count)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="first">
      <xsl:call-template name="min">
        <xsl:with-param name="a" select="$index + 1"/>
        <xsl:with-param name="b" select="$totalcount"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="last">
      <xsl:call-template name="min">
        <xsl:with-param name="a" select="$index + $count"/>
        <xsl:with-param name="b" select="$totalcount"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:if test="$totalcount &gt; 0">
      <table border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td class="paging">
            <div>
              <xsl:choose>
                <xsl:when test="$index &gt; 0">
                  <a title="%cmdFirstPage%">
                    <xsl:attribute name="href">
                      <xsl:value-of select="$url"/>
                      <xsl:text>&amp;index=</xsl:text>
                      <xsl:value-of select="0"/>
                      <xsl:text>&amp;count=</xsl:text>
                      <xsl:value-of select="$count"/>
                    </xsl:attribute>
                    <span>&lt;&lt;</span>
                  </a>
                </xsl:when>
                <xsl:otherwise>
                  <span class="inactive" style="color: #a8a8a8">&lt;&lt;</span>
                </xsl:otherwise>
              </xsl:choose>

              <span>&nbsp;&nbsp;&nbsp;</span>

              <xsl:choose>
                <xsl:when test="not($prevIndex = $index)">
                  <a title="%cmdPrevious%">
                    <xsl:attribute name="href">
                      <xsl:value-of select="$url"/>
                      <xsl:text>&amp;index=</xsl:text>
                      <xsl:value-of select="$prevIndex"/>
                      <xsl:text>&amp;count=</xsl:text>
                      <xsl:value-of select="$count"/>
                    </xsl:attribute>
                    <span>&lt;</span>
                  </a>
                </xsl:when>
                <xsl:otherwise>
                  <span class="inactive" style="color: #a8a8a8">&lt;</span>
                </xsl:otherwise>
              </xsl:choose>
              &nbsp;

              <xsl:variable name="activePage" select="($index div $count) + 1"/>
              <xsl:variable name="numPages" select="ceiling($totalcount div $count)"/>

              <xsl:if test="$activePage &lt;= $numPages and $activePage &gt; 0">
                <xsl:variable name="pagingStart">
                  <xsl:choose>
                    <xsl:when test="$activePage &lt; 5">
                      <xsl:value-of select="1"/>
                    </xsl:when>
                    <xsl:when test="$activePage &gt;= 5 and $numPages &lt;= 10">
                      <!--xsl:value-of select="($numPages - $activePage) + 1"/-->
                      <xsl:value-of select="1"/>
                    </xsl:when>
                    <xsl:when test="$numPages - $activePage &lt; 5">
                      <xsl:value-of select="$numPages - 9"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="$activePage - 4"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>

                <xsl:variable name="pagingEnd">
                  <xsl:choose>
                    <xsl:when test="$activePage &lt; 5">
                      <xsl:choose>
                        <xsl:when test="$numPages &gt;= 10">
                          <xsl:value-of select="10"/>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:value-of select="$numPages"/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:when>
                    <xsl:when test="$activePage &gt;= 5 and $numPages &lt;= 10">
                      <xsl:value-of select="$numPages"/>
                    </xsl:when>
                    <xsl:when test="$numPages - $activePage &lt; 5">
                      <xsl:value-of select="$numPages"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="$activePage + 5"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>

                <xsl:call-template name="createPaging">
                  <xsl:with-param name="numPages" select="$numPages"/>
                  <xsl:with-param name="url" select="$url"/>
                  <xsl:with-param name="index" select="$index"/>
                  <xsl:with-param name="count" select="$count"/>
                  <xsl:with-param name="pagingStart" select="$pagingStart"/>
                  <xsl:with-param name="pagingEnd" select="$pagingEnd"/>
                  <xsl:with-param name="page" select="$pagingStart"/>
                </xsl:call-template>
              </xsl:if>

              &nbsp;
              <xsl:choose>
                <xsl:when test="not($nextIndex = $index)">
                  <a title="%cmdNext%">
                    <xsl:attribute name="href">
                      <xsl:value-of select="$url"/>
                      <xsl:text>&amp;index=</xsl:text>
                      <xsl:value-of select="$nextIndex"/>
                      <xsl:text>&amp;count=</xsl:text>
                      <xsl:value-of select="$count"/>
                    </xsl:attribute>
                    <span>&gt;</span>
                  </a>
                </xsl:when>
                <xsl:otherwise>
                  <span class="inactive" style="color: #a8a8a8">&nbsp;&gt;</span>
                </xsl:otherwise>
              </xsl:choose>

              <span>&nbsp;&nbsp;&nbsp;</span>

              <xsl:choose>
                <xsl:when test="$totalcount &gt; ($index + $count)">
                  <a title="%cmdLastPage%">
                    <xsl:attribute name="href">
                      <xsl:value-of select="$url"/>
                      <xsl:text>&amp;index=</xsl:text>
                      <xsl:value-of select="($numPages * $count) - $count"/>
                      <xsl:text>&amp;count=</xsl:text>
                      <xsl:value-of select="$count"/>
                    </xsl:attribute>
                    <span>&gt;&gt;</span>
                  </a>
                </xsl:when>
                <xsl:otherwise>
                  <span class="inactive" style="color: #a8a8a8">&gt;&gt;</span>
                </xsl:otherwise>
              </xsl:choose>
            </div>

          </td>
        </tr>
      </table>
    </xsl:if>
  </xsl:template>

  <!--
    Template: createPaging
  -->
  <xsl:template name="createPaging">
    <xsl:param name="numPages"/>
    <xsl:param name="url"/>
    <xsl:param name="index"/>
    <xsl:param name="count"/>
    <xsl:param name="pagingStart"/>
    <xsl:param name="pagingEnd"/>
    <xsl:param name="page"/>
    <xsl:variable name="idx">
      <xsl:choose>
        <xsl:when test="$page = 1">
          <xsl:value-of select="0"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="($page * $count) - $count"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="activePage" select="ceiling($index div $count) + 1"/>

    <xsl:choose>
      <xsl:when test="$numPages = 1">
        <span style="padding:0 3px 0 3px;color:#a8a8a8">
          <xsl:value-of select="$page"/>
        </span>
      </xsl:when>
      <xsl:when test="$page = $activePage">
        <span style="padding:0 3px 0 3px;">
          <xsl:value-of select="$page"/>
        </span>
      </xsl:when>
      <xsl:otherwise>
        <a title="%txtPage%: {$page}" style="padding:0 3px 0 3px">
          <xsl:attribute name="href">
            <xsl:value-of select="$url"/>
            <xsl:text>&amp;index=</xsl:text>
            <xsl:value-of select="$idx"/>
            <xsl:text>&amp;count=</xsl:text>
            <xsl:value-of select="$count"/>
          </xsl:attribute>
          <xsl:value-of select="$page"/>
        </a>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="$page &lt; $pagingEnd">
      <xsl:call-template name="createPaging">
        <xsl:with-param name="numPages" select="$numPages"/>
        <xsl:with-param name="url" select="$url"/>
        <xsl:with-param name="index" select="$index"/>
        <xsl:with-param name="count" select="$count"/>
        <xsl:with-param name="pagingStart" select="$pagingStart"/>
        <xsl:with-param name="pagingEnd" select="$pagingEnd"/>
        <xsl:with-param name="page" select="$page + 1"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>  

  <!--
    Template: createPerPage
  -->
  <xsl:template name="createPerPage">
    <xsl:param name="n" select="20"/>
    <xsl:param name="url"/>
    <xsl:param name="index"/>
    <xsl:param name="count"/>
    <xsl:param name="totalcount"/>

    <xsl:choose>
      <xsl:when test="$totalcount &lt;= 20">
        <span class="inactive" style="color:#a8a8a8">
          <xsl:value-of select="$n"/>
        </span>
      </xsl:when>
      <xsl:otherwise>

        <xsl:choose>
          <xsl:when test="$n = $count">
            <xsl:value-of select="$n"/>
          </xsl:when>
          <xsl:otherwise>
            <a title="{$n} %txtPrPage%">
              <xsl:attribute name="href">
                <xsl:value-of select="$url"/>
                <xsl:text>&amp;index=</xsl:text>
                <xsl:value-of select="0"/>
                <xsl:text>&amp;count=</xsl:text>
                <xsl:value-of select="$n"/>
              </xsl:attribute>
              <xsl:value-of select="$n"/>
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template name="perPage">
    <xsl:param name="index" select="0"/>
    <xsl:param name="count" select="20"/>
    <xsl:param name="totalcount"/>
    <xsl:variable name="first">
      <xsl:call-template name="min">
        <xsl:with-param name="a" select="$index + 1"/>
        <xsl:with-param name="b" select="$totalcount"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="last">
      <xsl:call-template name="min">
        <xsl:with-param name="a" select="$index + $count"/>
        <xsl:with-param name="b" select="$totalcount"/>
      </xsl:call-template>
    </xsl:variable>
    %txtDisplaying%
    <xsl:value-of select="$first"/>
    <xsl:text>-</xsl:text>
    <xsl:value-of select="$last"/>
    <xsl:text> %of% </xsl:text>
    <xsl:value-of select="$totalcount"/>
  </xsl:template>
  

  <xsl:template name="countSelect">
    <xsl:param name="count" select="20"/>
    <select name="countSelect" onchange="setCount( this )">
      <option value="20">
        <xsl:if test="$count = 20">
          <xsl:attribute name="selected">
            <xsl:text>true</xsl:text>
          </xsl:attribute>
        </xsl:if>
        20 %txtPrPage%
      </option>
      <option value="50">
        <xsl:if test="$count = 50">
          <xsl:attribute name="selected">
            <xsl:text>true</xsl:text>
          </xsl:attribute>
        </xsl:if>
        50 %txtPrPage%
      </option>
      <option value="100">
        <xsl:if test="$count = 100">
          <xsl:attribute name="selected">
            <xsl:text>true</xsl:text>
          </xsl:attribute>
        </xsl:if>
        100 %txtPrPage%
      </option>
    </select>

  </xsl:template>


  <!--
    Template: max
    Returns the lowest number
  -->
  <xsl:template name="max">
    <xsl:param name="a"/>
    <xsl:param name="b"/>
    <xsl:variable name="a2" select="number($a)"/>
    <xsl:variable name="b2" select="number($b)"/>
    <xsl:choose>
      <xsl:when test="$a2 &gt; $b2">
        <xsl:value-of select="$a2"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$b2"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--
    Template: min
    Returns the lowest number
  -->
  <xsl:template name="min">
    <xsl:param name="a"/>
    <xsl:param name="b"/>
    <xsl:variable name="a2" select="number($a)"/>
    <xsl:variable name="b2" select="number($b)"/>
    <xsl:choose>
      <xsl:when test="$a2 &lt; $b2">
        <xsl:value-of select="$a2"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$b2"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>