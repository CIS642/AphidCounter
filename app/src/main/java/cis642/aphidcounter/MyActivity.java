package cis642.aphidcounter;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;


import cis642.aphidcounter.entity.Field;
import cis642.aphidcounter.manager.FileManager;
import cis642.aphidcounter.manager.PhotoSetManager;
import cis642.aphidcounter.storage.AddField;
import cis642.aphidcounter.storage.SelectField;


public class MyActivity extends Activity {
    public ArrayList<Field> listOfFields = new ArrayList<Field>();
    public ArrayList<PhotoSet> listOfPhotoSets = new ArrayList<PhotoSet>();
    private String fileName = "MyFields.txt";
    private String filePath = "MyFileStorage";

    private FileManager fileManager = new FileManager();

    /**
     * A list of photosets.
     */
    //private ArrayList<PhotoSet> photoSets = new ArrayList<PhotoSet>();
    private static PhotoSetManager psManager = PhotoSetManager.GetInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }

        setContentView(R.layout.activity_my);
        //StringBuffer datax = new StringBuffer("");

        //Change reading of file to method in SelectField.class
       /* listOfFields.add(new Field("First Field", "Bug 1"));
        listOfFields.add(new Field("Second Field", "Bug 2"));
        listOfFields.add(new Field("Third Field", "Bug 3"));*/
        /*try {

            FileInputStream fis = this.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            listOfFields = (ArrayList<Field>) ois.readObject();

            fis.close();

            ois.close();
            /*final InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader buffReader = new BufferedReader(isr);
            String readString = buffReader.readLine();
            while(readString != null) {
                datax.append(readString);
                readString = buffReader.readLine();
            }
            isr.close();

        }
        catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        catch (IOException e) {

            e.printStackTrace();
        }*/

        /*String savedDataOfFields = datax.toString();
        JSONArray fieldArray = new JSONArray();

        for(int i = 0; i < fieldArray.length(); i++) {
            try {
                JSONObject object = fieldArray.getJSONObject(i);

            }
            catch(Exception e) {
                break;
            }

        }*/

        /*String myData = "";
        try {
            FileInputStream fis = new FileInputStream(myInternalFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                myData = myData + strLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //myInputText.setText(myData);*/

        Button takePhotos = (Button) findViewById(R.id.takePhotos);
        takePhotos.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), TakePhotos.class);
                startActivityForResult(myIntent, 0);
            }

        });

        /**
         * Create the 'Add New Field' button.
         */
        Button addFieldScreen = (Button) findViewById(R.id.addFieldButton);
        addFieldScreen.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), AddField.class);
                //myIntent.putExtra("fields", listOfFields);
                startActivityForResult(myIntent, 0);
            }

        });

        Button selectFieldScreen = (Button) findViewById(R.id.selectField);
        selectFieldScreen.setOnClickListener(new OnClickListener() {
           public void onClick(View view) {
               Intent myIntent = new Intent(view.getContext(), SelectField.class);
               //myIntent.putExtra("newFields", listOfFields);
               startActivityForResult(myIntent, 0);
           }
        });

        /**
         * Create the 'View History' button.
         */
        Button viewHistory = (Button) findViewById(R.id.btnViewHistory);
        viewHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), ViewHistory.class);
                startActivityForResult(myIntent, 0);
            }
        });

        Button conversionTest = (Button) findViewById(R.id.btnConversionTest);
        conversionTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
/*
        Button next = (Button) findViewById(R.id.addPhotos);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), TakePhotos.class);
                startActivityForResult(myIntent, 0);
            }

        });

        Button examineScreen = (Button) findViewById(R.id.examineButton);
        examineScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent examinePhotosIntent = new Intent(view.getContext(), ExaminePhotosScreen.class);
                startActivityForResult(examinePhotosIntent, 0);
            }
        });

        Button helpScreen = (Button) findViewById(R.id.helpButton);
        helpScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent helpIntent = new Intent(view.getContext(), HelpScreen.class);
                startActivityForResult(helpIntent, 1);
            }

        });
        Button instructionScreen = (Button) findViewById(R.id.instructionScreenButton);
        instructionScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent instructionIntent = new Intent(view.getContext(), InstructionScreen.class);
                startActivityForResult(instructionIntent, 0);
            }
        });

        Button aboutScreen = (Button) findViewById(R.id.aboutButton);
        aboutScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent aboutIntent = new Intent(view.getContext(), AboutScreen.class);
                startActivityForResult(aboutIntent, 0);
            }
        });*/
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

    public void takePhotos(View view) {
        Intent myIntent = new Intent(view.getContext(), TakePhotos.class);
        startActivityForResult(myIntent, 0);
        /*//define the file-name to save photo taken by Camera activity
        String fileName = "new-photo-name.jpg";
        //create parameters for Intent with filename
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
        //imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //create new Intent
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);*/
    }


}


