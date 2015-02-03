package cis642.aphidcounter.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Staton on 11/30/2014.
 * This class manages the Photos.txt file that will be used to store data about the photos,
 * such as filename of original photo, converted, date, field, bug, aphid count, etc.
 */
public class PhotoManager
{
    /**
     * Column indices for the CSV file.
     */
    public final int ORIGINAL_PHOTO = 0, CONVERTED_PHOTO = 1, DATE_TAKEN = 2, FIELD_NAME = 3, CROP_TYPE = 4, BUG_TYPE = 5, APHID_COUNT = 6;

    /**
     * Column names for the CSV file.
     */
    public final String CSV_COL_NAMES = "originalphoto,convertedphoto,date,field,crop,bug,aphidcount", NOT_CONVERTED = "notconverted";

    /**
     * The single instance of this class.
     */
    private static PhotoManager instance = null;

    /**
     * The list containing data for a photo.
     */
    private ArrayList<String> photoList;

    /**
     * File manager object.
     */
    private FileManager fileManager;

    /**
     * The file where the photo information will be stored.
     */
    private File photostxt;

    /**
     * Get the number of converted photos.
     */
    private int convertedPhotoCount;

    /**
     * Constructs a new photo manager object.
     */
    private PhotoManager()
    {
        photoList = new ArrayList<String>();
        fileManager = new FileManager();
        photostxt = fileManager.GetPhotosDataFile(); //new File(fileManager.GetPhotoSetDirectory() + File.separator + fileManager.GetPhotosDataFile());
        convertedPhotoCount = 0;

        LoadPhotosFromFile();
    }

    /**
     * Gets the single instance of this class.
     * @return
     */
    public static PhotoManager GetInstance()
    {
        if (instance == null)
            instance = new PhotoManager();
        return instance;
    }

    public String GetPhotoInfo(int i)
    {
        if (i < photoList.size())
            return photoList.get(i);
        return null;
    }

    /**
     * Get the aphid count of a specific photo.
     * @param originalPhotoName The name of the photo file.
     * @return
     */
    public int GetAphidCount(String originalPhotoName)
    {
        for (int i = 0; i < photoList.size(); i++)
        {
            String[] info = photoList.get(i).split(",", -1);
            if (info[ORIGINAL_PHOTO].equals(originalPhotoName))
                return Integer.parseInt(info[APHID_COUNT]);
        }
        return 0;
    }

    public void SetConvertedInfo(int i, String filename, int aphidCount)
    {
        if (i < photoList.size())
        {
            String[] str = photoList.get(i).split(",", -1);
            str[CONVERTED_PHOTO] = filename;
            str[APHID_COUNT] = Integer.toString(aphidCount);

            photoList.set(i, str[ORIGINAL_PHOTO] + "," + str[CONVERTED_PHOTO] + "," + str[DATE_TAKEN] + "," + str[FIELD_NAME] + "," + str[CROP_TYPE] + "," + str[BUG_TYPE] + "," + str[APHID_COUNT]);
            convertedPhotoCount++;
        }

        SaveListToFile();
    }

    /**
     * Get the number of photos.
     * @return
     */
    public int GetPhotoCount()
    {
        return photoList.size();
    }

    /**
     * Get the count of converted photos.
     * @return
     */
    public int GetConvertedPhotoCount() {return convertedPhotoCount; }

    /**
     * Add new photo data to the list.
     * @param photoInfo CSV line, with photo filename, field, date, etc.
     */
    public void AddPhoto(String photoInfo)
    {
        photoList.add(photoInfo);

        SaveListToFile();
    }

    /**
     * Add a converted photo to the data set.
     * @param photoFileName filename of the converted photo.
     * @param aphidCount Number of aphids counted in the photo.
     */
    public void AddConvertedPhoto(String photoFileName, int aphidCount)
    {
        // Get the original photo name by splitting the converted file name.
        String originalName = photoFileName.split("_", -1)[1];

        for (int i = 0; i < photoList.size(); i++)
        {
            // Read the CSV line from the photolist & store it in array by splitting it up.
            String[] str = photoList.get(i).split(",", -1);

            if (originalName.equals(str[ORIGINAL_PHOTO]))
            {
                try
                {
                    // Change the CSV cell to the converted file name.
                    str[CONVERTED_PHOTO] = photoFileName;
                    // Change this CSV cell to the aphid count.
                    str[APHID_COUNT] = Integer.toString(aphidCount);

                    // Insert the updated line into the photo list.
                    photoList.set(i, str[ORIGINAL_PHOTO] + "," + str[CONVERTED_PHOTO] + "," + str[DATE_TAKEN] + "," + str[FIELD_NAME] + "," + str[CROP_TYPE] + "," + str[BUG_TYPE] + "," + str[APHID_COUNT]);

                    // Save the photo list to the Photos.txt file.
                    SaveListToFile();

                } catch (IndexOutOfBoundsException ex) { ex.printStackTrace(); }

                i = photoList.size();
            }

        }

    }

    /**
     * Save the Array list to the CSV file.
     */
    private void SaveListToFile()
    {
        if (photostxt.exists())
        {
            try
            {
                File temp = new File(fileManager.GetPhotoSetDirectory() + File.separator + "photostemp.txt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(temp));

                writer.write(CSV_COL_NAMES + "\r\n");
                for (int i = 0; i < photoList.size(); i++)
                    writer.write(photoList.get(i) + "\r\n");

                writer.close();

                temp.renameTo(photostxt);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Removes all the photo data for all photos that correspond to the given photoset id.
     * @param photoSetID ID of the photo set whose image data will be removed.
     */
    public void RemovePhotos(String photoSetID)
    {
        boolean hasRemoved = false;

        for (int i = 0; i < photoList.size(); i++)
        {
            String[] str = photoList.get(i).split(",", -1);
            String originalPhoto = str[ORIGINAL_PHOTO];
            String psID = originalPhoto.split("-", -1)[0];

            if (photoSetID.equals(psID))
            {
                if (!str[CONVERTED_PHOTO].equals(NOT_CONVERTED))
                    convertedPhotoCount--;

                photoList.remove(i);
                i--;
                hasRemoved = true;
            }

        }

        if (hasRemoved)
            SaveListToFile();
    }

    /**
     * Loads the photo data from the Photos.txt file into the array list.
     */
    private void LoadPhotosFromFile()
    {

        if (photostxt.exists())
        {
            try
            {
                BufferedReader br = new BufferedReader(new FileReader(photostxt));
                String line = br.readLine(); // read the header line first, to prevent it from being processed.

                while ((line = br.readLine()) != null)
                {
                    if (!line.split(",", -1)[CONVERTED_PHOTO].equals(NOT_CONVERTED))
                        convertedPhotoCount++;

                    photoList.add(line);
                }

                br.close();

            } catch (Exception ex) { ex.printStackTrace(); }
        }

    }

}
