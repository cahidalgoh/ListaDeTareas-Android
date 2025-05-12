package com.itesthida.listatareas.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.itesthida.listatareas.data.Category
import com.itesthida.listatareas.data.Task

// La clase hereda de SQLiteOpenHelper para la conexión a la base de datos
// Pasando el contexto, el nombre de la base de datos, la manera en que se recupera los datos de la base de datos
// y la versión de la base datos

class DatabaseManager (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object {
        // If you change the database schema, you must increment the database version.
        // Al cambiar la versión, se ejecutará el onUpgrade, con ello eliminará las tablas que
        // existen en la base de datos y las creará nuevamente
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "FeedReader.db"

        // Constante para la creación de la tabla CATEGORY
        private const val SQL_CREATE_CATEGORY_TASK =
            "CREATE TABLE ${Category.TABLE_NAME} (" +
                    "${Category.COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${Category.COLUMN_NAME_TITLE} TEXT)"

        private const val SQL_DELETE_CATEGORY_TASK = "DROP TABLE IF EXISTS ${Category.TABLE_NAME}"

        // Constante para la creación de la tabla TASK
        private const val SQL_CREATE_TASK =
            "CREATE TABLE ${Task.TABLE_NAME} (" +
                    "${Task.COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${Task.COLUMN_NAME_TITLE} TEXT," +
                    "${Task.COLUMN_NAME_DONE} INTEGER," +
                    "${Task.COLUMN_NAME_CATEGORY} INTEGER," +
                    "FOREIGN KEY (${Task.COLUMN_NAME_CATEGORY}) " +
                    "REFERENCES ${Category.TABLE_NAME} (${Category.COLUMN_NAME_ID}))"

        private const val SQL_DELETE_TASK = "DROP TABLE IF EXISTS ${Task.TABLE_NAME}"
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_CATEGORY_TASK)
        db.execSQL(SQL_CREATE_TASK)
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     *
     *
     * The SQLite ALTER TABLE documentation can be found
     * [here](http://sqlite.org/lang_altertable.html). If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     *
     *
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     *
     *
     *
     * *Important:* You should NOT modify an existing migration step from version X to X+1
     * once a build has been released containing that migration step.  If a migration step has an
     * error and it runs on a device, the step will NOT re-run itself in the future if a fix is made
     * to the migration step.
     *
     * For example, suppose a migration step renames a database column from `foo` to
     * `bar` when the name should have been `baz`.  If that migration step is released
     * in a build and runs on a user's device, the column will be renamed to `bar`.  If the
     * developer subsequently edits this same migration step to change the name to `baz` as
     * intended, the user devices which have already run this step will still have the name
     * `bar`.  Instead, a NEW migration step should be created to correct the error and rename
     * `bar` to `baz`, ensuring the error is corrected on devices which have already run
     * the migration step with the error.
     *
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onDestroy(db)
        onCreate(db)
    }

    /**
     * Función que se llama cuando existe una modificación en la base de datos
     *
     * @param db The database.
     */
    fun onDestroy(db: SQLiteDatabase){
        db.execSQL(SQL_DELETE_TASK)
        db.execSQL(SQL_DELETE_CATEGORY_TASK)
    }
}