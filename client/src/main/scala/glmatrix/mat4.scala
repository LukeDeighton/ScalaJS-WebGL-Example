package glmatrix

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal
object mat4 extends js.Object {

  /**
    * Creates a 4x4 identity matrix
    */
  def create(): Matrix4 = js.native

  def perspective(projectionMatrix: Matrix4, fovy: Double, aspect: Double, near: Double, far: Double): Unit = js.native

  //TODO could make a vec3 class
  def translate(out: Matrix4, a: Matrix4, v: List[Double]): Unit = js.native
}

@js.native
trait Matrix4 extends js.Object {

}