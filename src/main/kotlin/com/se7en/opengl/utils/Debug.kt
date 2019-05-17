package com.se7en.opengl.utils

object Debug {
    fun log(tag:String,message:String){
        System.out.println("[$tag]:$message")
    }

    fun log(message:String){
        System.out.println("$message")
    }
}