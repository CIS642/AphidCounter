package cis642.aphidcounter.storage;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import cis642.aphidcounter.R;
import cis642.aphidcounter.entity.Field;
import cis642.aphidcounter.storage.DatabaseOpenHelper;


/**
 * Created by JacobLPruitt on 10/26/2014.
 */
public class SelectField extends ListActivity {
   //get saved file from android device
    //show list of Fields from saved file
    public ArrayList<Field> listOfFields;

    private DatabaseOpenHelper mDbHelper;
    private SimpleCursorAdapter mAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_field_layout);

        mDbHelper = new DatabaseOpenHelper(this);

        Intent myIntent = getIntent();

        loadFields();
        Field f = (Field) myIntent.getSerializableExtra("AddField");
        if(f != null) {
            Toast.makeText(getApplicationContext(), f.name(), Toast.LENGTH_LONG).show();
            listOfFields.add(f);
        }
        else {
            listOfFields.add(new Field("Field 1", "Soy"));
            listOfFields.add(new Field("Field 2", "Soy"));
            listOfFields.add(new Field("Field 3", "Soy"));
        }

        //listOfFields = (ArrayList<Field>) myIntent.getSerializableExtra("newFields");
        ArrayAdapter<Field> adapter = new ArrayAdapter<Field>(this, android.R.layout.simple_list_item_1, listOfFields);
        ListView lv = (ListView) findViewById(R.id.selectFieldListView);
        lv.setAdapter(adapter);
        ArrayList<Field> listClone = listOfFields;
        saveFields(listClone);
    }

    public void saveFields(ArrayList<Field> fieldList) {
        String fileName = "MyFields.txt";
        String filePath = "MyFileStorage";
        ObjectOutputStream objectOut = null;
        try {
            /*FileOutputStream fos = this.openFileOutput(fileName, Activity.MODE_PRIVATE);
            final OutputStreamWriter osw = new OutputStreamWriter(fos);
            JSONArray array = new JSONArray();
            for(int i = 0; i < fieldList.size(); i++) {
                array.put(fieldList.get(i).getJSONObject());
            }
            osw.write(array.toString());
            osw.flush();
            osw.close();
            Toast.makeText(getApplicationContext(), array.toString(), Toast.LENGTH_LONG).show();*/
            FileOutputStream fos = this.openFileOutput(fileName, this.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            for(int i = 0; i < fieldList.size(); i++) {
                os.writeObject(fieldList.get(i));
            }
            os.flush();
            os.close();

        }
        catch (Exception fnfe) {
            fnfe.printStackTrace();
        }
    }
    private Cursor getAllFields(){
        return mDbHelper.getWritableDatabase().query(DatabaseOpenHelper.TABLE_NAME,DatabaseOpenHelper.columns,null,new String[] {},null,null,null);
    }
    private void loadFields() {
        listOfFields = new ArrayList<Field>();
        Cursor c = getAllFields();

        mAdapter = new SimpleCursorAdapter(this,R.layout.select_field_layout,c,DatabaseOpenHelper.columns,new int []{R.id.selectFieldListView});
        /*try {

            //FileInputStream fis = this.openFileInput("MyFields.txt");
            //ObjectInputStream ois = new ObjectInputStream(fis);
            //listOfFields = ois.readObject();

            //fis.close();

           // ois.close();
            /*final InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader buffReader = new BufferedReader(isr);
            String readString = buffReader.readLine();
            while(readString != null) {
                datax.append(readString);
                readString = buffReader.readLine();
            }
            //isr.close();

        }
        catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        catch (IOException e) {

            e.printStackTrace();
        }*/
    }
}
