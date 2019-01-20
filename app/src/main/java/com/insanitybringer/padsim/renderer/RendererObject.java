package com.insanitybringer.padsim.renderer;

import android.opengl.GLES30;

public class RendererObject
{
    private int vaoID;
    private int bufferID;
    private int indexID;

    private float x;

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    private float y;
    private Shader shader;

    public RendererObject(int vaoID, int bufferID, int indexID)
    {
        this.vaoID = vaoID;
        this.bufferID = bufferID;
        this.indexID = indexID;
    }

    public void SetShader(Shader shader)
    {
        this.shader = shader;
    }

    public void SetPosition(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public void UseObject()
    {
        GLES30.glBindVertexArray(vaoID);
    }
}
