[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](LICENSE)

介绍
-------------

Dolemgo 是一款服务于核能努基特的网络中转服务端

* 使用Java编写, 更快更轻便捷
* 支持自习开放插件

运行
-------------
输入 `java -jar dolemgo.jar`.

数据包通信
-------------
Dolemgo 有一套完整的通信系统，基于Http，并使用RSA+AES加密传输数据包
保证通信安全。该服务端适用于小游戏服务器动态房间状态获取，服务器云api开发等。

Nukkit 配套使用
------------
请构建或编译 DolemgoAPI

数据包简述
------------
Dolemgo 数据包使用二进制加密传输，该服务器端只能接受数据包
并处理后返回结果(Http)，不能主动发出数据包。
内容大致如下:
包名|密钥随机校验码|byte[]

数据包清单
------------
HandShakePacket 握手数据包
ServerAuthPacket 子服务器连接数据包
ServerClosePacket 子服务器关闭数据包
