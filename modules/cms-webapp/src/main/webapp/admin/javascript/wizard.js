/*****

Name: wizard.js
Author: Enonic as
Version: 1.0

Client side script for wizard functions.

*****/

function buttonClick(button) 
{
    submitForm(button.name);
}

function submitForm(buttonName) 
{
    var input = document.getElementById("__wizard_button");
    input.value = buttonName;

    var form = document.getElementById("formAdmin");
    form.submit();
}

function cancelClick(button) 
{
    var form = document.getElementById("formCancel");
    var input = form["__wizard_button"];
    input.value = button.name;
    form.submit();
}

function closeClick(button)
{
    cancelClick(button);
}

//function resetForm()
//{
//    alert("reset");
//    var form = document.getElementById("formAdmin");
//    form.reset();
//}