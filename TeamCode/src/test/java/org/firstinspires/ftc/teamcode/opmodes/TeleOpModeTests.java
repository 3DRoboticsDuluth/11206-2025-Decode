package org.firstinspires.ftc.teamcode.opmodes;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mockConstruction;

import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion;
import org.firstinspires.ftc.teamcode.commands.Commands;
import org.firstinspires.ftc.teamcode.controls.AutoControls;
import org.firstinspires.ftc.teamcode.controls.ConfigControls;
import org.firstinspires.ftc.teamcode.controls.ConveyorControls;
import org.firstinspires.ftc.teamcode.controls.DeflectorControls;
import org.firstinspires.ftc.teamcode.controls.DriveControls;
import org.firstinspires.ftc.teamcode.controls.FlywheelControls;
import org.firstinspires.ftc.teamcode.controls.GateControls;
import org.firstinspires.ftc.teamcode.controls.IntakeControls;
import org.firstinspires.ftc.teamcode.controls.KickstandControls;
import org.junit.Test;
import org.mockito.MockedConstruction;

public class TeleOpModeTests extends OpModeTestSupport {
    @Test
    public void testInitializeBuildsControlsAroundWaitForStart() {
        try (OpModeDependencies ignored = new OpModeDependencies();
             MockedConstruction<ConfigControls> configControls = mockConstruction(ConfigControls.class);
             MockedConstruction<DriveControls> driveControls = mockConstruction(DriveControls.class);
             MockedConstruction<IntakeControls> intakeControls = mockConstruction(IntakeControls.class);
             MockedConstruction<ConveyorControls> conveyorControls = mockConstruction(ConveyorControls.class);
             MockedConstruction<GateControls> gateControls = mockConstruction(GateControls.class);
             MockedConstruction<DeflectorControls> deflectorControls = mockConstruction(DeflectorControls.class);
             MockedConstruction<FlywheelControls> flywheelControls = mockConstruction(FlywheelControls.class);
             MockedConstruction<KickstandControls> kickstandControls = mockConstruction(KickstandControls.class);
             MockedConstruction<AutoControls> autoControls = mockConstruction(AutoControls.class)) {

            TestTeleOpMode opMode = new TestTeleOpMode();

            opMode.initialize();

            assertSame(BlocksOpModeCompanion.hardwareMap, opMode.hardwareMap);
            assertSame(opMode, OpMode.opMode);
            assertNotNull(OpMode.gamepad1);
            assertNotNull(OpMode.gamepad2);
            assertNotNull(Commands.auto);
            assert configControls.constructed().size() == 1;
            assert driveControls.constructed().size() == 1;
            assert intakeControls.constructed().size() == 1;
            assert conveyorControls.constructed().size() == 1;
            assert gateControls.constructed().size() == 1;
            assert deflectorControls.constructed().size() == 1;
            assert flywheelControls.constructed().size() == 1;
            assert kickstandControls.constructed().size() == 1;
            assert autoControls.constructed().size() == 1;
            assert opMode.waitForStartCalls == 1;
        }
    }
}
