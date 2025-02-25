package com.example.tasklist.data

object  UserData {
    private var name: String = ""
    private var email: String = ""
    private var image: String = ""

    fun setEmail(email: String){
        this.email = email
    }

    fun getEmail(): String{
        return email
    }

    fun setName(name: String){
        this.name = name
    }

    fun getName(): String{
        return name
    }

    fun setImage(image: String){
        this.image = image
    }

    fun getImage(): String{
        return image
    }

}