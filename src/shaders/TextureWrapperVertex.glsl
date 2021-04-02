#version 320 es

precision mediump float;

uniform vec2 u_WindowSize;

uniform vec3 u_SpritePosition; //vec3(X,Y,Escala)

in vec2 a_RectangleVertices;
out vec2 v_TexturePosition;


void main()
{
    float escala = u_SpritePosition.z;
    vec2 posicao_tela = (a_RectangleVertices.xy * escala + u_SpritePosition.xy);

    float z = 1.0;
    float w = 1.0;  // Me parece que aplica uma transformação de escala *1/w
    gl_Position = vec4(posicao_tela/u_WindowSize.xy,z,w);

    v_TexturePosition = vec2(a_RectangleVertices.x,-a_RectangleVertices.y);
}