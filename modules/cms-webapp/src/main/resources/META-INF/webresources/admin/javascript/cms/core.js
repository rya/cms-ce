if ( !cms ) var cms = {};

// *****************************************************************************************************************************************
// *** Browser checks.
// *****************************************************************************************************************************************
cms.browser = {};
cms.browser.isOpera = window.opera && opera.buildNumber;
cms.browser.isWebKit = /WebKit/.test(navigator.userAgent);
cms.browser.isOldWebKit = cms.browser.isWebKit && !window.getSelection().getRangeAt;
cms.browser.isIE = !cms.browser.isWebKit && !cms.browser.isOpera && (/MSIE/gi).test(navigator.userAgent) && (/Explorer/gi).test(navigator.appName);
cms.browser.isIE6 = cms.browser.isIE && /MSIE [56]/.test(navigator.userAgent);
cms.browser.isGecko = !cms.browser.isWebKit && /Gecko/.test(navigator.userAgent);
cms.browser.isMac = navigator.userAgent.indexOf('Mac') != -1;
cms.browser.isAir = /adobeair/i.test(navigator.userAgent);

if ( !cms.includes ) cms.includes = {};