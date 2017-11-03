package example

object SquareShaderSource extends ShaderSource {

  val vertexShader: String =
   """
     attribute vec4 aVertexPosition;
     attribute vec4 aVertexColour;

     uniform mat4 uModelViewMatrix;
     uniform mat4 uProjectionMatrix;

     varying lowp vec4 vColour;

     void main() {
       gl_Position = uProjectionMatrix * uModelViewMatrix * aVertexPosition;
       vColour = aVertexColour;
     }
   """

  val fragmentShader: String =
   """
     varying lowp vec4 vColour;

     void main(void) {
       gl_FragColor = vColour;
     }
   """
}
