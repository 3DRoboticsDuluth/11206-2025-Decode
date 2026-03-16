package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.subsystems.DeflectorSubsystem.INC;
import static org.firstinspires.ftc.teamcode.subsystems.DeflectorSubsystem.MAX;
import static org.firstinspires.ftc.teamcode.subsystems.DeflectorSubsystem.MIN;
import static org.firstinspires.ftc.teamcode.subsystems.DeflectorSubsystem.POS;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.nav;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.deflector;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.adaptations.solverslib.ServoEx;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class DeflectorSubsystemTests extends TestHarness {
    @Override
    public void setUp() {
        super.setUp();
        deflector = new DeflectorSubsystem() {{
            errors = new ArrayList<>();
            servo = mockDevice(ServoEx.class);
        }};
    }

    @Test
    public void testPeriodic() {
        POS = MAX;
        deflector.periodic();
        verify(deflector.servo).set(POS);
    }

    @Test
    public void testPeriodicReturnsWhenUnready() {
        deflector.errors.add("disabled");
        deflector.periodic();
    }

    @Test
    public void testUp() {
        double mid = (MIN + MAX) / 2;
        POS = mid;
        deflector.up();
        assert POS == mid + INC;
    }

    @Test
    public void testDown() {
        double mid = (MIN + MAX) / 2;
        POS = mid;
        deflector.down();
        assert POS == mid - INC;
    }

    @Test
    public void testCalculatePositionAndPeriodicNaNBranch() throws Exception {
        Method method = DeflectorSubsystem.class.getDeclaredMethod("calculatePosition");
        method.setAccessible(true);

        config.started = false;
        POS = 0.42;
        assert (double) method.invoke(deflector) == 0.42;

        config.started = true;
        when(nav.getGoalDistance()).thenReturn(100.0);
        assert !Double.isNaN((double) method.invoke(deflector));

        when(nav.getGoalDistance()).thenReturn(Double.NaN);
        deflector.periodic();
    }
}
