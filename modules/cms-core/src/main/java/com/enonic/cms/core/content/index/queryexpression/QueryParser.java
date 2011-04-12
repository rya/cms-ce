/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.queryexpression;

import org.codehaus.jparsec.OperatorTable;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.Terminals;
import org.codehaus.jparsec.Tokens;
import org.codehaus.jparsec._;
import org.codehaus.jparsec.functors.Binary;
import org.codehaus.jparsec.functors.Unary;
import org.codehaus.jparsec.misc.Mapper;
import org.codehaus.jparsec.pattern.Patterns;

public final class QueryParser
{
    private final static String[] OPERATORS = {"=", "!=", ">", ">=", "<", "<=", "(", ")", ","};

    private final static String[] KEYWORDS =
        {"LIKE", "NOT", "IN", "CONTAINS", "STARTS", "ENDS", "WITH", "OR", "AND", "ORDER", "BY", "ASC", "DESC"};

    private final Terminals terms;

    private final Parser<Tokens.Fragment> identifierToken;

    public QueryParser()
    {
        this.identifierToken = identifierToken();
        this.terms = Terminals.caseInsensitive( this.identifierToken.source(), OPERATORS, KEYWORDS );
    }

    private Parser<Tokens.Fragment> fragmentToken( final String pattern, final String tag )
    {
        return Scanners.pattern( Patterns.regex( pattern ), tag ).source().map( QueryMapper.stringToFragment( tag ) );
    }

    private Parser<Tokens.Fragment> identifierToken()
    {
        return fragmentToken( "[a-zA-Z\\*@]+[a-zA-Z0-9\\-_/\\.\\*@]*", Tokens.Tag.IDENTIFIER.name() );
    }

    private Parser<Tokens.Fragment> decimalToken()
    {
        return fragmentToken( "([-+])?[0-9]+(\\.[0-9]+)?", Tokens.Tag.DECIMAL.name() );
    }

    private Parser<?> tokenizer()
    {
        return Parsers.or( this.terms.tokenizer(), this.identifierToken, decimalToken(), Terminals.StringLiteral.DOUBLE_QUOTE_TOKENIZER,
                           Terminals.StringLiteral.SINGLE_QUOTE_TOKENIZER );
    }

    private Parser<_> ignored()
    {
        return Scanners.SQL_DELIMITER;
    }

    private Parser<String> identifier()
    {
        return Terminals.fragment( Tokens.Tag.IDENTIFIER );
    }

    private Parser<FieldExpr> fieldExpr()
    {
        return identifier().map( QueryMapper.stringToFieldExpr() );
    }

    private Parser<ValueExpr> numberExpr()
    {
        return Terminals.fragment( Tokens.Tag.DECIMAL.name() ).map( QueryMapper.stringToNumberExpr() );
    }

    private Parser<ValueExpr> stringExpr()
    {
        return Terminals.StringLiteral.PARSER.map( QueryMapper.stringToStringExpr() );
    }

    private Parser<ValueExpr> valueExpr()
    {
        return Parsers.or( numberExpr(), stringExpr() );
    }

    private Parser<ArrayExpr> arrayExpr()
    {
        return valueExpr().sepBy( term( "," ) ).map( QueryMapper.valuesToArrayExpr() ).between( term( "(" ), term( ")" ) );
    }

    private Parser<FunctionExpr> functionExpr()
    {
        return Parsers.sequence( identifier(), arrayExpr(), QueryMapper.functionExprMapper() );
    }

    private Parser<Expression> computedExpr()
    {
        return Parsers.or( valueExpr(), functionExpr() );
    }

    private Parser<CompareExpr> compareExpr( final String opStr, final Integer opNum, final Parser<? extends Expression> right )
    {
        return Parsers.sequence( fieldExpr(), term( opStr ).retn( opNum ), right, QueryMapper.compareExprMapper() );
    }

    private Parser<CompareExpr> relationalEqExpr()
    {
        return compareExpr( "=", CompareExpr.EQ, computedExpr() );
    }

    private Parser<CompareExpr> relationalNeqExpr()
    {
        return compareExpr( "!=", CompareExpr.NEQ, computedExpr() );
    }

    private Parser<CompareExpr> relationalGtExpr()
    {
        return compareExpr( ">", CompareExpr.GT, computedExpr() );
    }

    private Parser<CompareExpr> relationalGteExpr()
    {
        return compareExpr( ">=", CompareExpr.GTE, computedExpr() );
    }

    private Parser<CompareExpr> relationalLtExpr()
    {
        return compareExpr( "<", CompareExpr.LT, computedExpr() );
    }

    private Parser<CompareExpr> relationalLteExpr()
    {
        return compareExpr( "<=", CompareExpr.LTE, computedExpr() );
    }

    private Parser<CompareExpr> compareWithNotExpr( final Parser<?> opStr, final Integer opNum, final Integer notOpNum,
                                                    final Parser<? extends Expression> right )
    {
        final Parser<Integer> op = Parsers.or( phrase( "NOT" ).followedBy( opStr ).retn( notOpNum ), opStr.retn( opNum ) );
        return Parsers.sequence( fieldExpr(), op, right, QueryMapper.compareExprMapper() );
    }

    private Parser<CompareExpr> compareLikeExpr( final Parser<?> op, final Parser<ValueExpr> right )
    {
        return compareWithNotExpr( op, CompareExpr.LIKE, CompareExpr.NOT_LIKE, right );
    }

    private Parser<CompareExpr> compareLikeExpr()
    {
        return compareLikeExpr( term( "LIKE" ), stringExpr() );
    }

    private Parser<CompareExpr> compareLikePrefixSuffixExpr( final Parser<?> op, final String prefix, final String suffix )
    {
        return compareLikeExpr( op, stringExpr().map( QueryMapper.prefixSuffixMapper( prefix, suffix ) ) );
    }

    private Parser<CompareExpr> compareInExpr()
    {
        return compareWithNotExpr( term( "IN" ), CompareExpr.IN, CompareExpr.NOT_IN, arrayExpr() );
    }

    private Parser<CompareExpr> compareContainsExpr()
    {
        return compareLikePrefixSuffixExpr( term( "CONTAINS" ), "%", "%" );
    }

    private Parser<CompareExpr> compareStartsWithExpr()
    {
        return compareLikePrefixSuffixExpr( term( "STARTS" ).followedBy( term( "WITH" ).optional() ), null, "%" );
    }

    private Parser<CompareExpr> compareEndsWithExpr()
    {
        return compareLikePrefixSuffixExpr( term( "ENDS" ).followedBy( term( "WITH" ).optional() ), "%", null );
    }

    private Parser<CompareExpr> relationalExpr()
    {
        return Parsers.or( relationalEqExpr(), relationalNeqExpr(), relationalLtExpr(), relationalLteExpr(), relationalGtExpr(),
                           relationalGteExpr() );
    }

    private Parser<CompareExpr> matchExpr()
    {
        return Parsers.or( compareLikeExpr(), compareInExpr(), compareContainsExpr(), compareStartsWithExpr(), compareEndsWithExpr() );
    }

    private Parser<CompareExpr> compareExpr()
    {
        return Parsers.or( relationalExpr(), matchExpr() );
    }

    private Parser<Expression> logicalExpr()
    {
        final Parser.Reference<Expression> ref = Parser.newReference();
        final Parser<Expression> parser =
            new OperatorTable<Expression>().prefix( notExpr(), 30 ).infixl( logicalExpr( "AND", LogicalExpr.AND ), 20 ).infixl(
                logicalExpr( "OR", LogicalExpr.OR ), 10 ).build( paren( ref.lazy() ).or( compareExpr() ) ).label( "logicalExpr" );
        ref.set( parser );
        return parser;
    }

    private <T> Parser<T> paren( final Parser<T> parser )
    {
        return parser.between( term( "(" ), term( ")" ) );
    }

    private Parser<Unary<Expression>> notExpr()
    {
        return term( "NOT" ).next( Parsers.constant( QueryMapper.notExprMapper() ) );
    }

    private Parser<Binary<Expression>> logicalExpr( final String opStr, final Integer opNum )
    {
        return term( opStr ).next( Parsers.constant( QueryMapper.logicalExprMapper( opNum ) ) );
    }

    private Parser<OrderByExpr> orderByExpr()
    {
        return Parsers.sequence( term( "ORDER" ), term( "BY" ).optional(), orderFieldExpr().sepBy( term( "," ) ) ).map(
            QueryMapper.orderByExprMapper() );
    }

    private Parser<OrderFieldExpr> orderFieldExpr()
    {
        return Parsers.sequence( fieldExpr(), Parsers.or( term( "ASC" ).retn( false ), term( "DESC" ).retn( true ) ).optional( false ),
                                 QueryMapper.orderFieldExprMapper() );
    }

    private Parser<QueryExpr> queryExpr()
    {
        return Parsers.sequence( logicalExpr().optional(), orderByExpr().optional(), QueryMapper.queryExprMapper() );
    }

    private Parser<?> term( final String term )
    {
        return Mapper._( this.terms.token( term ) );
    }

    private Parser<?> phrase( final String phrase )
    {
        return Mapper._( this.terms.phrase( phrase.split( "\\s" ) ) );
    }

    public QueryExpr parse( final String str )
    {
        try
        {
            return queryExpr().from( tokenizer(), ignored() ).parse( str );
        }
        catch ( Exception e )
        {
            throw new QueryParserException( e );
        }
    }

    public static QueryParser newInstance()
    {
        return new QueryParser();
    }
}
