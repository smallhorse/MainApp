package com.ubtechinc.bluetoothrobot.handler


import com.google.protobuf.BoolValue
import com.google.protobuf.Int32Value
import com.ubtech.utilcode.utils.LogUtils
import com.ubtechinc.bluetoothrobot.CodeMaoService
import com.ubtechinc.bluetoothrobot.Robot2PhoneMsgMgr
import com.ubtechinc.bluetoothrobot.Statics
import com.ubtechinc.codemao.CodeMaoObserveFallClimb
import com.ubtechinc.codemao.CodeMaoObserveHeadRacket
import com.ubtechinc.protocollibrary.communite.IMsgHandler
import com.ubtechinc.protocollibrary.communite.ProtoBufferDispose
import com.ubtechinc.protocollibrary.protocol.MiniMessage
import com.ubtrobot.master.param.ProtoParam
import com.ubtrobot.transport.message.CallException
import com.ubtrobot.transport.message.Request
import com.ubtrobot.transport.message.Response
import com.ubtrobot.transport.message.StickyResponseCallback

/**
 * @Deseription 监听机器人姿态
 * @Author tanghongyu
 * @Time 2018/3/14 20:43
 */

class ObserverRobotPostureHandler : IMsgHandler {
    internal var responseCmdId: Short = 0
    override fun handleMsg(requestCmdId: Short, responseCmdId: Short, request: MiniMessage, peer: Object) {
        this.responseCmdId = responseCmdId
        val observeFallClimbRequest = ProtoBufferDispose.unPackData(
                CodeMaoObserveFallClimb.ObserveFallClimbRequest::class.java, request.dataContent) as CodeMaoObserveFallClimb.ObserveFallClimbRequest
        LogUtils.d("getIsSubscribe = " + observeFallClimbRequest.isSubscribe)
        val observeFallClimbResponse = CodeMaoObserveFallClimb.ObserveFallClimbResponse.newBuilder()
        CodeMaoService.instance.observerRobotPosture( ProtoParam.create(BoolValue.newBuilder().setValue(observeFallClimbRequest.isSubscribe).build()),object : StickyResponseCallback {
            override fun onFailure(p0: Request?, p1: CallException?) {
            }

            override fun onResponseCompletely(p0: Request?, response: Response?) {

            }

            override fun onResponseStickily(p0: Request?, response: Response?) {
                try {
                    var status = ProtoParam.from(response!!.param, Int32Value::class.java).protoMessage
                    Robot2PhoneMsgMgr.get().sendResponseData(responseCmdId, Statics.PROTO_VERSION, 0, observeFallClimbResponse.setStatus(status.value).build() , peer)
                } catch (e: ProtoParam.InvalidProtoParamException) {
                    Robot2PhoneMsgMgr.get().sendResponseData(responseCmdId, Statics.PROTO_VERSION, 0, CodeMaoObserveFallClimb.ObserveFallClimbResponse.newBuilder().build(), peer)
                    e.printStackTrace()
                }
            }

        })


    }


}
