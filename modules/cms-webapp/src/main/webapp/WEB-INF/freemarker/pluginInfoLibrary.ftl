[#ftl]

[#macro pluginInfoRow plugin]
[#if plugin.framework == false]
<li>
    ${plugin.name} -
    [#if plugin.active == false]
        <b style="color: #A00000">INACTIVE</b>
    [#else]
        <b style="color: #00A000">ACTIVE</b>
    [/#if]
    <ul>
        <li>${plugin.id} ${plugin.version} deployed ${plugin.timestamp}</li>
    </ul>
</li>
[/#if]
[/#macro]

[#macro pluginInfoRowWithDetails plugin]
<li>
    ${plugin.name} -
    [#if plugin.active == false]
        <b style="color: #A00000">INACTIVE</b>
    [#else]
        <b style="color: #00A000">ACTIVE</b>
    [/#if] -
    <a href="javascript: showDetails(${plugin.key})">details</a>
    | <a href="servlet/tools/com.enonic.cms.server.service.tools.PluginInfoController?op=custom&update=${plugin.key}">update</a>
    <ul>
        <li>${plugin.id} ${plugin.version} deployed ${plugin.timestamp}</li>
    </ul>
</li>
[/#macro]

[#macro pluginDetail plugin]
<div class="detailWindow" id="details-${plugin.key}">
    <b>Plugin Info</b> (<a href="javascript: hideDetails(${plugin.key})">Close</a>)

    <ul>
        <li>Id</li>
        <ul>
            <li>${plugin.id}</li>
        </ul>
        <li>Name</li>
        <ul>
            <li>${plugin.name}</li>
        </ul>
        <li>Version</li>
        <ul>
            <li>${plugin.version}</li>
        </ul>
        <li>Deployed</li>
        <ul>
            <li>${plugin.timestamp}</li>
        </ul>
        <li>Configuration</li>
        <ul>
            [#list plugin.config?keys as key]
            <li>${key} = ${plugin.config[key]}</li>
            [/#list]
        </ul>
    </ul>

</div>
[/#macro]
