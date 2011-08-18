Ext.define('CMS.view.dashboard.DashboardPalette', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.dashboardPalette',

    requires: [
        'CMS.view.dashboard.DragSource',
        'CMS.view.dashboard.Dashlet',
        'CMS.view.dashboard.ChartDashlet',
        'CMS.view.dashboard.GridDashlet'
    ],

    title: 'Dashboard Palette',
    split: true,
    collapsible: true,
    border: false,

    layout:'accordion',
    defaults: {
    },
    layoutConfig: {
        titleCollapse: false,
        animate: true,
        activeOnTop: true
    },
    initComponent: function() {
        var me = this;

        var generalStore = Ext.create("Ext.data.ArrayStore", {
            fields: [{
                name: "xtype"
            }, {
                name: "url"
            }, {
                name: "title"
            }, {
                name: "body"
            }],
            data: this.getGeneralData()
        });
        var reportsStore = Ext.create("Ext.data.ArrayStore", {
            fields: [{
                name: "xtype"
            }, {
                name: "url"
            }, {
                name: "title"
            }, {
                name: "body"
            }],
            data: this.getReportsData()
        });
        var systemStore = Ext.create("Ext.data.ArrayStore", {
            fields: [{
                name: "xtype"
            }, {
                name: "url"
            }, {
                name: "title"
            }, {
                name: "body"
            }],
            data: this.getSystemData()
        });

        var generalItem = Ext.create("Ext.view.View", {
            tpl: '<tpl for=".">' +
                '<div class="palette-item">' +
                    '<div class="item-icon">{title}</div>' +
                '</div>' +
             '</tpl>',
            itemSelector: 'div.palette-item',
            singleSelect: true,
            store: generalStore,
            listeners: {
                render:  {
                    fn: me.initDraggable
                }
            }
        });
        var reportsItem = Ext.create("Ext.view.View", {
            tpl: '<tpl for=".">' +
                '<div class="palette-item">' +
                    '<div class="item-icon">{title}</div>' +
                '</div>' +
             '</tpl>',
            itemSelector: 'div.palette-item',
            singleSelect: true,
            store: reportsStore,
            listeners: {
                render:  {
                    fn: me.initDraggable
                }
            }
        });
        var systemItem = Ext.create("Ext.view.View", {
            tpl: '<tpl for=".">' +
                '<div class="palette-item">' +
                    '<div class="item-icon">{title}</div>' +
                '</div>' +
             '</tpl>',
            itemSelector: 'div.palette-item',
            singleSelect: true,
            store: systemStore,
            listeners: {
                render:  {
                    fn: me.initDraggable
                }
            }
        });

        var general = Ext.create('Ext.Panel', {
                title: 'General',
                items: generalItem
            });
        var reports = Ext.create('Ext.Panel', {
                title: 'Reports',
                items: reportsItem
            });
        var system = Ext.create('Ext.Panel', {
                title: 'System',
                items: systemItem
            });

        this.items = [general, reports, system];

        this.callParent(arguments);
    },

    initDraggable: function()
    {
        this.dd = Ext.create('CMS.view.dashboard.DragSource', this )
    },

    getGeneralData: function() {
        return [
            ["CMS.view.dashboard.Dashlet", "", "Assigned to Me", 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh.'],
            ["CMS.view.dashboard.GridDashlet", "", "Content Search"]
        ];
    },
    getReportsData: function() {
        return [
            ["CMS.view.dashboard.GridDashlet", "", "Popular content"],
            ["CMS.view.dashboard.ChartDashlet", "", "Form Responses"]
        ];
    },
    getSystemData: function() {
        return [
            ["CMS.view.dashboard.Dashlet", "", "Live Traffic Map", 'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur? At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio.'],
            ["CMS.view.dashboard.ChartDashlet", "", "System health"]
        ];
    }

});