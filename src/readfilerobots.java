
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class readfilerobots {
    private HashMap disallowListCache = new HashMap();
    String fileName = null;

    public readfilerobots(){}

    // Verify URL format.
    public URL verifyUrl(String url) {
        if (!url.toLowerCase().startsWith("https://")&&!url.toLowerCase().startsWith("http://")){
            System.out.println("Noo "+url);
            return null;

        }

        // Verify format of URL.
        URL verifiedUrl = null;
        try {
            verifiedUrl = new URL(url);
            //   System.out.println("yeessss");

        } catch (MalformedURLException e) {
            return null;
        }

        return verifiedUrl;
    }


    // Check if robot is allowed to access the given URL.
    public boolean isRobotAllowed(URL urlToCheck) {
        String host = urlToCheck.getHost().toLowerCase();

        // Retrieve host's disallow list from cache.
        ArrayList disallowList =
                (ArrayList) disallowListCache.get(host);

        // If list is not in the cache, download and cache it.
        if (disallowList == null) {
            disallowList = new ArrayList();

            try {
                URL robotsFileUrl =
                        new URL("https://"+ host + "/robots.txt");

                // Open connection to robot file URL for reading.
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(
                                robotsFileUrl.openStream()));

                // Read robot file, creating list of disallowed paths.
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.indexOf("Disallow:") == 0) {
                        String disallowPath =
                                line.substring("Disallow:".length());

                        // Check disallow path for comments and 
                        // remove if present.
                        int commentIndex = disallowPath.indexOf("#");
                        if (commentIndex != - 1) {
                            disallowPath =
                                    disallowPath.substring(0, commentIndex);
                        }

                        // Remove leading or trailing spaces from 
                        // disallow path.
                        disallowPath = disallowPath.trim();

                        // Add disallow path to list.
                        disallowList.add(disallowPath);
                    }
                }

                // Add new disallow list to cache.
                disallowListCache.put(host, disallowList);
            } catch (Exception e) {
        /* Assume robot is allowed since an exception
           is thrown if the robot file doesn't exist. */
                if (host.contains("www.facebook.com") ||host.contains("www.youtube.com") ||host.contains("www.twitter.com")){
                    System.out.println("not allowed host --->  "+host);

                    return false ;
                }
                else
                    return true;
            }
        }
         
    /* Loop through disallow list to see if the
       crawling is allowed for the given URL. */
        String file = urlToCheck.getFile();
        for (int i = 0; i < disallowList.size(); i++) {
            String disallow = (String) disallowList.get(i);
            if (file.startsWith(disallow)) {
                return false;
            }
        }

        if (host.contains("www.facebook.com") ||host.contains("www.youtube.com") ||host.contains("www.twitter.com")){
            System.out.println("not allowed host --->  "+host);

            return false ;
        }

        return true;
    }
}