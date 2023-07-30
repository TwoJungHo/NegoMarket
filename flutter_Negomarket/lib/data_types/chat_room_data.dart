import 'dart:convert';

List<ChatRoom> chatRoomFromJson(String str) => List<ChatRoom>.from(json.decode(str).map((x) => ChatRoom.fromJson(x)));

String chatRoomToJson(List<ChatRoom> data) => json.encode(List<dynamic>.from(data.map((x) => x.toJson())));

class ChatRoom {
  String requestSubject;
  String title;
  String username1;
  String username2;

  ChatRoom({
    required this.requestSubject,
    required this.title,
    required this.username1,
    required this.username2,
  });

  factory ChatRoom.fromJson(Map<String, dynamic> json) => ChatRoom(
    requestSubject: json["requestSubject"],
    title: json["title"],
    username1: json["username1"],
    username2: json["username2"],
  );

  Map<String, dynamic> toJson() => {
    "requestSubject": requestSubject,
    "title": title,
    "username1": username1,
    "username2": username2,
  };
}
