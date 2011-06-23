Ext.define('CMS.store.UserStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.UserModel',

    pageSize: 10,
    autoLoad: true,
    remoteSort: true,
    buffered: true,

    proxy: {
        type: 'direct',
        directFn: CMS.rpc.accountAction.getUsers,
        reader : {
	        root : 'records',
		    totalProperty : 'total'
		}
    }
});
