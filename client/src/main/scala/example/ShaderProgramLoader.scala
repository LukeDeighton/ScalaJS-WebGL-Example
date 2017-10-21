package example

import org.scalajs.dom.raw.WebGLRenderingContext.{COMPILE_STATUS, FRAGMENT_SHADER, LINK_STATUS, VERTEX_SHADER}
import org.scalajs.dom.raw.{WebGLProgram, WebGLShader}
import GlobalState.{WebGL, gl}

import scala.scalajs.js

object ShaderProgramLoader {

  def load(shaderSource: ShaderSource): WebGLProgram = {
    val vertexShader = loadVertexShader(shaderSource.vertexShader)
    val fragmentShader = loadFragmentShader(shaderSource.fragmentShader)

    val shaderProgram = gl.createProgram()
    gl.attachShader(shaderProgram, vertexShader)
    gl.attachShader(shaderProgram, fragmentShader)
    gl.linkProgram(shaderProgram)

    if (!gl.getProgramParameter(shaderProgram, LINK_STATUS).asInstanceOf[Boolean]) {
      throw js.JavaScriptException("Failed to compile shader program: " + gl.getProgramInfoLog(shaderProgram))
    }

    shaderProgram
  }

  def loadVertexShader(source: String): WebGLShader = loadShader(gl, VERTEX_SHADER, source)

  def loadFragmentShader(source: String): WebGLShader = loadShader(gl, FRAGMENT_SHADER, source)

  def loadShader(gl: WebGL, shaderType: Int, source: String): WebGLShader = {
    val shader = gl.createShader(shaderType)
    gl.shaderSource(shader, source)
    gl.compileShader(shader)

    if (!gl.getShaderParameter(shader, COMPILE_STATUS).asInstanceOf[Boolean]) {
      throw js.JavaScriptException("Failed to compile shader: " + source)
    }

    shader
  }
}
