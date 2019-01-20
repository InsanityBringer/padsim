package com.insanitybringer.padsim.renderer;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.opengl.GLES30;

import com.insanitybringer.padsim.game.Monster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class Shader
{
    public int getShaderid()
    {
        return shaderid;
    }

    private int shaderid;
    //For debugging purposes atm
    private String name;

    //TODO: Debug encapuslation
    public boolean isValid = false;
    public String log = "it fine";

    public Shader(String name)
    {
        this.name = name;
    }

    public void useShader()
    {
        GLES30.glUseProgram(shaderid);
    }

    public void init()
    {
        shaderid = GLES30.glCreateProgram();
    }

    public abstract void findUniforms();

    public void addShader(String name, Resources resources, int type)
    {
        System.out.printf("Adding shader %s\n", name);
        /*StreamReader sr = new StreamReader(File.Open(filename, FileMode.Open));
        string shaderSource = sr.ReadToEnd();
        sr.Close();
        sr.Dispose();*/

        String shaderSource = loadShaderText(name, resources);
        int id = GLES30.glCreateShader(type);
        GLES30.glShaderSource(id, shaderSource);
        GLES30.glCompileShader(id);

        int[] status = new int[1];
        GLES30.glGetShaderiv(id, GLES30.GL_COMPILE_STATUS, status, 0);
        if (status[0] != 1)
        {
            System.out.printf("Error compiling shader %s:", name);
            String infolog = GLES30.glGetShaderInfoLog(id);
            System.out.println(infolog);
            log += "Compile error: \n";
            log += infolog;
            log += "\n";
        }
        else
        {
            System.out.printf("Successfully compiled %s\n", name);
            GLES30.glAttachShader(shaderid, id);
        }
    }

    public void linkShader()
    {
        GLES30.glLinkProgram(shaderid);
        int[] status = new int[1];
        GLES30.glGetProgramiv(shaderid, GLES30.GL_LINK_STATUS, status, 0);
        System.out.println("Linking program");
        if (status[0] != 1)
        {
            System.out.printf("Error linking program %s (id %d) with status code %d:", name, shaderid, status[0]);
            String log = GLES30.glGetProgramInfoLog(shaderid);
            this.log += "Link error: \n";
            this.log += log;
            this.log += "\n";
            System.out.println(log);
        }
        else
        {
            isValid = true;
        }
    }

    public String loadShaderText(String filename, Resources resources)
    {
        AssetManager assetManager = resources.getAssets();
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(filename))))
        {
            String line = reader.readLine();
            while (line != null)
            {
                stringBuilder.append(line);
                stringBuilder.append('\n'); //Probably not required, is JSON whitespace sensitive?
                line = reader.readLine();
            }
        }
        catch (IOException exc)
        {
            //TODO debug
            System.out.println("this probably shouldn't have happened? but it did, so...");
            return "";
        }

        return stringBuilder.toString();
    }
}
