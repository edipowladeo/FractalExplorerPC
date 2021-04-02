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

in vec2 v_TexturePosition;		// Interpolated position for this fragment.
in vec4 v_Color;

out vec4 fragColor;

vec4 paleta(uint iteracoes,float deltaT){
    vec4 cor;
    cor.a = 1.0;
    if (iteracoes == 0u) return cor;
    float iteracoesFloat = float(iteracoes);
    iteracoesFloat/=u_escalaPaleta;
   // iteracoesFloat+=deltaT;

    cor.r = 0.5+0.5*sin(iteracoesFloat);
    cor.g = 0.5+0.5*sin(iteracoesFloat+2.0);
    cor.b = 0.5+0.5*sin(iteracoesFloat-2.0);

    return cor;
}

void main()
{
    float multiplicador = 32.0;

    int x = int(v_TexturePosition.x*multiplicador);
    int y = int(v_TexturePosition.y*multiplicador);
    int pidex = y*int(multiplicador)+x;

    vec4 corIteracoes = paleta(texelFetch(u_Iteracoes,pidex).r,u_tempo);
    vec4 corPaleta = texelFetch(u_Paleta,pidex);

    fragColor = corIteracoes*0.5+corPaleta*0.5;
}
