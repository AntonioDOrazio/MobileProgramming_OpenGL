package it.tiburtinavalley.mpopengl;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Cube {

    // Descrivono il numero di componenti per ogni singola unità di quel tipo
    private static final int COORDINATES_DATA_SIZE = 3;
    private static final int COLOR_DATA_SIZE = 4;
    private static final int NORMAL_COORDINATES_DATA_SIZE = 3;

    private static final int BYTES_PER_FLOAT = 4;

    // Per memorizzare il codice degli shader
    private String vertexShaderCode;
    private String fragmentShaderCode;

    private final int mProgram;

    // Buffer in cui memorizzare le informazioni su vertex, normal e color
    private FloatBuffer vertexBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer colorBuffer;


    // X, Y, Z
    private final float[] cubeCoords =
            {
                    // La faccia "positiva" è quella che viene costruita leggendo per ogni triangolo i vertici in senso antiorario
                    // Per ottimizzare le prestazioni la faccia "negativa" in OpenGL non è renderizzata (backface culling)

                    // Front face
                    -1.0f, 1.0f, 1.0f,
                    -1.0f, -1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    -1.0f, -1.0f, 1.0f,
                    1.0f, -1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,

                    // Right face
                    1.0f, 1.0f, 1.0f,
                    1.0f, -1.0f, 1.0f,
                    1.0f, 1.0f, -1.0f,
                    1.0f, -1.0f, 1.0f,
                    1.0f, -1.0f, -1.0f,
                    1.0f, 1.0f, -1.0f,

                    // Back face
                    1.0f, 1.0f, -1.0f,
                    1.0f, -1.0f, -1.0f,
                    -1.0f, 1.0f, -1.0f,
                    1.0f, -1.0f, -1.0f,
                    -1.0f, -1.0f, -1.0f,
                    -1.0f, 1.0f, -1.0f,

                    // Left face
                    -1.0f, 1.0f, -1.0f,
                    -1.0f, -1.0f, -1.0f,
                    -1.0f, 1.0f, 1.0f,
                    -1.0f, -1.0f, -1.0f,
                    -1.0f, -1.0f, 1.0f,
                    -1.0f, 1.0f, 1.0f,

                    // Top face
                    -1.0f, 1.0f, -1.0f,
                    -1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, -1.0f,
                    -1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, -1.0f,

                    // Bottom face
                    1.0f, -1.0f, -1.0f,
                    1.0f, -1.0f, 1.0f,
                    -1.0f, -1.0f, -1.0f,
                    1.0f, -1.0f, 1.0f,
                    -1.0f, -1.0f, 1.0f,
                    -1.0f, -1.0f, -1.0f,
            };

    // R, G, B, A
    private final float[] color =
            {
                    // Front face (red)
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f, 1.0f,

                    // Right face (green)
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 0.0f, 1.0f,

                    // Back face (blue)
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,

                    // Left face (yellow)
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 0.0f, 1.0f,

                    // Top face (cyan)
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,

                    // Bottom face (magenta)
                    1.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f, 1.0f
            };

    // X, Y, Z
    // Queste sono le coordinate dei vettori ortogonali alla superficie delle facce del cubo
    // I vettori Normal sono utilizzati nel calcolo della luce
    private final float[] cubeNormalCoords =
            {
                    // Front face
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,

                    // Right face
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,

                    // Back face
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,
                    0.0f, 0.0f, -1.0f,

                    // Left face
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,
                    -1.0f, 0.0f, 0.0f,

                    // Top face
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,

                    // Bottom face
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f,
                    0.0f, -1.0f, 0.0f
            };

    // Gli handle sono le maniglie che consentono di interagire con le variabili dei programmi shader
    private int positionHandle;
    private int colorHandle;
    private int normalHandle;
    private int modelViewMatrixHandle;
    private int modelViewProjectionMatrixHandle;

    private final int vertexCount = cubeCoords.length / COORDINATES_DATA_SIZE;


    public Cube() {
        // Legge da file il codice degli shader
        vertexShaderCode = Utility.getShaderCodeFromFile(R.raw.vertex_cube);
        fragmentShaderCode = Utility.getShaderCodeFromFile(R.raw.fragment_cube);

        // Inizializza i buffer dei dati e inserisce informazioni su coordinate, colore e vettori normali
        vertexBuffer = ByteBuffer.allocateDirect(cubeCoords.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(cubeCoords).position(0);

        colorBuffer = ByteBuffer.allocateDirect(color.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer.put(color).position(0);

        normalBuffer = ByteBuffer.allocateDirect(cubeNormalCoords.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalBuffer.put(cubeNormalCoords).position(0);

        // Compila gli shader in OpenGL ES
        int vertexShader = Utility.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = Utility.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // Crea un programma OpenGL e ci collega i due shader
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] modelMatrix, float[] viewMatrix, float[] projectionMatrix, float[] lightPositionInEyeSpace) {
        // Attiva il programma OpenGL composto dagli shader compilati
        GLES20.glUseProgram(mProgram);

        // Dà allo shader informazioni sulla posizione dei vertici
        positionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(positionHandle, COORDINATES_DATA_SIZE, GLES20.GL_FLOAT, false,
                COORDINATES_DATA_SIZE * BYTES_PER_FLOAT, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Dà allo shader informazioni sul colore
        colorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");
        colorBuffer.position(0);
        GLES20.glVertexAttribPointer(colorHandle, COLOR_DATA_SIZE, GLES20.GL_FLOAT, false,
                COLOR_DATA_SIZE * BYTES_PER_FLOAT, colorBuffer);
        GLES20.glEnableVertexAttribArray(colorHandle);

        // Dà allo shader informazioni sui versori normali ai vertici del cubo
        normalHandle = GLES20.glGetAttribLocation(mProgram, "a_Normal");
        normalBuffer.position(0);
        GLES20.glVertexAttribPointer(normalHandle, NORMAL_COORDINATES_DATA_SIZE, GLES20.GL_FLOAT, false,
                NORMAL_COORDINATES_DATA_SIZE * BYTES_PER_FLOAT, normalBuffer);
        GLES20.glEnableVertexAttribArray(normalHandle);

        // Inizializza gli handle delle matrici e una matrice temporanea dove salvare i prodotti fra matrice
        modelViewMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVMatrix");
        modelViewProjectionMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");

        float[] tempMatrix = new float[16];

        // Calcola e passa allo shader matrice Model View
        Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        GLES20.glUniformMatrix4fv(modelViewMatrixHandle, 1, false, tempMatrix, 0);

        // Calcola e passa allo shader matrice Model View Projection
        Matrix.multiplyMM(tempMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
        GLES20.glUniformMatrix4fv(modelViewProjectionMatrixHandle, 1, false, tempMatrix, 0);

        // Passa allo shader informazioni sulla posizione della luce in eye space
        int lightPositionHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
        GLES20.glUniform3f(lightPositionHandle, lightPositionInEyeSpace[0], lightPositionInEyeSpace[1], lightPositionInEyeSpace[2]);

        // Disegna il cubo
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        GLES20.glDisableVertexAttribArray(positionHandle);

    }
}

