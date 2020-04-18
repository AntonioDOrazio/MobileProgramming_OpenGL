package it.tiburtinavalley.mpopengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MyGLRenderer implements GLSurfaceView.Renderer {

    private final float[] viewProjectionMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    private float[] rotationMatrix = new float[16];

    private Triangle mTriangle;
    private Square mSquare;
    private Cube mCube;
    private PointLight pointLight;

    public volatile float mAngle;
    public volatile float mXaxis;

    private float curXaxis;
    private float curYaxis;

    public float getmXaxis() {
        return mXaxis;
    }

    public void setmXaxis(float mXaxis) {
        this.mXaxis = mXaxis;
    }

    public float getmYaxis() {
        return mYaxis;
    }

    public void setmYaxis(float mYaxis) {
        this.mYaxis = mYaxis;
    }

    public volatile float mYaxis;


    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        curXaxis = 1f;
        curYaxis = 1f;

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Use culling to remove back faces.
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        // Enable depth testing
        //   GLES20.glEnable(GLES20.GL_DEPTH_TEST);


        // mTriangle = new Triangle();
        //mSquare = new Square();
        mCube = new Cube();
        pointLight = new PointLight();


        // Position the eye in front of the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = -0.5f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;


        // Inizializza la View Matrix
        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        float[] scratch = new float[16];

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

    /*    Matrix.setLookAtM(viewMatrix, 0,
                0, 0, -3,
                0f, 0f, 0f,
                0f, 1.0f, 0.0f
        );
*/

        rotateViewport();


        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // Per la rotazione. Creo una matrice di rotazione, la moltiplico per vPMatrix e passo in draw il risultato complessivo
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);

        // Impostazioni e rendering del triangolo
        Matrix.setRotateM(rotationMatrix, 0, angle, 0, 0, -1.0f);
        Matrix.multiplyMM(scratch, 0, viewProjectionMatrix, 0, rotationMatrix, 0);
        //mTriangle.draw(scratch);


        // Impostazioni e rendering del cubo
        // Calculate position of the light. Rotate and then push into the distance.
        float[] lightModelMatrix = new float[16]; // Verra usata per calcolare le posizioni della luce
        Matrix.setIdentityM(lightModelMatrix, 0);
        Matrix.translateM(lightModelMatrix, 0, 0.0f, 0.0f, -5.0f);
        Matrix.rotateM(lightModelMatrix, 0, angle, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(lightModelMatrix, 0, 0.0f, 0.0f, 2.0f);

        float[] tempPos = new float[4];
        Matrix.multiplyMV(tempPos, 0, lightModelMatrix, 0, pointLight.getPositionInModelSpace(), 0);
        pointLight.setPositionInWorldSpace(tempPos);

        Matrix.multiplyMV(tempPos, 0, viewMatrix, 0, pointLight.getPositionInWorldSpace(), 0);
        pointLight.setPositionInEyeSpace(tempPos);

        // Draw a cube
        float[] cubeModelMatrix = new float[16];
        Matrix.setIdentityM(cubeModelMatrix, 0);
        Matrix.translateM(cubeModelMatrix, 0, 0.0f, 0.0f, -5.0f);
        Matrix.rotateM(cubeModelMatrix, 0, angle, 1.0f, 0.0f, 0.0f);
        mCube.draw(cubeModelMatrix, viewMatrix, projectionMatrix, pointLight.getPositionInEyeSpace());
    }

    float oldTimeSinceStart = 0;

    public void rotateViewport()
    {
        float speed = 0.05f;

        // Calculate deltaTime
        float timeSinceStart = SystemClock.uptimeMillis();
        float deltaTime = timeSinceStart - oldTimeSinceStart;
        oldTimeSinceStart = timeSinceStart;

        float deltaX =  mXaxis * speed * deltaTime;
        float deltaY = mYaxis * speed * deltaTime;

        // Clamp between -2 and 2
        deltaX = Math.max(-2f, Math.min(deltaX, 2f));
        deltaY = Math.max(-2f , Math.min(deltaY, 2f));

        curXaxis = curXaxis + deltaX;
        curYaxis = curYaxis + deltaY;

        System.out.println("X e Y: " + curXaxis+ " " +curYaxis);

        // Position the eye in front of the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 5.5f;

        // We are looking toward the distance
        final float lookX = -curXaxis+1;
        final float lookY = -curYaxis+1;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;


        // Inizializza la View Matrix
        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        mXaxis = 0;
        mYaxis = 0;

    }
}
