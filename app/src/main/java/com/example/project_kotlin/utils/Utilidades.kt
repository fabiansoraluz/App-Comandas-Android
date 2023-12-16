package com.example.project_kotlin.utils

class Utilidades {
    public fun generarNumeroRandom(min: Int, max: Int): Int {
        return (Math.round(Math.random() * (max - min)) + min).toInt()
    }
}