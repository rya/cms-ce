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
        idField: '_path',
        fields: [
            {name: '_name', type: 'string'},
            {name: '_path', type: 'string'},
            {name: '_type', type: 'string'},
            {name: '_properties', type: 'auto'}
        ],
        idProperty: '_path'
    });

    var store = Ext.create('Ext.data.TreeStore', {
        model: 'Node',
        proxy: {
            type: 'ajax',
            url: 'jcrjson',
            reader: {
                root: '_children'
            }
        },

        nodeParam: '_path',
        defaultRootId: '/',
        autoLoad: true,
        folderSort: false
    });

    var tree = Ext.create('Ext.tree.Panel', {
        title: 'JCR Repository Tree',
        width: '100%',
        height: '100%',
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
            dataIndex: '_name'
        },{
            xtype: 'templatecolumn',
            text: 'Path',
            flex: 2,
            sortable: false,
            dataIndex: '_path',
            align: 'left',
            tpl: Ext.create('Ext.XTemplate', '{_path}')
        },{
            text: 'Type',
            flex: 1,
            dataIndex: '_type',
            sortable: false
        }],

        listeners: {
            itemclick: function(node, event){
                detailProperties.store.loadData(event.data._properties);
            }
        }
    });


    var storeDetails = Ext.create('Ext.data.Store', {
        fields:['name', 'value', 'type'],
        proxy: {
            type: 'memory',
            reader: {
                type: 'json'
            }
        }
    });
    
    var detailProperties = Ext.create('Ext.grid.Panel', {
        store: storeDetails,
        title: 'Node details',
        width: '100%',
        height: '100%',
        layout: 'fit',
        columnLines: true,
        frame: false,
        columns: [
            {
                text: 'Property',
                dataIndex: 'name',
                sortable: true,
                flex: 2
            },
            {
                text: 'Value',
                dataIndex: 'value',
                sortable: true,
                flex: 4
            },
            {
                text: 'Type',
                dataIndex: 'type',
                sortable: true,
                flex: 1
            }
        ],
        viewConfig: {
            trackOver : true,
            stripeRows: true
        }
    });


    var viewPort = new Ext.Viewport({
        renderTo: Ext.getBody(),
        layout: 'border',
        split: true,
        items: [
            {
                region: 'center',
                xtype: 'panel',
                items: [
                    tree
                ],
                flex:4
            },
            {
                region: 'south',
                xtype: 'panel',
                items: [
                    detailProperties
                ],
                flex:3,
                split: true
            }
        ]
    })

});

    </script>

</head>
<body>
</body>
</html>
