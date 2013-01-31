package org.ocpteamx.protocol.gdrive;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.ocpteam.component.DSContainer;
import org.ocpteam.interfaces.IAuthenticable;
import org.ocpteam.interfaces.IFile;
import org.ocpteam.interfaces.IFileSystem;
import org.ocpteam.misc.JLG;
import org.ocpteam.misc.LOG;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

public class GDriveFileSystem extends DSContainer<GDriveDataSource> implements
		IFileSystem {

	@Override
	public void checkoutAll(String localDir) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void commitAll(String localDir) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public IFile getFile(String dir) throws Exception {
		LOG.info("getFile with dir = " + dir);
		GDriveClient c = (GDriveClient) ds().getComponent(IAuthenticable.class);
		List<File> result = new ArrayList<File>();
		Files.List request = c.service.files().list();
		String id = getIdFromPath(dir);
		String query = "'" + id + "' in parents AND trashed != true";
		LOG.info(query);
		request.setQ(query);

		do {
			try {
				FileList files = request.execute();

				result.addAll(files.getItems());
				request.setPageToken(files.getNextPageToken());
			} catch (IOException e) {
				System.out.println("An error occurred: " + e);
				request.setPageToken(null);
			}
		} while (request.getPageToken() != null
				&& request.getPageToken().length() > 0);

		GDriveFileImpl fi = new GDriveFileImpl();
		for (File f : result) {
			fi.add(new GDriveFileImpl(f));
		}

		return fi;
	}

	private String getIdFromPath(String dir) throws Exception {
		LOG.info("getIdFromPath with dir = " + dir);
		if (dir.equals("/") || dir.equals("")) {
			return getRootId();
		}
		String name = JLG.basename(dir);
		String parentId = getIdFromPath(JLG.dirname(dir));
		return getId(name, parentId);
	}

	private String getId(String name, String parentId) throws Exception {
		GDriveClient c = (GDriveClient) ds().getComponent(IAuthenticable.class);
		List<File> result = new ArrayList<File>();
		Files.List request = c.service.files().list();
		request.setQ("'" + parentId + "' in parents AND title = '" + name
				+ "'AND trashed != true");

		FileList files = request.execute();

		result.addAll(files.getItems());
		if (result.isEmpty()) {
			throw new Exception("Google Drive path does not exist.");
		}

		File f = result.get(0);
		return f.getId();
	}

	private String getRootId() throws Exception {
		GDriveClient c = (GDriveClient) ds().getComponent(IAuthenticable.class);
		List<File> result = new ArrayList<File>();
		Files.List request = c.service.files().list();
		request.setQ("'root' in parents AND trashed != true");

		FileList files = request.execute();

		result.addAll(files.getItems());
		if (result.isEmpty()) {
			return null;
		}

		File f = result.get(0);
		return f.getParents().get(0).getId();
	}

	@Override
	public void mkdir(String existingParentDir, String newDir) throws Exception {
		String id = getIdFromPath(existingParentDir);
		File body = new File();
		List<ParentReference> l = new ArrayList<ParentReference>();
		ParentReference pr = new ParentReference();
		pr.setId(id);
		l.add(pr);
		body.setParents(l);
		body.setTitle(newDir);
		body.setDescription("Uploaded with GDSE");
		body.setMimeType("application/vnd.google-apps.folder");
		GDriveClient c = (GDriveClient) ds().getComponent(IAuthenticable.class);
		File f = c.service.files().insert(body).execute();
		LOG.info("f.id = " + f.getId());

	}

	@Override
	public void rm(String existingParentDir, String name) throws Exception {
		GDriveClient c = (GDriveClient) ds().getComponent(IAuthenticable.class);
		String id = getIdFromPath(existingParentDir + "/" + name);
		c.service.files().delete(id).execute();

	}

	@Override
	public void rename(String existingParentDir, String oldName, String newName)
			throws Exception {
		String id = getIdFromPath(existingParentDir + "/" + oldName);
		GDriveClient c = (GDriveClient) ds().getComponent(IAuthenticable.class);
		// First retrieve the file from the API.
		File file = c.service.files().get(id).execute();

		// File's new metadata.
		file.setTitle(newName);

		// Send the request to the API.
		File f = c.service.files().update(id, file).execute();
		LOG.info("f.Id = " + f.getId());
	}

	@Override
	public String getDefaultLocalDir() {
		return System.getProperty("user.home");
	}

	@Override
	public void checkout(String remoteDir, String remoteFilename,
			java.io.File localDir) throws Exception {
		if (!remoteDir.endsWith("/")) {
			remoteDir += "/";
		}
		String id = getIdFromPath(remoteDir + remoteFilename);
		GDriveClient c = (GDriveClient) ds().getComponent(IAuthenticable.class);

		// Send the request to the API.
		File f = c.service.files().get(id).execute();
		if (f == null) {
			throw new Exception("The file does not exist.");
		}
		LOG.info("f.id = " + f.getId());
		String url = f.getDownloadUrl();
		if (url == null || url.equals("")) {
			throw new Exception("This file is Google specific and cannot be downloaded.");
		}
		HttpResponse resp = c.service.getRequestFactory()
				.buildGetRequest(new GenericUrl(url)).execute();
		InputStream is = resp.getContent();
		FileOutputStream fos = new FileOutputStream(localDir.getAbsolutePath()
				+ "/" + remoteFilename, false);
		try {
			byte[] a = new byte[1024];
			int l = 0;
			while ((l = is.read(a)) != -1) {
				fos.write(a, 0, l);
			}
		} finally {
			is.close();
			fos.close();
		}

		LOG.info("Getting file with id = " + f.getId());
	}

	@Override
	public void commit(String remoteDir, java.io.File file) throws Exception {
		String id = getIdFromPath(remoteDir);
		if (file.isDirectory()) {
			mkdir(remoteDir, file.getName());
			for (java.io.File child : file.listFiles()) {
				LOG.info("child: " + child.getName());
				commit(remoteDir + "/" + file.getName(), child);
			}
		} else {
			File body = new File();
			List<ParentReference> l = new ArrayList<ParentReference>();
			ParentReference pr = new ParentReference();
			pr.setId(id);
			l.add(pr);
			body.setParents(l);
			body.setTitle(file.getName());
			body.setDescription("Uploaded with GDSE");
			body.setMimeType("text/plain");
			FileContent mediaContent = new FileContent("text/plain", file);

			GDriveClient c = (GDriveClient) ds().getComponent(
					IAuthenticable.class);
			File f = c.service.files().insert(body, mediaContent).execute();
			LOG.info("f.id = " + f.getId());
		}
	}

}
