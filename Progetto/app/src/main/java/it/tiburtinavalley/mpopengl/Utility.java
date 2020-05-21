package it.tiburtinavalley.mpopengl;

import android.content.Context;
import android.opengl.GLES20;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class Utility
{
    public static int loadShader(int type, String shaderCode)
    {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
    public static String getShaderCodeFromFile(int fileId) {
        try {

            // Per convertire il codice degli shader in stringhe
            InputStream inputStream =
                    ContextUtil.get().getResources().openRawResource(fileId);
            String shaderCode =
                    IOUtils.toString(inputStream, String.valueOf(Charset.defaultCharset()));
            inputStream.close();

            return shaderCode;
        } catch (IOException e) {
            return null;
        }
    }
}
