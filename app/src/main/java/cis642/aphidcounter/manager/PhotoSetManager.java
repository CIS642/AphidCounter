package cis642.aphidcounter.manager;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import cis642.aphidcounter.PhotoSet;
import cis642.aphidcounter.entity.Field;

/**
 * Created by Staton on 11/13/2014.
 */
public class PhotoSetManager
{
    private static FileManager fileManager = new FileManager();

    /**
     * Directory of where the photoset data will be stored.
     */
    //private static final File fileDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
    //       + File.separator + "AphidCounter" + File.separator + "PhotoSets");

    /**
     * Name of file to store the photoset information in.
     */
    //private static final String photoSetFileName = "PhotoSets.txt";

    /**
     * File to store the photoset information in.
     */
    //private static File photoSetFile;

    /**
     * The single instance of this class.
     */
    private static PhotoSetManager instance = null;

    /**
     * The list of photosets.
     */
    private static ArrayList<PhotoSet> photoSets = new ArrayList<PhotoSet>();

    private PhotoSetManager()
    {
        //DeleteAllPhotoSets(); // Call for testing, if you want to delete all saved photosets.
        //DeserializeList();

        // Create the directory and file for saving photoset information to:
        //CreateDirectoryAndFile();

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
     * Serialize the list of photo sets.
     */
    public static void SerializeList()
    {
        // Get the file to read the photosets from.
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"PhotoSets.bin");

        // If the file does not exist yet, create it.
        if(!file.exists())
        {
            try
            {
                file.createNewFile();
            } catch (IOException ex) { ex.printStackTrace(); }
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(photoSets);
            os.close();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    /**
     * Deserialize the list of photo sets.
     */
    private static void DeserializeList()
    {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"PhotoSets.bin");

        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream is = new ObjectInputStream(fis);
                photoSets = (ArrayList<PhotoSet>) is.readObject();
                is.close();
            } catch (Exception ex) { ex.printStackTrace(); }
        }

    }

    /**
     * Adds a new photo set to the list of photosets.
     * @param ps The new photo set to be added.
     */
    public static void Add(PhotoSet ps)
    {
        // Set the index for this photoset ID.
        ps.SetPhotoSetIndex(photoSets.size());
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
    public static PhotoSet Get(int i)
    {
        if (i < photoSets.size())
            return photoSets.get(i);
        return null;
    }

    /**
     * Removes a photo set.
     * @param i The index of the photo set to remove.
     */
    public static void Remove(int i)
    {
        // todo: get the index of the photoset at i. Then use that index to remove from the file.
        // todo: then save the newly updated file.

        // Remove the photo set from the list.
        if (i < photoSets.size() - 1)
            photoSets.remove(i);

        // since the indices for some photosets may have changed, updated the index of each set.
        for (int j = 0; j < photoSets.size(); j++)
            photoSets.get(j).SetPhotoSetIndex(j);
    }

    /**
     * Gets the number of photo sets.
     * @return The number of photo sets.
     */
    public static int Count()
    {
        return photoSets.size();
    }

    /**
     * For testing. Deletes all the photo sets.
     */
    private static void DeleteAllPhotoSets()
    {
        for (int i = 0; i < photoSets.size(); i++)
            photoSets.remove(i);
        //SerializeList();
    }

    /**
     * Attempts to create the directory and file for saving the photosets to.
     * Note: If the app gives a 'Permission Denied' error, change the settings on your phone
     * from "Mount as disk drive" to "charge only over usb".
     */
 /*   private void CreateDirectoryAndFile()
    {
        // If the directory does not exist, create it:
        if (!fileDirectory.exists())
        {
            Log.i("PHOTOSET MANAGER", "Directory doesn't exist");
            try
            {
                fileDirectory.mkdirs();
                Log.i("PHOTOSET MANAGER", "Directory created");
            } catch (Exception e) { e.printStackTrace(); }
        }

        photoSetFile = new File(fileDirectory + File.separator + photoSetFileName);

        // If the file does not exist, create it:
        if (!photoSetFile.exists())
        {
            Log.i("PHOTOSET MANAGER", "Photoset file does not exist");
            try
            {
                photoSetFile.createNewFile();
                Log.i("PHOTOSET MANAGER", "Photoset file created");
            } catch (Exception ex) { ex.printStackTrace(); }
        }

    }*/

    public static void Save()
    {
        SavePhotoSets();
    }

    /**
     * Saves the photo sets.
     */
    private static void SavePhotoSets()
    {
        //if (photoSetFile.exists())
        if (fileManager.GetPhotoSetDataFile().exists())
        {
            try
            {
                //FileOutputStream fOut = new FileOutputStream(photoSetFile);
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
        // TODO set photoset index (str[0])
        String[] str = line.split(",", -1);

        String[] date = str[1].split("\\.", -1);

        PhotoSet ps = new PhotoSet(str[2],
                                   new Field(str[3], str[4]),
                                   new GregorianCalendar(Integer.parseInt(date[0]),
                                                         Integer.parseInt(date[1]),
                                                         Integer.parseInt(date[2])));

        // add the photos filenames:
        for (int i = 5; i < str.length; i++) {
            // todo: also check for converted photos existing.
            if (fileManager.PhotoExists(str[i]))
                ps.AddPhoto(str[i]);
        }

        photoSets.add(ps);
    }

    private static String CreateCsvString(int i)
    {
        String line = "";

        if (i < photoSets.size()) {
            PhotoSet ps = photoSets.get(i);

            line += ps.GetPhotoSetIndex() + ",";
            line += ps.GetDateTaken().toString() + ",";
            line += ps.GetBugType() + ",";
            line += ps.GetField().name() + ",";
            line += ps.GetField().GetCropType();

            for (int j = 0; j < ps.GetPhotoCount(); j++)
                line += "," + ps.GetPhoto(j);   // add the filename of each photo.
        }

        return line;
    }

}
