package it.tiburtinavalley.mpopengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

    MyGLRenderer renderer;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;

    public MyGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);

        renderer = new MyGLRenderer();

        setRenderer(renderer);

        // Render the view only when there is a change in the drawing data.
        // To allow the triangle to rotate automatically, this line is commented out:
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {

            case MotionEvent.ACTION_MOVE:
                float dx = x - previousX;
                float dy = y - previousY;

                if (y > getHeight() / 2) {
                    dx = dx * -1;
                }

                if (y < getWidth() / 2) {
                    dy = dy * -1;
                }

                renderer.setAngle(
                        renderer.getAngle() +
                                ((dx + dy) * TOUCH_SCALE_FACTOR));
                requestRender();
        }

        previousX = x;
        previousY = y;

        // Per ruotare la viewport
        float xNorm = 2f * (e.getX() / getRootView().getWidth()) - 1f;
        float yNorm = -2f * (e.getY() / getRootView().getHeight()) + 1f;

        renderer.setmXaxis(xNorm);
        renderer.setmYaxis(yNorm);
        System.out.println(xNorm+ "DDD");
        System.out.println(yNorm);

        return true;
    }
}
