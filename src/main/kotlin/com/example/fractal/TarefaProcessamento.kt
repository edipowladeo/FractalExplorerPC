package com.example.fractal


/** um objeto Tarefa deve possuir todas as informacoes para que a tarefa seja executada*/

class TarefaProcessamento(val celula: Celula):Runnable {
   override fun run(){
        if (!celula.marcadaParaDestruicao)  {
            celula.processaIteracoesECriaTextura()
            celula.matrizIteracoesEstaCalculada = true
        }
    }
}

class TarefaCriarTexturaGL(val textura: TextureWrapper) : Runnable
{
   override fun run(){
       if (!textura.lersejadestriu()){
//    if(true){
        textura.createOGLTexture()
        ////Log.i("TarefaCriarTexturaGL ","executou tarefa criar textura Gl " + textura.getHandle())
        textura.validar()
    }
   }
}

class TarefaDesalocarTexturaGL(val textura: TextureWrapper) : Runnable
{
    override fun run(){
        textura.marcarComoJaDestruiu()
        textura.liberarRecursos()

        ////Log.i("DesalocarTexturaGL ","Desalocou textura Gl " + textura.getHandle())
    }
}

//TODO: BUG se eu chamar tarefa desalocar antes de tarefa alocar, causa vazamento de memória