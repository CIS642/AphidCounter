package cis642.aphidcounter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cis642.aphidcounter.activity.MyActivity;

/**
 * Created by JacobLPruitt on 10/13/2014.
 */
public class AboutScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_screen_layout);
        Button returnButton = (Button) findViewById(R.id.aboutReturnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MyActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

    }
}
