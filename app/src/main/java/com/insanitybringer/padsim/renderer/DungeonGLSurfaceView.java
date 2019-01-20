package com.insanitybringer.padsim.renderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.insanitybringer.padsim.game.GameState;

public class DungeonGLSurfaceView extends GLSurfaceView
{
    static UpdateRenderer renderer;
    public DungeonGLSurfaceView(Context context, GameState gameState)
    {
        super(context);
        setEGLContextClientVersion(3);
        if (renderer == null)
            renderer = new UpdateRenderer(gameState);

        this.setRenderer(renderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        float x = e.getX();
        float y = e.getY();

        if (e.getAction() == MotionEvent.ACTION_MOVE || e.getAction() == MotionEvent.ACTION_DOWN)
        {
            //System.out.printf("%f %f\n", x, y);
            renderer.touch(x, y);
        }
        else if (e.getAction() == MotionEvent.ACTION_UP)
        {
            renderer.touchUp();
        }

        return true;
    }
}
