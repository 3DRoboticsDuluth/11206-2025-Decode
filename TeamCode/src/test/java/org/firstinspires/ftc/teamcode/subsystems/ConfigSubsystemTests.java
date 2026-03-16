package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.game.Config.config;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.opMode;
import static org.firstinspires.ftc.teamcode.opmodes.OpMode.telemetry;
import static org.firstinspires.ftc.teamcode.subsystems.Subsystems.drive;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.TestHarness;
import org.firstinspires.ftc.teamcode.adaptations.ftc.PersistenceAdapter;
import org.firstinspires.ftc.teamcode.game.Alliance;
import org.firstinspires.ftc.teamcode.game.Side;
import org.firstinspires.ftc.teamcode.opmodes.OpMode;
import org.firstinspires.ftc.teamcode.subsystems.ConfigSubsystem.Change;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigSubsystemTests extends TestHarness {
    @Autonomous
    private static class AutoAnnotatedOpMode extends OpMode {
        @Override public void initialize() {}
    }

    private static class TestPersistenceAdapter extends PersistenceAdapter {
        static File settingsFile;
        static String readValue;
        static RuntimeException readFailure;
        static final List<String> writes = new ArrayList<>();

        @Override
        public File getSettingsFile(String fileName) {
            return settingsFile;
        }

        @Override
        public String readFile(File file) {
            if (readFailure != null) throw readFailure;
            return readValue;
        }

        @Override
        public void writeFile(File file, String json) {
            writes.add(file.getPath() + "|" + json);
        }
    }

    @Override
    public void setUp() {
        super.setUp();
        ConfigSubsystem.PERSISTENCE = false;
        ConfigSubsystem.persistence = new PersistenceAdapter();
        setThread(null);
        TestPersistenceAdapter.settingsFile = null;
        TestPersistenceAdapter.readValue = null;
        TestPersistenceAdapter.readFailure = null;
        TestPersistenceAdapter.writes.clear();
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
    public void testStartMethodReturnsWhenAlreadyStartedAndHandlesLoggingFailure() {
        config.started = true;
        Subsystems.config.start();

        config.started = false;
        try (MockedStatic<Log> logMock = mockStatic(Log.class)) {
            logMock.when(() -> Log.i(ArgumentMatchers.eq(ConfigSubsystem.class.getSimpleName()), anyString()))
                .thenThrow(new RuntimeException("boom"));
            Subsystems.config.start();
            logMock.verify(() -> Log.w(ArgumentMatchers.eq("Problem logging current config"), any(Throwable.class)));
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
        verify(drive).configureFollower(null);
    }
    
    @Test
    public void testSwitchingSide() {
        config.alliance = Alliance.RED;
        config.side = Side.NORTH;
        Subsystems.config.setEditable(true);
        Subsystems.config.changeItem(Change.NEXT);
        Subsystems.config.changeValue(Change.NEXT);
        assert config.side == Side.SOUTH;
        verify(drive).configureFollower(null);
    }

    @Test
    public void testModifyingDelayValue() {
        config.delay = 10.0;
        Subsystems.config.setEditable(true);
        for (int i = 0; i < 4; i++)
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

    @Test
    @SuppressWarnings("unchecked")
    public void testItemTelemetrySuppliersAndConsumers() throws Exception {
        Field itemsField = ConfigSubsystem.class.getDeclaredField("items");
        itemsField.setAccessible(true);
        List<ConfigSubsystem.Item> items = (List<ConfigSubsystem.Item>) itemsField.get(Subsystems.config);

        config.alliance = Alliance.RED;
        config.side = Side.NORTH;
        config.delay = 1.0;
        config.responsiveness = 0.5;
        config.robotCentric = false;
        config.goalDistanceOffsetSouth = 0;
        config.goalDistanceOffsetNorth = 0;
        config.goalAngleOffsetSouth = 0;
        config.goalAngleOffsetNorth = 0;

        for (ConfigSubsystem.Item item : items) {
            assert item.telemetrySupplier.get() != null;
            item.changeConsumer.accept(Change.NEXT);
        }

        for (ConfigSubsystem.Item item : items)
            item.changeConsumer.accept(Change.PREV);
    }

    @Test
    public void testChangeItemAndChangeValueGuards() {
        Subsystems.config.setEditable(false);
        Subsystems.config.changeItem(Change.NEXT);
        Subsystems.config.changeValue(Change.NEXT);

        Subsystems.config.setEditable(true);
        config.started = true;
        Subsystems.config.changeValue(Change.NEXT);
    }

    @Test
    public void testChangeValueEditableStartedAllowedBranch() {
        Subsystems.config.setEditable(true);
        config.started = true;
        for (int i = 0; i < 5; i++)
            Subsystems.config.changeItem(Change.NEXT);
        config.responsiveness = 0.5;
        Subsystems.config.changeValue(Change.NEXT);
        assert config.responsiveness > 0.5;
    }

    @Test
    public void testGetCaptionBranches() throws Exception {
        Method method = ConfigSubsystem.class.getDeclaredMethod("getCaption", String.class);
        method.setAccessible(true);

        Subsystems.config.setEditable(false);
        assert method.invoke(Subsystems.config, "Alliance").toString().equals("Config (Alliance)");

        Subsystems.config.setEditable(true);
        config.started = false;
        assert method.invoke(Subsystems.config, "Alliance").toString().startsWith(">");

        config.started = true;
        assert method.invoke(Subsystems.config, "Alliance").toString().startsWith("x");
    }

    @Test
    public void testGetCaptionNonSelectedEditableBranch() throws Exception {
        Method method = ConfigSubsystem.class.getDeclaredMethod("getCaption", String.class);
        method.setAccessible(true);
        Subsystems.config.setEditable(true);
        assert method.invoke(Subsystems.config, "Side").toString().equals("Config (Side)");

        for (int i = 0; i < 5; i++)
            Subsystems.config.changeItem(Change.NEXT);
        config.started = true;
        assert method.invoke(Subsystems.config, "Responsiveness").toString().startsWith(">");
    }

    @Test
    public void testResetGuardBranches() throws Exception {
        Method reset = ConfigSubsystem.class.getDeclaredMethod("reset");
        reset.setAccessible(true);

        config.alliance = null;
        config.side = Side.NORTH;
        reset.invoke(Subsystems.config);
        verify(drive, never()).configureFollower(any());

        config.alliance = Alliance.RED;
        config.side = null;
        reset.invoke(Subsystems.config);
        verify(drive, never()).configureFollower(any());
    }

    @Test
    public void testConstructorCreatesConfigAndAutoModeBranch() {
        org.firstinspires.ftc.teamcode.game.Config.config = null;
        opMode = mock(AutoAnnotatedOpMode.class);
        ConfigSubsystem subsystem = new ConfigSubsystem();
        assert org.firstinspires.ftc.teamcode.game.Config.config != null;
        assert config.auto;
        assert !config.teleop;
        assert !config.interrupt;
        assert !config.started;
        assert !config.goalLock;
    }

    @Test
    public void testConstructorLoadsPersistedConfigAndHandlesReadFailure() {
        File file = getBuildTempFile("config-test.json");
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        org.firstinspires.ftc.teamcode.game.Config.config = null;
        ConfigSubsystem.PERSISTENCE = true;
        ConfigSubsystem.persistence = new TestPersistenceAdapter();
        TestPersistenceAdapter.settingsFile = file;
        TestPersistenceAdapter.readValue = "{}";

        try (MockedStatic<Log> logMock = mockStatic(Log.class)) {
            ConfigSubsystem subsystem = new ConfigSubsystem();
            assert subsystem != null;
            logMock.verify(() -> Log.i(ArgumentMatchers.eq(ConfigSubsystem.class.getSimpleName()), ArgumentMatchers.eq("Config loaded")));

            org.firstinspires.ftc.teamcode.game.Config.config = null;
            logMock.reset();
            TestPersistenceAdapter.readFailure = new RuntimeException("boom");
            new ConfigSubsystem();
            logMock.verify(() -> Log.w(ArgumentMatchers.eq(ConfigSubsystem.class.getSimpleName()), ArgumentMatchers.eq("Config failed to load"), any(Throwable.class)));
        } finally {
            ConfigSubsystem.PERSISTENCE = false;
            ConfigSubsystem.persistence = new PersistenceAdapter();
        }
    }

    @Test
    public void testConstructorSkipsMissingPersistedFile() {
        org.firstinspires.ftc.teamcode.game.Config.config = null;
        ConfigSubsystem.PERSISTENCE = true;
        ConfigSubsystem.persistence = new TestPersistenceAdapter();
        TestPersistenceAdapter.settingsFile = getBuildTempFile("missing-config.json");

        new ConfigSubsystem();

        assert org.firstinspires.ftc.teamcode.game.Config.config != null;
        ConfigSubsystem.PERSISTENCE = false;
        ConfigSubsystem.persistence = new PersistenceAdapter();
    }

    @Test
    public void testPeriodicPersistenceBranchesAndThreadRun() throws Exception {
        File file = getBuildTempFile("config-write.json");
        ConfigSubsystem.PERSISTENCE = true;
        ConfigSubsystem.persistence = new TestPersistenceAdapter();
        setThread(null);
        TestPersistenceAdapter.settingsFile = file;

        try {
            Subsystems.config = new ConfigSubsystem();
            Subsystems.config.periodic();
            Thread thread = getThread();
            thread.join(1000);
            assert TestPersistenceAdapter.writes.size() == 1;
            assert TestPersistenceAdapter.writes.get(0).startsWith(file.getPath() + "|");

            Thread alive = new Thread(() -> {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {}
            });
            alive.start();
            setThread(alive);
            TestPersistenceAdapter.writes.clear();
            Subsystems.config.periodic();
            assert TestPersistenceAdapter.writes.isEmpty();
            alive.join(1000);

            Thread dead = new Thread(() -> {});
            dead.start();
            dead.join(1000);
            setThread(dead);
            Subsystems.config.periodic();
            Thread restarted = getThread();
            restarted.join(1000);
            assert TestPersistenceAdapter.writes.size() == 1;
        } finally {
            setThread(null);
            ConfigSubsystem.PERSISTENCE = false;
            ConfigSubsystem.persistence = new PersistenceAdapter();
        }
    }

    private static Thread getThread() throws Exception {
        Field threadField = ConfigSubsystem.class.getDeclaredField("thread");
        threadField.setAccessible(true);
        return (Thread) threadField.get(null);
    }

    private static void setThread(Thread thread) {
        try {
            Field threadField = ConfigSubsystem.class.getDeclaredField("thread");
            threadField.setAccessible(true);
            threadField.set(null, thread);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static File getBuildTempFile(String name) {
        return new File(new File("build", "tmp"), name);
    }
}
