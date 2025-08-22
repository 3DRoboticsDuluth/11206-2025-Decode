package org.firstinspires.ftc.teamcode.adaptations.limelight;

public class CoordinateMapper {
    private final double[][] cameraCoordinates;
    private final double[][] fieldCoordinates;

    public CoordinateMapper(double[][] cameraCoordinates, double[][] fieldCoordinates) {
        this.cameraCoordinates = cameraCoordinates;
        this.fieldCoordinates = fieldCoordinates;
    }

    public double[] getFieldCoordinates(double cameraX, double cameraY) {
        // Calculate scale factors based on the corners
        double scaleX = (fieldCoordinates[3][0] - fieldCoordinates[0][0]) / (cameraCoordinates[3][0] - cameraCoordinates[0][0]);
        double scaleY = (fieldCoordinates[3][1] - fieldCoordinates[0][1]) / (cameraCoordinates[3][1] - cameraCoordinates[0][1]);

        // Calculate the translation offsets based on the field coordinates of the bottom-left corner
        double translateX = fieldCoordinates[0][0] - (cameraCoordinates[0][0] * scaleX);
        double translateY = fieldCoordinates[0][1] - (cameraCoordinates[0][1] * scaleY);

        // Transform input angle to field coordinates
        double fieldX = (cameraX * scaleX) + translateX;
        double fieldY = (cameraY * scaleY) + translateY;

        return new double[] { fieldX, fieldY };
    }
}
