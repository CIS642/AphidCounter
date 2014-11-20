package cis642.aphidcounter.activity;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.lang.Object;
import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.content.ContentValues;

import cis642.aphidcounter.R;
import cis642.aphidcounter.storage.DatabaseOpenHelper;

/**
 * Created by JacobLPruitt on 9/29/2014.
 */
public class TakePhotos extends Activity{
    ImageView iv;

    private DatabaseOpenHelper mDbHelper;
    private SimpleCursorAdapter mAdapter;
    private Spinner fieldTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_photos);

        mDbHelper = new DatabaseOpenHelper(this);
        fieldTypeSpinner = (Spinner) findViewById(R.id.fieldTypeSpinner);
        Intent myIntent = getIntent();

        Cursor c = mDbHelper.getWritableDatabase().query(DatabaseOpenHelper.TABLE_NAME,DatabaseOpenHelper.columns,null,new String[] {},null,null,null);
        List<String> arraylist = new ArrayList<String>();
        while(c.moveToNext()) {
            arraylist.add(c.getString(1));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraylist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldTypeSpinner.setAdapter(dataAdapter);


        iv = (ImageView) findViewById(R.id.photo_image_view);
        Button next = (Button) findViewById(R.id.accessCameraButton);
        next.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == 0)
        {
            Bitmap theImage = (Bitmap) data.getExtras().get("data");
            iv.setImageBitmap(theImage);

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


}
