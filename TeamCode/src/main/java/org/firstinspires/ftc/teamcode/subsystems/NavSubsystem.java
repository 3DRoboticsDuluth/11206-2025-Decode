package org.firstinspires.ftc.teamcode.subsystems;

import static com.seattlesolvers.solverslib.util.MathUtils.clamp;
import static org.firstinspires.ftc.teamcode.game.Alliance.RED;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.game.Side.NORTH;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.vision;
import static org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem.ELEMENT_RADIUS;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.firstinspires.ftc.teamcode.game.Alliance;
import org.firstinspires.ftc.teamcode.game.Side;

/** @noinspection UnaryPlus, unused */
@Configurable
public class NavSubsystem {
    public static double TILE_WIDTH = 23.5;
    public static double ROBOT_LENGTH = 14.25; // 0.361950 meters
    public static double ROBOT_WIDTH = 11.375; // 0.288925 meters

    public Pose getStartPose() {
        return (config.side == null || config.side == Side.UNKNOWN ||
            config.alliance == null || config.alliance == Alliance.UNKNOWN) ?
                createPose(0, 0, 0) :
                (config.side == Side.NORTH) ?
                    getStartNorthPose() :
                    getStartSouthPose();
    }

    protected Pose getStartNorthPose() {
        return createPose(
            2.7 * TILE_WIDTH,
            config.alliance.sign * -0.8 * TILE_WIDTH,
            toRadians(config.alliance.sign * 0)
        );
    }

    protected Pose getStartSouthPose() {
        return createPose(
            -2.7 * TILE_WIDTH,
            config.alliance.sign * -0.8 * TILE_WIDTH,
            toRadians(config.alliance.sign * 0)
        );
    }

    public Pose getSpike0() {
        return createPose(
            2.1 * TILE_WIDTH,
            config.alliance.sign * (config.alliance == RED ? -2.75 : -2.65) * TILE_WIDTH,
            toRadians(config.alliance.sign * -15)
        );
    }

    public Pose getSpike1() {
        return createPose(
            1.5 * TILE_WIDTH,
            config.alliance.sign * -1.1 * TILE_WIDTH,
            toRadians(config.alliance.sign * -90)
        );
    }

    public Pose getSpike2() {
        return createPose(
            0.5 * TILE_WIDTH,
            config.alliance.sign * -1.1 * TILE_WIDTH,
            toRadians(config.alliance.sign * -90)
        );
    }

    public Pose getSpike3() {
        return createPose(
            -0.55 * TILE_WIDTH,
            config.alliance.sign * -1.1 * TILE_WIDTH,
            toRadians(config.alliance.sign * -90)
        );
    }

    public Pose getDepositSouthPose(double axialOffset, double lateralOffset) {
        return createPose(
            -1 * TILE_WIDTH,
            config.alliance.sign * -0.75 * TILE_WIDTH
        ).face(
            getGoalPose(), config.alliance.sign * (config.pose.x < TILE_WIDTH * -2 ? -182 : (config.pose.x > TILE_WIDTH * 2 ? -170 :  -175))
        ).axial(axialOffset).lateral(lateralOffset).face(
            getGoalPose(), config.alliance.sign * (config.pose.x < TILE_WIDTH * -2 ? -182 : (config.pose.x > TILE_WIDTH * 2 ? -170 :  -175))
        );
    }

    public Pose getDepositNorthPose(double axialOffset, double lateralOffset) {
        return createPose(
            2.3 * TILE_WIDTH,
            config.alliance.sign * -0.6 * TILE_WIDTH
        ).face(
            getGoalPose(), config.alliance.sign * (config.alliance == RED ? -177 : 178)
        ).axial(axialOffset).lateral(lateralOffset).face(
            getGoalPose(), config.alliance.sign * (config.alliance == RED ? -177 : 178)
        );
    }

    public double getDepositNorthPoseDistance() {
        return config.pose.hypot(Subsystems.nav.getDepositNorthPose(0, 0));
    }

    public Pose getGatePose() {
        return createPose(
            config.side == NORTH ? 0.15 * TILE_WIDTH : -0.15,
            config.alliance.sign * -2 * TILE_WIDTH,
            toRadians(config.alliance.sign * -90)
        );
    }

    public Pose getGateIntakePose() {
        return createPose(
            .65 * TILE_WIDTH,
            config.alliance.sign * -2.65 * TILE_WIDTH,
            toRadians(config.alliance.sign * -135)
        );
    }

    public Pose getGateIntakeDepartPose() {
        return createPose(
            .25 * TILE_WIDTH,
            config.alliance.sign * -2.65 * TILE_WIDTH,
            toRadians(config.alliance.sign * 220)
        );
    }

    public Pose getGoalPose() {
        return createPose(
            -2.75 * TILE_WIDTH,
            config.alliance.sign * -2.75 * TILE_WIDTH,
            toRadians(config.alliance.sign * 45)
        );
    }

    public double getGoalDistanceOffset() {
        return config.pose.x > TILE_WIDTH ?
            config.goalDistanceOffsetNorth :
            config.goalDistanceOffsetSouth;
    }

    public double getGoalDistance() {
        return (
            vision.botpose == null ? config.pose : vision.botpose
        ).hypot(this.getGoalPose()) + this.getGoalDistanceOffset();
    }

    public double getGoalHeadingOffset() {
        return toRadians(
            config.pose.x > TILE_WIDTH ?
                config.goalAngleOffsetNorth :
                config.goalAngleOffsetSouth
        );
    }

    public double getGoalHeadingRemaining() {
        return normalizeHeading(
            config.pose.heading - (
                this.getGoalPose().atan2(
                    vision.botpose == null ? config.pose : vision.botpose
                ) + this.getGoalHeadingOffset()
            )
        );
    }

    public Pose getChaseScanPose() {
        return createPose(
            2.25 * TILE_WIDTH,
            -0.75 * TILE_WIDTH * config.alliance.sign,
            toRadians(config.alliance.sign * -85)
        );
    }

    public Pose getChasePose(int execution) {
        return createPose(
            vision.element == null ? (2.75 - (execution % 3) * 0.75) * TILE_WIDTH : vision.element.x,
            2.4 * TILE_WIDTH * -config.alliance.sign,
            toRadians(config.alliance.sign * -90)
        );
    }

    public Pose getArtifactPose() {
        return new Pose(
            clamp(vision.element.x, TILE_WIDTH * -3 + ROBOT_WIDTH / 2, TILE_WIDTH * 3 - ROBOT_WIDTH / 2),
            (TILE_WIDTH * 3 - ELEMENT_RADIUS) * -config.alliance.sign,
            getArtifactHeading()
        );
    }

    public double getArtifactForwardRemaining() {
        return config.pose.x - getArtifactPose().x;
    }

    public double getArtifactStrafeRemaining() {
        return config.pose.y - getArtifactApproachY(getArtifactPose());
    }

    protected double getArtifactApproachY(Pose artifactPose) {
        if (isArtifactXAligned()) return artifactPose.y;
        double stagingY = getArtifactStagingY(artifactPose);
        if (!isRobotClearOfCenterline()) return getArtifactCenterlineClearY();
        return isPastArtifactStagingY(stagingY) ? stagingY : config.pose.y;
    }

    protected boolean isArtifactXAligned() {
        return abs(getArtifactForwardRemaining()) <= ELEMENT_RADIUS;
    }

    protected double getArtifactStagingY(Pose artifactPose) {
        return artifactPose.y + ELEMENT_RADIUS * 3 * config.alliance.sign;
    }

    protected boolean isPastArtifactStagingY(double stagingY) {
        return allianceSideY(config.pose.y) > allianceSideY(stagingY);
    }

    protected boolean isRobotClearOfCenterline() {
        return allianceSideY(config.pose.y) >= ROBOT_LENGTH / 2;
    }

    protected double getArtifactCenterlineClearY() {
        return ROBOT_LENGTH / 2 * -config.alliance.sign;
    }

    protected double allianceSideY(double y) {
        return y * -config.alliance.sign;
    }

    public double getArtifactHeadingRemaining() {
        return normalizeHeading(config.pose.heading - getArtifactPose().heading);
    }

    protected double getArtifactHeading() {
        return PI / 2 * -config.alliance.sign;
    }

    public Pose getParkingPose(boolean gate, Axial axial, Lateral lateral) {
        return createPose(
            gate ? 0 * TILE_WIDTH : (config.side.sign * (config.side == NORTH ? 2.6 : 2.4) * TILE_WIDTH),
            gate ? (1.75 * -config.alliance.sign * TILE_WIDTH) : ((config.side == NORTH ? -1.75 : -1) * TILE_WIDTH * config.alliance.sign),
            gate ? toRadians(config.alliance.sign * -90) : toRadians(90 + config.side.sign * 90),
            axial, lateral
        );
    }

    public Pose getBasePose() {
        return createPose(
            1.5 * TILE_WIDTH,
            config.alliance.sign * 1.33 * TILE_WIDTH,
            toRadians(config.alliance.sign * 0)
        );
    }

    public enum Axial {
        FRONT(+1), CENTER(0), BACK(-1);
        
        public final int signum;
        
        Axial(int signum) {
            this.signum = signum;
        }
    }

    public enum Lateral {
        LEFT(+1), CENTER(0), RIGHT(-1);
        
        public final int signum;
        
        Lateral(int signum) {
            this.signum = signum;
        }
    }

    public Pose createPose(double x, double y) {
        return createPose(x, y, 0, Axial.CENTER, Lateral.CENTER, Pose.HOLD_DEFAULT);
    }

    public Pose createPose(double x, double y, boolean hold) {
        return createPose(x, y, 0, Axial.CENTER, Lateral.CENTER, hold);
    }

    public Pose createPose(Pose pose, Axial axial) {
        return createPose(pose, axial, Lateral.CENTER, Pose.HOLD_DEFAULT);
    }

    public Pose createPose(Pose pose, Axial axial, boolean hold) {
        return createPose(pose, axial, Lateral.CENTER, hold);
    }

    public Pose createPose(Pose pose, Lateral lateral) {
        return createPose(pose, Axial.CENTER, lateral, Pose.HOLD_DEFAULT);
    }

    public Pose createPose(Pose pose, Lateral lateral, boolean hold) {
        return createPose(pose, Axial.CENTER, lateral, hold);
    }

    public Pose createPose(Pose pose, Axial axial, Lateral lateral) {
        return createPose(pose.x, pose.y, pose.heading, axial, lateral, Pose.HOLD_DEFAULT);
    }

    public Pose createPose(Pose pose, Axial axial, Lateral lateral, boolean hold) {
        return createPose(pose.x, pose.y, pose.heading, axial, lateral, hold);
    }

    public Pose createPose(double x, double y, double heading) {
        return createPose(x, y, heading, Axial.CENTER, Lateral.CENTER, Pose.HOLD_DEFAULT);
    }

    public Pose createPose(double x, double y, double heading, boolean hold) {
        return createPose(x, y, heading, Axial.CENTER, Lateral.CENTER, hold);
    }

    public Pose createPose(double x, double y, double heading, double axialOffset, double lateralOffset) {
        return createPose(x, y, heading, Axial.CENTER, Lateral.CENTER, axialOffset, lateralOffset, Pose.HOLD_DEFAULT);
    }

    public Pose createPose(double x, double y, double heading, double axialOffset, double lateralOffset, boolean hold) {
        return createPose(x, y, heading, Axial.CENTER, Lateral.CENTER, axialOffset, lateralOffset, hold);
    }

    public Pose createPose(double x, double y, double heading, Axial axial) {
        return createPose(x, y, heading, axial, Lateral.CENTER, Pose.HOLD_DEFAULT);
    }

    public Pose createPose(double x, double y, double heading, Axial axial, boolean hold) {
        return createPose(x, y, heading, axial, Lateral.CENTER, hold);
    }

    public Pose createPose(double x, double y, double heading, Axial axial, double axialOffset) {
        return createPose(x, y, heading, axial, Lateral.CENTER, axialOffset, 0, Pose.HOLD_DEFAULT);
    }

    public Pose createPose(double x, double y, double heading, Axial axial, double axialOffset, boolean hold) {
        return createPose(x, y, heading, axial, Lateral.CENTER, axialOffset, 0, hold);
    }

    public Pose createPose(double x, double y, double heading, Lateral lateral) {
        return createPose(x, y, heading, Axial.CENTER, lateral, Pose.HOLD_DEFAULT);
    }

    public Pose createPose(double x, double y, double heading, Lateral lateral, boolean hold) {
        return createPose(x, y, heading, Axial.CENTER, lateral, hold);
    }

    public Pose createPose(double x, double y, double heading, Lateral lateral, double lateralOffset) {
        return createPose(x, y, heading, Axial.CENTER, lateral, 0, lateralOffset, Pose.HOLD_DEFAULT);
    }

    public Pose createPose(double x, double y, double heading, Lateral lateral, double lateralOffset, boolean hold) {
        return createPose(x, y, heading, Axial.CENTER, lateral, 0, lateralOffset, hold);
    }

    public Pose createPose(double x, double y, double heading, Axial axial, Lateral lateral) {
        return createPose(x, y, heading, axial, lateral, 0, 0, Pose.HOLD_DEFAULT);
    }

    public Pose createPose(double x, double y, double heading, Axial axial, Lateral lateral, boolean hold) {
        return createPose(x, y, heading, axial, lateral, 0, 0, hold);
    }

    public Pose createPose(double x, double y, double heading, Axial axial, Lateral lateral, double axialOffset, double lateralOffset) {
        return createPose(x, y, heading, axial, lateral, axialOffset, lateralOffset, Pose.HOLD_DEFAULT);
    }

    public Pose createPose(double x, double y, double heading, Axial axial, Lateral lateral, double axialOffset, double lateralOffset, boolean hold) {
        double axialHeading = normalizeHeading(heading);
        axialOffset -= axial.signum * ROBOT_LENGTH / 2;
        x += cos(axialHeading) * axialOffset;
        y += sin(axialHeading) * axialOffset;

        double lateralHeading = normalizeHeading(heading + PI / 2);
        lateralOffset -= lateral.signum * ROBOT_WIDTH / 2;
        x += cos(lateralHeading) * lateralOffset;
        y += sin(lateralHeading) * lateralOffset;
        
        return new Pose(jitter(x), jitter(y), jitter(heading), hold);
    }

    public double normalizeHeading(double heading) {
        if (heading > +PI) heading -= PI * 2;
        if (heading < -PI) heading += PI * 2;
        return heading;
    }
    
    private double jitter(double value) {
        return value * (1 + Math.random() / 1000);
    }
}
