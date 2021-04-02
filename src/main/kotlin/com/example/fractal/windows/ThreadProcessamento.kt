package com.example.fractal.android

import com.example.fractal.Janela

class ThreadProcessamento(val janela: Janela):Thread() {
    override fun run() {
        try {
            while (true) {
                if (janela.executaTarefaDeProcessamentoComMaiorPrioridade()){
             //     ////Log.i("Thread", "running")
                } else {
                   // ////Log.i("Thread", "running idle")
                    sleep(15)
                }
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}