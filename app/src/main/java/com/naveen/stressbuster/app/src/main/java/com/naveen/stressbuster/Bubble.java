package com.naveen.stressbuster;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Bubble {
    public float x, y, radius, vy;
    public List<Particle> particles = new ArrayList<>();
    private boolean popped = false;

    public Bubble(float x, float y, float r, float vy) {
        this.x = x; this.y = y; this.radius = r; this.vy = vy;
    }

    public void update() {
        if (!popped) y += vy;
    }

    public void pop() {
        if (popped) return;
        popped = true;
        for (int i=0;i<10;i++) {
            particles.add(new Particle(x, y, (float)(Math.random()*6+2)));
        }
        radius = 0;
    }

    public void updateParticles() {
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle p = it.next();
            p.update();
            if (p.life <= 0) it.remove();
        }
    }

    public static class Particle {
        public float x,y,r;
        public float vx, vy;
        public float life = 1.0f;
        public Particle(float x, float y, float r) {
            this.x = x; this.y = y; this.r = r;
            this.vx = (float)((Math.random()-0.5)*6);
            this.vy = (float)((Math.random()-0.5)*6);
        }
        public void update() {
            x += vx; y += vy; life -= 0.03f;
        }
    }
}
