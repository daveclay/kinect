#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;
uniform vec2 texOffset;

varying vec4 vertTexCoord;

void main () {
/*
  vec2 uv = vertTexCoord.st;
  vec4 color = texture2D(texture, uv + vec2(.01, 0));
  gl_FragColor = color;
  */

  vec2 uv = vertTexCoord.st;
  vec4 c = texture2D(texture, uv);

  float increment = .001;
  float count = 3.0;

  for (float i = 1.0; i <= count; i++) {
      c += texture2D(texture, uv + increment);
      c += texture2D(texture, uv - increment);

      increment += .002;
  }
  /*
  c += texture2D(texture, uv+0.001);
  c += texture2D(texture, uv+0.003);
  c += texture2D(texture, uv+0.005);
  c += texture2D(texture, uv+0.007);
  c += texture2D(texture, uv+0.009);
  c += texture2D(texture, uv+0.011);

  c += texture2D(texture, uv-0.001);
  c += texture2D(texture, uv-0.003);
  c += texture2D(texture, uv-0.005);
  c += texture2D(texture, uv-0.007);
  c += texture2D(texture, uv-0.009);
  c += texture2D(texture, uv-0.011);
  */

  c = c / (count * 2.0 + 1.02);

  gl_FragColor = c;
}