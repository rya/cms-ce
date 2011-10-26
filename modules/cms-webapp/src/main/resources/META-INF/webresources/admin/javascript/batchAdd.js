/**
 * Helper functions for batch add.
 */

// Namespace
if ( !cms ) var cms = {};
if ( !cms.util ) cms.util = {};
if ( !cms.util.BatchAdd ) cms.util.BatchAdd = {};

cms.util.BatchAdd = {

    addButtonName: 'batch_add_button',
    checkBoxName: 'batch_add_checkbox',
    multipleCheckedCheckboxesIsAllowed: true,

    // -------------------------------------------------------------------------------------------------------------------------------------

    toggleCheckBox: function( key )
    {
        var checkbox = document.getElementById('checkbox_' + key);
        if (!checkbox) return;

        checkbox.checked = checkbox.checked !== true;
        this.enableDisableAddButton();
    },

    // -------------------------------------------------------------------------------------------------------------------------------------

    enableDisableAddButton: function()
    {
        var checkedCheckBoxes = cms.util.BatchAdd.countCheckedCheckBoxes();

        var buttons = document.getElementsByName(cms.util.BatchAdd.addButtonName);
        var buttonsLn = buttons.length;
        var multipleCheckedCheckboxesIsAllowed = cms.util.BatchAdd.multipleCheckedCheckboxesIsAllowed;
        var shimElement;

        for ( var i = 0; i < buttonsLn; i++ )
        {
            shimElement = document.getElementsByName('batchButtonShim')[i];

            if ( multipleCheckedCheckboxesIsAllowed )
            {
                if ( checkedCheckBoxes <= 0 )
                {
                    buttons[i].disabled = true;
                    cms.util.BatchAdd.addToolTip('%msgNoMembersSelected%');
                }
                else
                {
                    buttons[i].disabled = false;
                }
            }
            else
            {
                buttons[i].disabled = checkedCheckBoxes != 1;

                var toolTipText = ( checkedCheckBoxes > 1 ) ? '%msgOneMemberOnly%' : '%msgNoMembersSelected%';
                
                cms.util.BatchAdd.updateTooltipText(toolTipText);
            }

            buttons[i].style.cursor = buttons[i].disabled ? 'default' : 'pointer';
            shimElement.style.display = buttons[i].disabled ? 'block' : 'none';
        }
    },

    // -------------------------------------------------------------------------------------------------------------------------------------

    countCheckedCheckBoxes: function()
    {
        var counter = 0;
        var checkboxes = document.getElementsByName(this.checkBoxName);
        var checkboxesLn = checkboxes.length;

        for ( var i = 0; i < checkboxesLn; i++ )
        {
            var checkbox = checkboxes[i];
            if ( checkbox.checked )
            {
                counter++;
            }
        }
        
        return counter;
    },

    // -------------------------------------------------------------------------------------------------------------------------------------

    batchAdd: function( fn )
    {
        var checkboxes = document.getElementsByName(this.checkBoxName);
        var checkboxesLn = checkboxes.length;

        for ( var i = 0; i < checkboxesLn; i++ )
        {
            var checkbox = checkboxes[i];
            if ( checkbox.checked )
            {
                fn(checkbox.value);
            }
        }

        if ( parent ) // The doument is in a frameset.
            parent.close();
        else
            window.close();
        
    },

    // -------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Since disabled elements does not receive mouse input we need to create an element
     * stacked above the button so we candisplay a tooltip when the element is disabled.  
     */

    addToolTip: function( text )
    {
        var buttons = document.getElementsByName(cms.util.BatchAdd.addButtonName);
        var buttonsLn = buttons.length;

        if ( buttonsLn == 0 ) return;

        for ( var i = 0; i < buttonsLn; i++ )
        {
            var button = buttons[i];
            cms.util.BatchAdd.createButtonOverlay(i, button, text);
        }
    },

    // -------------------------------------------------------------------------------------------------------------------------------------

    updateTooltipText: function( text )
    {
        var shimElements = document.getElementsByName('batchButtonShim');

        for ( var i = 0; i < shimElements.length; i++ )
        {
            shimElements[0].title = text;
        }
    },

    // -------------------------------------------------------------------------------------------------------------------------------------

    createButtonOverlay: function( i, button, title )
    {
        var shim = document.getElementsByName('batchButtonShim')[i];

        if ( shim ) return;

        // See createNamedElement defined in admin.js 
        shim = createNamedElement( document, 'img', 'batchButtonShim');
        shim.src = './images/shim.gif';
        shim.title = title;
        shim.style.position = 'absolute';
        shim.style.top = findPosY(button) + 'px';
        shim.style.left = findPosX(button) + 'px';
        shim.style.width = button.offsetWidth;
        shim.style.height = button.offsetHeight;

        document.getElementsByTagName('body')[0].appendChild(shim);
    }

    // -------------------------------------------------------------------------------------------------------------------------------------
};
