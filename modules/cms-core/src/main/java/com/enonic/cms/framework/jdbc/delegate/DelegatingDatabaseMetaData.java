/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.delegate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;

/**
 * This class implements the delegating meta data.
 */
public class DelegatingDatabaseMetaData
    extends DelegatingBase
    implements DatabaseMetaData
{
    /**
     * Database meta data.
     */
    private final DatabaseMetaData md;

    /**
     * Connection.
     */
    private final Connection conn;

    /**
     * Construct the meta data.
     */
    public DelegatingDatabaseMetaData( DatabaseMetaData md, Connection conn )
    {
        this.conn = conn;
        this.md = md;
    }

    /**
     * Return the delegate.
     */
    public Object getDelegate()
    {
        return this.md;
    }

    public int getDatabaseMajorVersion()
        throws SQLException
    {
        return md.getDatabaseMajorVersion();
    }

    public int getDatabaseMinorVersion()
        throws SQLException
    {
        return md.getDatabaseMinorVersion();
    }

    public int getDefaultTransactionIsolation()
        throws SQLException
    {
        return md.getDefaultTransactionIsolation();
    }

    public int getDriverMajorVersion()
    {
        return md.getDriverMajorVersion();
    }

    public int getDriverMinorVersion()
    {
        return md.getDriverMinorVersion();
    }

    public int getJDBCMajorVersion()
        throws SQLException
    {
        return md.getJDBCMajorVersion();
    }

    public int getJDBCMinorVersion()
        throws SQLException
    {
        return md.getJDBCMinorVersion();
    }

    public int getMaxBinaryLiteralLength()
        throws SQLException
    {
        return md.getMaxBinaryLiteralLength();
    }

    public int getMaxCatalogNameLength()
        throws SQLException
    {
        return md.getMaxCatalogNameLength();
    }

    public int getMaxCharLiteralLength()
        throws SQLException
    {
        return md.getMaxCharLiteralLength();
    }

    public int getMaxColumnNameLength()
        throws SQLException
    {
        return md.getMaxColumnNameLength();
    }

    public int getMaxColumnsInGroupBy()
        throws SQLException
    {
        return md.getMaxColumnsInGroupBy();
    }

    public int getMaxColumnsInIndex()
        throws SQLException
    {
        return md.getMaxColumnsInIndex();
    }

    public int getMaxColumnsInOrderBy()
        throws SQLException
    {
        return md.getMaxColumnsInOrderBy();
    }

    public int getMaxColumnsInSelect()
        throws SQLException
    {
        return md.getMaxColumnsInSelect();
    }

    public int getMaxColumnsInTable()
        throws SQLException
    {
        return md.getMaxColumnsInTable();
    }

    public int getMaxConnections()
        throws SQLException
    {
        return md.getMaxConnections();
    }

    public int getMaxCursorNameLength()
        throws SQLException
    {
        return md.getMaxCursorNameLength();
    }

    public int getMaxIndexLength()
        throws SQLException
    {
        return md.getMaxIndexLength();
    }

    public int getMaxProcedureNameLength()
        throws SQLException
    {
        return md.getMaxProcedureNameLength();
    }

    public int getMaxRowSize()
        throws SQLException
    {
        return md.getMaxRowSize();
    }

    public int getMaxSchemaNameLength()
        throws SQLException
    {
        return md.getMaxSchemaNameLength();
    }

    public int getMaxStatementLength()
        throws SQLException
    {
        return md.getMaxStatementLength();
    }

    public int getMaxStatements()
        throws SQLException
    {
        return md.getMaxStatements();
    }

    public int getMaxTableNameLength()
        throws SQLException
    {
        return md.getMaxTableNameLength();
    }

    public int getMaxTablesInSelect()
        throws SQLException
    {
        return md.getMaxTablesInSelect();
    }

    public int getMaxUserNameLength()
        throws SQLException
    {
        return md.getMaxUserNameLength();
    }

    public int getResultSetHoldability()
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int getSQLStateType()
        throws SQLException
    {
        return md.getSQLStateType();
    }

    public boolean allProceduresAreCallable()
        throws SQLException
    {
        return md.allProceduresAreCallable();
    }

    public boolean allTablesAreSelectable()
        throws SQLException
    {
        return md.allTablesAreSelectable();
    }

    public boolean dataDefinitionCausesTransactionCommit()
        throws SQLException
    {
        return md.dataDefinitionCausesTransactionCommit();
    }

    public boolean dataDefinitionIgnoredInTransactions()
        throws SQLException
    {
        return md.dataDefinitionIgnoredInTransactions();
    }

    public boolean doesMaxRowSizeIncludeBlobs()
        throws SQLException
    {
        return md.doesMaxRowSizeIncludeBlobs();
    }

    public boolean isCatalogAtStart()
        throws SQLException
    {
        return md.isCatalogAtStart();
    }

    public boolean isReadOnly()
        throws SQLException
    {
        return md.isReadOnly();
    }

    public boolean locatorsUpdateCopy()
        throws SQLException
    {
        return md.locatorsUpdateCopy();
    }

    public boolean nullPlusNonNullIsNull()
        throws SQLException
    {
        return md.nullPlusNonNullIsNull();
    }

    public boolean nullsAreSortedAtEnd()
        throws SQLException
    {
        return md.nullsAreSortedAtEnd();
    }

    public boolean nullsAreSortedAtStart()
        throws SQLException
    {
        return md.nullsAreSortedAtStart();
    }

    public boolean nullsAreSortedHigh()
        throws SQLException
    {
        return md.nullsAreSortedHigh();
    }

    public boolean nullsAreSortedLow()
        throws SQLException
    {
        return md.nullsAreSortedLow();
    }

    public boolean storesLowerCaseIdentifiers()
        throws SQLException
    {
        return md.storesLowerCaseIdentifiers();
    }

    public boolean storesLowerCaseQuotedIdentifiers()
        throws SQLException
    {
        return md.storesLowerCaseQuotedIdentifiers();
    }

    public boolean storesMixedCaseIdentifiers()
        throws SQLException
    {
        return md.storesMixedCaseIdentifiers();
    }

    public boolean storesMixedCaseQuotedIdentifiers()
        throws SQLException
    {
        return md.storesMixedCaseQuotedIdentifiers();
    }

    public boolean storesUpperCaseIdentifiers()
        throws SQLException
    {
        return md.storesUpperCaseIdentifiers();
    }

    public boolean storesUpperCaseQuotedIdentifiers()
        throws SQLException
    {
        return md.storesUpperCaseQuotedIdentifiers();
    }

    public boolean supportsANSI92EntryLevelSQL()
        throws SQLException
    {
        return md.supportsANSI92EntryLevelSQL();
    }

    public boolean supportsANSI92FullSQL()
        throws SQLException
    {
        return md.supportsANSI92FullSQL();
    }

    public boolean supportsANSI92IntermediateSQL()
        throws SQLException
    {
        return md.supportsANSI92IntermediateSQL();
    }

    public boolean supportsAlterTableWithAddColumn()
        throws SQLException
    {
        return md.supportsAlterTableWithAddColumn();
    }

    public boolean supportsAlterTableWithDropColumn()
        throws SQLException
    {
        return md.supportsAlterTableWithDropColumn();
    }

    public boolean supportsBatchUpdates()
        throws SQLException
    {
        return md.supportsBatchUpdates();
    }

    public boolean supportsCatalogsInDataManipulation()
        throws SQLException
    {
        return md.supportsCatalogsInDataManipulation();
    }

    public boolean supportsCatalogsInIndexDefinitions()
        throws SQLException
    {
        return md.supportsCatalogsInIndexDefinitions();
    }

    public boolean supportsCatalogsInPrivilegeDefinitions()
        throws SQLException
    {
        return md.supportsCatalogsInPrivilegeDefinitions();
    }

    public boolean supportsCatalogsInProcedureCalls()
        throws SQLException
    {
        return md.supportsCatalogsInProcedureCalls();
    }

    public boolean supportsCatalogsInTableDefinitions()
        throws SQLException
    {
        return md.supportsCatalogsInTableDefinitions();
    }

    public boolean supportsColumnAliasing()
        throws SQLException
    {
        return md.supportsColumnAliasing();
    }

    public boolean supportsConvert()
        throws SQLException
    {
        return md.supportsConvert();
    }

    public boolean supportsCoreSQLGrammar()
        throws SQLException
    {
        return md.supportsCoreSQLGrammar();
    }

    public boolean supportsCorrelatedSubqueries()
        throws SQLException
    {
        return md.supportsCorrelatedSubqueries();
    }

    public boolean supportsDataDefinitionAndDataManipulationTransactions()
        throws SQLException
    {
        return md.supportsDataDefinitionAndDataManipulationTransactions();
    }

    public boolean supportsDataManipulationTransactionsOnly()
        throws SQLException
    {
        return md.supportsDataManipulationTransactionsOnly();
    }

    public boolean supportsDifferentTableCorrelationNames()
        throws SQLException
    {
        return md.supportsDifferentTableCorrelationNames();
    }

    public boolean supportsExpressionsInOrderBy()
        throws SQLException
    {
        return md.supportsExpressionsInOrderBy();
    }

    public boolean supportsExtendedSQLGrammar()
        throws SQLException
    {
        return md.supportsExtendedSQLGrammar();
    }

    public boolean supportsFullOuterJoins()
        throws SQLException
    {
        return md.supportsFullOuterJoins();
    }

    public boolean supportsGetGeneratedKeys()
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean supportsGroupBy()
        throws SQLException
    {
        return md.supportsGroupBy();
    }

    public boolean supportsGroupByBeyondSelect()
        throws SQLException
    {
        return md.supportsGroupByBeyondSelect();
    }

    public boolean supportsGroupByUnrelated()
        throws SQLException
    {
        return md.supportsGroupByUnrelated();
    }

    public boolean supportsIntegrityEnhancementFacility()
        throws SQLException
    {
        return md.supportsIntegrityEnhancementFacility();
    }

    public boolean supportsLikeEscapeClause()
        throws SQLException
    {
        return md.supportsLikeEscapeClause();
    }

    public boolean supportsLimitedOuterJoins()
        throws SQLException
    {
        return md.supportsLimitedOuterJoins();
    }

    public boolean supportsMinimumSQLGrammar()
        throws SQLException
    {
        return md.supportsMinimumSQLGrammar();
    }

    public boolean supportsMixedCaseIdentifiers()
        throws SQLException
    {
        return md.supportsMixedCaseIdentifiers();
    }

    public boolean supportsMixedCaseQuotedIdentifiers()
        throws SQLException
    {
        return md.supportsMixedCaseQuotedIdentifiers();
    }

    public boolean supportsMultipleOpenResults()
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean supportsMultipleResultSets()
        throws SQLException
    {
        return md.supportsMultipleResultSets();
    }

    public boolean supportsMultipleTransactions()
        throws SQLException
    {
        return md.supportsMultipleTransactions();
    }

    public boolean supportsNamedParameters()
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean supportsNonNullableColumns()
        throws SQLException
    {
        return md.supportsNonNullableColumns();
    }

    public boolean supportsOpenCursorsAcrossCommit()
        throws SQLException
    {
        return md.supportsOpenCursorsAcrossCommit();
    }

    public boolean supportsOpenCursorsAcrossRollback()
        throws SQLException
    {
        return md.supportsOpenCursorsAcrossRollback();
    }

    public boolean supportsOpenStatementsAcrossCommit()
        throws SQLException
    {
        return md.supportsOpenStatementsAcrossCommit();
    }

    public boolean supportsOpenStatementsAcrossRollback()
        throws SQLException
    {
        return md.supportsOpenStatementsAcrossRollback();
    }

    public boolean supportsOrderByUnrelated()
        throws SQLException
    {
        return md.supportsOrderByUnrelated();
    }

    public boolean supportsOuterJoins()
        throws SQLException
    {
        return md.supportsOuterJoins();
    }

    public boolean supportsPositionedDelete()
        throws SQLException
    {
        return md.supportsPositionedDelete();
    }

    public boolean supportsPositionedUpdate()
        throws SQLException
    {
        return md.supportsPositionedUpdate();
    }

    public boolean supportsSavepoints()
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean supportsSchemasInDataManipulation()
        throws SQLException
    {
        return md.supportsSchemasInDataManipulation();
    }

    public boolean supportsSchemasInIndexDefinitions()
        throws SQLException
    {
        return md.supportsSchemasInIndexDefinitions();
    }

    public boolean supportsSchemasInPrivilegeDefinitions()
        throws SQLException
    {
        return md.supportsSchemasInPrivilegeDefinitions();
    }

    public boolean supportsSchemasInProcedureCalls()
        throws SQLException
    {
        return md.supportsSchemasInProcedureCalls();
    }

    public boolean supportsSchemasInTableDefinitions()
        throws SQLException
    {
        return md.supportsSchemasInTableDefinitions();
    }

    public boolean supportsSelectForUpdate()
        throws SQLException
    {
        return md.supportsSelectForUpdate();
    }

    public boolean supportsStatementPooling()
        throws SQLException
    {
        return md.supportsStatementPooling();
    }

    public boolean supportsStoredProcedures()
        throws SQLException
    {
        return md.supportsStoredProcedures();
    }

    public boolean supportsSubqueriesInComparisons()
        throws SQLException
    {
        return md.supportsSubqueriesInComparisons();
    }

    public boolean supportsSubqueriesInExists()
        throws SQLException
    {
        return md.supportsSubqueriesInExists();
    }

    public boolean supportsSubqueriesInIns()
        throws SQLException
    {
        return md.supportsSubqueriesInIns();
    }

    public boolean supportsSubqueriesInQuantifieds()
        throws SQLException
    {
        return md.supportsSubqueriesInQuantifieds();
    }

    public boolean supportsTableCorrelationNames()
        throws SQLException
    {
        return md.supportsTableCorrelationNames();
    }

    public boolean supportsTransactions()
        throws SQLException
    {
        return md.supportsTransactions();
    }

    public boolean supportsUnion()
        throws SQLException
    {
        return md.supportsUnion();
    }

    public boolean supportsUnionAll()
        throws SQLException
    {
        return md.supportsUnionAll();
    }

    public boolean usesLocalFilePerTable()
        throws SQLException
    {
        return md.usesLocalFilePerTable();
    }

    public boolean usesLocalFiles()
        throws SQLException
    {
        return md.usesLocalFiles();
    }

    public boolean deletesAreDetected( int type )
        throws SQLException
    {
        return md.deletesAreDetected( type );
    }

    public boolean insertsAreDetected( int type )
        throws SQLException
    {
        return md.insertsAreDetected( type );
    }

    public boolean othersDeletesAreVisible( int type )
        throws SQLException
    {
        return md.othersDeletesAreVisible( type );
    }

    public boolean othersInsertsAreVisible( int type )
        throws SQLException
    {
        return md.othersInsertsAreVisible( type );
    }

    public boolean othersUpdatesAreVisible( int type )
        throws SQLException
    {
        return md.othersUpdatesAreVisible( type );
    }

    public boolean ownDeletesAreVisible( int type )
        throws SQLException
    {
        return md.ownDeletesAreVisible( type );
    }

    public boolean ownInsertsAreVisible( int type )
        throws SQLException
    {
        return md.ownInsertsAreVisible( type );
    }

    public boolean ownUpdatesAreVisible( int type )
        throws SQLException
    {
        return md.ownUpdatesAreVisible( type );
    }

    public boolean supportsResultSetHoldability( int holdability )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean supportsResultSetType( int type )
        throws SQLException
    {
        return md.supportsResultSetType( type );
    }

    public boolean supportsTransactionIsolationLevel( int level )
        throws SQLException
    {
        return md.supportsTransactionIsolationLevel( level );
    }

    public boolean updatesAreDetected( int type )
        throws SQLException
    {
        return md.updatesAreDetected( type );
    }

    public boolean supportsConvert( int fromType, int toType )
        throws SQLException
    {
        return md.supportsConvert( fromType, toType );
    }

    public boolean supportsResultSetConcurrency( int type, int concurrency )
        throws SQLException
    {
        return md.supportsResultSetConcurrency( type, concurrency );
    }

    public String getCatalogSeparator()
        throws SQLException
    {
        return md.getCatalogSeparator();
    }

    public String getCatalogTerm()
        throws SQLException
    {
        return md.getCatalogTerm();
    }

    public String getDatabaseProductName()
        throws SQLException
    {
        return md.getDatabaseProductName();
    }

    public String getDatabaseProductVersion()
        throws SQLException
    {
        return md.getDatabaseProductVersion();
    }

    public String getDriverName()
        throws SQLException
    {
        return md.getDriverName();
    }

    public String getDriverVersion()
        throws SQLException
    {
        return md.getDriverVersion();
    }

    public String getExtraNameCharacters()
        throws SQLException
    {
        return md.getExtraNameCharacters();
    }

    public String getIdentifierQuoteString()
        throws SQLException
    {
        return md.getIdentifierQuoteString();
    }

    public String getNumericFunctions()
        throws SQLException
    {
        return md.getNumericFunctions();
    }

    public String getProcedureTerm()
        throws SQLException
    {
        return md.getProcedureTerm();
    }

    public String getSQLKeywords()
        throws SQLException
    {
        return md.getSQLKeywords();
    }

    public String getSchemaTerm()
        throws SQLException
    {
        return md.getSchemaTerm();
    }

    public String getSearchStringEscape()
        throws SQLException
    {
        return md.getSearchStringEscape();
    }

    public String getStringFunctions()
        throws SQLException
    {
        return md.getStringFunctions();
    }

    public String getSystemFunctions()
        throws SQLException
    {
        return md.getSystemFunctions();
    }

    public String getTimeDateFunctions()
        throws SQLException
    {
        return md.getTimeDateFunctions();
    }

    public String getURL()
        throws SQLException
    {
        return md.getURL();
    }

    public String getUserName()
        throws SQLException
    {
        return md.getUserName();
    }

    public Connection getConnection()
        throws SQLException
    {
        return this.conn;
    }

    public ResultSet getCatalogs()
        throws SQLException
    {
        return md.getCatalogs();
    }

    public ResultSet getSchemas()
        throws SQLException
    {
        return md.getSchemas();
    }

    public ResultSet getTableTypes()
        throws SQLException
    {
        return md.getTableTypes();
    }

    public ResultSet getTypeInfo()
        throws SQLException
    {
        return md.getTypeInfo();
    }

    public ResultSet getExportedKeys( String catalog, String schema, String table )
        throws SQLException
    {
        return md.getExportedKeys( catalog, schema, table );
    }

    public ResultSet getImportedKeys( String catalog, String schema, String table )
        throws SQLException
    {
        return md.getImportedKeys( catalog, schema, table );
    }

    public ResultSet getPrimaryKeys( String catalog, String schema, String table )
        throws SQLException
    {
        return md.getPrimaryKeys( catalog, schema, table );
    }

    public ResultSet getProcedures( String catalog, String schemaPattern, String procedureNamePattern )
        throws SQLException
    {
        return md.getProcedures( catalog, schemaPattern, procedureNamePattern );
    }

    public ResultSet getSuperTables( String catalog, String schemaPattern, String tableNamePattern )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet getSuperTypes( String catalog, String schemaPattern, String typeNamePattern )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet getTablePrivileges( String catalog, String schemaPattern, String tableNamePattern )
        throws SQLException
    {
        return md.getTablePrivileges( catalog, schemaPattern, tableNamePattern );
    }

    public ResultSet getVersionColumns( String catalog, String schema, String table )
        throws SQLException
    {
        return md.getVersionColumns( catalog, schema, table );
    }

    public ResultSet getBestRowIdentifier( String catalog, String schema, String table, int scope, boolean nullable )
        throws SQLException
    {
        return md.getBestRowIdentifier( catalog, schema, table, scope, nullable );
    }

    public ResultSet getIndexInfo( String catalog, String schema, String table, boolean unique, boolean approximate )
        throws SQLException
    {
        return md.getIndexInfo( catalog, schema, table, unique, approximate );
    }

    public ResultSet getUDTs( String catalog, String schemaPattern, String typeNamePattern, int[] types )
        throws SQLException
    {
        return md.getUDTs( catalog, schemaPattern, typeNamePattern, types );
    }

    public ResultSet getAttributes( String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet getColumnPrivileges( String catalog, String schema, String table, String columnNamePattern )
        throws SQLException
    {
        return md.getColumnPrivileges( catalog, schema, table, columnNamePattern );
    }

    public ResultSet getColumns( String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern )
        throws SQLException
    {
        return md.getColumns( catalog, schemaPattern, tableNamePattern, columnNamePattern );
    }

    public ResultSet getProcedureColumns( String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern )
        throws SQLException
    {
        return md.getProcedureColumns( catalog, schemaPattern, procedureNamePattern, columnNamePattern );
    }

    public ResultSet getTables( String catalog, String schemaPattern, String tableNamePattern, String[] types )
        throws SQLException
    {
        return md.getTables( catalog, schemaPattern, tableNamePattern, types );
    }

    public ResultSet getCrossReference( String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog,
                                        String foreignSchema, String foreignTable )
        throws SQLException
    {
        return md.getCrossReference( primaryCatalog, primarySchema, primaryTable, foreignCatalog, foreignSchema, foreignTable );
    }

    public RowIdLifetime getRowIdLifetime()
        throws SQLException
    {
        return this.md.getRowIdLifetime();
    }

    public ResultSet getSchemas( String catalog, String schemaPattern )
        throws SQLException
    {
        return this.md.getSchemas( catalog, schemaPattern );
    }

    public boolean supportsStoredFunctionsUsingCallSyntax()
        throws SQLException
    {
        return this.md.supportsStoredFunctionsUsingCallSyntax();
    }

    public boolean autoCommitFailureClosesAllResultSets()
        throws SQLException
    {
        return this.md.autoCommitFailureClosesAllResultSets();
    }

    public ResultSet getClientInfoProperties()
        throws SQLException
    {
        return this.md.getClientInfoProperties();
    }

    public ResultSet getFunctions( String catalog, String schemaPattern, String functionNamePattern )
        throws SQLException
    {
        return this.md.getFunctions( catalog, schemaPattern, functionNamePattern );
    }

    public ResultSet getFunctionColumns( String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern )
        throws SQLException
    {
        return this.md.getFunctionColumns( catalog, schemaPattern, functionNamePattern, columnNamePattern );
    }

    public <T> T unwrap( Class<T> iface )
        throws SQLException
    {
        return this.md.unwrap( iface );
    }

    public boolean isWrapperFor( Class<?> iface )
        throws SQLException
    {
        return this.md.isWrapperFor( iface );
    }
}
