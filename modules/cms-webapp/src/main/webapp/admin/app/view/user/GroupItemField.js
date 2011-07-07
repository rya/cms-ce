Ext.define( 'CMS.view.user.GroupItemField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.groupItemField',

    layout: 'hbox',

    width: 300,

    initComponent: function()
    {

        this.items = [
            {
                xtype: 'hidden',
                name: 'membership',
                value: this.groupId
            },
            {
                xtype: 'label',
                text: this.title,
                cls: 'group-item'
            },
            {
                xtype: 'button',
                iconCls: 'icon-delete',
                action: 'deleteGroup'
            }];
        this.callParent( arguments );
    }

} )