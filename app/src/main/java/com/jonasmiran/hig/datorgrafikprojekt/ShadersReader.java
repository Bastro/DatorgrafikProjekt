package com.jonasmiran.hig.datorgrafikprojekt;

import android.content.Context;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Miran, Jonas on 2016-11-09.
 * Read vertex_shader & fragment_shader
 */
public class ShadersReader
{
    String vertexCode;
    String fragmentCode;

    /**
     * Both files in String object
     * @param vertexCode
     * @param fragmentCode
     */
    public ShadersReader(String vertexCode, String fragmentCode)
    {
        this.vertexCode = vertexCode;
        this.fragmentCode  = fragmentCode;
    }

    /**
     * Both files in File format
     * @param vertexShaderFile
     * @param fragmentShaderFile
     */
    public ShadersReader(File vertexShaderFile, File fragmentShaderFile)
    {
        vertexCode = ReadFile(createBufferedReader(vertexShaderFile));
        fragmentCode = ReadFile(createBufferedReader(fragmentShaderFile));
    }

    /**
     * Both files from Android Studio resources
     * @param ctxVertex
     * @param resIdVertex
     * @param ctxFragment
     * @param resIdFragment
     */
    public ShadersReader(Context ctxVertex, int resIdVertex, Context ctxFragment, int resIdFragment)
    {
        vertexCode = ReadFile(createBufferedReader(ctxVertex, resIdVertex));
        fragmentCode = ReadFile(createBufferedReader(ctxFragment, resIdVertex));
    }

    private static BufferedReader createBufferedReader(Context ctx, int resId) {
        InputStream inputStream = ctx.getResources().openRawResource(resId);
        InputStreamReader inputreader = new InputStreamReader(inputStream);

        return new BufferedReader(inputreader);
    }

    private static BufferedReader createBufferedReader(File file)
    {
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            return br;
        } catch (FileNotFoundException e){
            throw new RuntimeException(e);
        }
    }

    private static String ReadFile(BufferedReader buffreader)
    {
        String line;
        StringBuilder text = new StringBuilder();

        try
        {
            while ((line = buffreader.readLine()) != null)
            {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return text.toString();
    }

    /**
     * vertex_shader load
     * @return
     */
    public int loadVertexShader()
    {
        int shader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(shader, vertexCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * fragment_shader load
     * @return
     */
    public int loadFragmentShader()
    {
        int shader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(shader, fragmentCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
