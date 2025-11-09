package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.subsystems.SortingSubsystem.PASS;
import static org.firstinspires.ftc.teamcode.subsystems.SortingSubsystem.POS;
import static org.firstinspires.ftc.teamcode.subsystems.SortingSubsystem.SORT;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.sorting;
import static org.mockito.Mockito.verify;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.adaptations.hardware.Servo;
import org.junit.Test;

import java.util.ArrayList;

public class SortingSubsystemTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        sorting = new SortingSubsystem() {{
            errors = new ArrayList<>();
            servo = mockDevice(Servo.class);
        }};
    }

    @Test
    public void testPeriodic() {
        POS = SORT;
        sorting.periodic();
        verify(sorting.servo).setPosition(POS);
    }

    @Test
    public void testSort() {
        POS = PASS;
        sorting.sort();
        assert POS == SORT;
    }

    @Test
    public void testPass() {
        POS = SORT;
        sorting.pass();
        assert POS == PASS;
    }
}