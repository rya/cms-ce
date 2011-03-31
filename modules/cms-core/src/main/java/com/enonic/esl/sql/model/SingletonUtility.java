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

public class SingletonUtility
{

    public static void generateSingletonClass( File outputDirectory, Table table, String packageName )
    {
        try
        {
            String tableName = table.getName().substring( 1 );
            String className = tableName + "Table";
            FileOutputStream outputStream = new FileOutputStream( outputDirectory + File.separator + className + ".java" );
            PrintWriter writer = new PrintWriter( outputStream );

            writer.write( "package " + packageName + ";\n\n" );
            writer.write( "import com.enonic.esl.sql.model.*;\n\n" );
            writer.write( "public final class " + className + " extends Table {\n" );
            writer.write(
                "\tprivate static final " + className + " " + tableName + " = new " + className + "(\"" + table.getName() + "\", \"" +
                    table.getElementName() + "\", \"" + table.getParentName() + "\");\n\n" );

            Column[] columns = table.getColumns();
            StringBuffer addColumns = new StringBuffer();
            for ( int i = 0; i < columns.length; i++ )
            {
                boolean isRequired = columns[i].isRequired();
                boolean isPrimaryKey = columns[i].isPrimaryKey();
                if ( columns[i].isForeignKey() )
                {
                    ForeignKeyColumn fk = (ForeignKeyColumn) columns[i];
                    boolean isDelete = fk.isDelete();
                    writer.write( "\tpublic ForeignKeyColumn " + fk.getName() + " = new ForeignKeyColumn(\"" + fk.getName() + "\", \"" +
                        fk.getXPath() + "\", " + String.valueOf( isRequired ) + ", " + String.valueOf( isPrimaryKey ) +
                        ", Constants.COLUMN_" + fk.getType().getTypeString() + ", " +
                        columns[i].getType().getDataString( columns[i].getDefaultValue() ) + ", \"" + fk.getReferencedTableName() +
                        "\", \"" + fk.getReferencedColumnName() + "\", " + isDelete + ", -1);\n" );
                }
                else
                {
                    int size = columns[i].getSize();
                    writer.write( "\tpublic Column " + columns[i].getName() + " = new Column(\"" + columns[i].getName() + "\", \"" +
                        columns[i].getXPath() + "\", " + String.valueOf( isRequired ) + ", " + String.valueOf( isPrimaryKey ) +
                        ", Constants.COLUMN_" + columns[i].getType().getTypeString() + ", " +
                        columns[i].getType().getDataString( columns[i].getDefaultValue() ) + ", " + size + ");\n" );
                }
                addColumns.append( "\t\taddColumn(" + columns[i].getName() + ");\n" );
            }
            writer.write( "\n" );

            writer.write( "\tprivate " + className + "(String tableName, String elementName, String parentName) {\n" );
            writer.write( "\t\tsuper(tableName, elementName, parentName);\n" );
            writer.write( addColumns.toString() );
            //writer.write(addForeignKeys.toString());
            //writer.write(addIndexes.toString());
            writer.write( "\t}\n\n" );
            writer.write( "\tpublic static " + className + " getInstance() {\n" );
            writer.write( "\t\treturn " + tableName + ";\n" );
            writer.write( "\t}\n\n" );
            writer.write( "}" );

            writer.close();
        }
        catch ( FileNotFoundException fnfe )
        {
            fnfe.printStackTrace();
        }
    }

    public static void generateSingletonClass( File outputDirectory, View view, String packageName )
    {
        try
        {
            String viewName = view.getName().substring( 1 );
            String className = viewName + "View";
            FileOutputStream outputStream = new FileOutputStream( outputDirectory + File.separator + className + ".java" );
            PrintWriter writer = new PrintWriter( outputStream );

            writer.write( "package " + packageName + ";\n\n" );
            writer.write( "import com.enonic.esl.sql.model.*;\n\n" );
            writer.write( "public final class " + className + " extends View {\n" );
            writer.write(
                "\tprivate static final " + className + " " + viewName + " = new " + className + "(\"" + view.getName() + "\", \"" +
                    view.getElementName() + "\", \"" + view.getParentName() + "\");\n\n" );

            Column[] columns = view.getColumns();
            StringBuffer addColumns = new StringBuffer();
            for ( int i = 0; i < columns.length; i++ )
            {
                writer.write( "\tpublic Column " + columns[i].getName() + " = new Column(\"" + columns[i].getName() + "\", \"" +
                    columns[i].getXPath() + "\", Constants.COLUMN_" + columns[i].getType().getTypeString() + ");\n" );
                addColumns.append( "\t\taddColumn(" + columns[i].getName() + ");\n" );
            }
            writer.write( "\n" );

            writer.write( "\tprivate " + className + "(String tableName, String elementName, String parentName) {\n" );
            writer.write( "\t\tsuper(tableName, elementName, parentName);\n" );
            writer.write( addColumns.toString() );
            writer.write( "\t}\n\n" );
            writer.write( "\tpublic static " + className + " getInstance() {\n" );
            writer.write( "\t\treturn " + viewName + ";\n" );
            writer.write( "\t}\n\n" );
            writer.write( "}" );

            writer.close();
        }
        catch ( FileNotFoundException fnfe )
        {
            fnfe.printStackTrace();
        }
    }

    public static void generateSingletonClasses( File outputDirectory, Database database, String packageName )
    {
        try
        {
            String databaseName = database.getName();
            String className = databaseName + "Database";
            FileOutputStream outputStream = new FileOutputStream( outputDirectory + File.separator + className + ".java" );
            PrintWriter writer = new PrintWriter( outputStream );

            writer.write( "package " + packageName + ";\n\n" );
            writer.write( "import com.enonic.esl.sql.model.Database;\n\n" );
            writer.write( "public final class " + className + " extends Database {\n" );
            writer.write(
                "\tprivate static final " + className + " " + databaseName + " = new " + className + "(\"" + databaseName + "\"," +
                    database.getVersion() + ");\n\n" );

            // Generate table classes
            Table[] tables = database.getTables();
            StringBuffer addTables = new StringBuffer();
            for ( int i = 0; i < tables.length; i++ )
            {
                String tableName = tables[i].getName().substring( 1 );
                String tableClassName = tableName + "Table";
                writer.write( "\tpublic " + tableClassName + " t" + tableName + " = " + tableClassName + ".getInstance();\n" );
                addTables.append( "\t\taddTable(t" + tableName + ");\n" );
                generateSingletonClass( outputDirectory, tables[i], packageName );
            }

            // Generate view classes
            View[] views = database.getViews();
            StringBuffer addViews = new StringBuffer();
            for ( int i = 0; i < views.length; i++ )
            {
                String viewName = views[i].getName().substring( 1 );
                String viewClassName = viewName + "View";
                writer.write( "\tpublic " + viewClassName + " v" + viewName + " = " + viewClassName + ".getInstance();\n" );
                addViews.append( "\t\taddView(v" + viewName + ");\n" );
                generateSingletonClass( outputDirectory, views[i], packageName );
            }

            writer.write( "\n" );
            writer.write( "\tprivate " + className + "(String databaseName, int version) {\n" );
            writer.write( "\t\tsuper(databaseName, version);\n" );
            writer.write( addTables.toString() );
            writer.write( addViews.toString() );
            writer.write( "\t\tsetDatabaseMappings();\n" );
            writer.write( "\t}\n\n" );
            writer.write( "\tpublic static " + className + " getInstance() {\n" );
            writer.write( "\t\treturn " + databaseName + ";\n" );
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
