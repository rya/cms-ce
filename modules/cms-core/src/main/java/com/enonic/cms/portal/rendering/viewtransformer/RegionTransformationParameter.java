/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.viewtransformer;

import java.io.IOException;
import java.util.Map;

import com.enonic.cms.core.structure.page.RegionOrigin;
import com.enonic.cms.portal.instruction.PostProcessInstructionSerializer;
import com.enonic.cms.portal.instruction.RenderWindowInstruction;
import com.enonic.cms.core.structure.page.Region;
import com.enonic.cms.core.structure.page.Window;


public final class RegionTransformationParameter
    extends AbstractTransformationParameter
    implements TransformationParameter
{
    private Region region;

    private Map<String, Object> queryParams;

    private boolean inPage;

    public RegionTransformationParameter( Region region )
    {
        super( region.getName(), resolveOrigin( region ) );
        this.region = region;
    }

    private static TransformationParameterOrigin resolveOrigin( Region region )
    {
        if ( region.getOrigin().equals( RegionOrigin.PAGE ) )
        {
            return TransformationParameterOrigin.PAGE;
        }
        else if ( region.getOrigin().equals( RegionOrigin.PAGETEMPLATE ) )
        {
            return TransformationParameterOrigin.PAGETEMPLATE;
        }

        throw new IllegalArgumentException( "Unsupported region origin: " + region.getOrigin() );
    }

    public boolean isInPage()
    {
        return inPage;
    }

    public void setInPage( boolean inPage )
    {
        this.inPage = inPage;
    }

    public void setQueryParams( Map<String, Object> queryParams )
    {
        this.queryParams = queryParams;
    }

    public String getSeparator()
    {
        return region.getSeparator();
    }

    public Map<String, Object> getQueryParams()
    {
        return queryParams;
    }

    public Object getValue()
    {
        return generateWindowPlaceholdersInRegion();
    }

    private String generateWindowPlaceholdersInRegion()
    {
        StringBuffer result = new StringBuffer();

        for ( Window window : region.getWindows() )
        {
            RenderWindowInstruction instruction = new RenderWindowInstruction();
            instruction.setPortletWindowKey( window.getKey().asString() );
            //instruction.setParams( getQueryParams() );

            try
            {
                result.append( PostProcessInstructionSerializer.serialize(instruction) );
            }
            catch ( IOException e )
            {
            }
        }

        return result.toString();
    }

}
