package com.example.fractal.android

/** Bugs conhecidos*/
//TODO:Se der zoom até o final, ele cria milhares de tarefas.
//TODO: Shader só consegue ler 16 bits
// TODO: possuiTexturaValida? esta retornando true mesmo quando nao deveria


/**Ao procurar tarefas nas camadas, é preciso adrentar na janela
 *
 * 2020-05-02 22:20:21.740 26715-26742/com.example.fractal E/AndroidRuntime: FATAL EXCEPTION: Thread-3
Process: com.example.fractal, PID: 26715
java.util.ConcurrentModificationException
at java.util.TreeMap$PrivateEntryIterator.nextEntry(TreeMap.java:1212)
at java.util.TreeMap$ValueIterator.next(TreeMap.java:1257)
at com.example.fractal.Janela.executaTarefaDeProcessamentoComMaiorPrioridade(Janela.kt:239)
at com.example.fractal.threads.ThreadProcessamento.run(ThreadProcessamento.kt:19)*/
//TODO:Navegacao da camera com "mola" até local de interesse
//TODO:implementar celulas "intativas"


//TODO:openCL
//TODO:paletas
//TODO:paletas no shader
//TODO:interpolacao no shader
//TODO:multisampling no shader
//TODO:editor de paletas
//TODO:multiprecisao
//TODO:Algoritmo "mágico" para multiprecisao rápida
//TODO:Salvar locais de Interesse
//TODO:Controle de versão
