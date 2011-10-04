Ext.define('Common.PersistentGridSelectionPlugin', {

    extend: 'Ext.util.Observable',
    alias : 'plugin.persistentGridSelection',
    init: function(grid) {
        this.grid = grid;
        this.selections = []; // array of selected records
        this.selected = {}; // hash mapping record id to selected state
        this.ignoreSelectionChanges = '';
        grid.on('render', function() {
            // attach an interceptor for the selModel's onRefresh handler
            this.grid.view.un('refresh', this.grid.selModel.refresh, this.grid.selModel);
            this.grid.view.on('refresh', this.onViewRefresh, this );
            this.grid.view.headerCt.on('headerclick', this.onHeaderClick, this);
            // add a handler to detect when the user changes the selection
            this.grid.selModel.on('select', this.onRowSelect, this );
            this.grid.selModel.on('select', this.onRowSelect, this );
            this.grid.selModel.on('deselect', this.onRowDeselect, this);
            this.grid.dockedItems.items[1].on('beforechange', this.pageChange, this );             // not sure about this , looking for another way
        }, this);
    },

    // private
    onViewRefresh: function() {
        this.ignoreSelectionChanges = true;
        // explicitly refresh the selection model
        this.grid.selModel.refresh();
        // selection changed from view updates, restore full selection
        var ds = this.grid.getStore();
        for (var i = ds.getCount() - 1; i >= 0; i--) {
            if (this.selected[ds.getAt(i).internalId]) {
                this.grid.selModel.select(i,true,false);
            }
        }
        this.ignoreSelectionChanges = false;
    }, // end onViewRefresh

    pageChange: function() {
        this.ignoreSelectionChanges = true;
    },
    // private
    onSelectionClear: function() {
        if (! this.ignoreSelectionChanges) {
            // selection cleared by user
            // also called internally when the selection replaces the old selection
            this.selections = [];
            this.selected = {};
        }
    }, // end onSelectionClear

    // private
    onRowSelect: function(sm,rec,i,o) {
        if (! this.ignoreSelectionChanges) {
            if (!this.selected[rec.internalId])
            {
                this.selections.push(rec);
                this.selected[rec.internalId] = true;
            }

        }

    }, // end onRowSelect
    onHeaderClick: function(headerCt, header, e) {
        if (header.isCheckerHd) {
            e.stopEvent();
            var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
            if (isChecked) {
                // We have to supress the event or it will scrollTo the change
                this.clearSelections();
            } else {
                // We have to supress the event or it will scrollTo the change
                this.selectAll();
            }
        }
    },
    // private
    onRowDeselect: function(sm,rec,i,o){
        if (!this.ignoreSelectionChanges) {
            if (this.selected[rec.internalId]) {
                for (var i = this.selections.length - 1; i >= 0; i--) {
                    if (this.selections[i].internalId == rec.internalId) {
                        this.selections.splice(i, 1);
                        this.selected[rec.internalId] = false;
                        break;
                    }
                }
            }
        }
    }, // end onRowDeselect

    /**
     * Clears selections across all pages
     */
    clearSelections: function() {
        this.selections = [];
        this.selected = {};
        this.grid.selModel.deselectAll();
        this.onViewRefresh();
    }, // end clearSelections

    /**
     * Returns the selected records for all pages
     * @return {Array} Array of selected records
     */
    getSelection: function() {
        return [].concat(this.selections);
    }, // end getSelections

    /**
     * Selects all the rows in the grid, including those on other pages
     * Be very careful using this on very large datasets
     */
    selectAll: function() {
        /*
        var ds = this.grid.getStore();
        ds.suspendEvents();
        ds.load({               //problem is , when i load this store (ds),  the store of  grid take this new store and reload the data
            start: 0,
            limit: ds.getTotalCount() ,
            callback: function() {
                this.selections = ds.data.items.slice(0);
                this.selected = {};
                for (var i = this.selections.length - 1; i >= 0; i--) {
                    this.selected[this.selections[i].internalId] = true;
                };
                ds.resumeEvents();
                this.onViewRefresh();
            },
            scope: this
        });
        */
    }

});
