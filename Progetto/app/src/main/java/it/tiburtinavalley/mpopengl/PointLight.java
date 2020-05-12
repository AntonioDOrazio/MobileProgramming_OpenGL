package it.tiburtinavalley.mpopengl;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class PointLight {


    public float[] positionInWorldSpace;
    public float[] positionInEyeSpace;
    public float[] positionInModelSpace;

    private int programHandle;
    float mvpMatrix[] = new float[16];

    final String pointVertexShader =
            "uniform mat4 u_MVPMatrix;      \n"
                    + "attribute vec4 a_Position;     \n"
                    + "void main()                    \n"
                    + "{                              \n"
                    + "   gl_Position = u_MVPMatrix   \n"
                    + "               * a_Position;   \n"
                    + "   gl_PointSize = 5.0;         \n"
                    + "}                              \n";

    final String pointFragmentShader =
            "precision mediump float;       \n"
                    + "void main()                    \n"
                    + "{                              \n"
                    + "   gl_FragColor = vec4(1.0,    \n"
                    + "   1.0, 1.0, 1.0);             \n"
                    + "}                              \n";




    public PointLight() {
        // Centrata nell'origine all'istante iniziale
        positionInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
        positionInEyeSpace = new float[4];
        positionInWorldSpace = new float[4];


        final int pointVertexShaderHandle = Utility.loadShader(GLES20.GL_VERTEX_SHADER, pointVertexShader);
        final int pointFragmentShaderHandle = Utility.loadShader(GLES20.GL_FRAGMENT_SHADER, pointFragmentShader);

        programHandle = GLES20.glCreateProgram();
        GLES20.glAttachShader(programHandle, pointVertexShaderHandle);
        GLES20.glAttachShader(programHandle, pointFragmentShaderHandle);
        GLES20.glLinkProgram(programHandle);


    }


    int pointMVPMatrixHandle;
    int pointPositionHandle;


    public void draw(float[] modelMatrix, float[] viewMatrix, float[] projectionMatrix) {
        GLES20.glUseProgram(programHandle);
         pointMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
         pointPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");

        // Pass in the position.
        GLES20.glVertexAttrib3f(pointPositionHandle, getPositionInModelSpace()[0], getPositionInModelSpace()[1], getPositionInModelSpace()[2]);

        // Since we are not using a buffer object, disable vertex arrays for this attribute.
        GLES20.glDisableVertexAttribArray(pointPositionHandle);

        // Pass in the transformation matrix.
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the point.
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }
    
    

    public float[] getPositionInWorldSpace() {
        return positionInWorldSpace;
    }

    public void setPositionInWorldSpace(float[] positionInWorldSpace) {
        this.positionInWorldSpace = positionInWorldSpace;
    }

    public float[] getPositionInEyeSpace() {
        return positionInEyeSpace;
    }

    public void setPositionInEyeSpace(float[] positionInEyeSpace) {
        this.positionInEyeSpace = positionInEyeSpace;
    }

    public float[] getPositionInModelSpace() {
        return positionInModelSpace;
    }

    public void setPositionInModelSpace(float[] positionInModelSpace) {
        this.positionInModelSpace = positionInModelSpace;
    }
}
