<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:template name="chooseicon">
        <xsl:param name="filename"/>
        <xsl:param name="shaded" select="false()"/>
        <xsl:param name="title"/>

        <xsl:variable name="fname">
            <xsl:call-template name="getsuffix">
                <xsl:with-param name="fname" select="$filename"/>
            </xsl:call-template>
        </xsl:variable>
        
        <img border="0" width="23" height="16" align="absmiddle">
            <xsl:attribute name="src">
                <xsl:choose>
                    <xsl:when test="$fname='zip' or $fname='gz' or $fname='tar' or $fname='arj' or $fname='rar'">
                        <xsl:text>images/icon_zip</xsl:text>
                    </xsl:when>

                    <xsl:when test="$fname='avi' or $fname='wma' or $fname='wmv' or $fname='mp3' or $fname='mpeg' or $fname='mpg' or $fname='midi' or $fname='mid' or $fname='av' or $fname='wav'">
                        <xsl:text>images/icon_avi</xsl:text>
                    </xsl:when>

                    <xsl:when test="$fname='ppt'">
                        <xsl:text>images/icon_ppt</xsl:text>
                    </xsl:when>
                    
                    <xsl:when test="$fname='pps'">
                        <xsl:text>images/icon_pps</xsl:text>
                    </xsl:when>

                    <xsl:when test="$fname='doc' or $fname='rtf'">
                        <xsl:text>images/icon_doc</xsl:text>
                    </xsl:when>

                    <xsl:when test="$fname='xls'">
                        <xsl:text>images/icon_xls</xsl:text>
                    </xsl:when>

                    <xsl:when test="$fname='swf'">
                        <xsl:text>images/icon_swf</xsl:text>
                    </xsl:when>

                    <xsl:when test="$fname='txt' or $fname='text' or $fname='xml'">
                        <xsl:text>images/icon_txt</xsl:text>
                    </xsl:when>

                    <xsl:when test="$fname='tif' or $fname='tiff'">
                        <xsl:text>images/icon_tif</xsl:text>
                    </xsl:when>

                    <xsl:when test="$fname='bmp'">
                        <xsl:text>images/icon_bmp</xsl:text>
                    </xsl:when>

                    <xsl:when test="$fname='xsl'">
                        <xsl:text>images/icon_xsl</xsl:text>
                    </xsl:when>

                    <xsl:when test="$fname='eps'">
                        <xsl:text>images/icon_eps</xsl:text>
                    </xsl:when>

                    <xsl:when test="$fname='pdf'">
                        <xsl:text>images/icon_pdf</xsl:text>
                    </xsl:when>

                    <xsl:when test="$fname='jpg' or $fname='jpeg'">
                        <xsl:text>images/icon_jpg</xsl:text>
                    </xsl:when>

                    <xsl:when test="$fname='gif' or $fname='png'">
                        <xsl:text>images/icon_gif</xsl:text>
                    </xsl:when>

                    <xsl:when test="$fname='htm' or $fname='html'">
                        <xsl:text>images/icon_htm</xsl:text>
                    </xsl:when>

                    <xsl:when test="$fname='qt' or $fname='mov'">
                        <xsl:text>images/icon_qt</xsl:text>
                    </xsl:when>

                    <xsl:when test="$fname='js'">
                        <xsl:text>images/icon_js</xsl:text>
                    </xsl:when>

                    <xsl:when test="$fname='css'">
                        <xsl:text>images/icon_css</xsl:text>
                    </xsl:when>

                    <xsl:otherwise>
                        <xsl:text>images/icon_sys</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>

                <xsl:if test="$shaded">
                  <xsl:text>_shaded</xsl:text>
                </xsl:if>

                <xsl:text>.gif</xsl:text>
            </xsl:attribute>

            <xsl:if test="$title != ''">
              <xsl:attribute name="title">
                <xsl:value-of select="$title"/>
              </xsl:attribute>
            </xsl:if>
        </img>
    </xsl:template>

</xsl:stylesheet>