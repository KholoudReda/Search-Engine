//import javax.swing.text.Document;
import java.util.LinkedList;
import java.util.List;

public class MyUrl {
    private String url;
    private List<String> parent=new LinkedList<String>();
    private int pointsTo;
    private String html;
    MyUrl(){}
    public String getUrl(){return url;}
    public List<String> getParent(){return parent;}
    //public int getPointedBy(){return pointedBy;}
    public int getPointTo(){return pointsTo;}

    public String getHtml() {
        return html;
    }

    public void setUrl(String u){url=u;}

    public void setParent(String parent) {
        this.parent.add(parent);
    }

   /* public void setPointedBy(int pointedBy) {
        this.pointedBy = pointedBy;
    }
*/
    public void setPointTo(int pointTo) {
        this.pointsTo = pointTo;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
