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
    
    const float f = 1.0 / 9.0;
    
    c += f * texture2D(u_texture, v_texCoord0 + vec2(-radius.x, -radius.y));
    c += f * texture2D(u_texture, v_texCoord0 + vec2(      0.0, -radius.y));
    c += f * texture2D(u_texture, v_texCoord0 + vec2(+radius.x, -radius.y));

    c += f * texture2D(u_texture, v_texCoord0 + vec2(-radius.x,       0.0));
    c += f * texture2D(u_texture, v_texCoord0);
    c += f * texture2D(u_texture, v_texCoord0 + vec2(+radius.x,       0.0));

    c += f * texture2D(u_texture, v_texCoord0 + vec2(-radius.x, +radius.y));
    c += f * texture2D(u_texture, v_texCoord0 + vec2(      0.0, +radius.y));
    c += f * texture2D(u_texture, v_texCoord0 + vec2(+radius.x, +radius.y));

    gl_FragColor = c;
}
