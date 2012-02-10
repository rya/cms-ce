package net.sf.saxon.number;

/**
 * Numberer class for the Russian language
 */
public class Numberer_ru
        extends AbstractNumberer
{

    private static final long serialVersionUID = 1L;

    private static String[] russianOrdinalUnits =
            {"", "первый", "второй", "третий", "четвертый", "пятый", "шестой", "седьмой", "восьмой", "девятый",
                    "десятый", "одиннадцатый", "двенадцатый", "тринадцатый", "четырнадцатый", "пятнадцатый",
                    "шестнадцатый", "семнадцатый", "восемнадцатый", "девятнадцатый"};

    private static String[] russianOrdinalTens =
            {"", "десятый", "двадцатый", "тридцатый", "сороковой", "пятидесятый", "шестидесятый", "семидесятый",
                    "восьмидесятый", "девяностый"};

    private static String[] russianOrdinalHundreds =
            {"", "сотый", "двухсотый", "трехсотый", "четырехсотый", "пятисотый", "шестисотый", "семисотый",
                    "восьмисотый", "девятисотый"};

    private static String[] russianSpecUnits =
            {"", "одна", "двух", "трех", "черырех", "пяти", "шести", "семи", "восьми", "девяти", "десяти",
                    "одиннадцати", "двенадцати", "тринадцати", "четырнадцати", "пятнадцати", "шестнадцати",
                    "семнадцати", "восемнадцати", "девятнадцати"};

    private static String[] russianSpecTens =
            {"", "десяти", "двадцати", "тридцати", "сорока", "пятидесяти", "шестидесяти", "семидесяти", "восьмидесяти",
                    "девяносто"};

    private static String[] russianSpecHundreds =
            {"", "сто", "двухсот", "трехсот", "черырехсот", "пятисот", "шестисот", "семисот", "восьмисот", "девятисот"};

    private static String[] russianUnits =
            {"", "один", "два", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять", "десять", "одиннадцать",
                    "двенадцать", "тринадцать", "четырнадцать", "пятнадцать", "шестнадцать", "семнадцать",
                    "восемнадцать", "девятнадцать"};

    private static String[] russianTens =
            {"", "десять", "двадцать", "тридцать", "сорок", "пятьдесят", "шестьдесят", "семьдесят", "восемьдесят",
                    "девяносто"};

    private static String[] russianHundreds =
            {"", "сто", "двести", "триста", "четыреста", "пятьсот", "шестьсот", "семьсот", "восемьсот", "девятьсот"};

    private static String[] russianDays =
            {"понедельник", "вторник", "среда", "четверг", "пятница", "суббота", "воскресенье"};

    private static String[] russianDayAbbreviations = {"пн", "вт", "ср", "чт", "пт", "сб", "вс"};

    private static int[] minUniqueDayLength = {2, 2, 2, 2, 2, 2, 2};

    public String toBillion( String ordinalParam, long number, int wordCase )
    {
        String graduate = "миллиардный";

        if ( number >= 100000000000L )
        {
            if ( number % 100000000000L == 0 )
            {
                long billion = number / 100000000000L;
                String result = russianSpecHundreds[(int) billion];
                result += graduate;

                return applyCase( result, wordCase );
            }
        }
        else if ( number >= 10000000000L )
        {
            if ( number % 10000000000L == 0 )
            {
                long billion = number / 10000000000L;
                String result = russianSpecTens[(int) billion];
                result += graduate;

                return applyCase( result, wordCase );
            }
        }

        if ( number % 1000000000L == 0 )
        {
            long billion = number / 1000000000L;
            String result = russianSpecUnits[(int) billion];
            result += graduate;

            return applyCase( result, wordCase );
        }

        long rem = number % 1000000000;
        long billion = number / 1000000000;

        String result = ( number / 1000000000 == 1 ? " один" : toWords( number / 1000000000 ) ) + " миллиард" +
                getEnding( (int) billion ) + " " + toOrdinalWords( ordinalParam, rem, wordCase );

        return applyCase( result, wordCase );
    }

    public String toMillion( String ordinalParam, long number, int wordCase )
    {
        String graduate = "миллионный";

        if ( number >= 100000000 )
        {
            if ( number % 100000000 == 0 )
            {
                long billion = number / 100000000;
                String result = russianSpecHundreds[(int) billion];
                result += graduate;

                return applyCase( result, wordCase );
            }
        }
        else if ( number >= 10000000 )
        {
            if ( number % 10000000 == 0 )
            {
                long billion = number / 10000000;
                String result = russianSpecTens[(int) billion];
                result += graduate;

                return applyCase( result, wordCase );
            }
        }

        if ( number % 1000000 == 0 )
        {
            long billion = number / 1000000;
            String result = russianSpecUnits[(int) billion];
            result += graduate;

            return applyCase( result, wordCase );
        }

        long rem = number % 1000000;
        long million = number / 1000000;

        String result = ( number / 1000000 == 1 ? " один" : toWords( number / 1000000 ) ) + " миллион" +
                getEnding( (int) million ) + " " + toOrdinalWords( ordinalParam, rem, wordCase );

        return applyCase( result, wordCase );
    }

    public String toThousand( String ordinalParam, long number, int wordCase )
    {
        String graduate = "тысячный";

        if ( number >= 100000 )
        {
            if ( number % 100000 == 0 )
            {
                long billion = number / 100000;
                String result = russianSpecHundreds[(int) billion];
                result += graduate;

                return applyCase( result, wordCase );
            }

            if ( number % 1000 == 0 )
            {
                long thousand = number / 1000;

                String result = russianHundreds[(int) thousand / 100];
                long centum = thousand % 100;

                result += russianSpecTens[(int) centum / 10];
                result += russianSpecUnits[(int) centum % 10];
                result += graduate;

                return applyCase( result, wordCase );
            }

            long rem = number % 1000;

            String result = getHundredThousandCount( number );
            result += getThousandExponent( number );
            result += toOrdinalWords( ordinalParam, rem, wordCase );

            return applyCase( result, wordCase );
        }
        else if ( number >= 10000 )
        {
            if ( number % 10000 == 0 )
            {
                long billion = number / 10000;
                String result = russianSpecTens[(int) billion];
                result += graduate;

                return applyCase( result, wordCase );
            }

            if ( number % 1000 == 0 )
            {
                long thousand = number / 1000;

                if ( thousand > 10 && thousand < 20 )
                {
                    String result = russianSpecUnits[(int) thousand];
                    result += graduate;

                    return applyCase( result, wordCase );
                }
                else
                {
                    String result = russianSpecTens[(int) thousand / 10];
                    result += russianSpecUnits[(int) thousand % 10];
                    result += graduate;

                    return applyCase( result, wordCase );
                }
            }

            long rem = number % 1000;

            String result = getTenThousandCount( number );
            result += getThousandExponent( number );
            result += toOrdinalWords( ordinalParam, rem, wordCase );

            return applyCase( result, wordCase );
        }

        if ( number % 1000 == 0 )
        {
            long billion = number / 1000;
            String result = russianSpecUnits[(int) billion];
            result += graduate;

            return applyCase( result, wordCase );
        }

        long rem = number % 1000;

        String result = getThousandCount( number );
        result += getThousandExponent( number );
        result += toOrdinalWords( ordinalParam, rem, wordCase );

        return applyCase( result, wordCase );
    }

    @Override
    public String toOrdinalWords( String ordinalParam, long number, int wordCase )
    {
        if ( number >= 1000000000 )
        {
            return toBillion( ordinalParam, number, wordCase );
        }
        else if ( number >= 1000000 )
        {
            return toMillion( ordinalParam, number, wordCase );
        }
        else if ( number >= 1000 )
        {
            return toThousand( ordinalParam, number, wordCase );
        }
        else if ( ( number % 100 == 0 ) && ( number / 100 < 10 ) )
        {
            return russianOrdinalHundreds[(int) number / 100];
        }
        else if ( number >= 100 )
        {
            long rem = number % 100;

            if ( number % 100 == 0 )
            {
                return russianOrdinalHundreds[(int) number / 100];
            }

            String result = russianHundreds[(int) number / 100] + " " + toOrdinalWords( ordinalParam, rem, wordCase );
            return applyCase( result, wordCase );
        }

        if ( number < 20 )
        {
            return russianOrdinalUnits[(int) number];
        }
        else if ( number % 10 == 0 )
        {
            return russianOrdinalTens[(int) number / 10];
        }

        int rem = (int) ( number % 10 );
        String result = russianTens[(int) number / 10] + " " + russianOrdinalUnits[rem];

        return applyCase( result, wordCase );
    }

    private String applyCase( String number, int wordCase )
    {
        if ( wordCase == UPPER_CASE )
        {
            return number.trim().toUpperCase();
        }
        else if ( wordCase == LOWER_CASE )
        {
            return number.trim().toLowerCase();
        }

        return number.trim();
    }

    @Override
    public String toWords( long number )
    {
        if ( number >= 1000000000 )
        {
            long rem = number % 1000000000;
            long billion = number / 1000000000;

            String result = ( number / 1000000000 == 1 ? " один" : toWords( number / 1000000000 ) ) + " миллиард" +
                    getEnding( (int) billion ) + " " + toWords( rem );

            return result.trim();
        }
        else if ( number >= 1000000 )
        {
            long rem = number % 1000000;
            long million = number / 1000000;

            String result = ( number / 1000000 == 1 ? " один" : toWords( number / 1000000 ) ) + " миллион" +
                    getEnding( (int) million ) + " " + toWords( rem );

            return result.trim();
        }
        else if ( number >= 100000 )
        {
            long rem = number % 1000;

            String result = getHundredThousandCount( number );
            result += getThousandExponent( number );
            result += toWords( rem );

            return result.trim();
        }
        else if ( number >= 10000 )
        {
            long rem = number % 1000;

            String result = getTenThousandCount( number );
            result += getThousandExponent( number );
            result += toWords( rem );

            return result.trim();
        }
        else if ( number >= 1000 )
        {
            long rem = number % 1000;

            String result = getThousandCount( number );
            result += getThousandExponent( number );
            result += toWords( rem );

            return result.trim();
        }
        else if ( number >= 100 )
        {
            long rem = number % 100;
            String result = russianHundreds[(int) number / 100] + " " + toWords( rem );

            return result.trim();
        }
        else
        {
            if ( number < 20 )
            {
                return russianUnits[(int) number];
            }
            int rem = (int) ( number % 10 );
            String result = russianTens[(int) number / 10] + " " + russianUnits[rem];

            return result.trim();
        }
    }

    private String getEnding( int number )
    {
        if ( ( number % 100 > 10 ) && ( number % 100 < 20 ) )
        {
            return "ов";
        }
        else if ( number % 10 == 1 )
        {
            return "";
        }
        else if ( ( number % 10 > 1 ) && ( number % 10 < 5 ) )
        {
            return "а";
        }

        return "ов";
    }

    private String getThousandCount( long number )
    {
        if ( number / 1000 == 1 )
        {
            return " одна";
        }
        else if ( number / 1000 == 2 )
        {
            return " две";
        }

        return toWords( number / 1000 );
    }

    private String getHundredThousandCount( long number )
    {
        long mille = number / 1000;

        if ( mille % 100 == 1 )
        {
            long hundreds = number / 100000;
            return russianHundreds[(int) hundreds] + " одна";
        }
        else if ( ( mille % 10 == 1 ) && ( mille % 100 != 11 ) )
        {
            long hundreds = number / 100000;
            long tens = ( ( number / 1000 ) % 100 ) / 10;
            return russianHundreds[(int) hundreds] + " " + russianTens[(int) tens] + " одна";
        }
        else if ( mille % 100 == 2 )
        {
            long hundreds = number / 100000;
            return russianHundreds[(int) hundreds] + " две";
        }
        else if ( ( mille % 10 == 2 ) && ( mille % 100 != 12 ) )
        {
            long hundreds = number / 100000;
            long tens = ( ( number / 1000 ) % 100 ) / 10;
            return russianHundreds[(int) hundreds] + " " + russianTens[(int) tens] + " две";
        }

        return toWords( number / 1000 );
    }

    private String getTenThousandCount( long number )
    {
        long centum = number / 1000;

        if ( centum % 10 == 1 )
        {
            long hundreds = number / 10000;
            return russianTens[(int) hundreds] + " одна";
        }
        else if ( centum % 10 == 2 )
        {
            long hundreds = number / 10000;
            return russianTens[(int) hundreds] + " две";
        }

        return toWords( number / 1000 );
    }

    private String getThousandExponent( long number )
    {
        if ( ( ( number / 1000 ) % 10 == 1 ) && ( ( number / 1000 ) % 100 < 10 ) )
        {
            return " тысяча ";
        }
        else if ( ( ( number / 1000 ) % 100 >= 10 ) && ( ( number / 1000 ) % 100 <= 20 ) )
        {
            return " тысяч ";
        }
        else if ( ( ( number / 1000 ) % 10 > 1 ) && ( ( number / 1000 ) % 10 < 5 ) )
        {
            return " тысячи ";
        }
        else if ( ( number % 100 == 0 ) || ( ( number / 1000 ) % 10 ) == 0 )
        {
            return " тысяч ";
        }

        return " тысяча ";
    }

    @Override
    public String toWords( long number, int wordCase )
    {
        String s;
        if ( number == 0 )
        {
            s = "ноль";
        }
        else
        {
            s = toWords( number );
        }
        if ( wordCase == UPPER_CASE )
        {
            return s.toUpperCase();
        }
        else if ( wordCase == LOWER_CASE )
        {
            return s.toLowerCase();
        }
        else
        {
            return s;
        }
    }

    private static String[] russianMonths =
            {"января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября",
                    "декабря"};

    @Override
    public String monthName( int month, int minWidth, int maxWidth )
    {
        String name = russianMonths[month - 1];
        if ( maxWidth < 3 )
        {
            maxWidth = 3;
        }
        if ( name.length() > maxWidth )
        {
            name = name.substring( 0, maxWidth );
        }
        while ( name.length() < minWidth )
        {
            name = name + ' ';
        }
        return name;
    }

    @Override
    public String dayName( int day, int minWidth, int maxWidth )
    {
        String name = russianDays[day - 1];
        if ( maxWidth < 2 )
        {
            maxWidth = 2;
        }
        if ( name.length() > maxWidth )
        {
            name = russianDayAbbreviations[day - 1];
            if ( name.length() > maxWidth )
            {
                name = name.substring( 0, maxWidth );
            }
        }
        while ( name.length() < minWidth )
        {
            name = name + ' ';
        }
        if ( minWidth == 1 && maxWidth == 2 )
        {
            // special case
            name = name.substring( 0, minUniqueDayLength[day - 1] );
        }
        return name;
    }
}
