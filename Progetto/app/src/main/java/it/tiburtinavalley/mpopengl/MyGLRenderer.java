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


    private Cube mCube;
    private PointLight pointLight;
    private FloorPlane plane;

    private volatile float mXaxis;
    public void setmXaxis(float mXaxis) {
        this.mXaxis = mXaxis;
    }


    public MyGLRenderer(boolean isAutoCamera)
    {
        this.isAutoCamera = isAutoCamera;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        // Ripulisci la viewport con un unico sfondo grigio
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        // Per non renderizzare le facce negative
       GLES20.glEnable(GLES20.GL_CULL_FACE);

        // Inizializza gli elementi da renderizzare
        mCube = new Cube();
        pointLight = new PointLight();
        plane = new FloorPlane();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Crea una viewport di OpenGL che occupi tutto lo schermo
        GLES20.glViewport(0, 0, width, height);

        // Questi quattro elementi rappresentano le proporzioni dello schermo per effettuare una corretta proiezione
        float ratio = (float) width / height;
        final float left = -ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        // Stabiliscono le distanza minime e massime entro il quale gli elementi sono renderizzati
        // (Clipping planes)
        final float near = 1.5f;
        final float far = 30.0f;

        // Crea la matrice di proiezione
        Matrix.frustumM(projectionMatrix, 0, left, ratio, bottom, top, near, far);

        // Imposta un punto da guardare per la matrice View
        Matrix.setLookAtM(viewMatrix, 0,
                //eyeX, eyeY, eyeZ,
                0, 4, -12,
                //lookX, lookY, lookZ,
                0, 0, 0,
                //upX, upY, upZ
                0, 1, 0);


        // Moltiplica le due matrici per creare matrice View Projection, che include posizione in cui guardare e modo in cui
        // L'immagine è proiettata sullo schermo
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Matrice temporanea per effettuare operazioni varie
        float[] scratch = new float[16];

        // Ripulisce i buffer OpenGL prima di disegnare ulteriori elementi
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        // Attiva il controllo di profondità. Consente di rilevare automaticamente se un oggetto è dietro ad un altro
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Utilizzato per animazioni basate sul tempo
        long time = SystemClock.uptimeMillis() % 4000L;
        // Calcola un angolo per le rotazioni che varia nel tempo
        float angle = 0.090f * ((int) time);

        // Funzioni che ruotano la telecamera
        rotateViewport();


        // Impostazioni e rendering del cubo
        // Imposta posizione e rotazione della luce nel tempo
        float[] lightModelMatrix = new float[16]; // Verra usata per calcolare le posizioni della luce
        Matrix.setIdentityM(lightModelMatrix, 0);
        Matrix.translateM(lightModelMatrix, 0, 0.0f, 0.0f, -4.0f);
        Matrix.rotateM(lightModelMatrix, 0, angle, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(lightModelMatrix, 0, 0.0f, 0.0f, 4.0f);

        // Imposta gli attributi calcolati della luce nella classe PointLight
        float[] tempPos = new float[4];
        Matrix.multiplyMV(tempPos, 0, lightModelMatrix, 0, pointLight.getPositionInModelSpace(), 0);
        pointLight.setPositionInWorldSpace(tempPos);

        Matrix.multiplyMV(tempPos, 0, viewMatrix, 0, pointLight.getPositionInWorldSpace(), 0);
        pointLight.setPositionInEyeSpace(tempPos);


        // Disegna il plane e lo posiziona in basso
        float[] planeModelMatrix = new float[16];
        Matrix.setIdentityM(planeModelMatrix, 0);
        Matrix.translateM(planeModelMatrix, 0, 0.0f, -24.0f, 0f);
        plane.draw(planeModelMatrix, viewMatrix, projectionMatrix, pointLight.getPositionInEyeSpace());


        // Disegna un punto per simulare un'origine della luce
        pointLight.draw(lightModelMatrix, viewMatrix, projectionMatrix);


        // Primo cubo
        float[] cubeModelMatrix = new float[16];
        Matrix.setIdentityM(cubeModelMatrix, 0);
        Matrix.translateM(cubeModelMatrix, 0, .0f, .0f, 0f);
        Matrix.rotateM(cubeModelMatrix, 0, angle, 0.0f, -1.0f, 0.0f);
        mCube.draw(cubeModelMatrix, viewMatrix, projectionMatrix, pointLight.getPositionInEyeSpace());

        // Secondo cubo
        cubeModelMatrix = new float[16];
        Matrix.setIdentityM(cubeModelMatrix, 0);
        Matrix.translateM(cubeModelMatrix, 0, -4.4f,  (float) Math.sin(0.07*angle), -3.5f);
        Matrix.rotateM(cubeModelMatrix, 0, angle, .7f, 0, .7f);
        mCube.draw(cubeModelMatrix, viewMatrix, projectionMatrix, pointLight.getPositionInEyeSpace());


        // Terzo cubo
        cubeModelMatrix = new float[16];
        Matrix.setIdentityM(cubeModelMatrix, 0);
        Matrix.translateM(cubeModelMatrix, 0, 5f,  (float) Math.sin(0.05*angle), 4f);
        Matrix.rotateM(cubeModelMatrix, 0, angle, 0.0f, .5f, 0.0f);
        mCube.draw(cubeModelMatrix, viewMatrix, projectionMatrix, pointLight.getPositionInEyeSpace());

    }

    private float oldTimeSinceStart = 0;

    // Questa funzione effettua la rotazione della telecamera
    public void rotateViewport()
    {
        float speed = 8f;

        // Calcola la differenza di tempo  tra un frame e l'altro
        // Questo servirà per rendere il moto della telecamera dipendente
        // Dal tempo e non dal framerate, assicurando la stessa velocità per ogni dispositivo
        // Anche se meno performante
        float timeSinceStart = SystemClock.uptimeMillis();
        float deltaTime = timeSinceStart - oldTimeSinceStart;
        oldTimeSinceStart = timeSinceStart;

        float deltaX =  speed * deltaTime;

        // Se è stata impostata la videocamera in modo manuale, si moltiplica lo spostamento per l'input desiderato
        if (!isAutoCamera){
            deltaX *= mXaxis;
        }

        // Costringe lo spostamento massimo tra -2 e 2
        deltaX = Math.max(-2f, Math.min(deltaX, 2f));

        // Effettua la rotazione della telecamera di deltaX angolo lungo l'asse Y
        // Nota: l'asse è X perché l'input dallo schermo è preso in orizzontale, ma per avere
        // Una rotazione orizzontale bisogna ruotare lungo l'asse Y verticale.
        Matrix.rotateM(viewMatrix, 0, deltaX, 0, 1, 0);

    }

}
