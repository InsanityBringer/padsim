package com.insanitybringer.padsim.renderer;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;

public class TextureLoader
{
    public static int loadTexture(String filename, Resources resources) throws IOException
    {
        InputStream bitmapReader = resources.getAssets().open(filename);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, bitmapReader);
        int[] texids = new int[1];
        GLES30.glGenTextures(1, texids, 0);
        int texid = texids[0];

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texid);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmapDrawable.getBitmap(), 0);

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

        return texid;
    }
}
