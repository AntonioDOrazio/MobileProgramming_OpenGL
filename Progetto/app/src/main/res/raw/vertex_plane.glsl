
uniform mat4 u_MVPMatrix;      // A constant representing the combined model/view/projection matrix.
uniform mat4 u_MVMatrix;       // A constant representing the combined model/view matrix.

attribute vec4 a_Position;     // Per-vertex position information we will pass in.
attribute vec4 a_Color;        // Per-vertex color information we will pass in.
attribute vec3 a_Normal;       // Per-vertex normal information we will pass in.

varying vec3 v_Position;       // This will be passed into the fragment shader.
varying vec4 v_Color;          // This will be passed into the fragment shader.
varying vec3 v_Normal;         // This will be passed into the fragment shader.

void main()                         // The entry point for our vertex shader.
{
    // Trasformo i vertici in eye space +
     v_Position = vec3(u_MVMatrix * a_Position);

    // Trasformo i vettori normali alla superficie in eye space +
    v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));

    // Non modifico i colori. Lo faccio nel fragment in cui calcolo l'illuminazione e quindi il nuovo colore
    v_Color = a_Color;
    // gl_Position — contains the position of the current vertex.
    // Moltiplico per la matrice MVP per effettuare la proiezione finale
    // dello
    // spazio 3D per lo schermo 2D del dispositivo +
    gl_Position = u_MVPMatrix * a_Position;
}