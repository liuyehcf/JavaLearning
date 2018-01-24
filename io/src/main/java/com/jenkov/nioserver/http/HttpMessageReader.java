package com.jenkov.nioserver.http;

import com.jenkov.nioserver.IMessageReader;
import com.jenkov.nioserver.Message;
import com.jenkov.nioserver.MessageBuffer;
import com.jenkov.nioserver.Socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjenkov on 18-10-2015.
 */
public class HttpMessageReader implements IMessageReader {

    private MessageBuffer messageBuffer = null;

    private List<Message> completeMessages = new ArrayList<Message>();
    private Message nextMessage = null;

    public HttpMessageReader() {
    }

    @Override
    public void init(MessageBuffer readMessageBuffer) {
        this.messageBuffer = readMessageBuffer;
        this.nextMessage = messageBuffer.getMessage();
        this.nextMessage.metaData = new HttpHeaders();
    }

    @Override
    public void read(Socket socket, ByteBuffer byteBuffer) throws IOException {
        int bytesRead = socket.read(byteBuffer);
        byteBuffer.flip();

        if (byteBuffer.remaining() == 0) {
            byteBuffer.clear();
            return;
        }

        this.nextMessage.writeToMessage(byteBuffer);

        //这个地方就是与协议相关的，用于检测一个Message是否完整
        int endIndex = HttpUtil.parseHttpRequest(
                this.nextMessage.sharedArray,
                this.nextMessage.offset,
                this.nextMessage.offset + this.nextMessage.length,
                (HttpHeaders) this.nextMessage.metaData);

        //如果endIndex为-1则说明当前messageBuffer中的message是个partial message
        if (endIndex != -1) {
            //创建一个新的Message
            Message message = this.messageBuffer.getMessage();
            message.metaData = new HttpHeaders();

            //由于nextMessage中包含了前一个full message，可能还包含了下一个message的开始部分，因此要将开始部分拷贝到下一个message中
            message.writePartialMessageToMessage(nextMessage, endIndex);

            completeMessages.add(nextMessage);
            nextMessage = message;
        }
        byteBuffer.clear();
    }


    @Override
    public List<Message> getMessages() {
        return this.completeMessages;
    }

}
