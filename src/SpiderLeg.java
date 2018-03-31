import mpi.MPI;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.Vector;


public class SpiderLeg /*implements Runnable*/{

//    static private int count=0;

    //public static MyCounter count=new MyCounter();
    private  static int c;
    private int h = 0;
   // private Document htmlDocs;
    private Vector<MyUrl> l=new Vector<>();
    private String USER_AGENT="Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:58.0) Gecko/20100101 Firefox/58.0";
    int maxToVisit;

    private final  Vector<String> URLs =new Vector<>();
    private Vector<String> allURLs =new Vector<>();
    File f;
    FileWriter fw ;
    BufferedWriter bw;
    DBConnection DB;


    public SpiderLeg(DBConnection db,MyCounter c){
        this.DB=db;
       // count=c;
        this.c=0;

    }

    public void crawl(MyUrl[] url) {
        System.out.println("thread " + Thread.currentThread().getId() + " is CRAWLING!");
     //   int count = 0;
     //   MyUrl myUrl = new MyUrl();
     //   myUrl.setUrl(url);
        try {
            Connection connection;
            connection = Jsoup.connect(url[0].getUrl());
            connection.userAgent(USER_AGENT);
            Document htmldoc = connection.get();
            String s=htmldoc.toString();
            if(s.length()==0){
                System.out.println("Noooooooooooooooooooooooooooooooo HTML!");
            }
            System.out.println("htmldoc.toString().length() "+htmldoc.toString().length());
            url[0].setHtml(s);

            Elements links = htmldoc.select("a[href]");
            //System.out.println("------------------------------------------------------------------"+links);
            readfilerobots rfb = new readfilerobots();
            recrawler reclaw = new recrawler();////////////
            URL verifiedURL;
            url[0].setPointTo(links.size());


            System.out.println("------------------------------------------------------------------links.size() "+links.size());

          //  myUrl.setHtml(htmldoc);
            /////////////////////////////////////////////////////////////////////////////////////
          //  List<String> parents=new LinkedList<String>() ;


        //    System.out.println("myUrl.getUrl()"+url[0].getUrl());
        //    System.out.println("myUrl.getPointTo()"+url[0].getPointTo());
        //    System.out.println("myUrl.getHtml().toStringggggggggggggggggggggggggggggg()"+myUrl.getHtml().toString());
 //           System.out.println("myUrl.getParent()"+myUrl.getParent());

         //   c++;
            Boolean inserted;
        //    inserted = DB.insert_crawler(c,url[0]/*,parents*/);
            int n =0;
            int time;
            LocalTime now ;
            synchronized(URLs){
                c++;
                now = LocalTime.now();
                time = (now.getHour()*60) + now.getMinute();
                n = reclaw.rec(url[0].getUrl().toString());
                inserted = DB.insert_crawler(c,url[0] , n , time/*,parents*/);
            }
            int[] arr=new int[1];
            arr[0]=c;
            if(inserted)
            {
                try{Thread.sleep(1000);}
                catch (InterruptedException e){System.out.println("Interrupted!!!");}
                MPI.COMM_WORLD.Isend(arr,0,1,MPI.INT,1,10);}

            maxToVisit=0;

            for (Element link : links) {
                //System.out.println("beforeVisit.size() "+beforeVisit.size());
                //System.out.println("beforeVisit "+beforeVisit.get(i));
                verifiedURL = rfb.verifyUrl(link.attr("abs:href"));
                if (verifiedURL != null && rfb.isRobotAllowed(verifiedURL) && maxToVisit < 100) {
                    MyUrl page=new MyUrl();
                    page.setParent(url[0].getUrl());
                    page.setUrl(verifiedURL.toString());

                    //System.out.println("sob7y "+document);
                   // count++;
                    maxToVisit++;

                    l.add(page);
                   // System.out.println("num of urls "+l.size());
                   // System.out.println(verifiedURL);
                  //  count = l.size();
                    URLs.add(verifiedURL.toString());

                    //l.add(link.attr("abs:href"));
                } else if (maxToVisit >= 100 &&  h == 0) {
                    h =1 ;
                    break;
                }
                //l.add(link.attr("abs:href"));
                // System.out.println("l.size()"+l.size());
            }
        //    System.out.println("l.size()" + l.size());
        }
        catch (java.net.UnknownHostException e){
            System.out.println("da error mo5talef.");
        //    e.printStackTrace();
        }
        catch(java.net.SocketTimeoutException e){
            System.out.println("Time out exception !");
        //    e.printStackTrace();
        }
        catch (javax.net.ssl.SSLHandshakeException e){
            System.out.println("SSLHandshakeException !");
         //   e.printStackTrace();
        }
        catch (org.jsoup.UncheckedIOException UncheckedIOException){
            System.out.println("UncheckedIOException!");
          //  UncheckedIOException.printStackTrace();
        }
         catch (IOException ex) {
            System.out.println("exception while crawling! ");
          //  ex.printStackTrace();
        }

    }

    public void recrawl() {

        allURLs = DB.allUrls();
        for(int i =0 ; i < allURLs.size() ; i++){
            String s =allURLs.get(i);

            try{
                Connection connection;
                connection = Jsoup.connect(s);
                connection.userAgent(USER_AGENT);
                Document htmldoc = connection.get();
                String s1=htmldoc.toString();
                DB.AddDoc_Time(s, s1);
            }
            catch (java.net.UnknownHostException e){
                System.out.println("da error mo5talef.");
                //    e.printStackTrace();
            }
            catch(java.net.SocketTimeoutException e){
                System.out.println("Time out exception !");
                //    e.printStackTrace();
            }
            catch (javax.net.ssl.SSLHandshakeException e){
                System.out.println("SSLHandshakeException !");
                //   e.printStackTrace();
            }
            catch (org.jsoup.UncheckedIOException UncheckedIOException){
                System.out.println("UncheckedIOException!");
                //  UncheckedIOException.printStackTrace();
            }
            catch (IOException ex) {
                System.out.println("exception while crawling! ");
                //  ex.printStackTrace();
            }

        }
    }

    public Vector<MyUrl> getLinks(){
        return l;
    }

    public synchronized void printINfile()
    {
        writefefile rf = new writefefile();
        f = new File("allURLS.txt");
        int ret =0 ;
        try
        {
            f.createNewFile();
            fw = new FileWriter(f);
            bw  = new BufferedWriter(fw);
            for(int i =0 ; i < l.size() ; i++){
                // System.out.println("----->"+(URLs.elementAt(i)));
                ret= rf.makefiles(URLs.elementAt(i), i);
                while (ret ==-1)
                {
                    ret = rf.makefiles(URLs.elementAt(i), i);
                }
                bw.write((URLs.elementAt(i))+ System.getProperty("line.separator"));
            }
            bw.flush();
            bw.close();
            fw.close();
            System.out.println("Write all urls is finished");
        }
        catch (IOException ex)
        {
            System.out.println("file!");
            System.out.println(ex.getMessage());
        }
    }
}
