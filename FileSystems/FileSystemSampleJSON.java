import java.io.*;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class FileSystemSampleJSON
{
    public void createFileAndGetDetails(String fileName)
    {
        try
        {
            File fileReference = new File(fileName);
            if (fileReference.createNewFile())
            {
                System.out.println("Didn't exist; created new");
            }
            else
            {
                System.out.println("File already exists");
            }

            System.out.println(fileName + " is located at " + fileReference.getAbsolutePath());
            if (fileReference.canRead())
            {
                System.out.println(fileName + " is readable");
            }
            else
            {
                System.out.println(fileName + " is not readable");
            }
            if (fileReference.canWrite())
            {
                System.out.println(fileName + " is writable");
            }
            else
            {
                System.out.println(fileName + " is not writable");
            }
            if (fileReference.canExecute())
            {
                System.out.println(fileName + " is executable");
            }
            else
            {
                System.out.println(fileName + " is not executable");
            } 
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

   public void writeToFile(String fileName, String msg)
   {
      try (FileWriter fw = new FileWriter(fileName)) // try with resources
      {
         fw.write(msg);
         System.out.println("Wrote: " + msg + " to " + fileName);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

  public void readFromFile(String fileName)
  {
     File file = new File(fileName);
     try (Scanner reader = new Scanner(file))
     {
        String fullText = "";
        while (reader.hasNextLine())
        {
           String n1 = reader.nextLine();
           System.out.println("Next line: " + n1);
           fullText += n1;
           if (reader.hasNextLine())
           {
              fullText += System.lineSeparator();
           }
        }
     
        System.out.println("Contents of " + fileName + ": ");
        System.out.println(fullText);
     }
     catch (FileNotFoundException e)
     {
        e.printStackTrace();
     }
  }
  
    public void appendToFile(String fileName, String msg)
    {
        try (FileWriter fw = new FileWriter(fileName, true);)
        {
            fw.write(System.lineSeparator());
            fw.write(msg);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {   
        FileSystemSampleJSON fss = new FileSystemSampleJSON();
        fss.doJSONSample();
    }

    public void doJSONSample()
    {   
       String jsonFile = "sample.json";
       createFileAndGetDetails(jsonFile);
       writeToFile(jsonFile, "{}");
       try
       {
           JSONObject jo = (JSONObject)new JSONParser().parse(new FileReader(jsonFile));
           jo.put("name", "John");
           jo.put("age", "55");

           Map<String, String> map = new LinkedHashMap<String, String>(4);
           map.put("address", "123 Fake Street");
           map.put("city", "Nowhere");
           map.put("city", "Yeet");
           map.put("state", "Bliss");
           map.put("zip", "01010");

           jo.put("fulladdress", map);
           writeToFile(jsonFile, jo.toJSONString());
           readFromFile(jsonFile);
           System.out.println("Done running JSON Sample");
       }
       catch (FileNotFoundException e)
       {
           e.printStackTrace();
       }
       catch (IOException e)
       {
           e.printStackTrace();
       }
       catch (ParseException e)
       {
           e.printStackTrace();
       }

    }
}    

