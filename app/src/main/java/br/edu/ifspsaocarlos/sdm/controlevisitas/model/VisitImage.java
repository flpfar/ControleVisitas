package br.edu.ifspsaocarlos.sdm.controlevisitas.model;

public class VisitImage {
    private String mVisitId;
    private String mImageUri;
    private String mImageName;

    public VisitImage(String visitId, String imageName, String imageUri){
        if(imageName.trim().equals("")){
            imageName = "No name";
        }

        this.mVisitId = visitId;
        this.mImageName = imageName;
        this.mImageUri = imageUri;
    }
}
