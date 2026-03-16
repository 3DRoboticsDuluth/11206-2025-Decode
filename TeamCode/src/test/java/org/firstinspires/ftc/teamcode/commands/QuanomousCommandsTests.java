package org.firstinspires.ftc.teamcode.commands;

import static org.firstinspires.ftc.teamcode.commands.Commands.auto;
import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.game.Side.NORTH;
import static org.firstinspires.ftc.teamcode.game.Side.SOUTH;
import static org.firstinspires.ftc.teamcode.subsystems.NavSubsystem.TILE_WIDTH;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.seattlesolvers.solverslib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.adaptations.odometry.Pose;
import org.firstinspires.ftc.teamcode.adaptations.vision.Quanomous;
import org.firstinspires.ftc.teamcode.game.Alliance;
import org.firstinspires.ftc.teamcode.subsystems.NavSubsystem;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.MockedStatic;

public class QuanomousCommandsTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        config.alliance = Alliance.RED;
        when(auto.deposit(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyDouble(), org.mockito.ArgumentMatchers.anyDouble()))
            .thenAnswer(invocation -> new InstantCommand());
        when(auto.chase(org.mockito.ArgumentMatchers.anyInt()))
            .thenAnswer(invocation -> new InstantCommand());
        when(auto.intake(org.mockito.ArgumentMatchers.anyInt()))
            .thenAnswer(invocation -> new InstantCommand());
        when(auto.gateIntake())
            .thenAnswer(invocation -> new InstantCommand());
        when(auto.releaseGate())
            .thenAnswer(invocation -> new InstantCommand());
        when(auto.park(org.mockito.ArgumentMatchers.anyBoolean(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
            .thenAnswer(invocation -> new InstantCommand());
        when(auto.drive(org.mockito.ArgumentMatchers.any()))
            .thenAnswer(invocation -> new InstantCommand());
        when(nav.createPose(org.mockito.ArgumentMatchers.anyDouble(), org.mockito.ArgumentMatchers.anyDouble(),
            org.mockito.ArgumentMatchers.anyDouble(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
            .thenReturn(new Pose(0, 0, 0));
    }

    @Test
    public void testIntakeAndGateAndRelease() throws Exception {
        JSONObject intake = mock(JSONObject.class);
        when(intake.getInt("spike")).thenReturn(2);
        QuanomousCommands.intake(intake).initialize();
        verify(auto).intake(2);

        QuanomousCommands.intakeGate(mock(JSONObject.class)).initialize();
        verify(auto).gateIntake();

        QuanomousCommands.release(mock(JSONObject.class)).initialize();
        verify(auto).releaseGate();
    }

    @Test
    public void testDepositHandlesNearAndFarLocales() throws Exception {
        JSONObject near = mock(JSONObject.class);
        when(near.getString("locale")).thenReturn("near");
        when(near.getDouble("txo")).thenReturn(1.0);
        when(near.getDouble("tyo")).thenReturn(2.0);

        JSONObject far = mock(JSONObject.class);
        when(far.getString("locale")).thenReturn("far");
        when(far.getDouble("txo")).thenReturn(1.0);
        when(far.getDouble("tyo")).thenReturn(2.0);

        QuanomousCommands.deposit(near).initialize();
        QuanomousCommands.deposit(far).initialize();

        verify(auto).deposit(SOUTH, 1.0 * TILE_WIDTH, 2.0 * TILE_WIDTH);
        verify(auto).deposit(NORTH, 1.0 * TILE_WIDTH, 2.0 * TILE_WIDTH);
    }

    @Test
    public void testChaseHandlesZeroAndExplicitCycles() throws Exception {
        JSONObject zero = mock(JSONObject.class);
        when(zero.getInt("cycles")).thenReturn(0);

        JSONObject three = mock(JSONObject.class);
        when(three.getInt("cycles")).thenReturn(3);

        QuanomousCommands.chase(zero).initialize();
        QuanomousCommands.chase(three).initialize();

        verify(auto).chase(Integer.MAX_VALUE);
        verify(auto).chase(3);
    }

    @Test
    public void testParseAxialCoversAllCases() {
        assert QuanomousCommands.parseAxial("front") == NavSubsystem.Axial.FRONT;
        assert QuanomousCommands.parseAxial("back") == NavSubsystem.Axial.BACK;
        assert QuanomousCommands.parseAxial("center") == NavSubsystem.Axial.CENTER;
    }

    @Test
    public void testParseLateralCoversAllCases() {
        assert QuanomousCommands.parseLateral("left") == NavSubsystem.Lateral.LEFT;
        assert QuanomousCommands.parseLateral("right") == NavSubsystem.Lateral.RIGHT;
        assert QuanomousCommands.parseLateral("center") == NavSubsystem.Lateral.CENTER;
    }

    @Test
    public void testParkAndDrive() throws Exception {
        JSONObject park = mock(JSONObject.class);
        when(park.optString("axial", "center")).thenReturn("front");
        when(park.optString("lateral", "center")).thenReturn("left");
        when(park.optBoolean("gate", false)).thenReturn(true);
        QuanomousCommands.park(park).initialize();
        verify(auto).park(true, NavSubsystem.Axial.FRONT, NavSubsystem.Lateral.LEFT);

        JSONObject driveObject = mock(JSONObject.class);
        when(driveObject.getDouble("tx")).thenReturn(1.0);
        when(driveObject.getDouble("ty")).thenReturn(-2.0);
        when(driveObject.getDouble("h")).thenReturn(90.0);
        when(driveObject.optString("axial", "center")).thenReturn("back");
        when(driveObject.optString("lateral", "center")).thenReturn("right");
        QuanomousCommands.drive(driveObject).initialize();
        verify(nav).createPose(1.0 * TILE_WIDTH, 2.0 * -config.alliance.sign * TILE_WIDTH, 90.0, NavSubsystem.Axial.BACK, NavSubsystem.Lateral.RIGHT);
        verify(auto).drive(org.mockito.ArgumentMatchers.any());
    }

    @Test
    public void testExecuteHandlesEmptyAndPopulatedJsonArrays() throws Exception {
        config.quanomous = "test.json";
        QuanomousCommands subject = new QuanomousCommands();
        JSONArray empty = mock(JSONArray.class);
        when(empty.length()).thenReturn(0);

        JSONObject delay = mock(JSONObject.class);
        when(delay.getString("cmd")).thenReturn("delay");
        when(delay.getDouble("seconds")).thenReturn(1.0);

        JSONArray populated = mock(JSONArray.class);
        when(populated.length()).thenReturn(1);
        when(populated.getJSONObject(0)).thenReturn(delay);

        try (MockedStatic<Quanomous> quanomousMock = mockStatic(Quanomous.class)) {
            quanomousMock.when(() -> Quanomous.load("test.json"))
                .thenReturn(empty)
                .thenReturn(populated);

            subject.execute();
            subject.execute();
        }
    }

    @Test
    public void testExecuteRethrowsLoadFailures() {
        config.quanomous = "broken.json";
        QuanomousCommands subject = new QuanomousCommands();
        try (MockedStatic<Quanomous> quanomousMock = mockStatic(Quanomous.class)) {
            quanomousMock.when(() -> Quanomous.load("broken.json"))
                .thenThrow(new RuntimeException("broken"));
            assertThrows(RuntimeException.class, subject::execute);
        }
    }

    @Test
    public void testUncheckedLambdaWrapsExceptions() {
        assertThrows(RuntimeException.class, () ->
            QuanomousCommands.Lambda.unchecked(ignored -> {
                throw new Exception("boom");
            }).apply("x")
        );
    }
}
