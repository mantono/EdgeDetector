package diacheck.android.libs.net;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;

public class ImageSender
{
	public final static String SERVER_URL = "http://81.225.210.252/";
	
	private File image;
	private final URL url;
	private HttpURLConnection connection;
	
	public ImageSender(File image) throws IOException
	{
		this.image = image;
		this.url = new URL(SERVER_URL);
		this.connection = (HttpURLConnection) url.openConnection();
	}
	
	public boolean sendFile() throws IOException
	{
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setChunkedStreamingMode(0);
		OutputStream streamOut = new BufferedOutputStream(connection.getOutputStream());
		Files.copy(image.toPath(), streamOut);
		streamOut.flush();
		return false;
	}
}
