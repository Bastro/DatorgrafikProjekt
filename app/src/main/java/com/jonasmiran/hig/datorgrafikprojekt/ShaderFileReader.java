package com.jonasmiran.hig.datorgrafikprojekt;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Miran, Jonas on 31/10/2016.
 * Reads Raw shader file and returns a String
 */
public class ShaderFileReader {

    /**
     * Reads shader file from Android Studio resources and return file in a String
     * @param ctx
     * @param resId
     * @return
     */
    public static String readRawTextFile(Context ctx, int resId)
    {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while (( line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }
}
