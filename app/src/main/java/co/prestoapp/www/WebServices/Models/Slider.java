package co.prestoapp.www.WebServices.Models;

public class Slider {
    private int id;
    private String image;
    private String type;
    private String destination;
    private int data_id;
    private String data_title;
    private String data_type;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getData_id() {
        return data_id;
    }

    public void setData_id(int data_id) {
        this.data_id = data_id;
    }

    public String getData_title() {
        return data_title;
    }

    public void setData_title(String data_title) {
        this.data_title = data_title;
    }


    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public Slider(String image, String type, String destination, int data_id, String data_title,String data_type) {
        this.image = image;
        this.type = type;
        this.destination = destination;
        this.data_id = data_id;
        this.data_title=data_title;
        this.data_type=data_type;
    }
}
