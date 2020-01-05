package org.gejunwen.mixer.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

import static org.gejunwen.mixer.utils.DebugUtils.p;

public class EchoServer {

    Selector selector;
    volatile boolean isTerminated = false;


    public EchoServer() throws IOException {
        selector = Selector.open();
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.socket().bind(new InetSocketAddress(9000));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void run() throws IOException {
        while (!isTerminated) {
            selector.select();
            Set keys = selector.selectedKeys();
            Iterator it = keys.iterator();
            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                it.remove();
                if (key.isAcceptable()) {
                    doAccept(key);
                } else if (key.isReadable()) {
                    doRead(key);
                }
            }

        }
    }

    private void doRead(SelectionKey key) throws IOException {
        p("port: " + key.attachment());
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        if (clientChannel.read(byteBuffer) > 0) {
            byteBuffer.flip();
            byte[] data = byteBuffer.array();
            String info = new String(data).trim();
            p("从客户端发送过来的消息是：" + info);
            byteBuffer.clear();
            doWrite(clientChannel, byteBuffer);
        } else {
            key.cancel();
            clientChannel.close();
        }
    }

    private void doWrite(SocketChannel channel, ByteBuffer byteBuffer) throws IOException {
        channel.write(byteBuffer);
    }

    private void doAccept(SelectionKey key) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        try {
            SocketChannel channel = serverSocketChannel.accept();
            channel.configureBlocking(false);
            channel.register(this.selector, SelectionKey.OP_READ);
            p("Accept connection from server");
        } catch (IOException e) {
            p("Error while accept client connection");
        }

    }

    public static void main(String[] args) throws IOException {
        new EchoServer().run();
    }
}
