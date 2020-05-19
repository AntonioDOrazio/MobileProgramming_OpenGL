uniform mat4 u_MVPMatrix;      // Matrice ModelViewProjection
uniform mat4 u_MVMatrix;       // Matrice ModelView

attribute vec4 a_Position;     // Informazioni sulle posizioni dei vertici
attribute vec4 a_Color;        // Informazioni sui colori dei vertici
attribute vec3 a_Normal;       // Informazioni sulle normali dei vertici

// Output del vertex shader, poi input del fragment shader
varying vec3 v_Position;
varying vec4 v_Color;
varying vec3 v_Normal;

void main()
{
    // Trasformo i vertici in eye space
    v_Position = vec3(u_MVMatrix * a_Position);

    // Trasformo i vettori normali alla superficie in eye space
    v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));

    // Non modifico i colori. Lo faccio nel fragment in cui calcolo l'illuminazione e quindi il nuovo colore
    // Quindi l'output del colore corrisponde esattamente all'input ricevuto
    v_Color = a_Color;

    // gl_Position contiene le posizioni dei vertici
    // Moltiplico per la matrice MVP per effettuare la proiezione finale
    // dello spazio 3D per lo schermo 2D del dispositivo
    gl_Position = u_MVPMatrix * a_Position;
}