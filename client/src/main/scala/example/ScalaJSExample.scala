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

  import GlobalState._

  def main(): Unit = {
    val canvas = createCanvas(canvasSize = 700)
    dom.document.body.appendChild(canvas)
  }

  val render: js.Function1[Double, Unit] = { (time: Double) =>
    val timeSeconds = time * 0.001
    val deltaTime = timeSeconds - previousTime
    previousTime = timeSeconds

    squareRotation += deltaTime

    drawScene(shaderProgramInfo, positionBuffer, colourBuffer)

    dom.window.requestAnimationFrame(render)
  }

  def createCanvas(canvasSize: Int): html.Canvas = {
    val canvas = dom.document.createElement("canvas").asInstanceOf[html.Canvas]
    canvas.width = canvasSize
    canvas.height = canvasSize

    gl = canvas.getContext("webgl").asInstanceOf[raw.WebGLRenderingContext]
    shaderProgramInfo = loadShaderProgram()
    positionBuffer = initialisePositionBuffer()
    colourBuffer = initialiseColourBuffer()

    dom.window.requestAnimationFrame(render)

    canvas
  }


  def drawScene(programInfo: ShaderProgramInfo, positionBuffer: WebGLBuffer, colourBuffer: WebGLBuffer) = {
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

    mat4.rotate(modelViewMatrix, modelViewMatrix, GlobalState.squareRotation, js.Array(0.0, 0.0, 1.0))

    gl.useProgram(programInfo.program)

    gl.bindBuffer(ARRAY_BUFFER, positionBuffer)
    gl.vertexAttribPointer(
      programInfo.attributeLocations.vertexPosition,
      size = 2,
      FLOAT,
      normalized = false,
      stride = 0,
      offset = 0)
    gl.enableVertexAttribArray(
      programInfo.attributeLocations.vertexPosition)

    gl.bindBuffer(ARRAY_BUFFER, colourBuffer)
    gl.vertexAttribPointer(
      programInfo.attributeLocations.vertexColour,
      size = 4,
      FLOAT,
      normalized = false,
      stride = 0,
      offset = 0)
    gl.enableVertexAttribArray(
      programInfo.attributeLocations.vertexColour)

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

  def initialisePositionBuffer(): WebGLBuffer = {
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

  def initialiseColourBuffer(): WebGLBuffer = {
    val colorBuffer = gl.createBuffer()
    gl.bindBuffer(ARRAY_BUFFER, colorBuffer)
    val colours = js.Array(
      1.0,  1.0,  1.0,  1.0,    // white
      1.0,  0.0,  0.0,  1.0,    // red
      0.0,  1.0,  0.0,  1.0,    // green
      0.0,  0.0,  1.0,  1.0,    // blue
    )
    gl.bufferData(ARRAY_BUFFER, new Float32Array(colours), STATIC_DRAW)
    colorBuffer
  }

  def loadShaderProgram(): ShaderProgramInfo = {
    val program = ShaderProgramLoader.load(SquareShaderSource)

    ShaderProgramInfo(
      program = program,
      attributeLocations = AttributeLocations(
        vertexPosition = gl.getAttribLocation(program, "aVertexPosition"),
        vertexColour = gl.getAttribLocation(program, "aVertexColour")),
      uniformLocations = UniformLocations(
        projectionMatrix = gl.getUniformLocation(program, "uProjectionMatrix"),
        modelViewMatrix = gl.getUniformLocation(program, "uModelViewMatrix")
      ))
  }
}
