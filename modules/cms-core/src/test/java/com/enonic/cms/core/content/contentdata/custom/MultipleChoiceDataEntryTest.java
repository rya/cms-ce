/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.MultipleChoiceDataEntryConfig;

import static org.junit.Assert.*;

public class MultipleChoiceDataEntryTest
    extends AbstractEqualsTest
{
    private MultipleChoiceDataEntry oneAlternativeTestEntry;

    private MultipleChoiceDataEntry yesNoTestEntry;

    private MultipleChoiceDataEntry yesNoTestEntry2;

    private MultipleChoiceDataEntry yesNoTestEntry3;

    private MultipleChoiceDataEntry nullTextTestEntry;

    private MultipleChoiceDataEntry blankTextTestEntry;

    private MultipleChoiceDataEntry namesTestEntry;

    private MultipleChoiceDataEntry noListTestEntry;

    private MultipleChoiceDataEntry emptyTextEntry;

    private TextDataEntry textTextDataEntry;

    @Before
    public void setUp()
    {
        DataEntryConfig requiredConfig = new MultipleChoiceDataEntryConfig( "question1", true, "Required question", "result/question1" );
        DataEntryConfig nonRequiredConfig =
            new MultipleChoiceDataEntryConfig( "question2", false, "Non required question", "result/question2" );

        List<MultipleChoiceAlternative> tooFewAltsSet = new ArrayList<MultipleChoiceAlternative>();
        tooFewAltsSet.add( new MultipleChoiceAlternative( "Ja", true ) );

        List<MultipleChoiceAlternative> yesNoAltSet = new ArrayList<MultipleChoiceAlternative>();
        yesNoAltSet.add( new MultipleChoiceAlternative( "Ja", true ) );
        yesNoAltSet.add( new MultipleChoiceAlternative( "Nei", false ) );

        List<MultipleChoiceAlternative> yesNoAltSet2 = new ArrayList<MultipleChoiceAlternative>();
        yesNoAltSet2.add( new MultipleChoiceAlternative( "Ja", true ) );
        yesNoAltSet2.add( new MultipleChoiceAlternative( "Nei", false ) );

        List<MultipleChoiceAlternative> oneToFourSet = new ArrayList<MultipleChoiceAlternative>();
        oneToFourSet.add( new MultipleChoiceAlternative( "1", false ) );
        oneToFourSet.add( new MultipleChoiceAlternative( "2", true ) );
        oneToFourSet.add( new MultipleChoiceAlternative( "3", false ) );
        oneToFourSet.add( new MultipleChoiceAlternative( "4", true ) );

        List<MultipleChoiceAlternative> namesSet = new ArrayList<MultipleChoiceAlternative>();
        namesSet.add( new MultipleChoiceAlternative( "Lars", true ) );
        namesSet.add( new MultipleChoiceAlternative( "Trond", false ) );

        oneAlternativeTestEntry = new MultipleChoiceDataEntry( nonRequiredConfig, "Har du testet?", tooFewAltsSet );
        yesNoTestEntry = new MultipleChoiceDataEntry( nonRequiredConfig, "Har du testet?", yesNoAltSet );
        yesNoTestEntry2 = new MultipleChoiceDataEntry( nonRequiredConfig, "Har du testet?", yesNoAltSet2 );
        yesNoTestEntry3 = new MultipleChoiceDataEntry( nonRequiredConfig, "Har du testet?", yesNoAltSet );
        nullTextTestEntry = new MultipleChoiceDataEntry( nonRequiredConfig, null, yesNoAltSet );
        blankTextTestEntry = new MultipleChoiceDataEntry( nonRequiredConfig, " ", oneToFourSet );
        namesTestEntry = new MultipleChoiceDataEntry( requiredConfig, "Har du testet?", namesSet );
        noListTestEntry = new MultipleChoiceDataEntry( nonRequiredConfig, "Har du testet?", null );
        emptyTextEntry = new MultipleChoiceDataEntry( nonRequiredConfig, null, null );

        textTextDataEntry = new TextDataEntry( requiredConfig, "Har du testet?" );
    }

    @Test
    public void testBreaksRequiredContract()
    {
        assertTrue( "testBreaksRequiredContract(): Needs at least 2 alternatives.", oneAlternativeTestEntry.breaksRequiredContract() );
        assertFalse( "testBreaksRequiredContract(): 2 alternatives should be enough.", yesNoTestEntry.breaksRequiredContract() );
        assertTrue( "testBreaksRequiredContract(): Text cannot be null", nullTextTestEntry.breaksRequiredContract() );
        assertTrue( "testBreaksRequiredContract(): Text cannot be blank", blankTextTestEntry.breaksRequiredContract() );
    }

    @Test
    public void testHasValue()
    {
        assertTrue( "testHasValue(): Normal entry should have value.", yesNoTestEntry.hasValue() );
        assertTrue( "testHasValue(): Entry without text should still have a value", nullTextTestEntry.hasValue() );
        assertTrue( "testHasValue(): Entry without alternatives should still have a value", noListTestEntry.hasValue() );
        assertFalse( "testHasValue(): Entry without text or alternatives should not have a value", emptyTextEntry.hasValue() );
    }

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        return yesNoTestEntry;
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        return new Object[]{oneAlternativeTestEntry, nullTextTestEntry, blankTextTestEntry, textTextDataEntry, namesTestEntry};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        return yesNoTestEntry2;
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        return yesNoTestEntry3;
    }
}