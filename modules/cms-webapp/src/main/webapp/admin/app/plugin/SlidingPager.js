/*

This file is part of Ext JS 4

Copyright (c) 2011 Sencha Inc

Contact:  http://www.sencha.com/contact

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.

If you are unsure which license is appropriate for your use, please contact the sales department at http://www.sencha.com/contact.

*/
/**
* @class Ext.ux.SlidingPager
* @extends Object
* Plugin for PagingToolbar which replaces the textfield input with a slider
* @constructor
* Create a new ItemSelector
* @param {Object} config Configuration options
*/
Ext.define('CMS.plugin.SlidingPager', {
    extend: 'Object',
    alias : 'plugin.slidingPager',
    requires: [
        'Ext.slider.Single',
        'Ext.slider.Tip'
    ],

    constructor : function(config) {
        if (config) {
            Ext.apply(this, config);
        }
    },

    init : function(pbar){
        var indexOfPagingNumberField = pbar.items.indexOf(pbar.child("#inputItem")),
            slider;

        Ext.each(pbar.items.getRange(indexOfPagingNumberField - 2, indexOfPagingNumberField + 2), function(c){
            c.hide();
        });

        slider = Ext.create('Ext.slider.Single', {
            width: 114,
            minValue: 1,
            maxValue: 1,
            hideLabel: true,
            tipText: function(thumb) {
                return Ext.String.format('Page <b>{0}</b> of <b>{1}</b>', thumb.value, thumb.slider.maxValue);
            },
            listeners: {
                changecomplete: function(s, v){
                    pbar.store.loadPage(v);
                }
            }
        });

        var total = Ext.create('Ext.toolbar.TextItem', {
            cls: 'sliding-pager-text'
        });

        var tbFill = Ext.create('Ext.toolbar.Fill', {});

        var displayInfo = Ext.create('Ext.toolbar.TextItem', {
            cls: 'sliding-pager-text'
        });

        function getIndexOfPBarFirstButton() {
            return pbar.items.indexOf(pbar.child("#first"));
        }

        pbar.insert(getIndexOfPBarFirstButton(), total);
        pbar.insert(getIndexOfPBarFirstButton(), tbFill);
        pbar.insert(getIndexOfPBarFirstButton(), displayInfo);
        pbar.insert(getIndexOfPBarFirstButton() + 2, slider);

        pbar.on({
            change: function(paging, pageData) {
                // TODO: Configuration for internationalization.
                if (pageData.total) {
                    total.setText(pageData.total + ' Total');
                    displayInfo.setText('Displaying ' + pageData.fromRecord + '-' + pageData.toRecord);
                }
                slider.setMaxValue(pageData.pageCount);
                slider.setValue(pageData.currentPage);
            }

        });
    }
});
