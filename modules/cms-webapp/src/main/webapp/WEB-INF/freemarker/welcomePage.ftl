[#ftl]
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <title>Enonic CMS - Boot Page</title>

    <link rel="shortcut icon" type="image/x-icon" href="resources/favicon.ico"/>
    <link rel="stylesheet" href="resources/style.css" type="text/css"/>
</head>

<body>

<header>
    <img alt="Enonic-logo" id="logo-screen" src="resources/images/logo-screen.gif" title="Enonic">
</header>
<nav>
    <ul class="menu horizontal main clearfix">
        <li><a title="Welcome" class="first path" href="#">Welcome</a></li>
        <li><a title="Community" href="http://www.enonic.com/en/community" target="_blank">Community</a></li>
        <li><a title="Documentation" href="http://www.enonic.com/en/docs" target="_blank">Documentation</a></li>
        <li><a title="Support" href="http://www.enonic.com/en/support" target="_blank">Support</a></li>
        <li><a title="Contact us" class=" last" href="http://www.enonic.com/en/contact-us" target="_blank">Contact
            us</a></li>
    </ul>
</nav>

<div id="content-outer" class="clearfix">
    <div id="content" class="clearfix">
        <section id="tools" class="clearfix">
            <div class="tools box admin left clearfix">
                <div class="icon-admin left">
                    <a href="${baseUrl}/admin">
                        <img src="resources/images/icon-admin.png"/>
                    </a>
                </div>
                <div class="left">
                    <h3>Admin Console</h3>

                    <div>
                        <a href="${baseUrl}/admin">${baseUrl}/admin</a>
                    </div>
                </div>
            </div>

            <div class="tools box left clearfix">
                <div class="icon-webdav left">
                    <a href="${baseUrl}/dav">
                        <img src="resources/images/icon-webdav.png"/>
                    </a>
                </div>
                <div class="left">
                    <h3>WebDAV</h3>

                    <div>
                        <a href="${baseUrl}/dav">${baseUrl}/dav</a>
                    </div>
                </div>
            </div>

        </section>

        <section id="welcome">
            <h1>Welcome to Enonic CMS</h1>

            <p>
                Access this installation by choosing <strong>Admin Console</strong> above, or one of the
                <strong>sites</strong> to the right.<br>
                <i><strong>Note:</strong> Default username/password for full access is: admin/password</i>
            </p>
        </section>

        <section id="steps">
            <article class="step clearfix">
                <div class="number left">1</div>
                <div class="description left">
                    <h2>Learn</h2>

                    <p>
                        Dig in to documentation for Editors, Administrators, Developers and Operators.<br>
                        We also recommend developers to check out our tutorials.<br>
                        <a href="http://enonic.com/docs">- http://enonic.com/docs</a><br>
                        <a href="http://enonic.com/tutorials">- http://enonic.com/tutorials</a>
                    </p>
                </div>
            </article>
            <article class="step clearfix">
                <div class="number left">2</div>
                <div class="description left">
                    <h2>Create</h2>

                    <p>
                        Enonic provides sample templates, including a themes framework, utilities and modules for
                        building
                        new
                        sites
                        quickly.<br>
                        <a href="http://github.com/enonic/cms-packages">- http://github.com/enonic/cms-packages</a>
                    </p>
                </div>
            </article>
            <article class="step last clearfix">
                <div class="number left">3</div>
                <div class="description left">
                    <h2>Share</h2>

                    <p>
                        Join the Enonic Community for Updates, Forum and Tutorials. Get help,
                        <br>Discuss and share. All out
                        code is also available on GitHub - you are welcome with your contributions.<br>

                        <a href="http://enonic.com/community">- http://enonic.com/community</a><br>
                        <a href="http://github.com/enonic">- http://github.com/enonic</a>
                    </p>
                </div>
            </article>
        </section>
    </div>
    <aside>
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
    </aside>
</div>

<footer class="clearfix">${versionTitleVersion} - Licensed under AGPL 3.0</footer>

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

