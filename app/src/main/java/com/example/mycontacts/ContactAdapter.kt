import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.guardarcontactos.ContactDataBase
import com.example.guardarcontactos.Contacto
import com.example.guardarcontactos.UpdateContact
import com.example.guardarcontactos.R
import com.bumptech.glide.Glide

class ContactAdapter(private var contactos: List<Contacto>, private val context: Context) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private val db: ContactDataBase = ContactDataBase(context)

    // Classe interna para representar os elementos de um item da lista
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeView: TextView = itemView.findViewById(R.id.nomeContacto)
        val numeroView: TextView = itemView.findViewById(R.id.numeroContacto)
        val imageView: ImageView = itemView.findViewById(R.id.imagemContacto)
        val updateButton: ImageView = itemView.findViewById(R.id.botaoEditarContacto)
        val deleteButton: ImageView = itemView.findViewById(R.id.botaoApagarContacto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        // Infla o layout do item da lista e cria o ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        // Obtém o contacto atual com base na posição
        val contacto = contactos[position]

        // Define os valores do nome, número e imagem
        holder.nomeView.text = contacto.nome
        holder.numeroView.text = contacto.numero.toString()

        // Usando Glide para carregar a imagem (assumindo que 'contacto.imagem' é uma URI ou caminho)
        Glide.with(holder.itemView.context)
            .load(contacto.imagem) // Pode ser o caminho da imagem ou um URI
            .into(holder.imageView)

        // Configura o botão de atualização para abrir a atividade de edição do contacto
        holder.updateButton.setOnClickListener {
            // Passa o ID do contacto para a atividade de atualização
            val intent = Intent(holder.itemView.context, UpdateContact::class.java).apply {
                putExtra("contact_id", contacto.id)
            }
            holder.itemView.context.startActivity(intent)
        }


        // Configura o botão de eliminação para apagar a nota e atualizar a lista
        holder.deleteButton.setOnClickListener {
            db.deleteNote(contacto.id) // Elimina a nota da base de dados
            refreshData(db.obterTodosContactos()) // Atualiza os dados da lista
            Toast.makeText(holder.itemView.context, "Contacto eliminado", Toast.LENGTH_SHORT).show() // Notifica o utilizador
        }

    }

    override fun getItemCount(): Int {
        // Retorna o número total de notas na lista
        return contactos.size
    }

    fun refreshData(newNotes: List<Contacto>) {
        // Atualiza a lista de notas e notifica o RecyclerView para refrescar a exibição
        contactos = newNotes
        notifyDataSetChanged()
    }


}
