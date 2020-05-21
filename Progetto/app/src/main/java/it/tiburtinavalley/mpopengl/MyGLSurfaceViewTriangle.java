package it.tiburtinavalley.mpopengl;

import android.opengl.GLSurfaceView;

import android.content.Context;
import android.view.MotionEvent;



class MyGLSurfaceViewTriangle extends GLSurfaceView {

    private final MyGLRendererTriangle renderer;

    public MyGLSurfaceViewTriangle(Context context){
        super(context);

        // Crea un contesto OpenGL ES 2.0
        setEGLContextClientVersion(2);

        renderer = new MyGLRendererTriangle();

        // Imposta il Renderer per disegnare sulla GLSurfaceView
        setRenderer(renderer);

        // Renderizza la vista solo quando c'è una modifica nei dati del disegno
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent riporta i dettagli di input dal touchscreen e altri controlli di input.
        // In questo caso, sei interessato solo agli eventi in cui la posizione del tocco è cambiata.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - previousX;
                float dy = y - previousY;

                // senso inverso di rotazione sopra la linea mediana
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // senso inverso di rotazione a sinistra della linea mediana
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }

                renderer.setAngle(
                        renderer.getAngle() +
                                ((dx + dy) * TOUCH_SCALE_FACTOR));
                requestRender();
        }

        previousX = x;
        previousY = y;
        return true;
    }
}
