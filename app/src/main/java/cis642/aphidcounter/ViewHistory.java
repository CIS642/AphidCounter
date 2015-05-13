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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import cis642.aphidcounter.manager.PhotoSetManager;

/**
 * This class creates the UI for viewing all of the photo sets that have been previously taken.
 */
public class ViewHistory extends Activity {

    // The index of each item in the drop down spinner.
    private final int APHID_COUNT_HIGH_TO_LOW = 1;
    private final int APHID_COUNT_LOW_TO_HIGH = 2;
    private final int DATE_NEW_TO_OLD = 3;
    private final int DATE_OLD_TO_NEW = 4;
    private final int FIELD_NAME_ASCENDING = 5;
    private final int FIELD_NAME_DESCENDING = 6;

    /**
     * Array for holding photo sets. They will be stored in an array to make it easier
     * to sort by a specific criteria.
     */
    private PhotoSet photoSet[];

    /**
     * A list of photosets.
     */
    //private ArrayList<PhotoSet> photoSets = new ArrayList<PhotoSet>();
    private static PhotoSetManager psManager = PhotoSetManager.GetInstance();

    /**
     * A list of buttons that will be clicked to view each photo set in detail.
     */
    private ArrayList<Button> buttons = new ArrayList<Button>();

    private LinearLayout historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history);

        historyList = (LinearLayout) findViewById(R.id.historyLayout);

        // Populate the area with every photo set.
        initializePhotoSetArray();

        // Set the event handler for the Sort By dropdown menu
        setSortBySpinnerListener();

        // Set the event handler for the go back button:
        SetBackButtonListener();

        // Initially sort the Array by Date - New to Old
        sortDateNewToOld();

        // Create the buttons to view photo sets.
        CreateButtons();
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
     * Build the array that will hold the photo sets.
     */
    private void initializePhotoSetArray(){

        photoSet = new PhotoSet[psManager.Count()];

        for (int i = 0; i < photoSet.length; i++) {
            photoSet[i] = psManager.Get(i);
        }

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
     * Set the event handler for the 'sort by' dropdown menu.
     */
    private void setSortBySpinnerListener() {

        Spinner sortBySpinner = (Spinner) findViewById(R.id.sortBySpinner);

        sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                // Handle the event based on which item them chose
                // Note: 'position' is the 0-based index of the drop down menu.
                switch (position)
                {
                    case APHID_COUNT_HIGH_TO_LOW:
                        sortAphidCountHighToLow();
                        CreateButtons();
                        break;
                    case APHID_COUNT_LOW_TO_HIGH:
                        sortAphidCountLowToHigh();
                        CreateButtons();
                        break;
                    case DATE_NEW_TO_OLD:
                        sortDateNewToOld();
                        CreateButtons();
                        break;
                    case DATE_OLD_TO_NEW:
                        sortDateOldToNew();
                        CreateButtons();
                        break;
                    case FIELD_NAME_ASCENDING:
                        sortFieldNameAscending();
                        CreateButtons();
                        break;
                    case FIELD_NAME_DESCENDING:
                        sortFieldNameDescending();
                        CreateButtons();
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

    }

    /**
     * Create a button for each Photoset.
     * Each button will have the field name and Date the photos were taken.
     * Clicking the button will then start a new activity, showing the
     * bug type, crop type, and avg number of aphids for the entire set.
     */
    private void CreateButtons()
    {
        // Remove any previous buttons from the linear layout.
        historyList.removeAllViews();

        GregorianCalendar photoSetDate = new GregorianCalendar();

        for (int i = 0; i < photoSet.length; i++)
        {

            // Compare the date of this photoset with the date of the previous.
            // If the date is different, a new TextView with the current photo set's date
            // will be added to the view, to seperate photosets taken on different days.
//            if (!DateToString(photoSetDate).equals(DateToString(photoSet[i].GetDate())))
//            {
//                photoSetDate = photoSet[i].GetDate();
//                historyList.addView(CreateDateHeader(photoSetDate));
//            }

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
            button.setText(photoSet[i].GetField().name() + " - " +
                    photoSet[i].GetDateTaken() + " - " +
                    "Photo Count: " + photoSet[i].GetPhotoCount());

            // The object that will be passed through the button's intent must be declared final.
            final PhotoSet ps = photoSet[i];

            //final String photoSetIndex = Integer.toString(i);
            final String photoSetId = photoSet[i].GetPhotoSetID();
            // Create the intent for this button.
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent myIntent = new Intent(view.getContext(), ViewPhotoSet.class);
                    myIntent.putExtra("PhotoSetId", photoSetId);
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
                (date.get(date.MONTH)) + "." +
                date.get(date.DAY_OF_MONTH);
    }

    /**
     * Sorts the photosets by aphid count (high to low)
     */
    private void sortAphidCountHighToLow() {

        PhotoSet temp;
        int x, y;

        for (int i = 0; i < photoSet.length; i++) {
            for (int j = 0; j < photoSet.length - 1; j++) {

                x = photoSet[j].GetAverageAphidCount();
                y = photoSet[j + 1].GetAverageAphidCount();

                if (x < y) {
                    temp = photoSet[j + 1];
                    photoSet[j + 1] = photoSet[j];
                    photoSet[j] = temp;
                }

            }
        }

    }

    /**
     * Sorts the photosets by aphid count (low to high)
     */
    private void sortAphidCountLowToHigh() {

        PhotoSet temp;
        int x, y;

        for (int i = 0; i < photoSet.length; i++) {
            for (int j = 0; j < photoSet.length - 1; j++) {

                x = photoSet[j].GetAverageAphidCount();
                y = photoSet[j + 1].GetAverageAphidCount();

                if (x > y) {
                    temp = photoSet[j + 1];
                    photoSet[j + 1] = photoSet[j];
                    photoSet[j] = temp;
                }

            }
        }

    }

    /**
     * Sorts the photosets by date (new to old)
     */
    private void sortDateNewToOld() {

        PhotoSet temp;
        GregorianCalendar x, y;

        for (int i = 0; i < photoSet.length; i++) {
            for (int j = 0; j < photoSet.length - 1; j++) {

                x = photoSet[j].GetDate();
                y = photoSet[j + 1].GetDate();

                if (x.compareTo(y) < 0) {
                    temp = photoSet[j + 1];
                    photoSet[j + 1] = photoSet[j];
                    photoSet[j] = temp;
                }

            }
        }

    }

    /**
     * Sorts the photosets by date (old to new)
     */
    private void sortDateOldToNew() {

        PhotoSet temp;
        GregorianCalendar x, y;

        for (int i = 0; i < photoSet.length; i++) {
            for (int j = 0; j < photoSet.length - 1; j++) {

                x = photoSet[j].GetDate();
                y = photoSet[j + 1].GetDate();

                if (x.compareTo(y) > 0) {
                    temp = photoSet[j + 1];
                    photoSet[j + 1] = photoSet[j];
                    photoSet[j] = temp;
                }

            }
        }

    }

    /**
     * Sorts the photosets by field name (ascending order)
     */
    private void sortFieldNameAscending() {

        PhotoSet temp;
        String x, y;

        for (int i = 0; i < photoSet.length; i++) {
            for (int j = 0; j < photoSet.length - 1; j++) {

                x = photoSet[j].GetField().name().toLowerCase();
                y = photoSet[j + 1].GetField().name().toLowerCase();

                if (x.compareTo(y) > 0) {
                    temp = photoSet[j + 1];
                    photoSet[j + 1] = photoSet[j];
                    photoSet[j] = temp;
                }

            }
        }

    }

    /**
     * Sorts the photosets by field name (descending order)
     */
    private void sortFieldNameDescending() {

        PhotoSet temp;
        String x, y;

        for (int i = 0; i < photoSet.length; i++) {
            for (int j = 0; j < photoSet.length - 1; j++) {

                x = photoSet[j].GetField().name().toLowerCase();
                y = photoSet[j + 1].GetField().name().toLowerCase();

                if (x.compareTo(y) < 0) {
                    temp = photoSet[j + 1];
                    photoSet[j + 1] = photoSet[j];
                    photoSet[j] = temp;
                }

            }
        }

    }

}
