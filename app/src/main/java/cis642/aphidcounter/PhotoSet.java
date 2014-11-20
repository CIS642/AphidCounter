package cis642.aphidcounter;

import android.graphics.Bitmap;

import org.opencv.core.Mat;

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
     * The index of this photoset in PhotoSetManager's list of PhotoSets.
     */
    private int index;
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
     * A list of all the photo filenames for this set.
     */
    private ArrayList<String> photos;

    /**
     * A list of all the converted photo filenames for this set.
     */
    private ArrayList<String> convertedPhotos;

    /**
     * Constructs a new PhotoSet object, which contains a set of photos and information about
     * the photos.
     * @param bugType The type of bug that was photographed.
     * @param field The field where these photos were taken from.
     * @param dateTaken The date the photos were taken on.
     */
    public PhotoSet(String bugType, Field field, GregorianCalendar dateTaken) {
        photos = new ArrayList<String>();
        this.bugType = bugType;
        this.field = field;
        this.dateTaken = dateTaken;
    }

    /**
     * Get the index of this photoset.
     * @return This photoset's index number.
     */
    public int GetPhotoSetIndex() { return this.index; }

    /**
     * Set the index of this photoset.
     * @param i The index of the photoset.
     */
    public void SetPhotoSetIndex(int i) { this.index = i; }
    /**
     * Gets the type of bug in this photo set.
     * @return The type of bug that was photographed.
     */
    public String GetBugType() {
        return this.bugType;
    }

    /**
     * Set the bug type.
     * @param bugType The type of bug.
     */
    public void SetBugType(String bugType) { this.bugType = bugType; }

    /**
     * Gets the field where these photos were taken from.
     * @return The field where these photos were taken.
     */
    public Field GetField() {
        return this.field;
    }

    /**
     * Set the field where the photo was taken.
     * @param field The field.
     */
    public void SetField(Field field) { this.field = field; }

    /**
     * Gets the date the photos were taken on.
     * @return The date the photos were taken on.
     */
    public String GetDateTaken() {
        return this.dateTaken.get(dateTaken.YEAR) + "." +
               this.dateTaken.get(dateTaken.MONTH) + "." +
               this.dateTaken.get(dateTaken.DAY_OF_MONTH);
    }

    /**
     * Set the date taken.
     * @param dateTaken The date this photoset was taken.
     */
    public void SetDateTaken(GregorianCalendar dateTaken) { this.dateTaken = dateTaken; }

    /**
     * Get a photo at a specific index.
     * @param i The index in the array list for the photo.
     * @return The photo at the given index.
     */
    public String GetPhoto(int i)
    {
        if (i < this.photos.size())
            return photos.get(i);
        return null;
    }

    /**
     * Add a photo to the list of photos in this photo set.
     * @param photo The photo to add.
     */
    public void AddPhoto(String photo)
    {
        photos.add(photo);
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
