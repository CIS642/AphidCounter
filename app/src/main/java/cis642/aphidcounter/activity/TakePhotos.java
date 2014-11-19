package cis642.aphidcounter.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.util.GregorianCalendar;

import android.view.View;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import cis642.aphidcounter.PhotoSet;
import cis642.aphidcounter.entity.Field;
import cis642.aphidcounter.manager.FileManager;
import cis642.aphidcounter.manager.PhotoSetManager;

import cis642.aphidcounter.R;
import cis642.aphidcounter.storage.DatabaseOpenHelper;

/**
 * Created by JacobLPruitt on 9/29/2014.
 */
public class TakePhotos extends Activity
{

    /**
     * The alert that will display when the user clicks the Finish button.
     */
    private AlertDialog confirmFinish;

    /**
     * Original filename of the saved photo that will be renamed.
     */
    private final String photoName = "MYPHOTO.JPG";

    /**
     * For managing files and directories.
     */
    private FileManager fileManager = new FileManager();

    //private File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
    //       + File.separator + "AphidCounter" + File.separator + "Photos");

    /**
     * Flag that prevents the photoset from being added to the photoset manager more than once.
     */
    private boolean hasAddedPhotoSet = false;

    /**
     * The current photo count for this set.
     */
    private int photoCount = 0;

    /**
     * The photoset that will be added.
     */
    private final PhotoSet photoSet = new PhotoSet("", null, null);


    private DatabaseOpenHelper mDbHelper;
    private SimpleCursorAdapter mAdapter;


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_photos);

        mDbHelper = new DatabaseOpenHelper(this);

        Intent myIntent = getIntent();

        Cursor c = getAllFields();
        mAdapter = new SimpleCursorAdapter(this, R.layout.spinner_view_dropdown, c, DatabaseOpenHelper.columns, new int[] {R.id._id, R.id.fieldName,R.id.cropName},0);

        startManagingCursor(c);

        String [] columns = new String [] {"field_name"};
        int [] to = new int[]{R.id._id};

        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spnClients = (Spinner) findViewById(R.id.fieldTypeSpinner);
        spnClients.setAdapter(mAdapter);

        // Initialize the folders to save the photos to.
        //TryInitializeDirectory();

        //iv = (ImageView) findViewById(R.id.photo_image_view);

        //Create the photoset for this particular activity:
        //final PhotoSet photoSet = new PhotoSet("", null, null);
        photoSet.SetDateTaken(new GregorianCalendar());

        // Set the even handlers:


        SetBugTypeListener(photoSet);

        SetFieldTypeListener(photoSet);

        SetTakePhotoListener(photoSet);

        CreateFinishAlertDialog();

        SetFinishListener();

/* i added
        // Set the Listener handler for the bug type spinner.
        Spinner selectBug = (Spinner) findViewById(R.id.bugTypeSpinner);
        selectBug.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                // Get the bug that the user selected.
                String bugType = adapterView.getItemAtPosition(i).toString();

                // Set that as the bug for this photo set.
                photoSet.SetBugType(bugType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        // Set the event handler for the Take Photo button click.
        Button next = (Button) findViewById(R.id.accessCameraButton);
        next.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                photoSet.SetBugType("");
                photoSet.SetField(new Field());


                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);

i added*/

                //Intent myIntent = new Intent(view.getContext(), CapturePhoto.class);
                //startActivityForResult(myIntent, 0);
                /*String fileName = "new-photo-name.jpg";
                //create parameters for Intent with filename
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, fileName);
                values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
                //imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                //create new Intent
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);*/
/* i added
            }

        });
i added*/
    }
    private Cursor getAllFields(){
        return mDbHelper.getWritableDatabase().query(DatabaseOpenHelper.TABLE_NAME,DatabaseOpenHelper.columns,null,new String[] {},null,null,null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // on photo capture, result code = -1. on back button press, result code = 0
        if(requestCode == 0 && resultCode == -1)
        {
            //Bitmap theImage = (Bitmap) data.getExtras().get("data");
            //iv.setImageBitmap(theImage);

            // Rename the photo filename to something unique.
            String photoName = RenamePhoto();

            // If the photo was renamed successfully, add the file name to the photo set.
            if (!photoName.equals(""))
            {
                photoSet.AddPhoto(photoName);
                photoCount++;
            }

            // Only add the photoset if it has not been added yet.
            if (!hasAddedPhotoSet)
            {
                PhotoSetManager.Add(photoSet);
                hasAddedPhotoSet = true;
            }

            PhotoSetManager.Save();

            UpdateView();
        }



        //PhotoSetManager.SerializeList();

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
     * Set the event handler for when the user selects a type of bug.
     * @param photoSet
     */
    private void SetBugTypeListener(final PhotoSet photoSet)
    {
        Spinner selectBug = (Spinner) findViewById(R.id.bugTypeSpinner);

        selectBug.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                // Get the bug that the user selected.
                String bugType = adapterView.getSelectedItem().toString();

                // Set that as the bug for this photo set.
                photoSet.SetBugType(bugType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

    }

    /**
     * Set the event handler for when the user selects a field.
     * @param photoSet
     */
    private void SetFieldTypeListener(final PhotoSet photoSet)
    {
        Spinner selectField = (Spinner) findViewById(R.id.fieldTypeSpinner);
        selectField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                // Get the field that the user selected.
                String fieldName = adapterView.getSelectedItem().toString();

                Field field = new Field(fieldName, "Soy Bean");

                // Set that as the bug for this photo set.
                photoSet.SetField(field);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    /**
     * Set the event handler for when the user clicks the Take Photo button.
     * @param photoSet
     */
    private void SetTakePhotoListener(final PhotoSet photoSet)
    {
        Button next = (Button) findViewById(R.id.accessCameraButton);
        next.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

                //File filename = new File(directory + File.separator + "MYPHOTO.jpg");
                File photoFile = new File(fileManager.GetPhotosDirectory() + File.separator + photoName);

                // Start the activity to take a photo.
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                //if (directory.exists())
                if (fileManager.GetPhotosDirectory().exists())
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

                startActivityForResult(intent, 0);
            }
        });
    }

    /**
     * Set the handler for the Finish button. Displays a confirmation dialog upon click.
     */
    private void SetFinishListener()
    {
        Button finishButton = (Button) findViewById(R.id.finishPhotoSet);
        finishButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmFinish.show();
            }
        });
    }

    /**
     * Attempts to initialize the folder(s) where the photos will be stored in.
     */
/*    private void TryInitializeDirectory()
    {
        if (!directory.exists())
        {
            try
            {
                directory.mkdirs();
            } catch (Exception ex) { ex.printStackTrace(); }
        }

    } */

    /**
     * Renames the .jpg file containing the newly captured photo.
     */
    private String RenamePhoto()
    {
        String updatedPhotoName = Long.toString(System.currentTimeMillis()) + ".jpg";
        File from, to;
        boolean success = false;
        // photoname = photoname.replace(" ", "");

        try
        {
            //File from = new File(directory + File.separator + "MYPHOTO.jpg");
            //File updatedName = new File(directory + File.separator + photoname);

            from = new File(fileManager.GetPhotosDirectory() + File.separator + photoName);
            to = new File(fileManager.GetPhotosDirectory() + File.separator + updatedPhotoName);

            from.renameTo(to);

            if (to.exists())
                success = true;

        } catch (Exception ex) { ex.printStackTrace(); }

        if (success)
            return updatedPhotoName;
        else
            return "";
    }

    /**
     * Updates the view after the first photo has been taken.
     * It updates the text for the Take photo button, as well as updates the text for the
     * photo count textview. Also displays the "Finish" button after the first photo is taken.
     */
    private void UpdateView()
    {
        TextView numOfPhotos = (TextView) findViewById(R.id.photoCountText);

        if (photoCount == 1) {
            Button takePhotoButton = (Button) findViewById(R.id.accessCameraButton);
            Button finishButton = (Button) findViewById(R.id.finishPhotoSet);

            takePhotoButton.setText("Take Another Photo");
            finishButton.setVisibility(View.VISIBLE);
            finishButton.setEnabled(true);

            numOfPhotos.setVisibility(View.VISIBLE);
        }

        numOfPhotos.setText("Current Photo Count: " + photoCount);
    }

    /**
     * Creates the alert dialog, which will be displayed when the user clicks the Finish button.
     * This will allow them to confirm if they want to finish taking photos for this set.
     */
    private void CreateFinishAlertDialog()
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Finish taking photos?");
        alertBuilder.setCancelable(true);
        alertBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        alertBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        confirmFinish = alertBuilder.create();
    }

}
