package org.firstinspires.ftc.teamcode.adaptations.ftc;

import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.File;

public class PersistenceAdapter {
    public File getSettingsFile(String fileName) {
        return AppUtil.getInstance().getSettingsFile(fileName);
    }

    public String readFile(File file) {
        return ReadWriteFile.readFile(file);
    }

    public void writeFile(File file, String json) {
        ReadWriteFile.writeFile(file, json);
    }
}
