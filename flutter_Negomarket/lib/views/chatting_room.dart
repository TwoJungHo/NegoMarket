import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:provider/provider.dart';
import 'package:used_market/data_types/chat_message_data.dart';
import 'package:used_market/data_types/chat_room_data.dart';
import 'package:used_market/data_types/constants_data.dart';
import 'package:used_market/main.dart';
import 'package:used_market/view_models/chat_view_model.dart';
import 'package:http/http.dart' as http;

class ChattingRoom extends StatefulWidget {
  const ChattingRoom({Key? key, required this.chatRoom}) : super(key: key);
  final ChatRoom chatRoom;

  @override
  State<ChattingRoom> createState() => _ChattingRoomState();
}

class _ChattingRoomState extends State<ChattingRoom> {
  String _username = "";
  final TextEditingController _messageController = TextEditingController();
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
  }

  Future<String> getUsername() async {
    _username = (await secureStorage.read(key: 'username'))!;

    return _username;
  }

  Future<String> _pickImageAndUpload(int kind) async {
    final ImagePicker _picker = ImagePicker();

    final XFile? image =
    kind == 1
        ? await _picker.pickImage(source: ImageSource.gallery)
        : await _picker.pickImage(source: ImageSource.camera);

    if (image == null) {
      return "";
    }

    final http.MultipartRequest request = http.MultipartRequest(
      'POST',
      Uri.parse('${Constants.api_gate_url}/imgfile-service/uploadimg'),
    );

    request.files.add(await http.MultipartFile.fromPath(
      'file',
      image.path,
    ));

    final http.StreamedResponse streamedResponse = await request.send();

    final http.Response response = await http.Response.fromStream(streamedResponse);

    if (response.statusCode == 200) {
      print('Uploaded!');
      Map<String, dynamic> responseBody = jsonDecode(response.body);
      print('Response body: $responseBody');
      print('Result: ${responseBody['result']}');
      return '${responseBody['result']}';
    } else {
      print('Failed to upload image: ${response.statusCode}');
      print('Response body: ${response.body}');
      throw Exception("업로드 실패");
    }


  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<String>(
        future: getUsername(),
        builder: (BuildContext context, AsyncSnapshot<String> snapshot) {
          return Consumer<ChatViewModel>(builder: (context, model, child) {

              WidgetsBinding.instance.addPostFrameCallback((_) {
                if(_scrollController.hasClients){
                _scrollController.animateTo(
                  MediaQuery.of(context).viewInsets.bottom,
                  duration: Duration(milliseconds: 200),
                  curve: Curves.easeInOut,
                );

                _scrollController.animateTo(
                  _scrollController.position.maxScrollExtent,
                  duration: Duration(milliseconds: 200),
                  curve: Curves.easeInOut,
                );}
              });



            return Scaffold(
                resizeToAvoidBottomInset: true,
                backgroundColor: Color.fromRGBO(50, 50, 50, 1),
                appBar: PreferredSize(
                  preferredSize: Size.fromHeight(50.0),
                  child: AppBar(
                    leading: GestureDetector(
                        onTap: () {
                          Navigator.pop(context);
                        },
                        child: Image.asset('assets/images/img_appbar.png')),
                    backgroundColor: Color.fromRGBO(190, 10, 10, 1),
                    elevation: 0.0,
                    centerTitle: true,
                  ),
                ),

                floatingActionButton: Stack(


                  children: [
                    Positioned(
                      right: MediaQuery.of(context).size.width*0,
                      bottom: MediaQuery.of(context).size.height*0.06,
                      child: SizedBox(
                        width: MediaQuery.of(context).size.width*0.15,
                        child: FloatingActionButton(
                          backgroundColor: Color.fromRGBO(190, 50, 50, 0.6),
                        onPressed: () {
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
                                          String id = await _pickImageAndUpload(1);
                                          if (id != "") {
                                            ChatMessage chatMessage = ChatMessage(
                                              roomTitle: widget.chatRoom.title,
                                              receiver: widget.chatRoom.username1 == _username
                                                  ? widget.chatRoom.username2
                                                  : widget.chatRoom.username1,
                                              message:
                                              "${Constants.api_gate_url}/imgfile-service/getimgdata?id=$id",
                                              read: false,
                                            );
                                            model.sendMessage(chatMessage);
                                          }
                                          Navigator.pop(context); // 선택 후 바닥 시트를 닫습니다.
                                        },
                                      ),
                                      ListTile(
                                        leading: Icon(Icons.camera),
                                        title: Text('Camera'),
                                        onTap: () async {
                                          String id = await _pickImageAndUpload(2);
                                          if (id != "") {
                                            ChatMessage chatMessage = ChatMessage(
                                              roomTitle: widget.chatRoom.title,
                                              receiver: widget.chatRoom.username1 == _username
                                                  ? widget.chatRoom.username2
                                                  : widget.chatRoom.username1,
                                              message:
                                              "${Constants.api_gate_url}/imgfile-service/getimgdata?id=$id",
                                              read: false,
                                            );
                                            model.sendMessage(chatMessage);
                                          }
                                          Navigator.pop(context); // 선택 후 바닥 시트를 닫습니다.
                                        },
                                      ),
                                    ],
                                  ),
                                ),
                              );
                            },
                          );
                        },
                        child: Icon(Icons.camera),
                  ),
                      ),
                    ),]
                ),



                body: model.chatMessageListForRoom[widget.chatRoom.title] ==
                        null
                    ? CircularProgressIndicator()
                    : Column(children: [
                        Expanded(
                          child: Padding(
                            padding: EdgeInsets.fromLTRB(10, 0, 10, 0),
                            child: ListView.builder(
                              controller: _scrollController,
                              itemCount: model
                                  .chatMessageListForRoom[widget.chatRoom.title]!
                                  .length,
                              itemBuilder: (context, index) {


                                final chatMessage = model.chatMessageListForRoom[
                                    widget.chatRoom.title]![index];
                                if (chatMessage.sender == _username) {
                                  return MyMessageBubble(
                                    message: chatMessage.message,
                                    sender: chatMessage.sender!,
                                    read: chatMessage.read,
                                  );
                                } else {
                                  if(!chatMessage.read) {
                                    model.readMessage(chatMessage.id!);
                                    print(chatMessage.id!);
                                  }
                                  return OtherMessageBubble(
                                    message: chatMessage.message,
                                    sender: chatMessage.sender!,
                                  );
                                }



                              },
                            ),
                          ),
                        ),
                        Row(

                            children: [
                          Container(
                              //height: MediaQuery.of(context).size.height*0.08,
                              width: MediaQuery.of(context).size.width*0.83,
                              padding: EdgeInsets.all(5),
                              color: Colors.transparent,
                              child: TextFormField(
                                controller: _messageController,
                                maxLines: 1,

                                style: TextStyle(
                                color: Colors.grey
                                ),
                                decoration: InputDecoration(
                                  labelText: '행복한 Nego 생활!',
                                  labelStyle: TextStyle(color: Colors.amber),
                                  fillColor: Color.fromRGBO(0, 0, 0, 0.5),
                                  filled: true,
                                  enabledBorder: OutlineInputBorder(  // 활성 상태가 아닐 때의 테두리 스타일
                                    borderRadius: BorderRadius.circular(5.0),
                                    borderSide: BorderSide(color: Colors.black, width: 1.0),
                                  ),
                                  focusedBorder: OutlineInputBorder(  // 활성 상태일 때의 테두리 스타일
                                    borderRadius: BorderRadius.circular(5.0),
                                    borderSide: BorderSide(color: Color.fromRGBO(190, 10, 10, 0.7), width: 1.0),
                                  ),
                                ),
                              ),
                          ),
                          Container(
                            width: MediaQuery.of(context).size.width*0.15,
                            height: MediaQuery.of(context).size.height*0.05,
                            color: Colors.transparent,

                            child: ElevatedButton(
                                style: ButtonStyle(
                                  backgroundColor: MaterialStateProperty.all<Color>(Color.fromRGBO(190, 10, 10, 1))
                                ),
                                onPressed: () {
                                  ChatMessage chatMessage = ChatMessage(
                                      roomTitle: widget.chatRoom.title,
                                      receiver:
                                          widget.chatRoom.username1 == _username
                                              ? widget.chatRoom.username2
                                              : widget.chatRoom.username1,
                                      message: _messageController.text,
                                      read: false);

                                  model.sendMessage(chatMessage);
                                  _messageController.text = "";
                                },
                                child: Text("확인")),
                          ),



                        ]),
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
  final bool read;

  MyMessageBubble({required this.message, required this.sender, required this.read});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.end,
      children: <Widget>[
        read? Icon(Icons.done_all, color: Colors.blue)
        : Icon(Icons.done, color: Color.fromRGBO(10, 10, 190, 0.5)),
        Container(
          width: MediaQuery.of(context).size.width * 0.5,
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
                        '${Constants.api_gate_url}/imgfile-service/getimgdata?id=${extractIdFromUrl(message)}'),
                    fit: BoxFit.fitWidth,
                  ),
                )
              : Text(
                  message,
                  style: TextStyle(color: Colors.black),
                ),
        ),
        Container(
          height: 50,
          width: 50,
          child: ClipRRect(
              borderRadius: BorderRadius.circular(10),
              child: Image(
                image: NetworkImage(
                    "${Constants.api_gate_url}/user-service/pic/$sender"),
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
                    "${Constants.api_gate_url}/user-service/pic/$sender"),
                fit: BoxFit.cover,
              )),
        ),
        Container(
          width: MediaQuery.of(context).size.width * 0.5,
          margin: EdgeInsets.all(10.0),
          padding: EdgeInsets.all(10.0),
          decoration: BoxDecoration(
            color: Color.fromRGBO(30, 30, 30, 1),
            borderRadius: BorderRadius.circular(10.0),
          ),
          child: isImageUrl(message)
              ? Container(
                  width: 200,
                  height: 200,
                  child: Image(
                    image: NetworkImage(
                        '${Constants.api_gate_url}/imgfile-service/getimgdata?id=${extractIdFromUrl(message)}'),
                    fit: BoxFit.fitWidth,
                  ),
                )
              : Text(
                  message,
                  style: TextStyle(color: Color.fromRGBO(150, 150, 150, 1)),
                ),
        ),
      ],
    );
  }
}
