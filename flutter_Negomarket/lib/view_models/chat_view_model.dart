import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:http/http.dart' as http;
import 'package:used_market/data_types/chat_message_data.dart';
import 'package:used_market/data_types/chat_room_data.dart';
import 'package:used_market/data_types/constants_data.dart';

class ChatViewModel extends ChangeNotifier {
  final FlutterSecureStorage _secureStorage = FlutterSecureStorage();
  late final username;

  List<ChatRoom> _chatRoomList = [];
  Map<String, List<ChatMessage>> _chatMessageListForRoom = {};
  bool _isLoading = false;


  late StompClient _stompClient;
  
  List<ChatRoom> get chatRoomList => _chatRoomList;
  set chatRoomList(List<ChatRoom> value) {
    _chatRoomList = value;
  }

  Map<String, int> _unreadMessageCount = {};
  Map<String, int> get unreadMessageCount => _unreadMessageCount;
  set unreadMessageCount(Map<String, int> value) {
    _unreadMessageCount = value;
  }

  int _totalUnreadMessageCount = 0;
  int get totalUnreadMessageCount => _totalUnreadMessageCount;
  set totalUnreadMessageCount(int value) {
    _totalUnreadMessageCount = value;
  }



  
  
  Future<void> initializeChatService() async {
    _isLoading = true;
    String? jwtToken = await _secureStorage.read(key: 'jwt');
    username = await _secureStorage.read(key: 'username');
    print(jwtToken);
    print(username);

    final completer = Completer();

    _stompClient = StompClient(
      config: StompConfig(
        url: '${Constants.ws_stomp_url}/chat-service/wsstomp',
        onConnect: (frame) {
          completer.complete();
          print("Connected!!!!");
          print("Connected!!!!");
          print("Connected!!!!");
          print("Connected!!!!");
          print("Connected!!!!");
        },
        onWebSocketError: (dynamic error) {
          print("WebSocket Error: $error");
        },
        onStompError: (frame) {
          print("Server reported an error");
        },
        onDisconnect: (frame) {
          print("Disconnected");
        },
      ),
    );



    _stompClient.activate();
    await completer.future;


    final roomListResponse = await http.get(
      Uri.parse("${Constants.api_gate_url}/chat-service/findrooms"),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $jwtToken',
      },
    );

    if (roomListResponse.bodyBytes.isNotEmpty) {
      _chatRoomList.addAll(
          (jsonDecode(utf8.decode(roomListResponse.bodyBytes)) as List)
              .map((data) => ChatRoom.fromJson(data))
              .toList());

      for (ChatRoom chatRoom in _chatRoomList) {
        var chatMessageListData = await http.get(
          Uri.parse(
              "${Constants.api_gate_url}/chat-service/messagelist/${chatRoom.title}"),
          headers: {
            'Content-Type': 'application/json',
          },
        );
        List<ChatMessage> chatMessageList =
            chatMessageFromJson(utf8.decode(chatMessageListData.bodyBytes));
        _chatMessageListForRoom[chatRoom.title] = chatMessageList;
        _updateUnreadCount(chatRoom.title);
        joinRoom(chatRoom.title);
        notifyListeners();
      }
    }

    _stompClient.subscribe(
        destination: '/sub/chatroomnotify/$username',
        callback: (frame) async {
          _chatRoomList.add(ChatRoom.fromJson(jsonDecode(frame.body!)));
          final newTitle = ChatRoom.fromJson(jsonDecode(frame.body!)).title;

          var chatMessageListData = await http.get(
            Uri.parse(
                "${Constants.api_gate_url}/chat-service/messagelist/${newTitle}"),
            headers: {
              'Content-Type': 'application/json',
            },
          );
          List<ChatMessage> chatMessageList =
              chatMessageFromJson(utf8.decode(chatMessageListData.bodyBytes));
          _chatMessageListForRoom[newTitle] = chatMessageList;
          joinRoom(newTitle);
          notifyListeners();
        });

    _isLoading = false;

    notifyListeners();
  }

  void joinRoom(String roomTitle) {
    _updateUnreadCount(roomTitle);
    _stompClient.subscribe(
        destination: '/sub/chatroom/$roomTitle',
        callback: (frame) {
          _chatMessageListForRoom[roomTitle]!
              .add(ChatMessage.fromJson(jsonDecode(frame.body!)));
          _updateUnreadCount(roomTitle);
          notifyListeners();
        });

    _stompClient.subscribe(
        destination: '/sub/chatroom/read/$roomTitle',
        callback: (frame) {
          _chatMessageListForRoom[roomTitle]!.firstWhere((chatMessage) => chatMessage.id == ChatMessageReadState.fromJson(jsonDecode(frame.body!)).id).read = true;
          _updateUnreadCount(roomTitle);
          notifyListeners();
        });

  }


  void leaveRoom(String roomTitle) {
    // _subscriptions[roomName]?.unsubscribe();
    // _subscriptions.remove(roomName);
    // _messages.remove(roomName); 방을 나가면 해당 방의 메시지 리스트도 제거합니다.
  }

  void sendMessage(ChatMessage chatMessage) async {
    String bearerToken = "Bearer ${(await _secureStorage.read(key: "jwt"))!}";
    String messageString = jsonEncode(chatMessage.toJson());
    _stompClient.send(
        destination: '/pub/sendmessage',
        headers: {"Authorization": bearerToken},
        body: messageString);
  }

  void readMessage(int id) async {
    _stompClient.send(
      destination: '/pub/readmessage',
      body: '${id}'
    );
  }
  
  void _updateUnreadCount(String roomTitle) {
    int unreadCount = _chatMessageListForRoom[roomTitle]!.where((chatMessage) => chatMessage.receiver == username && chatMessage.read == false).length;
    
    _unreadMessageCount[roomTitle] = unreadCount;
    _totalUnreadMessageCount = _unreadMessageCount.values.reduce((total, element) => total + element);
  }

  // @override
  // void dispose() {
  //   _stompClient.deactivate();
  //   super.dispose();
  // }

  Map<String, List<ChatMessage>> get chatMessageListForRoom =>
      _chatMessageListForRoom;

  set chatMessageListForRoom(Map<String, List<ChatMessage>> value) {
    _chatMessageListForRoom = value;
    notifyListeners();
  }

  bool get isLoading => _isLoading;

  set isLoading(bool value) {
    _isLoading = value;
  }


}
