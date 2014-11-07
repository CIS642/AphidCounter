package cis642.aphidcounter.storage;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.io.Serializable;
import java.util.ArrayList;

import cis642.aphidcounter.R;
import cis642.aphidcounter.entity.Field;

/**
 * Created by JacobLPruitt on 10/26/2014.
 */
public class AddField extends Activity implements Serializable {
    public String name;
    public String cropType;

    private DatabaseOpenHelper mDbHelper;
    private SimpleCursorAdapter mAdapter;

    private ArrayList<Field> listOfFields;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_field_layout);

        mDbHelper = new DatabaseOpenHelper(this);
        //Intent myIntent = getIntent();
        //listOfFields = (ArrayList<Field>) myIntent.getSerializableExtra("fields");
        Button addFieldButton = (Button) findViewById(R.id.createFieldButton);
        addFieldButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                EditText addFieldText = (EditText) findViewById(R.id.fieldNameTextBox);
                name = addFieldText.getText().toString();
                Spinner addFieldSpinner = (Spinner) findViewById(R.id.addFieldCropSpinner);
                cropType = addFieldSpinner.getSelectedItem().toString();
                ContentValues values = new ContentValues();

                values.put(DatabaseOpenHelper.FIELD_NAME,name);
                values.put(DatabaseOpenHelper.FIELD_CROP,cropType);
                mDbHelper.getWritableDatabase().insert(DatabaseOpenHelper.TABLE_NAME, null, values);
                values.clear();

                Field f = new Field(name, cropType);
                Intent i = new Intent(view.getContext(), SelectField.class);
                i.putExtra("AddField", f);
                startActivityForResult(i, 0);

                //listOfFields.add(f);

                //Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(view.getContext(), SelectField.class);
                //intent.putExtra("newFields", listOfFields);
                //startActivityForResult(intent, 0);
            }
        });
    }



}
