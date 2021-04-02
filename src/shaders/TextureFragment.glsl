//#version 120

precision mediump float;

uniform sampler2D u_Texture;    // The input texture.

varying vec2 v_TexturePosition;		// Interpolated position for this fragment.
varying vec4 v_Color;


void main()
{
  //  gl_FragColor = v_Color;

    //gl_FragColor = (texture2D(u_Texture, v_Position.xy *1.0)+v_Color)*0.5;
//    vec4 color = vec4(v_Position.x,-v_Position.y,0.0,0.5);
    gl_FragColor = (texture2D(u_Texture, v_TexturePosition.xy))*1.0;
//gl_FragColor = color;
}