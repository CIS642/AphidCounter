package cis642.aphidcounter.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.GregorianCalendar;

import cis642.aphidcounter.AphidPhoto;
import cis642.aphidcounter.ImageConverter;
import cis642.aphidcounter.R;
import cis642.aphidcounter.entity.Field;
import cis642.aphidcounter.manager.PhotoManager;
import cis642.aphidcounter.manager.PhotoSetManager;
import cis642.aphidcounter.util.AphidCounter;

public class ConvertPhotos extends Activity {

    /**
     * Manager class that manages the photos.
     */
    private PhotoManager photoManager;

    /**
     * Manager class for photo sets.
     */
    private PhotoSetManager photoSetManager;

    /**
     * Index in the Photos array list in PhotoManager where the image info is stored,
     * used for when the photo is being converted.
     */
    private int index;

    /**
     * Flag used for checking if the conversion process should be halted.
     */
    private boolean haltConversion, canFinishActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_photos);

        haltConversion = false;
        canFinishActivity = false;
        photoManager = PhotoManager.GetInstance();
        photoSetManager = PhotoSetManager.GetInstance();

        // If there are no photos to convert, disable the convert button:
        if (photoManager.GetPhotoCount() == 0 || photoManager.GetPhotoCount() <= photoManager.GetConvertedPhotoCount())
            DisableConvertButton();

        UpdatePhotoCountText();

        // Set button listeners:
        SetBackButtonListener();

        SetStartConvertingButtonListener();

        SetStopConvertingButtonListener();
    }

    // disable back button press on this activity.
    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.convert_photos, menu);
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

    private void DisableConvertButton()
    {
        Button convert = (Button) findViewById(R.id.startConvertingButton);
        convert.setClickable(false);
    }

    /**
     * Set the event handler for the 'Back' button click.
     */
    private void SetBackButtonListener()
    {
        Button goBack = (Button) findViewById(R.id.goBackButton);
        goBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }

        });
    }

    /**
     * Set the event handler for the 'start converting' button press.
     */
    private void SetStartConvertingButtonListener()
    {
        Button convert = (Button) findViewById(R.id.startConvertingButton);
        convert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                UpdateView();
                try {
                    new ConvertPhotosTask().execute("");
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });
    }

    /**
     * Set the event handler for the 'stop converting' button press.
     */
    private void SetStopConvertingButtonListener()
    {
        Button stopConverting = (Button) findViewById(R.id.stopConvertingButton);
        stopConverting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if (canFinishActivity)
                    finish();
                else
                    CreateStopConvertingDialog();

            }
        });
    }

    /**
     * Updates the screen once the 'start converting' button has been pressed.
     * It will hide the 'home' and 'start converting' buttons, and show the
     * 'stop converting' button.
     */
    private void UpdateView()
    {
        // Remove the 'Home' button:
        Button goHome = (Button) findViewById(R.id.goBackButton);
        goHome.setClickable(false);
        ViewGroup vg = (ViewGroup) goHome.getParent();
        if (vg != null)
            vg.removeView(goHome);

        // Remove the start converting button:
        Button startConverting = (Button) findViewById(R.id.startConvertingButton);
        startConverting.setClickable(false);
        vg = (ViewGroup) startConverting.getParent();
        if (vg != null)
            vg.removeView(startConverting);

        // Show the 'stop converting' button:
        Button stopConverting = (Button) findViewById(R.id.stopConvertingButton);
        stopConverting.setVisibility(View.VISIBLE);
    }

    private void CreateStopConvertingDialog()
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Stop converting photos?");
        alertBuilder.setCancelable(true);
        alertBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //finish();
                        haltConversion = true;
                        Button stopConverting = (Button) findViewById(R.id.stopConvertingButton);
                        stopConverting.setText("Cancelling, Please Wait...");
                        stopConverting.setClickable(false);
                    }
                });
        alertBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alertBuilder.create().show();
    }

    /**
     * Update the text which shows the number of converted photos & number of total photos.
     */
    private void UpdatePhotoCountText()
    {
        TextView tv = (TextView) findViewById(R.id.numberOfUnconvertedLeft);
        tv.setText("Number of photos converted: " + photoManager.GetConvertedPhotoCount() + "/" + photoManager.GetPhotoCount());
    }

    /**
     * Check to see if the conversion should be halted.
     * @return
     */
    private boolean HaltConversion()
    {
        return this.haltConversion;
    }









    private class ConvertPhotosTask extends AsyncTask<String, Integer, String>
    {
        protected String doInBackground(String... strings)
        {
            // Loop through each line taken from Photos.txt
            for (int i = 0; i < photoManager.GetPhotoCount(); i++)
            {
                String[] str = photoManager.GetPhotoInfo(i).split(",", -1);

                // If the photo has not yet been converted, convert it.
                if (str[1].equals(photoManager.NOT_CONVERTED))
                {
                    ImageConverter converter = new ImageConverter(str[0]);

                    if (converter.ConvertImage()) // if the image was successfully converted, save it.
                    {
                        AphidCounter aphidCounter = new AphidCounter(converter.getConvertedImage());
                        int aphidCount = aphidCounter.countAphid();

                        // Add the photo info which will get saved to photos.txt
                        photoManager.SetConvertedInfo(i, converter.GetConvertedFileName(), aphidCount);

                        String field = str[photoManager.FIELD_NAME];
                        String crop = str[photoManager.CROP_TYPE];
                        String[] dateArr = str[photoManager.DATE_TAKEN].split("\\.", -1);
                        GregorianCalendar date = new GregorianCalendar(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[2]));

                        // Create a new photo object for the newly converted photo.
                        AphidPhoto photo = new AphidPhoto(converter.GetConvertedFileName(), new Field(field, crop), date);
                        photo.SetAphidCount(aphidCount);

                        // Add the newly converted photo to the appropriate Photoset:
                        PhotoSetManager.GetInstance().GetByID(str[photoManager.ORIGINAL_PHOTO].split("-", -1)[0]).AddConvertedPhoto(photo);
                        PhotoSetManager.GetInstance().Save();
                    }
                }

                if (HaltConversion())
                {
                    return "Conversion Stopped";
                }
            }
            return "Done";
        }

        protected void onProgressUpdate(Integer... progress)
        {

        }

        protected void onPostExecute(String strings)
        {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // after finish converting, vibrate phone for 250 ms
            v.vibrate(250);

            if (strings.equals("Conversion Stopped"))
                finish();
            canFinishActivity = true;
            Button stopConverting = (Button) findViewById(R.id.stopConvertingButton);
            stopConverting.setText("Done");
            stopConverting.setBackgroundColor(Color.parseColor("#009900"));
            UpdatePhotoCountText();
        }

    }
}
