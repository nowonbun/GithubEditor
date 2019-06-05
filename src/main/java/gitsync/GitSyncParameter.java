package gitsync;

public class GitSyncParameter {
	private boolean isStart;
	private final StringBuilder message;

	public GitSyncParameter() {
		message = new StringBuilder();
	}

	public String getMessage() {
		return this.message.toString();
	}

	public void addMessage(String message) {
		this.message.append(message);
	}

	public void clearMessage() {
		this.message.setLength(0);
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

}
