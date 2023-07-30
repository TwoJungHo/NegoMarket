import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:http/http.dart' as http;
import 'package:provider/provider.dart';
import 'package:used_market/data_types/constants_data.dart';
import 'package:used_market/view_models/geolocation_view_model.dart';

class SignUp extends StatefulWidget {
  const SignUp({Key? key}) : super(key: key);

  @override
  State<SignUp> createState() => _SignUpState();
}

class _SignUpState extends State<SignUp> {
  final _imagePicker = ImagePicker();
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  final TextEditingController _usernameController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final TextEditingController _password2Controller = TextEditingController();
  final TextEditingController _nameController = TextEditingController();

  double _longitude = 0.0;
  double _latitude = 0.0;

  Future<void> _pickUserPic(int kind, UserPicProvider userPicProvider) async {
    userPicProvider.picFile = kind == 1
        ? await _imagePicker.pickImage(source: ImageSource.gallery)
        : await _imagePicker.pickImage(source: ImageSource.camera);
  }

  Future<void> signUp(UserPicProvider userPicProvider) async {
    var multipartRequest = http.MultipartRequest(
        'POST', Uri.parse('${Constants.api_gate_url}/user-service/users'));

    if (userPicProvider.picFile == null) {
      return null;
    }

    multipartRequest.fields['longitude'] = _longitude.toString();
    multipartRequest.fields['latitude'] = _latitude.toString();
    multipartRequest.files.add(await http.MultipartFile.fromPath(
        'picFile', userPicProvider.picFile!.path));
    multipartRequest.fields['username'] = _usernameController.text;
    multipartRequest.fields['password'] = _passwordController.text;
    multipartRequest.fields['name'] = _nameController.text;

    final http.StreamedResponse streamedResponse =
        await multipartRequest.send();

    final http.Response response =
        await http.Response.fromStream(streamedResponse);

    if (response.statusCode == 201) {
      print('회원가입 성공');
      Map<String, dynamic> responseBody =
          jsonDecode(utf8.decode(response.bodyBytes));
      print('Response body: $responseBody');
      Navigator.pop(context);
    }
  }

  @override
  Widget build(BuildContext context) {
    _longitude = Provider.of<GeolocationViewModel>(context).longitude;
    _latitude = Provider.of<GeolocationViewModel>(context).latitude;
    return ChangeNotifierProvider(
        create: (context) => UserPicProvider(),
        child: Consumer<UserPicProvider>(
            builder: (context, userPicProvider, child) {
          return Scaffold(
            appBar: PreferredSize(
              preferredSize: Size.fromHeight(30.0),
              child: AppBar(
                leading: Image.asset('assets/images/img_appbar.png'),
                backgroundColor: Color.fromRGBO(190, 10, 10, 1),
                elevation: 0.0,
                centerTitle: true,
              ),
            ),
            body: Center(
              child: SingleChildScrollView(
                child: Column(
                  children: [
                    Text("회원가입"),
                    Column(
                      children: [
                        GestureDetector(
                          onTap: () {
                            showModalBottomSheet<void>(
                              context: context,
                              builder: (BuildContext context) {
                                return Container(
                                  height: 200,
                                  color: Colors.white,
                                  child: Center(
                                    child: Column(
                                      mainAxisAlignment: MainAxisAlignment.center,
                                      mainAxisSize: MainAxisSize.min,
                                      children: <Widget>[
                                        ListTile(
                                          leading: Icon(Icons.photo),
                                          title: Text('Gallery'),
                                          onTap: () async {
                                            _pickUserPic(1, userPicProvider);
                                            Navigator.pop(
                                                context); // 선택 후 바닥 시트를 닫습니다.
                                          },
                                        ),
                                        ListTile(
                                          leading: Icon(Icons.camera),
                                          title: Text('Camera'),
                                          onTap: () async {
                                            _pickUserPic(2, userPicProvider);
                                            Navigator.pop(
                                                context); // 선택 후 바닥 시트를 닫습니다.
                                          },
                                        ),
                                      ],
                                    ),
                                  ),
                                );
                              },
                            );
                          },
                          child: Container(
                            decoration: BoxDecoration(
                              border: Border.all(
                                color: Color.fromRGBO(190, 10, 10, 0.8),
                                width: 1,
                              ),
                              borderRadius: BorderRadius.circular(20),
                            ),
                            padding: EdgeInsets.all(5),
                            margin: EdgeInsets.all(5),
                            width: MediaQuery.of(context).size.width * 0.8,
                            height: MediaQuery.of(context).size.width * 0.8,
                            child: userPicProvider._picFile == null
                                ? Center(child: Text('탭하여 프로필 사진 설정'))
                                : Image.file(
                                    File(userPicProvider._picFile!.path)),
                          ),
                        ),
                        Form(
                          key: _formKey,
                          child: Container(
                            width: MediaQuery.of(context).size.width * 0.7,
                            child: Column(
                              children: [
                                TextFormField(
                                  controller: _usernameController,
                                  decoration: InputDecoration(labelText: '아이디'),
                                  validator: (String? value) {
                                    if (value == null || value.isEmpty) {
                                      return '아이디를 입력하세요!';
                                    }
                                  },
                                ),
                                TextFormField(
                                  controller: _passwordController,
                                  decoration: InputDecoration(labelText: '비밀번호'),
                                  validator: (String? value) {
                                    if (value == null || value.isEmpty) {
                                      return '비밀번호를 입력하세요';
                                    }
                                  },
                                ),
                                TextFormField(
                                  controller: _password2Controller,
                                  decoration:
                                      InputDecoration(labelText: '비밀번호 확인'),
                                  validator: (String? value) {
                                    if (value == null || value.isEmpty) {
                                      return '비밀번호 확인이 필요합니다';
                                    }
                                    if (value != _passwordController.text) {
                                      return '비밀번호가 일치 하지 않습니다';
                                    }
                                  },
                                ),
                                TextFormField(
                                  controller: _nameController,
                                  decoration: InputDecoration(labelText: '이름'),
                                  validator: (String? value) {
                                    if (value == null || value.isEmpty) {
                                      return '이름을 입력하세요!';
                                    }
                                  },
                                ),
                              ],
                            ),
                          ),
                        ),
                        ElevatedButton(onPressed: () {
                          if (_formKey.currentState!.validate()) {
                            signUp(userPicProvider);
                          }

                        }, child: Text("회원 가입"))
                      ],
                    ),
                  ],
                ),
              ),
            ),
          );
        }));
  }
}

class UserPicProvider with ChangeNotifier {
  XFile? _picFile;

  XFile? get picFile => _picFile;

  set picFile(XFile? value) {
    _picFile = value;
    notifyListeners();
  }
}
