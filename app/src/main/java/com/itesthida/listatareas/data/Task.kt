package com.itesthida.listatareas.data

data class Task(
    val idTask : Long,
    var titleTask : String,
    var done : Boolean,
    var category : Category
){

    companion object {
        // Constante para el di por defecto
        const val DEFAULT_ID = -1L
        // Constantes para los datos de la tabla, nombre de tabla y columnas
        const val TABLE_NAME = "TASK"

        const val COLUMN_NAME_ID = "ID"
        const val COLUMN_NAME_TITLE = "TITLE"
        const val COLUMN_NAME_DONE= "DONE"
        const val COLUMN_NAME_CATEGORY= "CATEGORY_ID"
    }
}
