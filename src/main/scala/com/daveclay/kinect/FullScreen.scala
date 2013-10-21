package com.daveclay.kinect

import processing.core.PApplet

object FullScreen {

    def main(args: Array[String]) {
        // -Djava.libarary.path=/home/daveclay/work/processing-install/opengl.so.
        PApplet.main(Array("--present", "com.daveclay.kinect." + args(0)))
    }

}