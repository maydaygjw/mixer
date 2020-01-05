package org.gejunwen.mixer.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static org.gejunwen.mixer.utils.DebugUtils.p;

public class EchoClient {

    private Selector selector;
    private int serverPort = 9000;


    public static void main(String[] args) throws IOException {

        new EchoClient().initClient();
    }

    private void initClient() throws IOException {

        this.selector = Selector.open();
        SocketChannel clientChannel = SocketChannel.open();
        clientChannel.configureBlocking(false);
        clientChannel.connect(new InetSocketAddress(this.serverPort));
        clientChannel.register(selector, SelectionKey.OP_CONNECT);

        for(;;) {
            this.selector.select();
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while(it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                if(key.isConnectable()) {
                    doConnect(key);
                } else if(key.isReadable()) {
                    doRead(key);
                }
            }
        }

    }

    private void doRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int read = channel.read(byteBuffer);
        if(read > 0) {
            p("Received from server: " + new String(byteBuffer.array()).trim());
        }
    }

    private void doConnect(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        clientChannel.register(selector, SelectionKey.OP_READ);
        p("Connected to server:" + clientChannel);
        clientChannel.finishConnect();
        clientChannel.configureBlocking(false);
        clientChannel.write(ByteBuffer.wrap("hello".getBytes()));
//        clientChannel.close();
    }
}
