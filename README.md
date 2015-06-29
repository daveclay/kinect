# README #

### Setup and Compiling ###

* Download Processing from http://www.processing.org
* Add the core.jar lib and the, uh, modes/lib/video/library/whatever-is-in-here.jar
* Download https://github.com/ThomasLengeling/KinectPV2
* Add the jar/ddls in library to the project
* Install the Windows Kinect v2.0 SDK from https://www.microsoft.com/en-us/download/details.aspx?id=44561

### Running BodyLocator ###

* Plug in the kinect
* Run BodyLocator
* Open a web browser (probably Chrome)
* Open src/main/webapp/index.html

### TODO BodyLocator ###

* Has a stupid name
* Recognizes only one person at a time for the moment (no particular reason it can't do more).
* Circle gesture is counter-clockwise - Kinect V2 is not mirrored like the original Kinect SimpleOpenNI was.
* Records the wrong hand - same reason as above. Gonna fix that like, today, so this is probably already out of date.
* I should probably split up the kinect-focused stuff from the other processing sketches.