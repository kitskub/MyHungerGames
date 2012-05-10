/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *                                                 *
 ******************************************************************************/
package com.randude14.hungergames.utils;

import com.randude14.hungergames.Logging;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtils {
    
    /**
     * Used to delete a folder.
     *
     * @param file The folder to delete.
     * @return true if the folder was successfully deleted.
     */
    public static boolean deleteFolder(File file) {
        if (file.exists()) {
            boolean ret = true;
            // If the file exists, and it has more than one file in it.
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    ret = ret && deleteFolder(f);
                }
            }
            return ret && file.delete();
        } else {
            return false;
        }
    }
    
    /**
     * Helper method to copy the world-folder
     * 
     * @returns if it had success
     */
    public static boolean copyFolder(File source, File target) {
        InputStream in = null;
        OutputStream out = null;
        try {
            if (source.isDirectory()) {

                if (!target.exists())
                    target.mkdir();

                String[] children = source.list();
                // for (int i=0; i<children.length; i++) {
                for (String child : children) {
                    copyFolder(new File(source, child), new File(target, child));
                }
            }
            else {
                in = new FileInputStream(source);
                out = new FileOutputStream(target);

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            return true;
        }
        catch (FileNotFoundException e) {
            Logging.log(Level.WARNING , "Exception while copying file: " + e.getMessage());
        }
        catch (IOException e) {
            Logging.log(Level.WARNING , "Exception while copying file: " + e.getMessage());
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) { }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignore) { }
            }
        }
        return false;
    }
}