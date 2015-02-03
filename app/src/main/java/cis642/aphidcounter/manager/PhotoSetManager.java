package cis642.aphidcounter.manager;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import cis642.aphidcounter.PhotoSet;

/**
 * Created by Staton on 11/13/2014.
 */
public class PhotoSetManager
{
    /**
     * File manager
     */
    private FileManager fileManager = new FileManager();

    /**
     * The single instance of this class.
     */
    private static PhotoSetManager instance = null;

    /**
     * The list of photosets.
     */
    private ArrayList<PhotoSet> photoSets = new ArrayList<PhotoSet>();

    /**
     * Constructs a new photo set manager.
     */
    private PhotoSetManager()
    {
        LoadPhotoSets();
    }

    /**
     * Gets the instance of this class.
     * @return The single instance of PhotoSetManager.
     */
    public static PhotoSetManager GetInstance()
    {
        if (instance == null)
            instance = new PhotoSetManager();
        return instance;
    }

    /**
     * Adds a new photo set to the list of photosets.
     * @param ps The new photo set to be added.
     */
    public void Add(PhotoSet ps)
    {
        Log.i("ADD PHOTOSET", "this photoset index set at position " + (photoSets.size() - 1));
        Log.i("ADD PHOTOSET", "current size: " + photoSets.size());

        // Add the photoset to the list.
        photoSets.add(ps);
        Log.i("ADD PHOTOSET", "new photo set added");
        Log.i("ADD PHOTOSET", "new size: " + photoSets.size());
        //SavePhotoSets();
    }

    /**
     * Gets a particular photo set.
     * @param i The index of the photo set.
     * @return The requested photo set.
     */
    public PhotoSet Get(int i)
    {
        if (i < photoSets.size())
            return photoSets.get(i);
        return null;
    }

    /**
     * Gets a photo set by its given ID.
     * @param id The ID of the photoset.
     * @return
     */
    public PhotoSet GetByID(String id)
    {
        for (int i = 0; i < photoSets.size(); i++)
        {
            if (photoSets.get(i).GetPhotoSetID().equals(id))
                return photoSets.get(i);
        }
        return null;
    }

    /**
     * Removes a photo set.
     * @param ps The photo set to remove.
     */
    public void Remove(PhotoSet ps)
    {

        if(photoSets.contains(ps))
        {
            // Remove the photo data from Photos.txt
            PhotoManager.GetInstance().RemovePhotos(ps.GetPhotoSetID());

            // Delete all the photo files first:
            for (int i = 0; i < ps.GetPhotoCount(); i++)
            {
                String photoPath = fileManager.GetPhotosDirectory() + File.separator + ps.GetPhoto(i).GetPhotoName();

                File photoFile = new File(photoPath);

                if (photoFile.exists())
                    photoFile.delete();
            }

            // Delete all the converted photo files:
            for (int j = 0; j < ps.GetConvertedPhotoCount(); j++)
            {
                String photoPath = fileManager.GetConvertedPhotosDirectory() + File.separator + ps.GetConvertedPhoto(j).GetPhotoName();

                File photoFile = new File(photoPath);

                if (photoFile.exists())
                    photoFile.delete();
            }

            // Get the ID of this photo set:
            String id = ps.GetPhotoSetID();

            // Remove the photoset data from Photosets.txt file
            RemovePhotoSetAndSave(id);

            // Remove the photo set from the list.
            photoSets.remove(ps);
        }

    }

    /**
     * Gets the number of photo sets.
     * @return The number of photo sets.
     */
    public int Count()
    {
        return photoSets.size();
    }

    /**
     * Save the photo sets to the photosets data file.
     */
    public void Save()
    {
        SavePhotoSets();
    }

    /**
     * Saves the photo sets.
     */
    private void SavePhotoSets()
    {
        if (fileManager.GetPhotoSetDataFile().exists())
        {
            try
            {
                FileOutputStream fOut = new FileOutputStream(fileManager.GetPhotoSetDataFile());

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fOut);

                for (int i = 0; i < photoSets.size(); i++)
                {
                    String line = CreateCsvString(i);

                    if (!line.equals(""))
                    {
                        outputStreamWriter.write(line);
                        outputStreamWriter.write("\r\n");
                    }

                }

                outputStreamWriter.close();

            } catch (Exception ex) { ex.printStackTrace(); } // error saving file
        }
    }

    /**
     * Loads the photoSets from the Photo set data file and stores them in the Photoset array list.
     */
    private void LoadPhotoSets()
    {
        if (fileManager.GetPhotoSetDataFile().exists())
        {
            try
            {
                FileInputStream fIn = new FileInputStream(fileManager.GetPhotoSetDataFile());

                BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));

                String line;

                while ((line = reader.readLine()) != null)
                {
                    AddToPhotoSets(line);
                }

                reader.close();

            } catch (Exception ex) { ex.printStackTrace(); } //error reading file
        }

    }

    /**
     * Uses the photoset data to create a new photo set and add it to the array list.
     * @param line The CSV line read in from the data file.
     */
    private void AddToPhotoSets(String line)
    {
        photoSets.add(new PhotoSet(line));
    }

    /**
     * Create a CSV string from a photo set.
     * @param i The index of the photoset in the array list.
     * @return CSV string containing photo set data.
     */
    private String CreateCsvString(int i)
    {
        String line = "";

        if (i < photoSets.size()) {
            PhotoSet ps = photoSets.get(i);

            line += ps.GetPhotoSetID() + ",";
            line += ps.GetDateTaken().toString() + ",";
            line += ps.GetBugType() + ",";
            line += ps.GetField().name() + ",";
            line += ps.GetField().GetCropType();

            for (int j = 0; j < ps.GetPhotoCount(); j++)
                line += "," + ps.GetPhoto(j).GetPhotoName();   // add the filename of each photo.

            for (int k = 0; k < ps.GetConvertedPhotoCount(); k++)
                line += "," + ps.GetConvertedPhoto(k).GetPhotoName(); // add filename of each converted photo.
        }

        return line;
    }

    /**
     * Removes the CSV line from the photosets.txt file and saves the updated file.
     * @param id the ID of the photoset to remove.
     */
    private void RemovePhotoSetAndSave(String id)
    {
        File tempFile = new File(fileManager.GetPhotoSetDirectory() + File.separator + "temp.txt");

        if (fileManager.GetPhotoSetDataFile().exists())
        {
            try
            {
                FileInputStream fIn = new FileInputStream(fileManager.GetPhotoSetDataFile());

                BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

                String line;

                while ((line = reader.readLine()) != null)
                {
                    String[] splitLine = line.split(",", -1);

                    // If the ID doesn't equal the ID of the index to remove, then write it.
                    // If the ID is equal, then that photoset will not be written (deleted).
                    if (!splitLine[0].equals(id))
                        writer.write(line + "\r\n");
                }

                writer.close();
                reader.close();

                tempFile.renameTo(fileManager.GetPhotoSetDataFile());

            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

}
