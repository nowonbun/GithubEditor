package compile;

public class CompileParameter {
	private CompileStatus status;
	private String message;
	private int progress;

	public CompileStatus getCompileStatus() {
		return status;
	}

	public void setCompileStatus(CompileStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getCompileStatusToInt() {
		return status.ordinal();
	}

}
