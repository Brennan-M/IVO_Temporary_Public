package com.ivolabs.android.ivo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.graphics.Matrix;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CameraFragment extends Fragment {

    public static final String TAG = "CameraFragment";

    private Camera camera;
    private SurfaceView surfaceView;
    private ParseFile pictureEntry;
    private ImageButton photoButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, container, false);

        photoButton = (ImageButton) v.findViewById(R.id.save_ivoPhoto_button);

        if (camera == null) {
            try {
                int cameraId = 0;
                camera = Camera.open(cameraId);
                photoButton.setEnabled(true);
            } catch (Exception e) {
                photoButton.setEnabled(false);
                Toast.makeText(getActivity(), "No Camera Found!", Toast.LENGTH_LONG).show();
            }
        }

        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (camera == null)
                    return;
                camera.takePicture(new Camera.ShutterCallback() {

                    @Override
                    public void onShutter() {
                        // nothing to do
                    }

                }, null, new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        savePhotoForEntry(data);
                    }

                });

            }
        });

        surfaceView = (SurfaceView) v.findViewById(R.id.camera_surface_view);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(new Callback() {

            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (camera != null) {
                        camera.setDisplayOrientation(90);
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error setting up preview", e);
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                // nothing to do here
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                // nothing here
            }

        });

        return v;
    }

    private void savePhotoForEntry(byte[] data) {

        // Resize photo from camera byte array
        Bitmap IvoImage = BitmapFactory.decodeByteArray(data, 0, data.length);


        // Override Android default landscape orientation and save portrait
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedScaledIvoImage = Bitmap.createBitmap(IvoImage, 0,
                0, IvoImage.getWidth(), IvoImage.getHeight(),
                matrix, true);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        rotatedScaledIvoImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);


        byte[] scaledData = bos.toByteArray();

        // Save the scaled image to Parse
        pictureEntry = new ParseFile("ivoPicture.jpg", scaledData);
        pictureEntry.saveInBackground(new SaveCallback() {

            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(getActivity(),
                            "Error saving: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.d("IVOTAG", "attached picture");
                    attachIvoPhoto(pictureEntry);
                }
            }
        });
    }

    private void attachIvoPhoto(ParseFile ivoPhoto) {
        ((HomescreenActivity) getActivity()).photoFile = ivoPhoto;
        Button postButton = (Button) getActivity().findViewById(R.id.submit_ivopost_button);
        postButton.setVisibility(View.VISIBLE);
        FragmentManager fm = getActivity().getFragmentManager();
        fm.popBackStack("HomescreenActivity", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (camera == null) {
            try {
                camera = Camera.open();
                photoButton.setEnabled(true);
            } catch (Exception e) {
                Log.i(TAG, "No camera: " + e.getMessage());
                photoButton.setEnabled(false);
                Toast.makeText(getActivity(), "No camera detected", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
        super.onPause();
    }
}