package org.firstinspires.ftc.teamcode.adaptations.pedropathing;

import static org.firstinspires.ftc.teamcode.game.Config.config;

import com.seattlesolvers.solverslib.command.CommandBase;

import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;

import java.util.function.Supplier;

public class ChaseCommand extends CommandBase {
    private final Supplier<Pose> chasePoseSupplier;
    private Pose lastChasePose;

    public ChaseCommand(Supplier<Pose> chasePoseSupplier) {
        this.chasePoseSupplier = chasePoseSupplier;
    }

    @Override
    public boolean isFinished() {
        if (lastChasePose == null || config.pose.hypot(lastChasePose) > 8)
            lastChasePose = this.chasePoseSupplier.get();
        return config.pose.hypot(lastChasePose) < 2;
    }
}
