package cis642.aphidcounter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cis642.aphidcounter.activity.ViewPhotos;
import cis642.aphidcounter.manager.PhotoManager;
import cis642.aphidcounter.manager.PhotoSetManager;


/**
 * This class creates the UI for viewing a particular photo set.
 */
public class ViewPhotoSet extends Activity {

    private String photoSetIndex, photoSetId;
    private PhotoSet photoSet;
    private int aphidCount;
    private PhotoManager photoManager;

    /**
     * A list of photosets.
     */
    //private ArrayList<PhotoSet> photoSets = new ArrayList<PhotoSet>();
    private static PhotoSetManager psManager = PhotoSetManager.GetInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo_set);

        // Get the photoset that was passed to this intent:
        photoSetId = (String) getIntent().getSerializableExtra("PhotoSetId");

//        if (null == photoSetId) {
//            // Coming from the View History activity, the index will be passed
//            photoSetIndex = (String) getIntent().getSerializableExtra("PhotoSet");
//            photoSet = psManager.Get(Integer.parseInt(photoSetIndex));
//        }
//        else {
            // Coming from the MyActivity activity, the ID will be passed
            photoSet = psManager.GetByID(photoSetId);
//        }

        aphidCount = 0;
        photoManager = PhotoManager.GetInstance();

        // Set the event handler for the go back button press:
        SetBackButtonListener();

        // Set event handler for View Photos button press:
        SetViewPhotoListener();

        // Set the event handler for View Converted Photos button press.
        SetViewConvertedPhotoListener();

        // Set event handler for Delete button press:
        SetDeletePhotoSetListener(photoSet);

        // Create the UI for this page.
        CreateTextViews();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_photo_set, menu);
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
     * Dynamically creates the UI for this activity. Displays the basic information concerning
     * this photoset.
     */
    private void CreateTextViews() {

        // Create a button for the ith index in the photoSets list.


        TextView bugType = (TextView) findViewById(R.id.bugTypeText);
        TextView fieldName = (TextView) findViewById(R.id.fieldText);
        TextView cropType = (TextView) findViewById(R.id.cropType);
        TextView dateTaken = (TextView) findViewById(R.id.dateTakenText);
        TextView photoCount = (TextView) findViewById(R.id.numberOfPhotosText);
        TextView avgBugCount = (TextView) findViewById(R.id.avgBugCountText);

        bugType.setText("Bug Type:   " + photoSet.GetBugType());
        fieldName.setText("Field:   " + photoSet.GetField().name());
        cropType.setText("Crop Type:   " + photoSet.GetField().GetCropType());
        dateTaken.setText("Date Taken:   " + photoSet.GetDateTaken());
        photoCount.setText("Photo Count:   " + photoSet.GetPhotoCount());
        avgBugCount.setText("Average Bug Count:   " + CalculateAphidAverage());

    }

    /**
     * Set the event handler for the 'Back' button click.
     */
    private void SetBackButtonListener()
    {
        Button goBack = (Button) findViewById(R.id.goBackViewHistory);

        goBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                setResult(1);
                finish();
            }

        });
    }

    /**
     * Set the event handler for the View Photos button
     */
    private void SetViewPhotoListener()
    {
        Button viewPhotos = (Button) findViewById(R.id.originalPhotos);
        viewPhotos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), ViewPhotos.class);
                //myIntent.putExtra("photoSetID", photoSetIndex);
                myIntent.putExtra("photoSetID", photoSet.GetPhotoSetID());
                myIntent.putExtra("photoType", "original");
                startActivityForResult(myIntent, 0);
            }
        });
    }

    /**
     * Set the event handler for the VIew Converted Photos button.
     */
    private void SetViewConvertedPhotoListener()
    {
        Button viewConvertedPhotos = (Button) findViewById(R.id.convertedPhotos);
        viewConvertedPhotos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), ViewPhotos.class);
                myIntent.putExtra("photoSetID", photoSet.GetPhotoSetID());
                myIntent.putExtra("photoType", "converted");
                startActivityForResult(myIntent, 0);
            }
        });
    }

    /**
     * Set the event handler for the Delete PhotoSet button
     */
    private void SetDeletePhotoSetListener(PhotoSet ps)
    {
        final PhotoSet psToDelete = ps;

        Button deletePhotoSet = (Button) findViewById(R.id.deletePhotoSet);
        deletePhotoSet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                CreateDeleteAlertDialog(psToDelete);
            }
        });
    }

    private void CreateDeleteAlertDialog(PhotoSet ps)
    {
        final PhotoSet psToDelete = ps;
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Delete this Photo Set? All images will also be deleted.");
        alertBuilder.setCancelable(true);
        alertBuilder.setPositiveButton("Delete Photo Set",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Delete the photo set:
                        PhotoSetManager.GetInstance().Remove(psToDelete);

                        setResult(0);
                        finish();
                    }
                });
        alertBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alertBuilder.create().show();
    }

    /**
     * Calculates the average aphid count for this photoset.
     * @return
     */
    private int CalculateAphidAverage()
    {
        int aphidCount = 0, totalPhotos = 0;
        String psID = photoSet.GetPhotoSetID();

        // Loop through each line that is in Photos.txt
        for (int i = 0; i < photoManager.GetPhotoCount(); i++)
        {
            try {
                String[] photoInfo = photoManager.GetPhotoInfo(i).split(",", -1);

                // If the info in this line correlates to this photo set's ID, get the aphid count
                if (photoInfo[photoManager.ORIGINAL_PHOTO].split("-", -1)[0].equals(psID)) {

                    if (!photoInfo[photoManager.CONVERTED_PHOTO].equals(photoManager.NOT_CONVERTED)) {
                        aphidCount += Integer.parseInt(photoInfo[photoManager.APHID_COUNT]);
                        totalPhotos++;
                    }

                }
            } catch (Exception ex) { ex.printStackTrace(); } // error reading photo info from photos.txt

        }

        if (totalPhotos == 0)
            return 0;
        return aphidCount / totalPhotos;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

    }
}
