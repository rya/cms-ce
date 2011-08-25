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
                              tabchange: this.checkUserstoreDirty
                          },
                          'userstoreFormPanel': {
                              afterrender: {
                                  fn: this.checkUserstoreSync,
                                  delay: 50
                              }
                          }
                      } );
    },

    syncUserstore: function( btn, evt, opts ) {
        Ext.Msg.confirm( "Warning", "Synchronizing may take some time. Do you want to proceed ?", function( answer ) {
            if ( "yes" == answer ) {
                var tab = btn.up('userstoreFormPanel');

                this.showSyncWindow( tab );

                var tabs = this.getTabs();
                if ( !tabs.userstoreSync ) tabs.userstoreSync = [];
                if ( !Ext.Array.contains( tabs.userstoreSync, tab.userstore.data.key ) )
                    tabs.userstoreSync.push( tab.userstore.data.key );
            }
        }, this);
    },

    showSyncWindow: function( tab ) {
        var detail;
        var iframe = this.getIframe();
        if ( iframe ) {
            detail = iframe.Ext.getCmp('userstoreDetailID');
        }
        if ( !tab.syncWindow ) {
            tab.syncWindow = Ext.createByAlias( 'widget.userstoreSyncWindow', {
                renderTo: tab.el.dom,
                parentTab: tab,
                closable: false
            } );
        }
        if ( !tab.syncWindow.syncTask ) {
            tab.syncWindow.syncTask = {
                run: function(){
                    //TODO get real data
                    var rand = Math.random();
                    var data = {
                        step: [ 1, 3, 'Users' ],
                        progress: rand,
                        count: [ Math.round( rand * 146 ), 146]
                    };
                    tab.syncWindow.updateData( data );
                    if ( detail ) {
                        detail.updateSync( data );
                    }
                },
                interval: 1000
            };
        }
        tab.el.mask();
        tab.syncWindow.show();
        Ext.TaskManager.start( tab.syncWindow.syncTask );
    },


    stopSyncUserstore: function( btn, evt, opts )
    {
        var win = btn.up( 'window' );
        Ext.TaskManager.stop( win.syncTask );
        win.close();

        var tab = win.parentTab;
        tab.el.unmask();

        var tabs = this.getTabs();
        Ext.Array.remove( tabs.userstoreSync, tab.userstore.data.key );
    },

    checkUserstoreSync: function( tab ) {
        var tabs = this.getTabs();
        if ( tabs.userstoreSync &&
                Ext.Array.contains( tabs.userstoreSync, tab.userstore.data.key ) ) {
            this.showSyncWindow( tab );
        }
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

    checkUserstoreDirty: function( tabPanel, newCard, oldCard, options )
    {
        if( newCard.id == 'tab-browse' ) {
            var iframe = this.getIframe();
            if ( iframe ) {
                if ( tabPanel.userstoreDirty ) {
                    var grid = iframe.Ext.getCmp('userstoreGridID');
                    if ( grid ) {
                        grid.getStore().load({
                            callback: function(records, operation, success) {
                                tabPanel.userstoreDirty = false;
                            }
                        });
                    }
                }
                var detail = iframe.Ext.getCmp('userstoreDetailID');
                if ( !Ext.isEmpty( tabPanel.userstoreSync ) ) {
                    detail.getLayout().setActiveItem(1);
                } else {
                    detail.getLayout().setActiveItem(0);
                }
            }
        }
    },

    notImplementedAction: function ( btn, evt, opts )
    {
        Ext.Msg.alert( "Not implemented", btn.action + " is not implemented yet." );
    },

    updateDetailsPanel: function( selModel, selected )
    {
        var userstore = selected[0];
        var userstoreDetail = this.getUserstoreDetail();

        if ( userstore )
        {
            userstoreDetail.updateData( userstore );
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
