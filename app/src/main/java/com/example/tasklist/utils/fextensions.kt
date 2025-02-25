package com.example.tasklist.utils

fun String.encodeEmail()=this.replace("@", "_AT_").replace(".", "_DOT_")