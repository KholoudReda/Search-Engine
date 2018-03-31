import shaded.com.mongodb.*;

import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Vector;

//import com.mongodb.*;
//import shaded.com.mongodb.MongoClient;


public class DBConnection {

    private MongoClient mongoClient= null;
    private DB database;
    private DBCollection Words_col;

    private DB db;
    private Mongo mongo = null;
    private DBCollection Doc_col;
    private final Vector<String> URLs =new Vector<>();


    public DBConnection(){
/*
        try{
            MongoClient mongoClient = new MongoClient(new ServerAddress("localhost",27017),
                    MongoClientOptions.builder()
                            .socketTimeout(1000)   //if it doesn't open it will throw exception
                            .build());

            database = mongoClient.getDB("Search_engine");
            Words_col = database.getCollection("Word");
            Doc_col=database.getCollection("Documents");

        }
        catch(MongoTimeoutException e){
            System.out.println(e);
        }
*/


       try {
            mongo = new Mongo("localhost", 27017);
            db = mongo.getDB("Search_engine");
            Words_col = db.getCollection("Word");
            Doc_col=db.getCollection("Documents");

       }
        catch (UnknownHostException e1) {
            e1.printStackTrace();
       }


    }

    //-----------------------------------------------------------------------------------------------------

    public void print_word_data(){
        DBCursor cursor=Words_col.find();

        try{
            while(cursor.hasNext()){
                System.out.println(cursor.next());
            }
        }
        finally {
            cursor.close();
        }


    }

    //............................................................
    public void print_Doc(){
        DBCursor cursor=Doc_col.find();

        try{
            while(cursor.hasNext()){
                System.out.println(cursor.next());
            }
        }
        finally {
            cursor.close();
        }


    }

    //............................................................................
    public void PrintAllWords(){

        DBCursor cursor = Words_col.find();
        try {
            while(cursor.hasNext()) {
                System.out.println(cursor.next());
            }
        } finally {
            cursor.close();
        }
    }

    //..............................................................
    //returns true if this word exists in the database!
    public Boolean Word_Exists(String n){

        DBCursor cursor = Words_col.find(new BasicDBObject("name", n));

        return cursor.hasNext();
    }

    //.............................................................
    public void ChangeToFalse(){

        DBObject elemMatch = new BasicDBObject("Stemed","travel");

        BasicDBObject Obj=new BasicDBObject("Stemed","walaa");
        DBObject updateQuery=new BasicDBObject("$set",Obj);

        Words_col.update(elemMatch, updateQuery);

    }

    //.............................................................
    //Insert a group of new words together
    public void Add_New_Words(ArrayList<DBObject>New_Words) {
        //save it into your database:
        Words_col.insert(New_Words);
    }
    //.............................................................

    //Add one new word to the database!

    public void Add_One_Word(BasicDBObject New_Word) {
        //save it into your database:
        Words_col.insert(New_Word);
    }
    //...................................................................

    public String GetHtml(int DocID){

        BasicDBObject allQuery = new BasicDBObject("ID",DocID);
     //   BasicDBObject fields = new BasicDBObject();
      //  fields.put("htmlDoc", 1);

       // DBCursor cursor = Doc_col.find(allQuery, fields);
        DBCursor cursor = Doc_col.find(allQuery);
if( cursor.hasNext()) {

    DBObject obj = cursor.next();
   // System.out.println("-----------------------------------------------------------------------------------indexeeeer");
    //System.out.println((String) obj.get("htmlDoc"));
    return (String) obj.get("htmlDoc");
}
        System.out.println(".........nothing matched");
return "";

    }



    //................................................................................

    //Given a stemmed word,get all the similar/unstemmed words of it
    public ArrayList<DBObject> Find_Unstemed(String W){
        DBObject Obj=null;
        ArrayList<DBObject> Arr=new ArrayList<DBObject>();

        DBObject elemMatch = new BasicDBObject("Stemed",W);

        DBCursor cursor = Words_col.find(elemMatch);

        try {
            while(cursor.hasNext()){
                Obj= cursor.next();
                Arr.add(Obj);
                System.out.println(Obj.get("name"));
            }
        } finally {
            cursor.close();
        }

        return Arr;
    }

    //..............................................................

    //find all the words which belong to a specific document"Given the Id of this document"
    public void WhichWordsInDoc(int DocID){


        DBObject findWords = new BasicDBObject("WebPages.ID",DocID);
        DBCursor cursor=Words_col.find(findWords);

        try{
            while(cursor.hasNext()){
                System.out.println(cursor.next().get("name"));
            }
        }
        finally {
            cursor.close();
        }
    }

    //........................................................................

    //find all the documents in which a given stemmed word appears
    public void FindDocuments(String S){
        int I;

        BasicDBObject query = new BasicDBObject("Stemed", S);

        //limit the fields to only have the WebPages field
        BasicDBObject fields = new BasicDBObject("WebPages",1).append("_id",false);
        DBCursor curs = Words_col.find(query, fields);

        while(curs.hasNext()) {
            DBObject o = curs.next();

            BasicDBList Pages = (BasicDBList) o.get("WebPages");

            // shows the WebPages array -- this is actually a collection of DBObjects
            //System.out.println(Pages.toString());

            //break it into a native java array
            BasicDBObject[] PagesArr = Pages.toArray(new BasicDBObject[0]);
            for(BasicDBObject dbObj : PagesArr) {

                //System.out.println(dbObj);
                I=(int)dbObj.get("ID");

            }
        }
    }

    //....................................................................................

    //Add a document to the array of web pages belonging to a specific word
    public void AddDocToWord(String W,Doc D){
        int value=D.getTF();
        DBObject elemMatch = new BasicDBObject("name",W);

        DBObject NewWord=new BasicDBObject("WebPages",new BasicDBObject("ID",D.getID()).append("TF",D.getTF()).append("Positions", D.getPositions()).append("TitleCount", D.GetTitleCount()).append("HeaderCount",D.GetHeaderCount()));
        DBObject updateQuery=new BasicDBObject("$push",NewWord);

        BasicDBObject incValue = new BasicDBObject("Count", value);
        BasicDBObject intModifier = new BasicDBObject("$inc", incValue);


        Words_col.update(elemMatch, updateQuery,true,true);
        Words_col.update(elemMatch, intModifier);
    }
    //......................................................................



    //...........................................................................

    //remove a specific document from all words
    public void RemoveDocFromWords(int DocID){

        DBObject findWords = new BasicDBObject("WebPages.ID",DocID);

        BasicDBObject O=new BasicDBObject("ID",DocID);
        BasicDBObject Obj=new BasicDBObject("WebPages",O);

        DBObject updateQuery=new BasicDBObject("$pull",Obj);

        // Words_col.update(findWords, updateQuery);


        DBCursor curs = Words_col.find(findWords);
        try {
            while(curs.hasNext()){
                DBObject O1= curs.next();
                Words_col.update(O1, updateQuery);
            }
        } finally {
            curs.close();
        }
    }


    public Boolean insert_crawler(int count, MyUrl myUrl, int num , int timerec/*, List<String> parents*/) {
     //  System.out.println("myUrl.getHtml()!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! "+count+" "+myUrl.getHtml());
        BasicDBObject doc = new BasicDBObject();
        doc.put("ID", count);
        doc.put("url", myUrl.getUrl());
        doc.put("num of pages I point to", myUrl.getPointTo());
        doc.put("htmlDoc", myUrl.getHtml());
//    doc.put("parent", myUrl.getParent());
        doc.put("num of parents",1);
        doc.put("parents",myUrl.getParent());
        doc.put("RecrawelTime",num);
        doc.put("LastRec",timerec);
        BasicDBObject o1 = new BasicDBObject();
        o1.put("url",myUrl.getUrl());

        DBCursor cursor =Doc_col.find(o1);

        if(cursor.hasNext()){
            BasicDBObject found=(BasicDBObject)cursor.next();
            BasicDBObject u1=new BasicDBObject();
            BasicDBObject u2=new BasicDBObject();
            BasicDBObject u3=new BasicDBObject();
            u3.put("parents",myUrl.getParent());
            u2.put("$addToSet",u3);
            Doc_col.update(o1,u2);
            return false;
        }


        else {
            System.out.println("adding! "+myUrl.getUrl()+ " from thread " + Thread.currentThread().getId());
            Doc_col.insert(doc);
            return true;
        }
    }
    public int Find_Time_Rec(String W){
        DBObject Obj=null;
        ArrayList<DBObject> Arr=new ArrayList<DBObject>();
        DBObject elemMatch = new BasicDBObject("url",W);

        DBCursor cursor = Doc_col.find(elemMatch);

        try {
            while(cursor.hasNext()){
                Obj= cursor.next();
                Arr.add(Obj);
                //    System.out.println(Obj.get("name"));
            }
        } finally {
            cursor.close();
        }

        return (int)Obj.get("RecrawelTime");
    }
    public LocalTime Find_Last_Rec(String W){
        DBObject Obj=null;
        ArrayList<DBObject> Arr=new ArrayList<DBObject>();
        DBObject elemMatch = new BasicDBObject("url",W);

        DBCursor cursor = Doc_col.find(elemMatch);

        try {
            while(cursor.hasNext()){
                Obj= cursor.next();
                Arr.add(Obj);
                //    System.out.println(Obj.get("name"));
            }
        } finally {
            cursor.close();
        }

        return (LocalTime)Obj.get("LastRec");
    }

    public void AddDoc_Time(String W,String D  ){
        //int value=D.getTF();
        LocalTime now = LocalTime.now();
        int minute = (now.getHour()*60)+now.getMinute();
        DBObject elemMatch = new BasicDBObject("url",W);

        DBObject NewWord=new BasicDBObject("htmlDoc",D);
        NewWord.put("LastRec",minute);

        // DBObject NewWord=new BasicDBObject("LastRec",l);

        DBObject updateQuery=new BasicDBObject("$set",NewWord);



        Doc_col.update(elemMatch, updateQuery,true,true);

    }

    public Vector<String> allUrls(){

        DBCursor cursor = Doc_col.find();
        try {
            while(cursor.hasNext()) {
                int t  = (int)cursor.next().get("RecrawelTime");
                int min =(int)cursor.next().get("LastRec");
                LocalTime now = LocalTime.now();
                int min2 = (now.getHour()*60)+now.getMinute();
                if ((min2-min) >= t ) // need to recrawl
                {
                    URLs.add((String)cursor.next().get("url"));
                }

                //  u=(String)cursor.next().get("url");
                // System.out.println(cursor.next());
            }
        }
        catch(java.lang.RuntimeException re){System.out.println("RuntimeException!!");}
        finally {
            cursor.close();
        }
        return URLs;
    }

    public void Close(){
        mongo.close();
    }



}