package com.smashdown.android.common.imagepicker.model;

import java.util.List;

public class HSImageFolderItem {

    public String            name;
    public List<HSImageItem> images;

    public HSImageFolderItem(String name, List<HSImageItem> list) {
        this.name = name;
        this.images = list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HSImageFolderItem that = (HSImageFolderItem) o;

        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result;
        return result;
    }
}