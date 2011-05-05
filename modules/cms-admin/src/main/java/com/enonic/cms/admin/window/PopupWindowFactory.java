package com.enonic.cms.admin.window;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Window;

import com.enonic.cms.admin.image.EmbeddedImageFactory;
import com.enonic.cms.admin.spring.VaadinComponent;
import com.enonic.cms.core.security.user.UserEntity;

/**
 * Created by IntelliJ IDEA.
 * User: vfiodarau
 * Date: 5/4/11
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */
@VaadinComponent
public class PopupWindowFactory
{

    @Autowired
    private EmbeddedImageFactory imageFactory;

    public Window createDeleteWindow( UserEntity user )
    {
        return new DeleteUserWindow( user, imageFactory );
    }

    public Window createChangePwdWindow( UserEntity user){
        return new ChangePasswordUserWindow(user, imageFactory);
    }

}
