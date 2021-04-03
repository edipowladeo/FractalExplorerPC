package com.example.fractal.threads

import com.example.fractal.Janela

class ThreadManipularJanelas(val janela: Janela):Thread() {
    override fun run() {
        try {
            while (true) {
                janela.atualizaCameraECamadas() //DoStuff
                //     Log.i("thread manipular janelas", "running")
             //   println("thread running $counter")

            }
        } catch (e: Throwable) {
          //  println("exception $e")
            e.printStackTrace()
        }
    }
}