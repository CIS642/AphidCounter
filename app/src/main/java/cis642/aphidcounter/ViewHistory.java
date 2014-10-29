package cis642.aphidcounter;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.GregorianCalendar;


public class ViewHistory extends ActionBarActivity {

    /**
     * A list of photosets.
     */
    private ArrayList<PhotoSet> photoSets = new ArrayList<PhotoSet>();

    private ArrayList<Button> buttons = new ArrayList<Button>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_view_history);

        ScrollView scrollView = new ScrollView(this);
        RelativeLayout relativeLayout = new RelativeLayout(this);

        RelativeLayout.LayoutParams relativeLayoutParams =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.FILL_PARENT,
                        RelativeLayout.LayoutParams.FILL_PARENT);

        scrollView.setLayoutParams(relativeLayoutParams);
        TestMakePhotoSets();
        CreateButtons();

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
        int bottomMargin;
        String bugType, cropType, field, date;
        for (int i = 0; i < photoSets.size(); i++) {

            bugType = photoSets.get(i).GetBugType();
            date = photoSets.get(i).GetDateTaken();

            // Create a button for the ith index in the photoSets list.
            Button button = new Button(this);

            button.setText(photoSets.get(i).GetFieldName() + " - " +
                           photoSets.get(i).GetDateTaken());

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent myIntent = new Intent(view.getContext(), ViewPhotoSet.class);
                    startActivityForResult(myIntent, 0);
                }
            });

            topMargin = i * 60;
            bottomMargin = topMargin + 65;
            SetButtonLayout(button, RelativeLayout.ALIGN_PARENT_LEFT, 20, topMargin, 20, 5);

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
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                RelativeLayout.LayoutParams.WRAP_CONTENT);

        buttonLayoutParameters.setMargins(marginLeft, marginTop, marginRight, marginBottom);

        button.setLayoutParams(buttonLayoutParameters);
    }

    /**
     * For testing.
     */
    private void TestMakePhotoSets() {

        //field type and name will be replaced by a field object containing type, name, and location.

        photoSets.add(new PhotoSet("Aphid", "Soy Bean", "Field 1", new GregorianCalendar(2014, 9, 29)));
        photoSets.add(new PhotoSet("Aphid", "Soy Bean", "Field 3", new GregorianCalendar(2014, 10, 2)));
        photoSets.add(new PhotoSet("Aphid", "Soy Bean", "Field 4", new GregorianCalendar(2014, 10, 20)));
        photoSets.add(new PhotoSet("Aphid", "Soy Bean", "Field 3", new GregorianCalendar(2014, 10, 22)));
        photoSets.add(new PhotoSet("Aphid", "Soy Bean", "Field 2", new GregorianCalendar(2014, 11, 7)));
        photoSets.add(new PhotoSet("Aphid", "Soy Bean", "Field 8", new GregorianCalendar(2014, 11, 15)));
        photoSets.add(new PhotoSet("Aphid", "Soy Bean", "Field 6", new GregorianCalendar(2014, 11, 19)));
        photoSets.add(new PhotoSet("Aphid", "Soy Bean", "Field 6", new GregorianCalendar(2014, 11, 20)));
        photoSets.add(new PhotoSet("Aphid", "Soy Bean", "Field 6", new GregorianCalendar(2014, 11, 22)));
        photoSets.add(new PhotoSet("Aphid", "Soy Bean", "Field 6", new GregorianCalendar(2014, 11, 28)));
    }

}
