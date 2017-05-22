package com.smashdown.android.common.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.smashdown.android.common.R;
import com.smashdown.android.common.imagepicker.model.HSImageFolderItem;
import com.smashdown.android.common.imagepicker.model.HSImageItem;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

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

    static public List<HSImageFolderItem> getBucketList(Context context) {
        List<HSImageFolderItem> folerList = new ArrayList<>();

        // which image properties are we querying
        String[] PROJECTION_BUCKET = {
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.DATA};

        // We want to order the albums by reverse chronological order. We abuse the
        // "WHERE" parameter to insert a "GROUP BY" clause into the SQL statement.
        // The template for "WHERE" parameter is like:
        //    SELECT ... FROM ... WHERE (%s)
        // and we make it look like:
        //    SELECT ... FROM ... WHERE (1) GROUP BY 1,(2)
        // The "(1)" means true. The "1,(2)" means the first two columns specified
        // after SELECT. Note that because there is a ")" in the template, we use
        // "(2" to match it.
        String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";
        String BUCKET_ORDER_BY = "MAX(" + MediaStore.Images.Media.DATE_TAKEN + ") DESC";

        // Get the base URI for the People table in the Contacts content provider.
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Cursor cur = context.getContentResolver().query(images, PROJECTION_BUCKET, BUCKET_GROUP_BY, null, BUCKET_ORDER_BY);

        Timber.i(" query count=" + cur.getCount());

        if (cur.moveToFirst()) {
            String bucket;
            String date;
            String data;

            do {
                HSImageFolderItem folderItem = new HSImageFolderItem();
                // Get the field values
                folderItem.bucketId = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
                folderItem.bucketDisplayName = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                folderItem.lastDateTaken = cur.getLong(cur.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN));
                folderItem.lastData = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA));
                Timber.i("Bucket=" + folderItem.bucketDisplayName + "  dateTaken=" + folderItem.lastDateTaken + "  _data=" + folderItem.lastData);
                folderItem.imageCount = getImageCountByFolderName(context, folderItem.bucketDisplayName);

                // Do something with the values.
                folerList.add(folderItem);
            } while (cur.moveToNext());
        }

        return folerList;
    }

    static public List<HSImageItem> getImagesByFolderName(Context context, String bucketName) {
        List<HSImageItem> images = new ArrayList<>();
        try {
            Cursor cursor = null;
            if (bucketName.equals(context.getString(R.string.hs_all_images))) {
                cursor = context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,
                        null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC");
            } else {
                cursor = context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " = \"" + bucketName + "\"", null, MediaStore.Images.Media.DATE_TAKEN + " DESC");
            }

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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return images;
    }

    static public long getImageCountByFolderName(Context context, String bucketName) {
        try {
            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            String searchParams = null;
            String bucket = bucketName;

            searchParams = MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " = \"" + bucket + "\"";
            // final String[] columns = { MediaStore.Images.Media.DATA,
            // MediaStore.Images.Media._ID };
            Cursor mPhotoCursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    searchParams, null, orderBy + " DESC");

            if (mPhotoCursor.getCount() > 0) {
                return mPhotoCursor.getCount();
            }

            mPhotoCursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return 0;
    }

}