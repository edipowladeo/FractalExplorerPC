package com.example.fractal.android


import com.example.fractal.Janela


class ThreadTexturas(val janela: Janela):Thread() {
    override fun run() {
        try {
            while (true) {
                janela.TarefasDesalocarTextura.executarTodas();
                janela.TarefasAlocarTextura.executarTodas();
                }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}