package org.apollo.update;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

import org.apollo.fs.IndexedFileSystem;
import org.apollo.net.codec.jaggrab.JagGrabRequest;
import org.apollo.net.codec.jaggrab.JagGrabResponse;
import org.apollo.update.resource.ResourceProvider;
import org.apollo.update.resource.VirtualResourceProvider;

/**
 * A worker which services JAGGRAB requests.
 * 
 * @author Graham
 */
public final class JagGrabRequestWorker extends RequestWorker<JagGrabRequest, ResourceProvider> {

	/**
	 * Creates the JAGGRAB request worker.
	 * 
	 * @param dispatcher The dispatcher.
	 * @param fs The file system.
	 */
	public JagGrabRequestWorker(UpdateDispatcher dispatcher, IndexedFileSystem fs) {
		super(dispatcher, new VirtualResourceProvider(fs));
	}

	@Override
	protected ChannelRequest<JagGrabRequest> nextRequest(UpdateDispatcher dispatcher) throws InterruptedException {
		return dispatcher.nextJagGrabRequest();
	}

	@Override
	protected void service(ResourceProvider provider, Channel channel, JagGrabRequest request) throws IOException {
		Optional<ByteBuffer> buf = provider.get(request.getFilePath());
		buf.ifPresent(buffer -> channel.writeAndFlush(new JagGrabResponse(Unpooled.wrappedBuffer(buffer))).addListener(ChannelFutureListener.CLOSE));
	}

}