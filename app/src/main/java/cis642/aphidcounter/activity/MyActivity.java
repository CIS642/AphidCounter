package cis642.aphidcounter.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.io.File;

import cis642.aphidcounter.MainActivity;
import cis642.aphidcounter.R;
import cis642.aphidcounter.ViewHistory;
import cis642.aphidcounter.ViewPhotoSet;
import cis642.aphidcounter.manager.FileManager;

//import cis642.aphidcounter.entity.Field;
//import cis642.aphidcounter.manager.PhotoSetManager;
//import cis642.aphidcounter.storage.AddField;
//import cis642.aphidcounter.storage.SelectField;


public class MyActivity extends Activity {

    /**
     * A list of photosets.
     */
    //private ArrayList<PhotoSet> photoSets = new ArrayList<PhotoSet>();
/*
    private static PhotoSetManager psManager = PhotoSetManager.GetInstance();

    public ArrayList<Field> listOfFields = new ArrayList<Field>();
    public ArrayList<PhotoSet> listOfPhotoSets = new ArrayList<PhotoSet>();
    private String fileName = "MyFields.txt";
    private String filePath = "MyFileStorage";
*/

    /**
     * For managing files.
     */
    private FileManager fileManager = new FileManager();

    private final int TAKE_PHOTOS_REQUEST_CODE = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
        setContentView(R.layout.activity_my);

        // Set event handlers for the buttons:
        SetTakePhotosButtonListener();

        SetViewPhotoSetsButtonListener();

        SetConvertPhotosButtonListener();

        SetSendEmailButtonListener();

        SetConversionTestButtonListener();

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
     * Event handler for the Take Photos button press.
     */
    private void SetTakePhotosButtonListener()
    {
        Button takePhotos = (Button) findViewById(R.id.takePhotos);
        takePhotos.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), TakePhotos.class);
                startActivityForResult(myIntent, TAKE_PHOTOS_REQUEST_CODE);
            }

        });
    }

    /**
     * Event handler for the View Photo Sets button press.
     */
    private void SetViewPhotoSetsButtonListener()
    {
        Button viewHistory = (Button) findViewById(R.id.btnViewHistory);
        viewHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), ViewHistory.class);
                startActivityForResult(myIntent, 0);
            }
        });
    }

    /**
     * Event handler for the Convert Photos button press.
     */
    private void SetConvertPhotosButtonListener()
    {
        Button convertPhotos = (Button) findViewById(R.id.btnConvert);
        convertPhotos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), ConvertPhotos.class);
                startActivityForResult(myIntent, 0);
            }
        });
    }

    /**
     * The event handler for the Send Email button press.
     */
    private void SetSendEmailButtonListener() {
        Button sendEmail = (Button) findViewById(R.id.btnEmail);
        sendEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendEmail();
            }
        });
    }

    /**
     * Sends the CSV data file in an email.
     */
    private void sendEmail() {
        String[] toEmail = { "" };
        String subject = "Subject of email";
        String body = "body of email";
        File file = fileManager.GetPhotosDataFile();

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, toEmail);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        if (file.exists())
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file));

        try {
            this.startActivity(Intent.createChooser(emailIntent, "Send email.."));
            //finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MyActivity.this, "Error: no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Event handler for the Conversion Test button press.
     */
    private void SetConversionTestButtonListener()
    {
        Button conversionTest = (Button) findViewById(R.id.btnConversionTest);
        conversionTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
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

        if (requestCode == TAKE_PHOTOS_REQUEST_CODE && resultCode == RESULT_OK) {

            String id = resultData.getStringExtra("id");
            Log.i("MyActivity - ID: ", id);
            Intent myIntent = new Intent(this, ViewPhotoSet.class);
            myIntent.putExtra("PhotoSetId", id);
            startActivityForResult(myIntent, 0);
        }

    }

}


