#version 120

attribute vec4 vPosition;
attribute vec4 vColor;
uniform mat4 uMVPMatrix;
varying vec4 c;

void main()
{
  c = vColor;
  gl_Position = uMVPMatrix * vPosition;
}