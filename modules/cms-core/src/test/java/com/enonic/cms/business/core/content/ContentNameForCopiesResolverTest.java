package com.enonic.cms.business.core.content;

import org.mockito.Mockito;

import junit.framework.TestCase;

import com.enonic.cms.store.dao.ContentEntityDao;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.category.CategoryEntity;

public class ContentNameForCopiesResolverTest
        extends TestCase
{
    private final CategoryEntity category = new CategoryEntity();

    private ContentEntityDao contentEntityDao;
    private ContentNameForCopiesResolver resolver;


    @Override
    public void setUp()
            throws Exception
    {
        // re-init contentEntityDao each time
        contentEntityDao = Mockito.mock( ContentEntityDao.class );
        resolver = new ContentNameForCopiesResolver(contentEntityDao);
    }

    private ContentEntity createContent( String name )
    {
        ContentEntity contentEntity = new ContentEntity();
        contentEntity.setCategory( category );
        contentEntity.setName( name );
        return contentEntity;
    }

    public void testFindUniqueNameInCategory_negativeCounter()
            throws Exception
    {
        assertEquals( resolver.findUniqueNameInCategory( createContent("Mypage(-2)")), "Mypage(1)" );
        assertEquals( resolver.findUniqueNameInCategory( createContent("Mypage(-1)")), "Mypage(1)" );
        assertEquals( resolver.findUniqueNameInCategory( createContent("Mypage(0)")), "Mypage(1)" );
    }


    public void testFindUniqueNameInCategory_emptyCategory()
            throws Exception
    {
        assertEquals( resolver.findUniqueNameInCategory( createContent("Mypage")), "Mypage(1)" );
        assertEquals( resolver.findUniqueNameInCategory( createContent("Mypage(hello)")), "Mypage(hello)(1)" );
        assertEquals( resolver.findUniqueNameInCategory( createContent("Mypage)")), "Mypage)(1)" );
        assertEquals( resolver.findUniqueNameInCategory( createContent("Mypage(")), "Mypage((1)" );
        assertEquals( resolver.findUniqueNameInCategory( createContent("Mypage()")), "Mypage()(1)" );
    }

    public void testFindUniqueNameInCategory_1()
            throws Exception
    {
        Mockito.when( contentEntityDao.checkNameExists( category, "Mypage"  )).thenReturn( true );
        Mockito.when( contentEntityDao.checkNameExists( category, "Mypage(1)" )).thenReturn(true );

        assertEquals( resolver.findUniqueNameInCategory( createContent("Mypage")), "Mypage(2)" );
    }

    public void testFindUniqueNameInCategory_2()
            throws Exception
    {
        Mockito.when( contentEntityDao.checkNameExists( category, "Mypage" )).thenReturn( true );
        Mockito.when( contentEntityDao.checkNameExists( category, "Mypage(2)")).thenReturn(true );

        assertEquals( resolver.findUniqueNameInCategory( createContent("Mypage(2)")), "Mypage(1)" );
    }

    public void testFindUniqueNameInCategory_3()
            throws Exception
    {
        Mockito.when( contentEntityDao.checkNameExists( category, ("Mypage" )) ).thenReturn( true );
        Mockito.when( contentEntityDao.checkNameExists( category, ("Mypage(1)" ) )).thenReturn( true );
        Mockito.when( contentEntityDao.checkNameExists( category, ("Mypage(2)" ) )).thenReturn( true );

        assertEquals( resolver.findUniqueNameInCategory( createContent("Mypage")), "Mypage(3)" );
        assertEquals( resolver.findUniqueNameInCategory( createContent("Mypage(1)")), "Mypage(3)" );
        assertEquals( resolver.findUniqueNameInCategory( createContent("Mypage(2)")), "Mypage(3)" );
    }



}
