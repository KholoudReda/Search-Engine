
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
/**
 *
 * @author dina
 */
public class writefefile {
    BufferedReader br ;
    URL url;
    File f;
    boolean isTwoEqual;
    File f1;
    FileInputStream fstream1;
    FileInputStream fstream2;
    DataInputStream in1;
    DataInputStream in2;
    BufferedReader bb1;
    BufferedReader bb2;
    String strLine1, strLine2;
    FileWriter fw ;
    BufferedWriter bw;
    String inputLine = null;
    boolean bool = false;
    int i = 0;
    public writefefile(){}
    public  int makefiles(String sourceUri, int num)
    {
        int n = 0 ;
        try
        {
            url = new URL(sourceUri);
            URLConnection conn = url.openConnection();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            f = new File("urlold"+num+".txt");
            f.createNewFile();
            fw = new FileWriter(f);
            bw  = new BufferedWriter(fw);

            while ((inputLine = br.readLine()) != null) {
                bw.write(inputLine + System.getProperty("line.separator"));
            }

            bw.flush();
            bw.close();
            fw.close();
            br.close();
            n =0 ;

        }

        catch (IOException e)
        {
            System.out.println("IOException1");
            n = -1;

        }
        return n;

    }

    public  int makenewfiles(String sourceUri, int num)
    {  int n = 0 ;
        try
        {

            url = new URL(sourceUri);
            URLConnection conn = url.openConnection();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            f = new File("urlnew"+num+".txt");
            f.createNewFile();
            fw = new FileWriter(f);
            bw  = new BufferedWriter(fw);

            while ((inputLine = br.readLine()) != null) {
                bw.write(inputLine + System.getProperty("line.separator"));
            }

            bw.flush();
            bw.close();
            fw.close();
            br.close();

            n = 0;

        }
        catch (IOException e)
        {
            System.out.println("IOException1");
            System.out.println(e.getMessage());
            System.out.println("gwa"+n);
            n = -1;
            System.out.println("gwa"+n);
        }
        return n;

    }

    public void recrawl( int all )
    {
        try
        {
            int g =0 ;
            while (i!= all){
                fstream1 = new FileInputStream("urlold"+i+".txt");
                fstream2 = new FileInputStream("urlnew"+i+".txt");
                in1 = new DataInputStream(fstream1);
                bb1 = new BufferedReader(new InputStreamReader(in1));
                in2 = new DataInputStream(fstream2);
                bb2 = new BufferedReader(new InputStreamReader(in2));
                while ((strLine1 = bb1.readLine())!= null && (strLine2 = bb2.readLine()) != null)
                {
                    if(!strLine1.equals( strLine2)){
                        g =1;
                        //     System.out.println(strLine1);
                        if (strLine1.contains("queries") &&strLine1.contains("seconds") )
                            g= 0;

                    }

                }
                if(g==0 )
                    System.out.println("not changed"+i);
                else
                    System.out.println("changed"+i);
                g=0;
                i++;
            }

        }
        catch(IOException e)
        {
            System.out.println("IOException2");
            System.out.println(e.getMessage());
        }
        System.out.println("DONE");
    }
}