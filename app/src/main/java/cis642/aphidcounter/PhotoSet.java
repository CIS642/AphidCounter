package cis642.aphidcounter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.DateFormatSymbols;

import cis642.aphidcounter.entity.Field;

/**
 * Created by Staton on 10/22/2014.
 */
public class PhotoSet implements Serializable {

    /**
     * The type of bugs in this set of photos.
     */
    private String bugType;

    // TODO: replace field name and croptype with field object.

    /**
     * The field where this set of photos was taken from.
     */
    private Field field;

    /**
     * The date these photos were taken on.
     */
    private GregorianCalendar dateTaken;

    /**
     * A list of all the photos for this set.
     */
    private ArrayList<String> photos; // change <AphidPhoto>

    /**
     * Constructs a new PhotoSet object, which contains a set of photos and information about
     * the photos.
     * @param bugType The type of bug that was photographed.
     * @param field The field where these photos were taken from.
     * @param dateTaken The date the photos were taken on.
     */
    public PhotoSet(String bugType, Field field, GregorianCalendar dateTaken) {
        this.bugType = bugType;
        this.field = field;
        this.dateTaken = dateTaken;
    }

    /**
     * Gets the type of bug in this photo set.
     * @return The type of bug that was photographed.
     */
    public String GetBugType() {
        return this.bugType;
    }

    /**
     * Gets the field where these photos were taken from.
     * @return The field where these photos were taken.
     */
    public Field GetField() {
        return this.field;
    }

    /**
     * Gets the date the photos were taken on.
     * @return The date the photos were taken on.
     */
    public String GetDateTaken() {
        return GetMonthName(this.dateTaken.get(dateTaken.MONTH)) + " " +
               this.dateTaken.get(dateTaken.DAY_OF_MONTH) + ", " +
               this.dateTaken.get(dateTaken.YEAR);
    }

    /**
     * Gets the number of photos in this set.
     * @return The number of photos in this photo set.
     */
    public int GetPhotoCount() {
        return this.photos.size();
    }

    /**
     * Gets a string of the month based on an integer number.
     * @param month Integer representing the month. 1 through 12.
     * @return The month's name. IE, if month = 3, returns "March".
     */
    private String GetMonthName(int month)
    {
        if (month < 1 || month > 12)
            return new DateFormatSymbols().getMonths()[0];

        return new DateFormatSymbols().getMonths()[month - 1];
    }

}
