package cis642.aphidcounter.manager;

import android.os.Environment;

import java.io.File;

/**
 * Created by Staton on 11/15/2014.
 */
public class FileManager
{
    /**
     * The directory to the PhotoSets folder, which contains the CSV file for saving/loading photosets.
     */
    private final File photoSetDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "AphidCounter" + File.separator + "PhotoSets");

    /**
     * The directory to the Photos folder, where the photos are saved to.
     */
    private final File photosDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "AphidCounter" + File.separator + "Photos");

    /**
     * Directory to where the converted photos will be stored.
     */
    private File convertedPhotosDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "AphidCounter" + File.separator + "ConvertedPhotos");

    /**
     * Name of file to store the photoset information in.
     */
    private final String photoSetFileName = "PhotoSets.txt";

    /**
     * The file where the photoset data will be stored.
     */
    private File photoSetDataFile;

    /**
     * Initialize this class and try to create the directories.
     */
    public FileManager()
    {
        TryCreateDirectories();
    }

    /**
     * Get the directory to the photo set folder.
     * @return
     */
    public File GetPhotoSetDirectory(){ return this.photosDirectory; }

    /**
     * Get the directory to where the photos are stored.
     * @return
     */
    public File GetPhotosDirectory(){ return this.photosDirectory; }

    /**
     * Get the directory to where the converted photos are stored.
     * @return
     */
    public File GetConvertedPhotosDirectory() { return this.convertedPhotosDirectory; }

    /**
     * Get the file where the photo set data is stored.
     * @return
     */
    public File GetPhotoSetDataFile() { return this.photoSetDataFile; }

    /**
     * Attempt to create the directories and files if they do not already exist.
     */
    private void TryCreateDirectories()
    {

        // attempt to create the photo set directory if it does not yet exist:
        if (!photoSetDirectory.exists())
        {
            try
            {
                photoSetDirectory.mkdirs();
            } catch (Exception ex) { ex.printStackTrace(); }
        }


        // attempt to create the photo directory if it does not yet exist:
        if (!photosDirectory.exists())
        {
            try
            {
                photosDirectory.mkdirs();
            } catch (Exception ex) { ex.printStackTrace(); }
        }


        // attemp to create the converted photos directory if it does not yet exist:
        if (!convertedPhotosDirectory.exists())
        {
            try
            {
                convertedPhotosDirectory.mkdirs();
            } catch (Exception ex) { ex.printStackTrace(); }
        }


        photoSetDataFile = new File(photoSetDirectory + File.separator + photoSetFileName);

        // If the file does not exist, create it:
        if (!photoSetDataFile.exists())
        {
            try
            {
                photoSetDataFile.createNewFile();
            } catch (Exception ex) { ex.printStackTrace(); }
        }

    }

    /**
     * Checks to see if a particular photo exists in the directory.
     * @param photoFile A string of the photo file name (ie "151222.jpg")
     * @return True if it exists, false if it does not.
     */
    public boolean PhotoExists(String photoFile)
    {
        return (new File(photosDirectory + File.separator + photoFile)).exists();
    }

    /**
     * Checks to see if a particular photo exists in the directory.
     * @param photoFile A string of the converted photo file name
     * @return True if the photo exists, false if it does not.
     */
    public boolean ConvertedPhotoExists(String photoFile)
    {
        return (new File(convertedPhotosDirectory + File.separator + photoFile)).exists();
    }

    /**
     * Checks to see if the photo set data file exists.
     * @return True if it exists, false if it does not.
     */
    public boolean PhotoSetFileExists()
    {
        return (new File(photoSetDirectory + File.separator + photoSetDataFile)).exists();
    }

}
