package cis642.aphidcounter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import cis642.aphidcounter.entity.Field;
import cis642.aphidcounter.manager.PhotoSetManager;

/**
 * This class creates the UI for viewing all of the photo sets that have been previously taken.
 */
public class ViewHistory extends ActionBarActivity {

    /**
     * A list of photosets.
     */
    //private ArrayList<PhotoSet> photoSets = new ArrayList<PhotoSet>();
    private static PhotoSetManager psManager = PhotoSetManager.GetInstance();

    /**
     * A list of buttons that will be clicked to view each photo set in detail.
     */
    private ArrayList<Button> buttons = new ArrayList<Button>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make this screen scrollable (have a scroll bar)
        ScrollView scrollView = new ScrollView(this);

        RelativeLayout relativeLayout = new RelativeLayout(this);

        // Set the layout parameters
        RelativeLayout.LayoutParams relativeLayoutParams =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.FILL_PARENT,
                        RelativeLayout.LayoutParams.FILL_PARENT);

        scrollView.setLayoutParams(relativeLayoutParams);

        // Statically create photosets, for testing.
        //TestMakePhotoSets();

        //psManager.SerializeList();



        // Create the buttons that will appear on the screen, which the user will click on
        // to view the photoset.
        CreateButtons();

        // Add each button to the layout.
        for (int i = 0; i < buttons.size(); i ++)
            relativeLayout.addView(buttons.get(i));

        scrollView.addView(relativeLayout);
        setContentView(scrollView, relativeLayoutParams);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_history, menu);
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
     * Create a button for each Photoset.
     * Each button will have the field name and Date the photos were taken.
     * CLicking the button will then start a new activity, showing the
     * bug type, crop type, and avg number of aphids for the entire set.
     */
    private void CreateButtons() {

        int topMargin = 0;

        // Loop through each photoset in the photosets list, and create a button that can be
        // clicked on in order to view the photoset in detail.
        for (int i = 0; i < psManager.Count(); i++) {

            // Create a button for the ith index in the photoSets list.
            Button button = new Button(this);

            // Set the styling of the button:
            button.setBackgroundColor(Color.parseColor("#CC333333"));
            button.setTextColor(Color.parseColor("#FFFFFF"));
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

            // Set the text that will appear on the button.
            // Currently displays the field name and date taken.
            button.setText(psManager.Get(i).GetField().name() + " - " +
                           psManager.Get(i).GetDateTaken() + " - " +
                           "Photo Count: " + psManager.Get(i).GetPhotoCount());

            // The object that will be passed through the button's intent must be declared final.
            final PhotoSet ps = psManager.Get(i);

            final String photoSetIndex = Integer.toString(i);

            // Create the intent for this button.
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent myIntent = new Intent(view.getContext(), ViewPhotoSet.class);
                    myIntent.putExtra("PhotoSet", photoSetIndex);
                    startActivityForResult(myIntent, 0);
                }
            });

            // Set the layout for where the button will appear on the screen.
            topMargin = (i * 110) + (10 * (i + 1));
            SetButtonLayout(button, RelativeLayout.ALIGN_PARENT_LEFT, 10, topMargin, 10, 5);

            // Add that button the the buttons List.
            buttons.add(button);


        }
    }

    /**
     * Set the parameters for the button layout.
     * @param button The button.
     * @param centered Located where in relation to the parent.
     * @param marginLeft Left margin.
     * @param marginTop Top margin.
     * @param marginRight Right margin.
     * @param marginBottom Button margin.
     */
    private void SetButtonLayout(Button button, int centered, int marginLeft, int marginTop,
                                 int marginRight, int marginBottom) {
        RelativeLayout.LayoutParams buttonLayoutParameters =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                                RelativeLayout.LayoutParams.MATCH_PARENT);

        buttonLayoutParameters.setMargins(marginLeft, marginTop, marginRight, marginBottom);

        button.setLayoutParams(buttonLayoutParameters);
    }

    /**
     * For testing. Statically creates a bunch of photo sets.
     */
    private void TestMakePhotoSets() {

        //field type and name will be replaced by a field object containing type, name, and location.

        psManager.Add(new PhotoSet("Aphid", new Field("Field 1", "Soy Bean"), new GregorianCalendar(2014, 9, 29)));
        psManager.Add(new PhotoSet("Aphid", new Field("Field 97", "Soy Bean"), new GregorianCalendar(2014, 10, 2)));
        psManager.Add(new PhotoSet("Aphid", new Field("Field 54", "Soy Bean"), new GregorianCalendar(2014, 10, 20)));
        psManager.Add(new PhotoSet("Aphid", new Field("Field 3", "Soy Bean"), new GregorianCalendar(2014, 10, 22)));
        psManager.Add(new PhotoSet("Aphid", new Field("Field 5", "Soy Bean"), new GregorianCalendar(2014, 11, 7)));
        psManager.Add(new PhotoSet("Aphid", new Field("Field 2", "Soy Bean"), new GregorianCalendar(2014, 11, 15)));
        psManager.Add(new PhotoSet("Aphid", new Field("Field 4", "Soy Bean"), new GregorianCalendar(2014, 11, 19)));
        psManager.Add(new PhotoSet("Aphid", new Field("Field 8", "Soy Bean"), new GregorianCalendar(2014, 11, 20)));
        psManager.Add(new PhotoSet("Aphid", new Field("Field 6", "Soy Bean"), new GregorianCalendar(2014, 11, 22)));
        psManager.Add(new PhotoSet("Aphid", new Field("Field 7", "Soy Bean"), new GregorianCalendar(2014, 11, 28)));
    }

}
