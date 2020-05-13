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

    // TODO caricare da file
    private final String vertexShaderCode =
            "uniform mat4 u_MVPMatrix;      // A constant representing the combined model/view/projection matrix.\n" +
                    "uniform mat4 u_MVMatrix;       // A constant representing the combined model/view matrix.\n" +
                    "\n" +
                    "attribute vec4 a_Position;     // Per-vertex position information we will pass in.\n" +
                    "attribute vec4 a_Color;        // Per-vertex color information we will pass in.\n" +
                    "attribute vec3 a_Normal;       // Per-vertex normal information we will pass in.\n" +
                    "\n" +
                    "varying vec3 v_Position;       // This will be passed into the fragment shader.\n" +
                    "varying vec4 v_Color;          // This will be passed into the fragment shader.\n" +
                    "varying vec3 v_Normal;         // This will be passed into the fragment shader.\n" +
                    "\n" +
                    "void main()                         // The entry point for our vertex shader.\n" +
                    "{\n" +
                    "    // Trasformo i vertici in eye space\n" +
                    "     v_Position = vec3(u_MVMatrix * a_Position);\n" +
                    "\n" +
                    "    // Trasformo i vettori normali alla superficie in eye space\n" +
                    "    v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));\n" +
                    "\n" +
                    "    // Non modifico i colori. Lo faccio nel fragment in cui calcolo l'illuminazione e quindi il nuovo colore\n" +
                    "    v_Color = a_Color;\n" +
                    "    // gl_Position — contains the position of the current vertex.\n" +
                    "    // Moltiplico per la matrice MVP per effettuare la proiezione finale\n" +
                    "    // dello\n" +
                    "    // spazio 3D per lo schermo 2D del dispositivo\n" +
                    "    gl_Position = u_MVPMatrix * a_Position;\n" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;       // Set the default precision to medium. We don't need as high of a\n" +
                    "                               // precision in the fragment shader.\n" +
                    "uniform vec3 u_LightPos;       // The position of the light in eye space.\n" +
                    "\n" +
                    "\n" +
                    "varying vec3 v_Position;       // Posizione interpolata del singolo frammento\n" +
                    "varying vec4 v_Color;          // This is the color from the vertex shader interpolated across the\n" +
                    "                               // triangle per fragment.\n" +
                    "varying vec3 v_Normal;         // Interpolated normal for this fragment.\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "void main()                       // The entry point for our fragment shader.\n" +
                    "{\n" +
                    "   float distance = length(u_LightPos - v_Position);\n" +
                    "\n" +
                    "   vec3 lightVector = normalize(u_LightPos - v_Position);\n" +
                    "\n" +
                    "   float diffuse = max(dot(v_Normal, lightVector), 0.0);\n" +
                    "\n" +
                    "   diffuse = diffuse * (1.0 / (1.0 + (0.10 * distance * distance)));\n" +
                    "\n" +
                    "   // Add ambient lighting\n" +
                    "   diffuse = diffuse + 0.2;\n" + // prima era diffuse + 0.3
                    "\n" +
                    "   gl_FragColor = (v_Color * diffuse);\n" +
                    "}";






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

