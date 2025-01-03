package com.example.guardarcontactos


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.guardarcontactos.databinding.ActivityAddContactBinding


class AddContact : AppCompatActivity() {

    private lateinit var binding: ActivityAddContactBinding
    private lateinit var db: ContactDataBase
    private var imagemUri: Uri? = null // Guardar o URI da imagem selecionada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ContactDataBase(this)

        // Evento de clique no ImageView para selecionar uma imagem
        binding.imagemContactoView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

        binding.botaoGuardar.setOnClickListener {
            // Obter os novos valores dos campos
            val nome = binding.nomeEditText.text.toString()
            val numeroTexto = binding.numeroEditText.text.toString()
            val morada = binding.moradaEditText.text.toString()

            if (nome.isEmpty() || numeroTexto.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val numero = try {
                numeroTexto.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Por favor, insira um número válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Criar o objeto Contacto
            val contacto =  Contacto(0, nome, numero, morada, imagemUri.toString())
            //Inserir contacto na base de dados
            db.inserirContacto(contacto)
            // Encerrar a atividade atual após salvar o contacto
            finish()
            // Mostrar uma mensagem  a dizer que foi salvo o contacto
            Toast.makeText(this, "Contacto guardado", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            imagemUri = data?.data // Obtém o URI da imagem selecionada
            binding.imagemContactoView.setImageURI(imagemUri) // Mostra a imagem no ImageView
        }
    }
}
