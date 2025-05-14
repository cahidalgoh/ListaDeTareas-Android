package com.itesthida.listatareas.activities

import android.os.Bundle
import android.util.Log
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

        // 4.- Obtenemos el id de la categoría, si no llega un valor, -1 como valor por defecto
        val categoryId = intent.getLongExtra("CATEGORY_ID", -1L)

        // 7.- Obtenemos el id de la tarea, si no llega un valor, -1 como valor por defecto
        val taskId = intent.getLongExtra("TASK_ID", -1L)

        // 8.- Obtenemos la categoría a partir del id que recuperamos anteriormente del intent
        category = categoryDAO.findCategoryTaskById(categoryId)!!

        // 9.- También comrobamos si es una edición de una tarea
        if(taskId == Task.DEFAULT_ID){
            // Es un alta nueva de una tarea, se crea un objeto task
            task = Task(Task.DEFAULT_ID, "", false, category)
        } else{

            // Aquí podemos actualizar el texto del botón, en este caso para una actualización, UPDATE TASK
            binding.btnSaveTask.setText("Update Task")

            // Es edición de una tarea, otenemos la tarea a partir del taskId recuperado del intent
            task = taskDAO.findTaskById(taskId)!!

            // Actualizamos el valor del editText de la tarea para que luego pueda ser editada por el usuario
            binding.etTaskTitle.setText(task.titleTask)
        }

        // 10.- Llamamos al binding para establecer el comportamiento cuando se pulse el botón save
        binding.btnSaveTask.setOnClickListener {
            // 11.- Obtenemos el título que ha introducido el usuario en el edit text
            var tasktitle = binding.etTaskTitle.text.toString()

            // 12.- Ya tenemos el objeto Task creado en el paso 9, solo actualizamos el título
            task.titleTask = tasktitle

            // 13.- Guardamos o actualizamos la tarea
            if (task.idTask == Task.DEFAULT_ID){
                // Insertamos task
                taskDAO.insertTask(task)

                Log.i("NEW TASK", "Inserted a new TASK: $tasktitle")
            } else {
                // Actualizamos task
                taskDAO.updateTask(task)

                Log.i("UPDATE TASK", "Updated TASK with id: ${task.idTask}, new title: ${task.titleTask}")
            }

            // 14.- Una vez guardados los cambios, ya sea alta o modificación, cerramos el activity de la tarea
            finish()
        }
    }
}