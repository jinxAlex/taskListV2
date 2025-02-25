package com.example.tasklist.data.db

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.tasklist.model.Aplicacion


class Database: SQLiteOpenHelper(Aplicacion.appContext, Aplicacion.DB, null, Aplicacion.version){

    private val tabla = "CREATE TABLE ${Aplicacion.TABLA}( ID INTEGER PRIMARY KEY AUTOINCREMENT, " + "NOMBRE TEXT UNIQUE NOT NULL)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(tabla)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if(newVersion > oldVersion){
            db?.execSQL("drop table ${Aplicacion.TABLA}")
            onCreate(db)
        }
    }

}