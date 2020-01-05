package org.gejunwen.mixer.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

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

        this.selector.select();
        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
        while(it.hasNext()) {
            SelectionKey key = it.next();
            it.remove();
            if(key.isConnectable()) {
                doConnect(key);
            }
        }
    }

    private void doConnect(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        System.out.println("Connected to server:" + clientChannel);
        clientChannel.finishConnect();
        clientChannel.configureBlocking(false);
        clientChannel.write(ByteBuffer.wrap("hello".getBytes()));
        clientChannel.close();
    }
}
