import mpi.*;

public class Main
{

   static private  DBConnection DB=null;
    public static void main( String[] args )
    {

        DB=new DBConnection();

        //BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
       MPI.Init(args);
       int me = MPI.COMM_WORLD.Rank();
       int size = MPI.COMM_WORLD.Size();
        if(me==0) {
            MyCounter counter=new MyCounter();
            Spider sp = new Spider(DB);
            System.out.println("Spider created!");
            sp.startCrawler();

      }
      else{

            Indexer I=new Indexer(DB);

           // I.TEST();

            System.out.println("Indexer created!");
      }

       // DB.print_Doc();
    }


}
