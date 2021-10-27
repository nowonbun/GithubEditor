package bean;

public class AnalysisBean {
  private int idx;
  private String url;
  private String referrer;
  private String browser;
  private String agent;
  private String createddate;

  public int getIdx() {
    return idx;
  }

  public void setIdx(int idx) {
    this.idx = idx;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getReferrer() {
    return referrer;
  }

  public void setReferrer(String referrer) {
    this.referrer = referrer;
  }

  public String getBrowser() {
    return browser;
  }

  public void setBrowser(String browser) {
    this.browser = browser;
  }

  public String getAgent() {
    return agent;
  }

  public void setAgent(String agent) {
    this.agent = agent;
  }

  public String getCreateddate() {
    return createddate;
  }

  public void setCreateddate(String createddate) {
    this.createddate = createddate;
  }

}
