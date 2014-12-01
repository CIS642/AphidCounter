package cis642.aphidcounter.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.lang.Object;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.view.View;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.content.ContentValues;
import android.widget.TextView;

import cis642.aphidcounter.AphidPhoto;
import cis642.aphidcounter.PhotoSet;
import cis642.aphidcounter.R;
import cis642.aphidcounter.entity.Field;
import cis642.aphidcounter.manager.FileManager;
import cis642.aphidcounter.manager.PhotoManager;
import cis642.aphidcounter.manager.PhotoSetManager;
import cis642.aphidcounter.storage.AddField;
import cis642.aphidcounter.storage.DatabaseOpenHelper;

/**
 * Created by JacobLPruitt on 9/29/2014.
 */
public class TakePhotos extends Activity{

    private DatabaseOpenHelper mDbHelper;
    private SimpleCursorAdapter mAdapter;
    private Spinner fieldTypeSpinner;

    /**
     * A list of photosets.
     */
    //private ArrayList<PhotoSet> photoSets = new ArrayList<PhotoSet>();
    private static PhotoSetManager psManager = PhotoSetManager.GetInstance();

    /**
     * Manages saving photos to the Photos.txt file.
     */
    private PhotoManager photoManager = PhotoManager.GetInstance();

    /**
     * The alert that will display when the user clicks the Finish button.
     */
    private AlertDialog confirmFinish;

    /**
     * Original filename of the saved photo that will be renamed.
     */
    private final String TEMP_PHOTO_NAME= "MYPHOTO.JPG";

    /**
     * Text for adding a new field.
     */
    private final String ADD_NEW_FIELD = "Add new field...";

    /**
     * Text for adding a new bug.
     */
    private final String ADD_NEW_BUG = "Add new bug...";

    /**
     * The ID for this photo set that will also be used for all photo file names for this set.
     */
    private String photoSetID;

    /**
     * For managing files and directories.
     */
    private FileManager fileManager = new FileManager();

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

    private boolean selectedBug = false, selectedField = false;

    private ArrayAdapter<String> dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_photos);

        // Create the unique ID that will be associated with this photo set and it's images.
        photoSetID = Long.toString(System.currentTimeMillis() / 1000);

        mDbHelper = new DatabaseOpenHelper(this);
        fieldTypeSpinner = (Spinner) findViewById(R.id.fieldTypeSpinner);
        Intent myIntent = getIntent();

        Cursor c = mDbHelper.getWritableDatabase().query(DatabaseOpenHelper.TABLE_NAME,DatabaseOpenHelper.columns,null,new String[] {},null,null,null);
        List<String> arraylist = new ArrayList<String>();

        // Initialize array with the option to add a new field.
        // When this item is clicked, it will launch the Add Field activity.
        arraylist.add("");
        arraylist.add(ADD_NEW_FIELD);

        while(c.moveToNext()) {
            arraylist.add(c.getString(1));
        }

        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraylist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldTypeSpinner.setAdapter(dataAdapter);


        //Create the photoset for this particular activity:
        //final PhotoSet photoSet = new PhotoSet("", null, null);
        photoSet.SetDateTaken(new GregorianCalendar());
        //photoSet.SetField(new Field("Test Field Name", "Test Crop Name"));

        // Set the even handlers:
        SetBackButtonListener();

        SetBugTypeListener(photoSet);

        SetFieldTypeListener(photoSet);

        SetTakePhotoListener(photoSet);

        CreateFinishAlertDialog();

        SetFinishListener();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == 0)
        {
            // on photo capture, result code = -1. on back button press, result code = 0
            if(requestCode == 0 && resultCode == -1)
            {
                // Rename the photo filename to something unique.
                String photoName = RenamePhoto();

                // If the photo was renamed successfully, add the file name to the photo set.
                if (!photoName.equals(""))
                {
                    AphidPhoto photo = new AphidPhoto(photoName, photoSet.GetField(), photoSet.GetDate());
                    photoSet.AddPhoto(photo);

                    // Add the photo to the Photo Manager, which will save it to Photos.txt
                    photoManager.AddPhoto(photoName + "," +
                                          "notconverted" + "," +
                                          photoSet.GetDateTaken() + "," +
                                          photoSet.GetField().name() + "," +
                                          photoSet.GetField().GetCropType() + "," +
                                          photoSet.GetBugType() + "," +
                                          "0");
                    photoCount++;
                }

                // Only add the photoset to the PS manager if it has not been added yet.
                if (!hasAddedPhotoSet)
                {
                    photoSet.SetPhotoSetID(photoSetID);
                    psManager.Add(photoSet);
                    hasAddedPhotoSet = true;

                    // Disable the field and bug selection spinners.
                    Spinner selectField = (Spinner) findViewById(R.id.fieldTypeSpinner);
                    Spinner selectBug = (Spinner) findViewById(R.id.bugTypeSpinner);

                    selectField.setEnabled(false);
                    selectBug.setEnabled(false);
                }

                //PhotoSetManager.Save();
                psManager.Save();

                UpdateView();
            }

            // reset the spinner after coming from the Add Field activity.
            if (resultCode == 1)
            {
                Spinner selectField = (Spinner) findViewById(R.id.fieldTypeSpinner);
                selectField.setSelection(0);
            }

            // reset the spinner after coming from the add bug activity (todo)
            if (resultCode == 2)
            {
                Spinner selectBug = (Spinner) findViewById(R.id.bugTypeSpinner);
                selectBug.setSelection(0);
            }

        }

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
     * Set the event handler for when the user selects a type of bug.
     * @param photoSet
     */
    private void SetBugTypeListener(final PhotoSet photoSet)
    {
        final Spinner selectBug = (Spinner) findViewById(R.id.bugTypeSpinner);

        selectBug.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                // Get the bug that the user selected.
                String bugType = adapterView.getSelectedItem().toString();

                if (bugType.equals(ADD_NEW_BUG))
                {
                    // todo: add bug activity
                }
                else if (!bugType.equals(""))
                {
                    // Set that as the bug for this photo set.
                    photoSet.SetBugType(bugType);
                    selectedBug = true;
                    EnableTakePhotosButton();
                }
                else // blank item is selected so take photos button will be disabled.
                {
                    selectedBug = false;
                    EnableTakePhotosButton();
                }
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
                fieldName = fieldName.replaceAll(",", ".");

                // If the user selected the "add new field..." option,
                // Start the activity to allow them to add a new field.
                if (fieldName.equals(ADD_NEW_FIELD))
                {
                    Intent myIntent = new Intent(view.getContext(), AddField.class);
                    startActivityForResult(myIntent, 0);
                }
                else if (!fieldName.equals(""))
                {
                    Field field = new Field(fieldName, "Soy Bean");

                    // Set that as the field for this photo set.
                    photoSet.SetField(field);
                    selectedField = true;
                    EnableTakePhotosButton();
                }
                else // blank item is selected so take photos button will be disabled.
                {
                    selectedField = false;
                    EnableTakePhotosButton();
                }
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

                File photoFile = new File(fileManager.GetPhotosDirectory() + File.separator + TEMP_PHOTO_NAME);

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
     * Renames the .jpg file containing the newly captured photo.
     */
    private String RenamePhoto()
    {
        String updatedPhotoName = photoSetID + "-" + (photoCount + 1) + ".jpg";
        File from, to;
        boolean success = false;
        // photoname = photoname.replace(" ", "");

        try
        {
            //File from = new File(directory + File.separator + "MYPHOTO.jpg");
            //File updatedName = new File(directory + File.separator + photoname);

            from = new File(fileManager.GetPhotosDirectory() + File.separator + TEMP_PHOTO_NAME);
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
            Button goBackButton = (Button) findViewById(R.id.goBackButton);

            // Remove the go back button after the user has taken a pic, as they will now use
            // The 'Done' button to return to the parent activity.
            ViewGroup vg = (ViewGroup) goBackButton.getParent();
            if (vg != null)
                vg.removeView(goBackButton);

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

    /**
     * Enable/Disable the take photos button based on if the user selected bug and field types.
     */
    private void EnableTakePhotosButton()
    {
        Button takePhotos = (Button)findViewById(R.id.accessCameraButton);

        if (selectedBug && selectedField)
        {
            takePhotos.setEnabled(true);
        }
        else
        {
            takePhotos.setEnabled(false);
        }
    }

}
