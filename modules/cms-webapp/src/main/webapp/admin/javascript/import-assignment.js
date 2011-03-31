function showAssignmentFieldset()
{
  var fieldset = document.getElementById( 'assignment-fieldset' );
  fieldset.style.display = 'block';
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function hideAssignmentFieldset()
{
  var fieldset = document.getElementById( 'assignment-fieldset' );
  fieldset.style.display = 'none';
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function clearAssignmentFields()
{
  userPickerAutoComplete__assignee.removeUser();
  var date_assignment_duedate = document.getElementById('date_assignment_duedate');
  var time_assignment_duedate = document.getElementById('time_assignment_duedate');
  var _assignment_description = document.getElementById('_assignment_description');

  date_assignment_duedate.value = '';
  time_assignment_duedate.value = '';
  _assignment_description.value = '';
}