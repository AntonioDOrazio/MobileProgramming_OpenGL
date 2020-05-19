package it.tiburtinavalley.mpopengl;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class FloorPlane {

    private static final int COORDINATES_DATA_SIZE = 3;
    private static final int COLOR_DATA_SIZE = 4;
    private static final int NORMAL_COORDINATES_DATA_SIZE = 3;


    private static final int BYTES_PER_FLOAT = 4;

    private String vertexShaderCode;
    private String fragmentShaderCode;


    private final int mProgram;

    private FloatBuffer vertexBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer colorBuffer;


    // X, Y, Z
    private final float[] cubeCoords =
            {
                    // In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
                    // if the points are counter-clockwise we are looking at the "front". If not we are looking at
                    // the back. OpenGL has an optimization where all back-facing triangles are culled, since they
                    // usually represent the backside of an object and aren't visible anyways.


                    // Top face
                    -20.0f, 20.0f, -20.0f,
                    -20.0f, 20.0f, 20.0f,
                    20.0f, 20.0f, -20.0f,
                    -20.0f, 20.0f, 20.0f,
                    20.0f, 20.0f, 20.0f,
                    20.0f, 20.0f, -20.0f,

            };

    // R, G, B, A
    private final float[] color =
            {
                    // Top face (cyan)
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
                    0.0f, 1.0f, 1.0f, 1.0f,
            };

    // X, Y, Z
    // The normal is used in light calculations and is a vector which points
    // orthogonal to the plane of the surface. For a cube model, the normals
    // should be orthogonal to the points of each face.
    final float[] cubeNormalCoords =
            {
                    // Top face
                    0.0f, 2.0f, 0.0f,
                    0.0f, 2.0f, 0.0f,
                    0.0f, 2.0f, 0.0f,
                    0.0f, 2.0f, 0.0f,
                    0.0f, 2.0f, 0.0f,
                    0.0f, 2.0f, 0.0f,
            };


    private int positionHandle;
    private int colorHandle;
    private int normalHandle;
    private int modelViewMatrixHandle;
    private int modelViewProjectionMatrixHandle;

    private final int vertexCount = cubeCoords.length / COORDINATES_DATA_SIZE;
    private final int vertexStride = COORDINATES_DATA_SIZE * BYTES_PER_FLOAT;   // never used


    public FloorPlane() {
        vertexShaderCode = Utility.getShaderCodeFromFile(R.raw.vertex_plane);
        fragmentShaderCode = Utility.getShaderCodeFromFile(R.raw.fragment_plane);

        // Initialize the buffers.
        vertexBuffer = ByteBuffer.allocateDirect(cubeCoords.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(cubeCoords).position(0);

        colorBuffer = ByteBuffer.allocateDirect(color.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer.put(color).position(0);

        normalBuffer = ByteBuffer.allocateDirect(cubeNormalCoords.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalBuffer.put(cubeNormalCoords).position(0);

        int vertexShader = Utility.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = Utility.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] modelMatrix, float[] viewMatrix, float[] projectionMatrix, float[] lightPositionInEyeSpace) {
        GLES20.glUseProgram(mProgram);

        // Dà allo shader informazioni sulla posizione dei vertici
        positionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        //       vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(positionHandle, COORDINATES_DATA_SIZE, GLES20.GL_FLOAT, false,
                COORDINATES_DATA_SIZE * BYTES_PER_FLOAT, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Dà allo shader informazioni sul colore
        colorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");
        //     colorBuffer.position(0);
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

        // Pass in the light position in eye space.
        int lightPositionHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");

        GLES20.glUniform3f(lightPositionHandle, lightPositionInEyeSpace[0], lightPositionInEyeSpace[1], lightPositionInEyeSpace[2]);

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        GLES20.glDisableVertexAttribArray(positionHandle);

    }
}

