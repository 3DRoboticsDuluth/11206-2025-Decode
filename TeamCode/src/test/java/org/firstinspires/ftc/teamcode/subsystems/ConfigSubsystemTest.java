package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import android.util.Log;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.game.Alliance;
import org.firstinspires.ftc.teamcode.game.Side;
import org.firstinspires.ftc.teamcode.subsystems.ConfigSubsystem.Change;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;

public class ConfigSubsystemTest extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        Subsystems.config = new ConfigSubsystem();
    }
    
    @Test
    public void testStartMethod() {
        try (MockedStatic<Log> logMock = mockStatic(Log.class)) {
            Subsystems.config.start();
            assert config.started;
            logMock.verify(() -> Log.i(ArgumentMatchers.eq(ConfigSubsystem.class.getSimpleName()), anyString()));
        }
    }
    
    @Test
    public void testUpdatingTelemetry() {
        config.auto = true;
        Subsystems.config.periodic();
        verify(telemetry, atLeastOnce()).addData(anyString(), ArgumentMatchers.any());
        verify(telemetry).addLine(anyString());
    }
    
    @Test
    public void testSwitchingAlliance() {
        config.alliance = Alliance.RED;
        config.side = Side.NORTH;
        Subsystems.config.setEditable(true);
        Subsystems.config.changeValue(Change.NEXT);
        assert config.alliance == Alliance.BLUE;
        verify(drive).resetPose();
    }
    
    @Test
    public void testSwitchingSide() {
        config.alliance = Alliance.RED;
        config.side = Side.NORTH;
        Subsystems.config.setEditable(true);
        Subsystems.config.changeItem(Change.NEXT);
        Subsystems.config.changeValue(Change.NEXT);
        assert config.side == Side.SOUTH;
        verify(drive).resetPose();
    }

    @Test
    public void testModifyingDelayValue() {
        config.delay = 10.0;
        Subsystems.config.setEditable(true);
        for (int i = 0; i < 2; i++)
            Subsystems.config.changeItem(Change.NEXT);
        Subsystems.config.changeValue(Change.NEXT);
        assert config.delay == 10.5;
        Subsystems.config.changeValue(Change.PREV);
        assert config.delay == 10.0;
        config.delay = 0.0;
        Subsystems.config.changeValue(Change.PREV);
        assert config.delay == 0.0;
        config.delay = 30.0;
        Subsystems.config.changeValue(Change.NEXT);
        assert config.delay == 30.0;
    }
}
