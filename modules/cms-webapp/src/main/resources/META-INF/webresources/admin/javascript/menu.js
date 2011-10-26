var useCookies = true;
var useCookieExpireDate = false;

function openTree() {
    for( key in branchOpen ) {
        if ( branchOpen[key] ){
            openBranch(key);
        }
    }
}

function closeTree() {
    document.cookie = cookiename + "= ";
    window.location = window.location;
}

function isBranchClosed(key) {
  if (document.getElementById('id'+key)) {
    return document.getElementById('id'+key).style.display == 'none';
  }
}

function decodeUtf8( str )
{
    var s;
    try
    {
        s = decodeURIComponent( escape( str ) );
    }
    catch(e) { /**/ }

    return s;
}

function openBranch(key) {

    var _key;
    if ( document.all )
        _key = decodeUtf8(key);
    else
        _key = key;

    if ( document.getElementById('id' + _key) && document.getElementById('img' + _key) ) {

        if (isBranchClosed(_key)) {
            document.getElementById('id'+_key).style.display = '';

            if ( document.getElementById('img'+_key).src.search(/Tplus\.png/) != -1 ){
                document.getElementById('img'+_key).src = 'javascript/images/Tminus.png';
            }
            else{
                document.getElementById('img'+_key).src = 'javascript/images/Lminus.png';
            }

            if (_key.search(/\-site/) == -1) {
                branchOpen[_key] = true;
            }
        }
        else {
            document.getElementById('id'+_key).style.display = 'none';

            if ( document.getElementById('img'+_key).src.search(/Tminus\.png/) != -1 ){
                document.getElementById('img'+_key).src = 'javascript/images/Tplus.png';
            }
            else{
                document.getElementById('img'+_key).src = 'javascript/images/Lplus.png';
            }

            if (_key.search(/\-site/) == -1) {
                branchOpen[_key] = false;
            }
        }

        if(useCookies) {
            setCookie();
        }
    }

}

function changeSite( domainkey)
{
    document.location = "adminpage?page=5&redirect=adminpage%3Fpage%3D2%26op%3Dbrowse%26loadsitepage%3Dtrue%26selecteddomainkey%3D"+domainkey;
}

function loadUnit(unitKey, domainKey) {

    var key = "-unit" + unitKey;
    branchOpen[key] = true;
    if (useCookies) {
        setCookie();
    }

    document.splash['redirect'].value = "adminpage?page=2&op=browse&selecteddomainkey="+ domainKey +"&selectedunitkey="+ unitKey;
    document.splash.submit();
}

function loadTopCategory(topCategoryKey) {
	var url = document.splash['redirect'].value;
    url = setParameter(url, "topcategorykey", topCategoryKey);
    document.splash['redirect'].value = url;
    document.splash.submit();
}


function loadMenu(menuKey, domainKey) {

    var key = "-rootmenu" + menuKey;
    branchOpen[key] = true;
    if (useCookies) {
        setCookie();
    }

    document.splash['redirect'].value = "adminpage?page=2&op=browse&selecteddomainkey="+ domainKey +"selectedmenukey="+ menuKey;
    document.splash.submit();
}

function loadBranch(type, key) {
    if (type == 'category') {
    	var url = document.splash['redirect'].value;
    	url = setParameter(url, "selectedunitkey", key);
    	document.splash['redirect'].value = url;
    }
    else if (type == 'menu') {
    	var url = document.splash['redirect'].value;
    	url = setParameter(url, "selectedmenukey", key);
    	document.splash['redirect'].value = url;
    }
    document.splash.submit();
}

function setCookie()
{
    var cookieValue = "";

    var i = 0;

    for ( key in branchOpen ){
        if ( branchOpen[key] ){
            if ( i > 0 )
                cookieValue += ",";
            i++;
            cookieValue += key;
        }
    }

    if ( useCookieExpireDate )
    {
        var date = new Date();
        var daysToExpire = 18250;
                                        
        date.setTime(date.getTime() + (daysToExpire * 24 * 60 * 60 * 1000));

        cookieValue += '; expires=' + date.toGMTString();
    }

    try
    {
        document.cookie = cookiename + "=" + cookieValue;
    }
    catch (err)
    {
        /**/
    }
}