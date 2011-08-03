module("CodeMirror");

var g_dataSourceSampe = '';

g_dataSourceSampe += '<datasources>\n';
g_dataSourceSampe += '  <datasource>\n';
g_dataSourceSampe += '    <methodname>getContent</methodname>\n';
g_dataSourceSampe += '    <parameters>\n';
g_dataSourceSampe += '      <parameter name="key" type="int[]">${select(param.key,-1)}</parameter>\n';
g_dataSourceSampe += '      <parameter name="query" type="string"></parameter>\n';
g_dataSourceSampe += '      <parameter name="orderby" type="string"></parameter>\n';
g_dataSourceSampe += '      <parameter name="index" type="int">0</parameter>\n';
g_dataSourceSampe += '      <parameter name="count" type="int">10</parameter>\n';
g_dataSourceSampe += '      <parameter name="includeData" type="boolean">true</parameter>\n';
g_dataSourceSampe += '      <parameter name="childrenLevel" type="int">1</parameter>\n';
g_dataSourceSampe += '      <parameter name="parentLevel" type="int">0</parameter>\n';
g_dataSourceSampe += '    </parameters>\n';
g_dataSourceSampe += '  </datasource>\n';
g_dataSourceSampe += '  <datasource>\n';
g_dataSourceSampe += '    <methodname>getContentBySection</methodname>\n';
g_dataSourceSampe += '    <parameters>\n';
g_dataSourceSampe += '      <parameter name="id" type="int[]">${select(portal.pageKey,-1)}</parameter>\n';
g_dataSourceSampe += '      <parameter name="levels" type="int">1</parameter>\n';
g_dataSourceSampe += '      <parameter name="query" type="string"></parameter>\n';
g_dataSourceSampe += '      <parameter name="orderby" type="string"></parameter>\n';
g_dataSourceSampe += '      <parameter name="index" type="int">0</parameter>\n';
g_dataSourceSampe += '      <parameter name="count" type="int">10</parameter>\n';
g_dataSourceSampe += '      <parameter name="includeData" type="boolean">true</parameter>\n';
g_dataSourceSampe += '      <parameter name="childrenLevel" type="int">1</parameter>\n';
g_dataSourceSampe += '      <parameter name="parentLevel" type="int">0</parameter>\n';
g_dataSourceSampe += '    </parameters>\n';
g_dataSourceSampe += '  </datasource>\n';
g_dataSourceSampe += '</datasources>\n';

var g_XMLFirstDataSourceIndented = '';

g_XMLFirstDataSourceIndented +='<datasources>\n';
g_XMLFirstDataSourceIndented +='  <datasource>\n';
g_XMLFirstDataSourceIndented +='    <methodname>getContent</methodname>\n';
g_XMLFirstDataSourceIndented +='    <parameters>\n';
g_XMLFirstDataSourceIndented +='      <parameter name="key" type="int[]">${select(param.key,-1)}</parameter>\n';
g_XMLFirstDataSourceIndented +='      <parameter name="query" type="string"></parameter>\n';
g_XMLFirstDataSourceIndented +='      <parameter name="orderby" type="string"></parameter>\n';
g_XMLFirstDataSourceIndented +='      <parameter name="index" type="int">0</parameter>\n';
g_XMLFirstDataSourceIndented +='      <parameter name="count" type="int">10</parameter>\n';
g_XMLFirstDataSourceIndented +='      <parameter name="includeData" type="boolean">true</parameter>\n';
g_XMLFirstDataSourceIndented +='      <parameter name="childrenLevel" type="int">1</parameter>\n';
g_XMLFirstDataSourceIndented +='      <parameter name="parentLevel" type="int">0</parameter>\n';
g_XMLFirstDataSourceIndented +='    </parameters>\n';
g_XMLFirstDataSourceIndented +='  </datasource>\n';
g_XMLFirstDataSourceIndented +='<datasource>\n';
g_XMLFirstDataSourceIndented +='<methodname>getContentBySection</methodname>\n';
g_XMLFirstDataSourceIndented +='<parameters>\n';
g_XMLFirstDataSourceIndented +='<parameter name="id" type="int[]">${select(portal.pageKey,-1)}</parameter>\n';
g_XMLFirstDataSourceIndented +='<parameter name="levels" type="int">1</parameter>\n';
g_XMLFirstDataSourceIndented +='<parameter name="query" type="string"></parameter>\n';
g_XMLFirstDataSourceIndented +='<parameter name="orderby" type="string"></parameter>\n';
g_XMLFirstDataSourceIndented +='<parameter name="index" type="int">0</parameter>\n';
g_XMLFirstDataSourceIndented +='<parameter name="count" type="int">10</parameter>\n';
g_XMLFirstDataSourceIndented +='<parameter name="includeData" type="boolean">true</parameter>\n';
g_XMLFirstDataSourceIndented +='<parameter name="childrenLevel" type="int">1</parameter>\n';
g_XMLFirstDataSourceIndented +='<parameter name="parentLevel" type="int">0</parameter>\n';
g_XMLFirstDataSourceIndented +='</parameters>\n';
g_XMLFirstDataSourceIndented +='</datasource>\n';
g_XMLFirstDataSourceIndented +='</datasources>\n';


test( 'XML is valid', function()
{
    expect( 2 );

    g_codeMirror.setCode( g_dataSourceSampe );

    equals( g_codeMirror.getCode( ), g_dataSourceSampe, 'Is XML perserved' );
    equals( isStringValidXML( g_codeMirror.getCode( ) ), true, 'Valid XML (client validation)' );
} );

test( 'Indenting', function()
{
    expect( 2 );

    var trimmedXML = g_dataSourceSampe.replace( / {2,}/g, '' );

    g_codeMirror.setCode( trimmedXML );
    g_codeMirror.reindent( );

    equals( g_codeMirror.getCode( ), g_dataSourceSampe, 'Is XML perserved' );

    g_codeMirror.setCode( trimmedXML );
    g_codeMirror.selectLines( g_codeMirror.nthLine(2), 0, g_codeMirror.nthLine(15), 0);
    g_codeMirror.reindentSelection();

    equals( g_codeMirror.getCode( ), g_XMLFirstDataSourceIndented, 'Is XML perserved' );
} );

