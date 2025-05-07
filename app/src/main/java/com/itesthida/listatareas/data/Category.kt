package com.itesthida.listatareas.data

data class Category(
    val idCategoryTask : Long,
    var titleCategoryTask : String
){

    companion object {
        // Constantes para los datos de la tabla, nombre de tabla y columnas
        const val TABLE_NAME = "CATEGORY"
        const val COLUMN_NAME_ID = "ID"
        const val COLUMN_NAME_TITLE = "TITLE"
    }
}
