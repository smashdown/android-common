package com.smashdown.android.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

public class IntentUtils {

    public static void sendEmail(Activity context, int chooserTitleResId, String from, String to, String title) {
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(context);
        builder.setType("message/rfc822");
        builder.addEmailTo(to);

        StringBuilder content = new StringBuilder(title);
        if (!TextUtils.isEmpty(from)) {
            content.append("(").append(from).append(")");
        }
        builder.setSubject(content.toString());
        builder.setChooserTitle(chooserTitleResId);
        builder.startChooser();
    }

    public static void call(Activity context, String phoneNumber, boolean immediately) {
        Intent myIntent = null;

        if (immediately) {
            myIntent = new Intent(Intent.ACTION_CALL);
        } else {
            myIntent = new Intent(Intent.ACTION_DIAL);
        }
        String phNum = "tel:" + phoneNumber;
        myIntent.setData(Uri.parse(phNum));
        context.startActivity(myIntent);
    }

    public static void sendMessage(Context context, String phone) {
        Uri uri = Uri.parse("smsto:" + phone);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        // it.putExtra("sms_body", "The SMS text");
        context.startActivity(it);
    }

    public static void sendShareIntent(Context context, String chooserTitle, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, chooserTitle));
    }


    public static void goToMarket(Activity activity, String appPackageName) {
        // getPackageName() from Context or Activity object
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


    public static String takePhoto(Fragment fragment, String providerAuthority, int REQ_TAKE_PHOTO) {
        // Ensure that there's a camera activity to handle the intent
        try {
            // Create the File where the photo should go
            File photoFile = createImageFile(fragment.getActivity());
            Uri photoURI = FileProvider.getUriForFile(fragment.getActivity(), providerAuthority, photoFile);
            String absoluteFilePath = photoFile.getAbsolutePath();

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            if (intent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
                fragment.startActivityForResult(intent, REQ_TAKE_PHOTO);
                return absoluteFilePath;
            } else {
                Timber.e("takePhoto - no activity to handle this intent");
            }
        } catch (IOException ex) {
            // Error occurred while creating the File
            ex.printStackTrace();
        }
        return null;

    }

    //    public static String takePhoto(Activity context, String providerAuthority, int REQ_TAKE_PHOTO) {
    //        // Ensure that there's a camera activity to handle the intent
    //        try {
    //            // Create the File where the photo should go
    //            File photoFile = createImageFile(context);
    //            Uri photoURI = FileProvider.getUriForFile(context, providerAuthority, photoFile);
    //            String absoluteFilePath = photoFile.getAbsolutePath();
    //
    //            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    //            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
    //
    //            if (intent.resolveActivity(context.getPackageManager()) != null) {
    //                context.startActivityForResult(intent, REQ_TAKE_PHOTO);
    //                return absoluteFilePath;
    //            } else {
    //                Timber.e("takePhoto - no activity to handle this intent");
    //            }
    //        } catch (IOException ex) {
    //            // Error occurred while creating the File
    //            ex.printStackTrace();
    //        }
    //        return null;
    //    }

    private static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "jpeg_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        // mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
