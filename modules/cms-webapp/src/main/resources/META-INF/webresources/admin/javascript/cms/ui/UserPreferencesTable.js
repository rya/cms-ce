UserPreferenceTable = {

    preferencesJson : []
    // -------------------------------------------------------------------------------------------------------------------------------------

    ,createTable : function( preferencesJson, idOfElementToAppendTableTo )
    {
        var t = this, doc = document, elementToAppendTo, table, tr;
        
        t.preferencesJson = preferencesJson;
        doc.getElementById( idOfElementToAppendTableTo ).innerHTML = '';

        elementToAppendTo = doc.getElementById( idOfElementToAppendTableTo );
        table = doc.createElement( 'table' );
        table.cellPadding = 2;
        tr = doc.createElement( 'tr' );
        table.id = 'preference-table';
        t.createTableHeader( table );
        elementToAppendTo.appendChild( table );

        var addGlobal = true;
        var addSite = true;
        var addPage = true;
        var addPortlet = true;
        var addWindow = true;

        if ( addGlobal ) t.addScope( 'GLOBAL' );
        if ( addSite ) t.addScope( 'SITE' );
        if ( addPage ) t.addScope( 'PAGE' );
        if ( addPortlet ) t.addScope( 'PORTLET' );
        if ( addWindow ) t.addScope( 'WINDOW' );
    }
    // -------------------------------------------------------------------------------------------------------------------------------------

    ,createTableHeader : function( table )
    {
        var th, doc = document;
        var thead = doc.createElement( 'thead' );
        var tr = doc.createElement( 'tr' );

        th = doc.createElement( 'th' );
        th.innerHTML = '&nbsp;';
        tr.appendChild( th );

        th = doc.createElement( 'th' );
        th.innerHTML = 'Global';
        tr.appendChild( th );

        th = doc.createElement( 'th' );
        th.innerHTML = 'Site';
        tr.appendChild( th );

        th = doc.createElement( 'th' );
        th.innerHTML = 'Page';
        tr.appendChild( th );

        th = doc.createElement( 'th' );
        th.innerHTML = 'Portlet';
        tr.appendChild( th );

        th = doc.createElement( 'th' );
        th.innerHTML = 'Window';
        tr.appendChild( th );

        th = doc.createElement( 'th' );
        th.innerHTML = 'Key';
        tr.appendChild( th );

        th = doc.createElement( 'th' );
        th.innerHTML = 'Value';
        tr.appendChild( th );

        thead.appendChild( tr );

        table.appendChild( thead );
    }
    // -------------------------------------------------------------------------------------------------------------------------------------

    ,addScope : function( scope )
    {
        var preferences = this.getPreferencesByScope( scope );
        if ( preferences.length  === 0 )
            return;

        var key, preference;
        for ( key in preferences )
        {
            preference = preferences[ key ];
            this.createRow( preference );
        }
    }
    // -------------------------------------------------------------------------------------------------------------------------------------

    ,createRow : function( preference )
    {
        var t = this, doc = document;

        var table = doc.getElementById( 'preference-table' );
        if ( !table )
        {
            alert( 'table not created' );
            return;
        }

        var tr, td1, td2, td3, td4, td5, td6, td7, td8, td9;
        /*
        tr = doc.createElement( 'tr' );
        td1 = doc.createElement( 'td' );
        td1.colSpan = '8';
        td1.innerHTML = preference.scope;

        tr.appendChild( td1 );
        table.appendChild( tr );
        */

        tr = doc.createElement( 'tr' );
        td2 = doc.createElement( 'td' );
        td3 = doc.createElement( 'td' );
        td4 = doc.createElement( 'td' );
        td5 = doc.createElement( 'td' );
        td6 = doc.createElement( 'td' );
        td7 = doc.createElement( 'td' );
        td8 = doc.createElement( 'td' );
        td9 = doc.createElement( 'td' );

        td2.style.width = '50px';
        td2.style.textAlign = 'center';
        td3.style.textAlign = 'center';

        td2.innerHTML = t.createScopeIconHtml( preference.scope );
        td3.innerHTML = preference.scope === 'GLOBAL' ? '<img src="images/icon_check_noborder.gif" />' : '';
        td4.innerHTML = preference.siteName;
        td5.innerHTML = preference.menuItemPath;
        td6.innerHTML = preference.scope === 'PORTLET' ? preference.portletName : '';
        td7.innerHTML = preference.scope === 'WINDOW' ? preference.portletName : '';
        td8.innerHTML = preference.key;
        //td9.innerHTML = $.base64.decode( preference.value );
        td9.innerHTML = $().crypt({method:"b64dec",source: preference.value });

        td2.vAlign = 'top';
        td3.vAlign = 'top';
        td4.vAlign = 'top';
        td5.vAlign = 'top';
        td6.vAlign = 'top';
        td7.vAlign = 'top';
        td8.vAlign = 'top';
        td9.vAlign = 'top';

        tr.appendChild( td2 );
        tr.appendChild( td3 );
        tr.appendChild( td4 );
        tr.appendChild( td5 );
        tr.appendChild( td6 );
        tr.appendChild( td7 );
        tr.appendChild( td8 );
        tr.appendChild( td9 );
        
        table.appendChild( tr );
    }
    // -------------------------------------------------------------------------------------------------------------------------------------

    ,createScopeIconHtml : function( scopeType )
    {
        var imgHtml = '<img src="images/';
        
        switch( scopeType )
        {
            case 'GLOBAL':
                imgHtml += 'icon_domain.png';
                break;
            case 'SITE':
                imgHtml += 'icon_site.gif';
                break;
            case 'PAGE':
                imgHtml += 'icon_menuitem_standard_show.gif';
                break;
            case 'PORTLET':
                imgHtml += 'icon_objects.gif';
                break;
            case 'WINDOW':
                imgHtml += 'icon_objects.gif';
                break;
        }

        imgHtml += '" alt="' + scopeType + '" title="' + scopeType + '"/>';
        
        return imgHtml;
    }
    // -------------------------------------------------------------------------------------------------------------------------------------

    ,getPreferencesByScope : function( scope )
    {
        var foundPreferences = [];
        var key, preference, scopeField;

        for ( key in this.preferencesJson )
        {
            preference = this.preferencesJson[ key ];

            scopeField = preference[ 'scope' ];

            if ( scopeField === scope )
            {
                foundPreferences.push( preference )
            }
        }

        return foundPreferences;
    }
    // -------------------------------------------------------------------------------------------------------------------------------------

    ,init : function( userKey )
    {
        var t = this;

        AjaxService.getUserPreferences( userKey, {
            callback : function( preferencesJson )
            {
                if ( preferencesJson.length > 0 )
                {
                    t.createTable( preferencesJson, 'preferences-table-container' );
                }
            }
        });
    }

};