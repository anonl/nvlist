#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_texCoord0;

uniform sampler2D u_texture;

/* Color multiplication matrix: r' = r * rr + g * rg + b * rb + a * ra */
uniform mat4 u_matrix;

/* Color offset: r' = r + roff */
uniform vec4 u_offset;

void main() {
    vec4 c = texture2D(u_texture, v_texCoord0);

    c.rgb /= c.a; // Unpremultiply
    c *= u_matrix;
    c += u_offset;
    c.rgb *= c.a; // Premultiply

    gl_FragColor = c;
}
