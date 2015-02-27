package cis642.aphidcounter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
    Button cancel_button;
    Button loadPhoto;
    Button take_photo;
    ImageView aphid_image;

    ImageConverter imageConverter;

    final int READ_REQUEST_CODE = 42;

    Uri imageUri;
    Bitmap bmConvertedImage;

    int aphidCount;

    ConvertPhotosTask task = null;

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

        cancel_button = (Button) findViewById(R.id.cancel_button);
        setCancelButtonListener();

        setLoadPhotoButtonListener();

//        take_photo = (Button) findViewById(R.id.examine_photo);
//        take_photo.setOnClickListener(
//            new ImageButton.OnClickListener(){
//                @Override
//                public void onClick(View view){
//                    Intent myIntent = new Intent(view.getContext(), TakePhotos.class);
//                    startActivityForResult(myIntent,0);
//                }
//            }
//        );
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

    /**
     * Set the event handler for the cancel button press.
     */
    private void setCancelButtonListener() {

        cancel_button.setOnClickListener(new ImageButton.OnClickListener(){
                                             @Override
                                             public void onClick(View view){

                                             }
                                         }
        );

    }

    /**
     * Set the event handler for the load photo button press.
     */
    private void setLoadPhotoButtonListener() {

        loadPhoto = (Button) findViewById(R.id.load_photo);

        loadPhoto.setOnClickListener(new ImageButton.OnClickListener(){
                                         @Override
                                         public void onClick(View view){
                                             Intent myIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                             myIntent.addCategory(Intent.CATEGORY_OPENABLE);
                                             myIntent.setType("image/jpg");

                                             startActivityForResult(myIntent, READ_REQUEST_CODE);
                                         }
                                     }
        );

    }


    /**
     * After selecting a photo from the file dialog, this method will be called.
     * @param requestCode
     * @param resultCode
     * @param resultData
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {

                loadPhoto.setClickable(false);

                imageUri = resultData.getData();
                task = new ConvertPhotosTask();
                task.execute(""); // have array of 4 tasks to do 4 images

                //uriToBitmap(resultData.getData());

            }
        }

    }

    /**
     *
     * @param uriData
     */
    private void uriToBitmap(Uri uriData) {

        Uri uri = uriData;
        Log.i("", "Uri: " + uri.toString());
        Mat source = new Mat();
        Bitmap bitmap = null;

        try {

            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

        } catch (Exception ex) { ex.printStackTrace(); }

        if (null != bitmap) {

            Utils.bitmapToMat(bitmap, source);

            convertLoadedImage(source);

        }

    }

    /**
     * Converts the loaded image matrix. Then sets the results to the screen.
     * @param source
     */
    private void convertLoadedImage(Mat source) {

        Mat convertedImage = new Mat();
        imageConverter = new ImageConverter();

        try {

            imageConverter.setSource(source);                       // Set the source Mat
            imageConverter.ConvertImage();                          // Convert the image Mat
            convertedImage = imageConverter.getConvertedImage();    // Get the converted Image Mat

            AphidCounter aphidCounter = new AphidCounter(convertedImage);
            Log.i("", "Counting aphids");
            aphidCount = aphidCounter.countAphid();
            Log.i("", "Aphid count: " + aphidCount);


            // Create a bitmap to store the converted image:
            bmConvertedImage = Bitmap.createBitmap(convertedImage.cols(),
                    convertedImage.rows(),
                    Bitmap.Config.ARGB_8888);

            Utils.matToBitmap(convertedImage, bmConvertedImage);    // Convert the Mat to bitmap

        } catch (Exception ex) { ex.printStackTrace(); }

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



    private class ConvertPhotosTask extends AsyncTask<String, Integer, String>
    {

        protected String doInBackground(String... strings)
        {

            if (!this.isCancelled())
                uriToBitmap(imageUri);

            return "Done";

        }

        protected void onProgressUpdate(Integer... progress)
        {

        }

        protected void onPostExecute(String strings)
        {

            Log.i("", "onPostExecute() called");
            loadPhoto.setClickable(true);

            // Get the TextView that will show the aphid count, & then append the aphid count to it.
            TextView tvAphidCount = (TextView) findViewById(R.id.aphid_count_text);
            tvAphidCount.append(" " + aphidCount);

            // Get the imageview of the pic shown on the app screen:
            ImageView ivAphidPic = (ImageView) findViewById(R.id.aphid_image);

            // Update the image shown on the app screen to the newly converted image:
            ivAphidPic.setImageBitmap(bmConvertedImage);

        }

    }

}
























