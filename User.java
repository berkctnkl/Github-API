
// This class is created to store user information. Getter, setter and toString() methods are implemented.
public class User {

    private String userName;
    private String location;
    private String company;
    private Integer contributions;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Integer getContributions() {
        return contributions;
    }

    public void setContributions(Integer contributions) {
        this.contributions = contributions;
    }


    @Override
    public String toString() {
        return "user: "+userName+", "+"location: "+location+", "+"company: "+company+", "+"contributions: "+contributions;
    }
}
