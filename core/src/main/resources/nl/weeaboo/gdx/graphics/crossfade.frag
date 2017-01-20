#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoord0;
varying vec2 v_texCoord1;

uniform sampler2D u_tex0;
uniform sampler2D u_tex1;
uniform float alpha; // Blend factor [0, 1]

void main() {
    vec4 c0 = texture2D(u_tex0, v_texCoord0);
    vec4 c1 = texture2D(u_tex1, v_texCoord1);

    gl_FragColor = v_color * mix(c0, c1, alpha);    
}
