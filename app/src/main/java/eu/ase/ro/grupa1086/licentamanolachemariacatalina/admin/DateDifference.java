package eu.ase.ro.grupa1086.licentamanolachemariacatalina.admin;

import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateDifference {

    public static void compareDates(String d1,String d2)
    {
        try{
            // If you already have date objects then skip 1

            //1
            // Create 2 dates starts
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ROOT);
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();

            if(date1 != null && date2 != null) {
                cal1.setTime(date1);
                cal2.setTime(date2);

                Date objDate = new Date(); // Current System Date and time is assigned to objDate
                System.out.println(sdf.format(objDate));


                System.out.println("Date1"+sdf.format(date1));
                System.out.println("Date2"+sdf.format(date2));System.out.println();

                // Create 2 dates ends
                //1

                // Date object is having 3 methods namely after,before and equals for comparing
                // after() will return true if and only if date1 is after date 2
                if(date1.after(date2)){
                    System.out.println("Data de început nu poate să fie după data de sfârșit");
                    return;
                }
                // before() will return true if and only if date1 is before date2
                if(date2.before(date1)){
                    System.out.println("Data de sfârșit nu poate să fie înainte de data de început");
                }

                //equals() returns true if both the dates are equal
//            if(date1.equals(date2)){
//                System.out.println("Date1 is equal Date2");
//            }

                System.out.println();
            }

        }
        catch(ParseException ex){
            ex.printStackTrace();
        }
    }

    public static int compareDates(Date date1,Date date2)
    {
        // if you already have date objects then skip 1
        //1
        //1
        int result = 0;
        //date object is having 3 methods namely after,before and equals for comparing
        //after() will return true if and only if date1 is after date 2
        if(date1.after(date2)){
            System.out.println("Data de început nu poate să fie după data de sfârșit");
            result = 1;
            return result;
        }

        //before() will return true if and only if date1 is before date2
        if(date2.before(date1)){
            System.out.println("Data de sfârșit nu poate să fie înainte de data de început");
            result = -1;
            return result;
        }


        //equals() returns true if both the dates are equal
//        if(date1.equals(date2)){
//            System.out.println("Date1 is equal Date2");
//        }

        return result;
    }
}
