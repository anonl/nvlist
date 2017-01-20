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
    vec4 c = vec4(0.0);

    const float f = 1.0 / 11.0;
    for (float n = -5.0; n <= 5.0; n++) {
        c += f * texture2D(u_texture, v_texCoord0 + n * radius);
    }

    /*
    const float f = 1.0 / 3.0;
    c += f * texture2D(u_texture, v_texCoord0 - radius);
    c += f * texture2D(u_texture, v_texCoord0);
    c += f * texture2D(u_texture, v_texCoord0 + radius);
    */

    gl_FragColor = c;
}
