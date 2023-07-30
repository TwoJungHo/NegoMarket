import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:used_market/data_types/constants_data.dart';
import 'package:used_market/main.dart';
import 'package:used_market/view_models/chat_view_model.dart';
import 'package:used_market/views/chatting_room.dart';

class ChatRoomList extends StatefulWidget {
  const ChatRoomList({super.key});

  @override
  State<ChatRoomList> createState() => _ChatRoomListState();
}

class _ChatRoomListState extends State<ChatRoomList> {
  String _username = "";

  Future<String> getUsername() async {
    _username = (await secureStorage.read(key: 'username'))!;

    return _username;
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<String>(
        future: getUsername(),
        builder: (BuildContext context, AsyncSnapshot<String> snapshot) {
          return Consumer<ChatViewModel>(builder: (context, model, child) {
            return model.isLoading == true
                ? CircularProgressIndicator()
                : Padding(
                    padding: EdgeInsets.fromLTRB(8, 8, 8, 0),
                    child: ListView.builder(
                        itemCount: model.chatRoomList.length,
                        itemBuilder: (context, index) {
                          return Column(
                            children: [
                              InkWell(
                                  onTap: () {
                                    Navigator.push(
                                        context,
                                        MaterialPageRoute(
                                            builder: (context) => ChattingRoom(
                                                chatRoom: model
                                                    .chatRoomList[index])));
                                  },
                                  child: ChatRoomTile(
                                    title: model.chatRoomList[index].title,
                                    talkerName: _username !=
                                            model.chatRoomList[index].username1
                                        ? model.chatRoomList[index].username1
                                        : model.chatRoomList[index].username2,
                                  )),
                              Divider(),
                            ],
                          );
                        }),
                  );
          });
        });
  }
}

class ChatRoomTile extends StatelessWidget {
  final String title;
  final String talkerName;

  const ChatRoomTile(
      {super.key, required this.talkerName, required this.title});

  @override
  Widget build(BuildContext context) {
    return Consumer<ChatViewModel>(builder: (context, model, child) {
      return Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Container(
            width: MediaQuery.of(context).size.width * 0.141,
            child: Column(
              children: [
                Stack(
                  children: [
                    ClipRRect(
                      borderRadius: BorderRadius.circular(20),
                      child: SizedBox(
                        width: MediaQuery.of(context).size.width * 0.141,
                        height: MediaQuery.of(context).size.width * 0.141,
                        child: Image.network(
                          '${Constants.api_gate_url}/user-service/pic/$talkerName',
                          fit: BoxFit.cover,
                        ),
                      ),
                    ),
                    if (model.unreadMessageCount[title]! > 0)
                      Positioned(
                        left: 0,
                        child: Container(
                          padding: EdgeInsets.all(0.5),
                          decoration: BoxDecoration(
                            color: Colors.red,
                            borderRadius: BorderRadius.circular(10),
                          ),
                          constraints: BoxConstraints(
                            minWidth: 20,
                            minHeight: 20,
                          ),
                          child: Text(
                            '${model.unreadMessageCount[title]}',
                            style: TextStyle(
                              color: Colors.white,
                              fontSize: 20,
                            ),
                            textAlign: TextAlign.center,
                          ),
                        ),
                      ),
                  ],
                ),
                Text('$talkerName')
              ],
            ),
          ),
          SizedBox(
            width: MediaQuery.of(context).size.width * 0,
          ),
          Container(
              width: MediaQuery.of(context).size.width * 0.5,
              height: MediaQuery.of(context).size.width * 0.141,
              alignment: AlignmentDirectional.centerEnd,
              child: model.chatMessageListForRoom[title]!.length > 0
                  ? Row(
                      children: [
                        Container(
                          child: ClipRRect(
                            borderRadius: BorderRadius.circular(
                                MediaQuery.of(context).size.width * 0.15),
                            child: SizedBox(
                              width: MediaQuery.of(context).size.width * 0.05,
                              height: MediaQuery.of(context).size.width * 0.05,
                              child: Image.network(
                                  '${Constants.api_gate_url}/user-service/pic/${model.chatMessageListForRoom[title]!.last.sender}',
                                  fit: BoxFit.cover),
                            ),
                          ),
                        ),
                        SizedBox(
                          width: MediaQuery.of(context).size.width * 0.04,
                        ),
                        Container(
                          alignment: AlignmentDirectional.centerStart,
                          width: MediaQuery.of(context).size.width * 0.40,
                          height: MediaQuery.of(context).size.height * 0.140,
                          child: Text(
                            '${model.chatMessageListForRoom[title]!.last.message}',
                            maxLines: 3,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ),
                      ],
                    )
                  : Text(""))
        ],
      );
    });
  }
}
