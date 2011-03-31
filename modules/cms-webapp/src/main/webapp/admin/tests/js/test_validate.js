module("validate.js");

var g_testInput = document.getElementById('test-input-field');

function setValue(v)
{
    g_testInput.value = v;
}

test( 'content_nameIsValid', function()
{
    expect( 9 );

    equals( contentNameIsValid( 'content-name' ), true, 'content name should be valid' );
    equals( contentNameIsValid( ' starts-with-space' ), false, '  name starts with space should not be valid' );
    equals( contentNameIsValid( 'ends-with-space ' ), false, ' name ends with space should not be valid' );
    equals( contentNameIsValid( '  starts-with-two-spaces' ), false, '  name starts with two spaces should not be valid' );
    equals( contentNameIsValid( 'ends-with-two-spaces  ' ), false, ' name ends with two spaces should not be valid' );
    equals( contentNameIsValid( '' ), true, ' blank is valid' );
    equals( contentNameIsValid( 'with;semi-colon' ), false, 'semi-colon should not be valid' );
    equals( contentNameIsValid( 'with/forward-slash' ), false, 'forward slash should not be valid' );
    equals( contentNameIsValid( 'with#fragment-identifier' ), false, '# should not be valid' );
} );

test( 'regExpDate', function()
{
    expect( 11 );

    equals( regExpDate.test( '01.01.2020' ), true, '01.01.2020' );
    equals( regExpDate.test( '04.12.2009' ), true, '04.12.2009' );
    equals( regExpDate.test( '13.1.2009' ), false, 'Month is not padded (13.1.2009)' );
    equals( regExpDate.test( '0.12.2009' ), false, 'Day is not padded and starts with 0 (0.12.2009)' );
    equals( regExpDate.test( '01.13.09' ), false, 'Year has only 2 digits (01.13.09)' );
    equals( regExpDate.test( '19.13.09' ), false, 'Month is not in valid month range (19.13.09)' );
    equals( regExpDate.test( '20/10.1987' ), false, 'Format is invalid (20/10.1987)' );
    equals( regExpDate.test( '1922.08.12' ), false, 'Format is invalid (1922.08.12)' );
    equals( regExpDate.test( '32.05.2013' ), false, 'Day is out of valid range (32.05.2013)' );
    equals( regExpDate.test( '29.02.2000' ), true, 'Leapyear (29.02.2000)' );
    equals( regExpDate.test( '28.02.2000' ), true, 'Non leapyear (28.02.2000)' );

} );

test( 'regExpTime', function()
{
    expect( 6 );

    equals( regExpTime.test( '12:05' ), true, '12:05' );
    equals( regExpTime.test( '00:00' ), true, '00:00' );
    equals( regExpTime.test( '24:00' ), false, '24:00 is not allowed' );
    equals( regExpTime.test( '2:05' ), false, 'Hour is not padded (2:05)' );
    equals( regExpTime.test( '25:23' ), false, 'Hour is out of valid hour range (25:23)' );
    equals( regExpTime.test( '12:62' ), false, 'Minutes is out of valid seconds range (12:62)' );
} );

test( 'regExpTimeSeconds', function()
{
    expect( 3 );

    equals( regExpTimeSeconds.test( '12:15:29' ), true, '12:15:29' );
    equals( regExpTimeSeconds.test( '00:12:43' ), true, '00:12:43' );
    equals( regExpTimeSeconds.test( '14:0:69' ), false, 'Minutes is not valid (14:0:69)' );
} );

test( 'regExpURLSimple', function()
{
    expect( 16 );

    equals( regExpURLSimple.test( 'http://www.enonic.com' ), true, 'http://www.enonic.com' );
    equals( regExpURLSimple.test( 'https://www.enonic.com' ), true, 'https://www.enonic.com' );
    equals( regExpURLSimple.test( 'http://enonic.com' ), true, 'http://www.enonic.com' );
    equals( regExpURLSimple.test( 'https://enonic.com' ), true, 'https://www.enonic.com' );
    equals( regExpURLSimple.test( 'ftp://enonic.com' ), true, 'ftp://enonic.com' );
    equals( regExpURLSimple.test( 'dav://enonic.com' ), true, 'dav://enonic.com' );
    equals( regExpURLSimple.test( 'file://enonic.com' ), true, 'file://enonic.com' );
    equals( regExpURLSimple.test( 'go://enonic.com' ), true, 'go://enonic.com' );
    equals( regExpURLSimple.test( 'gopher://enonic.com' ), true, 'gopher://enonic.com' );
    equals( regExpURLSimple.test( 'imap://enonic.com' ), true, 'imap://enonic.com' );
    equals( regExpURLSimple.test( 'nntp://enonic.com' ), true, 'nntp://enonic.com' );
    equals( regExpURLSimple.test( 'pop://enonic.com' ), true, 'pop://enonic.com' );
    equals( regExpURLSimple.test( 'snmp://enonic.com' ), true, 'snmp://enonic.com' );
    equals( regExpURLSimple.test( 'telnet://enonic.com' ), true, 'telnet://enonic.com' );
    equals( regExpURLSimple.test( 'wais://enonic.com' ), true, 'wais://enonic.com' );
    equals( regExpURLSimple.test( 'fisk://enonic.com' ), false, 'fisk://enonic.com' );
} );

test( 'regExpEmail', function()
{
    expect( 37 );

    // http://fightingforalostcause.net/misc/2006/compare-email-regex.php
    // James Watts and Francisco Jose Martin Moreno
    equals( regExpEmail.test( 'pluss-sign-in-name+tag@domain.com' ), true, 'pluss-sign-in-name+tag@domain.com' );
    equals( regExpEmail.test( 'l3tt3rsAndNumb3rs@domain.com' ), true, 'has-dash@domain.com' );
    equals( regExpEmail.test( 'has-dash@domain.com' ), true, 'has-dash@domain.com' );
    equals( regExpEmail.test( 'hasApostrophe.o\'leary@domain.org' ), true, 'hasApostrophe.o\'leary@domain.org' );
    equals( regExpEmail.test( 'uncommonTLD@domain.museum' ), true, 'uncommonTLD@domain.museum' );
    equals( regExpEmail.test( 'uncommonTLD@domain.travel' ), true, 'uncommonTLD@domain.travel' );
    equals( regExpEmail.test( 'uncommonTLD@domain.mobi' ), true, 'uncommonTLD@domain.mobi' );
    equals( regExpEmail.test( 'countryCodeTLD@domain.uk' ), true, 'countryCodeTLD@domain.uk' );
    equals( regExpEmail.test( 'countryCodeTLD@domain.rw' ), true, 'countryCodeTLD@domain.rw' );
    equals( regExpEmail.test( 'lettersInDomain@911.com' ), true, 'lettersInDomain@911.com' );
    equals( regExpEmail.test( 'underscore_inLocal@domain.net' ), true, 'underscore_inLocal@domain.net' );
    equals( regExpEmail.test( 'IPInsteadOfDomain@127.0.0.1' ), true, 'IPInsteadOfDomain@127.0.0.1' );
    equals( regExpEmail.test( 'IPAndPort@127.0.0.1:25' ), true, 'IPAndPort@127.0.0.1:25' );
    equals( regExpEmail.test( 'subdomain@sub.domain.com' ), true, 'subdomain@sub.domain.com' );
    equals( regExpEmail.test( 'local@dash-inDomain.com' ), true, 'local@dash-inDomain.com' );
    equals( regExpEmail.test( 'dot.inLocal@foo.com' ), true, 'dot.inLocal@foo.com' );
    equals( regExpEmail.test( 'a@singleLetterLocal.org' ), true, 'a@singleLetterLocal.org' );
    equals( regExpEmail.test( 'singleLetterDomain@x.org' ), true, 'singleLetterDomain@x.org' );
    equals( regExpEmail.test( '&*=?^+{}\'~@validCharsInLocal.net' ), true, '&*=?^+{}\'~@validCharsInLocal.net' );
    equals( regExpEmail.test( 'foor@bar.newTLD' ), true, 'foor@bar.newTLD' );
    equals( regExpEmail.test( 'missingDomain@.com' ), false, 'missingDomain@.com' );
    equals( regExpEmail.test( '@missingLocal.org' ), false, '@missingLocal.org' );
    equals( regExpEmail.test( 'missingatSign.net' ), false, 'missingatSign.net' );
    equals( regExpEmail.test( 'missingDot@com' ), false, 'missingDot@com' );
    equals( regExpEmail.test( 'two@@signs.com' ), false, 'two@@signs.com' );
    equals( regExpEmail.test( 'colonButNoPort@127.0.0.1:' ), false, 'colonButNoPort@127.0.0.1:' );
    equals( regExpEmail.test( 'someone-else@127.0.0.1.26' ), false, 'someone-else@127.0.0.1.26' );
    equals( regExpEmail.test( '.localStartsWithDot@domain.com' ), false, '.localStartsWithDot@domain.com' );
    equals( regExpEmail.test( 'localEndsWithDot.@domain.com' ), false, 'localEndsWithDot.@domain.com' );
    equals( regExpEmail.test( 'two..consecutiveDots@domain.com' ), false, 'two..consecutiveDots@domain.com' );
    equals( regExpEmail.test( 'domainStartsWithDash@-domain.com' ), false, 'domainStartsWithDash@-domain.com' );
    equals( regExpEmail.test( 'domainEndsWithDash@domain-.com' ), false, 'domainEndsWithDash@domain-.com' );
    equals( regExpEmail.test( 'numbersInTLD@domain.c0m' ), false, 'numbersInTLD@domain.c0m' );
    equals( regExpEmail.test( 'missingTLD@domain.' ), false, 'missingTLD@domain.' );
    equals( regExpEmail.test( ' starts-with-space@domain.com' ), false, ' starts-with-space@domain.com' );
    equals( regExpEmail.test( 'ends-with-space@domain.com ' ), false, 'ends-with-space@domain.com ' );
    equals( regExpEmail.test( 'local@SecondLevelDomainNamesAreInvalidIfTheyAreLongerThan64Charactersss.org' ), false,
            'local@SecondLevelDomainNamesAreInvalidIfTheyAreLongerThan64Charactersss.org' );
} );

test( 'regExpDecimal', function()
{
    expect( 6 );

    equals( regExpDecimal.test( 1.4 ), true, 1.4, '1.4' );
    equals( regExpDecimal.test( 34.53 ), true, 34.53, '34.53' );
    equals( regExpDecimal.test( .4 ), true, .4, '.4' );
    equals( regExpDecimal.test( 2.4 ), true, 1.4, '1.4' );
    equals( regExpDecimal.test( 5 ), true, 5, '5' );
    equals( regExpDecimal.test( '6,5' ), false, '6,5', 'Wrong separator 6,5' );
} );

test( 'regExpInt', function()
{
    expect( 8 );

    equals( regExpInt.test( 3 ), true, 3, '3' );
    equals( regExpInt.test( 11 ), true, 11, '11' );
    equals( regExpInt.test( 101 ), true, 101, '101' );
    equals( regExpInt.test( 1002 ), true, 1002, '1002' );
    equals( regExpInt.test( -1 ), false, -1, '-1' );
    equals( regExpInt.test( 9.7 ), false, 9.7, '9.7' );
    equals( regExpInt.test( 'abc' ), false, 'abc', 'abc' );
    equals( regExpInt.test( '#' ), false, '#', '#' );
} );

test( 'regExpAZ09_dot_dash_underscore', function()
{
    expect( 8 );

    equals( regExpAZ09_dot_dash_underscore.test( 'tellusetadipiscingdignissim' ), true, 'Only letters' );
    equals( regExpAZ09_dot_dash_underscore.test( '123tellusetadipiscingdignissim' ), true, 'Numbers and letters' );
    equals( regExpAZ09_dot_dash_underscore.test( 'tellusetadipiscingdignissim123' ), true, 'Letters and numbers' );
    equals( regExpAZ09_dot_dash_underscore.test( 'tellusetadipisci5598ngdignissim' ), true, 'Letters and numbers mixed' );
    equals( regExpAZ09_dot_dash_underscore.test( 'TellusetADIpisCi5598ngdignisSIm' ), true, 'Lowercase and uppercase letters and numbers mixed' );
    equals( regExpAZ09_dot_dash_underscore.test( 'Telluse.tadipis-cingdig_nissim.' ), true, 'Has dot dash and underscore' );
    equals( regExpAZ09_dot_dash_underscore.test( 'Tellus et adipiscing dignissim' ), false, 'Has spaces' );
    equals( regExpAZ09_dot_dash_underscore.test( '%$&##' ), false, '(%$&##) None characters' );
} );

test( 'isEmpty()', function()
{
    expect( 4 );

    setValue( '' );
    equals( isEmpty( g_testInput ), true, '' );

    setValue( ' ' );
    equals( isEmpty( g_testInput ), true, ' ' );

    setValue( '   ' );
    equals( isEmpty( g_testInput ), true, '   ' );

    setValue( 'Some text' );
    equals( isEmpty( g_testInput ), false, 'Some text' );
} );

test( 'isStringValidXML()', function()
{
    expect( 5 );

    var complexXML = '<data>';
    complexXML += '<customers>';
    complexXML += '<customer id="1">';
    complexXML += 'A';
    complexXML += '</customer>';
    complexXML += '</customers>';
    complexXML += '</data>';

    var simple = '<data/>';

    var xmlWithMissingClosingTagForDataElem = '<data>';
    xmlWithMissingClosingTagForDataElem += '<customers>';
    xmlWithMissingClosingTagForDataElem += '<customer>';
    xmlWithMissingClosingTagForDataElem += 'A';
    xmlWithMissingClosingTagForDataElem += '</customer>';
    xmlWithMissingClosingTagForDataElem += '</customers>';

    var xmlWhereGTIsMissing = '<data';
    xmlWhereGTIsMissing += '<customers>';
    xmlWhereGTIsMissing += '<customer>';
    xmlWhereGTIsMissing += 'A';
    xmlWhereGTIsMissing += '</customer>';
    xmlWhereGTIsMissing += '</customers>';
    xmlWhereGTIsMissing += '</data>';

    var xmlWhereTagStartsWithNumber = '<1-data>';
    xmlWhereTagStartsWithNumber += '<customers>';
    xmlWhereTagStartsWithNumber += '<customer id="1">';
    xmlWhereTagStartsWithNumber += 'A';
    xmlWhereTagStartsWithNumber += '</customer>';
    xmlWhereTagStartsWithNumber += '</customers>';
    xmlWhereTagStartsWithNumber += '</1-data>';

    equals( isStringValidXML( complexXML ), true, 'Complex' );
    equals( isStringValidXML( simple ), true, 'Simple' );
    equals( isStringValidXML( xmlWithMissingClosingTagForDataElem ), false, 'Missing closing tag' );
    equals( isStringValidXML( xmlWhereGTIsMissing ), false, '(<data) > is missing' );
    equals( isStringValidXML( xmlWhereTagStartsWithNumber ), false, '(<1-data>) Tag name starts with a number' );
} );

test( 'getStrDateAsDate()', function()
{
    expect( 6 );

    equals( getStrDateAsDate( '01.12.2009 10:12' ).getDate(), '1' );
    equals( getStrDateAsDate( '01.12.2009 10:12' ).getMonth(), '11' );
    equals( getStrDateAsDate( '01.12.2009 10:12' ).getFullYear(), '2009' );
    equals( getStrDateAsDate( '01.12.2009 10:12' ).getHours(), '10' );
    equals( getStrDateAsDate( '01.12.2009 10:12' ).getMinutes(), '12' );
    equals( getStrDateAsDate( '01.12.2009 10:12:15' ).getSeconds(), '15' );
} );

test( 'isArray()', function()
{
    expect( 7 );

    var _object = {a:1, b:2, c:3};
    var _array = ['a', 'b', 'c'];
    var _emptyArray = [];
    var _string = 'fisk';
    var _number = 18;
    var _undefined = undefined;
    var _null = null;

    equals( isArray( _object ), false );
    equals( isArray( _array ), true );
    equals( isArray( _emptyArray ), true );
    equals( isArray( _string ), false );
    equals( isArray( _number ), false );
    equals( isArray( _undefined ), false );
    equals( isArray( _null ), false );
} );

test( 'Trim', function()
{
    expect( 4 );

    equals( Trim( ' Tellus et adipiscing dignissim.' ), 'Tellus et adipiscing dignissim.' );
    equals( Trim( 'Tellus et adipiscing dignissim. ' ), 'Tellus et adipiscing dignissim.' );
    equals( Trim( ' Tellus et adipiscing dignissim. ' ), 'Tellus et adipiscing dignissim.' );
    equals( Trim( '     Tellus et adipiscing dignissim.   ' ), 'Tellus et adipiscing dignissim.' );
} );

test( 'str_trim', function()
{
    expect( 4 );

    equals( str_trim( ' Tellus et adipiscing dignissim.' ), 'Tellus et adipiscing dignissim.' );
    equals( str_trim( 'Tellus et adipiscing dignissim. ' ), 'Tellus et adipiscing dignissim.' );
    equals( str_trim( ' Tellus et adipiscing dignissim. ' ), 'Tellus et adipiscing dignissim.' );
    equals( str_trim( '     Tellus et adipiscing dignissim.   ' ), 'Tellus et adipiscing dignissim.' );
} );

test( 'str_containsOnlyWhitespace', function()
{
    expect( 3 );

    equals( str_containsOnlyWhitespace( ' ' ), true );
    equals( str_containsOnlyWhitespace( '    ' ), true );
    equals( str_containsOnlyWhitespace( '' ), false );
} );