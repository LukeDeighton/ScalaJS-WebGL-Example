package example

import org.scalajs.dom.raw

object GlobalState {

  type WebGL = raw.WebGLRenderingContext

  var gl: WebGL = _
}
