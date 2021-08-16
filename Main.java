import org.json.simple.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;


public class Main {

    public static void main(String[] args) {

        try {
            PrintWriter writer=new PrintWriter(new FileWriter("output.txt"));
            String organizationUrl = "https://api.github.com/orgs/apache";
            GithubAPI githubAPI = new GithubAPI();

            JSONObject organizationJSON = githubAPI.getOrganization(organizationUrl);
            JSONArray reposJSON=githubAPI.getRepos(organizationJSON);

            // This arrayList stores the repos which will be used in this app.
            ArrayList<JSONObject> selectedRepos=new ArrayList<JSONObject>();


            // Add 5 repos to selectedRepos ArrayList.
            for(int i=0;i<5;i++){
                selectedRepos.add((JSONObject) reposJSON.get(i));
            }

                // Do the related processes for each repo
                for(JSONObject repo:selectedRepos){
                    // Waiting 1 minute because the rate limit of Github search API is 20 per minute.
                    Thread.sleep(60000);
                    JSONArray commitsJSON = githubAPI.getCommits(repo);
                    HashMap<String, Integer> userCommitCounts = githubAPI.getUserCommitCounts(commitsJSON);
                    ArrayList<User> users = githubAPI.getUsers(userCommitCounts);

                    // Print the result for each repo to the "output.txt" file.
                    for (User user : users) {
                        writer.println("repo: " + (String) repo.get("name") + ", " + user);

                    }


                }

            // Close the writer
            writer.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }








    }
}