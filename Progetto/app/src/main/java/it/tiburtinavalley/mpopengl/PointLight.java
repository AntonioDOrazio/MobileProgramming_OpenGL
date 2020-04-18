package it.tiburtinavalley.mpopengl;

public class PointLight {


    public float[] positionInWorldSpace;
    public float[] positionInEyeSpace;
    public float[] positionInModelSpace;

    public PointLight() {
        // Centrata nell'origine all'istante iniziale
        positionInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
        positionInEyeSpace = new float[4];
        positionInWorldSpace = new float[4];
    }


    public float[] getPositionInWorldSpace() {
        return positionInWorldSpace;
    }

    public void setPositionInWorldSpace(float[] positionInWorldSpace) {
        this.positionInWorldSpace = positionInWorldSpace;
    }

    public float[] getPositionInEyeSpace() {
        return positionInEyeSpace;
    }

    public void setPositionInEyeSpace(float[] positionInEyeSpace) {
        this.positionInEyeSpace = positionInEyeSpace;
    }

    public float[] getPositionInModelSpace() {
        return positionInModelSpace;
    }

    public void setPositionInModelSpace(float[] positionInModelSpace) {
        this.positionInModelSpace = positionInModelSpace;
    }
}
