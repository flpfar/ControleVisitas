package br.edu.ifspsaocarlos.sdm.controlevisitas.model;

public class VisitImage {
    private String mVisitId;
    private String mImageUri;
    private String mImageId;
    private String mImageName;

    public VisitImage(){}

    public VisitImage(String visitId, String imageUri, String imageId, String imageName){
        this.mVisitId = visitId;
        this.mImageUri = imageUri;
        this.mImageId = imageId;
        this.mImageName = imageName;
    }

    public String getmVisitId() {
        return mVisitId;
    }

    public void setmVisitId(String mVisitId) {
        this.mVisitId = mVisitId;
    }

    public String getmImageUri() {
        return mImageUri;
    }

    public void setmImageUri(String mImageUri) {
        this.mImageUri = mImageUri;
    }

    public String getmImageId() {
        return mImageId;
    }

    public void setmImageId(String mImageId) {
        this.mImageId = mImageId;
    }

    public String getmImageName() {
        return mImageName;
    }

    public void setmImageName(String mImageName) {
        this.mImageName = mImageName;
    }
}
