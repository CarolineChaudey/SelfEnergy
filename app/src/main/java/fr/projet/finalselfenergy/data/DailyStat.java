package fr.projet.finalselfenergy.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by caroline on 03/07/17.
 */

public class DailyStat {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
    private Date _day;
    private Integer _steps;

    public DailyStat(Date day, Integer _steps) {
        this._day = day;
        this._steps = _steps;
    }

    public DailyStat(String day, Integer _steps) throws ParseException {
        this._day = DATE_FORMAT.parse(day);
        this._steps = _steps;
    }

    public String getFormattedDate() {
        return DATE_FORMAT.format(this._day);
    }

    public Date get_day() {
        return _day;
    }

    public void set_day(Date _day) {
        this._day = _day;
    }

    public Integer get_steps() {
        return _steps;
    }

    public void set_steps(Integer _steps) {
        this._steps = _steps;
    }

    @Override
    public String toString() {
        return "DailyStat{" +
                "_day=" + _day +
                ", _steps=" + _steps +
                '}';
    }
}
