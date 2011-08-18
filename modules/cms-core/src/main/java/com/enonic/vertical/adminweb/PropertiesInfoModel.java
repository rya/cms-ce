package com.enonic.vertical.adminweb;

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
    /*
       Directories

       Home = /home/cms-46ce-unstable-enonic/enonic-cms/cms.home
       Config = /home/cms-46ce-unstable-enonic/enonic-cms/cms.home/config

       Config files

       cms.properties (download)
       site-43.properties (download)
       vhost.properties (download)

       DataSource Info

       databaseProductName = PostgreSQL
       databaseProductVersion = 8.4.4
       driverName = PostgreSQL Native Driver
       driverVersion = PostgreSQL 8.2 JDBC3 with SSL (build 510)
       JDBCMajorVersion = 3
       JDBCMinorVersion = 0
       transactionIsolation = TRANSACTION_READ_COMMITTED
       url = jdbc:postgresql://127.0.0.1:5432/cms-46ce-unstable-enonic

       Configuration Properties

        */

    private String homeDirPath;

    private String configDirPath;

    private Properties systemProperties;

    private Properties datasourceProperties;

    private Properties configurationProperties;

    private static final String ROOT_XML_NAME = "model";

    public Document toXML()
    {
        PropertiesXmlCreator xmlCreator = new PropertiesXmlCreator();

        Element modelEl = new org.jdom.Element( ROOT_XML_NAME );

        Document doc = new Document( modelEl );
        modelEl.addContent( xmlCreator.createPropertiesElement( "systemProperties", "systemProperty", systemProperties ) );
        modelEl.addContent( xmlCreator.createPropertiesElement( "datasourceProperties", "datasourceProperty", datasourceProperties ) );
        modelEl.addContent(
            xmlCreator.createPropertiesElement( "configurationProperties", "configurationProperty", configurationProperties ) );

        return doc;
    }


    public void setHomeDirPath( String homeDirPath )
    {
        this.homeDirPath = homeDirPath;
    }

    public void setConfigDirPath( String configDirPath )
    {
        this.configDirPath = configDirPath;
    }

    public void setSystemProperties( Properties systemProperties )
    {
        this.systemProperties = systemProperties;
    }

    public void setDatasourceProperties( Properties datasourceProperties )
    {
        this.datasourceProperties = datasourceProperties;
    }

    public void setConfigurationProperties( Properties configurationProperties )
    {
        this.configurationProperties = configurationProperties;
    }
}
