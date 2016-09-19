package com.smashdown.android.common.imagepicker.event;

import com.smashdown.android.common.imagepicker.model.HSImageFolderItem;

/**
 * Created by smashdown on 2016. 9. 19..
 */
public class HSEventImageFolderSelected {
    public HSImageFolderItem item;

    public HSEventImageFolderSelected(HSImageFolderItem folderItem) {
        this.item = folderItem;
    }
}
