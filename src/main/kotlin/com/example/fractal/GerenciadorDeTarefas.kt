package com.example.fractal

import java.util.concurrent.locks.ReentrantLock

class GerenciadorDeTarefas<T:Runnable>{
    val lock = ReentrantLock()
    private var tarefas = emptyList<T>().toMutableList()

    /** é importante executar as tarefas depois de abrir a trava*/
    fun add(tarefa:T){
        lock.lock()
        tarefas.add(tarefa)
        lock.unlock()
    }

    fun executarTodas(){
        lock.lock()
        val TarefasClone =  tarefas.toList()
        tarefas.clear()
        lock.unlock()
        TarefasClone.forEach {it.run()}
    }

    fun getQtdeTarefas(): Int{
        lock.lock()
        val size = tarefas.size
        lock.unlock()
        return size
    }

   fun executarPrimeira():Boolean{
       lock.lock()


       if (tarefas.isNotEmpty()){
           val tarefa = tarefas.removeAt(0)
           lock.unlock()
           tarefa.run()
           return true
       }
       lock.unlock()
       return false
   }

    fun retornarPrimeiraOrNull():T?{
        lock.lock()
        if (tarefas.isNotEmpty()){
            val tarefa = tarefas.removeAt(0)
            lock.unlock()
            return tarefa
        }
        lock.unlock()
        return null
    }
}



//TODO: implementar funcao consumetask