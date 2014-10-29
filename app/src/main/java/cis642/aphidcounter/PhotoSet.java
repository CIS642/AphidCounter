package cis642.aphidcounter;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Staton on 10/22/2014.
 */
public class PhotoSet {

    /**
     * The type of bugs in this set of photos.
     */
    private String bugType;

    // TODO: replace field name and croptype with field object.

    /**
     * The type of crop for this set of photos.
     */
    private String cropType;

    /**
     * The name of the crop field which this set of photos was taken in.
     */
    private String fieldName;

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
     * @param cropType The type of crop that the bug was photographed on.
     * @param fieldName The name of the crop field that the photos were taken in.
     * @param dateTaken The date the photos were taken on.
     */
    public PhotoSet(String bugType, String cropType, String fieldName, GregorianCalendar dateTaken) {
        this.bugType = bugType;
        this.cropType = cropType;
        this.fieldName = fieldName;
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
     * Gets the type of crop the bug was photographed on.
     * @return The crop type the bug was photographed on.
     */
    public String GetCropType() {
        return this.cropType;
    }

    /**
     * Gets the name of the crop field that the photos were taken in.
     * @return The name of the crop field the photos were taken in.
     */
    public String GetFieldName() {
        return this.fieldName;
    }

    /**
     * Gets the date the photos were taken on.
     * @return The date the photos were taken on.
     */
    public String GetDateTaken() {

        return this.dateTaken.get(dateTaken.MONTH) + " " +
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



}
