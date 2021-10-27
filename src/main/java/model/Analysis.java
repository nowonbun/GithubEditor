package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the analysis database table.
 * 
 */
@Entity
@Table(name="analysis")
@NamedQuery(name="Analysi.findAll", query="SELECT a FROM Analysis a")
public class Analysis implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int idx;

	private String agent;

	private String browser;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createddate;

	private String referrer;

	private String url;

	public Analysis() {
	}

	public int getIdx() {
		return this.idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public String getAgent() {
		return this.agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getBrowser() {
		return this.browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public Date getCreateddate() {
		return this.createddate;
	}

	public void setCreateddate(Date createddate) {
		this.createddate = createddate;
	}

	public String getReferrer() {
		return this.referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}