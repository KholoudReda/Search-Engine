
public class Word {

    private int id;
    private String name;
    private String stemed;
    private int IDF;


    private static int Wcount=1;

    private Doc Document=null;


    public Word(String N,String S,int ID,int Pos){

        id=++Wcount;
        Document=new Doc(ID,Pos);
        name=N;
        stemed=S;
        IDF=0;

    }

    public void Inc_TitleCount(){
        Document.TitleCount();
    }

    public void Inc_HeaderCount(){
        Document.HeaderIncrement();
    }


    public int getId() {
        return id;
    }



    public void setId(int id) {
        this.id = id;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStemed() {

        return stemed;
    }

    public void setStemed(String stemed) {
        this.stemed = stemed;
    }

    public Doc getDocument() {
        return Document;
    }

    public void setDocument(Doc document) {
        Document = document;
    }


    public void Addposition(int DocID,int pos){

        Document.Add_postion(pos);

    }

}