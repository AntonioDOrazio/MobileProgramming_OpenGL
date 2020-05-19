
precision mediump float;       // Set the default precision to medium. We don't need as high of a
                               // precision in the fragment shader.
uniform vec3 u_LightPos;       // The position of the light in eye space.


varying vec3 v_Position;       // Posizione interpolata del singolo frammento
varying vec4 v_Color;          // This is the color from the vertex shader interpolated across the
                               // triangle per fragment.
varying vec3 v_Normal;         // Interpolated normal for this fragment.



void main()                       // The entry point for our fragment shader.
{
   float distance = length(u_LightPos - v_Position);

   vec3 lightVector = normalize(u_LightPos - v_Position);

   float diffuse = max(dot(v_Normal, lightVector), 0.0);

   diffuse = diffuse * (1.0 / (1.0 + (0.10 * distance * distance)));

   // Add ambient lighting
   diffuse = diffuse + 0.2;  // prima era diffuse + 0.3

   gl_FragColor = (v_Color * diffuse);
}