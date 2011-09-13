<html>
<head>
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="-1"/>

    <title>JCR Repository</title>
    <link rel="stylesheet" type="text/css" href="../admin/ext/resources/css/ext-all.css">
    <link rel="stylesheet" type="text/css" href="../admin/resources/css/main.css">
    <script type="text/javascript" src="../admin/ext/ext-all-debug.js"></script>
    <script type="text/javascript">

Ext.require([
    'Ext.data.*',
    'Ext.grid.*',
    'Ext.tree.*'
]);

Ext.onReady(function() {

    Ext.define('Node', {
        extend: 'Ext.data.Model',
        idField: 'path',
        fields: [
            {name: 'name', type: 'string'},
            {name: 'path', type: 'string'},
            {name: 'type', type: 'string'}
        ],
        idProperty: 'path'
    });

    var store = Ext.create('Ext.data.TreeStore', {
        model: 'Node',
        proxy: {
            type: 'ajax',
            url: 'jcrjson'
        },
        nodeParam: 'path',
        defaultRootId: '/',
        autoLoad: true,
        folderSort: false
    });

    var tree = Ext.create('Ext.tree.Panel', {
        title: 'JCR Repository Tree',
        width: '100%',
        height: '100%',
        renderTo: Ext.getBody(),
        collapsible: false,
        useArrows: true,
        rootVisible: false,
        store: store,
        multiSelect: false,
        singleExpand: false,
        lines: true,
        columns: [{
            xtype: 'treecolumn',
            text: 'Name',
            flex: 1,
            sortable: false,
            dataIndex: 'name'
        },{
            xtype: 'templatecolumn',
            text: 'Path',
            flex: 2,
            sortable: false,
            dataIndex: 'path',
            align: 'left',
            tpl: Ext.create('Ext.XTemplate', '{path}')
        },{
            text: 'Type',
            flex: 1,
            dataIndex: 'type',
            sortable: false
        }]
    });

});

    </script>

</head>
<body>
</body>
</html>
