package org.firstinspires.ftc.teamcode.opmodes;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.seattlesolvers.solverslib.command.CommandScheduler;

import org.firstinspires.ftc.teamcode.subsystems.Subsystems;
import org.junit.Test;
import org.mockito.MockedStatic;

public class OpModeTests extends OpModeTestSupport {
    @Test
    public void testWaitForStartRunsSchedulerUntilStarted() {
        CommandScheduler scheduler = mock(CommandScheduler.class);
        TestWaitOpMode opMode = new TestWaitOpMode();
        setLinearOpModeState(opMode, "isStarted", false);
        setLinearOpModeState(opMode, "stopRequested", false);

        doAnswer(invocation -> {
            setLinearOpModeState(opMode, "isStarted", true);
            return null;
        }).when(scheduler).run();

        try (MockedStatic<CommandScheduler> schedulerMock = mockStatic(CommandScheduler.class)) {
            schedulerMock.when(CommandScheduler::getInstance).thenReturn(scheduler);

            opMode.waitForStart();

            verify(scheduler, times(1)).run();
            verify(Subsystems.config).start();
        }
    }

    @Test
    public void testWaitForStartSkipsSchedulerWhenStopAlreadyRequested() {
        CommandScheduler scheduler = mock(CommandScheduler.class);
        TestWaitOpMode opMode = new TestWaitOpMode();
        setLinearOpModeState(opMode, "isStarted", false);
        setLinearOpModeState(opMode, "stopRequested", true);

        try (MockedStatic<CommandScheduler> schedulerMock = mockStatic(CommandScheduler.class)) {
            schedulerMock.when(CommandScheduler::getInstance).thenReturn(scheduler);

            opMode.waitForStart();

            verify(scheduler, never()).run();
            verify(Subsystems.config).start();
        }
    }
}
