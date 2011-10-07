package com.enonic.cms.core.preference;

import com.enonic.cms.core.security.user.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;

public class PreferenceAccessResolverTest
{
    private PreferenceAccessResolver resolver;

    @Before
    public void setUp()
    {
        this.resolver = new PreferenceAccessResolver();
    }

    @Test
    public void testHasReadAccess()
    {
        final UserEntity user = Mockito.mock(UserEntity.class);

        Mockito.when(user.isAnonymous()).thenReturn(true);
        assertFalse(this.resolver.hasReadAccess(user));

        Mockito.when(user.isAnonymous()).thenReturn(false);
        assertTrue(this.resolver.hasReadAccess(user));
    }

    @Test
    public void testHasWriteAccess()
    {
        final UserEntity user = Mockito.mock(UserEntity.class);

        Mockito.when(user.isAnonymous()).thenReturn(true);
        assertFalse(this.resolver.hasWriteAccess(user));

        Mockito.when(user.isAnonymous()).thenReturn(false);
        assertTrue(this.resolver.hasWriteAccess(user));
    }
}
