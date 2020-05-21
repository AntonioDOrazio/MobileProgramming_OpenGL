package it.tiburtinavalley.mpopengl;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {

    private static final int COORDS_PER_VERTEX = 3;
    private static final int BYTES_PER_FLOAT = 4;

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 uMVPMatrix;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private final int mProgram;

    private FloatBuffer vertexBuffer;

    private static float triangleCoords[] = { //in senso antiorario:
            0.0f,  0.622008459f, 0.0f, // sopra
            -0.5f, -0.311004243f, 0.0f, // sotto sinistra
            0.5f, -0.311004243f, 0.0f  // sotto destra
    };

    private float[] color = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    private int positionHandle;
    private int colorHandle;

    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * BYTES_PER_FLOAT;  // 4 bytes per vertex

    // Usata per accedere e impostare la view transformation
    private int vPMatrixHandle;


    public Triangle() {

        // inizializza un buffer byte per le coordinate dei vertici
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * BYTES_PER_FLOAT);
        // usa l'ordine dei byte nativo dell'hardware del dispositivo
        bb.order(ByteOrder.nativeOrder());
        // crea un buffer di floating point dal ByteBuffer
        vertexBuffer = bb. asFloatBuffer();
        // aggiunge le coordinate al buffer di floating point
        vertexBuffer.put(triangleCoords);
        //imposta il buffer per leggere la prima coordinata
        vertexBuffer.position(0);

        int vertexShader = Utility.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);

        int fragmentShader = Utility.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // Crea un programma OpenGL ES
        mProgram = GLES20.glCreateProgram();
        // Aggiunge il vertex shader al programma
        GLES20.glAttachShader(mProgram, vertexShader);
        // Aggiunge il fragment shader al programma
        GLES20.glAttachShader(mProgram, fragmentShader);
        // Crea gli eseguibili del programma OpenGL ES
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float [] mvpMatrix) {

        // Aggiunge il programma all'ambiente OpenGL ES
        GLES20.glUseProgram(mProgram);

        // Ottiene e abilita un handle su una variabile nello shader
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Preparare i dati delle coordinate del triangolo
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // Ottiene un handle alla variabile del colore e imposta un valore
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // Ottiene un handle per modellare la  transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Passa la projection and view transformation allo shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        // Disegna il triangolo
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        //Disabilita l'array di vertici
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
