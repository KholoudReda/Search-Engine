import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class Spider implements Runnable{
    private int max_pages=200;
    private static MyUrl page;
    protected List <MyUrl>pagesToVisit=new LinkedList<>();
    private static Set<MyUrl> VisitedPages=new HashSet<>();
    DBConnection DB;
    //public String nextt;
    SpiderLeg SL=null;
    //private List<String> beforeVisit=new LinkedList<String>();
    public static MyCounter co=new MyCounter();

    public Spider(DBConnection db){
        this.DB=db;
        SL=new SpiderLeg(DB,co);

    }

    public void startCrawler(){
        BufferedReader bfr=new BufferedReader(new InputStreamReader(System.in));

        String name;
        int n=1;

        File fileName = new File("seeds.txt");

    //    System.out.println("How many threads do you want to create in CRAWLER ?");
/*        try{
            System.out.println("in");
            name = bfr.readLine();
            System.out.println("out");
            n=Integer.parseInt(name);
            System.out.println(n);
        }

        catch(IOException i){ System.out.println("Catching exception!");}
*/
        //String fileName="SeedSet.txt";
        String line;
        try  {
            FileReader f = new FileReader(fileName);
            BufferedReader br=new BufferedReader(f);

            while((line=br.readLine())!=null)
            {
                MyUrl page=new MyUrl();
                page.setUrl(line);
                page.setParent("");
                pagesToVisit.add(page);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        for(int i=0;i<10;i++){
            Thread t =new Thread(this);
            t.start();
            //t.stop();
        }
    }

    public void run(){
        System.out.println("hi from thread "+Thread.currentThread().getId());
       // synchronized (pagesToVisit/*,VisitedPages*/)
       // {
            search();
            SL.recrawl();


        // }
    }

   synchronized public MyUrl nextURL(){
       // synchronized (pagesToVisit)

            if(VisitedPages.size()<max_pages && pagesToVisit.size()!=0)
            {
                do{
                  //  System.out.println("pagesToVisit.size = "+pagesToVisit.size());
                    page = pagesToVisit.remove(0);
                    //System.out.println(nextt);
                  //  System.out.println("VisitedPages.contains(page.getUrl())"+VisitedPages.contains(page.getUrl()));
                }while(VisitedPages.contains(page)&&pagesToVisit.size()!=0);
                VisitedPages.add(page);
              //  System.out.println("VisitedPages.add "+ page.getUrl()+" with parent "+page.getParent());
              //  System.out.println("gowa next!");
                System.out.println("VisitedPages.size = "+VisitedPages.size());

                return page;
            }

        return null;
    }

    public void search (/*String url*/){

        MyUrl currentURL;
       // synchronized(SL)
       // {
            while(VisitedPages.size()<max_pages) {
                currentURL = nextURL();
                if(currentURL!=null) {
                    System.out.println("currentURL in thread " + Thread.currentThread().getId() + " is " + currentURL.getUrl());
                    // }
                    MyUrl[] u=new MyUrl[1];
                    u[0]=currentURL;
                    SL.crawl(u);

           /* for(int i = 0; i < pagesToVisit.size(); i++) {
                   System.out.println(pagesToVisit.get(i));
            }*/
                    if(pagesToVisit.size()<200)
                    {
                     pagesToVisit.addAll(SL.getLinks());
                 //    System.out.println("pagesToVisit.size() = " +pagesToVisit.size());
                 //    System.out.println("VisitedPages.size() "+VisitedPages.size());

                    }
                }
        /*        else{
                    try{
                        System.out.println("finalize..................................");
                        this.finalize();}
                    catch (Throwable throwable) {
                    // throwable.printStackTrace();
                        System.out.println("finalize..................................");
                    }
                }
         */
        }
    }

}
