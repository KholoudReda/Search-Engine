
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author dina
 */
public class recrawler {

    public recrawler(){}
    public int rec(String url) {
        URL nUrl = null;
        String host;
        try {
            nUrl = new URL(url);
            host = nUrl.getHost().toLowerCase();

        } catch (MalformedURLException e) {
            return 0;
        }
        //   URL nUrl = new URL(url);
        int n = 0;
        if (host.contains("watchcartoononline")||host.contains("cartoonsons")||host.contains("dreamytricks")||host.contains("popcornflix")||host.contains("dreamytricks")||host.contains("classiccinemaonline")||host.contains("retrovision")||host.contains("dvdtalk")||host.contains("moviemistakes")||host.contains("mkyong")||host.contains("alistapart")||host.contains("codecademy")||host.contains("tutsplus")||host.contains("developers.google")||host.contains("scotch")||host.contains("programmingpraxis")||host.contains("xkcd")||host.contains("arstechnica")||host.contains("ocw.mit.edu")||host.contains("linkedin")||host.contains("gobyexample")||host.contains("apple"))
            n = 1440;
        else if(host.contains("artfire")||host.contains("funnyordie")||host.contains("cheezburger")||host.contains("awkwardfamilyphotos")||host.contains("madeitmyself")||host.contains("folksy")||host.contains("maxcdn")||host.contains("firstsiteguide")||host.contains("sourceforge")||host.contains("bitbucket")||host.contains("sitepoint")||host.contains("coursera")||host.contains("blog.codinghorror")||host.contains("usersnap")||host.contains("usersnap"))
            n = 720 ;
        else if(host.contains("hellacocktail")||host.contains("maltby")||host.contains("rubyviolet")||host.contains("food52")||host.contains("steenbergs")||host.contains("mexgrocer.co.uk")||host.contains("housebrand")||host.contains("slideshare")||host.contains("github")||host.contains("soundcloud")||host.contains("sayidaty")||host.contains("goodreads")||host.contains("nasa")||host.contains("outlook.live.com")||host.contains("wikipedia"))
            n = 360 ;
        else
            n = 30;
        return n;
    }
}

