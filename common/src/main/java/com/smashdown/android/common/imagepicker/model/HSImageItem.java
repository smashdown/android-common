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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HSImageItem that = (HSImageItem) o;

        if (bucketDisplayName != null ? !bucketDisplayName.equals(that.bucketDisplayName) : that.bucketDisplayName != null)
            return false;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null)
            return false;
        return data != null ? data.equals(that.data) : that.data == null;

    }

    @Override
    public int hashCode() {
        int result = bucketDisplayName != null ? bucketDisplayName.hashCode() : 0;
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}