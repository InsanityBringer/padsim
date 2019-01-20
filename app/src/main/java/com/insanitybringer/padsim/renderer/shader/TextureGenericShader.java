package com.insanitybringer.padsim.renderer.shader;

import android.opengl.GLES30;

import com.insanitybringer.padsim.renderer.Shader;

public class TextureGenericShader extends Shader
{
    private int scaleLoc;
    private int rotationLoc;
    private int projectionLoc;
    private int textureLoc;
    private int offsetLoc;
    private int alphaLoc;
    private int colorLoc;
    private int textureOriginLoc;
    private int textureSizeLoc;
    public TextureGenericShader(String name)
    {
        super(name);
    }

    @Override
    public void findUniforms()
    {
        useShader();
        int shaderID = getShaderid();
        scaleLoc = GLES30.glGetUniformLocation(shaderID, "scale");
        rotationLoc = GLES30.glGetUniformLocation(shaderID, "rotation");
        projectionLoc = GLES30.glGetUniformLocation(shaderID, "projectionMatrix");
        textureLoc = GLES30.glGetUniformLocation(shaderID, "surfaceTexture");
        offsetLoc = GLES30.glGetUniformLocation(shaderID, "offset");
        alphaLoc = GLES30.glGetUniformLocation(shaderID, "alpha");
        colorLoc = GLES30.glGetUniformLocation(shaderID, "colorMultiplier");
        textureOriginLoc = GLES30.glGetUniformLocation(shaderID, "textureOrigin");
        textureSizeLoc = GLES30.glGetUniformLocation(shaderID, "texturePartSize");
    }

    public int getScaleLoc()
    {
        return scaleLoc;
    }

    public int getRotationLoc()
    {
        return rotationLoc;
    }

    public int getProjectionLoc()
    {
        return projectionLoc;
    }

    public int getTextureLoc()
    {
        return textureLoc;
    }

    public int getOffsetLoc()
    {
        return offsetLoc;
    }

    public int getAlphaLoc()
    {
        return alphaLoc;
    }

    public int getColorLoc()
    {
        return colorLoc;
    }

    public int getTextureOriginLoc()
    {
        return textureOriginLoc;
    }

    public int getTextureSizeLoc()
    {
        return textureSizeLoc;
    }
}
