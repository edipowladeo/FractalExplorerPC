#version 320 es

#if __VERSION__ < 130
#define TEXTURE2D texture2D
#else
#define TEXTURE2D texture
#endif

precision highp float;
//precision highp samplerBuffer;

uniform highp usamplerBuffer u_TextureBuffer;    // The input texture buffer.
uniform sampler2D u_Texture;    // The input texture.

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

    uvec4 tex = texelFetch(u_TextureBuffer,pidex);
    uint valor_inteiro = tex.r;
    vec4 outputcolor = paleta(valor_inteiro,u_tempo);

    fragColor = outputcolor;
}
