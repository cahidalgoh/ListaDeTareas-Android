package com.itesthida.listatareas.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.itesthida.listatareas.R
import com.itesthida.listatareas.adapters.CategoryAdapter
import com.itesthida.listatareas.data.Category
import com.itesthida.listatareas.data.CategoryDAO
import com.itesthida.listatareas.databinding.ActivityMainBinding
import com.itesthida.listatareas.databinding.DialogCreateCategoryBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    // Para configurar el recyclerView
    lateinit var adapter: CategoryAdapter
    var categoryList: List<Category> = emptyList()

    lateinit var categoryDAO: CategoryDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        //setContentView(R.layout.activity_main)
        // Inicializamos directamente por la vista en lugar del identificador de la vista
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializamos el DAO pasando el contexto
        categoryDAO = CategoryDAO(this)

        // Obtenemos todas las categorías que existen en la base de datos
        categoryList = categoryDAO.getAllCategoryTask()

        // Le pasamos la lista de categorías y la función para cuando se haga clikc en una categoría
        adapter = CategoryAdapter(categoryList, {
            // He pulsado una categoría
        }, { position ->
            // Edit category
            // Obtenemos la categoría a modificar
            val category = categoryList[position]

            // Mostramos el dialog category para modificar la categoría
            showCategoryDialog(category)

        }, { position ->
            // Delete category
            // Mostramos el dialogo para confirmar si quiere borrar
            showDeleteCategoryWarning(position)
        })

        // Asignamos el adapter
        binding.recyclerView.adapter = adapter
        // Le ponemos el layaout manager
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Cuando se pulse el botón de añadir categoría, llamamos a la función showCategory
        binding.btnAddCategory.setOnClickListener {
            // PAra el alta n ueva de una categoría, le pasamos un objeto category con el id -1
            showCategoryDialog(Category(-1L,""))
        }
    }

    // Función que muestra un diálogo, la utilizaremos para crear una
    // nueva categoría o para editarla si ya existe
    fun showCategoryDialog(category: Category){

        // Inflamos el binidig del dialog_create_category
        val dialogBinding = DialogCreateCategoryBinding.inflate(layoutInflater)

        dialogBinding.etCategoryTitle.setText(category.titleCategoryTask)

        // Variables para el dialog category, por defecto, un alta de categoría
        var dialogTitle = R.string.create_category_text
        var dialogCategoryIcon = R.drawable.ic_add

        // Es una edición de categoría?
        if (category.idCategoryTask != -1L){
            // Actualizamos los valores necesarios para utilizarlos en el dialog para una edición de categoría
            dialogTitle = R.string.edit_category_text
            dialogCategoryIcon = R.drawable.ic_edit
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(dialogTitle)
            //.setMessage("Hello")
            .setView(dialogBinding.root)// Se lo pasamos a la vista en la que queremos mostrar
            .setPositiveButton(android.R.string.ok, { dialog, which ->

                category.titleCategoryTask = dialogBinding.etCategoryTitle.text.toString()

                if (category.idCategoryTask != -1L){

                    // Categoría existe
                    // Modificamos la categoría
                    categoryDAO.updateCategory(category)

                } else{

                    // Nueva categoría
                    // Insertamos la nueva categoría
                    categoryDAO.insertCategory(category)

                }
                //val category = Category(-1, title)

                Log.i("DIALOG_CATEGORY", "Título de la categoría a crerar: $title")

                // Después de guardar la nueva categoría creada, actualizamos la vista
                loadCategories()
            })
            .setNegativeButton(android.R.string.cancel, null)
            .setIcon(dialogCategoryIcon)
            .show()
    }

    // Función para mostrar al usuario la advertencia al borrar una categoría
    fun showDeleteCategoryWarning(position : Int){

        // Obtenemos la categoría a borrar
        val category = categoryList[position]

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.delete_category_text))
            .setMessage(getString(R.string.messageDeleteWarning, "${category.titleCategoryTask}"))
            .setPositiveButton(android.R.string.ok, { dialog, which ->
                // Delete category
                // Llamada al dao para efectuar el borrado
                categoryDAO.deleteCategory(category)

                // Actualizamos la vista de las categorías
                loadCategories()

            })
            .setNegativeButton(android.R.string.cancel, null)
            .setIcon(R.drawable.ic_delete)
            .show()

    }

    // Función para cargar las categorías en la vista
    fun loadCategories(){
        categoryList = categoryDAO.getAllCategoryTask()
        // Actualizamos la vista llamando al adapter para refrescar el recyclerview
        adapter.updateItems(categoryList)
    }
}