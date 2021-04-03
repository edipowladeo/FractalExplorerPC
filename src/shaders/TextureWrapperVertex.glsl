#version 320 es

precision mediump float;

uniform vec2 u_WindowSize; //vec2(X,Y) size in pixels

uniform vec3 u_SpritePosition; //vec3(X,Y,Escala)

in vec2 a_RectangleVertices;// [retangulo 0,0 até 1,1]
out vec2 v_TexturePosition;


void main()
{
    float escalaRetangulo = u_SpritePosition.z;

    vec2 posicao_tela = (a_RectangleVertices.xy * escalaRetangulo + u_SpritePosition.xy);

    float z = 1.0;  // profundidade
    float w = 1.0;  // Me parece que aplica uma transformação de escala *1/w

    //[-1 ate +1] com origem no centro da tela
    gl_Position = vec4(posicao_tela/u_WindowSize.xy,z,w);

    v_TexturePosition = vec2(a_RectangleVertices.x,-a_RectangleVertices.y);


}