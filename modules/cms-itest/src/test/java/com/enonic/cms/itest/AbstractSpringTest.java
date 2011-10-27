package com.enonic.cms.itest;

import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:com/enonic/cms/itest/base-core-test-context.xml")
@TransactionConfiguration(defaultRollback = true)
@DirtiesContext
@Transactional
public abstract class AbstractSpringTest
{
}
