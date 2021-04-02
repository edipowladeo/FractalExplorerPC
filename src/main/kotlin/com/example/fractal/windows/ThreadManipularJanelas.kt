package com.example.fractal.windows

import com.example.fractal.Janela

class ThreadManipularJanelas(val janela: Janela):Thread() {

    override fun run() {
        try {
            while (true) {
                janela.atualizaCameraECamadas();
                //     Log.i("thread manipular janelas", "running")
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}