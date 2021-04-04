#version 320 es

#if __VERSION__ < 130
#define TEXTURE2D texture2D
#else
#define TEXTURE2D texture
#endif

precision highp float;
//precision highp samplerBuffer;

uniform highp usamplerBuffer u_Iteracoes;    // The input texture buffer.
uniform mediump samplerBuffer u_Paleta;    // The input texture buffer.

uniform float u_tempo;
uniform float u_escalaPaleta;
uniform ivec2 u_dimSprite;

in vec2 v_TexturePosition;		// Interpolated position for this fragment.
in vec4 v_Color;

out vec4 fragColor;

vec4 paleta(uint iteracoes,float deltaT){
    int tamPaleta = 1024; // hardcoded
    float velocidadeCircular = 0.1; //hardcoded

    if (iteracoes == 0u) return vec4(0.0,0.0,0.0,1.0); //Cor do set de mandelbrot

    int pidex = int(iteracoes/10u)%tamPaleta;
    pidex = int(float(pidex) + deltaT*velocidadeCircular)%tamPaleta;
    vec4 cor = texelFetch(u_Paleta,pidex);
    return cor;
}

void main()
{

    int x = int(v_TexturePosition.x*float(u_dimSprite.x));
    int y = int(v_TexturePosition.y*float(u_dimSprite.y));
    int pidex = y*int(u_dimSprite.x)+x;

    vec4 corIteracoes = paleta(texelFetch(u_Iteracoes,pidex).r,u_tempo);

//    corIteracoes = texelFetch(u_Paleta,pidex); //debug

    fragColor = corIteracoes;
}
