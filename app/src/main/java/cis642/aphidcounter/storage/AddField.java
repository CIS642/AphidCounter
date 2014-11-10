package cis642.aphidcounter.storage;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import cis642.aphidcounter.R;

/**
 * Created by JacobLPruitt on 10/26/2014.
 */
public class AddField extends Activity implements View.OnClickListener{
    public String fieldName;
    public String cropType;

    private DatabaseOpenHelper mDbHelper;
    private SimpleCursorAdapter mAdapter;

    Button addFieldButton;
    EditText textBox_FieldName;
    Spinner spinner_FieldCrop;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_field_layout);

        mDbHelper = new DatabaseOpenHelper(this);

        addFieldButton = (Button) findViewById(R.id.createFieldButton);
        addFieldButton.setOnClickListener(this);

        textBox_FieldName = (EditText) findViewById(R.id.fieldNameTextBox);
        spinner_FieldCrop = (Spinner) findViewById(R.id.addFieldCropSpinner);

        /*addFieldButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {

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
        });*/
    }




    public void onClick(View v){
        if(v.getId() == R.id.createFieldButton){
            fieldName = textBox_FieldName.getText().toString();
            cropType = spinner_FieldCrop.getSelectedItem().toString();
            ContentValues values = new ContentValues();
            values.put(DatabaseOpenHelper.FIELD_NAME,fieldName);
            values.put(DatabaseOpenHelper.FIELD_CROP,cropType);
            mDbHelper.getWritableDatabase().insert(DatabaseOpenHelper.TABLE_NAME, null, values);
            values.clear();
            finish();
        }
    }
}
