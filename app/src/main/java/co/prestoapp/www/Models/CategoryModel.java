package co.prestoapp.www.Models;

public class CategoryModel {

    private String bannerUrl;
    private String name;
    private int id;

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CategoryModel(String bannerUrl, String name,int id){
        this.bannerUrl=bannerUrl;
        this.name=name;
        this.id=id;

    }
}
