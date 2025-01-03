package com.example.guardarcontactos

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import androidx.appcompat.app.AppCompatActivity
import com.example.guardarcontactos.databinding.ActivityEditContactBinding

class UpdateContact : AppCompatActivity() {
    private lateinit var binding: ActivityEditContactBinding
    private lateinit var db: ContactDataBase
    private var noteID: Int = -1 // Armazena o ID do contacto (se existir)
    private var imagemUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ContactDataBase(this)

        // Verificar se há um ID passado pela Intent
        noteID = intent.getIntExtra("contact_id", -1)
        if (noteID != -1) {
            // Carregar os dados do contacto existente
            val contacto = db.getNoteByID(noteID)
            binding.nomeEditText.setText(contacto.nome)
            binding.numeroEditText.setText(contacto.numero.toString())
            binding.moradaEditText.setText(contacto.morada)
            imagemUri = Uri.parse(contacto.imagem) // Assume que a imagem é uma URI
            if (imagemUri != null) {
                Glide.with(this).load(imagemUri).into(binding.imagemContactoView) // Mostrar a imagem
            }
        }

        binding.botaoGuardar.setOnClickListener {
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

            if (noteID == -1) {
                // Criar um novo contacto
                val contacto = Contacto(0, nome, numero, morada, imagemUri?.toString() ?: "")
                db.inserirContacto(contacto)
                Toast.makeText(this, "Contacto adicionado", Toast.LENGTH_LONG).show()
            } else {
                // Atualizar contacto existente
                val contacto = Contacto(noteID, nome, numero, morada, imagemUri?.toString() ?: "")
                db.updateNote(contacto)
                Toast.makeText(this, "Contacto atualizado", Toast.LENGTH_LONG).show()
            }
            finish()
        }
    }
}
