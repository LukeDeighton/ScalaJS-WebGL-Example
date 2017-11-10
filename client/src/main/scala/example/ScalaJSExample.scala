package example

import glmatrix.mat4
import org.scalajs.dom
import org.scalajs.dom.raw.WebGLBuffer
import org.scalajs.dom.raw.WebGLRenderingContext._
import org.scalajs.dom.{html, raw}

import scala.scalajs.js
import scala.scalajs.js.typedarray.{Float32Array, Uint16Array}

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

    drawScene(shaderProgramInfo, positionBuffer, colourBuffer, indicesBuffer)

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
    indicesBuffer = initialiseIndicesBuffer()

    dom.window.requestAnimationFrame(render)

    canvas
  }

  def drawScene(programInfo: ShaderProgramInfo, positionBuffer: WebGLBuffer, colourBuffer: WebGLBuffer, indicesBuffer: WebGLBuffer) = {
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
    mat4.rotate(modelViewMatrix, modelViewMatrix, GlobalState.squareRotation * 0.7, js.Array(0.0, 1.0, 0.0))

    gl.useProgram(programInfo.program)

    gl.bindBuffer(ARRAY_BUFFER, positionBuffer)
    gl.vertexAttribPointer(
      programInfo.attributeLocations.vertexPosition,
      size = 3,
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

    gl.bindBuffer(ELEMENT_ARRAY_BUFFER, indicesBuffer)
    gl.drawElements(TRIANGLES, count = 36, UNSIGNED_SHORT, offset = 0)
  }

  def initialisePositionBuffer(): WebGLBuffer = {
    val positions = js.Array(
      // Front face
      -1.0, -1.0,  1.0,
      1.0, -1.0,  1.0,
      1.0,  1.0,  1.0,
      -1.0,  1.0,  1.0,

      // Back face
      -1.0, -1.0, -1.0,
      -1.0,  1.0, -1.0,
      1.0,  1.0, -1.0,
      1.0, -1.0, -1.0,

      // Top face
      -1.0,  1.0, -1.0,
      -1.0,  1.0,  1.0,
      1.0,  1.0,  1.0,
      1.0,  1.0, -1.0,

      // Bottom face
      -1.0, -1.0, -1.0,
      1.0, -1.0, -1.0,
      1.0, -1.0,  1.0,
      -1.0, -1.0,  1.0,

      // Right face
      1.0, -1.0, -1.0,
      1.0,  1.0, -1.0,
      1.0,  1.0,  1.0,
      1.0, -1.0,  1.0,

      // Left face
      -1.0, -1.0, -1.0,
      -1.0, -1.0,  1.0,
      -1.0,  1.0,  1.0,
      -1.0,  1.0, -1.0,
    )

    val positionBuffer = gl.createBuffer()
    gl.bindBuffer(ARRAY_BUFFER, positionBuffer)
    gl.bufferData(ARRAY_BUFFER, new Float32Array(positions), STATIC_DRAW)
    positionBuffer
  }

  def initialiseColourBuffer(): WebGLBuffer = {
    val faceColours = js.Array(
      js.Array(1.0,  1.0,  1.0,  1.0),    // Front face: white
      js.Array(1.0,  0.0,  0.0,  1.0),    // Back face: red
      js.Array(0.0,  1.0,  0.0,  1.0),    // Top face: green
      js.Array(0.0,  0.0,  1.0,  1.0),    // Bottom face: blue
      js.Array(1.0,  1.0,  0.0,  1.0),    // Right face: yellow
      js.Array(1.0,  0.0,  1.0,  1.0),    // Left face: purple
    )

    var colours = js.Array[Double]()
    for (j <- 0 until faceColours.length) {
      val colour = faceColours(j)

      colours = colours.concat(colour, colour, colour, colour)
    }

    val colorBuffer = gl.createBuffer()
    gl.bindBuffer(ARRAY_BUFFER, colorBuffer)
    gl.bufferData(ARRAY_BUFFER, new Float32Array(colours), STATIC_DRAW)
    colorBuffer
  }

  def initialiseIndicesBuffer(): WebGLBuffer = {
    val indices = js.Array(
      0,  1,  2,      0,  2,  3,    // front
      4,  5,  6,      4,  6,  7,    // back
      8,  9,  10,     8,  10, 11,   // top
      12, 13, 14,     12, 14, 15,   // bottom
      16, 17, 18,     16, 18, 19,   // right
      20, 21, 22,     20, 22, 23,   // left
    )

    val indicesBuffer = gl.createBuffer()
    gl.bindBuffer(ELEMENT_ARRAY_BUFFER, indicesBuffer)
    gl.bufferData(ELEMENT_ARRAY_BUFFER, new Uint16Array(indices), STATIC_DRAW)
    indicesBuffer
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
