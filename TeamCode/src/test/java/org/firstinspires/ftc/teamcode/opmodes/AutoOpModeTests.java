package org.firstinspires.ftc.teamcode.opmodes;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.seattlesolvers.solverslib.command.Command;

import org.firstinspires.ftc.teamcode.game.Alliance;
import org.firstinspires.ftc.teamcode.game.Config;
import org.firstinspires.ftc.teamcode.game.Side;
import org.firstinspires.ftc.teamcode.controls.ConfigControls;
import org.firstinspires.ftc.teamcode.controls.DriveControls;
import org.junit.Test;
import org.mockito.MockedConstruction;

public class AutoOpModeTests extends OpModeTestSupport {
    @Test
    public void testInitializeSchedulesAutoCommandWhenConfigured() {
        Command autoCommand = mock(Command.class);

        try (OpModeDependencies dependencies = new OpModeDependencies(autoCommand);
             MockedConstruction<ConfigControls> configControls = mockConstruction(ConfigControls.class);
             MockedConstruction<DriveControls> driveControls = mockConstruction(DriveControls.class)) {

            TestAutoOpMode opMode = new TestAutoOpMode();
            opMode.onWait = () -> {
                Config.config.alliance = Alliance.RED;
                Config.config.side = Side.NORTH;
            };

            opMode.initialize();

            assert configControls.constructed().size() == 1;
            assert driveControls.constructed().isEmpty();
            assert opMode.waitForStartCalls == 1;
            assertSame(autoCommand, opMode.scheduledCommand);
            verify(dependencies.autoCommandsMock.constructed().get(0)).execute();
        }
    }

    @Test
    public void testInitializeThrowsWhenConfigMissing() {
        try (OpModeDependencies ignored = new OpModeDependencies();
             MockedConstruction<ConfigControls> configControls = mockConstruction(ConfigControls.class);
             MockedConstruction<DriveControls> driveControls = mockConstruction(DriveControls.class)) {

            TestAutoOpMode opMode = new TestAutoOpMode();

            RuntimeException exception = assertThrows(RuntimeException.class, opMode::initialize);

            //noinspection DataFlowIssue
            assert exception.getMessage().contains("Alliance and/or Side is null");
            assert configControls.constructed().size() == 1;
            assert driveControls.constructed().isEmpty();
            assert opMode.waitForStartCalls == 1;
        }
    }

    @Test
    public void testInitializeReturnsEarlyWhenStopRequested() {
        Command autoCommand = mock(Command.class);

        try (OpModeDependencies dependencies = new OpModeDependencies(autoCommand);
             MockedConstruction<ConfigControls> configControls = mockConstruction(ConfigControls.class);
             MockedConstruction<DriveControls> driveControls = mockConstruction(DriveControls.class)) {

            TestAutoOpMode opMode = new TestAutoOpMode();
            opMode.onWait = () -> setLinearOpModeState(opMode, "stopRequested", true);

            opMode.initialize();

            assert configControls.constructed().size() == 1;
            assert driveControls.constructed().isEmpty();
            assert opMode.waitForStartCalls == 1;
            assert opMode.scheduledCommand == null;
            verify(dependencies.autoCommandsMock.constructed().get(0), never()).execute();
        }
    }

    @Test
    public void testInitializeThrowsWhenOnlySideMissing() {
        try (OpModeDependencies ignored = new OpModeDependencies();
             MockedConstruction<ConfigControls> configControls = mockConstruction(ConfigControls.class);
             MockedConstruction<DriveControls> driveControls = mockConstruction(DriveControls.class)) {

            TestAutoOpMode opMode = new TestAutoOpMode();
            opMode.onWait = () -> Config.config.alliance = Alliance.RED;

            RuntimeException exception = assertThrows(RuntimeException.class, opMode::initialize);

            //noinspection DataFlowIssue
            assert exception.getMessage().contains("Alliance and/or Side is null");
            assert configControls.constructed().size() == 1;
            assert driveControls.constructed().isEmpty();
            assert opMode.waitForStartCalls == 1;
        }
    }
}
