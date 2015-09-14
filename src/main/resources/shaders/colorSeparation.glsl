#define PROCESSING_TEXTURE_SHADER

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;

// The inverse of the texture dimensions along X and Y
uniform vec2 texOffset;

varying vec4 vertTexCoord;

uniform vec2 resolution;
uniform float time;

// void mainImage( out vec4 fragColor, in vec2 fragCoord )
void main() {
	vec2 uv = vertTexCoord.xy;
	//vec2 delta = vec2( (sin(time*2.3)+sin(time*1.3)+sin(time/13.7)*0.2+sin(time/.4))/60.0, 0 );
	vec2 delta = vec2((sin(time * 100)+sin(time*15)+sin(time))/60, 0);
	vec4 txt = texture2D(texture,uv.xy);
	txt.r = texture2D(texture,uv.xy+delta ).r;
	txt.b = texture2D(texture,uv.xy-delta ).b;
	gl_FragColor = txt;
}