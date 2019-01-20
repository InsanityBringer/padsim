package com.insanitybringer.padsim.renderer;

import android.opengl.GLES30;

import com.insanitybringer.padsim.game.Orb;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class RendererData
{
    public static final int NumCharacters = 16;
    public static int[] BigNumXs = { 382, 300, 382, 297, 382, 561, 382, 594, 382, 526 }; //30
    public static int[] BigNumYs = { 229, 289, 301, 429, 158, 1, 417, 1, 461, 1 }; //41

    //agh
    public static int[] MediumNumXs = { 525, 227, 525, 525, 525, 525, 525, 525, 525, 525 }; //30
    public static int[] MediumNumYs = { 105, 237, 133, 161, 189, 217, 245, 273, 301, 329 }; //41

    public static float[] healthRs = {.25f, .25f, 1.0f, 1.0f, 1.0f};
    public static float[] healthGs = {1.0f, .88f, 1.0f, .75f, .25f};
    public static float[] healthBs = {.25f, 1.0f, .38f, .38f, .25f};

    public static float[] attributeColorsR = { 1.0f, .25f, .25f, 1.0f, 1.0f };
    public static float[] attributeColorsG = { .25f, 1.0f, 1.0f, 1.0f, .25f };
    public static float[] attributeColorsB = { .25f, 1.0f, .25f, .25f, 1.0f };

    public final static int NumOrbs = 10;
    public static RendererObject generateOrbObjects()
    {
        RendererObject orbs;

        float[] orbUVs = {  0, 0, 104f/512f, 104f/512f, //fre
                104f/512f, 0, 208f/512f, 104f/512f, //water
                208f/512f, 0, 312f/512f, 104f/512f, //wood
                312f/512f, 0, 416f/512f, 104f/512f, //light
                0, 104f/512f, 104f/512f, 208f/512f, //dark
                0, 408f/512f, 88f/512f, 496f/512f, //heart
                104f/512f, 104f/512f, 190f/512f, 190f/512f, //jammer
                0, 312f/512f, 94f/512f, 406f/512f, //poison
                0, 208f/512f, 104f/512f, 312f/512f, //super poison
                416f/512f, 0, 502f/512f, 104f/512f }; //bomb

        float[] vertBuffer = new float[4 * 6 * NumOrbs];

        int[] vaoNames = new int[1];
        int[] indexNames = new int[1];
        int[] bufferNames = new int[1];
        GLES30.glGenBuffers(1, bufferNames, 0);
        GLES30.glGenBuffers(1, indexNames, 0);
        GLES30.glGenVertexArrays(1, vaoNames, 0);
        //0 1 2 3 4 5
        //6 7 8 9 10 11
        //12 13 14 15 16 17
        //18 19 20 21 22 23

        int uvIndex;
        int vertIndex;
        FloatBuffer vbuffer;// = FloatBuffer.allocate(24);
        float xsize;
        float ysize;

        for (int i = 0; i < NumOrbs; i++)
        {
            System.out.printf("Generating orb %d\n", i);
            xsize = ysize = Orb.orbSizes[i];
            if (i == 9) //fuck bombs
            {
                xsize = 86f;
            }
            //Fill out the buffer for the current orb. Counterclockwise, 4 vert TRIANGLE_FAN
            uvIndex = 4 * i;
            vertIndex = 4 * 6 * i;
            vertBuffer[vertIndex + 0] = -xsize / 2;
            vertBuffer[vertIndex + 1] = ysize / 2;

            vertBuffer[vertIndex + 6] = -xsize / 2;
            vertBuffer[vertIndex + 7] = -ysize / 2;

            vertBuffer[vertIndex + 12] = xsize / 2;
            vertBuffer[vertIndex + 13] = -ysize / 2;

            vertBuffer[vertIndex + 18] = xsize / 2;
            vertBuffer[vertIndex + 19] = ysize / 2;
            //These things are the same for each orb (realistically I should just push two element verts down the pipeline?)
            vertBuffer[vertIndex + 2] = vertBuffer[vertIndex + 8] = vertBuffer[vertIndex + 14] = vertBuffer[vertIndex + 20] = 0.0f; //All Zs are the same
            vertBuffer[vertIndex + 3] = vertBuffer[vertIndex + 9] = vertBuffer[vertIndex + 15] = vertBuffer[vertIndex + 21] = 1.0f; //as are W
            //Fill out UVs
            vertBuffer[vertIndex + 4] = orbUVs[uvIndex + 0];
            vertBuffer[vertIndex + 5] = orbUVs[uvIndex + 3];

            vertBuffer[vertIndex + 10] = orbUVs[uvIndex + 0];
            vertBuffer[vertIndex + 11] = orbUVs[uvIndex + 1];

            vertBuffer[vertIndex + 16] = orbUVs[uvIndex + 2];
            vertBuffer[vertIndex + 17] = orbUVs[uvIndex + 1];

            vertBuffer[vertIndex + 22] = orbUVs[uvIndex + 2];
            vertBuffer[vertIndex + 23] = orbUVs[uvIndex + 3];
        }

        vbuffer = ByteBuffer.allocateDirect(4 * vertBuffer.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vbuffer.put(vertBuffer, 0, vertBuffer.length);
        vbuffer.position(0);

        //Orbs do not use an index buffer
        //orbs = new RendererObject(bufferNames[0], 0, vaoNames[0]);
        orbs = new RendererObject(vaoNames[0], bufferNames[0], 0);
        orbs.UseObject();
        UpdateRenderer.errorCheck("Binding orb VAO");
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, bufferNames[0]);
        UpdateRenderer.errorCheck("Binding orb %d buffers");
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, 4 * vertBuffer.length, vbuffer, GLES30.GL_STATIC_DRAW);
        UpdateRenderer.errorCheck("Filling orb %d buffers");

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(0, 4, GLES30.GL_FLOAT, false, 4 * 6, 0);
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 4 * 6, 4 * 4);
        UpdateRenderer.errorCheck("Filling orb attributes");

        return orbs;
    }

    public static RendererObject generateGenericObject()
    {
        float[] genericVerts = {0f, 0f, 0.0f, 1.0f, 0f, 0f,  1f, 0f, 0.0f, 1.0f, 1.0f, 0.0f,
                0f, 1f, 0.0f, 1.0f, 0.0f, 1.0f,  1f, 1f, 0.0f, 1.0f, 1.0f, 1.0f,};

        int[] genericIndicies = { 0, 3, 1, 3, 0, 2 };

        int[] vaoid = new int[1];
        int[] bufferids = new int[2];

        GLES30.glGenVertexArrays(1, vaoid, 0);
        GLES30.glGenBuffers(2, bufferids, 0);

        IntBuffer ibuffer = ByteBuffer.allocateDirect(genericIndicies.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        ibuffer.put(genericIndicies, 0, genericIndicies.length);
        ibuffer.position(0);
        FloatBuffer vbuffer = ByteBuffer.allocateDirect(genericVerts.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vbuffer.put(genericVerts, 0, genericVerts.length);
        vbuffer.position(0);

        RendererObject object = new RendererObject(vaoid[0], bufferids[0], bufferids[1]);
        object.UseObject();
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, bufferids[0]);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, bufferids[1]);
        UpdateRenderer.errorCheck("Binding generic buffers");
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, 4 * genericVerts.length, vbuffer, GLES30.GL_STATIC_DRAW);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,4 * genericIndicies.length, ibuffer, GLES30.GL_STATIC_DRAW);
        UpdateRenderer.errorCheck("Filling generic buffers");

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(0, 4, GLES30.GL_FLOAT, false, 4 * 6, 0);
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 4 * 6, 4 * 4);
        UpdateRenderer.errorCheck("Filling generic attributes");

        return object;
    }

    public static RendererObject generateParticleObject()
    {
        float[] genericVerts = {-.5f, -.5f, 0.0f, 1.0f, 0f, 0f,  .5f, -.5f, 0.0f, 1.0f, 1.0f, 0.0f,
                -.5f, .5f, 0.0f, 1.0f, 0.0f, 1.0f,  .5f, .5f, 0.0f, 1.0f, 1.0f, 1.0f,};

        int[] genericIndicies = { 0, 3, 1, 3, 0, 2 };

        int[] vaoid = new int[1];
        int[] bufferids = new int[2];

        GLES30.glGenVertexArrays(1, vaoid, 0);
        GLES30.glGenBuffers(2, bufferids, 0);

        IntBuffer ibuffer = ByteBuffer.allocateDirect(genericIndicies.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        ibuffer.put(genericIndicies, 0, genericIndicies.length);
        ibuffer.position(0);
        FloatBuffer vbuffer = ByteBuffer.allocateDirect(genericVerts.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vbuffer.put(genericVerts, 0, genericVerts.length);
        vbuffer.position(0);

        RendererObject object = new RendererObject(vaoid[0], bufferids[0], bufferids[1]);
        object.UseObject();
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, bufferids[0]);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, bufferids[1]);
        UpdateRenderer.errorCheck("Binding generic buffers");
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, 4 * genericVerts.length, vbuffer, GLES30.GL_STATIC_DRAW);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,4 * genericIndicies.length, ibuffer, GLES30.GL_STATIC_DRAW);
        UpdateRenderer.errorCheck("Filling generic buffers");

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(0, 4, GLES30.GL_FLOAT, false, 4 * 6, 0);
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 4 * 6, 4 * 4);
        UpdateRenderer.errorCheck("Filling generic attributes");

        return object;
    }

    public static RendererObject generateCheckerboard30()
    {
        float[] checkerboardBuffer6x5 = new float[6 * 5 * 6 * 4]; //7 verts wide, 6 verts tall, 6 attributes per vert, 4 verts per cell
        int[] checkerboardIndexBuffer6x5 = new int[6 * 5 * 6]; //7 verts wide, 6 verts tall, 6 indexes per triangle pair
        float[] darkCheckerUVs = { 300f / 512f, 280f / 512f, 405f / 512f, 385f / 512f };
        float[] lightCheckerUVs = { 405f / 512f, 280f / 512f, 510f / 512f, 385f / 512f };
        int index = 0;
        int vertIndex = 0;
        int baseVert = 0;
        float xpos, ypos, xend, yend;

        int[] vaoid = new int[1];
        int[] bufferids = new int[2];

        GLES30.glGenVertexArrays(1, vaoid, 0);
        GLES30.glGenBuffers(2, bufferids, 0);

        //why am i allowed to write code
        for (int x = 0; x < 6; x++)
        {
            for (int y = 0; y < 5; y++)
            {
                xpos = (x) * 105.0f;
                ypos = (y) * 105.0f;
                xend = (x + 1) * 105.0f;
                yend = (y + 1) * 105.0f;

                //why am i allowed to write code
                checkerboardBuffer6x5[index + 0] = xpos;
                checkerboardBuffer6x5[index + 1] = ypos;
                checkerboardBuffer6x5[index + 2] = 0.0f;
                checkerboardBuffer6x5[index + 3] = 1.0f;
                checkerboardBuffer6x5[index + 6] = xend;
                checkerboardBuffer6x5[index + 7] = ypos;
                checkerboardBuffer6x5[index + 8] = 0.0f;
                checkerboardBuffer6x5[index + 9] = 1.0f;
                checkerboardBuffer6x5[index + 12] = xend;
                checkerboardBuffer6x5[index + 13] = yend;
                checkerboardBuffer6x5[index + 14] = 0.0f;
                checkerboardBuffer6x5[index + 15] = 1.0f;
                checkerboardBuffer6x5[index + 18] = xpos;
                checkerboardBuffer6x5[index + 19] = yend;
                checkerboardBuffer6x5[index + 20] = 0.0f;
                checkerboardBuffer6x5[index + 21] = 1.0f;
                if (((x % 2 != 0) ^ (y % 2 != 0)))
                {
                    checkerboardBuffer6x5[index + 4] = lightCheckerUVs[0];
                    checkerboardBuffer6x5[index + 5] = lightCheckerUVs[1];
                    checkerboardBuffer6x5[index + 10] = lightCheckerUVs[2];
                    checkerboardBuffer6x5[index + 11] = lightCheckerUVs[1];
                    checkerboardBuffer6x5[index + 16] = lightCheckerUVs[2];
                    checkerboardBuffer6x5[index + 17] = lightCheckerUVs[3];
                    checkerboardBuffer6x5[index + 22] = lightCheckerUVs[0];
                    checkerboardBuffer6x5[index + 23] = lightCheckerUVs[3];
                }
                else
                {
                    checkerboardBuffer6x5[index + 4] = darkCheckerUVs[0];
                    checkerboardBuffer6x5[index + 5] = darkCheckerUVs[1];
                    checkerboardBuffer6x5[index + 10] = darkCheckerUVs[2];
                    checkerboardBuffer6x5[index + 11] = darkCheckerUVs[1];
                    checkerboardBuffer6x5[index + 16] = darkCheckerUVs[2];
                    checkerboardBuffer6x5[index + 17] = darkCheckerUVs[3];
                    checkerboardBuffer6x5[index + 22] = darkCheckerUVs[0];
                    checkerboardBuffer6x5[index + 23] = darkCheckerUVs[3];
                }

                checkerboardIndexBuffer6x5[vertIndex + 0] = baseVert;
                checkerboardIndexBuffer6x5[vertIndex + 1] = baseVert + 2;
                checkerboardIndexBuffer6x5[vertIndex + 2] = baseVert + 1;

                checkerboardIndexBuffer6x5[vertIndex + 3] = baseVert + 2;
                checkerboardIndexBuffer6x5[vertIndex + 4] = baseVert;
                checkerboardIndexBuffer6x5[vertIndex + 5] = baseVert + 3;

                index += 24;
                vertIndex += 6;
                baseVert += 4;
            }
        }
        IntBuffer ibuffer = ByteBuffer.allocateDirect(checkerboardIndexBuffer6x5.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        ibuffer.put(checkerboardIndexBuffer6x5, 0, 6 * 5 * 6);
        ibuffer.position(0);
        //FloatBuffer vbuffer = FloatBuffer.allocate(6 * 5 * 6 * 4);
        //vbuffer.put(checkerboardBuffer6x5, 0, 6 * 5 * 6 * 4);
        //FloatBuffer vbuffer = FloatBuffer.wrap(checkerboardBuffer6x5);
        FloatBuffer vbuffer = ByteBuffer.allocateDirect(checkerboardBuffer6x5.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vbuffer.put(checkerboardBuffer6x5, 0, 6 * 5 * 6 * 4);
        vbuffer.position(0);

        RendererObject checkerboardObject = new RendererObject(vaoid[0], bufferids[0], bufferids[1]);
        checkerboardObject.UseObject();
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, bufferids[0]);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, bufferids[1]);
        UpdateRenderer.errorCheck("Binding 6x5 buffers");
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, 4 * 6 * 5 * 6 * 4, vbuffer, GLES30.GL_STATIC_DRAW);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,4 * 6 * 5 * 6, ibuffer, GLES30.GL_STATIC_DRAW);
        UpdateRenderer.errorCheck("Filling 6x5 buffers");

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(0, 4, GLES30.GL_FLOAT, false, 4 * 6, 0);
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 4 * 6, 4 * 4);
        UpdateRenderer.errorCheck("Filling 6x5 attributes");

        return checkerboardObject;
    }

    public static RendererObject generateHealthBarBack()
    {
        float[] healthBarVerts = {
                9.0f, 0.0f, 0.0f, 1.0f, 176f/512f, 48f/512f,
                46.0f, 0.0f, 0.0f, 1.0f, 214f/512f, 48f/512f,
                618.0f, 0.0f, 0.0f, 1.0f, 244f/512f, 48f/512f,
                630.0f, 0.0f, 0.0f, 1.0f, 256f/512f, 48f/512f,

                9.0f, 36.0f, 0.0f, 1.0f, 176f/512f, 84f/512f,
                46.0f, 36.0f, 0.0f, 1.0f, 214f/512f, 84f/512f,
                618.0f, 36.0f, 0.0f, 1.0f, 244f/512f, 84f/512f,
                630.0f, 36.0f, 0.0f, 1.0f, 256f/512f, 84f/512f };

        int[] healthBarIndexes = { 0, 5, 1, 5, 0, 4, 1, 6, 2, 6, 1, 5, 2, 7, 3, 7, 2, 6 };

        int[] vaoid = new int[1];
        int[] bufferids = new int[2];

        GLES30.glGenVertexArrays(1, vaoid, 0);
        GLES30.glGenBuffers(2, bufferids, 0);

        IntBuffer ibuffer = ByteBuffer.allocateDirect(healthBarIndexes.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        ibuffer.put(healthBarIndexes, 0, healthBarIndexes.length);
        ibuffer.position(0);
        FloatBuffer vbuffer = ByteBuffer.allocateDirect(healthBarVerts.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vbuffer.put(healthBarVerts, 0, healthBarVerts.length);
        vbuffer.position(0);

        RendererObject object = new RendererObject(vaoid[0], bufferids[0], bufferids[1]);
        object.UseObject();
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, bufferids[0]);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, bufferids[1]);
        UpdateRenderer.errorCheck("Binding generic buffers");
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, 4 * healthBarVerts.length, vbuffer, GLES30.GL_STATIC_DRAW);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,4 * healthBarIndexes.length, ibuffer, GLES30.GL_STATIC_DRAW);
        UpdateRenderer.errorCheck("Filling generic buffers");

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(0, 4, GLES30.GL_FLOAT, false, 4 * 6, 0);
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 4 * 6, 4 * 4);
        UpdateRenderer.errorCheck("Filling generic attributes");

        return object;
    }

    public static RendererObject generateEnemyHealthBarBack()
    {
        float[] healthBarVerts =
                {0.0f, 105.0f, 0.0f, 1.0f, 175f/512f, 102f/512f,
                13.0f, 105.0f, 0.0f, 1.0f, 188f/512f, 102f/512f,
                87.0f, 105.0f, 0.0f, 1.0f, 228f/512f, 102f/512f,
                100.0f, 105.0f, 0.0f, 1.0f, 241f/512f, 102f/512f,

                0.0f, 125.0f, 0.0f, 1.0f, 175f/512f, 122f/512f,
                13.0f, 125.0f, 0.0f, 1.0f, 188f/512f, 122f/512f,
                87.0f, 125.0f, 0.0f, 1.0f, 228f/512f, 122f/512f,
                100.0f, 125.0f, 0.0f, 1.0f, 241f/512f, 122f/512f,

                0f, -30.0f, 0.0f, 1.0f, 442f/512f, 83f/512f,
                27f, -30.0f, 0.0f, 1.0f, 469f/512f, 83f/512f,
                0f, -4.0f, 0.0f, 1.0f, 442f/512f, 109f/512f,
                27f, -4.0f, 0.0f, 1.0f, 469f/512f, 109f/512f,

                0f, 112.0f, 0.0f, 1.0f, 321f/512f, 109f/512f,
                78f, 112.0f, 0.0f, 1.0f, 325f/512f, 109f/512f,
                0f, 117.0f, 0.0f, 1.0f, 321f/512f, 114f/512f,
                78f, 117.0f, 0.0f, 1.0f, 325f/512f, 114f/512f};

        int[] healthBarIndexes = { 0, 5, 1, 5, 0, 4, 1, 6, 2, 6, 1, 5, 2, 7, 3, 7, 2, 6,  8, 10, 11, 8, 11, 9, 12, 14, 15, 12, 15, 13 };

        int[] vaoid = new int[1];
        int[] bufferids = new int[2];

        GLES30.glGenVertexArrays(1, vaoid, 0);
        GLES30.glGenBuffers(2, bufferids, 0);

        IntBuffer ibuffer = ByteBuffer.allocateDirect(healthBarIndexes.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        ibuffer.put(healthBarIndexes, 0, healthBarIndexes.length);
        ibuffer.position(0);
        FloatBuffer vbuffer = ByteBuffer.allocateDirect(healthBarVerts.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vbuffer.put(healthBarVerts, 0, healthBarVerts.length);
        vbuffer.position(0);

        RendererObject object = new RendererObject(vaoid[0], bufferids[0], bufferids[1]);
        object.UseObject();
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, bufferids[0]);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, bufferids[1]);
        UpdateRenderer.errorCheck("Binding generic buffers");
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, 4 * healthBarVerts.length, vbuffer, GLES30.GL_STATIC_DRAW);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,4 * healthBarIndexes.length, ibuffer, GLES30.GL_STATIC_DRAW);
        UpdateRenderer.errorCheck("Filling generic buffers");

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);
        GLES30.glVertexAttribPointer(0, 4, GLES30.GL_FLOAT, false, 4 * 6, 0);
        GLES30.glVertexAttribPointer(1, 2, GLES30.GL_FLOAT, false, 4 * 6, 4 * 4);
        UpdateRenderer.errorCheck("Filling generic attributes");

        return object;
    }

    public static float getSmallCharX(int i)
    {
        float baseXUV = 182;
        int xindex = i % 10;

        return baseXUV + (xindex * 32);
    }

    public static float getSmallCharY(int i)
    {
        float baseYUV = 179;
        int yindex = i / 10;

        return baseYUV + (yindex * 32);
    }
}
