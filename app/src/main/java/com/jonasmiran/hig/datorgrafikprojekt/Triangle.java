package com.jonasmiran.hig.datorgrafikprojekt;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Triangle {

    static final int VERTEX_POS_SIZE = 4;
    static final int COLOR_SIZE = 4;

    static final int VERTEX_ATTRIB_SIZE = VERTEX_POS_SIZE;
    static final int COLOR_ATTRIB_SIZE = COLOR_SIZE;

    private int VERTEX_COUNT = triangleData.length / VERTEX_ATTRIB_SIZE;

    private FloatBuffer vertexDataBuffer;
    private FloatBuffer colorDataBuffer;
    private IntBuffer indexBuffer;

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

    private final int mProgram;

    private final String vertexShaderCode = ShaderFileReader.readRawTextFile(MyGLSurfaceView.context, R.raw.vertex_shader);

    private final String fragmentShaderCode = ShaderFileReader.readRawTextFile(MyGLSurfaceView.context, R.raw.fragment_shader);

    private int mMVPMatrixHandle;

    private int positionHandle;
    private int colorHandle;

    private ArcGridFileReader arcGridFileReader = new ArcGridFileReader(MyGLSurfaceView.context, R.raw.dem);
    private float[][] heightData = arcGridFileReader.getRasterData();;

    private float color[];

    public Triangle() {
        color = new float[heightData.length];
        triangleData = getVertices(heightData);
        VERTEX_COUNT = triangleData.length / VERTEX_ATTRIB_SIZE;

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

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX_COUNT);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }

    private int rowSize = arcGridFileReader.getNRows();
    private int colSize = arcGridFileReader.getNColls();
    private float cellSize = arcGridFileReader.getCellSize();

    public float[] getVertices (float[][] heightData)
    {
        float[] oneDHeightArray = new float[heightData.length * 4];

        //Vi har endast y-värden så därför behöver vi sätta in x och z och w
        float x = cellSize / 2;
        float z = cellSize / 2;
        float w = 1.0f;

        int index = 0;
        int yIndex = 1;
        int zIndex = 2;
        int wIndex = 3;
        int j = 0;

        for(int i = 0; i < rowSize; i++)
        {
            if(isEven(i)) {
                for (j = j; j < colSize; j++)
                {
                    if (wIndex == heightData.length)
                        break;

                    if(isOdd(j))
                    {
                        z += cellSize;
                    } else
                    {
                        x += cellSize;
                        z -= cellSize;
                        index += 4;
                        yIndex += 3;
                        zIndex += 2;
                        wIndex++;
                    }

                    oneDHeightArray[index] = x;
                    oneDHeightArray[yIndex] = (heightData[i][j]);
                    oneDHeightArray[zIndex] = z;
                    oneDHeightArray[wIndex] = w;

                    index += 4;
                    yIndex += 4;
                    zIndex += 4;
                    wIndex += 4;
                }
            }
        }

        return oneDHeightArray;
    }

    public boolean isEven(int num) { return num % 2 == 0; }

    public boolean isOdd(int num) { return num % 2 == 1; }
}
