package com.insanitybringer.padsim.renderer.particle;

import com.insanitybringer.padsim.renderer.Effect;
import com.insanitybringer.padsim.renderer.RendererData;
import com.insanitybringer.padsim.renderer.UpdateRenderer;

public class PlayerAttack extends Effect
{
    public PlayerAttack(float srcx, float srcy, float dstx, float dsty, int attribute)
    {
        size = textureSize = 64.0f;
        textureX = 192.0f; textureY = 128.0f;
        particles = new Particle[1];
        Particle particle = new Particle();
        particle.x = particle.srcx = srcx;
        particle.y = particle.srcy = srcy;
        particle.dstx = dstx;
        particle.dsty = dsty;
        particle.r = RendererData.attributeColorsR[attribute];
        particle.g = RendererData.attributeColorsG[attribute];
        particle.b = RendererData.attributeColorsB[attribute];
        particles[0] = particle;
    }

    @Override
    public boolean isLive()
    {
        return ticks < 7;
    }

    @Override
    public void tick()
    {
        super.tick();
        float progress = ticks / 8.0f;
        float ly;
        if (progress < 1.0001)
        {
            for (Particle particle : particles)
            {
                ly = (progress * 2) - 1.0f;
                particle.x = lerp(particle.srcx, particle.dstx, progress);
                particle.y = (((ly * ly) - 1.0f) * 105.0f) + lerp(particle.srcy, particle.dsty, progress);
            }
        }
        /*else
        {
            particles[0].x = particles[0].dstx;
            particles[0].y = particles[0].dsty;
        }*/
    }
}
