import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class GithubAPI {
    // This method is used to sort users by their commit counts in descending order and it returns the sorted hashMap
    private HashMap<String,Integer> sortMapByValue(HashMap<String,Integer> map){
        LinkedList<Map.Entry<String, Integer>> linkedList = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());
       linkedList.sort(new Comparator<Map.Entry<String, Integer>>() {

           @Override
           public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
               return (o2.getValue()).compareTo(o1.getValue());
           }
       });

        HashMap<String,Integer> sortedMap=new LinkedHashMap<String,Integer>();

        for(Map.Entry<String,Integer> entry:linkedList){
            sortedMap.put(entry.getKey(),entry.getValue());
        }

        return sortedMap;

    }


    // This method is used to parse string to a JSONObject and it returns the result
    private JSONObject parseJSONObject(String jsonString){
        int length=jsonString.length();
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<length;i++){
            char jsonStringChar=jsonString.charAt(i);

            // This if statement is used to check the char is " or not and replace it with "\"" to parse string correctly
            if(jsonStringChar=='"'){
                stringBuilder.append("\"");
            }

            else{
                stringBuilder.append(jsonStringChar);
            }

        }
        // Parser is used to parse the string
        JSONParser jsonParser=new JSONParser();

        // This JSONObject is used to store the JSONObject  which is returned from parser.parse() method
        JSONObject jsonObject=new JSONObject();

        try {
            jsonObject=(JSONObject) jsonParser.parse(stringBuilder.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }


    // This method is used to parse strings to JSONArray and it returns the the result as JSONArray
    private JSONArray parseJSONArray(String jsonString){
        int length=jsonString.length();
        StringBuilder stringBuilder=new StringBuilder();

        for(int i=0;i<length;i++){
            char jsonStringChar=jsonString.charAt(i);

            // This if statement is used to check the char is " or not and replace it with "\"" to parse string correctly
            if(jsonStringChar=='"'){
                stringBuilder.append("\"");
            }

            else{
                stringBuilder.append(jsonStringChar);
            }

        }

        // Parser is used to parse the string
        JSONParser jsonParser=new JSONParser();
        // This JSONArray is used to store the JSONArray  which is returned from parser.parse() method
        JSONArray jsonArray=new JSONArray();
        try {
            jsonArray=(JSONArray) jsonParser.parse(stringBuilder.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    // This method is used to get  Organization Information in JSON format from the API whose URL is given.
    public JSONObject getOrganization(String organizationUrl){

        StringBuilder stringBuilder=new StringBuilder();
        String line;

        // In this try block a get request is sent to API to get organization information.
        try {

            URL url=new URL(organizationUrl);

            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while((line=in.readLine())!=null){
                stringBuilder.append(line);
            }



        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        String jsonString=stringBuilder.toString();
        return parseJSONObject(jsonString);


    }


    /* This method is used to get repos of a given organization (as a JSONObject) in JSON format.
      It returns the repos as JSONArray              */
    public JSONArray getRepos(JSONObject organization) {
        // In GithubAPI maximum result on a page is  100 and the per_page variable represents this value.
        String reposUrl = (((String) organization.get("repos_url")) + "?per_page=100&page=");
        String line;

        JSONArray reposJSON = new JSONArray();

        int pageNumber = 1;

        try {

            URL url = new URL(reposUrl + pageNumber);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // The page number represents the current page number and its maximum value is limited as 6 not to exceed the rate limit.
            while(((line=in.readLine())!=null && pageNumber<=6)) {

                pageNumber++;

                url = new URL(reposUrl + pageNumber);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                // Parse each pages to JSONArray
                JSONArray jsonArray = parseJSONArray(line);
                int lengthJSONArray = jsonArray.size();

                // Add each repo in a to JSONArray(reposJSON)
                for (int i = 0; i < lengthJSONArray; i++) {
                    reposJSON.add((JSONObject) (jsonArray.get(i)));
                }
            }

            } catch(MalformedURLException e){
                e.printStackTrace();


            } catch(IOException e){
                e.printStackTrace();
            }




        return reposJSON;

    }

    /* This method is used to get commits of a given repo (as a JSONObject) in JSON format.
     It returns the commits as JSONArray                                                                                         */
    public JSONArray getCommits(JSONObject repo){

        String repoName=(String) repo.get("name"); // Get the name of repo
        JSONObject owner=(JSONObject)  repo.get("owner"); // Get the owner of the repo as JSONObject
        String ownerName=(String)  owner.get("login"); // Get the username of the owner

        // The url that can be used to get commits of the repo
        String commitsUrl="https://api.github.com/repos/"+ownerName+"/"+repoName+"/commits?per_page=100&page=";
        String line;

        // The JSON object stores the commits as JSONArray
        JSONArray commitsJSON=new JSONArray();
        int pageNumber=1;

        // Sendig GET request to the API to get the commits
        try {
            URL url=new URL(commitsUrl+pageNumber);
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream()));


            // The maximum value of page number is limeted as 6  not to exceed the rate limit.
            while(((line=in.readLine())!=null && pageNumber<=6)) {

                pageNumber++;

                url = new URL(commitsUrl + pageNumber);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String jsonString = line;
                // Parsing the commits on the page as JSONArray.
                JSONArray jsonArray = parseJSONArray(jsonString);
                int lengthJSONArray = jsonArray.size();

                // Adding each commit to commitsJSON
                for (int i = 0; i < lengthJSONArray; i++) {
                    commitsJSON.add((JSONObject) (jsonArray.get(i)));
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();


        } catch (IOException e) {
            e.printStackTrace();
        }




        return commitsJSON;

    }


    // This method gets commit counts by name, sorts the hash map object value and returns it.
    public HashMap<String,Integer> getUserCommitCounts(JSONArray commits){

        HashMap <String,Integer> commitCountsByUser=new HashMap<String,Integer>();
        int commitsSize=commits.size();

        for(int i=0;i<commitsSize;i++){
            JSONObject commit=(JSONObject) ((JSONObject) commits.get(i)).get("commit");
            JSONObject committer=(JSONObject) commit.get("committer");
            String committerName=((String) committer.get("name"))
                    .replaceAll(" ","") // Delete spaces in the name of the user

                    // Replacing special characters in the name of the user.
                    .replaceAll("é","e")
                    .replaceAll("ö","o")
                    .replaceAll("ü","u")
                    .replaceAll("ä","a");

            // If the commit the first commit of the user set the count as 1 and add the user to the hashmap
            if(!commitCountsByUser.containsKey(committerName)){
                commitCountsByUser.put(committerName,1);
            }

            // If the user has commits before increase the commit count by 1.
            else{
                commitCountsByUser.put(committerName,commitCountsByUser.get(committerName)+1);
            }

        }

        return sortMapByValue(commitCountsByUser);

    }

   // This method search users by name gets users' information and returns these as an ArrayList of users.
    public ArrayList<User> getUsers(HashMap<String,Integer> userCommitCounts){

        // ArrayList to store users
        ArrayList<User> users=new ArrayList<User>();
        // The url for searching users by criteria.
        String url="https://api.github.com/search/users?q=";

        String[] keyArray= userCommitCounts.keySet().toArray(new String[userCommitCounts.size()]);
        String name,line;

        // Get the most committing then users or get all users if number of users less than 10
        for(int i=0;i<10 && i<keyArray.length;i++){
            name=keyArray[i];
            // The object is created to store user.
            User user=new User();

            // Sending get requst to search users by their names.
            try {
                URL searchUrl=new URL(url+"\""+name+"\"");
                HttpURLConnection connection= (HttpURLConnection) searchUrl.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader in=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                line=in.readLine();
                String jsonString=line;

                // Parsing the response as JSONObject.
                JSONObject responseJSON=parseJSONObject(jsonString);

                // If the user could not found  set user's attributes as Not Found.
                if((Long) responseJSON.get("total_count")==0 ){

                    user.setUserName("Not Found");
                    user.setLocation("Not Found");
                    user.setCompany("Not Found");



                }

                // Get user's information from its url.
                else{

                    // Get the users array.
                    JSONArray items= (JSONArray) responseJSON.get("items");
                    // Get the user from array.
                    JSONObject item=(JSONObject) items.get(0);

                    // Sending get request to user's url.
                    URL userUrl=new URL((String) item.get("url"));
                    HttpURLConnection userConnection= (HttpURLConnection) userUrl.openConnection();
                    userConnection.setRequestMethod("GET");
                    BufferedReader userIn=new BufferedReader(new InputStreamReader(userConnection.getInputStream()));

                    line=userIn.readLine();

                    jsonString=line;
                    // Parsing the user to json object from string.
                    JSONObject userJSON=parseJSONObject(jsonString);

                    // Get user's attributes.
                    String userName=(String) userJSON.get("login");
                    String location=(String) userJSON.get("location");
                    String company=(String) userJSON.get("company");


                    // Set user's username
                    user.setUserName(userName);
                    // Firstly set location and company as the string "null"
                    user.setLocation("null");
                    user.setCompany("null");

                    // If the location is not null set the location as user's location.
                   if(location!=null){
                       user.setLocation(location);
                   }

                    // If the company is not null set the company as user's company.
                   if(company!=null){
                       user.setCompany(company);
                   }




                }

                // Set the user's contributiıons as user's commit count.
                user.setContributions(userCommitCounts.get(name));
                // Add the user to users ArrayList
                users.add(user);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return users;
    }

}
