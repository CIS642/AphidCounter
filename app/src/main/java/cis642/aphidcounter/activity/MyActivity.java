package cis642.aphidcounter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.opencv.android.OpenCVLoader;

import cis642.aphidcounter.MainActivity;
import cis642.aphidcounter.R;
import cis642.aphidcounter.ViewHistory;

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

        //SetConversionTestButtonListener();

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
                startActivityForResult(myIntent, 0);
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
     * Event handler for the Conversion Test button press.
     */
/*    private void SetConversionTestButtonListener()
    {
        Button conversionTest = (Button) findViewById(R.id.btnConversionTest);
        conversionTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
    }
*/
}


