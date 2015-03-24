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
// return this.aphidCount;
//}
    /**
     * Gets the name of the converted file.
     * @return
     */
    public String GetConvertedFileName()
    {
        return this.convertedFile;
    }

    public boolean ConvertImage() {
        boolean success = false;
        int width, height;
        double resaled_widthFactor = 1.0;
        double rescaled_heightFactor = 1.0;
        Mat resizedImage;
        width = source.width();
        height = source.height();
        resizedImage = new Mat();
        if(width > 1200 || height > 1200){
            Imgproc.resize(source,resizedImage,new Size(1000, 850), 0, 0,Imgproc.INTER_NEAREST);
            resaled_widthFactor = resizedImage.width()/(source.width()*1.0);
            rescaled_heightFactor = resizedImage.height()/(source.height() * 1.0);
            width = (int)(width * resaled_widthFactor);
            height = (int) (height * rescaled_heightFactor);
            source = resizedImage;
        }
        Mat matDiskStrel10 = createDisk(diskStrel10);
        Mat matDiskStrel25 = createDisk(diskStrel25);

        Mat grayScaled = new Mat();
        grayScaled = grayScalConversion(source);
        Mat J4 = new Mat();
        J4 = imadjust(grayScaled);
        Mat background = new Mat();
        int strelSize = (int)(450 * resaled_widthFactor);
        Mat strel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(strelSize,strelSize));
        background = imopen(grayScaled,strel);
        Mat I2 = new Mat();
        Core.absdiff(J4,background,I2);
        Log.i("IMGocnv.main","removed bg");
        int strelFactorOfThree = (int) (Math.ceil(70.0 * resaled_widthFactor));
        strelSize = 3 * strelFactorOfThree;
        strelSize = (int)(210 * resaled_widthFactor);
        Mat octaStrel = octagonStrel(strelSize);
        Mat I3 = new Mat();
        I3 = imtophat(I2, octaStrel);
        Mat I4 = new Mat();
        I4 = imfill(I3);
        Mat M1 = new Mat();
        Imgproc.medianBlur(I4,M1,3);
        Mat I5 = new Mat();
        I5 = imfill(M1);
        Mat I6 = new Mat();
        strelSize = (int)(45 * resaled_widthFactor);
        strel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,new Size(strelSize,strelSize));
        I6 = imtophat(I5, strel);
        Mat I7 = new Mat();
        I7 = imfill(I6);
        Mat M2 = new Mat();
        Imgproc.medianBlur(I7,M2,4);
        Mat I8 = new Mat();
        I8 = imfill(M2);
        Mat I9 = new Mat();
        strelSize = (int)(10 * resaled_widthFactor);
        Log.i("STRELSIZE:", String.valueOf(strelSize));
        if(strelSize < 3){
            strelSize = 5;
        }
        //strel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE, new Size(strelSize,strelSize));
        I9 = imtophat(I8,matDiskStrel10);
        Mat M3 = new Mat();
        M3 = imfill(I9);
        Mat I10 = new Mat();
        strelSize = (int)(25 * resaled_widthFactor);
        //strel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_ELLIPSE, new Size(strelSize,strelSize));
        I10 = imtophat(M3,matDiskStrel25);
        Mat I11 = new Mat();
        I11 = imfill(I10);
        Mat I12 = new Mat();
        I12 = imadjust(I11);

        Mat heirarchy = new Mat();
        convertedImage = findEdges(I12);
        convertedFile = "c_" + sourceFile;
        String convertedImgFile = fileManager.GetConvertedPhotosDirectory() + File.separator + convertedFile;

        try {
            Highgui.imwrite(convertedImgFile, convertedImage);
                //aphidCount = 0;
            success = true;
        } catch (Exception ex) { ex.printStackTrace(); }
        return success;
    }
    public static Mat grayScalConversion(Mat src){
        Mat grayScaled = new Mat();
        Imgproc.cvtColor(src,grayScaled,Imgproc.COLOR_RGB2GRAY);
        return grayScaled;
    }
    public static Mat imadjust(Mat src){
        Mat imadjusted = src.clone();
        imadjusted.convertTo(imadjusted,-1, 1.0);
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
        Core.absdiff(src,imopened,topHatted);
        return topHatted;
    }
    public static Mat imfill(Mat src){
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat herarchy = new Mat();
        Mat contourMat = src.clone();
        Imgproc.findContours(contourMat, contours,herarchy, Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);

        for(int i=0; i< contours.size();i++){
            //System.out.println(Imgproc.contourArea(contours.get(i)));
            if (Imgproc.contourArea(contours.get(i)) < 230 ){
                Rect rect = Imgproc.boundingRect(contours.get(i));
                //System.out.println(rect.height);
                if (rect.height > 15 && rect.width > 15){
                    Scalar scalar = new Scalar(src.get((int)rect.y + rect.width/2,(int)rect.x+rect.height/2));
                    //Core.rectangle(src, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),scalar);
                    Imgproc.floodFill(src,new Mat(), new Point(rect.x+(rect.width/2),rect.y+(rect.height/2)),scalar);
                }
            }
        }
        return src;
    }

    public static Mat findingCoutours(Mat src) {
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(src, contours, new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
        for(int i=0; i< contours.size();i++){
            System.out.println(Imgproc.contourArea(contours.get(i)));
           // if (Imgproc.contourArea(contours.get(i)) > 50 ){
                Rect rect = Imgproc.boundingRect(contours.get(i));
                System.out.println(rect.height);
                //if (rect.height > 28){
                    Core.rectangle(src, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(0,0,0));
                //}
            //}
        }
        return src;
    }
    public static Mat findEdges(Mat src){
        Mat mask = new Mat();
        Imgproc.Canny(src,mask,100,200);
        return mask;
    }


    public static Mat octagonStrel(int size){
        Mat octaStrel = new Mat();
        int dim = 1 + (size *2);
        Mat strel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(dim,dim));
        octaStrel = rotate(strel);
        return octaStrel;
    }

    public static Mat createDisk(int[][] arrayOfValues){
        Mat matObject = new Mat();
        matObject.create( arrayOfValues.length, arrayOfValues[0].length, CvType.CV_8UC1 ); // 8-bit single channel image
        System.out.println(arrayOfValues.length);
        for(int i = 0 ; i < arrayOfValues.length ; i ++){
            for(int j = 0 ; j < arrayOfValues[i].length ; j++){
                matObject.put(i,j,arrayOfValues[i][j]);
            }
        }
        return matObject;
    }

    public static Mat createLine(int size, int flag){//0 for vertical, 1 for horizontal
        Mat strel = new Mat();
        if(flag == 0)
            Imgproc.getStructuringElement(Imgproc.CV_SHAPE_CROSS, new Size(1,size));
        else
            Imgproc.getStructuringElement(Imgproc.CV_SHAPE_CROSS, new Size(size,1));

        return strel;
    }


    private static Mat rotate(Mat src){
        Point pt = new Point(src.width()/2,src.height()/2);
        Mat r = new Mat();
        r = Imgproc.getRotationMatrix2D(pt,45.0,1.0);
        Mat rotatedImage = new Mat();
        Imgproc.warpAffine(src,rotatedImage,r,new Size(src.width(),src.height()));
        return rotatedImage;
    }

    private static void displayMat(Mat mat){
        for(int i = 0 ; i < mat.width(); i ++){
            String line = "";
            for(int j = 0 ; j < mat.height() ; j ++){
                line += String.valueOf((int)mat.get(i,j)[0]) + " ";
            }
        }
    }
    private static int [][] diskStrel10 =
            {
                    {0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0},
                    {0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0},
                    {0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0},
                    {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
                    {0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0},
                    {0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0},
                    {0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0}
            };

    private static int[][] diskStrel25 =
            {
                    {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0},
                    {0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0},
                    {0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0},
                    {0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0},
                    {0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0},
                    {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
                    {0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0},
                    {0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0},
                    {0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0},
                    {0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0},
                    {0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0}

            };
}