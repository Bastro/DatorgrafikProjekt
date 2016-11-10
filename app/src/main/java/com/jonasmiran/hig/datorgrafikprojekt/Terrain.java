package com.jonasmiran.hig.datorgrafikprojekt;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Miran, Jonas on 31/10/2016.
 * draw a Terrain using OpenGL
 */
public class Terrain {

    static final int VERTEX_POS_SIZE = 4;
    static final int COLOR_SIZE = 4;

    static final int VERTEX_ATTRIB_SIZE = VERTEX_POS_SIZE;

    private int VERTEX_COUNT;

    private FloatBuffer vertexDataBuffer;
    private ShortBuffer indexBuffer;

    private float triangleData[] = {   // in counterclockwise order:
            0.0f,  0.622008459f, 0.0f, 1.0f, // top
            -0.5f, -0.311004243f, 0.0f, 1.0f, // bottom left
            0.5f, -0.311004243f, 0.0f, 1.0f, // bottom right
    };

    private short indices[];

    private final int mProgram;

    private final String vertexShaderCode = ShaderFileReader.readRawTextFile(MyGLSurfaceView.context, R.raw.vertex_shader);

    private final String fragmentShaderCode = ShaderFileReader.readRawTextFile(MyGLSurfaceView.context, R.raw.fragment_shader);

    private int mMVPMatrixHandle;

    private int positionHandle;
    private int yMaxHandle;

    private ArcGridFileReader arcGridFileReader = new ArcGridFileReader(MyGLSurfaceView.context, R.raw.dem);
    private float[][] heightData = arcGridFileReader.getRasterData();
    private float yMax = arcGridFileReader.getMax();

    public Terrain() {
        triangleData = getVertices(heightData);
        VERTEX_COUNT = triangleData.length / VERTEX_ATTRIB_SIZE;
        indices = getIndices(rowSize, colSize, indices);

        ByteBuffer bbv = ByteBuffer.allocateDirect(
                triangleData.length * 4);
        bbv.order(ByteOrder.nativeOrder());

        vertexDataBuffer = bbv.asFloatBuffer();
        vertexDataBuffer.put(triangleData);
        vertexDataBuffer.position(0);

        indexBuffer = ByteBuffer.allocateDirect(indices.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(indices);
        indexBuffer.position(0);

        int vertexShader = CGRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = CGRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(mProgram, vertexShader);

        GLES20.glAttachShader(mProgram, fragmentShader);

        GLES20.glLinkProgram(mProgram);

    }

    /**
     * Draw Terrain using a Matrix
     * @param mvpMatrix
     */
    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        yMaxHandle = GLES20.glGetUniformLocation(mProgram, "yMax");
        GLES20.glUniform1f(yMaxHandle, yMax);

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(positionHandle, VERTEX_POS_SIZE,
                GLES20.GL_FLOAT, false,
                VERTEX_ATTRIB_SIZE * 4, vertexDataBuffer);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, indices.length-2986, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    private int rowSize = arcGridFileReader.getNRows();
    private int colSize = arcGridFileReader.getNColls();
    private float cellSize = arcGridFileReader.getCellSize();

    private float[] getVertices(float[][] heightData)
    {
        return createVertices(heightData);
    }

    private float[] createVertices(float[][] heightData)
    {
        float[] vertices = new float[rowSize * colSize * VERTEX_POS_SIZE];

        //Vi har endast y-värden så därför behöver vi sätta in x och z och w
        float x = cellSize;
        float z = cellSize;
        float w = 1.0f;

        int index = 0;
        int yIndex = 1;
        int zIndex = 2;
        int wIndex = 3;

        for(int i = 0; i < rowSize; i++)
        {
                for (int j = 0; j < colSize; j++)
                {

                    vertices[index] = x;
                    vertices[yIndex] = (heightData[i][j]) * cellSize;
                    vertices[zIndex] = z;
                    vertices[wIndex] = w;

                    index += 4;
                    yIndex += 4;
                    zIndex += 4;
                    wIndex += 4;

                    x += cellSize;
                }

            x = cellSize;
            z += cellSize;
        }

        return vertices;
    }

    public boolean isEven(int num) { return num % 2 == 0; }

    private short[] getIndices(int rows, int columns, short[] indices)
    {
        return createIndices(rows, columns, indices);
    }

    /*
     * Skapar en array som innehåller ordningen på hur alla vertex ska ritas ut.
     *
     * based on http://www.chadvernon.com/blog/resources/directx9/terrain-generation-with-a-heightmap/
     */
    private short[] createIndices(int rows, int columns, short[] indices)
    {
        indices = new short[((columns * 2) * (rows - 1) + (rows - 2))];

        int index = 0;
        for ( int z = 0; z < rows - 1; z++ )
        {
            // Even rows move left to right, odd rows move right to left.
            if ( isEven(z) )
            {
                // Even row
                int x;
                for ( x = 0; x < columns; x++ )
                {
                    indices[index++] = (short)(x + (z * columns));
                    indices[index++] = (short)(x + (z * columns) + columns);
                }
                // Insert degenerate vertex if this isn't the last row
                if ( z != rows - 2)
                {
                    indices[index++] = (short)(--x + (z * columns));
                }
            }
            else
            {
                // Odd row
                int x;
                for ( x = columns - 1; x >= 0; x-- )
                {
                    indices[index++] = (short)(x + (z * columns));
                    indices[index++] = (short) (x + (z * columns) + columns);
                }
                // Insert degenerate vertex if this isn't the last row
                if ( z != rows - 2)
                {
                    indices[index++] = (short)(++x + (z * columns));
                }
            }
        }
        return indices;
    }
}
