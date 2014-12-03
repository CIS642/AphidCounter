package cis642.aphidcounter;


import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.io.File;

/**
 * Created by Staton on 9/25/2014.
 */
public class ImageConverter {

    /**
     * The source image (as a Matrix).
     */
    private Mat source;

    /**
     * The final converted image (as a Matrix).
     */
    private Mat convertedImage;

    /**
     * Constructs a new ImageConverter object.
     */
    public ImageConverter() {
        this.source = new Mat();
        this.convertedImage = new Mat();
    }

    /**
     * Sets the source image matrix.
     * @param source The source image's matrix.
     */

    public void setSource(Mat source) {
        this.source = source;
    }

    /**
     * Gets the source image matrix.
     * @return THe source image's matrix.
     */
    public Mat getSource() {
        return this.source;
    }

    /**
     * Gets the converted image's matrix.
     * @return The converted image's matrix.
     */
    public Mat getConvertedImage() {
        return this.convertedImage;
    }

    /**
     * Algorithm for converting the original color image to a black and white binary image.
     */
   /* public Mat convertToGrayScale(Mat source){
        Mat grayScaleImage = new Mat();
        Log.i("Process Trace","begining grayscale conversion");
        Imgproc.cvtColor(source, grayScaleImage, Imgproc.COLOR_RGB2GRAY);
        Log.i("Process Trace","done with grayscale conversion");
        return grayScaleImage;
    }*/

    private final double bgStrelConst_width = 0.01;
    private final double bgStrelConst_height = 0.01;
    public void ConvertImage() {
        //converts the image  to grayscale
        int bgStrel_width = (int) Math.round(source.width() * bgStrelConst_width);
        int bgStrel_height = (int) Math.round(source.width() * bgStrelConst_width);
        Log.i("Process Trace","begining grayscale conversion");
        Imgproc.cvtColor(source, convertedImage, Imgproc.COLOR_RGB2GRAY);
        Log.i("Process Trace","done with grayscale conversion");


        //adjust the image intensity/contrast
        Log.i("Process Trace","begining image intensity/contrast adjustment");
        //Imgproc.equalizeHist(convertedImage,convertedImage);
        convertedImage.convertTo(convertedImage, -1, 1.5, -100.0);
        Log.i("Process Trace","done with image intensity/contrast adjustment");

        //Removing Background
        //Creating background Strel
        Mat background = convertedImage.clone();
        Imgproc.medianBlur(background, background, 39);
        Log.i("Process Trace","begining to create backgroundStrel");
        Log.i("Process Trace","backgroundStrel created");
        Log.i("Process Trace","Eroding convertedImage against backgroundStrel");
        Imgproc.threshold(background,background,90,110,Imgproc.THRESH_BINARY);
        Imgproc.erode(background,background,Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(bgStrel_width,bgStrel_height)));
        Log.i("Process Trace","Final Background created");
        Log.i("Process Trace","begining to subtract background from image");
        Core.absdiff(convertedImage,background,convertedImage);
        Log.i("Process Trace","Background removed");

        //Imgproc.equalizeHist(convertedImage,convertedImage);// correction before applying filters

/**/


        //Imgproc.medianBlur(convertedImage, convertedImage, 3);
        //something
        //Log.i("Displaying Mat", convertedImage.);
        //ty to blur out the fine details fo creating the bg.
        //this will make a better mask for removing the bg
       //Mat bg = new Mat();
        //Imgproc.threshold(convertedImage,bg,90,110,Imgproc.THRESH_BINARY);
        //Core.absdiff(convertedImage,bg,convertedImage);

        //Removing Background and Noise


/*
        //Initial Contrast enhancement
        Imgproc.equalizeHist(convertedImage,convertedImage);

        //mockProcess: assuming bg and noise are removed
        Mat octagon = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_CROSS, new Size(210,210));
        Imgproc.dilate(convertedImage,convertedImage,octagon);
        Imgproc.floodFill(convertedImage,convertedImage,new Point(0,0),new Scalar(255,255,255));
        Imgproc.medianBlur(convertedImage,convertedImage,7);
        Imgproc.equalizeHist(convertedImage,convertedImage);
        Imgproc.floodFill(convertedImage,convertedImage,new Point(0,0),new Scalar(255,255,255));


        Imgproc.equalizeHist(convertedImage,convertedImage);
*/
    }

}
