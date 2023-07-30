import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;
import 'package:used_market/data_types/constants_data.dart';

class LoginModel extends ChangeNotifier {
  final FlutterSecureStorage _secureStorage = FlutterSecureStorage();
  bool _isJwtValid = false;
  bool _isLoading = false;

  bool get isLoading => _isLoading;

  set isLoading(bool value) {
    _isLoading = value;
  }

  bool get isJwtValid => _isJwtValid;

  Future<void> validateJwt() async {
    _isLoading = true;
    String? jwtToken = await _secureStorage.read(key: 'jwt');

    if (jwtToken == null) {
      _isJwtValid = false;
    } else {
      var response = await http.get(
        Uri.parse('${Constants.api_gate_url}/user-service/tokenValidation'),
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Authorization': 'Bearer $jwtToken',
        },
      );

      if (response.statusCode == 200) {
        var responseData = jsonDecode(response.body);
        _isJwtValid = responseData ?? false;
      } else {
        throw Exception("백엔드 서버 오류");
      }
    }
    _isLoading = false;

    notifyListeners();
  }
}
