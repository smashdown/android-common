package com.smashdown.android.common.imagepicker.event;

import java.util.List;

/**
 * Created by smashdown on 2016. 5. 15..
 */
public class TFEventImageSelected {
    private String       folderName;
    private List<String> selectedImageUris;

    public TFEventImageSelected(String folderName, List<String> selectedImageUris) {
        this.folderName = folderName;
        this.selectedImageUris = selectedImageUris;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<String> getSelectedImageUris() {
        return selectedImageUris;
    }

    public void setSelectedImageUris(List<String> selectedImageUris) {
        this.selectedImageUris = selectedImageUris;
    }
}
