package com.example.fractal

import java.lang.Math.pow

open class
JanelaPropriedades {
    var ExibirInformacoesDaJanelaEmOverlay = false
    val NumThreadsProcessamento = 4
    val samplingIteracoes  = 64

    val escalaPaleta = samplingIteracoes*16f
    val limiteDivergencia = 256
    val maxIteracoes = 1024

    var FatorScroll = 2.0f
    var fatorEscala = 1.5f

    //samplingIteracoes*maxIteracoes < 2^16 OU SEJA TÁ INDO 16 BIT CORRETAMENTE
    var circularCores = true
    var velocidadeCircularCores = 1f

    //TODO: remover fator debug e criar janelas de alocação e inatividade
    val fator_debug = 1//0.5 //tamanho da janela de alocação, usado para debug, release deve ser =1
    val minTamanhoAparentePixel = 2f // mínimo tamanho que o pixel pode assumir na tela, valores <1 representam multisampling
    val quantidadeDeCamadasAlemDaPrincipal = 4  // camadas de preview (baixa resolucao) processadas antes da camada de alta qualidade
    val tamSprite = Cvetor2i(32,32)

    var TaxaZoom = 0.0f
    var TaxaPan = 0.0f
    var MagInicial = 10

    var MaginiciallF = pow(0.5,MagInicial.toDouble())/1.12
    var PosicaoCameraInicial = CoordenadasPlanoEDelta(CoordenadasPlano(-0.5,0.0),MaginiciallF)
        //var MagInicial = 15
    //var PosicaoCameraInicial = CoordenadasPlano(-1.78058,0.0)


    /* var PosicaoInicial: CoordenadasPlanoEDelta;

       telaCheia: Bool = ;
       CVetor2i DimensoesTextura;
       CVetor2i Recobrimento;
       CVetor2i DimensoesMatrizCelulas;

       bool DebugCamadas;
       bool DebugCelulas;

       bool UsarShader;

       float NavegacaoVelocidade;
       float NavegacaoVelocidadeZoom;
       int   DuracaoFrameDesejada = DURACAOFRAMEDESEJADA;//ms 1/taxa de quadros

       sf::Vector2f DimensoesJanelaFractal;
       sf::Vector2f JanelaFractalDesenho;
       sf::Vector2f JanelaFractalAlocar;
       sf::Vector2f JanelaFractalDesalocar;

       int MagAlocarMax;
       int MagAlocarMin;
       int MagDesalocarMax;
       int MagDesalocarMin;

       float RazaoTamJanelaFractalAlocar;
       float RazaoTamJanelaFractalDesenho;
       float RazaoTamJanelaFractalDesalocar;

       float RazaoJanelaFractalsDebug;

       CPropriedades();
       void AtualizarLimites();*/
}