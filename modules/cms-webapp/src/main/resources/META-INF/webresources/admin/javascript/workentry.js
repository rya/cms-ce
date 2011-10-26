/*****

Name: workentry.js
Author: Enonic as
Version: 1.0

Client side script for work entry wizard functions.

*****/


specialIndex = 0;
var specialFields = new Array(6);
specialFields[0] = null;
specialFields[1] = new Array("%fldRepeatInterval%", "stepstate_workentry_trigger_repeat_@interval", validateRequired);
specialFields[2] = new Array("%fldRepeatCount%", "stepstate_workentry_trigger_repeat_@interval", validateRequired);
specialFields[3] = new Array("%fldMinutes%", "stepstate_workentry_trigger_hourly_@minutes", validateRequired);
specialFields[4] = new Array("%fldTime%", "stepstate_workentry_trigger_daily_@time", validateRequired);
specialFields[5] = new Array("%fldCronExpression%", "stepstate_workentry_trigger_cron", validateRequired);

var validatedFields = new Array(4);
validatedFields[0] = new Array("%fldName%", "stepstate_workentry_name", validateRequired);
validatedFields[1] = new Array("%fldClass%", "stepstate_workentry_workclass", validateDropdown);
validatedFields[2] = specialFields[specialIndex];
validatedFields[3] = null;

function removeProperty( table, objThis ) 
{
  var count = itemcount(formAdmin[objThis.name]);
  if( count == 1 ) {
    document.formAdmin["stepstate_workentry_properties_property_@name"].value = "";
    document.formAdmin["stepstate_workentry_properties_property_@value"].value = "";
    return;
  }
  
  var index = getObjectIndex(objThis);
  document.getElementById(table).deleteRow(index);
}
				
function addProperty( table ) 
{
  addTableRow( table, 0, true );
}

function setValidatedFields(field1, count)
{
  specialIndex = field1;
  validatedFields[2] = specialFields[specialIndex];
  if (count > 1)
    validatedFields[3] = specialFields[specialIndex + 1];
  else
    validatedFields[3] = null;
}

function setShowHide(showInterval, showCount, showMinutes, showTime, showExpression)
{
  // repeat interval
  var tr = document.getElementById("tr_repeat_interval");
  if (showInterval)
    tr.style.display = '';
  else
    tr.style.display = 'none';

  // repeat count
  tr = document.getElementById("tr_repeat_count");
  if (showCount)
    tr.style.display = '';
  else
    tr.style.display = 'none';

  // hourly (minutes)
  tr = document.getElementById("tr_hourly");
  if (showMinutes)
    tr.style.display = '';
  else
    tr.style.display = 'none';

  // daily (time)
  tr = document.getElementById("tr_daily");
  if (showTime)
    tr.style.display = '';
  else
    tr.style.display = 'none';

  // custom (cron expression)
  tr = document.getElementById("tr_cron");
  if (showExpression)
    tr.style.display = '';
  else
    tr.style.display = 'none';
}
				
function typeChange( dropDown ) 
{
  var selectedOption = dropDown.options[dropDown.selectedIndex];

  if (selectedOption.value == 'once') {
    var type = document.getElementById("idtype");
    type.value = "simple";

    setShowHide(false, false, false, false, false);
    setValidatedFields(0, 0);
  }

  else if (selectedOption.value == 'infinite') {
    var type = document.getElementById("idtype");
    type.value = "simple";

    setShowHide(true, false, false, false, false);
    setValidatedFields(1, 1);
  }

  else if (selectedOption.value == 'repeatedly') {
    var type = document.getElementById("idtype");
    type.value = "simple";

    setShowHide(true, true, false, false, false);
    setValidatedFields(1, 2);
  }

  else if (selectedOption.value == 'hourly') {
    var type = document.getElementById("idtype");
    type.value = "cron";

    setShowHide(false, false, true, false, false);
    setValidatedFields(3, 1);
  }

  else if (selectedOption.value == 'daily') {
    var type = document.getElementById("idtype");
    type.value = "cron";

    setShowHide(false, false, false, true, false);
    setValidatedFields(4, 1);
  }

  else if (selectedOption.value == 'custom') {
    var type = document.getElementById("idtype");
    type.value = "cron";

    setShowHide(false, false, false, false, true);
    setValidatedFields(5, 1);
  }
}

function setDateTime(name)
{
    var inputDate = document.getElementById("date" + name);
    var inputTime = document.getElementById("time" + name);
    var inputHidden = document.getElementById("id" + name);
    inputHidden.value = inputDate.value + " " + inputTime.value;
}

function setDefaultTime(name)
{
    var inputTime = document.getElementById("time" + name);
    if (inputTime.value == '')
        inputTime.value = '12:00:00';
}