package com.jonasmiran.hig.datorgrafikprojekt;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Miran on 31/10/2016.
 */
public class ArcGridFileReader {

    private Map<String, Float> metaData = new HashMap();
    private final String NCOLS = "ncols";
    private final String NROWS = "nrows";
    private final String XLLCENTER = "xllcenter";
    private final String YLLCENTER = "yllcenter";
    private final String CELLSIZE = "cellsize";
    private final String NODATA_VALUE = "nodata_value";

    private float[][] rasterValues;

    public ArcGridFileReader(Context ctx, int resId)
    {
        rasterValues = readFile(ctx, resId);
    }

    private float[][] readFile(Context ctx, int resId)
    {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        String fileContents;
        int lineNumber = 0;

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        try
        {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null)
            {
                if (lineNumber > 5) //Metadata length
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

    public float[][] getRasterData()
    {
        return rasterValues;
    }

    public float getXLLCenter()
    {
        return metaData.get(XLLCENTER);
    }

    public float getYLLCenter()
    {
        return metaData.get(YLLCENTER);
    }

    public float getCellSize()
    {
        return metaData.get(CELLSIZE);
    }

    public int getNRows()
    {
        return metaData.get(NROWS).intValue();
    }

    public int getNColls()
    {
        return metaData.get(NCOLS).intValue();
    }

}
