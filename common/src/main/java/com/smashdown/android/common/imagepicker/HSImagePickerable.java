package com.smashdown.android.common.imagepicker;

import com.smashdown.android.common.imagepicker.model.HSImageFolderItem;
import com.smashdown.android.common.imagepicker.model.HSImageItem;

import java.util.List;

public interface HSImagePickerable {
    void onImageFolderSelected(int pos);

    void onImageSelected(HSImageItem item);

    void onImageUnelected(HSImageItem item);

    List<HSImageFolderItem> getFolderList();

    List<HSImageItem> getSelectedImageItems();

    Integer getSelectedFolderIndex();
}