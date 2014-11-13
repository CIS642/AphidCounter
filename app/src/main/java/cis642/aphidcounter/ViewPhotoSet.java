package cis642.aphidcounter;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * This class creates the UI for viewing a particular photo set.
 */
public class ViewPhotoSet extends ActionBarActivity {

    private PhotoSet photoSet;
    private ArrayList<TextView> textView = new ArrayList<TextView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the photoset that was passed to this intent:
        photoSet = (PhotoSet) getIntent().getSerializableExtra("PhotoSet");

        // Make the screen scrollable (have a scroll bar)
        ScrollView scrollView = new ScrollView(this);

        RelativeLayout relativeLayout = new RelativeLayout(this);

        // Set the layout parameters.
        RelativeLayout.LayoutParams relativeLayoutParams =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.FILL_PARENT,
                        RelativeLayout.LayoutParams.FILL_PARENT);

        // Create the UI for this page.
        CreateTextViews();

        // Go through each text view and add it to the layout.
        for (int i = 0; i < textView.size(); i ++)
            relativeLayout.addView(textView.get(i));

        scrollView.addView(relativeLayout);
        setContentView(scrollView, relativeLayoutParams);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_photo_set, menu);
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
     * Dynamically creates the UI for this activity. Displays the basic information concerning
     * this photoset.
     */
    private void CreateTextViews() {

        // Create a button for the ith index in the photoSets list.
        TextView textView1 = new TextView(this);

        textView1.setText("Field: " + photoSet.GetField().name());
        SetTextLayout(textView1, RelativeLayout.ALIGN_PARENT_LEFT, 20, 0, 20, 5);
        textView.add(textView1);

        textView1 = new TextView(this);
        textView1.setText("Crop Type: " + photoSet.GetField().GetCropType());
        SetTextLayout(textView1, RelativeLayout.ALIGN_PARENT_LEFT, 20, 20, 20, 5);
        textView.add(textView1);

        textView1 = new TextView(this);
        textView1.setText("Bug Type: " + photoSet.GetBugType());
        SetTextLayout(textView1, RelativeLayout.ALIGN_PARENT_LEFT, 20, 40, 20, 5);
        textView.add(textView1);

        textView1 = new TextView(this);
        textView1.setText("Date: " + photoSet.GetDateTaken());
        SetTextLayout(textView1, RelativeLayout.ALIGN_PARENT_LEFT, 20, 60, 20, 5);
        textView.add(textView1);

        textView1 = new TextView(this);
        textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView1.setText("Avg Bug Count per Leaf: ");
        SetTextLayout(textView1, RelativeLayout.ALIGN_PARENT_LEFT, 20, 80, 20, 5);
        textView.add(textView1);
    }

    /**
     * Sets the layout for the text view.
     * @param tv The text view.
     * @param centered Where to center the text.
     * @param marginLeft Left margin.
     * @param marginTop Top margin.
     * @param marginRight Right margin.
     * @param marginBottom Bottom margin.
     */
    private void SetTextLayout(TextView tv, int centered, int marginLeft, int marginTop,
                               int marginRight, int marginBottom) {

        RelativeLayout.LayoutParams textLayoutParameters =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

        textLayoutParameters.setMargins(marginLeft, marginTop, marginRight, marginBottom);

        tv.setLayoutParams(textLayoutParameters);
    }
}
