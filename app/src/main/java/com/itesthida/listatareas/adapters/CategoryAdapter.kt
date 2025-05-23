package com.itesthida.listatareas.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.itesthida.listatareas.data.Category
import com.itesthida.listatareas.databinding.ItemCategoryBinding

class CategoryAdapter(
    var items : List<Category>,
    val onItemClick : (position : Int) -> Unit,
    val onItemEditClick : (position : Int) -> Unit,
    val onItemDeleteClick : (position : Int) -> Unit
): Adapter<CategoryViewHolder>(){
    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.
     *
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     *
     * The new ViewHolder will be used to display items of the adapter using
     * [.onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary [View.findViewById] calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .getItemViewType
     * @see .onBindViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        // Obtenemos la vista de la celda en la que se va a mostrar el categorye, a partir del LayoutInflater pasando el contexto (parent.context),
        // hacemos el inflate pasando el layaout (item_category), el parent y un booleano que indica si se ancla o no al padre
        // val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)

        // 1 - Para el acceso a los componentes del layout
        // Ahora en lugar de inflar la vista a partir del xml, se lo pedimos al binding
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        // Devolvemos el CategoryViewHolder en la que pasamos la vista de la celda
        // return CategoryViewHolder(view)

        // 2 - Para el acceso a los componentes del layout
        // Devolvemos el vieHolder ahora pasando el binding
        return CategoryViewHolder(binding)

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the `position` parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use [ViewHolder.getAdapterPosition] which will
     * have the updated adapter position.
     *
     * Override [.onBindViewHolder] instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        // Esta función se ejecuta cada vez que se va a visualizar una celda
        // Obtenemos el categorye que se va a pintar en la posición que nos pasan como parámetro
        val category = items[position]
        // Al holder le indicamos el category a pintar
        holder.render(category)

        // Para el detalle, cuando se pulse uno de los items de categoryes
        holder.itemView.setOnClickListener {
            // Llamada a la función onItemClick, pasándole la posición del item pulsado
            onItemClick(position)
        }
        // Cualdo pulsemos el botón editar de una categoría
        holder.binding.btnEditCategory.setOnClickListener {
            // Llamada a la función onItemClick, pasándole la posición del item pulsado
            onItemEditClick(position)
        }
        // Cualdo pulsemos el botón delete de una categoría
        holder.binding.btnDeleteCategory.setOnClickListener {
            // Llamada a la función onItemClick, pasándole la posición del item pulsado
            onItemDeleteClick(position)
        }
    }

    /**
     * Función que actualiza el listado de categoryes que se muestra en pantalla
     * Recibe un listado nuevo con los categoryes a listar
     */
    fun updateItems(items : List<Category>){
        // Los items son iguales a los items recibidos como parámetro
        this.items = items
        // Notificamos al adapter que el conjunto de datos ha cambiado
        notifyDataSetChanged()
    }

}


/**
 * ViewHolder que se le pasa a la clase SuperHeroAdapter
 * Esta clase, recibe un View, extiende de ViewHolder del RecyclerView al cual se le pasa el view
 */
// 3 - Para el acceso a los componentes del layout
// Cambiamos el objeto que recibe en el constructor (ub¡n View por un ItemCategoryBinding) y también actualizamos el origen del view en el padre
// cambiamos el view por la vista que representa toda la celda, ente caso es un LinearLayout
// Para poder acceder al binding y las propiedades de la vista dentro de la clase, hay declarar como una propiedad en el constructor, le ponemos val delante del nombre del objeto en el constructor
// class CategoryViewHolder(view : View) : ViewHolder(view){
class CategoryViewHolder(val binding : ItemCategoryBinding) : ViewHolder(binding.root){
    // Declaramos y obtenemos los componentes de la vista
    // 4 - Para el acceso a los componentes del layout
    // Ya no es necesario la declaración de los componentes de la vista

    // var tvName : TextView = view.findViewById(R.id.tvName)
    // var ivAvatar : ImageView = view.findViewById(R.id.ivAvatar)

    // Recibe el categorye, del cual se van a obtener los datos que se van a pintar en la vista
    fun render(category: Category){
        // 5 - Para el acceso a los componentes del layout
        // Accedemos a los componentes de la vista a través del binding
        // Pintamos el nombre del superhoeroe en el componente tvName
        // tvName.text = category.name
        binding.tvTitleCategory.text = category.titleCategoryTask
    }
}
