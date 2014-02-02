package com.daveclay.processing.kinect.api;

import SimpleOpenNI.SimpleOpenNI;

/**
 _newUserMethod          = getMethodRef(obj,"onNewUser",new Class[] { SimpleOpenNI.class,int.class });
 _lostUserMethod         = getMethodRef(obj,"onLostUser",new Class[] { SimpleOpenNI.class,int.class });
 _outOfSceneUserMethod   = getMethodRef(obj,"onOutOfSceneUser",new Class[] { SimpleOpenNI.class,int.class });
 _visibleUserMethod      = getMethodRef(obj,"onVisibleUser",new Class[] { SimpleOpenNI.class,int.class });
 */
public interface UserListener {
    public void onNewUser(SimpleOpenNI curContext, int userId);
    public void onLostUser(SimpleOpenNI kinect, int userId);
}
