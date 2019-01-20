package com.insanitybringer.padsim.renderer;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.insanitybringer.padsim.game.Card;
import com.insanitybringer.padsim.game.DamageNumber;
import com.insanitybringer.padsim.game.Enemy;
import com.insanitybringer.padsim.game.GameState;
import com.insanitybringer.padsim.game.Orb;
import com.insanitybringer.padsim.game.OrbAnimationState;
import com.insanitybringer.padsim.renderer.shader.TextureFixedShader;
import com.insanitybringer.padsim.renderer.shader.TextureGenericShader;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class UpdateRenderer implements GLSurfaceView.Renderer
{
    public static final float TeamBaseline = 676.0f;
    public static final int VirtualResolution = 640;
    private static final double timestep = 1.0d / 30.0d;
    private GameState gameState;
    //private float aspectRatio = 1.7125f; //"16/9"
    private float aspectRatio = 1.5f; //3/2
    private int screenWidth, screenHeight;
    private float gameHeight;
    private float gameWindowOrigin = 0.0f; //Only important for 2:1 aspect ratio
    private int originX, originY;

    //Texture names
    private int uipat1TexName;
    private int block2TexName;
    private int treasureTexName;
    private int bgTexName;
    private int[] cardNames = new int[6];
    private int[] enemyNames = new int[7];
    private int[] particleTextures = new int[2];

    //Shader names
    public static TextureGenericShader textureGenericShader;
    public static TextureFixedShader textureFixedShader;

    //Renderer objects
    private RendererObject boardObject;
    private RendererObject orbObjects;
    private RendererObject genericObject;
    private RendererObject healthBarBackObject;
    private RendererObject particleObject;
    private RendererObject enemyHealthBarBackObject;

    public UpdateRenderer(GameState gameState)
    {
        this.gameState = gameState;
    }

    private double currentTime = 0, gameTime = 0;

    private float liveTouchX, liveTouchY;
    private float touchX, touchY;
    private boolean touchDown = false;
    private boolean lastTouchDown = false;

    public void init()
    {
        gameTime = currentTime = time();
        loadObjects();
        loadShaders();
        loadGlobalTextures();
    }

    private void loadShaders()
    {
        textureGenericShader = new TextureGenericShader("TextureGeneric");
        textureGenericShader.init();
        textureGenericShader.addShader("shader/VertexTextureGeneric.txt", gameState.getApplication().getResources(), GLES30.GL_VERTEX_SHADER);
        textureGenericShader.addShader("shader/FragmentTextureGeneric.txt", gameState.getApplication().getResources(), GLES30.GL_FRAGMENT_SHADER);
        textureGenericShader.linkShader();
        errorCheck("Linking shaders");
        if (textureGenericShader.isValid)
        {
            textureGenericShader.findUniforms();
            errorCheck("Finding uniforms");
        }

        textureFixedShader = new TextureFixedShader("textureFixed");
        textureFixedShader.init();
        textureFixedShader.addShader("shader/VertexTextureFixed.txt", gameState.getApplication().getResources(), GLES30.GL_VERTEX_SHADER);
        textureFixedShader.addShader("shader/FragmentTextureGeneric.txt", gameState.getApplication().getResources(), GLES30.GL_FRAGMENT_SHADER);
        textureFixedShader.linkShader();
        errorCheck("Linking textureFixedShader");
        if (textureFixedShader.isValid)
        {
            textureFixedShader.findUniforms();
            errorCheck("Finding uniforms");
        }
        else
        {
            gameState.debug = textureFixedShader.log;
        }
    }

    private void loadGlobalTextures()
    {
        try
        {
            uipat1TexName = TextureLoader.loadTexture("UIPAT1.PNG", gameState.getApplication().getResources());
            block2TexName = TextureLoader.loadTexture("BLOCK2.PNG", gameState.getApplication().getResources());
            treasureTexName = TextureLoader.loadTexture("TREASURE.PNG", gameState.getApplication().getResources());
            bgTexName = TextureLoader.loadTexture("MERGEBG.PNG", gameState.getApplication().getResources());
            particleTextures[0] = TextureLoader.loadTexture("EFC_FIRE.PNG", gameState.getApplication().getResources());
            particleTextures[1] = uipat1TexName;
        }
        catch (IOException exc)
        {
            exc.printStackTrace();
        }
        errorCheck("Loading global textures");
    }

    private void loadObjects()
    {
        boardObject = RendererData.generateCheckerboard30();
        errorCheck("Loading board object");
        orbObjects = RendererData.generateOrbObjects();
        errorCheck("Loading orb objects");
        genericObject = RendererData.generateGenericObject();
        errorCheck("Loading generic object");
        healthBarBackObject = RendererData.generateHealthBarBack();
        particleObject = RendererData.generateParticleObject();
        enemyHealthBarBackObject = RendererData.generateEnemyHealthBarBack();
    }

    public void initTeamTextures()
    {
        Card[] team = gameState.getTeam();
        for (int i = 0; i < 6; i++)
        {
            Card card = team[i];
            if (card.getID() != 0)
            {
                if (cardNames[i] != 0)
                    GLES30.glDeleteTextures(1, cardNames, i);

                try
                {
                    cardNames[i] = TextureLoader.loadTexture(String.format(Locale.getDefault(), "icon/%04d.png", card.getID()), gameState.getApplication().getResources());
                    System.out.printf("Got texture id %d\n", cardNames[i]);
                }
                catch (IOException exc)
                {
                    System.out.println("we failed");
                    exc.printStackTrace();
                }
            }
        }
        Enemy[] enemies = gameState.enemies;
        for (int i = 0; i < 6; i++)
        {
            Enemy enemy = enemies[i];
            if (enemy != null)
            {
                if (enemyNames[i] != 0)
                    GLES30.glDeleteTextures(1, cardNames, i);

                try
                {
                    enemyNames[i] = TextureLoader.loadTexture(String.format(Locale.getDefault(), "icon/%04d.png", enemy.getID()), gameState.getApplication().getResources());
                    System.out.printf("Got texture id %d\n", enemyNames[i]);
                }
                catch (IOException exc)
                {
                    System.out.println("we failed");
                    exc.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES30.glEnable(GLES30.GL_BLEND);
        init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        //GLES30.glViewport(0, 0, width, height);
        initTeamTextures();
        int aspectWidth = (int)(height / aspectRatio);
        if (width < aspectWidth)
        {
            int aspectHeight = (int)(width * aspectRatio);
            screenWidth = width;
            screenHeight = aspectHeight;
            originX = 0;
            originY = (height - aspectHeight) / 2;
            GLES30.glViewport(0, originY, width, aspectHeight);
        }
        else
        {
            screenWidth = aspectWidth;
            screenHeight = height;
            originY = 0;
            originX = (width - aspectWidth) / 2;
            GLES30.glViewport(originX, 0, aspectWidth, height);
        }

        float[] projectionMatrix = new float[16];
        Matrix.orthoM(projectionMatrix, 0, 0, VirtualResolution, VirtualResolution * aspectRatio, 0, -5, 5);
        textureGenericShader.useShader();
        GLES30.glUniformMatrix4fv(textureGenericShader.getProjectionLoc(), 1, false, projectionMatrix, 0);
        textureFixedShader.useShader();
        GLES30.glUniformMatrix4fv(textureFixedShader.getProjectionLoc(), 1, false, projectionMatrix, 0);
        errorCheck("Projection matrix");
        gameHeight = VirtualResolution * aspectRatio;
        gameState.setScreenHeight(gameHeight);
    }

    public void touch(float x, float y)
    {
        x = projectX(x);
        y = projectY(y);
        liveTouchX = x;
        liveTouchY = y;
        touchDown = true;
        gameState.touch(x, y);
    }

    public void touchUp()
    {
        gameState.touchUp();
        touchDown = false;
    }

    public float projectX(float x)
    {
        float lx = x - originX;
        lx /= screenWidth;
        return lx * VirtualResolution;
    }

    public float projectY(float y)
    {
        float ly = y - originY;
        ly /= screenHeight;
        return ly * gameHeight;
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        if (!textureFixedShader.isValid)
            GLES30.glClearColor(1.0f, 0.5f, 0.5f, 1.0f);
        currentTime = time();
        if (currentTime - gameTime > 1.0)
        {
            System.out.println("Can't keep up! Dropping ticks");
            gameTime = currentTime;
        }
        while (gameTime < currentTime)
        {
            gameTime += timestep;
            touchX = liveTouchX; touchY = liveTouchY;
            gameState.update();
        }

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        if (textureFixedShader.isValid)
        {
            textureGenericShader.useShader();
            GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, bgTexName);
            drawBackground();
            drawEnemies();
            drawCards();
            drawDamageNumbers();
            textureFixedShader.useShader();
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, uipat1TexName);
            drawBoard();
            if (gameState.getBoard().getHeldOrb() != null)
                drawHeldOrb(touchX, touchY, gameState.getBoard().getHeldOrb());
            textureGenericShader.useShader();
            //genericObject.UseObject();
            //GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, bgTexName);
            //drawDebugObject();
            particleObject.UseObject();
            drawEffects();
        }
    }

    public static void errorCheck(String context)
    {
        int errorCode = GLES30.glGetError();
        //System.out.printf("Error check in context %s: \n", context);
        while (errorCode != GLES30.GL_NO_ERROR)
        {
            System.out.printf("GL Error in context %s: ", context);
            switch (errorCode)
            {
                case GLES30.GL_INVALID_OPERATION:
                    System.out.println("Invalid operation. ");
                    break;
                case GLES30.GL_INVALID_VALUE:
                    System.out.println("Invalid value. ");
                    break;
                case GLES30.GL_INVALID_ENUM:
                    System.out.println("Invalid enum. ");
                    break;
                case GLES30.GL_OUT_OF_MEMORY:
                    System.out.println("Out of memory. ");
                    break;
                default:
                    System.out.println("Unspecified error. ");
                    break;
            }

            errorCode = GLES30.glGetError();
        }
    }

    private void drawBoard()
    {
        boardObject.UseObject();
        GLES30.glUniform1i(textureFixedShader.getTextureLoc(), 0);
        GLES30.glUniform1f(textureFixedShader.getAlphaLoc(), 1.0f);
        GLES30.glUniform3f(textureFixedShader.getColorLoc(), 1.0f, 1.0f, 1.0f);
        GLES30.glUniform2f(textureFixedShader.getScaleLoc(), 1.0f, 1.0f);
        GLES30.glUniform3f(textureFixedShader.getOffsetLoc(), 3.0f, gameHeight - (105 * 5f), 0.0f);
        errorCheck("Board uniforms");

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6 * 5 * 2 * 3, GLES30.GL_UNSIGNED_INT, 0);

        drawHealthBar(gameState.currentHealth, gameState.maxHealth);

        Orb[][] orbs = gameState.getBoard().getOrbs();
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, block2TexName);
        for (int x = 5; x >= 0; x--)
        {
            for (int y = 0; y < 5; y++)
            {
                drawOrb(orbs[x][y], false);
            }
        }

        //Animating orbs (swapping, etc) need a separate pass to avoid overlapping with other orbs
        for (int x = 5; x >= 0; x--)
        {
            for (int y = 0; y < 5; y++)
            {
                drawOrb(orbs[x][y], true);
            }
        }
    }

    private void drawOrb(Orb orb, boolean animating)
    {
        if ((orb.getAnimationState() != OrbAnimationState.Idle) == animating)
        {
            orbObjects.UseObject();
            float scale = 1.0f;
            GLES30.glUniform2f(textureFixedShader.getScaleLoc(), scale, scale);
            GLES30.glUniform1f(textureFixedShader.getAlphaLoc(), orb.getAlpha());
            GLES30.glUniform3f(textureFixedShader.getOffsetLoc(), 3.0f + orb.getPosX(), gameHeight - orb.getPosY(), 0.0f);
            //GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 4 * orb.getAttribute(), 4);
        }
    }

    private void drawHealthBar(int min, int max)
    {
        healthBarBackObject.UseObject();
        //GLES30.glUniform2f(textureFixedShader.getScaleLoc(), 1.0f, 1.0f);
        //GLES30.glUniform1f(textureFixedShader.getAlphaLoc(), 1.0f);
        GLES30.glUniform3f(textureFixedShader.getOffsetLoc(), 0.0f, gameHeight - 563, 0.0f);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 18, GLES30.GL_UNSIGNED_INT, 0);
        drawHealthBarValue(min, max);
        textureFixedShader.useShader();
    }

    private void drawHealthBarValue(int min, int max)
    {
        textureGenericShader.useShader();
        genericObject.UseObject();
        float scale = (float)min / max;
        GLES30.glUniform1i(textureGenericShader.getTextureLoc(), 0);
        GLES30.glUniform1f(textureGenericShader.getAlphaLoc(), 1.0f);
        GLES30.glUniform3f(textureGenericShader.getColorLoc(), 1.0f, 1.0f, 1.0f);
        GLES30.glUniform2f(textureGenericShader.getScaleLoc(), (float)571 * scale, (float)12);
        GLES30.glUniform3f(textureGenericShader.getOffsetLoc(), 46.0f, gameHeight - 552.0f, 0.0f);
        GLES30.glUniform2f(textureGenericShader.getTextureOriginLoc(), 269.0f, 66.0f);
        GLES30.glUniform2f(textureGenericShader.getTextureSizeLoc(), 14.0f, 12.0f);
        errorCheck("Debug uniforms");

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0);
        drawHealthValue(614, gameHeight - 552, min, max);
    }

    private void drawHealthValue(float x, float y, int min, int max)
    {
        String num1 = Integer.toString(min);
        String num2 = Integer.toString(max);

        int length = num1.length() + num2.length() + 1;
        float baseoffset = length * 16;
        GLES30.glUniform2f(textureGenericShader.getTextureSizeLoc(), 21, 27);
        GLES30.glUniform2f(textureGenericShader.getScaleLoc(), 21, 27);
        GLES30.glUniform3f(textureGenericShader.getColorLoc(), RendererData.healthRs[0], RendererData.healthGs[0], RendererData.healthBs[0]);
        errorCheck("Health num overall uniforms");

        for (int i = 0; i < num1.length(); i++)
        {
            char c = num1.charAt(i);
            c -= '0';

            GLES30.glUniform3f(textureGenericShader.getOffsetLoc(), x + (16 * i) - baseoffset, y, 0.0f);
            GLES30.glUniform2f(textureGenericShader.getTextureOriginLoc(), RendererData.getSmallCharX(c), RendererData.getSmallCharY(c));
            errorCheck("Health num first uniforms");

            GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0);
            errorCheck("Health num first draw");
        }
        //the / character
        GLES30.glUniform3f(textureGenericShader.getOffsetLoc(), x + (16 * num1.length()) - baseoffset, y, 0.0f);
        GLES30.glUniform2f(textureGenericShader.getTextureOriginLoc(), RendererData.getSmallCharX(10), RendererData.getSmallCharY(10));
        errorCheck("Health num slash uniforms");
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0);
        errorCheck("Health num slash draw");
        //Max value
        for (int i = 0; i < num2.length(); i++)
        {
            char c = num2.charAt(i);
            c -= '0';

            GLES30.glUniform3f(textureGenericShader.getOffsetLoc(), x + (16 * (i + num1.length() + 1)) - baseoffset, y, 0.0f);
            GLES30.glUniform2f(textureGenericShader.getTextureOriginLoc(), RendererData.getSmallCharX(c), RendererData.getSmallCharY(c));
            errorCheck("Health num second uniforms");

            GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0);
            errorCheck("Health num second draw");
        }
    }

    public void drawCards()
    {
        genericObject.UseObject();
        GLES30.glUniform2f(textureGenericShader.getScaleLoc(), (float)100, (float)100);
        GLES30.glUniform2f(textureGenericShader.getTextureOriginLoc(), 0.0f, 0.0f);
        GLES30.glUniform2f(textureGenericShader.getTextureSizeLoc(), 100.0f, 100.0f);

        Card[] team = gameState.getTeam();
        for (int i = 0; i < 6; i++)
        {
            if (team[i].getID() != 0)
            {
                drawCard(i, team[i]);
            }
        }

        //Twice to avoid changing state as much
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, treasureTexName);
        for (int i = 0; i < 6; i++)
        {
            if (team[i].getID() != 0)
            {
                drawCardDamage(team[i]);
            }
        }
        GLES30.glUniform3f(textureGenericShader.getColorLoc(), 1.0f, 1.0f, 1.0f);
    }

    public void drawCard(int slot, Card card)
    {
        //TODO: Atlas card textures for efficiency
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, cardNames[slot]);
        GLES30.glUniform3f(textureGenericShader.getOffsetLoc(), card.getPosX(), gameHeight - TeamBaseline - card.getPosY(), 0.0f);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0);
    }

    public void drawEnemies()
    {
        GLES30.glUniform2f(textureGenericShader.getScaleLoc(), (float)100, (float)100);
        GLES30.glUniform2f(textureGenericShader.getTextureOriginLoc(), 0.0f, 0.0f);
        GLES30.glUniform2f(textureGenericShader.getTextureSizeLoc(), 100.0f, 100.0f);

        Enemy[] enemies = gameState.enemies;
        for (int i = 0; i < 7; i++)
        {
            if (enemies[i] != null)
            {
                drawEnemy(i, enemies[i]);
            }
        }
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, uipat1TexName);
        GLES30.glUniform2f(textureGenericShader.getTextureOriginLoc(), 0.0f, 0.0f);
        GLES30.glUniform2f(textureGenericShader.getTextureSizeLoc(), 512.0f, 512.0f);
        for (int i = 0; i < 7; i++)
        {
            if (enemies[i] != null)
            {
                drawEnemyHealth(i, enemies[i]);
            }
        }

        GLES30.glUniform1f(textureGenericShader.getAlphaLoc(), 1.0f);
    }

    public void drawEnemy(int slot, Enemy enemy)
    {
        //TODO: Atlas card textures for efficiency
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, enemyNames[slot]);
        GLES30.glUniform3f(textureGenericShader.getOffsetLoc(), enemy.x, 150 + enemy.y, 0.0f);
        GLES30.glUniform1f(textureGenericShader.getAlphaLoc(), enemy.alpha);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0);
    }

    public void drawEnemyHealth(int slot, Enemy enemy)
    {
        enemyHealthBarBackObject.UseObject();
        GLES30.glDisable(GLES30.GL_CULL_FACE);
        GLES30.glUniform2f(textureGenericShader.getScaleLoc(), (float)1, (float)1);
        GLES30.glUniform3f(textureGenericShader.getOffsetLoc(), enemy.x, 150 + enemy.y, 0.0f);
        GLES30.glUniform1f(textureGenericShader.getAlphaLoc(), enemy.alpha);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 24, GLES30.GL_UNSIGNED_INT, 0);

        float scale = enemy.displayHealth.getCurrent() / (float)enemy.spawnHealth;
        int att = enemy.attribute;
        GLES30.glUniform2f(textureGenericShader.getScaleLoc(), (float)scale, (float)1);
        GLES30.glUniform3f(textureGenericShader.getOffsetLoc(), enemy.x + 11, 150 + enemy.y, 0.0f);
        GLES30.glUniform3f(textureGenericShader.getColorLoc(), RendererData.attributeColorsR[att], RendererData.attributeColorsG[att], RendererData.attributeColorsB[att]);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 24 * 4);

        att = enemy.subAttribute;
        if (att != -1)
        {
            scale = Math.min(0.5f, scale);
            GLES30.glUniform2f(textureGenericShader.getScaleLoc(), (float)scale, (float)1);
            GLES30.glUniform3f(textureGenericShader.getColorLoc(), RendererData.attributeColorsR[att], RendererData.attributeColorsG[att], RendererData.attributeColorsB[att]);
            GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 24 * 4);
        }

        GLES30.glUniform3f(textureGenericShader.getColorLoc(), 1.0f, 1.0f, 1.0f);
    }

    public void drawCardDamage(Card card)
    {
        if (!card.showNumber) return;
        String dmg = Integer.toString(card.getDisplayedDamage());
        int len = dmg.length();
        int pixellen = len * 24 + 6;
        int digit;
        float xoffs = 0;

        float xscale;// = 100.0f / pixellen;
        if (pixellen < 100)
        {
            xscale = 1.0f;
            //xoffs = 50 - (pixellen / 2.0f);
        }
        else
        {
            xscale = 100.0f / pixellen;
            pixellen = 100;
        }

        //float boost = 1.0f + ((float)Math.Sin(card.NumTimer / 16.0 * Math.PI) * .5f);
        float boost = 1.0f + (card.getBoost());
        xoffs = 50.0f - (pixellen * .5f * boost);
        float yoffs = (float)Math.sin((card.getBoost()) * Math.PI);
        float yadjust = ((41.0f * boost) - 41.0f) * 0.25f;

        GLES30.glUniform2f(textureGenericShader.getScaleLoc(), 30.0f * boost * xscale, 41.0f * boost);
        GLES30.glUniform2f(textureGenericShader.getTextureSizeLoc(), 30.0f, 41.0f);
        GLES30.glUniform3f(textureGenericShader.getColorLoc(), RendererData.attributeColorsR[card.getAttribute()],
                RendererData.attributeColorsG[card.getAttribute()], RendererData.attributeColorsB[card.getAttribute()]);
        errorCheck("Damage num overall uniforms");

        for (int i = 0; i < len; i++)
        {
            digit = dmg.charAt(i) - '0';
            GLES30.glUniform3f(textureGenericShader.getOffsetLoc(), (card.getPosX() + (24.0f * i * xscale * boost) + xoffs), gameHeight - TeamBaseline - (card.getPosY() - 32.0f) - (yoffs * 60.0f) - yadjust, 0.0f);
            GLES30.glUniform2f(textureGenericShader.getTextureOriginLoc(), (float)RendererData.BigNumXs[digit], (float)RendererData.BigNumYs[digit]);
            errorCheck("Damage num digit uniforms");

            GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0);

        }
    }

    public void drawDamageNumbers()
    {
        List<DamageNumber> numbers = gameState.getNumbers();
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, treasureTexName);
        for (DamageNumber number : numbers)
        {
            drawDamageNumber(number);
        }
        GLES30.glUniform3f(textureGenericShader.getColorLoc(), 1.0f, 1.0f, 1.0f);
    }

    public void drawDamageNumber(DamageNumber number)
    {
        String dmg = Integer.toString(number.interpolator.getCurrent());
        int len = dmg.length();
        int pixellen = len * 16 + 6;
        int digit;
        float xoffs = 0;

        //float boost = 1.0f + ((float)Math.Sin(card.NumTimer / 16.0 * Math.PI) * .5f);
        xoffs = -(pixellen * .5f);

        GLES30.glUniform2f(textureGenericShader.getScaleLoc(), 18.0f, 23.0f);
        GLES30.glUniform1f(textureFixedShader.getAlphaLoc(), number.alpha);
        GLES30.glUniform2f(textureGenericShader.getTextureSizeLoc(), 18.0f, 23.0f);
        GLES30.glUniform3f(textureGenericShader.getColorLoc(), RendererData.attributeColorsR[number.attribute],
                RendererData.attributeColorsG[number.attribute], RendererData.attributeColorsB[number.attribute]);
        errorCheck("Damage num overall uniforms");

        for (int i = 0; i < len; i++)
        {
            digit = dmg.charAt(i) - '0';
            GLES30.glUniform3f(textureGenericShader.getOffsetLoc(), (number.x + (i * 14.0f) + xoffs),  (number.y - 11.0f), 0.0f);
            GLES30.glUniform2f(textureGenericShader.getTextureOriginLoc(), (float)RendererData.MediumNumXs[digit], (float)RendererData.MediumNumYs[digit]);
            errorCheck("Damage num digit uniforms");

            GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0);

        }
    }

    public void drawEffects()
    {
        List<Effect> effects = gameState.getEffects();
        for (Effect effect : effects)
        {
            drawEffect(effect);
        }
    }

    public void drawEffect(Effect effect)
    {
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, particleTextures[effect.texture]);
        GLES30.glUniform2f(textureGenericShader.getScaleLoc(), effect.size, effect.size);
        GLES30.glUniform2f(textureGenericShader.getTextureOriginLoc(), effect.textureX, effect.textureY);
        GLES30.glUniform2f(textureGenericShader.getTextureSizeLoc(), effect.textureSize, effect.textureSize);

        GLES30.glBlendFunc(GLES30.GL_ONE, GLES30.GL_ONE);

        for (Effect.Particle particle : effect.particles)
        {
            drawParticle(particle, effect.fromBottom);
        }
    }

    public void drawParticle(Effect.Particle particle, boolean fromBottom)
    {
        if (fromBottom)
        {
            GLES30.glUniform3f(textureGenericShader.getOffsetLoc(), particle.x, gameHeight - particle.y, 0.0f);
        }
        else
        {
            GLES30.glUniform3f(textureGenericShader.getOffsetLoc(), particle.x, particle.y, 0.0f);
        }
        GLES30.glUniform3f(textureGenericShader.getColorLoc(), particle.r, particle.g, particle.b);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0);
    }

    private void drawHeldOrb(float x, float y, Orb orb)
    {
        orbObjects.UseObject();
        float scale =  1.2f;
        GLES30.glUniform2f(textureFixedShader.getScaleLoc(), scale, scale);
        GLES30.glUniform1f(textureFixedShader.getAlphaLoc(), 1.0f);
        GLES30.glUniform3f(textureFixedShader.getOffsetLoc(), x, y - 30, 0.0f);
        //GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 4 * orb.getAttribute(), 4);
    }

    private void drawBackground()
    {
        genericObject.UseObject();
        GLES30.glUniform1i(textureGenericShader.getTextureLoc(), 0);
        GLES30.glUniform1f(textureGenericShader.getAlphaLoc(), 1.0f);
        GLES30.glUniform3f(textureGenericShader.getColorLoc(), 1.0f, 1.0f, 1.0f);
        GLES30.glUniform2f(textureGenericShader.getScaleLoc(), (float)VirtualResolution, (float)VirtualResolution);
        GLES30.glUniform3f(textureGenericShader.getOffsetLoc(), 0.0f, -150.0f, 0.0f);
        GLES30.glUniform2f(textureGenericShader.getTextureOriginLoc(), 0.0f, 0.0f);
        GLES30.glUniform2f(textureGenericShader.getTextureSizeLoc(), 512.0f, 512.0f);
        errorCheck("Background uniforms");

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0);
    }

    private void drawDebugObject()
    {
        genericObject.UseObject();
        GLES30.glUniform1i(textureGenericShader.getTextureLoc(), 0);
        GLES30.glUniform1f(textureGenericShader.getAlphaLoc(), 1.0f);
        GLES30.glUniform3f(textureGenericShader.getColorLoc(), 1.0f, 1.0f, 1.0f);
        GLES30.glUniform2f(textureGenericShader.getScaleLoc(), (float)VirtualResolution, (float)gameHeight);
        GLES30.glUniform3f(textureGenericShader.getOffsetLoc(), 0.0f, 0.0f, 0.0f);
        GLES30.glUniform2f(textureGenericShader.getTextureOriginLoc(), 0.0f, 0.0f);
        GLES30.glUniform2f(textureGenericShader.getTextureSizeLoc(), 512.0f, 512.0f);
        errorCheck("Debug uniforms");

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0);
    }

    private double time()
    {
        return System.currentTimeMillis() / 1000.0d;
    }
}
