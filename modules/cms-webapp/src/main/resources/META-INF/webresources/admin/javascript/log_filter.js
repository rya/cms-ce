/*****

Name: log_filter.js
Author: Enonic as
Version: 1.0

Client side script for section wizard functions.

*****/

var site = null;
var type = null;
var from = null;
var to = null;

function applyFilter() {
    var pageURL = document.getElementById("_pageurl").value;
    if (site != null || type != null || from != null || to != null) {
        pageURL = pageURL + "&filter=";
        if (site != null)
            pageURL = pageURL + site + ";";
        if (type != null)
            pageURL = pageURL + type + ";";
        if (from != null)
            pageURL = pageURL + "fr" + from + ";";
        if (to != null)
            pageURL = pageURL + "to" + to + ";";
    }

    document.location.href = pageURL;
}

function updateTypeSelect(type) {
    var selectObject = document.getElementById("_type");
    selectObject.selectedIndex = 0;

    // remove old options
    //var options = selectObject.options;
    var option = selectObject.childNodes;

    for (var i = option.length - 1; i > 0; i--) {
        //options.remove(i);
       if (option[i].tagName == 'OPTION') {
        selectObject.removeChild(option[i]);
       }
    }
    // add new options
    if (type == "admin" || type == "all") {
        var option = document.createElement("OPTION");
        selectObject.appendChild(option);
        option.innerHTML = "%txtLogin%";
        option.value = "ty0";

        option = document.createElement("OPTION");
        selectObject.appendChild(option);
        option.innerHTML = "%txtLoginFailed%";
        option.value = "ty2";

        option = document.createElement("OPTION");
        selectObject.appendChild(option);
        option.innerHTML = "%txtLogout%";
        option.value = "ty3";

        option = document.createElement("OPTION");
        selectObject.appendChild(option);
        option.innerHTML = "%txtLoginSite%";
        option.value = "ty1";

        option = document.createElement("OPTION");
        selectObject.appendChild(option);
        option.innerHTML = "%txtContent% %txtCreated%";
        option.value = "ta0;ty4";

        option = document.createElement("OPTION");
        selectObject.appendChild(option);
        option.innerHTML = "%txtContent% %txtUpdated%";
        option.value = "ta0;ty5";

        option = document.createElement("OPTION");
        selectObject.appendChild(option);
        option.innerHTML = "%txtContent% %txtRemoved%";
        option.value = "ta0;ty6";
    }

    if (type == "site" || type == "all") {
        option = document.createElement("OPTION");
        selectObject.appendChild(option);
        option.innerHTML = "%txtContent% %txtRead%";
        option.value = "ta0;ty7";
    }

    if (type == "admin" || type == "all") {
        option = document.createElement("OPTION");
        selectObject.appendChild(option);
        option.innerHTML = "%txtMenuItem% %txtCreated%";
        option.value = "ta1;ty4";

        option = document.createElement("OPTION");
        selectObject.appendChild(option);
        option.innerHTML = "%txtMenuItem% %txtUpdated%";
        option.value = "ta1;ty5";

        option = document.createElement("OPTION");
        selectObject.appendChild(option);
        option.innerHTML = "%txtMenuItem% %txtRemoved%";
        option.value = "ta1;ty6";
    }

    if (type == "site" || type == "all") {
        option = document.createElement("OPTION");
        selectObject.appendChild(option);
        option.innerHTML = "%txtMenuItem% %txtRead%";
        option.value = "ta1;ty7";
    }
}

function siteChanged(selectObject) {
    var value = selectObject.options[selectObject.selectedIndex].value;
    if (value == 'none')
        site = null;
    else
        site = value;

    if (site == 'sia')
        updateTypeSelect("admin");
    else if (site == null)
        updateTypeSelect("all");
    else {
        var selectObject = document.getElementById("_type");
        var value = selectObject.options[selectObject.selectedIndex].value;
        if (value.substring(0, 2) == 'ty' || value == 'none')
            updateTypeSelect("site");
    }
}

function typeChanged(selectObject) {
    var value = selectObject.options[selectObject.selectedIndex].value;
    if (value == 'none')
        type = null;
    else
        type = value;
}

function dateChanged(name, dropdown) {
    if (dropdown.name == name + "_year") {
        updateDropdown(name, "_month", 1, dropdown.selectedIndex);
    }
    else {
        updateDropdown(name, "_year", 6, dropdown.selectedIndex);
    }

    var year = null;
    var month = null;
    if (dropdown.selectedIndex > 0) {
        var selectObject = document.getElementById(name + "_year");
        year = selectObject.options[selectObject.selectedIndex].value;
        selectObject = document.getElementById(name + "_month");
        month = selectObject.options[selectObject.selectedIndex].value;
    }

    if (name == "_from") {
        if (year != null && month != null) {
            from = "01." + monthToString(month) + "." + year;
        } else {
            from = null;
        }
    }
    else {
        if (year != null && month != null) {
            to = "01." + monthToString( rollMonth( parseInt( month ) ) ) + "." + rollYear( parseInt(month), parseInt(year) ) ;
        } else {
            to = null;
        }
    }
}

function updateDropdown(name, suffix, newIndex, selectedIndex) {
    var selectObject = document.getElementById(name + suffix);
    if (selectedIndex > 0) {
        if (selectObject.selectedIndex == 0)
            selectObject.selectedIndex = newIndex;
    }
    else {
        selectObject.selectedIndex = 0;
    }
}

function monthToString(month) {
    if (month < 10)
        return "0" + month;
    else
        return "" + month;
}

function rollMonth(month) {
    if (month == 12)
        return 1;
    else
        return month + 1;
}

function rollYear(month, year) {
    var tempYear = year;
    if (month == 12)  {
        tempYear = year + 1;
    } else {
        tempYear = year;
    }
    return tempYear;
}
