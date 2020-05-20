package it.tiburtinavalley.mpopengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

    MyGLRenderer renderer;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;

    public MyGLSurfaceView(Context context, boolean isAutoCamera) {
        super(context);

        setEGLContextClientVersion(2);

        renderer = new MyGLRenderer(isAutoCamera);

        setRenderer(renderer);

        // Render the view only when there is a change in the drawing data.
        // To allow the triangle to rotate automatically, this line is commented out:
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();

        // Per ruotare la viewport con il tocco ai lati

        // Controllo sulla quantità di spazio in cui è possibile eseguire l'input
        if(Math.abs(e.getX() - getRootView().getWidth()) < 400 || Math.abs(e.getX()) < 400){
            float xNorm = 2f * (e.getX() / getRootView().getWidth()) - 1f;
            renderer.setmXaxis(xNorm);
        }

        // Azzera l'input nel momento in cui si alza il dito dallo schermo
        if (e.getAction() == MotionEvent.ACTION_UP)
        {
            renderer.setmXaxis(0);
        }

        return true;
    }

}
