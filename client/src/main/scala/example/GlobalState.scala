package example

import org.scalajs.dom.raw
import org.scalajs.dom.raw.WebGLBuffer

object GlobalState {

  type WebGL = raw.WebGLRenderingContext
  var gl: WebGL = _

  var previousTime: Double = 0.0
  var rotation: Double = 0.0
  var shaderProgramInfo: ShaderProgramInfo = _
  var positionBuffer: WebGLBuffer = _
  var colourBuffer: WebGLBuffer = _
  var indicesBuffer: WebGLBuffer = _
  var translationX: Double = 0.0
  var translationY: Double = 0.0
  var translationZ: Double = 0.0
}
