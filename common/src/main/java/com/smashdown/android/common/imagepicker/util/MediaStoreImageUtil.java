package com.smashdown.android.common.imagepicker.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.smashdown.android.common.imagepicker.model.HSImageItem;

import java.util.ArrayList;
import java.util.List;

public class MediaStoreImageUtil {

    public static final String[] proj = new String[]{
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.BUCKET_ID,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.TITLE,
            MediaStore.Images.ImageColumns.DESCRIPTION,
            MediaStore.Images.ImageColumns.SIZE,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.MIME_TYPE,
            MediaStore.Images.ImageColumns.ORIENTATION,
            MediaStore.Images.ImageColumns.DATE_ADDED,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DATE_MODIFIED,
            MediaStore.Images.ImageColumns.IS_PRIVATE,
            MediaStore.Images.ImageColumns.LATITUDE,
            MediaStore.Images.ImageColumns.LONGITUDE,
            MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC,
            MediaStore.Images.ImageColumns.PICASA_ID
    };


    static public List<HSImageItem> getAllImage(Context context) {
        List<HSImageItem> images = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                proj,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " desc ");

        if (cursor.moveToFirst()) {
            do {
                HSImageItem image = new HSImageItem();
                image._id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                image.bucketId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID));
                image.bucketDisplayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
                image.displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                image.title = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.TITLE));
                image.description = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DESCRIPTION));
                image.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE));
                image.data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                image.mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.MIME_TYPE));
                image.orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
                image.dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED));
                image.dateTaken = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN));
                image.dateModified = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_MODIFIED));
                image.isPrivate = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.IS_PRIVATE));
                image.latitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LATITUDE));
                image.longitude = cursor.getDouble(cursor.getColumnIndex(MediaStore.Images.ImageColumns.LONGITUDE));
                image.miniThumbMagic = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.MINI_THUMB_MAGIC));
                image.picasaId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.PICASA_ID));

                images.add(image);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed())
            cursor.close();

        return images;
    }

    static public Cursor getImageCursor(Context context, String folderName) {
        return context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                proj,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + "=" + folderName,
                null,
                MediaStore.Images.Media.DATE_ADDED + " desc ");
    }

    static public long getImageCountByFolderName(Context context, String folderName) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{"COUNT(*)"},
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + "=" + folderName,
                null,
                null);
        if (cursor == null)
            return 0;
        else {
            long count = cursor.getLong(0);
            cursor.close();

            return count;
        }
    }

}