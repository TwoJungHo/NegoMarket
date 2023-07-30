import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:used_market/view_models/chat_view_model.dart';

class ChatRoom extends StatelessWidget {
  const ChatRoom({Key? key, required this.roomTitle}) : super(key: key);
  final String roomTitle;

  @override
  Widget build(BuildContext context) {
    return Consumer<ChatViewModel>(
      builder: (context, model, child) {
        return Scaffold(
          body: model.chatMessageListForRoom[roomTitle] != null
              ? ListView.builder(
            itemCount: model.chatMessageListForRoom[roomTitle]!.length,
            itemBuilder: (context, index){
              return Column(
                children: [ListTile(
                  title: Text(model.chatMessageListForRoom[roomTitle]![index].message)
                ),]
              );
            },
          )
              : CircularProgressIndicator()
        );
      }
    );
  }
}
