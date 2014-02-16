package csv;

import java.io.IOException;

/**
 * Created by Alex on 2/6/14.
 */
public class CSV_Main
{
    public static void main(String[] args) throws IOException
    {
        CSVFormat format = new CSVFormat();
        format.addItem("Value");
        format.addItem("Num");

        System.out.println(format.contains("Value"));
        System.out.println(format.contains("X"));

        System.out.println(format.getIndex("Num"));
        System.out.println(format.getIndex("X"));

        format.lockFormat();

        CSVRecord record = new CSVRecord();
        record.setFormat(format);
        record.setItem("Value", "2");

        CSVRecord bla = new CSVRecord(format);
        bla.setItem("Num", "3,3");

        CSVFileWriter file = new CSVFileWriter();
        file.setFile("csv_test");
        file.setFormat(format);
        file.writeRecord(record);
        file.writeRecord(bla);
    }
}
