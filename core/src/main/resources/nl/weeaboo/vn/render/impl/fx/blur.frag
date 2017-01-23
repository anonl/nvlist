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

    const float f = 1.0 / 6.0;
    // Sample at 0.5, 2.5, 4.5
    // This takes advantage of the GPU's hardware for linear interpolation.
    // Doesn't work when the texture uses GL_NEAREST
    for (float n = 0.5; n <= 5.0; n += 2.0) {
        c += f * texture2D(u_texture, v_texCoord0 - n * radius);
        c += f * texture2D(u_texture, v_texCoord0 + n * radius);
    }

    gl_FragColor = c;
}
