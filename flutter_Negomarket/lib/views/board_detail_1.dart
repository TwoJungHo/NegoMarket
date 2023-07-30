import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:used_market/data_types/board_data.dart';
import 'package:http/http.dart' as http;
class BoardDetail extends StatelessWidget {
  final String id;
  const BoardDetail({Key? key, required this.id}) : super(key: key);

Future<BoardResponse> getBoard() async {

  String url = 'http://10.0.2.2:8000/board-service/boards/$id';
  final response = await http.get(
    Uri.parse(url),
    headers:{
      "Content-Type": "application/json",
    },
  );
  BoardResponse boardResponse = BoardResponse.fromJson(
      jsonDecode(
          utf8.decode(response.bodyBytes)
      )
  );

  return boardResponse;

}

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<BoardResponse>(
      future: getBoard(),
      builder: (BuildContext context, AsyncSnapshot<BoardResponse> snapshot){
        if(snapshot.connectionState == ConnectionState.waiting){
          return CircularProgressIndicator();
        } else {
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
            body:Center(
                child: Column(
                  children: [
                    Image.network('http://10.0.2.2:8000/sell-service/img/${snapshot.data!.sellId}'),
                    Text(snapshot.data!.username),
                    Text(
                      snapshot.data!.title
                  ),
                  ]
                )
            )
        );
        }
      },

    );
  }
}
