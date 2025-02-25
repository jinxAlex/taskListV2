package com.example.tasklist.data.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.tasklist.model.Aplicacion

class Crud {
    fun create(categoria: String): Long{
        val con = Aplicacion.llave.writableDatabase
        return try{
            con.insertWithOnConflict(
                Aplicacion.TABLA,null,categoria.toContentValues(), SQLiteDatabase.CONFLICT_IGNORE
            )
        }catch (e: Exception){
            e.printStackTrace()
            -1L
        }finally {
            con.close()
        }
    }

    fun readCategorias(): MutableList<String>{
        val listaCategorias = mutableListOf<String>()
        val con = Aplicacion.llave.readableDatabase
        try{
            val cursor = con.query(
                Aplicacion.TABLA,
                arrayOf("id","nombre"),
                null,
                null,
                null,
                null,
                null
            )
            while (cursor.moveToNext()){
                val categoria = cursor.getString(1)
                listaCategorias.add(categoria)
            }
        }catch(e: Exception){
            e.printStackTrace()
        }finally {
            con.close()
        }
        return listaCategorias
    }

    fun deleteCategoria(categoria: String): Boolean{
        val con = Aplicacion.llave.writableDatabase
        val categoriaBorrada = con.delete(Aplicacion.TABLA,"nombre = ?", arrayOf(categoria))
        con.close()
        return categoriaBorrada != 0
    }

    private fun String.toContentValues(): ContentValues {
        return ContentValues().apply {
            put("NOMBRE", this@toContentValues)
        }
    }
}