package com.itesthida.listatareas.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.itesthida.listatareas.R
                                                        import com.itesthida.listatareas.adapters.TaskAdapter
import com.itesthida.listatareas.data.Category
import com.itesthida.listatareas.data.CategoryDAO
import com.itesthida.listatareas.data.Task
import com.itesthida.listatareas.data.TaskDAO
import com.itesthida.listatareas.databinding.ActivityTaskListBinding

class TaskListActivity : AppCompatActivity() {
    // Declaramos el binding
    lateinit var binding: ActivityTaskListBinding

    lateinit var categotyDAO: CategoryDAO
    lateinit var category: Category

    lateinit var taskDAO: TaskDAO
    lateinit var taskList: List<Task>

    // Adapter para los Tasks
    lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Construimos el binding
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        // Actualizamos el layout, activity_task_list por el root del binding
        // setContentView(R.layout.activity_task_list)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Inicializamos las variables necesarias
        // Para el categoryDAO y el taskDAO le pasamos como argumento el contexto
        categotyDAO = CategoryDAO(this)
        taskDAO = TaskDAO(this)

        // Este activity recibirá el id de la categoría
        val categoryId = intent.getLongExtra("CATEGORY_ID", -1)

        // Recuperamos la categoría
        category = categotyDAO.findCategoryTaskById(categoryId)!!
        // No es necesario recuperar las tareas
        // Las recuperamos en el inResume()
        taskList = emptyList()

        // Una vez que tenemos la lista de las tareas, pasamos a construir el adapter
        // pasando como parámetro la lista de tareas, la función lambda para el click y la función lambda para el checkbox
        adapter = TaskAdapter(taskList, { position ->

            // He hecho click en una tarea
            // Obtenemos la tarea pulsada
            val task = taskList[position]

            // Creamos el intent pasando el contexto y el activity al que queremos ir
            intent = Intent(this, TaskActivity::class.java)

            // Añadimos los parámetros para que puedan ser utilizados en la vista a la que queremos ir
            // Ya sea para una alta o una edición de una tarea
            intent.putExtra("CATEGORY_ID", category.idCategoryTask)
            intent.putExtra("TASK_ID", task.idTask)

            // Por último, iniciamos el start pasando el intent
            startActivity(intent)
        }, { position ->

            // He pulsado el check de una tarea
            // Obtenemos la tarea pulsada
            val task = taskList[position]

            // Cambiamos el valor de la propiedad done de la tarea
            // independientemente de si se marca o se desmarca el check
            task.done = !task.done

            // Actualizamos la tarea en la base de datos
            taskDAO.updateTask(task)

            // Actualizamos la vista
            reloadData()

        })

        // Si ya tenemos el binding, podemos acceder al recyclerView y decirle que su adapter es
        // el que tenemos creado previamente
        binding.recyclerView.adapter = adapter
        // También el layout manager de tipo vertical
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Establecemos el título de la pantalla en el actionBar
        supportActionBar?.title = category.titleCategoryTask
        // Habilitamos el botón volver
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Establecemos el evento del botón addTask
        binding.btnAddTask.setOnClickListener{
            // Creamos el intent pasando el contexto y el activity al que queremos ir
            intent = Intent(this, TaskActivity::class.java)

            // Añadimos el parámetro para que pueda ser utilizado en la vista a la que queremos ir
            intent.putExtra("CATEGORY_ID", category.idCategoryTask)

            // Por último, iniciamos el start pasando el intent
            startActivity(intent)
        }

    }

    // Sobreescribimos el métoodo onResume para refrescar la vista con las tareas
    override fun onResume() {
        super.onResume()
        // Actualizamos la vista
        reloadData()

    }

    /**
     * Función que actualiza la vista con las tareas, esta función es llamada cuando se añade una
     * tarea o se modifica.
     */
    fun reloadData(){
        // Obtenemos la lista de tareas de la categoría
        taskList = taskDAO.getAllTasksByCategory(category)
        // Llamamos al adapter para que actalice la vista
        adapter.updateItems(taskList)
    }

    // Función para establecer el comportamiento del botón volver
    // Al habilitar el botón, necesitamos sobreescribir la función onOptionsItemSelected
    // para que reponda de una manera personalizada al ser pulsado
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            // Si el item del menu en el que se ha pulsado es el botón home,
            // llamamos a la función finish que nos cierra la pantalla o acción
            android.R.id.home ->{
                finish()
                return true
            } else -> return super.onOptionsItemSelected(item)
        }
    }
}