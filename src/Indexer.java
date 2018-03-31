//import com.mongodb.BasicDBObject;
//import com.mongodb.DBObject;
import shaded.com.mongodb.*;

import mpi.MPI;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.tartarus.snowball.ext.englishStemmer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;


public class Indexer implements Runnable {

    private DBConnection Database;

    public  Indexer(DBConnection DB){

        Database=DB;


        //Create threads
        for(int i=0;i<5;i++){
            Thread t =new Thread(this);
            t.start();
        }

//           run();

    }

    public void run(){

        while(true) {
            int[] Rec = new int[1];

            MPI.COMM_WORLD.Recv(Rec, 0, 1, MPI.INT, 0, 10);

           // if(Integer.toString(Rec[0])!=null)
            System.out.println("------------------------------------------------>"+Rec[0]);
                Do_Work(Rec[0]);

        }
    }


    public void Do_Work(int PageId){

        System.out.println("the number of page is "+PageId);

        String D=Database.GetHtml(PageId);

        String Keyword,Stemed;
        HashMap<String,Word>Word_tobeInserted = new HashMap<String,Word>();
        englishStemmer stemm=new englishStemmer();
        String[] zaTitle=new String[1];
        String[] zaHeader=new String[1];

        String [] Ret=doScrapping(D,zaTitle,zaHeader);

        //String [] Ret=doScrapping("<p>An <a href='http://example.com/'><b>example</b></a> link.</p>");
        //System.out.println(Ret[0]);



        for(int i=0;i<Ret.length;i++) {

            Keyword = Ret[i];
            if (Keyword.length() > 1 || Keyword == "a" || Keyword=="i") {

                //--------------->stem the word
                stemm.setCurrent(Keyword);
                stemm.stem();
                Stemed = stemm.getCurrent();


                if (Word_tobeInserted.containsKey(Keyword)) {

                    Word_tobeInserted.get(Keyword).Addposition(PageId, i);
                    ;
                } else {
                    Word_tobeInserted.put(Keyword, new Word(Keyword, Stemed, PageId, i));
                }
            }
        }

        //Adding the title data

        if(zaTitle[0].length() != 0){

            String[] Title = zaTitle[0].split("\\s");
            for(int i=0;i<Title.length;i++){
                if(Title[i].length() != 0){
                    if (Word_tobeInserted.containsKey(Title[i]))
                        Word_tobeInserted.get(Title[i]).Inc_TitleCount();
                }
            }
        }

        //Adding the headers data
        if(zaHeader !=null){
            String[] Header = zaHeader[0].split("\\s");
            for(int i=0;i<Header.length;i++){
                if(Header[i].length() != 0) {
                    if (Word_tobeInserted.containsKey(Header[i]))

                        Word_tobeInserted.get(Header[i]).Inc_HeaderCount();
                }
            }
        }

        //update this new data to database
        Update_Database(Word_tobeInserted,PageId);

        // AT The end of the loop "iterating on the documents" clear the maps
        Word_tobeInserted.clear();

    }



    public synchronized  void Update_Database(HashMap<String,Word>Words,int PageId){

        ArrayList<DBObject> Word_Objects = new ArrayList<DBObject>();

        //Remove this document from the database
        Database.RemoveDocFromWords(PageId);


        System.out.println("the # of words"+Words.size());
        for(Entry<String, Word> m:Words.entrySet()){
            if(Database.Word_Exists(m.getKey())){
                //Insert these words to the database
                Database.AddDocToWord(m.getKey(),m.getValue().getDocument());
            }
            else

                Word_Objects.add(create_Word_DBObject(m.getValue()));
        }


        //Insert these words to the database
        Database.Add_New_Words(Word_Objects);
        Word_Objects.clear();
    }


    //return the array of words in web page,T is the title,H the headers

    public String[] doScrapping(String html,String [] T,String [] H){

        Document doc = Jsoup.parse(html);

        String text = doc.body().text();

        text=text.toLowerCase();
        text=text.replaceAll("[^A-Za-z0-9]", " ");
        text=text.replaceAll("[0123456789]"," ");

        String[] splited = text.split("\\s");  //split it to be separate words in an array

        T[0]=doc.title();
        T[0]=T[0].toLowerCase();
        T[0]=T[0].replaceAll("[^A-Za-z0-9]", " ");
        T[0]=T[0].replaceAll("[0123456789]"," ");


        H[0] = doc.select("h1").text();
        H[0]+=" "+doc.select("h2").text();
        H[0]+=" "+doc.select("h3").text();
        H[0]+=" "+doc.select("h4").text();
        H[0]+=" "+doc.select("h5").text();
        H[0]+=" "+doc.select("h6").text();

        H[0]=H[0].toLowerCase();
        H[0]=H[0].replaceAll("[^A-Za-z0-9]", " ");
        H[0]=H[0].replaceAll("[0123456789]"," ");

        return splited;


    }


    private static BasicDBObject create_Word_DBObject(Word W) {


        BasicDBObject zaWord = new BasicDBObject();
        zaWord.put("id", W.getId());
        zaWord.put("name", W.getName());
        zaWord.put("Stemed", W.getStemed());


        Doc Page=W.getDocument();

        int val=Page.getTF();
        BasicDBObject Document = new BasicDBObject();
        Document.put("ID",Page.getID());
        Document.put("TF",val);
        Document.put("Positions", Page.getPositions());
        Document.put("TitleCount", Page.GetTitleCount());
        Document.put("HeaderCount", Page.GetHeaderCount());

        zaWord.put("Count",val);

        ArrayList<DBObject> Temp = new ArrayList<DBObject>();
        Temp.add(Document);

        zaWord.put("WebPages", Temp);

        return zaWord;
    }

}

