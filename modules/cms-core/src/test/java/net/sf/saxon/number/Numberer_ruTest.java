package net.sf.saxon.number;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import junit.framework.Assert;
import junit.framework.TestCase;

public class Numberer_ruTest
        extends TestCase
{

    public void test_verify_java_source_is_encoded_as_utf8 ()
    {
         assertEquals( "\u0439", "й" );
    }

    public void testToWords()
    {
        Numberer_ru numberer = new Numberer_ru();

        Assert.assertEquals( decode( "" ), numberer.toWords( 0 ) );
        Assert.assertEquals( decode( "один" ), numberer.toWords( 1 ) );
        Assert.assertEquals( decode( "два" ), numberer.toWords( 2 ) );
        Assert.assertEquals( decode( "три" ), numberer.toWords( 3 ) );
        Assert.assertEquals( decode( "четыре" ), numberer.toWords( 4 ) );
        Assert.assertEquals( decode( "десять" ), numberer.toWords( 10 ) );
        Assert.assertEquals( decode( "одиннадцать" ), numberer.toWords( 11 ) );
        Assert.assertEquals( decode( "двенадцать" ), numberer.toWords( 12 ) );
        Assert.assertEquals( decode( "тринадцать" ), numberer.toWords( 13 ) );
        Assert.assertEquals( decode( "четырнадцать" ), numberer.toWords( 14 ) );

        Assert.assertEquals( decode( "двадцать" ), numberer.toWords( 20 ) );
        Assert.assertEquals( decode( "двадцать один" ), numberer.toWords( 21 ) );
        Assert.assertEquals( decode( "двадцать два" ), numberer.toWords( 22 ) );
        Assert.assertEquals( decode( "двадцать три" ), numberer.toWords( 23 ) );
        Assert.assertEquals( decode( "двадцать четыре" ), numberer.toWords( 24 ) );

        Assert.assertEquals( decode( "сто" ), numberer.toWords( 100 ) );
        Assert.assertEquals( decode( "двести" ), numberer.toWords( 200 ) );

        Assert.assertEquals( decode( "сто двадцать" ), numberer.toWords( 120 ) );
        Assert.assertEquals( decode( "сто двадцать один" ), numberer.toWords( 121 ) );
        Assert.assertEquals( decode( "сто двадцать два" ), numberer.toWords( 122 ) );
        Assert.assertEquals( decode( "сто двадцать три" ), numberer.toWords( 123 ) );
        Assert.assertEquals( decode( "сто двадцать четыре" ), numberer.toWords( 124 ) );

        Assert.assertEquals( decode( "одна тысяча" ), numberer.toWords( 1000 ) );
        Assert.assertEquals( decode( "одна тысяча один" ), numberer.toWords( 1001 ) );
        Assert.assertEquals( decode( "одна тысяча сто двадцать" ), numberer.toWords( 1120 ) );
        Assert.assertEquals( decode( "две тысячи" ), numberer.toWords( 2000 ) );

        Assert.assertEquals( decode( "две тысячи сто двадцать" ), numberer.toWords( 2120 ) );
        Assert.assertEquals( decode( "две тысячи сто двадцать один" ), numberer.toWords( 2121 ) );
        Assert.assertEquals( decode( "две тысячи сто двадцать два" ), numberer.toWords( 2122 ) );
        Assert.assertEquals( decode( "две тысячи сто двадцать три" ), numberer.toWords( 2123 ) );
        Assert.assertEquals( decode( "две тысячи сто двадцать четыре" ), numberer.toWords( 2124 ) );

        Assert.assertEquals( decode( "две тысячи двадцать" ), numberer.toWords( 2020 ) );
        Assert.assertEquals( decode( "две тысячи двадцать один" ), numberer.toWords( 2021 ) );
        Assert.assertEquals( decode( "две тысячи двадцать два" ), numberer.toWords( 2022 ) );
        Assert.assertEquals( decode( "две тысячи двадцать три" ), numberer.toWords( 2023 ) );
        Assert.assertEquals( decode( "две тысячи двадцать четыре" ), numberer.toWords( 2024 ) );

        Assert.assertEquals( decode( "десять" ), numberer.toWords( 10 ) );
        Assert.assertEquals( decode( "сто" ), numberer.toWords( 100 ) );
        Assert.assertEquals( decode( "одна тысяча" ), numberer.toWords( 1000 ) );
        Assert.assertEquals( decode( "десять тысяч" ), numberer.toWords( 10000 ) );
        Assert.assertEquals( decode( "сто тысяч" ), numberer.toWords( 100000 ) );
        Assert.assertEquals( decode( "один миллион" ), numberer.toWords( 1000000 ) );
        Assert.assertEquals( decode( "десять миллионов" ), numberer.toWords( 10000000 ) );
        Assert.assertEquals( decode( "сто миллионов" ), numberer.toWords( 100000000 ) );
        Assert.assertEquals( decode( "один миллиард" ), numberer.toWords( 1000000000 ) );
        Assert.assertEquals( decode( "десять миллиардов" ), numberer.toWords( 10000000000L ) );
        Assert.assertEquals( decode( "сто миллиардов" ), numberer.toWords( 100000000000L ) );

        Assert.assertEquals( decode( "двадцать" ), numberer.toWords( 20 ) );
        Assert.assertEquals( decode( "двести" ), numberer.toWords( 200 ) );
        Assert.assertEquals( decode( "две тысячи" ), numberer.toWords( 2000 ) );
        Assert.assertEquals( decode( "двадцать тысяч" ), numberer.toWords( 20000 ) );
        Assert.assertEquals( decode( "двести тысяч" ), numberer.toWords( 200000 ) );
        Assert.assertEquals( decode( "два миллиона" ), numberer.toWords( 2000000 ) );
        Assert.assertEquals( decode( "двадцать миллионов" ), numberer.toWords( 20000000 ) );
        Assert.assertEquals( decode( "двести миллионов" ), numberer.toWords( 200000000 ) );
        Assert.assertEquals( decode( "два миллиарда" ), numberer.toWords( 2000000000 ) );
        Assert.assertEquals( decode( "двадцать миллиардов" ), numberer.toWords( 20000000000L ) );
        Assert.assertEquals( decode( "двести миллиардов" ), numberer.toWords( 200000000000L ) );
        Assert.assertEquals(
                decode( "четыреста двенадцать миллиардов четыреста двенадцать миллионов четыреста двенадцать тысяч четыреста двенадцать" ),
                numberer.toWords( 412412412412L ) );
        Assert.assertEquals( decode( "два миллиарда два" ), numberer.toWords( 2000000002 ) );

        Assert.assertEquals( decode( "один миллиард сто одиннадцать миллионов сто одиннадцать тысяч сто одиннадцать" ),
                             numberer.toWords( 1111111111 ) );
        Assert.assertEquals( decode( "сто один миллиард сто один миллион сто одна тысяча сто один" ),
                             numberer.toWords( 101101101101L ) );
        Assert.assertEquals(
                decode( "сто одиннадцать миллиардов сто одиннадцать миллионов сто одиннадцать тысяч сто одиннадцать" ),
                numberer.toWords( 111111111111L ) );
        Assert.assertEquals( decode( "сто двадцать миллиардов сто двадцать миллионов сто двадцать тысяч сто двадцать" ),
                             numberer.toWords( 120120120120L ) );
        Assert.assertEquals(
                decode( "сто двадцать один миллиард сто двадцать один миллион сто двадцать одна тысяча сто двадцать один" ),
                numberer.toWords( 121121121121L ) );
        Assert.assertEquals(
                decode( "двести двадцать два миллиарда двести двадцать два миллиона двести двадцать две тысячи двести двадцать два" ),
                numberer.toWords( 222222222222L ) );
        Assert.assertEquals( decode( "четыреста одна тысяча пятьсот пятьдесят пять" ), numberer.toWords( 401555 ) );
        Assert.assertEquals( decode( "четыреста шестьдесят одна тысяча пятьсот пятьдесят пять" ),
                             numberer.toWords( 461555 ) );
        Assert.assertEquals( decode( "четыреста шестьдесят три тысячи пятьсот пятьдесят пять" ),
                             numberer.toWords( 463555 ) );
        Assert.assertEquals( decode( "четыреста две тысячи пятьсот пятьдесят пять" ), numberer.toWords( 402555 ) );
        Assert.assertEquals( decode( "четыреста шестьдесят две тысячи пятьсот пятьдесят пять" ),
                             numberer.toWords( 462555 ) );
        Assert.assertEquals( decode( "двадцать две тысячи" ), numberer.toWords( 22000 ) );
        Assert.assertEquals( decode( "двести двадцать две тысячи" ), numberer.toWords( 222000 ) );
        Assert.assertEquals( decode( "четыреста одна тысяча сто один" ), numberer.toWords( 401101 ) );
        Assert.assertEquals( decode( "четыреста сорок одна тысяча сто один" ), numberer.toWords( 441101 ) );
        Assert.assertEquals( decode( "четыреста двадцать две тысячи сто один" ), numberer.toWords( 422101 ) );
        Assert.assertEquals( decode( "сорок одна тысяча сто один" ), numberer.toWords( 41101 ) );
        Assert.assertEquals(
                decode( "триста тридцать три миллиарда триста тридцать три миллиона триста тридцать три тысячи триста тридцать три" ),
                numberer.toWords( 333333333333L ) );
    }

    public void testSpecWords1()
    {
        Numberer_ru numberer = new Numberer_ru();
        Assert.assertEquals( decode( "первый" ), numberer.toOrdinalWords( "", 1, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "десятый" ), numberer.toOrdinalWords( "", 10, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "сотый" ), numberer.toOrdinalWords( "", 100, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "однатысячный" ), numberer.toOrdinalWords( "", 1000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "десятитысячный" ),
                             numberer.toOrdinalWords( "", 10000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "стотысячный" ),
                             numberer.toOrdinalWords( "", 100000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "однамиллионный" ),
                             numberer.toOrdinalWords( "", 1000000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "десятимиллионный" ),
                             numberer.toOrdinalWords( "", 10000000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "стомиллионный" ),
                             numberer.toOrdinalWords( "", 100000000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "однамиллиардный" ),
                             numberer.toOrdinalWords( "", 1000000000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "десятимиллиардный" ),
                             numberer.toOrdinalWords( "", 10000000000L, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "стомиллиардный" ),
                             numberer.toOrdinalWords( "", 100000000000L, AbstractNumberer.LOWER_CASE ) );
    }

    public void testSpecWords2()
    {
        Numberer_ru numberer = new Numberer_ru();
        Assert.assertEquals( decode( "второй" ), numberer.toOrdinalWords( "", 2, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двадцатый" ), numberer.toOrdinalWords( "", 20, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двухсотый" ), numberer.toOrdinalWords( "", 200, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двухтысячный" ),
                             numberer.toOrdinalWords( "", 2000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двадцатитысячный" ),
                             numberer.toOrdinalWords( "", 20000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двухсоттысячный" ),
                             numberer.toOrdinalWords( "", 200000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двухмиллионный" ),
                             numberer.toOrdinalWords( "", 2000000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двадцатимиллионный" ),
                             numberer.toOrdinalWords( "", 20000000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двухсотмиллионный" ),
                             numberer.toOrdinalWords( "", 200000000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двухмиллиардный" ),
                             numberer.toOrdinalWords( "", 2000000000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двадцатимиллиардный" ),
                             numberer.toOrdinalWords( "", 20000000000L, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двухсотмиллиардный" ),
                             numberer.toOrdinalWords( "", 200000000000L, AbstractNumberer.LOWER_CASE ) );
    }

    public void testSpecWords3()
    {
        Numberer_ru numberer = new Numberer_ru();
        Assert.assertEquals( decode( "третий" ), numberer.toOrdinalWords( "", 3, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "тридцатый" ), numberer.toOrdinalWords( "", 30, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "трехсотый" ), numberer.toOrdinalWords( "", 300, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "трехтысячный" ),
                             numberer.toOrdinalWords( "", 3000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "тридцатитысячный" ),
                             numberer.toOrdinalWords( "", 30000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "трехсоттысячный" ),
                             numberer.toOrdinalWords( "", 300000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "трехмиллионный" ),
                             numberer.toOrdinalWords( "", 3000000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "тридцатимиллионный" ),
                             numberer.toOrdinalWords( "", 30000000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "трехсотмиллионный" ),
                             numberer.toOrdinalWords( "", 300000000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "трехмиллиардный" ),
                             numberer.toOrdinalWords( "", 3000000000L, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "тридцатимиллиардный" ),
                             numberer.toOrdinalWords( "", 30000000000L, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "трехсотмиллиардный" ),
                             numberer.toOrdinalWords( "", 300000000000L, AbstractNumberer.LOWER_CASE ) );
    }

    public void testSpecWords11()
    {
        Numberer_ru numberer = new Numberer_ru();

        Assert.assertEquals( decode( "одиннадцатый" ), numberer.toOrdinalWords( "", 11, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "сто первый" ), numberer.toOrdinalWords( "", 101, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "сто десятый" ), numberer.toOrdinalWords( "", 110, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "одна тысяча первый" ),
                             numberer.toOrdinalWords( "", 1001, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "одна тысяча десятый" ),
                             numberer.toOrdinalWords( "", 1010, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "одна тысяча сотый" ),
                             numberer.toOrdinalWords( "", 1100, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "одна тысяча сто десятый" ),
                             numberer.toOrdinalWords( "", 1110, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "десять тысяч первый" ),
                             numberer.toOrdinalWords( "", 10001, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "десять тысяч десятый" ),
                             numberer.toOrdinalWords( "", 10010, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "десять тысяч сто десятый" ),
                             numberer.toOrdinalWords( "", 10110, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "сто тысяч десятый" ),
                             numberer.toOrdinalWords( "", 100010, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "сто одна тысяча десятый" ),
                             numberer.toOrdinalWords( "", 101010, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "сто тысяч сто десятый" ),
                             numberer.toOrdinalWords( "", 100110, AbstractNumberer.LOWER_CASE ) );
    }

    public void testSpecWords202()
    {
        Numberer_ru numberer = new Numberer_ru();

        Assert.assertEquals( decode( "двадцать второй" ),
                             numberer.toOrdinalWords( "", 22, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двести второй" ),
                             numberer.toOrdinalWords( "", 202, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двести двадцатый" ),
                             numberer.toOrdinalWords( "", 220, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "две тысячи второй" ),
                             numberer.toOrdinalWords( "", 2002, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "две тысячи двадцатый" ),
                             numberer.toOrdinalWords( "", 2020, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "две тысячи двухсотый" ),
                             numberer.toOrdinalWords( "", 2200, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "две тысячи двести двадцатый" ),
                             numberer.toOrdinalWords( "", 2220, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двадцать тысяч второй" ),
                             numberer.toOrdinalWords( "", 20002, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двадцать тысяч двадцатый" ),
                             numberer.toOrdinalWords( "", 20020, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двадцать тысяч двести двадцатый" ),
                             numberer.toOrdinalWords( "", 20220, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двести тысяч двадцатый" ),
                             numberer.toOrdinalWords( "", 200020, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двести две тысячи двадцатый" ),
                             numberer.toOrdinalWords( "", 202020, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двести две тысячи двести двадцатый" ),
                             numberer.toOrdinalWords( "", 202220, AbstractNumberer.LOWER_CASE ) );
    }

    public void testSpecWords22()
    {
        Numberer_ru numberer = new Numberer_ru();

        Assert.assertEquals( decode( "одиннадцатитысячный" ),
                             numberer.toOrdinalWords( "", 11000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "тридцатишеститысячный" ),
                             numberer.toOrdinalWords( "", 36000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "стодесятитысячный" ),
                             numberer.toOrdinalWords( "", 110000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "триста шестьдесят шесть тысяч шестисотый" ),
                             numberer.toOrdinalWords( "", 366600, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "тристатридцатишеститысячный" ),
                             numberer.toOrdinalWords( "", 336000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "три миллиона шестьсотшестидесятишеститысячный" ),
                             numberer.toOrdinalWords( "", 3666000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "один миллион стотысячный" ),
                             numberer.toOrdinalWords( "", 1100000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "один миллион стодесятитысячный" ),
                             numberer.toOrdinalWords( "", 1110000, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "один миллиард один миллион стодесятитысячный" ),
                             numberer.toOrdinalWords( "", 1001110000L, AbstractNumberer.LOWER_CASE ) );
    }

    public void testToOrdinalWords()
    {
        Numberer_ru numberer = new Numberer_ru();

        Assert.assertEquals( decode( "" ), numberer.toOrdinalWords( "", 0, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "первый" ), numberer.toOrdinalWords( "", 1, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "второй" ), numberer.toOrdinalWords( "", 2, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "третий" ), numberer.toOrdinalWords( "", 3, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "четвертый" ), numberer.toOrdinalWords( "", 4, AbstractNumberer.LOWER_CASE ) );

        Assert.assertEquals( decode( "десятый" ), numberer.toOrdinalWords( "", 10, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "одиннадцатый" ), numberer.toOrdinalWords( "", 11, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двенадцатый" ), numberer.toOrdinalWords( "", 12, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "тринадцатый" ), numberer.toOrdinalWords( "", 13, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "четырнадцатый" ),
                             numberer.toOrdinalWords( "", 14, AbstractNumberer.LOWER_CASE ) );

        Assert.assertEquals( decode( "двадцатый" ), numberer.toOrdinalWords( "", 20, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двадцать первый" ),
                             numberer.toOrdinalWords( "", 21, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двадцать второй" ),
                             numberer.toOrdinalWords( "", 22, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двадцать третий" ),
                             numberer.toOrdinalWords( "", 23, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двадцать четвертый" ),
                             numberer.toOrdinalWords( "", 24, AbstractNumberer.LOWER_CASE ) );

        Assert.assertEquals( decode( "сотый" ), numberer.toOrdinalWords( "", 100, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "двухсотый" ), numberer.toOrdinalWords( "", 200, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "девятисотый" ), numberer.toOrdinalWords( "", 900, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "сто двадцатый" ),
                             numberer.toOrdinalWords( "", 120, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "сто двадцать первый" ),
                             numberer.toOrdinalWords( "", 121, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "сто двадцать второй" ),
                             numberer.toOrdinalWords( "", 122, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "сто двадцать третий" ),
                             numberer.toOrdinalWords( "", 123, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "сто двадцать четвертый" ),
                             numberer.toOrdinalWords( "", 124, AbstractNumberer.LOWER_CASE ) );

        Assert.assertEquals( decode( "две тысячи сто двадцатый" ),
                             numberer.toOrdinalWords( "", 2120, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "две тысячи сто двадцать первый" ),
                             numberer.toOrdinalWords( "", 2121, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "две тысячи сто двадцать второй" ),
                             numberer.toOrdinalWords( "", 2122, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "две тысячи сто двадцать третий" ),
                             numberer.toOrdinalWords( "", 2123, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "две тысячи сто двадцать четвертый" ),
                             numberer.toOrdinalWords( "", 2124, AbstractNumberer.LOWER_CASE ) );

        Assert.assertEquals( decode( "две тысячи двадцатый" ),
                             numberer.toOrdinalWords( "", 2020, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "две тысячи двадцать первый" ),
                             numberer.toOrdinalWords( "", 2021, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "две тысячи двадцать второй" ),
                             numberer.toOrdinalWords( "", 2022, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "две тысячи двадцать третий" ),
                             numberer.toOrdinalWords( "", 2023, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "две тысячи двадцать четвертый" ),
                             numberer.toOrdinalWords( "", 2024, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals(
                decode( "четыреста двенадцать миллиардов четыреста двенадцать миллионов четыреста двенадцать тысяч четыреста двенадцатый" ),
                numberer.toOrdinalWords( "", 412412412412L, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "два миллиарда второй" ),
                             numberer.toOrdinalWords( "", 2000000002, AbstractNumberer.LOWER_CASE ) );

        Assert.assertEquals( decode( "один миллиард сто одиннадцать миллионов сто одиннадцать тысяч сто одиннадцатый" ),
                             numberer.toOrdinalWords( "", 1111111111, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "сто один миллиард сто один миллион сто одна тысяча сто первый" ),
                             numberer.toOrdinalWords( "", 101101101101L, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals(
                decode( "сто одиннадцать миллиардов сто одиннадцать миллионов сто одиннадцать тысяч сто одиннадцатый" ),
                numberer.toOrdinalWords( "", 111111111111L, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals(
                decode( "сто двадцать миллиардов сто двадцать миллионов сто двадцать тысяч сто двадцатый" ),
                numberer.toOrdinalWords( "", 120120120120L, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals(
                decode( "сто двадцать один миллиард сто двадцать один миллион сто двадцать одна тысяча сто двадцать первый" ),
                numberer.toOrdinalWords( "", 121121121121L, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals(
                decode( "двести двадцать два миллиарда двести двадцать два миллиона двести двадцать две тысячи двести двадцать второй" ),
                numberer.toOrdinalWords( "", 222222222222L, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "четыреста одна тысяча пятьсот пятьдесят пятый" ),
                             numberer.toOrdinalWords( "", 401555, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "четыреста шестьдесят одна тысяча пятьсот пятьдесят пятый" ),
                             numberer.toOrdinalWords( "", 461555, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "четыреста шестьдесят три тысячи пятьсот пятьдесят пятый" ),
                             numberer.toOrdinalWords( "", 463555, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "четыреста две тысячи пятьсот пятьдесят пятый" ),
                             numberer.toOrdinalWords( "", 402555, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "четыреста шестьдесят две тысячи пятьсот пятьдесят пятый" ),
                             numberer.toOrdinalWords( "", 462555, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "четыреста одна тысяча сто первый" ),
                             numberer.toOrdinalWords( "", 401101, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "четыреста сорок одна тысяча сто первый" ),
                             numberer.toOrdinalWords( "", 441101, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "четыреста двадцать две тысячи сто первый" ),
                             numberer.toOrdinalWords( "", 422101, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals( decode( "сорок одна тысяча сто первый" ),
                             numberer.toOrdinalWords( "", 41101, AbstractNumberer.LOWER_CASE ) );
        Assert.assertEquals(
                decode( "триста тридцать три миллиарда триста тридцать три миллиона триста тридцать три тысячи триста тридцать третий" ),
                numberer.toOrdinalWords( "", 333333333333L, AbstractNumberer.LOWER_CASE ) );
    }

    public void testDayName()
    {
        Numberer_ru numberer = new Numberer_ru();

        Assert.assertEquals( decode( "понедельник" ), numberer.dayName( 1, 0, 20 ) );
        Assert.assertEquals( decode( "вторник" ), numberer.dayName( 2, 0, 20 ) );
        Assert.assertEquals( decode( "среда" ), numberer.dayName( 3, 0, 20 ) );
        Assert.assertEquals( decode( "четверг" ), numberer.dayName( 4, 0, 20 ) );
        Assert.assertEquals( decode( "пятница" ), numberer.dayName( 5, 0, 20 ) );
        Assert.assertEquals( decode( "суббота" ), numberer.dayName( 6, 0, 20 ) );
        Assert.assertEquals( decode( "воскресенье" ), numberer.dayName( 7, 0, 20 ) );

        Assert.assertEquals( decode( "пн" ), numberer.dayName( 1, 0, 3 ) );
        Assert.assertEquals( decode( "вт" ), numberer.dayName( 2, 0, 3 ) );
        Assert.assertEquals( decode( "ср" ), numberer.dayName( 3, 0, 3 ) );
        Assert.assertEquals( decode( "чт" ), numberer.dayName( 4, 0, 3 ) );
        Assert.assertEquals( decode( "пт" ), numberer.dayName( 5, 0, 3 ) );
        Assert.assertEquals( decode( "сб" ), numberer.dayName( 6, 0, 3 ) );
        Assert.assertEquals( decode( "вс" ), numberer.dayName( 7, 0, 3 ) );

        Assert.assertEquals( decode( "пн" ), numberer.dayName( 1, 0, 2 ) );
        Assert.assertEquals( decode( "вт" ), numberer.dayName( 2, 0, 2 ) );
        Assert.assertEquals( decode( "ср" ), numberer.dayName( 3, 0, 2 ) );
        Assert.assertEquals( decode( "чт" ), numberer.dayName( 4, 0, 2 ) );
        Assert.assertEquals( decode( "пт" ), numberer.dayName( 5, 0, 2 ) );
        Assert.assertEquals( decode( "сб" ), numberer.dayName( 6, 0, 2 ) );
        Assert.assertEquals( decode( "вс" ), numberer.dayName( 7, 0, 2 ) );
    }

    public void testMonthName()
    {
        Numberer_ru numberer = new Numberer_ru();

        Assert.assertEquals( decode( "января" ), numberer.monthName( 1, 0, 20 ) );
        Assert.assertEquals( decode( "февраля" ), numberer.monthName( 2, 0, 20 ) );
        Assert.assertEquals( decode( "марта" ), numberer.monthName( 3, 0, 20 ) );
        Assert.assertEquals( decode( "апреля" ), numberer.monthName( 4, 0, 20 ) );
        Assert.assertEquals( decode( "мая" ), numberer.monthName( 5, 0, 20 ) );
        Assert.assertEquals( decode( "июня" ), numberer.monthName( 6, 0, 20 ) );
        Assert.assertEquals( decode( "июля" ), numberer.monthName( 7, 0, 20 ) );
        Assert.assertEquals( decode( "августа" ), numberer.monthName( 8, 0, 20 ) );
        Assert.assertEquals( decode( "сентября" ), numberer.monthName( 9, 0, 20 ) );
        Assert.assertEquals( decode( "октября" ), numberer.monthName( 10, 0, 20 ) );
        Assert.assertEquals( decode( "ноября" ), numberer.monthName( 11, 0, 20 ) );
        Assert.assertEquals( decode( "декабря" ), numberer.monthName( 12, 0, 20 ) );

        Assert.assertEquals( decode( "янв" ), numberer.monthName( 1, 0, 3 ) );
        Assert.assertEquals( decode( "фев" ), numberer.monthName( 2, 0, 3 ) );
        Assert.assertEquals( decode( "мар" ), numberer.monthName( 3, 0, 3 ) );
        Assert.assertEquals( decode( "апр" ), numberer.monthName( 4, 0, 3 ) );
        Assert.assertEquals( decode( "мая" ), numberer.monthName( 5, 0, 3 ) );
        Assert.assertEquals( decode( "июн" ), numberer.monthName( 6, 0, 3 ) );
        Assert.assertEquals( decode( "июл" ), numberer.monthName( 7, 0, 3 ) );
        Assert.assertEquals( decode( "авг" ), numberer.monthName( 8, 0, 3 ) );
        Assert.assertEquals( decode( "сен" ), numberer.monthName( 9, 0, 3 ) );
        Assert.assertEquals( decode( "окт" ), numberer.monthName( 10, 0, 3 ) );
        Assert.assertEquals( decode( "ноя" ), numberer.monthName( 11, 0, 3 ) );
        Assert.assertEquals( decode( "дек" ), numberer.monthName( 12, 0, 3 ) );
    }

    private String decode( String s )
    {
        try
        {
            return URLDecoder.decode( s, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( e );
        }
    }
}
