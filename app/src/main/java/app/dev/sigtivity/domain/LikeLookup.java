package app.dev.sigtivity.domain;

/**
 * Created by Ravi on 11/21/2015.
 */
public class LikeLookup {
    private boolean liked;
    private int likes;

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
