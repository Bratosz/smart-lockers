package pl.bratosz.smartlockers.exels;

import java.util.Date;

public class LoadedRow {
    private String lastName;
    private String firstName;
    private long barCode;
    private int ordinalNo;
    private int articleNo;
    private int lockerNo;
    private int boxNo;
    private Date releaseDate;
    private Date washingDate;

    public LoadedRow(String lastName,
                     String firstName,
                     String barCode,
                     double ordinalNo,
                     double articleNo,
                     double lockerNo,
                     double boxNo,
                     Date washingDate) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.barCode = Long.parseLong(barCode);
        this.ordinalNo = (int) ordinalNo;
        this.articleNo = (int) articleNo;
        this.lockerNo = (int) lockerNo;
        this.boxNo = (int) boxNo;
        this.washingDate = washingDate;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public long getBarCode() {
        return barCode;
    }

    public int getOrdinalNo() {
        return ordinalNo;
    }

    public int getArticleNo() {
        return articleNo;
    }

    public int getLockerNo() {
        return lockerNo;
    }

    public int getBoxNo() {
        return boxNo;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public Date getWashingDate() {
        return washingDate;
    }

}
