package ex.next.watertanker.models;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.databinding.Bindable;

import ex.next.watertanker.Constants;

public class Orders {

    private String key;
    private String fName;
    private String lName;
    private String email;
    private String phone;
    private String cnic;
    private String type;
    private String latitude;
    private String longitude;
    private String price;
    private String address;
    private String status;
    private String supplier_name;
    private String supplier_phone;
    private String supplier_email;
    private String supplier_address;
    private String supplier_latitude;
    private String supplier_longitude;
    private String order_time;
    private String delivery_time;

    public Orders() {
    }

    public Orders(String key, String fName, String lName, String email, String phone, String cnic, String type, String latitude, String longitude,
                  String price, String address, String status, String supplier_name, String supplier_phone, String supplier_email, String supplier_address,
                  String supplier_latitude, String supplier_longitude, String order_time, String delivery_time) {
        this.key = key;
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.phone = phone;
        this.cnic = cnic;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
        this.address = address;
        this.status = status;
        this.supplier_name = supplier_name;
        this.supplier_phone = supplier_phone;
        this.supplier_email = supplier_email;
        this.supplier_address = supplier_address;
        this.supplier_latitude = supplier_latitude;
        this.supplier_longitude = supplier_longitude;
        this.order_time = order_time;
        this.delivery_time = delivery_time;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSupplier_name() {
        return supplier_name;
    }

    public void setSupplier_name(String supplier_name) {
        this.supplier_name = supplier_name;
    }

    public String getSupplier_phone() {
        return supplier_phone;
    }

    public void setSupplier_phone(String supplier_phone) {
        this.supplier_phone = supplier_phone;
    }

    public String getSupplier_email() {
        return supplier_email;
    }

    public void setSupplier_email(String supplier_email) {
        this.supplier_email = supplier_email;
    }

    public String getSupplier_address() {
        return supplier_address;
    }

    public void setSupplier_address(String supplier_address) {
        this.supplier_address = supplier_address;
    }

    public String getSupplier_latitude() {
        return supplier_latitude;
    }

    public void setSupplier_latitude(String supplier_latitude) {
        this.supplier_latitude = supplier_latitude;
    }

    public String getSupplier_longitude() {
        return supplier_longitude;
    }

    public void setSupplier_longitude(String supplier_longitude) {
        this.supplier_longitude = supplier_longitude;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public String getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(String delivery_time) {
        this.delivery_time = delivery_time;
    }
}