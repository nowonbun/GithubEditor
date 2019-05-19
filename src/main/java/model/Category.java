package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

@Entity
@NamedQuery(name = "Category.findAll", query = "SELECT c FROM Category c")
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String code;

	private byte isactive;

	private String name;

	private String uniqcode;

	@OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
	private List<Post> posts;

	public Category() {
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public byte getIsactive() {
		return this.isactive;
	}

	public void setIsactive(byte isactive) {
		this.isactive = isactive;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUniqcode() {
		return this.uniqcode;
	}

	public void setUniqcode(String uniqcode) {
		this.uniqcode = uniqcode;
	}

	public List<Post> getPosts() {
		return this.posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	public Post addPost(Post post) {
		getPosts().add(post);
		post.setCategory(this);

		return post;
	}

	public Post removePost(Post post) {
		getPosts().remove(post);
		post.setCategory(null);

		return post;
	}

}