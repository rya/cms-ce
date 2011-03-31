
function content_advancedsearch_addAssignee( key, type, nameQualifiedName, userstorename )
{
    var assigneeInputElem = document.getElementById('_assignee');
    var assigneeViewElem = document.getElementById('view_assignee');

    var assigneeViewElemIsFormInput = assigneeViewElem.nodeName.toLowerCase() === 'input';

    assigneeInputElem.value = key;

    if ( assigneeViewElemIsFormInput )
    {
        assigneeViewElem.value = nameQualifiedName;
    }
    else
    {
        assigneeViewElem.innerHTML = nameQualifiedName;
    }
}


function content_advancedsearch_addAssigner( key, type, nameQualifiedName, userstorename )
{
    var assignerInputElem = document.getElementById('_assigner');
    var assignerViewElem = document.getElementById('view_assigner');

    var assignerViewElemIsFormInput = assignerViewElem.nodeName.toLowerCase() === 'input';

    assignerInputElem.value = key;

    if ( assignerViewElemIsFormInput )
    {
        assignerViewElem.value = nameQualifiedName;
    }
    else
    {
        assignerViewElem.innerHTML = nameQualifiedName;
    }
}

