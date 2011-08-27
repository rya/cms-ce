Ext.define( 'CMS.controller.MainPanelController', {
    extend: 'Ext.app.Controller',

    stores: [
        'UserstoreConfigStore',
        'UserstoreConnectorStore'
    ],
    models: [
        'UserstoreConfigModel',
        'UserstoreConnectorModel'
    ],

    views: [
        'Shared.CmsTabPanel',
        'MainPanel'
    ],

    refs: [
        {ref: 'cmsTabPanel', selector: 'cmsTabPanel'},
        {ref: 'mainPanel', selector: 'mainPanel'}
    ],

    init: function()
    {
        // Add events to application scope.
        this.application.on({
            createUserstoreTab: {
                fn: this.createUserstoreTab,
                scope: this
            },
            newUserstore: {
                fn: this.createUserstoreTab,
                scope: this

            },
            editUserstore: {
                fn: this.createUserstoreTab,
                scope: this
            },
            closeUserstoreTab: {
                fn: this.closeUserstoreTab,
                scope: this
            }
        });

        this.control( {
            'viewport': {
                afterrender: this.addBrowseTab
            }
            /*
            '*[action=newUserstore]': {
                click: function() {
                    this.createUserstoreTab( true );
                }
            },
            '*[action=editUserstore]': {
                click: function() {
                    this.createUserstoreTab( false );
                }
            },
            '*[action=deleteUserstore]': {
                click: this.notImplementedAction
            },
            '*[action=syncUserstore]': {
                click: this.notImplementedAction
            },
            '*[action=saveUserstore]': {
                click: this.saveUserstore
            },
            '*[action=cancelUserstore]': {
                click: this.closeUserstoreTab
            },
            'userstoreForm #defaultCheckbox': {
                change: this.handleDefaultChange
            },
            'userstoreForm textfield[name=name]': {
                keyup: this.handleUserstoreChange
            },
            'userstoreForm combobox[name=connectorName]': {
                change: this.handleConnectorChange
            },
            'cmsTabPanel': {
                tabchange: this.checkUserstoreDirty
            }
            */
        } );
    },

    addBrowseTab: function( component, options )
    {
        this.getCmsTabPanel().addTab( {
           id: 'tab-browse',
           title: 'Browse',
           closable: false,
           xtype: 'panel',
           layout: 'border',
           items: [
               {
                   region: 'center',
                   xtype: 'mainPanel'
               }
           ]
        } );
    },

    createUserstoreTab: function( userstore, forceNew )
    {
        var tabs = this.getTabs();
        if ( tabs )
        {
            if ( !forceNew && userstore ) {

                var showPanel = this.getMainPanel();

                showPanel.el.mask( "Loading..." );

                Ext.Ajax.request( {
                    url: '/admin/data/userstore/config',
                    method: 'GET',
                    params: {
                        name: userstore.name
                    },
                    success: function( response )
                    {
                        var obj = Ext.decode( response.responseText, true );
                        // add missing fields for now
                        Ext.apply( obj, {
                            userCount: 231,
                            userPolicy: 'User Policy',
                            groupCount: 12,
                            groupPolicy: 'Group Policy',
                            lastModified: '2001-07-04 12:08:56',
                            plugin: 'Plugin Name'
                        });
                        showPanel.el.unmask();
                        tabs.addTab( {
                            xtype: 'userstoreFormPanel',
                            id: 'tab-userstore-' + userstore.key,
                            title: userstore.name,
                            userstore: {
                                data: obj
                            }
                        } );
                    }
                } );
            } else {
                tabs.addTab( {
                    xtype: 'userstoreFormPanel'
                } );
            }
        }
    },

    closeUserstoreTab: function( button, e, eOpts )
    {
        var tabs = this.getTabs();
        if ( tabs )
        {
            var tab = button.up( 'userstoreFormPanel' );
            tabs.remove( tab, true );
        }
    },


    getTabs: function() {
        // returns tabs if executed in the system scope
        var tabs = this.getCmsTabPanel();
        // returns tabs if executed inside the iframe of the system app
        if ( tabs == null && window.parent )
        {
            tabs = window.parent.Ext.getCmp( 'systemTabPanelID' );
        }
        return tabs;
    }

} );
