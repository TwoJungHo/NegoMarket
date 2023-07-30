import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'dart:convert';

import 'package:provider/provider.dart';
import 'package:used_market/data_types/constants_data.dart';
import 'package:used_market/view_models/login_view_model.dart';
import 'package:used_market/views/signup.dart';

class LoginPage extends StatefulWidget {
  @override
  _LoginPageState createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  final TextEditingController _usernameController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final storage = FlutterSecureStorage();

  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(
        children: [
          TextFormField(
            controller: _usernameController,
            decoration: const InputDecoration(labelText: 'ID'),
            validator: (String? value) {
              if (value == null || value.isEmpty) {
                return 'ID를 입력하세요';
              }
              return null;
            },
          ),
          TextFormField(
            controller: _passwordController,
            decoration: const InputDecoration(labelText: 'Password'),
            obscureText: true,
            validator: (String? value) {
              if (value == null || value.isEmpty) {
                return '비밀 번호를 입력하세요';
              }
              return null;
            },
            onFieldSubmitted: (value) {
              if (_formKey.currentState!.validate()) {
                _login(_usernameController.text, _passwordController.text,
                    context);
              }
            },
          ),
          Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
            ElevatedButton(
              onPressed: () {
                if (_formKey.currentState!.validate()) {
                  _login(_usernameController.text, _passwordController.text,
                      context);
                }
              },
              child: Text('로그인'),
            ),
            ElevatedButton(
              onPressed: () {
                Navigator.push(
                    context, MaterialPageRoute(builder: (context) => SignUp()));
              },
              child: Text('회원 가입'),
            ),
          ])
        ],
      ),
    );
  }

  void _login(String username, String password, BuildContext context) async {
    try {
      var response = await http.post(
        Uri.parse('${Constants.api_gate_url}/user-service/login'),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode({'username': username, 'password': password}),
      );

      if (response.statusCode == 200) {
        Map<String, dynamic> responseBody = jsonDecode(response.body);
        Map<String, dynamic> result = responseBody['result'];
        String jwt = result['token'];

        await storage.write(key: 'jwt', value: jwt);
        await storage.write(key: 'username', value: username);

        Provider.of<LoginModel>(context, listen: false).validateJwt();
      } else {
        print('Request failed with status: ${response.statusCode}.');
      }
    } catch (e) {
      print('Error occurred: $e');
    }
  }
}
