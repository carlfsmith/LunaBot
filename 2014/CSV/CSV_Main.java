import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class CSV_Main
{
    public static void main(String argv[]) throws Exception
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

        CSVFile file = new CSVFile();
        file.setFile("csv_test");
        file.setFormat(format);
        file.writeRecord(record);
        file.writeRecord(bla);
    }

}
