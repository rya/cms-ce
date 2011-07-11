package com.enonic.cms.admin.country;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: vfiodarau
 * Date: 7/11/11
 * Time: 11:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class CallingCodesModel
{

    public List<CallingCodeModel> codes;

    public List<CallingCodeModel> getCodes()
    {
        return codes;
    }

    public void setCodes( List<CallingCodeModel> codes )
    {
        this.codes = codes;
    }


}
