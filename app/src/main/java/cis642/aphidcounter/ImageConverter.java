package cis642.aphidcounter;


import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cis642.aphidcounter.manager.FileManager;

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
     * Filename of the source image.
     */
    private String sourceFile;

    /**
     * Filename of the converted image.
     */
    private String convertedFile;

    /**
     * For managing files.
     */
    private FileManager fileManager = new FileManager();

    /**
     * Number of aphids found on this photo.
     */
    //private int aphidCount = 0;

    /**
     * Constructs a new ImageConverter object.
     */
    public ImageConverter() {
        this.source = new Mat();
        this.convertedImage = new Mat();
    }

    public ImageConverter(String sourceFile)
    {
        this.source = new Mat();
        this.convertedImage = new Mat();
        this.sourceFile = sourceFile;

        source = Highgui.imread(fileManager.GetPhotosDirectory() + File.separator + sourceFile);
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
     * Get the number of aphids found on this photo.
     * @return
     */
    //public int GetAphidCount()
    //{
    //    return this.aphidCount;
    //}

    /**
     * Gets the name of the converted file.
     * @return
     */
    public String GetConvertedFileName()
    {
        return this.convertedFile;
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


    private final double bgStrelConst_width = 0.02;
    private final double bgStrelConst_height = 0.02;

    public boolean ConvertImage() {
        boolean success = false;
        //Getting image variables to be used with respect to image size used.
        int width = source.width();
        int height = source.height();
        int bgStrel_width = (int) Math.round(width * bgStrelConst_width);
        int bgStrel_height = (int) Math.round(height * bgStrelConst_height);


        //converts the image to grayscale
        Log.i("Process Trace", "begining grayscale conversion");
        Imgproc.cvtColor(source, convertedImage, Imgproc.COLOR_RGB2GRAY);
        Log.i("Process Trace","done with grayscale conversion");


        //adjust the image intensity/contrast
        Log.i("Process Trace","begining image intensity/contrast adjustment");
        convertedImage.convertTo(convertedImage, -1, 1.0, -100.0);
        Log.i("Process Trace","done with image intensity/contrast adjustment");
        //Removing Background
        //Creating background Strel
        Mat imageBg = convertedImage.clone();
        Imgproc.medianBlur(imageBg, imageBg, 21);//blur out aphids in bg
        //Imgproc.threshold(imageBg,imageBg,90,110,Imgproc.THRESH_BINARY);
        // Imgproc.erode(imageBg,imageBg,Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(bgStrel_width,bgStrel_height)));


        Core.absdiff(convertedImage, imageBg, convertedImage);
        //dont use equalize find alternative for fixing contrast

        //convertedImage.convertTo(convertedImage,-1,1.0, -100);

        Mat round = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE, new Size(4,4));
        Imgproc.dilate(convertedImage,convertedImage,round);

        convertedFile = "c_" + sourceFile;

        String convertedImgFile = fileManager.GetConvertedPhotosDirectory() + File.separator + convertedFile;

        try {
            Highgui.imwrite(convertedImgFile, convertedImage);
            //aphidCount = 0;

            success = true;
        } catch (Exception ex) { ex.printStackTrace(); }

        return success;
    }


}
