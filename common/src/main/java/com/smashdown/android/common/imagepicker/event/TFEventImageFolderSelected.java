package com.smashdown.android.common.imagepicker.event;


import com.smashdown.android.common.imagepicker.model.HSImageFolderItem;

/**
 * Created by smashdown on 2016. 5. 15..
 */
public class TFEventImageFolderSelected {
    private HSImageFolderItem folderItem;

    public TFEventImageFolderSelected(HSImageFolderItem item) {
        this.folderItem = item;
    }

    public HSImageFolderItem getFolderItem() {
        return folderItem;
    }

    public void setFolderItem(HSImageFolderItem folderItem) {
        this.folderItem = folderItem;
    }
}
