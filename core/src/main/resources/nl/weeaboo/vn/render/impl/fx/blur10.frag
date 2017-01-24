#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying vec2 v_texCoord0;

uniform sampler2D u_texture;
uniform vec2 radius;

#define numSamples 10.0

void main() {
    vec4 c = vec4(0.0);

    const float halfNumSamples = 0.5 * numSamples;
    const float weight = 1.0 / numSamples;
    const float f = 1.0 / halfNumSamples;
    for (float n = 0.5 - halfNumSamples; n <= halfNumSamples; n += 1.0) {
        c += weight * texture2D(u_texture, v_texCoord0 + f * n * radius);
    }

    gl_FragColor = c;
}
