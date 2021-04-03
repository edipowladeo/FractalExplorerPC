package com.example.fractal

typealias UniformHandle = Int
typealias WindowHandle = Long

typealias  CoordenadaPlano = Double
typealias  CoordenadasPlano = Cvetor2d
typealias  CoordenadaTela = Double
typealias  CoordenadasTela = Cvetor2d
typealias  CoordenadaTelai = Int
typealias  CoordenadasTelai = Cvetor2i
typealias  TipoDelta = Double
typealias  TipoCor = TRGB
typealias  TipoArrayIteracoes = IntArray
typealias  TipoSegundosI = Long

fun CoordenadasTela.toCoordenadasPlano(camera: PosicaoCamera) = CoordenadasPlano(
        camera.x + x * camera.Delta * 2,
        camera.y + y * camera.Delta * 2
    )

class RetanguloPlano{
    var min = CoordenadasPlano()
    var max = CoordenadasPlano()
    constructor(){}
    constructor(Min:CoordenadasPlano,Max:CoordenadasPlano){
        min=Min
        max=Max
    }
}

class PosicaoCamera : CoordenadasPlano {
    var Delta: TipoDelta = 0.0
    constructor():super(){}
    constructor(coordenadas: CoordenadasPlano, delta: TipoDelta):super(coordenadas){
        Delta=delta
    }
}

//TODO:implementar templates

/*CVetor2<T>
{
    var x : T;
    var y : T;

    CVetor2() {}
    CVetor2(T x, T y) { this->x = x; this->y = y; }
    CVetor2(const CVetor2<T>& C) { x = C.x; y = C.y; }
    void operator=(const CVetor2<T>& C) { x = C.x; y = C.y; }
    void operator+(const CVetor2<T>& C) { x += C.x; y += C.y; }
    void operator-(const CVetor2<T>& C) { x -= C.x; y -= C.y; }
    template <typename E>
    void operator*(const E& escalar) { x *= escalar; y *= escalar; }
    template <typename E>
    void operator/(const E& escalar) { x /= escalar; y /= escalar; }
    CVetor2<T>(sf::Vector2u vetor)
    {
        x = vetor.x;
        y = vetor.y;
    }
};

*//*
class Cvetor<T:BigInteger>{
    var x:T
    var y:T
    constructor(X:T,Y:T){
        x=X
        y=Y
    }
    fun plus(C: Cvetor<T>){
        x+=C.x
        y+=C.y
    }
    fun minus(C: Cvetor<T>){
        x-=C.x
        y-=C.y
    }
    fun not(C: Cvetor<T>):Cvetor<T>{
       return Cvetor<T>(-x,-y)
    }
}

private operator fun <T> T.plus(x: T): T {
    T = T + 1
}
*/

class Cvetor2i{
    var x:Int = 0
    var y:Int = 0
    constructor(X:Int,Y:Int){
        x=X
        y=Y
    }
    constructor(){}
}

open class Cvetor2d{
    var x:Double = 0.0
    var y:Double = 0.0
    constructor(X:Double,Y:Double){
        x=X
        y=Y
    }
    constructor(X:Int,Y:Int){
        x=X.toDouble()
        y=Y.toDouble()
    }
    constructor(){}
    constructor(C: Cvetor2d){
        x = C.x
        y = C.y
    }
    constructor(C: Cvetor2i){
        x = C.x.toDouble()
        y = C.y.toDouble()
    }

    override fun toString(): String {
        return " x: " + x.format(2).toString() + " y: " + y.format(2).toString()
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)

class TRGB{
    var r:Byte = 0
    var g:Byte = 0
    var b:Byte = 0
    constructor(R:Byte,G:Byte,B:Byte){
        r=R
        g=G
        b=B
    }
    constructor(R:Float,G:Float,B:Float){
        r=(R*255f).toByte()
        g=(G*255f).toByte()
        b=(B*255f).toByte()
    }
}
