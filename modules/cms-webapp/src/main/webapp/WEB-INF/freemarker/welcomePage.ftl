[#ftl]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Enonic CMS - Boot Page</title>

    <link rel="shortcut icon" type="image/x-icon" href="${baseUrl}/resources/favicon.ico"/>
    <link rel="stylesheet" href="${baseUrl}/resources/bootpage.css" type="text/css"/>
</head>

<body id="main">

<div id="logo">
    <img alt="Enonic-logo" id="logo-screen" src="${baseUrl}/resources/images/logo-screen.gif" title="Enonic"/>
</div>
<div id="nav">
    <ul class="menu horizontal main clearfix">
        <li><a title="Welcome" class="first path" href="#">Welcome</a></li>
        <li><a title="Community" href="http://www.enonic.com/en/community" rel="external">Community</a></li>
        <li><a title="Documentation" href="http://www.enonic.com/en/docs" rel="external">Documentation</a></li>
        <li><a title="Support" href="http://www.enonic.com/en/support" rel="external">Support</a></li>
        <li><a title="Contact us" class=" last" href="http://www.enonic.com/en/contact-us" rel="external">Contact
            us</a></li>
    </ul>
</div>

<div id="content-outer" class="clearfix">
    <div id="content" class="clearfix">
        <div id="management-components" class="clearfix">
            <div class="component box admin left clearfix">
                <div class="icon-admin left">
                    <a href="${baseUrl}/admin">
                        <img src="${baseUrl}/resources/images/icon-admin.png" alt="Admin"/>
                    </a>
                </div>
                <div class="left">
                    <h3>Admin Console</h3>

                    <div>
                        <a href="${baseUrl}/admin">${baseUrl}/admin</a>
                    </div>
                </div>
            </div>

            <div class="component box left clearfix">
                <div class="icon-webdav left">
                    <a href="${baseUrl}/dav">
                        <img src="${baseUrl}/resources/images/icon-webdav.png" alt="WebDAV"/>
                    </a>
                </div>
                <div class="left">
                    <h3>WebDAV</h3>

                    <div>
                        <a href="${baseUrl}/dav">${baseUrl}/dav</a>
                    </div>
                </div>
            </div>

        </div>

        <div id="welcome">
            <h1>Welcome to Enonic CMS</h1>

            <p>
                Access this installation by choosing <strong>Admin Console</strong> above, or one of the
                <strong>sites</strong> to the right.<br/>
                <em><strong>Note:</strong> Default username/password for full access is: admin/password</em>
            </p>
        </div>

        <div id="steps">
            <div class="step clearfix">
                <div class="number left">1</div>
                <div class="description left">
                    <h2>Learn</h2>

                    <p>
                        Dig in to documentation for Editors, Administrators, Developers and Operators.<br/>
                        We also recommend developers to check out our tutorials.<br/>
                        <a href="http://enonic.com/docs" rel="external">- http://enonic.com/docs</a><br/>
                        <a href="http://enonic.com/tutorials" rel="external">- http://enonic.com/tutorials</a>
                    </p>
                </div>
            </div>
            <div class="step clearfix">
                <div class="number left">2</div>
                <div class="description left">
                    <h2>Create</h2>

                    <p>
                        Enonic provides sample templates, including a themes framework, utilities and modules for
                        building
                        new
                        sites
                        quickly.<br/>
                        <a href="http://github.com/enonic/cms-packages" rel="external">- http://github.com/enonic/cms-packages</a>
                    </p>
                </div>
            </div>
            <div class="step last clearfix">
                <div class="number left">3</div>
                <div class="description left">
                    <h2>Share</h2>

                    <p>
                        Join the Enonic Community for Updates, Forum and Tutorials. Get help,
                        <br/>Discuss and share. All out
                        code is also available on GitHub - you are welcome with your contributions.<br/>

                        <a href="http://enonic.com/community" rel="external">- http://enonic.com/community</a><br/>
                        <a href="http://github.com/enonic" rel="external">- http://github.com/enonic</a>
                    </p>
                </div>
            </div>
        </div>
    </div>
    <div id="east">
        <div id="sites" class="box">
            <h2>Sites</h2>
            <ul>

            [#if upgradeNeeded == false]
                [#list sites?keys?sort as key]
                    <li>
                        <h3>${key}</h3>

                        <div>
                            <a href="${baseUrl}/site/${sites[key]}/">
                            ${baseUrl}/site/${sites[key]}/
                            </a>
                        </div>
                    </li>
                [/#list]
            [/#if]
            [#if upgradeNeeded == true]
                <li>
                    <h3>N/A</h3>
                </li>
            [/#if]
            </ul>
        </div>
    </div>
</div>

<div class="clearfix" id="footer">
    <div id="license" class="left">
        ${versionTitleVersion} - Licensed under AGPL 3.0
    </div>
    <div id="social" class="right">
        <a href="http://www.enonic.com/en/rss" rel="external">
            <img src="${baseUrl}/resources/images/icon-rss-large.png" alt="RSS"/>
        </a>
        <a href="http://twitter.com/#!/enonic_cms" rel="external">
            <img src="${baseUrl}/resources/images/icon-twitter-large.png" alt="Enonic on Twitter"/>
        </a>
    </div>
</div>

<script type="text/javascript" src="${baseUrl}/resources/bootpage.js">//</script>

</body>
</html>

<!--
TODO: Add upgrade info boxes

[#if modelUpgradeNeeded == true]
<div class="infoBoxError">
    <b>Upgrade Needed!</b>
    <br/>
    Database upgrade from model <b>${upgradeFrom}</b> to model <b>${upgradeTo}</b> is needed. Admin or site will not
    work correctly if not upgraded. Go to <a href="${baseUrl}/upgrade">upgrade</a> to upgrade.
</div>
[/#if]
[#if softwareUpgradeNeeded == true]
<div class="infoBoxError">
    <b>Software Upgrade Needed!</b>
    <br/>
    Database model is newer than software allows. Please upgrade the software. Admin or site will not
    work correctly if not upgraded.
</div>
[/#if]
-->

