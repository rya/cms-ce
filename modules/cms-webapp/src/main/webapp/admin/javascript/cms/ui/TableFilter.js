TableFilter = {
    inputElement : null
    ,table : null
    ,tableRows : null
    ,cache : []
    ,columnNumberToSeachIn : 0

    ,createCache : function()
    {
        var t = this, doc = document;
        var tr, columnToIndex, dataToStore;
        var i;
        for ( i = 0; i < t.tableRows.length; i++ )
        {
            if ( i === 0 ) continue;
            
            tr =  t.tableRows[i];
            columnToIndex = tr.getElementsByTagName( 'td' )[ t.columnNumberToSeachIn ];
            dataToStore = columnToIndex.innerHTML.replace(/^\s+|\s+$/g,"");
            t.cache.push( [ i, dataToStore ] );
        }
    }

    ,filterTable : function( valueToFind )
    {
        var t = this;
        var data, rowIdx, td;
        var row;

        if ( valueToFind === '' )
        {
            t.cleanAllTableCells();
            t.displayAllRows();
            return;
        }

        var i;
        for ( i = 0; i < t.cache.length; i++ )
        {
            rowIdx = t.cache[i][0];
            data = t.cache[i][1];
            row = t.tableRows[rowIdx];

            if ( data.indexOf( valueToFind ) > -1 )
            {
                td = row.getElementsByTagName('td')[ t.columnNumberToSeachIn ];
                t.doHighlight( td, data, valueToFind );
                row.style.display = '';
            }
            else
            {
                row.style.display = 'none';
            }
        }
    }

    ,doHighlight : function( td, tdContent, textToHighlight )
    {
        var t = this;
        var pattern = new RegExp('(' + textToHighlight +')', 'gi');
          
        t.cleanTableCell( td );
        td.innerHTML = tdContent.replace( pattern, '<span class="table-filter-highlight">$1</span>' );
    }

    ,displayAllRows : function( td )
    {
        var t = this;
        var i;
        for ( i = 0; i < t.tableRows.length; i++ )
        {
            t.tableRows[i].style.display = '';
        }
    }

    ,cleanTableCell : function( td )
    {
        td.innerHTML = td.innerHTML.replace(/<span class="table-filter-highlight">/gi, '');
        td.innerHTML = td.innerHTML.replace(/<\/span>/gi, '');
    }

     ,cleanAllTableCells : function()
    {
        var t = this, td;

        var i;
        for ( i = 0; i < t.tableRows.length; i++ )
        {
            if ( i == 0 ) continue;

            td = t.tableRows[i].getElementsByTagName('td')[ t.columnNumberToSeachIn ];
            t.cleanTableCell( td );
        }
    }

    ,addKeyUpEvent : function()
    {
        var t = this;

        t.inputElement.onkeyup = function( e )
        {
            t.filterTable( this.value );
        }
    }

    ,init : function( inputElementId, tableId, columnNumberToSeachIn )
    {
        var t = this, doc = document;

        t.inputElement = doc.getElementById( inputElementId );
        t.table = doc.getElementById( tableId );
        if ( !t.table ) return;
        t.tableRows = t.table.getElementsByTagName( 'tr' );
        t.columnNumberToSeachIn = columnNumberToSeachIn;
        
        t.createCache();
        t.addKeyUpEvent();
    }
};