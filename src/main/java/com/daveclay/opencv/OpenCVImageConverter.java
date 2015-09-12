package com.daveclay.opencv;

import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.IOException;

public class OpenCVImageConverter {

    /*
     * Converts/writes a Mat into a BufferedImage.
     *
     * @param matrix Mat of type CV_8UC3 or CV_8UC1
     * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
     */
    public static BufferedImage toBufferedImage(Mat matrix) throws IOException {
        int size = (int) (matrix.total() * matrix.channels());
        byte[] temp = new byte[size];
        matrix.get(0, 0, temp);

        int width = matrix.width();
        int height = matrix.height();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        DataBuffer buffer = new DataBufferByte(temp, temp.length);
        SampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, width, height, 3, width*3, new int[]{2,1,0});
        Raster raster = Raster.createRaster(sampleModel, buffer, null);
        image.setData(raster);

        return image;
    }

}
