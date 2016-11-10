package com.jonasmiran.hig.datorgrafikprojekt;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Miran, Jonas on 31/10/2016.
 * Reading a ArcGrid raster text file and store data to access
 */
public class ArcGridFileReader {

    private Map<String, Float> metaData;
    private final String NCOLS = "ncols";
    private final String NROWS = "nrows";
    private final String XLLCENTER = "xllcenter";
    private final String YLLCENTER = "yllcenter";
    private final String CELLSIZE = "cellsize";
    private final String NODATA_VALUE = "nodata_value";
    private final int META_DATA_LENGTH = 5;

    private float[][] rasterValues;

    /**
     * Read from Android resources
     * @param ctx
     * @param resId
     */
    public ArcGridFileReader(Context ctx, int resId)
    {
        this(ctx.getResources().openRawResource(resId));
    }

    /**
     * Read from InputStream
     * @param inputStream
     */
    public ArcGridFileReader (InputStream inputStream)
    {
        metaData = new HashMap();
        rasterValues = readFile(createBufferedReader(inputStream));
    }

    /**
     * Read from file
     * @param file
     */
    public ArcGridFileReader (File file)
    {
        metaData = new HashMap();
        rasterValues = readFile(createBufferedReader(file));
    }

    private BufferedReader createBufferedReader(File file)
    {
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            return br;
        } catch (FileNotFoundException e){

        }
        return null;
    }

    private BufferedReader createBufferedReader(InputStream inputStream)
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        return br;
    }

    private float[][] readFile(BufferedReader br)
    {

        String fileContents;
        int lineNumber = 0;

        try
        {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null)
            {
                if (lineNumber > META_DATA_LENGTH)
                {
                    sb.append(line);
                } else
                {
                    String[] part = line.split("\\s+");
                    metaData.put(part[0], Float.parseFloat(part[1]));
                }
                line = br.readLine();

                lineNumber++;
            }

            fileContents = sb.toString();
            br.close();

            return getData(fileContents, metaData.get(NROWS), metaData.get(NCOLS));

        } catch (IOException e)
        {
            return null;
        }
    }

    private float[][] getData(String textFile, float rowSize, float colSize)
    {
        int partOfTextFile = 1;
        String[] singleValue = textFile.split("\\s+");

        float[][] rasterValues = new float[((int) rowSize)][((int) colSize)];

        for (int row = 0; row < rowSize; row++)
        {
            for (int col = 0; col < colSize; col++)
            {
                rasterValues[row][col] = Float.parseFloat(singleValue[partOfTextFile++]);
                System.out.print(rasterValues[row][col] + "   "); //TODO: Debug. Should be removed.
            }
            System.out.println(); //TODO: Debug. Should be removed.
        }

        return rasterValues;
    }

    /**
     * Height data
     * @return
     */
    public float[][] getRasterData()
    {
        return rasterValues;
    }

    /**
     * X value for center of height data
     * @return
     */
    public float getXLLCenter()
    {
        return metaData.get(XLLCENTER);
    }

    /**
     * Y value for center of height data
     * @return
     */
    public float getYLLCenter()
    {
        return metaData.get(YLLCENTER);
    }

    /**
     *
     * @return
     */
    public float getCellSize()
    {
        return metaData.get(CELLSIZE);
    }

    /**
     * Number of rows
     * @return
     */
    public int getNRows()
    {
        return metaData.get(NROWS).intValue();
    }

    /**
     * Number of colums
     * @return
     */
    public int getNColls()
    {
        return metaData.get(NCOLS).intValue();
    }

    /**
     * Nodata_value value
     * @return
     */
    public float getNoDataValue()
    {
        return metaData.get(NODATA_VALUE);
    }

}
