package com.example.fractal

/*
import java.util.*
import java.util.concurrent.locks.ReentrantLock

typealias Key=Int

class PoolRecursos<T:Alocavel>{
    val lock = ReentrantLock();
    private var tamanho = 0
    private var tamanhodesejado = 0
    private var posicoesocupadas = 0

    var recursos = emptyMap<Key,T>().toMutableMap()
    var freeItensKeys = emptySet<Key>().toMutableSet()


    fun setTamanhoDesejado(Tamanhodesejado: Int){
        lock.lock()
        tamanhodesejado = Tamanhodesejado
        lock.unlock()
    }

    private fun tentaAlocar(){
        lock.unlock()
 //       while (tamanho<tamanhodesejado){
//            recursos.add()
   //     }
        lock.unlock()
    }

    fun add(item:T){
        tamanho++
        posicoesocupadas++
     //   recursos.put(key,item)

    }

    fun Devolver(key:Key){
        recursos.remove(key)
        freeItensKeys.add(key)
        posicoesocupadas--
    }

    fun emprestar():T?{
        var chave = freeItensKeys.firstOrNull()?
        chave?.let{key->
            return recursos.get(key)
        }
    }

    private fun

    fun getRecurso(key):T{
        return recursos()
    }

    fun encolher(tamDesejado: Int){

    }

    fun encolher(){
        encolher(0)
    }

    fun getTamanho():Int{return tamanho}

}*/
