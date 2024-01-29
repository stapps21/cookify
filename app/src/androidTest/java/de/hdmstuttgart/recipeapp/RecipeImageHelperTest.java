package de.hdmstuttgart.recipeapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import de.hdmstuttgart.recipeapp.utils.RecipeImageHelper;

@RunWith(AndroidJUnit4.class)
public class RecipeImageHelperTest {
    private RecipeImageHelper recipeImageHelper;

    private static final String RECIPE_IMAGE_DIR_NAME = "RecipeImages";
    private static final String RECIPE_IMAGE_PREVIEW_DIR_NAME = "RecipePreviewImages";
    private static final String RECIPE_CACHE_IMAGE_NAME = "croppedImgRcp.jpg";

    private File mDirectory;
    private File mPreviewDirectory;
    private File mCacheDirectory;


    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        recipeImageHelper = new RecipeImageHelper(context);
        mCacheDirectory = new File(context.getExternalCacheDir() + File.separator);
        mPreviewDirectory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), RECIPE_IMAGE_PREVIEW_DIR_NAME);
        mDirectory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), RECIPE_IMAGE_DIR_NAME);
    }

    @Test
    public void testCropImage() {
        Bitmap original = BitmapFactory.decodeResource(InstrumentationRegistry.getInstrumentation().getTargetContext().getResources(),
                android.R.drawable.ic_menu_gallery);

        Bitmap cropped = recipeImageHelper.cropImage(original, 0);

        assertTrue(cropped.getHeight() > 0);
        assertTrue(cropped.getWidth() > 0);
    }

    @Test
    public void testSaveImageToCache() {
        Bitmap original = BitmapFactory.decodeResource(InstrumentationRegistry.getInstrumentation().getTargetContext().getResources(),
                android.R.drawable.ic_menu_gallery);

        boolean saved = recipeImageHelper.saveImageToCache(original);

        assertTrue(saved);
    }

    @Test
    public void testSaveImageToExternalStorage() {
        Bitmap original = BitmapFactory.decodeResource(InstrumentationRegistry.getInstrumentation().getTargetContext().getResources(),
                android.R.drawable.ic_menu_gallery);

        boolean saved = recipeImageHelper.saveImageToExternalStorage(original, "test_img.jpg");

        assertTrue(saved);
    }

    @Test
    public void testSavePreview() throws Exception {
        Bitmap recipeBitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        String imgName = "testPreview.jpg";

        // Save preview
        boolean result = recipeImageHelper.savePreview(recipeBitmap, imgName);

        // Assert that the savePreview method returned true, indicating success
        assertTrue(result);

        // Check if the preview image was saved successfully
        File previewFile = new File(mPreviewDirectory, imgName);
        assertTrue(previewFile.exists());

        // Check if the preview image was scaled to the correct size
        Bitmap previewBitmap = BitmapFactory.decodeFile(previewFile.getAbsolutePath());
        assertEquals(3 * 128, previewBitmap.getHeight());
        assertEquals(4 * 128, previewBitmap.getWidth());
    }

    @Test
    public void testMoveCacheToExternalStorage() throws Exception {
        Bitmap recipeBitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        recipeImageHelper.saveImageToCache(recipeBitmap);

        // Check if the cache image was saved successfully
        File cacheFile = new File(mCacheDirectory, RECIPE_CACHE_IMAGE_NAME);
        assertTrue(cacheFile.exists());

        // Move the cache image to external storage
        String rcpName = recipeImageHelper.moveCacheToExternalStorage(false);

        // Check if the cache image was moved successfully
        File externalFile = new File(mDirectory, rcpName);
        assertTrue(externalFile.exists());
    }

    @Test
    public void testDeleteFileIfExists() throws Exception {
        File testFile = new File(mCacheDirectory, "testFile.txt");

        // Create the test file
        testFile.createNewFile();
        assertTrue(testFile.exists());

        // Delete the test file
        recipeImageHelper.deleteFileIfExists(testFile);
        assertFalse(testFile.exists());
    }
}
