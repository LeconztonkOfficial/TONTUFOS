package tontufosmp2.curse;

public class CurseData {

    private final String curseId;
    private final long activateTime;
    private boolean warned;

    public CurseData(String curseId, long activateTime) {
        this.curseId = curseId;
        this.activateTime = activateTime;
        this.warned = false;
    }

    public String curseId() {
        return curseId;
    }

    public long activateTime() {
        return activateTime;
    }

    public boolean wasWarned() {
        return warned;
    }

    public void markWarned() {
        this.warned = true;
    }
}
