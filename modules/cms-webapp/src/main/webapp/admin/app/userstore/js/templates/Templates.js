if ( !Templates )
{
    var Templates = {};
}

Templates.userstore = {
    detailPanelInfo:
        '<div class="detail-info">' +
            '<h3>{name}</h3>' +
            '<dl>' +
                '<dt>Key</dt><dd>{key}</dd>' +
                '<dt>Default Store</dt><dd>{defaultStore}</dd>' +
                '<dt>Connector Name</dt><dd>{connectorName}</dd>' +
            '</dl>' +
        '</div>'

    ,gridPanelNameRenderer:
        '<div class="cms-grid-title">{0}</div><div class="cms-grid-description">{1}</div>'

    ,editFormHeader:
        '<div class="cms-userstore-info">' +
            '<h1>{name}</h1>' +
            '<em>{connectorName}</em>' +
        '</div>'

};
