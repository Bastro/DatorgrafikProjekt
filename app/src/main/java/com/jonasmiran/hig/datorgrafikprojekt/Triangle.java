package com.jonasmiran.hig.datorgrafikprojekt;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Triangle {

    static final int VERTEX_POS_SIZE = 4;
    static final int COLOR_SIZE = 4;

    static final int VERTEX_ATTRIB_SIZE = VERTEX_POS_SIZE;
    static final int COLOR_ATTRIB_SIZE = COLOR_SIZE;

    private int VERTEX_COUNT;

    private FloatBuffer vertexDataBuffer;
    private FloatBuffer colorDataBuffer;
    private ShortBuffer indexBuffer;

    private float triangleData[] = {   // in counterclockwise order:
            0.0f,  0.622008459f, 0.0f, 1.0f, // top
            -0.5f, -0.311004243f, 0.0f, 1.0f, // bottom left
            0.5f, -0.311004243f, 0.0f, 1.0f, // bottom right
    };

    private float colorData[] = {   // in counterclockwise order:
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
    private float[][] heightData = arcGridFileReader.getRasterData();

    public Triangle() {
        colorData = new float[((((rowSize*colSize-colSize*2)*2)-rowSize+2)+colSize*2)*4];
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

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, VERTEX_COUNT); //GL_Triangles och andra typer ger liknande resultat

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }

    private int rowSize = arcGridFileReader.getNRows();
    private int colSize = arcGridFileReader.getNColls();
    private float cellSize = arcGridFileReader.getCellSize();

    public float[] getVertices (float[][] heightData)
    {
        float[] vertices = new float[rowSize * colSize * VERTEX_POS_SIZE];

        //Vi har endast y-värden så därför behöver vi sätta in x och z och w
        float x = cellSize / 2;
        float z = cellSize / 2;
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

                    setColor(heightData[i][j]);

                }
            x = cellSize;
            z += cellSize;
        }

        return vertices;
    }

    /**
     * Lånat från Dennis och Daniel. Användes för debugging. Ger bättre resultat än getVertices just nu.
     * @param heightData
     * @return
     */
    public float[] looop(float[][] heightData){

        float matrix[][];
        int hightMulti = 20;
            matrix = heightData;
        float cellSize = arcGridFileReader.getCellSize();
        float[] array = new float[((((rowSize*colSize-colSize*2)*2)-rowSize+2)+colSize*2)*4];
        int sizeOfArray = ((((rowSize*colSize-colSize*2)*2)-rowSize+2)+colSize*2)*4;
        int index = 4,j=1,arrayindex=0, rowEven=0,rowOdd=-1,row=0;
        float x= cellSize/2,z=cellSize/2;
        array[0]=x;
        array[1]=matrix[0][0] * 50;
        array[2]=z;
        array[3]=1f;
        setColor(array[1]);

        for (int i = 0; i < rowSize-1; i++) {
            if((i%2)==0 ){
                rowOdd = rowOdd+2;

                for (j=j; j < (colSize*2); j++) {

                    if((j%2) !=0 ){
                        z=z+cellSize;
                        row=rowOdd;
                    }
                    else{
                        x=x+cellSize;
                        z=z-cellSize;
                        row=rowEven;
                        arrayindex++;
                    }

                    setColor(array[j]);

                    array[index]=x;
                    array[index+1]=(matrix[row][arrayindex]) * 50;
                    array[index+2]=z;
                    array[index+3]=1f;
                    index = index+4;

                }

            }
            else{
                rowEven=rowEven+2;
                for (j=j; j > 1; j--) {

                    if((j%2) !=0 ){
                        x=x-cellSize;
                        z=z-cellSize;
                        row=rowOdd;
                        arrayindex--;
                    }
                    else{
                        z=z+cellSize;
                        row=rowEven;
                    }


                    setColor(array[j]);
                    array[index]=x;
                    array[index+1]=matrix[row][arrayindex] * 50;
                    array[index+2]=z;
                    array[index+3]=1f;
                    index = index+4;

                }




            }


        }


        return array;
    }


    private int colorIndex = 0;

    /**
     * Lånat från Dennis och Daniel. Bara för att testa. Behövs för att ritfunktionen inte kraschar.
     * @param hight
     */
    private void setColor(float hight){

        if(hight>60){
            colorData[colorIndex]=0.5f;
            colorData[colorIndex+1]=0.3f;
            colorData[colorIndex+2]=0.0f;
            colorData[colorIndex+3]=1f;
        }
        else if(hight<45&&hight>20){
            colorData[colorIndex]=0.3f;
            colorData[colorIndex+1]=0.7f;
            colorData[colorIndex+2]=0.1f;
            colorData[colorIndex+3]=1f;
        }
        else if(hight>44&&hight<61) {
            colorData[colorIndex]=1.0f;
            colorData[colorIndex+1]=1.0f;
            colorData[colorIndex+2]=1.0f;
            colorData[colorIndex+3]=1f;

        }
        else{
           colorData[colorIndex]=0f;
           colorData[colorIndex+1]=0.1f;
           colorData[colorIndex+2]=0.8f;
           colorData[colorIndex+3]=1f;
        }
        colorIndex=colorIndex+4;
    }

    public boolean isEven(int num) { return num % 2 == 0; }

    public boolean isOdd(int num) { return num % 2 == 1; }

    /**
     * Temporär. Tänkte prova rita allt med en index buffer istället.
     * http://stackoverflow.com/questions/5915753/generate-a-plane-with-triangle-strips
     * @param rows
     * @param columns
     * @param indices
     * @return
     */
    public short[] createIndices(int rows, int columns, short[] indices)
    {
        indices = new short[rows * 2];

        // Set up indices
        short i = 0;
        for (short r = 0; r < rows - 1; ++r) {
            indices[i++] =(short) (r * columns);
            for (short c = 0; c < columns; ++c) {
                indices[i++] =(short) (r * columns + c);
                indices[i++] =(short) ((r + 1) * columns + c);
            }
            indices[i++] = (short)((r + 1) * columns + (columns - 1));
        }
        return indices;
    }

}
