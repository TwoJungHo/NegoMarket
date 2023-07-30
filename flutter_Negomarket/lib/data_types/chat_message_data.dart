import 'dart:convert';

List<ChatMessage> chatMessageFromJson(String str) => List<ChatMessage>.from(json.decode(str).map((x) => ChatMessage.fromJson(x)));

String chatMessageToJson(List<ChatMessage> data) => json.encode(List<dynamic>.from(data.map((x) => x.toJson())));

class ChatMessage {
  int? id;
  String? roomTitle;
  DateTime? sendAt;
  String? sender;
  String receiver;
  String message;
  bool read;

  ChatMessage({
    this.id,
    this.roomTitle,
    this.sendAt,
    this.sender,
    required this.receiver,
    required this.message,
    required this.read,
  });

  factory ChatMessage.fromJson(Map<String, dynamic> json) => ChatMessage(
    id: json["id"],
    roomTitle: json["roomTitle"],
    sendAt: DateTime.parse(json["sendAt"]),
    sender: json["sender"],
    receiver: json["receiver"],
    message: json["message"],
    read: json["read"],
  );

  Map<String, dynamic> toJson() => {
    "id": id,
    "roomTitle": roomTitle,
    "sendAt": sendAt?.toIso8601String(),
    "sender": sender,
    "receiver": receiver,
    "message": message,
    "read": read,
  };


}

class ChatMessageReadState {
  int id;
  bool read;

  ChatMessageReadState({
    required this.id,
    required this.read,
  });

  factory ChatMessageReadState.fromJson(Map<String, dynamic> json) => ChatMessageReadState(
    id: json["id"],
    read: json["read"],
  );

  Map<String, dynamic> toJson() => {
    "id": id,
    "read": read,
  };
}