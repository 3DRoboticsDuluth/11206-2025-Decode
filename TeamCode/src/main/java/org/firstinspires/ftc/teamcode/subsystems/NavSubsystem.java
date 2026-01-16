package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.game.Alliance.RED;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static java.lang.Math.PI;
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
    public static double ROBOT_LENGTH = 14.25;
    public static double ROBOT_WIDTH = 11.375;

    public Pose getStartPose() {
        return config.side == null || config.side == Side.UNKNOWN ||
            config.alliance == null || config.alliance == Alliance.UNKNOWN ?
            new Pose(0, 0, 0) :
                config.side == Side.NORTH ?
                    getStartNorthPose() :
                    getStartSouthPose();
    }

    protected Pose getStartNorthPose() {
        return createPose(
            3 * TILE_WIDTH,
            config.alliance.sign * -1 * TILE_WIDTH,
            toRadians(config.alliance.sign * 0),
            Axial.FRONT, config.alliance == RED ? Lateral.LEFT : Lateral.RIGHT, -3.25, 0
        );
    }

    protected Pose getStartSouthPose() {
        return createPose(
            -3 * TILE_WIDTH,
            config.alliance.sign * -1 * TILE_WIDTH,
            toRadians(config.alliance.sign * 0),
            Axial.BACK, config.alliance == RED ? Lateral.LEFT : Lateral.RIGHT, +1, 0
        );
    }

    public Pose getSpike0Approach() {
        return createPose(
            2.25 * TILE_WIDTH,
            config.alliance.sign * -2.5 * TILE_WIDTH,
            toRadians(config.alliance.sign * -90)
        );
    }

    public Pose getSpike0() {
        return createPose(
            2 * TILE_WIDTH,
            config.alliance.sign * -2.6 * TILE_WIDTH,
            toRadians(config.alliance.sign * -45)
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
            -0.5 * TILE_WIDTH,
            config.alliance.sign * -1.1 * TILE_WIDTH,
            toRadians(config.alliance.sign * -90)
        );
    }

    public Pose getDepositSouthPose(double axialOffset, double lateralOffset) {
        return createPose(
            -0.75 * TILE_WIDTH,
            config.alliance.sign * -0.75 * TILE_WIDTH
        ).face(
            getGoalPose(), config.alliance == RED ? -180 : 180
        ).axial(axialOffset).lateral(lateralOffset).face(
            getGoalPose(), config.alliance == RED ? -180 : 180
        );
    }

    public Pose getDepositNorthPose(double axialOffset, double lateralOffset) {
        return createPose(
            2.5 * TILE_WIDTH,
            config.alliance.sign * -0.5 * TILE_WIDTH
        ).face(
            getGoalPose(), config.alliance == RED ? -180 : 180
        ).axial(axialOffset).lateral(lateralOffset).face(
            getGoalPose(), config.alliance == RED ? -180 : 180
        );
    }

    public Pose getGatePose() {
        return createPose(
            0 * TILE_WIDTH,
            config.alliance.sign * -2 * TILE_WIDTH,
            toRadians(config.alliance.sign * -90)
        );
    }

    public Pose getGoalPose() {
        return new Pose(
            -2.75 * TILE_WIDTH,
            config.alliance.sign * -2.75 * TILE_WIDTH,
            toRadians(config.alliance.sign * 45)
        );
    }

    public double getGoalHeadingOffset() {
        return config.pose.x > TILE_WIDTH ?
            config.goalAngleOffsetNorth :
            config.goalAngleOffsetSouth;
    }

    public double getGoalHeadingRemaining() {
        return normalizeHeading(
            config.pose.heading - (getGoalPose().atan2(config.pose) + toRadians(getGoalHeadingOffset()))
        );
    }

    public double getGoalDistance() {
        return (config.goalLock ? config.pose : new Pose(0,0,0)).hypot(
            getGoalPose()
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
        return createPose(x, y, 0, Axial.CENTER, Lateral.CENTER);
    }

    public Pose createPose(Pose pose, Axial axial) {
        return createPose(pose, axial, Lateral.CENTER);
    }

    public Pose createPose(Pose pose, Lateral lateral) {
        return createPose(pose, Axial.CENTER, lateral);
    }

    public Pose createPose(Pose pose, Axial axial, Lateral lateral) {
        return createPose(pose.x, pose.y, pose.heading, axial, lateral);
    }

    public Pose createPose(double x, double y, double heading) {
        return createPose(x, y, heading, Axial.CENTER, Lateral.CENTER);
    }

    public Pose createPose(double x, double y, double heading, double axialOffset, double lateralOffset) {
        return createPose(x, y, heading, Axial.CENTER, Lateral.CENTER, axialOffset, lateralOffset);
    }

    public Pose createPose(double x, double y, double heading, Axial axial) {
        return createPose(x, y, heading, axial, Lateral.CENTER);
    }

    public Pose createPose(double x, double y, double heading, Axial axial, double axialOffset) {
        return createPose(x, y, heading, axial, Lateral.CENTER, axialOffset, 0);
    }

    public Pose createPose(double x, double y, double heading, Lateral lateral) {
        return createPose(x, y, heading, Axial.CENTER, lateral);
    }

    public Pose createPose(double x, double y, double heading, Lateral lateral, double lateralOffset) {
        return createPose(x, y, heading, Axial.CENTER, lateral, 0, lateralOffset);
    }

    public Pose createPose(double x, double y, double heading, Axial axial, Lateral lateral) {
        return createPose(x, y, heading, axial, lateral, 0, 0);
    }

    public Pose createPose(double x, double y, double heading, Axial axial, Lateral lateral, double axialOffset, double lateralOffset) {
        double axialHeading = normalizeHeading(heading);
        axialOffset -= axial.signum * ROBOT_LENGTH / 2;
        x += cos(axialHeading) * axialOffset;
        y += sin(axialHeading) * axialOffset;

        double lateralHeading = normalizeHeading(heading + PI / 2);
        lateralOffset -= lateral.signum * ROBOT_WIDTH / 2;
        x += cos(lateralHeading) * lateralOffset;
        y += sin(lateralHeading) * lateralOffset;
        
        return new Pose(jitter(x), jitter(y), jitter(heading));
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
