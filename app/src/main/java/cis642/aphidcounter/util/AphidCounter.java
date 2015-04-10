package cis642.aphidcounter.util;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by japshvincent on 12/2/2014.
 */
public class AphidCounter {
    private Mat processedImage;

    public AphidCounter(Mat processedImage){
        setProcessedImage(processedImage);
    }
    public void setProcessedImage(Mat processedImage){
        this.processedImage = processedImage;
    }
    public Mat getProcessedImage(){
        return processedImage;
    }
    public int countAphid(){
        if(processedImage == null){
            return 0;
        }
        Mat hierarchy = processedImage.clone();
        Imgproc.findContours ( processedImage, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE );
        int counter = 0;
        for(int i = 0 ; i < contours.size(); i ++){
            if(Imgproc.contourArea(contours.get(i)) > 1.5) {
                Rect rect = Imgproc.boundingRect(contours.get(i));
                double rectArea = rect.width * rect.height;
                double contourRatio = Math.min(rect.width * 1.0,rect.height * 1.0)/Math.max(rect.width * 1.0,rect.height * 1.0);
                if (rectArea < 200 && rectArea > 5 && rect.width < 20 && rect.height < 20 && contourRatio > 0.7){
                    counter++;
                }
            }
        }
        return counter;
    }
    private  List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

}