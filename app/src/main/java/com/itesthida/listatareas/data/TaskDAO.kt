package com.itesthida.listatareas.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.itesthida.listatareas.utils.DatabaseManager

// Esta clase necesita el context para poder pasarlo al DatabaseManager y así poder construirlo
class TaskDAO(val context: Context) {

    private lateinit var db : SQLiteDatabase

    private fun  openConnection(){
        db = DatabaseManager(context).writableDatabase
    }

    private fun  closeConnection(){
        db.close()
    }

    // Insert Task
    fun insertTask (task: Task){

        // Abrimos conexión a la base de datos
        openConnection()

        try {

            // Create a new map of values, where column names are the keys
            // ContentValues, es el objeto que necesita SQLite en la que indicamos columna-valor para luego
            // pedirle que inserte el registro con todos los pares columna-valor que se han añadido
            val values = ContentValues()
            values.put(Task.COLUMN_NAME_TITLE, task.titleTask)
            values.put(Task.COLUMN_NAME_DONE, task.done)
            values.put(Task.COLUMN_NAME_CATEGORY, task.category.idCategoryTask)

            // Insert the new row, returning the primary key value of the new row
            // Se llama al db, que ya tiene los permisos de escritura
            val newRowId = db.insert(Task.TABLE_NAME, null, values)

            Log.i("DATABASE", "Inserted a Task with id: $newRowId")

        } catch (ex : Exception){
            ex.printStackTrace()
        } finally {
            // Al finalizar siempre cerramos la conexión con la base de datos
            closeConnection()
        }

    }

    // Update Task
    fun updateTask (task: Task){

        // Abrimos conexión a la base de datos
        openConnection()

        try {
            // New value for one column
            val values = ContentValues().apply {
                put(Task.COLUMN_NAME_TITLE, task.titleTask)
                put(Task.COLUMN_NAME_DONE, task.done)
                put(Task.COLUMN_NAME_CATEGORY, task.category.idCategoryTask)
            }

            // Which row to update, based on the title
            val selection = "${Task.COLUMN_NAME_ID} = ${task.idTask}"
            val selectionArgs = null //arrayOf("MyOldTitle")
            // Issue SQL statement
            val count = db.update(// Llamda a base de datos
                Task.TABLE_NAME,
                values,
                selection,
                selectionArgs
            )

            Log.i("DATABASE", "Updated Task with id: ${task.idTask}")

        } catch (ex : Exception){
            ex.printStackTrace()
        } finally {
            // Al finalizar siempre cerramos la conexión con la base de datos
            closeConnection()
        }

    }

    // Delete Task
    fun deleteTask (task: Task){

        // Abrimos conexión a la base de datos
        openConnection()

        try {
            // Define 'where' part of query
            val selection = "${Task.COLUMN_NAME_ID} = ${task.idTask}"
            // Specify arguments in placeholder
            val selectionArgs = null //arrayOf("MyOldTitle")
            // Issue SQL statement
            var deletedRows = db.delete(Task.TABLE_NAME, selection, selectionArgs)

            Log.i("DATABASE", "Deleted Task with id: ${task.idTask}")

        } catch (ex : Exception){
            ex.printStackTrace()
        } finally {
            // Al finalizar siempre cerramos la conexión con la base de datos
            closeConnection()
        }

    }

    // Find Task
    fun findTaskById (id: Long) : Task? {

        // Abrimos conexión a la base de datos
        openConnection()

        var task : Task? = null
        try {
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            val projection = arrayOf(
                Task.COLUMN_NAME_ID,
                Task.COLUMN_NAME_TITLE,
                Task.COLUMN_NAME_DONE,
                Task.COLUMN_NAME_CATEGORY
            )

            // Filter results WHERE "id" = 'id'
            val selection = "${Task.COLUMN_NAME_ID} = $id"
            val selectionArgs = null// arrayOf("My Title")

            // How you want the results sorted in the resulting Cursor
            val sortOrder = null//"${FeedEntry.COLUMN_NAME_SUBTITLE} DESC"

            val cursor = db.query(
                Task.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
            )

            if (cursor.moveToNext()){
                // El cursor tiene el registro que coincide con el id que buscamos
                // Creamos el objeto Task con los datos recuperados

                // Para obtener los datos de cada columna con el tipo de dato que corresponde
                // Podemos pasarle el índice de la columna que queremos recuperar a partir del nombre de la columna
                // // Es decir. le preguntamos al cursor, ¿Cuál es el índice de la columna XXXXX?
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_TITLE))
                // Para hacer un valor boolean, preguntamos si en base de datos tenemos para done != 0,
                // si se cumple, será true (1) de lo contrario será false (0)
                val done = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_DONE)) != 0
                val category = cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_CATEGORY))

                // Obtenemos la categoría a partir del id de la categoría
                val categoryTask = CategoryDAO(context).findCategoryTaskById(category)!!

                task = Task(id, title, done, categoryTask)
            }
            // Cerramos el cursos
            cursor.close()

            Log.i("DATABASE", "Selected Task with id: $id")

        } catch (ex : Exception){
            ex.printStackTrace()
        } finally {
            // Al finalizar siempre cerramos la conexión con la base de datos
            closeConnection()
        }
        return task
    }

    // Find All Tasks
    fun getAllTasks () : List<Task> {

        // Abrimos conexión a la base de datos
        openConnection()

        var taskList : MutableList<Task> = mutableListOf()
        try {
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            val projection = arrayOf(
                Task.COLUMN_NAME_ID,
                Task.COLUMN_NAME_TITLE,
                Task.COLUMN_NAME_DONE,
                Task.COLUMN_NAME_CATEGORY
            )

            // Filter results WHERE "id" = 'id'
            val selection = null//"${Task.COLUMN_NAME_ID} = $id"
            val selectionArgs = null// arrayOf("My Title")

            // How you want the results sorted in the resulting Cursor
            val sortOrder = null//"${FeedEntry.COLUMN_NAME_SUBTITLE} DESC"

            val cursor = db.query(
                Task.TABLE_NAME,   // The table to query
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
                // Creamos el objeto Task con los datos recuperados

                // Para obtener los datos de cada columna con el tipo de dato que corresponde
                // Podemos pasarle el índice de la columna que queremos recuperar a partir del nombre de la columna
                // // Es decir. le preguntamos al cursor, ¿Cuál es el índice de la columna XXXXX?
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_TITLE))
                // Para hacer un valor boolean, preguntamos si en base de datos tenemos para done != 0,
                // si se cumple, será true (1) de lo contrario será false (0)
                val done = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_DONE)) != 0
                val category = cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_TITLE))

                // Obtenemos la categoría a partir del id de la categoría
                val categoryTask = CategoryDAO(context).findCategoryTaskById(category)!!

                val task = Task(id, title, done, categoryTask)

                // Aadimos la categoría a la lista
                taskList.add(task)
            }
            // Cerramos el cursos
            cursor.close()

            Log.i("DATABASE", "Selected ${taskList.size} Tasks")

        } catch (ex : Exception){
            ex.printStackTrace()
        } finally {
            // Al finalizar siempre cerramos la conexión con la base de datos
            closeConnection()
        }
        return taskList

    }

    /**
     * @return a list of tasks grouped by a category
     */
    // Find All Tasks by Category
    fun getAllTasksByCategory (category : Category) : List<Task> {

        // Abrimos conexión a la base de datos
        openConnection()

        var taskList : MutableList<Task> = mutableListOf()
        try {
            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            val projection = arrayOf(
                Task.COLUMN_NAME_ID,
                Task.COLUMN_NAME_TITLE,
                Task.COLUMN_NAME_DONE,
                Task.COLUMN_NAME_CATEGORY
            )

            // Filter results WHERE "id" = 'id'
            val selection = "${Task.COLUMN_NAME_CATEGORY} = ${category.idCategoryTask}"
            val selectionArgs = null// arrayOf("My Title")

            // How you want the results sorted in the resulting Cursor
            val sortOrder = "${Task.COLUMN_NAME_DONE}"//"${FeedEntry.COLUMN_NAME_SUBTITLE} DESC"

            val cursor = db.query(
                Task.TABLE_NAME,   // The table to query
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
                // Creamos el objeto Task con los datos recuperados

                // Para obtener los datos de cada columna con el tipo de dato que corresponde
                // Podemos pasarle el índice de la columna que queremos recuperar a partir del nombre de la columna
                // // Es decir. le preguntamos al cursor, ¿Cuál es el índice de la columna XXXXX?
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_TITLE))
                // Para hacer un valor boolean, preguntamos si en base de datos tenemos para done != 0,
                // si se cumple, será true (1) de lo contrario será false (0)
                val done = cursor.getInt(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_DONE)) != 0
                val categoryId = cursor.getLong(cursor.getColumnIndexOrThrow(Task.COLUMN_NAME_CATEGORY))

                // Obtenemos la categoría a partir del id de la categoría
                val categoryTask = CategoryDAO(context).findCategoryTaskById(categoryId)!!

                val task = Task(id, title, done, categoryTask)

                // Aadimos la categoría a la lista
                taskList.add(task)
            }
            // Cerramos el cursor
            cursor.close()

            Log.i("DATABASE", "Selected ${taskList.size} Tasks")

        } catch (ex : Exception){
            ex.printStackTrace()
        } finally {
            // Al finalizar siempre cerramos la conexión con la base de datos
            closeConnection()
        }
        return taskList

    }
}