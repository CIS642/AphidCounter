package cis642.aphidcounter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.*;

import cis642.aphidcounter.activity.TakePhotos;
import cis642.aphidcounter.util.AphidCounter;

import static cis642.aphidcounter.R.drawable.ic_launcher;

//THIS"LL B FOR TESTNG PURPOSES
public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    Button testing_process;
    Button take_photo;
    ImageView aphid_image;
    ImageConverter imageConverter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
        /**
         * Initialize the Take Picture button.
         */

        testing_process = (Button) findViewById(R.id.testing_button);
        testing_process.setOnClickListener(this);

        take_photo = (Button) findViewById(R.id.examine_photo);
        take_photo.setOnClickListener(
            new ImageButton.OnClickListener(){
                @Override
                public void onClick(View view){
                    Intent myIntent = new Intent(view.getContext(), TakePhotos.class);
                    startActivityForResult(myIntent,0);
                }
            }
        );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onClick(View view) {
        Log.i("PROCESS TRACE", "Doing image processing");

        Mat source = new Mat();
        Mat convertedImage = new Mat();

        imageConverter = new ImageConverter();
        AphidCounter aphidCounter;

        try {
            // Load the image resource as a Mat:
            source = Utils.loadResource(MainActivity.this, R.drawable.test_img2_small);

            imageConverter.setSource(source);                       // Set the source Mat
            imageConverter.ConvertImage();                          // Convert the image Mat
            convertedImage = imageConverter.getConvertedImage();    // Get the converted Image Mat

            // Create a bitmap to store the converted image:
            Bitmap bmConvertedImage = Bitmap.createBitmap(convertedImage.cols(),
                    convertedImage.rows(),
                    Bitmap.Config.ARGB_8888);
            aphidCounter = new AphidCounter(convertedImage);
            Log.i("Average AphidCount: ", Integer.toString(aphidCounter.countAphid()));
            Utils.matToBitmap(convertedImage, bmConvertedImage);    // Convert the Mat to bitmap

            // Get the imageview of the pic shown on the app screen:
            ImageView ivAphidPic = (ImageView) findViewById(R.id.aphid_image);

            // Update the image shown on the app screen to the newly converted image:
            ivAphidPic.setImageBitmap(bmConvertedImage);

        } catch(Exception e){
            e.printStackTrace();
        }
    }

}
























