package it.tiburtinavalley.mpopengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRendererTriangle implements GLSurfaceView.Renderer {

    private Triangle mTriangle;

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Imposta il colore della cornice dello sfondo
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Inizializza un triangolo
        mTriangle = new Triangle();
    }

    // vPMatrix è l'abbreviazione per "Model View Projection Matrix"
    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    private float[] rotationMatrix = new float[16];
    @Override
    public void onDrawFrame(GL10 gl) {

        float[] scratch = new float[16];

        // Ridisegna il colore di sfondo
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Imposta la posizione della camera (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calcola la projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        /*
         Crea una rotation transformation per il triangolo
         long time = SystemClock.uptimeMillis() % 4000L;
         float angle = 0.090f * ((int) time);
        */
        Matrix.setRotateM(rotationMatrix, 0, -mAngle, 0, 0, -1.0f);

        // Combina la rotation matrix con la projection and camera view
        // Si noti che il fattore vPMatrix deve essere il primo nell'ordine
        // Affinchè il prodotto della moltiplicazione tra matrici sia corretto.
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);

        // Disegna il triangolo
        mTriangle.draw(scratch);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {

        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // Questa projection matrix viene applicata alle coordinate dell'oggetto nel metodo onDrawFrame ()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public volatile float mAngle;

    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }
}