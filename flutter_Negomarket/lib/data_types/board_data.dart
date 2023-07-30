// To parse this JSON data, do
//
//     final board = boardFromJson(jsonString);

import 'dart:convert';

BoardResponse boardResponseFromJson(String str) => BoardResponse.fromJson(json.decode(str));
String boardResponseToJson(BoardResponse data) => json.encode(data.toJson());

class BoardResponse {
  int id;
  String sellId;
  String username;
  String title;
  String deltaString;
  String htmlString;
  SellInfoResponse sellInfoResponse;

  BoardResponse({
    required this.id,
    required this.sellId,
    required this.username,
    required this.title,
    required this.deltaString,
    required this.htmlString,
    required this.sellInfoResponse,
  });

  factory BoardResponse.fromJson(Map<String, dynamic> json) => BoardResponse(
    id: json["id"],
    sellId: json["sellId"],
    username: json["username"],
    title: json["title"],
    deltaString: json["deltaString"],
    htmlString: json["htmlString"],
    sellInfoResponse: SellInfoResponse.fromJson(json["sellInfoResponse"]),
  );

  Map<String, dynamic> toJson() => {
    "id": id,
    "sellId": sellId,
    "username": username,
    "title": title,
    "deltaString": deltaString,
    "htmlString": htmlString,
    "sellInfoResponse": sellInfoResponse.toJson(),
  };
}



class SellInfoResponse {
  String id;
  String username;
  String? buyer;
  String productName;
  int price;
  DateTime createAt;
  DateTime updateAt;
  DateTime? finishAt;
  double longitude;
  double latitude;
  String sellState;
  bool reviewed;

  SellInfoResponse({
    required this.id,
    required this.username,
    this.buyer,
    required this.productName,
    required this.price,
    required this.createAt,
    required this.updateAt,
    this.finishAt,
    required this.longitude,
    required this.latitude,
    required this.sellState,
    required this.reviewed,
  });

  factory SellInfoResponse.fromJson(Map<String, dynamic> json) => SellInfoResponse(
    id: json["id"],
    username: json["username"],
    buyer: json["buyer"],
    productName: json["productName"],
    price: json["price"],
    createAt: DateTime.parse(json["createAt"]),
    updateAt: DateTime.parse(json["updateAt"]),
    finishAt: json["finishAt"],
    longitude: json["longitude"]?.toDouble(),
    latitude: json["latitude"]?.toDouble(),
    sellState: json["sellState"],
    reviewed: json["reviewed"],
  );

  Map<String, dynamic> toJson() => {
    "id": id,
    "username": username,
    "buyer": buyer,
    "productName": productName,
    "price": price,
    "createAt": createAt.toIso8601String(),
    "updateAt": updateAt.toIso8601String(),
    "finishAt": finishAt,
    "longitude": longitude,
    "latitude": latitude,
    "sellState": sellState,
    "reviewed": reviewed,
  };
}

class BoardRequest {
  String? id;
  String? sellId;
  String title;
  String deltaString;
  String htmlString;
  String? buyer;
  String productName;
  int price;
  double longitude;
  double latitude;

  BoardRequest({
    this.id,
    this.sellId,
    required this.title,
    required this.deltaString,
    required this.htmlString,
    this.buyer,
    required this.productName,
    required this.price,
    required this.longitude,
    required this.latitude,
  });



  Map<String, dynamic> toJson() => {
    "id": id,
    "sellId": sellId,
    "title": title,
    "deltaString": deltaString,
    "htmlString": htmlString,
    "buyer": buyer,
    "productName": productName,
    "price": price,
    "longitude": longitude,
    "latitude": latitude,
  };
}