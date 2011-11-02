/*****

Name: properties.js
Author: Enonic as
Version: 1.0

Client side script for the properties tab.

*****/

// view event log function
function viewEventLog(tableKey, domainKey, key, userKey) {
    url = "adminpage?page=350&op=browse";
    if (domainKey >= 0)
        url = url + "&selecteddomainkey=" + domainKey;
    if (tableKey >= 0)
        url = url + "&tablekey=" + tableKey + "&tablekeyvalue=" + key;
    if (userKey != null)
        url = url + "&userkey=" + userKey;

    var width = 875;
    var height = 600;
    var l = (screen.width - width) / 2;
    var t = (screen.height - height) / 2;

    newWindow = window.open(url, "Eventlog", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" + height + ",top=" + t + ",left=" + l);
    newWindow.focus();
}