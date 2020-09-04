package daut.mazlami.pratki.model;

import java.util.ArrayList;

public class PostLocationResponse {
    ArrayList<PostLocation> postLocations;

    public PostLocationResponse(ArrayList<PostLocation> postLocations) {
        this.postLocations = postLocations;
    }

    public PostLocationResponse(){}

    public ArrayList<PostLocation> getPostLocations() {
        return postLocations;
    }

    public void setPostLocations(ArrayList<PostLocation> postLocations) {
        this.postLocations = postLocations;
    }
}
