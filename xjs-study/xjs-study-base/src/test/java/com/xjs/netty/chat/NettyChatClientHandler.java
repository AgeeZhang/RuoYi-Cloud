package com.xjs.netty.chat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 聊天室处理类
 * @author xiejs
 * @since 2022-03-09
 */
public class NettyChatClientHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 通道读取就绪事件
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg);
    }


}
