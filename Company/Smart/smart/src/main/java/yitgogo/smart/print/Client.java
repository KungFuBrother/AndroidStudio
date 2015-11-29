package yitgogo.smart.print;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author Tiger
 * 
 *         基于Netty的Socket客户端，与服务器通信，接受浏览记录和订单消息
 */
public class Client {

	EventLoopGroup workerGroup = new NioEventLoopGroup();
	ChannelFuture channelFuture;
	ChannelInboundHandlerAdapter handler;

	String hostAddress = "";
	int hostPort = 0;

	public Client(String hostAddress, int hostPort,
			ChannelInboundHandlerAdapter handler) {
		this.hostAddress = hostAddress;
		this.hostPort = hostPort;
		this.handler = handler;
	}

	public void stop() {
		channelFuture.cancel(true);
		workerGroup.shutdownGracefully();
	}

	/**
	 * 建立连接
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(workerGroup).channel(NioSocketChannel.class);
			bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			bootstrap.handler(new ChannelInitializer<SocketChannel>() {
				public void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast("encode", new StringEncoder());
					pipeline.addLast("decode", new StringDecoder());
					pipeline.addLast(handler);
				}
			});
			channelFuture = bootstrap.connect(hostAddress, hostPort);
			channelFuture.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
		}
	}

}
