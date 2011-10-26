<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>
    
    <xsl:template name="dropdown-color">
      <xsl:param name="width"/>
      <xsl:param name="name"/>
        
      <select name="{$name}" style="width:{$width}px;"> 
		<option value="" selected="selected">%fldNone%</option>
		<option style="background-color: #ff0000" value="#ff0000">#FF0000</option>
		<option style="background-color: #ffff00" value="#ffff00">#FFFF00</option>
		<option style="background-color: #00ff00" value="#00ff00">#00FF00</option>
		<option style="background-color: #00ffff" value="#00ffff">#00FFFF</option>
		<option style="background-color: #0000ff" value="#0000ff">#0000FF</option>
		<option style="background-color: #ff00ff" value="#ff00ff">#FF00FF</option>
		<option style="background-color: #ffffff" value="#ffffff">#FFFFFF</option>
		<option style="background-color: #f5f5f5" value="#f5f5f5">#F5F5F5</option>
		<option style="background-color: #dcdcdc" value="#dcdcdc">#DCDCDC</option>
		<option style="background-color: #d3d3d3" value="#d3d3d3">#D3D3D3</option>
		<option style="background-color: #c0c0c0" value="#c0c0c0">#C0C0C0</option>
		<option style="background-color: #a9a9a9" value="#a9a9a9">#A9A9A9</option>
		<option style="background-color: #808080" value="#808080">#808080</option>
		<option style="background-color: #696969" value="#696969">#696969</option>
		<option style="background-color: #000000" value="#000000">#000000</option>
		<option style="background-color: #2f4f4f" value="#2f4f4f">#2F4F4F</option>
		<option style="background-color: #708090" value="#708090">#708090</option>
		<option style="background-color: #778899" value="#778899">#778899</option>
		<option style="background-color: #4682b4" value="#4682b4">#4682B4</option>
		<option style="background-color: #4169e1" value="#4169e1">#4169E1</option>
		<option style="background-color: #6495ed" value="#6495ed">#6495ED</option>
		<option style="background-color: #b0c4de" value="#b0c4de">#B0C4DE</option>
		<option style="background-color: #7b68ee" value="#7b68ee">#7B68EE</option>
		<option style="background-color: #6a5acd" value="#6a5acd">#6A5ACD</option>
		<option style="background-color: #483d8b" value="#483d8b">#483D8B</option>
		<option style="background-color: #191970" value="#191970">#191970</option>
		<option style="background-color: #000080" value="#000080">#000080</option>
		<option style="background-color: #00008b" value="#00008b">#00008B</option>
		<option style="background-color: #0000cd" value="#0000cd">#0000CD</option>
		<option style="background-color: #1e90ff" value="#1e90ff">#1E90FF</option>
		<option style="background-color: #00bfff" value="#00bfff">#00BFFF</option>
		<option style="background-color: #87cefa" value="#87cefa">#87CEFA</option>
		<option style="background-color: #87ceeb" value="#87ceeb">#87CEEB</option>
		<option style="background-color: #add8e6" value="#add8e6">#ADD8E6</option>
		<option style="background-color: #b0e0e6" value="#b0e0e6">#B0E0E6</option>
		<option style="background-color: #f0ffff" value="#f0ffff">#F0FFFF</option>
		<option style="background-color: #e0ffff" value="#e0ffff">#E0FFFF</option>
		<option style="background-color: #afeeee" value="#afeeee">#AFEEEE</option>
		<option style="background-color: #00ced1" value="#00ced1">#00CED1</option>
		<option style="background-color: #5f9ea0" value="#5f9ea0">#5F9EA0</option>
		<option style="background-color: #48d1cc" value="#48d1cc">#48D1CC</option>
		<option style="background-color: #00ffff" value="#00ffff">#00FFFF</option>
		<option style="background-color: #40e0d0" value="#40e0d0">#40E0D0</option>
		<option style="background-color: #20b2aa" value="#20b2aa">#20B2AA</option>
		<option style="background-color: #008b8b" value="#008b8b">#008B8B</option>
		<option style="background-color: #008080" value="#008080">#008080</option>
		<option style="background-color: #7fffd4" value="#7fffd4">#7FFFD4</option>
		<option style="background-color: #66cdaa" value="#66cdaa">#66CDAA</option>
		<option style="background-color: #8fbc8f" value="#8fbc8f">#8FBC8F</option>
		<option style="background-color: #3cb371" value="#3cb371">#3CB371</option>
		<option style="background-color: #2e8b57" value="#2e8b57">#2E8B57</option>
		<option style="background-color: #006400" value="#006400">#006400</option>
		<option style="background-color: #008000" value="#008000">#008000</option>
		<option style="background-color: #228b22" value="#228b22">#228B22</option>
		<option style="background-color: #32cd32" value="#32cd32">#32CD32</option>
		<option style="background-color: #00ff00" value="#00ff00">#00FF00</option>
		<option style="background-color: #7fff00" value="#7fff00">#7FFF00</option>
		<option style="background-color: #7cfc00" value="#7cfc00">#7CFC00</option>
		<option style="background-color: #adff2f" value="#adff2f">#ADFF2F</option>
		<option style="background-color: #98fb98" value="#98fb98">#98FB98</option>
		<option style="background-color: #90ee90" value="#90ee90">#90EE90</option>
		<option style="background-color: #00ff7f" value="#00ff7f">#00FF7F</option>
		<option style="background-color: #00fa9a" value="#00fa9a">#00FA9A</option>
		<option style="background-color: #556b2f" value="#556b2f">#556B2F</option>
		<option style="background-color: #6b8e23" value="#6b8e23">#6B8E23</option>
		<option style="background-color: #808000" value="#808000">#808000</option>
		<option style="background-color: #bdb76b" value="#bdb76b">#BDB76B</option>
		<option style="background-color: #b8860b" value="#b8860b">#B8860B</option>
		<option style="background-color: #daa520" value="#daa520">#DAA520</option>
		<option style="background-color: #ffd700" value="#ffd700">#FFD700</option>
		<option style="background-color: #f0e68c" value="#f0e68c">#F0E68C</option>
		<option style="background-color: #eee8aa" value="#eee8aa">#EEE8AA</option>
		<option style="background-color: #ffebcd" value="#ffebcd">#FFEBCD</option>
		<option style="background-color: #ffe4b5" value="#ffe4b5">#FFE4B5</option>
		<option style="background-color: #f5deb3" value="#f5deb3">#F5DEB3</option>
		<option style="background-color: #ffdead" value="#ffdead">#FFDEAD</option>
		<option style="background-color: #deb887" value="#deb887">#DEB887</option>
		<option style="background-color: #d2b48c" value="#d2b48c">#D2B48C</option>
		<option style="background-color: #bc8f8f" value="#bc8f8f">#BC8F8F</option>
		<option style="background-color: #a0522d" value="#a0522d">#A0522D</option>
		<option style="background-color: #8b4513" value="#8b4513">#8B4513</option>
		<option style="background-color: #d2691e" value="#d2691e">#D2691E</option>
		<option style="background-color: #cd853f" value="#cd853f">#CD853F</option>
		<option style="background-color: #f4a460" value="#f4a460">#F4A460</option>
		<option style="background-color: #8b0000" value="#8b0000">#8B0000</option>
		<option style="background-color: #800000" value="#800000">#800000</option>
		<option style="background-color: #a52a2a" value="#a52a2a">#A52A2A</option>
		<option style="background-color: #b22222" value="#b22222">#B22222</option>
		<option style="background-color: #cd5c5c" value="#cd5c5c">#CD5C5C</option>
		<option style="background-color: #f08080" value="#f08080">#F08080</option>
		<option style="background-color: #fa8072" value="#fa8072">#FA8072</option>
		<option style="background-color: #e9967a" value="#e9967a">#E9967A</option>
		<option style="background-color: #ffa07a" value="#ffa07a">#FFA07A</option>
		<option style="background-color: #ff7f50" value="#ff7f50">#FF7F50</option>
		<option style="background-color: #ff6347" value="#ff6347">#FF6347</option>
		<option style="background-color: #ff8c00" value="#ff8c00">#FF8C00</option>
		<option style="background-color: #ffa500" value="#ffa500">#FFA500</option>
		<option style="background-color: #ff4500" value="#ff4500">#FF4500</option>
		<option style="background-color: #dc143c" value="#dc143c">#DC143C</option>
		<option style="background-color: #ff0000" value="#ff0000">#FF0000</option>
		<option style="background-color: #ff1493" value="#ff1493">#FF1493</option>
		<option style="background-color: #ff00ff" value="#ff00ff">#FF00FF</option>
		<option style="background-color: #ff69b4" value="#ff69b4">#FF69B4</option>
		<option style="background-color: #ffb6c1" value="#ffb6c1">#FFB6C1</option>
		<option style="background-color: #ffc0cb" value="#ffc0cb">#FFC0CB</option>
		<option style="background-color: #db7093" value="#db7093">#DB7093</option>
		<option style="background-color: #c71585" value="#c71585">#C71585</option>
		<option style="background-color: #800080" value="#800080">#800080</option>
		<option style="background-color: #8b008b" value="#8b008b">#8B008B</option>
		<option style="background-color: #9370db" value="#9370db">#9370DB</option>
		<option style="background-color: #8a2be2" value="#8a2be2">#8A2BE2</option>
		<option style="background-color: #4b0082" value="#4b0082">#4B0082</option>
		<option style="background-color: #9400d3" value="#9400d3">#9400D3</option>
		<option style="background-color: #9932cc" value="#9932cc">#9932CC</option>
		<option style="background-color: #ba55d3" value="#ba55d3">#BA55D3</option>
		<option style="background-color: #da70d6" value="#da70d6">#DA70D6</option>
		<option style="background-color: #ee82ee" value="#ee82ee">#EE82EE</option>
		<option style="background-color: #dda0dd" value="#dda0dd">#DDA0DD</option>
		<option style="background-color: #d8bfd8" value="#d8bfd8">#D8BFD8</option>
		<option style="background-color: #e6e6fa" value="#e6e6fa">#E6E6FA</option>
		<option style="background-color: #f8f8ff" value="#f8f8ff">#F8F8FF</option>
		<option style="background-color: #f0f8ff" value="#f0f8ff">#F0F8FF</option>
		<option style="background-color: #f5fffa" value="#f5fffa">#F5FFFA</option>
		<option style="background-color: #f0fff0" value="#f0fff0">#F0FFF0</option>
		<option style="background-color: #fafad2" value="#fafad2">#FAFAD2</option>
		<option style="background-color: #fffacd" value="#fffacd">#FFFACD</option>
		<option style="background-color: #fff8dc" value="#fff8dc">#FFF8DC</option>
		<option style="background-color: #ffffe0" value="#ffffe0">#FFFFE0</option>
		<option style="background-color: #fffff0" value="#fffff0">#FFFFF0</option>
		<option style="background-color: #fffaf0" value="#fffaf0">#FFFAF0</option>
		<option style="background-color: #faf0e6" value="#faf0e6">#FAF0E6</option>
		<option style="background-color: #fdf5e6" value="#fdf5e6">#FDF5E6</option>
		<option style="background-color: #faebd7" value="#faebd7">#FAEBD7</option>
		<option style="background-color: #ffe4c4" value="#ffe4c4">#FFE4C4</option>
		<option style="background-color: #ffdab9" value="#ffdab9">#FFDAB9</option>
		<option style="background-color: #ffefd5" value="#ffefd5">#FFEFD5</option>
		<option style="background-color: #fff5ee" value="#fff5ee">#FFF5EE</option>
		<option style="background-color: #fff0f5" value="#fff0f5">#FFF0F5</option>
		<option style="background-color: #ffe4e1" value="#ffe4e1">#FFE4E1</option>
		<option style="background-color: #fffafa" value="#fffafa">#FFFAFA</option>
	  </select>
    </xsl:template>
    
</xsl:stylesheet>
