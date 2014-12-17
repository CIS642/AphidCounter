package cis642.aphidcounter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import cis642.aphidcounter.manager.PhotoSetManager;

/**
 * This class creates the UI for viewing all of the photo sets that have been previously taken.
 */
public class ViewHistory extends Activity {

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
        setContentView(R.layout.activity_view_history);

        // Set the event handler for the go back button:
        SetBackButtonListener();

        LinearLayout historyList = (LinearLayout) findViewById(R.id.historyLayout);

        // Create the buttons that will appear on the screen, which the user will click on
        // to view the photoset.
        CreateButtons(historyList);
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
     * Set the event handler for the 'Back' button click.
     */
    private void SetBackButtonListener()
    {
        Button goBack = (Button) findViewById(R.id.goBackButton);

        goBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }

        });
    }

    /**
     * Create a button for each Photoset.
     * Each button will have the field name and Date the photos were taken.
     * CLicking the button will then start a new activity, showing the
     * bug type, crop type, and avg number of aphids for the entire set.
     */
    private void CreateButtons(LinearLayout historyList)
    {
        GregorianCalendar photoSetDate = new GregorianCalendar();
        boolean showDateFlag = true;

        for (int i = (psManager.Count() - 1); i >= 0; i--)
        {

            // Compare the date of this photoset with the date of the previous.
            // If the date is different, a new TextView with the current photo set's date
            // will be added to the view, to seperate photosets taken on different days.
            if (!DateToString(photoSetDate).equals(DateToString(psManager.Get(i).GetDate())))
            {
                photoSetDate = psManager.Get(i).GetDate();
                showDateFlag = true;
            }

            if (showDateFlag)
            {
                historyList.addView(CreateDateHeader(photoSetDate));
                showDateFlag = false;
            }

            // Create a button for the ith index in the photoSets list.
            Button button = new Button(this);

            // Set the parameters of the button:
            button.setPadding(5, 10, 5, 10);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 10, 0, 0);

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

            historyList.addView(button, layoutParams);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0)
        {
            if (resultCode == 0)
                finish();
        }

    }

    /**
     * Create a header that displays the date.
     * @param photoSetDate The date to display.
     * @return Text view to be added to the main view.
     */
    private TextView CreateDateHeader(GregorianCalendar photoSetDate)
    {
        TextView tv = new TextView(this);
        tv.setText(DateToString(photoSetDate));
        tv.setTextSize(16);
        tv.setPadding(2, 10, 2, 0);

        return tv;
    }

    /**
     * Converts the date into a readable string.
     * @param date The date.
     * @return The date as a string that is easy to read
     */
    private String DateToString(GregorianCalendar date)
    {
        return date.get(date.YEAR) + "." +
                date.get(date.MONTH) + "." +
                date.get(date.DAY_OF_MONTH);
    }

    /**
     * Creates the button to go back to the parent activity when pressed.
     */
    /*private Button CreateBackButton()
    {
        Button goBack = new Button(this);

        goBack.setText("< Back");
        // Set the styling of the button:
        goBack.setBackgroundColor(Color.parseColor("#009900"));
        goBack.setTextColor(Color.parseColor("#FFFFFF"));
        goBack.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

    }*/
}
