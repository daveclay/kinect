package com.daveclay.processing.examples.vector

import processing.core.{PMatrix3D, PVector, PApplet}
import processing.core.PConstants._
import com.daveclay.processing.examples.vector.PMatrix3DTest.StringConstrainer

object PAppletRunner {
    def main(args: Array[String]) {
        PApplet.main(args(0))
    }
}

class DCApplet extends PApplet {

    override def setup() {
    }

    def drawHUD() {
        textSize(11)
        fill(color(255, 255, 255))
    }

    override def keyPressed() {
        System.out.println("keyCode: " + keyCode)
        keyCode match {
            case LEFT =>
                onKeyLeft()
            case RIGHT =>
                onKeyRight()
            case UP =>
                onKeyUp()
            case DOWN =>
                onKeyDown()
            case SHIFT =>
                onKeyShift()
            case _ =>
        }
    }

    var onKeyLeft: () => Unit = _
    var onKeyRight: () => Unit = _
    var onKeyUp: () =>  Unit = _
    var onKeyDown: () => Unit = _
    var onKeyShift: () => Unit = _
}

/*
class PMatrix3DTestScala extends DCApplet {

    val orbitMatrix = new PMatrix3D()
    val rotationMatrix = new PMatrix3D()

    var center = new PVector()
    val x = new PVector()
    val y = new PVector()
    val z = new PVector()

    var angleX: Float = 0
    var angleY: Float = 0
    var angleZ: Float = 0

    val stringConstrainer = new StringConstrainer()

    override def setup() {
        size(800, 800, P3D) //OPENGL);
        center.set(width / 2, height / 2, -800)
    }

    onKeyLeft = () => angleX += .01f
    onKeyRight = () => angleX -= .01f
    onKeyUp = () => angleY += .01f
    onKeyDown = () => angleY -= .01f

    override def draw() {
        background(80)
        lights()

        calculateMatrixStuff()

        drawOrbitingCube()
        moveUniverseAndDraw()

        textSize(11)
        fill(color(255, 255, 255))
        text("x: " + x + "\ny: " + y + "\nz: " + z, 30, 30)
    }

    private def drawOrbitingCube() {
        pushMatrix()
        translateToCenter()
        orbitMatrix.set(rotationMatrix)
        orbitMatrix.translate(300, 0, 0)
        applyMatrix(orbitMatrix)
        drawOrbitBox()


        popMatrix()
    }

    private def moveUniverseAndDraw() {
        pushMatrix()
        translateToCenter()
        applyMatrix(rotationMatrix)
        drawElements()
        popMatrix()
    }

    private def translateToCenter() {
        translate(center.x, center.y, center.z)
    }

    private def drawElements() {
        drawCenterBox()
        drawXAxis()
        drawZAxis()
        drawYAxis()
    }

    private def drawOrbitBox() {
        fill(color(0, 0, 180))
        stroke(color(255, 255, 255))
        box(100)
    }

    private def drawCenterBox() {
        fill(color(255, 0, 0))
        stroke(color(255, 255, 255))
        box(200)
    }

    private def calculateMatrixStuff() {
        rotationMatrix.rotateX(angleX)
        rotationMatrix.rotateY(angleY)
        rotationMatrix.rotateZ(angleZ)

        x.set(0, 0, 0)
        rotationMatrix.mult(new PVector(1, 0, 0), x)

        y.set(0, 0, 0)
        rotationMatrix.mult(new PVector(0, 1, 0), y)

        z.set(0, 0, 0)
        rotationMatrix.mult(new PVector(0, 0, 1), z)
    }

    private def drawXAxis() {
        pushMatrix()
        translate(-10000, 0, 0)
        noStroke()
        fill(color(0, 255, 0))
        box(30000, 1, 1)
        popMatrix()
    }

    private def drawZAxis() {
        pushMatrix()
        translate(0, 0, -10000)
        noStroke()
        fill(color(255, 0, 0))
        box(1, 1, 30000)
        popMatrix()
    }

    private def drawYAxis() {
        pushMatrix()
        translate(0, -10000, 0)
        noStroke()
        fill(color(0, 0, 255))
        box(1, 30000, 1)
        popMatrix()
    }
}
*/