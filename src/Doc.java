
import java.util.ArrayList;

public class Doc {

    //private static int Pcount = 1;
    private int ID;
    private int TF;

    private ArrayList<Integer> Positions = new ArrayList<Integer>();
    private int TitleCount,HeaderCount;


    public Doc(int ID,int pos){
        this.ID=ID;
        Positions.add(pos);
        TF=1;
        TitleCount=0;
        HeaderCount=0;
    }

    public void TitleCount(){
        TitleCount++;
    }

    public void HeaderIncrement(){
        HeaderCount++;
    }

    public int GetTitleCount(){
        return TitleCount;
    }

    public int GetHeaderCount(){
        return HeaderCount;
    }


    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }


    public int getTF() {
        return TF;
    }


    public void setTF(int tF) {
        TF = tF;
    }


    public ArrayList<Integer> getPositions() {
        return Positions;
    }


    public void setPositions(ArrayList<Integer> positions) {
        Positions = positions;
    }

    public void Add_postion(int pos){

        Positions.add(pos);
        TF++;
    }

}