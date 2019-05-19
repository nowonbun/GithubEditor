package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@NamedQuery(name = "Attachment.findAll", query = "SELECT a FROM Attachment a")
public class Attachment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idx;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createddate;

	@Lob
	private byte[] data;

	private byte isdeleted;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastupdateddate;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "post_attachemt", joinColumns = { @JoinColumn(name = "attach_idx") }, inverseJoinColumns = { @JoinColumn(name = "post_idx") })
	private List<Post> posts;

	public Attachment() {
	}

	public int getIdx() {
		return this.idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public Date getCreateddate() {
		return this.createddate;
	}

	public void setCreateddate(Date createddate) {
		this.createddate = createddate;
	}

	public byte[] getData() {
		return this.data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte getIsdeleted() {
		return this.isdeleted;
	}

	public void setIsdeleted(byte isdeleted) {
		this.isdeleted = isdeleted;
	}

	public Date getLastupdateddate() {
		return this.lastupdateddate;
	}

	public void setLastupdateddate(Date lastupdateddate) {
		this.lastupdateddate = lastupdateddate;
	}

	public List<Post> getPosts() {
		return this.posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

}