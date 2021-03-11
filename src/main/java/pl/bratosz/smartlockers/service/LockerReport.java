package pl.bratosz.smartlockers.service;

import java.util.List;

public class LockerReport {
    private int lockerNumber;
    private int loadedBoxesAmount;
    private List<Integer> duplicatedBoxes;

    public int getLockerNumber() {
        return lockerNumber;
    }

    public void setLockerNumber(int lockerNumber) {
        this.lockerNumber = lockerNumber;
    }

    public int getLoadedBoxesAmount() {
        return loadedBoxesAmount;
    }

    public void setLoadedBoxesAmount(int loadedBoxesAmount) {
        this.loadedBoxesAmount = loadedBoxesAmount;
    }

    public List<Integer> getDuplicatedBoxes() {
        return duplicatedBoxes;
    }

    public void setDuplicatedBoxes(List<Integer> duplicatedBoxes) {
        this.duplicatedBoxes = duplicatedBoxes;
    }
}
