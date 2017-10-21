package example

import org.scalajs.dom.raw.{WebGLProgram, WebGLUniformLocation}

case class ShaderProgramInfo(program: WebGLProgram,
                             attributeLocations: AttributeLocations,
                             uniformLocations: UniformLocations)

case class AttributeLocations(vertexPosition: Int)

case class UniformLocations(projectionMatrix: WebGLUniformLocation, modelViewMatrix: WebGLUniformLocation)