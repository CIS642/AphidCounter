package cis642.aphidcounter;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.Button;

/**
 * Created by JacobLPruitt on 10/13/2014.
 */
public class HelpScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_screen_layout);
        Button returnButton = (Button) findViewById(R.id.helpBackButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MyActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

    }
}
