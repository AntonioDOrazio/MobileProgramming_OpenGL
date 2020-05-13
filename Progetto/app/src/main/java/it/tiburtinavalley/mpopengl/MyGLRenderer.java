package it.tiburtinavalley.mpopengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MyGLRenderer implements GLSurfaceView.Renderer {

    private boolean isAutoCamera;

    private final float[] viewProjectionMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    private float[] rotationMatrix = new float[16];

    //private Triangle mTriangle;
    //private Square mSquare;
    private Cube mCube;
    private PointLight pointLight;
    private FloorPlane plane;


    private volatile float mAngle;
    private volatile float mXaxis;

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

    public MyGLRenderer(boolean isAutoCamera)
    {
        this.isAutoCamera = isAutoCamera;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        curXaxis = 1f;
        curYaxis = 1f;

        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        // Use culling to remove back faces.
       GLES20.glEnable(GLES20.GL_CULL_FACE);

        // Enable depth testing
        //   GLES20.glEnable(GLES20.GL_DEPTH_TEST);


        // mTriangle = new Triangle();
        //mSquare = new Square();
        mCube = new Cube();
        pointLight = new PointLight();
        plane = new FloorPlane();



    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        final float left = -ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;   // a cosa servee??
        final float far = 10.0f;   // a cosa servee??

     //   Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, 40.f);
       // Matrix.frustumM(projectionMatrix, 0,left, right, -1.5f, 1.5f, 1, 40);
        Matrix.frustumM(projectionMatrix, 0, left, ratio, bottom, top, 1.5f, 30.f);

        Matrix.setLookAtM(viewMatrix, 0,
                //eyeX, eyeY, eyeZ,
                0, 4, -12,
                //lookX, lookY, lookZ,
                0, 0, 0,
                //upX, upY, upZ
                0, 1, 0);


        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        float[] scratch = new float[16];

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Per la rotazione. Creo una matrice di rotazione, la moltiplico per vPMatrix e passo in draw il risultato complessivo
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
/*
        Matrix.setLookAtM(viewMatrix, 0,
                4, 0, 2,
                0f, 0f, 0f,
                0f, 1.0f, 0.0f
        );
*/
        //Set view matrix from eye position
        rotateViewport();


        // Impostazioni e rendering del triangolo
        Matrix.setRotateM(rotationMatrix, 0, angle, 0, 0, -1.0f);
        Matrix.multiplyMM(scratch, 0, viewProjectionMatrix, 0, rotationMatrix, 0);
        //mTriangle.draw(scratch);


        // Impostazioni e rendering del cubo
        // Calculate position of the light. Rotate and then push into the distance.
        float[] lightModelMatrix = new float[16]; // Verra usata per calcolare le posizioni della luce
        Matrix.setIdentityM(lightModelMatrix, 0);
        Matrix.translateM(lightModelMatrix, 0, 0.0f, 0.0f, -4.0f);
        Matrix.rotateM(lightModelMatrix, 0, angle, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(lightModelMatrix, 0, 0.0f, 0.0f, 4.0f);



        float[] tempPos = new float[4];
        Matrix.multiplyMV(tempPos, 0, lightModelMatrix, 0, pointLight.getPositionInModelSpace(), 0);
        pointLight.setPositionInWorldSpace(tempPos);

        Matrix.multiplyMV(tempPos, 0, viewMatrix, 0, pointLight.getPositionInWorldSpace(), 0);
        pointLight.setPositionInEyeSpace(tempPos);





        // Draw a plane
        float[] planeModelMatrix = new float[16];
        Matrix.setIdentityM(planeModelMatrix, 0);
        Matrix.translateM(planeModelMatrix, 0, 0.0f, -24.0f, 0f);
        //Matrix.rotateM(planeModelMatrix, 0, angle, 0f, 1.0f, 0.0f);
        plane.draw(planeModelMatrix, viewMatrix, projectionMatrix, pointLight.getPositionInEyeSpace());


        // Draw a point representing the light
        pointLight.draw(lightModelMatrix, viewMatrix, projectionMatrix);


        // Draw a cube
        float[] cubeModelMatrix = new float[16];
        Matrix.setIdentityM(cubeModelMatrix, 0);

        Matrix.translateM(cubeModelMatrix, 0, .0f, .0f, 0f);
        Matrix.rotateM(cubeModelMatrix, 0, angle, 0.0f, -1.0f, 0.0f);
        mCube.draw(cubeModelMatrix, viewMatrix, projectionMatrix, pointLight.getPositionInEyeSpace());

        // Draw a cube
        cubeModelMatrix = new float[16];
        Matrix.setIdentityM(cubeModelMatrix, 0);

        Matrix.translateM(cubeModelMatrix, 0, -4.4f,  (float) Math.sin(0.07*angle), -3.5f);
        Matrix.rotateM(cubeModelMatrix, 0, angle, .7f, 0, .7f);
        mCube.draw(cubeModelMatrix, viewMatrix, projectionMatrix, pointLight.getPositionInEyeSpace());


        // Draw a cube
        cubeModelMatrix = new float[16];
        Matrix.setIdentityM(cubeModelMatrix, 0);

        Matrix.translateM(cubeModelMatrix, 0, 5f,  (float) Math.sin(0.05*angle), 4f);
        Matrix.rotateM(cubeModelMatrix, 0, angle, 0.0f, .5f, 0.0f);
        mCube.draw(cubeModelMatrix, viewMatrix, projectionMatrix, pointLight.getPositionInEyeSpace());

    }

    private float oldTimeSinceStart = 0;

    public void rotateViewport()
    {
        float speed = 8f;

        // Calculate deltaTime
        float timeSinceStart = SystemClock.uptimeMillis();
        float deltaTime = timeSinceStart - oldTimeSinceStart;
        oldTimeSinceStart = timeSinceStart;

        float deltaX =  speed * deltaTime;
        float deltaY =  speed * deltaTime;

        if (!isAutoCamera){
            deltaX *= mXaxis;
            deltaY *= mYaxis;
        }

        // Clamp between -2 and 2
        deltaX = Math.max(-2f, Math.min(deltaX, 2f));
        deltaY = Math.max(-2f , Math.min(deltaY, 2f));

        curXaxis = curXaxis + deltaX;
        curYaxis = curYaxis + deltaY;

        Matrix.rotateM(viewMatrix, 0, deltaX, 0, 1, 0);
     //   Matrix.rotateM(viewMatrix, 0, deltaY, (float) Math.sqrt(2), 0, 0);
       // Matrix.rotateM(viewMatrix, 0, deltaY, 0, 0, (float) Math.sqrt(2));

    }

}
