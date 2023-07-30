import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:used_market/data_types/chat_message_data.dart';
import 'package:used_market/main.dart';
import 'package:used_market/view_models/chat_view_model.dart';

class ChatRoom extends StatefulWidget {
  const ChatRoom({Key? key, required this.roomTitle}) : super(key: key);
  final String roomTitle;

  @override
  State<ChatRoom> createState() => _ChatRoomState();
}

class _ChatRoomState extends State<ChatRoom> {
  String _username = "";
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
  }

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

              WidgetsBinding.instance.addPostFrameCallback((_) {

                _scrollController.animateTo(
                  MediaQuery.of(context).viewInsets.bottom,
                  duration: Duration(milliseconds: 200),
                  curve: Curves.easeInOut,
                );

                _scrollController.animateTo(
                  _scrollController.position.maxScrollExtent,
                  duration: Duration(milliseconds: 200),
                  curve: Curves.easeInOut,
                );

              });



            return Scaffold(
                resizeToAvoidBottomInset: true,
                backgroundColor: Color.fromRGBO(50, 50, 50, 1),
                body: model.chatMessageListForRoom[widget.roomTitle] == null
                    ? CircularProgressIndicator()
                    : Column(children: [
                        Expanded(
                          child: ListView.builder(
                            controller: _scrollController,
                            itemCount: model
                                .chatMessageListForRoom[widget.roomTitle]!
                                .length,
                            itemBuilder: (context, index) {
                              final chatMessage = model.chatMessageListForRoom[
                                  widget.roomTitle]![index];
                              if (chatMessage.sender == _username) {
                                return MyMessageBubble(
                                  message: chatMessage.message,
                                  sender: chatMessage.sender!,
                                );
                              } else {
                                return OtherMessageBubble(
                                  message: chatMessage.message,
                                  sender: chatMessage.sender!,
                                );
                              }
                            },
                          ),
                        ),
                        Row(
                          children: [Container(
                            height: 70,
                              width: 330,
                              padding: EdgeInsets.all(10.0),
                              child: TextFormField(
                                decoration: InputDecoration(
                                  labelText: '대화를 입력하세요',
                                  border: OutlineInputBorder(),
                                ),
                              )),
                          Container(
                            width: 50,
                            height: 45,
                            child: ElevatedButton(
                              style: ButtonStyle(

                              ),
                                onPressed: (){




                                },
                                child:
                                Text("확인")),
                          )]
                        ),
                      ]));

          });
        });
  }
}

bool isImageUrl(String text) {
  final RegExp exp =
      RegExp(r"^(http|https)://.*/imgfile-service/getimgdata\?id=.*$");
  return exp.hasMatch(text);
}

String extractIdFromUrl(String text) {
  const String prefix = 'imgfile-service/getimgdata?id=';
  final int startIndex = text.indexOf(prefix);
  return text.substring(startIndex + prefix.length);
}

class MyMessageBubble extends StatelessWidget {
  final String message;
  final String sender;

  MyMessageBubble({required this.message, required this.sender});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.end,
      children: <Widget>[
        Container(
          margin: EdgeInsets.all(10.0),
          padding: EdgeInsets.all(10.0),
          decoration: BoxDecoration(
            color: Color.fromRGBO(90, 90, 90, 1),
            borderRadius: BorderRadius.circular(10.0),
          ),
          child: isImageUrl(message)
              ? Container(
                  width: 200,
                  height: 200,
                  child: Image(
                    image: NetworkImage(
                        'http://10.0.2.2:8000/imgfile-service/getimgdata?id=${extractIdFromUrl(message)}'),
                    fit: BoxFit.fitWidth,
                  ),
                )
              : Text(
                  message,
                  style: TextStyle(color: Colors.white),
                ),
        ),
        Container(
          height: 50,
          width: 50,
          child: ClipRRect(
              borderRadius: BorderRadius.circular(10),
              child: Image(
                image: NetworkImage(
                    "http://10.0.2.2:8000/user-service/pic/$sender"),
                fit: BoxFit.cover,
              )),
        ),
      ],
    );
  }
}

class OtherMessageBubble extends StatelessWidget {
  final String message;
  final String sender;

  OtherMessageBubble({required this.message, required this.sender});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.start,
      children: <Widget>[
        Container(
          height: 50,
          width: 50,
          child: ClipRRect(
              borderRadius: BorderRadius.circular(10),
              child: Image(
                image: NetworkImage(
                    "http://10.0.2.2:8000/user-service/pic/$sender"),
                fit: BoxFit.cover,
              )),
        ),
        Container(
          margin: EdgeInsets.all(10.0),
          padding: EdgeInsets.all(10.0),
          decoration: BoxDecoration(
            color: Colors.orange,
            borderRadius: BorderRadius.circular(10.0),
          ),
          child: isImageUrl(message)
              ? Container(
                  width: 200,
                  height: 200,
                  child: Image(
                    image: NetworkImage(
                        'http://10.0.2.2:8000/imgfile-service/getimgdata?id=${extractIdFromUrl(message)}'),
                    fit: BoxFit.fitWidth,
                  ),
                )
              : Text(
                  message,
                  style: TextStyle(color: Color.fromRGBO(10, 10, 10, 0.6)),
                ),
        ),
      ],
    );
  }
}
