package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.gate;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.sorting;
import static org.mockito.Mockito.verify;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.junit.Test;

public class SortingSubsystemTest extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        sorting = new SortingSubsystem() {{
            servo = mockDevice(Servo.class);
        }};
    }

    @Test
    public void testOpen() {
        SortingSubsystem.POS = SortingSubsystem.PASS;
        sorting.sort();
        assert SortingSubsystem.POS == SortingSubsystem.SORT;
    }

    @Test
    public void testClose() {
        SortingSubsystem.POS = SortingSubsystem.SORT;
        sorting.pass();
        assert SortingSubsystem.POS == SortingSubsystem.SORT;
    }
}