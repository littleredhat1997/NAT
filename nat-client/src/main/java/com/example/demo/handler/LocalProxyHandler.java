package com.example.demo.handler;

import io.netty.channel.ChannelHandlerContext;
import com.example.demo.protocol.Message;
import com.example.demo.protocol.MessageType;

import java.util.HashMap;

/**
 * Created by wucao on 2019/3/2.
 */
public class LocalProxyHandler extends MessageHandler {

    private MessageHandler proxyHandler;
    private String remoteChannelId;

    public LocalProxyHandler(MessageHandler proxyHandler, String remoteChannelId) {
        this.proxyHandler = proxyHandler;
        this.remoteChannelId = remoteChannelId;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] data = (byte[]) msg;
        Message message = new Message();
        message.setType(MessageType.DATA);
        message.setData(data);
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("channelId", remoteChannelId);
        message.setMetaData(metaData);
        proxyHandler.getCtx().writeAndFlush(message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Message message = new Message();
        message.setType(MessageType.DISCONNECTED);
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("channelId", remoteChannelId);
        message.setMetaData(metaData);
        proxyHandler.getCtx().writeAndFlush(message);
    }
}