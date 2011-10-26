if ( !cms ) var cms = {};
if ( !cms.element ) cms.element = {};
if ( !cms.element.Css ) cms.element.Css = {};

cms.element.Css = {

    // Finds and returns all elements with the given searchClass.
    // if no startNode is given the search will start from document.
    getElementsByClassName : function( searchClass, startNode, tag )
    {
        if ( startNode == null )
            startNode = document;

        // Native function.
        if ( document.getElementsByClassName && tag == null )
            return startNode.getElementsByClassName(searchClass);

        var classElements = new Array();
        if ( tag == null )
            tag = '*';
        var els = startNode.getElementsByTagName(tag);
        var elsLen = els.length;
        var pattern = new RegExp("(^|\\s)" + searchClass + "(\\s|$)");
        for ( var i = 0,j = 0; i < elsLen; i++ )
        {
            if ( pattern.test(els[i].className) )
            {
                classElements[j] = els[i];
                j++;
            }
        }

        return classElements;
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    // Returns true if the element has class with searchClass.
    hasClass : function( element, searchClass )
    {
        if ( typeof element == 'undefined' || element == null || !RegExp )
            return false;

        var re = new RegExp("(^|\\s)" + searchClass + "(\\s|$)");

        if ( typeof( element ) == "string" )
            return re.test(element);
        else if ( typeof(element) == "object" && element.className )
            return re.test(element.className);

        return false;
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    // Adds class to the element.
    addClass: function( element, value )
    {
        if ( this.hasClass( element, value ) ) return;
        if ( !element.className )
            element.className = value;
        else
        {
            var newClassName = element.className;
            newClassName += " ";
            newClassName += value;
            element.className = newClassName;
        }
    },

    // -------------------------------------------------------------------------------------------------------------------------------------

    // Removes the class from the element.
    removeClass: function( element, value )
    {
        if ( this.hasClass(element, value) )
        {
            var reg = new RegExp('(\\s|^)' + value + '(\\s|$)');
            element.className = element.className.replace(reg, ' ');
        }
    }
};
