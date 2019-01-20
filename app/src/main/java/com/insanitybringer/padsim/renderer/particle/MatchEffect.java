package com.insanitybringer.padsim.renderer.particle;

import com.insanitybringer.padsim.game.Match;
import com.insanitybringer.padsim.renderer.Effect;
import com.insanitybringer.padsim.renderer.RendererData;
import com.insanitybringer.padsim.renderer.UpdateRenderer;

public class MatchEffect extends Effect
{
    public MatchEffect(Match match, int[] positions)
    {
        size = textureSize = 64.0f;
        fromBottom = true;
        textureX = 192.0f; textureY = 128.0f;
        particles = new Particle[positions.length];
        for (int i = 0; i < positions.length; i++)
        {
            Particle particle = new Particle();
            particle.srcx = match.centerX;
            particle.srcy = match.centerY;
            particle.dstx = 5 + (positions[i] * 106) + 50.0f;
            particle.dsty = UpdateRenderer.TeamBaseline - 50.0f;
            //particle.r = RendererData.attributeColorsR[match.attribute];
            //particle.g = RendererData.attributeColorsG[match.attribute];
            //particle.b = RendererData.attributeColorsB[match.attribute];
            particles[i] = particle;
        }
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
        for (Particle particle : particles)
        {
            ly = (progress * 2) - 1.0f;
            particle.x = lerp(particle.srcx, particle.dstx, progress);
            particle.y = ((1.0f - (ly * ly)) * 105.0f) + lerp(particle.srcy, particle.dsty, progress);
        }
    }
}
