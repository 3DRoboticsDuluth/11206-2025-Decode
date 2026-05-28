package org.firstinspires.ftc.teamcode.adaptations.util;

import com.qualcomm.robotcore.util.ElapsedTime;

public class Debounce {
    public ElapsedTime timer;
    private boolean previous = false;

    public Debounce() {
        this(new ElapsedTime());
    }

    public Debounce(ElapsedTime timer) {
        this.timer = timer;
    }

    public boolean triggered(boolean current, double threshold) {
        boolean tripped = current && !previous;
        boolean debounced = tripped && timer.seconds() >= threshold;

        previous = current;
        if (current) timer.reset();

        return debounced;
    }

    public void reset() {
        previous = false;
        timer.reset();
    }
}
