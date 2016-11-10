#version 120

precision mediump float;
varying vec4 c;
varying float y;
varying float max;

void main()
{
    float r = 0, g = 0, b = 0, a = 1.0f;
    float ratio = y / max;
    float heightColor = y / 255;
    vec4 color;
    int colorMultiplier = 2;

    if(y > 55)
    {
        r = 1;
        g = 1;
        b = 1;
    }
    else if(y <= 45 && y >= 20)
    {
        r = 0;
        g = heightColor * colorMultiplier;
        b = 0;
    }
    else if(y >= 44 && y <= 60)
    {
        r = heightColor * colorMultiplier;
        g = heightColor * colorMultiplier;
        b = 0;
    }
    else
    {
        r = 0;
        g = 0;
        b = heightColor * colorMultiplier;
    }
        color = vec4(r, g, b, a);

    gl_FragColor = color;
}