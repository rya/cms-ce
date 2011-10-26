<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output method="html"/>

        <xsl:template name="formbuilder_javascript">

function formbuilder_editField(obj) {
        var buttonList = document.getElementsByName(obj.name);

        // Find row index
        var idx = 0;
        for (; idx &lt; buttonList.length; ++idx) {
                if (buttonList[idx] == obj){
                        break;
                }
        }

        ++idx; // Skip the row at the top
		
        <xsl:text>showPopupWindow('adminpage?page=</xsl:text>
        <xsl:value-of select="$page"/>
        <xsl:text>&amp;op=formbuilder&amp;subop=typeselector&amp;type='+ document.getElementsByName('field_type')[idx - 1].value +'</xsl:text>
        <xsl:text>&amp;row='+ idx ,</xsl:text>
        <xsl:text> 'fieldselector', 500, 500);</xsl:text>
}
        </xsl:template>
</xsl:stylesheet>

