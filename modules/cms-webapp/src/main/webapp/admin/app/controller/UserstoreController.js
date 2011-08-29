Ext.define( 'CMS.controller.UserstoreController', {
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
        'userstore.ContextMenu',
        'userstore.GridPanel',
        'userstore.DetailPanel',
        'userstore.UserstoreFormPanel',
        'userstore.SyncWindow'
    ],

    refs: [
        {ref: 'tabPanel', selector: 'cmsTabPanel'},
        {ref: 'userstoreDetail', selector: 'userstoreDetail'},
        {ref: 'userstoreGrid', selector: 'userstoreGrid'},
        {ref: 'userstoreShow', selector: 'userstoreShow'},
        {ref: 'userstoreForm', selector: 'userstoreForm'},
        {ref: 'userstoreFormPanel', selector: 'userstoreFormPanel'},
        {ref: 'userstoreContextMenu', selector: 'userstoreContextMenu', autoCreate: true, xtype: 'userstoreContextMenu'}
    ],

    init: function()
    {
        this.control( {
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
                              click: this.syncUserstore
                          },
                          '*[action=stopSyncUserstore]': {
                              click: this.stopSyncUserstore
                          },
                          '*[action=saveUserstore]': {
                              click: this.saveUserstore
                          },
                          '*[action=cancelUserstore]': {
                              click: this.closeUserstoreTab
                          },
                          'userstoreGrid': {
                              selectionchange: this.updateDetailsPanel,
                              itemcontextmenu: this.popupMenu
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
                              tabchange: this.handleUserstoreTabchange
                          },
                          'userstoreFormPanel': {
                              show: {
                                  fn: this.checkUserstoreSync,
                                  delay: 50
                              }
                          }
                      } );
    },

    syncUserstore: function( btn, evt, opts )
    {
        Ext.Msg.confirm( "Warning", "Synchronizing may take some time. Do you want to proceed ?", function( answer ) {
            if ( "yes" == answer ) {
                var tabs = this.getTabs();
                var tab = btn.up('userstoreFormPanel');
                var detail = btn.up('userstoreDetail');
                var userstore = tab ? tab.userstore : detail.getCurrentUserstore();

                var task = {
                    run: function() {
                        //TODO get real data
                        var rand = Math.random();
                        var data = {
                            key: userstore.data.key,
                            step: [ 1, 3, 'Users' ],
                            progress: rand,
                            count: [ Math.round( rand * 146 ), 146]
                        };
                        if ( this.tab && this.tab.syncWindow )
                            this.tab.syncWindow.updateData( data );
                        if ( this.detail ) {
                            this.detail.updateSync( data );
                        }
                    },
                    interval: 1000,
                    tab: tab,
                    detail: detail
                };
                Ext.TaskManager.start( task );

                var sync = {
                    key: userstore.data.key,
                    userstore: userstore,
                    task: task
                };

                if ( !tabs.userstoreSync ) tabs.userstoreSync = [];
                tabs.userstoreSync.push( sync );

                if ( tab ) {
                    this.updateSyncWindow( tab, sync );
                } else if (detail) {
                    this.updateDetailsView( detail );
                }
            }
        }, this);
    },

    updateSyncWindow: function( tab, sync )
    {
        if ( sync ) {
            if ( !tab.syncWindow ) {
                tab.syncWindow = Ext.createByAlias( 'widget.userstoreSyncWindow', {
                    renderTo: tab.el.dom,
                    parentTab: tab,
                    closable: false
                } );
            }
            if ( sync.task ) sync.task.tab = tab;
            if ( !tab.el.isMasked() )tab.el.mask();
            if ( !tab.syncWindow.isVisible() ) tab.syncWindow.show();
        } else if ( tab.syncWindow ) {
            if ( tab.el.isMasked() ) tab.el.unmask();
            if ( tab.syncWindow.isVisible() ) tab.syncWindow.close();
        }
    },

    stopSyncUserstore: function( btn, evt, opts )
    {
        var win = btn.up( 'window' );
        var detail = btn.up('userstoreDetail');
        var tabs = this.getTabs();
        var userstore = win ? win.parentTab.userstore : detail.getCurrentUserstore();

        Ext.each( tabs.userstoreSync, function( sync ) {
            if ( sync.key == userstore.data.key ) {
                Ext.TaskManager.stop( sync.task );
                Ext.Array.remove( tabs.userstoreSync, sync );
                return false;
            }
        });

        if ( win ) {
            this.updateSyncWindow( win.parentTab, false );
        } else if ( detail ) {
            this.updateDetailsView( detail );
        }
    },

    checkUserstoreSync: function( tab )
    {
        var tabs = this.getTabs();
        var s;
        Ext.each( tabs.userstoreSync, function( sync ) {
            if ( sync.key == tab.userstore.data.key ) {
                s = sync;
                return false;
            }
        });
        this.updateSyncWindow( tab, s );
    },

    handleDefaultChange: function( field, newValue, oldValue, options )
    {
        if ( newValue )
        {
            Ext.Msg.confirm( "Important", "Do you really want to set this userstore default ?", function( button )
            {
                if ( "no" == button )
                {
                    field.setValue( oldValue );
                }
            } );
        }
    },

    handleConnectorChange: function( field, newValue, oldValue, options )
    {
        var form = field.up( 'userstoreForm' );
        var newVals = form.userstore || { data: {} };
        var record = field.store.findRecord( field.valueField, newValue );
        if ( record )
        {
            newVals.data[ field.name ] = record.data[ field.displayField ];
            form.updateUserstoreHeader( newVals );
        }
    },

    handleUserstoreChange: function( field, evt, opts )
    {
        var form = field.up( 'userstoreForm' );
        var newVals = form.userstore || { data: {} };
        newVals.data[field.name] = field.getValue();
        form.updateUserstoreHeader( newVals );
        var tab = field.up( 'userstoreFormPanel' );
        tab.setTitle( newVals.data.name );
    },

    createUserstoreTab: function( forceNew )
    {
        var tabs = this.getTabs();
        if ( tabs )
        {
            var selection = this.getUserstoreGrid().getSelectionModel().getSelection();
            var userstore;

            if ( !forceNew && selection && selection.length > 0 ) {
                userstore = selection[0];
                var showPanel = this.getUserstoreShow();

                showPanel.el.mask( "Loading..." );
                Ext.Ajax.request( {
                    url: 'data/userstore/config',
                    method: 'GET',
                    params: {
                        name: userstore.data.name
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
                            id: 'tab-userstore-' + userstore.data.key,
                            title: userstore.data.name,
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

    closeUserstoreTab: function( btn, evt, opts )
    {
        var tabs = this.getTabs();
        if ( tabs )
        {
            var tab = btn.up( 'userstoreFormPanel' );
            tabs.remove( tab, true );
        }
    },

    saveUserstore: function()
    {
        var form = this.getUserstoreForm().getForm();
        var tabs = this.getTabs();
        if ( form.isValid() )
        {
            form.submit( {
                 success: function( form, action )
                 {
                     Ext.Msg.alert( 'Success', action.result.msg  || 'Userstore has been saved.' );
                     tabs.userstoreDirty = true;
                 },
                 failure: function( form, action )
                 {
                     Ext.Msg.alert( 'Error',
                                    action.result.msg  || 'Userstore has not been saved.' );
                 }
             } );
        }
    },

    handleUserstoreTabchange: function ( tabs, newCard, oldCard, opts) {
        if ( 'tab-browse' == newCard.getId() ) {
            var iframe = this.getIframe();
            if ( iframe ) {
                var grid = iframe.Ext.getCmp( 'userstoreGridID' );
                if (grid)
                    this.updateGridView( grid, tabs );
                var detail = iframe.Ext.getCmp( 'userstoreDetailID' );
                if (detail)
                    this.updateDetailsView( detail );
            }
        }
    },

    updateGridView: function( grid, tabs )
    {
        if ( tabs.userstoreDirty ) {
            grid.getStore().load({
                callback: function(records, operation, success) {
                    tabs.userstoreDirty = false;
                }
            });
        }
    },

    notImplementedAction: function ( btn, evt, opts )
    {
        Ext.Msg.alert( "Not implemented", btn.action + " is not implemented yet." );
    },

    updateDetailsView: function( detail ) {
        var tabs = this.getTabs();
        var isSyncing = false;
        Ext.each( tabs.userstoreSync, function( sync ) {
            if ( sync.key == detail.currentUserstore.data.key ) {
                isSyncing = true;
                if ( sync.task ) sync.task.detail = detail;
                return false;
            }
        });
        detail.setActiveView( isSyncing ? 'sync' : 'default' );
    },


    updateDetailsPanel: function( selModel, selected )
    {
        var userstore = selected[0];
        var userstoreDetail = this.getUserstoreDetail();

        if ( userstore ) {
            userstoreDetail.updateData( userstore );
            this.updateDetailsView( userstoreDetail );
        }

        userstoreDetail.setTitle( selected.length + " userstore selected" );
    },

    popupMenu: function( view, rec, node, index, e )
    {
        e.stopEvent();
        this.getUserstoreContextMenu().showAt( e.getXY() );
        return false;
    },


    getTabs: function() {
        // returns tabs if executed in the system scope
        var tabs = this.getTabPanel();
        // returns tabs if executed inside the iframe of the system app
        if ( tabs == null && window.parent )
        {
            tabs = window.parent.Ext.getCmp( 'systemTabPanelID' );
        }
        return tabs;
    },

    getIframe: function() {
        var el = Ext.get('system-iframe');
        return el ? el.dom.contentWindow : null;
    }

} );
