/**
 * PersistentGridSelectionPlugin
 * Based on joeri's RowSelectionPaging post,2009-02-26
 *
 * Only tested on grids using the CheckboxModel
 */
Ext.define('Common.PersistentGridSelectionPlugin', {

    extend: 'Ext.util.Observable',
    pluginId: 'persistentGridSelection',
    alias : 'plugin.persistentGridSelection',
    init: function(grid) {
        this.grid = grid;
        this.selections = [];
        this.selected = {};
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
            // TODO: Use getComponent or ComponentQuery instead of items[1]
            this.grid.dockedItems.items[1].on('beforechange', this.pageChange, this );
        }, this);
    },

    // private
    onViewRefresh: function() {
        this.ignoreSelectionChanges = true;
        // explicitly refresh the selection model
        this.grid.selModel.refresh();
        // selection changed from view updates, restore full selection
        var ds = this.grid.getStore();
        // TODO: Optimized.
        for (var i = ds.getCount() - 1; i >= 0; i--) {
            if (this.selected[ds.getAt(i).internalId]) {
                this.grid.selModel.select(i,true,false);
            }
        }
        this.ignoreSelectionChanges = false;
    },

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
    },

    // private
    onRowSelect: function(sm,rec,i,o) {
        if (! this.ignoreSelectionChanges) {
            if (!this.selected[rec.internalId])
            {
                this.selections.push(rec);
                this.selected[rec.internalId] = true;
            }

        }

    },

    onHeaderClick: function(headerCt, header, e) {
        if (header.isCheckerHd) {
            e.stopEvent();
            var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
            if (isChecked) {
                this.clearSelections();
            } else {
                this.grid.selModel.selectAll();
            }
        }

        return false;
    },

    // private
    onRowDeselect: function(rowModel,record,index,eOpts){
        if (!this.ignoreSelectionChanges) {
            if (this.selected[record.internalId]) {
                for (var j = this.selections.length - 1; j >= 0; j--) {
                    if (this.selections[j].internalId == record.internalId) {
                        this.selections.splice(j, 1);
                        this.selected[record.internalId] = false;
                        break;
                    }
                }
            }
        }
    },

    // private
    notifySelectionModelAboutSelectionChange: function()
    {
        this.grid.selModel.fireEvent("selectionchange", {});
    },

    /**
     * Clears selections across all pages
     */
    clearSelections: function() {
        this.selections = [];
        this.selected = {};
        this.grid.selModel.deselectAll();
        this.onViewRefresh();
        this.notifySelectionModelAboutSelectionChange();
    },

    /**
     * Returns the selected records for all pages
     * @return {Array} Array of selected records
     */
    getSelection: function() {
        return [].concat(this.selections);
    },

    /**
     * Returns the selection count for all pages
     * @return {Number} Number of selected records
     */
    getSelectionCount: function() {
        return this.getSelection().length;
    },

    /**
     * Removes an record from the selection
      * @param record
     */
    deselect: function(record)
    {
        this.onRowDeselect(this.grid.selModel, record);
        // If the deselected use is on the current page we need to programmatically deselect it.
        this.grid.selModel.deselect(record);
        this.notifySelectionModelAboutSelectionChange();
    },

    /**
     * Selects all the rows in the grid, including those on other pages
     * Be very careful using this on very large datasets
     */
    selectAll: function() {
        this.grid.selModel.selectAll();
    }

});
