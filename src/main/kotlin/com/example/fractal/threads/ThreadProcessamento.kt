package com.example.fractal.threads

import com.example.fractal.Janela

class ThreadProcessamento(val janela: Janela):Thread() {
    override fun run() {
        try {
            while (true) {
                //         println("Thread Processamento rodando")
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