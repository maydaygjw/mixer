package org.gejunwen.mixer.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

import static org.gejunwen.mixer.utils.DebugUtils.p;

public class EchoServer {

    private Selector selector;
    private volatile boolean isTerminated = false;


    private EchoServer() throws IOException {
        selector = Selector.open();
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.socket().bind(new InetSocketAddress(9000));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void run() {
        while (!isTerminated) {
            try {
                selector.select();
            } catch (IOException e) {
                closeSelector();
            }
            Set keys = selector.selectedKeys();
            Iterator it = keys.iterator();
            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                it.remove();
                try {
                    if (key.isAcceptable()) {
                        doAccept(key);
                    } else if (key.isReadable()) {
                        doRead(key);
                    }
                }catch(IOException e) {
                    e.printStackTrace();
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException ignored) {

                    }
                }
            }
        }
        closeSelector();
    }

    private void closeSelector() {
        try {
            this.selector.close();
        }catch (IOException e) {
            p("Error occur when close connection");
        }
    }

    private void doRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        if (clientChannel.read(byteBuffer) > 0) {
            byteBuffer.flip();
            byte[] data = byteBuffer.array();
            String info = new String(data).trim();
            p("message from client：" + info);
            doWrite(clientChannel, byteBuffer);
            byteBuffer.clear();
        } else {
            key.cancel();
            clientChannel.close();
        }
    }

    private void doWrite(SocketChannel channel, ByteBuffer byteBuffer) throws IOException {
        channel.write(byteBuffer);
    }

    private void doAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverSocketChannel.accept();
        channel.configureBlocking(false);
        channel.register(this.selector, SelectionKey.OP_READ);
        p("Accept connection from server");

    }

    public static void main(String[] args) throws IOException {
        new EchoServer().run();
    }
}
