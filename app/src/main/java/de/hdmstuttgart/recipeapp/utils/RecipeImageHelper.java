package de.hdmstuttgart.recipeapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class helps to save and read images from the internal storage
 * also includes a cache and preview feature
 */
public class RecipeImageHelper {

    private static final String TAG = "RecipeImageHelper";
    private static final String RECIPE_IMAGE_DIR_NAME = "RecipeImages";
    private static final String RECIPE_IMAGE_PREVIEW_DIR_NAME = "RecipePreviewImages";
    private static final String RECIPE_CACHE_IMAGE_NAME = "croppedImgRcp.jpg";

    private final File mDirectory;
    private final File mPreviewDirectory;
    private final File mCacheDirectory;

    public RecipeImageHelper(Context context) {
        mCacheDirectory = new File(context.getExternalCacheDir() + File.separator);
        mPreviewDirectory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), RECIPE_IMAGE_PREVIEW_DIR_NAME);
        mDirectory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), RECIPE_IMAGE_DIR_NAME);
    }

    public Bitmap cropImage(Bitmap original, int rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree); // TODO: get rotation from file (Exif information)

        Bitmap rotated = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);

        int originalWidth = rotated.getWidth();
        int originalHeight = rotated.getHeight();

        int targetHeight = (int) (originalWidth * 0.75);

        int x = 0;
        int y = (originalHeight - targetHeight) / 2;


        return Bitmap.createBitmap(rotated, x, y, originalWidth, targetHeight);
    }

    public boolean saveImageToCache(@NonNull Bitmap bitmap) {
        return saveImage(bitmap, RECIPE_CACHE_IMAGE_NAME, mCacheDirectory, false);
    }

    public boolean saveImageToExternalStorage(@NonNull Bitmap recipeBitmap, @NonNull String imgName) {
        return saveImage(recipeBitmap, imgName, mDirectory, false);
    }

    public boolean savePreview(@NonNull Bitmap recipeBitmap, @NonNull String imgName) {
        return saveImage(recipeBitmap, imgName, mPreviewDirectory, true);
    }


    public String moveCacheToExternalStorage(boolean savePreview) {
        String filePath = new File(mCacheDirectory, RECIPE_CACHE_IMAGE_NAME).getAbsolutePath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        String rcpName = "rcp-" + System.currentTimeMillis() + ".jpg";
        saveImageToExternalStorage(bitmap, rcpName);

        if (savePreview) {
            savePreview(bitmap, rcpName);
        }

        return rcpName;
    }

    public void deleteFileIfExists(File file) {
        if (file.exists()) file.delete();
    }

    private boolean saveImage(@NonNull Bitmap recipeBitmap, @NonNull String imgName, File directory, boolean preview) {

        // Directory doesn't exist and can't be created => escape function
        if (!directory.exists() && !directory.mkdirs()) {
            Log.d(TAG, "saveImage: could not create recipe image directory");
            return false;
        }

        // Save File
        File imgFile = new File(directory, imgName);
        deleteFileIfExists(imgFile);
        try {

            // File could not be created
            if (!imgFile.createNewFile()) {
                Log.d(TAG, "saveImage: could not create recipe image file");
                return false;
            }

            if (preview) {
                recipeBitmap = Bitmap.createScaledBitmap(recipeBitmap, 4 * 128, 3 * 128, true);
            }

            // "Write Bitmap" to file
            FileOutputStream fos = new FileOutputStream(imgFile);
            recipeBitmap.compress(Bitmap.CompressFormat.JPEG, preview ? 50 : 100, fos);
            fos.flush();
            fos.close(); // TODO: close also if we catch an exception => otherwise data breach
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "saveImage: " + e.getLocalizedMessage());
            return false;
        }
        Log.d(TAG, "saveImage: image saved successfully (" + imgName + ")");
        return true;
    }

    public Uri getImageUri(@Nullable String imgName, boolean preview) {

        File directory = preview ? mPreviewDirectory : mDirectory;

        Log.d(TAG, "getImageUri: No image available");
        if (imgName == null) {
            return Uri.EMPTY;
        }

        // Return Empty Uri if Directory doesn't exist
        if (!directory.exists()) {
            Log.d(TAG, "getImageUri: recipe image directory doesn't exist");
            return Uri.EMPTY;
        }

        // getUri
        File imgFile = new File(directory, imgName);
        if (imgFile.exists()) {
            Log.d(TAG, "getImageUri: getUri successfully (" + imgName + ")");
            return Uri.fromFile(imgFile);
        }
        Log.e(TAG, "getImageUri: unexpected error occurred (" + imgName + ")");
        return Uri.EMPTY;
    }

    public void deleteImage(String imgName, boolean hasPreview) {
        List<Uri> deleteFilesByUris = new ArrayList<>();
        deleteFilesByUris.add(getImageUri(imgName, false));
        if (hasPreview) {
            deleteFilesByUris.add(getImageUri(imgName, true));
        }

        for (Uri uri : deleteFilesByUris) {
            File fdelete = new File(uri.getPath());
            if (fdelete.exists()) {
                if (fdelete.delete()) {
                    Log.i(TAG, "image deleted: " + uri.getPath());
                } else {
                    Log.i(TAG, "image NOT deleted: " + uri.getPath());
                }
            }
        }
    }
}
