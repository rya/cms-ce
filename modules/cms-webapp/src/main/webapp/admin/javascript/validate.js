/*****

Name: validate.js
Author: Enonic as
Version: 1.0

Client side script for validating form fields.
See "validateTest.html" for usage.

*****/


/*
BrowserDetector()
Parses User-Agent string into useful info.

Source: Webmonkey Code Library
(http://www.hotwired.com/webmonkey/javascript/code_library/)

Author: Rich Blaylock
Author Email: blaylock@wired.com

Usage: var bd = new BrowserDetector(navigator.userAgent);
*/


// Utility function to trim spaces from both ends of a string
function Trim(inString) {
  var retVal = "";
  var start = 0;
  while ((start < inString.length) && (inString.charAt(start) == ' ')) {
    ++start;
  }
  var end = inString.length;
  while ((end > 0) && (inString.charAt(end - 1) == ' ')) {
    --end;
  }
  retVal = inString.substring(start, end);
  return retVal;
}

// str_trim also trims "    ", \f\n\r\t\v
// http://blog.stevenlevithan.com/archives/faster-trim-javascript
function str_trim( str )
{
    str = str.replace(/^\s+/, '');
    for (var i = str.length - 1; i >= 0; i--)
    {
        if (/\S/.test(str.charAt(i)))
        {
            str = str.substring(0, i + 1);
            break;
        }
    }
    return str;
}

// str_containsOnlyWhitespace
// returns true if str contains \f\n\r\t\v
function str_containsOnlyWhitespace( str )
{
    return /^\s+$/.test(str);    
}

function BrowserDetector(ua) {

// Defaults
  this.browser = "Unknown";
  this.platform = "Unknown";
  this.version = "";
  this.majorver = "";
  this.minorver = "";

  uaLen = ua.length;

// ##### Split into stuff before parens and stuff in parens
  var preparens = "";
  var parenthesized = "";

  i = ua.indexOf("(");
  if (i >= 0) {
    preparens = Trim(ua.substring(0,i));
        parenthesized = ua.substring(i+1, uaLen);
        j = parenthesized.indexOf(")");
        if (j >= 0) {
          parenthesized = parenthesized.substring(0, j);
        }
  }
  else {
    preparens = ua;
  }

// ##### First assume browser and version are in preparens
// ##### override later if we find them in the parenthesized stuff
  var browVer = preparens;

  var tokens = parenthesized.split(";");
  var token = "";
// # Now go through parenthesized tokens
  for (var i=0; i < tokens.length; i++) {
    token = Trim(tokens[i]);
        //## compatible - might want to reset from Netscape
        if (token == "compatible") {
          //## One might want to reset browVer to a null string
          //## here, but instead, we'll assume that if we don't
          //## find out otherwise, then it really is Mozilla
          //## (or whatever showed up before the parens).
        //## browser - try for Opera or IE
    }
        else if (token.indexOf("MSIE") >= 0) {
      browVer = token;
    }
    else if (token.indexOf("Opera") >= 0) {
      browVer = token;
    }
        //'## platform - try for X11, SunOS, Win, Mac, PPC
    else if ((token.indexOf("X11") >= 0) || (token.indexOf("SunOS") >= 0) ||
(token.indexOf("Linux") >= 0)) {
      this.platform = "Unix";
        }
    else if (token.indexOf("Win") >= 0) {
      this.platform = token;
        }
    else if ((token.indexOf("Mac") >= 0) || (token.indexOf("PPC") >= 0)) {
      this.platform = token;
        }
  }

  var msieIndex = browVer.indexOf("MSIE");
  if (msieIndex >= 0) {
    browVer = browVer.substring(msieIndex, browVer.length);
  }

  var leftover = "";
  if (browVer.substring(0, "Mozilla".length) == "Mozilla") {
    this.browser = "Netscape";
        leftover = browVer.substring("Mozilla".length+1, browVer.length);
  }
  else if (browVer.substring(0, "Lynx".length) == "Lynx") {
    this.browser = "Lynx";
        leftover = browVer.substring("Lynx".length+1, browVer.length);
  }
  else if (browVer.substring(0, "MSIE".length) == "MSIE") {
    this.browser = "IE";
    leftover = browVer.substring("MSIE".length+1, browVer.length);
  }
  else if (browVer.substring(0, "Microsoft Internet Explorer".length) ==
"Microsoft Internet Explorer") {
    this.browser = "IE"
        leftover = browVer.substring("Microsoft Internet Explorer".length+1,
browVer.length);
  }
  else if (browVer.substring(0, "Opera".length) == "Opera") {
    this.browser = "Opera"
    leftover = browVer.substring("Opera".length+1, browVer.length);
  }

  leftover = Trim(leftover);

  // # Try to get version info out of leftover stuff
  i = leftover.indexOf(" ");
  if (i >= 0) {
    this.version = leftover.substring(0, i);
  }
  else
  {
    this.version = leftover;
  }
  j = this.version.indexOf(".");
  if (j >= 0) {
    this.majorver = this.version.substring(0,j);
    this.minorver = this.version.substring(j+1, this.version.length);
  }
  else {
    this.majorver = this.version;
  }


} // function BrowserCap



// Test function to display results from BrowserCap for a given string
function testBrowserDetector(uaString) {
  document.write("testing: " + uaString + "<BR>");
  var bc = new BrowserDetector(uaString);
  document.write("&nbsp;&nbsp;Browser: " + bc.browser + "<BR>");
  document.write("&nbsp;&nbsp;Platform: " + bc.platform + "<BR>");
  document.write("&nbsp;&nbsp;Version: " + bc.version + "<BR>");
  document.write("&nbsp;&nbsp;Majorver: " + bc.majorver + "<BR>");
  document.write("&nbsp;&nbsp;Minorver: " + bc.minorver + "<BR>");
  document.write("&nbsp;<BR>");
}

// regular expressions for URL, Date, Time and email
// http://www.regextester.com/
//var regExpDate = /^\d{1,2}\.\d{1,2}\.[1-9]\d{3}$/;
var regExpDate = /^$|(((0[1-9]|[12][0-9]|3[01])([\.])(0[13578]|10|12)([\.])(\d{4}))|(([0][1-9]|[12][0-9]|30)([\.])(0[469]|11)([\.])(\d{4}))|((0[1-9]|1[0-9]|2[0-8])([\.])(02)([\.])(\d{4}))|((29)(\.)(02)([\.])([02468][048]00))|((29)([\.])(02)([\.])([13579][26]00))|((29)([\.])(02)([\.])([0-9][0-9][0][48]))|((29)([\.])(02)([\.])([0-9][0-9][2468][048]))|((29)([\.])(02)([\.])([0-9][0-9][13579][26])))/;
//var regExpTime = /^[0-2]?[0-9]:[0-5][0-9]$/;
var regExpTime = /^([0-1][0-9]|[2][0-3]):([0-5][0-9])$/;
var regExpTimeSeconds = /^[0-2]?[0-9]:[0-5][0-9]:[0-5][0-9]$/;
var regExpURL = /^((http|https|ftp):\/\/.+\..+$)|((http|https|ftp):\/\/$)/;
var regExpURLSimple = /^$|^(http|https|ftp|dav|file|go|gopher|imap|ldap|nntp|pop|snmp|telnet|wais):\/\/.*/;
var regExpEmail = /^([\w\!\#$\%\&\'\*\+\-\/\=\?\^\`{\|\}\~]+\.)*[\w\!\#$\%\&\'\*\+\-\/\=\?\^\`{\|\}\~]+@((((([a-z0-9]{1}[a-z0-9\-]{0,62}[a-z0-9]{1})|[a-z])\.)+[a-z]{2,6})|(\d{1,3}\.){3}\d{1,3}(\:\d{1,5})?)$/i;
var regExpDecimal = /^[0-9]+(\.[0-9]+)?$/;
var regExpInt = /^[0-9]+$/;
var regExpAZ09_dot_dash_underscore = /^[a-zA-Z0-9\.\-_]+$/;

// error messages norwegian
var errorURL = "%errInvalidURL%";
var errorURLSimple = "%errInvalidURLSimple%";
var errorTime = "%errInvalidTime%";
var errorTimeSeconds = "%errInvalidTimeSeconds%";
var errorDate = "%errInvalidDate%";
var errorEmail = "%errInvalidEMail%";
var errorCustom = "%errCustom%";
var errorRequired = "%errRequired% ";
var errorHTMLEditor = "%errRequired% ";
var errorDropdown = "%errRequired% ";
var errorMissingURLDescription = "%errMissingURLDescription%";
var errorMissingURL =  "%errMissingURL%";
var errorDecimal = "%errDecimal%";;
var errorInt = "%errInt%";
var errorAllRequired = "%errAllRequired%";
var errorAtLeastOneRequired = "%errAtLeastOneRequired%";
var errorAZ09 = "%errAZ09%";
var errorXML = "%errInvalidXML%";
var errorContainsWhitespaceOnly = "%errContainsOnlyWhitespace%";
var errOnlineFromIsLaterThanOnlineTo = '%errOnlineFromIsLaterThanOnlineTo%';

// number of days in each month
var daysInMonth = new Array(12);
daysInMonth[0] = 31;
daysInMonth[1] = 29;
daysInMonth[2] = 31;
daysInMonth[3] = 30;
daysInMonth[4] = 31;
daysInMonth[5] = 30;
daysInMonth[6] = 31;
daysInMonth[7] = 31;
daysInMonth[8] = 30;
daysInMonth[9] = 31;
daysInMonth[10] = 30;
daysInMonth[11] = 31;

function contentNameIsValid( nameValue )
{
    var invalidNamePattern = new RegExp("^\\s+|[\\/#;]|\\s+$", 'gi');

    return !invalidNamePattern.test( nameValue );
}

function contentValidateName( element, tabPane )
{
    if ( !contentNameIsValid( element.value ) )
    {
        var errorMessage = '%errNameIllegalChars% ' + '\\ / # ;';
        error( element, errorMessage, tabPane );

        return false;
    }

    return true;
}

function validateURL(e)
{
	var i;
    
	if (isArray(e)) {
	    for(i = 0; i < e.length; i++) {
		    if ( !validateURL(e[i]) )
				return false;
		}
	} else if (!isEmpty(e)) {
        if (!regExpURL.test(e.value)) {
			error(e, errorURL);
			return false;
        }
    }

	return true;
}

function validateURLSimple(e)
{
	var i;

	if (isArray(e)) {
	    for(i = 0; i < e.length; i++) {
		    if ( !validateURLSimple(e[i]) )
				return false;
		}
	} else if (!isEmpty(e)) {
        if (!regExpURLSimple.test(e.value)) {
			error(e, errorURLSimple);
			return false;
        }
    }

	return true;
}

function validateURLList(formName, urls_field, descriptions_field)
{
	var f = document.forms[formName];

	var urls = f.all[urls_field];
	var descs = f.all[descriptions_field];

	if (isArray(urls)) {

		var i;
		for(i = 0; i < urls.length; ++i) {
			if (!isEmpty(urls[i])) {
				if (isEmpty(descs[i])) {
					alert(errorMissingURLDescription);
					return false;
				}
			} else {
				if (!isEmpty(descs[i])) {
					alert(errorMissingURL);
					return false;
				}
			}
		}
	} else {
		if (!isEmpty(urls)) {
			if (isEmpty(descs)) {
				alert(errorMissingURLDescription);
				return false;
			}
		} else {
			if (!isEmpty(descs)) {
				alert(errorMissingURL);
				return false;
			}
		}
	}

	return true;
}


function isEmpty(e)
{
    if( e == null || e.value == null )
    {
        return true;
    }

    var trimmedValue = str_trim(e.value);
    return trimmedValue == "";
}

function error(element, msg, tabPane)
{
    alert(msg);

    if (!element)
        return;

    try {
        // Fx will not switch tab so we have to force it.
        if (!document.all) {
            var _objTabPage = getParentNodeOfClass( element, "tab-page" );
            if ( typeof tabPane.setSelectedPage == 'function' )
                tabPane.setSelectedPage( _objTabPage.id );
        }
    }
    catch (e){

        if( tabPane != null ) {
            // get tabPageId to the page where the form element is.
            var objTabPage = getParentNodeOfClass( element, "tab-page" );
            // move the pane to the right page
            tabPane.setSelectedPage( objTabPage.id );
            moveFocusTo( element );
        }
    }
    moveFocusTo( element );
}

function findPos(obj) {
	var curleft = curtop = 0;
	if (obj.offsetParent) {
		curleft = obj.offsetLeft;
		curtop = obj.offsetTop;
		while (obj = obj.offsetParent) {
			curleft += obj.offsetLeft;
			curtop += obj.offsetTop;
		}
	}
	return [curleft,curtop];
}

function moveFocusTo(element) {

    if(element.isDisabled)
        return;

    var focusElement, cords;
    // Try finding the cordinates of the required element.

    // HTML area
    if (element.className && element.className.indexOf('editor-textarea') > -1)
    {
        var id = element.id;
        tinyMCE.get(id).focus();
        focusElement = document.getElementById(id + '_parent');
        cords = findPos(focusElement);
        window.scrollTo(0, cords[1]);
    } else
    {
        if ( element.type != undefined )
        {
            element.focus();
        }


        focusElement = element;

        if (element.type == 'hidden')
        {
            focusElement = element.nextSibling;
        }

        cords = findPos(focusElement);
        window.scrollTo(0, cords[1]);
    }
}

// PROBABLY NOT IN USE!
function validateHTMLEditor(e, fieldname)
{
	if (e.value == '') {
		alert(errorHTMLEditor + fieldname);
		return false;
	}

	return true;
}

function validateXML(e, fieldname, tabPane )
{
    var i;
    var XMLInputFields = document.getElementsByName(e.name);

    for( i = 0; i < XMLInputFields.length; i++ )
    {
        // An empty XML field is valid XML.
        if ( isEmpty(XMLInputFields[i] ) )
        {
            continue;
        }

        if ( !isStringValidXML( XMLInputFields[i].value ) )
        {
            error(XMLInputFields[i], errorXML + ': ' + fieldname, tabPane);
            return false;
        }
        else
        {
            return true;
        }

    }

    return true;
}

function isStringValidXML( xmlString )
{
    var isValid = true;
    var xmlDoc, parser;

    if ( window.ActiveXObject ) // IE
    {
        xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
        xmlDoc.async = "false";
        xmlDoc.loadXML(xmlString);

        if ( xmlDoc.parseError.errorCode != 0 )
        {
            isValid = false;
        }
    }
    else if ( document.implementation.createDocument ) // W3C
    {
        parser = new DOMParser();
        xmlDoc = parser.parseFromString(xmlString, "text/xml");

        if ( xmlDoc.documentElement.nodeName == "parsererror" )
        {
            isValid = false;
        }
    }
    else
    {
        // User agent has no DOM parser.
    }

    return isValid;
}

function validateDropdown(e, fieldname, tabPane)
{
    var i;

    if (e.options == null && isArray(e)) {
        for(i = 0; i < e.length; i++) {
            if ( !validateDropdown(e[i], fieldname, tabPane) )
                return false;
        }
    }
    else {
	for (i = 0; i < e.options.length; i++) {
		if (e.options[i].selected) {
                	if (e.options[i].value == "") {
				error(e, errorDropdown + fieldname, tabPane);
				return false;
			}
			return true
 		}
	}
    }

    return true;
}

function validateEmail(e, tabPane)
{
    var i;

    if (isArray(e)) {
        for(i = 0; i < e.length; i++) {
            if ( !validateEmail(e[i]) )
                return false;
        }
    }
    else if (!isEmpty(e)) {
        if (!regExpEmail.test(e.value)) {
            error(e, errorEmail, tabPane);
            return false;
        }
    }

    return true;
}

function validateCustom(e, fieldName, tabPane, regExp) {
	if (regExp == undefined || regExp.length == 0)
		return true;

	try {
		var regExpCustom = new RegExp(regExp);

	    var i;

	    if (isArray(e)) {
	        for(i = 0; i < e.length; i++) {
	            if ( !validateCustom(e[i], fieldName, tabPane, regExp) )
	                return false;
	        }
	    }
	    else if (!isEmpty(e)) {
	        if (!regExpCustom.test(e.value)) {
	            error(e, errorCustom+": "+fieldName, tabPane);
	            return false;
	        }
	    }

	    return true;
	}
	catch(e){
		alert("Validation failed, invalid RegExp syntax? RegExp: "+regExp);
		return false;
	}
}

function validateRegexp(e, fieldName, tabPane, regExp, errorMsg) {
	if (regExp == undefined || regExp.length == 0)
		return true;

	try {
		var regExpCustom = new RegExp(regExp);

	    var i;

	    if (isArray(e)) {
	        for(i = 0; i < e.length; i++) {
	            if ( !validateCustom(e[i], fieldName, tabPane, regExp) )
	                return false;
	        }
	    }
	    else if (!isEmpty(e)) {
	        if (!regExpCustom.test(e.value)) {
	            error(e, errorMsg, tabPane);
	            return false;
	        }
	    }

	    return true;
	}
	catch(e){
		alert("Validation failed, invalid RegExp syntax? RegExp: "+regExp);
		return false;
	}
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function getStrDateAsDate( strDate )
{
    var day = strDate.substring(0,2);
    var month = strDate.substring(3,5);
    var year = strDate.substring(6,10);
    var hours = strDate.substring(11,13);
    var minutes = strDate.substring(14,16);
    var seconds = strDate.substring(17,19);

    day = day.substring(0,1) === '0' ? day.substring(1,2) : day;
    month = month.substring(0,1) === '0' ? month.substring(1,2) : month;

    day = parseInt(day);
    month = parseInt(month) - 1; // JS Date: January is 0, Feb is 1, and so on.
    year = parseInt(year);

    return new Date(year, month, day, hours, minutes, seconds);
}
// -----------------------------------------------------------------------------------------------------------------------------------------

/**
 * DateTimeRangeValidator constructor function.
 *
 * Constructs a new DateTimeRangeValidator instance. 
 *
 * @param {String} startFieldNamePostfix The start fields name postfix.
 * @param {String} endFieldNamePostfix The end fields name postfix.
 * @param {Boolean} includeSeconds Has the fields seconds.
 * @param {Object} errorMessages An object containing the error messages (key:value).
 */
function DateTimeRangeValidator( startFieldNamePostfix, endFieldNamePostfix, includeSeconds, errorMessages )
{
    this.startFieldNamePostfix = startFieldNamePostfix;
    this.endFieldNamePostfix = endFieldNamePostfix;
    this.includeSeconds = includeSeconds;
    this.errorMessages = errorMessages;
}
// -----------------------------------------------------------------------------------------------------------------------------------------

/**
 * Validates start datetime and end datetime fields for:
 * - valid formats
 * - if the start datetime is later than end datetime.
 */
DateTimeRangeValidator.prototype.validate = function()
{
    var t = this;
    var startDateElement = document.forms['formAdmin']['date' + t.startFieldNamePostfix];
    var endDateElement = document.forms['formAdmin']['date' + t.endFieldNamePostfix];
    var startDateFocusHookElem = document.getElementById('date' + t.startFieldNamePostfix + '_focus_hook');

    if ( !t.isDateTimeStartAndDateTimeEndValid() )
    {
        return false;
    }

    t.showHideOnlineFromToErrorMessage(false, '');

    if ( startDateElement.value !== '' && endDateElement.value !== '' )
    {
        if ( t.isStartDateTimeLaterThanOrSameAsEndDateTime() )
        {
            t.showHideOnlineFromToErrorMessage(true, t.errorMessages.startDatetimeIsLaterThanEndDatetime );
            startDateFocusHookElem.focus();
            return false;
        }
    }

    return true;
};
// -----------------------------------------------------------------------------------------------------------------------------------------

/**
 * Returns true if the datetime start and end datetime end has a valid format.
 *
 * @return {Boolean} True if the datetime start and end datetime end has a valid format.
 */
DateTimeRangeValidator.prototype.isDateTimeStartAndDateTimeEndValid = function()
{
    var t = this;
    var doc = document;
    var startDateElement = doc.forms['formAdmin']['date' + t.startFieldNamePostfix];
    var endDateElement   = doc.forms['formAdmin']['date' + t.endFieldNamePostfix];
    var startTimeElement = doc.forms['formAdmin']['time' + t.startFieldNamePostfix];
    var endTimeElement   = doc.forms['formAdmin']['time' + t.endFieldNamePostfix];
    var startTimeFocusHookElem = doc.getElementById('time' + t.startFieldNamePostfix + '_focus_hook');
    var endDateFocusHookElem   = doc.getElementById('date' + t.endFieldNamePostfix + '_focus_hook');
    var endTimeFocusHookElem   = doc.getElementById('time' + t.endFieldNamePostfix + '_focus_hook');
    var startDateFocusHookElem = doc.getElementById('date' + t.startFieldNamePostfix + '_focus_hook');

    var invalidStartDate = !isEmpty(startDateElement) && !checkDate(startDateElement.value);
    var invalidStartTime = !isEmpty(startTimeElement) && !checkTime(startTimeElement.value, t.includeSeconds);
    var invalidEndDate = !isEmpty(endDateElement) && !checkDate(endDateElement.value);
    var invalidEndTime = !isEmpty(endTimeElement) && !checkTime(endTimeElement.value, t.includeSeconds);

    var errorMesssageTimeFormat = (t.includeSeconds) ? errorTimeSeconds : errorTime;

    if ( invalidStartDate )
    {
        t.showHideOnlineFromToErrorMessage(true, t.errorMessages.startLabel + ': ' + errorDate);
        startDateFocusHookElem.focus();
        return false;
    }
    if ( invalidStartTime )
    {
        t.showHideOnlineFromToErrorMessage(true,  t.errorMessages.startLabel + ': ' + errorMesssageTimeFormat);
        startTimeFocusHookElem.focus();
        return false;
    }
    if ( invalidEndDate )
    {
        t.showHideOnlineFromToErrorMessage(true, t.errorMessages.startLabel + ': ' + errorDate);
        endDateFocusHookElem.focus();
        return false;
    }
    if ( invalidEndTime )
    {
        t.showHideOnlineFromToErrorMessage(true, t.errorMessages.endLabel + ': ' + errorMesssageTimeFormat);
        endTimeFocusHookElem.focus();
        return false;
    }

    return true;
};
// -----------------------------------------------------------------------------------------------------------------------------------------

/**
 * Checks if start datetime is later than end datetime
 *
 * @return {Boolean} True if start datetime is later than or same as end datetime.
 */
DateTimeRangeValidator.prototype.isStartDateTimeLaterThanOrSameAsEndDateTime = function()
{
    var t = this;
    var doc = document;

    var startDateElement  = doc.forms['formAdmin']['date' + t.startFieldNamePostfix];
    var startTimeElement  = doc.forms['formAdmin']['time' + t.startFieldNamePostfix];
    var endDateElement    = doc.forms['formAdmin']['date' + t.endFieldNamePostfix];
    var endTimeElement    = doc.forms['formAdmin']['time' + t.endFieldNamePostfix];

    var startDate = startDateElement.value;
    var startTime = startTimeElement.value === '' ? '00:00:00' : startTimeElement.value;
    var endDate = endDateElement.value;
    var endTime = endTimeElement.value === '' ? '00:00:00' : endTimeElement.value;

    if ( !t.includeSeconds )
    {
        startTime = startTime.substring(0, 4);
        endTime = endTime.substring(0, 4);
    }

    var startDateTime = getStrDateAsDate(startDate + ' ' + startTime);
    var endDateTime = getStrDateAsDate(endDate + ' ' + endTime);

    return startDateTime >= endDateTime;
};
// -----------------------------------------------------------------------------------------------------------------------------------------

/**
 * Show or hide the error message.
 *
 * @param {Boolean} show
 * @param {String} message
 */
DateTimeRangeValidator.prototype.showHideOnlineFromToErrorMessage = function( show, message )
{
    var errorMessageContainerElement = document.getElementById('textfielddatetime-error-message');

    errorMessageContainerElement.innerHTML = message;
    errorMessageContainerElement.style.display = ( show ) ? 'block' : 'none';
};
// -----------------------------------------------------------------------------------------------------------------------------------------

function validateTime(e, tabPane)
{
    var i;

    if (isArray(e)) {
        for(i = 0; i < e.length; i++) {
            if ( !validateTime(e[i]) )
               return false;
        }
    }
    else if (!isEmpty(e)) {
        if (!checkTime(e.value, false)) {
            error(e, errorTime, tabPane);
            return false;
        }
    }

    return true;
}


function validateTimeSeconds(e, tabPane)
{
    var i;

    if (isArray(e)) {
        for(i = 0; i < e.length; i++) {
            if ( !validateTimeSeconds(e[i]) )
               return false;
        }
    }
    else if (!isEmpty(e)) {
        if (!checkTime(e.value, true)) {
            error(e, errorTimeSeconds, tabPane);
            return false;
        }
    }

    return true;
}


function validateDate(e, elementCaption, tabPane)
{
	var i;

	if (isArray(e)) {
	    for(i = 0; i < e.length; i++) {
		    if ( !validateDate(e[i], elementCaption, tabPane) )
				return false;
		}
    } else if (!isEmpty(e)) {
        if (!checkDate(e.value)) {
			error(e, errorDate, tabPane);
			return false;
        }
    }

	return true;
}

function validateAZ09_dot_dash_underscore(e, tabPane)
{
	var i;

	if (isArray(e)) {
	    for(i = 0; i < e.length; i++) {
		    if ( !validateAZ09(e[i]) )
				return false;
		}
    } else if (!isEmpty(e)) {
        if (!regExpAZ09_dot_dash_underscore.test(e.value)) {
			error(e, errorAZ09, tabPane);
			return false;
        }
    }

	return true;
}


function validateDecimal(e, tabPane)
{
    var i;

    if (isArray(e)) {
        for(i = 0; i < e.length; i++) {
            if (!validateDecimal(e[i]))
                return false;
        }
    }
    else if (isEmpty(e)) {
        e.value = "0.00";
    }
    else  {
        if (!regExpDecimal.test(e.value)) {
            error(e, errorDecimal, tabPane);
            return false;
        }
    }

    return true;
}


function validateInt(e, tabPane)
{
    var i;

    if (isArray(e)) {
        for(i = 0; i < e.length; i++) {
            if (!validateInt(e[i]))
                return false;
        }
    }
    else if (isEmpty(e)) {
       // e.value = "0";
    }
    else  {
        if (!regExpInt.test(e.value)) {
            error(e, errorInt, tabPane);
            return false;
        }
    }

    return true;
}


function validateRadioButtons(e, fieldname, tabPane)
{
    var i;
    var valid = false;
    for( i = 0; i < e.length; i++ ) {
	if ( e[i].checked) {
	    valid = true;
	}
    }

    if( !valid ) {
        error(e, errorRequired + fieldname, tabPane);
        return false;
    }
    else {
        return true;
    }
}


function validateCheckbox(e, fieldname, tabPane)
{
    if (e.checked)
	return true;
    else {
        error(e, errorRequired + fieldname, tabPane);
        return false;
    }
}

function validateRelatedContent(e, fieldname, tabPane) {
  if (e == undefined) {
		error(e, errorRequired + fieldname, tabPane);
		return false;
	}

	if (e.nodeName == "TABLE") {
		// This is a multiple relatedcontent input
		if (e.getElementsByTagName("input").length == 0) {
			error(e, errorRequired + fieldname, tabPane);
			return false;
		}
	}
	else if (e.nodeName == "SELECT") {
		// This is a single dropdown
		if (e.options[e.selectedIndex].value == "") {
			error(e, errorRequired + fieldname, tabPane);
			return false;
		}
	}
	else if (e.length) {
		// This is an array of either dropdowns or tables
		for (var i=0; i<e.length; i++) {
			if (e[i].nodeName == "TABLE") {
				// This is a multiple relatedcontent input
				if (e[i].getElementsByTagName("input").length == 0) {
					error(e[i], errorRequired + fieldname, tabPane);
					return false;
				}
			}
			else if (e[i].nodeName == "SELECT") {
				// This is a single relatedcontent input (dropdown)
				if (e[i].options[e[i].selectedIndex].value == "") {
					error(e[i], errorRequired + fieldname, tabPane);
					return false;
				}
			}
			else {
				// This shouldn't happen
			}
		}
	}
	return true;
}

function validateRequired(e, fieldname, tabPane, ignoreFirstPos) {
  var i;



  if (isArray(e)) {
    // element er et kobble med related content ( e[i].value )
    for( i = 0; i < e.length; i++ ) {
      if (ignoreFirstPos && i == 0)
        continue;

      if (e[i].value == "") {
        error(e[i], errorRequired + fieldname, tabPane);
        return false;
      }
    }
    return true;
  } else {
    if (isEmpty(e)) {
      error(e, errorRequired + fieldname, tabPane);
      return false;
    } else {
      return true;
    }
  }
}

function validateRequiredOpen( e, fieldname, tabPane, ignoreFirstPos )
{
    var i, isOpen;

    if ( isArray( e ) )
    {
        for ( i = 0; i < e.length; i++ )
        {
            if ( ignoreFirstPos && i == 0 )
                continue;

            isOpen = !e[i].disabled || !e[i].readOnly;

            if ( e[i].value == "" && isOpen )
            {
                error( e[i], errorRequired + fieldname, tabPane );
                return false;
            }
        }
        return true;
    }
    else
    {
        isOpen = !e.readOnly;

        if ( isEmpty( e ) && isOpen )
        {
            error( e, errorRequired + fieldname, tabPane );
            return false;
        }
        else
        {
            return true;
        }
    }
}

function validateAllRequired(e, fieldname, tabPane)
{
    var i;

	if (isArray(e)) {
	    var valid = true;

        for( i = 0; i < e.length; i++ ) {
		    if ( e[i].value == "") {
			    valid = false;
			}
        }
        if( !valid ) {
            error(e, errorAllRequired + fieldname, tabPane);
            return false;
        }
        else {
            return true;
        }

    }
    else {
        if (isEmpty(e)) {
            error(e, errorAllRequired + fieldname, tabPane);
            return false;
        }
        else {
            return true;
        }
    }
}

function validateAtLeastOne(e, fieldname, tabPane) {
	var i;

	var valid = false;

	if (isArray(e)) {
        for( i = 0; i < e.length; i++ ) {
		    if (e[i].checked) {
			    valid = true;
			}
        }
    }
    else {
        if (isEmpty(e)) {
            valid = false;
        }
        else {
        	if (e.checked)
	            valid = true;
	        else
	        	valid = false;
        }
    }

    if( !valid ) {
        error(e, errorAtLeastOneRequired + fieldname, tabPane);
        return false;
    }
    else {
        return true;
    }
}

function isArray( obj )
{
    if ( typeof obj === 'undefined' || obj === null )
    {
        return false;
    }
    
    return ( typeof( obj ) != 'string' && typeof( obj.length ) != 'undefined' );
}

function checkAll(formName, fields, tabPane)
{
    var i;
	var f = document.forms[formName];

	for (i = 0; i < fields.length; i++)
	{
		if ( fields[i] != null)
		{
		    var element = f[fields[i][1]];
	        var elementCaption = fields[i][0];
	        var regExp;

            if ( element !== undefined && str_containsOnlyWhitespace(element.value) )
            {
                error(element, errorContainsWhitespaceOnly + ': ' + elementCaption, tabPane);
                return false;
            }

	        if (fields[i].length > 4)
			{
	        	regExp = fields[i][3];
	        	var errorMsg = fields[i][4];
	        	if ( !fields[i][2]( element, elementCaption, tabPane, regExp, errorMsg ) )
	                return false;
	        }
			else if (fields[i].length > 3) {
	        	regExp = fields[i][3];
	        	if ( !fields[i][2]( element, elementCaption, tabPane, regExp ) )
	                return false;
	        }
	        else {
	            if ( !fields[i][2]( element, elementCaption, tabPane) )
	                return false;
	        }
        }
    }

	return true;
}


function checkTime(timeString, seconds)
{
    // get hour and check that it is ok
    var hour = parseInt(timeString.substr(0, 2));

    if (seconds && !regExpTimeSeconds.test(timeString))
        return false;
    else if (!seconds && !regExpTime.test(timeString))
        return false;

    if (hour > 23)
        return false;

    return true;
}

function checkDate (dateString)
{
    if( isEmpty(dateString) )
    {
        return true;
    }

	// check date formatting
    if (!regExpDate.test(dateString))
		return false;

	// get day, month and year
	pos = dateString.indexOf(".");
    var day = dateString.substr(0, pos) - 0;
	var month = dateString.substr(pos + 1, 2) - 0;
	pos = dateString.indexOf(".", pos + 1);
	var year = dateString.substr(pos + 1, 4) - 0;
	var daysInFeb = 0

	// check date and month
	if (month < 1 || month > 12)
		return false
    if (day > daysInMonth[month - 1])
		return false;

	// special for february
	if (month == 2) {
		if ( ( (year % 4 == 0) && !(year % 100 == 0) ) || (year % 400 == 0) )
	    	daysInFeb = 29
	  	else
	   		daysInFeb = 28

	  	if (day > daysInFeb)
	    	return false;
	}

	return true;
}

