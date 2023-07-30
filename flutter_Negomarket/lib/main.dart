import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:provider/provider.dart';
import 'package:used_market/home.dart';
import 'package:used_market/view_models/chat_view_model.dart';
import 'package:used_market/view_models/geolocation_view_model.dart';
import 'package:used_market/view_models/login_view_model.dart';
import 'package:used_market/view_models/range_search_view_model.dart';
import 'package:used_market/views/login.dart';

final secureStorage = FlutterSecureStorage();
bool isJwtValid = false;

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(MultiProvider(
    providers: [
      ChangeNotifierProvider<LoginModel>(
        create: (context) => LoginModel()..validateJwt(),
      ),
      ChangeNotifierProvider<ChatViewModel>(
          create: (context) => ChatViewModel()..initializeChatService()),
      ChangeNotifierProvider<GeolocationViewModel>(
          create: (context) => GeolocationViewModel()..checkPermission())
    ],
    child: MyApp(),
  ));
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(primarySwatch: Colors.red, fontFamily: 'pretty'),
      home: Consumer<LoginModel>(builder: (context, loginModel, child) {
        return loginModel.isLoading
            ? Scaffold(
                appBar: PreferredSize(
                  preferredSize: Size.fromHeight(30.0),
                  child: AppBar(
                    leading: Image.asset('assets/images/img_appbar.png'),
                    backgroundColor: Color.fromRGBO(190, 10, 10, 1),
                    elevation: 0.0,
                    centerTitle: true,
                  ),
                ),
                body: Center(child: CircularProgressIndicator(color: Colors.pinkAccent,)),
              )
            : !loginModel.isJwtValid
                ? Consumer<GeolocationViewModel>(
                  builder: (context, geolocationViewModel, snapshot) {
                    return geolocationViewModel.isLoading
                        ? Scaffold(
                      appBar: PreferredSize(
                        preferredSize: Size.fromHeight(30.0),
                        child: AppBar(
                          leading:
                          Image.asset('assets/images/img_appbar.png'),
                          backgroundColor: Color.fromRGBO(190, 10, 10, 1),
                          elevation: 0.0,
                          centerTitle: true,
                        ),
                      ),
                      body: Center(child: CircularProgressIndicator(color: Colors.black,)),
                    )
                      : Scaffold(
                        appBar: PreferredSize(
                          preferredSize: Size.fromHeight(30.0),
                          child: AppBar(
                            leading: Image.asset('assets/images/img_appbar.png'),
                            backgroundColor: Color.fromRGBO(190, 10, 10, 1),
                            elevation: 0.0,
                            centerTitle: true,
                          ),
                        ),
                        body: LoginPage(),
                      );
                  }
                )
                : Consumer<GeolocationViewModel>(
                    builder: (context, geolocationViewModel, snapshot) {
                    return geolocationViewModel.isLoading
                        ? Scaffold(
                            appBar: PreferredSize(
                              preferredSize: Size.fromHeight(30.0),
                              child: AppBar(
                                leading:
                                    Image.asset('assets/images/img_appbar.png'),
                                backgroundColor: Color.fromRGBO(190, 10, 10, 1),
                                elevation: 0.0,
                                centerTitle: true,
                              ),
                            ),
                            body: Center(child: CircularProgressIndicator(color: Colors.black,)),
                          )
                        : Consumer<ChatViewModel>(
                            builder: (context, chatViewModel, child) {
                              return chatViewModel.isLoading
                                  ? Scaffold(
                                      appBar: PreferredSize(
                                        preferredSize: Size.fromHeight(30.0),
                                        child: AppBar(
                                          leading: Image.asset(
                                              'assets/images/img_appbar.png'),
                                          backgroundColor:
                                              Color.fromRGBO(190, 10, 10, 1),
                                          elevation: 0.0,
                                          centerTitle: true,
                                        ),
                                      ),
                                      body: Center(
                                          child: CircularProgressIndicator(color: Colors.green,)),
                                    )
                                  : MultiProvider(
                                      providers: [
                                        ChangeNotifierProvider<
                                                RangeListViewModel>(
                                            create: (context) =>
                                                RangeListViewModel()),
                                      ],
                                      child: Home(),
                                    );
                            },
                          );
                  });
      }),
    );
  }
}
