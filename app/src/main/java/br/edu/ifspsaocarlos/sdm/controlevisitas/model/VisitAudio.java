package br.edu.ifspsaocarlos.sdm.controlevisitas.model;

public class VisitAudio {
    private String mVisitId;
    private String mAudioUri;
    private String mAudioId;
    private String mAudioName;

    public VisitAudio() {
    }

    public VisitAudio(String mVisitId, String mAudioUri, String mAudioId, String mAudioName) {
        this.mVisitId = mVisitId;
        this.mAudioUri = mAudioUri;
        this.mAudioId = mAudioId;
        this.mAudioName = mAudioName;
    }

    public String getmVisitId() {
        return mVisitId;
    }

    public void setmVisitId(String mVisitId) {
        this.mVisitId = mVisitId;
    }

    public String getmAudioUri() {
        return mAudioUri;
    }

    public void setmAudioUri(String mAudioUri) {
        this.mAudioUri = mAudioUri;
    }

    public String getmAudioId() {
        return mAudioId;
    }

    public void setmAudioId(String mAudioId) {
        this.mAudioId = mAudioId;
    }

    public String getmAudioName() {
        return mAudioName;
    }

    public void setmAudioName(String mAudioName) {
        this.mAudioName = mAudioName;
    }
}
