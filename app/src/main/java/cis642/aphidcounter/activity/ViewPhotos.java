package cis642.aphidcounter.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import cis642.aphidcounter.PhotoSet;
import cis642.aphidcounter.R;
import cis642.aphidcounter.manager.FileManager;
import cis642.aphidcounter.manager.PhotoSetManager;

public class ViewPhotos extends ActionBarActivity {

    private FileManager fileManager = new FileManager();
    private PhotoSetManager psManager = PhotoSetManager.GetInstance();
    private String photoSetID;
    private PhotoSet photoSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photos);

        // Set event handler for the back button press.
        SetBackButtonListener();

        photoSetID = (String) getIntent().getSerializableExtra("photoSetID");
        photoSet = psManager.GetByID(photoSetID);

        if (photoSet != null)
            PopulateWithImages();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_photos, menu);
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
     * Set the event handler for the 'Back' button click.
     */
    private void SetBackButtonListener()
    {
        Button goBack = (Button) findViewById(R.id.goBackViewPhotoSet);

        goBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }

        });
    }

    /**
     * Populates the activity with resized images that can be clicked on.
     */
    private void PopulateWithImages()
    {
        LinearLayout gallery = (LinearLayout) findViewById(R.id.photo_gallery);

        if (getIntent().getSerializableExtra("photoType").equals("original"))
        {
            for (int i = 0; i < photoSet.GetPhotoCount(); i++)
            {
                if (fileManager.PhotoExists(photoSet.GetPhoto(i).GetPhotoName()))
                {
                    // Create a new linear layout that will contain the photo and information below it:
                    LinearLayout picAndInfo = new LinearLayout(this);
                    picAndInfo.setOrientation(LinearLayout.VERTICAL);

                    // Create an image view which will hold a resized image for display:
                    ImageView iv = new ImageView(this);

                    // Get the path to a particular image at the ith index of the original photos photoset:
                    String imgPath = fileManager.GetPhotosDirectory().toString() + File.separator.toString() + photoSet.GetPhoto(i).GetPhotoName();

                    try
                    {
                        // get the image:
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgPath);

                        // get a resized version of the image for display:
                        iv.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 240, 320, false));

                        iv.setPadding(10, 10, 10, 10);
                        // Add the resized image to the view:
                        picAndInfo.addView(iv);

                        // Set the text that will appear below the image:
                        SetTextBelowImage(i, picAndInfo, "original");

                        // Add the photo and text to the main view:
                        gallery.addView(picAndInfo);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }
        }
        else
        {
            DisplayConverted(gallery);
        }

    }

    /**
     * Adds text information below each image
     * @param i - The index of the photo in the photoset's original-image array list.
     * @param picAndInfo - The linear layout the text will be added to. Already contains the photo.
     * @return - The linear layout with the added text.
     */
    private LinearLayout SetTextBelowImage(int i, LinearLayout picAndInfo, String photoType)
    {

        TextView tv = new TextView(this);
        if (photoType.equals("original")){ tv.setText("File: " + photoSet.GetPhoto(i).GetPhotoName()); }
        else { tv.setText("File: " + photoSet.GetConvertedPhoto(i).GetPhotoName()); }
        tv.setTextSize(12);
        tv.setPadding(10, 0, 0, 0);
        picAndInfo.addView(tv);


        tv = new TextView(this);
        tv.setText("Date: " + photoSet.GetDateTaken());
        tv.setTextSize(12);
        tv.setPadding(10, 0, 0, 0);
        picAndInfo.addView(tv);

        tv = new TextView(this);
        tv.setText("Bug: " + photoSet.GetBugType());
        tv.setTextSize(12);
        tv.setPadding(10, 0, 0, 0);
        picAndInfo.addView(tv);

        tv = new TextView(this);
        tv.setText("Field: " + photoSet.GetField().name());
        tv.setTextSize(12);
        tv.setPadding(10, 0, 0, 0);
        picAndInfo.addView(tv);

        tv = new TextView(this);
        tv.setText("Crop: " + photoSet.GetField().GetCropType());
        tv.setTextSize(12);
        tv.setPadding(10, 0, 0, 0);
        picAndInfo.addView(tv);

        tv = new TextView(this);
        tv.setText("Aphid Count: " + 0);
        tv.setTextSize(12);
        tv.setPadding(10, 0, 0, 0);
        picAndInfo.addView(tv);



        return picAndInfo;
    }


    private void DisplayConverted(LinearLayout gallery)
    {
        for (int i = 0; i < photoSet.GetConvertedPhotoCount(); i++)
        {
            if (!photoSet.GetConvertedPhoto(i).equals(""))
            {

                if (fileManager.ConvertedPhotoExists(photoSet.GetConvertedPhoto(i).GetPhotoName()))
                {
                    // Create a new linear layout that will contain the photo and information below it:
                    LinearLayout picAndInfo = new LinearLayout(this);
                    picAndInfo.setOrientation(LinearLayout.VERTICAL);

                    // Create an image view which will hold a resized image for display:
                    ImageView iv = new ImageView(this);

                    // Get the path to a particular image at the ith index of the original photos photoset:
                    String imgPath = fileManager.GetConvertedPhotosDirectory().toString() + File.separator.toString() + photoSet.GetConvertedPhoto(i).GetPhotoName();

                    try
                    {
                        // get the image:
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgPath);

                        // get a resized version of the image for display:
                        iv.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 240, 320, false));

                        iv.setPadding(10, 10, 10, 10);
                        // Add the resized image to the view:
                        picAndInfo.addView(iv);

                        // Set the text that will appear below the image:
                        SetTextBelowImage(i, picAndInfo, "converted");

                        // Add the photo and text to the main view:
                        gallery.addView(picAndInfo);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }
        }

    }

}

