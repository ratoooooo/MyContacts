package com.example.guardarcontactos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.jetbrains.annotations.NotNull

class ContactDataBase(contexto: Context) :
    SQLiteOpenHelper(contexto, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "contactapp.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "contacts"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NOME = "nome"
        private const val COLUMN_NUMERO = "numero"
        private const val COLUMN_MORADA = "morada"
        private const val COLUMN_IMAGEM = "imagem"
    }

    override fun onCreate(bd: SQLiteDatabase?) {
        // Criar a tabela na base de dados
        val criarTabela = """
            CREATE TABLE $TABLE_NAME(
                $COLUMN_ID INTEGER PRIMARY KEY, 
                $COLUMN_NOME TEXT, 
                $COLUMN_NUMERO INTEGER, 
                $COLUMN_MORADA TEXT,
                $COLUMN_IMAGEM TEXT
            )
        """.trimIndent()
        // Executar a query de criação da tabela
        bd?.execSQL(criarTabela)
    }

    override fun onUpgrade(bd: SQLiteDatabase?, versaoAntiga: Int, novaVersao: Int) {
        // Atualizar a base de dados: remover a tabela antiga e criar uma nova
        val apagarTabela = "DROP TABLE IF EXISTS $TABLE_NAME"
        bd?.execSQL(apagarTabela)
        onCreate(bd)
    }

    fun inserirContacto(contacto: Contacto) {
        // Inserir um novo contacto na base de dados
        val bd = writableDatabase
        val valores = ContentValues().apply {
            put(COLUMN_NOME, contacto.nome)
            put(COLUMN_NUMERO, contacto.numero)
            put(COLUMN_MORADA, contacto.morada)
            put(COLUMN_IMAGEM, contacto.imagem)
        }
        bd.insert(TABLE_NAME, null, valores)
        bd.close()
    }

    fun obterTodosContactos(): List<Contacto> {
        // Obter todos os contactos armazenados na base de dados
        val listaContactos = mutableListOf<Contacto>()
        val bd = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val cursor = bd.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME))
            val numero = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NUMERO))
            val morada = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MORADA))
            val imagem = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGEM))

            val contacto = Contacto(id, nome, numero, morada, imagem)
            listaContactos.add(contacto)
        }
        cursor.close()
        bd.close()
        return listaContactos
    }

    fun updateNote(contacto: Contacto) {
        // Atualiza um contacto existente com base no ID
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_NOME, contacto.nome)
            put(COLUMN_NUMERO, contacto.numero)
            put(COLUMN_MORADA, contacto.morada)
            put(COLUMN_IMAGEM, contacto.imagem)
        }
        val whereClauses = "$COLUMN_ID = ?"
        val whereArrays = arrayOf(contacto.id.toString())
        db.update(TABLE_NAME, values, whereClauses, whereArrays)
        db.close()
    }

    fun getNoteByID(contactoID: Int): Contacto {
        // Obtém uma nota específica com base no ID
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $contactoID"
        val cursor = db.rawQuery(query, null)
        cursor.moveToNext()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val nome = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME))
        val morada = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MORADA))
        val imagem = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MORADA))
        val numero = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NUMERO))
        cursor.close()
        db.close()
        return Contacto(id,nome, numero, morada, imagem)
    }

    fun deleteNote(contactoID: Int) {
        // Remove uma nota da base de dados com base no ID
        val db = writableDatabase
        val whereClauses = "$COLUMN_ID = ?"
        val whereArrays = arrayOf(contactoID.toString())
        db.delete(TABLE_NAME, whereClauses, whereArrays)
        db.close()
    }
}
