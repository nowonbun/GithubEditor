package compile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import common.LocalPaths;
import common.PropertyMap;
import common.Util;
import model.Attachment;

public class FileManager extends AbstractManager {
	private String httpServer;
	private String attachPath;

	public FileManager() {
		super();
		this.httpServer = PropertyMap.getInstance().getProperty("config", "httpServer");
	}

	public void initGitDirectory() {
		File file = new File(httpServer);
		for (File f : file.listFiles()) {
			if (f.getAbsolutePath().indexOf(".git") != -1) {
				continue;
			}
			deleteFiles(f);
		}
		File attachPath = new File(httpServer + File.separator + "contents");
		if (attachPath.exists()) {
			deleteFiles(attachPath);
		}
		attachPath.mkdir();
		this.attachPath = attachPath.getAbsolutePath();
	}

	private void deleteFiles(File file) {
		deleteFiles(file.getAbsolutePath());
	}

	private void deleteFiles(String path) {
		File file = new File(path);

		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				if (f.getAbsolutePath().indexOf(".git") != -1) {
					continue;
				}
				deleteFiles(f.getAbsolutePath());
			}
			file.delete();
		}
		if (file.isFile()) {
			if (file.getAbsolutePath().indexOf(".git") != -1) {
				return;
			}
			file.delete();
		}
	}

	public void copyDirectoryToGitRoot(String dirName) {
		List<File> files = getFiles(LocalPaths.getWebRootPath() + File.separator + dirName);
		File newDir = new File(httpServer + File.separator + dirName);
		if (newDir.exists()) {
			deleteFiles(newDir);
		}
		newDir.mkdir();
		files.forEach(f -> {
			try {
				copyFile(f, newDir.getAbsolutePath() + File.separator + f.getName());
			} catch (Throwable e) {
				getLogger().error(e);
			}
		});
	}

	private List<File> getFiles(String path) {
		return getFiles(path, null);
	}

	private List<File> getFiles(String path, List<File> list) {
		if (list == null) {
			list = new ArrayList<>();
		}
		File file = new File(path);

		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				getFiles(f.getAbsolutePath(), list);
			}
		}
		if (file.isFile()) {
			list.add(file);
		}
		return list;
	}

	private void createFile(String path, byte[] data) {
		try (FileOutputStream stream = new FileOutputStream(path)) {
			stream.write(data, 0, data.length);
		} catch (Throwable e) {
			getLogger().error(e);
			throw new RuntimeException(e);
		}
	}

	public void createFile(String filename, String data) {
		createFile(httpServer + File.separator + filename, data.getBytes(StandardCharsets.UTF_8));
	}

	public void createAttachfiles(int postIdx, List<Attachment> attachlist) {
		File postAttach = new File(this.attachPath + File.separator + postIdx);
		if (postAttach.exists()) {
			deleteFiles(postAttach);
		}
		postAttach.mkdir();
		for (Attachment attach : attachlist) {
			try {
				createFile(
						postAttach.getAbsoluteFile() + File.separator + attach.getIdx() + "_"
								+ URLEncoder.encode(attach.getFilename(), StandardCharsets.UTF_8.toString()),
						attach.getData());
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void copyToHttpRoot(String gitRoot, String groupName, String filepermission, String dirpermission) {

		deleteFiles(gitRoot);
		File http = new File(gitRoot);
		//http.mkdir();

		try {
			// The group own will be changed.
			GroupPrincipal group = FileSystems.getDefault().getUserPrincipalLookupService()
					.lookupPrincipalByGroupName(groupName);
			Files.getFileAttributeView(http.toPath(), PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS)
					.setGroup(group);
		} catch (Throwable e) {
			getLogger().error(e);
		}

		copyDirectory(httpServer, gitRoot, filepermission, dirpermission, true, false);
	}

	private void copyDirectory(String src, String dest, String filepermission, String dirpermission, boolean git,
			boolean isSetPermission) {
		File source = new File(src);
		File destination = new File(dest);
		if (source.isDirectory()) {
			if (!destination.exists()) {
				destination.mkdir();
			}
			if (isSetPermission) {
				try {
					Files.getFileAttributeView(destination.toPath(), PosixFileAttributeView.class,
							LinkOption.NOFOLLOW_LINKS).setPermissions(PosixFilePermissions.fromString(dirpermission));
				} catch (Throwable e) {
					getLogger().error(destination.getAbsolutePath(), e);
				}
			}
			File[] files = source.listFiles();
			for (File file : files) {
				if (git && file.getAbsolutePath().indexOf(".git") != -1) {
					continue;
				}
				copyDirectory(src + File.separator + file.getName(), dest + File.separator + file.getName(),
						filepermission, dirpermission, false, true);
			}
		}
		if (source.isFile()) {
			try {
				Util.copyFile(src, dest);
			} catch (Throwable e) {
				getLogger().error(e);
			}
			if (isSetPermission) {
				try {
					Files.getFileAttributeView(destination.toPath(), PosixFileAttributeView.class,
							LinkOption.NOFOLLOW_LINKS).setPermissions(PosixFilePermissions.fromString(filepermission));
				} catch (Throwable e) {
					getLogger().error(destination.getAbsolutePath(), e);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void copyFile(File src, File dest) throws FileNotFoundException, IOException {
		Util.copyFile(src.getAbsolutePath(), dest.getAbsolutePath());
	}

	private void copyFile(File src, String dest) throws FileNotFoundException, IOException {
		Util.copyFile(src.getAbsolutePath(), dest);
	}
}
