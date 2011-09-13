Ext.define( 'CMS.view.user.AddressPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.addressPanel',

    xtype: 'form',
    draggable: true,
    layout: 'anchor',
    bodyPadding: 10,
    defaults: {
        anchor: '100%'
    },

    padding: 10,

    initComponent: function() {
        if (this.values == null){
            this.values = [];
        }
        this.title = this.values['label'] == null ? '[no title]' : this.values['label'];
        var countryField, regionField;
        if ( this.iso )
        {
            var countryStore = Ext.data.StoreManager.lookup( 'CountryStore' );
            var regionStore = new CMS.store.RegionStore();
            countryField = {
                xtype: 'combobox',
                store: countryStore,
                fieldLabel: 'Country',
                valueField: 'code',
                displayField: 'englishName',
                queryMode: 'local',
                minChars: 1,
                emptyText: 'Please select',
                name: 'iso-country',
                itemId: 'iso-country',
                value: this.values['iso-country'],
                disabled: this.readonly
            };
            regionField = new Ext.form.field.ComboBox({
                xtype: 'combobox',
                store: regionStore,
                valueField: 'code',
                displayField: 'englishName',
                queryMode: 'local',
                minChars: 1,
                emptyText: 'Please select',
                fieldLabel: 'Region',
                name: 'iso-region',
                itemId: 'iso-region',
                value: this.values['iso-region'],
                disabled: this.values['iso-region'] == null ? true : false
            });
            if (this.values['iso-country'] && this.values['iso-region']){
                Ext.apply( regionStore.proxy.extraParams, {
                    'countryCode': this.values['iso-country']
                } );
                regionStore.load({
                    callback: function(){
                        regionField.setValue(this.values['iso-region']);
                    }
                });
            }
        }
        else
        {
            countryField = {
                xtype: 'textfield',
                fieldLabel: 'Country',
                name: 'country',
                itemId: 'address-country',
                value: this.values['country'],
                disabled: this.readonly
            };
            regionField = {
                xtype: 'textfield',
                fieldLabel: 'Region',
                name: 'region',
                itemId: 'address-region',
                value: this.values['region'],
                disabled: this.readonly
            };
        }
        this.items = [
                {
                    xtype: 'textfield',
                    fieldLabel: 'Label',
                    name: 'label',
                    itemId: 'address-label',
                    enableKeyEvents: true,
                    value: this.values['label'],
                    bubbleEvents: ['keyup'],
                    disabled: this.readonly
                },
                {
                    xtype: 'textfield',
                    fieldLabel: 'Street',
                    name: 'street',
                    itemId: 'address-street',
                    value: this.values['street'],
                    disabled: this.readonly
                },
                {
                    xtype: 'textfield',
                    fieldLabel: 'Postal Code',
                    name: 'postal-code',
                    itemId: 'address-postal-code',
                    value: this.values['postal-code'],
                    disabled: this.readonly
                },
                {
                    xtype: 'textfield',
                    fieldLabel: 'Postal Address',
                    name: 'postal-address',
                    itemId: 'address-postal-address',
                    value: this.values['postal-address'],
                    disabled: this.readonly
                },
                countryField,
                regionField
            ];
        this.callParent( arguments );
        this.initDrag();
    },

    initDrag: function(){
        var overrides = {
            // Only called when element is dragged over the a dropzone with the same ddgroup
            onDragEnter : function(evtObj, targetElId) {
                // Colorize the drag target if the drag node's parent is not the same as the drop target
                if (targetElId != this.el.dom.parentNode.id) {
                    this.el.addClass('dropOK');
                }
                else {
                    // Remove the invitation
                    this.onDragOut();
                }
            },
            // Only called when element is dragged out of a dropzone with the same ddgroup
            onDragOut : function(evtObj, targetElId) {
                this.el.removeClass('dropOK');
            },
            //Called when mousedown for a specific amount of time
            b4StartDrag : function() {
                if (!this.el) {
                    this.el = Ext.get(this.getEl());
                }
                //this.el.highlight();
                //Cache the original XY Coordinates of the element, we'll use this later.
                this.originalXY = this.el.getXY();
            },
            // Called when element is dropped not anything other than a
            // dropzone with the same ddgroup
            onInvalidDrop : function() {
                this.invalidDrop = true;

            },
            endDrag : function() {
                if (this.invalidDrop === true) {
                    this.el.removeClass('dropOK');

                    var animCfgObj = {
                        easing   : 'elasticOut',
                        duration : 1,
                        scope    : this,
                        callback : function() {
                            this.el.dom.style.position = '';
                        }
                    };
                    this.el.moveTo(this.originalXY[0], this.originalXY[1], animCfgObj);
                    delete this.invalidDrop;
                }

            },
            // Called upon successful drop of an element on a DDTarget with the same
            onDragDrop : function(evtObj, targetElId) {
                // Wrap the drop target element with Ext.Element
                var dropEl = Ext.get(targetElId);

                // Perform the node move only if the drag element's parent is not the same as the drop target
                if (this.el.dom.parentNode.id != targetElId) {

                    // Move the element
                    dropEl.appendChild(this.el);

                    // Remove the drag invitation
                    this.onDragOut(evtObj, targetElId);

                    // Clear the styles
                    this.el.dom.style.position ='';
                    this.el.dom.style.top = '';
                    this.el.dom.style.left = '';
                }
                else {
                    // This was an invalid drop, lets call onInvalidDrop to initiate a repair
                    this.onInvalidDrop();
                }
            }
        };

        var dd = new Ext.dd.DD(this, 'address', {
            isTarget  : false
        });
        Ext.apply(dd, overrides);
    }

})
