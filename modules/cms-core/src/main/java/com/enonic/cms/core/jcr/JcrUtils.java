package com.enonic.cms.core.jcr;

import java.io.ByteArrayOutputStream;

import javax.jcr.Session;

import org.jdom.Document;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.jcr.wrapper.JcrSession;
import com.enonic.cms.core.jcr.wrapper.JcrWrappers;

public final class JcrUtils
{

    public static String sessionViewToXml( JcrSession jcrSession, String absolutePath, boolean prettyPrint )
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try
        {
            Session session = JcrWrappers.unwrap( jcrSession );
            session.exportSystemView( absolutePath, out, true, false );
            String result = out.toString();
            if ( prettyPrint )
            {
                Document xmlDoc = JDOMUtil.parseDocument( result );
                result = JDOMUtil.prettyPrintDocument( xmlDoc );
            }
            return result;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

}
