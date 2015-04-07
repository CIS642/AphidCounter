package cis642.aphidcounter;

import android.app.Activity;
import android.content.Intent;
import android.database.CursorJoiner;
import android.graphics.Bitmap;
import android.graphics.Interpolator;
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
public class MainActivity extends ActionBarActivity {//implements View.OnClickListener {

    private Button testing_process, cancel_button, loadPhoto, take_photo;
    private ImageView aphid_image;

    private ImageConverter imageConverter;

    private final int READ_REQUEST_CODE = 42;

    private Uri imageUri;
    private Bitmap bitmap, bmConvertedImage;
    private Mat source, convertedImage;

    private int aphidCount;

    private ConvertPhotosTask task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }


        //testing_process = (Button) findViewById(R.id.testing_button);
        //testing_process.setOnClickListener(this);

        cancel_button = (Button) findViewById(R.id.cancel_button);
        setCancelButtonListener();

        setLoadPhotoButtonListener();

        imageConverter = new ImageConverter();

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
                                                 Log.i("MainActivity", "cancelled pressed");
                                                 cancel_button.setEnabled(false);
                                                 cancel_button.setText("Cancelling...");
                                                 imageConverter.setCancel(true);
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

                TextView tvAphidCount = (TextView) findViewById(R.id.aphid_count_text);
                tvAphidCount.setText("Aphid Count: ");

                loadPhoto.setEnabled(false);
                cancel_button.setEnabled(true);
                imageUri = resultData.getData();
                //uriToBitmap(imageUri);

//                if (null != bitmap) {
//
//                    Mat source = new Mat();
//
//                    Utils.bitmapToMat(bitmap, source);
//
//                    convertLoadedImage(source);
//
//                }

                task = new ConvertPhotosTask();
                task.execute(imageUri); // have array of 4 tasks to do 4 images

            }
        }

    }

    /**
     *
     * @param uriData
     */
    private void uriToBitmap(Uri uriData) {

        Log.i("", "Uri: " + uriData.toString());

        try {

            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriData);

        } catch (Exception ex) { ex.printStackTrace(); }

    }

    /**
     * Converts the loaded image matrix. Then sets the results to the screen.
     * @param source
     */
    private void convertLoadedImage(Mat source) {

        try {

            imageConverter = new ImageConverter();
            imageConverter.setSource(source);                       // Set the source Mat
            imageConverter.ConvertImage();                          // Convert the image Mat

            if (!imageConverter.getCancel())    // if the conversion was not cancelled
            {
                convertedImage = imageConverter.getConvertedImage();    // Get the converted Image Mat

                AphidCounter aphidCounter = new AphidCounter(convertedImage);
                Log.i("MainActivity", "Counting aphids");
                aphidCount = aphidCounter.countAphid();
                Log.i("MainActivity", "Aphid count: " + aphidCount);


                // Create a bitmap to store the converted image:
                bmConvertedImage = Bitmap.createBitmap(convertedImage.cols(),
                        convertedImage.rows(),
                        Bitmap.Config.ARGB_8888);

                Utils.matToBitmap(convertedImage, bmConvertedImage);    // Convert the Mat to bitmap
            }

        } catch (Exception ex) { ex.printStackTrace(); }

    }


//    @Override
//    public void onClick(View view) {
//        Log.i("PROCESS TRACE", "Doing image processing");
//
//        source = new Mat();
//        convertedImage = new Mat();
//        imageConverter = new ImageConverter();
//        AphidCounter aphidCounter;
//
//        try {
//            // Load the image resource as a Mat:
//            source = Utils.loadResource(MainActivity.this, R.drawable.test_img2_small);
//
//            imageConverter.setSource(source);                       // Set the source Mat
//            imageConverter.ConvertImage();                          // Convert the image Mat
//            convertedImage = imageConverter.getConvertedImage();    // Get the converted Image Mat
//
//            // Create a bitmap to store the converted image:
//            Bitmap bmConvertedImage = Bitmap.createBitmap(convertedImage.cols(), convertedImage.rows(), Bitmap.Config.ARGB_8888);
//            aphidCounter = new AphidCounter(convertedImage);
//            Log.i("Average AphidCount: ", Integer.toString(aphidCounter.countAphid()));
//            Utils.matToBitmap(convertedImage, bmConvertedImage);    // Convert the Mat to bitmap
//
//            // Get the imageview of the pic shown on the app screen:
//            ImageView ivAphidPic = (ImageView) findViewById(R.id.aphid_image);
//
//            // Update the image shown on the app screen to the newly converted image:
//            ivAphidPic.setImageBitmap(bmConvertedImage);
//
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//    }



    private class ConvertPhotosTask extends AsyncTask<Uri, Integer, String> {

        protected String doInBackground(Uri... uris) {

            try { uriToBitmap(uris[0]); } catch (Exception ex){ }

            if (null != bitmap) {

                Mat source = new Mat();

                Utils.bitmapToMat(bitmap, source);

                convertLoadedImage(source);

            }

            Log.i("doinbackground", "cancelled??: " + imageConverter.getCancel());

            if (imageConverter.getCancel())
                return "Cancelled";
            return "Done";

        }


        protected void onProgressUpdate(Integer... progress) {

        }


        protected void onPostExecute(String strings) {

            Log.i("onpostexecute", "onPostExecute() called");
            cancel_button.setEnabled(false);
            loadPhoto.setEnabled(true);

            if (strings.equals("Cancelled")) {

                Log.i("onpostexecute", "Cancelled");
                cancel_button.setText("Cancel");
                source = null;
                bitmap = null;
                imageUri = null;
                imageConverter = null;
                finish();

            } else {
                Log.i("onpostexecute", strings);
                // Get the TextView that will show the aphid count, & then append the aphid count to it.
                TextView tvAphidCount = (TextView) findViewById(R.id.aphid_count_text);
                tvAphidCount.setText("Aphid Count: " + aphidCount);

                // Get the imageview of the pic shown on the app screen:
                ImageView ivAphidPic = (ImageView) findViewById(R.id.aphid_image);

                // Update the image shown on the app screen to the newly converted image:
                ivAphidPic.setImageBitmap(bmConvertedImage);

                bmConvertedImage = null;
                convertedImage = null;
                source = null;
                bitmap = null;
                imageUri = null;
                imageConverter = null;

            }

        }

    }

}
























