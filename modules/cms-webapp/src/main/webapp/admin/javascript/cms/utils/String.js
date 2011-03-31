if ( !cms ) var cms = {};
if ( !cms.utils ) cms.utils = {};
if ( !cms.utils.String ) cms.utils.String = {};

cms.utils.String = {
    // Removes forward and trainling white space from the string. 
    trim: function( str )
    {
        return str.replace(/^\s+/, '').replace(/\s+$/, '');
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    // Returns true if the string starts with value.
    startsWith: function( str, value )
    {
        if ( str.length < value.length )
            return false;
        else
            return str.substring(0, value.length) === value;
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    // Returns true if the string ends with value.
    endsWith: function( str, value )
    {
        if ( str.length < value.length )
            return false;
        else
            return str.substring(str.length - value.length, str.length) === value;
    },
    
    // -------------------------------------------------------------------------------------------------------------------------------------

    // Returns true if the string ends with value.
    concat: function( arr )
    {
        return arr.join(' ');
    }
};
