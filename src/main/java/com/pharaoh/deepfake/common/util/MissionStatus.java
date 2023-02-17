package com.pharaoh.deepfake.common.util;

import lombok.Data;

@Data
public class MissionStatus {
    boolean isBusy;
    String work;

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public MissionStatus(boolean isBusy, String work) {
        this.isBusy = isBusy;
        this.work = work;
    }
}
