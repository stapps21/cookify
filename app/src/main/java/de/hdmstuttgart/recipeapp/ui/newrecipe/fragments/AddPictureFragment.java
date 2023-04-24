package de.hdmstuttgart.recipeapp.ui.newrecipe.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.hdmstuttgart.recipeapp.databinding.FragmentAddPictureBinding;
import de.hdmstuttgart.recipeapp.interfaces.IOnContinueClickListener;
import de.hdmstuttgart.recipeapp.ui.newrecipe.SharedPageViewModel;
import de.hdmstuttgart.recipeapp.utils.RecipeImageHelper;


/**
 * A placeholder fragment containing a simple view.
 */
public class AddPictureFragment extends Fragment implements IOnContinueClickListener {

    private static final String TAG = "AddPictureFragment";

    private FragmentAddPictureBinding mBinding;
    private SharedPageViewModel mSharedPageViewModel;
    private Context mContext;

    // Permission handling
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private ActivityResultLauncher<String[]> multiplePermissionLauncher;

    //////////// Camera & Gallery
    // Views
    private PreviewView mPreviewView;
    private Button mbtnTakePhoto, mbtnSelectPhoto;
    private ImageButton mbtnTrigger;
    private ImageButton mbtnRetake;
    // State
    private PictureState mPictureState = PictureState.DEFAULT;
    private enum PictureState {
        DEFAULT,
        TAKE_PICTURE,
        PICTURE_SHOWN_CAMERA,
        PICTURE_SHOWN_GALLERY
    }

    // RecipeImageHelper
    private RecipeImageHelper mRecipeImageHelper;


    /////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    /////////////////////////////////////////////////////////////////////////

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mRecipeImageHelper = new RecipeImageHelper(mContext);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPageViewModel = new ViewModelProvider(requireActivity()).get(SharedPageViewModel.class);

        ActivityResultContracts.RequestMultiplePermissions multiplePermissionsContract = new ActivityResultContracts.RequestMultiplePermissions();
        multiplePermissionLauncher = registerForActivityResult(multiplePermissionsContract, isGranted -> {
            Log.d("PERMISSIONS", "Launcher result: " + isGranted.toString());
            if (isGranted.containsValue(false)) {
                Log.d("PERMISSIONS", "At least one of the permissions was not granted");
                //multiplePermissionLauncher.launch(REQUIRED_PERMISSIONS);
            } else {
                startCamera();
            }
        });

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Setup ViewBinding
        mBinding = FragmentAddPictureBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();

        // init views
        initViews();

        // setup default preview mode
        handlePictureState(PictureState.DEFAULT);

        // onclick -> select to take a photo
        mbtnTakePhoto.setOnClickListener(view -> {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                askPermissions(multiplePermissionLauncher);
            }
        });

        // onclick -> image from gallery
        mbtnSelectPhoto.setOnClickListener(view -> selectImageFromGallery());

        // onclick -> Retake the photo / select another from gallery
        mbtnRetake.setOnClickListener(v -> handlePictureState(PictureState.DEFAULT));

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - " + mPictureState.name());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    /////////////////////////////////////////////////////////////////////////
    // INIT
    /////////////////////////////////////////////////////////////////////////

    private void initViews() {
        mPreviewView = mBinding.camera;
        mbtnTrigger = mBinding.btnTrigger;
        mbtnTakePhoto = mBinding.btnTakePhoto;
        mbtnSelectPhoto = mBinding.btnSelectFromGallery;
        mbtnRetake = mBinding.btnRetake;
    }

    /////////////////////////////////////////////////////////////////////////
    // PERMISSION HANDLING
    /////////////////////////////////////////////////////////////////////////

    private void askPermissions(ActivityResultLauncher<String[]> multiplePermissionLauncher) {
        if (!allPermissionsGranted()) {
            Log.d("PERMISSIONS", "Launching multiple contract permission launcher for ALL required permissions");
            multiplePermissionLauncher.launch(REQUIRED_PERMISSIONS);
        } else {
            Log.d("PERMISSIONS", "All permissions are already granted");
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {

            // Android's migration to scoped storage => Don't need to request for WRITE_EXTERNAL_STORAGE on Android 11 or later
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                continue;
            }

            // if permission DENIED
            if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /////////////////////////////////////////////////////////////////////////
    // PREVIEW AND CAMERA
    /////////////////////////////////////////////////////////////////////////

    private void handlePictureState(PictureState pictureState) {
        this.mPictureState = pictureState;
        switch (pictureState) {
            case DEFAULT:
                mSharedPageViewModel.setImageSource(SharedPageViewModel.IMAGE_SOURCE.NONE);
                mBinding.camera.setVisibility(View.GONE);
                mBinding.ivPreview.setVisibility(View.VISIBLE);
                mBinding.ivPreview.setImageBitmap(null);
                mBinding.ivPreview.setBackgroundColor(Color.BLACK);
                mbtnTrigger.setVisibility(View.GONE);
                mbtnRetake.setVisibility(View.GONE);
                mbtnSelectPhoto.setVisibility(View.VISIBLE);
                mbtnTakePhoto.setVisibility(View.VISIBLE);
                break;
            case TAKE_PICTURE:
                mSharedPageViewModel.setImageSource(SharedPageViewModel.IMAGE_SOURCE.NONE);
                mBinding.camera.setVisibility(View.VISIBLE);
                mBinding.ivPreview.setVisibility(View.GONE);
                mbtnTrigger.setVisibility(View.VISIBLE);
                mbtnRetake.setVisibility(View.GONE);
                mbtnSelectPhoto.setVisibility(View.GONE);
                mbtnTakePhoto.setVisibility(View.GONE);
                break;
            case PICTURE_SHOWN_CAMERA:
                mSharedPageViewModel.setImageSource(SharedPageViewModel.IMAGE_SOURCE.CAMERA);
                mBinding.camera.setVisibility(View.GONE);
                mBinding.ivPreview.setVisibility(View.VISIBLE);
                mbtnTrigger.setVisibility(View.GONE);
                mbtnRetake.setVisibility(View.VISIBLE);
                mbtnSelectPhoto.setVisibility(View.GONE);
                mbtnTakePhoto.setVisibility(View.GONE);
                break;
            case PICTURE_SHOWN_GALLERY:
                mSharedPageViewModel.setImageSource(SharedPageViewModel.IMAGE_SOURCE.GALLERY);
                mBinding.camera.setVisibility(View.GONE); // TODO: solve this smarter (same as in OICTURE CAMERA SHOWN....)
                mBinding.ivPreview.setVisibility(View.VISIBLE);
                mbtnTrigger.setVisibility(View.GONE);
                mbtnRetake.setVisibility(View.VISIBLE);
                mbtnSelectPhoto.setVisibility(View.GONE);
                mbtnTakePhoto.setVisibility(View.GONE);
                break;
        }
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(mContext);
        handlePictureState(PictureState.TAKE_PICTURE);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(mContext));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        mPreviewView.setScaleType(PreviewView.ScaleType.FILL_CENTER);

        // Set ratio to 4:3
        Preview preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build();

        // Use back camera
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3);


        final ImageCapture imageCapture = builder
                .setTargetRotation(((Activity) mContext).getWindowManager().getDefaultDisplay().getRotation())
                .build();
        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis, imageCapture);

        // USER CLICKED ON "take picture"
        mbtnTrigger.setOnClickListener(v -> {

            // Delete cached image
            File tmpRecipeFile = new File(mContext.getExternalCacheDir() + File.separator + "notCroppedRcp" + ".jpg");
            mRecipeImageHelper.deleteFileIfExists(tmpRecipeFile);

            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(tmpRecipeFile).build();
            imageCapture.takePicture(outputFileOptions, mExecutor, new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                    // crop bitmap and save to cache
                    Bitmap croppedBitmap = mRecipeImageHelper.cropImage(BitmapFactory.decodeFile(tmpRecipeFile.getAbsolutePath()), 90);
                    mRecipeImageHelper.saveImageToCache(croppedBitmap);

                    // Update UI
                    ((Activity) mContext).runOnUiThread(() -> {
                        handlePictureState(PictureState.PICTURE_SHOWN_CAMERA);
                        mBinding.ivPreview.setImageBitmap(croppedBitmap);
                        cameraProvider.unbindAll();
                    });
                }

                @Override
                public void onError(@NonNull ImageCaptureException error) {
                    error.printStackTrace();
                    cameraProvider.unbindAll();
                }
            });
        });
    }

    /////////////////////////////////////////////////////////////////////////
    // LOAD IMAGE FROM GALLERY
    /////////////////////////////////////////////////////////////////////////

    private void selectImageFromGallery() {
        Intent i = new Intent();
        i.setType("image/jpeg"); // TODO: For later updates also support other file formats
        i.setAction(Intent.ACTION_GET_CONTENT);
        launchGalleryActivity.launch(i);
    }

    private final ActivityResultLauncher<Intent> launchGalleryActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    // check for result
                    if (data != null && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), selectedImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        handeRecivedImageFromGallery(selectedImageBitmap);
                    }
                }
            }
    );

    private void handeRecivedImageFromGallery(Bitmap bitmap) {
        Bitmap croppedBitmap = mRecipeImageHelper.cropImage(bitmap, 0);
        mRecipeImageHelper.saveImageToCache(croppedBitmap);
        handlePictureState(PictureState.PICTURE_SHOWN_GALLERY);
        mBinding.ivPreview.setImageBitmap(croppedBitmap);
    }

    /////////////////////////////////////////////////////////////////////////
    // IOnContinueClickListener
    /////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onClickContinue() {
        // No required fields
        return true;
    }
}