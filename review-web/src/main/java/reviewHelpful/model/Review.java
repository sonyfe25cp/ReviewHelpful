package reviewHelpful.model;

/**
 * Created by OmarTech on 15-5-6.
 */
public class Review {
    private int id;
    private String text;
    private double score;
    private int weight;
    private int recommend_friend;
    private int rating;
    private boolean centroid;//是中心点


    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getRecommend_friend() {
        return recommend_friend;
    }

    public void setRecommend_friend(int recommend_friend) {
        this.recommend_friend = recommend_friend;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isCentroid() {
        return centroid;
    }

    public void setCentroid(boolean centroid) {
        this.centroid = centroid;
    }
}
