package com.enonic.cms.core.tools.rpc;

import com.enonic.cms.api.client.LocalClient;
import com.enonic.cms.api.client.binrpc.BinRpcInvocation;
import com.enonic.cms.api.client.binrpc.BinRpcInvocationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;

@Component
@Path("/rpc/bin")
@Produces("application/x-java-serialized-object")
public class BinRpcResource
{
    private LocalClient client;

    @Autowired
    public void setLocalClient(final LocalClient client)
    {
        this.client = client;
    }

    @POST
    public StreamingOutput handle(final InputStream in)
        throws Exception
    {
        final BinRpcInvocation invocation = readInvocation(in);
        final BinRpcInvocationResult result = invokeAndCreateResult(invocation);
        return writeInvocationResult(result);
    }

    private BinRpcInvocation readInvocation(final InputStream in)
        throws IOException, ClassNotFoundException
    {
        final ObjectInputStream ois = new ObjectInputStream( in );

        try
        {
            final Object obj = ois.readObject();
            if ( !( obj instanceof BinRpcInvocation ) )
            {
                throw new IOException("Deserialized object needs to be assignable to type ["
                        + BinRpcInvocation.class.getName() + "]: " + obj );
            }
            else
            {
                return (BinRpcInvocation) obj;
            }
        }
        finally
        {
            ois.close();
        }
    }

    private BinRpcInvocationResult invokeAndCreateResult(final BinRpcInvocation invocation)
    {
        try
        {
            final Object value = invocation.invoke( this.client );
            return new BinRpcInvocationResult( value );
        }
        catch ( final Throwable ex )
        {
            return new BinRpcInvocationResult( ex );
        }
    }

    private StreamingOutput writeInvocationResult(final BinRpcInvocationResult result)
        throws IOException
    {
        return new StreamingOutput()
        {
            public void write(final OutputStream out)
                throws IOException, WebApplicationException
            {
                ObjectOutputStream oos = new ObjectOutputStream( out );

                try
                {
                    oos.writeObject( result );
                    oos.flush();
                }
                finally
                {
                    oos.close();
                }
            }
        };
    }
}
