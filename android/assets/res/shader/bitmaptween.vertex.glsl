attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
attribute vec2 a_texCoord1;
attribute vec2 a_texCoord2;

uniform mat4 u_projTrans;
varying vec4 v_color;
varying vec2 v_texCoord0;
varying vec2 v_texCoord1;
varying vec2 v_controlCoord;

void main() {
    v_color = a_color;
    v_texCoord0 = a_texCoord0;
    v_texCoord1 = a_texCoord1;
    v_controlCoord = a_texCoord2;
    gl_Position = u_projTrans * a_position;
}
