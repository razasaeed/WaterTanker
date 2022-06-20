package ex.next.watertanker.models;

public class Users {

    private String key;
    private String fName;
    private String lName;
    private String email;
    private String phone;
    private String cnic;
    private String password;
    private String type;
    private String latitude;
    private String longitude;
    private String fullTankerPrice;
    private String halfTankerPrice;
    private String address;
    private String userStatus;

    public Users() {
    }

    public Users(String key, String fName, String lName, String email, String phone, String cnic, String password, String type, String latitude, String longitude, String fullTankerPrice, String halfTankerPrice, String address, String userStatus) {
        this.key = key;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.phone = phone;
        this.cnic = cnic;
        this.password = password;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fullTankerPrice = fullTankerPrice;
        this.halfTankerPrice = halfTankerPrice;
        this.address = address;
        this.userStatus = userStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getFullTankerPrice() {
        return fullTankerPrice;
    }

    public void setFullTankerPrice(String fullTankerPrice) {
        this.fullTankerPrice = fullTankerPrice;
    }

    public String getHalfTankerPrice() {
        return halfTankerPrice;
    }

    public void setHalfTankerPrice(String halfTankerPrice) {
        this.halfTankerPrice = halfTankerPrice;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}