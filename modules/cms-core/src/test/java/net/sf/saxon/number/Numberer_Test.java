package net.sf.saxon.number;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.Assert;
import junit.framework.TestCase;

public class Numberer_Test
    extends TestCase
{
    enum State
    {
        exist,
        not_exist
    }

    /* must exist in application (but it is empty) */
    public void testFormatDate_cn()
        throws Exception
    {
        checkLanguage( "cn", State.exist );
    }

    /* must exist in saxon */
    public void testFormatDate_de()
        throws Exception
    {
        checkLanguage( "de", State.exist );
    }

    /* must not exist */
    public void testFormatDate_es()
        throws Exception
    {
        checkLanguage( "es", State.not_exist );
    }

    /* must exist in application */
    public void testFormatDate_no()
        throws Exception
    {
        checkLanguage( "no", State.exist );
    }

    /* must exist in application */
    public void testFormatDate_ru()
        throws Exception
    {
        checkLanguage( "ru", State.exist );
    }

    private void checkLanguage( final String language, final State exist )
        throws Exception
    {
        final String name = String.format( "/net/sf/saxon/number/format-date-%s.xsl", language );
        final InputStream resource = getClass().getResourceAsStream( name );

        final StreamSource xslStreamSource = new StreamSource( resource );
        final StreamSource xmlStreamSource = new StreamSource( new StringReader( "<xml/>" ) );

        final TransformerFactory tFactory = TransformerFactory.newInstance();
        final Transformer transformer = tFactory.newTransformer( xslStreamSource );

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final StreamResult streamResult = new StreamResult( outputStream );

        transformer.transform( xmlStreamSource, streamResult );

        if ( exist == State.exist )
        {
            Assert.assertFalse( "contains [Language: en] in the date",
                                new String( outputStream.toByteArray() ).contains( "[Language: en]" ) );
        }
        else
        {
            Assert.assertTrue( "not contains [Language: en] in the date",
                               new String( outputStream.toByteArray() ).contains( "[Language: en]" ) );
        }
    }

}
