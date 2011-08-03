Ext.define( 'CMS.controller.UserstoreController', {
    extend: 'Ext.app.Controller',

    stores: [
        'UserstoreConfigStore'
    ],
    models: [
        'UserstoreConfigModel'
    ],
    views: [
        'userstore.ContextMenu',
        'userstore.GridPanel',
        'userstore.DetailPanel',
        'userstore.UserstoreFormPanel'
    ],

    refs: [
        {ref: 'tabPanel', selector: 'cmsTabPanel'},
        {ref: 'userstoreDetail', selector: 'userstoreDetail'},
        {ref: 'userstoreGrid', selector: 'userstoreGrid'},
        {ref: 'userstoreShow', selector: 'userstoreShow'},
        {ref: 'userstoreForm', selector: 'userstoreForm'},
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
                              click: this.notImplementedAction
                          },
                          '*[action=saveUserstore]': {
                              click: this.notImplementedAction
                          },
                          '*[action=cancelUserstore]': {
                              click: this.notImplementedAction
                          },
                          'userstoreGrid': {
                              selectionchange: this.updateDetailsPanel,
                              itemcontextmenu: this.popupMenu
                          },
                          'userstoreForm textfield': {
                              keyup: this.handleUserstoreChange
                          },
                          'userstoreForm combobox': {
                              change: this.handleConnectorCahnge
                          }
                      } );
    },

    handleConnectorCahnge: function( field, newValue, oldValue, options ) {
        var form = field.up( 'userstoreForm' );
        var newVals = form.userstore || { data: {} };
        var record = field.store.findRecord( field.valueField, newValue );
        if ( record ) {
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

    createUserstoreTab: function( forceNew ) {

        // remember that userstores is executed inside
        // an iframe in system app which hosts tabs
        var parent = window.parent;
        if ( parent ) {
            var tabs = parent.Ext.getCmp('systemTabPanelID');
            if ( tabs ) {

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
                                plugin: 'Plugin Name',
                                connectorName: 'Connector name'
                            });
                            showPanel.el.unmask();
                            tabs.addTab( {
                                xtype: 'userstoreFormPanel',
                                id: 'tab-userstore-' + userstore.id,
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
        }
    },

    notImplementedAction: function ( btn, evt, opts ) {
        Ext.Msg.alert("Not implemented", btn.action + " is not implemented yet.");
    },

    updateDetailsPanel: function( selModel, selected )
    {
        var userstore = selected[0];
        var userstoreDetail = this.getUserstoreDetail();

        if ( userstore )
        {
            userstoreDetail.update( userstore.data );
            userstoreDetail.setCurrentUserstore( userstore.data );
        }

        userstoreDetail.setTitle( selected.length + " userstore selected" );
    },

    popupMenu: function( view, rec, node, index, e )
    {
        e.stopEvent();
        this.getUserstoreContextMenu().showAt( e.getXY() );
        return false;
    }

} );
