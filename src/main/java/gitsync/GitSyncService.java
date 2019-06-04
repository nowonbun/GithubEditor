package gitsync;

public class GitSyncService {
	private static GitSyncService instance = null;
	public static GitSyncService getInstance() {
		if(instance == null) {
			instance = new GitSyncService();
		}
		return instance;
	}
	private GitSyncService() {
		
	}
	
	public void start() {
		//https://hojak99.tistory.com/338
		//https://www.mkyong.com/java/how-to-execute-shell-command-from-java/
	}
}
