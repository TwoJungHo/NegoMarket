import 'dart:convert';

List<Sell> sellFromJson(String str) => List<Sell>.from(json.decode(str).map((x) => Sell.fromJson(x)));

String sellToJson(List<Sell> data) => json.encode(List<dynamic>.from(data.map((x) => x.toJson())));

class Sell {
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
  SellState sellState;
  bool reviewed;

  Sell({
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

  factory Sell.fromJson(Map<String, dynamic> json) => Sell(
    id: json["id"],
    username: json["username"],
    buyer: json["buyer"],
    productName: json["productName"],
    price: json["price"],
    createAt: DateTime.parse(json["createAt"]),
    updateAt: DateTime.parse(json["updateAt"]),
    finishAt: json["finishAt"] == null ? null : DateTime.parse(json["finishAt"]),
    longitude: json["longitude"]?.toDouble(),
    latitude: json["latitude"]?.toDouble(),
    sellState: sellStateValues.map[json["sellState"]]!,
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
    "finishAt": finishAt?.toIso8601String(),
    "longitude": longitude,
    "latitude": latitude,
    "sellState": sellStateValues.reverse[sellState],
    "reviewed": reviewed,
  };
}

enum SellState { ON_SALE, RESERVED, SOLD_OUT }

final sellStateValues = EnumValues({
  "ON_SALE": SellState.ON_SALE,
  "RESERVED": SellState.RESERVED,
  "SOLD_OUT": SellState.SOLD_OUT
});

class EnumValues<T> {
  Map<String, T> map;
  late Map<T, String> reverseMap;

  EnumValues(this.map);

  Map<T, String> get reverse {
    reverseMap = map.map((k, v) => MapEntry(v, k));
    return reverseMap;
  }
}
