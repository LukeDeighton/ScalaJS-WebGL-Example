package glmatrix

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import scala.scalajs.js.typedarray.Float32Array

@js.native
@JSGlobal
object mat4 extends js.Object {

  /**
    * Creates a 4x4 identity matrix
    */
  def create(): Matrix4 = js.native

  def perspective(projectionMatrix: Matrix4, fieldOfView: Double, aspect: Double, near: Double, far: Double): Unit = js.native

  def translate(out: Matrix4, a: Matrix4, v: js.Array[Double]): Unit = js.native

  def rotate(out: Matrix4, a: Matrix4, rad: Double, axis: js.Array[Double]): Unit = js.native
}

@js.native
trait Matrix4 extends Float32Array