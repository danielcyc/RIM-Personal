package rims.command;

import rims.core.ResourceList;
import rims.core.Storage;
import rims.core.Ui;

import rims.exception.RimsException;

import java.io.IOException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

//@@author isbobby
/**
 * Stats Command requires the user to enter a pair of dates with interval less
 * than 14 days. It will query for the following information and display them to
 * the user: average resource borrowed per day number of resource in use per day.
 */
public class StatsCommand extends Command {
    private Date dateFrom;
    private Date dateTill;
    private String dateFromString;
    private String dateTillString;

    /**
     * Constructor of an StatsCommand, which takes in a pair of specified dates.
     *
     * @throws RimsException catches parse exception when handling dates.
     */
    public StatsCommand(String dateFrom, String dateTill) throws RimsException {
        this.dateFromString = dateFrom;
        this.dateTillString = dateTill;
        this.dateFrom = stringToDate(dateFrom);
        this.dateTill = stringToDate(dateTill);
    }

    /**
     * The stats command takes in two dates, and displays the number of resources
     * being booked in bar graph.
     *
     * @param ui        An instance of the user interface.
     * @param storage   An instance of the Storage class.
     * @param resources The ResourceList, containing all the created Resources thus
     *                  far.
     * @throws RimsException if there is an error in parsing data.
     */
    @Override
    public void execute(Ui ui, Storage storage, ResourceList resources)
            throws RimsException {

        long interval = TimeUnit.DAYS.convert((dateTill.getTime() - dateFrom.getTime()), TimeUnit.MILLISECONDS) + 1;
        if (interval >= 15) {
            throw new RimsException("The date interval is too large (more than 14 days)");
        }
        ui.printLine();
        ui.print("Here are the required stats:");
        ui.printLine();

        ui.printDash();
        ui.print("Resource in use each day");
        ui.printDash();
        Date currentDate = dateFrom;
        int totalCount = 0;
        for (int i = 0; i < interval; i++) {
            int count = resources.getBookedNumberOfResourceForDate(currentDate);
            totalCount += count;
            String bar = "";
            for (int j = 0; j < count; j++) {
                bar += "= ";
            }
            ui.print(dateToStringWithoutTime(currentDate) + "|" + bar);
            currentDate = incrementDay(currentDate);
        }
        double average = totalCount / (double) interval;
        average = BigDecimal.valueOf(average).setScale(3, RoundingMode.HALF_UP).doubleValue();

        ui.printDash();
        ui.print("Average resource in use per day: " + Double.toString(average));
        ui.printDash();
        ui.printLine();
    }

    /**
     * Converts a date and time inputted by the user in String format, into a Date
     * object.
     *
     * @param stringDate the date and time inputted by the user in String format.
     * @return a Date object representing the date and time inputted by the user.
     * @throws RimsException stringDate cannot be formatted.
     */
    private Date stringToDate(String stringDate) throws RimsException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HHmm");
        Date dateValue;
        try {
            dateValue = formatter.parse(stringDate);
        } catch (ParseException e) {
            throw new RimsException("Invalid format of date " + stringDate + "!");
        }
        return dateValue;
    }

    /**
     * Converts a Date object to a compact String, to be saved into a data file.
     *
     * @param thisDate the Date object to be converted into a String.
     * @return a String representing the Date object.
     */
    private String dateToString(Date thisDate) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HHmm");
        String stringDate = format.format(thisDate);
        return stringDate;
    }

    /**
     * Converts a Date object to a compact String without time field.
     *
     * @param thisDate the Date object to be converted into a String.
     * @return a String representing the Date object.
     */
    private String dateToStringWithoutTime(Date thisDate) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HHmm");
        String stringDate = format.format(thisDate);
        String[] words = stringDate.split(" ");
        return words[0];
    }

    /**
     * This utility method takes in a date, increment it by 1 day, then return it.
     *
     * @param thisDate the date to be incremented.
     * @return the incremented date object.
     * @throws RimsException if newDate cannot be formatted into a Date from a String.
     */
    private Date incrementDay(Date thisDate) throws RimsException {
        String newDate = dateToString(thisDate);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HHmm");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(newDate));
        } catch (ParseException e) {
            throw new RimsException("Invalid format of date " + newDate + "!");
        }
        c.add(Calendar.DATE, 1); // number of days to add
        newDate = sdf.format(c.getTime()); // dt is now the new date
        return stringToDate(newDate);
    }

}