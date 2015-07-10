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

* Working on multi-user version.
* Stage could support multi-user, but the user-based events would need to be refactored out from the stage size.
* StageMonitor for each user line up at the bottom with background colors matching the user?
* Shade the outline of the user with that user's color. Or large circles at their hand and head positions with those colors.
* Has a stupid name
* Recognizes only one person at a time for the moment (no particular reason it can't do more).
* I should probably split up the kinect-focused stuff from the other processing sketches.
* a "Z" gesture would probably work fine.