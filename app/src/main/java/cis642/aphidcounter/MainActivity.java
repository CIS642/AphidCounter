package cis642.aphidcounter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
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

import cis642.aphidcounter.util.AphidCounter;


/**
 * This class is used just for testing purposes.
 * It allows the user to manually select an image stored on their phone and convert it.
 * It will then display the aphid count and the time it took to convert the image.
 */
public class MainActivity extends Activity {//ActionBarActivity {//implements View.OnClickListener {

    private Button loadPhoto, goBack;
    private ImageView ivAphidPic, ivAphidPicConverted;
    private TextView tvAphidCount, tvConversionTime;

    private ImageConverter imageConverter;
    private AphidCounter aphidCounter;

    private final int READ_REQUEST_CODE = 42;

    private Uri imageUri;
    private Bitmap bitmap, bmConvertedImage;
    private Mat source, convertedImage;

    private int aphidCount;
    private long startConvertingTime, endConvertingTime;
    private double totalConversionTime;

    private ConvertPhotosTask task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }

        initializeViews();

        setLoadPhotoButtonListener();

        SetBackButtonListener();

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

    // disable back button press on this activity.
    @Override
    public void onBackPressed() {
    }

    /**
     * Initializes the Views (Buttons, text, etc.) when the activity first loads.
     */
    private void initializeViews() {
        goBack = (Button) findViewById(R.id.goBack);
        loadPhoto = (Button) findViewById(R.id.load_photo);
        tvAphidCount = (TextView) findViewById(R.id.aphid_count_text);
        tvConversionTime = (TextView) findViewById(R.id.conversion_time);
        ivAphidPic = (ImageView) findViewById(R.id.aphid_image);
        ivAphidPicConverted = (ImageView) findViewById(R.id.aphid_image_after);
    }

    /**
     * Set the event handler for the 'Back' button click.
     */
    private void SetBackButtonListener() {
        goBack = (Button) findViewById(R.id.goBack);

        goBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                task = null;
                finish();
            }

        });
    }

    /**
     * Set the event handler for the load photo button press.
     */
    private void setLoadPhotoButtonListener() {

        loadPhoto.setOnClickListener(new ImageButton.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
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
     *
     * @param requestCode
     * @param resultCode
     * @param resultData
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {

                imageUri = resultData.getData();

                // Disable the back button, so the user can't press this during conversion.
                goBack.setEnabled(false);

                // Hide the load photo button.
                loadPhoto.setEnabled(false);
                loadPhoto.setVisibility(View.GONE);

                // "Reset" these text views incase it contains data from a previous converted image.
                tvAphidCount.setText("Aphid Count: ");
                tvConversionTime.setText("Time: ");

                // Display the image that was selected:
                ivAphidPic.setImageURI(imageUri);

                // Clear out the converted image.
                ivAphidPicConverted.setImageResource(android.R.color.transparent);

                task = null;
                task = new ConvertPhotosTask();
                task.execute(imageUri);         // Start converting the image on a new thread.

            }
        }

    }

    /**
     * Convert the URI data to a bitmap.
     *
     * @param uriData
     */
    private void uriToBitmap(Uri uriData) {

        Log.i("", "Uri: " + uriData.toString());

        try {

            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriData);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Converts the loaded image matrix. Then sets the results to the screen.
     *
     * @param source
     */
    private void convertLoadedImage(Mat source) {

        try {

            imageConverter = new ImageConverter();
            imageConverter.setSource(source);   // Set the source Mat
            imageConverter.ConvertImage();      // Convert the image Mat

            // Get the converted image mat:
            convertedImage = imageConverter.getConvertedImage();

            aphidCounter = new AphidCounter(convertedImage);
            aphidCount = aphidCounter.countAphid();

            Log.i("MainActivity", "Aphid count: " + aphidCount);


            // Create a bitmap to store the converted image:
            bmConvertedImage = Bitmap.createBitmap(convertedImage.cols(),
                    convertedImage.rows(),
                    Bitmap.Config.ARGB_8888);

            // Convert the mat to bitmap:
            Utils.matToBitmap(convertedImage, bmConvertedImage);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private class ConvertPhotosTask extends AsyncTask<Uri, Integer, String> {

        protected String doInBackground(Uri... uris) {

            startConvertingTime = System.currentTimeMillis();

            try {
                uriToBitmap(uris[0]);
            } catch (Exception ex) {
            }

            if (null != bitmap) {

                source = new Mat();

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

            // Get the time that the conversion ended at:
            endConvertingTime = System.currentTimeMillis();

            // calculate the difference (in secs) between start and end time
            totalConversionTime = (double) ((endConvertingTime - startConvertingTime) / 1000);

            // Enable the load photo and back buttons
            loadPhoto.setEnabled(true);
            goBack.setEnabled(true);

            // Update the Aphid count and Conversion time text:
            tvAphidCount.setText("Aphid Count: " + aphidCount);
            tvConversionTime.setText("Time: " + totalConversionTime + " seconds");

            // Update the image shown on the app screen to the newly converted image:
            ivAphidPicConverted.setImageBitmap(bmConvertedImage);

            bmConvertedImage = null;
            convertedImage = null;
            source = null;
            bitmap = null;
            imageUri = null;
            imageConverter = null;
            aphidCounter = null;

        }

    }

}