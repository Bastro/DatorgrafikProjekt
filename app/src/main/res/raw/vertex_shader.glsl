#version 120

attribute vec4 vPosition;
attribute vec4 vColor;
attribute float yMax;
uniform mat4 uMVPMatrix;
varying vec4 c;
varying float y;
varying float max;

void main()
{
  y = vPosition[1] / 50.0;
  max = yMax;

  gl_Position = uMVPMatrix * vPosition;
}