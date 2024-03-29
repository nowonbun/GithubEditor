package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@NamedQuery(name="Post.findAll", query="SELECT p FROM Post p")
public class Post implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idx;

	@Lob
	private String contents;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createddate;

	private boolean isdeleted;

	private boolean isreservation;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastupdateddate;

	private String tag;

	private String title;

	@OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Attachment> attachments;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinTable(name="cronpost", joinColumns = { @JoinColumn(name = "post_idx") }, inverseJoinColumns = { @JoinColumn(name = "cronhistory_idx") })
	private Cronhistory cronhistory;
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Category category;

	public Post() {
	}

	public int getIdx() {
		return this.idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public String getContents() {
		return this.contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public Date getCreateddate() {
		return this.createddate;
	}

	public void setCreateddate(Date createddate) {
		this.createddate = createddate;
	}

	public boolean getIsdeleted() {
		return this.isdeleted;
	}

	public void setIsdeleted(boolean isdeleted) {
		this.isdeleted = isdeleted;
	}

	public boolean getIsreservation() {
		return this.isreservation;
	}

	public void setIsreservation(boolean isreservation) {
		this.isreservation = isreservation;
	}

	public Date getLastupdateddate() {
		return this.lastupdateddate;
	}

	public void setLastupdateddate(Date lastupdateddate) {
		this.lastupdateddate = lastupdateddate;
	}

	public String getTag() {
		return this.tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Attachment> getAttachments() {
		return this.attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public Attachment addAttachment(Attachment attachment) {
		getAttachments().add(attachment);
		attachment.setPost(this);

		return attachment;
	}

	public Attachment removeAttachment(Attachment attachment) {
		getAttachments().remove(attachment);
		attachment.setPost(null);

		return attachment;
	}

	public Cronhistory getCronhistory() {
		return this.cronhistory;
	}

	public void setCronhistory(Cronhistory cronhistory) {
		this.cronhistory = cronhistory;
	}

	public Category getCategory() {
		return this.category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

}