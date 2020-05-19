precision mediump float;       // Imposta la precisione del tipo di dato float a medium

uniform vec3 u_LightPos;       // Posizione della luce in eye space

varying vec3 v_Position;       // Informazioni sulla posizione interpolata per ogni singolo frammento
varying vec4 v_Color;          // Informazioni sul colore interpolata per ogni singolo frammento
varying vec3 v_Normal;         // Informazioni sulle normali interpolata per ogni singolo frammento


void main()
{
   // Calcolo la distanza tra la luce e la posizione del frammento
   float distance = length(u_LightPos - v_Position);

   // Normalizzo la distanza per ottenere un versore che punta dalla luce al frammento
   vec3 lightVector = normalize(u_LightPos - v_Position);
   float diffuse = max(dot(v_Normal, lightVector), 0.0);

   // Calcolo di quanto il frammento è illuminato. La luce si attenua seguendo il quadrato della distanza
   diffuse = diffuse * (1.0 / (1.0 + (0.10 * distance * distance)));

   // Aggiungo al risultato una luce costante per aumentare la luminosità
   diffuse = diffuse + 0.2;

   // Do in output il colore dei frammenti moltiplicato per la quantità di illuminazione
   gl_FragColor = (v_Color * diffuse); 
}