package example

import example.GlobalState.gl
import glmatrix.mat4
import org.scalajs.dom
import org.scalajs.dom.raw.WebGLRenderingContext._
import org.scalajs.dom.{html, raw}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSGlobalScope, JSImport}
import scala.scalajs.js.typedarray.Float32Array

object ScalaJSExample extends js.JSApp {

  def main(): Unit = {
    val canvas = createCanvas(canvasSize = 1000)
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

  def drawScene(programInfo: ShaderProgramInfo, buffers: js.Array[Double]) = {
    gl.clearColor(0.0, 0.0, 0.0, 1.0)
    gl.clearDepth(1.0)
    gl.enable(DEPTH_TEST)
    gl.depthFunc(LEQUAL)
    gl.clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT)

    val fieldOfView = 45 * Math.PI / 180;   // in radians
    val aspect = gl.canvas.clientWidth / gl.canvas.clientHeight
    val zNear = 0.1
    val zFar = 100.0
    val projectionMatrix = mat4.create()

    mat4.perspective(projectionMatrix, fieldOfView, aspect, zNear, zFar)

    val modelViewMatrix = mat4.create()

    println(modelViewMatrix)

    mat4.translate(modelViewMatrix, modelViewMatrix, List(-0.0, 0.0, -6.0))

    println(modelViewMatrix)
//    println(mat4)
//    val mat = mat4.create()

//    mat

    //    val projectionMatrix = mat4.create()

  }

  def initialiseBuffers(): js.Array[Double] = {
    val positionBuffer = gl.createBuffer()

    gl.bindBuffer(ARRAY_BUFFER, positionBuffer)

    val positions = js.Array(
       1.0,  1.0,
      -1.0,  1.0,
       1.0, -1.0,
      -1.0, -1.0
    )

    gl.bufferData(ARRAY_BUFFER, new Float32Array(positions), STATIC_DRAW)

    positions
  }

  def loadShaderProgram(): ShaderProgramInfo = {
    val program = ShaderProgramLoader.load(SquareShaderSource)
    gl.useProgram(program)

    ShaderProgramInfo(
      program = program,
      attributeLocations = AttributeLocations(
        vertexPosition = gl.getAttribLocation(program, "aVertexPosition")),
      uniformLocations = UniformLocations(
        projectionMatrix = gl.getUniformLocation(program, "uProjectionMatrix"),
        modelViewMatrix = gl.getUniformLocation(program, "uModelViewMatrix")
      ))
  }

  //
  //    gl.clearColor(0.4, 0.0, 0.5, 0.8)
  //    gl.clear(COLOR_BUFFER_BIT)
  //
  //
  //    val vertices = new Float32Array(js.Array(
  //      -0.3f,-0.3f,  0.3f,-0.3f,  0.0f,0.3f,  0.2f,0.2f,
  //      0.6f,0.6f,  0.4f,-0.4f))
  //
  //    val buffer = gl.createBuffer()
  //    gl.bindBuffer(ARRAY_BUFFER, buffer)
  //    gl.bufferData(ARRAY_BUFFER, vertices, STATIC_DRAW)
  //
  //    val programDynamic = program.asInstanceOf[scala.scalajs.js.Dynamic]
  //    programDynamic.color = gl.getUniformLocation(program, "color")
  //
  //    val temp2 = scala.scalajs.js.Array[Double]()
  //    temp2.push(0f, 1f, 0.5f, 1.0f)
  //    gl.uniform4fv(programDynamic.color.asInstanceOf[dom.raw.WebGLUniformLocation], temp2)
  //
  //    programDynamic.position = gl.getAttribLocation(program, "position")
  //    gl.enableVertexAttribArray(programDynamic.position.asInstanceOf[Int])
  //    gl.vertexAttribPointer(programDynamic.position.asInstanceOf[Int], 2, FLOAT, normalized = false, 0, 0)
  //    gl.drawArrays(TRIANGLES, 0, vertices.length / 2)
}
