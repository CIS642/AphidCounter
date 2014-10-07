package cis642.aphidcounter;


import android.graphics.Matrix;
import android.os.Environment;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

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
    public void ConvertImage() {
        Imgproc.cvtColor(source, convertedImage, Imgproc.COLOR_RGB2GRAY);
        convertedImage.convertTo(convertedImage, -1, 1.0, -50.0);
        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE,new Size(5,5));
        Imgproc.dilate(convertedImage,convertedImage,element);
        Imgproc.medianBlur(convertedImage,convertedImage, 5);
    }

}
