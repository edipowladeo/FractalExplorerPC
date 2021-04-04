package com.example.fractal.threads


import com.example.fractal.Janela


class ThreadTexturas(val janela: Janela):Thread() {
    override fun run() {
        try {
            while (true) {
                janela.tarefasDesalocarTextura.executarTodas();
                janela.tarefasAlocarTextura.executarTodas();
                }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}