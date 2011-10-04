if ( !Templates )
{
    var Templates = {};
}

Templates.common = {
    wizardPanelSteps:
        '<ol class="cms-wizard-steps clearfix">' +
            '<tpl for=".">' +
                '<li>' +
                    '<a href="javascript:;" {[ this.isCurrent( xindex - 1 ) ? "class=current" : "" ]}>' +
                        '<span class="text">{[xindex]}. {[  (values.stepTitle || values.title) ]}<span>' +
                        '<span class="arrow"></span>' +
                    '</a>' +
                '</li>' +
            '</tpl>' +
        '</ol>'

    ,userInfo:
        '<div>' +
            '<div class="cms-user-info clearfix">' +
                '<div class="cms-user-photo cms-left">' +
                    '<img alt="User" src="data/user/photo?key={key}&thumb=true"/>' +
                '</div>' +
                '<div class="cms-left">' +
                    '{displayName}<br/>' +
                    '({qualifiedName})<br/>' +
                    '<a href="mailto:{email}:">{email}</a>' +
                '</div>' +
            '</div>' +
        '</div>'

};
