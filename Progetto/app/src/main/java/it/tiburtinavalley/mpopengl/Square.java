package it.tiburtinavalley.mpopengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Square {


    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    private static final int COORDS_PER_VERTEX = 3;   //a cosa serve??
    private static final int BYTES_PER_FLOAT = 4;

    private static float[] squareCoords = {
            -0.5f,  0.5f, 0.0f,   // top left
            -0.5f, -0.5f, 0.0f,   // bottom left
            0.5f, -0.5f, 0.0f,   // bottom right
            0.5f,  0.5f, 0.0f }; // top right

    private short[] drawOrder = { 0, 1, 2 ,0, 2 ,3 };

    float[] color = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public Square() {
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * BYTES_PER_FLOAT);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb. asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);


        ByteBuffer dlB = ByteBuffer.allocateDirect(drawOrder.length *2);
        dlB.order(ByteOrder.nativeOrder());
        drawListBuffer = dlB.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

    }
}
