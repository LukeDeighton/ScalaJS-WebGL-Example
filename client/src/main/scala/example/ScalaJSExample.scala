package example

import example.GlobalState.gl
import glmatrix.mat4
import org.scalajs.dom
import org.scalajs.dom.raw.WebGLBuffer
import org.scalajs.dom.raw.WebGLRenderingContext._
import org.scalajs.dom.{html, raw}

import scala.scalajs.js
import scala.scalajs.js.typedarray.Float32Array

object ScalaJSExample extends js.JSApp {

  def main(): Unit = {
    val canvas = createCanvas(canvasSize = 700)
    dom.document.body.appendChild(canvas)
  }

  def createCanvas(canvasSize: Int): html.Canvas = {
    val canvas = dom.document.createElement("canvas").asInstanceOf[html.Canvas]
    canvas.width = canvasSize
    canvas.height = canvasSize

    GlobalState.gl = canvas.getContext("webgl").asInstanceOf[raw.WebGLRenderingContext]

    val shaderProgramInfo = loadShaderProgram()
    val buffers = initialiseBuffers()

    drawScene(shaderProgramInfo, buffers)

    canvas
  }

  def drawScene(programInfo: ShaderProgramInfo, buffers: WebGLBuffer) = {
    gl.clearColor(0.0, 0.0, 0.0, 1.0)
    gl.clearDepth(1.0)
    gl.enable(DEPTH_TEST)
    gl.depthFunc(LEQUAL)
    gl.clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT)

    val fieldOfView = 45.0 * Math.PI / 180.0
    val aspect = gl.canvas.width / gl.canvas.height

    val projectionMatrix = mat4.create()

    mat4.perspective(projectionMatrix, fieldOfView, aspect, near = 0.1, far = 100.0)

    val modelViewMatrix = mat4.create()

    mat4.translate(modelViewMatrix, modelViewMatrix, js.Array(-0.0, 0.0, -6.0))

    gl.bindBuffer(ARRAY_BUFFER, buffers)
    gl.vertexAttribPointer(
      programInfo.attributeLocations.vertexPosition,
      size = 2,
      FLOAT,
      normalized = false,
      stride = 0,
      offset = 0)
    gl.enableVertexAttribArray(
      programInfo.attributeLocations.vertexPosition)

    gl.useProgram(programInfo.program)

    gl.uniformMatrix4fv(
      programInfo.uniformLocations.projectionMatrix,
      transpose = false,
      projectionMatrix)
    gl.uniformMatrix4fv(
      programInfo.uniformLocations.modelViewMatrix,
      transpose = false,
      modelViewMatrix)

    gl.drawArrays(TRIANGLE_STRIP, first = 0, count = 4)
  }

  def initialiseBuffers(): WebGLBuffer = {
    val positionBuffer = gl.createBuffer()

    gl.bindBuffer(ARRAY_BUFFER, positionBuffer)

    val positions = js.Array(
       1.0,  1.0,
      -1.0,  1.0,
       1.0, -1.0,
      -1.0, -1.0
    )

    gl.bufferData(ARRAY_BUFFER, new Float32Array(positions), STATIC_DRAW)

    positionBuffer
  }

  def loadShaderProgram(): ShaderProgramInfo = {
    val program = ShaderProgramLoader.load(SquareShaderSource)

    ShaderProgramInfo(
      program = program,
      attributeLocations = AttributeLocations(
        vertexPosition = gl.getAttribLocation(program, "aVertexPosition")),
      uniformLocations = UniformLocations(
        projectionMatrix = gl.getUniformLocation(program, "uProjectionMatrix"),
        modelViewMatrix = gl.getUniformLocation(program, "uModelViewMatrix")
      ))
  }
}
