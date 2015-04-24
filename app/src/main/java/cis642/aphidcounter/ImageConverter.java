package cis642.aphidcounter;


import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cis642.aphidcounter.manager.FileManager;
import cis642.aphidcounter.util.Constants;

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
     * Flag that is set when the user wishes to stop converting.
     */
    private static boolean cancelConversion = false;
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
     * Sets the cancel flag when the user wishes to stop converting.
     * @param cancel
     */
    public void setCancel(boolean cancel) { this.cancelConversion = cancel; }
    /**
     * Gets the cancellation flag's status.
     * @return
     */
    public boolean getCancel() { return this.cancelConversion; }
    /**
     * Gets the name of the converted file.
     * @return
     */
    public String GetConvertedFileName()
    {
        return this.convertedFile;
    }

    public boolean ConvertImage() {
        //Disk Structured Element Creation.
        Mat matDiskStrel10 = createDisk(Constants.DISKSTRELARRAY_10);
        Mat matDiskStrel25 = createDisk(Constants.DISKSTRELARRAY_25);

        //Image conversion success flag
        boolean success = false;

        //Image resizing for uniformity across different image size input
        Mat resizedImage = new Mat();
        if(source.width() > source.height())
            Imgproc.resize(source,resizedImage,new Size(1000, 850), 0, 0,Imgproc.INTER_NEAREST);
        else
            Imgproc.resize(source,resizedImage,new Size(850, 1000), 0, 0,Imgproc.INTER_NEAREST);
        source = resizedImage;


        Mat I1 = new Mat();
        Log.d(Constants.CONVERSION_STATUS, "Step 1: Converting to GrayScale");
        I1 = grayScalConversion(source);


        Mat J4 = new Mat();
        Log.i(Constants.CONVERSION_STATUS, "Step 2: Adjusting Image Contrast");
        J4 = imadjust(I1,true);


        Mat background = new Mat();
        int bgStrelSize = 450;
        Mat strel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(bgStrelSize,bgStrelSize));
        Log.i(Constants.CONVERSION_STATUS, "Step 3: Creating Morphed Bacground from GrayScaled");
        background = imopen(I1,strel);


        Mat I2 = new Mat();
        Log.i(Constants.CONVERSION_STATUS, "Step 4: Subtracting Morphed Background from Adjusted GrayScaled");
        Core.subtract(J4, background, I2);


        Mat J3 = new Mat();
        Log.i(Constants.CONVERSION_STATUS, "Step 5: Adjusting Contrast");
        J3 = imadjust(I2,false);

        int octStrelSize = 210;
        Mat octaStrel = octagonStrel(octStrelSize);
        Mat I3 = new Mat();
        Log.i(Constants.CONVERSION_STATUS, "Step 6: Tophat morphing using octagon of size 210");
        I3 = imtophat(J3, octaStrel);


        Mat I4 = new Mat();
        Log.i(Constants.CONVERSION_STATUS, "Step 7: Filling Holes");
        I4 = imfill(I3,false);


        Mat M1 = new Mat();
        Log.i(Constants.CONVERSION_STATUS, "Step 8: Median Blur kernel size: 3");
        Imgproc.medianBlur(I4,M1,3);


        Mat I5 = new Mat();
        Log.i(Constants.CONVERSION_STATUS, "Step 9: Filling Holes");
        I5 = imfill(M1,false);


        Mat I6 = new Mat();
        int rectStrelSize = 45;
        strel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,new Size(rectStrelSize,rectStrelSize));
        Log.i(Constants.CONVERSION_STATUS, "Step 10: Tophat morphing using square of size 45");
        I6 = imtophat(I5, strel);


        Mat I7 = new Mat();
        Log.i(Constants.CONVERSION_STATUS, "Step 11: Filling Holes");
        I7 = imfill(I6,false);


        Mat M2 = new Mat();
        Log.i(Constants.CONVERSION_STATUS, "Step 12: Median Blur kernel size: 3");
        Imgproc.medianBlur(I7,M2,3);


        Mat I8 = new Mat();
        Log.i(Constants.CONVERSION_STATUS, "Step 13: Filling Holes");
        I8 = imfill(M2,false);


        Mat I9 = new Mat();
        Log.i(Constants.CONVERSION_STATUS, "Step 14: Tophat morphing using Disk of size 10");
        I9 = imtophat(I8,matDiskStrel10);


        Mat M3 = new Mat();
        Log.i(Constants.CONVERSION_STATUS, "Step 15: Median Blur kernel size: 1");
        Imgproc.medianBlur(I9, M3, 3);


        Mat I10 = new Mat();
        Log.i(Constants.CONVERSION_STATUS, "Step 17: Tophat morphing using Disk of size 25");
        I10 = imtophat(I9,matDiskStrel25);


        Mat I11 = new Mat();
        Log.i(Constants.CONVERSION_STATUS, "Step 18: Filling Holes");
        I11 = imfill(I10,false);

        Mat I12 = new Mat();
        Log.i(Constants.CONVERSION_STATUS, "Step 19: Adjusting Contrast");
        I12 = imadjust(I11,false);


        Mat BWs1 = new Mat();
        Log.i(Constants.CONVERSION_STATUS, "Step 20: Creating Mat of edges");
        Imgproc.Canny(I12.clone(),BWs1,100,200);

        Mat edges = new Mat();
        Log.i(Constants.CONVERSION_STATUS, "Step 21: Filling Holes of Mat of edges, Final hole fill");
        edges = imfill(BWs1,true);

        Mat BWsdil = new Mat();
        Mat lineStrel1 = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_CROSS, new Size(2,1));
        Mat lineStrel2 = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_CROSS, new Size(1,2));
        Log.i(Constants.CONVERSION_STATUS, "Step 22: Removing Lines");
        Imgproc.erode(edges,BWsdil, lineStrel1);
        Imgproc.erode(BWsdil.clone(), BWsdil, lineStrel2);

        convertedImage = BWsdil;
        convertedFile = "c_" + sourceFile;
        String convertedImgFile = fileManager.GetConvertedPhotosDirectory() + File.separator + convertedFile;

        try {
            Highgui.imwrite(convertedImgFile, convertedImage);
            success = true;
        } catch (Exception ex) { ex.printStackTrace(); }
        return success;
    }

    public static Mat createDisk(int[][] arrayOfValues){
        Mat matObject = new Mat();
        matObject.create( arrayOfValues.length, arrayOfValues[0].length, CvType.CV_8UC1 );
        for(int i = 0 ; i < arrayOfValues.length ; i ++){
            for(int j = 0 ; j < arrayOfValues[i].length ; j++){
                matObject.put(i,j,arrayOfValues[i][j]);
            }
        }
        return matObject;
    }

    public static void displayMatInConsole(Mat mat){
        for(int i = 0 ; i < mat.height(); i ++){
            for(int j = 0 ; j < mat.width(); j ++){
                System.out.print(mat.get(i, j)[0] + " ");
            }
            System.out.println("\n");
        }
    }

    public static Mat grayScalConversion(Mat src){
        Mat grayScaled = new Mat();
        Imgproc.cvtColor(src,grayScaled,Imgproc.COLOR_RGB2GRAY);
        return grayScaled;
    }

    public static Mat imadjust(Mat src, boolean flag){
        Mat imadjusted = src.clone();
        Core.MinMaxLocResult res = Core.minMaxLoc(src.clone());
        if(flag)
            src.clone().convertTo(imadjusted, -1,1.5,-120);
        else
            src.clone().convertTo(imadjusted, -1,1.15);
        return imadjusted;
    }
    public static Mat imopen(Mat src, Mat strel){
        Mat dst = new Mat();
        Imgproc.erode(src,dst,strel);
        Mat dst2 = new Mat();
        Imgproc.dilate(dst, dst2, strel);
        return dst2;
    }
    public static Mat imtophat(Mat src,Mat strel){
        Mat topHatted = new Mat();
        Mat imopened = new Mat();
        imopened = imopen(src,strel);
        Core.subtract(src,imopened,topHatted);
        return topHatted;
    }
    public static Mat imfill(Mat src, boolean isFinal){
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat herarchy = new Mat();
        Mat contourMask = src.clone();
        Imgproc.findContours(src.clone(), contours,herarchy, Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
        for(int i=0; i< contours.size();i++){
            if (Imgproc.contourArea(contours.get(i)) > 3 ){
                Rect rect = Imgproc.boundingRect(contours.get(i));
                int rectArea = rect.width * rect.height;
                if (rectArea < 250 && rectArea > 5 && (rect.width < 50 && rect.height < 50)){
                    Scalar scalar = new Scalar(src.get((int)rect.y + rect.width/2,(int)rect.x+rect.height/2));
                    if(isFinal) scalar = new Scalar(255,255,255);
                    List<MatOfPoint> tempContour = new ArrayList<MatOfPoint>();
                    tempContour.add(contours.get(i));
                    Imgproc.drawContours(contourMask,tempContour,-1, scalar,-1);
                }
            }
        }

        return contourMask;
    }

    public static Mat octagonStrel(int size){
        Mat octaStrel = new Mat();
        int dim = 1 + (size *2);
        Mat strel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(dim,dim));
        octaStrel = rotate(strel);
        return octaStrel;
    }


    private static Mat rotate(Mat src){
        Point pt = new Point(src.width()/2,src.height()/2);
        Mat r = new Mat();
        r = Imgproc.getRotationMatrix2D(pt,45.0,1.0);
        Mat rotatedImage = new Mat();
        Imgproc.warpAffine(src,rotatedImage,r,new Size(src.width(),src.height()));
        return rotatedImage;
    }
}