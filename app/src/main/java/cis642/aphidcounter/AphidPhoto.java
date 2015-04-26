package cis642.aphidcounter;

import java.util.GregorianCalendar;

import cis642.aphidcounter.entity.Field;

/**
 * Created by Staton on 11/26/2014.
 */
public class AphidPhoto
{

    /**
     * The number of aphids in this photo.
     */
    private int aphidCount = 0;

    /**
     * The filename of the photo.
     */
    private String photoName = "";

    /**
     * The field the photo was taken in.
     */
    private Field field;

    /**
     * The date this photo was taken.
     */
    private GregorianCalendar dateTaken;

    /**
     * Constructs a new Aphid Photo object.
     * @param photoName The file name of the photo.
     * @param field The field the photo was taken in.
     */
    public AphidPhoto(String photoName, Field field, GregorianCalendar dateTaken)
    {
        this.photoName = photoName;
        this.field = field;
        this.dateTaken = dateTaken;
    }

    /**
     *  Gets the aphid count in this photo.
     * @return The aphid count in this photo.
     */
    public int GetAphidCount() { return this.aphidCount; }

    /**
     * Sets the aphid count for this photo.
     * @param aphidCount The number of aphids.
     */
    public void SetAphidCount(int aphidCount) { this.aphidCount = aphidCount; }

    /**
     * Get the filename of this photo.
     * @return this photo's filename.
     */
    public String GetPhotoName() { return this.photoName; }

    /**
     * Set the filename for this photo.
     * @param photoName Photo's filename.
     */
    public void SetPhotoName(String photoName) { this.photoName = photoName; }

    /**
     * Gets the field where this photo was taken.
     * @return Field where this photo was taken.
     */
    public Field GetField() { return this.field; }

    /**
     * Sets the field where this photo was taken.
     * @param field Field where this photo was taken.
     */
    public void SetField(Field field) { this.field = field; }

    /**
     * Get the date this photo was taken on.
     * @return Date this photo was taken on.
     */
    public GregorianCalendar GetDateTaken() { return this.dateTaken; }

    /**
     * Set the date this photo was taken on.
     * @param dateTaken The date this photo was taken on.
     */
    public void SetDateTaken(GregorianCalendar dateTaken) { this.dateTaken = dateTaken; }
}
