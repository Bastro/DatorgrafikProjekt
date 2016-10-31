package com.jonasmiran.hig.datorgrafikprojekt;

import android.opengl.GLES20;
import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {

    static final int VERTEX_POS_SIZE = 4;
    static final int COLOR_SIZE = 4;

    static final int VERTEX_ATTRIB_SIZE = VERTEX_POS_SIZE;
    static final int COLOR_ATTRIB_SIZE = COLOR_SIZE;

    private final int VERTEX_COUNT = triangleData.length / VERTEX_ATTRIB_SIZE;

    private FloatBuffer vertexDataBuffer;
    private FloatBuffer colorDataBuffer;

    static float triangleData[] = {   // in counterclockwise order:
            0.0f,  0.622008459f, 0.0f, 1.0f, // top
            -0.5f, -0.311004243f, 0.0f, 1.0f, // bottom left
            0.5f, -0.311004243f, 0.0f, 1.0f, // bottom right
    };

    static float colorData[] = {   // in counterclockwise order:
            1.0f, 0.0f, 0.0f, 1.0f, // Red
            0.0f, 1.0f, 0.0f, 1.0f, // Green
            0.0f, 0.0f, 1.0f, 1.0f// Blue
    };

    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    private final int mProgram;

    private final String vertexShaderCode = ShaderFileReader.readRawTextFile(MyGLSurfaceView.context, R.raw.vertex_shader);

    private final String fragmentShaderCode = ShaderFileReader.readRawTextFile(MyGLSurfaceView.context, R.raw.fragment_shader);

    private int mMVPMatrixHandle;

    private int positionHandle;
    private int colorHandle;

    float[][] rasterData;

    public Triangle() {

        /**
         * Just a test to get values from DEM.
         */
        ArcGridFileReader arcGridFileReader = new ArcGridFileReader(MyGLSurfaceView.context, R.raw.dem);
        rasterData = arcGridFileReader.getRasterData();

        ByteBuffer bbv = ByteBuffer.allocateDirect(
                triangleData.length * 4);
        bbv.order(ByteOrder.nativeOrder());

        vertexDataBuffer = bbv.asFloatBuffer();
        vertexDataBuffer.put(triangleData);
        vertexDataBuffer.position(0);

        ByteBuffer bbc = ByteBuffer.allocateDirect(
                colorData.length * 4);
        bbc.order(ByteOrder.nativeOrder());

        colorDataBuffer = bbc.asFloatBuffer();
        colorDataBuffer.put(colorData);
        colorDataBuffer.position(0);

        int vertexShader = CGRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = CGRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgram, vertexShader);

        GLES20.glAttachShader(mProgram, fragmentShader);

        GLES20.glLinkProgram(mProgram);

    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(positionHandle, VERTEX_POS_SIZE,
                GLES20.GL_FLOAT, false,
                VERTEX_ATTRIB_SIZE * 4, vertexDataBuffer);

        colorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_SIZE,
                GLES20.GL_FLOAT, false,
                COLOR_ATTRIB_SIZE * 4, colorDataBuffer);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTEX_COUNT);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }
}
