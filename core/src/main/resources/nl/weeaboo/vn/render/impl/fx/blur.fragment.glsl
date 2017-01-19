#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_texCoord0;

uniform sampler2D u_texture;
uniform vec2 radius;

void main() {
    vec4 c;
    
    const float f = 1.0 / 11.0;

    for (int n = -5; n <= 5; n++) {    
        c += f * texture2D(u_texture, v_texCoord0 + n * radius);
    }
    
    gl_FragColor = c;
}
