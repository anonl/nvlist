#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoord0;
varying vec2 v_texCoord1;
varying vec2 v_controlCoord;

uniform sampler2D u_tex0;
uniform sampler2D u_tex1;
uniform sampler2D u_controlTex;
uniform sampler2D u_interpolationLUT;

void main() {
    vec4 c0 = texture2D(u_tex0, v_texCoord0);
    vec4 c1 = texture2D(u_tex1, v_texCoord1);

	// Generate 2D lookup vector into interpolation texture
	vec2 lutPos = texture2D(u_controlTex, v_controlCoord).rr;
	float alpha = texture2D(u_interpolationLUT, lutPos).a;

    // EDIT: NVList now uses premultiplied alpha
	  // Premultiply because libGDX doesn't premultiply textures for some reason
	  // c0.rgb *= c0.a;
	  // c1.rgb *= c1.a;
    
    gl_FragColor = v_color * mix(c0, c1, alpha);    
}
