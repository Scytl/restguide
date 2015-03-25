package com.scytl.rest.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("/p12")
public class P12Resource {

	@Inject
	TemporaryFolder temporaryFolder;

	@POST
	@Path("/chunkedUpload")
	@Consumes("*/*")
	@Produces(MediaType.APPLICATION_JSON)
	public Response chunked(InputStream reader) throws IOException {
		UUID uploadId = UUID.randomUUID();

		java.nio.file.Path file = temporaryFolder.newFile(uploadId.toString());
		long offset = IOUtil.copyToFile(file, reader, Integer.MAX_VALUE);
		return Response
				.status(202)
				.entity("{\n" + "  \"uploadId\": \"" + uploadId + "\",\n"
						+ "  \"offset\": " + offset + "\n" + "}").build();
	}

	@PUT
	@Path("/chunkedUpload")
	@Consumes("*/*")
	@Produces(MediaType.APPLICATION_JSON)
	public Response chunked(InputStream reader, @QueryParam("uploadId") String uploadId, @QueryParam("offset") Long offset) throws IOException {
		File file = new File(temporaryFolder.getRoot(), uploadId);
		
		if(isUploadIdValid(file)) {
			if(isOffsetValid(file, offset)) {
				long newOffset = IOUtil.copyToFile(file.toPath(), reader, Integer.MAX_VALUE);
				return Response.status(202).entity("{\n" + 
						"  \"uploadId\": \""+uploadId+"\",\n" + 
						"  \"offset\": " + (offset + newOffset) + "\n" + 
						"}").build();
			} else {
				return Response.status(400).build();
			}
		} else {
			return Response.noContent().build();
		}
	}

	private boolean isOffsetValid(File file, long offset) {
		return file.length() == offset;
	}

	private boolean isUploadIdValid(File file) {
		return file.exists();
	}
	
	@POST
	@Path("/commitChunkedUpload")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response commit(@QueryParam("uploadId") String uploadId, String content) {
		File file = new File(temporaryFolder.getRoot(), uploadId);
		
		if(isUploadIdValid(file)) {
			JsonReader parser = Json.createReader(new StringReader(content));
			JsonObject readObject = parser.readObject();
			file.renameTo(new File(temporaryFolder.getRoot(), readObject.getString("filename")));
			return Response.created(URI.create("/"+uploadId)).build();
		} else {
			return Response.noContent().build();
		}
	}
}
