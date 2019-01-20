package com.insanitybringer.padsim.renderer.shader;

import android.opengl.GLES30;

import com.insanitybringer.padsim.renderer.Shader;

public class TextureFixedShader extends Shader
{
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

    private int scaleLoc;
    private int rotationLoc;
    private int projectionLoc;
    private int textureLoc;
    private int offsetLoc;
    private int alphaLoc;
    private int colorLoc;

    public TextureFixedShader(String name)
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
    }
}
