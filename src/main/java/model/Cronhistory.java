package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Entity
@NamedQuery(name="Cronhistory.findAll", query="SELECT c FROM Cronhistory c")
public class Cronhistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idx;

	@Temporal(TemporalType.TIMESTAMP)
	private Date processedDate;

	private int state;

	@OneToMany(mappedBy="cronhistory")
	private List<Post> posts;

	public Cronhistory() {
	}

	public int getIdx() {
		return this.idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public Date getProcessedDate() {
		return this.processedDate;
	}

	public void setProcessedDate(Date processedDate) {
		this.processedDate = processedDate;
	}

	public int getState() {
		return this.state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public List<Post> getPosts() {
		return this.posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	public Post addPost(Post post) {
		getPosts().add(post);
		post.setCronhistory(this);

		return post;
	}

	public Post removePost(Post post) {
		getPosts().remove(post);
		post.setCronhistory(null);

		return post;
	}
}