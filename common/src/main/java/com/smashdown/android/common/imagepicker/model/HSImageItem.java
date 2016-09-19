package com.smashdown.android.common.imagepicker.model;

public class HSImageItem {
    public long   _id;
    public String bucketId;
    public String bucketDisplayName;
    public String displayName;
    public String title;
    public String description;
    public long   size;
    public String data;
    public String mimeType;
    public int    orientation;
    public long   dateAdded;
    public long   dateTaken;
    public long   dateModified;
    public int    isPrivate;
    public double latitude;
    public double longitude;
    public int    miniThumbMagic;
    public String picasaId;

    @Override
    public boolean equals(Object o) {
        if (o instanceof HSImageItem) {
            return ((HSImageItem) o).data.equals(data);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }
}