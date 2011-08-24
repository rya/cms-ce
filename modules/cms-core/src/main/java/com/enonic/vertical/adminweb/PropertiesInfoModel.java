package com.enonic.vertical.adminweb;

import java.util.Map;
import java.util.Properties;

import org.jdom.Document;
import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 8/17/11
 * Time: 7:37 AM
 */
public class PropertiesInfoModel
{

    private Properties systemProperties;

    private Properties datasourceProperties;

    private Map<Object, Object> configurationProperties;

    private static final String ROOT_XML_NAME = "model";


    public Document toXML()
    {
        PropertiesXmlCreator xmlCreator = new PropertiesXmlCreator();

        Element modelEl = new Element( ROOT_XML_NAME );

        Document doc = new Document( modelEl );

        modelEl.addContent( xmlCreator.createElement( "systemProperties", "systemProperty", systemProperties ) );
        modelEl.addContent( xmlCreator.createElement( "datasourceProperties", "datasourceProperty", datasourceProperties ) );
        modelEl.addContent( xmlCreator.createElement( "configurationProperties", "configurationProperty", configurationProperties ) );

        return doc;
    }


    public void setSystemProperties( Properties systemProperties )
    {
        this.systemProperties = systemProperties;
    }

    public void setDatasourceProperties( Properties datasourceProperties )
    {
        this.datasourceProperties = datasourceProperties;
    }

    public void setConfigurationProperties( Map<Object, Object> configurationProperties )
    {
        this.configurationProperties = configurationProperties;
    }
}
