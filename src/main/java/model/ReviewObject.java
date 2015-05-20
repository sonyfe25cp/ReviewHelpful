package model;

import java.util.List;

/**
 * Created by OmarTech on 15-5-6.
 */
public class ReviewObject {
    private int id;
    private String companyName;
    private List<Review> originReviews;


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<Review> getOriginReviews() {
        return originReviews;
    }

    public void setOriginReviews(List<Review> originReviews) {
        this.originReviews = originReviews;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ReviewObject{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", originReviews=" + originReviews +
                '}';
    }
}
