package pl.bratosz.smartlockers.calculator;

import pl.bratosz.smartlockers.model.ClientArticle;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.utils.Utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Date;

public class CalcCloth {


    public static BigDecimal calculateRedemptionPrice(Cloth cloth) {
        ClientArticle clientArticle = cloth.getClientArticle();
        Date releaseDate = cloth.getReleaseDate();
        int depreciationPeriod = clientArticle.getDepreciationPeriod();
        int monthsBetween = getMonthsBetween(releaseDate, LocalDateTime.now());
        float percentPerMonth = getPercentRedemptionPerMonth(
                clientArticle.getDepreciationPercentageCap(),
                depreciationPeriod);
        BigDecimal bigDecimal = calculatePrice(
                monthsBetween,
                depreciationPeriod,
                percentPerMonth,
                clientArticle.getRedemptionPrice());
        return bigDecimal;
    }

    private static BigDecimal calculatePrice(
            int monthsBetween,
            int depreciationPeriod,
            float percentPerMonth,
            double redemptionPrice) {
        if(monthsBetween > depreciationPeriod) monthsBetween = depreciationPeriod;
        double priceForMonth = redemptionPrice * (percentPerMonth / 100f);
        return BigDecimal.valueOf(redemptionPrice - (priceForMonth * monthsBetween));
    }

    private static int getMonthsBetween(Date releaseDate, LocalDateTime now) {
        long between = ChronoUnit.MONTHS.between(Utils.convert(releaseDate), now);
        if(between < 0) {
            return 0;
        } else {
            return (int) between;
        }
    }

    private static float getPercentRedemptionPerMonth(
            int percentageCap,
            int period) {
        return percentageCap / (period * 1.0f);
    }

}
