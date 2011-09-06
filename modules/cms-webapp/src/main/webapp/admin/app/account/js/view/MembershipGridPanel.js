Ext.define( 'App.view.MembershipGridPanel', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.membershipGridPanel',

    layout: 'fit',
    loadMask: true,
    columnLines: true,
    frame: false,
    store: 'GroupStore',

    initComponent: function()
    {
        this.selModel = new Ext.selection.CheckboxModel();
        this.columns = [
            {
                text: 'Name',
                dataIndex: 'name',
                flex: 1
            },
            {
                text: 'Userstore',
                dataIndex: 'userStore'
            },
            {
                text: 'Member Count',
                dataIndex: 'memberCount'
            },
            {
                text: 'Restricted Enrollment',
                dataIndex: 'restrictedEnrollment'
            }
        ];

        this.callParent( arguments );
    }

} );