Ext.define( 'App.view.wizard.UserWizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userWizardPanel',
    requires: [
        'Common.WizardPanel',
        'App.view.wizard.UserStoreListPanel',
        'App.view.wizard.UserWizardToolbar',
        'App.view.EditUserFormPanel',
        'App.view.wizard.WizardStepUserPanel',
        'App.view.wizard.WizardStepMembershipPanel',
        'App.view.wizard.WizardStepFinalizePanel',
    ],

    layout: {
        type: 'hbox',
        align: 'stretch',
        padding: 10
    },

    defaults: {
        border: false
    },

    tbar: {
        xtype: 'userWizardToolbar'
    },

    toggleDisplayNameField: function(e, t){
        var className = t.attributes.getNamedItem('class').nodeValue;
        if (className == 'edit-button'){
            var displayNameField = Ext.get('display-name');
            var readonly = displayNameField.getAttribute('readonly');
            if (readonly){
                displayNameField.dom.removeAttribute('readonly');
            }else{
                displayNameField.set({readonly: true});
            }
            displayNameField.addCls('cms-edited-field');
        }
    },

    toggleEditButton: function(e, t){
        var editButton = Ext.get('edit-button');
        if (e.type == 'mouseover'){
            editButton.show();
        }
        if (e.type == 'mouseout'){
            editButton.hide();
        }
    },

    resizeFileUpload: function( file ) {
        file.el.down( 'input[type=file]' ).setStyle( {
            width: file.getWidth(),
            height: file.getHeight()
        } );
    },

    initComponent: function()
    {
        var me = this;

        var userImage = Ext.create( 'Ext.Img', {
            src: 'resources/images/x-user.png',
            width: 100,
            height: 100
        } );

        var uploadForm = Ext.create('Ext.form.Panel', {
            fileUpload: true,
            width: 100,
            height: 100,
            frame: false,
            border: false,
            style: {
                position: 'absolute',
                top: 0,
                left: 0,
                padding: 0,
                margin: 0,
                opacity: 0
            },
            items: [{
                xtype: 'filefield',
                name: 'photo',
                buttonOnly: true,
                hideLabel: true,
                width: 100,
                height: 100,
                buttonConfig: {
                    width: 100,
                    height: 100
                },
                listeners: {
                    afterrender: function( imgUpl ) {
                        me.resizeFileUpload( imgUpl );
                    },
                    change: function( imgUpl, path, eOpts ) {
                        var form = this.up('form').getForm();
                        if( form.isValid() )
                        {
                            form.submit( {
                                url: 'data/user/photo',
                                method: 'POST',
                                waitMsg: 'Uploading your photo...',
                                success: function( form, action ) {
                                    userImage.setSrc( action.result.src );
                                    me.resizeFileUpload( imgUpl );
                                },
                                failure: function(form, action) {
                                    Ext.Msg.show({
                                        title: 'Failure',
                                        msg: 'File was not uploaded.',
                                        minWidth: 200,
                                        modal: true,
                                        icon: Ext.Msg.INFO,
                                        buttons: Ext.Msg.OK
                                    });
                                }
                            } );
                        }
                    }
                }
            }]
        });


        me.items = [
            {
                width: 100,
                items: [
                    userImage,
                    uploadForm
                ]
            },
            {
                flex: 1,
                layout: {
                    type: 'vbox',
                    align: 'stretch',
                    padding: '0 10'
                },
                defaults: {
                    border: false
                },
                items: [
                    {
                        xtype: 'panel',
                        cls: 'cms-new-user-header',
                        styleHtmlContent: true,
                        listeners: {
                            click: {
                                element: 'body',
                                fn: me.toggleDisplayNameField
                            },
                            mouseover: {
                                element: 'body',
                                fn: me.toggleEditButton
                            },
                            mouseout: {
                                element: 'body',
                                fn: me.toggleEditButton
                            }
                        },
                        html: '<div class="cms-wizard-header clearfix">' +
                                '<div class="right">' +
                                '<h1><input id="display-name" type="text" value="New User" readonly="true" class="cms-display-name"/></h1><a href="javascript:;" id="edit-button" class="edit-button"></a>' +
                                '<p >-User Wizard: <span id="q-userstore"></span><span id="q-username"></span></p></div></div>'
                    },
                    {
                        flex: 1,
                        xtype: 'wizardPanel',
                        showControls: false,
                        items: [
                            {
                                stepTitle: 'Userstore',
                                xtype: 'userStoreListPanel'
                            },
                            {
                                stepTitle: "Profile",
                                itemId: 'userForm',
                                xtype: 'editUserFormPanel',
                                enableToolbar: false
                            },
                            {
                                stepTitle: "User",
                                xtype: 'wizardStepUserPanel'
                            },
                            {
                                stepTitle: "Memberships",
                                xtype: 'wizardStepMembershipPanel'
                            },
                            {
                                stepTitle: "Finalize",
                                xtype: 'wizardStepFinalizePanel'
                            }
                        ]
                    }
                ]
            }
        ];
        this.callParent( arguments );
    }

} );
