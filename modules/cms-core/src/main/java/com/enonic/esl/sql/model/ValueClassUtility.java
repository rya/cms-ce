/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

/*
 * Created on 03.mai.2004
 */
package com.enonic.esl.sql.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ValueClassUtility
{

    public static void generateTableValueClass( File outputDirectory, Table table, String packageName )
    {
        try
        {
            String tableName = table.getName().substring( 1 );
            String className = tableName;
            FileOutputStream outputStream = new FileOutputStream( outputDirectory + File.separator + className + ".java" );
            PrintWriter writer = new PrintWriter( outputStream );

            // Package name
            writer.write( "package " + packageName + ";\n\n" );

            // Imports
            writer.write( "import com.enonic.vertical.engine.dbmodel.*;\n" );
            writer.write( "import java.util.List;\n\n" );

            // Class definition
            writer.write( "public final class " + className + " extends Entity {\n" );

            // Global variables
            ArrayList<String> columnList = new ArrayList<String>();
            Column[] columns = table.getColumns();
            List<Column> requiredColumns = new ArrayList<Column>();
            List<Column> optionalColumns = new ArrayList<Column>();

            for ( int i = 0; i < columns.length; i++ )
            {
                boolean required = columns[i].isRequired();
                if ( required )
                {
                    requiredColumns.add( columns[i] );
                }
                else
                {
                    optionalColumns.add( columns[i] );
                }
                columnList.add( columns[i].getRealName( false ) );
                writer.write( "\tprivate " );
                writer.write( columns[i].getType().getJavaTypeString() );
                writer.write( " " );
                writer.write( columns[i].getRealName( false ) );
                writer.write( ";\n" );
            }
            writer.write( "\n" );

            // Create constructor with List parameter
            writer.write( "\tpublic " + className + "(List columnValues) {\n" );
            for ( int i = 0; i < columns.length; i++ )
            {
                writer.write( "\t\t" );
                writer.write( columns[i].getRealName( false ) );
                writer.write( " = (" );
                writer.write( columns[i].getType().getJavaTypeString() );
                writer.write( ")columnValues.get(" + i + ")" );
                writer.write( ";\n" );
            }

            for ( int i = 0; i < columns.length; i++ )
            {
                writer.write( "\t\tthis.columnValues.add(" );
                writer.write( columns[i].getRealName( false ) );
                writer.write( ");\n" );
            }
            writer.write( "\t}\n\n" );

            // Create constructor with required parameters
            writer.write( "\tpublic " + className + "(" );
            for ( int i = 0; i < requiredColumns.size(); i++ )
            {
                Column column = requiredColumns.get( i );
                writer.write( column.getType().getJavaTypeString() );
                writer.write( " " );
                writer.write( column.getRealName( false ) );

                if ( i < requiredColumns.size() - 1 )
                {
                    writer.write( ", " );
                }
            }
            writer.write( ") {\n" );

            // Store constructor parameters in global variables
            for ( int i = 0; i < requiredColumns.size(); i++ )
            {
                writer.write( "\t\tthis." );
                writer.write( requiredColumns.get( i ).getRealName( false ) );
                writer.write( " = " );
                writer.write( requiredColumns.get( i ).getRealName( false ) );
                writer.write( ";\n" );
            }

            for ( int i = 0; i < columnList.size(); i++ )
            {
                writer.write( "\t\tcolumnValues.add(" );
                writer.write( columnList.get( i ) );
                writer.write( ");\n" );
            }
            writer.write( "\t}\n\n" );

            // Generate getTable method
            writer.write( "\tprotected com.enonic.esl.sql.model.Table getTable() {\n" );
            writer.write( "\t\treturn " );
            writer.write( tableName );
            writer.write( "Table.getInstance();\n" );
            writer.write( "\t}\n\n" );

            // Generate getEntityTitle method
            Column titleColumn = table.getTitleColumn();
            if ( titleColumn != null )
            {
                writer.write( "\tpublic String getEntityTitle() {\n" );
                writer.write( "\t\treturn " );
                writer.write( titleColumn.getRealName( false ) );
                writer.write( ";\n" );
                writer.write( "\t}\n\n" );
            }

            // Generate getter methods
            for ( int i = 0; i < columns.length; i++ )
            {
                writer.write( "\tpublic " );
                writer.write( columns[i].getType().getJavaTypeString() );
                writer.write( " get" );
                writer.write( columns[i].getRealName( true ) );
                writer.write( "() {\n" );
                writer.write( "\t\treturn " );
                writer.write( columns[i].getRealName( false ) );
                writer.write( ";\n" );
                writer.write( "\t}\n\n" );
            }

            // Generate setter methods
            for ( int i = 0; i < columns.length; i++ )
            {
                writer.write( "\tpublic void set" );
                writer.write( columns[i].getRealName( true ) );
                writer.write( "(" );
                writer.write( columns[i].getType().getJavaTypeString() );
                writer.write( " " );
                writer.write( columns[i].getRealName( false ) );
                writer.write( ") {\n" );
                writer.write( "\t\tthis." );
                writer.write( columns[i].getRealName( false ) );
                writer.write( " = " );
                writer.write( columns[i].getRealName( false ) );
                writer.write( ";\n" );
                writer.write( "\t}\n\n" );
            }

            writer.write( "}" );
            writer.close();
        }
        catch ( FileNotFoundException fnfe )
        {
            fnfe.printStackTrace();
        }
    }

    public static void generateViewValueClass( File outputDirectory, View view, String packageName )
    {
        try
        {
            String viewName = view.getName().substring( 1 );
            String className = viewName + "RO";
            FileOutputStream outputStream = new FileOutputStream( outputDirectory + File.separator + className + ".java" );
            PrintWriter writer = new PrintWriter( outputStream );

            // Package name
            writer.write( "package " + packageName + ";\n\n" );

            // Imports
            writer.write( "import com.enonic.vertical.engine.dbmodel.*;\n" );
            writer.write( "import java.util.List;\n\n" );

            // Class definition
            writer.write( "public final class " + className + " extends Entity {\n" );

            // Global variables
            ArrayList<String> columnList = new ArrayList<String>();
            Column[] columns = view.getColumns();
            List<Column> requiredColumns = new ArrayList<Column>();
            List<Column> optionalColumns = new ArrayList<Column>();

            for ( int i = 0; i < columns.length; i++ )
            {
                boolean required = columns[i].isRequired();
                if ( required )
                {
                    requiredColumns.add( columns[i] );
                }
                else
                {
                    optionalColumns.add( columns[i] );
                }
                columnList.add( columns[i].getRealName( false ) );
                writer.write( "\tprivate " );
                writer.write( columns[i].getType().getJavaTypeString() );
                writer.write( " " );
                writer.write( columns[i].getRealName( false ) );
                writer.write( ";\n" );
            }
            writer.write( "\n" );

            // Create constructor with List parameter
            writer.write( "\tpublic " + className + "(List columnValues) {\n" );
            for ( int i = 0; i < columns.length; i++ )
            {
                writer.write( "\t\t" );
                writer.write( columns[i].getRealName( false ) );
                writer.write( " = (" );
                writer.write( columns[i].getType().getJavaTypeString() );
                writer.write( ")columnValues.get(" + i + ")" );
                writer.write( ";\n" );
            }

            for ( int i = 0; i < columns.length; i++ )
            {
                writer.write( "\t\tthis.columnValues.add(" );
                writer.write( columns[i].getRealName( false ) );
                writer.write( ");\n" );
            }
            writer.write( "\t}\n\n" );

            // Generate getTable method
            writer.write( "\tprotected com.enonic.esl.sql.model.Table getTable() {\n" );
            writer.write( "\t\treturn " );
            writer.write( viewName );
            writer.write( "View.getInstance();\n" );
            writer.write( "\t}\n\n" );

            // Generate getEntityTitle method
            Column titleColumn = view.getTitleColumn();
            if ( titleColumn != null )
            {
                writer.write( "\tpublic String getEntityTitle() {\n" );
                writer.write( "\t\treturn " );
                writer.write( titleColumn.getRealName( false ) );
                writer.write( ";\n" );
                writer.write( "\t}\n\n" );
            }

            // Generate getter methods
            for ( int i = 0; i < columns.length; i++ )
            {
                writer.write( "\tpublic " );
                writer.write( columns[i].getType().getJavaTypeString() );
                writer.write( " get" );
                writer.write( columns[i].getRealName( true ) );
                writer.write( "() {\n" );
                writer.write( "\t\treturn " );
                writer.write( columns[i].getRealName( false ) );
                writer.write( ";\n" );
                writer.write( "\t}\n\n" );
            }
            writer.write( "}" );
            writer.close();
        }
        catch ( FileNotFoundException fnfe )
        {
            fnfe.printStackTrace();
        }
    }

    public static void generateValueClasses( File outputDirectory, Database database, String packageName )
    {
        Table[] tables = database.getTables();
        ArrayList<Table> tableClasses = new ArrayList<Table>();
        for ( int i = 0; i < tables.length; i++ )
        {
            if ( tables[i].getElementName() != null && tables[i].getElementName().length() > 0 )
            {
                tableClasses.add( tables[i] );
                generateTableValueClass( outputDirectory, tables[i], packageName );
            }
        }
        View[] views = database.getViews();
        ArrayList<View> viewClasses = new ArrayList<View>();
        for ( int i = 0; i < views.length; i++ )
        {
            if ( views[i].getElementName() != null && views[i].getElementName().length() > 0 )
            {
                viewClasses.add( views[i] );
                generateViewValueClass( outputDirectory, views[i], packageName );
            }
        }
        generateClassList( outputDirectory, tableClasses, viewClasses, packageName );
    }

    public static void generateClassList( File outputDirectory, List<Table> tableClasses, List<View> viewClasses, String packageName )
    {
        try
        {
            String className = "ClassList";
            FileOutputStream outputStream = new FileOutputStream( outputDirectory + File.separator + className + ".java" );
            PrintWriter writer = new PrintWriter( outputStream );

            // Package name
            writer.write( "package " + packageName + ";\n\n" );

            // Imports
            writer.write( "import java.util.HashMap;\n" );
            writer.write( "import com.enonic.vertical.engine.dbmodel.*;\n\n" );

            // Class definition
            writer.write( "public final class " + className + " {\n" );

            // Global
            writer.write( "private static HashMap classes = new HashMap();\n\n" );
            writer.write( "\tstatic {\n" );

            // Table instances
            for ( int i = 0; i < tableClasses.size(); i++ )
            {
                String name = tableClasses.get( i ).getName().substring( 1 );
                writer.write( "\t\tclasses.put(" );
                writer.write( name );
                writer.write( "Table.getInstance(), " );
                writer.write( name );
                writer.write( ".class);\n" );
            }

            // View instances
            for ( int i = 0; i < viewClasses.size(); i++ )
            {
                String name = viewClasses.get( i ).getName().substring( 1 );
                writer.write( "\t\tclasses.put(" );
                writer.write( name );
                writer.write( "View.getInstance(), " );
                writer.write( name );
                writer.write( "RO" );
                writer.write( ".class);\n" );
            }

            writer.write( "\t}\n\n" );

            writer.write( "\tpublic static Class getClass(com.enonic.esl.sql.model.Table table) {\n" );
            writer.write( "\t\treturn (Class)classes.get(table);\n" );
            writer.write( "\t}\n\n" );

            writer.write( "}" );
            writer.close();
        }
        catch ( FileNotFoundException fnfe )
        {
            fnfe.printStackTrace();
        }
    }
}
