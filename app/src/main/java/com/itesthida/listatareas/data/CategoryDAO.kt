package com.itesthida.listatareas.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.itesthida.listatareas.utils.DatabaseManager

// Esta clase necesita el context para poder pasarlo al DatabaseManager y así poder construirlo
class CategoryDAO(val context: Context) {

    private lateinit var db : SQLiteDatabase

    private fun  openConnection(){
        db = DatabaseManager(context).writableDatabase
    }

    private fun  closeConnection(){
        db.close()
    }

    // Insert Category
    fun insertCategory (category: Category){

        // Abrimos conexión a la base de datos
        openConnection()

        try {

            // Create a new map of values, where column names are the keys
            // ContentValues, es el objeto que necesita SQLite en la que indicamos columna-valor para luego
            // pedirle que inserte el registro con todos los pares columna-valor que se han añadido
            val values = ContentValues()
            values.put(Category.COLUMN_NAME_TITLE, category.titleCategoryTask)

            // Insert the new row, returning the primary key value of the new row
            // Se llama al db, que ya tiene los permisos de escritura
            val newRowId = db.insert(Category.TABLE_NAME, null, values)

            Log.i("DATABASE", "Inserted a Category with id: $newRowId")

        } catch (ex : Exception){
            ex.printStackTrace()
        } finally {
            // Al finalizar siempre cerramos la conexión con la base de datos
            closeConnection()
        }

    }

    // Update Category
    fun updateCategory (category: Category){

        // Abrimos conexión a la base de datos
        openConnection()

        try {
            // New value for one column
            val values = ContentValues().apply {
                put(Category.COLUMN_NAME_TITLE, category.titleCategoryTask)
            }

            // Which row to update, based on the title
            val selection = "${Category.COLUMN_NAME_ID} = ${category.idCategoryTask}"
            val selectionArgs = null //arrayOf("MyOldTitle")
            // Issue SQL statement
            val count = db.update(// Llamda a base de datos
                Category.TABLE_NAME,
                values,
                selection,
                selectionArgs
            )

            Log.i("DATABASE", "Updated Category with id: ${category.idCategoryTask}")

        } catch (ex : Exception){
            ex.printStackTrace()
        } finally {
            // Al finalizar siempre cerramos la conexión con la base de datos
            closeConnection()
        }

    }

    // Delete Category
    fun deleteCategory (category: Category){

        // Abrimos conexión a la base de datos
        openConnection()

        try {
            // Define 'where' part of query
            val selection = "${Category.COLUMN_NAME_ID} = ${category.idCategoryTask}"
            // Specify arguments in placeholder
            val selectionArgs = null //arrayOf("MyOldTitle")
            // Issue SQL statement
            var deletedRows = db.delete(Category.TABLE_NAME, selection, selectionArgs)

            Log.i("DATABASE", "Deleted Category with id: ${category.idCategoryTask}")

        } catch (ex : Exception){
            ex.printStackTrace()
        } finally {
            // Al finalizar siempre cerramos la conexión con la base de datos
            closeConnection()
        }

    }

    // Find Category
    fun findCategoryTaskById (id: Long) : Category? {

        // Abrimos conexión a la base de datos
        openConnection()

        var category : Category? = null
        try {
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            val projection = arrayOf(
                Category.COLUMN_NAME_ID,
                Category.COLUMN_NAME_TITLE
            )

            // Filter results WHERE "id" = 'id'
            val selection = "${Category.COLUMN_NAME_ID} = $id"
            val selectionArgs = null// arrayOf("My Title")

            // How you want the results sorted in the resulting Cursor
            val sortOrder = null//"${FeedEntry.COLUMN_NAME_SUBTITLE} DESC"

            val cursor = db.query(
                Category.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
            )

            if (cursor.moveToNext()){
                // El cursor tiene el registro que coincide con el id que buscamos
                // Creamos el objeto Category con los datos recuperados

                // Para obtener los datos de cada columna con el tipo de dato que corresponde
                // Podemos pasarle el índice de la columna que queremos recuperar a partir del nombre de la columna
                // // Es decir. le preguntamos al cursor, ¿Cuál es el índice de la columna XXXXX?
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Category.COLUMN_NAME_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(Category.COLUMN_NAME_TITLE))
                category = Category(id, title)
            }
            // Cerramos el cursos
            cursor.close()

            Log.i("DATABASE", "Selected Category with id: $id")

        } catch (ex : Exception){
            ex.printStackTrace()
        } finally {
            // Al finalizar siempre cerramos la conexión con la base de datos
            closeConnection()
        }
        return category
    }

    // Find All Categories
    fun getAllCategoryTask () : List<Category> {

        // Abrimos conexión a la base de datos
        openConnection()

        var categoryList : MutableList<Category> = mutableListOf()
        try {
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            val projection = arrayOf(
                Category.COLUMN_NAME_ID,
                Category.COLUMN_NAME_TITLE
            )

            // Filter results WHERE "id" = 'id'
            val selection = null//"${Category.COLUMN_NAME_ID} = $id"
            val selectionArgs = null// arrayOf("My Title")

            // How you want the results sorted in the resulting Cursor
            val sortOrder = null//"${FeedEntry.COLUMN_NAME_SUBTITLE} DESC"

            val cursor = db.query(
                Category.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
            )

            // Mientras haya categorias, creamos una categoría nueva y la añadimos a la lista
            while (cursor.moveToNext()){
                // El cursor tiene el registro que coincide con el id que buscamos
                // Creamos el objeto Category con los datos recuperados

                // Para obtener los datos de cada columna con el tipo de dato que corresponde
                // Podemos pasarle el índice de la columna que queremos recuperar a partir del nombre de la columna
                // // Es decir. le preguntamos al cursor, ¿Cuál es el índice de la columna XXXXX?
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Category.COLUMN_NAME_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(Category.COLUMN_NAME_TITLE))
                var category = Category(id, title)

                // Aadimos la categoría a la lista
                categoryList.add(category)
            }
            // Cerramos el cursos
            cursor.close()

            Log.i("DATABASE", "Selected ${categoryList.size} Categories")

        } catch (ex : Exception){
            ex.printStackTrace()
        } finally {
            // Al finalizar siempre cerramos la conexión con la base de datos
            closeConnection()
        }
        return categoryList

    }
}