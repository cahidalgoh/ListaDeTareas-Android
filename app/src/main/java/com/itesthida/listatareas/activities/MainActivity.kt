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
        adapter = CategoryAdapter(categoryList){
            // He pulsado una categoría
        }

        // Asignamos el adapter
        binding.recyclerView.adapter = adapter
        // Le ponemos el layaout manager
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Cuando se pulse el botón de añadir categoría, llamamos a la función showCategory
        binding.btnAddCategory.setOnClickListener {
            showCategoryDialog()
        }
    }

    // Función que muestra un diálogo, la utilizaremos para crear una nueva categorí
    fun showCategoryDialog(){

        // Inflamos el binidig del dialog_create_category
        val dialogBinding = DialogCreateCategoryBinding.inflate(layoutInflater)

        MaterialAlertDialogBuilder(this)
            .setTitle("Create Category")
            //.setMessage("Hello")
            .setView(dialogBinding.root)// Se lo pasamos a la vista en la que queremos mostrar
            .setPositiveButton(android.R.string.ok, { dialog, which ->
                val title = dialogBinding.etCategoryTitle.text.toString()

                val category = Category(-1, title)

                // Insertamos la nueva categoría
                categoryDAO.insertCategory(category)

                Log.i("DIALOG_CATEGORY", "Título de la categoría a crerar: $title")

                // Después de guardar la nueva categoría creada, actualizamos la vista
                loadCategories()
            })
            .setNegativeButton(android.R.string.cancel, null)
            .setIcon(R.drawable.ic_add)
            .show()
    }

    // Función para cargar las categorías en la vista
    fun loadCategories(){
        categoryList = categoryDAO.getAllCategoryTask()
        // Actualizamos la vista llamando al adapter para refrescar el recyclerview
        adapter.updateItems(categoryList)
    }
}