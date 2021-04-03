package com.example.fractal


/**
Os vetores coordCelulasX(CX) e Os coordCelulasY(CY) armazendam as coordenadas no plano e na tela das células (c)
é uma otimização para evitar o cálculo repetido destas coordenadas, visto que são iguais para colunas e linhas
As coordenadas no plano são copiadas paras as células por valor , visto que são constantes.


        CX0 CX1 CX2 CX3

 CY0    c00 c10 c20 c30
 CY1    c01 c11 c21 c31
 CY2    c02 c12 c22 c32
 CX3    c03 c13 c23 c33

A matriz de células é implementada usando uma Lista (colunas) de lista (linhas) de células

Esta classe possui métodos para inserir e remover linhas e colunas,
estes métodos manipulam e os vetores coordCelulas e a matriz de células ao mesmo tempo
 */


class Camada(val janela: Janela, coordenadasIniciais: CoordenadasPlanoEDelta)
{

//TODO: isto é realmente necessario? classe coordcelula1d, pode ser implementados dois vetores
    private class CoordCelula1D{
        var coordenadaPlano : CoordenadaPlano = 0.0
        var coordenadaTela : CoordenadaTela = 0.0
        constructor(){}
        constructor(CoordenadaPlano : CoordenadaPlano, CoordenadaTela : CoordenadaTela){
            coordenadaPlano = CoordenadaPlano
            coordenadaTela = CoordenadaTela
            }
        constructor(CoordenadaPlano : CoordenadaPlano){
            coordenadaPlano = CoordenadaPlano
        }
        }
    enum class Direcao {
        INCREMENTO, DECREMENTO
    }
    /**lista de tarefas*/
    val TarefasProcessamento = GerenciadorDeTarefas<TarefaProcessamento>()

    /**Vetor de Coordenadas das células*/
    private var coordCelulasX = MutableList<CoordCelula1D>(1){ CoordCelula1D(coordenadasIniciais.x) }
    private var coordCelulasY = MutableList<CoordCelula1D>(1){ CoordCelula1D(coordenadasIniciais.y) }

    internal val Delta: TipoDelta = coordenadasIniciais.Delta

    /**Lista (colunas) de lista (linhas) de células*/
    internal var Celulas = MutableList<MutableList<Celula>>(1){MutableList(1){
            Celula(this,coordenadasIniciais,janela.tamSprite)
    }
    }

    init{
        posicionaTodasCelulasNaTela()
    }

    fun atualizaMatrizDeCelulas() {
        // presume-se que os valores minimos e maximos da janela estão atualizados
        //TODO:usar tipos e operator overload para melhorar a redabilidade
        var larguraPlano = janela.tamSprite.x.toDouble() * Delta
        var alturaPlano = janela.tamSprite.y.toDouble() * Delta

        val coord_min_alocar = CoordenadasPlano(
            janela.coordMin.x,
            janela.coordMin.y
        )
        val coord_max_alocar = CoordenadasPlano(
            janela.coordMax.x - larguraPlano,
            janela.coordMax.y - alturaPlano
        )
        val coord_min_desalocar = CoordenadasPlano(
            janela.coordMin.x - larguraPlano,
            janela.coordMin.y - alturaPlano
        )
        val coord_max_desalocar = CoordenadasPlano(
            janela.coordMax.x,
            janela.coordMax.y
        )

        while (coordCelulasX.first().coordenadaPlano > coord_min_alocar.x) adicionarColuna(Direcao.DECREMENTO)
        while (coordCelulasX.last().coordenadaPlano < coord_max_alocar.x) adicionarColuna(Direcao.INCREMENTO)
        while (coordCelulasY.first().coordenadaPlano > coord_min_alocar.y) adicionarLinha(Direcao.DECREMENTO)
        while (coordCelulasY.last().coordenadaPlano < coord_max_alocar.y) adicionarLinha(Direcao.INCREMENTO)

           if (coordCelulasX.size > 1){
        if (coordCelulasX.first().coordenadaPlano < coord_min_desalocar.x) removerColuna(Direcao.DECREMENTO)
    }
        if (coordCelulasX.size > 1){
        if (coordCelulasX.last().coordenadaPlano > coord_max_desalocar.x)         removerColuna(Direcao.INCREMENTO)
        }
        if (coordCelulasY.size > 1){
         if (coordCelulasY.first().coordenadaPlano < coord_min_desalocar.y)         removerLinha(Direcao.DECREMENTO)
        }
        if (coordCelulasY.size > 1){
        if (coordCelulasY.last().coordenadaPlano > coord_max_desalocar.y)         removerLinha(Direcao.INCREMENTO)
        }
    }

    fun getCoordenadasPlanoCelulaCantoSupEsq(): CoordenadasPlano {
        return CoordenadasPlano(
            coordCelulasX.first().coordenadaPlano,
            coordCelulasY.first().coordenadaPlano
        )
    }

    fun getCoordenadasPlanoEDelta(): CoordenadasPlanoEDelta {
        return CoordenadasPlanoEDelta(getCoordenadasPlanoCelulaCantoSupEsq(),Delta)
    }

    fun PosicionaCamadaNaTela() {
        lateinit var coordenadasTela : CoordenadasTela

        val coordenadasPlano = getCoordenadasPlanoCelulaCantoSupEsq()
        janela.PosicaoCameraAtual.also {
            coordenadasTela = CoordenadasTela(
                (coordenadasPlano.x - it.x) / it.Delta,
                (coordenadasPlano.y - it.y) / it.Delta)
        }

        val razao = Delta / janela.PosicaoCameraAtual.Delta
        coordCelulasX.forEachIndexed{i,it->
            it.coordenadaTela=coordenadasTela.x + i*razao * janela.tamSprite.x}
        coordCelulasY.forEachIndexed{j,it->
            it.coordenadaTela=coordenadasTela.y + j*razao * janela.tamSprite.y}
    }

    fun getCoordenadaTelaDaCelula(i:Int,j:Int): CoordenadasTela {
        return  CoordenadasTela(
            coordCelulasX[i].coordenadaTela,
            coordCelulasY[j].coordenadaTela)
    }

    //TODO: verificar se é melhor usar este método na camada toda ou chamar o método getCoordenadaTelaDaCelula na hora de renderizar
    fun posicionaTodasCelulasNaTela(){
        PosicionaCamadaNaTela()
            Celulas.forEachIndexed{i,colunas ->
                colunas.forEachIndexed{j,linhas ->
                    linhas.coordenadasTela = getCoordenadaTelaDaCelula(i,j)
                }
            }
    }

    fun logCells(){
        Celulas.forEachIndexed{i,it ->
            ////Log.i("Debug","x: "+i.toString())
            it.forEachIndexed{j,it ->
                ////Log.i("Debug","y: "+ j.toString())
                ////Log.i("Debug","pos "+it.coordenadasTela.toString())
                ////Log.i("Debug","posP "+it.coordenadasPlano.toString())
            }
        }
    }

    //TODO: Adicionar Coluna e linha não seta coordenadas tela dos vetores, eles ficam em zero durante um frame
    //TODO: refatorar nomes
    fun adicionarColuna(direcao: Direcao){
        println("adicionou")
        val tamY = coordCelulasY.size
        val tamX = coordCelulasX.size

        val indiceX = if (direcao== Direcao.INCREMENTO)  tamX else 0

        val novacoordenadaOffset = if (direcao== Direcao.INCREMENTO) (tamX*Delta*janela.tamSprite.x) else (-Delta*janela.tamSprite.x)

        /** incrementa o vetor de coordenadas */
        val novaCoordenadaX = CoordCelula1D(coordCelulasX.first().coordenadaPlano + novacoordenadaOffset)
        coordCelulasX.add(indiceX,novaCoordenadaX)

        /**cria uma nova coluna */
        val novaColuna = emptyList<Celula>().toMutableList()

        /**Popula a coluna criada com uma linha para cada coordenada Y*/
        coordCelulasY.forEachIndexed { indiceY, it ->
            val novaCelula = Celula(this, CoordenadasPlano(
                coordCelulasX[indiceX].coordenadaPlano,
                coordCelulasY[indiceY].coordenadaPlano),
                janela.tamSprite)
            novaColuna.add(novaCelula)
        }

        /**Insere nova Coluna na matriz de células */
        Celulas.add(indiceX,novaColuna)
    }


    fun adicionarLinha(direcao: Direcao){
        val tamY = coordCelulasY.size
        val tamX = coordCelulasX.size

        val indiceY = if (direcao== Direcao.INCREMENTO)  tamY else 0

        val novacoordenadaOffset = if (direcao== Direcao.INCREMENTO) (tamY*Delta*janela.tamSprite.y) else (-Delta*janela.tamSprite.y)

        /** incrementa o vetor de coordenadas */
        val novaCoordenadaY = CoordCelula1D(coordCelulasY.first().coordenadaPlano + novacoordenadaOffset)
        coordCelulasY.add(indiceY, novaCoordenadaY)

        /** para cada coluna ...*/
        coordCelulasX.forEachIndexed{indiceX, it ->
            /** ... cria uma nova célula*/
            val novaCelula = Celula(this,
                CoordenadasPlano(
                    coordCelulasX[indiceX].coordenadaPlano,
                    coordCelulasY[indiceY].coordenadaPlano
                ),
                janela.tamSprite)
            Celulas[indiceX].add(indiceY,novaCelula)
        }
    }


    fun removerColuna(direcao: Direcao){
        println("removeu")
        val tamY = coordCelulasY.size
        val tamX = coordCelulasX.size

        val indiceX = if (direcao== Direcao.INCREMENTO)  tamX-1 else 0

        /** remove Coordenadas do vetor*/
        coordCelulasX.removeAt(indiceX)

        /** liberar recursos das células*/
        Celulas[indiceX].forEach {celula ->
            celula.liberaRecursos()
        }

        /** remove coluna*/
        Celulas.removeAt(indiceX)
    }

    fun removerLinha(direcao: Direcao) {
        val tamY = coordCelulasY.size
        val tamX = coordCelulasX.size

        val indiceY = if (direcao == Direcao.INCREMENTO) tamY - 1 else 0

        /** remove Coordenadas do vetor*/
        coordCelulasY.removeAt(indiceY)

        /** liberar recursos das células*/
        Celulas.forEach { linha ->
            linha[indiceY].liberaRecursos()
        }

        /** remove linha*/
        Celulas.forEach { linha ->
            linha.removeAt(indiceY)
        }
    }

    fun liberarRecursos(){
        Celulas.forEach { linha ->
            linha.forEach{celula->
                celula.liberaRecursos()
            }
        }
    }

    override fun toString(): String {
       return super.toString()
    }


}