package com.naveen.stressbuster;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private Thread thread;
    private boolean running = false;
    private SurfaceHolder holder;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<Bubble> bubbles = new ArrayList<>();
    private Random rand = new Random();
    private long lastSpawn = 0;
    private Vibrator vibrator;
    private boolean sessionActive = false;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void resetSession() {
        bubbles.clear();
        sessionActive = true;
    }

    public void endSession() {
        sessionActive = false;
    }

    public void pause() { running = false; }
    public void resume() { if (thread==null || !thread.isAlive()) startThread(); }

    private void startThread() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (running) {
            if (!holder.getSurface().isValid()) continue;
            long now = System.currentTimeMillis();
            if (sessionActive && now - lastSpawn > 800) {
                spawnBubble();
                lastSpawn = now;
            }

            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                drawFrame(canvas);
                holder.unlockCanvasAndPost(canvas);
            }

            updateBubbles();

            try { Thread.sleep(16); } catch (InterruptedException e) { }
        }
    }

    private void spawnBubble() {
        int w = getWidth();
        int h = getHeight();
        float x = rand.nextInt(Math.max(1,w));
        float y = h + 50;
        float radius = 40 + rand.nextInt(60);
        float vy = - (2 + rand.nextFloat()*4);
        bubbles.add(new Bubble(x,y,radius,vy));
    }

    private void updateBubbles() {
        Iterator<Bubble> it = bubbles.iterator();
        while (it.hasNext()) {
            Bubble b = it.next();
            b.update();
            if (b.y + b.radius < -100) it.remove();
            b.updateParticles();
        }
    }

    private void drawFrame(Canvas canvas) {
        canvas.drawColor(0xFFB3E5FC);
        for (Bubble b : bubbles) {
            paint.setAlpha(200);
            paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(8,0,0,0x55000000);
            canvas.drawOval(new RectF(b.x-b.radius, b.y-b.radius, b.x+b.radius, b.y+b.radius), paint);
            for (Bubble.Particle p : b.particles) {
                paint.setAlpha((int)(255 * p.life));
                canvas.drawCircle(p.x, p.y, p.r, paint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN) {
            float tx = event.getX();
            float ty = event.getY();
            for (Bubble b : bubbles) {
                float dx = tx - b.x;
                float dy = ty - b.y;
                if (dx*dx + dy*dy <= b.radius*b.radius) {
                    b.pop();
                    if (vibrator!=null) vibrator.vibrate(30);
                }
            }
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) { startThread(); }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { running = false; }
}
