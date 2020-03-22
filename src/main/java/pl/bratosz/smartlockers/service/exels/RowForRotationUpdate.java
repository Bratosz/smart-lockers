package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Date;
import java.util.Map;

import static pl.bratosz.smartlockers.service.exels.Index.*;

public class RowForRotationUpdate extends LoadedRow {
    private int ordinalNo;
    private int articleNo;
    private int lockerNo;
    private int boxNo;
    private Date washingDate;

    public RowForRotationUpdate(Row row, Map<Index, Integer> indexes) {
        super(row, indexes);
        setOrdinalNo(ORDINAL_NO);
        setArticleNo(ARTICLE_NO);
        setLockerNo(LOCKER_NO);
        setBoxNo(BOX_NO);
        setWashingDate(WASHING_DATE);
    }

    private void setOrdinalNo(Index ordinalNo) {
        this.ordinalNo = (int) getCellByIndex(ordinalNo).getNumericCellValue();
    }

    private void setArticleNo(Index articleNo) {
        this.articleNo = (int) getCellByIndex(articleNo).getNumericCellValue();
    }

    private void setLockerNo(Index lockerNo) {
        this.lockerNo = (int) getCellByIndex(lockerNo).getNumericCellValue();
    }

    private void setBoxNo(Index boxNo) {
        this.boxNo = (int) getCellByIndex(boxNo).getNumericCellValue();
    }

    private void setWashingDate(Index washingDate) {
        this.washingDate = getCellByIndex(washingDate).getDateCellValue();
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

    public Date getWashingDate() {
        return washingDate;
    }
}
