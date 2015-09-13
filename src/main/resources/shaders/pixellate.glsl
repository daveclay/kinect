#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;
uniform vec2 texOffset;

varying vec4 vertColor;
varying vec4 vertTexCoord;

uniform float cellSize;
// by Nikos Papadopoulos, 4rknova / 2015
// WTFPL

// define S (iResolution.x / 6e1) // The cell size.

void main() {
// void mainImage(out vec4 c, vec2 p)
    //gl_FragColor = texture2D(iChannel0, floor((p + .5) / S) * S / iResolution.xy);
    gl_FragColor = texture2D(texture, floor((vertTexCoord) / cellSize) * cellSize);
}
