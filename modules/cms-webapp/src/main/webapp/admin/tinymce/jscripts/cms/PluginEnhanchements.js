/*
  This file contains functions and methods that extends TinyMCEs dialogs like setting
  focus on the first element and so on.

  IMPORTANT!!
  When updateing an plugin, this file must be embedded in the plugin HTML file(s).
*/

/*
  Method: cmsSetfocus

  Tries to set focus on the first element in the plugin dialog form.
*/
function cmsInitDialog() {
  try {
    if (document.forms[0] != null && document.forms[0].elements.length > 0) {
      var el = document.forms[0].elements;
      var elLn = el.length;
      for(var i = 0; i < elLn; i++) {
        var current = el[i];
        if (current.type == 'text' && !current.disabled) {
          current.focus();
          break;
        }
      }
      cmsAddEnterEvent(document.forms[0]);
    }
    var firstElement = null;
    if (tinyMCEPopup.getWindowArg("mode") == 'replace') {
      firstElement = document.getElementById('replace_panel_searchstring');
    }

    if (firstElement)
      firstElement.focus();

  } catch(err) {  }
}
// ---------------------------------------------------------------------------------------------------------------------
/*

*/
function cmsAddEnterEvent(form) {
  var el = form.elements;
  var elLn = el.length;
  for(var i = 0; i < elLn; i++) {
    var current = el[i];
    if (current.type == 'text') {
       cmsAddEvent(current, 'keydown', function(e) {

         if (!e) var e = window.event;
         var code;
         if (e.keyCode) code = e.keyCode;
         else if (e.which) code = e.which;

         if (code == 13) {
           try {
             document.getElementById('insert').onclick();
           } catch (err) { /**/ }
        } else if (code == 27) {
            try {
              document.getElementById('cancel').onclick();
            } catch (err) {
              tinyMCEPopup.close();
            }
         }
       });
    }
  }
}

// ---------------------------------------------------------------------------------------------------------------------

/*
  Method : cmsOnLoadChain

  Method that prevents existing onload events to be overridden.

  Parameters:

    func - Function/Method to be added.
*/
function cmsOnLoadChain(func) {
  var oldonload = window.onload;
	if (typeof window.onload != 'function') {
		window.onload = func;
	} else {
		window.onload = function() {
		if (oldonload) {
			oldonload();
		}
		func();
		}
	}
}
// ---------------------------------------------------------------------------------------------------------------------

/*
  Method : cmsAddEvent

  Adds an event to an element.

  Parameters:

    obj - The element.
    type - Event type.
    fn - The function.
*/
function cmsAddEvent(obj, type, fn) {
  if ( obj.attachEvent ) {
    obj['e'+type+fn] = fn;
    obj[type+fn] = function(){obj['e'+type+fn]( window.event );}
    obj.attachEvent( 'on'+type, obj[type+fn] );
  } else
    obj.addEventListener( type, fn, false );
}

/*
  Method : cmsRemoveEvent

  Removes an event from an element.

  Parameters:

    obj - The element.
    type - Event type.
    fn - The function.
*/
function cmsRemoveEvent(obj, type, fn) {
  if ( obj.detachEvent ) {
    obj.detachEvent( 'on'+type, obj[type+fn] );
    obj[type+fn] = null;
  } else
    obj.removeEventListener( type, fn, false );
}
// ---------------------------------------------------------------------------------------------------------------------

cmsOnLoadChain(cmsInitDialog);