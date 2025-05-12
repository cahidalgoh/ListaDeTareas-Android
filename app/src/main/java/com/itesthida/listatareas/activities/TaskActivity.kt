package com.itesthida.listatareas.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.itesthida.listatareas.R
import com.itesthida.listatareas.data.Category
import com.itesthida.listatareas.data.CategoryDAO
import com.itesthida.listatareas.data.Task
import com.itesthida.listatareas.data.TaskDAO
import com.itesthida.listatareas.databinding.ActivityTaskBinding
import com.itesthida.listatareas.databinding.ActivityTaskListBinding

class TaskActivity : AppCompatActivity() {

    lateinit var binding: ActivityTaskBinding

    lateinit var category: Category
    lateinit var task: Task

    lateinit var taskDAO: TaskDAO
    lateinit var categoryDAO: CategoryDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 1.- Construimos el binding
        binding = ActivityTaskBinding.inflate(layoutInflater)
        // 2.- Actualizamos el layout, activity_task por el root del binding
        // setContentView(R.layout.activity_task)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 3.- Inicializamos las variables necedsarias
        // El CategoryDAO pasando el contexto
        categoryDAO = CategoryDAO(this)
        // Lo mimos para el TaskDAO
        taskDAO = TaskDAO(this)

        // 4.-Obtenemos el id de la categoría que lo pasan a través del intent
        // Y si no llega, -1 como valor por defecto
        val categoryId = intent.getLongExtra("CATEGORY_ID", -1L)

        // 5.- Obtenemos la categoría a partir del id que recuperamos anteriormente del intent
        category = categoryDAO.findCategoryTaskById(categoryId)!!

        // 6.- Llamamos al binding para establecer el comportamiento cuando se pulse el botón save
        binding.btnSaveTask.setOnClickListener {
            // 7.- Obtenemos el título que ha introducido el usuario en el edit text
            var tasktitle = binding.etTaskTitle.toString()

            // 8.- Creamos la tarea con los parámetros requeridos
            task = Task(-1L, tasktitle, false, category)

            // 9.- Guardamos la tarea
            taskDAO.insertTask(task)
            // 10.- Una vez guardada la tarea, cerramos el activity de la tarea
            finish()
        }
    }
}