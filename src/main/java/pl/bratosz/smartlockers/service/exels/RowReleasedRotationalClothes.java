package pl.bratosz.smartlockers.service.exels;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Date;
import java.util.Map;

import static pl.bratosz.smartlockers.service.exels.Index.*;

public class RowReleasedRotationalClothes extends LoadedRow {
    private Date releaseDate;

    public RowReleasedRotationalClothes(Row row, Map<Index, Integer> indexes) {
        super(row, indexes);
        setReleaseDate(RELEASE_DATE);
    }

    private void setReleaseDate(Index releaseDate) {
        this.releaseDate = getCellByIndex(releaseDate).getDateCellValue();
    }

    public Date getReleaseDate() {
        return releaseDate;
    }
}
