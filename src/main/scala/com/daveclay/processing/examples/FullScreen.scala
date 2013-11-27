package com.daveclay.processing.examples

import processing.core.PApplet

object FullScreen {

    def main(args: Array[String]) {
        // http://www.slideshare.net/eskimoblood/processing-in-intellij
        // -Djava.libarary.path=/home/daveclay/work/processing-install/opengl.so.
        PApplet.main(Array("--present", "com.daveclay.processing." + args(0)))
    }

}