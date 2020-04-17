package it.tiburtinavalley.mpopengl;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

    MyGLRenderer renderer;

    public MyGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);

        renderer = new MyGLRenderer();

        setRenderer(renderer);

        // Render the view only when there is a change in the drawing data.
        // To allow the triangle to rotate automatically, this line is commented out:
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
